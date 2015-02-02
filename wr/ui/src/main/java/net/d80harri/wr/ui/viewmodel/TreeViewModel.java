package net.d80harri.wr.ui.viewmodel;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public abstract class TreeViewModel<T> {

	private TreeItem<TreeViewModel<T>> treeItem;
	private T model;
	
	public TreeViewModel(TreeItem<TreeViewModel<T>> treeItem, T model) {
		this.treeItem = treeItem;
		this.model = model;
	}

	public abstract ObservableList<T> getChildren();
	
	private ObservableList<TreeViewModel<T>> treeViewModels;
	
	public ObservableList<TreeViewModel<T>> getTreeViewModels() {
		if (treeViewModels == null) {
			treeViewModels = new MappedList<TreeViewModel<T>, T>(getChildren(), i -> new TreeViewM)
		}
		return treeViewModels;
	}
	
	private ObservableList<TreeItem<TreeViewModel<T>>> treeItems; 
	
	public ObservableList<TreeItem<TreeViewModel<T>>> getTreeItems() {
		if (treeItems == null) {
			treeItems = new MappedList<TreeItem<TreeViewModel<T>>, TreeViewModel<T>>(getTreeViewModels(), (i) -> new TreeItem<TreeViewModel<T>>(i));
		}
		return treeItems;
	}

	public T getModel() {
		return model;
	}
	
}
