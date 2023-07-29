package com.server.pickplace.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPageReservationMoreResponseDto {

    Long userId;
    Long placeId;
    Long roomId;
    String nickname;
    String placeName;
    String placePhone;

    PlaceAddressDto placeAddress ;

    Float placeRating ;
    Integer placeReviewCnt ;
    String roomName;
    Integer roomPrice;
    String startDate;
    String endDate;
    String startTime;

    Number personnel;
    String reservationStatus;
    Boolean reviewExistence;

    @JsonProperty("reservationDate")
    String updateDate;

    Long reservationId;
}
