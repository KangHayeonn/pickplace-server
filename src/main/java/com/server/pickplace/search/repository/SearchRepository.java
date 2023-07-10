package com.server.pickplace.search.repository;

import com.server.pickplace.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Place, Long>, SearchRepositoryCustom {


}
