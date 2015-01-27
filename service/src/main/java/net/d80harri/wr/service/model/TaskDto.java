package net.d80harri.wr.service.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.d80harri.wr.db.model.Repeater;
import net.d80harri.wr.db.model.Task.State;

public class TaskDto {
	private Long id;
	private String title;
	private String content;
	private Date scheduled;
	private Repeater repeater;
	private State state;
	private Date deadline;
	private List<Date> reminders;
	private int estimate;
	private List<TaskDto> children = new ArrayList<TaskDto>();
	private TaskDto parent;
	
	public TaskDto() {}
	
	public TaskDto(String title) {
		this.title = title;
	}
	
	public TaskDto(String title, TaskDto parent) {
		this(title);
		this.setParent(parent);
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<TaskDto> getChildren() {
		return children;
	}
	
	public void addChild(TaskDto child) {
		if (!this.children.contains(child)) {
			children.add(child);
			child.setParent(this);
		}
	}

	public TaskDto getParent() {
		return parent;
	}
	
	public void setParent(TaskDto parent) {
		if (this.parent != parent) {
			this.parent = parent;
			parent.addChild(this);
		}
	}
	
	
}
