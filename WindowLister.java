import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinUser;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class WindowLister {
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);
        HWND FindWindow(String lpClassName, String lpWindowName);
        boolean IsWindowVisible(HWND hWnd);
        boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer data);
        int GetClassNameW(HWND hWnd, byte[] lpClassName, int nMaxCount);  // Use GetClassNameW for Unicode support
        int GetWindowTextW(HWND hWnd, byte[] lpString, int nMaxCount);
    }

    public static String normalizeTitle(String title) {
        // Remove non-ASCII and non-printable characters
        return title.replaceAll("[^\\p{Print}]", "").trim();
    }


    public static Vector<WindowInfo> listVisibleWindows() {
        Vector<WindowInfo> windows = new Vector<>();
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            // Check if the window is visible
            if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                // Buffer for class name
                byte[] className = new byte[1024];
                User32.INSTANCE.GetClassNameW(hWnd, className, className.length);
                String classNameStr = null;
                try {
                    classNameStr = new String(className, "UTF-16LE");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                // Buffer for window title
                byte[] windowText = new byte[1024];
                User32.INSTANCE.GetWindowTextW(hWnd, windowText, windowText.length);
                String windowTitle = null;
                try {
                    windowTitle = new String(windowText, "UTF-16LE");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                // Only display windows with non-empty titles
                if (!windowTitle.trim().isEmpty()) {
                    System.out.println("Class Name: " + normalizeTitle(classNameStr) + "                      Window Title: " + normalizeTitle(windowTitle));
                    windows.add(new WindowInfo(classNameStr, windowTitle));
                }

            }
            return true; // Continue enumeration
        }, null);
        return windows;
    }


    public static HWND findWindowByClassName(String targetClassName) {
        final HWND[] foundHwnd = {null}; // Array to hold the found HWND

        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            byte[] className = new byte[1024];
            User32.INSTANCE.GetClassNameW(hWnd, className, className.length);
            String retrievedClassName = null;
            try {
                retrievedClassName = new String(className, "UTF-16LE");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Normalize both retrieved and target class names
            if (retrievedClassName.equals(targetClassName)) {
                foundHwnd[0] = hWnd; // Set the found HWND
                return false; // Stop enumeration when found
            }
            return true; // Continue enumeration
        }, null);

        return foundHwnd[0]; // Returns the HWND of the matched window or null if not found
    }

    public static HWND findWindowByTitle(String targetTitle) {
        final HWND[] foundHwnd = {null}; // Array to hold the found HWND

        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            // Buffer for window title
            byte[] windowText = new byte[1024];
            User32.INSTANCE.GetWindowTextW(hWnd, windowText, windowText.length);
            String retrievedTitle = null;
            try {
                retrievedTitle = new String(windowText, "UTF-16LE");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Check if the normalized retrieved title matches the target title
            if (retrievedTitle.equals(targetTitle)) {
                foundHwnd[0] = hWnd; // Set the found HWND
                return false; // Stop enumeration when found
            }
            return true; // Continue enumeration
        }, null);

        return foundHwnd[0]; // Returns the HWND of the matched window or null if not found
    }
}
