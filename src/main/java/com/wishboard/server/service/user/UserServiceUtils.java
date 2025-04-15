package com.wishboard.server.service.user;

import static com.wishboard.server.common.exception.ErrorCode.*;

import com.wishboard.server.common.exception.ConflictException;
import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.domain.user.AuthType;
import com.wishboard.server.domain.user.TemporaryNickname;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.domain.user.repository.UserRepository;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserServiceUtils {

    // TODO 현재는 소셜 로그인에만 고려되어 있는 코드
    static void validateNotExistsUser(UserRepository userRepository, String socialId, UserProviderType socialType) {
        if (userRepository.existsBySocialIdAndSocialType(socialId, socialType)) {
            throw new ConflictException(String.format("이미 존재하는 유저 (%s - %s) 입니다", socialId, socialType), CONFLICT_USER_EXCEPTION);
        }
    }

    public static void existsByEmailAndAuthType(UserRepository userRepository, String email, AuthType authType) {
        if (userRepository.existsByEmailAndAuthType(email, authType)) {
            throw new ConflictException(String.format("이미 존재하는 유저 (authType: %s, email: %s) 입니다", authType.getValue(), email), CONFLICT_USER_EXCEPTION);
        }
    }

    public static User findUserBySocialIdAndSocialType(UserRepository userRepository, String socialId, UserProviderType socialType) {
        return userRepository.findUserBySocialIdAndSocialType(socialId, socialType);
    }

    public static User findByEmailAndAuthType(UserRepository userRepository, String email, AuthType authType) {
        return userRepository.findByEmailAndAuthType(email, authType)
            .orElseThrow(() -> new NotFoundException(
                String.format("이메일(%s)과 인증 유형(%s)에 해당하는 사용자를 찾을 수 없습니다.", email, authType), NOT_FOUND_USER_EXCEPTION));
    }

    public static User findUserById(UserRepository userRepository, Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("존재하지 않는 유저 (%s) 입니다", userId), NOT_FOUND_USER_EXCEPTION);
        }
        return user;
    }

    public static String getRandomNickname() {
        return new TemporaryNickname().getNickname();
    }
}
