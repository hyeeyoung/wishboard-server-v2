package com.wishboard.server.version.presentation;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.domain.OsType;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.resolver.HeaderOsType;
import com.wishboard.server.version.application.dto.VersionDto;
import com.wishboard.server.version.application.service.GetVersionByOsTypeUseCase;
import com.wishboard.server.version.application.service.GetVersionUseCase;
import com.wishboard.server.version.application.service.UpdateVersionUseCase;
import com.wishboard.server.version.presentation.docs.VersionControllerDocs;
import com.wishboard.server.version.presentation.dto.request.UpdateVersionRequest;
import com.wishboard.server.version.presentation.dto.response.VersionInfoResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VersionController implements VersionControllerDocs {
	private final ModelMapper modelMapper;
	private final GetVersionByOsTypeUseCase getVersionByOsTypeUseCase;
	private final UpdateVersionUseCase updateVersionUseCase;
	private final GetVersionUseCase getVersionUseCase;

	@GetMapping("/v2/version/check")
	@Override
	public SuccessResponse<VersionInfoResponse> getVersionByOs(@HeaderOsType OsType osType) {
		var versionInfoDto = getVersionByOsTypeUseCase.execute(osType);
		return SuccessResponse.success(SuccessCode.VERSION_LIST_SUCCESS, modelMapper.map(versionInfoDto, VersionInfoResponse.class));
	}

	@PutMapping("/v2/version")
	@Override
	public SuccessResponse<VersionInfoResponse> updateVersionByOs(@HeaderOsType OsType osType, @Valid @RequestBody UpdateVersionRequest request) {
		var versionInfoDto = updateVersionUseCase.execute(osType, request.toCommand());
		return SuccessResponse.success(SuccessCode.VERSION_LIST_SUCCESS, modelMapper.map(versionInfoDto, VersionInfoResponse.class));
	}

	@GetMapping("/v2/version")
	@Override
	public SuccessResponse<List<VersionInfoResponse>> getVersions(@HeaderOsType OsType osType) {
		List<VersionDto> versionList = getVersionUseCase.execute();
		var response = versionList.stream()
			.map(version -> modelMapper.map(version, VersionInfoResponse.class))
			.toList();
		return SuccessResponse.success(SuccessCode.VERSION_LIST_SUCCESS, response);
	}
}
