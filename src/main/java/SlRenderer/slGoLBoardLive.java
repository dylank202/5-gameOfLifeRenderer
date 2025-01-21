package SlRenderer;

import java.util.ArrayList;

public class    slGoLBoardLive extends slGoLBoard {
    public slGoLBoardLive(int numRows, int numCols) {
        super(numRows, numCols);
    }

    public slGoLBoardLive(int numRows, int numCols, int numAlive) {
        super(numRows, numCols, numAlive);
    }

    @Override
    public int countLiveTwoDegreeNeighbors(int row, int col) {

//        return java.util.stream.IntStream.rangeClosed(-1, 1).flatMap(i -> java.util.stream.IntStream.rangeClosed(-1, 1).map(j -> (!(Math.floorMod((row + i + NUM_ROWS), NUM_ROWS) == row && Math.floorMod((col + j + NUM_COLS), NUM_COLS) == col)) ? ((liveCellArray[Math.floorMod((row + i + NUM_ROWS), NUM_ROWS)][Math.floorMod((col + j + NUM_COLS), NUM_COLS)]) ? 1 : 0) : 0)).sum();

//        return IntStream.rangeClosed(-1, 1).flatMap(i -> IntStream.rangeClosed(-1, 1).map(j -> (!(Math.floorMod((row + i + NUM_ROWS), NUM_ROWS) == row && Math.floorMod((col + j + NUM_COLS), NUM_COLS) == col)) ? ((liveCellArray[Math.floorMod((row + i + NUM_ROWS), NUM_ROWS)][Math.floorMod((col + j + NUM_COLS), NUM_COLS)]) ? 1 : 0) : 0 )).sum();

//        for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) count += (!(Math.floorMod((row + i + NUM_ROWS), NUM_ROWS) == row && Math.floorMod((col + j + NUM_COLS), NUM_COLS) == col)) ? ((liveCellArray[Math.floorMod((row + i + NUM_ROWS), NUM_ROWS)][Math.floorMod((col + j + NUM_COLS), NUM_COLS)]) ? 1 : 0) : 0;

        int count = 0, currentRow, currentCol;

        for (int i = -1; i <= 1; i++) {
            currentRow = Math.floorMod((row + i + NUM_ROWS), NUM_ROWS);
            for (int j = -1; j <= 1; j++) {
                currentCol = Math.floorMod((col + j + NUM_COLS), NUM_COLS);
                count += (!(currentRow == row && currentCol == col)) ? ((liveCellArray[currentRow][currentCol]) ? 1 : 0) : 0;
//                if (!(currentRow == row && currentCol == col)) if (liveCellArray[currentRow][currentCol]) count++;
            }
        }

//        for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) count += (!(Math.floorMod((row + i + NUM_ROWS), NUM_ROWS) == row && Math.floorMod((col + j + NUM_COLS), NUM_COLS) == col)) ? ((liveCellArray[Math.floorMod((row + i + NUM_ROWS), NUM_ROWS)][Math.floorMod((col + j + NUM_COLS), NUM_COLS)]) ? 1 : 0) : 0;
        return count;
    }

