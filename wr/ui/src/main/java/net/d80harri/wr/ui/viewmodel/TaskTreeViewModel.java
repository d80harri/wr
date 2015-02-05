package net.d80harri.wr.ui.viewmodel;

import java.util.stream.Collectors;

import org.fxmisc.easybind.EasyBind;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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
	
	private ObjectProperty<TreeItem<TaskViewModel>> selectedTaskTreeItem;
	
	public ReadOnlyObjectProperty<TreeItem<TaskViewModel>> selectedTaskTreeItemProperty() {
		if (selectedTaskTreeItem == null) {
			selectedTaskTreeItem = new SimpleObjectProperty<TreeItem<TaskViewModel>>();
			selectedTaskTreeItem.bind(EasyBind.select(selectionModelProperty()).selectObject(sm -> sm.selectedItemProperty())); // TODO: easybind
			selectedTaskTreeItem.addListener(this::onSelectionChanged);
		}
		return selectedTaskTreeItem;
	}
	
	private void onSelectionChanged(ObservableValue<? extends TreeItem<TaskViewModel>> obs, TreeItem<TaskViewModel> old, TreeItem<TaskViewModel> n) {
		getSelectionModel().select(n);
		n.getValue().saveOrUpdate();
	}
	
	public TreeItem<TaskViewModel> getSelectedTaskTreeItem() {
		return selectedTaskTreeItemProperty().get();
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
		
		if (getSelectedTaskTreeItem() == null) {
			TaskViewModel model = new TaskViewModel(new TaskDto("No title"), null, true);
			result = new TreeItem<TaskViewModel>(model);
			getRootTaskTreeItem().getChildren().add(result);
		} else {
			TaskViewModel dto = getSelectedTaskTreeItem().getValue().addNewChild();
			result = new TreeItem<TaskViewModel>(dto);
			getSelectedTaskTreeItem().getChildren().add(result);
		}

		getSelectedTaskTreeItem().setExpanded(true);
		setSelectedTask(result);
		return result;
	}

	public void deleteSelectedSubtree(WrService service) {
		if (getSelectedTaskTreeItem() != null) {
			TaskViewModel toDelete = getSelectedTaskTreeItem().getValue();
			TreeItem<TaskViewModel> nextSelect = getSelectedTaskTreeItem().nextSibling();
			if (nextSelect == null) {
				nextSelect = getSelectedTaskTreeItem().getParent();
			}
			getSelectedTaskTreeItem().getParent().getChildren().remove(getSelectedTaskTreeItem());
			getSelectionModel().select(nextSelect);
			toDelete.delete(service);
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
