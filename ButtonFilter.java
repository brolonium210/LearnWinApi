import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinUser;

import java.io.UnsupportedEncodingException;

public class ButtonFilter {
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        boolean EnumChildWindows(HWND hWndParent, WinUser.WNDENUMPROC lpEnumFunc, Pointer data);
        int GetClassNameW(HWND hWnd, byte[] lpClassName, int nMaxCount);
        int GetWindowTextW(HWND hWnd, byte[] lpString, int nMaxCount);
        boolean GetWindowRect(HWND hWnd, RECT rect);
    }

    public static void listFilteredButtons(HWND parentHwnd) {
        final int minButtonWidth = 50;
        final int maxButtonWidth = 150;
        final int minButtonHeight = 20;
        final int maxButtonHeight = 50;
        final int minX = 0;  // Define the area for button positions if applicable
        final int maxX = 500;
        final int minY = 200;
        final int maxY = 700;

        User32.INSTANCE.EnumChildWindows(parentHwnd, (hWnd, data) -> {
            // Retrieve the class name
            byte[] className = new byte[1024];
            User32.INSTANCE.GetClassNameW(hWnd, className, className.length);
            String classNameStr = null;
            try {
                classNameStr = new String(className, "UTF-16LE").replaceAll("\0", "").trim();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Retrieve the window title (if available)
            byte[] windowText = new byte[1024];
            User32.INSTANCE.GetWindowTextW(hWnd, windowText, windowText.length);
            String windowTitleStr = null;
            try {
                windowTitleStr = new String(windowText, "UTF-16LE").replaceAll("\0", "").trim();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Retrieve the position and dimensions of the child window
            RECT rect = new RECT();
            if (User32.INSTANCE.GetWindowRect(hWnd, rect)) {
                int x = rect.left;
                int y = rect.top;
                int width = rect.right - rect.left;
                int height = rect.bottom - rect.top;

                // Check if this control matches button-like properties
                boolean isButtonSize = (width >= minButtonWidth && width <= maxButtonWidth &&
                        height >= minButtonHeight && height <= maxButtonHeight);
                boolean isButtonPosition = (x >= minX && x <= maxX && y >= minY && y <= maxY);
                boolean isLikelyButton = isButtonSize && isButtonPosition;

                if (isLikelyButton) {
                    System.out.println("Likely Button - Class Name: " + classNameStr +
                            ", Window Title: " + windowTitleStr +
                            ", Position - X: " + x + ", Y: " + y +
                            ", Width: " + width + ", Height: " + height);
                }
            }
            return true; // Continue enumeration
        }, null);
    }

//    public static void main(String[] args) {
//        HWND parentHwnd = User32.INSTANCE.FindWindow(null, "Window Title"); // Replace with the actual window title
//        if (parentHwnd != null) {
//            listFilteredButtons(parentHwnd);
//        } else {
//            System.out.println("Window not found.");
//        }
//    }
}
