package com.wishboard.server.domain.notifications;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotifications is a Querydsl query type for Notifications
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotifications extends EntityPathBase<Notifications> {

    private static final long serialVersionUID = -431607257L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotifications notifications = new QNotifications("notifications");

    public final com.wishboard.server.domain.common.QAuditingTimeEntity _super = new com.wishboard.server.domain.common.QAuditingTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final DateTimePath<org.joda.time.LocalDateTime> itemNotificationDate = createDateTime("itemNotificationDate", org.joda.time.LocalDateTime.class);

    public final EnumPath<ItemNotificationType> itemNotificationType = createEnum("itemNotificationType", ItemNotificationType.class);

    public final QNotificationId notificationId;

    public final BooleanPath readState = createBoolean("readState");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QNotifications(String variable) {
        this(Notifications.class, forVariable(variable), INITS);
    }

    public QNotifications(Path<? extends Notifications> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotifications(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotifications(PathMetadata metadata, PathInits inits) {
        this(Notifications.class, metadata, inits);
    }

    public QNotifications(Class<? extends Notifications> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.notificationId = inits.isInitialized("notificationId") ? new QNotificationId(forProperty("notificationId"), inits.get("notificationId")) : null;
    }

}

