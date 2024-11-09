// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2024T2, Assignment 1
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

/**
 * DeShredder allows a user to sort fragments of a shredded document ("shreds") into strips, and
 * then sort the strips into the original document.
 * The program shows
 *   - a list of all the shreds along the top of the window, 
 *   - the working strip (which the user is constructing) just below it.
 *   - the list of completed strips below the working strip.
 * The "rotate" button moves the first shred on the list to the end of the list to let the
 *  user see the shreds that have disappeared over the edge of the window.
 * The "shuffle" button reorders the shreds in the list randomly.
 * The user can use the mouse to drag shreds between the list at the top and the working strip,
 *  and move shreds around in the working strip to get them in order.
 * When the user has the working strip complete, they can move
 *  the working strip down into the list of completed strips, and reorder the completed strips.
 *
 */
public class DeShredder {

    // Fields to store the lists of Shreds and strips.  These should never be null.
    private List<Shred> allShreds = new ArrayList<Shred>();    //  List of all shreds
    private List<Shred> workingStrip = new ArrayList<Shred>(); // Current strip of shreds
    private List<List<Shred>> completedStrips = new ArrayList<List<Shred>>();

    // Constants for the display and the mouse
    public static final double LEFT = 20;       // left side of the display
    public static final double TOP_ALL = 20;    // top of list of all shreds 
    public static final double GAP = 5;         // gap between strips
    public static final double SIZE = Shred.SIZE; // size of the shreds

    public static final double TOP_WORKING = TOP_ALL+SIZE+GAP;
    public static final double TOP_STRIPS = TOP_WORKING+(SIZE+GAP);

    //Fields for recording where the mouse was pressed  (which list/strip and position in list)
    // note, the position may be past the end of the list!
    private List<Shred> fromStrip;   // The strip (List of Shreds) that the user pressed on
    private int fromPosition = -1;   // index of shred in the strip

    private int shredCount = 0;
    private boolean hasGameStarted = false;

    private Shred tempShred;
    private List<Shred> tempList = new ArrayList<Shred>();

    private Integer completedIndex = 0;

    private boolean firstPress = false;
    private boolean isInCompletedRange = false;
    private boolean isFromCompletedRange = false;
    private boolean isToCompletedRange = false;







    /**
     * Initialises the UI window, and sets up the buttons. 
     */
    public void setupGUI() {
        UI.addButton("Load library",   this::loadLibrary);
        UI.addButton("Rotate",         this::rotateList);
        UI.addButton("Shuffle",        this::shuffleList);
        UI.addButton("Complete Strip", this::completeStrip);
        UI.addButton("Quit",           UI::quit);

        UI.setMouseListener(this::doMouse);
        UI.setWindowSize(1000,800);
        UI.setDivider(0);
    }

