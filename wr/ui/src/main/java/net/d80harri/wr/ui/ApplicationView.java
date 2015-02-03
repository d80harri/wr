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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.ui.viewmodel.ApplicationViewModel;
import net.d80harri.wr.ui.viewmodel.TaskTreeViewModel;
import net.d80harri.wr.ui.viewmodel.TaskViewModel;
import net.d80harri.wr.ui.viewmodel.TreeViewModel;

public class ApplicationView extends BorderPane implements Initializable {

	@FXML private TreeTableView<TaskViewModel> tree;
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
		titleColumn.setCellValueFactory(p -> ((TaskTreeViewModel)p.getValue()).titleProperty());
		tree.setRoot(applicationViewModel.getRootTaskTreeViewModel());

		menuAppendChild.setOnAction((e) -> applicationViewModel.addTaskToSelected());
		menuReload.setOnAction((e) -> applicationViewModel.reload(service) );
		menuDeleteSubtree.setOnAction((e) -> applicationViewModel.deleteSelectedSubtree(service));
		button.setOnAction(this::onButtonClicked);
		
		applicationViewModel.selectedTaskProperty().addListener((obs, o, n) -> tree.getSelectionModel().select((TreeViewModel)n));
		tree.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> applicationViewModel.selectedTaskProperty().set((TaskTreeViewModel)n));
		
//		applicationViewModel.selectedTaskProperty().bind(tree.getSelectionModel().selectedItemProperty());
		applicationViewModel.load(service);
		
		applicationViewModel.selectedTaskProperty().addListener((obs, o, n) -> taskView.setModel(n.getValue()));
	}

	private void onButtonClicked(ActionEvent evt) {
		System.out.println(applicationViewModel);
	}


}
