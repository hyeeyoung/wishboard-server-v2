package com.wishboard.server.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSocialInfo is a Querydsl query type for SocialInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QSocialInfo extends BeanPath<SocialInfo> {

    private static final long serialVersionUID = 702351171L;

    public static final QSocialInfo socialInfo = new QSocialInfo("socialInfo");

    public final EnumPath<UserProviderType> providerType = createEnum("providerType", UserProviderType.class);

    public final StringPath socialId = createString("socialId");

    public QSocialInfo(String variable) {
        super(SocialInfo.class, forVariable(variable));
    }

    public QSocialInfo(Path<? extends SocialInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSocialInfo(PathMetadata metadata) {
        super(SocialInfo.class, metadata);
    }

}

