package com.intellibird.cipollino.core.xml;

public enum Namespaces {
	SUITE {

		@Override
		public String getPrefix() {
			return "c";
		}

		@Override
		public String getNamespaceURI() {
			return "http://www.intellibird.com/cipollino/test-suite/1.0";
		}
	};

	abstract public String getPrefix();

	abstract public String getNamespaceURI();

	static public Namespaces fromNamespaceURI(String namespaceURI) {
		Namespaces[] namespaces = Namespaces.values();
		for (Namespaces namespace : namespaces) {
			if (namespace.getNamespaceURI().equals(namespaceURI)) {
				return namespace;
			}
		}
		return null;
	}

	public static String getJAXBPath() {
		return "com.intellibird.cipollino.core.schema";
	}
}
