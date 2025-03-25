package com.wishboard.server.config.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "페이지 정보")
public class MyPageable {

    @Schema(description = "페이지 번호(0...N)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer page;

    @Schema(description = "페이지 크기", example = "10", minimum = "0", maximum = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer size;

    @Schema(description = "정렬(사용법: 칼럼명,ASC|DESC)", example = "name,ASC", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> sort;
}
