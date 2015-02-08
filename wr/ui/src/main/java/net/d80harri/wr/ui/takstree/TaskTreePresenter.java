package net.d80harri.wr.ui.takstree;

import static org.fxmisc.easybind.EasyBind.listBind;
import static org.fxmisc.easybind.EasyBind.map;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

import org.fxmisc.easybind.EasyBind;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.task.TaskPresentationModel;

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
		model = new TaskPresentationModel();

		tree.setRoot(createTreeItem(model));
		
		for (TaskDto dto : service.getAllTrees()) {
			TaskPresentationModel tpm = new TaskPresentationModel(dto);
			model.getChildren().add(tpm);
		}
		model.getChildren().addAll(service.getAllTrees().stream()
				.map(i -> new TaskPresentationModel(i))
				.collect(Collectors.toList()));
	}

	private TaskPresentationModel createModel(List<TaskDto> allTrees) {
		TaskPresentationModel model = new TaskPresentationModel();
		model.getChildren().addAll(allTrees.stream()
				.map(i -> new TaskPresentationModel(i)) 
				.collect(Collectors.toList()));
		return model;
	}

	private TreeItem<TaskPresentationModel> createTreeItem(
			TaskPresentationModel model) {
		TreeItem<TaskPresentationModel> result = new TreeItem<TaskPresentationModel>(model);
		listBind(result.getChildren(), map(model.getChildren(), i -> new TreeItem<TaskPresentationModel>(i)));
		model.getChildren().addListener(new ListChangeListener<TaskPresentationModel>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends TaskPresentationModel> c) {
				while (c.next()) {
					if (c.wasAdded()) {
						for (TaskPresentationModel added : c.getAddedSubList()) {
							result.getChildren().addAll(createTreeItem(added));
						}
					}
				}
			}
			
		});
//		for (TaskPresentationModel child : model.getChildren()) {
//			createTreeItem(child);
//		}
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
			if (evt.isAltDown()) {
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
				TaskPresentationModel precessor = s.getParent().getChildren().get(idx-1);
				precessor.getChildren().add(s);
			}
		});
	}

	private void outdentTask() {
		throw new RuntimeException("NYI");
	}

	private void addSiblingTask() {
		doWithSelectedTask(s -> {
			s.getParent().getChildren().add(new TaskPresentationModel(new TaskDto("new")));
		});
	}
	
	private void addRootTask() {
		model.getChildren().add(new TaskPresentationModel(new TaskDto("new")));
	}
	
	private void doWithSelectedTask(Consumer<TaskPresentationModel> func) {
		TreeItem<TaskPresentationModel> selectedItem = tree.getSelectionModel().getSelectedItem();
		TaskDto selectedTask = selectedItem.getValue().getModel();
		if (selectedTask != TaskPresentationModel.NULL_TASK) {
			func.accept(selectedItem.getValue());
		}
	}
}
