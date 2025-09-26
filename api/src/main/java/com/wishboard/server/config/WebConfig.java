package com.wishboard.server.config;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wishboard.server.config.converter.AddTypeConverter;
import com.wishboard.server.config.interceptor.AuthInterceptor;
import com.wishboard.server.config.interceptor.UserAgentValidationInterceptor;
import com.wishboard.server.config.resolver.HeaderOsTypeResolver;
import com.wishboard.server.config.resolver.UserIdResolver;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;

	private final UserIdResolver userIdResolver;
	private final HeaderOsTypeResolver headerOsTypeResolver;
	private final UserAgentValidationInterceptor userAgentValidationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor);
		registry.addInterceptor(userAgentValidationInterceptor);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(userIdResolver);
		resolvers.add(headerOsTypeResolver);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new AddTypeConverter());
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/message");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public MessageSource validationMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/validation");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(validationMessageSource());
		return bean;
	}
}
