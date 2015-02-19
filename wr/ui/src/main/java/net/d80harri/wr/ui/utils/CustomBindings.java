package net.d80harri.wr.ui.utils;

import org.fxmisc.easybind.EasyBind;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class CustomBindings {

	public static <T> void bindSelectedItem(final Property<T> selectedItem, final ObservableValue<TreeItem<T>> selectedTreeItem) {
		selectedTreeItem.addListener(new ChangeListener<TreeItem<T>>() {
			private boolean updating = false;
			@Override
			public void changed(ObservableValue<? extends TreeItem<T>> observable,
					TreeItem<T> oldValue, TreeItem<T> newValue) {
				if (!updating) {
					updating = true;

					selectedItem.setValue(newValue.getValue());
					
					updating = false;
				}
			}
		});
	}
	
	public static <T> void bindSelectedItem(final Property<T> selectedItem, final SelectionModel<T> selectionModel) {
		selectedItem.bind(selectionModel.selectedItemProperty());
//		selectedTreeItem.addListener(new ChangeListener<TreeItem<T>>() {
//			private boolean updating = false;
//			@Override
//			public void changed(ObservableValue<? extends TreeItem<T>> observable,
//					TreeItem<T> oldValue, TreeItem<T> newValue) {
//				if (!updating) {
//					updating = true;
//
//					selectedItem.setValue(newValue.getValue());
//					
//					updating = false;
//				}
//			}
//		});
	}
	
	public static <T> ChangeListener<T> bindSelectedItem(TreeTableView<T> view, final ObservableValue<T> selectedItem) {
		ChangeListener<T> result = new ChangeListener<T>() {
			private boolean updating = false;
			
			@Override
			public void changed(ObservableValue<? extends T> observable,
					T oldValue, T newValue) {
				if (!updating) {
					updating = true;
					
					view.getSelectionModel().select(findItem(view.getRoot(), newValue));
					
					updating = false;
				}
			}
		};
		
		selectedItem.addListener(result);
		
		return result;
	}
	
	public static <T> void bindSelectedItemBidirectional(final Property<T> selectedItem, TreeTableView<T> view) {
		bindSelectedItem(selectedItem, EasyBind.select(view.selectionModelProperty()).selectObject(sm -> sm.selectedItemProperty()));
		bindSelectedItem(view, selectedItem);
	}
	
	private static <T> TreeItem<T> findItem(TreeItem<T> tree, T value) {
		if (tree.getValue() == value) {
			return tree;
		} else {
			for (TreeItem<T> child : tree.getChildren()) {
				TreeItem<T> res = findItem(child, value);
				if (res != null) {
					return res;
				}
			}
			return null;
		}
	}
}
