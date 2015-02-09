package net.d80harri.wr.ui.takstree;

import static org.fxmisc.easybind.EasyBind.listBind;
import static org.fxmisc.easybind.EasyBind.map;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import javax.inject.Inject;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.task.TaskPresentationModel;
import net.d80harri.wr.ui.utils.DebugUtils;

public class TaskTreePresenter implements Initializable {
	@FXML private TreeTableView<TaskPresentationModel> tree;
	@FXML private TreeTableColumn<TaskPresentationModel, String> titleColumn;
	@FXML private TreeTableColumn<TaskPresentationModel, Integer> debugColumn;
	@FXML private Button addButton;
	
	@Inject WrService service;
	
	private TaskPresentationModel model;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		load();
		
		titleColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TaskPresentationModel,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<TaskPresentationModel, String> param) {
				return param.getValue().getValue().titleProperty();
			}
		});
		
		debugColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TaskPresentationModel,Integer>, ObservableValue<Integer>>() {

			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<TaskPresentationModel, Integer> param) {
				ObjectProperty<Integer> prop = new SimpleObjectProperty<Integer>();
				prop.bind(Bindings.size(param.getValue().getValue().getChildren()).asObject());
				return prop;
			}
		});
		
		addButton.setOnAction((e) -> addRootTask());
	}
	
	private void load() {
		model = createRootModel(service.getAllTrees());
		tree.setRoot(createTreeItem(model));
	}
	
	private TaskPresentationModel createRootModel(Collection<TaskDto> trees) {
		TaskPresentationModel result = new TaskPresentationModel();
		for (TaskDto dto : trees) {
			result.getChildren().add(createModel(dto));
		}
		return result;
	}
	
	private TaskPresentationModel createModel(TaskDto dto) {
		TaskPresentationModel result = new TaskPresentationModel(dto);
		
		for (TaskDto child : dto.getChildren()) {
			result.getChildren().add(createModel(child));
		}
		
		return result;
	}

	private TreeItem<TaskPresentationModel> createTreeItem(
			TaskPresentationModel model) {
		TreeItem<TaskPresentationModel> result = new TreeItem<TaskPresentationModel>(model);
		listBind(result.getChildren(), map(model.getChildren(), i -> createTreeItem(i)));
		return result;
	}
	
	@FXML
	private void onKeyPressedInTable(KeyEvent evt) {
		switch(evt.getCode()) {
		case ENTER: 
			addSiblingTask(); 
			evt.consume(); 
			break;
		case TAB:
			if (evt.isShiftDown()) {
				outdentTask();
			} else {
				indentTask();
			}
			evt.consume();
			break;
		case DELETE: 
			deleteTask(); 
			evt.consume();
			break;
		default: // nothing to do
		}
	}

	private void deleteTask() {
		doWithSelectedTask(s -> {
			service.deleteSubtree(s.getId());
			if (s.getParent() != null) {
				s.getParent().getChildren().remove(s);
			}
		});
	}

	private void indentTask() {
		doWithSelectedTask(s -> {
			int idx = s.getParent().getChildren().indexOf(s);
			if (idx != 0) {
				DebugUtils.printTreeStructure(s.getParent(), 0, i -> i.getChildren());
				TaskPresentationModel precessor = s.getParent().getChildren().get(idx-1);
				precessor.getChildren().add(s);
				DebugUtils.printTreeStructure(precessor, 0, i -> i.getChildren());
			}
		});
	}

	private void outdentTask() {
		doWithSelectedTask(s -> {
			TaskPresentationModel grandParent = s.getParent().getParent();
			if (grandParent != null) {
				int idxOfParent = grandParent.getChildren().indexOf(s.getParent());
				grandParent.getChildren().add(idxOfParent+1, s);
			}
		});
	}

	private void addSiblingTask() {
		doWithSelectedTask(s -> {
			int idxOfSelected = s.getParent().getChildren().indexOf(s);
			
			s.getParent().getChildren().add(idxOfSelected+1, new TaskPresentationModel(new TaskDto("new")));
		});
	}
	
	private void addRootTask() {
		model.getChildren().add(new TaskPresentationModel(new TaskDto("new")));
	}
	
	private void doWithSelectedTask(Consumer<TaskPresentationModel> func) {
		TreeItem<TaskPresentationModel> selectedItem = tree.getSelectionModel().getSelectedItem();
		if (selectedItem.getValue() != this.model) {
			func.accept(selectedItem.getValue());
		}
	}
}
