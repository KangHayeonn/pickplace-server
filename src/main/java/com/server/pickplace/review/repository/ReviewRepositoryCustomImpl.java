package com.server.pickplace.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.member.entity.QMember;
import com.server.pickplace.place.entity.QPlace;
import com.server.pickplace.place.entity.QRoom;
import com.server.pickplace.reservation.entity.QReservation;
import com.server.pickplace.review.dto.MemberReviewResponse;
import com.server.pickplace.review.dto.QMemberReviewResponse;
import com.server.pickplace.review.entity.QReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.util.List;

import static com.server.pickplace.member.entity.QMember.*;
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
    public List<MemberReviewResponse> getMemberReviewDtosByEmail(String email) {

        List<MemberReviewResponse> memberReviewResponseList = queryFactory
                .select(new QMemberReviewResponse(
                                review.id,
                                place.name,
                                reservation.id,
                                review.rating,
                                review.content,
                                member.name,
                                review.createdDate
                        )
                )
                .from(review)
                .join(review.reservation, reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(reservation.member, member)
                .where(member.email.eq(email))
                .fetch();

        return memberReviewResponseList;
    }

}
