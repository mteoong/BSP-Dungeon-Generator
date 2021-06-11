package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    public static void addHexagon(int s, int x, int y, TETile[][] world) {
        for (int i = 0; i < s; i++) {
            int numVals = s + 2*i;
            printLayer(s, numVals, x, y, world);
            y++;
        }
        for (int i = s-1; i >= 0; i--) {
            int numVals = s + 2*i;
            printLayer(s, numVals, x, y, world);
            y++;
        }
    }

    private static void printLayer(int side, int blocks, int x, int y, TETile[][] world) {
        int blanks = ((side + 2 * (side - 1)) - blocks) / 2;
        x += blanks;
        for (int i = 0; i < blocks; i++) {
            world[x][y] = Tileset.WALL;
            x++;
        }
    }


    public static void tesselationNineteen(int s, int x, int y, TETile[][] world) {
        int addX = 2*s - 1;
        printTesselation(s, 3, x, y + 2*s, world);
        //3, x, y-2s-1,
        printTesselation(s, 4, x + addX, y+s, world);
        printTesselation(s, 5, x + 2*addX, y, world);
        printTesselation(s, 4, x + 3*addX, y+s, world);
        printTesselation(s, 3, x + 4*addX, y + 2*s, world);

    }


    private static void printTesselation(int s, int numHexes, int x, int y, TETile[][] world) {
        for (int i = 0; i < numHexes; i++) {
            addHexagon(s, x, y, world);
            y += 2 * s;
        }

    }





    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] curHex = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                curHex[x][y] = Tileset.NOTHING;
            }
        }
        //addHexagon(4, 0, 0, curHex);
        tesselationNineteen(5, 0, 0, curHex);

        ter.renderFrame(curHex);
    }

}
