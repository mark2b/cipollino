package org.cipollino.core.logger;

import static org.cipollino.core.logger.Severity.ERROR;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Message {

	public String id() default "";

	public String code();

	public String message();

	public Severity severity() default ERROR;

	public boolean suppressMessageCode() default false;

	public String scope() default "";
}
