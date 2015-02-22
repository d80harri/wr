package net.d80harri.wr.ui.task.commands;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.ui.core.ChangeManager;
import net.d80harri.wr.ui.core.PresentationModelCommand;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class DeleteTaskSubtreeCommand extends PresentationModelCommand<Void> {
	private WrService service;
	private TaskPresentationModel task;

	@Override
	protected Void call() throws Exception {
		if (task.getChangeManager().getState() != ChangeManager.State.Deleted) {
			service.deleteSubtree(task.getId());
			if (task.getParent() != null) {
				task.getParent().getChildren().remove(task);
			}
			task.getChangeManager().setState(ChangeManager.State.Deleted);
		}
		return null;
	}

}
