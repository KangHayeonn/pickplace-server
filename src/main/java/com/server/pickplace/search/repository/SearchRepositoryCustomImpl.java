package com.server.pickplace.search.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import java.util.*;

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


    @Override
    public Map<Long, Integer> getRoomUnitCountMap(DetailPageRequest detailPageRequest, Long placeId) {

        Map<Long, Integer> roomUnitCountMap = new HashMap<>();

        List<Tuple> roomUnitCountMapTupleList = queryFactory
                .select(room.id, room.amount.sum())
                .from(room)
                .join(room.place, place)
                .where(place.id.eq(placeId))
                .groupBy(room.id)
                .fetch();

        for (Tuple tuple : roomUnitCountMapTupleList) {

            Long roomId = tuple.get(0, Long.class);
            Integer unitCount = tuple.get(1, Integer.class);

            roomUnitCountMap.put(roomId, unitCount);
        }

        return roomUnitCountMap;
    }

    @Override
    public Map<Long, Integer> getUnableRoomCountMap(DetailPageRequest detailPageRequest, Long placeId) {

        Map<Long, Integer> unableRoomCountMap = new HashMap<>();

        List<Tuple> unableRoomCountTupleList = queryFactory
                .select(room.id, unit.id.countDistinct())
                .from(room)
                .join(room.place, place)
                .join(reservation.room, room)
                .join(reservation.unit, unit)
                .where(

                        dateCheckByRequest(detailPageRequest),

                        timeCheckByRequest(detailPageRequest)
                )
                .groupBy(room.id)
                .fetch();

        for (Tuple tuple : unableRoomCountTupleList) {

            Long roomId = tuple.get(0, Long.class);
            Integer unitCount = tuple.get(1, Integer.class);

            unableRoomCountMap.put(roomId, unitCount);

        }

        return unableRoomCountMap;
    }

    @Override
    public Slice<PlaceResponse> findSliceByDto(CategorySearchRequest categorySearchRequest, Pageable pageable) {

        Point point = extractPointByAddress(categorySearchRequest.getAddress());

        List<Tuple> roomCountTupleList = getRoomCountTupleListByCategoryDto(categorySearchRequest, point);

        HashMap<Long, Integer> roomCountMap = getRoomCountMapByRoomCountTupleList(roomCountTupleList);

        List<Long> placeBeforeList = getPlaceBeforeListByRoomCountTupleList(roomCountTupleList);

        List<Tuple> placeReservationCountTupleList = getPlaceReservationCountTupleListByCategoryDto(categorySearchRequest, placeBeforeList);

        List<Long> placeIdList = getPlaceIdListByRoomCountMapAndPlaceReservationCountTupleList(roomCountMap, placeReservationCountTupleList);

        List<PlaceResponse> placeResponseList = getPlaceResponseListByPageableAndPlaceIdList(pageable, placeIdList, categorySearchRequest);

        boolean hasNext = getPageableByPlaceResponseList(pageable, placeResponseList);

        return new SliceImpl<>(placeResponseList, pageable, hasNext);

    }


    @Override
    public Slice<PlaceResponse> findSliceByDto(BasicSearchRequest basicSearchRequest, Pageable pageable) {

        Point point = extractPointByAddress(basicSearchRequest.getAddress());

        List<Tuple> roomCountTupleList = getRoomCountTupleListByBasicSearchDto(basicSearchRequest, point);

        HashMap<Long, Integer> roomCountMap = getRoomCountMapByRoomCountTupleList(roomCountTupleList);

        List<Long> placeBeforeList = getPlaceBeforeListByRoomCountTupleList(roomCountTupleList);

        List<Tuple> placeReservationCountTupleList = getPlaceReservationCountTupleListByBasicSearchDto(basicSearchRequest, placeBeforeList);

        List<Long> placeIdList = getPlaceIdListByRoomCountMapAndPlaceReservationCountTupleList(roomCountMap, placeReservationCountTupleList);

        List<PlaceResponse> placeResponseList = getPlaceResponseListByPageableAndPlaceIdList(pageable, placeIdList, basicSearchRequest);

        boolean hasNext = getPageableByPlaceResponseList(pageable, placeResponseList);

        return new SliceImpl<>(placeResponseList, pageable, hasNext);

    }


    @Override
    public Slice<PlaceResponse> findSliceByDto(DetailSearchRequest detailSearchRequest, Pageable pageable) {

        Point point = extractPointByAddress(detailSearchRequest.getAddress());

        List<Tuple> roomCountTupleList = getRoomCountTupleListByDetailSearchDto(detailSearchRequest, point);

        Integer tagAmount = detailSearchRequest.getTagId().size();

        HashMap<Long, Integer> roomCountMap = getRoomCountMapByRoomCountTupleListInDetail(roomCountTupleList, tagAmount);

        List<Long> placeBeforeList = getPlaceBeforeListByRoomCountTupleListInDetail(roomCountTupleList, tagAmount);

        List<Tuple> placeReservationCountTupleList = getPlaceReservationCountTupleListByDetailDto(detailSearchRequest, placeBeforeList);

        List<Long> placeIdList = getPlaceIdListByRoomCountMapAndPlaceReservationCountTupleList(roomCountMap, placeReservationCountTupleList);

        List<PlaceResponse> placeResponseList = getPlaceResponseListByPageableAndPlaceIdList(pageable, placeIdList, detailSearchRequest);

        boolean hasNext = getPageableByPlaceResponseList(pageable, placeResponseList);

        return new SliceImpl<>(placeResponseList, pageable, hasNext);

    }

    private List<Tuple> getPlaceReservationCountTupleListByDetailDto(DetailSearchRequest detailSearchRequest, List<Long> placeBeforeList) {
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

                        dateCheckByRequest(detailSearchRequest)

                )
                .groupBy(place.id)
                .fetch();
        return placeReservationCountTupleList;
    }

    private List<Long> getPlaceBeforeListByRoomCountTupleListInDetail(List<Tuple> roomCountTupleList, Integer tagAmount) {
        List<Long> placeBeforeList = new ArrayList<>();

        for (Tuple tuple : roomCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer tagCount = tuple.get(1, Integer.class);

            if (tagCount == tagAmount) {
                placeBeforeList.add(placeId);
            }

        }
        return placeBeforeList;
    }

    private HashMap<Long, Integer> getRoomCountMapByRoomCountTupleListInDetail(List<Tuple> roomCountTupleList, Integer tagAmount) {
        HashMap<Long, Integer> roomCountMap = new HashMap<>();

        for (Tuple tuple : roomCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer tagCount = tuple.get(1, Integer.class);
            Integer roomCount = tuple.get(2, Integer.class);

            if (tagCount == tagAmount) {
                roomCountMap.put(placeId, roomCount / tagAmount);
            }

        }
        return roomCountMap;
    }

    private List<Tuple> getRoomCountTupleListByDetailSearchDto(DetailSearchRequest detailSearchRequest, Point point) {
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
        return roomCountTupleList;
    }

    private List<Tuple> getPlaceReservationCountTupleListByBasicSearchDto(BasicSearchRequest basicSearchRequest, List<Long> placeBeforeList) {
        List<Tuple> placeReservationCountTupleList = queryFactory
                .select(place.id, unit.id.countDistinct())
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(reservation.unit, unit)
                .where(

                        place.id.in(placeBeforeList),

                        dateCheckByRequest(basicSearchRequest)

                )
                .groupBy(place.id)
                .fetch();
        return placeReservationCountTupleList;
    }

    private List<Tuple> getRoomCountTupleListByBasicSearchDto(BasicSearchRequest basicSearchRequest, Point point) {
        List<Tuple> roomCountTupleList = queryFactory
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
        return roomCountTupleList;
    }

    private boolean getPageableByPlaceResponseList(Pageable pageable, List<PlaceResponse> placeResponseList) {
        boolean hasNext = false;
        if (placeResponseList.size() > pageable.getPageSize()) {
            placeResponseList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }

    private List<PlaceResponse> getPlaceResponseListByPageableAndPlaceIdList(Pageable pageable, List<Long> placeIdList, NormalSearchRequest request) {
        List<Place> placeList = new ArrayList<>();
        List<PlaceResponse> placeResponseList = new ArrayList<>();

        List<Tuple> placeRoomPriceMinTuple = queryFactory
                .select(place, room.price.min())
                .from(place)
                .join(room.place, place)
                .where(place.id.in(placeIdList))
                .groupBy(place.id)
                .orderBy(eqRecommend(request), eqLowPrice(request), eqHighPrice(request))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        for (Tuple tuple : placeRoomPriceMinTuple) {
            Place place = tuple.get(0, Place.class);
            placeList.add(place);
        }

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


        return placeResponseList;
    }

    private OrderSpecifier<Float> eqRecommend(NormalSearchRequest request) {

        if (request.getSearchType().equals("추천 순")) {
            return place.rating.desc();
        }

        return null;
    }

    private OrderSpecifier<Integer> eqLowPrice(NormalSearchRequest request) {

        if (request.getSearchType().equals("낮은 가격순")) {
            return room.price.asc();
        }

        return null;
    }

    private OrderSpecifier<Integer> eqHighPrice(NormalSearchRequest request) {

        if (request.getSearchType().equals("높은 가격순")) {
            return room.price.desc();
        }

        return null;
    }

    private List<Long> getPlaceIdListByRoomCountMapAndPlaceReservationCountTupleList(HashMap<Long, Integer> roomCountMap, List<Tuple> placeReservationCountTupleList) {
        List<Long> placeIdList = new ArrayList<>();

        for (Tuple tuple : placeReservationCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer reservationRoomCount = tuple.get(1, Integer.class);
            Integer totalRoomCount = roomCountMap.get(placeId);

            if (reservationRoomCount < totalRoomCount) {
                placeIdList.add(placeId);
            }
        }
        return placeIdList;
    }

    private List<Tuple> getPlaceReservationCountTupleListByCategoryDto(CategorySearchRequest categorySearchRequest, List<Long> placeBeforeList) {
        List<Tuple> placeReservationCountTupleList = queryFactory
                .select(place.id, unit.id.countDistinct())
                .from(reservation)
                .join(reservation.room, room)
                .join(room.place, place)
                .join(place, categoryPlace.place)
                .join(reservation.unit, unit)
                .where(

                        place.id.in(placeBeforeList),

                        dateCheckByRequest(categorySearchRequest)


                )
                .groupBy(place.id)
                .fetch();
        return placeReservationCountTupleList;
    }

    private List<Long> getPlaceBeforeListByRoomCountTupleList(List<Tuple> roomCountTupleList) {
        List<Long> placeBeforeList = new ArrayList<>();

        for (Tuple tuple : roomCountTupleList) {
            Long placeId = tuple.get(0, Long.class);

            placeBeforeList.add(placeId);

        }
        return placeBeforeList;
    }

    private HashMap<Long, Integer> getRoomCountMapByRoomCountTupleList(List<Tuple> roomCountTupleList) {
        HashMap<Long, Integer> roomCountMap = new HashMap<>();

        for (Tuple tuple : roomCountTupleList) {
            Long placeId = tuple.get(0, Long.class);
            Integer roomCount = tuple.get(1, Integer.class);

            roomCountMap.put(placeId, roomCount);
        }
        return roomCountMap;
    }

    private List<Tuple> getRoomCountTupleListByCategoryDto(CategorySearchRequest categorySearchRequest, Point point) {
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
        return roomCountTupleList;
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

    private BooleanExpression dateCheckByRequest(SearchRequest request) {


        BooleanExpression cond1 = reservation.startDate.goe(request.getStartDate()).and(reservation.startDate.lt(request.getEndDate()));
        BooleanExpression cond2 = reservation.endDate.gt(request.getStartDate()).and(reservation.endDate.loe(request.getEndDate()));
        BooleanExpression condition = cond1.or(cond2);

        return condition;
    }

    private BooleanExpression timeCheckByRequest(DetailPageRequest request) {


        BooleanExpression cond1 = reservation.startTime.goe(request.getStartTime()).and(reservation.startTime.lt(request.getEndTime()));
        BooleanExpression cond2 = reservation.endTime.gt(request.getStartTime()).and(reservation.endTime.loe(request.getEndTime()));
        BooleanExpression condition = cond1.or(cond2);

        return condition;
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
