package com.wishboard.server.config.enummapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wishboard.server.common.util.EnumMapper;

@Configuration
public class EnumMapperConfig {

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();

        return enumMapper;
    }
}
