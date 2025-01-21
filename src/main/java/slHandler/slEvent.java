package slHandler;

import SlRenderer.slGoLBoardLive;
import slge.slKeyListener;

import java.awt.*;

import static csc133.spot.*;
import static org.lwjgl.glfw.GLFW.*;
import static slge.slKeyListener.isKeyPressed;

public abstract class slEvent {

    slEvent() { }

    protected static boolean delayEvent(boolean delayFrame) {
        if (isKeyPressed(GLFW_KEY_D)) {
            if (haltOnEvent) haltRender = true;
            delayFrame = !delayFrame;
            if (delayFrame) {
                delayEndTime = System.currentTimeMillis() + 500;
            }
            slKeyListener.resetKeypressEvent(GLFW_KEY_D);
            if (haltRender) haltRender = false;
        }
        return delayFrame;
    }

    protected static boolean framerateDisp(boolean dispFrames) {
        if (isKeyPressed(GLFW_KEY_F)) {
            if (haltOnEvent) haltRender = true;
            dispFrames = !dispFrames;
            slKeyListener.resetKeypressEvent(GLFW_KEY_F);
            if (haltRender) haltRender = false;
        }

        return dispFrames;
    }

    protected  static boolean liveCellsDisp(boolean dispLiveCells) {
        if (isKeyPressed(GLFW_KEY_I)) {
            if (haltOnEvent) haltRender = true;
            dispLiveCells = !dispLiveCells;
            slKeyListener.resetKeypressEvent(GLFW_KEY_I);
            if (haltRender) haltRender = false;
        }
        return dispLiveCells;
    }

    protected static void haltEvent() {
        if (isKeyPressed(GLFW_KEY_H)) {
            if (haltOnEvent) haltRender = true;
            slKeyListener.resetKeypressEvent(GLFW_KEY_H);
        }
    }

    protected static boolean loadBoard() {
        boolean update = false;
        if (isKeyPressed(GLFW_KEY_L)) {
            if (haltOnEvent) haltRender = true;

            update = slFileHandler.loadBoardFile();

            // simulates a key release for the specified key, manually updating the glfw key status
            if (robotKeyReset) {
                try {
                    new Robot().keyRelease(GLFW_KEY_L);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
            }
            slKeyListener.resetKeypressEvent(GLFW_KEY_L);
        }
        return update;
    }

    protected static void resetBoard() {
        if (isKeyPressed(GLFW_KEY_R)) {
            if (haltOnEvent) haltRender = true;
            my_board = new slGoLBoardLive(maxRows, maxCols);
            slKeyListener.resetKeypressEvent(GLFW_KEY_R);
        }
    }

    protected static void saveBoard() {
        if (isKeyPressed(GLFW_KEY_S)) {
            if (haltOnEvent) haltRender = true;

            slFileHandler.saveBoardFile();

            // simulates a key release for the specified key, manually updating the glfw key status
            if (robotKeyReset) {
                try {
                    new Robot().keyRelease(GLFW_KEY_S);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
            }
            slKeyListener.resetKeypressEvent(GLFW_KEY_S);
        }
    }

    protected static void resumeEvent() {
        if ((isKeyPressed(GLFW_KEY_SPACE))) {
            if (haltOnEvent) haltRender = false;
            slKeyListener.resetKeypressEvent(GLFW_KEY_SPACE);
        }
    }

    protected static void helpDisp() {
        if (isKeyPressed(GLFW_KEY_SLASH) &&
                (isKeyPressed(GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW_KEY_RIGHT_SHIFT))) {

            if (haltOnEvent) haltRender = true;
            printText();
            slKeyListener.resetKeypressEvent(GLFW_KEY_SLASH);
            if (isKeyPressed(GLFW_KEY_LEFT_SHIFT))
                slKeyListener.resetKeypressEvent(GLFW_KEY_LEFT_SHIFT);
            if (isKeyPressed(GLFW_KEY_RIGHT_SHIFT))
                slKeyListener.resetKeypressEvent(GLFW_KEY_RIGHT_SHIFT);
        }
    }

    private static void printText() {
        System.out.println("\n[?] -> Print this text\n");
        System.out.println("[d] -> Toggle 500ms frame delay");
        System.out.println("[f] -> Toggle framerate display in console");
        System.out.println("[i] -> Toggle live cell count display in console (overwrites FPS)");
        System.out.println("[h] -> Halt the rendering (\"Pause\" the rendering)");
        System.out.println("""
                            [l] -> Load the GoL Board from a selected file.
                                     If a file name is selected that doesn't already exist, a new board will be randomly
                                     generated, saved to a new file with the chosen name, and loaded back into the program.
                                     Boards of different dimensions are able to be read without any issues.""");
        System.out.println("[r] -> Randomize the GoL Board and restart");
        System.out.println("""
                            [s] -> Save the current Board status to a file selected by the user.
                                     If a file name is selected that doesn't already exist, or doesn't fulfill the
                                     conditions (must have .ca file extension), a new file will be generated. Then the
                                     current status of the board will be written and saved to the file. Otherwise, the
                                     current Board will overwrite all the previous data contained in the file.""");
        System.out.println("[SPACE] -> Resume rendering (\"Unhalt\" the render, or \"Unpause\")");
        System.out.println("[ESC] -> Exit the application");
    }

}
