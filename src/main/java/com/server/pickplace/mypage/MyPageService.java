package com.server.pickplace.mypage;

import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import com.server.pickplace.mypage.dto.MyPageReservationMoreResponseDto;
import com.server.pickplace.mypage.dto.MyPageReservationResponseDto;
import com.server.pickplace.mypage.dto.PlaceAddressDto;
import com.server.pickplace.mypage.repository.MyPageReservationRepository;
import com.server.pickplace.mypage.repository.MyPageReviewRepository;
import com.server.pickplace.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MyPageReservationRepository reservationRepository;
    private final MyPageReviewRepository reviewRepository;

    private final ModelMapper modelMapper;

    public List<MyPageReservationResponseDto> reservationDetails (HttpServletRequest httpServletRequest, Long id){
        Optional<List<Reservation>> reservationDetail = reservationRepository.findReservationListByMemberId(id);

        if(reservationDetail==null){
            throw new MemberException(MemberErrorResult.NOT_RESERVATION);
        }

        List<MyPageReservationResponseDto> responseDto = reservationDetail.get().stream().map(reservation -> modelMapper.map(reservation, MyPageReservationResponseDto.class))
                .collect(Collectors.toList());

        for (int i =0 ; i< responseDto.size() ; i ++ ){
            responseDto.get(i).setUpdateDate(reservationDetail.get().get(i).getUpdatedDate().toString());
            responseDto.get(i).setPlaceName(reservationDetail.get().get(i).getRoom().getPlace().getName());
            responseDto.get(i).setPlaceId(reservationDetail.get().get(i).getRoom().getPlace().getId());
        }

        return responseDto;

    }

    public List<MyPageReservationMoreResponseDto> reservationDetailsMore (HttpServletRequest httpServletRequest, Long id){
        Optional<List<Reservation>> reservationDetailsMore = reservationRepository.findReservationListById(id);

        if(reservationDetailsMore==null){
            throw new MemberException(MemberErrorResult.NOT_RESERVATION);
        }


        System.out.println();

        List<MyPageReservationMoreResponseDto> responseDto = reservationDetailsMore.get().stream().map(reservation -> modelMapper.map(reservation, MyPageReservationMoreResponseDto.class))
                .collect(Collectors.toList());

        for (int i =0 ; i< responseDto.size() ; i ++ ){

            Reservation data = reservationDetailsMore.get().get(i);

            PlaceAddressDto place = new PlaceAddressDto();
            place.setAddress(data.getRoom().getPlace().getAddress().toString());
            place.setLatitude(data.getRoom().getPlace().getX());
            place.setLongitude(data.getRoom().getPlace().getY());

            responseDto.get(i).setNickname(reservationDetailsMore.get().get(i).getMember().getName());
            responseDto.get(i).setUpdateDate(data.getUpdatedDate().toString());
            responseDto.get(i).setPlaceName(data.getRoom().getPlace().getName());
            responseDto.get(i).setPlaceId(data.getRoom().getPlace().getId());
            responseDto.get(i).setUserId(data.getMember().getId());
            responseDto.get(i).setPlacePhone(data.getRoom().getPlace().getNumber());
            responseDto.get(i).setPlaceRating(data.getRoom().getPlace().getRating());
            responseDto.get(i).setPlaceAddress(place);


            Long reservationId = reservationDetailsMore.get().get(i).getId();

            //리뷰 갯수
            responseDto.get(i).setPlaceReviewCnt(reviewRepository.findReviewListByReservationId(reservationId).get().size());
            responseDto.get(i).setReviewExistence(!reviewRepository.findReviewListByReservationId(reservationId).get().isEmpty());



            responseDto.get(i).setPersonnel(reservationDetailsMore.get().get(i).getRoom().getPeopleNum());

        }

        return responseDto;

    }
}
