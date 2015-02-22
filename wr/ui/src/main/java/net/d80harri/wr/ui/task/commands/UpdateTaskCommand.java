package net.d80harri.wr.ui.task.commands;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.core.PresentationModelCommand;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class UpdateTaskCommand extends PresentationModelCommand<Void> {
	private final WrService service;
	private final TaskPresentationModel model;

	public UpdateTaskCommand(WrService service, TaskPresentationModel model) {
		this.service = service;
		this.model = model;
	}

	@Override
	protected Void call() throws Exception {
		TaskDto dto = new TaskDto();
		dto.setTitle(model.getTitle());
		dto.setId(model.getId());
		service.updateTask(dto);
		return null;
	}

}
