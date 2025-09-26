package com.wishboard.server.user.domain.repository;

import static com.wishboard.server.user.domain.model.QUser.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wishboard.server.user.domain.model.User;
import com.wishboard.server.user.domain.model.UserProviderType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsBySocialIdAndSocialType(String socialId, UserProviderType socialType) {
		return queryFactory.selectOne()
			.from(user)
			.fetchFirst() != null;
	}

	// @Override
	// public User findUserById(Long id) {
	//     return queryFactory
	//             .selectFrom(user)
	//             .where(
	//                     user.id.eq(id)
	//             )
	//             .fetchOne();
	// }

	@Override
	public User findUserBySocialIdAndSocialType(String socialId, UserProviderType socialType) {
		return null;
	}

	// @Override
	// public User findUserBySocialIdAndSocialType(String socialId, UserSocialType socialType) {
	//     return queryFactory
	//             .selectFrom(user)
	//             .where(
	//                     user.socialInfo.socialId.eq(socialId),
	//                     user.socialInfo.socialType.eq(socialType)
	//             )
	//             .fetchOne();
	// }
}
