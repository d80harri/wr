package net.d80harri.wr.ui.takstree;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.core.TreePresentationModel;
import net.d80harri.wr.ui.task.TaskPresentationModel;
import net.d80harri.wr.ui.task.commands.CreateTaskPresentationModelCommand;

public class TaskTreePresentationModel extends TreePresentationModel<TaskPresentationModel> {
	private final ChangeListener<TaskPresentationModel> selectedModelPropertyChangedListener = new ChangeListener<TaskPresentationModel>() {
		private boolean updating = false;
		
		@Override
		public void changed(
				ObservableValue<? extends TaskPresentationModel> observable,
				TaskPresentationModel oldValue,
				TaskPresentationModel newValue) {
			if (updating)
				return;
			updating = true;
			if (oldValue != null && oldValue != getRootModel()) {
				oldValue.update();
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
			updating = false;
		}
	};
	
	private WrService service;
	
	public TaskTreePresentationModel(WrService service) {
		this.service = service;
		bind();
	}

	private void bind() {
		selectedModelProperty().addListener(selectedModelPropertyChangedListener);
		
	}

	public void load(WrService service) {
		this.setRootModel(createRootModel(service.getAllTrees()));
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
					if (newValue)
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

	private CreateTaskPresentationModelCommand createTaskCommand;
	
	public CreateTaskPresentationModelCommand getCreateTaskCommand() {
		if (createTaskCommand == null) {
			createTaskCommand = new CreateTaskPresentationModelCommand(this.service, s -> setSelectedModel(s));
		}
		return createTaskCommand;
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