    // return how many live cells are in the updated board
    /*
        Rules:
        1. Live Two Degree Neighbors < 2 --> Kill
        2. Live Two Degree Neighbors == 2 || Live Neighbors == 3 --> Retain
        3. Live Two Degree Neighbors > 3 --> Kill
        4. Dead with Live Two Degree Neighbors == 3 --> Alive again
    */
    @Override
    public int updateNextCellArray() {
        updatedCells.clear();
        liveCells.clear();

//        int retVal = 0;
//
//        int nln = 0;  // Number Live Neighbors
//        boolean ccs = true; // Current Cell Status
//        for (int row = 0; row < NUM_ROWS; ++row) {
//            for (int col = 0; col < NUM_COLS; ++col) {
//                ccs = liveCellArray[row][col];
//                nln = countLiveTwoDegreeNeighbors(row, col);
//                if (!ccs) {
//                    if (nln == 3) {
//                        nextCellArray[row][col] = true;
//                        ++retVal;
//                    } else {
//                        nextCellArray[row][col] = false;
//                    }
//                } else if (ccs) {
//                    // Current Cell Status is true
//                    if (nln < 2 || nln > 3) {
//                        nextCellArray[row][col] = false;
//                    } else if (nln == 2 || nln == 3){
//                        // nln == 2 || nln == 3
//                        nextCellArray[row][col] = true;
//                        ++retVal;
//                    }
//                }
//            }  // for (int row = 0; ...)
//        }



        int retVal = 0;
        int rowMax = lastUpdatedRow;
        int colMax = lastUpdatedCol;
        int rowMin = firstUpdatedRow;
        int colMin = firstUpdatedCol;

        rowMax = Math.min(rowMax + 20, NUM_ROWS);
        colMax = Math.min(colMax + 20, NUM_COLS);
        rowMin = Math.max(rowMin - 20, 0);
        colMin = Math.max(colMin - 20, 0);

        int maxCol = 0, maxRow = 0, minRow = NUM_ROWS, minCol = NUM_COLS;

        int nln = 0;  // Number Live Neighbors
        boolean ccs = true; // Current Cell Status
        for (int row = rowMin; row < rowMax; ++row){
            for (int col = colMin; col < colMax; ++col) {
                ccs = liveCellArray[row][col];
                nln = countLiveTwoDegreeNeighbors(row, col);

                if (!ccs) {
                    if (nln == 3) {
                        nextCellArray[row][col] = true;
                        liveCells.add(new int[]{row, col});
                        ++retVal;
                    } else {
                        nextCellArray[row][col] = false;
                    }
                } else if (ccs) {
                    // Current Cell Status is true
                    if (nln < 2 || nln > 3) {
                        nextCellArray[row][col] = false;
                    } else if (nln == 2 || nln == 3){
                        // nln == 2 || nln == 3
                        nextCellArray[row][col] = true;
                        liveCells.add(new int[]{row, col});
                        ++retVal;
                    }
                }


                if (liveCellArray[row][col] != nextCellArray[row][col]) {
                    updatedCells.add(new int[] {row, col});
                    if (col > maxCol) maxCol = col;
                    if (row > maxRow) maxRow = row;
                    if (col < minCol) minCol = col;
                    if (row < minRow) minRow = row;

                }
            }  // for (int row = 0; ...)
            lastUpdatedRow = maxRow;
            lastUpdatedCol = maxCol;
            firstUpdatedRow = minRow;
            firstUpdatedCol = minCol;


            if (lastUpdatedCol < NUM_COLS) {
                for (int i = 0; i < NUM_ROWS; i++) {
                    if (liveCellArray[i][0]) {
                        lastUpdatedCol = NUM_COLS;
                        break;
                    }
                }
            }
            if (lastUpdatedRow < NUM_ROWS) {
                for (int i = 0; i < NUM_COLS; i++) {
                    if (liveCellArray[0][i]) {
                        lastUpdatedRow = NUM_ROWS;
                        break;
                    }
                }
            }

            if (firstUpdatedCol > 0) {
                for (int i = 0; i < NUM_ROWS; i++) {
                    if (liveCellArray[i][NUM_COLS-1]) {
                        firstUpdatedCol = 0;
                        break;
                    }
                }
            }
            if (firstUpdatedRow > 0) {
                for (int i = 0; i < NUM_COLS; i++) {
                    if (liveCellArray[NUM_ROWS-1][i]) {
                        firstUpdatedRow = 0;
                        break;
                    }
                }
            }

        }  //  for (int col = 0; ...)

        boolean[][] tmp = liveCellArray;
        liveCellArray = nextCellArray;
        nextCellArray = tmp;

        return retVal;
    }  //  int updateNextCellArray()
}