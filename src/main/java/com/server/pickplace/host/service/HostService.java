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
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HostService {

    private final HostRepository hostRepository;
    private final ModelMapper modelMapper;

    public List<PlaceResponse> findPlaceDtoListByEmail(String email) {

        Optional<List<Place>> optionalPlaces = hostRepository.findPlaceListByEmail(email);

        if (optionalPlaces.isPresent()) {

            List<Place> places = optionalPlaces.get();

            List<PlaceResponse> placeDtos = places.stream()
                    .map(place -> modelMapper.map(place, PlaceResponse.class))
                    .collect(Collectors.toList());

            return placeDtos;

        } else {
            return null;
        }

    }

    public List<RoomResponse> findRoomDtoListByPlaceId(Long placeId) {

        Optional<List<Room>> optionalRooms = hostRepository.findOptionalRoomListByPlaceId(placeId);

        if (optionalRooms.isPresent()) {

            List<Room> rooms = optionalRooms.get();

            List<RoomResponse> roomDtos = rooms.stream().map(room -> modelMapper.map(room, RoomResponse.class))
                    .collect(Collectors.toList());

            return roomDtos;

        } else {
            return null;
        }

    }

    public PlaceResponse findPlaceDtoByPlaceId(Long placeId) {

        Optional<Place> optionalPlace = hostRepository.findOptionalPlaceByPlaceId(placeId);

        Place place = optionalPlace.orElseThrow(() -> new HostException(HostErrorResult.NOT_EXIST_PLACE));

        PlaceResponse placeResponse = modelMapper.map(place, PlaceResponse.class);

        return placeResponse;

    }

    public List<ReservationResponse> findReservationDtoListByPlaceId(Long placeId) {

        Optional<List<Reservation>> optionalReservations = hostRepository.findOptionalReservationListByPlaceId(placeId, LocalDate.now());

        if (optionalReservations.isPresent()) {

            List<Reservation> reservations = optionalReservations.get();

            List<ReservationResponse> reservationDtos = reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationResponse.class))
                    .collect(Collectors.toList());

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
                ReservationResponse reservationResponse = modelMapper.map(reservation, ReservationResponse.class);

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

        Optional<List<Object[]>> optionalMemberReservationPlaceList = hostRepository.findOptionalMemberReservationPlaceListByReservationId(reservationId); // reservationId만 유효하다면, 셋 다 존재해야함

        if (!optionalMemberReservationPlaceList.isPresent()) {
            throw new HostException(HostErrorResult.NOT_EXIST_RESERVATION);
        }

        Object[] memberReservationPlaceList = optionalMemberReservationPlaceList.get().get(0);

        Member member = (Member) memberReservationPlaceList[0];
        Reservation reservation = (Reservation) memberReservationPlaceList[1];
        Place place = (Place) memberReservationPlaceList[2];

        MemberResponse MemberDto = MemberResponse.builder()
                .name(member.getName()).build();

        ReservationResponse reservationDto = modelMapper.map(reservation, ReservationResponse.class);

        PlaceResponse placeDto = modelMapper.map(place, PlaceResponse.class);

        Map<String, Object> map = new HashMap<>();

        map.put("member", MemberDto);
        map.put("reservation", reservationDto);
        map.put("place", placeDto);

        return map;

    }

    public void savePlaceAndRoomsByDto(PlaceRequest placeRequest, Member host, List<RoomReqeust> roomReqeusts) {

        // 같은 트랜잭션

        Place place = modelMapper.map(placeRequest, Place.class);
        place.setPoint(new Point(127.011804, 38.478695));
        place.setMember(host);

        hostRepository.savePlace(place);

        List<Room> rooms = new ArrayList<>();

        for (RoomReqeust roomReqeust : roomReqeusts) {
            Room room = modelMapper.map(roomReqeust, Room.class);
            room.setPlace(place);
            rooms.add(room);
        }

        for (Room room : rooms) {
            hostRepository.saveRoom(room);
        }

    }

}