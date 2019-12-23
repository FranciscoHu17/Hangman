import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;


public class BuildGUI {
	public VBox root;
	public BorderPane gamefield;
	public Scene scene;
	public Stage hangmanStage;
	public GameMechanics game;
	
	public BuildGUI(Stage stage) {
		root = new VBox();
		scene = new Scene(root,1280,720);
		
		hangmanStage = stage;
		hangmanStage.getIcons().add(new Image("file:src/HangmanIcon.jpg"));
		hangmanStage.setTitle("Hangman");
		root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
		makeToolBar();
		resetGame();
	}
	
	public void makeToolBar() {
		ToolBar toolBar = new ToolBar(newButton(), loadButton(), saveButton(), exitButton());
		root.getChildren().add(toolBar);
		
	}
	
	public Button newButton() {
		ImageView newIcon = new ImageView(new Image("file:src/New.png"));
		Button newButton = new Button("", newIcon);
		newButton.setOnMousePressed(e1 -> {
			if(!game.guessing.inProgress) {
				resetGame();
				Button startButton = new Button("Start Playing");
				startButton.setOnMousePressed(e2 -> {
					startButton.setDisable(true);
					root.getChildren().add(gamefield);
					game.startGame();
					game.passRoot(root);
				});
				root.getChildren().add(startButton);
				newButton.setDisable(true);
			}
			else
				confirmSaveAfterNew();
		});
		
		return newButton;
	}
	
	public Button loadButton() {
		ImageView loadIcon = new ImageView(new Image("file:src/Load.png"));
		Button loadButton = new Button("", loadIcon); 
		loadButton.setOnMousePressed(e -> {
			if(game.guessing.inProgress)
				confirmSaveAfterLoad();
			else
				loadFile();
		});
		
		return loadButton;
	}
	
	public Button saveButton() {
		ImageView saveIcon = new ImageView(new Image("file:src/Save.png"));
		Button saveButton = new Button("", saveIcon); 
		saveButton.setDisable(true);
		saveButton.setOnMousePressed(e -> {
			saveGame();
		});
		
		return saveButton;
	}
	
	public Button exitButton() {
		ImageView exitIcon = new ImageView(new Image("file:src/Exit.png"));
		Button exitButton = new Button("", exitIcon); 
		exitButton.setOnMousePressed(e -> {
			if(game.guessing.inProgress)
				confirmSaveAfterExit();
			else
				hangmanStage.close();
		});
		
		return exitButton;
	}
	
