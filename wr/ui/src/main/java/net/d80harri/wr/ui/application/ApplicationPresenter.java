package net.d80harri.wr.ui.application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import net.d80harri.wr.ui.takstree.TaskTreeView;

public class ApplicationPresenter implements Initializable {
	@FXML BorderPane taskTree;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		taskTree.setCenter(new TaskTreeView().getView());
	}
	
	@FXML
	private void debug(ActionEvent evt) {
		System.out.println();
	}
}
