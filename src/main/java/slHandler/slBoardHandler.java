package slHandler;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static csc133.spot.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUniform3f;


public class slBoardHandler {

    private static double lastFrameTime = 0;

    private slBoardHandler() { }

    public static float[] getBgVerts() {
        int gridWidth = (maxCols * length) + ((maxCols - 1) * padding),
                gridHeight = (maxRows * length) + ((maxRows - 1) * padding),
                yOffset = 0;

        int horizOffset = ((winWidth / 2) - (gridWidth / 2)),
                vertOffset = ((winHeight / 2) - (gridHeight / 2));

        if (winHeight >= monitorH) yOffset = ((winHeight - monitorH) + horizOffset * 2);

        //        int ymax = winHeight - vertOffset - yOffset, ymin = ymax - length;

        int xmin = ((winWidth / 2) - (gridWidth / 2)),
                ymin = ((winHeight / 2) - (gridHeight / 2)) - yOffset,
                xmax = xmin + gridWidth,
                ymax = ymin + gridHeight;

        return new float[]{xmin, ymin, xmax, ymin, xmax, ymax, xmin, ymax};
    }


    public static int[] getIndexArrayForSquares() {

        int ips = 6, vps = 4; // indices per square, vertices per square

        int[] indices = new int[maxRows * maxCols * ips];
        int vIndex = 0, myI = 0;

        while (myI < indices.length) {
            indices[myI++] = vIndex;
            indices[myI++] = vIndex+1;
            indices[myI++] = vIndex+2;

            indices[myI++] = vIndex;
            indices[myI++] = vIndex+2;
            indices[myI++] = vIndex+3;
            vIndex += vps;
        }
        return indices;
    }

    public static float[] getVertexArray() {

        int gridWidth = (maxCols * length) + ((maxCols - 1) * padding),
                gridHeight = (maxRows * length) + ((maxRows - 1) * padding);

        int horizOffset = ((winWidth / 2) - (gridWidth / 2)),
                vertOffset = ((winHeight / 2) - (gridHeight / 2));

        // vert per square, vertices per vertex
        int vps = 4, fpv = 2;
        float[] vertices = new float[maxRows * maxCols * vps * fpv];

        int xmin = horizOffset, xmax = xmin + length;
        int yOffset = 0;
        if (winHeight >= monitorH) yOffset = ((winHeight - monitorH) + horizOffset * 2);
        int ymax = winHeight - vertOffset - yOffset, ymin = ymax - length;

        int index = 0;
        for (int i = 0; i < maxRows; i++) {
            for (int j = 0; j < maxCols; j++) {
                vertices[index++] = xmin;   // bottom left X
                vertices[index++] = ymin;   // bottom left Y

                vertices[index++] = xmax;   // bottom right X
                vertices[index++] = ymin;   // bottom right Y

                vertices[index++] = xmax;   // top right X
                vertices[index++] = ymax;   // top right Y

                vertices[index++] = xmin;   // top left X
                vertices[index++] = ymax;   // top left Y

                xmin = xmax + padding;
                xmax = xmin + length;
            }
            xmin = horizOffset;
            xmax = xmin + length;
            ymax = ymin - padding;
            ymin = ymax - length;
        }
        return vertices;
    }

    public static void setBufferArray() {

        bufferArray = new int[maxRows][maxCols][2];

        for (int i = 0; i < maxRows; i++) {
            for (int j = 0; j < maxCols; j++) {
                bufferArray[i][j][0] = 0;
                bufferArray[i][j][1] = 0;
            }
        }

        float[] tempVertexArray = new float[8];
        int[] tempIndexArray = new int[6];
        int loopIndex = 0;

        System.out.println("Loading...");

        tempIndexArray[0] = 0;
        tempIndexArray[1] = 1;
        tempIndexArray[2] = 2;
        tempIndexArray[3] = 0;
        tempIndexArray[4] = 2;
        tempIndexArray[5] = 3;

        for (int row = 0; row < maxRows; row++) {
            for (int col = 0; col < maxCols; col++) {
                bufferArray[row][col][0] = glGenBuffers();
                bufferArray[row][col][1] = glGenBuffers();

                tempVertexArray[0] = vertices[(loopIndex * 8)];
                tempVertexArray[1] = vertices[(loopIndex * 8) + 1];
                tempVertexArray[2] = vertices[(loopIndex * 8) + 2];
                tempVertexArray[3] = vertices[(loopIndex * 8) + 3];
                tempVertexArray[4] = vertices[(loopIndex * 8) + 4];
                tempVertexArray[5] = vertices[(loopIndex * 8) + 5];
                tempVertexArray[6] = vertices[(loopIndex * 8) + 6];
                tempVertexArray[7] = vertices[(loopIndex * 8) + 7];

                glBindBuffer(GL_ARRAY_BUFFER, bufferArray[row][col][0]);
                glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                        createFloatBuffer(tempVertexArray.length).
                        put(tempVertexArray).flip(), GL_STATIC_DRAW);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferArray[row][col][1]);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                        createIntBuffer(tempIndexArray.length).
                        put(tempIndexArray).flip(), GL_STATIC_DRAW);

