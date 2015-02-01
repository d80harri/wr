package net.d80harri.wr.ui.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import org.junit.Test;

public class ListViewModelTest {
	
	@Test
	public void simpleTest() throws Throwable {
		List<String> model = new ArrayList<String>();
		ListViewModel<String, Integer> vm = new ListViewModel<String, Integer>(model, i -> Integer.toString(i));
		
		vm.listProperty().getValue().add(1);
		assertThat(model).containsExactly("1");
		
		vm.listProperty().add(2);
		assertThat(model).containsExactly("1", "2");
		
		vm.listProperty().set(FXCollections.observableArrayList(1, 3, 5));
		assertThat(model).containsExactly("1", "3", "5");
		
		vm.listProperty().remove((Object)3);
		assertThat(model).containsExactly("1", "5");
		
		vm.listProperty().add(1, 3);
		assertThat(model).containsExactly("1", "3", "5");
		
		vm.listProperty().remove(1);
		assertThat(model).containsExactly("1", "5");
	}
	
	public String strModel;
	
	@Test
	public void test2() throws Throwable {
		List<String> model = new ArrayList<String>();
		ObservableList<String> list = FXCollections.observableList(model);
		list.addListener((ListChangeListener.Change<? extends String> c) -> {System.out.println(model);});
		list.add("1");
		list.add("2");
		
		list.setAll("3", "2");
		
		StringProperty p = new SimpleStringProperty();
		p.addListener((obs, o, n) -> strModel = n);
		System.out.println(strModel);
		p.set("asdf");
		System.out.println(strModel);
		
	}
}
