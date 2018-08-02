package dump.sh.minesweeper.controllers;

import com.google.gson.Gson;
import dump.sh.minesweeper.objects.*;
import dump.sh.minesweeper.repositories.LeaderboardRepository;
import dump.sh.minesweeper.services.GameService;
import dump.sh.minesweeper.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MSController {

    @Value("${ACCESS_TOKEN}")
    private String accessToken;

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    @Autowired
    private GameService gameService;

    @Autowired
    private LeaderboardRepository leaderboardRepository;


    /**
     * Handler for slash-command action
     * @param paramMap form-encoded parameters from Slack
     * @return ResponseEntity
     */
    @RequestMapping("/slash-command")
    public ResponseEntity slashCommand(@RequestBody MultiValueMap<String, String> paramMap) {

        String channelId = paramMap.getFirst("channel_id");
        String channelName = paramMap.getFirst("channel_name");
        String text = paramMap.getFirst("text");
        String userId = paramMap.getFirst("user_id");
        String userName = paramMap.getFirst("user_name");

        // check for game end command
        if(text.equalsIgnoreCase("end")) {
            if(!gameService.gameExists(channelId)) {
                return ResponseEntity.ok("No game in progress. Feel free to start one!");
            }
            else {
                gameService.endGame(channelId);

                // get user display name
                User user = getUserInfo(userId);
                String player = userName;
                if(user != null) {
                    player = user.profile.display_name_normalized;
                    if (player.length() == 0) {
                        player = user.profile.real_name_normalized;
                    }
                }

                return ResponseEntity.ok(String.format("Game forcefully ended by *%s*.", player));
            }
        }
        // check for leaderboard request
        else if(text.equalsIgnoreCase("leaderboard")) {
            String msgText = generateLeaderboardText(channelId, channelName);

            // build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            // build message
            Message msg = new Message.MessageBuilder()
                    .text(msgText)
                    .channel(channelId)
                    .build();

            // send response
            HttpEntity<Message> request = new HttpEntity<>(msg, headers);
            RestTemplate rt = new RestTemplate();
            rt.postForObject("https://slack.com/api/chat.postMessage", request, String.class);

            return ResponseEntity.ok().build();
        }
        // check if game does not exist
        else if(!gameService.gameExists(channelId)) {
            try {
                int width = 5;
                int height = 5;
                int mines = 7;

                if(text != null && text.length() > 0) {
                    // get field parameters
                    String parameters[] = text.split(",");

                    if(parameters.length != 3) {
                        return ResponseEntity.ok("Invalid parameters. Should be: `width,height,#mines`");
                    }

                    width = Integer.parseInt(parameters[0].trim());
                    height = Integer.parseInt(parameters[1].trim());
                    mines = Integer.parseInt(parameters[2].trim());
                }

                // limit parameters to comply with slack api and minimum game requirements
                width = Math.min(width, 5);
                width = Math.max(width, 3);
                height = Math.min(height, 10);
                height = Math.max(height, 3);
                mines = Math.min(mines, 49);
                mines = Math.max(mines, 5);

                // start game
                Game game = gameService.startGame(channelId, width, height, mines);
                if (game == null) {
                    return ResponseEntity.ok("Unknown error while trying to start a game! :(");
                }

                // game started! generate board for user
                List<Attachment> atcList = game.getBoardAttachments();

                // build headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);

                // build message
                Message msg = new Message.MessageBuilder()
                        .text("Started new game! Click a tile to reveal. When only bombs are left, you win!")
                        .channel(channelId)
                        .attachments(atcList)
                        .build();

                // send response
                HttpEntity<Message> request = new HttpEntity<>(msg, headers);
                RestTemplate rt = new RestTemplate();
                rt.postForObject("https://slack.com/api/chat.postMessage", request, String.class);

                return ResponseEntity.ok().build();
            }
            catch(Exception e) {
                return ResponseEntity.ok("Unknown error while trying to start a game! :(");
            }
        }
        else {
            return ResponseEntity.ok("Game already in progress!");
        }
    }


    /**
     * Button click handler
     * @param parameters Payload from Slack
     * @return ResponseEntity
     */
    @RequestMapping("/button")
    public ResponseEntity button(@RequestBody String parameters) {

        if(parameters.contains("\"url_verification\"")) {
            // url verification of service
            Gson gson = new Gson();
            Challenge c = gson.fromJson(parameters, Challenge.class);

            return ResponseEntity.ok(c.getChallenge());
        }
        else if(parameters.contains("payload=")) {
            // button click handler
            try {
                String jsonResult = java.net.URLDecoder.decode(parameters.substring(8), "UTF-8");

                // deserialize message
                Gson gson = new Gson();
                InteractiveMessage im = gson.fromJson(jsonResult, InteractiveMessage.class);

                // find game object
                Game game = gameService.getGame(im.getChannel().getId());
                if(game == null) {
                    return ResponseEntity.ok().build();
                }

                // parse button coordinates
                String[] coords = im.getActions().get(0).value.split(",");

                // click tile!
                int clickResult = game.clickTile(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

                if(clickResult == -2) {
                    // do nothing if this tile has already been revealed
                    return ResponseEntity.ok().build();
                }

                // get user display name
                User user = getUserInfo(im.getUser().getId());
                String player = im.getUser().getName();
                if(user != null) {
                    player = user.profile.display_name_normalized;
                    if (player.length() == 0) {
                        player = user.profile.real_name_normalized;
                    }
                }

                // get updated board for response message
                List<Attachment> atcList = game.getBoardAttachments();

                // generate game text status
                String statusText = String.format("Woo! :grin: %s revealed a safe tile at %s.", player, im.getActions().get(0).value);

                // losing status
                if(clickResult == 9) {
                    statusText = String.format("Game over, man! :cry: *%s* hit a bomb at %s. Their score has been reduced by *%d*.", player, im.getActions().get(0).value, game.getScoreFactor());
                    gameService.endGame(im.getChannel().getId());

                    updateLeaderboard(im.getChannel().getId(), user.id, -game.getScoreFactor());
                }
                // winning status
                else if(clickResult == -1) {
                    statusText = String.format("*%s* clicked the last non-bomb. They win! :sunglasses: Their score has been increased by *%d*.", player, game.getScoreFactor());
                    gameService.endGame(im.getChannel().getId());

                    updateLeaderboard(im.getChannel().getId(), user.id, game.getScoreFactor());
                }

                // build message
                Message msg = new Message.MessageBuilder()
                        .text(statusText)
                        .channel(im.getChannel().getId())
                        .build();

                if(clickResult != 9 && clickResult != -1) {
                    msg.setAttachments(atcList);
                }

                return ResponseEntity.ok(msg);
            }
            catch(Exception e) {
                return ResponseEntity.ok("Uh oh! Something went wrong: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Unhandled action! x_x");
    }


    /**
     * Authroization handler for Slack
     * @param code Auth code
     * @return ResponseEntity
     */
    @RequestMapping("/slack/authorize")
    public ResponseEntity authorize(@RequestParam String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", code);        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        RestTemplate rt = new RestTemplate();
        String response = rt.postForObject("https://slack.com/api/oauth.access", entity, String.class);

        return ResponseEntity.ok(response);
    }


    /**
     * Retrieves User object for specified user ID
     * @param userId User ID
     * @return User Object
     */
    User getUserInfo(String userId) {

        // build headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);

        // build GET URL
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString("https://slack.com/api/users.info")
                .queryParam("token", accessToken)
                .queryParam("user", userId);

        // Send request
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

        // good response
        if(response.toString().contains("\"ok\":true")) {
            Gson gson = new Gson();
            UserResponse ur = gson.fromJson(response.getBody(), UserResponse.class);
            return ur.user;
        }

        // bad response
        return null;
    }


    /**
     * Updates leaderboard for given channel/user/game
     * @param channelId Channel ID
     * @param userId User ID
     * @param scoreFactor Game object
     */
    private void updateLeaderboard(String channelId, String userId, int scoreFactor) {
        Leaderboard lb = leaderboardRepository.findByChannelId(channelId);

        // found leaderboard
        if(lb != null) {

            // update existing user
            if (lb.scores.containsKey(userId)) {
                lb.scores.put(userId, lb.scores.get(userId) + scoreFactor);
                leaderboardRepository.save(lb);
            }
            // add new user
            else {
                lb.scores.put(userId, scoreFactor);
                leaderboardRepository.save(lb);
            }
        }
        // create new leaderboard
        else {
            lb = new Leaderboard(channelId);
            lb.scores.put(userId, scoreFactor);
            leaderboardRepository.save(lb);
        }
    }


    /**
     * Generates leaderboard message to send to channel
     * @param channelId Channel ID
     * @param channelName Channel display Name
     * @return String
     */
    private String generateLeaderboardText(String channelId, String channelName) {
        String text = "No leaderboard for this channel yet. Play some games!";

        Leaderboard lb = leaderboardRepository.findByChannelId(channelId);

        // found leaderboard
        if(lb != null) {
            Map<String, Integer> scores = MapUtil.sortByValue(lb.scores);

            text = String.format("Top 5 scores for *#%s*:\n", channelName);

            int count = 0;
            for(Map.Entry<String, Integer> score: scores.entrySet()) {

                // get user info
                User user = getUserInfo(score.getKey());
                String player = score.getKey();
                if(user != null) {
                    player = user.profile.display_name_normalized;
                    if (player.length() == 0) {
                        player = user.profile.real_name_normalized;
                    }
                }

                text += String.format("\t\u2022 %s: %d\n", player, score.getValue());

                // only print the top 5 scores
                count++;
                if(count >= 5) {
                    break;
                }
            }
        }

        return text;
    }
}
