package com.wishboard.server.user.domain.model;

import java.util.Random;

import lombok.Getter;

@Getter
public class TemporaryNickname {
	private String nickname;

	public TemporaryNickname() {
		Random random = new Random();
		Adjective adjective = Adjective.values()[random.nextInt(Adjective.values().length)];
		Noun noun = Noun.values()[random.nextInt(Noun.values().length)];

		setNickname(adjective.getKorean(), noun.getKorean());
	}

	private void setNickname(String adjective, String noun) {
		this.nickname = adjective + " " + noun;
	}

	public enum Adjective {
		KKAMJJIKHAN("깜찍한"),
		GIYEOUN("귀여운"),
		AGIJAGIHAN("아기자기한"),
		SARANGSEUREOUN("사랑스러운"),
		REOBEULRIHAN("러블리한"),
		NANGMANITNEUN("낭만있는"),
		SUNSUHAN("순수한"),
		SENCHIHAN("센치한"),
		DANGDANGHAN("당당한"),
		SAECHIMHAN("새침한"),
		SIKEUHAN("시크한"),
		GAMGAKITNEUN("감각있는"),
		SENSEUITNEUN("센스있는"),
		BINTIJIHAN("빈티지한"),
		SIMPEULHAN("심플한"),
		DAJEONGHAN("다정한"),
		SINBIRUOUN("신비로운"),
		ALROKDALLOKHAN("알록달록한"),
		BANJJAGINEUN("반짝이는"),
		AREUNGEORINEUN("아른거리는"),
		HAENGBOGHAN("행복한"),
		BALLALHAN("발랄한"),
		HAEMALHAN("해맑은"),
		TTATTEUTHAN("따뜻한"),
		ANEUKHAN("아늑한"),
		POGEUNHAN("포근한"),
		DONGGEURAN("동그란");

		private final String korean;

		Adjective(String korean) {
			this.korean = korean;
		}

		public String getKorean() {
			return korean;
		}
	}

	public enum Noun {
		JINJU("진주"),
		RIBON("리본"),
		REUGEU("러그"),
		MUDUDEUNG("무드등"),
		KAENDEUL("캔들"),
		TEONTEIBEUL("턴테이블"),
		HEDEUSET("헤드셋"),
		PEURIJIA("프리지아"),
		RABENDEO("라벤더"),
		PPOGULI("뽀글이"),
		BOLKAEP("볼캡"),
		BEREMO("베레모"),
		SYOLDEOBAEK("숄더백"),
		SEONGEULRASEU("선글라스"),
		YANGMAL("양말"),
		MOKGEORRI("목걸이"),
		GWIGEORRI("귀걸이"),
		NITEU("니트"),
		JANGGAB("장갑"),
		MOKDORI("목도리"),
		CHEONGBAGJI("청바지"),
		BANJI("반지");

		private final String korean;

		Noun(String korean) {
			this.korean = korean;
		}

		public String getKorean() {
			return korean;
		}
	}
}
