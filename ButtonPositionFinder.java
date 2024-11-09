import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinUser;

import java.io.UnsupportedEncodingException;

public class ButtonPositionFinder {
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);
        HWND FindWindowW(String lpClassName, String lpWindowName); // Add this line
        boolean EnumChildWindows(HWND hWndParent, WinUser.WNDENUMPROC lpEnumFunc, Pointer data);
        int GetClassNameW(HWND hWnd, byte[] lpClassName, int nMaxCount);
        boolean GetWindowRect(HWND hWnd, RECT rect);
    }

    public static void listButtonPositions(HWND parentHwnd) {
        User32.INSTANCE.EnumChildWindows(parentHwnd, (hWnd, data) -> {
            // Buffer for class name
            byte[] className = new byte[512];
            User32.INSTANCE.GetClassNameW(hWnd, className, className.length);
            String classNameStr = null;
            try {
                classNameStr = new String(className, "UTF-16LE").replaceAll("\0", "").trim();
                System.out.println(classNameStr);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Check if the child window is a button
            if ("Button".equals(classNameStr)) {
                // Get the button's position
                RECT rect = new RECT();
                if (User32.INSTANCE.GetWindowRect(hWnd, rect)) {
                    int x = rect.left;
                    int y = rect.top;
                    int width = rect.right - rect.left;
                    int height = rect.bottom - rect.top;

                    System.out.println("Button Position - X: " + x + ", Y: " + y +
                            ", Width: " + width + ", Height: " + height);
                } else {
                    System.out.println("Failed to get position for button.");
                }
            }
            return true; // Continue enumeration
        }, null);
    }

//    public static void main(String[] args) {
//        HWND parentHwnd = User32.INSTANCE.FindWindowW(null, "DeShredder – Main.java"); // Replace with actual title
//        if (parentHwnd != null) {
//            listButtonPositions(parentHwnd);
//        } else {
//            System.out.println("Window not found.");
//        }
//    }
}

