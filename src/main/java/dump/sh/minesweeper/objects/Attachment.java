package dump.sh.minesweeper.objects;

import java.util.List;

public class Attachment {
    private String id, name, text, callback_id, type, value, style, fallback;
    private List<Action> actions;

    public Attachment(AttachmentBuilder attachmentBuilder) {
        this.text = attachmentBuilder.text;
        this.type = attachmentBuilder.type;
        this.actions = attachmentBuilder.actions;
        this.callback_id = attachmentBuilder.callback_id;
    }

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCallback_id() {
        return callback_id;
    }

    public void setCallback_id(String callback_id) {
        this.callback_id = callback_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public static class AttachmentBuilder {
        private String text;
        private String type;
        private List<Action> actions;
        private String callback_id;

        public AttachmentBuilder text(String val) {
            text = val;
            return this;
        }

        public AttachmentBuilder attachmentType(String val) {
            type = val;
            return this;
        }

        public AttachmentBuilder actions(List<Action> val) {
            actions = val;
            return this;
        }

        public AttachmentBuilder callbackId(String val) {
            callback_id = val;
            return this;
        }

        public Attachment build() {
            return new Attachment(this);
        }
    }
}