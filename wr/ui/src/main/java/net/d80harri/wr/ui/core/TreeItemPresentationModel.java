package net.d80harri.wr.ui.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

public abstract class TreeItemPresentationModel<
			P extends TreeItemPresentationModel<?, ME, P>, 
			C extends TreeItemPresentationModel<ME, ?, C>, 
			ME extends TreeItemPresentationModel<P, C, ME>> {

	private ME myself = (ME)this;
	
	public TreeItemPresentationModel() {
		super();
	}

	private BooleanProperty selected = new SimpleBooleanProperty(this,
			"selected");

	public BooleanProperty selectedProperty() {
		return selected;
	}

	public boolean isSelected() {
		return selectedProperty().get();
	}

	public void setSelected(boolean selected) {
		selectedProperty().set(selected);
	}

	private BooleanProperty expanded = new SimpleBooleanProperty(this,
			"expanded");

	public final BooleanProperty expandedProperty() {
		return this.expanded;
	}

	public final boolean isExpanded() {
		return this.expandedProperty().get();
	}

	public final void setExpanded(final boolean expanded) {
		this.expandedProperty().set(expanded);
	}

	public P getParent() {
		return parentProperty().get();
	}

	public void setParent(P parent) {
		if (this.getParent() != parent) {
			if (this.getParent() != null) {
				this.getParent().getChildren().remove(this);
			}
			parentProperty().set(parent);
			if (this.getParent() != null) {
				getParent().addChild(myself);
			}
		}
	}

	public void addChild(C child) {
		if (!this.getChildren().contains(child)) {
			getChildren().add(child);
			child.setParent(myself);
		}
	}

	public void addChild(int i, C child) {
		if (!this.getChildren().contains(child)) {
			getChildren().add(i, child);
			child.setParent(myself);
		}
	}

	public abstract ObjectProperty<P> parentProperty();

	public abstract ObservableList<C> getChildren();
}