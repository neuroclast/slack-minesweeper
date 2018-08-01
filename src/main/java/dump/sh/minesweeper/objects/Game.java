package dump.sh.minesweeper.objects;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int width, height, numMines;
    private int[][] field;

    public Game(int width, int height, int numMines) {
        this.field = new int[width][height];
        this.width = width;
        this.height = height;
        this.numMines = numMines;
    }

    public List<Attachment> getBoardAttachments() {
        List<Attachment> atcList = new ArrayList<>();

        for(int y = 0; y < height; y++) {
            List<Action> actList = new ArrayList<>();

            for(int x = 0; x < width; x++) {
                String text = "\u2007";

                if(field[x][y] > 0) {
                    text = Integer.toString(field[x][y]);
                }

                Action act = new Action.ActionBuilder().name("gameButton").text(text).type("button").value(x + "," + y).build();
                actList.add(act);
            }

            Attachment atc = new Attachment.AttachmentBuilder().attachmentType("default").callbackId("button_click").actions(actList).build();
            atcList.add(atc);
        }

        return atcList;
    }


    public void revealTile(int x, int y) {
        field[x][y] = 5;}
}
