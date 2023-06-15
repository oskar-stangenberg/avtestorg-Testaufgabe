package de.avtest.testaufgabe.juniortask.rest;

import de.avtest.testaufgabe.juniortask.data.GameBoard;
import de.avtest.testaufgabe.juniortask.data.enums.GameMark;
import de.avtest.testaufgabe.juniortask.data.enums.GameStatus;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GameControllerTests {
    @Test
    void testGameStatusSystem() {
        GameController gameController = new GameController();
        final GameBoard gameBoard = new GameBoard(3);
        assertThat(gameController.determineGameStatus(gameBoard)).isEqualTo(GameStatus.UNDECIDED);
        gameBoard.setSpace(0,0, GameMark.CIRCLE);
        gameBoard.setSpace(0,1, GameMark.CIRCLE);
        gameBoard.setSpace(0,2, GameMark.CIRCLE);
        assertThat(gameController.determineGameStatus(gameBoard)).isEqualTo(GameStatus.HUMAN_WON);
        gameBoard.setSpace(0,2, GameMark.CROSS);
        assertThat(gameController.determineGameStatus(gameBoard)).isEqualTo(GameStatus.UNDECIDED);
        gameBoard.setSpace(1,2, GameMark.CROSS);
        assertThat(gameController.determineGameStatus(gameBoard)).isEqualTo(GameStatus.UNDECIDED);
        gameBoard.setSpace(2,2, GameMark.CROSS);
        assertThat(gameController.determineGameStatus(gameBoard)).isEqualTo(GameStatus.ROBOT_WON);

        final GameBoard evenBoard = new GameBoard(2);
        assertThat(gameController.determineGameStatus(evenBoard)).isEqualTo(GameStatus.UNDECIDED);
        evenBoard.setSpace(0,0,GameMark.CIRCLE);
        evenBoard.setSpace(0,1,GameMark.CIRCLE);
        assertThat(gameController.determineGameStatus(evenBoard)).isEqualTo(GameStatus.HUMAN_WON);

        final GameBoard staleBoard = new GameBoard(3);
        staleBoard.setSpace(0,0, GameMark.CROSS);
        staleBoard.setSpace(1,0, GameMark.CROSS);
        staleBoard.setSpace(2,0, GameMark.CIRCLE);

        staleBoard.setSpace(0,1, GameMark.CIRCLE);
        staleBoard.setSpace(1,1, GameMark.CIRCLE);
        staleBoard.setSpace(2,1, GameMark.CROSS);

        staleBoard.setSpace(0,2, GameMark.CROSS);
        staleBoard.setSpace(1,2, GameMark.CIRCLE);
        staleBoard.setSpace(2,2, GameMark.CROSS);

        assertThat(gameController.determineGameStatus(staleBoard)).isEqualTo(GameStatus.STALEMATE);

        final GameBoard fullWonBoard = new GameBoard(3);
        fullWonBoard.setSpace(0,0, GameMark.CROSS);
        fullWonBoard.setSpace(1,0, GameMark.CROSS);
        fullWonBoard.setSpace(2,0, GameMark.CROSS);

        fullWonBoard.setSpace(0,1, GameMark.CIRCLE);
        fullWonBoard.setSpace(1,1, GameMark.CIRCLE);
        fullWonBoard.setSpace(2,1, GameMark.CROSS);

        fullWonBoard.setSpace(0,2, GameMark.CROSS);
        fullWonBoard.setSpace(1,2, GameMark.CIRCLE);
        fullWonBoard.setSpace(2,2, GameMark.CIRCLE);

        assertThat(gameController.determineGameStatus(fullWonBoard)).isEqualTo(GameStatus.ROBOT_WON);
    }
}
