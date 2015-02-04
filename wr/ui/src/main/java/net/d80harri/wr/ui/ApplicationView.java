package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.ui.viewmodel.ApplicationViewModel;
import net.d80harri.wr.ui.viewmodel.TaskViewModel;

public class ApplicationView extends BorderPane implements Initializable {

	@FXML private TreeTableView<TaskViewModel> tree;
	@FXML private TaskView taskView;
	@FXML private TreeTableColumn<TaskViewModel, String> titleColumn;
	@FXML private MenuItem menuAppendChild;
	@FXML private MenuItem menuReload;
	@FXML private MenuItem menuDeleteSubtree;
	@FXML private Button button;

	private WrService service = new WrService();

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
	
	private ApplicationViewModel applicationViewModel;
	
	public ApplicationViewModel getApplicationViewModel() {
		if (applicationViewModel == null) {
			applicationViewModel = new ApplicationViewModel();
			applicationViewModel.getTaskTreeViewModel().selectionModelProperty().bindBidirectional(tree.selectionModelProperty());
			tree.rootProperty().bindBidirectional(applicationViewModel.getTaskTreeViewModel().rootTaskTreeViewModelProperty());
		}
		return applicationViewModel;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getApplicationViewModel().load(service);
		
		titleColumn.setCellValueFactory(p -> { 
			if (p.getValue().getValue() == null) {
				return null;
			} else {
				return p.getValue().getValue().titleProperty();
			}
		});
		
		menuAppendChild.setOnAction((e) -> applicationViewModel.getTaskTreeViewModel().addTaskToSelected());
		menuReload.setOnAction((e) -> getApplicationViewModel().getTaskTreeViewModel().reload(service) );
		menuDeleteSubtree.setOnAction((e) -> {
			int oldSelectIdx = getApplicationViewModel().getTaskTreeViewModel().getSelectedTask().getParent().getChildren().indexOf(applicationViewModel.getTaskTreeViewModel().getSelectedTask());
			getApplicationViewModel().getTaskTreeViewModel().deleteSelectedSubtree(service);
			tree.getSelectionModel().select(oldSelectIdx);
		});
		button.setOnAction(this::onButtonClicked);
	}

	private void onButtonClicked(ActionEvent evt) {
		System.out.println(applicationViewModel);
	}


}
