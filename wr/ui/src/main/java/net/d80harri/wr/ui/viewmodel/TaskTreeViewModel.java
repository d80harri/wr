package net.d80harri.wr.ui.viewmodel;

import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class TaskTreeViewModel {
		
	private ObjectProperty<TreeItem<TaskViewModel>> rootTaskTreeViewModel;
	
	public ObjectProperty<TreeItem<TaskViewModel>> rootTaskTreeViewModelProperty() {
		if (rootTaskTreeViewModel == null) {
			rootTaskTreeViewModel = new SimpleObjectProperty<TreeItem<TaskViewModel>>(createRootTreeItem());
		}
		return rootTaskTreeViewModel;
	}
	
	public TreeItem<TaskViewModel> getRootTaskTreeItem() {
		return rootTaskTreeViewModel.get();
	}

	private ObservableList<TaskViewModel> rootTasks;
	
	public ObservableList<TaskViewModel> getRootTasks() {
		if (rootTasks == null) {
			rootTasks = FXCollections.observableArrayList();
		}
		return rootTasks;
	}
	
	private ObjectProperty<TreeTableViewSelectionModel<TaskViewModel>> selectionModel;
	
	public ObjectProperty<TreeTableViewSelectionModel<TaskViewModel>> selectionModelProperty() {
		if (selectionModel == null) {
			selectionModel = new SimpleObjectProperty<TreeTableView.TreeTableViewSelectionModel<TaskViewModel>>();
		}
		return selectionModel;
	}
	
	public TreeTableViewSelectionModel<TaskViewModel> getSelectionModel() {
		return selectionModel.get();
	}
	
	public TreeItem<TaskViewModel> getSelectedTask() {
		return getSelectionModel().getSelectedItem();
	}
	
	public void setSelectedTask(TreeItem<TaskViewModel> selected) {
		getSelectionModel().select(selected);
	}
	
	private BooleanProperty loaded;
	
	public BooleanProperty loadedProperty() {
		if (loaded == null) {
			loaded = new SimpleBooleanProperty(false);
		}
		
		return loaded;
	}
	
	public boolean isLoaded() {
		return loadedProperty().get();
	}
	
	public void setLoaded(boolean loaded) {
		loadedProperty().set(loaded);
	}
	
	
	public void load(WrService service) {
		if (!isLoaded()) {
			getRootTaskTreeItem().getChildren().addAll(service.getAllTrees().stream().map((i) -> createTreeItem(new TaskViewModel(i, null, true))).collect(Collectors.toList()));
			setLoaded(true);
		}
	}
	
	public void reload(WrService service) {
		getRootTaskTreeItem().getChildren().clear();
		setLoaded(false);
		load(service);
	}
	
	public TreeItem<TaskViewModel> addTaskToSelected() {
		TreeItem<TaskViewModel> result = null;
		if (getSelectedTask() == null) {
			TaskViewModel model = new TaskViewModel(new TaskDto("No title"), null, true);
			result = new TreeItem<TaskViewModel>(model);
			getRootTaskTreeItem().getChildren().add(result);
		} else {
			TaskViewModel dto = getSelectedTask().getValue().addNewChild();
			result = new TreeItem<TaskViewModel>(dto);
			getSelectedTask().getChildren().add(result);
		}

		setSelectedTask(result);
		return result;
	}

	public void deleteSelectedSubtree(WrService service) {
		if (getSelectedTask() != null) {
			getSelectedTask().getValue().delete(service);
			getSelectedTask().getParent().getChildren().remove(getSelectedTask());
		}
	}
	
	
	private TreeItem<TaskViewModel> createRootTreeItem() {
		TreeItem<TaskViewModel> result = new TreeItem<TaskViewModel>(new TaskViewModel(new TaskDto("root"), null, true));
		
		for (TaskViewModel model : getRootTasks()) {
			result.getChildren().add(createTreeItem(model));
		}
		
		return result;
	}

	private TreeItem<TaskViewModel> createTreeItem(TaskViewModel model) {
		TreeItem<TaskViewModel> result = new TreeItem<TaskViewModel>(model);
		
		for (TaskViewModel child : model.getChildrenViews()) {
			result.getChildren().add(createTreeItem(child));
		}
		
		return result;
	}
	
}