	public void saveGame() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As");
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Hangman Files", "*.hng"));
		File selectedFile = fileChooser.showSaveDialog(hangmanStage);
		if (selectedFile != null) {
			try {
				FileOutputStream fileOut = new FileOutputStream(selectedFile.getAbsolutePath());
	            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
	            objectOut.writeObject(game.guessing.guessedLetters);
	            objectOut.writeObject(game.word);
	            objectOut.writeObject(game.remainingGuesses);
	            objectOut.close();
	            fileOut.close();
			}
			catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public void loadFile() {
		FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Open");
		 fileChooser.getExtensionFilters().add(
		         new ExtensionFilter("Hangman Files", "*.hng"));
		 File selectedFile = fileChooser.showOpenDialog(hangmanStage);
		 if (selectedFile != null) {
			 try {
		            FileInputStream fileIn = new FileInputStream(selectedFile.getAbsoluteFile());
		            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
		 
				    updateGame((String)objectIn.readObject(),
			    			(String)objectIn.readObject(),
			    			(int)objectIn.readObject());
		            objectIn.close();
		            fileIn.close();
		 
		        } catch (Exception e) {
		            e.printStackTrace();
		    }

		 }
	}
	
	public Object readObjectFromFile(String pathname) {
		 try {
			 
	            FileInputStream fileIn = new FileInputStream(pathname);
	            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
	 
	            Object obj = objectIn.readObject();
	            objectIn.close();
	            fileIn.close();
	            System.out.println(obj);
	            return obj;
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	}
	
	public void resetGame() {
		game = new GameMechanics();
		gamefield = game.gamefield;
		if(root.getChildren().size() > 1) {
			root.getChildren().remove(1);
			root.getChildren().remove(1);
		}
		game.setStageScene(hangmanStage, scene);
		root.setAlignment(Pos.TOP_CENTER);
		hangmanStage.setScene(scene);
	}
	
	public void updateGame(String guessedLetters, String word, int remainingGuesses) {
		if(root.getChildren().size() > 2) {
			root.getChildren().remove(2);
		}
		game = new GameMechanics();
		game.word = word;
		game.remainingGuesses = remainingGuesses;
		gamefield = game.gamefield;
		game.setStageScene(hangmanStage, scene);
		game.renderHangman();
		
		
		root.getChildren().add(gamefield);
		game.startGame();
		game.renderHangman();
		game.root = root;
		for(int i = 0; i < guessedLetters.length(); i++)
			game.guessing.findLetter(guessedLetters.charAt(i));
	}
	
	public void confirmSaveAfterLoad() {
		VBox confirm = new VBox();
		Stage savePopUp = new Stage();
		Label msg = new Label("Save?");
		confirm.setAlignment(Pos.CENTER);
		confirm.getChildren().add(msg);
		Button button = new Button("YES");
		button.setOnMousePressed(e -> {
			savePopUp.close();
			saveGame();
		});
		confirm.getChildren().add(button);
		button = new Button("NO");
		button.setOnMousePressed(e -> {
			loadFile();
			savePopUp.close();
		});
		confirm.getChildren().add(button);
		button = new Button("CANCEL");
		button.setOnMousePressed(e -> {
			savePopUp.close();
		});
		confirm.getChildren().add(button);
		savePopUp.setScene(new Scene(confirm, 480, 240));
		savePopUp.show();
	}
	
	public void confirmSaveAfterExit() {
		VBox confirm = new VBox();
		Stage savePopUp = new Stage();
		Label msg = new Label("Save?");
		confirm.setAlignment(Pos.CENTER);
		confirm.getChildren().add(msg);
		Button button = new Button("YES");
		button.setOnMousePressed(e -> {
			savePopUp.close();
			saveGame();
		});
		confirm.getChildren().add(button);
		button = new Button("NO");
		button.setOnMousePressed(e -> {
			hangmanStage.close();
			savePopUp.close();
		});
		confirm.getChildren().add(button);
		button = new Button("CANCEL");
		button.setOnMousePressed(e -> {
			savePopUp.close();
		});
		confirm.getChildren().add(button);
		savePopUp.setScene(new Scene(confirm, 480, 240));
		savePopUp.show();
	}
	
	public void confirmSaveAfterNew() {
		VBox confirm = new VBox();
		Stage savePopUp = new Stage();
		Label msg = new Label("Save?");
		confirm.setAlignment(Pos.CENTER);
		confirm.getChildren().add(msg);
		Button button = new Button("YES");
		button.setOnMousePressed(e -> {
			savePopUp.close();
			saveGame();
		});
		confirm.getChildren().add(button);
		button = new Button("NO");
		button.setOnMousePressed(e -> {
			savePopUp.close();
			resetGame();
			Button startButton = new Button("Start Playing");
			startButton.setOnMousePressed(e2 -> {
				startButton.setDisable(true);
				root.getChildren().add(gamefield);
				game.startGame();
				game.passRoot(root);
			});
			root.getChildren().add(startButton);
			((ToolBar) root.getChildren()).getItems().get(0).setDisable(true);
		});
		confirm.getChildren().add(button);
		button = new Button("CANCEL");
		button.setOnMousePressed(e -> {
			savePopUp.close();
		});
		confirm.getChildren().add(button);
		savePopUp.setScene(new Scene(confirm, 480, 240));
		savePopUp.show();
	}
}
