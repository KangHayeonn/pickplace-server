package com.server.pickplace.host.service;

import com.server.pickplace.common.service.CommonService;
import com.server.pickplace.host.dto.*;
import com.server.pickplace.host.error.HostErrorResult;
import com.server.pickplace.host.error.HostException;
import com.server.pickplace.host.repository.HostRepository;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.place.entity.*;
import com.server.pickplace.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HostService extends CommonService {

    private final HostRepository hostRepository;
    private final ModelMapper modelMapper;

    public List<PlaceResponse> findPlaceDtoListById(Long id) {

        Optional<List<Place>> optionalPlaces = hostRepository.findPlaceListById(id);

        if (optionalPlaces.isPresent()) {

            List<Place> places = optionalPlaces.get();

            List<CategoryStatus> categoryStatusList = places.stream().map(place -> place.getCategories().get(0).getCategory().getStatus())
                    .collect(Collectors.toList());

            List<PlaceResponse> placeDtos = places.stream()
                    .map(place -> modelMapper.map(place, PlaceResponse.class))
                    .collect(Collectors.toList());

            for (int i = 0; i < categoryStatusList.size(); i++) {
                CategoryStatus categoryStatus = categoryStatusList.get(i);
                PlaceResponse placeResponse = placeDtos.get(i);
                placeResponse.setCategoryStatus(categoryStatus);
            }

            return placeDtos;

        } else {
            return new ArrayList<>();
        }

    }

    public List<RoomInfoResponse> findRoomDtoListByPlaceId(Long placeId) {

        Optional<List<Room>> optionalRooms = hostRepository.findOptionalRoomListByPlaceId(placeId);

        if (optionalRooms.isPresent()) {

            List<Room> rooms = optionalRooms.get();

            List<RoomInfoResponse> roomDtos = rooms.stream().map(room -> modelMapper.map(room, RoomInfoResponse.class))
                    .collect(Collectors.toList());

            return roomDtos;

        } else {
            return null;
        }

    }

    public PlaceResponse findPlaceDtoByPlaceId(Long id, Long placeId) {

        Optional<Place> optionalPlace = hostRepository.findOptionalPlaceByPlaceId(placeId);

        Place place = optionalPlace.orElseThrow(() -> new HostException(HostErrorResult.NOT_EXIST_PLACE));
        memberPlaceIdCheck(id, place);

        PlaceResponse placeResponse = modelMapper.map(place, PlaceResponse.class);
        placeResponse.setCategoryStatus(place.getCategories().get(0).getCategory().getStatus());

        return placeResponse;

    }

    public List<ReservationResponse> findReservationDtoListByPlaceId(Long placeId) {

        Optional<List<Reservation>> optionalReservations = hostRepository.findOptionalReservationListByPlaceId(placeId);

        if (optionalReservations.isPresent()) {

            List<Reservation> reservations = optionalReservations.get();

            List<ReservationResponse> reservationDtos = reservations.stream().map(reservation -> modelMapper.map(reservation, ReservationResponse.class))
                    .collect(Collectors.toList());

            return reservationDtos;

        } else {
            return null;
        }
    }


    public Map<String, Object> createReservationDtoMapByEmail(Long id) {

        Optional<List<Object[]>> optionalReservationsAndNames = hostRepository.findOptionalReservationAndNamesById(id);

        Map<Place, List<ReservationResponse>> map = new HashMap<>();
        List<Object> placeList = new ArrayList<>();

        if (optionalReservationsAndNames.isPresent()) {

            List<Object[]> reservationAndNames = optionalReservationsAndNames.get();

            for (Object[] reservationsAndName : reservationAndNames) {

                Reservation reservation = (Reservation) reservationsAndName[0];
                Place place = (Place) reservationsAndName[1];
                ReservationResponse reservationResponse = modelMapper.map(reservation, ReservationResponse.class);

                List<ReservationResponse> reservations = map.getOrDefault(place, new ArrayList<>());
                reservations.add(reservationResponse);

                map.put(place, reservations);

            }

            for (Map.Entry<Place, List<ReservationResponse>> localEntry : map.entrySet()) {
                Map<String, Object> localMap = new HashMap<>();

                localMap.put("placeName", localEntry.getKey().getName());
                localMap.put("placeCategory", localEntry.getKey().getCategories().get(0).getCategory().getStatus());
                localMap.put("reservations", localEntry.getValue());

                placeList.add(localMap);
            }

            Map<String, Object> globalMap = new HashMap<>(){{
                put("placeList", placeList);
            }};

            return globalMap;

        }

        return new HashMap<>();
    }
    public Map<String, Object> getMemberReservationPlaceDtoMapByReservationId(Long reservationId, Long id) {

        Optional<List<Object[]>> optionalMemberReservationPlaceList = hostRepository.findOptionalMemberReservationPlaceListByReservationId(reservationId); // reservationId만 유효하다면, 셋 다 존재해야함

        if (!optionalMemberReservationPlaceList.isPresent()) {
            throw new HostException(HostErrorResult.NOT_EXIST_RESERVATION);
        }

        Object[] memberReservationPlaceList = optionalMemberReservationPlaceList.get().get(0);

        Member member = (Member) memberReservationPlaceList[0];
        Reservation reservation = (Reservation) memberReservationPlaceList[1];
        Place place = (Place) memberReservationPlaceList[2];

        reservationMemberCheck(member, id);

        MemberResponse MemberDto = MemberResponse.builder()
                .name(member.getName()).build();

        ReservationResponse reservationDto = modelMapper.map(reservation, ReservationResponse.class);

        PlaceResponse placeDto = modelMapper.map(place, PlaceResponse.class);
        placeDto.setCategoryStatus(place.getCategories().get(0).getCategory().getStatus());

        Map<String, Object> map = new HashMap<>();

        map.put("member", MemberDto);
        map.put("reservation", reservationDto);
        map.put("place", placeDto);

        return map;

    }

    private void reservationMemberCheck(Member member, Long id) {
        if (!member.getId().equals(id)) {
            throw new HostException(HostErrorResult.NO_PERMISSION);
        }
    }

    public Long savePlaceAndRoomsByDto(PlaceRoomReqeuest placeRoomReqeuest, Member host) {

        // 같은 트랜잭션

        PlaceRequest placeRequest = placeRoomReqeuest.getPlace();
        List<RoomReqeust> roomReqeusts = placeRoomReqeuest.getRooms();
        CategoryStatus categoryStatus = placeRoomReqeuest.getCategory();
        List<TagStatus> tagStatusList = placeRoomReqeuest.getTagList();


        Place place = modelMapper.map(placeRequest, Place.class);
        place.setPoint(new Point(placeRequest.getX(), placeRequest.getY()));
        place.setMember(host);

        Long placeId = hostRepository.savePlace(place);

        List<Room> rooms = new ArrayList<>();

        for (RoomReqeust roomReqeust : roomReqeusts) {
            Room room = modelMapper.map(roomReqeust, Room.class);
            room.setPlace(place);
            rooms.add(room);
        }

        for (Room room : rooms) {
            hostRepository.saveRoom(room);
            hostRepository.saveUnitByRoom(room);
        }

        Category category = hostRepository.findCategoryByCategoryStatus(categoryStatus);

        CategoryPlace categoryPlace = CategoryPlace.builder().category(category).place(place).build();
        hostRepository.saveCategoryPlace(categoryPlace);

        List<Tag> tagList = hostRepository.findTagListByTagStatusList(tagStatusList);
        tagList.forEach(tag -> hostRepository.saveTagPlace(TagPlace.builder().tag(tag).place(place).build()));

        return placeId;
    }

    private void memberPlaceIdCheck(Long id, Place place) {
        if (!place.getMember().getId().equals(id)) {
            throw new HostException(HostErrorResult.NO_PERMISSION);
        }
    }

    public Member hostCheck(Long id) {

        Optional<Member> optionalHost = hostRepository.findById(id);
        Member host = optionalHost.get();

        if (host.getRole() != MemberRole.HOST) {
            throw new HostException(HostErrorResult.NO_PERMISSION);
        }

        return host;


    }

    // 본인 소유 플레이스Id 아님, 존재하지 않는 플레이스Id
    public void updatePlaceByDto(Long placeId, PlaceUpdateRequest placeUpdateRequest, Long id) {

        // 1. placeId로 place 조회
        // 2. 널이면, 에러  + place.getemail != email -> 권한없음

        Optional<Place> optionalPlace = hostRepository.findOptionalPlaceByPlaceIdFetchCategoryAndTag(placeId);
        Place place = optionalPlace.orElseThrow(() -> new HostException(HostErrorResult.NOT_EXIST_PLACE));
        if (!place.getMember().getId().equals(id)) {
            throw new HostException(HostErrorResult.NO_PERMISSION);
        }

        Category category = hostRepository.findCategoryByCategoryStatus(placeUpdateRequest.getCategory());
        List<Tag> tagList = hostRepository.findTagListByTagStatusList(placeUpdateRequest.getTagList());


        // 3. 수정

        hostRepository.updatePlaceByDto(place, category, tagList, placeUpdateRequest);



    }

    public void deletePlace(Long placeId, Long id) {

        Optional<Place> optionalPlace = hostRepository.findOptionalPlaceByPlaceId(placeId);
        Place place = optionalPlace.orElseThrow(() -> new HostException(HostErrorResult.NOT_EXIST_PLACE));
        if (!place.getMember().getId().equals(id)) {
            throw new HostException(HostErrorResult.NO_PERMISSION);
        }

        hostRepository.deletePlace(place);


    }

    public void updateRoomByDto(RoomReqeust roomReqeust, Long roomId, Long id) {
        Optional<Room> optionalRoom = hostRepository.findRoomByRoomId(roomId);
        Room room = optionalRoom.orElseThrow(() -> new HostException(HostErrorResult.NOT_EXIST_ROOM));
        if (!room.getPlace().getMember().getId().equals(id)) {
            throw new HostException(HostErrorResult.NO_PERMISSION);
        }

        hostRepository.updateRoom(room, roomReqeust);

    }

    public void deleteRoomByRoomId(Long roomId, Long id) {
        Optional<Room> optionalRoom = hostRepository.findRoomByRoomId(roomId);
        Room room = optionalRoom.orElseThrow(() -> new HostException(HostErrorResult.NOT_EXIST_ROOM));
        if (!room.getPlace().getMember().getId().equals(id)) {
            throw new HostException(HostErrorResult.NO_PERMISSION);
        }

        hostRepository.deleteRoom(room);
    }
}
