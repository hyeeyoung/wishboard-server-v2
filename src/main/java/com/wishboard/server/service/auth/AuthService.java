package com.wishboard.server.service.auth;

import com.wishboard.server.service.auth.dto.request.LoginDto;

public interface AuthService {

    Long login(LoginDto request);
}
