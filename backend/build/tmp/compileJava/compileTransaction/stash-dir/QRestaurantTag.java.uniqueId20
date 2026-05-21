package com.eatproject.backend.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRestaurantTag is a Querydsl query type for RestaurantTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantTag extends EntityPathBase<RestaurantTag> {

    private static final long serialVersionUID = -1726055351L;

    public static final QRestaurantTag restaurantTag = new QRestaurantTag("restaurantTag");

    public final EnumPath<com.eatproject.backend.common.CategoryType> category = createEnum("category", com.eatproject.backend.common.CategoryType.class);

    public final StringPath customTag = createString("customTag");

    public final ListPath<Restaurant, QRestaurant> restaurants = this.<Restaurant, QRestaurant>createList("restaurants", Restaurant.class, QRestaurant.class, PathInits.DIRECT2);

    public final NumberPath<Integer> tagId = createNumber("tagId", Integer.class);

    public QRestaurantTag(String variable) {
        super(RestaurantTag.class, forVariable(variable));
    }

    public QRestaurantTag(Path<? extends RestaurantTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurantTag(PathMetadata metadata) {
        super(RestaurantTag.class, metadata);
    }

}

