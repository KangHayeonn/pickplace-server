package com.server.pickplace.reservation.service;

import com.server.pickplace.reservation.dto.AccountPayRequest;
import com.server.pickplace.reservation.dto.CardInfoResponse;
import com.server.pickplace.reservation.dto.CardPayRequest;
import com.server.pickplace.reservation.error.ReservationErrorResult;
import com.server.pickplace.reservation.error.ReservationException;
import com.server.pickplace.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    public Map<String, Object> getReservationPageMapByEmailAndRoomId(String email, Long roomId) {

        Map<String, Object> reservationPageMapByEmailAndRoomId = reservationRepository.getReservationPageMapByEmailAndRoomId(email, roomId);// 그냥 위임만

        return reservationPageMapByEmailAndRoomId;
    }

    public CardInfoResponse getCardInfoDto(String email, String cardNum) {

        String memberName = reservationRepository.findMemberNameByEmail(email);

        CardInfoResponse cardInfoResponse = CardInfoResponse.builder().name(memberName).number(cardNum).build();

        return cardInfoResponse;
    }

    public void payByCardAndReservation(String email, CardPayRequest cardPayRequest) {

        // 결제
        String inputCardPassword = cardPayRequest.getCardPassword();

        String memberPassword = reservationRepository.findMemberPasswordByEmail(email);

        if (!inputCardPassword.equals(memberPassword)) {
            throw new ReservationException(ReservationErrorResult.WRONG_CARD_PASSWORD);
        }

        // 예약
        reservationRepository.makeReservation(email, cardPayRequest);

    }


    public void payByAccountAndReservation(String email, AccountPayRequest accountPayRequest) {

        // 결제
        String inputAccountPassword = accountPayRequest.getAccountPassword();

        String memberPassword = reservationRepository.findMemberNameByEmail(email);

        if (!inputAccountPassword.equals(memberPassword)) {
            throw new ReservationException(ReservationErrorResult.WRONG_CARD_PASSWORD);
        }

        // 예약
        reservationRepository.makeReservation(email, accountPayRequest);

    }
}
