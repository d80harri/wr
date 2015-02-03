package net.d80harri.wr.ui.viewmodel;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class BindingsTest {

	@Test
	public void castBinding() {
		ObservableList<String> string = FXCollections.observableArrayList();
		ObservableList<Double> ints = new MappedList<Double, String>(string, i -> Double.parseDouble(i), i -> String.valueOf(i));
		
		ints.add(3.0);
		Assertions.assertThat(string).containsSequence("3.0");
		Assertions.assertThat(ints).containsSequence(3.0);
		
		string.add("2.0");
		Assertions.assertThat(string).containsSequence("3.0", "2.0");
		Assertions.assertThat(ints).containsSequence(3.0, 2.0);
		
		Collections.reverse(ints);
		Assertions.assertThat(string).containsSequence("2.0", "3.0");
		Assertions.assertThat(ints).containsSequence(2.0, 3.0);
		
		Collections.reverse(string);
		Assertions.assertThat(string).containsSequence("3.0", "2.0");
		Assertions.assertThat(ints).containsSequence(3.0, 2.0);
		
		string.remove("3.0");
		Assertions.assertThat(string).containsSequence("2.0");
		Assertions.assertThat(ints).containsSequence(2.0);
		
		string.add("3.0");
		ints.remove(2.0);
		Assertions.assertThat(string).containsSequence("3.0");
		Assertions.assertThat(ints).containsSequence(3.0);
		
	}

	@Test
	public void testListener() throws Throwable {
		ObservableList<String> string = FXCollections.observableArrayList();
		ObservableList<Double> ints = new MappedList<Double, String>(string, i -> Double.parseDouble(i), i -> String.valueOf(i));
		IntegerProperty stringUpdate = new SimpleIntegerProperty();
		IntegerProperty intsUpdate = new SimpleIntegerProperty();
		
		string.addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c) {
				stringUpdate.set(stringUpdate.get() + 1);
			};
		});
		
		ints.addListener(new ListChangeListener<Double>() {
			public void onChanged(ListChangeListener.Change<? extends Double> c) {
				intsUpdate.set(intsUpdate.get() + 1);
			};
		});
		
		ints.add(1.0);
		Assertions.assertThat(intsUpdate.get()).isEqualTo(1);
		Assertions.assertThat(stringUpdate.get()).isEqualTo(1);
		
		string.add("1");
		Assertions.assertThat(intsUpdate.get()).isEqualTo(2);
		Assertions.assertThat(stringUpdate.get()).isEqualTo(2);
	}
}
