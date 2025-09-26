package com.wishboard.server.version.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.version.domain.model.Deploy;

public interface DeployRepository extends JpaRepository<Deploy, Long> {
	Optional<Deploy> findByPlatform(String value);
}
