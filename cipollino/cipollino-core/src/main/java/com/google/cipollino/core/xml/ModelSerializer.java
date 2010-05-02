package com.google.cipollino.core.xml;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.cipollino.core.error.ErrorCode;
import com.google.cipollino.core.error.Status;
import com.google.cipollino.core.services.PropertiesService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ModelSerializer {

	public static final String BEAN_NAME = "modelSerializer";

	@Inject
	private PropertiesService propertiesService;

	@SuppressWarnings("unchecked")
	public <T> T read(Status status, Reader reader, Class<T> clazz) {
		try {
			DocumentBuilder builder = createDocumentBuilder();
			InputSource inputSource = new InputSource(reader);
			Document document = builder.parse(inputSource);
			Unmarshaller u = createUnmarshaller();
			JAXBElement<T> element = (JAXBElement<T>) u.unmarshal(document);
			return (T) element.getValue();
		} catch (Exception e) {
			status.add(Status.createStatus(ErrorCode.XmlParsingError.getCode(), e));
			return null;
		}
	}

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setXIncludeAware(true);
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		return builder;
	}

	private ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}

	private Unmarshaller createUnmarshaller() throws JAXBException {
		JAXBContext jc = createJAXBContext();
		Unmarshaller u = jc.createUnmarshaller();
		return u;
	}

	private JAXBContext createJAXBContext() throws JAXBException {
		return JAXBContext.newInstance(propertiesService.getJaxbPath(), getClassLoader());
	}
}
