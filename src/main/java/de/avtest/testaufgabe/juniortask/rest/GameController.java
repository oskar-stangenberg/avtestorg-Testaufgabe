package de.avtest.testaufgabe.juniortask.rest;

import de.avtest.testaufgabe.juniortask.data.GameBoard;
import de.avtest.testaufgabe.juniortask.data.GameBoardSlice;
import de.avtest.testaufgabe.juniortask.data.enums.GameMark;
import de.avtest.testaufgabe.juniortask.data.enums.GamePlayer;
import de.avtest.testaufgabe.juniortask.data.enums.GameStatus;
import de.avtest.testaufgabe.juniortask.data.dbo.GameBoardDBO;
import de.avtest.testaufgabe.juniortask.repository.GameBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("api/game")
public class GameController {

  private final Map<String, GameBoard> storedGames = new LinkedHashMap<>();
  private final Random random = new Random();
  @Autowired private CopyrightController copyrightController;

  @Autowired private GameBoardRepository gameBoardRepository;

  /**
   *
   * @param gameBoard
   * @return
   */
  protected ResponseEntity<String> statusOutput(GameBoard gameBoard) {
    final GameStatus gameStatus = determineGameStatus(gameBoard);
    final var finalOutput = switch (gameStatus) {
      case HUMAN_WON -> "You won the game! Congratulations!";
      case ROBOT_WON -> "The bot won the game...";
      case STALEMATE -> "It's a draw";
      default -> "";
    };

    return ResponseEntity.ok(copyrightController.getCopyright() +
      System.lineSeparator() +
      System.lineSeparator() +
      gameBoard.draw() +
      finalOutput
    );
  }

  /**
   * Determines the current game-status
   * @param gameBoard
   * @return
   */
  protected GameStatus determineGameStatus(GameBoard gameBoard) {
    final List<GameBoardSlice> slices = gameBoard.getSlices();
    for (GameBoardSlice slice : slices) {
      final GameStatus winner = findSliceWinner(slice);
      if (winner == GameStatus.HUMAN_WON || winner == GameStatus.ROBOT_WON) {
        return winner;
      }
    }
    if(!gameBoard.spaceIsLeft()) {
      return GameStatus.STALEMATE;
    }
    return GameStatus.UNDECIDED;
  }

  /**
   * Find the winner of a slice
   * @param slice
   * @return
   */
  protected GameStatus findSliceWinner(GameBoardSlice slice) {
    int numHuman = 0;
    int numRobot = 0;
    final List<GameMark> spaces = slice.getSpaces();
    for (GameMark space : spaces) {
      switch (space) {
        case CIRCLE -> numHuman++;
        case CROSS -> numRobot++;
      }
    }

    final int boardSize = spaces.size();
    if (numHuman == boardSize) {
      return GameStatus.HUMAN_WON;
    } else if (numRobot == boardSize) {
      return GameStatus.ROBOT_WON;
    } else if (numHuman > 0 && numRobot > 0) {
      return GameStatus.STALEMATE;
    } else {
      return GameStatus.UNDECIDED;
    }
  }

  /**
   * Is the given player allowed to take the next turn?
   * @param gameBoard
   * @param player
   * @return
   */
  protected boolean isAllowedToPlay(GameBoard gameBoard, GamePlayer player) {
    // ##### TASK 6 - No cheating! #################################################################################

    return gameBoard.getLastPlayer() != player;
  }

  /**
   * Save the GameBoard to the database
   * @param gameBoard
   * @return
   */
  protected void saveGameBoard(String uuid, GameBoard gameBoard) {
    gameBoardRepository.save(GameBoardDBO.fromGameBoard(uuid, gameBoard));
  }

  /**
   *
   * @param gameId The ID of the game
   * @param x The x position entered by the player
   * @param y The y position entered by the player
   * @return
   */
  @GetMapping(value = "play", produces = "text/plain")
  public ResponseEntity<String> play(@RequestParam String gameId, @RequestParam int x, @RequestParam int y) {
    // Loading the game board
    var gameBoard = getGameBoard(gameId);

    if(gameBoard == null) {
      return ResponseEntity.notFound().build();
    }

    // Check if the given position is actually valid; can't have the player draw a cross on the table next to the
    // game board ;)
    if (x < 0 || y < 0 || x >= gameBoard.getSize() || y >= gameBoard.getSize()) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Position outside of the game board");
    }

