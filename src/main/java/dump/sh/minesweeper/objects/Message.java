package dump.sh.minesweeper.objects;

import java.util.ArrayList;
import java.util.List;

public final class Message {
    private ResponseType response_type;
    private String text, token, channel, username, bot_id, type, subtype, ts;
    private List<Attachment> attachments;

    public Message(MessageBuilder messageBuilder) {
        this.response_type = messageBuilder.response_type;
        this.text = messageBuilder.text;
        this.attachments = messageBuilder.attachments;
        this.token = messageBuilder.token;
        this.channel = messageBuilder.channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public ResponseType getResponse_type() {
        return response_type;
    }

    public void setResponse_type(ResponseType response_type) {
        this.response_type = response_type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBot_id() {
        return bot_id;
    }

    public void setBot_id(String bot_id) {
        this.bot_id = bot_id;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public static class MessageBuilder {
        private ResponseType response_type;
        private String text;
        private String token;
        private String channel;
        private List<Attachment> attachments = new ArrayList<>();

        public final MessageBuilder responseType(ResponseType val) {
            response_type = val;
            return this;
        }

        public MessageBuilder text(String val) {
            text = val;
            return this;
        }

        public MessageBuilder token(String val) {
            token = val;
            return this;
        }

        public MessageBuilder channel(String val) {
            channel = val;
            return this;
        }

        public MessageBuilder attachments(List<Attachment> val) {
            attachments = val;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }

    public enum ResponseType {
        ephemeral, in_channel
    }
}
