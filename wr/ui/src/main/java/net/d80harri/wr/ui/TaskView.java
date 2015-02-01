package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.viewmodel.TaskViewModel;

public class TaskView extends VBox implements Initializable {

	@FXML private TextField title;
	@FXML private TextArea content;

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
		
	}

	private ObjectProperty<TaskViewModel> model;
	
	public ObjectProperty<TaskViewModel> modelProperty() {
		if (model == null) {
			model = new SimpleObjectProperty<>(null);
			model.addListener((obs, o, n) -> {
				unbindModel(o);
				bindModel(n);
			});
		}
		return model;
	}
	
	public TaskViewModel getModel() {
		return modelProperty().get();
	}
	
	public void setModel(TaskViewModel model) {
		modelProperty().set(model);
	}
	
	private void bindModel(TaskViewModel model) {
		if (model != null) {
			title.textProperty().bindBidirectional(model.titleProperty());
			content.textProperty().bindBidirectional(model.contentProperty());
		}
	}
	
	private void unbindModel(TaskViewModel model) {
		if (model != null) {
			title.textProperty().unbindBidirectional(model.titleProperty());
			content.textProperty().unbindBidirectional(model.contentProperty());
		}
	}

	public void focusOnTitle() {
		title.requestFocus();
	} 
}
