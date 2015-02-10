package net.d80harri.wr.ui.task;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.model.TaskDto;

public class TaskPresentationModel {

	public TaskPresentationModel() {
		
	}

	public TaskPresentationModel(TaskDto task) {
		setTitle(task.getTitle());
		id = new SimpleObjectProperty<Long>(task.getId());
	}

	private ReadOnlyObjectProperty<Long> id = null;

	public ReadOnlyObjectProperty<Long> idProperty() {
		return id;
	}

	public Long getId() {
		return idProperty().get();
	}

	private StringProperty title = null;

	public StringProperty titleProperty() {
		if (title == null) {
			title = new SimpleStringProperty();
		}
		return title;
	}

	public String getTitle() {
		return titleProperty().get();
	}
	
	public void setTitle(String title) {
		titleProperty().set(title);
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
}
