package com.server.pickplace.reservation.service;

import com.server.pickplace.reservation.repository.ReservationRepository;
import com.server.pickplace.search.repository.SearchRepository;
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
}
