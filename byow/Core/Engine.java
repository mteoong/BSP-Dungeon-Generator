package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 100;
    public static final int HEIGHT = 60;
    //For map building
    int levelsLow = 3;
    int levelsHigh = 5;
    static Random r = new Random();
    TETile[][] world;
    Cell root;
    //For the Avatar
    int avatarX = 0;
    int avatarY = 0;
    //For Saving and Loading
    String keysPressed;
    String todo;
    File CWD = new File(System.getProperty("user.dir"));
    File saveFile = Paths.get(CWD.getPath(), ".saveFile.txt").toFile();
    boolean cont;
    boolean load = false;
    boolean replay = false;
    boolean stringInput = false;

    public Engine() {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        root = new Cell(0, 0, HEIGHT, WIDTH, null, null);

        // initialize tiles
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        keysPressed = "";
        todo = "";
    }
    /**
    public static void main(String[] args) {
        Engine x = new Engine();
        x.interactWithInputString("n7193300625454684331saaawasdaawd:q");
        x = new Engine();
        x.interactWithInputString("lwsd:q");
        System.out.println(x.toString());
    }
    */
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void startGame() {
        ter.initialize(WIDTH, HEIGHT + 4);

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, (HEIGHT + 3) * 8 / 10,  "CS 61B THE GAME");
        font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, (HEIGHT + 3) * 5 / 10, "New Game (N)");
        StdDraw.text(WIDTH / 2, (HEIGHT + 3) * 4 / 10, "Load Game (L)");
        StdDraw.text(WIDTH / 2, (HEIGHT + 3) * 3 / 10, "Replay (R)");
        StdDraw.text(WIDTH / 2, (HEIGHT + 3) * 2 / 10, "Quit (Q)");
        StdDraw.show();
    }

    public void interactWithKeyboard() {
        cont = true;
        //Main Menu
        if (!stringInput) {
            startGame();
        }
        menu();
        //In Game
        String key;
        boolean fromToDo = false;

        while (cont) {
            if (!stringInput) {
                hud((int) StdDraw.mouseX(), (int) StdDraw.mouseY());
            }

            if (!todo.equals("")) {
                key = todo.substring(0, 1);
                todo = todo.substring(1);
                fromToDo = true;
            } else if (stringInput) {
                break;
            } else if (StdDraw.hasNextKeyTyped()) {
                key = Character.toString(StdDraw.nextKeyTyped());
                fromToDo = false;
            } else {
                ter.renderFrame(world);
                continue;
            }
            keysPressed += key;

            world[avatarX][avatarY] = Tileset.NOTHING;
            switch (key) {
                case "W":
                case "w":
                    if (!world[avatarX][avatarY + 1].equals(Tileset.WALL)) {
                        avatarY += 1;
                    }
                    break;
                case "A":
                case "a":
                    if (!world[avatarX - 1][avatarY].equals(Tileset.WALL)) {
                        avatarX -= 1;
                    }
                    break;
                case "S":
                case "s":
                    if (!world[avatarX][avatarY - 1].equals(Tileset.WALL)) {
                        avatarY -= 1;
                    }
                    break;
                case "D":
                case "d":
                    if (!world[avatarX + 1][avatarY].equals(Tileset.WALL)) {
                        avatarX += 1;
                    }
                    break;
                case ":":
                    break;
                case "q":
                case "Q":
                    if (keysPressed.charAt(keysPressed.length() - 2) == ':') {
                        saveAndQuit();
                    }
                    break;
                default:
            }
            world[avatarX][avatarY] = Tileset.AVATAR;
            if (!stringInput) {
                if (fromToDo) {
                    if (replay) {
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ter.renderFrame(world);
                    }
                } else {
                    ter.renderFrame(world);
                }
            }
        }
        if (!stringInput) {
            System.exit(0);
        }
    }

    public void menu() {
        String key;
        while (true) { //N, L, Q
            if (!todo.equals("")) {
                key = todo.substring(0, 1);
                todo = todo.substring(1);
            } else if (stringInput) {
                break;
            } else if (StdDraw.hasNextKeyTyped()) {
                key = Character.toString(StdDraw.nextKeyTyped());
            } else {
                continue;
            }

            switch (key) {
                case "n":
                case "N":
                    keysPressed += key;
                    newGameScreen("");
                    return;
                case "l":
                case "L":
                    loadGame();
                    return;
                case "q":
                case "Q":
                    quit();
                    return;
                case "r":
                case "R":
                    replay();
                    return;
                default:
            }
        }
    }

    public void newGameScreen(String s) {
        if (!stringInput) {
            StdDraw.clear(Color.BLACK);
            StdDraw.text(WIDTH / 2, HEIGHT * 6 / 10, "Enter a random seed: ");
            StdDraw.text(WIDTH / 2, HEIGHT * 5 / 10, s);
            StdDraw.text(WIDTH / 2, HEIGHT * 4 / 10, "(S to confirm)");
            if (!load && !replay) {
                StdDraw.show();
            }
        }

        String key;
        while (true) {
            if (!todo.equals("")) {
                key = todo.substring(0, 1);
                todo = todo.substring(1);
            } else if (stringInput) {
                break;
            } else if (StdDraw.hasNextKeyTyped()) {
                key = Character.toString(StdDraw.nextKeyTyped());
            } else {
                continue;
            }
            keysPressed += key;

            if (key.equals("S") || key.equals("s")) {
                createGame(s);
                return;
            }
            newGameScreen(s + key);
            return;
        }
    }

    public void createGame(String s) {
        r = new Random(s.hashCode());
        int levels = r.nextInt(levelsHigh - levelsLow) + levelsLow;
        bspRecursive(levels);
        connect(root);
        makeGuy();
        if (!stringInput) {
            ter.initialize(WIDTH, HEIGHT + 4);
            ter.renderFrame(world);
        }
    }

    public void loadGame() {
        String contents = "";
        if (!saveFile.exists()) {
            quit();
            return;
        }
        try {
            contents = Files.readString(saveFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        todo = contents.substring(0, contents.length() - 2) + todo;
        load = true;
        menu();
    }

    public void quit() {
        cont = false;
    }

    public void saveAndQuit() {
        if (!saveFile.exists()) {
            saveFile.delete();
        }
        try {
            saveFile.createNewFile();
        } catch (IOException excp) {
            excp.printStackTrace();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.write(keysPressed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        quit();
    }

    public void replay() {
        String contents = "";
        if (!saveFile.exists()) {
            quit();
            return;
        }
        try {
            contents = Files.readString(saveFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        replay = true;
        todo = contents.substring(0, contents.length() - 2) + todo;
        menu();
    }

    public void hud(int mouseX, int mouseY) {
        StdDraw.setPenColor(StdDraw.WHITE);
        if (mouseY >= HEIGHT) {
            return;
        }

        if (world[mouseX][mouseY].equals(Tileset.NOTHING)) {
            StdDraw.text(WIDTH / 2, HEIGHT + 2,
                    "Block: NOTHING  ||  Date: " + new Date().toString());
        } else if (world[mouseX][mouseY].equals(Tileset.WALL)) {
            StdDraw.text(WIDTH / 2, HEIGHT + 2,
                    "Block: WALL  ||  Date: "  + new Date().toString());
        } else if (world[mouseX][mouseY].equals(Tileset.AVATAR)) {
            StdDraw.text(WIDTH / 2, HEIGHT + 2,
                    "Block: AVATAR ||  Date: " + new Date().toString());
        }

        StdDraw.show();
    }



    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        todo = input;
        stringInput = true;
        interactWithKeyboard();
        return world;
    }

    public void makeGuy() {
        if (avatarX == 0 && avatarY == 0) {
            ArrayList<Integer> coord = randomRoom(root);
            avatarX = coord.get(0);
            avatarY = coord.get(1);
        }
        world[avatarX][avatarY] = Tileset.AVATAR;
    }

    public ArrayList<Integer> randomRoom(Cell c) {
        if (c.left == null && c.right == null) {
            ArrayList<Integer> coord = new ArrayList();
            coord.add(Math.max(1, c.room.roomX + c.room.roomWidth / 2));
            coord.add(Math.max(1, c.room.roomY + c.room.roomHeight / 2));
            return coord;
        } else {
            if (c.left.room == null) {
                return randomRoom(c.right);
            } else if (c.right.room == null) {
                return randomRoom(c.left);
            } else if (r.nextBoolean()) {
                return randomRoom(c.left);
            } else {
                return randomRoom(c.right);
            }
        }
    }

    public void bspRecursive(int levels) {
        bsp(root, levels);
    }

    public void bsp(Cell cell, int n) {
        if (n == 0) {
            createRoom(cell);
            return;
        }
        int splitBound = r.nextInt(100) + 125;
        //splitBound is a number between 1.25 and 2.25. Experiment with values for bound later
        // determine direction of split
        // if the width is >splitPos% larger than height, we split vertically
        // else we split horizontally
        int splitPos;
        int perc = r.nextInt(40) + 30; //Percentage between 30 and 70
        if (cell.width > cell.height && (cell.width / cell.height) >= splitBound / 100) {
            //split vertically somewhere between 30 and 70% of the width
            splitPos = (perc * cell.width) / 100;
            cell.left = new Cell(cell.x, cell.y, cell.height, splitPos, null, null);
            cell.right = new Cell(cell.x + splitPos + 1, cell.y,
                    cell.height, cell.width - splitPos - 1, null, null);
        } else {
            //split horizontally somewhere between 30 and 70% of the height
            splitPos = (perc * cell.height) / 100;
            cell.left = new Cell(cell.x, cell.y, splitPos, cell.width, null, null);
            cell.right = new Cell(cell.x, cell.y + splitPos + 1,
                    cell.height - splitPos - 1, cell.width, null, null);
        }
        if (cell.left.width > 5 && cell.left.height > 5) {
            bsp(cell.left, n - 1);
        }
        if (cell.right.width > 5 && cell.right.height > 5) {
            bsp(cell.right, n - 1);
        }
    }

    public void createRoom(Cell c) {
        if (r.nextInt(100) < 20) {
            return;
        }
        int xCoord = c.x + 1 + ((r.nextInt(40) * (c.width)) / 100);
        int yCoord = c.y + 1 + ((r.nextInt(40) * (c.height)) / 100);
        int roomWidth = (((r.nextInt(40) + 60) * (c.x + c.width - xCoord)) / 100) - 1;
        int roomHeight = (((r.nextInt(40) + 60) * (c.y + c.height - yCoord)) / 100) - 1;
        if (roomWidth < 3) {
            roomWidth = 3;
        }
        if (roomHeight < 3) {
            roomHeight = 3;
        }

        c.createRoom(xCoord, yCoord, roomWidth, roomHeight);

        for (int j = xCoord; j < xCoord + roomWidth; j++) {
            world[j][yCoord] = Tileset.WALL;
            world[j][yCoord + roomHeight - 1] = Tileset.WALL;
        }
        for (int i = yCoord; i < yCoord + roomHeight; i++) {
            world[xCoord][i] = Tileset.WALL;
            world[xCoord + roomWidth - 1][i] = Tileset.WALL;
        }
    }

    private void connect(Cell cell) {
        if (cell == null) {
            return;
        }
        connect(cell.left);
        connect(cell.right);
        setBoundary(cell);

        if (cell.left == null || cell.right == null) {
            return;
        }

        Cell.Room left = cell.left.room;
        Cell.Room right = cell.right.room;

        if (left == null || right == null) {
            return;
        }

        if (cell.left.x == cell.right.x) { //Vertical Connect
            int y = cell.right.y;
            int xMin = Math.max(left.roomX, right.roomX);
            int xMax = Math.min(left.roomX + left.roomWidth, right.roomX + right.roomWidth);
            int x;
            if (xMin > xMax) {
                if (left.roomX > right.roomX) {
                    //left is to the right
                    int length = r.nextInt(left.roomWidth - 1)
                            + left.roomX - (right.roomX + right.roomWidth - 1);
                    horizontalExtension(right, length);
                    x = length + (right.roomX + right.roomWidth - 1);
                } else {
                    int length = r.nextInt(right.roomWidth - 1)
                            + right.roomX - (left.roomX + left.roomWidth - 1);
                    horizontalExtension(left, length);
                    x = length + (left.roomX + left.roomWidth - 1);
                }
            } else if (xMin == xMax) {
                x = xMin;
            } else {
                x = r.nextInt(xMax - xMin) + xMin;
            }

            verticalConnect(x, y, 1);
            verticalConnect(x, y, -1);
        } else if (cell.left.y == cell.right.y) { //Horizontal Connect
            int x = cell.right.x;
            int yMin = Math.max(left.roomY, right.roomY);
            int yMax = Math.min(left.roomY + left.roomHeight, right.roomY + right.roomHeight);
            int y;
            if (yMin > yMax) {
                if (left.roomY > right.roomY) {
                    //left is on top
                    int length = r.nextInt(left.roomHeight - 1)
                            + left.roomY - (right.roomY + right.roomHeight - 1);
                    verticalExtension(right, length);
                    y = length + (right.roomY + right.roomHeight - 1);
                } else {
                    int length = r.nextInt(right.roomHeight - 1)
                            + right.roomY - (left.roomY + left.roomHeight - 1);
                    verticalExtension(left, length);
                    y = length + (left.roomY + left.roomHeight - 1);
                }
            } else if (yMax == yMin) {
                y = yMin;
            } else {
                y = r.nextInt(yMax - yMin) + yMin;
            }

            horizontalConnect(x, y, -1);
            horizontalConnect(x, y, 1);
        }
    }

    public void verticalExtension(Cell.Room room, int length) {
        int x = r.nextInt(room.roomWidth - 2) + room.roomX + 1;
        int y = room.roomY + room.roomHeight;
        world[x][y - 1] = Tileset.NOTHING;
        for (int i = 0; i <= length; i++) { //length is negative
            world[x + 1][y + i] = Tileset.WALL;
            world[x - 1][y + i] = Tileset.WALL;
        }
        world[x][y + length] = Tileset.WALL;
    }

    public void horizontalExtension(Cell.Room room, int length) {
        int y = r.nextInt(room.roomHeight - 2) + room.roomY + 1;
        int x = room.roomX + room.roomWidth;
        world[x - 1][y] = Tileset.NOTHING;
        for (int i  = 0; i <= length; i++) {
            world[x + i][y + 1] = Tileset.WALL;
            world[x + i][y - 1] = Tileset.WALL;
        }
        world[x + length][y] = Tileset.WALL;
    }

    //right and left
    public void horizontalConnect(int x, int y, int dir) {
        /** Cases
         * 1. All three miss wall.
         * 2. Only bottom hits wall.
         * 3. Bottom and mid hit wall.
         * 4. All three hit wall.
         * 5. Top and mid hit wall.
         * 6. Only top hits wall.
         * */
        world[x][y + 1] = Tileset.WALL;
        world[x][y - 1] = Tileset.WALL;

        while (true) {
            boolean top = world[x + dir][y + 1].equals(Tileset.WALL);
            boolean mid = world[x + dir][y].equals(Tileset.WALL);
            boolean bot = world[x + dir][y - 1].equals(Tileset.WALL);
            if (bot) {
                if (mid) {
                    if (top) { //case 4
                        world[x + dir][y] = Tileset.NOTHING;
                        world[x + 2 * dir][y] = Tileset.NOTHING;
                        break;
                    }
                    //case 3
                    world[x + dir][y] = Tileset.NOTHING;
                    world[x + 2 * dir][y] = Tileset.NOTHING;
                    world[x + dir][y + 1] = Tileset.WALL;
                    if (world[x + 2 * dir][y + 1].equals(Tileset.WALL)) {
                        break;
                    }
                    world[x + 2 * dir][y + 1] = Tileset.WALL;
                    world[x + 3 * dir][y + 1] = Tileset.WALL;
                    break;
                }
                //case 2
                world[x + dir][y + 1] = Tileset.WALL;
                if (world[x + 2 * dir][y + 1].equals(Tileset.WALL)) {
                    world[x + 2 * dir][y] = Tileset.NOTHING;
                } else {
                    world[x + 2 * dir][y] = Tileset.NOTHING;
                    world[x + 3 * dir][y] = Tileset.WALL;
                    world[x + 2 * dir][y + 1] = Tileset.WALL;
                    world[x + 3 * dir][y + 1] = Tileset.WALL;
                    world[x + 2 * dir][y - 1] = Tileset.NOTHING;
                }
                break;
            } else if (top) {
                if (mid) { //case 5
                    world[x + dir][y] = Tileset.NOTHING;
                    world[x + 2 * dir][y] = Tileset.NOTHING;
                    world[x + dir][y - 1] = Tileset.WALL;
                    if (world[x + 2 * dir][y - 1].equals(Tileset.WALL)) {
                        break;
                    } else {
                        world[x + 2 * dir][y - 1] = Tileset.WALL;
                        world[x + 3 * dir][y - 1] = Tileset.WALL;
                    }
                    break;
                }
                // case 6
                world[x + dir][y - 1] = Tileset.WALL;
                if (world[x + 2 * dir][y - 1].equals(Tileset.WALL)) {
                    world[x + 2 * dir][y] = Tileset.NOTHING;
                } else {
                    world[x + 2 * dir][y] = Tileset.NOTHING;
                    world[x + 3 * dir][y] = Tileset.WALL;
                    world[x + 2 * dir][y - 1] = Tileset.WALL;
                    world[x + 3 * dir][y - 1] = Tileset.WALL;
                    world[x + 2 * dir][y + 1] = Tileset.NOTHING;
                }
                break;
            } else { // case 1
                world[x + dir][y + 1] = Tileset.WALL;
                world[x + dir][y - 1] = Tileset.WALL;
                x = x + dir;
            }
        }
    }

    public void verticalConnect(int x, int y, int dir) {
        /** Cases
         * 1. All three miss wall.
         * 2. Only bottom hits wall.
         * 3. Bottom and mid hit wall.
         * 4. All three hit wall.
         * 5. Top and mid hit wall.
         * 6. Only top hits wall.
         * */
        world[x + 1][y] = Tileset.WALL;
        world[x - 1][y] = Tileset.WALL;

        while (true) {
            boolean top = world[x + 1][y + dir].equals(Tileset.WALL);
            boolean mid = world[x][y + dir].equals(Tileset.WALL);
            boolean bot = world[x - 1][y + dir].equals(Tileset.WALL);
            if (bot) {
                if (mid) {
                    if (top) { //case 4
                        world[x][y + dir] = Tileset.NOTHING;
                        world[x][y + 2 * dir] = Tileset.NOTHING;
                        break;
                    }
                    //case 3
                    world[x][y + dir] = Tileset.NOTHING;
                    world[x][y + 2 * dir] = Tileset.NOTHING;
                    world[x + 1][y + dir] = Tileset.WALL;
                    if (world[x + 1][y + 2 * dir].equals(Tileset.WALL)) {
                        break;
                    }
                    world[x + 1][y + 2 * dir] = Tileset.WALL;
                    world[x + 1][y + 3 * dir] = Tileset.WALL;
                    break;
                }
                //case 2
                world[x + 1][y + dir] = Tileset.WALL;
                if (world[x + 1][y + 2 * dir].equals(Tileset.WALL)) {
                    world[x][y + 2 * dir] = Tileset.NOTHING;
                } else {
                    world[x][y + 2 * dir] = Tileset.NOTHING;
                    world[x][y + 3 * dir] = Tileset.WALL;
                    world[x + 1][y + 2 * dir] = Tileset.WALL;
                    world[x + 1][y + 3 * dir] = Tileset.WALL;
                    world[x - 1][y + 2 * dir] = Tileset.NOTHING;
                }
                break;
            } else if (top) {
                if (mid) { //case 5
                    world[x][y + dir] = Tileset.NOTHING;
                    world[x][y + 2 * dir] = Tileset.NOTHING;
                    world[x - 1][y + dir] = Tileset.WALL;
                    if (world[x - 1][y + 2 * dir].equals(Tileset.WALL)) {
                        break;
                    } else {
                        world[x - 1][y + 2 * dir] = Tileset.WALL;
                        world[x - 1][y + 3 * dir] = Tileset.WALL;
                    }
                    break;
                }
                // case 6
                world[x - 1][y + dir] = Tileset.WALL;
                if (world[x - 1][y + 2 * dir].equals(Tileset.WALL)) {
                    world[x][y + 2 * dir] = Tileset.NOTHING;
                } else {
                    world[x][y + 3 * dir] = Tileset.WALL;
                    world[x - 1][y + 2 * dir] = Tileset.WALL;
                    world[x - 1][y + 3 * dir] = Tileset.WALL;
                    world[x + 1][y + 2 * dir] = Tileset.NOTHING;
                }
                break;

            } else { // case 1
                world[x + 1][y + dir] = Tileset.WALL;
                world[x - 1][y + dir] = Tileset.WALL;
                y += dir;
            }
        }
    }

    public void setBoundary(Cell cell) {
        if (cell.left == null && cell.right == null) {
            return;
        }
        Cell.Room left = cell.left.room;
        Cell.Room right = cell.right.room;

        if (left == null) {
            cell.room = cell.right.room;
        } else if (right == null) {
            cell.room = cell.left.room;
        } else {
            int xMin = Math.min(left.roomX, right.roomX);
            int yMin = Math.min(left.roomY, right.roomY);
            int xMax = Math.max(left.roomX + left.roomWidth, right.roomX + right.roomWidth);
            int yMax = Math.max(left.roomY + left.roomHeight, right.roomY + right.roomHeight);

            cell.createRoom(xMin, yMin, xMax - xMin, yMax - yMin);
        }
    }

}

