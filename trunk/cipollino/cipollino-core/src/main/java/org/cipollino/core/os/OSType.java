package org.cipollino.core.os;

import org.cipollino.core.os.unix.LinuxUtils;
import org.cipollino.core.os.unix.UnixUtils;
import org.cipollino.core.os.windows.WindowsUtils;

public enum OSType {
	MACOS {

		@Override
		public OSUtils getOSUtils() {
			return new UnixUtils();
		}

		@Override
		protected boolean isCurrentOS() {
			return System.getProperty("os.name").equals("Mac OS X");
		}

		@Override
		public OSFamily getFamily() {
			return OSFamily.MAC;
		}
	},
	SOLARIS {

		@Override
		public OSUtils getOSUtils() {
			return new UnixUtils();
		}

		@Override
		protected boolean isCurrentOS() {
			return System.getProperty("os.name").equals("SunOS")
					|| System.getProperty("os.name").equals("Solaris");
		}

		@Override
		public OSFamily getFamily() {
			return OSFamily.UNIX;
		}
	},
	AIX {

		@Override
		public OSUtils getOSUtils() {
			return new UnixUtils();
		}

		@Override
		protected boolean isCurrentOS() {
			return System.getProperty("os.name").equals("AIX");
		}

		@Override
		public OSFamily getFamily() {
			return OSFamily.UNIX;
		}
	},
	HPUX {

		@Override
		public OSUtils getOSUtils() {
			return new UnixUtils();
		}

		@Override
		protected boolean isCurrentOS() {
			return System.getProperty("os.name").equals("HP-UX");
		}

		@Override
		public OSFamily getFamily() {
			return OSFamily.UNIX;
		}
	},
	LINUX {

		@Override
		public OSUtils getOSUtils() {
			return new LinuxUtils();
		}

		@Override
		protected boolean isCurrentOS() {
			return System.getProperty("os.name").equals("Linux");
		}

		@Override
		public OSFamily getFamily() {
			return OSFamily.UNIX;
		}
	},
	WINDOWS {
		@Override
		public OSUtils getOSUtils() {
			return new WindowsUtils();
		}

		@Override
		protected boolean isCurrentOS() {
			return System.getProperty("os.name").startsWith("Windows");
		}

		@Override
		public OSFamily getFamily() {
			return OSFamily.WINDOWS;
		}
	};

	abstract public OSFamily getFamily();

	abstract public OSUtils getOSUtils();

	public String getArch() {
		return System.getProperty("os.arch");
	}

	public String getVersion() {
		return System.getProperty("os.version");
	}

	@SuppressWarnings("unchecked")
	public <T> T getOSUtils(Class<T> clazz) {
		return (T) getOSUtils();
	}

	abstract protected boolean isCurrentOS();

	public static OSType getCurrent() {
		for (OSType type : OSType.values()) {
			if (type.isCurrentOS()) {
				return type;
			}
		}
		throw new RuntimeException("Unrecognized Operation System");
	}
}