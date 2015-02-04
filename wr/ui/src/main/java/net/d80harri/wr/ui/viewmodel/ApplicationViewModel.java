package net.d80harri.wr.ui.viewmodel;

import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class ApplicationViewModel {
		
	private TreeItem<TaskViewModel> rootTaskTreeViewModel;
	
	public TreeItem<TaskViewModel> getRootTaskTreeViewModel() {
		if (rootTaskTreeViewModel == null) {
			rootTaskTreeViewModel = createRootTreeItem();			
		}
		return rootTaskTreeViewModel;
	}

	private ObservableList<TaskViewModel> rootTasks;
	
	public ObservableList<TaskViewModel> getRootTasks() {
		if (rootTasks == null) {
			rootTasks = FXCollections.observableArrayList();
		}
		return rootTasks;
	}
	
	private ObjectProperty<TreeItem<TaskViewModel>> selectedTask;
	
	public ObjectProperty<TreeItem<TaskViewModel>> selectedTaskProperty() {
		if (selectedTask == null) {
			selectedTask = new SimpleObjectProperty<TreeItem<TaskViewModel>>();
			selectedTask.addListener((obs, o, n) -> {
				if (o != null) {
					o.getValue().saveOrUpdate();
				}
			});
		}
		return selectedTask;
	}
	
	public TreeItem<TaskViewModel> getSelectedTask() {
		return selectedTaskProperty().get();
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
			getRootTaskTreeViewModel().getChildren().addAll(service.getAllTrees().stream().map((i) -> createTreeItem(new TaskViewModel(i, null, true))).collect(Collectors.toList()));
			setLoaded(true);
		}
	}
	
	public void reload(WrService service) {
		getRootTaskTreeViewModel().getChildren().clear();
		setLoaded(false);
		load(service);
	}
	
	public TreeItem<TaskViewModel> addTaskToSelected() {
		TreeItem<TaskViewModel> result = null;
		if (getSelectedTask() == null) {
			TaskViewModel model = new TaskViewModel(new TaskDto("No title"), null, true);
			result = new TreeItem<TaskViewModel>(model);
			getRootTaskTreeViewModel().getChildren().add(result);
		} else {
			TaskViewModel dto = getSelectedTask().getValue().addNewChild();
			result = new TreeItem<TaskViewModel>(dto);
			getSelectedTask().getChildren().add(result);
		}

		selectedTaskProperty().set(result);
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
