package com.server.pickplace.host.repository;


import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.host.dto.PlaceRequest;
import com.server.pickplace.host.dto.PlaceUpdateRequest;
import com.server.pickplace.host.dto.RoomReqeust;
import com.server.pickplace.host.error.HostErrorResult;
import com.server.pickplace.host.error.HostException;
import com.server.pickplace.place.entity.*;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.entity.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.server.pickplace.place.entity.QCategory.*;
import static com.server.pickplace.place.entity.QCategoryPlace.*;
import static com.server.pickplace.place.entity.QPlace.*;
import static com.server.pickplace.place.entity.QRoom.*;
import static com.server.pickplace.place.entity.QTag.*;
import static com.server.pickplace.place.entity.QTagPlace.*;
import static com.server.pickplace.place.entity.QUnit.*;
import static com.server.pickplace.reservation.entity.QReservation.*;
import static com.server.pickplace.review.entity.QReview.*;

@RequiredArgsConstructor
@Slf4j
public class HostRepositoryCustomImpl implements HostRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public Long savePlace(Place place) {
        em.persist(place);

        return place.getId();
    }

    @Override
    public void saveRoom(Room room) {
        em.persist(room);
    }


    @Override
    public Category findCategoryByCategoryStatus(CategoryStatus categoryStatus) {
        Category category1 = queryFactory
                .select(category)
                .from(category)
                .where(category.status.eq(categoryStatus))
                .fetch().get(0);

        return category1;
    }


    @Override
    public void saveCategoryPlace(CategoryPlace categoryPlace) {
        em.persist(categoryPlace);

    }


    @Override
    public List<Tag> findTagListByTagStatusList(List<TagStatus> tagStatusList) {
        List<Tag> tagList = queryFactory
                .select(tag)
                .from(tag)
                .where(tag.tagStatus.in(tagStatusList))
                .fetch();

        return tagList;
    }


    @Override
    public void saveTagPlace(TagPlace tagPlace) {
        em.persist(tagPlace);
    }


    @Override
    public void saveUnitByRoom(Room room) {
        for (int i = 1; i < room.getAmount() + 1; i++) {
            Unit unit = Unit.builder().room(room).name(String.format("%d번방", i)).build();
            em.persist(unit);
        }
    }


    @Override
    public void updatePlaceByDto(Place place, Category category, List<Tag> tagList, PlaceUpdateRequest placeUpdateRequest) {

        // 기본 정보
        PlaceRequest placeInfo = placeUpdateRequest.getPlace();
        placeSetting(place, placeInfo);

        // 카테고리
        place.getCategories().get(0).setCategory(category);

        // 태그
        List<TagPlace> beforeTagPlaceList = queryFactory
                .select(tagPlace)
                .from(tagPlace)
                .where(tagPlace.place.id.eq(place.getId()))
                .fetch();

        beforeTagPlaceList.forEach(em::remove);

        tagList.forEach(tag ->
                em.persist(TagPlace.builder().tag(tag).place(place).build())
        );


    }

    @Override
    public void deletePlace(Place targetPlace) {

        List<Tuple> allLinkedDataList = queryFactory
                .select(room, unit, reservation, review, categoryPlace, tagPlace)
                .from(place)
                .leftJoin(place.rooms, room).on(room.place.eq(targetPlace))
                .leftJoin(room.reservations, reservation)
                .leftJoin(reservation.review, review)
                .leftJoin(room.units, unit)
                .join(place.categories, categoryPlace)
                .join(place.tags, tagPlace)
                .fetch();

        List<Room> roomList = new ArrayList<>();
        List<Unit> unitList = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        List<Review> reviewList = new ArrayList<>();
        List<CategoryPlace> categoryPlaceList = new ArrayList<>();
        List<TagPlace> tagPlaceList = new ArrayList<>();

        for (Tuple tuple : allLinkedDataList) {
            Room room = tuple.get(0, Room.class);
            Unit unit = tuple.get(1, Unit.class);
            Reservation reservation = tuple.get(2, Reservation.class);
            Review review = tuple.get(3, Review.class);
            CategoryPlace categoryPlace = tuple.get(4, CategoryPlace.class);
            TagPlace tagPlace = tuple.get(5, TagPlace.class);

            roomList.add(room);
            unitList.add(unit);
            reservationList.add(reservation);
            reviewList.add(review);
            categoryPlaceList.add(categoryPlace);
            tagPlaceList.add(tagPlace);
        }


        for (Review review : reviewList) {
            if (review != null) {
                em.remove(review);
            }
        }

        for (Reservation reservation : reservationList) {
            if (reservation != null) {
                em.remove(reservation);
            }
        }

        for (Unit unit : unitList) {
            if (unit != null) {
                em.remove(unit);
            }
        }

        for (Room room : roomList) {
            if (room != null) {
                em.remove(room);
            }
        }

        categoryPlaceList.forEach(em::remove);
        tagPlaceList.forEach(em::remove);
        em.remove(targetPlace);

    }

    @Override
    public void updateRoom(Room room, RoomReqeust roomReqeust) {
        room.setPrice(roomReqeust.getPrice());
        room.setName(roomReqeust.getName());
        room.setPeopleNum(roomReqeust.getPeopleNum());

        Integer existingAmount = room.getAmount();
        Integer newAmount = roomReqeust.getAmount();

        roomAmountUpdate(room, existingAmount, newAmount);

        room.setAmount(roomReqeust.getAmount());
    }

    private void roomAmountUpdate(Room room, Integer existingAmount, Integer newAmount) {
        int quota = newAmount - existingAmount;

        if (newAmount > existingAmount) {

            // 1번방부터 차례대로 탐색해서, 갯수 맞춰질때까지 등록
            List<String> unitNameList = queryFactory
                    .select(unit.name)
                    .from(unit)
                    .where(unit.room.eq(room))
                    .fetch();

            int i = 1;
            int count = 0;

            while (quota != count) {
                String unitName = String.format("%d번방", i);
                if (!unitNameList.contains(unitName)) {
                    Unit unit = Unit.builder().name(unitName).room(room).build();
                    em.persist(unit);
                    count++;
                }
                i++;
            }

        } else if (newAmount < existingAmount) {

            // 예약 있는 호실 조회 ( 예약 유무의 기준은 endDate )
            List<Unit> reservationExistUnitList = queryFactory
                    .selectDistinct(unit)
                    .from(unit)
                    .join(unit.reservations, reservation)
                    .where(
                            unit.room.eq(room),
                            reservation.endDate.goe(LocalDate.now())

                    )
                    .fetch();

            List<Unit> allUnitList = queryFactory
                    .select(unit)
                    .from(unit)
                    .where(unit.room.eq(room))
                    .fetch();

            allUnitList.removeAll(reservationExistUnitList);

            if (allUnitList.size() < -quota) {
                throw new HostException(HostErrorResult.CANT_CHANGE_ROOM_AMOUNT);
            } else {
                for (int i = 0; i < -quota; i++) {
                    Unit unit = allUnitList.get(i);

                    List<Tuple> reservationReviewTupleList = queryFactory
                            .select(reservation, review)
                            .from(reservation)
                            .leftJoin(reservation.review, review)
                            .where(reservation.unit.eq(unit))
                            .orderBy(reservation.unit.id.asc())
                            .fetch();

                    for (Tuple tuple : reservationReviewTupleList) {
                        Reservation reservation = tuple.get(0, Reservation.class);
                        Review review = tuple.get(1, Review.class);

                        if (review != null) {
                            em.remove(review);
                        }
                        em.remove(reservation);
                    }

                    em.remove(unit);
                }
            }

        }

    }

    @Override
    public void deleteRoom(Room targetRoom) {

        List<Tuple> allLinkedDataList = queryFactory
                .select(review, reservation, unit)
                .from(room)
                .leftJoin(room.units, unit).on(unit.room.eq(targetRoom))
                .leftJoin(unit.reservations, reservation)
                .leftJoin(reservation.review, review)
                .fetch();

        List<Review> reviewList = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        List<Unit> unitList = new ArrayList<>();


        for (Tuple tuple : allLinkedDataList) {

            Review review = tuple.get(0, Review.class);
            Reservation reservation = tuple.get(1, Reservation.class);
            Unit unit = tuple.get(2, Unit.class);

            reviewList.add(review);
            reservationList.add(reservation);
            unitList.add(unit);

        }

        for (Review review : reviewList) {
            if (review != null) {
                em.remove(review);
            }
        }

        for (Reservation reservation : reservationList) {
            if (reservation != null) {
                em.remove(reservation);
            }
        }

        for (Unit unit : unitList) {
            if (unit != null) {
                em.remove(unit);
            }
        }

        em.remove(targetRoom);

    }

    private void placeSetting(Place place, PlaceRequest placeInfo) {
        place.setName(placeInfo.getName());
        place.setNumber(placeInfo.getNumber());
        place.setAddress(placeInfo.getAddress());
        place.setX(placeInfo.getX());
        place.setY(placeInfo.getY());
    }
}
