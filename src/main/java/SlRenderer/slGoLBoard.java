package SlRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
public abstract class slGoLBoard {
    protected int NUM_ROWS;
    protected int NUM_COLS;
    protected int lastUpdatedRow, lastUpdatedCol, firstUpdatedRow, firstUpdatedCol;
    protected static ArrayList<int[]> updatedCells = new ArrayList<>();
    protected static ArrayList<int[]> liveCells = new ArrayList<>();

    protected boolean[][]  cellArrayA, cellArrayB, liveCellArray, nextCellArray;

    protected slGoLBoard(int numRows, int numCols) {

        NUM_ROWS = numRows;
        NUM_COLS = numCols;
        lastUpdatedRow = NUM_ROWS;
        lastUpdatedCol = NUM_COLS;
        firstUpdatedRow = 0;
        firstUpdatedCol = 0;
        cellArrayA = new boolean[NUM_ROWS][NUM_COLS];
        cellArrayB = new boolean[NUM_ROWS][NUM_COLS];
        updatedCells.clear();
        liveCells.clear();

        Random myRandom = new Random();
        for (int row = 0; row < NUM_ROWS; ++row) {
            for (int col = 0; col < NUM_COLS; ++col) {
//                cellArrayA[row][col] = myRandom.nextBoolean();
                cellArrayA[row][col] = false;
                cellArrayB[row][col] = false;
            }
        }
        liveCellArray = cellArrayA;

        int spawnSizeX = 1, spawnSizeY = 1;
        int spawnPosX = 0, spawnPosY = 0;
        int numToSpawn = (int)(Math.random() * (NUM_ROWS*NUM_COLS));
//        for (int x = 0; x < 100; x++) {
        for (int x = 0; x < numToSpawn; x++) {


            spawnPosY = (int) (Math.random() * NUM_ROWS);
            spawnPosX = (int) (Math.random() * NUM_COLS);
            while (liveCellArray[Math.floorMod(spawnPosY, NUM_ROWS)][Math.floorMod(spawnPosX, NUM_COLS)]) {
                spawnPosY++;
                if (spawnPosY >= NUM_ROWS) {
                    spawnPosY = 0;
                    spawnPosX++;
                }
            }

            for (int i = spawnPosY; i < spawnPosY + spawnSizeY; i++) {
                for (int j = spawnPosX; j < spawnPosX + spawnSizeX; j++)
                    liveCellArray[Math.floorMod(i, NUM_ROWS)][Math.floorMod(j, NUM_COLS)] = true;
            }

        }

        // all true
//        for (int i = 0; i < NUM_ROWS; i++) {
//            for (int j = 0; j < NUM_COLS; j++) {
//                liveCellArray[i][j] = true;
//            }
//        }

        nextCellArray = cellArrayB;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (liveCellArray[row][col]) {
                    liveCells.add(new int[]{row, col});
                }
            }
        }

    }  //  public slGoLBoard(int numRows, int numCols)

    private boolean[][] gridSpawn(int sizeX, int sizeY, int posX, int posY) {
        int spawnSizeX = sizeX, spawnSizeY = sizeY;
        int spawnPosX = posX, spawnPosY = posY;
        for (int i = spawnPosY; i < spawnPosY + spawnSizeY; i++) {
            for (int j = spawnPosX; j < spawnPosX + spawnSizeX; j++)
                liveCellArray[Math.floorMod(i, NUM_ROWS)][Math.floorMod(j, NUM_COLS)] = true;
        }
        return liveCellArray;
    }

    // Create a Board with a given number of cells alive - the alive cells
    // are placed randomly placed applying Durstenfeld-Knuth random shuffling
    protected slGoLBoard(int numRows, int numCols, int numAlive) {
        NUM_ROWS = numRows;
        NUM_COLS = numCols;
        lastUpdatedRow = NUM_ROWS;
        lastUpdatedCol = NUM_COLS;
        firstUpdatedRow = 0;
        firstUpdatedCol = 0;
        cellArrayA = new boolean[NUM_ROWS][NUM_COLS];
        cellArrayB = new boolean[NUM_ROWS][NUM_COLS];
        updatedCells.clear();

        Random myRandom = new Random();
        for (int row = 0; row < NUM_ROWS; ++row) {
            for (int col = 0; col < NUM_COLS; ++col) {
//                cellArrayA[row][col] = myRandom.nextBoolean();
                cellArrayA[row][col] = false;
                cellArrayB[row][col] = false;
            }
        }
        liveCellArray = cellArrayA;

        int spawnSizeX = 1, spawnSizeY = 1;
        int spawnPosX = 0, spawnPosY = 0;
//        for (int x = 0; x < 100; x++) {
        for (int x = 0; x < numAlive; x++) {


            spawnPosY = (int) (Math.random() * NUM_ROWS);
            spawnPosX = (int) (Math.random() * NUM_COLS);
            while (liveCellArray[Math.floorMod(spawnPosY, NUM_ROWS)][Math.floorMod(spawnPosX, NUM_COLS)]) {
                spawnPosY++;
                if (spawnPosY >= NUM_ROWS) {
                    spawnPosY = 0;
                    spawnPosX++;
                }
            }

            for (int i = spawnPosY; i < spawnPosY + spawnSizeY; i++) {
                for (int j = spawnPosX; j < spawnPosX + spawnSizeX; j++)
                    liveCellArray[Math.floorMod(i, NUM_ROWS)][Math.floorMod(j, NUM_COLS)] = true;
            }

        }
        nextCellArray = cellArrayB;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (liveCellArray[row][col]) {
                    liveCells.add(new int[]{row, col});
                }
            }
        }

