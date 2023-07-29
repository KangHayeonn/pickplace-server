package com.server.pickplace.mypage;

import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import com.server.pickplace.mypage.dto.MyPageReservationResponseDto;
import com.server.pickplace.mypage.repository.MyPageReservationRepository;
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
}
