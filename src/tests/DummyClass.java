package tests;

import java.io.Serializable;

@SuppressWarnings("all")
public class DummyClass extends DummySuperClass implements Serializable {

	private DummyClass2[][][] d2 = new DummyClass2[1][1][1];

	// private String str = "";
	public DummyClass3 hasDummy3(DummyClass3 c3) {
		DummyClass3 d3 = new DummyClass3();
		return d3;
	}
}
