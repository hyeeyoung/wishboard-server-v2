package com.wishboard.server.user.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.user.domain.model.AuthType;
import com.wishboard.server.user.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	boolean existsByEmailAndAuthType(final String email, final AuthType authType);

	@EntityGraph(attributePaths = {"fcmTokens"})
	Optional<User> findByEmailAndAuthType(final String email, final AuthType authType);

	@EntityGraph(attributePaths = {"fcmTokens"})
	Optional<User> findUserById(final Long id);

	boolean existsByNickname(String nickname);
}
