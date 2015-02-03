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
import net.d80harri.wr.ui.TaskView;

public class ApplicationViewModel {
		
	private TaskTreeViewModel rootTaskTreeViewModel = new TaskTreeViewModel(null, new TaskViewModel(new TaskDto(), null, true));
	
	public TaskTreeViewModel getRootTaskTreeViewModel() {
		return rootTaskTreeViewModel;
	}
	
	private ObjectProperty<TaskTreeViewModel> selectedTask;
	
	public ObjectProperty<TaskTreeViewModel> selectedTaskProperty() {
		if (selectedTask == null) {
			selectedTask = new SimpleObjectProperty<TaskTreeViewModel>();
			selectedTask.addListener((obs, o, n) -> {
				if (o != null) {
					o.saveOrUpdate();
				}
			});
		}
		return selectedTask;
	}
	
	public TaskTreeViewModel getSelectedTask() {
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
			getRootTaskTreeViewModel().getChildren().addAll(service.getAllTrees().stream().map((i) -> new TaskTreeViewModel(null, new TaskViewModel(i, null, true))).collect(Collectors.toList()));
			setLoaded(true);
		}
	}
	
	public void reload(WrService service) {
		getRootTaskTreeViewModel().getChildren().clear();
		setLoaded(false);
		load(service);
	}
	
	public TaskTreeViewModel addTaskToSelected() {
		TaskTreeViewModel result = null;
		if (getSelectedTask() == null) {
			TaskViewModel model = new TaskViewModel(new TaskDto("No title"), null, true);
			result = new TaskTreeViewModel(getRootTaskTreeViewModel(), model);
		} else {
			getSelectedTask().addNewChild();
		}

		selectedTaskProperty().set(result);
		return result;
	}

	public void deleteSelectedSubtree(WrService service) {
		getSelectedTask().delete(service);
		if (getSelectedTask().getParent() == null) {
			getRootTaskTreeViewModel().getChildren().remove(getSelectedTask());
		}
	}
	
	
}
