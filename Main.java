import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;
//import vector;




public class Main {

    public static void main(String[] args) {
        DeShredder ds =new DeShredder();
//        ds.setupGUI();
//        WindowLister.listVisibleWindows();
        Vector<WindowInfo> windows = WindowLister.listVisibleWindows();


//        HWND hwnd = WindowLister.findWindowByTitle(windows.get(0).getWindowTitle());
//        ChildWindowEnumerator.listAllChildWindows(hwnd);

    }


}
