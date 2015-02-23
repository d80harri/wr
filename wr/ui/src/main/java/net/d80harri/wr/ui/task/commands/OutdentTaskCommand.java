package net.d80harri.wr.ui.task.commands;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.ui.core.PresentationModelCommand;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class OutdentTaskCommand extends PresentationModelCommand<Void> {
	private final WrService service;
	private final TaskPresentationModel model;

	public OutdentTaskCommand(WrService service, TaskPresentationModel model) {
		this.service = service;
		this.model = model;
	}

	@Override
	protected Void call() throws Exception {
		TaskPresentationModel grandParent = model.getParent().getParent();
		if (grandParent != null) {
			int idxOfParent = grandParent.getChildren().indexOf(
					model.getParent());
			
			service.moveSubtree(model.getId(), grandParent.getId());
			
			grandParent.addChild(idxOfParent + 1, model);
			model.setSelected(true);
		}

		return null;
	}

}
