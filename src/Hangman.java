import javafx.scene.*;
import javafx.stage.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.image.*;

public class Hangman extends Application{
	public static void main(String args[]) {
		launch(args);
	}


	@Override
	public void start(Stage hangmanStage) throws Exception {
		BuildGUI gui = new BuildGUI(hangmanStage);
		hangmanStage.show();
	}
}
