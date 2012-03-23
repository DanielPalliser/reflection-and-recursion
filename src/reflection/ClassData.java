package reflection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Scanner;

@SuppressWarnings("rawtypes")
/**
 * This class reads in class names from a file, produces statistics objects 
 * from classes named as strings, and writes statistitcs produced from these 
 * objects into a csv file
 * @author Daniel Palliser
 *
 */
public class ClassData {

	/**
	 * @uml.property  name="ignoreJavaAPI"
	 */
	private Boolean ignoreJavaAPI;
	/**
	 * @uml.property  name="useRecursion"
	 */
	private Boolean useRecursion;

	public ClassData() {
		ignoreJavaAPI = false;
		useRecursion = true;
	}

	private void analysis(String[] classes) {
		RefferredClasses refClasses = new RefferredClasses(ignoreJavaAPI,
				useRecursion);
		Hashtable<String, ClassStatsHolder[]> statsTable = new Hashtable<String, ClassStatsHolder[]>();

		for (String className : classes) {// for each listed class
			// get reffered classes
			String[] refClassesArr;
			try {
				refClassesArr = refClasses.getReferredClasses(className);

				// create array of stats holders
				ClassStatsHolder[] statsArr = new ClassStatsHolder[refClassesArr.length];
				for (int i = 0; i < refClassesArr.length; i++) {
					// get stats for each refferred class
					statsArr[i] = getStatistics(refClassesArr[i]);
				}
				// add array to hashtable, indexed by class name
				statsTable.put(className, statsArr);
			} catch (ClassNotFoundException e) {
				System.out.println("class not found: " + className);
			}
		}
		// now get info, and put into csv file
		try {
			writeCSVFile(classes, statsTable);
		} catch (IOException e) {
			System.err.println("Error writing to file");
		}

	}

	private void writeCSVFile(String[] keys,
			Hashtable<String, ClassStatsHolder[]> table) throws IOException {
		String filename = "data API " + ignoreJavaAPI + " R " + useRecursion
				+ ".csv";
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		out.write("Class Name,Average Fields,Referred Classes,Total Methods,Average Methods,"
				+ "Average Parameters,Average Constructors,Average Parameters per Constructor");
		// create filewriter
		for (String key : keys) {
			Integer totalClasses;
			Integer totalMethods = 0;
			Double averageMethods;
			Double averageParameters = 0.0;
			Double avgNumConstr = 0.0;
			Double avgConstParams = 0.0;
			Double avgNumFields = 0.0;
			// getting statistics
			ClassStatsHolder[] statsArray = table.get(key);
			totalClasses = statsArray.length;
			for (ClassStatsHolder classStats : statsArray) {
				totalMethods += classStats.getNumMethods();
				averageParameters += classStats.getTotalParams();
				avgNumConstr += classStats.getNumConstructors();
				avgConstParams += classStats.getAvgConstPerams();
				avgNumFields += classStats.getNumFields();
			}
			averageMethods = totalMethods.doubleValue() / totalClasses;
			averageParameters = averageParameters / totalMethods;
			avgNumConstr = avgNumConstr.doubleValue() / totalClasses;
			avgConstParams = avgConstParams / totalClasses;
			if (0.0 != avgNumFields) {
				avgNumFields = avgNumFields / totalClasses;
			}

			StringBuffer buf = new StringBuffer();
			buf.append("\n");
			buf.append(key);
			buf.append(",");
			buf.append(avgNumFields);
			buf.append(",");
			buf.append(totalClasses);
			buf.append(",");
			buf.append(totalMethods);
			buf.append(",");
			buf.append(averageMethods);
			buf.append(",");
			buf.append(averageParameters);
			buf.append(",");
			buf.append(avgNumConstr);
			buf.append(",");
			buf.append(avgConstParams);
			out.write(buf.toString());
		}
		out.close();
		System.out
				.println("\nStatisticts for the following Classes written to: "
						+ filename);
		for (String s : keys) {
			System.out.println(s);
		}
	}

	/**
	 * this method reads a list of classes from a file with one class per line
	 * 
	 * @param filename
	 *            the path of the file
	 * @return an array of strings representing class names
	 */
	private String[] readNamesFile(String filename) {
		String[] ret = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String classNames;
			classNames = br.readLine();
			if (classNames != null) {
				ret = classNames.split(",");
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * This class returns a ClassStatsHolder object which holds statistics about
	 * a class
	 * 
	 * @param classname
	 * @return
	 */
	private ClassStatsHolder getStatistics(String classname) {
		ClassStatsHolder ret = null;
		try {
			// get methods, constructors etc
			Class theClass = Class.forName(classname);
			Integer numFields = theClass.getDeclaredFields().length;
			Method[] methods = theClass.getDeclaredMethods();
			Integer members = (theClass.getDeclaredFields().length)
					+ methods.length;
			Constructor[] constructors = theClass.getDeclaredConstructors();

			// create ararys of method names an number of parameters
			String[] totalMethods = new String[methods.length];
			Integer[] paramaters = new Integer[methods.length];
			for (int i = 0; i < methods.length; i++) {
				totalMethods[i] = methods[i].getName();
				paramaters[i] = methods[i].getParameterTypes().length;
			}

			ret = new ClassStatsHolder(classname, totalMethods, paramaters,
					members, constructors, numFields);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();// will never be reached if recursion is done
		}

		return ret;
	}

	public static void main(String args[]) {
		// array to store named classes
		ClassData classData = new ClassData();
		classData.runMenu();
	}

	private void runMenu() {
		String[] classes;
		String in;
		Scanner scan = new Scanner(System.in);
		char option = 0;
		while (option != 'q' && option != 'Q') {
			StringBuffer printString = new StringBuffer(
					"\nPlease enter one of the following options: ");
			printString
					.append("\nC: find stats for classes recursively referred to by a class");
			printString
					.append("\nF: find stats for classes recursively referred to by classes named in a file");
			printString
					.append("\nT: Toggle ignore classes in a \"java.\" package. Current: ");
			printString.append(ignoreJavaAPI);
			printString.append("\nR: Toggle recursion. Current: ");
			printString.append(useRecursion);
			printString.append("\nQ: quit");
			System.out.println(printString.toString());
			option = scan.nextLine().charAt(0);
			switch (option) {
			case 'c':
			case 'C':
				System.out.println("Please enter the name of the Class");
				in = scan.nextLine();
				classes = new String[] { in };
				this.analysis(classes);
				break;
			case 'f':
			case 'F':
				System.out.println("Please enter the name of the file");
				in = scan.nextLine();
				classes = this.readNamesFile(in);
				this.analysis(classes);
				break;
			case 't':
			case 'T':
				if (ignoreJavaAPI) {
					ignoreJavaAPI = false;
				} else {
					ignoreJavaAPI = true;
				}
				break;
			case 'r':
			case 'R':
				if (useRecursion) {
					useRecursion = false;
				} else {
					useRecursion = true;
				}
				break;
			}
		}
	}
}
