package com.wishboard.server.service.version;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.domain.deploy.repository.DeployRepository;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.service.version.command.UpdateVersionCommand;
import com.wishboard.server.service.version.dto.VersionDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VersionService {
	private final ModelMapper modelMapper;
	private final DeployRepository deployRepository;

	public VersionDto getVersionByOs(OsType osType) {
		var version = deployRepository.findByPlatform(osType.getValue()).orElseThrow(() -> new NotFoundException("Version not found"));
		return modelMapper.map(version, VersionDto.class);
	}

	public VersionDto updateVersion(OsType osType, UpdateVersionCommand updateVersionCommand) {
		var version = deployRepository.findByPlatform(osType.getValue()).orElseThrow(() -> new NotFoundException("Version not found"));
		version.updateVersionSpec(updateVersionCommand.minVersion(), updateVersionCommand.recommendedVersion());
		return modelMapper.map(version, VersionDto.class);
	}

	public List<VersionDto> getVersions() {
		var versions = deployRepository.findAll();
		return versions.stream()
			.map(version -> modelMapper.map(version, VersionDto.class))
			.toList();
	}
}
