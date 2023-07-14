package com.server.pickplace.host.Integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.pickplace.host.dto.PlaceResponse;
import com.server.pickplace.host.dto.ReservationResponse;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.reservation.entity.ReservationStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class IntegrationTest {

//    @PersistenceContext EntityManager em;
    @Autowired MockMvc mvc;
    @Autowired ModelMapper modelMapper;

    private final String hostJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6ImFiY0BuYXZlci5jb20ifQ.QptS0V6x0RPP-MgXqKSYMaK-vIq0FTAaLGxeWIkNvo4";
    private final String userJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6ImRjZUBuYXRlLmNvbSJ9.lX3cyoaLodRv0VOab5DWYB_UKYvgPfMaCFPRIHouqNo";
    ObjectMapper objectMapper = new ObjectMapper();

//    @Test
//    void 공간관리페이지정상작동() throws Exception {
//
//        //given
//        MvcResult result = mvc.perform(get("/api/v1/host/place"
//                        ).header(HttpHeaders.AUTHORIZATION, hostJwt)
//                ).andExpect(status().isOk())
//                .andReturn();
//
//        //when
//        String responseJson = result.getResponse().getContentAsString();
//        JSONObject responseObject = new JSONObject(responseJson);
//        JSONArray jsonArray = responseObject.getJSONArray("data");
//
//
//        //then
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            PlaceResponse placeResponse = objectMapper.readValue(jsonObject.toString(), PlaceResponse.class);
//
//            System.out.println("placeResponse = " + placeResponse);
//
//            assertThat(placeResponse.getAddress()).isNotNull();
//            assertThat(placeResponse.getId()).isNotNull();
//            assertThat(placeResponse.getName()).isNotNull();
//
//        }
//
//    }
//
//    @BeforeEach
//    @Test
//    void setUp() throws Exception {
//
//        Member host1 = Member.builder()
//                .email("abc@naver.com")
//                .password("1q2w3e4r!")
//                .number("01012345678")
//                .name("선빈")
//                .role(MemberRole.HOST)
//                .build();
//
//        Member user1 = Member.builder()
//                .email("dce@nate.com")
//                .password("1q2w3e4r!")
//                .number("01090123456")
//                .name("abcdefgh")
//                .role(MemberRole.USER)
//                .build();
//
//        em.persist(host1);
//        em.persist(user1);
//
//        Place place1 = Place.builder()
//                .address("서울특별시 동대문구 이문로 107")
//                .number(null)
//                .name("영화장")
//                .point(new Point(127.011803, 38.478694))
//                .member(host1)
//                .build();
//
//        Place place2 = Place.builder()
//                .address("서울특별시 동대문구 경희대로 26")
//                .number(null)
//                .name("맥날")
//                .point(new Point(127.022303, 38.445694))
//                .member(host1)
//                .build();
//
//        em.persist(place1);
//        em.persist(place2);
//
//        Room room1 = Room.builder()
//                .price(10000)
//                .name("영화장 실내")
//                .amount(3)
//                .peopleNum(5)
//                .place(place1)
//                .build();
//
//        Room room2 = Room.builder()
//                .price(20000)
//                .name("영화장 실외")
//                .amount(5)
//                .peopleNum(7)
//                .place(place1)
//                .build();
//
//        Room room3 = Room.builder()
//                .price(30000)
//                .name("맥날 실내")
//                .amount(2)
//                .peopleNum(3)
//                .place(place2)
//                .build();
//
//        Room room4 = Room.builder()
//                .price(40000)
//                .name("맥날 실외")
//                .amount(4)
//                .peopleNum(6)
//                .place(place2)
//                .build();
//
//        em.persist(room1);
//        em.persist(room2);
//        em.persist(room3);
//        em.persist(room4);
//
//
//        Reservation reservation1 = Reservation.builder()
//                .reservationNum("ff112233")
//                .peopleNum(1)
//                .status(ReservationStatus.APPROVAL)
//                .startDate(LocalDate.of(2023, 6, 1))
//                .startTime(LocalTime.of(11, 00))
//                .endDate(LocalDate.of(2023, 6, 2))
//                .endTime(LocalTime.of(12, 00))
//                .member(user1)
//                .room(room1)
//                .build();
//
//
//        Reservation reservation2 = Reservation.builder()
//                .reservationNum("ff112233")
//                .peopleNum(2)
//                .status(ReservationStatus.PAYMENT)
//                .startDate(LocalDate.of(2023, 8, 1))
//                .startTime(LocalTime.of(11, 00))
//                .endDate(LocalDate.of(2023, 8, 2))
//                .endTime(LocalTime.of(12, 00))
//                .member(user1)
//                .room(room2)
//                .build();
//
//
//        em.persist(reservation1);
//        em.persist(reservation2);
//
//        em.clear();
//
//    }

}
