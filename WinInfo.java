import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;



public class WinInfo {
    private String name;
    private String className;
    private HWND hwnd;
    private RECT rect;
    private int winX;
    private int winY;

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("User32", User32.class);

        HWND FindWindow(String lpClass,String lpName);
        boolean GetWindowRect(HWND hwnd,RECT rect);
    }

    public WinInfo(String name, String className) {
        this.name = name;
        this.className = className;
        this.hwnd = findWindow(this.className,this.name);
        if(hwnd != null) {
            setWinXY();
        }
    }

    public void setWinXY() {
        this.rect = getRect(this.hwnd);
        this.winX = rect.left;
        this.winY = rect.top;
    }

    public String getName() {
        return name;
    }
    public String getClassName() {
        return className;
    }
    public HWND getHWND() {
        return hwnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setHwnd(HWND hwnd) {
        this.hwnd = hwnd;
    }

    private HWND findWindow(String lpClass,String lpName) {
        HWND hwnd = User32.INSTANCE.FindWindow(lpClass,lpName);
        return hwnd;
    }
    private RECT getRect(HWND hwnd) {
        RECT temp = new RECT();
        if(User32.INSTANCE.GetWindowRect(hwnd,temp)){
            return temp;
        }else{
            return null;
        }
    }
}
