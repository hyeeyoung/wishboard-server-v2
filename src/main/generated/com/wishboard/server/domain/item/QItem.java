package com.wishboard.server.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -1802791853L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final com.wishboard.server.domain.common.QAuditingTimeEntity _super = new com.wishboard.server.domain.common.QAuditingTimeEntity(this);

    public final StringPath addType = createString("addType");

    public final ListPath<com.wishboard.server.domain.cart.Cart, com.wishboard.server.domain.cart.QCart> carts = this.<com.wishboard.server.domain.cart.Cart, com.wishboard.server.domain.cart.QCart>createList("carts", com.wishboard.server.domain.cart.Cart.class, com.wishboard.server.domain.cart.QCart.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final com.wishboard.server.domain.folder.QFolder folder;

    public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

    public final StringPath itemImg = createString("itemImg");

    public final StringPath itemImgUrl = createString("itemImgUrl");

    public final StringPath itemMemo = createString("itemMemo");

    public final StringPath itemName = createString("itemName");

    public final StringPath itemPrice = createString("itemPrice");

    public final ListPath<com.wishboard.server.domain.notifications.Notifications, com.wishboard.server.domain.notifications.QNotifications> notifications = this.<com.wishboard.server.domain.notifications.Notifications, com.wishboard.server.domain.notifications.QNotifications>createList("notifications", com.wishboard.server.domain.notifications.Notifications.class, com.wishboard.server.domain.notifications.QNotifications.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public final com.wishboard.server.domain.user.QUser user;

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

    public QItem(Path<? extends Item> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.folder = inits.isInitialized("folder") ? new com.wishboard.server.domain.folder.QFolder(forProperty("folder"), inits.get("folder")) : null;
        this.user = inits.isInitialized("user") ? new com.wishboard.server.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

