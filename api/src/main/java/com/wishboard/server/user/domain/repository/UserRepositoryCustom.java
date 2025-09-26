package com.wishboard.server.user.domain.repository;

import com.wishboard.server.user.domain.model.User;
import com.wishboard.server.user.domain.model.UserProviderType;

public interface UserRepositoryCustom {

	boolean existsBySocialIdAndSocialType(String socialId, UserProviderType socialType);

	// User findUserById(Long id);

	User findUserBySocialIdAndSocialType(String socialId, UserProviderType socialType);

}
