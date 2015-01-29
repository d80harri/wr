package net.d80harri.wr.ui.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;


public class ListViewModel<T, U> {
	private Function<U, T> conversionFunction;
	private BiPredicate<U, T> comparator;
	private final List<T> model;
	
	public ListViewModel(List<T> model, Function<U, T> conversionFunction) {
		this.model = model;
		this.conversionFunction = conversionFunction;
		init();
	}
	
	public void init() {
		listProperty().addListener(this::onChanged);
	}
	
	private Map<U, T> mapping = new HashMap<>(); 
	private final ListProperty<U> list = new SimpleListProperty<U>(FXCollections.observableArrayList());
	
	public ListProperty<U> listProperty() {
		return list;
	}

	public void onChanged(
			javafx.collections.ListChangeListener.Change<? extends U> c) {
		while (c.next()) {
			if (c.wasPermutated()) {
				for (int i = c.getFrom(); i< c.getTo(); ++i) {
					// TBD
				}
				throw new RuntimeException("Not yet implemented");
			} else if (c.wasUpdated()) {
				throw new RuntimeException("Not yet implemented");
			} else {
				for (U elem : c.getRemoved()) {
					T orig = mapping.get(elem);
					model.remove(orig);
				}
				int i=0;
				for (U elem : c.getAddedSubList()) {
					T converted = convert(elem);
					model.add(c.getFrom() + i, converted);
					mapping.put(elem, converted);
					i++;
				}				
			}
		}
	}
	
	protected T convert(U obj) {
		return conversionFunction.apply(obj);
	}

}