    // Prevent the player from playing if the game has already ended
    if (this.determineGameStatus(gameBoard) != GameStatus.UNDECIDED) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to play. The game has already ended.");
    }

    // Prevent the player from playing if it is not his turn
    if (!this.isAllowedToPlay(gameBoard, GamePlayer.HUMAN)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to play. It is the bot's turn!");
    }

    // ##### TASK 4 - Let the player make their move ###############################################################

    // Check if the space is already claimed
    if(gameBoard.getSpace(x, y)  != GameMark.NONE) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This space has already been claimed!");
    }

    // Update the board
    gameBoard.setSpace(x, y, GameMark.CIRCLE);

    // Saving the game board and output it to the player
    saveGameBoard(gameId, gameBoard);

    return this.statusOutput(gameBoard);
  }

  /**
   * Attempts to load the game first from the local storedGames and then from the database
   * @param gameId
   * @return
   */
  private GameBoard getGameBoard(String gameId) {
    synchronized (storedGames) {
      final GameBoard gameBoard = storedGames.get(gameId);
      if(gameBoard != null) {
        return gameBoard;
      }

      final Optional<GameBoardDBO> boardDBOOptional = gameBoardRepository.findById(gameId);
      if (boardDBOOptional.isEmpty()) {
        return null;
      }
      final GameBoardDBO boardDBO = boardDBOOptional.get();
      final GameBoard loadedGameBoard = new GameBoard(boardDBO.boardSize());
      boardDBO.applyToGameBoard(loadedGameBoard);
      storedGames.put(gameId, loadedGameBoard);
      return loadedGameBoard;
    }
  }

  @GetMapping(value = "playBot", produces = "text/plain")
  public ResponseEntity<String> playBot(@RequestParam String gameId) {
    // Loading the game board
    var gameBoard = getGameBoard(gameId);

    if(gameBoard == null) {
      return ResponseEntity.notFound().build();
    }

    // ##### TASK 5 - Understand the bot ###########################################################################
    // =============================================================================================================
    // This first step to beat your enemy is to thoroughly understand them.
    // Luckily, as a developer, you can literally look into its head. So, check out the bot logic and try to
    // understand what it does.
    // =============================================================================================================

    // Prevent the player from playing if the game has already ended
    if (determineGameStatus(gameBoard) != GameStatus.UNDECIDED) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to play. The game has already ended.");
    }

    // Prevent the player from playing if it is not his turn
    if (!this.isAllowedToPlay(gameBoard, GamePlayer.ROBOT)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The bot is not allowed to play. It is the human's turn!");
    }

    var freeSpaces = new LinkedList<Map<String, Integer>>();
    // get all rows of our game board
    for (int y = 0; y < gameBoard.getSize(); y++) {
      var row = gameBoard.getRow(y);
      // get all spaces inside the row
      for (int x = 0; x < gameBoard.getSize(); x++) {
        // check whether the space is still free
        var space = row.getSpace(x);
        if (space.isFree()) {
          // save the free space to our free spaces list
          freeSpaces.add(Map.of("x", x, "y", y));
        }
      }
    }

    // get random free space from our list
    var randomFreeSpace = freeSpaces.stream().skip(random.nextInt(freeSpaces.size())).findFirst().orElseGet(() -> freeSpaces.get(0));

    // Update the board
    gameBoard.setSpace(randomFreeSpace.get("x"), randomFreeSpace.get("y"), GameMark.CROSS);

    // Saving the game board and output it to the player
    saveGameBoard(gameId, gameBoard);

    return this.statusOutput(gameBoard);
  }

  @GetMapping(value = "display", produces = "text/plain")
  public ResponseEntity<String> display(@RequestParam String gameId) {
    // Loading the game board
    var gameBoard = getGameBoard(gameId);
    if(gameBoard == null) {
      return ResponseEntity.notFound().build();
    }
    return this.statusOutput(gameBoard);
  }

  @GetMapping(value = "create", produces = "text/plain")
  public ResponseEntity<String> create(@RequestParam(defaultValue = "3") int boardSize) {
    if(boardSize < 3 || boardSize > 100) {
      return ResponseEntity.badRequest().body("The boardSize must be between 3 and 100");
    }
    // Loading the game board
    var uuid = UUID.randomUUID().toString();
    final GameBoard gameBoard = new GameBoard(boardSize);
    synchronized (storedGames) {
      storedGames.put(uuid, gameBoard);
    }
    saveGameBoard(uuid, gameBoard);
    return ResponseEntity.ok(uuid);
  }
}
