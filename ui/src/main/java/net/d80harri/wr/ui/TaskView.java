package net.d80harri.wr.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class TaskView extends VBox implements Initializable {

	@FXML
	private final StringProperty test = new SimpleStringProperty();

	public StringProperty testProperty() {
		return this.test;
	}

	public String getTest() {
		return test.get();
	}

	public void setTest(String test) {
		this.test.set(test);
	}
	
	private final ObjectProperty<TaskViewModel> task = new SimpleObjectProperty<TaskViewModel>();

	public ObjectProperty<TaskViewModel> taskProperty() {
		return this.task;
	}

	public TaskViewModel getTask() {
		return task.get();
	}

	public void setTask(TaskViewModel task) {
		this.task.set(task);
	} 

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
		test.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				System.out.println(newValue);
				
			}
		});
	}

}
