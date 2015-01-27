package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.viewmodel.TaskViewModel;

public class TaskView extends VBox implements Initializable {

	private TaskViewModel model = new TaskViewModel(new TaskDto());
	
	@FXML private TextField title;

	public TaskView() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"/fxml/Task.fxml"));
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
		bindModel();
	}

	private void bindModel() {
		title.textProperty().bindBidirectional(model.titleProperty());
	}
	
	private void unbindModel() {
		title.textProperty().unbindBidirectional(model.titleProperty());
	}
	
	public TaskViewModel getModel() {
		return model;
	}
	
	public void setModel(TaskViewModel task) {
		unbindModel();
		this.model = task;
		bindModel();
	}

	public void focusOnTitle() {
		title.requestFocus();
	} 
}
