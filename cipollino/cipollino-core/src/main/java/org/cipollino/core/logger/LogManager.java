package org.cipollino.core.logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogManager {

	private static final Logger logger = LoggerFactory.getLogger(LogManager.class);

	private final Class<?> enumClass;

	private String scope = null;

	private ResourceBundle bundle = null;

	private String bundleName = null;

	public LogManager(Class<?> sourceClass) {
		this(sourceClass, Locale.getDefault());
	}

	public LogManager(Class<?> enumClass, Locale locale) {
		this.enumClass = enumClass;
		Messages messagesAnnotation = (Messages) enumClass.getAnnotation(Messages.class);
		if (messagesAnnotation != null) {
			scope = messagesAnnotation.scope();
			bundleName = messagesAnnotation.bundleName();
			if (bundleName != null && !bundleName.isEmpty()) {
				try {
					bundle = ResourceBundle.getBundle(bundleName, locale, enumClass.getClassLoader());
				} catch (MissingResourceException e) {
					logger.debug(String.format("Missing Resource Bundle [%s]", bundleName));
				}
			}
		}
	}

	public void print(Enum<?> sourceEnum, Object source, Object... args) {
		print(sourceEnum, source == null ? null : source.getClass(), args);
	}

	public void print(Enum<?> sourceEnum, Class<?> sourceClass, Object... args) {
		String message = null;
		String messageId = null;
		String messageCode = null;
		Severity severity = Severity.ERROR;
		String scope = null;
		boolean suppressMessageCode = false;
		try {
			String name = (String) enumClass.getMethod("name").invoke(sourceEnum);
			Field field = enumClass.getField(name);
			Message messageAnnotation = field.getAnnotation(Message.class);
			if (messageAnnotation != null) {
				messageCode = messageAnnotation.code();
				messageId = messageAnnotation.id();
				if (messageId == null || messageId.isEmpty()) {
					messageId = messageCode;
				}
				message = messageAnnotation.message();
				suppressMessageCode = messageAnnotation.suppressMessageCode();
				severity = messageAnnotation.severity();
				scope = messageAnnotation.scope();
				if ((scope == null || scope.isEmpty()) && this.scope != null && !this.scope.isEmpty()) {
					scope = this.scope;
				}
			} else {
				logger.error("Illegal source object.");
			}
		} catch (Exception e) {
			// Should not happen
			logger.error(e.getMessage(), e);
		}
		Logger logger = null;
		if (sourceClass != null) {
			logger = LoggerFactory.getLogger(sourceClass);
		} else if (scope != null && !scope.isEmpty()) {
			logger = LoggerFactory.getLogger(scope);
		} else {
			logger = LoggerFactory.getLogger("");
		}

		if (severity.isEnabled(logger)) {
			Throwable t = null;
			if (args != null && args.length > 0 && args[args.length - 1] instanceof Throwable) {
				t = (Throwable) args[args.length - 1];
				args = Arrays.copyOf(args, args.length - 1);
			}
			String formattedMessage = format(messageId, messageCode, message, suppressMessageCode, args);
			severity.print(logger, formattedMessage, t);
		}
	}

	public String format(Enum<?> sourceEnum, Object... args) {
		String message = null;
		String messageId = null;
		String messageCode = null;
		boolean suppressMessageCode = false;
		try {
			String name = (String) enumClass.getMethod("name").invoke(sourceEnum);
			Field field = enumClass.getField(name);
			Message messageAnnotation = field.getAnnotation(Message.class);
			if (messageAnnotation != null) {
				messageCode = messageAnnotation.code();
				messageId = messageAnnotation.id();
				if (messageId == null || messageId.isEmpty()) {
					messageId = messageCode;
				}
				message = messageAnnotation.message();
				suppressMessageCode = messageAnnotation.suppressMessageCode();
			} else {
				logger.error("Illegal source object.");
			}
		} catch (Exception e) {
			// Should not happen
			logger.error(e.getMessage(), e);
		}
		return format(messageId, messageCode, message, suppressMessageCode, args);
	}

	public String format(String messageId, String messageCode, String message, boolean suppressMessageCode, Object[] args) {
		if (bundle != null) {
			try {
				message = bundle.getString(messageId);
			} catch (MissingResourceException e) {
				logger.debug(String.format("Missing message [%s] in the bundle [%s]", messageId, bundleName));
			}
		}
		if (args != null && args.length > 0) {
			try {
				message = String.format(message, args);
			} catch (Throwable ex) {
				logger.warn("Message format failed", ex);
			}
		}
		if (messageCode != null && !suppressMessageCode) {
			message = String.format("(%s) %s", messageCode, message);
		}
		return message == null ? "" : message;
	}
}
