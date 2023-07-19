package com.server.pickplace.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.QPlace;
import com.server.pickplace.reservation.entity.QReservation;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.dto.*;
import com.server.pickplace.review.entity.Review;
import com.server.pickplace.review.error.ReviewErrorResult;
import com.server.pickplace.review.error.ReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.List;

import static com.server.pickplace.member.entity.QMember.*;
import static com.server.pickplace.place.entity.QPlace.*;
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
    public List<ReviewResponse> getReviewDtosByEmail(String email) {

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
                .where(member.email.eq(email))
                .fetch();

        return reviewResponseList;
    }

    @Override
    public PlaceReviewResponse getPlaceReviewDtoByPlaceId(Long placeId) {

        PlaceReviewResponse placeReviewResponse = queryFactory
                .select(new QPlaceReviewResponse(place.id, place.name, place.rating, place.reviewCount))
                .from(place)
                .where(place.id.eq(placeId))
                .fetchOne();

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

        ReviewDetailResponse reviewDetailResponse = queryFactory
                .select(new QReviewDetailResponse(review.id, member.name, review.updatedDate, place.address, reservation.updatedDate, review.rating, review.content))
                .from(review)
                .join(review.reservation, reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(reservation.member, member)
                .where(review.id.eq(reviewId))
                .fetchOne();

        return reviewDetailResponse;
    }

    @Override
    public void createReview(Reservation reserv, CreateReviewRequest createReviewRequest) {
        Review review = Review.builder().content(createReviewRequest.getContent()).rating(createReviewRequest.getRating()).reservation(reserv).build();

        em.persist(review);

        Place reservationPlace = queryFactory
                .select(place)
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .where(reservation.id.eq(reserv.getId()))
                .fetchOne();

        reservationPlace.setReviewCount(reservationPlace.getReviewCount() + 1);
        reservationPlace.setRating(reservationPlace.getRating() + createReviewRequest.getRating());

    }

    @Override
    public void amendReview(String email, AmendReviewRequest amendReviewRequest, Long reviewId) {

        // 1. 리뷰ID null check + 리뷰ID와 토큰 정보 일치 체크
        // 2. 리뷰 수정

        Review review1 = getReviewInAmendAndDelete(email, reviewId);

        review1.setContent(amendReviewRequest.getContent());
        review1.setRating(amendReviewRequest.getRating());

    }

    @Override
    public void deleteReview(String email, Long reviewId) {

        // 1. 리뷰ID null check + 리뷰ID와 토큰 정보 일치 체크
        // 2. 리뷰 삭제

        Review review = getReviewInAmendAndDelete(email, reviewId);

        Place place1 = queryFactory
                .select(place)
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .where(reservation.id.eq(review.getReservation().getId()))
                .fetchOne();

        place1.setRating(place1.getRating() - review.getRating());
        place1.setReviewCount(place1.getReviewCount() - 1);

        em.remove(review);

    }


    private Review getReviewInAmendAndDelete(String email, Long reviewId) {

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
