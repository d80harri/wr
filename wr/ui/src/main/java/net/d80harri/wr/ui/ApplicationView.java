package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
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
import net.d80harri.wr.ui.viewmodel.TreeViewModel;
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
		Bindings.bindContent(tree.getRoot().getChildren(), applicationViewModel.getRootTaskViewModels());
		
		applicationViewModel.getRootTaskViewModels().addListener(new ListChangeListener<TreeViewModel>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends TreeViewModel> arg0) {
				System.out.println(arg0);
			}
			
		});
		
		menuAppendChild.setOnAction((e) -> applicationViewModel.addTaskToSelected());
		menuReload.setOnAction((e) -> applicationViewModel.reload(service) );
		menuDeleteSubtree.setOnAction((e) -> applicationViewModel.deleteSelectedSubtree(service));
		button.setOnAction(this::onButtonClicked);
		
		applicationViewModel.selectedTaskProperty().addListener((i) -> tree.getSelectionModel().select((TreeViewModel)i));
		tree.getSelectionModel().selectedItemProperty().addListener((i) -> applicationViewModel.selectedTaskProperty().set((TreeViewModel)i));
		
//		applicationViewModel.selectedTaskProperty().bind(tree.getSelectionModel().selectedItemProperty());
		applicationViewModel.load(service);
		
		applicationViewModel.selectedTaskProperty().addListener((obs, o, n) -> taskView.modelProperty().set(n.getValue()));
	}

	private void onButtonClicked(ActionEvent evt) {
		
	}


}
