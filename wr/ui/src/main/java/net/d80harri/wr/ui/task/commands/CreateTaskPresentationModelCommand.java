package net.d80harri.wr.ui.task.commands;

import java.util.function.Consumer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.core.PresentationModelCommand;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class CreateTaskPresentationModelCommand extends PresentationModelCommand<TaskPresentationModel> {
	private WrService service;
	private Consumer<TaskPresentationModel> onSelected;
	
	public CreateTaskPresentationModelCommand(WrService service, Consumer<TaskPresentationModel> onSelected) {
		this.service = service;
		this.onSelected = onSelected;
	}
	
	@Override
	protected TaskPresentationModel call() throws Exception {
		return createModel(new TaskDto("New"));
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
					if (newValue) {						
						onSelected.accept(toSelect);
					}
					updating = false;
				}
			}
		});
		for (TaskDto child : dto.getChildren()) {
			result.addChild(createModel(child));
		}
		return result;
	}
}
