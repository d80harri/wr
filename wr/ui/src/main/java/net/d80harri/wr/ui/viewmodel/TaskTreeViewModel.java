package net.d80harri.wr.ui.viewmodel;

import net.d80harri.wr.service.WrService;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class TaskTreeViewModel extends TreeViewModel<TaskViewModel> {

	public TaskTreeViewModel(TreeItem<TreeViewModel<TaskViewModel>> treeItem,
			TaskViewModel model) {
		super(treeItem, model);
	}

	@Override
	public ObservableList<TaskViewModel> getChildren() {
		return getModel().getChildren();
	}

	
	public void saveOrUpdate() {
		getModel().saveOrUpdate();
	}

	public void addNewChild() {
		getModel().addNewChild();
	}

	public void delete(WrService service) {
		getModel().delete(service);
	}
}
