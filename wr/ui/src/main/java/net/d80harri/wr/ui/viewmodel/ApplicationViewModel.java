package net.d80harri.wr.ui.viewmodel;

import net.d80harri.wr.service.WrService;

public class ApplicationViewModel {

	private TaskTreeViewModel taskTreeViewModel;
	
	public TaskTreeViewModel getTaskTreeViewModel() {
		if (taskTreeViewModel == null) {
			taskTreeViewModel = new TaskTreeViewModel();
		}
		return taskTreeViewModel;
	}

	public void load(WrService service) {
		getTaskTreeViewModel().load(service);
	}
}
