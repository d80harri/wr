package net.d80harri.wr.ui.viewmodel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import javafx.collections.FXCollections;

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
}
