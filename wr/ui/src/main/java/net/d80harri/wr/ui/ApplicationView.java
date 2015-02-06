package net.d80harri.wr.ui;

import static javafx.beans.binding.Bindings.equal;
import static javafx.beans.binding.Bindings.isNull;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
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
		
		taskView.modelProperty().bind(applicationViewModel.selectedInTreeProperty());
		taskView.visibleProperty().bind(
				isNull(applicationViewModel.getTaskTreeViewModel().selectedTaskTreeItemProperty()).not()
				.or(
						equal(
								applicationViewModel.getTaskTreeViewModel().selectedTaskTreeItemProperty(), 
								getApplicationViewModel().getTaskTreeViewModel().rootTaskTreeViewModelProperty()).not()));
		
		titleColumn.setCellValueFactory(p -> { 
			if (p.getValue().getValue() == null) {
				return null;
			} else {
				return p.getValue().getValue().titleProperty();
			}
		});

		titleColumn.setCellFactory(new Callback<TreeTableColumn<TaskViewModel, String>, TreeTableCell<TaskViewModel, String>>() {

			@Override
			public TreeTableCell<TaskViewModel, String> call(
					TreeTableColumn<TaskViewModel, String> param) {
				TextFieldTreeTableCell<TaskViewModel, String> result = new TextFieldTreeTableCell<TaskViewModel, String>(new DefaultStringConverter());

				return result;
			}
			
		});
		
		titleColumn.setOnEditCommit(this::onTreeTableColumnEditCommit);
		
		menuAppendChild.setOnAction(this::onAppendChild);
		menuReload.setOnAction((e) -> getApplicationViewModel().getTaskTreeViewModel().reload(service) );
		menuDeleteSubtree.setOnAction((e) -> applicationViewModel.getTaskTreeViewModel().deleteSelectedSubtree(service));
		button.setOnAction(this::onButtonClicked);	
	}
	
	private void onTreeTableColumnEditCommit(CellEditEvent<TaskViewModel, String> event) {
		applicationViewModel.getTaskTreeViewModel().getSelectedTaskTreeItem().getValue().setTitle(event.getNewValue());
		event.getRowValue().getValue().saveOrUpdate();
		TreeItem<TaskViewModel> created = applicationViewModel.getTaskTreeViewModel().addTaskToSelectedAsSibling();
//		tree.getSelectionModel().select(created);
		tree.edit(tree.getSelectionModel().getSelectedIndex(), titleColumn);
	}
	
	private void onAppendChild(ActionEvent evt) {
		getApplicationViewModel().getTaskTreeViewModel().getSelectedTaskTreeItem().setExpanded(true);
		TreeItem<TaskViewModel> newItem = applicationViewModel.getTaskTreeViewModel().addTaskToSelected();
		tree.edit(tree.getSelectionModel().getSelectedIndex(), titleColumn);
	}

	private void onButtonClicked(ActionEvent evt) {
		System.out.println(applicationViewModel);
	}

}
