package slHandler;

import SlRenderer.slGoLBoard;
import SlRenderer.slGoLBoardLive;

import javax.swing.*;
import java.io.*;

import static csc133.spot.*;

public class slFileHandler {

    private static JFileChooser chooser = new JFileChooser("src/main/java");

    private slFileHandler() { }


    protected static void saveBoardFile() {
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                writeFile(getFile(chooser), my_board);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static boolean loadBoardFile() {
        boolean sizeUpdate = false;
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File tempFile = getFile(chooser);
            if (!tempFile.exists()) {
                try {
                    writeFile(tempFile, new slGoLBoardLive(maxRows, maxCols));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                sizeUpdate = readFile(tempFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return sizeUpdate;
    }

    private static boolean readFile(File tempFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(tempFile.getPath()));
        int rowNum = 0, prevRows = maxRows, prevCols = maxCols;
        boolean sizeUpdate = false;

        String line = reader.readLine();
        maxRows = Integer.parseInt(line);
        line = reader.readLine();
        maxCols = Integer.parseInt(line);
        my_board = new slGoLBoardLive(maxRows, maxCols);
        if (maxRows != prevRows || maxCols != prevCols) {
            slBoardHandler.updateBoardSize();
            sizeUpdate = true;
        }
        line = reader.readLine();
        while (line != null) {
            for (int i = 0; i < maxCols; i++) {
                if (line.charAt(i*2) == '0') {
                    my_board.setCellDead(rowNum, i);
                } else if (line.charAt(i*2) == '1') {
                    my_board.setCellAlive(rowNum, i);
                }
            }
            rowNum++;
            line = reader.readLine();
        }
        reader.close();
        return sizeUpdate;
    }

    private static void writeFile(File tempFile, slGoLBoard my_board) throws IOException {
        FileWriter writer = new FileWriter(tempFile.getPath());
        writer.write(maxRows + "\n");
        writer.write(maxCols + "\n");
        for (int i = 0; i < maxRows; i++) {
            for (int j = 0; j < maxCols; j++) {
                if (my_board.getLiveCellArray()[i][j]) writer.write("1 ");
                else writer.write("0 ");
            }
            writer.write("\n");
        }
        writer.close();
    }

    private static File getFile(JFileChooser chooser) {
        if (!((chooser.getSelectedFile().getName().endsWith(".ca")))) {
            return new File (chooser.getSelectedFile().getPath() + ".ca");
        } else {
            return new File (chooser.getSelectedFile().getPath());
        }
    }
}