//        gridSpawn(2, 2, 1, 5);
//
//        gridSpawn(2, 1, 13, 3);
//        gridSpawn(1, 1, 12, 4);
//        gridSpawn(1, 3, 11, 5);
//        gridSpawn(1, 1, 12, 8);
//        gridSpawn(2, 1, 13, 9);
//        gridSpawn(1, 1, 15, 6);
//        gridSpawn(1, 1, 16, 4);
//        gridSpawn(1, 1, 16, 8);
//        gridSpawn(1, 3, 17, 5);
//        gridSpawn(1, 1, 18, 6);
//
//        gridSpawn(2, 3, 21, 3);
//        gridSpawn(1, 1, 23, 2);
//        gridSpawn(1, 1, 23, 6);
//        gridSpawn(1, 2, 25, 1);
//        gridSpawn(1, 2, 25, 6);
////
//        gridSpawn(2, 2, 35, 3);

//        liveCellArray = gridSpawn(1, 1, 5, 5, liveCellArray);
//        liveCellArray = gridSpawn(3, 1, 4, 7, liveCellArray);
//        liveCellArray = gridSpawn(1, 1, 6, 6, liveCellArray);


//        nextCellArray = gridSpawn(1, 1, 5, 5, nextCellArray);
//        nextCellArray = gridSpawn(3, 1, 4, 7, nextCellArray);
//        nextCellArray = gridSpawn(1, 1, 6, 6, nextCellArray);
//        nextCellArray = gridSpawn(6, 1, 5, 4, nextCellArray);

    }  //  public slGoLBoard(int numRows, int numCols, int numAlive)

    public boolean[][] getLiveCellArray() {
        return liveCellArray;
    }
    private boolean[][] getNextCellArray() {
        return nextCellArray;
    }

    protected ArrayList<int[]> getUpdatedCells() {
        return updatedCells;
    }

    public ArrayList<int[]> getUpdatedLives() {
        return liveCells;
    }

    public void setCellAlive(int row, int col){
        liveCellArray[row][col] = true;
    }

    public void setCellDead(int row, int col){
        liveCellArray[row][col] = false;
    }

    private void setAllCells(boolean value) {
        for (boolean[] rows : liveCellArray) {
            for (boolean cell : rows) {
                cell = value;
            }
        }
    }  //  void setAllCells()

    private void copyNextToLive() {
        for (int row = 0; row < nextCellArray.length; ++row){
            System.arraycopy(nextCellArray[row], 0, liveCellArray[row], 0, nextCellArray[row].length);
        }
        return;
    }  //  void copyNextToLive()

    protected void printGoLBoard() {
        for (boolean[] my_row : liveCellArray) {
            for (boolean my_val : my_row) {
                if (my_val) {
                    System.out.print("\u001B[34m" + 1 + "\u001B[0m "); // hope it's okay i made it blue, makes my eyes feel better :)
                } else {
                    System.out.print(0 + " ");
                }
            }  //  for (bool my_val : my_row)
            System.out.println();
        }  //  for (bool[] my_row : my_array)
    }  //  void printGoLBoard()

    protected abstract int countLiveTwoDegreeNeighbors(int row, int col);
    public abstract int updateNextCellArray();

    // when saving: if user didnt add .ca, then just add .ca to the end of the file name. save file here in golboard i guess?

}  //  public class slGoLBoard