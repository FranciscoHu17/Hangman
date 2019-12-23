import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import java.io.Serializable;

public class GameMechanics{
	public Guessing guessing;
	public BorderPane gamefield;
	public VBox guess, root;
	public Scene scene;
	public Stage hangmanStage;
	public String word = "";
	public int remainingGuesses;
	
	public GameMechanics() {
		gamefield = new BorderPane();
		guess = new VBox();
		guessing = new Guessing(word);
		remainingGuesses = 10;
		word = chooseWord(totalLines());
	}
	
	public void setStageScene(Stage stage, Scene scn) {
		hangmanStage = stage;
		scene = scn;
	}

	public void startGame() {
		Label hangmanLabel = new Label("Hangman");
		hangmanLabel.setFont(new Font("Arial", 36));
		gamefield.setAlignment(hangmanLabel, Pos.TOP_CENTER);
		gamefield.setTop(hangmanLabel);
		setUpGuess();
		gamefield.setAlignment(guess,Pos.CENTER);
		gamefield.setRight(guess);
		playGame();
	}
	
	public void playGame() {
		selectLetter();
	}
	
	public void setUpGuess() {
		guess.getChildren().add(new Label("Remaining Guesses: " + remainingGuesses));
		guessing = new Guessing(word);
		VBox.setMargin(guessing.alphabet, new Insets(50,0,0,0));
		guess.getChildren().add(guessing.unknown);
		guess.getChildren().add(guessing.alphabet);
		
	}
	
	public void updateCounter() {
		guess.getChildren().add(0, new Label("Remaining Guesses: " + remainingGuesses));
		guess.getChildren().remove(1);
	}
	
	public int totalLines() {
		File file = new File(getClass().getResource("words.txt").getPath()); 
		int totalLines = 0;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) 
			   totalLines++; 
		} catch (FileNotFoundException e) {
			System.out.print("File not found");
		} catch (IOException e) {
			System.out.print("File not found");
		}
		return totalLines; 
	}
	
	public String chooseWord(int totalLines) {
		File file = new File(getClass().getResource("words.txt").getPath()); 
		int rndm = (int) (Math.random()*totalLines + 1);
		String str = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			for(int i = 0; i < rndm; i++) {
			 str = br.readLine();	
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public void selectLetter() {
		scene.setOnKeyPressed(e -> {
			if(e.getCode().toString().length() == 1 && guessing.findLetter((int)e.getCode().toString().charAt(0))) {
				remainingGuesses--;
				updateCounter();
				renderHangman();
			}
			boolean endCondition = remainingGuesses == 0 || guessing.unguessedLetters == 0;
			if(endCondition) {
				guessing.inProgress = false;
				scene.setOnKeyPressed(null);
				guessing.revealLetter();
				endGame();
			}
			((ToolBar) root.getChildren().get(0)).getItems().get(2).setDisable(!guessing.inProgress);
			if(endCondition || guessing.inProgress)
				((ToolBar) root.getChildren().get(0)).getItems().get(0).setDisable(false);
		});
	}
	
	public void passRoot(VBox pass) {
		root = pass;
	}
	
	public void endGame() {
		VBox gameover = new VBox();
		Stage endPopUp = new Stage();
		Label msg = new Label("You won");
		gameover.setAlignment(Pos.CENTER);
		if(guessing.unguessedLetters != 0)
			msg.setText("You lost (the word was \"" + word + " \")");
		Button close = new Button("CLOSE");
		close.setOnMousePressed(e -> {
			hangmanStage.close();
			endPopUp.close();
		});
		gameover.getChildren().add(msg);
		gameover.getChildren().add(close);
		endPopUp.setScene(new Scene(gameover, 480, 240));
		endPopUp.show();
	}
	
	public void renderHangman() {
		ImageView hangman = new ImageView(new Image("file:src/hangman" + (10 - remainingGuesses) + ".png"));
		gamefield.setLeft(hangman);
	}
	
}

