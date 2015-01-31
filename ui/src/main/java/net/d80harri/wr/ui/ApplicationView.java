package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.viewmodel.ApplicationViewModel;
import net.d80harri.wr.ui.viewmodel.MappedList;
import net.d80harri.wr.ui.viewmodel.TaskViewModel;

public class ApplicationView extends BorderPane implements Initializable {

	@FXML private TreeTableViewWithItems<TaskViewModel> tree;
	@FXML private TaskView taskView;
	@FXML private TreeTableColumn<TaskViewModel, String> titleColumn;
	@FXML private MenuItem menuAppendChild;
	@FXML private MenuItem menuReload;
	@FXML private MenuItem menuDeleteSubtree;
	@FXML private Button button;

	private WrService service = new WrService();

	private ApplicationViewModel applicationViewModel = new ApplicationViewModel();

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
		titleColumn.setCellValueFactory((p) -> p.getValue() == null || p.getValue().getValue() == null ? null : p.getValue().getValue()
				.titleProperty());
		tree.getSelectionModel().selectedItemProperty()
				.addListener(this::onSelectedTaskChanged);
		tree.setRoot(new TreeItem<>(applicationViewModel.getRootTaskViewModel()));
		tree.setItems(applicationViewModel.getRootTaskViewModel().getChildren());
		//tree.rootProperty().bindBidirectional(applicationViewModel.rootItemProperty());
		menuAppendChild.setOnAction(this::addTaskToSelected);
		menuReload.setOnAction((e) -> applicationViewModel.getRootTaskViewModel().load(service) );
		menuDeleteSubtree.setOnAction(this::deleteSelectedSubtree);
		button.setOnAction(this::onButtonClicked);
	}

	private void onButtonClicked(ActionEvent evt) {
		System.out.println();
	}

	private void addTaskToSelected(ActionEvent evt) {
		tree.getSelectionModel()
				.getSelectedItem().getValue().addNewChild();
	}
	
	private void deleteSelectedSubtree(ActionEvent evt) {
		throw new RuntimeException("NYI");
	}

	private void onSelectedTaskChanged(
			ObservableValue<? extends TreeItem<TaskViewModel>> observable,
			TreeItem<TaskViewModel> oldValue, TreeItem<TaskViewModel> newValue) {
		if (oldValue != null && oldValue.getValue() != applicationViewModel.getRootTaskViewModel())
			oldValue.getValue().saveOrUpdate();
		taskView.setModel(newValue.getValue());
	}

}
