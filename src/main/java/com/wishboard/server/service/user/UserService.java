package com.wishboard.server.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.service.user.dto.request.CreateUserDto;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long registerUser(CreateUserDto request) {
        UserServiceUtils.validateNotExistsUser(userRepository, request.getSocialId(), request.getSocialType());
        User user = userRepository.save(User.newInstance(request.getSocialId(), request.getSocialType()));
        return user.getId();
    }
}
