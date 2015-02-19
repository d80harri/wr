package net.d80harri.wr.ui.takstree;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class TaskTreePresentationModel {
	private final ChangeListener<TaskPresentationModel> selectedModelPropertyChangedListener = new ChangeListener<TaskPresentationModel>() {

		@Override
		public void changed(
				ObservableValue<? extends TaskPresentationModel> observable,
				TaskPresentationModel oldValue,
				TaskPresentationModel newValue) {
			if (oldValue != null && oldValue != getRootModel()) {
				oldValue.update(service);
			}
			if (oldValue != null) {
				oldValue.setSelected(false);
			}
			if (newValue != null) {
				newValue.setSelected(true);
				if (newValue.getParent() != null) {
					newValue.getParent().setExpanded(true);
				}
			}
			
		}
	};
	
	private ObjectProperty<TaskPresentationModel> selectedModel = new SimpleObjectProperty<TaskPresentationModel>(this, "selectedModel", null);

	private WrService service;
	
	public TaskTreePresentationModel(WrService service) {
		this.service = service;
		bind();
	}

	private void bind() {
		selectedModelProperty().addListener(selectedModelPropertyChangedListener);
		
	}

	public final ObjectProperty<TaskPresentationModel> selectedModelProperty() {
		return this.selectedModel;
	}

	public final net.d80harri.wr.ui.task.TaskPresentationModel getSelectedModel() {
		return this.selectedModelProperty().get();
	}

	public final void setSelectedModel(
			final net.d80harri.wr.ui.task.TaskPresentationModel selectedModel) {
		this.selectedModelProperty().set(selectedModel);
	}

	
	private ObjectProperty<TaskPresentationModel> rootModel = null;
	
	public final ObjectProperty<TaskPresentationModel> rootModelProperty() {
		if (rootModel == null) {
			rootModel = new SimpleObjectProperty<TaskPresentationModel>(this, "rootModel", new TaskPresentationModel(this.service));
		}
		return this.rootModel;
	}

	public final net.d80harri.wr.ui.task.TaskPresentationModel getRootModel() {
		return this.rootModelProperty().get();
	}

	public final void setRootModel(
			final net.d80harri.wr.ui.task.TaskPresentationModel rootModel) {
		this.rootModelProperty().set(rootModel);
	}

	public void load(WrService service) {
		this.setRootModel(createRootModel(service.getAllTrees()));
	}
	
	public void select(TaskPresentationModel selected) {
		if (this.getSelectedModel() != null) {
			this.getSelectedModel().setSelected(false);			
		}
		if (selected != null) {
			selected.setSelected(true);
			if (selected.getParent() != null)
				selected.getParent().setExpanded(true);
		}
		this.setSelectedModel(selected);
	}
	
	
	public TaskPresentationModel createRootModel(List<TaskDto> rootDtos) {
		TaskPresentationModel result = new TaskPresentationModel(this.service);
		
		for (TaskDto dto : rootDtos) {
			result.addChild(createModel(dto));
		}
		return result;
	}

	private TaskPresentationModel createModel(TaskDto dto) {
		TaskPresentationModel result = new TaskPresentationModel(this.service, dto);
		result.selectedProperty().addListener(new ChangeListener<Boolean>() {
			private TaskPresentationModel toSelect = result;
			private boolean updating = false;
			
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (!updating) {
					updating = true;
					setSelectedModel(toSelect);
					updating = false;
				}
			}
		});
		for (TaskDto child : dto.getChildren()) {
			result.addChild(createModel(child));
		}
		return result;
	}

	public void addRootTask() {
		getRootModel().addChild(createModel(new TaskDto("new")));
	}

	public void addSiblingToSelected() {
		getSelectedModel().addSibling(createModel(new TaskDto("new")));
	}

	public void outdentSelectedTask() {
		getSelectedModel().outdentTask();
	}

	public void indentSelectedTask() {
		getSelectedModel().indentTask();
	}

	public void deleteSelectedSubtree() {
		getSelectedModel().deleteSubtree();
	}
}
