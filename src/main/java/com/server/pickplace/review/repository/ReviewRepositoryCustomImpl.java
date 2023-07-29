package com.server.pickplace.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.QCategory;
import com.server.pickplace.place.entity.QCategoryPlace;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.dto.*;
import com.server.pickplace.review.entity.Review;
import com.server.pickplace.review.error.ReviewErrorResult;
import com.server.pickplace.review.error.ReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.server.pickplace.member.entity.QMember.*;
import static com.server.pickplace.place.entity.QCategory.*;
import static com.server.pickplace.place.entity.QCategoryPlace.*;
import static com.server.pickplace.place.entity.QPlace.place;
import static com.server.pickplace.place.entity.QRoom.*;
import static com.server.pickplace.reservation.entity.QReservation.*;
import static com.server.pickplace.review.entity.QReview.*;


@RequiredArgsConstructor
@Slf4j
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<ReviewCategoryResponse> getReviewDtosByEmail(String email) {

        List<ReviewCategoryResponse> reviewCateogryResponseList = queryFactory
                .select(new QReviewCategoryResponse(
                                review.id,
                                place.name,
                                category.status,
                                reservation.id,
                                review.rating,
                                review.content,
                                member.name,
                                review.updatedDate
                        )
                )
                .from(review)
                .join(review.reservation, reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(reservation.member, member)
                .join(place.categories, categoryPlace)
                .join(categoryPlace.category, category)
                .where(member.email.eq(email))
                .fetch();

        return reviewCateogryResponseList;
    }

    @Override
    public PlaceReviewResponse getPlaceReviewDtoByPlaceId(Long placeId) {

        Optional<PlaceReviewResponse> optionalPlaceReviewResponse = Optional.ofNullable(
                queryFactory
                .select(new QPlaceReviewResponse(place.id, place.name, place.rating, place.reviewCount))
                .from(place)
                .where(place.id.eq(placeId))
                .fetchOne()
        );

        PlaceReviewResponse placeReviewResponse = optionalPlaceReviewResponse.orElseThrow(() -> new ReviewException(ReviewErrorResult.NO_EXIST_PLACE_ID));

        return placeReviewResponse;
    }

    @Override
    public List<ReviewResponse> getReviewResponseListByPlaceId(Long placeId) {

        List<ReviewResponse> reviewResponseList = queryFactory
                .select(new QReviewResponse(
                                review.id,
                                place.name,
                                reservation.id,
                                review.rating,
                                review.content,
                                member.name,
                                review.updatedDate
                        )
                )
                .from(review)
                .join(review.reservation, reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(reservation.member, member)
                .where(place.id.eq(placeId))
                .fetch();


        return reviewResponseList;
    }


    @Override
    public ReviewDetailResponse getReviewDetailDtoByReviewId(Long reviewId) {

        Optional<ReviewDetailResponse> optionalReviewDetailResponse = Optional.ofNullable(
                queryFactory
                        .select(new QReviewDetailResponse(review.id, place.name, category.status, member.name, review.updatedDate, place.address, reservation.updatedDate, review.rating, review.content))
                        .from(review)
                        .join(review.reservation, reservation)
                        .join(reservation.room, room)
                        .join(room.place, place)
                        .join(reservation.member, member)
                        .join(place.categories, categoryPlace)
                        .join(categoryPlace.category, category)
                        .where(review.id.eq(reviewId))
                        .fetchOne()
        );
        ReviewDetailResponse reviewDetailResponse = optionalReviewDetailResponse.orElseThrow(() -> new ReviewException(ReviewErrorResult.NO_EXIST_REVIEW_ID));

        return reviewDetailResponse;
    }

    @Override
    public void createReview(Reservation reservation1, CreateReviewRequest createReviewRequest) {
        Review review = Review.builder().content(createReviewRequest.getContent()).rating(createReviewRequest.getRating()).reservation(reservation1).build();

        em.persist(review);

        Place reservationPlace = queryFactory
                .select(place)
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .where(reservation.id.eq(reservation1.getId()))
                .fetchOne();

        reservationPlace.setReviewCount(reservationPlace.getReviewCount() + 1);
        reservationPlace.setRating(reservationPlace.getRating() + createReviewRequest.getRating());

    }

    @Override
    public void updateReview(String email, UpdateReviewRequest updateReviewRequest, Long reviewId) {

        // 1. 리뷰ID null check + 리뷰ID와 토큰 정보 일치 체크
        // 2. 리뷰 수정

        Review review = getReviewInUpdateAndDelete(email, reviewId);

        Place place = getPlaceByReservationIdFetchedByReview(review);

        Float newRating = updateReviewRequest.getRating();
        Float originalRating = review.getRating();

        place.setRating(place.getRating() + (newRating - originalRating));

        review.setContent(updateReviewRequest.getContent());
        review.setRating(updateReviewRequest.getRating());

    }

    @Override
    public void deleteReview(String email, Long reviewId) {

        // 1. 리뷰ID null check + 리뷰ID와 토큰 정보 일치 체크
        // 2. 리뷰 삭제

        Review review = getReviewInUpdateAndDelete(email, reviewId);

        Place place1 = getPlaceByReservationIdFetchedByReview(review);

        place1.setRating(place1.getRating() - review.getRating());
        place1.setReviewCount(place1.getReviewCount() - 1);

        em.remove(review);

    }

    private Place getPlaceByReservationIdFetchedByReview(Review review) {
        Place place1 = queryFactory
                .select(place)
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .where(reservation.id.eq(review.getReservation().getId()))
                .fetchOne();
        return place1;
    }


    private Review getReviewInUpdateAndDelete(String email, Long reviewId) {

        Review review1 = queryFactory
                .select(review)
                .from(review)
                .join(review.reservation, reservation)
                .fetchJoin()
                .join(reservation.member, member)
                .fetchJoin()
                .where(review.id.eq(reviewId))
                .fetchOne();

        if (review1 == null) {
            throw new ReviewException(ReviewErrorResult.NO_EXIST_REVIEW_ID);
        } else if (!review1.getReservation().getMember().getEmail().equals(email)) {
            throw new ReviewException(ReviewErrorResult.NO_PERMISSION);
        }
        return review1;
    }


}
