package com.server.pickplace.host.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.place.entity.*;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

import java.util.List;

import static com.server.pickplace.place.entity.QCategory.*;
import static com.server.pickplace.place.entity.QTag.*;

@RequiredArgsConstructor
public class HostRepositoryCustomImpl implements HostRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public void savePlace(Place place) {
        em.persist(place);
    }

    @Override
    public void saveRoom(Room room) {
        em.persist(room);
    }


    @Override
    public Category findCategoryByCategoryStatus(CategoryStatus categoryStatus) {
        Category category1 = queryFactory
                .select(category)
                .from(category)
                .where(category.status.eq(categoryStatus))
                .fetch().get(0);

        return category1;
    }


    @Override
    public void saveCategoryPlace(CategoryPlace categoryPlace) {
        em.persist(categoryPlace);

    }


    @Override
    public List<Tag> findTagListByTagStatusList(List<TagStatus> tagStatusList) {
        List<Tag> tagList = queryFactory
                .select(tag)
                .from(tag)
                .where(tag.tagStatus.in(tagStatusList))
                .fetch();

        return tagList;
    }


    @Override
    public void saveTagPlace(TagPlace tagPlace) {
        em.persist(tagPlace);
    }
}
