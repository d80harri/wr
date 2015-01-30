package net.d80harri.wr.ui.viewmodel;

import net.d80harri.wr.service.model.TaskDto;

public class ApplicationViewModel {
	public static TaskViewModel ROOT_MODEL = new TaskViewModel(new TaskDto("root"), false);
	private TaskViewModel taskTree = ROOT_MODEL;
	
	public TaskViewModel rootItemProperty() {
		return taskTree;
	}
	
}
