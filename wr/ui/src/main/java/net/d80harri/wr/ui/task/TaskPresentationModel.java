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

	private ObservableList<TaskPresentationModel> children;

	public ObservableList<TaskPresentationModel> getChildren() {
		if (children == null) {
			children = FXCollections.observableArrayList();
//			listBind(model.getChildren(), map(children, i -> i.model));
			children.addListener(new ListChangeListener<TaskPresentationModel>() {

				@Override
				public void onChanged(
						javafx.collections.ListChangeListener.Change<? extends TaskPresentationModel> c) {
					while (c.next()) {
						if (c.wasPermutated()) {
							for (int i = c.getFrom(); i < c.getTo(); ++i) {
								// permutate
							}
						} else if (c.wasUpdated()) {
							// update item
						} else {
							for (TaskPresentationModel delChildren : c.getRemoved()) {
								delChildren.setParent(null);
							}
							for (TaskPresentationModel addChildren : c.getAddedSubList()) {
								addChildren.setParent(TaskPresentationModel.this);
							}
						}
					}
				}
			});
		}
		return children;
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
	
	@Override
	public String toString() {
		return getTitle() + " " + getChildren().size();
	}
}
