package com.wishboard.server.version.application.dto.command;

public record UpdateVersionCommand(
	String minVersion,
	String recommendedVersion
) {
}
