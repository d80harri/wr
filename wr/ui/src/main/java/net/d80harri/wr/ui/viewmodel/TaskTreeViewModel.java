package net.d80harri.wr.ui.viewmodel;

import net.d80harri.wr.service.WrService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TaskTreeViewModel extends TreeViewModel<TaskViewModel> {

	public TaskTreeViewModel(TreeViewModel<TaskViewModel> parent,
			TaskViewModel model) {
		super(parent, model);
	}

	private ObservableList<TreeViewModel<TaskViewModel>> children;
	@Override
	public ObservableList<TreeViewModel<TaskViewModel>> getChildrenData() {
		if (children == null) {
			if (model == null) {
				children = new MappedList<TreeViewModel<TaskViewModel>, TaskViewModel>(FXCollections.observableArrayList(), i -> new TaskTreeViewModel(this, i), i -> i.model);
			} else {
				children = new MappedList<TreeViewModel<TaskViewModel>, TaskViewModel>(model.getChildren(), i -> new TaskTreeViewModel(this, i), i -> i.model);
			}
			children.addListener(new ListChangeListener<TreeViewModel<TaskViewModel>>() {

				@Override
				public void onChanged(
						javafx.collections.ListChangeListener.Change<? extends TreeViewModel<TaskViewModel>> c) {
					System.out.println("CHANGE: " + children.size());
				}
				
			});
		}
		return children;
	}

	public void saveOrUpdate() {
		this.model.saveOrUpdate();
	}

	public void addNewChild() {
		this.model.addNewChild();
	}

	public void delete(WrService service) {
		this.model.delete(service);
	}	
}
