package net.d80harri.wr.ui.takstree.binding;

import static org.fxmisc.easybind.EasyBind.listBind;
import static org.fxmisc.easybind.EasyBind.map;
import net.d80harri.wr.ui.task.TaskPresentationModel;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeItem;import javafx.scene.control.TreeTableView;


public class TaskTreeBinding extends ObjectBinding<TreeItem<TaskPresentationModel>>{
	
	private TaskPresentationModel model;
	private TreeTableView<TaskPresentationModel> tree;
	private TreeItem<TaskPresentationModel> computedItem;
	
	public TaskTreeBinding(TaskPresentationModel model, TreeTableView<TaskPresentationModel> tree) {
		this.model = model;
		this.tree = tree;
		
//		bind(this.model.selectedProperty());
	}
	
	@Override
	protected TreeItem<TaskPresentationModel> computeValue() {
		computedItem = new TreeItem<TaskPresentationModel>(this.model);
		listBind(computedItem.getChildren(), map(model.getChildren(), i -> {
			TreeItem<TaskPresentationModel> child = new TaskTreeBinding(i, tree).get();
			
			return child;
		}));
		
		model.selectedProperty().addListener(listener);
		
		computedItem.addEventHandler(TreeItem.<TaskPresentationModel>childrenModificationEvent(), c -> {
			if (c.wasRemoved()) {
//				for (TreeItem<TaskPresentationModel> item : c.getRemovedChildren()) {
//					item.getValue().selectedProperty().removeListener(listener);
//				}
				this.invalidate();
			}
			c.consume();
		});
		
		return computedItem;
	}

	private final ChangeListener<? super Boolean> listener = (obs, o, n) -> {
		if (n) {
			tree.getSelectionModel().select(computedItem);
		}
	};
}
