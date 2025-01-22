# 5-gameOfLifeRenderer

## Update grid of squares to render live and dead cells in real time according to Conway's Game of Life rules.

### Initial assignment:
- Create a board of 1s and 0s to represent the GoL (Game of Life) board in the backend. Update each cell in the
board every render frame to keep track of the live and dead cells.
- Link the rendered square grid to the GoL board and display live and dead cells using different colors in the render window.
- Update given GoL rules to accurately reflect Conway's GoL rules (given rules inaccurately updated live and dead neighbor cells).
- Implement render keyboard controls using keyboard event handlers:
  - Utilize GLFW's keyCallback function to process any key presses along with GLFW's `glfwPollEvents()` method to check for any
new events each frame.
  - Given a keyListener class to implement correctly with the existing project. This handles all general key presses.
  - Must code and implement given functions for different keys.
  - When a key is pressed, the application should pause and wait for a `SPACE` keypress before continuing.
  - Initial keys and functionality required by assignment:
    |Key|Action|
    |-|-|
    |D|Toggle 500ms frame delay|
    |F|Toggle framerate display in console|
    |H|Halt the rendering (\"Pause\" the rendering)|
    |L|Load the GoL Board from a selected file.|
    |R|Randomize the GoL Board and restart (\"Reset\")|
    |S|Save the current Board status to a file selected by the user.|
    |SPACE|Resume rendering (\"Unhalt\" the render, or \"Unpause\")|
    |?|Print help text (Text from this table) to console|
    |ESC|Exit the application|
  - For loading a file: If a file name is selected that doesn't already exist, a new board will be randomly generated, saved to a new file with the chosen name, and loaded back into the program.
  - For saving a file: If a file name is selected that doesn't already exist, or doesn't fulfill the conditions (must have `.ca` file extension), a new file will be generated. Then the current status of the board will be written and saved to the file. Otherwise, the current Board will overwrite all the previous data contained in the file.
<br>

**Completed initial assignment includes:**
- SPOT file: contains all universal variables in one place. This makes things like changing monitor resolution, board size, square
render colors, etc., simple and quick.
- Conway's Game of Life, running in backend: utilizes a 2D array to represent an NxN board of 1s (live cells) and 0s (dead cells).
Each frame, updates every cell according to the GoL rules to either die, come back to life, or remain alive/dead (unchanged).
- Uses OpenGL to directly render multiple vertices onto the screen as a grid of squares. Each square is colored as either alive
or dead. The grid is redrawn every frame, and each square's color is updated to represent its life status.
- Has ability to read keypresses in real time, and perform actions based on those keypresses.
- Can load in a fixed size board from a file and simulate the GoL on that, as well as able to save a board as a file to load it later.

### Personal additions and modifications:

**Optimizations**
Optimized the backend GoL board updates and OpenGL rendering to increase FPS by 600-1000%:
- Update GoL Board cell array update method to be more efficient:
  - Store only live cells in an ArrayList of integer arrays. Used to store only live cells and
their locations (coordinates) on the grid, so we don't have to keep track of dead cells too.
  - Keep track of the minimum and maximum X and Y coordinates that have been updated each frame.
This enables us to only check and update within a certain (smaller) area on the board instead of the entire
grid every time because of the way the cells update.
- Update rendering logic to increase frame rendering speed:
  - Reorganize rendering logic throughout project to optimize rendering pipeline.
  - Optimize to only render live cell squares on a single static background, when applicable. Saves render
time particularly for larger board sizes.
- Other small (~1-3 line) fixes and optimizations.

**Keyboard Features**
Add keyboard features and improve existing key functions, allowing for a smoother user experience:
- New and existing key changes and additions:
  |Key|Action|Updates|
  |-|-|-|
  |F|Toggle framerate display in console|FPS counter updates live in place on console instead of printing new lines each frame|
  |I|Toggle live cell count display in console (overwrites FPS)|New|
  |L|Load the GoL Board from a selected file.|Able to load a board of different dimensions than the current board.|
- Initial assignment calls for the application to halt immediately after every keyboard action. Added ability
to seamlessly continue the flow of the application during a keypress by not halting application after keypresses.
- Add ability to display which keys are currently detected as being pressed, and when they are detected as released,
by GLFW. This is useful for debugging keyboard functions.


**Bug Fixes and Simple Upgrades**

- Fix memory leaks and optimize buffer handling.
- Fix certain keys repeating keypresses (keys would get \"stuck\") by utilizing Java's Robot fuctions to simulate
the release of keys that would otherwise be detected as still held down.
- Separate all keyboard events into separate classes to allow for easier readability, using abstract classes and
children classes to handle all events within the application. This allowed to call a single method to process each
event instead of going through every event function.
- Add ability to have rainbow changing color squares be rendered instead of just one live color for the squares.
- Window resizes according to given monitor resolution, and centers itself in the screen.
- Window size adjusts automatically to accomodate any given size of board, within monitor resolution size.
