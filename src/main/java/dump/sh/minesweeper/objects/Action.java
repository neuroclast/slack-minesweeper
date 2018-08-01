package dump.sh.minesweeper.objects;

public class Action {
    private String id, name, text, type, value, style;

    public Action(ActionBuilder actionBuilder) {
        this.name = actionBuilder.name;
        this.text = actionBuilder.text;
        this.type = actionBuilder.type;
        this.value = actionBuilder.value;
        this.style = actionBuilder.style;
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

    public static class ActionBuilder {
        private String name;
        private String text;
        private String type;
        private String value;
        private String style;

        public ActionBuilder name(String val) {
            name = val;
            return this;
        }

        public ActionBuilder text(String val) {
            text = val;
            return this;
        }

        public ActionBuilder type(String val) {
            type = val;
            return this;
        }

        public ActionBuilder value(String val) {
            value = val;
            return this;
        }

        public ActionBuilder style(String val) {
            style = val;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }
}
