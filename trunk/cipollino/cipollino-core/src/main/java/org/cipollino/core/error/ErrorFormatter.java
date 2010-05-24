package org.cipollino.core.error;
//package org.cipollino.core.error;
//
//import java.lang.reflect.Field;
//import java.util.Locale;
//import java.util.MissingResourceException;
//import java.util.ResourceBundle;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.cipollino.core.annotations.Message;
//import org.cipollino.core.annotations.MessageBundle;
//
//public class ErrorFormatter {
//
//	private static final Logger logger = LoggerFactory.getLogger(ErrorFormatter.class);
//
//	private String messageBundleName = null;
//
//	@SuppressWarnings("unchecked")
//	private Class sourceClass;
//
//	private ResourceBundle bundle = null;
//
//	@SuppressWarnings("unchecked")
//	public ErrorFormatter(Class sourceClass, Locale locale) {
//		this(sourceClass, locale, sourceClass.getClassLoader());
//	}
//
//	@SuppressWarnings("unchecked")
//	public ErrorFormatter(Class sourceClass, Locale locale, ClassLoader classLoader) {
//		this.sourceClass = sourceClass;
//		MessageBundle messageBundleAnnotation = (MessageBundle) sourceClass.getAnnotation(MessageBundle.class);
//		if (messageBundleAnnotation != null) {
//			this.messageBundleName = messageBundleAnnotation.value();
//			try {
//				bundle = ResourceBundle.getBundle(messageBundleName, locale, classLoader);
//			} catch (MissingResourceException e) {
//				logger.debug(String.format("Missing Resource Bundle [%s]", messageBundleName));
//			}
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public String format(Enum<?> source, Object... param) {
//		String message = null;
//		String messageId = null;
//		String messageCode = null;
//		boolean suppressMessageCode = false;
//		try {
//			String name = (String) sourceClass.getMethod("name").invoke(source);
//			Field field = sourceClass.getField(name);
//			Message messageAnnotation = field.getAnnotation(Message.class);
//			if (messageAnnotation != null) {
//				messageCode = messageAnnotation.code();
//				messageId = messageAnnotation.id();
//				if (messageId == null) {
//					messageId = messageCode;
//				}
//				message = messageAnnotation.message();
//				suppressMessageCode = messageAnnotation.suppressMessageCode();
//				if (bundle != null) {
//					try {
//						message = bundle.getString(messageId);
//					} catch (MissingResourceException e) {
//						logger.debug(String.format("Missing message [%s] in the bundle [%s]", messageId, messageBundleName));
//					}
//				}
//			} else {
//				logger.error("Illegal source object.");
//			}
//		} catch (Exception e) {
//			// Should not happen
//			logger.error(e.getMessage(), e);
//		}
//		if (param != null && param.length > 0) {
//			try {
//				message = String.format(message, param);
//			} catch (Throwable ex) {
//				logger.warn("Message format failed", ex);
//			}
//		}
//		if (messageCode != null && !suppressMessageCode) {
//			message = String.format("(%s) %s", messageCode, message);
//		}
//		return message == null ? "" : message;
//	}
//}
