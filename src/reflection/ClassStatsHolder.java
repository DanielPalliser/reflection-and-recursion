package reflection;

import java.lang.reflect.Constructor;
/**
 * This class holds information about a Java Class and is able to produce statistics about that class
 * @author Daniel Palliser
 *
 */
public class ClassStatsHolder {

	private String className;
	private Integer totalMembers;
	private Integer[] numParams; // the Integer in this array is the number of parameters of the method in the same place in methods
	private String[] methods;
	private Integer numFields;
	@SuppressWarnings("rawtypes")
	private Constructor[] constructors;

	/**
	 * the purpose of this constructor is to prevent an empty stats object from being created
	 */
	@SuppressWarnings("unused")
	private ClassStatsHolder(){}
	/**
	 * this constructor takes all the neccassary information about a class needed to produce statistics
	 * @param className the full name of the class, in cluding package
	 * @param methods an array of strings containing the names of the methods in the class
	 * @param numParams an array of Integers representing the number of parameters of the method at the same position in methods
	 * @param totalMembers the total number of members of the class
	 * @param constructors the array of constructor Objects representing the constructors for the class
	 * @param numFields the number of declared fields in the class
	 */
	@SuppressWarnings("rawtypes")
	public ClassStatsHolder(String className, String[] methods,
			Integer[] numParams, Integer totalMembers, Constructor[] constructors, Integer numFields) {
		this.className = className;
		this.methods = methods;
		this.numParams = numParams; // number of parameters in each method
		this.totalMembers = totalMembers;
		this.numFields = numFields;
		this.constructors = constructors;
	}

	/**
	 * This method returns a string with each method and the number of parameters it has
	 * @return
	 */
	public String printString() {
		StringBuffer ret = new StringBuffer("");
		ret.append(className);
		ret.append(" has ");
		ret.append(numParams.length);
		ret.append(" methods with the following number of parameters ");
		for (int i = 0; i < methods.length; i++) {
			ret.append("\nMethod ");
			ret.append(methods[i]);
			ret.append(" has ");
			ret.append(numParams[i]);
			ret.append(" parameters");
		}
		return ret.toString();
	}

	public Integer getNumMethods() {
		return methods.length;
	}

	/**
	 * @return
	 */
	public Integer getTotalMembers() {
		return totalMembers;
	}

	/**
	 * @return
	 */
	public String getClassName() {
		return className;
	}
/**
 * 
 * @return the total number of parameters
 */
	public Integer getTotalParams() {
		Integer ret = 0;
		for (Integer i: numParams){
			ret += i;
		}
		return ret;
	}
	
	/**
	 * 
	 * @return the average number of parameters in the constructors of the class
	 */
	@SuppressWarnings("rawtypes")
	public Double getAvgConstPerams(){
		Double ret = 0.0;
		for(Constructor cons: constructors){
			ret += cons.getParameterTypes().length;
		}
		if(0.0 != ret){ret = ret/constructors.length;}
		return ret;
	}
	
	public Integer getNumConstructors(){
		return constructors.length;
	}

	public Double getNumFields() {
		return numFields.doubleValue();
	}
}
