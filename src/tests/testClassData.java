package tests;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;

import org.junit.Before;
import org.junit.Test;

import reflection.ClassData;
import reflection.ClassStatsHolder;
import reflection.RefferredClasses;

@SuppressWarnings("all")
public class testClassData {

	private ClassData classData;
	private static final String CLASSNAME = "java.lang.Object";
	
	@Before
	public void setUp() {
		classData = new ClassData();
	}

	@Test
	public void testGetReferredClasses() {
		try {
			RefferredClasses refClasses = new RefferredClasses(false, true);
			Class theClass = Class.forName(CLASSNAME);
			Class[] results = refClasses.getReferredClasses(theClass);
			for (Class c : results) {
				System.err.println(c.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("caught exception");
		}

		// assertEquals(referredClasses[1].getName(),"tests.DummyClass3");
	}

}