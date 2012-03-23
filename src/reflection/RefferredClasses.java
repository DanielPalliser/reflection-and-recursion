package reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Hashtable;

/**
 * This class gets all the classes referred to by a given class, given as a
 * string representing the name of the class or as a Class object
 * 
 * @author Daniel Palliser
 * 
 */
@SuppressWarnings("rawtypes")
public class RefferredClasses {

	private Hashtable<String, Class> table;
	private Boolean ignoreJavaAPI;
	private Boolean useRecursion;

	public RefferredClasses() {
		ignoreJavaAPI = false;
	}

	public RefferredClasses(Boolean ignoreJavaAPI, Boolean useRecursion) {
		this.ignoreJavaAPI = ignoreJavaAPI;
		this.useRecursion = useRecursion;
	}

	/**
	 * this method is an adapter for getReferredClasses(Class source), allowing
	 * it to work with strings representing class names
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public String[] getReferredClasses(String className)
			throws ClassNotFoundException {
		Class theClass = Class.forName(className);
		Class[] classes = this.getReferredClasses(theClass);

		String[] ret = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			String tmp = classes[i].getName();
			ret[i] = tmp; // .replaceAll("[]", "");
		}
		return ret;
	}

	/**
	 * This method returns an array of Class objects representing all the
	 * classes the given class refers to
	 * 
	 * @param source
	 *            the given class
	 * @return the classes the given class refers to
	 */
	public Class[] getReferredClasses(Class source) {
		table = new Hashtable<String, Class>(13);
		table.put(source.getName(), source);
		getClassesRecursive(source);
		Class[] ret = null;
		Collection<Class> values = table.values();
		ret = values.toArray(new Class[1]);
		return ret;

	}

	/*
	 * method uses recursion to get all classes directly and indirectly referred
	 * to by the class
	 */
	private void getClassesRecursive(Class source) {
		Class superclass = source.getSuperclass();
		if (null != superclass) {
			recursionChecks(superclass);
		}

		Class[] interfaces = source.getInterfaces();
		for (Class c : interfaces) {
			recursionChecks(c);
		}

		Field[] fields = source.getDeclaredFields();// get fields
		for (Field f : fields) {
			Class c = f.getType(); // get type of field
			recursionChecks(c);
		}

		Constructor[] constructors = source.getDeclaredConstructors();
		for (Constructor con : constructors) {
			Class[] params = con.getParameterTypes();
			for (Class c : params) {
				recursionChecks(c);
			}
		}

		Method[] methods = source.getMethods();
		for (Method m : methods) {
			Class returnType = m.getReturnType();
			recursionChecks(returnType);
			Class[] params = m.getParameterTypes();
			for (Class c : params) {
				recursionChecks(c);
			}
		}
	}

	/*
	 * this method and getClassesRecursion() are mutually recursive. This method
	 * is called on Class objects to check if they are primative, get the types
	 * of arrays, and check if the class is from java api
	 * 
	 * @param
	 */
	private void recursionChecks(Class c) {

		while (c.isArray()) {
			c = c.getComponentType();
		}
		if (useRecursion) {
			Boolean isJavaAPI = (c.getName().matches("(java\\.).*") || c
					.getName().matches("(sun\\.).*"));
			if (!(isJavaAPI && ignoreJavaAPI)
					&& !(table.containsKey(c.getName())) && !(c.isPrimitive())) {
				table.put(c.getName(), c);
				getClassesRecursive(c);
			}
		} else if (!(table.containsKey(c.getName())) && !(c.isPrimitive())) {
			table.put(c.getName(), c);
		}
	}
}