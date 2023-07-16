package com.server.pickplace.search.config;

import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.place.entity.*;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.reservation.entity.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
@Profile("!test")
@Transactional
@Slf4j
public class SearchMockUpSetting {

    @Autowired EntityManager em;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {

        Member host = hostSetting();

        Member user = userSetting();

        List<Place> placeList = placeSetting(host);

        List<Room> roomList = RoomSetting();

        List<Unit> unitList = UnitSetting();

        List<Category> categoryList = CategorySetting();

        List<Tag> tagList = tagSetting();

        List<CategoryPlace> categoryPlaceList = categoryPlaceSetting(placeList, categoryList);

        List<TagPlace> tagPlaceList = tagPlaceSetting(placeList, tagList);

        ////////////////////////////////////////////////////////////////////////////////////////////////

        roomSet(placeList, roomList);

        unitSet(roomList, unitList);

        List<Reservation> reservationList = reservationSetting(unitList, user);

        /////////////////////////////////////////////////////////////////////////////////////////////////

        em.persist(host);
        em.persist(user);
        placeList.stream().forEach(o -> em.persist(o));
        roomList.stream().forEach(o -> em.persist(o));
        unitList.stream().forEach(o -> em.persist(o));
        categoryList.stream().forEach(o -> em.persist(o));
        tagList.stream().forEach(o -> em.persist(o));
        categoryPlaceList.stream().forEach(o -> em.persist(o));
        tagPlaceList.stream().forEach(o -> em.persist(o));
        reservationList.stream().forEach(o -> em.persist(o));
    }

    private List<Reservation> reservationSetting(List<Unit> unitList, Member user) {
        List<Reservation> reservationList = new ArrayList<>();

        int i = 0;

        LocalDateTime beforeBaseDateTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
        LocalDateTime afterBaseDateTime = beforeBaseDateTime.plus(Duration.ofHours(1));

        for (Unit unit : unitList) {

            LocalDateTime beforeDateTime = beforeBaseDateTime.plus(Duration.ofHours(i));
            LocalDateTime afterDateTime = afterBaseDateTime.plus(Duration.ofHours(i));

            Reservation reservation1 = Reservation.builder()
                    .reservationNum(String.valueOf(i))
                    .peopleNum((i % 8) + 2)
                    .status(ReservationStatus.APPROVAL)
                    .startDate(LocalDate.of(2023, 7, 12))
                    .endDate(LocalDate.of(2023, 7, 12))
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(12, 0))
                    .member(user)
                    .room(unit.getRoom())
                    .unit(unit)
                    .build();

            Reservation reservation2 = Reservation.builder()
                    .reservationNum(String.valueOf(i))
                    .peopleNum((i % 8) + 2)
                    .status(ReservationStatus.APPROVAL)
                    .startDate(beforeDateTime.toLocalDate())
                    .endDate(afterDateTime.toLocalDate())
                    .startTime(beforeDateTime.toLocalTime())
                    .endTime(afterDateTime.toLocalTime())
                    .member(user)
                    .room(unit.getRoom())
                    .unit(unit)
                    .build();


            reservationList.add(reservation1);
            reservationList.add(reservation2);


            i++;



        }

