package org.cipollino.core.os;

public enum OSType {
	MACOS {

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
		protected boolean isCurrentOS() {
			return System.getProperty("os.name").startsWith("Windows");
		}

		@Override
		public OSFamily getFamily() {
			return OSFamily.WINDOWS;
		}
	};

	abstract public OSFamily getFamily();

	public String getArch() {
		return System.getProperty("os.arch");
	}

	public String getVersion() {
		return System.getProperty("os.version");
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