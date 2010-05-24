//package com.google.cipollino.core.test;
//
//import static org.testng.Assert.assertNotNull;
//import static org.testng.Assert.assertTrue;
//
//import java.io.InputStreamReader;
//
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import com.google.inject.Guice;
//import com.google.inject.Inject;
//import com.google.inject.Injector;
//import com.google.cipollino.core.DIModule;
//import com.google.cipollino.core.error.Status;
//import com.google.cipollino.core.error.Status.Severity;
//import com.google.cipollino.core.model.Directives;
//import com.google.cipollino.core.schema.DirectivesType;
//import com.google.cipollino.core.xml.ModelSerializer;
//import com.google.cipollino.core.xml.X2JModelFactory;
//
//public class X2JModelFactoryTest {
//
//	@Inject
//	ModelSerializer modelSerializer;
//
//	@Inject
//	X2JModelFactory modelFactory;
//
//	@BeforeClass
//	void init() {
//		Injector injector = Guice.createInjector(new DIModule());
//		injector.injectMembers(this);
//	}
//
//	@Test
//	public void parseTest() {
//		Status status = Status.createStatus(Severity.SUCCESS);
//		DirectivesType directivesType = modelSerializer.read(status, new InputStreamReader(getClass()
//				.getResourceAsStream("/spy-config1.xml")), DirectivesType.class);
//		assertTrue(status.isSuccess());
//		assertNotNull(directivesType);
//		Directives suite = modelFactory.create(directivesType);
//		// assertTrue(suite.getTests().containsKey("test1"));
//		// com.google.cipollino.core.model.Test test =
//		// suite.getTests().get("test1");
//		// assertTrue(test.getMethods().containsKey("method1"));
//		// Method method = test.getMethods().get("method1");
//		// assertTrue(method.getSnapshotItems().containsKey("arg1"));
//		// SnapshotItem item1 = method.getSnapshotItems().get("arg1");
//		// assertEquals(item1.getIndex(), 0);
//		// SnapshotItem item2 = method.getSnapshotItems().get("ret");
//		// assertEquals(test.getAsserts().size(), 2);
//		// Assert assert1 = test.getAsserts().get(0);
//		// Assert assert2 = test.getAsserts().get(1);
//		// assertEquals(assert1.getActual(), "${" + item1.getId() + "}");
//		// assertEquals(assert1.getExpected(), "${" + item2.getId() + "}");
//		// assertEquals(assert1.getType(), AssertType.EQUALS);
//		// assertEquals(assert2.getExpected(), "3");
//	}
// }
