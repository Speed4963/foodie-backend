package com.eatproject.backend.posts.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -384500010L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.eatproject.backend.common.QBaseTimeEntity _super = new com.eatproject.backend.common.QBaseTimeEntity(this);

    public final com.eatproject.backend.board.entity.QBoard board;

    public final DateTimePath<java.time.LocalDateTime> bumpAt = createDateTime("bumpAt", java.time.LocalDateTime.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Integer> depth = createNumber("depth", Integer.class);

    public final StringPath imgUrl = createString("imgUrl");

    public final BooleanPath isAnonymous = createBoolean("isAnonymous");

    public final BooleanPath isLocked = createBoolean("isLocked");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> lockedAt = createDateTime("lockedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final StringPath preview = createString("preview");

    public final NumberPath<Long> quoteId = createNumber("quoteId", Long.class);

    public final NumberPath<Integer> replyCount = createNumber("replyCount", Integer.class);

    public final StringPath thumbUrl = createString("thumbUrl");

    public final StringPath writer = createString("writer");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new com.eatproject.backend.board.entity.QBoard(forProperty("board")) : null;
    }

}

