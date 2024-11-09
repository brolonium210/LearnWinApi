import java.util.Vector;

public class WindowInfo {
    private String className;
    private String windowTitle;

    public WindowInfo(String className, String windowTitle) {
        this.className = className;
        this.windowTitle = windowTitle;
    }

    public String getClassName() {
        return className;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

}
