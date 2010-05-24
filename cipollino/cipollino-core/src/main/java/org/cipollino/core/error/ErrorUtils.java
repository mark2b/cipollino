package org.cipollino.core.error;

import java.lang.reflect.Field;

import org.cipollino.core.logger.Message;


public class ErrorUtils {

	public static String getCode(Enum<?> instance) {
		try {
			String name = (String) instance.getClass().getMethod("name")
					.invoke(instance);
			Field field = instance.getClass().getField(name);
			Message messageAnnotation = field.getAnnotation(Message.class);
			if (messageAnnotation != null) {
				String messageCode = messageAnnotation.code();
				return messageCode;
			}
			throw new IllegalStateException("Missing @Message annotation.");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static ErrorMessageMetadata getMetadata(Enum<?> instance) {
		try {
			String name = (String) instance.getClass().getMethod("name")
					.invoke(instance);
			Field field = instance.getClass().getField(name);
			Message messageAnnotation = field.getAnnotation(Message.class);
			if (messageAnnotation != null) {
				ErrorMessageMetadata metadata = new ErrorMessageMetadata();
				metadata.id = messageAnnotation.id();
				metadata.code = messageAnnotation.code();
				metadata.message = messageAnnotation.message();
				metadata.scope = messageAnnotation.scope();
				metadata.severity = messageAnnotation.severity();
				return metadata;
			}
			throw new IllegalStateException("Missing @Message annotation.");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