                loopIndex++;
            }
        }

        System.out.println("Buffer generation complete.");
    }

    // used to update the colors of the grid squares dynamically (aka rainbow).
    // updated more elegant solution, not fully tested and may still be able to be done better
    private static void updateColors() {
        if (rainbow) {
            // color change values
            float colorChange = 0.0024f;

            if (VEC_RC_LIVE.y <= 0f) VEC_RC_LIVE.z += colorChange;
            else if (VEC_RC_LIVE.x <= 0f && VEC_RC_LIVE.y >= 0f) VEC_RC_LIVE.z -= colorChange;

            if (VEC_RC_LIVE.x <= 0f) VEC_RC_LIVE.y += colorChange;
            else if (VEC_RC_LIVE.z <= 0f && VEC_RC_LIVE.x >= 0f) VEC_RC_LIVE.y -= colorChange;

            if (VEC_RC_LIVE.z <= 0f) VEC_RC_LIVE.x += colorChange;
            else if (VEC_RC_LIVE.y <= 0f && VEC_RC_LIVE.z >= 0f) VEC_RC_LIVE.x -= colorChange;
        }
    }

    public static void drawStuff(int renderColorLocation) {
        float squareColorX = 0f, squareColorY = 0f, squareColorZ = 0f;
        int indexOffset = 0;

        int vertexSize = 2, vertexStride = 0;
        long vertexPointer = 0L;

        // separate buffer for every grid square
        // massive increase to load time and huge memory consumption,
        // so not viable with larger grids; doesn't increase performance
        // past < 20k live cells anyway, and starts decreasing past that number,
        // so better to just use the old method of one buffer with every square
//
//        if (padding == 0) {
//            for (int i = 0; i < my_board.getUpdatedLives().size(); i++) {
//                int[] squareIndex = my_board.getUpdatedLives().get(i);
//                int row = squareIndex[0];
//                int col = squareIndex[1];
//                indexOffset = (row * maxCols + col) * 6;
//
//                squareColorX = VEC_RC_LIVE.x; // Live color
//                squareColorY = VEC_RC_LIVE.y;
//                squareColorZ = VEC_RC_LIVE.z;
//
//                glBindBuffer(GL_ARRAY_BUFFER, bufferArray[row][col][0]);
//                glEnableClientState(GL_VERTEX_ARRAY);
//                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferArray[row][col][1]);
//                glVertexPointer(vertexSize, GL_FLOAT, vertexStride, vertexPointer);
//
//                glUniform3f(renderColorLocation, squareColorX, squareColorY, squareColorZ);
//                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
//            }
//        } else {
//            // render all squares on grid
//            for (int row = 0; row < maxRows; row++) {
//                for (int col = 0; col < maxCols; col++) {
//                    indexOffset = (row * maxCols + col) * 6;
//
//                    if (my_board.getLiveCellArray()[row][col]) {
//                        squareColorX = VEC_RC_LIVE.x; // Live color
//                        squareColorY = VEC_RC_LIVE.y;
//                        squareColorZ = VEC_RC_LIVE.z;
//
//                        glBindBuffer(GL_ARRAY_BUFFER, bufferArray[row][col][0]);
//                        glEnableClientState(GL_VERTEX_ARRAY);
//                        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferArray[row][col][1]);
//                        glVertexPointer(vertexSize, GL_FLOAT, vertexStride, vertexPointer);
//
//                        glUniform3f(renderColorLocation, squareColorX, squareColorY, squareColorZ);
//                        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
//
//                    } else if (padding > 0) {
//                        squareColorX = VEC_RC_DEAD.x; // Dead color
//                        squareColorY = VEC_RC_DEAD.y;
//                        squareColorZ = VEC_RC_DEAD.z;
//
//                        glBindBuffer(GL_ARRAY_BUFFER, bufferArray[row][col][0]);
//                        glEnableClientState(GL_VERTEX_ARRAY);
//                        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferArray[row][col][1]);
//                        glVertexPointer(vertexSize, GL_FLOAT, vertexStride, vertexPointer);
//
//                        glUniform3f(renderColorLocation, squareColorX, squareColorY, squareColorZ);
//                        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
//                    }
//                }
//            }
//        }
//
//        if (padding == 0) {
//            glBindBuffer(GL_ARRAY_BUFFER, vboBG);
//            glEnableClientState(GL_VERTEX_ARRAY);
//            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBG);
//            glVertexPointer(vertexSize, GL_FLOAT, vertexStride, vertexPointer);
//
//            glUniform3f(renderColorLocation, VEC_RC_DEAD.x, VEC_RC_DEAD.y, VEC_RC_DEAD.z);
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
//        }

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        if (padding == 0) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glVertexPointer(vertexSize, GL_FLOAT, vertexStride, vertexPointer);
        }

        if (padding == 0) {
            for (int i = 0; i < my_board.getUpdatedLives().size(); i++) {
                int[] squareIndex = my_board.getUpdatedLives().get(i);
                int row = squareIndex[0];
                int col = squareIndex[1];

                squareColorX = VEC_RC_LIVE.x; // Live color
                squareColorY = VEC_RC_LIVE.y;
                squareColorZ = VEC_RC_LIVE.z;
                indexOffset = (row * maxCols + col) * 6;
                glUniform3f(renderColorLocation, squareColorX, squareColorY, squareColorZ);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (long) indexOffset * Integer.BYTES);
            }
        } else {
            // render all squares on grid
            for (int k = 0; k < maxRows; k++) {
                for (int i = 0; i < maxCols; i++) {
                    if (my_board.getLiveCellArray()[k][i]) {
                        squareColorX = VEC_RC_LIVE.x; // Live color
                        squareColorY = VEC_RC_LIVE.y;
                        squareColorZ = VEC_RC_LIVE.z;
                        indexOffset = (k * maxCols + i) * 6;
                        glUniform3f(renderColorLocation, squareColorX, squareColorY, squareColorZ);
                        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (long) indexOffset * Integer.BYTES);
                    } else if (padding > 0) {
                        squareColorX = VEC_RC_DEAD.x; // Dead color
                        squareColorY = VEC_RC_DEAD.y;
                        squareColorZ = VEC_RC_DEAD.z;
                        indexOffset = (k * maxCols + i) * 6;
                        glUniform3f(renderColorLocation, squareColorX, squareColorY, squareColorZ);
                        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (long) indexOffset * Integer.BYTES);
                    }
                }
            }
        }
        if (padding == 0) {
            glBindBuffer(GL_ARRAY_BUFFER, vboBG);
            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBG);
            glVertexPointer(vertexSize, GL_FLOAT, vertexStride, vertexPointer);

            glUniform3f(renderColorLocation, VEC_RC_DEAD.x, VEC_RC_DEAD.y, VEC_RC_DEAD.z);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }

