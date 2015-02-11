package net.d80harri.wr.ui.takstree;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class TaskTreePresentationModel {
	private final ObjectProperty<TaskPresentationModel> rootModel = new SimpleObjectProperty<TaskPresentationModel>(this, "rootModel", new TaskPresentationModel());
	private ObjectProperty<TaskPresentationModel> selectedModel = new SimpleObjectProperty<TaskPresentationModel>(this, "selectedModel", null);

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

	public final ObjectProperty<TaskPresentationModel> rootModelProperty() {
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
	
	
	public TaskPresentationModel createRootModel(List<TaskDto> rootDtos) {
		TaskPresentationModel result = new TaskPresentationModel();
		
		for (TaskDto dto : rootDtos) {
			result.addChild(createModel(dto));
		}
		return result;
	}

	private TaskPresentationModel createModel(TaskDto dto) {
		TaskPresentationModel result = new TaskPresentationModel(dto);
		for (TaskDto child : dto.getChildren()) {
			result.addChild(createModel(child));
		}
		return result;
	}
}
