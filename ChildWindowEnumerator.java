import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinUser;

import java.io.UnsupportedEncodingException;

public class ChildWindowEnumerator {
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        boolean EnumChildWindows(HWND hWndParent, WinUser.WNDENUMPROC lpEnumFunc, Pointer data);
        int GetClassNameW(HWND hWnd, byte[] lpClassName, int nMaxCount);
        int GetWindowTextW(HWND hWnd, byte[] lpString, int nMaxCount);
        boolean GetWindowRect(HWND hWnd, RECT rect);
    }

    public static void listAllChildWindows(HWND parentHwnd) {
        User32.INSTANCE.EnumChildWindows(parentHwnd, (hWnd, data) -> {
            // Buffer for class name
            byte[] className = new byte[512];
            User32.INSTANCE.GetClassNameW(hWnd, className, className.length);
            String classNameStr = null;
            try {
                classNameStr = new String(className, "UTF-16LE").replaceAll("\0", "").trim();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Buffer for window title
            byte[] windowText = new byte[512];
            User32.INSTANCE.GetWindowTextW(hWnd, windowText, windowText.length);
            String windowTitleStr = null;
            try {
                windowTitleStr = new String(windowText, "UTF-16LE").replaceAll("\0", "").trim();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Get the position of the child window
            RECT rect = new RECT();
            if (User32.INSTANCE.GetWindowRect(hWnd, rect)) {
                int x = rect.left;
                int y = rect.top;
                int width = rect.right - rect.left;
                int height = rect.bottom - rect.top;

                // Print details of each child window
                System.out.println("Class Name: " + classNameStr +
                        ", Window Title: " + windowTitleStr +
                        ", Position - X: " + x + ", Y: " + y +
                        ", Width: " + width + ", Height: " + height);
            } else {
                System.out.println("Failed to get position for child window.");
            }

            return true; // Continue enumeration
        }, null);
    }

//    public static void main(String[] args) {
//        HWND parentHwnd = User32.INSTANCE.FindWindow(null, "Window Title"); // Replace with actual window title
//        if (parentHwnd != null) {
//            listAllChildWindows(parentHwnd);
//        } else {
//            System.out.println("Parent window not found.");
//        }
//    }
}
