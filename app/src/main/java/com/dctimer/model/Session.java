package com.dctimer.model;

import android.util.Log;

import java.io.Serializable;

public class Session implements Serializable {
    private int id;
    private String name;
    private int puzzle;
    private int count;
    private int multiPhase;
    private int avg;
    private int sorting;

    public Session(int id, String name, int puzzle, int multiPhase, int avg, int sorting) {
        this.id = id;
        this.name = name;
        this.puzzle = puzzle;
        this.multiPhase = multiPhase;
        this.avg = avg;
        this.sorting = sorting;
        //Log.w("dct", "["+id+"]"+name+"/"+sorting);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(int puzzle) {
        this.puzzle = puzzle;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMultiPhase() {
        return multiPhase;
    }

    public void setMultiPhase(int mp) {
        this.multiPhase = mp;
    }

    public int getAvg() {
        return avg;
    }

    public int getRa1() {
        return avg % 1000 + 1;
    }

    public int getRa2() {
        return (avg / 1000) % 1000 + 1;
    }

    public void setRa1(int ra1) {
        int ra2 = avg / 1000;
        avg = ra2 * 1000 + (ra1 - 1);
    }

    public void setRa2(int ra2) {
        int ra1 = avg % 1000;
        avg = (ra2 - 1) * 1000 + ra1;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public void setAvg(int ra1, int ra2) {
        this.avg = (ra1 - 1) + (ra2 - 1) * 1000;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }
}
