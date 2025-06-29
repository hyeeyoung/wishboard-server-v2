package com.wishboard.server.config.converter;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.item.domain.model.AddType;

public class AddTypeConverter implements Converter<String, AddType> {

	@Override
	public AddType convert(String addType) {
		if (!StringUtils.hasText(addType)) {
			throw new ValidationException("addType is null or empty", VALIDATION_ITEM_CREATE_ADD_TYPE_EXCEPTION);
		}
		return AddType.valueOf(addType);
	}
}


