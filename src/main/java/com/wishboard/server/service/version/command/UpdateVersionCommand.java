package com.wishboard.server.service.version.command;

public record UpdateVersionCommand(
	String minVersion,
	String recommendedVersion
) {
}