//        // render only squares within updated range
//        for (int k = 0; k < my_board.getUpdatedCells().size(); k++) {
//            int[] squareIndex = my_board.getUpdatedCells().get(k);
//            int i = squareIndex[0];
//            int j = squareIndex[1];
//
//            if (my_board.getLiveCellArray()[i][j]) {
//                squareColorX = VEC_RC_DYN.x; // Live color
//                squareColorY = VEC_RC_DYN.y;
//                squareColorZ = VEC_RC_DYN.z;
//            } else {
//                squareColorX = 0.0f; // Dead color
//                squareColorY = 0.0f;
//                squareColorZ = 0.0f;
//            }
//
//            glUniform3f(renderColorLocation, squareColorX, squareColorY, squareColorZ);
//            // Calculate the offset for the indices of the current square
//            indexOffset = (i * maxCols + j) * 6;
//
//            // Draw the square using the calculated offset
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (long) indexOffset * Integer.BYTES);
//        }
    }

    public static double boardAndFrames(boolean delayFrame, double framesPerSec) {
        if (delayFrame) {
            if (System.currentTimeMillis() > delayEndTime) {
                delayEndTime = System.currentTimeMillis() + 500;
                liveCells = my_board.updateNextCellArray();
                updateColors();

                lastFrameTime = System.nanoTime() - lastFrameTime;
                framesPerSec = 1 / (lastFrameTime / 1000000000);
                lastFrameTime = System.nanoTime();
            }
        } else {
            liveCells = my_board.updateNextCellArray();
            updateColors();

            lastFrameTime = System.nanoTime() - lastFrameTime;
            framesPerSec = 1 / (lastFrameTime / 1000000000);

            lastFrameTime = System.nanoTime();
        }
        return framesPerSec;
    }

    public static void updateBoardSize() {
        int newHeight = 0, newWidth = 0;
        int heightPadding = 0, widthPadding = 0;

        newHeight = ((winHeight) / maxRows) - (((winHeight) / maxRows) / 4);
        heightPadding = newHeight / 4;
        newWidth = ((winWidth) / maxCols) - (((winWidth) / maxCols) / 4);
        widthPadding = newWidth / 4;

        if (newHeight < newWidth) {
            length = newHeight;
            padding = heightPadding;
        } else {
            length = newWidth;
            padding = widthPadding;
        }

        if (length == 0) length = 1;

        vertices = getVertexArray();
        indices = getIndexArrayForSquares();
        bgVertices = getBgVerts();
        // separate buffer for every square
//        setBufferArray();
    }
}