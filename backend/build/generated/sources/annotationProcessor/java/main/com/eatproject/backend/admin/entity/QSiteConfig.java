package com.eatproject.backend.admin.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSiteConfig is a Querydsl query type for SiteConfig
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteConfig extends EntityPathBase<SiteConfig> {

    private static final long serialVersionUID = 924386203L;

    public static final QSiteConfig siteConfig = new QSiteConfig("siteConfig");

    public final NumberPath<Integer> alertThreshold = createNumber("alertThreshold", Integer.class);

    public final NumberPath<Integer> boardThreadLimit = createNumber("boardThreadLimit", Integer.class);

    public final NumberPath<Integer> configId = createNumber("configId", Integer.class);

    public final StringPath footerInfo = createString("footerInfo");

    public final BooleanPath maintenanceMode = createBoolean("maintenanceMode");

    public final StringPath siteName = createString("siteName");

    public final NumberPath<Integer> threadReplyLimit = createNumber("threadReplyLimit", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath updatedBy = createString("updatedBy");

    public QSiteConfig(String variable) {
        super(SiteConfig.class, forVariable(variable));
    }

    public QSiteConfig(Path<? extends SiteConfig> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSiteConfig(PathMetadata metadata) {
        super(SiteConfig.class, metadata);
    }

}

