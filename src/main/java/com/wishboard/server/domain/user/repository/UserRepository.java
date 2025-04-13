package com.wishboard.server.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.domain.user.AuthType;
import com.wishboard.server.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	boolean existsByEmailAndAuthType(final String email, final AuthType authType);
	Optional<User> findByEmailAndAuthType(final String email, final AuthType authType);}
