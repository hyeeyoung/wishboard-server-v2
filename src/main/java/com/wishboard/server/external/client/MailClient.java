package com.wishboard.server.external.client;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.wishboard.server.common.exception.InternalServerException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MailClient {
	private final JavaMailSender mailSender;

	public void sendEmailWithVerificationCode(String to, String verificationCode) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

		try {
			mimeMessageHelper.addTo(to);
			mimeMessageHelper.setSubject(getMailSubject());
			mimeMessageHelper.setText(generateHtml(verificationCode), true);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new InternalServerException(String.format("이메일 전송 중 예상치 못한 서버 에러가 발생하였습니다. (e=%s)", e.getMessage()));
		}
	}

	private String getMailSubject() {
		return "[Wishboard] 이메일 로그인을 위한 인증번호를 보내드려요.";
	}

	private String generateHtml(String verificationCode) {
		return String.format("""
        <p>로그인 화면에서 아래의 인증번호를 입력하고 로그인을 완료해주세요. 인증코드는 5분 동안 유효합니다.</p>
        <br />
        <h3>%s</h3>
        <br />
        <p>혹시 요청하지 않은 인증 메일을 받으셨나요? 걱정하지 마세요. </p>
        <p>고객님의 이메일 주소가 실수로 입력된 것일 수 있어요. 직접 요청한 인증 메일이 아닌 경우 무시해주세요.</p>
        <br />
        <p>멋진 하루 보내세요!</p>
        <br />
        <p>Wishboard 팀 드림</p>
    """, verificationCode);
	}
}
