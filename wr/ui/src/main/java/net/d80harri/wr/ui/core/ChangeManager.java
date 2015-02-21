package net.d80harri.wr.ui.core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class ChangeManager<T> {
	private final T client;

	public ChangeManager(T client) {
		this.client = client;
	}

	private Property<State> state = null;

	public final Property<State> stateProperty() {
		if (state == null) {
			state = new SimpleObjectProperty<ChangeManager.State>(this, "state", State.Detached);
		}
		return this.state;
	}

	public final net.d80harri.wr.ui.core.ChangeManager.State getState() {
		return this.stateProperty().getValue();
	}

	public final void setState(
			final net.d80harri.wr.ui.core.ChangeManager.State state) {
		this.stateProperty().setValue(state);
	}

	public static enum State {
		Detached, UpToDate, Deleted, Changed
	}

}
