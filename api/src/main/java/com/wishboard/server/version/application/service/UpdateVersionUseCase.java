package com.wishboard.server.version.application.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.domain.OsType;
import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.version.application.dto.VersionDto;
import com.wishboard.server.version.application.dto.command.UpdateVersionCommand;
import com.wishboard.server.version.domain.repository.DeployRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateVersionUseCase {
	private final ModelMapper modelMapper;
	private final DeployRepository deployRepository;

	public VersionDto execute(OsType osType, UpdateVersionCommand updateVersionCommand) {
		var version = deployRepository.findByPlatform(osType.getValue()).orElseThrow(() -> new NotFoundException("Version not found"));
		version.updateVersionSpec(updateVersionCommand.minVersion(), updateVersionCommand.recommendedVersion());
		return modelMapper.map(version, VersionDto.class);
	}
}
