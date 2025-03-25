package com.wishboard.server.service.user;

import static com.wishboard.server.common.exception.ErrorCode.*;

import com.wishboard.server.common.exception.ConflictException;
import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.domain.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceUtils {

    static void validateNotExistsUser(UserRepository userRepository, String socialId, UserProviderType socialType) {
        if (userRepository.existsBySocialIdAndSocialType(socialId, socialType)) {
            throw new ConflictException(String.format("이미 존재하는 유저 (%s - %s) 입니다", socialId, socialType), CONFLICT_USER_EXCEPTION);
        }
    }

    public static User findUserBySocialIdAndSocialType(UserRepository userRepository, String socialId, UserProviderType socialType) {
        return userRepository.findUserBySocialIdAndSocialType(socialId, socialType);
    }

    public static User findUserById(UserRepository userRepository, Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("존재하지 않는 유저 (%s) 입니다", userId), NOT_FOUND_USER_EXCEPTION);
        }
        return user;
    }
}
