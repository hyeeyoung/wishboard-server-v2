package com.wishboard.server.item.application.service.support;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.common.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ItemValidator {

	public void validateDateInFuture(ItemNotificationType itemNotificationType, String itemNotificationDate) {
		if (!StringUtils.hasText(itemNotificationDate)) {
			return;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime inputDateTime = LocalDateTime.parse(itemNotificationDate, formatter);

		ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		ZonedDateTime inputKST = inputDateTime.atZone(ZoneId.of("Asia/Seoul"));

		if (inputKST.isBefore(nowKST)) {
			throw new ValidationException(String.format("알림 시간은 현재 시각보다 이후여야 합니다. (now: %s, input:%s)", nowKST.toString(), inputKST.toString()),
				VALIDATION_NOTIFICATION_EXCEPTION);
		}

		if (inputKST.getMinute() != 30 && inputKST.getMinute() != 0) {
			throw new ValidationException("알림 시간은 30분 단위로 설정해야 합니다.", VALIDATION_NOTIFICATION_MINUTE_EXCEPTION);
		}
	}
}
