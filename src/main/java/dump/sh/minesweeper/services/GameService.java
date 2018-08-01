package dump.sh.minesweeper.services;

import dump.sh.minesweeper.objects.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores games and their objects
 */
@Service
public class GameService {
    private Map<String, Game> games = new HashMap<>();


    /**
     * Checks if a game has already started for a channelId
     * @param channelId Channel ID to check
     * @return boolean
     */
    public boolean gameExists(String channelId) {
        return games.containsKey(channelId);
    }


    /**
     * Starts a new game for a channel
     * @param channelId Channel to start game for
     * @param width board width
     * @param height board height
     * @param numMines number of mines on board
     * @return Game object
     */
    public Game startGame(String channelId, int width, int height, int numMines) {
        if(gameExists(channelId)) {
            return null;
        }

        Game game = new Game(width, height, numMines);
        games.put(channelId, game);

        return game;
    }


    /**
     * Ends a game
     * @param channelId Channel ID
     */
    public void endGame(String channelId) {
        if(gameExists(channelId)) {
            games.remove(channelId);
        }
    }


    /**
     * Retrieves game object for channel
     * @param channelId Channel ID
     * @return Game object or null
     */
    public Game getGame(String channelId) {
        return games.getOrDefault(channelId, null);
    }
}
