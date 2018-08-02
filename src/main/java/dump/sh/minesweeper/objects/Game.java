package dump.sh.minesweeper.objects;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private List<Point> mines;
    private int width, height, numMines;
    private int[][] field;
    private boolean[][] revealed;
    private boolean generated = false;
    private int scoreFactor = 0;


    /**
     * Initializes a new game state
     * @param width horizontal tiles
     * @param height vertical tiles
     * @param numMines number of mines on board
     */
    public Game(int width, int height, int numMines) {
        this.field = new int[width][height];
        this.width = width;
        this.height = height;
        this.numMines = numMines;
        mines = new ArrayList<>();

        // set all tiles to not-revealed
        this.revealed = new boolean[width][height];

        // calc score factor
        scoreFactor = (int) (10.0 * (double) numMines / ((double) width * (double) height));
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumMines() {
        return numMines;
    }

    public int getScoreFactor() {
        return scoreFactor;
    }

    /**
     * Generates a new game field
     * @param clickX x-coordinate user starts with
     * @param clickY y-coordinate user starts with
     */
    public void generateField(int clickX, int clickY) {
        // place mines randomly in field
        // 'tries' prevents an endless loop in case of bar parameters
        int tries = 0;
        for(int i = 0; i < numMines && tries < numMines * 5; i++) {
            Random rand = new Random();
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            // retry if this spot already has a mine or if it's the starting spot
            if(field[x][y] != 0 || (x == clickX && y == clickY)) {
                i--;
                tries++;
                continue;
            }

            field[x][y] = 9;
            mines.add(new Point(x, y));
        }

        // generate integer distances to surrounding mines
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < height; x++) {
                if(field[x][y] == 9) {
                    continue;
                }

                for(Point p: mines) {
                    double distance = Math.hypot(x-p.x, y-p.y);

                    if(distance < 2) {
                        field[x][y]++;
                    }
                }
            }
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
                String style = "default";
                String text = ":white:";

                if(revealed[x][y]) {
                    if(field[x][y] == 9) {
                        text = ":collision:";
                        style = "danger";
                    }
                    else if(field[x][y] > 0) {
                        text = Integer.toString(field[x][y]) + "\u2000";
                    }
                    else {
                        text = ":white_check_mark:";
                        style = "primary";
                    }
                }

                Action act = new Action.ActionBuilder().name("gameButton").text(text).type("button").value(x + "," + y).style(style).build();
                actList.add(act);
            }

            Attachment atc = new Attachment.AttachmentBuilder().attachmentType("default").callbackId("button_click").actions(actList).build();
            atcList.add(atc);
        }

        return atcList;
    }


    /**
     * Handle user click on tile
     * @param clickX x coordinate
     * @param clickY y coordinate
     * @return int
     */
    public int clickTile(int clickX, int clickY) {
        // do nothing if they click an already revealed tile
        if(revealed[clickX][clickY]) {
            return -2;
        }

        // generate board if first click
        if(!generated) {
            generateField(clickX, clickY);
            generated = true;
        }

        // reveal tile
        revealed[clickX][clickY] = true;

        // check for winning condition
        if(field[clickX][clickY] != 9) {
            int numRevealed = 0;
            int totalTiles = width * height;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < height; x++) {
                    if (revealed[x][y]) {
                        numRevealed++;
                    }
                }
            }

            if(totalTiles - numRevealed == numMines) {
                // winner winner chicken dinner!
                return -1;
            }
        }

        return field[clickX][clickY];
    }
}
