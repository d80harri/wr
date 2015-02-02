package net.d80harri.wr.ui.viewmodel;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class ApplicationViewModel {
		
	private ObservableList<TaskDto> rootTasks;
	
	public ObservableList<TaskDto> getRootTasks() {
		if (rootTasks == null) {
			rootTasks = FXCollections.observableArrayList();
		}
		return rootTasks;
	}
	
	private ObservableList<TaskViewModel> rootTaskViewModels;
	
	public ObservableList<TaskViewModel> getRootTaskViewModels() {
		if (rootTaskViewModels == null) {
			rootTaskViewModels = new MappedList<TaskViewModel, TaskDto>(getRootTasks(), (i) -> new TaskViewModel(i, null, true));
		}
		return rootTaskViewModels;
	}
	
	private ObjectProperty<TaskViewModel> selectedTask;
	
	public ObjectProperty<TaskViewModel> selectedTaskProperty() {
		if (selectedTask == null) {
			selectedTask = new SimpleObjectProperty<TaskViewModel>();
			selectedTask.addListener((obs, o, n) -> {
				if (o != null) {
				o.saveOrUpdate();
				}
			});
		}
		return selectedTask;
	}
	
	public TaskViewModel getSelectedTask() {
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
			getRootTasks().addAll(service.getAllTrees());
			setLoaded(true);
		}
	}
	
	public void reload(WrService service) {
		getRootTasks().clear();
		setLoaded(false);
		load(service);
	}
	
	public void addTaskToSelected() {
		if (getSelectedTask() == null) {
			getRootTasks().add(new TaskDto("No title"));
		} else {
			getSelectedTask().addNewChild();
		}
	}

	public void deleteSelectedSubtree(WrService service) {
		getSelectedTask().delete(service);
		if (getSelectedTask().getParent() == null) {
			List<TaskDto> toDelete = getRootTasks().stream().filter((i) -> i.getId() == getSelectedTask().getId() || getSelectedTask().getId().equals(i.getId())).collect(Collectors.toList());
			getRootTasks().removeAll(toDelete);
		}
	}
	
	
}
