package com.server.pickplace.mypage.repository;

import com.server.pickplace.place.entity.Category;
import com.server.pickplace.place.entity.CategoryPlace;
import com.server.pickplace.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyPageCategoryRepository extends JpaRepository<CategoryPlace, Long> {

    Optional<List<CategoryPlace>> findCategoryPlaceByPlaceId(Long id);

}
