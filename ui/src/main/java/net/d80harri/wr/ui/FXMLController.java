package net.d80harri.wr.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;
import net.d80harri.wr.db.SessionHandler;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class FXMLController implements Initializable {
	private WrService service = new WrService();

	private final StringProperty dummy = new SimpleStringProperty();

	public StringProperty dummyProperty() {
		return this.dummy;
	}

	public String getDummy() {
		return dummy.get();
	}

	public void setDummy(String dummy) {
		this.dummy.set(dummy);
	}
	
	@FXML
	private Label label;

	@FXML
	private TreeTableView<TaskDto> tree;

	@FXML
	private ContextMenu nodeMenu;

	@FXML
	private MenuItem menuAppendChild;

	@FXML
	private MenuItem menuDeleteSubtree;

	@FXML
	private TaskView taskView;

	@FXML
	private TreeTableColumn<TaskDto, String> titleColumn;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		dummy.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				System.out.println("blubb");
			}
		});
		SessionHandler.configure("jdbc:h2:~/productive;AUTO_SERVER=true");
		// initTestData();
		// TODO

		tree.setRoot(convertToTreeItem(service.getAllTrees()));

		titleColumn
				.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TaskDto, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(
							CellDataFeatures<TaskDto, String> param) {
						if (param.getValue().getValue() == null) {
							return new ReadOnlyStringWrapper();
						} else {
							return new ReadOnlyStringWrapper(param.getValue()
									.getValue().getTitle());
						}
					}
				});

		tree.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<TaskDto>>() {

					@Override
					public void changed(
							ObservableValue<? extends TreeItem<TaskDto>> observable,
							TreeItem<TaskDto> oldValue,
							TreeItem<TaskDto> newValue) {
						if (taskView.getTask() != null)
							taskView.getTask().update();
						if (newValue.getValue() == null) {
							taskView.setTask(null);
						} else {
							taskView.setTask(new TaskViewModel(newValue.getValue().getId(), newValue.getValue().getTitle(), newValue.getValue().getContent()));
						}
					}
				});
	}

	@FXML
	public void appendChild(ActionEvent evt) {
		TreeItem<TaskDto> selectedItem = tree.getSelectionModel()
				.getSelectedItem();
		if (selectedItem != null) {
			TaskDto newTask = new TaskDto("New Task");
			TreeItem<TaskDto> newItem = new TreeItem<>(newTask);

			if (selectedItem.getValue() == null) {
				service.storeSubtree(null, newTask);
			} else {
				service.storeSubtree(selectedItem.getValue().getId(), newTask);
			}
			selectedItem.getChildren().add(newItem);
			tree.getSelectionModel().select(newItem);
		}
	}

	@FXML
	public void deleteSubTree(ActionEvent evt) {
		TreeItem<TaskDto> selectedItem = tree.getSelectionModel()
				.getSelectedItem();
		if (selectedItem != null) {
			service.deleteSubtree(selectedItem.getValue().getId());
			selectedItem.getParent().getChildren().remove(selectedItem);
		}
	}

	private TreeItem<TaskDto> convertToTreeItem(List<TaskDto> tasks) {
		TreeItem<TaskDto> result = new TreeItem<>(null);

		for (TaskDto child : tasks) {
			TreeItem<TaskDto> item = convertToTreeItem(child);
			result.getChildren().add(item);
		}

		return result;
	}

	private TreeItem<TaskDto> convertToTreeItem(TaskDto task) {
		TreeItem<TaskDto> result = new TreeItem<>(task);

		for (TaskDto child : task.getChildren()) {
			result.getChildren().add(convertToTreeItem(child));
		}

		return result;
	}
}
