package com.wishboard.server.domain.deploy.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DeployRepositoryImpl implements DeployRepositoryCustom {

    private final JPAQueryFactory queryFactory;
}
