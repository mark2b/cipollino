package org.cipollino.core.xml;

import static org.cipollino.core.error.ErrorCode.XmlParsingError;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cipollino.core.error.ErrorException;
import org.cipollino.core.services.PropertiesService;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ModelSerializer {

	public static final String BEAN_NAME = "modelSerializer";

	@Inject
	private PropertiesService propertiesService;

	@SuppressWarnings("unchecked")
	public <T> T read(Reader reader, Class<T> clazz) {
		try {
			DocumentBuilder builder = createDocumentBuilder();
			InputSource inputSource = new InputSource(reader);
			Document document = builder.parse(inputSource);
			Unmarshaller u = createUnmarshaller();
			JAXBElement<T> element = (JAXBElement<T>) u.unmarshal(document);
			return (T) element.getValue();
		} catch (Exception e) {
			throw new ErrorException(XmlParsingError, e, e.getMessage());
		}
	}

	private DocumentBuilder createDocumentBuilder()
			throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
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
		return JAXBContext.newInstance(propertiesService.getJaxbPath(),
				getClassLoader());
	}
}
