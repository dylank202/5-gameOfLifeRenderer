package SlRenderer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import slHandler.slBoardHandler;
import slHandler.slEventHandler;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static csc133.spot.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class slSingleBatchRenderer {

    private static final int OGL_MATRIX_SIZE = 16;
    private static long window;

    private static FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
    private static int vpMatLocation = 0, renderColorLocation = 0;

    public static void render() {
        window = SlRenderer.slWindow.get(winWidth, winHeight, winPosX, winPosY);
        try {
            renderLoop();
            SlRenderer.slWindow.destroyOglWindow();
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    } // void render()

    private static void renderLoop() {
        glfwPollEvents();
        initOpenGL();
        renderObjects();
        /* Process window messages in the main thread */
        while (!glfwWindowShouldClose(window)) {
            glfwWaitEvents();
        }
    } // void renderLoop()

    private static void initOpenGL() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glViewport(0, 0, winWidth, winHeight);
        glClearColor(VEC_RC_BG.x, VEC_RC_BG.y, VEC_RC_BG.z, VEC_RC_BG.w);
        // don't call glCreateProgram() here - we have no gl-context here
        int shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
//        Matrix4f viewProjMatrix = new Matrix4f();
        String uniformVarName = "viewProjMatrix";
        glShaderSource(vs,
                "uniform mat4 viewProjMatrix;" +
                        "void main(void) {" +
                        " gl_Position = viewProjMatrix * gl_Vertex;" +
                        "}");
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        String colorUniformVarName = "renderColorLocation";
        glShaderSource(fs,
                "uniform vec3 renderColorLocation;" + // Define a uniform variable for color
                        "void main(void) {" +
                        " gl_FragColor = vec4(renderColorLocation, 1.0);" + // Use the uniform color
                        "}");
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
        glUseProgram(shader_program);
        vpMatLocation = glGetUniformLocation(shader_program, uniformVarName);
        renderColorLocation = glGetUniformLocation(shader_program, colorUniformVarName);
        return;
    } // void initOpenGL()

    public static void bufferHandling() {

        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
        glDeleteBuffers(vboBG);
        glDeleteBuffers(iboBG);
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        vboBG = glGenBuffers();
        iboBG = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                createFloatBuffer(vertices.length).
                put(vertices).flip(), GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                createIntBuffer(indices.length).
                put(indices).flip(), GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, vboBG);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                createFloatBuffer(bgVertices.length).
                put(bgVertices).flip(), GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBG);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                createIntBuffer(bgIndices.length).
                put(bgIndices).flip(), GL_STATIC_DRAW);



        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glVertexPointer(2, GL_FLOAT, 0, 0L);

        // separate buffers for every square
