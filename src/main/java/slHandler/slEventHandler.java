package slHandler;

public class slEventHandler {

    public enum Event { DELAY, FPS, LIVE, HALT, LOAD, RESET, SAVE, RESUME, HELP; }
    private static boolean staticBool = false;

    public slEventHandler() {  }

    public static boolean processEvent(Event event, boolean bool1) {
        switch (event) {
            case DELAY:
                return slEvent.delayEvent(bool1);
            case FPS:
                return slEvent.framerateDisp(bool1);
            case LIVE:
                return slEvent.liveCellsDisp(bool1);
            case HALT:
                slEvent.haltEvent();
                break;
            case LOAD:
                if (!staticBool) staticBool = slEvent.loadBoard();
                return staticBool;
            case RESET:
                slEvent.resetBoard();
                break;
            case SAVE:
                slEvent.saveBoard();
                break;
            case RESUME:
                staticBool = false;
                slEvent.resumeEvent();
                break;
            case HELP:
                slEvent.helpDisp();
                break;
        }
        return staticBool;
    }
}
