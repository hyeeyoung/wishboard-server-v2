package com.wishboard.server.domain.user;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.model.EnumModel;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OsType implements EnumModel {
    IOS("IOS"),
    AOS("AOS"),
    SERVER("SERVER"),
    UNKNOWN("UNKNOWN");

    private final String value;

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }

    public static OsType fromUserAgent(String userAgent) {
        String osSegment = userAgent.split("/")[0].split("-")[1].toUpperCase();
        try {
            return OsType.valueOf(osSegment);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
