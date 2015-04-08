package net.d80harri.wr.ui.task.commands;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.ui.core.ChangeManager;
import net.d80harri.wr.ui.core.PresentationModelCommand;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class IndentTaskCommand extends PresentationModelCommand<Void> {
	private final WrService service;
	private final TaskPresentationModel model;

	public IndentTaskCommand(WrService service,
			TaskPresentationModel taskPresentationModel) {
		this.service = service;
		this.model = taskPresentationModel;
	}

	@Override
	protected Void call() throws Exception {
		if (model.getChangeManager().getState() == ChangeManager.State.UpToDate){
			model.update();
		}
		
		int idx = model.getParent().getChildren().indexOf(model);
		if (idx != 0) {			
			TaskPresentationModel precessor = model.getParent().getChildren()
					.get(idx - 1);
			
			service.moveSubtree(model.getId(), precessor.getId());
			
			precessor.addChild(model);
			model.setExpanded(true);
			model.setSelected(true);
		}

		return null;
	}

}
