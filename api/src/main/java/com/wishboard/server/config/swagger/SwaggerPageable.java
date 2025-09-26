package com.wishboard.server.config.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
	@Parameter(name = "page", description = "페이지 번호 (0..N) [기본값: 0]", schema = @Schema(type = "integer", defaultValue = "0", nullable = true)),
	@Parameter(name = "size", description = "페이지 번호 (0..100) [기본값: 10]", schema = @Schema(type = "integer", defaultValue = "10")),
	@Parameter(name = "pageable", hidden = true)
})
public @interface SwaggerPageable {
}
