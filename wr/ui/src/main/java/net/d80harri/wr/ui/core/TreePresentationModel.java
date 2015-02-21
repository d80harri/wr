package net.d80harri.wr.ui.core;

import net.d80harri.wr.ui.task.TaskPresentationModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TreePresentationModel<R extends TreeItemPresentationModel<R, R, R>> {

	private ObjectProperty<R> rootModel = null;

	public final ObjectProperty<R> rootModelProperty() {
		if (rootModel == null) {
			rootModel = new SimpleObjectProperty<R>(this, "rootModel", null);
		}
		return this.rootModel;
	}

	public final R getRootModel() {
		return this.rootModelProperty().get();
	}

	public final void setRootModel(final R rootModel) {
		this.rootModelProperty().set(rootModel);
	}

	private ObjectProperty<R> selectedModel = new SimpleObjectProperty<R>(this,
			"selectedModel", null);

	public final ObjectProperty<R> selectedModelProperty() {
		return this.selectedModel;
	}

	public final R getSelectedModel() {
		return this.selectedModelProperty().get();
	}

	public final void setSelectedModel(final R selectedModel) {
		this.selectedModelProperty().set(selectedModel);
	}
	
	public void select(R selected) {
		if (this.getSelectedModel() != null) {
			this.getSelectedModel().setSelected(false);			
		}
		if (selected != null) {
			selected.setSelected(true);
			if (selected.getParent() != null)
				selected.getParent().setExpanded(true);
		}
		this.setSelectedModel(selected);
	}
}
