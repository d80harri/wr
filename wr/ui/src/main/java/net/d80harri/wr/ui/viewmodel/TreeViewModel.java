package net.d80harri.wr.ui.viewmodel;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public abstract class TreeViewModel<T> extends TreeItem<T> {

	protected T model;
	
	public TreeViewModel(TreeViewModel<T> parent, T model) {
		if (parent != null) {
			parent.getChildren().add(this);
		}
		this.model = model;
	}

	public abstract ObservableList<TreeViewModel<T>> getChildrenData();
	
	private ObservableList<TreeItem<T>> children;
	@Override
	public ObservableList<TreeItem<T>> getChildren() {
		if (this.children == null) {
			children = new MappedList<TreeItem<T>, TreeViewModel<T>>(getChildrenData(), i -> (TreeItem<T>) i, i -> (TreeViewModel<T>) i);
		}
		return children;
	}
	

	public T getModel() {
		return model;
	}
	
}
