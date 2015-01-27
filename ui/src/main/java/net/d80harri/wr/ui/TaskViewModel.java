package net.d80harri.wr.ui;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskViewModel {
	private WrService service = new WrService();
	
	public TaskViewModel(Long id, String title, String content) {
		this.setId(id);
		this.setTitle(title);
		this.setContent(content);
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
	
	private final StringProperty content = new SimpleStringProperty();
	public StringProperty contentProperty() {
		return this.content;
	}
	public String getContent() {
		return content.get();
	}
	public void setContent(String content) {
		this.content.set(content);
	}
	
	private final LongProperty id = new SimpleLongProperty();
	public LongProperty idProperty() {
		return this.id;
	}
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) {
		this.id.set(id);
	}
	
	public void update() {
		service.updateTask(createTaskDto());
	}

	private TaskDto createTaskDto() {
		TaskDto result = new TaskDto(this.getTitle());
		result.setId(this.getId());
		result.setContent(this.getContent());
		return result;
	}
}
