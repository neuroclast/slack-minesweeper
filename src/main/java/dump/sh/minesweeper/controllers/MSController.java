package dump.sh.minesweeper.controllers;

import com.google.gson.Gson;
import dump.sh.minesweeper.objects.*;
import dump.sh.minesweeper.services.GameService;
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

@RestController
@RequestMapping("/minesweeper")
public class MSController {

    @Value("${ACCESS_TOKEN}")
    private String accessToken;

    @Autowired
    private GameService gameService;


    /**
     * Handler for slash-command action
     * @param paramMap form-encoded parameters from Slack
     * @return ResponseEntity
     */
    @RequestMapping("/slash-command")
    public ResponseEntity slashCommand(@RequestBody MultiValueMap<String, String> paramMap) {
        System.out.println("slash-command called");

        String channelId = paramMap.getFirst("channel_id");
        String text = paramMap.getFirst("text");

        // check if game does not exist
        if(!gameService.gameExists(channelId)) {
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

                // limit parameters to comply with slack api
                width = Math.min(width, 5);
                height = Math.min(height, 10);
                mines = Math.min(mines, 49);

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
            // check for game end command
            if(text.equalsIgnoreCase("end")) {
                gameService.endGame(channelId);
                return ResponseEntity.ok("Game ended.");
            }

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
        System.out.println("button called");

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
                    return ResponseEntity.ok("Uh oh! Something went wrong! :(");
                }

                // parse button coordinates
                String[] coords = im.getActions().get(0).value.split(",");
                game.clickTile(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

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

                // build message
                Message msg = new Message.MessageBuilder()
                        .text("Last move by " + player + " at " + im.getActions().get(0).value + ".")
                        .channel(im.getChannel().getId())
                        .attachments(atcList)
                        .build();

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
        System.out.println("authorize called");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id","335547207316.406120316919");
        map.add("client_secret","34430ac022923cddbe1e49585af8b3f1");
        map.add("code", code);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

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

}
