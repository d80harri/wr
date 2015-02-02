package net.d80harri.wr.ui.viewmodel;

import java.util.function.Function;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class BindingsTest {

	@Test
	public void castBinding() {
		ObjectProperty<TreeItem<TaskViewModel>> treeItem = new SimpleObjectProperty<>();
		ObjectProperty<TreeViewModel> taskTreeItemViewModel = new SimpleObjectProperty<>();
		
		taskTreeItemViewModel.bindBidirectional(taskTreeItemViewModel);

		bindWithCast(treeItem, taskTreeItemViewModel, (TreeViewModel i) -> (TreeViewModel) i);
		
		TreeViewModel m = new TreeViewModel(null);
		treeItem.set(m);
		Assertions.assertThat(taskTreeItemViewModel.get()).isSameAs(m);
	}

	private <T, U> void bindWithCast(Property<T> treeItem,
			Property<U> taskTreeItemViewModel, Function<T, U> convert1, Function<U, T> convert2) {
		treeItem.addListener(new ChangeListener<T>() {

			@Override
			public void changed(
					ObservableValue<? extends T> obs,
					T o, T n) {
				taskTreeItemViewModel.setValue(convert1.apply(n)); 
			}
		});
		
		taskTreeItemViewModel.addListener(new ChangeListener<U>() {

			@Override
			public void changed(
					ObservableValue<? extends U> arg0,
					U arg1, U n) {
				treeItem.setValue(convert2.apply(n));
			}
		});
	}
}
