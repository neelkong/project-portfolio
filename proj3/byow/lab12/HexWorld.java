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



    public static TETile[][] world;
    public static TERenderer ter;

    public static void main(String[] args) {
        ter = new TERenderer();
        init();
        for( int x = 0; x < 27; x++) {
            for( int y = 0; y < 27; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        fill(13, 0, 7);
        ter.renderFrame(world);
    }


    public static void init() {
        ter.initialize(27, 27);
        world = new TETile[27][27];
    }

    public static void fill( int x, int y, int size) {
        addHexagon(x, y, size);
        int[][] spaces = positionCalc(x, y, size);
        for( int i = 0; i < spaces.length; i++) {
            if( checkSpace(spaces[i][0], spaces[i][1], size)) {
                fill(spaces[i][0], spaces[i][1], size);
            }
        }
    }
    public static void addHexagon(int posX, int posY, int size) {
        int width = size * 2;
        int height = size * 2;
        int startX = posX - (size / 2);
        printRow(width, startX, posY);
        printRow(width, startX, posY - 1);
        for( int i = 0; width - (2 * i) > size - 1; i++) {
            printRow(width - ( 2 * i), startX + i, posY + i);
            printRow(width - ( 2 * i), startX + i, posY - 1 - i);
        }
    }

    private static int[][] positionCalc(int posX, int posY, int size) {
        int offsetX = size / 2 + 2;
        int offsetY = size / 2;
        int[][] ret = new int[4][2];
        ret[0][0] = posX + offsetX;
        ret[0][1] = posY + offsetY;
        ret[1][0] = posX + offsetX;
        ret[1][1] = posY - offsetY;
        ret[2][0] = posX - offsetX;
        ret[2][1] = posY + offsetY;
        ret[3][0] = posX - offsetX;
        ret[3][1] = posY - offsetY;
        return ret;
    }
    private static boolean checkSpace( int posX, int posY, int size) {
        int width = size * 2;
        int height = size * 2;
        int startX = posX - (size / 2);
        printRow(width, startX, posY);
        printRow(width, startX, posY - 1);
        for( int i = 0; width - (2 * i) > size - 1; i++) {
            boolean clear1 = checkRow(width - ( 2 * i), startX + i, posY + i);
            boolean clear2 = checkRow(width - ( 2 * i), startX + i, posY - 1 - i);
            if( !( clear1 || clear2)) {
                return false;
            }
        }
        return true;
    }
    private static void printRow(int length, int posX, int posY) {
        int start = posX;
        int printed = 0;
        if( length % 2 == 0) {
            start += 1;
        }
        while( printed < length) {
            world[start][posY] = Tileset.FLOWER;
            start += 1;
            printed += 1;
        }
    }
    private static boolean checkRow(int length, int posX, int posY) {
        int start = posX;
        int printed = 0;
        if( length % 2 == 0) {
            start += 1;
        }
        while( printed < length) {
            if( world[start][posY] != Tileset.NOTHING) {
                return false;
            }
            start += 1;
            printed += 1;
        }
        return true;
    }

    private void printSpace(int length, int posX, int posY) {
        for( int i = 0; i < length; i++) {
            world[posX][posY] = Tileset.NOTHING;
        }
    }
}
