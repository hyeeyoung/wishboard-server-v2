package com.wishboard.server.domain.deploy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.domain.deploy.Deploy;

public interface DeployRepository extends JpaRepository<Deploy, Long> {
	Optional<Deploy> findByPlatform(String value);
}
