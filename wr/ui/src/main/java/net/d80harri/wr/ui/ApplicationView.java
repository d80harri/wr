package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.BorderPane;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.ui.viewmodel.ApplicationViewModel;
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
		tree.setRoot(new TreeItem<>(null));
		tree.setItems(applicationViewModel.getRootTaskViewModels());

		menuAppendChild.setOnAction((e) -> applicationViewModel.addTaskToSelected());
		menuReload.setOnAction((e) -> applicationViewModel.reload(service) );
		menuDeleteSubtree.setOnAction((e) -> applicationViewModel.deleteSelectedSubtree(service));
		button.setOnAction(this::onButtonClicked);
		
		applicationViewModel.selectedTaskProperty().bind(Bindings.select(tree.getSelectionModel().selectedItemProperty(), "value"));
		applicationViewModel.load(service);
		
		taskView.modelProperty().bind(applicationViewModel.selectedTaskProperty());
	}

	private void onButtonClicked(ActionEvent evt) {
		
	}


}
