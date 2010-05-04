package com.google.cipollino.core.i18n;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cipollino.core.annotations.Message;
import com.google.cipollino.core.annotations.MessageBundle;

public class MessageFormatter {

	private static final Logger log = LoggerFactory.getLogger(MessageFormatter.class);

	private String messageBundleName = null;

	@SuppressWarnings("unchecked")
	private Class sourceClass;

	private ResourceBundle bundle = null;

	@SuppressWarnings("unchecked")
	public MessageFormatter(Class sourceClass, Locale locale) {
		this.sourceClass = sourceClass;
		MessageBundle messageBundleAnnotation = (MessageBundle) sourceClass.getAnnotation(MessageBundle.class);
		if (messageBundleAnnotation != null) {
			this.messageBundleName = messageBundleAnnotation.value();
			try {
				bundle = ResourceBundle.getBundle(messageBundleName, locale, Thread.currentThread()
						.getContextClassLoader());
			} catch (MissingResourceException e) {
				log.debug(String.format("Missing Resource Bundle [%s]", messageBundleName));
			}

		}
	}

	@SuppressWarnings("unchecked")
	public String format(Object source, Object... param) {
		String message = null;
		if (sourceClass.isEnum()) {
			try {
				String name = (String) sourceClass.getMethod("name").invoke(source);
				Field field = sourceClass.getField(name);
				Message messageAnnotation = field.getAnnotation(Message.class);
				if (messageAnnotation != null) {
					String messageId = messageAnnotation.id();
					message = messageAnnotation.value();
					if (bundle != null) {
						try {
							message = bundle.getString(messageId);
						} catch (MissingResourceException e) {
							log.debug(String.format("Missing mesage [%s] in the bundle [%s]", messageId,
									messageBundleName));
						}
					}
				}
			} catch (Exception e) {
				// Should not happen
				log.error(e.getMessage(), e);
			}
			if (param != null && param.length > 0) {
				try {
					message = String.format(message, param);
				} catch (Throwable ex) {
					log.warn("Message format failed", ex);
				}
			}
		} else {
			log.error("Illegal source object.");
		}
		return message == null ? "" : message;
	}
}
