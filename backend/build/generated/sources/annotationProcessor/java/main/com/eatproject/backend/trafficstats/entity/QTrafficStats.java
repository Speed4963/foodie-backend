package com.eatproject.backend.trafficstats.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrafficStats is a Querydsl query type for TrafficStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrafficStats extends EntityPathBase<TrafficStats> {

    private static final long serialVersionUID = -531226501L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrafficStats trafficStats = new QTrafficStats("trafficStats");

    public final com.eatproject.backend.board.entity.QBoard board;

    public final StringPath keyword = createString("keyword");

    public final NumberPath<Integer> mentionCount = createNumber("mentionCount", Integer.class);

    public final DatePath<java.time.LocalDate> statDate = createDate("statDate", java.time.LocalDate.class);

    public final NumberPath<Long> statId = createNumber("statId", Long.class);

    public QTrafficStats(String variable) {
        this(TrafficStats.class, forVariable(variable), INITS);
    }

    public QTrafficStats(Path<? extends TrafficStats> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrafficStats(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrafficStats(PathMetadata metadata, PathInits inits) {
        this(TrafficStats.class, metadata, inits);
    }

    public QTrafficStats(Class<? extends TrafficStats> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new com.eatproject.backend.board.entity.QBoard(forProperty("board")) : null;
    }

}

