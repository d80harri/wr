package net.d80harri.wr.ui.takstree.binding;

import static org.fxmisc.easybind.EasyBind.listBind;
import static org.fxmisc.easybind.EasyBind.map;

import org.fxmisc.easybind.EasyBind;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeTableView;
import net.d80harri.wr.ui.task.TaskPresentationModel;

public class TaskTreeBinding extends
		ObjectBinding<TreeItem<TaskPresentationModel>> {

	private TaskPresentationModel model;
	private TreeTableView<TaskPresentationModel> tree;
	private TreeItem<TaskPresentationModel> computedItem;

	public TaskTreeBinding(TaskPresentationModel model,
			TreeTableView<TaskPresentationModel> tree) {
		this.model = model;
		this.tree = tree;
	}

	@Override
	protected TreeItem<TaskPresentationModel> computeValue() {
		computedItem = new TreeItem<TaskPresentationModel>(this.model);
		listBind(
				computedItem.getChildren(),
				map(model.getChildren(),
						i -> new TaskTreeBinding(i, tree).get()));

		model.selectedProperty().addListener(selectedPropertyChangedListener);

		computedItem.parentProperty().addListener(parentChangedListener);

		return computedItem;
	}

	private final ChangeListener<? super Boolean> selectedPropertyChangedListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			if (newValue) {
				tree.getSelectionModel().select(computedItem);
			}
		}

	};

	private final EventHandler<TreeModificationEvent<TaskPresentationModel>> childModificationEventHanlder = new EventHandler<TreeItem.TreeModificationEvent<TaskPresentationModel>>() {

		@Override
		public void handle(TreeModificationEvent<TaskPresentationModel> event) {
			if (event.wasRemoved()) {
				for (TreeItem<TaskPresentationModel> item : event
						.getRemovedChildren()) {
					onTreeItemDeleted(event.getTreeItem(), item);
				}
			}
			event.consume();
		}

	};

	private final ChangeListener<? super TreeItem<TaskPresentationModel>> parentChangedListener = new ChangeListener<TreeItem<TaskPresentationModel>>() {

		@Override
		public void changed(
				ObservableValue<? extends TreeItem<TaskPresentationModel>> observable,
				TreeItem<TaskPresentationModel> oldValue,
				TreeItem<TaskPresentationModel> newValue) {
			if (oldValue == null)
				return;
			oldValue.addEventHandler(TreeItem
					.<TaskPresentationModel> childrenModificationEvent(),
					childModificationEventHanlder);
			oldValue.parentProperty().removeListener(parentChangedListener);
		}
	};

	private void onTreeItemDeleted(TreeItem<TaskPresentationModel> parent, TreeItem<TaskPresentationModel> deleted) {
		deleted.parentProperty().removeListener(parentChangedListener);
		deleted.getValue().selectedProperty().removeListener(selectedPropertyChangedListener);
		parent.removeEventHandler(TreeItem
					.<TaskPresentationModel> childrenModificationEvent(), childModificationEventHanlder);
	}
	
}
