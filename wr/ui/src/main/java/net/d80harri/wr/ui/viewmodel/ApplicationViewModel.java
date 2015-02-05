package net.d80harri.wr.ui.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.d80harri.wr.service.WrService;

import org.fxmisc.easybind.EasyBind;

public class ApplicationViewModel {

	private TaskTreeViewModel taskTreeViewModel;
	
	public TaskTreeViewModel getTaskTreeViewModel() {
		if (taskTreeViewModel == null) {
			taskTreeViewModel = new TaskTreeViewModel();
		}
		return taskTreeViewModel;
	}
	
	private ObjectProperty<TaskViewModel> selectedInTree;
	
	public ObjectProperty<TaskViewModel> selectedInTreeProperty() {
		if (selectedInTree == null) {
			selectedInTree = new SimpleObjectProperty<TaskViewModel>();
			selectedInTree.bind(EasyBind.select(getTaskTreeViewModel().selectedTaskTreeItemProperty()).selectObject(s -> s.valueProperty()));
		}
		return selectedInTree;
	}
	
	public void load(WrService service) {
		getTaskTreeViewModel().load(service);
	}

}