        return reservationList;
    }

    private void unitSet(List<Room> roomList, List<Unit> unitList) {
        int i = 0;
        for (Unit unit : unitList) {
            unit.setRoom(roomList.get(i % 30));
            i++;
        }
    }

    private void roomSet(List<Place> placeList, List<Room> roomList) {
        int i = 0;
        for (Room room : roomList) {
            room.setPlace(placeList.get(i % 10));
            i++;
        }
    }


    private List<TagPlace> tagPlaceSetting(List<Place> placeList, List<Tag> tagList) {
        List<TagPlace> tagPlaceList = new ArrayList<>();

        Random random = new Random();

        for (Place place : placeList) {

            int i = random.nextInt(7);

            TagPlace tagPlace = TagPlace.builder().place(place).tag(tagList.get(i)).build();
            TagPlace tagPlace2 = TagPlace.builder().place(place).tag(tagList.get(i + 1)).build();

            tagPlaceList.add(tagPlace);
            tagPlaceList.add(tagPlace2);

        }

        return tagPlaceList;
    }



    private List<CategoryPlace> categoryPlaceSetting(List<Place> placeList, List<Category> categoryList) {
        List<CategoryPlace> categoryPlaceList = new ArrayList<>();

        int i = 0;
        for (Place place : placeList) {

            CategoryPlace categoryPlace = CategoryPlace.builder().place(place).category(categoryList.get(i / 2)).build();

            categoryPlaceList.add(categoryPlace);

            i++;

        }
        return categoryPlaceList;
    }

    private List<Tag> tagSetting() {
        List<Tag> tagList = new ArrayList<>();
        for (TagStatus value : TagStatus.values()) {
            Tag tag = Tag.builder().tagStatus(value).build();
            tagList.add(tag);
        }
        return tagList;
    }

    private List<Category> CategorySetting() {
        List<Category> categoryList = new ArrayList<>();
        for (CategoryStatus categoryStatus : CategoryStatus.values()) {
            Category category = Category.builder().status(categoryStatus).build();
            categoryList.add(category);
        }
        return categoryList;
    }

    private List<Unit> UnitSetting() {
        List<Unit> unitList = new ArrayList<>();

        for (int i = 0; i < 150; i++) {
            Unit unit = Unit.builder().name("호실" + String.valueOf(i)).build();
            unitList.add(unit);
        }

        return unitList;
    }

    private List<Room> RoomSetting() {
        List<Room> roomList = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            Room room = Room.builder()
                    .price(10000 * i + 1000)
                    .name("방 " + String.valueOf(i))
                    .amount(5)
                    .peopleNum(i + 1)
                    .build();
            roomList.add(room);

        }
        return roomList;
    }

    private List<Place> placeSetting(Member host) {
        Place place1 = Place.builder()
                .address("서울특별시 동대문구 이문로 107")
                .number(null)
                .name("영화장")
                .point(new Point(127.058748, 37.5974476))
                .x(127.058748)
                .y(37.5974476)
                .member(host)
                .rating((float) 35)
                .reviewCount(10)
        .build();

        Place place2 = Place.builder()
                .address("서울특별시 중구 창경궁로 62-29")
                .number(null)
                .name("우래옥")
                .point(new Point(126.998711, 37.5681704))
                .x(126.998711)
                .y(37.5681704)
                .member(host)
                .rating((float) 40)
                .reviewCount(10)
                .build();

        Place place3 = Place.builder()
                .address("서울특별시 용산구 백범로99길 50")
                .number(null)
                .name("몽탄")
                .point(new Point(126.972203, 37.5360194))
                .x(126.972203)
                .y(37.5360194)
                .member(host)
                .rating((float) 31)
                .reviewCount(10)
                .build();

        Place place4 = Place.builder()
                .address("서울특별시 용산구 백범로99길 50")
                .number(null)
                .name("꿉당")
                .point(new Point(127.018972, 37.5166142))
                .x(127.018972)
                .y(37.5166142)
                .member(host)
                .rating((float) 32)
                .reviewCount(10)
                .build();

        Place place5 = Place.builder()
                .address("서울특별시 용산구 남영동 한강대로80길 17")
                .number(null)
                .name("남영돈")
                .point(new Point(126.973762, 37.5427322))
                .x(126.973762)
                .y(37.5427322)
                .member(host)
                .rating((float) 33)
                .reviewCount(10)
                .build();

        Place place6 = Place.builder()
                .address("서울특별시 용산구 백범로99길 51")
                .number(null)
                .name("금돼지식당")
                .point(new Point(127.011648, 37.5570878))
                .x(127.011648)
                .y(37.5570878)
                .member(host)
                .rating((float) 34)
                .reviewCount(10)
                .build();

        Place place7 = Place.builder()
                .address("서울특별시 동작구 사당제1동 동작대로7길 19")
                .number(null)
                .name("전주전집")
                .point(new Point(126.980636, 37.4796366))
                .x(126.980636)
                .y(37.4796366)
                .member(host)
                .rating((float) 36)
                .reviewCount(10)
                .build();

        Place place8 = Place.builder()
                .address("서울특별시 성동구 아차산로7길 7")
                .number(null)
                .name("성수족발")
                .point(new Point(127.054194, 37.5460032))
                .x(127.054194)
                .y(37.5460032)
                .member(host)
                .rating((float) 37)
                .reviewCount(10)
                .build();

        Place place9 = Place.builder()
                .address("서울특별시 서초대로77길 54")
                .number(null)
                .name("하이디라오 서초")
                .point(new Point(127.024714, 37.5025847))
                .x(127.024714)
                .y(37.5025847)
                .member(host)
                .rating((float) 38)
                .reviewCount(10)
                .build();

        Place place0 = Place.builder()
                .address("서울특별시 용산구 남영동 89-5")
                .number(null)
                .name("화양연가")
                .point(new Point(126.973412, 37.5415969))
                .x(126.973412)
                .y(37.5415969)
                .member(host)
                .rating((float) 39)
                .reviewCount(10)
                .build();

        List<Place> places = Arrays.asList(place0, place1, place2, place3, place4, place5, place6, place7, place8, place9);

        return places;

    }

    private Member userSetting() {
        Member user = Member.builder()
        .email("dce@nate.com")
        .password("1q2w3e4r!")
        .number("01090123456")
        .name("abcdefgh")
        .role(MemberRole.USER)
        .build();

        return user;
    }

    private Member hostSetting() {
        Member host = Member.builder()
        .email("abc@naver.com")
        .password("1q2w3e4r!")
        .number("01012345678")
        .name("선빈")
        .role(MemberRole.HOST)
        .build();

        return host;
    }

}
