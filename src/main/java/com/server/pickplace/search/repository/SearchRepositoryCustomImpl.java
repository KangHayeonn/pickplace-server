package com.server.pickplace.search.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.search.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.geo.Point;

import javax.persistence.EntityManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.server.pickplace.place.entity.QCategoryPlace.*;
import static com.server.pickplace.place.entity.QPlace.*;
import static com.server.pickplace.place.entity.QRoom.room;
import static com.server.pickplace.place.entity.QTagPlace.*;
import static com.server.pickplace.place.entity.QUnit.*;
import static com.server.pickplace.reservation.entity.QReservation.*;


public class SearchRepositoryCustomImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SearchRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    HashMap<Long, Integer> roomCountMap = new HashMap<>();
    List<Long> placeIdList = new ArrayList<>();
    List<PlaceResponse> placeResponseList = new ArrayList<>();
    List<Long> placeBeforeList = new ArrayList<>();

    @Override
    public List<Long> getUnableRoomList(DetailPageRequest detailPageRequest, Long placeId) {
        queryFactory
                .select(room)
                .from(room)
                .leftJoin(reservation)
                .on(room.id.eq(reservation.room.id));

        return new ArrayList<>();  // [WIP]
    }

    @Override
    public Slice<PlaceResponse> findSliceByDto(CategorySearchRequest categorySearchRequest, Pageable pageable) {

        Point point = extractPointByAddress(categorySearchRequest.getAddress());

        List<Tuple> roomCountTupleList = queryFactory
                .select(place.id, room.amount.sum())
                .from(room)
                .join(room.place, place)
                .join(place, categoryPlace.place)
                .where(
                        Expressions.numberTemplate(Double.class,
                                        "ST_Distance_Sphere({0}, POINT({1}, {2}))",
                                        place.point, point.getX(), point.getY())
                                .loe(categorySearchRequest.getDistance()),

                        categoryPlace.category.id.eq(categorySearchRequest.getCategory())

                )
                .groupBy(place.id)
                .fetch();

        for (Tuple tuple : roomCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer roomCount = tuple.get(1, Integer.class);

            roomCountMap.put(placeId, roomCount);
            placeBeforeList.add(placeId);
        }


        List<Tuple> placeReservationCountTupleList = queryFactory
                .select(place.id, unit.id.countDistinct())
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(place, categoryPlace.place)
                .join(reservation.unit, unit)
                .where(

                        place.id.in(placeBeforeList),

                        reservation.startDate.after(categorySearchRequest.getStartDate()).or(reservation.startDate.eq(categorySearchRequest.getStartDate())),

                        reservation.endDate.before(categorySearchRequest.getEndDate()).or(reservation.endDate.eq(categorySearchRequest.getEndDate()))


                        )
                .groupBy(place.id)
                .fetch();

        for (Tuple tuple : placeReservationCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer reservationRoomCount = tuple.get(1, Integer.class);
            Integer totalRoomCount = roomCountMap.get(placeId);

            if (reservationRoomCount < totalRoomCount) {
                placeIdList.add(placeId);
            }
        }

        List<Place> placeList = queryFactory
                .selectFrom(place)
                .where(place.id.in(placeIdList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        for (Place place : placeList) {
            PlaceResponse placeResponse = PlaceResponse.builder()
                    .id(place.getId())
                    .name(place.getName())
                    .rating(place.getRating())
                    .reviewCount(place.getReviewCount())
                    .address(
                            new HashMap<>() {
                                {
                                    put("address", place.getAddress());
                                    put("latitude", place.getPoint().getX());
                                    put("latitude", place.getPoint().getY());
                                }
                            }
                    ).build();

            placeResponseList.add(placeResponse);
        }

        boolean hasNext = false;
        if (placeResponseList.size() > pageable.getPageSize()) {
            placeResponseList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(placeResponseList, pageable, hasNext);


    }

    @Override
    public Slice<PlaceResponse> findSliceByDto(BasicSearchRequest basicSearchRequest, Pageable pageable) {


        Point point = extractPointByAddress(basicSearchRequest.getAddress());

        List<Tuple> roomCountTupleList = getRoomCountTupleListByBasicDto(basicSearchRequest, point);

        for (Tuple tuple : roomCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer roomCount = tuple.get(1, Integer.class);

            roomCountMap.put(placeId, roomCount);
            placeBeforeList.add(placeId);
        }


        List<Tuple> placeReservationCountTupleList = queryFactory
                .select(place.id, unit.id.countDistinct())
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(reservation.unit, unit)
                .where(

                        place.id.in(placeBeforeList),

                        reservation.startDate.after(basicSearchRequest.getStartDate()).or(reservation.startDate.eq(basicSearchRequest.getStartDate())),

                        reservation.endDate.before(basicSearchRequest.getEndDate()).or(reservation.endDate.eq(basicSearchRequest.getEndDate()))
                )
                .groupBy(place.id)
                .fetch();

        for (Tuple tuple : placeReservationCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer reservationRoomCount = tuple.get(1, Integer.class);
            Integer totalRoomCount = roomCountMap.get(placeId);

            if (reservationRoomCount < totalRoomCount) {
                placeIdList.add(placeId);
            }
        }

        List<Place> placeList = queryFactory
                .selectFrom(place)
                .where(place.id.in(placeIdList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        for (Place place : placeList) {
            PlaceResponse placeResponse = PlaceResponse.builder()
                    .id(place.getId())
                    .name(place.getName())
                    .rating(place.getRating())
                    .reviewCount(place.getReviewCount())
                    .address(
                            new HashMap<>() {
                                {
                                    put("address", place.getAddress());
                                    put("latitude", place.getPoint().getX());
                                    put("latitude", place.getPoint().getY());
                                }
                            }
                    ).build();

            placeResponseList.add(placeResponse);
        }

        boolean hasNext = false;
        if (placeResponseList.size() > pageable.getPageSize()) {
            placeResponseList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(placeResponseList, pageable, hasNext);

    }
    @Override
    public Slice<PlaceResponse> findSliceByDto(DetailSearchRequest detailSearchRequest, Pageable pageable) {

        Point point = extractPointByAddress(detailSearchRequest.getAddress());

        List<Tuple> roomCountTupleList = queryFactory
                .select(place.id, tagPlace.tag.id.countDistinct(), room.amount.sum())
                .from(room)
                .join(room.place, place)
                .join(place, categoryPlace.place)
                .join(place, tagPlace.place)
                .where(
                        Expressions.numberTemplate(Double.class,
                                        "ST_Distance_Sphere({0}, POINT({1}, {2}))",
                                        place.point, point.getX(), point.getY())
                                .loe(detailSearchRequest.getDistance()),

                        categoryPlace.category.id.eq(detailSearchRequest.getCategory()),

                        room.peopleNum.goe(detailSearchRequest.getUserCnt()),

                        tagPlace.tag.id.in(detailSearchRequest.getTagId())

                )
                .groupBy(place.id)
                .fetch();

        Integer tagAmount = detailSearchRequest.getTagId().size();
        for (Tuple tuple : roomCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer tagCount = tuple.get(1, Integer.class);
            Integer roomCount = tuple.get(2, Integer.class);

            if (tagCount == tagAmount) {
                roomCountMap.put(placeId, roomCount / tagAmount);
                placeBeforeList.add(placeId);
            }

        }

        List<Tuple> placeReservationCountTupleList = queryFactory
                .select(place.id, unit.id.countDistinct())
                .from(room)
                .join(room.place, place)
                .join(place, categoryPlace.place)
                .join(place, tagPlace.place)
                .join(room, reservation.room)
                .join(unit, reservation.unit)
                .where(
                        place.id.in(placeBeforeList),

                        reservation.startDate.after(detailSearchRequest.getStartDate()).or(reservation.startDate.eq(detailSearchRequest.getStartDate())),

                        reservation.endDate.before(detailSearchRequest.getEndDate()).or(reservation.endDate.eq(detailSearchRequest.getEndDate()))

                        )
                .groupBy(place.id)
                .fetch();


        for (Tuple tuple : placeReservationCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer reservationRoomCount = tuple.get(1, Integer.class);
            Integer totalRoomCount = roomCountMap.get(placeId);

            if (reservationRoomCount < totalRoomCount) {
                placeIdList.add(placeId);
            }
        }

        List<Place> placeList = queryFactory
                .selectFrom(place)
                .where(place.id.in(placeIdList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        for (Place place : placeList) {
            PlaceResponse placeResponse = PlaceResponse.builder()
                    .id(place.getId())
                    .name(place.getName())
                    .rating(place.getRating())
                    .reviewCount(place.getReviewCount())
                    .address(
                            new HashMap<>() {
                                {
                                    put("address", place.getAddress());
                                    put("latitude", place.getPoint().getX());
                                    put("latitude", place.getPoint().getY());
                                }
                            }
                    ).build();

            placeResponseList.add(placeResponse);
        }

        boolean hasNext = false;
        if (placeResponseList.size() > pageable.getPageSize()) {
            placeResponseList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(placeResponseList, pageable, hasNext);

    }


    private List<Tuple> getRoomCountTupleListByBasicDto(BasicSearchRequest basicSearchRequest, Point point) {
        return queryFactory
                .select(place.id, room.amount.sum())
                .from(room)
                .join(room.place, place)
                .where(
                        Expressions.numberTemplate(Double.class,
                                        "ST_Distance_Sphere({0}, POINT({1}, {2}))",
                                        place.point, point.getX(), point.getY())
                                .loe(basicSearchRequest.getDistance())

                )
                .groupBy(place.id)
                .fetch();
    }

    private Point extractPointByAddress(String address) {

        try {
            String encodedAddress = URLEncoder.encode(address);

            String stringURL = "https://dapi.kakao.com/v2/local/search/address?query=" + encodedAddress;

            URL url = new URL(stringURL);
            String line;
            StringBuilder sb = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "KakaoAK 280f805ef93902ffb1a8a38d6308a9e0");


// API 응답메시지를 불러와서 문자열로 저장
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String text = sb.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(text);

            JsonNode documentsNode = jsonNode.get("documents");
            if (documentsNode != null && documentsNode.isArray()) {
                JsonNode documentNode = documentsNode.get(0);
                if (documentNode != null) {
                    Float xValue = (float) documentNode.get("x").asDouble();
                    Float yValue = (float) documentNode.get("y").asDouble();

                    System.out.println("x: " + xValue);
                    System.out.println("y: " + yValue);

                    Point point = new Point(xValue, yValue);

                    return point;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return null;
    }

}
