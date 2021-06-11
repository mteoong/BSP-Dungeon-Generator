package byow.Core;
public class Cell {

    int x;
    int y;
    int height;
    int width;
    Cell left;
    Cell right;
    Room room;

    public Cell(int x, int y, int height, int width, Cell left, Cell right) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.left = left;
        this.right = right;
        room = null;
    }

    public void createRoom(int xCoord, int yCoord, int roomWidth, int roomHeight) {
        room = new Room(xCoord, yCoord, roomWidth, roomHeight);
    }

    public class Room {
        int roomX;
        int roomY;
        int roomWidth;
        int roomHeight;

        public Room(int roomX, int roomY, int roomWidth, int roomHeight) {
            this.roomX = roomX;
            this.roomY = roomY;
            this.roomHeight = roomHeight;
            this.roomWidth = roomWidth;
        }
    }


}
