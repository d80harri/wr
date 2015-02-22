package net.d80harri.wr.ui.task;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.core.ChangeManager;
import net.d80harri.wr.ui.core.PresentationModelCommand;
import net.d80harri.wr.ui.core.TreeItemPresentationModel;
import net.d80harri.wr.ui.task.commands.DeleteTaskSubtreeCommand;
import net.d80harri.wr.ui.task.commands.IndentTaskCommand;
import net.d80harri.wr.ui.task.commands.OutdentTaskCommand;
import net.d80harri.wr.ui.task.commands.UpdateTaskCommand;

public class TaskPresentationModel
		extends
		TreeItemPresentationModel<TaskPresentationModel, TaskPresentationModel, TaskPresentationModel> {
	private ChangeManager<TaskPresentationModel> changeManager = new ChangeManager<TaskPresentationModel>(
			this);

	private WrService service;

	public TaskPresentationModel(WrService service) {
		this.service = service;
	}

	public TaskPresentationModel(WrService service, TaskDto task) {
		this.service = service;
		getChangeManager().setState(ChangeManager.State.UpToDate);
		setTitle(task.getTitle());
		id = new SimpleObjectProperty<Long>(task.getId());
	}

	public ChangeManager<TaskPresentationModel> getChangeManager() {
		return changeManager;
	}

	private ReadOnlyObjectProperty<Long> id = new SimpleObjectProperty<Long>(
			this, "id");

	public ReadOnlyObjectProperty<Long> idProperty() {
		return id;
	}

	public Long getId() {
		return idProperty().get();
	}

	private StringProperty title = null;

	public StringProperty titleProperty() {
		if (title == null) {
			title = new SimpleStringProperty();
		}
		return title;
	}

	public String getTitle() {
		return titleProperty().get();
	}

	public void setTitle(String title) {
		titleProperty().set(title);
	}

	private ObjectProperty<TaskPresentationModel> parent = null;

	public ObjectProperty<TaskPresentationModel> parentProperty() {
		if (parent == null) {
			parent = new SimpleObjectProperty<TaskPresentationModel>();

			parent.addListener((obs, o, n) -> {
				if (n != null) {
					n.addChild(this);
				}
			});
		}
		return parent;
	}

	private ObservableList<TaskPresentationModel> children = FXCollections
			.observableArrayList();

	public final ObservableList<net.d80harri.wr.ui.task.TaskPresentationModel> getChildren() {
		return this.children;
	}

	private ObjectProperty<PresentationModelCommand<Void>> updateCommand;

	public final ObjectProperty<PresentationModelCommand<Void>> updateCommandProperty() {
		if (updateCommand == null) {
			updateCommand = new SimpleObjectProperty<PresentationModelCommand<Void>>(this, "updatedCommand", new UpdateTaskCommand(service, this));
		}
		return this.updateCommand;
	}

	public final PresentationModelCommand<Void> getUpdateCommand() {
		return this.updateCommandProperty().get();
	}

	public final void setUpdateCommand(
			final PresentationModelCommand<Void> updateCommand) {
		this.updateCommandProperty().set(updateCommand);
	}

	private ObjectProperty<PresentationModelCommand<Void>> deleteSubtreeCommand;
	
	public final ObjectProperty<PresentationModelCommand<Void>> deleteSubtreeCommandProperty() {
		if (this.deleteSubtreeCommand == null) {
			deleteSubtreeCommand = new SimpleObjectProperty<PresentationModelCommand<Void>>(this, "deleteSubtreeCommand", new DeleteTaskSubtreeCommand());
		}
		return this.deleteSubtreeCommand;
	}

	public final PresentationModelCommand<Void> getDeleteSubtreeCommand() {
		return this.deleteSubtreeCommandProperty().get();
	}

	public final void setDeleteSubtreeCommand(
			final PresentationModelCommand<Void> deleteSubtreeCommand) {
		this.deleteSubtreeCommandProperty().set(deleteSubtreeCommand);
	}
	
	private ObjectProperty<PresentationModelCommand<Void>> indentTaskCommand;
	
	public final ObjectProperty<PresentationModelCommand<Void>> indentTaskCommandProperty() {
		if (indentTaskCommand == null) {
			indentTaskCommand = new SimpleObjectProperty<PresentationModelCommand<Void>>(this, "indentTaskCommand", new IndentTaskCommand(this.service, this));
		}
		return this.indentTaskCommand;
	}

	public final PresentationModelCommand<Void> getIndentTaskCommand() {
		return this.indentTaskCommandProperty().get();
	}

	public final void setIndentTaskCommand(
			final PresentationModelCommand<java.lang.Void> indentTaskCommand) {
		this.indentTaskCommandProperty().set(indentTaskCommand);
	}

	private ObjectProperty<PresentationModelCommand<Void>> outdentTaskCommand;
	
	public final ObjectProperty<PresentationModelCommand<Void>> outdentTaskCommandProperty() {
		if (outdentTaskCommand == null) {
			outdentTaskCommand = new SimpleObjectProperty<PresentationModelCommand<Void>>(this, "outdentTaskCommand", new OutdentTaskCommand(this.service, this));
		}
		return this.outdentTaskCommand;
	}

	public final net.d80harri.wr.ui.core.PresentationModelCommand<java.lang.Void> getOutdentTaskCommand() {
		return this.outdentTaskCommandProperty().get();
	}

	public final void setOutdentTaskCommand(
			final net.d80harri.wr.ui.core.PresentationModelCommand<java.lang.Void> outdentTaskCommand) {
		this.outdentTaskCommandProperty().set(outdentTaskCommand);
	}
	
	public void update() {
		getUpdateCommand().start();
	}

	public void deleteSubtree() {
		getDeleteSubtreeCommand().start();
	}

	public void indentTask() {
		getIndentTaskCommand().start();
	}

	public void outdentTask() {
		getOutdentTaskCommand().start();
	}

	public void addSibling(TaskPresentationModel taskPresentationModel) {
		int idxOfSelected = this.getParent().getChildren().indexOf(this);

		this.getParent().addChild(idxOfSelected + 1, taskPresentationModel);
	}

	@Override
	public String toString() {
		return getTitle() + " " + getChildren().size();
	}

}
