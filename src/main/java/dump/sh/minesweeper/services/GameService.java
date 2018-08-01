package dump.sh.minesweeper.services;

import dump.sh.minesweeper.objects.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameService {
    private Map<String, Game> games = new HashMap<>();

    public boolean gameExists(String channelId) {
        return games.containsKey(channelId);
    }

    public Game startGame(String channelId, int width, int height, int numMines) {
        if(gameExists(channelId)) {
            return null;
        }

        Game game = new Game(width, height, numMines);
        games.put(channelId, game);

        return game;
    }

    public void endGame(String channelId) {
        if(gameExists(channelId)) {
            games.remove(channelId);
        }
    }

    public Game getGame(String channelId) {
        return games.getOrDefault(channelId, null);
    }
}
