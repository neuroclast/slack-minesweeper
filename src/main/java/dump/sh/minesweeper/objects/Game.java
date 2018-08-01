package dump.sh.minesweeper.objects;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private int width, height, numMines;
    private int[][] field;
    private boolean[][] revealed;


    /**
     * Initializes a new game
     * @param width horizontal tiles
     * @param height vertical tiles
     * @param numMines number of mines on board
     */
    public Game(int width, int height, int numMines) {
        this.field = new int[width][height];
        this.width = width;
        this.height = height;
        this.numMines = numMines;

        // set all tiles to not-revealed
        this.revealed = new boolean[width][height];

        // place mines randomly in field
        // 'tries' prevents an endless loop in case of bar parameters
        int tries = 0;
        for(int i = 0; i < numMines && tries < numMines * 5; i++) {
            Random rand = new Random();
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            // retry if this spot already has a mine
            if(field[x][y] != 0) {
                i--;
                tries++;
                continue;
            }

            field[x][y] = 9;
        }
    }


    /**
     * Generates attachment list for board
     * @return List<Attachment>
     */
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


    /**
     * Handle user click on tile
     * @param x x coordinate
     * @param y y coordinate
     */
    public void clickTile(int x, int y) {
        field[x][y] = 5;
    }
}
