package com.server.pickplace.member.service;

import com.server.pickplace.host.dto.ReservationResponse;
import com.server.pickplace.member.dto.InfoNicknameRequestDto;
import com.server.pickplace.member.dto.InfoPhoneRequestDto;
import com.server.pickplace.member.dto.InfoResponseDto;
import com.server.pickplace.member.dto.LoginResponseDto;
import com.server.pickplace.member.dto.mypageDto.MemberReservationResponseDto;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.repository.MemberReservationRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import com.server.pickplace.reservation.entity.Reservation;
import io.jsonwebtoken.io.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Base64.getUrlDecoder;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberInfoService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MemberReservationRepository memberReservationRepository;
    private final ModelMapper modelMapper;

    public Map<String, Object> info(HttpServletRequest httpServletRequest, Long memberId){

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) httpServletRequest); //access Token 가져옴

        Map<String, Object> InfoMap = new HashMap<>();

        //유효한 토큰만 통과 // 아닌건 예외처리
        if (token != null && jwtTokenProvider.validateToken(token)) {

            //존재하지 않는 회원 예외 처리
            memberRepository.findById(memberId).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

            String payloadJWT = token.split("\\.")[1];
            Base64.Decoder decoder = getUrlDecoder();

            String payload = new String(decoder.decode(payloadJWT));
            JsonParser jsonParser = new BasicJsonParser();
            Map<String, Object> jsonArray = jsonParser.parseMap(payload);
            String email = (String) jsonArray.get("sub");

            // 토큰의 주인과 id가 일치하는지 확인후 예외처리
            if(!memberRepository.findById(memberId).get().getEmail().equals(email)){
                throw new MemberException(MemberErrorResult.NOT_AUTHENTICATION);
            }

            InfoResponseDto infoResponseDto = InfoResponseDto.builder()
                    .email(memberRepository.findById(memberId).get().getEmail())
                    .phone(memberRepository.findById(memberId).get().getNumber())
                    .nickname(memberRepository.findById(memberId).get().getName())
                    .build();

            InfoMap.put("member", infoResponseDto);

            //로그인 성공 api 전송
            return InfoMap;

        }
        return InfoMap;
    }

    public void phoneUpdate(HttpServletRequest httpServletRequest, InfoPhoneRequestDto requestDto){

        Long id = requestDto.getMemberId();
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        checkInfoValid(httpServletRequest,id);
        //비밀번호만 변경
        member.setNumber(requestDto.getPhone());

    }

    public void nicknameUpdate(HttpServletRequest httpServletRequest, InfoNicknameRequestDto requestDto){

        Long id = requestDto.getMemberId();
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        checkInfoValid(httpServletRequest,id);
        //비밀번호만 변경
        member.setName(requestDto.getNickname());

    }

    public void checkInfoValid (HttpServletRequest httpServletRequest, Long id){
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) httpServletRequest); //access Token 가져옴


        //유효한 토큰만 통과 // 아닌건 예외처리
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //존재하지 않는 회원
//            Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
            String payloadJWT = token.split("\\.")[1];
            Base64.Decoder decoder = getUrlDecoder();

            String payload = new String(decoder.decode(payloadJWT));
            JsonParser jsonParser = new BasicJsonParser();
            Map<String, Object> jsonArray = jsonParser.parseMap(payload);
            String email = (String) jsonArray.get("sub");

            // 토큰의 주인과 id가 일치하는지 확인후 예외처리
            if(!memberRepository.findById(id).get().getEmail().equals(email)){
                throw new MemberException(MemberErrorResult.NOT_AUTHENTICATION);
            }
        }
    }

    public String reservationDetails (HttpServletRequest httpServletRequest, Long id){
//        List<Reservation> reservationDetail = memberReservationRepository.findByMember_Id(id);

        Optional<List<Reservation>> reservationDetail = memberReservationRepository.findReservationListByMemberId(id);
        System.out.println(reservationDetail.get());
//
//        if(reservationDetail==null){
//            throw new MemberException(MemberErrorResult.MEMBER_NOT_FOUND); // 예약 내역이 없습니다 처리
//        }
//
//        List<Reservation> reservations = reservationDetail.get();
//
//        List<MemberReservationResponseDto> responseDto = reservations.stream().map(reservation -> modelMapper.map(reservation, MemberReservationResponseDto.class))
//                .collect(Collectors.toList());

//        System.out.println(responseDto.get(0).getPlaceId());


//        Map< Object, Object > reservationMap = new HashMap<>();

//        reservationMap.put("reservationDetails",)
//        for(int i=0 ; i<reservationDetail.size();i++){
//            reservationMap.put(i,reser)
//
//        }


//        System.out.println(reservationDetail.get(0).getMember().getId());
//
//        System.out.println(reservationDetail.get().get(0));
//        .orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND)); //존재하지 않는 아이디 예외처리
//        List<Object[]> reservationAndNames = reservationDetail.
//
//        for (Object[] reservationsAndName : reservationAndNames) {
//
//            Reservation reservation = (Reservation) reservationsAndName[0];
//            String key = (String) reservationsAndName[1];
//            ReservationResponse reservationResponse = modelMapper.map(reservation, ReservationResponse.class);
//
//            List<ReservationResponse> reservations = map.getOrDefault(key, new ArrayList<>());
//            reservations.add(reservationResponse);
//
//            map.put(key, reservations);
//
//        }
//        System.out.println(reservationDetail);

        return "goof";

    }
}


