package net.d80harri.wr.ui;

import com.airhacks.afterburner.injection.Injector;

import net.d80harri.wr.db.SessionHandler;
import net.d80harri.wr.ui.application.ApplicationView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

	private static final String DB_URL = "jdbc:h2:~/prod;AUTO_SERVER=true";
//	private static final String DB_URL = "jdbc:h2:~/wr;AUTO_SERVER=true";
	
	@Override
	public void start(Stage stage) throws Exception {
		SessionHandler.configure(DB_URL);
		
		ApplicationView appView = new ApplicationView();
		Scene scene = new Scene(appView.getView());
		stage.setTitle("White Rabbit");
		final String uri = getClass().getResource("app.css").toExternalForm();
		scene.getStylesheets().add(uri);
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		Injector.forgetAll();
		SessionHandler.getInstance().close();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
