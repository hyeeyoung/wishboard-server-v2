package com.wishboard.server.auth.infrastructure.apple;

public interface AppleTokenProvider {

	String getSocialIdFromIdToken(String idToken);
}
