package com.wishboard.server.version.application.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.version.application.dto.VersionDto;
import com.wishboard.server.version.domain.repository.DeployRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetVersionUseCase {
	private final DeployRepository deployRepository;
	private final ModelMapper modelMapper;

	public List<VersionDto> execute() {
		var versions = deployRepository.findAll();
		return versions.stream()
			.map(version -> modelMapper.map(version, VersionDto.class))
			.toList();
	}
}
