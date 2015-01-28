package net.d80harri.wr.ui.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public abstract class ListViewModel<T, U> {
	private List<T> model;
	
	public ListViewModel(List<T> model) {
		this.model = model;
		init();
	}
	
	public void init() {
//		listProperty().addListener(this::onChanged);
	}
	
	private ListProperty<U> list = new SimpleListProperty<U>();
	
	public ListProperty<U> listProperty() {
		return list;
	}

	public void onChanged(
			javafx.collections.ListChangeListener.Change<? extends U> c) {
		model = c.getList().stream().<T>map(i -> convert(i)).collect(Collectors.<T>toList());
	}
	
	protected abstract T convert(U obj);
	
	public static void main(String[] args) {
		ListViewModel<String, Integer> vm = new ListViewModel<String, Integer>(new ArrayList<String>()) {

			@Override
			protected String convert(Integer obj) {
				return Integer.toString(obj);
			}
		};
		
		vm.listProperty().getValue().add(1);
		vm.model.forEach(e -> System.out.println(e));
	}

}
