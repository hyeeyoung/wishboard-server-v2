package com.wishboard.server.common.domain;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditingTimeEntity {

	@CreationTimestamp
	@Column(name = "createAt")
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updateAt")
	private LocalDateTime updatedAt;
}
