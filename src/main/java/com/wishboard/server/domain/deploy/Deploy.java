package com.wishboard.server.domain.deploy;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wishboard.server.domain.common.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "deploy")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deploy extends AuditingTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, nullable = false)
	private String platform;

	@Column(name = "min_version",length = 20, nullable = false)
	private String minVersion;

	@Column(name = "recommended_version", length = 20, nullable = false)
	private String recommendedVersion;

	@Column(name="release_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate releaseDate;
}
