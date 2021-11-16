package gitlet;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Random;

public class WorldGen {
     package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Random;

    public class WorldGeneration {
        /**
         * An ArrayDeque that keeps track of all the Rooms generated in the world.
         */
        public static final ArrayDeque<Room> ROOMS = new ArrayDeque<Room>();
        public static final File CWD = new File(System.getProperty("user.dir"));
        public static File save = new File(CWD, "worldSave.txt");

        public static void main(String[] args) {

        }
        public static void saveWorld(Player p1) {
            String contents = Persistence.readContentsAsString(save);
            Persistence.writeContents(save, "n" + contents + "s" + p1.getMovements() + p1.getAvatar());
        }

        public static ArrayDeque<Room> getRoom() {
            return ROOMS;
        }

        /**
         * A method that creates a new Random object with the given seed.
         * The method then uses the Random object to randomly generate the number
         * of rooms to be generated. Then, generateWorld calls on helper methods to
         * build rooms and hallways and returns an altered world.
         *
         * @param seed
         * @param world
         * @return
         */
        public static TETile[][] generateWorld(long seed, TETile[][] world) {
            Random dice = new Random(seed);
            String input = String.valueOf(seed);
            Persistence.writeContents(save, input);
            int numStructures = RandomUtils.uniform(dice, 8, 15);
            while (numStructures > 0) {
                addRoom(world, dice);
                numStructures -= 1;
            }
            connectAll(world, dice);
            return world;
        }

        /**
         * ConnectAll goes through every room generated in the world and tracked by
         * the rooms ArrayDeque and connects them with the addHallway object. *.
         *
         * @param tiles
         * @param dice
         */
        private static void connectAll(TETile[][] tiles, Random dice) {
            Iterator iter = ROOMS.iterator();
            Room curr = (Room) iter.next();
            while (iter.hasNext()) {
                Room second = (Room) iter.next();
                addHallway(tiles, curr, second);
                curr = second;
            }
        }

        private static void addHallway(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            if (coreY1 - coreY2 < 0 && coreX1 == coreX2) {
                buildNorth(world, main, other);
            } else if (coreY1 - coreY2 < 0 && coreX1 - coreX2 < 0) {
                buildNorthEast(world, main, other);
            } else if (coreX1 - coreX2 < 0 && coreY1 == coreY2) {
                buildEast(world, main, other);
            } else if (coreX1 - coreX2 < 0 && coreY1 - coreY2 > 0) {
                buildSouthEast(world, main, other);
            } else if (coreY1 - coreY2 > 0 && coreX1 == coreX2) {
                buildSouth(world, main, other);
            } else if (coreY1 - coreY2 > 0 && coreX1 - coreX2 > 0) {
                buildSouthWest(world, main, other);
            } else if (coreX1 - coreX2 > 0  && coreY1 == coreY2) {
                buildWest(world, main, other);
            } else if (coreX1 - coreX2 > 0 && coreY1 - coreY2 < 0) {
                buildNorthWest(world, main, other);
            }
        }

        private static void buildNorth(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            int xProgress = main.getConnectorX();
            int yProgress = main.getYLoc() + main.getHeight() - 1;
            while (yProgress < coreY2) {
                placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
                yProgress += 1;
            }
        }

        private static void buildNorthEast(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            int xProgress = main.getConnectorX();
            int yProgress = main.getYLoc() + main.getHeight() - 1;
            while (yProgress < coreY2) {
                placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
                yProgress += 1;
            }

            placeTile(world, xProgress - 1, yProgress + 1, Tileset.WALL);
            placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
            placeTile(world, xProgress + 1, yProgress + 1, Tileset.WALL);
            placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
            placeTile(world, xProgress, yProgress, Tileset.FLOOR);
            placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
            xProgress += 1;

            while (xProgress <= other.getXLoc()) {
                placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
                if (xProgress == other.getXLoc()) {
                    placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                }
                xProgress += 1;
            }
        }

        private static void buildEast(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            int xProgress = main.getXLoc() + main.getWidth() - 1;
            int yProgress = coreY1;
            while (xProgress <= other.getXLoc()) {
                placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
                xProgress += 1;
            }
        }

        private static void buildSouthEast(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            int xProgress = main.getConnectorX();
            //todo: double check this
            int yProgress = main.getYLoc() - 1;
            while (yProgress > coreY2) {
                placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
                yProgress -= 1;
            }

            placeTile(world, xProgress - 1, yProgress - 1, Tileset.WALL);
            placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
            placeTile(world, xProgress - 1, yProgress - 1, Tileset.WALL);
            placeTile(world, xProgress + 1, yProgress, Tileset.FLOOR);
            placeTile(world, xProgress, yProgress, Tileset.FLOOR);
            placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
            xProgress += 1;

            while (xProgress < other.getXLoc()) {
                placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
                if (xProgress == other.getXLoc()) {
                    placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                }
                xProgress += 1;
            }
        }

