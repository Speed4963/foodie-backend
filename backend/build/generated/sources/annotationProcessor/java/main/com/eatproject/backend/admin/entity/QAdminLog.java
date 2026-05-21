package com.eatproject.backend.admin.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdminLog is a Querydsl query type for AdminLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminLog extends EntityPathBase<AdminLog> {

    private static final long serialVersionUID = 1458805799L;

    public static final QAdminLog adminLog = new QAdminLog("adminLog");

    public final StringPath actionType = createString("actionType");

    public final StringPath adminEmail = createString("adminEmail");

    public final NumberPath<Integer> approvedBoard = createNumber("approvedBoard", Integer.class);

    public final StringPath bannedUser = createString("bannedUser");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> logId = createNumber("logId", Long.class);

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> targetPost = createNumber("targetPost", Long.class);

    public QAdminLog(String variable) {
        super(AdminLog.class, forVariable(variable));
    }

    public QAdminLog(Path<? extends AdminLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdminLog(PathMetadata metadata) {
        super(AdminLog.class, metadata);
    }

}

