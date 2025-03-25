package com.wishboard.server.domain.user.repository;

import static com.wishboard.server.domain.user.QUser.*;

import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.UserProviderType;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    @Override
    public User findUserById(Long id) {
        return queryFactory
                .selectFrom(user)
                .where(
                        user.id.eq(id)
                )
                .fetchOne();
    }

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
