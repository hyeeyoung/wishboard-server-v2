package com.wishboard.server.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 678250323L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.wishboard.server.domain.common.QAuditingTimeEntity _super = new com.wishboard.server.domain.common.QAuditingTimeEntity(this);

    public final EnumPath<AuthType> authType = createEnum("authType", AuthType.class);

    public final ListPath<com.wishboard.server.domain.cart.Cart, com.wishboard.server.domain.cart.QCart> carts = this.<com.wishboard.server.domain.cart.Cart, com.wishboard.server.domain.cart.QCart>createList("carts", com.wishboard.server.domain.cart.Cart.class, com.wishboard.server.domain.cart.QCart.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final StringPath email = createString("email");

    public final StringPath fcmToken = createString("fcmToken");

    public final ListPath<com.wishboard.server.domain.folder.Folder, com.wishboard.server.domain.folder.QFolder> folders = this.<com.wishboard.server.domain.folder.Folder, com.wishboard.server.domain.folder.QFolder>createList("folders", com.wishboard.server.domain.folder.Folder.class, com.wishboard.server.domain.folder.QFolder.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final ListPath<com.wishboard.server.domain.item.Item, com.wishboard.server.domain.item.QItem> items = this.<com.wishboard.server.domain.item.Item, com.wishboard.server.domain.item.QItem>createList("items", com.wishboard.server.domain.item.Item.class, com.wishboard.server.domain.item.QItem.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final ListPath<com.wishboard.server.domain.notifications.Notifications, com.wishboard.server.domain.notifications.QNotifications> notifications = this.<com.wishboard.server.domain.notifications.Notifications, com.wishboard.server.domain.notifications.QNotifications>createList("notifications", com.wishboard.server.domain.notifications.Notifications.class, com.wishboard.server.domain.notifications.QNotifications.class, PathInits.DIRECT2);

    public final EnumPath<OsType> osType = createEnum("osType", OsType.class);

    public final StringPath password = createString("password");

    public final StringPath profileImg = createString("profileImg");

    public final BooleanPath pushState = createBoolean("pushState");

    public final QSocialInfo socialInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.socialInfo = inits.isInitialized("socialInfo") ? new QSocialInfo(forProperty("socialInfo")) : null;
    }

}

