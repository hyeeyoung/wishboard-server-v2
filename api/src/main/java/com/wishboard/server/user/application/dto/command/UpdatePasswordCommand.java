package com.wishboard.server.user.application.dto.command;

public record UpdatePasswordCommand(
	String newPassword) {
}
