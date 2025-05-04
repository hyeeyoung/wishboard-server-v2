package com.wishboard.server.controller.version;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.resolver.HeaderOsType;
import com.wishboard.server.controller.version.request.UpdateVersionRequest;
import com.wishboard.server.controller.version.response.VersionInfoResponse;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.service.version.VersionService;
import com.wishboard.server.service.version.dto.VersionDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VersionController implements VersionControllerDocs {
	private final ModelMapper modelMapper;
	private final VersionService versionService;

	@GetMapping("/v2/version/check")
	@Override
	public SuccessResponse<VersionInfoResponse> getVersionByOs(@HeaderOsType OsType osType) {
		var versionInfoDto = versionService.getVersionByOs(osType);
		return SuccessResponse.success(SuccessCode.VERSION_LIST_SUCCESS, modelMapper.map(versionInfoDto, VersionInfoResponse.class));
	}

	@PutMapping("/v2/version")
	@Override
	public SuccessResponse<VersionInfoResponse> updateVersionByOs(@HeaderOsType OsType osType, @Valid @RequestBody UpdateVersionRequest request) {
		var versionInfoDto = versionService.updateVersion(osType, request.toCommand());
		return SuccessResponse.success(SuccessCode.VERSION_LIST_SUCCESS, modelMapper.map(versionInfoDto, VersionInfoResponse.class));
	}

	@GetMapping("/v2/version")
	@Override
	public SuccessResponse<List<VersionInfoResponse>> getVersions(@HeaderOsType OsType osType) {
		List<VersionDto> versionList = versionService.getVersions();
		var response = versionList.stream()
			.map(version -> modelMapper.map(version, VersionInfoResponse.class))
			.toList();
		return SuccessResponse.success(SuccessCode.VERSION_LIST_SUCCESS, response);
	}
}