//        slBoardHandler.setBufferArray();
    }

    private static void renderObjects() {

        bufferHandling();

        slCamera my_cam = new slCamera();
        my_cam.setProjectionOrtho();
        Matrix4f viewProjMatrix = my_cam.getProjectionMatrix();
        boolean unifMatrix4fvTranspose = false;
        glUniformMatrix4fv(vpMatLocation, unifMatrix4fvTranspose,
                viewProjMatrix.get(myFloatBuffer));
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        boolean delayFrame = false, dispFrames = false, fpsCleared = false, dispLiveCells = false,
                dispLiveCleared = false;
        double framesPerSec = 0;
        Boolean[] hasKeyBeenPressed = new Boolean[9];
        Arrays.fill(hasKeyBeenPressed, false);
        Boolean[] arrayCopy = Arrays.copyOf(hasKeyBeenPressed, 9);


        // start render
        while (!glfwWindowShouldClose(window)) {

            glfwPollEvents();
            slEventHandler.processEvent(slEventHandler.Event.RESUME, false);

            if (!haltRender) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                framesPerSec = slBoardHandler.boardAndFrames(delayFrame, framesPerSec);
                delayFrame = slEventHandler.processEvent(slEventHandler.Event.DELAY, delayFrame);
                dispFrames = slEventHandler.processEvent(slEventHandler.Event.FPS, dispFrames);
                dispLiveCells = slEventHandler.processEvent(slEventHandler.Event.LIVE, dispLiveCells);
                slEventHandler.processEvent(slEventHandler.Event.HALT, false);
                if (slEventHandler.processEvent(slEventHandler.Event.LOAD, false)) {
                    bufferHandling();
                }
                slEventHandler.processEvent(slEventHandler.Event.SAVE, false);
                slEventHandler.processEvent(slEventHandler.Event.RESET, false);
                slEventHandler.processEvent(slEventHandler.Event.HELP, false);

                if (dispFrames) {
                    System.out.print("\rFPS: " + (int) Math.round(framesPerSec));
                    fpsCleared = false;
                } else if (!fpsCleared){
                    System.out.print("\r\r\r");
                    fpsCleared = true;
                }

                if (dispLiveCells) {
                    System.out.print("\rLive Cells: " + liveCells);
                    dispLiveCleared = false;
                } else if (!dispLiveCleared){
                    System.out.print("\r\r\r");
                    dispLiveCleared = true;
                }


                slBoardHandler.drawStuff(renderColorLocation);
                glfwSwapBuffers(window);
            }

            if (glfwKeyStatusDisplay) {
                displayKeyValues(hasKeyBeenPressed, dispFrames, dispLiveCells, arrayCopy);
            }
        }
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    } // renderObjects

    private static void displayKeyValues(Boolean[] keyPressed, boolean dispFrames, boolean dispLiveCells,
                                         Boolean[] arrayCopy) {


        boolean dispOn = ((dispFrames || dispLiveCells));

        boolean stuckKey = false;

        int countLast = 0, countCur = 0;
        for (Boolean b : keyPressed) {
            if (b) {
                countCur++;
            }
        }
        for (Boolean b : arrayCopy) {
            if (b) {
                countLast++;
            }
        }

        boolean validL = false, validS = false;
        if (!arrayCopy[4] && countCur < 2)
            validL = true;
        if (!arrayCopy[6] && countCur < 2)
            validS = true;

        arrayCopy = Arrays.copyOf(keyPressed, 9);

        if (validL && countCur < 2) {
            arrayCopy[4] = false;
        } else {
            stuckKey = true;
        }

        if (validS && countCur < 2) {
            arrayCopy[6] = false;
        } else {
            stuckKey = true;
        }

        keyPressed[0] = dispKeyVal(GLFW_KEY_D, "[D]", keyPressed[0], dispOn, stuckKey, false, countCur);
        keyPressed[1] = dispKeyVal(GLFW_KEY_F, "[F]", keyPressed[1], dispOn, stuckKey, false, countCur);
        keyPressed[2] = dispKeyVal(GLFW_KEY_I, "[I]", keyPressed[2], dispOn, stuckKey, false, countCur);
        keyPressed[3] = dispKeyVal(GLFW_KEY_H, "[H]", keyPressed[3], dispOn, stuckKey, false, countCur);
        keyPressed[4] = dispKeyVal(GLFW_KEY_L, "[L]", keyPressed[4], dispOn, stuckKey, true, countCur);
        keyPressed[5] = dispKeyVal(GLFW_KEY_R, "[R]", keyPressed[5], dispOn, stuckKey, false, countCur);
        keyPressed[6] = dispKeyVal(GLFW_KEY_S, "[S]", keyPressed[6], dispOn, stuckKey, true, countCur);
        keyPressed[7] = dispKeyVal(GLFW_KEY_SPACE, "[Space]", keyPressed[7], dispOn, stuckKey, false, countCur);
        keyPressed[8] = dispKeyVal(GLFW_KEY_LEFT_SHIFT, "[?]", keyPressed[8], dispOn, stuckKey, false, countCur);

        countCur = 0;
        countLast = 0;
        for (Boolean b : keyPressed) {
            if (b) {
                countCur++;
            }
        }
        for (Boolean b : arrayCopy) {
            if (b) {
                countLast++;
            }
        }

        if (countCur == (countLast - 1)) {
            stuckKey = true;
        }

        if (stuckKey && (countCur == (countLast - 1))) {
            keyPressed[4] = dispKeyVal(GLFW_KEY_L, "[L]", false, dispOn, stuckKey, true, 0);
            keyPressed[6] = dispKeyVal(GLFW_KEY_S, "[S]", false, dispOn, stuckKey, true, 0);
        }
    }

    private static boolean dispKeyVal(int glfwKey, String keyStr, boolean keyPressed, boolean displayOn, boolean stuck,
                                      boolean problemKey, int countCur) {
        // handle "?" keypress
        if (glfwKey == GLFW_KEY_LEFT_SHIFT) {
            if ((glfwGetKey(window, GLFW_KEY_SLASH) == GLFW_PRESS) &&
                ( (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) ||
                  (glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS) )
            ) {
                System.out.print("\r\r\r");
                System.out.printf("Current GLFW status for key %s is PRESS", keyStr);
                keyPressed = true;
            }
            if (keyPressed) {
                if ((glfwGetKey(window, GLFW_KEY_SLASH) == GLFW_RELEASE) ||
                    ( (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_RELEASE) &&
                        (glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_RELEASE) )
                ) {
                    System.out.printf("\nCurrent GLFW status for key %s is RELEASE\n", keyStr);
                    keyPressed = false;
                }
            }
        // handle all other key presses
        } else {
            if (glfwGetKey(window, glfwKey) == GLFW_PRESS) {
                stuck = countCur > 0;
                if (!keyPressed && displayOn && (stuck && !problemKey)) System.out.print("\r\r\r");
                if (!keyPressed && !(stuck && !problemKey)) System.out.print("\r\r\r");
                if (!keyPressed && stuck && !problemKey && !displayOn) {
                    System.out.println();
                }
                if (!keyPressed && (!(stuck && (problemKey || !displayOn)) || !problemKey))
                    System.out.printf("Current GLFW status for key %s is PRESS", keyStr);
                if (!keyPressed && displayOn) System.out.println();
                keyPressed = true;
            }
            if (keyPressed && !(stuck && problemKey)) {
                if (glfwGetKey(window, glfwKey) == GLFW_RELEASE) {
                    if (displayOn) System.out.print("\r\r\r");
                    else System.out.println();
                    System.out.printf("Current GLFW status for key %s is RELEASE\n", keyStr);
                    keyPressed = false;
                }
            }
        }
        return keyPressed;
    }
}