package com.server.pickplace.host.service;

import com.server.pickplace.host.dto.*;
import com.server.pickplace.host.error.HostErrorResult;
import com.server.pickplace.host.error.HostException;
import com.server.pickplace.host.repository.HostRepository;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import com.server.pickplace.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HostService {

    private final HostRepository hostRepository;
    private final ModelMapper modelMapper;

    public List<PlaceResponse> findPlaceDtoListByEmail(String email) {

        Optional<List<Place>> optionalPlaces = hostRepository.findPlaceListByEmail(email);

        List<PlaceResponse> placeDtos = new ArrayList<>();

        if (optionalPlaces.isPresent()) {

            List<Place> places = optionalPlaces.get();

            for (Place place : places) {

                placeDtos.add(
                        modelMapper.map(place, PlaceResponse.class)
                );

            }
            return placeDtos;

        } else {
            return null;
        }

    }

    public List<RoomResponse> findRoomDtoListByPlaceId(Long placeId) {

        Optional<List<Room>> optionalRooms = hostRepository.findOptionalRoomListByPlaceId(placeId);

        List<RoomResponse> roomDtos = new ArrayList<>();

        if (optionalRooms.isPresent()) {

            List<Room> rooms = optionalRooms.get();

            for (Room room : rooms) {

                roomDtos.add(
                        modelMapper.map(room, RoomResponse.class)
                );

            }

            return roomDtos;

        } else {
            return null;
        }

    }

    public PlaceResponse findPlaceDtoByPlaceId(Long placeId) {

        Optional<Place> optionalPlace = hostRepository.findOptionalPlaceByPlaceId(placeId);

        if (optionalPlace.isPresent()) {

            Place place = optionalPlace.get();

            PlaceResponse placeResponse = modelMapper.map(place, PlaceResponse.class);

            return placeResponse;
        } else {
            throw new HostException(HostErrorResult.NOT_EXIST_PLACE);
        }


    }

    public List<ReservationResponse> findReservationDtoListByPlaceId(Long placeId) {

        Optional<List<Reservation>> optionalReservations = hostRepository.findOptionalReservationListByPlaceId(placeId, LocalDate.now());

        List<ReservationResponse> reservationDtos = new ArrayList<>();

        if (optionalReservations.isPresent()) {

            List<Reservation> reservations = optionalReservations.get();

            for (Reservation reservation : reservations) {

                reservationToDtos(reservationDtos, reservation);
            }

            return reservationDtos;

        } else {
            return null;
        }
    }


    public Map<String, List<ReservationResponse>> createReservationDtoMapByEmail(String email) {

        Optional<List<Object[]>> optionalReservationsAndNames = hostRepository.findOptionalReservationAndNamesByEmail(email, LocalDate.now());

        Map<String, List<ReservationResponse>> map = new HashMap<>();

        if (optionalReservationsAndNames.isPresent()) {

            List<Object[]> reservationAndNames = optionalReservationsAndNames.get();

            for (Object[] reservationsAndName : reservationAndNames) {

                Reservation reservation = (Reservation) reservationsAndName[0];
                String key = (String) reservationsAndName[1];
                ReservationResponse reservationResponse = reservationToDto(reservation);

                List<ReservationResponse> reservations = map.getOrDefault(key, new ArrayList<>());
                reservations.add(reservationResponse);

                map.put(key, reservations);

            }

            return map;

        } else {
            return null;
        }
    }

    public Map<String, Object> getMemberReservationPlaceDtoMapByReservationId(Long reservationId) {

        Optional<Object[]> optionalMemberReservationPlaceList = hostRepository.findOptionalMemberReservationPlaceListByReservationId(reservationId); // reservationId만 유효하다면, 셋 다 존재해야함

        if (optionalMemberReservationPlaceList.isEmpty()) {
            throw new HostException(HostErrorResult.NOT_EXIST_RESERVATION);
        }

        Object[] memberReservationPlaceList = optionalMemberReservationPlaceList.get();

        Member member = (Member) memberReservationPlaceList[0];
        Reservation reservation = (Reservation) memberReservationPlaceList[1];
        Place place = (Place) memberReservationPlaceList[2];

        MemberResponse MemberDto = MemberResponse.builder()
                .name(member.getName()).build();

        ReservationResponse reservationDto = reservationToDto(reservation);
        reservationDto.setCreatedDate(reservation.getCreatedDate());
        reservationDto.setPeopleNum(reservation.getPeopleNum());

        PlaceResponse placeDto = modelMapper.map(place, PlaceResponse.class);

        Map<String, Object> map = new HashMap<>();

        map.put("member", MemberDto);
        map.put("reservation", reservationDto);
        map.put("place", placeDto);

        return map;

    }

    private void reservationToDtos(List<ReservationResponse> reservationDtos, Reservation reservation) {
        LocalDateTime checkIn = LocalDateTime.of(reservation.getStartDate(), reservation.getStartTime());
        LocalDateTime checkOut = LocalDateTime.of(reservation.getEndDate(), reservation.getEndTime());

        reservationDtos.add(
                ReservationResponse.builder()
                        .id(reservation.getId())
                        .roomName(reservation.getRoom().getName())  // spring.jpa.properties.hibernate.default_batch_fetch_size 옵션으로 N + 1 방지
                        .checkInTime(checkIn)
                        .checkOutTime(checkOut)
                        .status(reservation.getStatus())
                        .build()
        );
    }

    private ReservationResponse reservationToDto(Reservation reservation) {
        LocalDateTime checkIn = LocalDateTime.of(reservation.getStartDate(), reservation.getStartTime());
        LocalDateTime checkOut = LocalDateTime.of(reservation.getEndDate(), reservation.getEndTime());

        ReservationResponse reservationResponse = ReservationResponse.builder()
                .id(reservation.getId())
                .roomName(reservation.getRoom().getName())  // spring.jpa.properties.hibernate.default_batch_fetch_size 옵션으로 N + 1 방지
                .checkInTime(checkIn)
                .checkOutTime(checkOut)
                .status(reservation.getStatus())
                .build();

        return reservationResponse;
    }

    public void savePlaceByDto(PlaceRequest placeRequest) {

        Place place = modelMapper.map(placeRequest, Place.class);

        hostRepository.savePlace(place);

    }

    public void saveRoomByDtos(List<RoomReqeust> roomReqeusts) {

        List<Room> rooms = new ArrayList<>();

        for (RoomReqeust roomReqeust : roomReqeusts) {
            Room room = modelMapper.map(roomReqeust, Room.class);
            rooms.add(room);
        }

        for (Room room : rooms) {
            hostRepository.saveRoom(room);
        }


    }
}
