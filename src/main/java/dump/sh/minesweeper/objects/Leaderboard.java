package dump.sh.minesweeper.objects;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;

public class Leaderboard {

    @Id
    public String channelId;

    public Map<String, Integer> scores = new HashMap<>();

    public Leaderboard(String channelId) {
        this.channelId = channelId;
    }
}
