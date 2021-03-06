package com.myproject.tictactoe;

import com.myproject.tictactoe.ai.Bot;
import com.myproject.tictactoe.ai.GameState;
import com.myproject.tictactoe.ai.minimax.AlphaBetaPruningBot;
import com.myproject.tictactoe.ai.minimax.MinimaxBot;
import com.myproject.tictactoe.ai.pattern.PatternBot;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * Обрабатывает действия пользователя в графическом интерфейсе.
 */
public class Controller {
    @FXML
    private GridPane grid;
    @FXML
    private AnchorPane panel;
    @FXML
    private Canvas canvas;
    @FXML
    private RadioButton minimax;
    @FXML
    private RadioButton alphaBeta;
    @FXML
    private RadioButton pattern;
    @FXML
    private RadioButton cross;
    @FXML
    private RadioButton zero;

    protected final static char CROSS = '×';
    protected final static char ZERO = '￮';
    protected final static char EMPTY = ' ';

    private Bot aiBot;
    private char playerSymbol;
    private char botSymbol;

    /** Вызывается при нажатии на одну из клеток игрового поля, отображает ход игрока. */
    public void step(Event event) {
        Button button = (Button) event.getSource();
        button.setText(String.valueOf(playerSymbol));
        button.setDisable(true);

        botStep();
    }

    /** Вызывается до или после хода игрока, отображает ход бота. */
    private void botStep() {
        ObservableList<Node> cells = grid.getChildren();
        StringBuilder sb = new StringBuilder();
        cells.forEach(child -> sb.append(((Button) child).getText()));

        String field = sb.toString();
        GameState gameState = aiBot.play(field);
        int place = gameState.getPlace();

        if (place >= 0) {
            Button button = ((Button) cells.get(place));
            button.setText(String.valueOf(botSymbol));
            button.setDisable(true);
        }

        drawLine(gameState.getWinningIndex());

        switch (gameState.getStatus()) {
            case LOSS:
                showGameResult("You lost");
                break;
            case WIN:
                showGameResult("You Won");
                break;
            case DRAW:
                showGameResult("Draw");
                break;
        }
    }

    /** Вызывается при нажатии на кнопку новой игры, сбрасывает состояние игрового поля. */
    public void newGame() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        grid.setDisable(false);
        ObservableList<Node> cells = grid.getChildren();
        cells.forEach(child -> {
            Button cell = (Button) child;
            cell.setText(String.valueOf(EMPTY));
            cell.setDisable(false);
        });

        panel.setDisable(true);

        if (playerSymbol == ZERO) {
            botStep();
        }
    }

    /**
     * Устанавливает в качестве бота выбранный алгоритм.
     * @see Bot
     */
    public void selectAI(Event event) {
        RadioButton aiSelector = (RadioButton) event.getSource();
        aiBot = (Bot) aiSelector.getUserData();
    }

    /**В зависимости от выбора игрока, устанавливает символ для игрока и бота. */
    public void selectSymbol(Event event) {
        RadioButton symbolSelector = (RadioButton) event.getSource();
        playerSymbol = (char) symbolSelector.getUserData();
        botSymbol = (playerSymbol == CROSS) ? ZERO : CROSS;
    }

    /**
     * Рисует линию поверх игрового поля при победе игрока или бота
     *
     * @param index номер выигрышной комбинации
     */
    private void drawLine(int index) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);

        switch (index) {
            case 0:
                gc.strokeLine(25, 50, 275, 50);
                break;
            case 1:
                gc.strokeLine(25, 150, 275, 150);
                break;
            case 2:
                gc.strokeLine(25, 250, 275, 250);
                break;
            case 3:
                gc.strokeLine(50, 25, 50, 275);
                break;
            case 4:
                gc.strokeLine(150, 25, 150, 275);
                break;
            case 5:
                gc.strokeLine(250, 25, 250, 275);
                break;
            case 6:
                gc.strokeLine(25, 25, 275, 275);
                break;
            case 7:
                gc.strokeLine(275, 25, 25, 275);
                break;
        }
    }

    /**
     * Отображает сообщение с результатом игры.
     *
     * @param message результат игры
     */
    private void showGameResult(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Game over");
        alert.setContentText(message);
        alert.show();
        grid.setDisable(true);
        panel.setDisable(false);
    }

    /**
     * Вызывается при запуске игры, устанавливает значения для переключателей и стандартные параметры
     * (алгоритм бота - минимакс, символ игрока - крестик, символ бота - нолик).
     */
    @FXML
    public void initialize() {
        minimax.setUserData(new MinimaxBot());
        alphaBeta.setUserData(new AlphaBetaPruningBot());
        pattern.setUserData(new PatternBot());

        cross.setUserData(CROSS);
        zero.setUserData(ZERO);

        aiBot = (Bot) minimax.getUserData();
        playerSymbol = CROSS;
        botSymbol = ZERO;
    }
}
