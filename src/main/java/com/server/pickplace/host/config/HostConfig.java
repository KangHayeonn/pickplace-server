package com.server.pickplace.host.config;

import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.reservation.entity.ReservationStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.Point;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class HostConfig {

    @Autowired EntityManager em;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initialize() {
        Member host1 = Member.builder()
                .email("abc@naver.com")
                .password("1q2w3e4r!")
                .number("01012345678")
                .name("선빈")
                .role(MemberRole.HOST)
                .build();

        Member user1 = Member.builder()
                .email("dce@nate.com")
                .password("1q2w3e4r!")
                .number("01090123456")
                .name("abcdefgh")
                .role(MemberRole.USER)
                .build();

        em.persist(host1);
        em.persist(user1);

        Place place1 = Place.builder()
                .address("서울특별시 동대문구 이문로 107")
                .number(null)
                .name("영화장")
                .point(new Point(127.011803, 38.478694))
                .member(host1)
                .build();

        Place place2 = Place.builder()
                .address("서울특별시 동대문구 경희대로 26")
                .number(null)
                .name("맥날")
                .point(new Point(127.022303, 38.445694))
                .member(host1)
                .build();

        em.persist(place1);
        em.persist(place2);

        Room room1 = Room.builder()
                .price(10000)
                .name("영화장 실내")
                .amount(3)
                .peopleNum(5)
                .place(place1)
                .build();

        Room room2 = Room.builder()
                .price(20000)
                .name("영화장 실외")
                .amount(5)
                .peopleNum(7)
                .place(place1)
                .build();

        Room room3 = Room.builder()
                .price(30000)
                .name("맥날 실내")
                .amount(2)
                .peopleNum(3)
                .place(place2)
                .build();

        Room room4 = Room.builder()
                .price(40000)
                .name("맥날 실외")
                .amount(4)
                .peopleNum(6)
                .place(place2)
                .build();

        em.persist(room1);
        em.persist(room2);
        em.persist(room3);
        em.persist(room4);


        Reservation reservation1 = Reservation.builder()
                .reservationNum("ff112233")
                .peopleNum(1)
                .status(ReservationStatus.APPROVAL)
                .startDate(LocalDate.of(2023, 6, 1))
                .startTime(LocalTime.of(11, 00))
                .endDate(LocalDate.of(2023, 6, 2))
                .endTime(LocalTime.of(12, 00))
                .member(user1)
                .room(room1)
                .build();


        Reservation reservation2 = Reservation.builder()
                .reservationNum("ff112233")
                .peopleNum(2)
                .status(ReservationStatus.PAYMENT)
                .startDate(LocalDate.of(2023, 8, 1))
                .startTime(LocalTime.of(11, 00))
                .endDate(LocalDate.of(2023, 8, 2))
                .endTime(LocalTime.of(12, 00))
                .member(user1)
                .room(room2)
                .build();


        em.persist(reservation1);
        em.persist(reservation2);

    }

}
