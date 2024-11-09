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

/**
 * Shred stores information about a shred.
 * All shreds are 40x40 png images.
 */

public class Shred{

    // Fields to store
    //   the name of the image
    //   the id of the image
    public static final double SIZE = 40;
    private String filename;
    private int id;            // ID of the shred


    private double x;
    private double y;


    // Constructor
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Shred() {
        this.filename = "";
        this.id = -1;
        this.x = 0.0;
        this.y = 0.0;
    }


    public Shred(Path dir, int id){
        filename = dir.resolve(id+".png").toString();
        this.id = id;
    }

    /**
     * Draw the shred (no border) at the specified coordinates
     */
    public void draw(double left, double top){
        UI.drawImage(filename, left, top, SIZE, SIZE);
    }

    /**
     * Draw the shred with a border at the specified coordinates
     */
    public void drawWithBorder(double left, double top){
        UI.drawImage(filename, left, top, SIZE, SIZE);
        UI.drawRect(left, top, SIZE, SIZE);
    }

    /**
     * Return a string representation of a Shred
     */
    public String toString(){
        return "ID:"+id;
    }

    public String getFilename() {
        return filename;
    }

    public int getId() {
        return id;
    }

}
