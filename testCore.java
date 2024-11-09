import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;

public class testCore {
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        HWND FindWindowW(String lpClassName, String lpWindowName);

        boolean GetWindowRectW(HWND hWnd, RECT rect);

        void SetCursorPos(int x, int y);

        void mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);
    }


    public static HWND findWindow(String className, String windowName) {

//        System.out.println(hWnd);
        return User32.INSTANCE.FindWindowW(className, windowName);
    }

    public static boolean getWindowRect(HWND hWnd, RECT rect) {
        return User32.INSTANCE.GetWindowRectW(hWnd, rect);
    }

//    public static boolean getWindowXY(HWND hWnd, RECT rect) {
//        return User32.INSTANCE.GetWindowRectW(hWnd, rect);
//    }



    public static void moveCursorTo(int x, int y) {
        User32.INSTANCE.SetCursorPos(x, y);
    }

    public static void clickAtCurrentPosition() {
        final int MOUSEEVENTF_LEFTDOWN = 0x0002;
        final int MOUSEEVENTF_LEFTUP = 0x0004;
        User32.INSTANCE.mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
        User32.INSTANCE.mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
    }
}
