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
import javafx.beans.value.ChangeListener;
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

import org.fxmisc.easybind.EasyBind;

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
	
	private TreeItem<TaskPresentationModel> model;
	
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
				prop.bind(Bindings.size(param.getValue().getChildren()).asObject());
				return prop;
			}
		});
		
		addButton.setOnAction((e) -> addRootTask());
	}
	
	private void load() {
		model = createRootModel(service.getAllTrees());
		tree.setRoot(model);
	}
	
	private TreeItem<TaskPresentationModel> createRootModel(Collection<TaskDto> trees) {
		TreeItem<TaskPresentationModel> result = new TreeItem<TaskPresentationModel>(new TaskPresentationModel());
		for (TaskDto dto : trees) {
			result.getChildren().add(createModel(dto));
		}
		return result;
	}
	
	private TreeItem<TaskPresentationModel> createModel(TaskDto dto) {
		TreeItem<TaskPresentationModel> result = new TreeItem<TaskPresentationModel>(new TaskPresentationModel(dto));
		
		for (TaskDto child : dto.getChildren()) {
			result.getChildren().add(createModel(child));
		}
		
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
			service.deleteSubtree(s.getValue().getId());
			if (s.getParent() != null) {
				s.getParent().getChildren().remove(s);
			}
		});
	}

	private void indentTask() {
		doWithSelectedTask(s -> {
			int idx = s.getParent().getChildren().indexOf(s);
			if (idx != 0) {
				TreeItem<TaskPresentationModel> precessor = s.getParent().getChildren().get(idx-1);
				s.getParent().getChildren().remove(s);
				precessor.getChildren().add(s);
				
				tree.getSelectionModel().select(s);
				s.getParent().setExpanded(true);
			}
		});
	}

	private void outdentTask() {
		doWithSelectedTask(s -> {
			TreeItem<TaskPresentationModel> grandParent = s.getParent().getParent();
			if (grandParent != null) {
				int idxOfParent = grandParent.getChildren().indexOf(s.getParent());
				grandParent.getChildren().add(idxOfParent+1, s);
				tree.getSelectionModel().select(s);
			}
		});
	}

	private void addSiblingTask() {
		doWithSelectedTask(s -> {
			int idxOfSelected = s.getParent().getChildren().indexOf(s);
			
			s.getParent().getChildren().add(idxOfSelected+1, new TreeItem<TaskPresentationModel>(new TaskPresentationModel(new TaskDto("new"))));
		});
	}
	
	private void addRootTask() {
		model.getChildren().add(new TreeItem<TaskPresentationModel>(new TaskPresentationModel(new TaskDto("new"))));
	}
	
	private void doWithSelectedTask(Consumer<TreeItem<TaskPresentationModel>> func) {
		TreeItem<TaskPresentationModel> selectedItem = tree.getSelectionModel().getSelectedItem();
		if (selectedItem != this.model) {
			func.accept(selectedItem);
		}
	}
}
