package net.d80harri.wr.ui.utils;

import java.util.Collection;
import java.util.function.Function;

public class DebugUtils {
	public static <T> void printTreeStructure(T object, int depth, Function<T, Collection<T>> childProducer) {
		for (int i=0; i<depth; i++)
			System.out.print(" ");
		System.out.println(object);
		
		for (T child : childProducer.apply(object)) {
			printTreeStructure(child, depth+1, childProducer);			
		}
	}
	
	public static <T> void printMultipleTreesStructure(Collection<T> coll, Function<T, Collection<T>> childProducer) {
		for (T object : coll) {
			printTreeStructure(object, 0, childProducer);
		}
	}
}
