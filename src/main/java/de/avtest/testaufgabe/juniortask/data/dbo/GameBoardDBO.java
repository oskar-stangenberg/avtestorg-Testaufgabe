package de.avtest.testaufgabe.juniortask.data.dbo;


import de.avtest.testaufgabe.juniortask.data.GameBoard;
import de.avtest.testaufgabe.juniortask.data.enums.GameMark;
import de.avtest.testaufgabe.juniortask.data.enums.GamePlayer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.beans.ConstructorProperties;
import java.util.Objects;

@Entity(name = "GAME_BOARD")
public class GameBoardDBO {
    @Id
    private String gameId;
    private int boardSize;
    private GamePlayer lastPlayer;
    private String encodedState;

    public GameBoardDBO() {
    }

    public GameBoardDBO(String gameId, int boardSize, GamePlayer lastPlayer, String encodedState) {
        this.gameId = gameId;
        this.boardSize = boardSize;
        this.lastPlayer = lastPlayer;
        this.encodedState = encodedState;
    }

    public static GameBoardDBO fromGameBoard(String gameId, GameBoard gameBoard) {
        final String encodedState = encodeGameBoard(gameBoard);
        return new GameBoardDBO(gameId, gameBoard.getSize(), gameBoard.getLastPlayer(), encodedState);
    }

    public void applyToGameBoard(GameBoard gameBoard) {
        if (encodedState == null) {
            gameBoard.clear();
            return;
        }
        final char[] stateChars = encodedState.toCharArray();
        if (stateChars.length != gameBoard.getSize() * gameBoard.getSize()) {
            throw new IllegalStateException("Invalid encoded state detected for " + this);
        }
        for (int y = 0; y < gameBoard.getSize(); y++) {
            for (int x = 0; x < gameBoard.getSize(); x++) {
                final char encodedMark = stateChars[y * gameBoard.getSize() + x];
                GameMark mark = switch (encodedMark) {
                    case 'X' -> GameMark.CROSS;
                    case 'O' -> GameMark.CIRCLE;
                    case '_' -> GameMark.NONE;
                    default -> throw new IllegalStateException("Invalid encoded state detected for " + this);
                };
                gameBoard.setSpace(x, y, mark);
            }
        }
        gameBoard.setLastPlayer(lastPlayer);
    }

    public static String encodeGameBoard(GameBoard gameBoard) {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < gameBoard.getSize(); y++) {
            for (int x = 0; x < gameBoard.getSize(); x++) {
                final GameMark mark = gameBoard.getSpace(x, y);
                char encodedMark = switch (mark) {
                    case CIRCLE -> 'O';
                    case CROSS -> 'X';
                    default -> '_';
                };
                builder.append(encodedMark);
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "GameBoardDBO{" +
            "gameId=" + gameId +
            ", boardSize=" + boardSize +
            ", encodedState='" + encodedState + '\'' +
            '}';
    }

    public int boardSize() {
        return boardSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (GameBoardDBO) obj;
        return Objects.equals(this.gameId, that.gameId) &&
            this.boardSize == that.boardSize &&
            Objects.equals(this.lastPlayer, that.lastPlayer) &&
            Objects.equals(this.encodedState, that.encodedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, boardSize, lastPlayer, encodedState);
    }

}
