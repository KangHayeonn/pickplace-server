package com.server.pickplace.host.config;

import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.reservation.entity.ReservationStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

//@Component
@Profile("!test")
@Transactional
public class HostConfig {

}


