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
		
	private ObservableList<TreeViewModel> rootTaskViewModels;
	
	public ObservableList<TreeViewModel> getRootTaskViewModels() {
		if (rootTaskViewModels == null) {
			rootTaskViewModels = FXCollections.observableArrayList();
		}
		return rootTaskViewModels;
	}
	
	private ObjectProperty<TreeViewModel> selectedTask;
	
	public ObjectProperty<TreeViewModel> selectedTaskProperty() {
		if (selectedTask == null) {
			selectedTask = new SimpleObjectProperty<TreeViewModel>();
			selectedTask.addListener((obs, o, n) -> {
				if (o != null) {
					o.saveOrUpdate();
				}
			});
		}
		return selectedTask;
	}
	
	public TreeViewModel getSelectedTask() {
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
			getRootTaskViewModels().addAll(service.getAllTrees().stream().map((i) -> new TreeViewModel(new TaskViewModel(i, null, true))).collect(Collectors.toList()));
			setLoaded(true);
		}
	}
	
	public void reload(WrService service) {
		getRootTaskViewModels().clear();
		setLoaded(false);
		load(service);
	}
	
	public TreeViewModel addTaskToSelected() {
		TreeViewModel result = null;
		if (getSelectedTask() == null) {
			TaskViewModel model = new TaskViewModel(new TaskDto("No title"), null, true);
			result = new TreeViewModel(model);
			getRootTaskViewModels().add(result);
		} else {
			getSelectedTask().addNewChild();
		}

		selectedTaskProperty().set(result);
		return result;
	}

	public void deleteSelectedSubtree(WrService service) {
		getSelectedTask().delete(service);
		if (getSelectedTask().getParent() == null) {
			getRootTaskViewModels().remove(getSelectedTask());
		}
	}
	
	
}