        private static void buildSouth(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            int xProgress = main.getConnectorX();
            //todo: double check this
            int yProgress = main.getYLoc() - 1;
            while (yProgress > coreY2) {
                placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
                yProgress -= 1;
            }
        }

        private static void buildSouthWest(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            int xProgress = main.getConnectorX();
            //todo: double check this
            int yProgress = main.getYLoc() - 1;
            while (yProgress > coreY2) {
                placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
                yProgress -= 1;
            }

            placeTile(world, xProgress - 1, yProgress - 1, Tileset.WALL);
            placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
            placeTile(world, xProgress + 1, yProgress - 1, Tileset.WALL);
            placeTile(world, xProgress - 1, yProgress, Tileset.FLOOR);
            placeTile(world, xProgress, yProgress, Tileset.FLOOR);
            placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
            xProgress -= 1;

            while (xProgress > other.getXLoc()) {
                placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
                if (xProgress == other.getXLoc() + other.getWidth()) {
                    placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                }
                xProgress -= 1;
            }
        }

        private static void buildWest(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            //todo: double check xProgress
            int xProgress = main.getXLoc();
            int yProgress = coreY1;
            while (xProgress >= other.getXLoc()) {
                placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
                xProgress -= 1;
            }
        }

        private static void buildNorthWest(TETile[][] world, Room main, Room other) {
            int coreX1 = main.getConnectorX();
            int coreY1 = main.getConnectorY();
            int coreX2 = other.getConnectorX();
            int coreY2 = other.getConnectorY();

            int xProgress = main.getConnectorX();
            //todo: double check this
            int yProgress = main.getYLoc() + main.getHeight() - 1;
            while (yProgress < coreY2) {
                placeTile(world, xProgress - 1, yProgress, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
                yProgress += 1;
            }

            placeTile(world, xProgress - 1, yProgress + 1, Tileset.WALL);
            placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
            placeTile(world, xProgress + 1, yProgress + 1, Tileset.WALL);
            placeTile(world, xProgress - 1, yProgress, Tileset.FLOOR);
            placeTile(world, xProgress, yProgress, Tileset.FLOOR);
            placeTile(world, xProgress + 1, yProgress, Tileset.WALL);
            xProgress -= 1;

            while (xProgress > other.getXLoc()) {
                placeTile(world, xProgress, yProgress + 1, Tileset.WALL);
                placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                placeTile(world, xProgress, yProgress - 1, Tileset.WALL);
                if (xProgress == other.getXLoc() + other.getWidth()) {
                    placeTile(world, xProgress, yProgress, Tileset.FLOOR);
                }
                xProgress -= 1;
            }
        }

        private static void placeTile(TETile[][] world, int x, int y, TETile type) {
            if (x >= 0 && x < world.length && y >= 0 && y < world[0].length) {
                if (world[x][y].equals(Tileset.WALL) && type.equals(Tileset.FLOOR)) {
                    world[x][y] = type;
                }
                if (world[x][y].equals(Tileset.NOTHING)) {
                    world[x][y] = type;
                }
            }
        }
        /**
         * This method adds a randomly generated room to the world.
         * It randomly selects a width, height and location for the room.
         * If a room already exists at a randomly generated location, nothing happens.
         * After drawing the room, a Room object is added to the rooms ArrayDeque to be tracked.
         *
         * @param tiles
         * @param dice
         */
        private static void addRoom(TETile[][] tiles, Random dice) {
            int minHSize = 5;
            int maxHSize = tiles.length / 4;
            int minVSize = 5;
            int maxVSize = tiles[0].length / 4;
            int width = RandomUtils.uniform(dice, minHSize, maxHSize);
            int height = RandomUtils.uniform(dice, minVSize, maxVSize);
            int xLoc = RandomUtils.uniform(dice, 2, tiles.length - width - 2);
            int yLoc = RandomUtils.uniform(dice, 2, tiles[0].length - height - 2);
            if (!checkSpace(width, height, xLoc, yLoc, tiles)) {
                return;
            }
            for (int i = xLoc; i < xLoc + width; i++) {
                for (int j = yLoc; j < yLoc + height; j++) {
                    if (i == xLoc || i == xLoc + width - 1 || j == yLoc || j == yLoc + height - 1) {
                        tiles[i][j] = Tileset.WALL;
                    } else {
                        tiles[i][j] = Tileset.FLOOR;
                    }
                }
            }
            ROOMS.add(new Room(yLoc + 1, xLoc + 1, width - 1, height - 1, dice));
        }

        /**
         * This method checks to see if a potential Room will be built on top of an existing one.
         * Returns true if it does, false otherwise.
         *
         * @param width
         * @param height
         * @param xLoc
         * @param yLoc
         * @param tiles
         * @return
         */
        private static boolean checkSpace(int width, int height, int xLoc, int yLoc, TETile[][] tiles) {
            for (int i = xLoc; i < xLoc + width; i++) {
                for (int j = yLoc; j < yLoc + height; j++) {
                    if (!tiles[i][j].equals(Tileset.NOTHING)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
