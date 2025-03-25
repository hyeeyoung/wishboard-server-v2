package com.wishboard.server.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
}
