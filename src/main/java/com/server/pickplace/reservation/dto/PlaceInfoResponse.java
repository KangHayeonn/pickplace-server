package com.server.pickplace.reservation.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceInfoResponse {

    private String placeName;

    private String roomName;

    private Integer roomMaxNum;

    private String address;

}
