package world.map;

public class TileMap {
    private int[][] tiles;
    private int rows;
    private int cols;

    public TileMap(int[][] tiles) {
        this.tiles = tiles;
        this.rows = tiles.length;
        this.cols = tiles[0].length;
    }

    public int getTile(int row, int col) {
        return tiles[row][col];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean estaDentro(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean esCaminable(int row, int col) {
        if (!estaDentro(row, col)) {
            return false;
        }

        int tile = getTile(row, col);
//PLAYA 0-9
        // 0 = arena caminable
        // 1 = pared bloqueada
        // 2 = agua bloqueada
        // 3 = piedra bloqueada (en arena)
//BOSQUE 10-19
        // 10 = pasto caminable
//BOSQUE 20-29
        // 20 = roca caminable
        return tile != 1 && tile != 2 && tile != 3;
    }
}