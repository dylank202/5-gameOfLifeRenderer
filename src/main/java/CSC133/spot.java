package csc133;

import SlRenderer.slGoLBoard;
import SlRenderer.slGoLBoardLive;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWKeyCallback;
import slHandler.slBoardHandler;

public class spot {
    // monitor width and height, in pixels. monitorW isn't used anywhere else yet but
    // i might as well put it here with monitorH so we have both in one place
    public static final int monitorW = 2560, monitorH = 1440;
    // padding / offset from the edges of the screen (assuming correct monitor wid & ht)
    public static int winPosX = 75, winPosY = 100;

    public static int winWidth = monitorW - winPosX * 2;    // window width
    public static int winHeight = monitorH - winPosY * 2;   // window height

    // onEvent, in case we don't want to have to press resume every time we perform an action
    public static boolean haltRender = false, haltOnEvent = true;

    public static GLFWKeyCallback keyCallback;

    public static int maxRows = 10, maxCols = 12, length = 50, padding = 10;

    public static slGoLBoard my_board = new slGoLBoardLive(maxRows, maxCols);

    public static Vector4f VEC_RC_DEAD =
            new Vector4f(0.2f, 0.2f, 0.2f, 1.0f); // "vector render color" for square - dead
    public static Vector4f VEC_RC_LIVE =
            new Vector4f(0.0f, 0.7f, 0.8f, 1.0f); // "vector render color" for square - live
    public static Vector4f VEC_RC_BG =
            new Vector4f(0.05f, 0.0f, 0.1f, 1.0f); // "vector render color" for background

    public static boolean rainbow = false;  // t/f for rainbow color changing for live color or not

    public static long delayEndTime = 0;    // universal time for the next frame to render when delay is active

    // needed to dynamically update size of board based on the file the user loads
    public static float[] vertices = slBoardHandler.getVertexArray();
    public static int[] indices = slBoardHandler.getIndexArrayForSquares();

    public static float[] bgVertices = slBoardHandler.getBgVerts();
    public static int[] bgIndices = {0, 1, 2, 0, 2, 3};

    public static int vbo = 0, ibo = 0, vboBG = 0, iboBG = 0;

    public static int liveCells = 0;

    // separate buffers for every grid square
    public static int[][][] bufferArray;

    // keyReset turns on or off the robot, which triggers a manual "release" to be sent to glfw,
    // releasing the stuck key when another window opens.
    // keyStatus toggles the console display of the current key and its status according to glfw:
    // if glfw thinks the key is pressed, console will display that. the display does NOT reflect the
    // actual status of the key, ONLY the value that glfw holds for that key (using glfwGetKey())
    public static boolean robotKeyReset = true, glfwKeyStatusDisplay = true;

}
