package com.wishboard.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test")
@RestController
@RequiredArgsConstructor
public class TestController {

	@GetMapping("/hello")
	public String hello() {
		return "hello wishboard server!";
	}
}
