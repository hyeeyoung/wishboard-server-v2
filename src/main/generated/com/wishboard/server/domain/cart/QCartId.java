package com.wishboard.server.domain.cart;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCartId is a Querydsl query type for CartId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCartId extends BeanPath<CartId> {

    private static final long serialVersionUID = 1418153198L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCartId cartId = new QCartId("cartId");

    public final com.wishboard.server.domain.item.QItem item;

    public final com.wishboard.server.domain.user.QUser user;

    public QCartId(String variable) {
        this(CartId.class, forVariable(variable), INITS);
    }

    public QCartId(Path<? extends CartId> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCartId(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCartId(PathMetadata metadata, PathInits inits) {
        this(CartId.class, metadata, inits);
    }

    public QCartId(Class<? extends CartId> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.wishboard.server.domain.item.QItem(forProperty("item"), inits.get("item")) : null;
        this.user = inits.isInitialized("user") ? new com.wishboard.server.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

