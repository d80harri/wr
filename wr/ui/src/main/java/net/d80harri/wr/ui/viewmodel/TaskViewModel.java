package net.d80harri.wr.ui.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class TaskViewModel  {
	private final WrService service = new WrService();
	
	private TaskDto task;
	
	
	public TaskViewModel(TaskDto dto, TaskViewModel parent, boolean loaded) {
		this.task = dto;
		this.parent = parent;
		if (this.parent != null)
		this.parent.getChildrenViews().add(this);
		loadedProperty().set(loaded);
	}
	
	private boolean deleted = false;
	
	private LongProperty id;
	
	public LongProperty idProperty() {
		if (id == null) {
			id = new SimpleLongProperty(task.getId());
			id.addListener((obs, o, n) -> task.setId((Long)n));
		}
		return id;
	}
	
	public Long getId() {
		return idProperty().get();
	}
	
	public void setId(Long id) {
		idProperty().set(id);
	}
	
	private final TaskViewModel parent;
	
	public TaskViewModel getParent() {
		return parent;
	}
	
	private StringProperty title;

	public StringProperty titleProperty() {
		if (title == null) {
			title = new SimpleStringProperty(task.getTitle());
			title.addListener((obs, o, n) -> task.setTitle(n)); 
		}
		return this.title;
	}

	public String getTitle() {
		return titleProperty().get();
	}

	public void setTitle(String title) {
		this.titleProperty().set(title);
	}
	
	private StringProperty content;
	
	public StringProperty contentProperty() {
		if (content == null) {
			content = new SimpleStringProperty(task.getContent());
			content.addListener((obs, o, n) -> task.setContent(n));
		}
		return content;
	}
	
	public String getContent() {
		return contentProperty().get();
	}
	
	public void setContent(String content) {
		contentProperty().set(content);
	}
	
	private ObservableList<TaskDto> childDtos;
	
	public ObservableList<TaskDto> getChildDtos() {
		if (childDtos == null) {
			this.childDtos = FXCollections.observableList(this.task.getChildren());
		}
		return childDtos;
	}
	
	private ObservableList<TaskViewModel> childrenViews;
	
	public ObservableList<TaskViewModel> getChildrenViews() {
		if (this.childrenViews == null) {
			this.childrenViews = new MappedList<TaskViewModel, TaskDto>(this.getChildDtos(), 
					i -> { i.setParent(this.task);return new TaskViewModel(i, this, true);},
					i -> i.task);
		}
		return childrenViews;
	}
	
	private BooleanProperty loaded;
	
	public BooleanProperty loadedProperty() {
		if (loaded == null) {
			loaded = new SimpleBooleanProperty(false);
		}
		return loaded;
	}
	
	public boolean isLoaded() {
		return loadedProperty().get();
	}
	
	public void setLoaded(boolean loaded) {
		this.loadedProperty().set(loaded);
	}
	
	public void saveOrUpdate() {
		if (!deleted) {
			if (task.getId() == null) {
				service.storeSubtree(task.getParent() == null ? null : task.getParent().getId(), task);
			} else {
				service.updateTask(task);
			}
		}
	}

	public TaskViewModel addChild(TaskDto task) {
		TaskViewModel result = new TaskViewModel(task, this, true);
		this.getChildrenViews().add(result);
		return result;
	}
	
	public TaskViewModel addNewChild() {
		TaskDto parent = getParent() == null ? null : getParent().task;
		return addChild(new TaskDto("Unnamed Child", parent));
	}
	
	public void load(WrService service) {
		if (!this.isLoaded()) {
			if (task.getId() == null) {
				service.getAllTrees().forEach(i -> addChild(i));
			} else {
				throw new RuntimeException("NYI");
			}
		}
	}

	public void reload(WrService service) {
		if (!isLoaded()) {
			load(service);
		} else {
			if (task.getId() == null) {
				service.getAllTrees().forEach(i -> addChild(i));
			} else {
				throw new RuntimeException("NYI");
			}
		}
	}

	public void delete(WrService service) {
		service.deleteSubtree(getId());
		this.deleted = true;
		if (getParent() != null) {
			getParent().getChildDtos().remove(this.task);
		}
	}
}
