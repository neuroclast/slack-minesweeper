package dump.sh.minesweeper.objects;

import java.util.*;

import org.springframework.data.annotation.Id;

public class Leaderboard {

    @Id
    public String channelId;

    public Map<String, UserScore> scores = new HashMap<>();

    public Leaderboard(String channelId) {
        this.channelId = channelId;
    }

    public List<UserScore> getSortedScores() {
        List<UserScore> result = new ArrayList<>(scores.values());
        result.sort(new Comparator<UserScore>() {
            @Override
            public int compare(UserScore o1, UserScore o2) {
                if(o1.score == o2.score) {
                    return 0;
                }

                return o1.score > o2.score ? 1 : -1;
            }
        });

        return result;
    }
}
