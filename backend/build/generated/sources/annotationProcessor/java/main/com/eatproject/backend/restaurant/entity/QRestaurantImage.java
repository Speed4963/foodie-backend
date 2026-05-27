package com.eatproject.backend.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRestaurantImage is a Querydsl query type for RestaurantImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantImage extends EntityPathBase<RestaurantImage> {

    private static final long serialVersionUID = -891619766L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRestaurantImage restaurantImage = new QRestaurantImage("restaurantImage");

    public final StringPath category = createString("category");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final NumberPath<Long> imgId = createNumber("imgId", Long.class);

    public final StringPath imgUrl = createString("imgUrl");

    public final BooleanPath isMain = createBoolean("isMain");

    public final QRestaurant restaurant;

    public final StringPath thumbUrl = createString("thumbUrl");

    public QRestaurantImage(String variable) {
        this(RestaurantImage.class, forVariable(variable), INITS);
    }

    public QRestaurantImage(Path<? extends RestaurantImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRestaurantImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRestaurantImage(PathMetadata metadata, PathInits inits) {
        this(RestaurantImage.class, metadata, inits);
    }

    public QRestaurantImage(Class<? extends RestaurantImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.restaurant = inits.isInitialized("restaurant") ? new QRestaurant(forProperty("restaurant"), inits.get("restaurant")) : null;
    }

}

