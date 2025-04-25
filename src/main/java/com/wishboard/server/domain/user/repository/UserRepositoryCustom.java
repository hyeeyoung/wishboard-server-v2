package com.wishboard.server.domain.user.repository;

import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.UserProviderType;

public interface UserRepositoryCustom {

	boolean existsBySocialIdAndSocialType(String socialId, UserProviderType socialType);

	// User findUserById(Long id);

	User findUserBySocialIdAndSocialType(String socialId, UserProviderType socialType);

}
