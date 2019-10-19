
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
	@SuppressWarnings("static-access")
	public void start(Stage primaryStage) throws Exception {

		// starter menu
		StackPane mainMenu = new StackPane();
		BackgroundImage backgroundImage = new BackgroundImage(new Image("/images/background.jpg"),
				BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		mainMenu.setBackground(new Background(backgroundImage));
		Button playButton = new Button("", new ImageView(new Image("/images/play.png")));
		mainMenu.getChildren().add(playButton);

		Scene mainScene = new Scene(mainMenu, 1020, 840);

		// construct levels
		Level level1 = new Level("level1");
		Level level2 = new Level("level2");
		Level level3 = new Level("level3");
		Level level4 = new Level("level4");
		Level level5 = new Level("level5");

		// construct level1 borderPane
		BorderPane bp1 = new BorderPane();
		Button button1 = new Button("Next Level");
		button1.setDisable(true);
		Label score1 = new Label("Score : " + level1.getCounter());
		bp1.setCenter(level1.getLevel());
		bp1.setLeft(score1);
		bp1.setRight(button1);
		bp1.setAlignment(score1, Pos.CENTER_LEFT);
		bp1.setAlignment(button1, Pos.CENTER_RIGHT);
		bp1.setMargin(level1.getLevel(), new Insets(12));
		bp1.setMargin(score1, new Insets(12));
		bp1.setMargin(button1, new Insets(12));

		// construct level2 borderPane
		BorderPane bp2 = new BorderPane();
		Button button2 = new Button("Next Level");
		button2.setDisable(true);
		Label score2 = new Label("Score : " + level2.getCounter());
		bp2.setCenter(level2.getLevel());
		bp2.setLeft(score2);
		bp2.setRight(button2);
		bp2.setAlignment(score2, Pos.CENTER_LEFT);
		bp2.setAlignment(button2, Pos.CENTER_RIGHT);
		bp2.setMargin(level2.getLevel(), new Insets(12));
		bp2.setMargin(score2, new Insets(12));
		bp2.setMargin(button2, new Insets(12));

		// construct level3 borderPane
		BorderPane bp3 = new BorderPane();
		Button button3 = new Button("Next Level");
		button3.setDisable(true);
		Label score3 = new Label("Score : " + level3.getCounter());
		bp3.setCenter(level3.getLevel());
		bp3.setLeft(score3);
		bp3.setRight(button3);
		bp3.setAlignment(score3, Pos.CENTER_LEFT);
		bp3.setAlignment(button3, Pos.CENTER_RIGHT);
		bp3.setMargin(level3.getLevel(), new Insets(12));
		bp3.setMargin(score3, new Insets(12));
		bp3.setMargin(button3, new Insets(12));

		// construct level4 borderPane
		BorderPane bp4 = new BorderPane();
		Button button4 = new Button("Next Level");
		button4.setDisable(true);
		Label score4 = new Label("Score : " + level3.getCounter());
		bp4.setCenter(level4.getLevel());
		bp4.setLeft(score4);
		bp4.setRight(button4);
		bp4.setAlignment(score4, Pos.CENTER_LEFT);
		bp4.setAlignment(button4, Pos.CENTER_RIGHT);
		bp4.setMargin(level4.getLevel(), new Insets(12));
		bp4.setMargin(score4, new Insets(12));
		bp4.setMargin(button4, new Insets(12));

		// construct level5 borderPane
		BorderPane bp5 = new BorderPane();
		Button button5 = new Button("Next Level");
		button5.setDisable(true);
		Label score5 = new Label("Score : " + level3.getCounter());
		bp5.setCenter(level5.getLevel());
		bp5.setLeft(score5);
		bp5.setRight(button5);
		bp5.setAlignment(score5, Pos.CENTER_LEFT);
		bp5.setAlignment(button5, Pos.CENTER_RIGHT);
		bp5.setMargin(level5.getLevel(), new Insets(12));
		bp5.setMargin(score5, new Insets(12));
		bp5.setMargin(button5, new Insets(12));

		primaryStage.setTitle("Plumber Game");
		primaryStage.setScene(mainScene);

		// main menu events

		playButton.setOnAction(e -> {
			primaryStage.setScene(new Scene(bp1));
		});

		// scene1 events
		bp1.setOnMouseMoved(e -> {
			score1.setText("Score : " + level1.getCounter());
			if (level1.isAnimation()) {
				button1.setDisable(false);
			}
		});

		button1.setOnAction(e -> {
			primaryStage.setScene(new Scene(bp2));
		});

		// scene2 events
		bp2.setOnMouseMoved(e -> {
			score2.setText("Score : " + level2.getCounter());
			if (level2.isAnimation()) {
				button2.setDisable(false);
			}
		});

		button2.setOnAction(e -> {
			primaryStage.setScene(new Scene(bp3));
		});

		// scene3 events
		bp3.setOnMouseMoved(e -> {
			score3.setText("Score : " + level3.getCounter());
			if (level3.isAnimation()) {
				button3.setDisable(false);
			}
		});

		button3.setOnAction(e -> {
			primaryStage.setScene(new Scene(bp4));
		});

		// scene4 events
		bp4.setOnMouseMoved(e -> {
			score4.setText("Score : " + level4.getCounter());
			if (level4.isAnimation()) {
				button4.setDisable(false);
			}
		});

		button4.setOnAction(e -> {
			primaryStage.setScene(new Scene(bp5));
		});

		// scene5 events
		bp5.setOnMouseMoved(e -> {
			score5.setText("Score : " + level5.getCounter());
		});

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
