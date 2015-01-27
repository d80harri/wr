package net.d80harri.wr.db.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class Task {
	public enum State {
		TODO,
		DONE
	}
	
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column
	private String title;
	
	@Column
	@Lob
	private String content;
	
	@Column
	private Date scheduled;
	
	@Transient
	private Repeater repeater;
	
	@Column
	private State state;
	
	@Column
	private Date deadline;
	
	@Transient
	private List<Date> reminders;
	
	@Column(columnDefinition = "int default 0")
	private int estimate;
	
	@Column
	private long left = 1;
	
	@Column
	private long right = 2;

	public Task() {}
	
	public Task(String title) {
		this.title = title;
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
	
	public long getRight() {
		return right;
	}
	
	public void setRight(long right) {
		this.right = right;
	}
	
	public long getLeft() {
		return left;
	}
	
	public void setLeft(long left) {
		this.left = left;
	}
}
