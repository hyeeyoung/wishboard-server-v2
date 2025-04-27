package com.wishboard.server.controller.item.request;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.util.StringUtils;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.domain.notifications.ItemNotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemRequest {

	@Schema(description = "folderId", example = "1")
	private Long folderId;

	@Schema(description = "itemName", example = "나이키 V2K")
	@NotBlank(message = "{item.itemName.notBlank}")
	private String itemName;

	@Schema(description = "itemPrice", example = "12300")
	private int itemPrice;

	@Schema(description = "itemMemo", example = "메모입니다.")
	private String itemMemo;

	@Schema(description = "itemUrl", example = "https://naver.com")
	private String itemUrl;

	@Schema(description = "알림 타입", example = "REMINDER")
	private ItemNotificationType itemNotificationType;

	@Schema(description = "알림 날짜", example = "2025-01-01 10:00:00")
	private String itemNotificationDate;

	public void validateDateInFuture() {
		if (!StringUtils.hasText(itemNotificationDate)) {
			return;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime inputDateTime = LocalDateTime.parse(this.itemNotificationDate, formatter);

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
