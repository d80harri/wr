package net.d80harri.wr.ui.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.HierarchyData;

public class TaskViewModel implements HierarchyData<TaskViewModel> {
	private final WrService service = new WrService();
	
	private final TaskDto task;
	
	
	public TaskViewModel(TaskDto dto, boolean loaded) {
		this.task = dto;
		loadedProperty().set(loaded);
	}
	
	private LongProperty id;
	
	public LongProperty idProperty() {
		if (id == null) {
			id = new SimpleLongProperty(task.getId());
			id.addListener((obs, o, n) -> task.setId((Long)n));
		}
		return id;
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
			this.childrenViews = new MappedList<TaskViewModel, TaskDto>(this.getChildDtos(), i -> new TaskViewModel(i, true));
			this.childrenViews.addListener((ListChangeListener.Change<? extends TaskViewModel> c) -> {System.out.println(c);});
		}
		return childrenViews;
	}
	
	@Override
	public ObservableList<TaskViewModel> getChildren() {
		return getChildrenViews();
	}
	
	private BooleanProperty loaded;
	
	public BooleanProperty loadedProperty() {
		if (loaded == null) {
			loaded = new SimpleBooleanProperty(false);
		}
		return loaded;
	}
	
	public void saveOrUpdate() {
		if (task.getId() == null) {
			service.storeSubtree(task.getParent() == null ? null : task.getParent().getId(), task);
		} else {
			service.updateTask(task);
		}
	}

	public void addChild(TaskDto task) {
		this.getChildDtos().add(task);
	}
	
	public void addNewChild() {
		addChild(new TaskDto("Unnamed Child"));
	}
	
	public void load(WrService service) {
		if (task.getId() == null) {
			service.getAllTrees().forEach(i -> addChild(i));
		} else {
			throw new RuntimeException("NYI");
		}
	}
}
