package com.eatproject.backend.board.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -1395412163L;

    public static final QBoard board = new QBoard("board");

    public final com.eatproject.backend.common.QBaseTimeEntity _super = new com.eatproject.backend.common.QBaseTimeEntity(this);

    public final NumberPath<Integer> boardId = createNumber("boardId", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Short> generation = createNumber("generation", Short.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> parentBoardId = createNumber("parentBoardId", Integer.class);

    public final NumberPath<Integer> postCount = createNumber("postCount", Integer.class);

    public final StringPath proposedBy = createString("proposedBy");

    public final StringPath slug = createString("slug");

    public final StringPath status = createString("status");

    public final DateTimePath<java.time.LocalDateTime> statusChangedAt = createDateTime("statusChangedAt", java.time.LocalDateTime.class);

    public QBoard(String variable) {
        super(Board.class, forVariable(variable));
    }

    public QBoard(Path<? extends Board> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoard(PathMetadata metadata) {
        super(Board.class, metadata);
    }

}

