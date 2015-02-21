package net.d80harri.wr.ui.task;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.core.ChangeManager;
import net.d80harri.wr.ui.core.TreeItemPresentationModel;

public class TaskPresentationModel extends TreeItemPresentationModel<TaskPresentationModel, TaskPresentationModel, TaskPresentationModel> {
	private ChangeManager<TaskPresentationModel> changeManager = new ChangeManager<TaskPresentationModel>(this);
	
	private WrService service;
	
	public TaskPresentationModel(WrService service) {
		this.service = service;
	}

	public TaskPresentationModel(WrService service, TaskDto task) {
		this.service = service;
		getChangeManager().setState(ChangeManager.State.UpToDate);
		setTitle(task.getTitle());
		id = new SimpleObjectProperty<Long>(task.getId());
	}

	public ChangeManager<TaskPresentationModel> getChangeManager() {
		return changeManager;
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

	private ObservableList<TaskPresentationModel> children = FXCollections.observableArrayList();
	
	public final ObservableList<net.d80harri.wr.ui.task.TaskPresentationModel> getChildren() {
		return this.children;
	}
	
	public void update(WrService service) {
		TaskDto dto = new TaskDto();
		dto.setTitle(this.getTitle());
		dto.setId(this.getId());
		service.updateTask(dto);
	}
	
	public void deleteSubtree() {
		if (getChangeManager().getState() != ChangeManager.State.Deleted) {
			service.deleteSubtree(getId());
			if (getParent() != null) {
				getParent().getChildren().remove(this);
			}
			getChangeManager().setState(ChangeManager.State.Deleted);
		}
	}

	public void indentTask() {
		int idx = getParent().getChildren().indexOf(this);
		if (idx != 0) {
			TaskPresentationModel precessor = getParent().getChildren()
					.get(idx - 1);
			precessor.addChild(this);
			setExpanded(true);
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
