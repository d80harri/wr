package net.d80harri.wr.ui.task;

import static org.fxmisc.easybind.EasyBind.listBind;
import static org.fxmisc.easybind.EasyBind.map;
import static org.fxmisc.easybind.EasyBind.subscribe;

import java.util.stream.Collectors;

import org.fxmisc.easybind.EasyBind;

import javafx.beans.binding.Binding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.model.TaskDto;

public class TaskPresentationModel {
	public static final TaskDto NULL_TASK = new TaskDto();

	private final TaskDto model;

	public TaskPresentationModel() {
		model = NULL_TASK;
	}

	public TaskDto getModel() {
		return model;
	}

	public TaskPresentationModel(TaskDto task) {
		this.model = task;
		setTitle(task.getTitle());
		getChildren().addAll(task.getChildren().stream().map(i -> new TaskPresentationModel(i)).collect(Collectors.toList()));
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
			subscribe(title, model::setTitle);
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
			listBind(model.getChildren(), map(children, i -> i.model));
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
								if (addChildren.getParent() != TaskPresentationModel.this)
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
				if (n == null) {
					model.setParent(null);
				} else {
					model.setParent(n.model.getParent());
					if (!n.getChildren().contains(this))
					n.getChildren().add(this);
				}

				if (o != null) {
					o.getChildren().remove(this);
				}
			});
		}
		return parent;
	}

	public TaskPresentationModel getParent() {
		return parentProperty().get();
	}

	public void setParent(TaskPresentationModel parent) {
		parentProperty().set(parent);
	}
	
	@Override
	public String toString() {
		return getTitle() + " " + getChildren().size();
	}
}
