package net.d80harri.wr.ui.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class TaskViewModel {
	private final WrService service = new WrService();
	
	private final TaskDto task;
	
	public TaskViewModel(TaskDto dto) {
		this.task = dto;
		initialize();
	}
	
	public void initialize() {
		title.addListener((obs, o, n) -> task.setTitle(n)); 
	}
	
	private final StringProperty title = new SimpleStringProperty();

	public StringProperty titleProperty() {
		return this.title;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}
	
	private final ListProperty<TaskViewModel> children = new SimpleListProperty<TaskViewModel>();
	
	public ListProperty<TaskViewModel> childrenProperty() {
		return children;
	}

	public void saveOrUpdate() {
		if (task.getId() == null) {
			service.storeSubtree(task.getParent() == null ? null : task.getParent().getId(), task);
		} else {
			service.updateTask(task);
		}
	}

}
