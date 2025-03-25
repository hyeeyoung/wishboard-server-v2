package com.wishboard.server.domain.common;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuditingTimeEntity is a Querydsl query type for AuditingTimeEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QAuditingTimeEntity extends EntityPathBase<AuditingTimeEntity> {

    private static final long serialVersionUID = 3845311L;

    public static final QAuditingTimeEntity auditingTimeEntity = new QAuditingTimeEntity("auditingTimeEntity");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public QAuditingTimeEntity(String variable) {
        super(AuditingTimeEntity.class, forVariable(variable));
    }

    public QAuditingTimeEntity(Path<? extends AuditingTimeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuditingTimeEntity(PathMetadata metadata) {
        super(AuditingTimeEntity.class, metadata);
    }

}

