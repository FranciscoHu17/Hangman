import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import java.io.Serializable;

public class Guessing{
	public HBox unknown;
	public VBox alphabet;
	public Button saveButton;
	public String word, guessedLetters = "";
	public int unguessedLetters;
	public boolean inProgress;
	
	public Guessing(String wd) {
		unknown = new HBox();
		alphabet = new VBox();
		word = wd;
		unguessedLetters = word.length();
		inProgress = false;
		
		setUpUnknown();
		setUpAlphabet();
	}
	
	public void setUpUnknown() {
		for(int i = 0; i < word.length(); i++) {
			Rectangle rect = new Rectangle(30,30);
			Text text = new Text();
			rect.setFill(Color.WHITE);
			rect.setStroke(Color.LIGHTGRAY);
			StackPane stack = new StackPane();
			stack.getChildren().addAll(rect, text);
			
			unknown.getChildren().add(stack);
		}
	}
	
	public void setUpAlphabet() {
		HBox row = new HBox();
		for(int i = 0; i < 26; i++) {
			if(i%7 == 0) {
				row = new HBox();
				alphabet.getChildren().add(row);
			}
			Rectangle rect = new Rectangle(50,50);
			Text ch = new Text(Character.toString((char)(i+65)));
			rect.setFill(Color.LIMEGREEN);
			rect.setStroke(Color.LIGHTGRAY);	
			StackPane stack = new StackPane();
			stack.getChildren().addAll(rect, ch);
			row.getChildren().add(stack);
		}
	}
	
	public boolean findLetter(int ascii) {
		int row = (ascii - 65) / 7;
		int col = (ascii - 65) % 7;
		
		if(65 <= ascii && ascii <= 90) {
			StackPane stack = (StackPane) ((Pane) alphabet.getChildren().get(row)).getChildren().get(col);
			if(((Shape) stack.getChildren().get(0)).getFill() == Color.LIMEGREEN){
				((Shape) stack.getChildren().get(0)).setFill(Color.FORESTGREEN);
				inProgress = true;
				if(word.contains(Character.toString((char) ascii).toLowerCase())) {
					guessedLetters += Character.toString((char) ascii);
					placeLetter(ascii);
					return false;
				}
				return true;
			}
			return false;
		}
		return false;
	}
	
	public void placeLetter(int ascii) {
		for(int i = 0; i < word.length(); i++) {
			String ch = Character.toString((char)ascii);
			if(word.substring(i, i+1).equalsIgnoreCase(ch)) {
				((Text) ((StackPane) unknown.getChildren().get(i)).getChildren().get(1)).setText(ch);
				unguessedLetters--;
			}
		}
	}
	
	public void revealLetter() {
		for(int i = 0; i < word.length(); i++) {
			StackPane stack = (StackPane) unknown.getChildren().get(i);
		
			if(((Text) stack.getChildren().get(1)).getText().equals("")) {
				((Text) stack.getChildren().get(1)).setText(word.substring(i, i+1).toUpperCase());
				((Shape) stack.getChildren().get(0)).setFill(Color.DARKGRAY);
			}
		}
	}


	

	
	
}
