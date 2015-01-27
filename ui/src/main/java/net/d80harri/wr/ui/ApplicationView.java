package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.viewmodel.TaskViewModel;

public class ApplicationView extends BorderPane implements Initializable {

	@FXML
	private TreeTableView<TaskViewModel> tree;
	@FXML
	private TaskView taskView;
	@FXML
	private TreeTableColumn<TaskViewModel, String> titleColumn;
	@FXML
	private MenuItem menuAppendChild;

	private WrService service = new WrService();

	private TaskViewModel model = new TaskViewModel(new TaskDto("root"));

	public ApplicationView() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"/fxml/Application.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		titleColumn.setCellValueFactory((p) -> p.getValue().getValue()
				.titleProperty());
		tree.getSelectionModel().selectedItemProperty()
				.addListener(this::onSelectedTaskChanged);
		tree.setRoot(createRootTreeItem());
		menuAppendChild.setOnAction(this::addTaskToSelected);
	}

	private TreeItem<TaskViewModel> createRootTreeItem() {
		TreeItem<TaskViewModel> result = new TreeItem<TaskViewModel>(model);
		List<TaskDto> allTrees = service.getAllTrees();
		for (TaskDto dto : allTrees) {
			result.getChildren().add(createTreeItem(dto));
		}
		return result;
	}

	private TreeItem<TaskViewModel> createTreeItem(TaskDto dto) {
		TreeItem<TaskViewModel> result = new TreeItem<TaskViewModel>(new TaskViewModel(dto));

		for (TaskDto child : dto.getChildren()) {
			result.getChildren().add(createTreeItem(child));
		}

		return result;
	}

	private void addTaskToSelected(ActionEvent evt) {
		TreeItem<TaskViewModel> selectedItem = tree.getSelectionModel()
				.getSelectedItem();
		TreeItem<TaskViewModel> newItem = new TreeItem<TaskViewModel>(new TaskViewModel(new TaskDto("New task")));
		selectedItem.getChildren().add(
				newItem);
		tree.getSelectionModel().select(newItem);
		taskView.focusOnTitle();
	}

	private void onSelectedTaskChanged(
			ObservableValue<? extends TreeItem<TaskViewModel>> observable,
			TreeItem<TaskViewModel> oldValue, TreeItem<TaskViewModel> newValue) {
		taskView.setModel(newValue.getValue());
		if (oldValue != null && oldValue.getValue() != model)
			oldValue.getValue().saveOrUpdate();
	}

}
