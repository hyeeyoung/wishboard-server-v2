package com.wishboard.server.domain.deploy;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeploy is a Querydsl query type for Deploy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeploy extends EntityPathBase<Deploy> {

    private static final long serialVersionUID = 1188948115L;

    public static final QDeploy deploy = new QDeploy("deploy");

    public final com.wishboard.server.domain.common.QAuditingTimeEntity _super = new com.wishboard.server.domain.common.QAuditingTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath minVersion = createString("minVersion");

    public final StringPath platform = createString("platform");

    public final StringPath recommendedVersion = createString("recommendedVersion");

    public final DatePath<org.joda.time.LocalDate> releaseDate = createDate("releaseDate", org.joda.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QDeploy(String variable) {
        super(Deploy.class, forVariable(variable));
    }

    public QDeploy(Path<? extends Deploy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeploy(PathMetadata metadata) {
        super(Deploy.class, metadata);
    }

}

