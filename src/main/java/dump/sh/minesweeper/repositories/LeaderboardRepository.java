package dump.sh.minesweeper.repositories;

import dump.sh.minesweeper.objects.Leaderboard;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeaderboardRepository extends MongoRepository<Leaderboard, String> {
    Leaderboard findByChannelId(String channelId);
}
