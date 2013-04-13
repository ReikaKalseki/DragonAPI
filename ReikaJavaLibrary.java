package Reika.DragonAPI;

import java.util.ArrayList;
import java.util.List;

public class ReikaJavaLibrary {
	
	/** Generic write-to-console function. Args: Object */
	public static void pConsole(Object obj) {
		Class cl = obj.getClass();
		System.out.println(String.valueOf(obj)+" of Class "+String.valueOf(cl));
	}
	
	/** A complement to Java's built-in List-to-Array. Args: Array of any object (ints, strings, etc). */
	public static List makeListFromArray(Object[] obj) {
		List li = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			li.add(obj[i]);
		}
		return li;
	}
	
	public static List makeListFrom(Object obj) {
		List li = new ArrayList();
		li.add(obj);
		return li;
	}
}
