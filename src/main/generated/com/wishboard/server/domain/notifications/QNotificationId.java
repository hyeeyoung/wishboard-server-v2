package com.wishboard.server.domain.notifications;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationId is a Querydsl query type for NotificationId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QNotificationId extends BeanPath<NotificationId> {

    private static final long serialVersionUID = -494924281L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationId notificationId = new QNotificationId("notificationId");

    public final com.wishboard.server.domain.item.QItem item;

    public final com.wishboard.server.domain.user.QUser user;

    public QNotificationId(String variable) {
        this(NotificationId.class, forVariable(variable), INITS);
    }

    public QNotificationId(Path<? extends NotificationId> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationId(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationId(PathMetadata metadata, PathInits inits) {
        this(NotificationId.class, metadata, inits);
    }

    public QNotificationId(Class<? extends NotificationId> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.wishboard.server.domain.item.QItem(forProperty("item"), inits.get("item")) : null;
        this.user = inits.isInitialized("user") ? new com.wishboard.server.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

