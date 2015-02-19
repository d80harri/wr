package net.d80harri.wr.ui.task;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;

public class TaskPresentationModel {
	private WrService service;
	
	public TaskPresentationModel(WrService service) {
		this.service = service;
	}

	public TaskPresentationModel(WrService service, TaskDto task) {
		this.service = service;
		setTitle(task.getTitle());
		id = new SimpleObjectProperty<Long>(task.getId());
	}

	private ReadOnlyObjectProperty<Long> id = new SimpleObjectProperty<Long>(this, "id");

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

	private ObservableList<TaskPresentationModel> children = FXCollections.observableArrayList();

	

	private ObjectProperty<TaskPresentationModel> parent = null;

	public ObjectProperty<TaskPresentationModel> parentProperty() {
		if (parent == null) {
			parent = new SimpleObjectProperty<TaskPresentationModel>();
			
			parent.addListener((obs, o, n) -> {
				if (n != null) {
					n.addChild(this);
				}
			});
		}
		return parent;
	}

	public TaskPresentationModel getParent() {
		return parentProperty().get();
	}

	public void setParent(TaskPresentationModel parent) {
		if (this.getParent() != parent) {
			if (this.getParent() != null) {
				this.getParent().getChildren().remove(this);
			}
			parentProperty().set(parent);
			if (this.getParent() != null) {
				parentProperty().get().addChild(this);
			}
		}
	}
	
	public void addChild(TaskPresentationModel child) {
		if (!this.getChildren().contains(child)) {
			getChildren().add(child);
			child.setParent(this);
		}
	}

	public final ObservableList<net.d80harri.wr.ui.task.TaskPresentationModel> getChildren() {
		return this.children;
	}
	
	public void addChild(int i, TaskPresentationModel child) {
		if (!this.getChildren().contains(child)) {
			getChildren().add(i, child);
			child.setParent(this);
		}
	}
	
	private BooleanProperty selected = new SimpleBooleanProperty(this, "selected");
	
	public BooleanProperty selectedProperty() {
		return selected;
	}
	
	public boolean isSelected() {
		return selectedProperty().get();
	}
	
	public void setSelected(boolean selected) {
		selectedProperty().set(selected);
	}
	
	private BooleanProperty expanded = new SimpleBooleanProperty(this, "expanded");
	
	public final BooleanProperty expandedProperty() {
		return this.expanded;
	}
	
	public final boolean isExpanded() {
		return this.expandedProperty().get();
	}
	
	public final void setExpanded(final boolean expanded) {
		this.expandedProperty().set(expanded);
	}
	
	private BooleanProperty deleted = null;
	
	public final BooleanProperty deletedProperty() {
		if (deleted == null) {
			deleted = new SimpleBooleanProperty(this, "deleted");
		}
		return this.deleted;
	}
	
	public final boolean isDeleted() {
		return this.deletedProperty().get();
	}
	
	public final void setDeleted(final boolean deleted) {
		this.deletedProperty().set(deleted);
	}
	
	
	public void update(WrService service) {
		TaskDto dto = new TaskDto();
		dto.setTitle(this.getTitle());
		dto.setId(this.getId());
		service.updateTask(dto);
	}
	
	public void deleteSubtree() {
		if (!isDeleted()) {
			service.deleteSubtree(getId());
			if (getParent() != null) {
				getParent().getChildren().remove(this);
			}
			setDeleted(true);
		}
	}

	public void indentTask() {
		int idx = getParent().getChildren().indexOf(this);
		if (idx != 0) {
			TaskPresentationModel precessor = getParent().getChildren()
					.get(idx - 1);
			precessor.addChild(this);
			setSelected(true);
		}
	}
	
	public void outdentTask() {
		TaskPresentationModel grandParent = getParent().getParent();
		if (grandParent != null) {
			int idxOfParent = grandParent.getChildren().indexOf(
					getParent());
			grandParent.addChild(idxOfParent + 1, this);
			setSelected(true);
		}
	}
	
	public void addSibling(TaskPresentationModel taskPresentationModel) {
		int idxOfSelected = this.getParent().getChildren().indexOf(this);

		this.getParent()
				.addChild(idxOfSelected + 1, taskPresentationModel);
	}
	
	@Override
	public String toString() {
		return getTitle() + " " + getChildren().size();
	}

}