    private boolean isIttheSolution(Path pathToPng) {
        boolean toBig = false;
        try {
            BufferedImage image = ImageIO.read(pathToPng.toFile());
            int width = image.getWidth();
            if(width>40){
                toBig = true;
            }
//            System.out.println("Width of the image: " + width);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toBig;
    }


    /**
     * Asks user for a library of shreds, loads it, and redisplays.
     * Uses UIFileChooser to let user select library
     * and finds out how many images are in the library
     * Calls load(...) to construct the List of all the Shreds
     */
    public void loadLibrary(){
        Path filePath = Path.of(UIFileChooser.open("Choose first shred in directory"));
        Path directory = filePath.getParent(); //subPath(0, filePath.getNameCount()-1);
        int count=1;
        while(Files.exists(directory.resolve(count+".png"))){ count++; }
        //loop stops when count.png doesn't exist
        count = count-1;
        load(directory, count);   // YOU HAVE TO COMPLETE THE load METHOD
        display();
    }

    /**
     * Empties out all the current lists (the list of all shreds,
     *  the working strip, and the completed strips).
     * Loads the library of shreds into the allShreds list.
     * Parameters are the directory containing the shred images and the number of shreds.
     * Each new Shred needs the directory and the number/id of the shred.
     */
    public void load(Path dir, int count) {
        List<Path> shredLocs = null;
        Stream<Path> shredStream = Stream.empty();
        //follow up on this the way a list is instantiated as an ArrayList
        List<Shred> shredTemp = new ArrayList<Shred>();
        //preload the completed lists so that i dont keep getting null errors
        //the new ones are going to be added to position 0
        //I think this can go as I am just adding another line for every strip I add
//        for (int i = 0; i < 5; i++) {
//            completedStrips.add(new ArrayList<Shred>());
//        }

        try{
            shredStream = Files.list(dir);
//            System.out.println("Stream Success");
            shredLocs = shredStream.toList();
            shredStream.close();
            shredCount = shredLocs.size();
            //closing this stream changed everything ,this was eating up so much memory ,dont know why??
//            BufferedImage img = null;

            int solution = 0;
            for(int i = 0;i<shredLocs.size();i++){
                Path temp = shredLocs.get(i);
                if(!isIttheSolution(temp)){
                    solution = i;
                }
            }
            System.out.println(solution);
//            shredLocs.remove(solution);
//            shredLocs.forEach(element -> if(!isIttheSolution(element)){  });

        }catch(IOException ex){
            System.out.println("problem loading Stream");
        }
//            shredStream.forEach(element -> System.out.println("hello"));
//            shredStream.forEach(System.out::println);
//            shredLocs.forEach(shredTemp.add(Shred()));
//            count = shredLocs.size();
//            for(Path p:shredLocs){
//                System.out.println(count);
//                Shred tShred = new Shred(dir,count);
//                allShreds.add(tShred);
//                count = count-1;
//            }
        count = shredLocs.size();
        for (int i = count - 1; i >= 0; i--) {
            System.out.println(count - i);
            Shred tShred = new Shred(dir, count - i);
            allShreds.add(tShred);
        }
        allShreds.forEach(element ->System.out.println(element.toString()));
//            workingStrip = allShreds;
//            completedStrips.add(allShreds);
//            completedStrips.add(allShreds);
        hasGameStarted = true;

    }

    /**
     * Rotate the list of all shreds by one step to the left
     * and redisplay;
     * Should not have an error if the list is empty
     * (Called by the "Rotate" button)
     */
    public void rotateList(){

        Shred temp = allShreds.get(0);
//        List<Shred> tempShreds = allShreds;
//        tempShreds.remove(0);

        Integer asSize = (allShreds.size()-1);
//        allShreds.addAll(tempShreds);
//        allShreds.set(asSize,temp);
        allShreds.remove(0);
        allShreds.add(temp);
        UI.sleep(100);
        display();

    }

    /**
     * Shuffle the list of all shreds into a random order
     * and redisplay;
     */
    public void shuffleList(){
        /*# YOUR CODE HERE */

    }

    /**
     * Move the current working strip to the end of the list of completed strips.
     * (Called by the "Complete Strip" button)
     */
    public void completeStrip(){
        completedStrips.add(0,new ArrayList<>(workingStrip));
        workingStrip.clear();
        display();

    }

    /**
     * Simple Mouse actions to move shreds and strips
     *  User can
     *  - move a Shred from allShreds to a position in the working strip
     *  - move a Shred from the working strip back into allShreds
     *  - move a Shred around within the working strip.
     *  - move a completed Strip around within the list of completed strips
     *  - move a completed Strip back to become the working strip
     *    (but only if the working strip is currently empty)
     * Moving a shred to a position past the end of a List should put it at the end.
     * You should create additional methods to do the different actions - do not attempt
     *  to put all the code inside the doMouse method - you will lose style points for this.
     * Attempting an invalid action should have no effect.
     * Note: doMouse uses getStrip and getColumn, which are written for you (at the end).
     * You should not change them.
     */
    public void doMouse(String action, double x, double y){
        System.out.println("mouse used");
        System.out.println(" ");
//        for(List<Shred> test:completedStrips){
//            for(Shred test2:test){
//                System.out.println(test2.toString());
//            }
//        }
//        int completedSize = 0;
//        for(List<Shred> test:completedStrips){
//            completedSize+=test.size();
//        }
//        int allShreds_size
//        if((allShreds.size())+(workingStrip.size())+completedSize){
//
//        }
//        if(tempShred.toString() != null){
//            tempShred.draw(x,y);
//        }
//idea
        //boolean firstPress = true when pressed
        //also make an empty temp shred
        //case if action equals pressed and firstpress = false
        //then do the normal stuff,fill temp shred with selected shred
        //remove temp shred from allShreds
        //
        //case if action equals pressed and firstpress is true run tempshred draw

        //case released all other normal stuff + set first press to false again


        if (action.equals("pressed") && hasGameStarted){
//            firstPress = true;
            //this will get shifteed into its own function if it works

            fromStrip = getStrip(y);
//            System.out.println(workingStrip == completedStrips);
            if(fromStrip == allShreds){
                try {
                    fromPosition = getColumn(x);
                    tempShred.setFilename(fromStrip.get(fromPosition).getFilename());
                    tempShred.setId(fromStrip.get(fromPosition).getId());
                } catch (IndexOutOfBoundsException e) {
                    tempShred = null;
//                    System.err.println("Error: Invalid index accessed in fromStrip. Index: " + fromPosition);
//                    e.printStackTrace();
                }
            }
            if(fromStrip == workingStrip){
                isFromCompletedRange = false;
                fromPosition = getColumn(x);
                tempShred.setFilename(fromStrip.get(fromPosition).getFilename());
                tempShred.setId(fromStrip.get(fromPosition).getId());
            }
            else{
                isFromCompletedRange = true;
                tempList = fromStrip;
            }


//            if(isInCompletedRange){
//                isFromCompletedRange = true;
//                tempList.addAll(fromStrip);
//                completedStrips.remove(fromStrip);
//
//            }else {
//                     // the List of shreds to move from (possibly null)
//                fromPosition = getColumn(x);  // the index of the shred to move (may be off the end)
//                tempShred.setFilename(fromStrip.get(fromPosition).getFilename());
//                tempShred.setId(fromStrip.get(fromPosition).getId());
//                fromStrip.remove(fromPosition);
//            }
            //hand back off to the doMouse function ,we now know the strip is coming from the completed
            //list nd will be handled in this manner
//            isInCompletedRange = false;
        }
//        if (action.equals("pressed")&&firstPress){
//            display();
//            tempShred.draw(x,y);
//        }
//        if(action.equals("dragged")){
//            fromStrip = getStrip(y);      // the List of shreds to move from (possibly null)
//            if(isInCompletedRange){
//
//            }else {
//
//                fromPosition = getColumn(x);  // the index of the shred to move (may be off the end)
//                tempShred.setFilename(fromStrip.get(fromPosition).getFilename());
//                tempShred.setId(fromStrip.get(fromPosition).getId());
//                fromStrip.remove(fromPosition);
//            }
////            display();
////            tempShred.draw(x,y);
//        }
//            while(action.equals("dragged")){
//                display();
//                tempShred.draw(x,y);
//            }
        if (action.equals("released") && hasGameStarted){
//            firstPress = false;
            List<Shred> toStrip = getStrip(y); // the List of shreds to move to (possibly null)
            int toPosition = getColumn(x);
            System.out.println(toPosition);
            if(toStrip == allShreds){
                if(fromStrip == allShreds){
                    assert true;
                    //just do nothing
                }else if(fromStrip == workingStrip){
                    if (toStrip.size() < toPosition) {
                        toStrip.add(tempShred);
                        fromStrip.remove(fromPosition);
                    }else if(toPosition < toStrip.size()){
                        toStrip.add(toPosition, tempShred);
                        fromStrip.remove(fromPosition);
                    } else {
                        toStrip.add(toPosition, tempShred);
                        fromStrip.remove(fromPosition);
                    }


//                    toStrip.add(toPosition,tempShred);

                }
                else if(isFromCompletedRange){
                    if(toPosition >= allShreds.size())
                    {
                        allShreds.addAll(tempList);
                        completedStrips.remove(fromStrip);
                    }else {
                        allShreds.addAll(toPosition, tempList);
                        completedStrips.remove(fromStrip);
                    }

                    isFromCompletedRange = false;
                    //fix the remove later dunno how this will shake out
                }
            }if(toStrip == workingStrip){
//                int toPosition = getColumn(x);
                if(fromStrip == allShreds){
                    if (toStrip.size() < toPosition) {
                        toStrip.add(tempShred);
                        fromStrip.remove(fromPosition);
                    } else {
                        toStrip.add(toPosition, tempShred);
                        fromStrip.remove(fromPosition);
                    }

//                    toStrip.add(toPosition,tempShred);

                }
                else if(fromStrip == workingStrip){
                    if (toStrip.size() < toPosition) {
                        fromStrip.remove(fromPosition);
                        toStrip.add(tempShred);

                    } else {
                        fromStrip.remove(fromPosition);
                        toStrip.add(toPosition, tempShred);
                    }


                }
                else if(isFromCompletedRange){
                    if(toPosition > workingStrip.size()){
                        workingStrip.addAll(tempList);
                        completedStrips.remove(fromStrip);
                    }else {
                        workingStrip.addAll(toPosition, tempList);
                        completedStrips.remove(fromStrip);
                    }
                    //fix the remove later dunno how this will shake out
                    isFromCompletedRange = false;
                }
            }else{
                if(fromStrip == allShreds){
                    assert true;
                }
                else if(fromStrip == workingStrip){
                    assert true;
                }
                else if(isFromCompletedRange){
                    if((toPosition > completedStrips.size())||(toStrip == null)){
//                        System.out.println(toPosition);
                        completedStrips.add(tempList);
                        completedStrips.remove(fromStrip);
                    }else if(toPosition >= toStrip.size()){
                        toStrip.addAll(tempList);
                        completedStrips.remove(fromStrip);
                    }else{
                        toStrip.addAll(toPosition, tempList);
                        completedStrips.remove(fromStrip);
                    }

                }
            }


//
//            if(isInCompletedRange && isFromCompletedRange){
//                //at this point we have found that this strip is also in the completed range and is
//                //to have the from strip added to the end of it
//                toStrip.addAll(tempList);
//                isInCompletedRange = false;
////                completedStrips.add(new ArrayList<Shred>());
//            }
//            if(!isInCompletedRange && isFromCompletedRange){
//                fromStrip.addAll(tempList);
//                System.out.println("nope");
//            }else {
//                int toPosition = getColumn(x);     // the index to move the shred to (may be off the end)
            // perform the correct action, depending on the from/to strips/positions
            /*# YOUR CODE HERE */
//            System.out.println(toStrip.get(toPosition));
//            toStrip.forEach(System.out::println);
//                if (toStrip.size() < toPosition) {
//                    toStrip.add(tempShred);
//                } else {
//                    toStrip.add(toPosition, tempShred);
//                }
//            }
//            isInCompletedRange = false;
//            display();
            tempShred = new Shred();
            tempList = new ArrayList<Shred>();
        }

        display();
//        tempShred.draw(x,y);
    }


    // Additional methods to perform the different actions, called by doMouse

    /*# YOUR CODE HERE */

    //=============================================================================
    // Completed for you. Do not change.
    // loadImage and saveImage may be useful for the challenge.

    /**
     * Displays the remaining Shreds, the working strip, and all completed strips
     */
    public void display(){
        UI.clearGraphics();

        // list of all the remaining shreds that haven't been added to a strip
        double x=LEFT;
        for (Shred shred : allShreds){
            shred.drawWithBorder(x, TOP_ALL);
            x+=SIZE;
        }

        //working strip (the one the user is workingly working on)
        x=LEFT;
        for (Shred shred : workingStrip){
            shred.draw(x, TOP_WORKING);
            x+=SIZE;
        }
        UI.setColor(Color.red);
        UI.drawRect(LEFT-1, TOP_WORKING-1, SIZE*workingStrip.size()+2, SIZE+2);
        UI.setColor(Color.black);

        //completed strips
        double y = TOP_STRIPS;
        for (List<Shred> strip : completedStrips){
            x = LEFT;
            for (Shred shred : strip){
                shred.draw(x, y);
                x+=SIZE;
            }
            UI.drawRect(LEFT-1, y-1, SIZE*strip.size()+2, SIZE+2);
            y+=SIZE+GAP;
        }
    }

    /**
     * Returns which column the mouse position is on.
     * This will be the index in the list of the shred that the mouse is on, 
     * (or the index of the shred that the mouse would be on if the list were long enough)
     */
    public int getColumn(double x){
        return (int) ((x-LEFT)/(SIZE));
    }

    /**
     * Returns the strip that the mouse position is on.
     * This may be the list of all remaining shreds, the working strip, or
     *  one of the completed strips.
     * If it is not on any strip, then it returns null.
     */
    public List<Shred> getStrip(double y){
        int row = (int) ((y-TOP_ALL)/(SIZE+GAP));
        if (row<=0){
            return allShreds;
        }
        else if (row==1){
            return workingStrip;
        }
        else if (row-2<completedStrips.size()){
            return completedStrips.get(row-2);
        }
        else {
            return null;
        }
    }


    /**
     * Load an image from a file and return as a two-dimensional array of Color.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public Color[][] loadImage(String imageFileName) {
        if (imageFileName==null || !Files.exists(Path.of(imageFileName))){
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(Files.newInputStream(Path.of(imageFileName)));
            int rows = img.getHeight();
            int cols = img.getWidth();
            Color[][] ans = new Color[rows][cols];
            for (int row = 0; row < rows; row++){
                for (int col = 0; col < cols; col++){                 
                    Color c = new Color(img.getRGB(col, row));
                    ans[row][col] = c;
                }
            }
            return ans;
        } catch(IOException e){UI.println("Reading Image from "+imageFileName+" failed: "+e);}
        return null;
    }

    /**
     * Save a 2D array of Color as an image file
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public  void saveImage(Color[][] imageArray, String imageFileName) {
        int rows = imageArray.length;
        int cols = imageArray[0].length;
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Color c =imageArray[row][col];
                img.setRGB(col, row, c.getRGB());
            }
        }
        try {
            if (imageFileName==null) { return;}
            ImageIO.write(img, "png", Files.newOutputStream(Path.of(imageFileName)));
        } catch(IOException e){UI.println("Image reading failed: "+e);}

    }
}
