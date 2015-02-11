package net.d80harri.wr.ui.takstree;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import javax.inject.Inject;

import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

import net.d80harri.wr.service.WrService;
import net.d80harri.wr.service.model.TaskDto;
import net.d80harri.wr.ui.takstree.binding.TaskTreeBinding;
import net.d80harri.wr.ui.task.TaskPresentationModel;
import net.d80harri.wr.ui.utils.DebugUtils;

public class TaskTreePresenter implements Initializable {
	@FXML
	private TreeTableView<TaskPresentationModel> tree;
	@FXML
	private TreeTableColumn<TaskPresentationModel, String> titleColumn;
	@FXML
	private TreeTableColumn<TaskPresentationModel, Integer> debugColumn;
	@FXML
	private Button addButton;

	@Inject
	WrService service;

	private ObjectProperty<TaskTreePresentationModel> model = new SimpleObjectProperty<TaskTreePresentationModel>(
			this, "model", new TaskTreePresentationModel());

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

		debugColumn
				.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TaskPresentationModel, Integer>, ObservableValue<Integer>>() {

					@Override
					public ObservableValue<Integer> call(
							CellDataFeatures<TaskPresentationModel, Integer> param) {
						ObjectProperty<Integer> prop = new SimpleObjectProperty<Integer>();
						prop.bind(Bindings.size(
								param.getValue().getValue().getChildren())
								.asObject());
						return prop;
					}
				});

		addButton.setOnAction((e) -> addRootTask());
	}

	private void load() {
		tree.rootProperty().bind(createTreeItemBinding(this.model));
		getModel().load(service);
		//
		// model = createRootModel(service.getAllTrees());
		// TreeItem<TaskPresentationModel> rootItem = new TaskTreeBinding(model,
		// tree).get();
		// tree.setRoot(rootItem);
		//
		// tree.getSelectionModel().selectedItemProperty().addListener((obs, o,
		// n) -> {
		// if (o != null) {
		// o.getValue().setSelected(false);
		// }
		// if (n != null) {
		// n.getValue().setSelected(true);
		// }
		// });
	}

	private ObservableValue<? extends TreeItem<TaskPresentationModel>> createTreeItemBinding(
			ObjectProperty<TaskTreePresentationModel> model) {
		return new ObjectBinding<TreeItem<TaskPresentationModel>>() {
			{
				bind(model, model.getValue().rootModelProperty());
			}

			@Override
			protected TreeItem<TaskPresentationModel> computeValue() {
				ObservableValue<TaskPresentationModel> rootModel = EasyBind
						.select(model).selectObject(m -> m.rootModelProperty());

				TreeItem<TaskPresentationModel> result = new TreeItem<TaskPresentationModel>();
				result.valueProperty().bind(rootModel);

				Bindings.bindContent(result.getChildren(),
						createTreeItemListBinding(rootModel.getValue()
								.getChildren()));
				return result;
			}

		};
	}

	private ObservableList<TreeItem<TaskPresentationModel>> createTreeItemListBinding(
			ObservableList<TaskPresentationModel> children) {
		return new ListBinding<TreeItem<TaskPresentationModel>>() {

			{
				super.bind(children);
			}

			@Override
			protected ObservableList<TreeItem<TaskPresentationModel>> computeValue() {
				ObservableList<TreeItem<TaskPresentationModel>> result = FXCollections
						.observableArrayList();

				for (TaskPresentationModel child : children) {
					TreeItem<TaskPresentationModel> childItem = new TreeItem<TaskPresentationModel>(
							child);
					Bindings.bindContent(childItem.getChildren(),
							createTreeItemListBinding(child.getChildren()));
					result.add(childItem);
				}
				return result;
			}

		};
	}

	@FXML
	private void onKeyPressedInTable(KeyEvent evt) {
		switch (evt.getCode()) {
		case ENTER:
			addSiblingTask();
			evt.consume();
			break;
		case TAB:
			if (evt.isShiftDown()) {
				outdentTask();
			} else {
				indentTask();
			}
			evt.consume();
			break;
		case DELETE:
			deleteTask();
			evt.consume();
			break;
		default: // nothing to do
		}
	}

	private void deleteTask() {
		doWithSelectedTask(s -> {
			service.deleteSubtree(s.getId());
			if (s.getParent() != null) {
				s.getParent().getChildren().remove(s);
			}
		});
	}

	private void indentTask() {
		doWithSelectedTask(s -> {
			int idx = s.getParent().getChildren().indexOf(s);
			if (idx != 0) {
				DebugUtils.printTreeStructure(s.getParent(), 0,
						i -> i.getChildren());
				TaskPresentationModel precessor = s.getParent().getChildren()
						.get(idx - 1);
				precessor.addChild(s);
				s.setSelected(true);
				DebugUtils.printTreeStructure(precessor, 0,
						i -> i.getChildren());
			}
		});
	}

	private void outdentTask() {
		doWithSelectedTask(s -> {
			TaskPresentationModel grandParent = s.getParent().getParent();
			if (grandParent != null) {
				int idxOfParent = grandParent.getChildren().indexOf(
						s.getParent());
				grandParent.getChildren().add(idxOfParent + 1, s);
				s.setSelected(true);
			}
		});
	}

	private void addSiblingTask() {
		doWithSelectedTask(s -> {
			int idxOfSelected = s.getParent().getChildren().indexOf(s);

			s.getParent()
					.getChildren()
					.add(idxOfSelected + 1,
							new TaskPresentationModel(new TaskDto("new")));
		});
	}

	private void addRootTask() {
		// model.getChildren().add(new TaskPresentationModel(new
		// TaskDto("new")));
	}

	private void doWithSelectedTask(Consumer<TaskPresentationModel> func) {
		TreeItem<TaskPresentationModel> selectedItem = tree.getSelectionModel()
				.getSelectedItem();
		if (selectedItem.getValue() != this.getModel().getRootModel()) {
			func.accept(selectedItem.getValue());
		}
	}

	public final ObjectProperty<TaskTreePresentationModel> modelProperty() {
		return this.model;
	}

	public final TaskTreePresentationModel getModel() {
		return this.modelProperty().get();
	}

	public final void setModel(final TaskTreePresentationModel model) {
		this.modelProperty().set(model);
	}

	public static void main(String[] args) {
		ObservableList<Integer> ints = FXCollections.observableArrayList();
		ObservableList<String> str = FXCollections.observableArrayList();

		Bindings.bindContent(str, new ListBinding<String>() {
			{
				bind(ints);
			}

			@Override
			protected ObservableList<String> computeValue() {
				ObservableList<String> result = FXCollections
						.observableArrayList();

				for (Integer i : ints) {
					result.add("" + i);
				}

				return result;
			}
		});

		System.out.println(str);
		ints.add(17);
		ints.add(4);
		System.out.println(str);
	}
}
