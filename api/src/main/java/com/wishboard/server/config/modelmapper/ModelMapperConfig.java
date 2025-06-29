package com.wishboard.server.config.modelmapper;

import org.hibernate.collection.spi.PersistentCollection;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		// entity 에 setter 를 추가하지 않기 위해
		modelMapper.getConfiguration().setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

		// 매칭 전략 - 같은 타입의 같은 필드명일 경우에만 매핑
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		// fetch type이 lazy 적용된 필드 매핑 제외
		modelMapper.getConfiguration().setPropertyCondition(context -> !(context.getSource() instanceof PersistentCollection));

		return modelMapper;
	}
}
