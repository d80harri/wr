package net.d80harri.wr.ui.takstree;

import static org.fxmisc.easybind.EasyBind.select;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import javax.inject.Inject;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.ui.task.TaskPresentationModel;
import net.d80harri.wr.ui.utils.CustomBindings;

import org.fxmisc.easybind.EasyBind;

public class TaskTreePresenter implements Initializable {
	@FXML
	private TreeTableView<TaskPresentationModel> tree;
	@FXML
	private TreeTableColumn<TaskPresentationModel, String> titleColumn;
	@FXML
	private TreeTableColumn<TaskPresentationModel, String> debugColumn;
	@FXML
	private Button addButton;

	@Inject
	WrService service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		load();

		titleColumn
				.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TaskPresentationModel, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(
							CellDataFeatures<TaskPresentationModel, String> param) {
						return param.getValue().getValue().titleProperty();
					}
				});
				
		titleColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		titleColumn.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<TaskPresentationModel,String>>() {
			
			@Override
			public void handle(CellEditEvent<TaskPresentationModel, String> event) {
				event.getRowValue().getValue().setTitle(event.getNewValue());
			}
		});
		
		debugColumn
				.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TaskPresentationModel, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(
							CellDataFeatures<TaskPresentationModel, String> param) {
						ObjectProperty<Integer> prop = new SimpleObjectProperty<Integer>();
						prop.bind(Bindings.size(
								param.getValue().getValue().getChildren())
								.asObject());
						return new StringBinding() {
							{
								bind(param.getValue().getValue().getChildren(), param.getValue().getValue().expandedProperty(), param.getValue().getValue().selectedProperty());
							}
							@Override
							protected String computeValue() {
								String expanded = param.getValue().getValue().isExpanded() ? "expaned" : "";
								String selected = param.getValue().getValue().isSelected() ? "selected" : "";
								return "(" + param.getValue().getValue().getChildren().size() + ")" + expanded + ", " + selected;
							}
						};
					}
				});

		addButton.setOnAction((e) -> getModel().addRootTask());
	}

	private void load() {
		tree.rootProperty().bind(createTreeItemBinding(this.modelProperty()));
		CustomBindings.bindSelectedItemBidirectional(getModel().selectedModelProperty(), tree);
		getModel().load(service);
	}


	private ObjectProperty<TaskTreePresentationModel> model = null;

	
	public final ObjectProperty<TaskTreePresentationModel> modelProperty() {
		if (model == null) {
			model = new SimpleObjectProperty<TaskTreePresentationModel>(
					this, "model", new TaskTreePresentationModel(service));
		}
		return this.model;
	}

	public final TaskTreePresentationModel getModel() {
		return this.modelProperty().get();
	}

	public final void setModel(final TaskTreePresentationModel model) {
		this.modelProperty().set(model);
	}

	private ObservableValue<? extends TreeItem<TaskPresentationModel>> createTreeItemBinding(
			ObjectProperty<TaskTreePresentationModel> model) {
		return new ObjectBinding<TreeItem<TaskPresentationModel>>() {
			{
				bind(model, model.getValue().rootModelProperty());
			}
			
			@Override
			protected TreeItem<TaskPresentationModel> computeValue() {
				ObservableValue<TaskPresentationModel> rootModel = select(model).selectObject(m -> m.rootModelProperty());
					TreeItem<TaskPresentationModel> result = new TreeItem<TaskPresentationModel>();
					result.valueProperty().bind(rootModel);
					
				if (rootModel.getValue() != null) {
					Bindings.bindContent(result.getChildren(),
							createTreeItemListBinding(rootModel.getValue()
									.getChildren()));
				}
				return result;
			}
			
		};
	}
	
	private ObservableList<TreeItem<TaskPresentationModel>> createTreeItemListBinding(
			ObservableList<TaskPresentationModel> children) {
		return EasyBind.map(children, new Function<TaskPresentationModel, TreeItem<TaskPresentationModel>>() {
			
			@Override
			public TreeItem<TaskPresentationModel> apply(TaskPresentationModel t) {
				TreeItem<TaskPresentationModel> result = new TreeItem<>(t);
				result.expandedProperty().bindBidirectional(t.expandedProperty());
				Bindings.bindContent(result.getChildren(), createTreeItemListBinding(t.getChildren()));
				return result;
			}
		});
	}
	
	@FXML
	private void onKeyPressedInTable(KeyEvent evt) {
		switch (evt.getCode()) {
		case ENTER:
			getModel().addSiblingToSelected();
			evt.consume();
			break;
		case TAB:
			if (evt.isShiftDown()) {
				getModel().outdentSelectedTask();
			} else {
				getModel().indentSelectedTask();
			}
			evt.consume();
			break;
		case DELETE:
			getModel().deleteSelectedSubtree();
			evt.consume();
			break;
		default: // nothing to do
		}
	}
}
