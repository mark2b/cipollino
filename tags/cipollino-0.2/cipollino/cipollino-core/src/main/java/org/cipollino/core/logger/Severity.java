package org.cipollino.core.logger;

import org.slf4j.Logger;

public enum Severity {
	ERROR {
		@Override
		public boolean isEnabled(Logger logger) {
			return logger.isErrorEnabled();
		}

		@Override
		public void print(Logger logger, String message, Throwable t) {
			logger.error(message, t);
		}
	},
	WARNING {
		@Override
		public boolean isEnabled(Logger logger) {
			return logger.isWarnEnabled();
		}

		@Override
		public void print(Logger logger, String message, Throwable t) {
			logger.warn(message, t);
		}
	},
	INFO {
		@Override
		public boolean isEnabled(Logger logger) {
			return logger.isInfoEnabled();
		}

		@Override
		public void print(Logger logger, String message, Throwable t) {
			logger.info(message, t);
		}
	},
	TRACE {
		@Override
		public boolean isEnabled(Logger logger) {
			return logger.isTraceEnabled();
		}

		@Override
		public void print(Logger logger, String message, Throwable t) {
			logger.trace(message, t);
		}
	},
	DEBUG {
		@Override
		public boolean isEnabled(Logger logger) {
			return logger.isDebugEnabled();
		}

		@Override
		public void print(Logger logger, String message, Throwable t) {
			logger.debug(message, t);
		}
	};

	abstract public boolean isEnabled(Logger logger);

	abstract public void print(Logger logger, String message, Throwable t);
}
