package net.d80harri.wr.ui.viewmodel;

import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class TaskViewModel {
	private final WrService service = new WrService();
	
	private final TaskDto task;
	
	public TaskViewModel(TaskDto dto) {
		this.task = dto;
	}
	
	private StringProperty title = null;;

	public StringProperty titleProperty() {
		if (title == null) {
			try {
				title = new JavaBeanStringPropertyBuilder().bean(task).name("title").build();
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return this.title;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public void saveOrUpdate() {
		if (task.getId() == null) {
			service.storeSubtree(task.getParent() == null ? null : task.getParent().getId(), task);
		} else {
			service.updateTask(task);
		}
	}
}
