package dump.sh.minesweeper.objects;

import java.util.List;

public class InteractiveMessage {
    private String type, callback_id, action_ts, message_ts, attachment_id, token, response_url, trigger_id;
    private List<InteractiveAction> actions;
    private Team team;
    private Channel channel;
    private User user;
    private boolean is_app_unfurl;
    private Message original_message;

    public String getResponse_url() {
        return response_url;
    }

    public void setResponse_url(String response_url) {
        this.response_url = response_url;
    }

    public String getTrigger_id() {
        return trigger_id;
    }

    public void setTrigger_id(String trigger_id) {
        this.trigger_id = trigger_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCallback_id() {
        return callback_id;
    }

    public void setCallback_id(String callback_id) {
        this.callback_id = callback_id;
    }

    public String getAction_ts() {
        return action_ts;
    }

    public void setAction_ts(String action_ts) {
        this.action_ts = action_ts;
    }

    public String getMessage_ts() {
        return message_ts;
    }

    public void setMessage_ts(String message_ts) {
        this.message_ts = message_ts;
    }

    public String getAttachment_id() {
        return attachment_id;
    }

    public void setAttachment_id(String attachment_id) {
        this.attachment_id = attachment_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<InteractiveAction> getActions() {
        return actions;
    }

    public void setActions(List<InteractiveAction> actions) {
        this.actions = actions;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isIs_app_unfurl() {
        return is_app_unfurl;
    }

    public void setIs_app_unfurl(boolean is_app_unfurl) {
        this.is_app_unfurl = is_app_unfurl;
    }

    public Message getOriginal_message() {
        return original_message;
    }

    public void setOriginal_message(Message original_message) {
        this.original_message = original_message;
    }

    public class InteractiveAction {
        public String name, type, value;
    }

    public class Team {
        private String id, domain;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
    }

    public class Channel {
        private String id, name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class User {
        private String id, name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
