package com.dctimer.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cs.min2phase.CubieCube;
import cs.min2phase.Util;

public class SmartCube implements Serializable {
    public static final int UNKNOWN = 0;
    public static final int GIIKER_CUBE = 1;
    public static final int GAN_CUBE = 2;
    public static final int GO_CUBE = 3;
    private String name;
    private String address;
    private int type;
    private int connected;
    private int version;
    private String cubeState;
    private int batteryValue;
    private List<Integer> rawData;
    private CubieCube cc;
    private int preIdx;
    private int result;
    private int moves;
    private List<Integer> preMoveList;
    private List<Integer> moveList;
    private StateChangedCallback callback;

    public SmartCube(String name, String address) {
        this.name = name;
        this.address = address;
        rawData = new ArrayList<>();
        cc = new CubieCube();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getConnected() {
        return connected;
    }

    public void setConnected(int connected) {
        this.connected = connected;
    }

    public String getCubeState() {
        return cubeState;
    }

    public int setCubeState(String state) {
        this.cubeState = state;
        return Util.toCubieCube(state, cc);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getBatteryValue() {
        return batteryValue;
    }

    public void setBatteryValue(int batteryValue) {
        this.batteryValue = batteryValue;
    }

    public int getResult() {
        return result;
    }

    public void setStateChangedCallback(StateChangedCallback callback) {
        this.callback = callback;
    }

    public int getMovesCount() {
        return moves;
    }

    public void applyMove(int move, int time, String scramble) {
        rawData.add(move << 16 | time);
        cc = cc.move(move);
        cubeState = Util.toFaceCube(cc);
        if (cubeState.equals(scramble) && callback != null)
            callback.onScrambled(this);
        if (cubeState.equals("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB") && callback != null)
            callback.onSolved(this);
    }

    public void markScrambled() {
        preIdx = rawData.size();
    }

    public void markSolved() {
        cubeState = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";
        cc = new CubieCube();
        rawData = new ArrayList<>();
        preIdx = 0;
    }

    public void calcResult() {
        result = 0;
        moves = rawData.size() - preIdx;
        preMoveList = new ArrayList<>();
        moveList = new ArrayList<>();
        for (int i = 0; i < preIdx; i++) {
            int move = rawData.get(i) >> 16;
            if (preMoveList.size() == 0) preMoveList.add(move);
            else if (preMoveList.get(preMoveList.size() - 1) == move) {
                if (move % 3 == 1) preMoveList.add(move);
                else {
                    int turn = move / 3;
                    preMoveList.add(turn * 3 + 1);
                }
            } else preMoveList.add(move);
        }
        //Log.w("dct", "start "+preIdx+" size "+rawData.size());
        for (int i = preIdx; i < rawData.size(); i++) {
            if (i != preIdx)
                result += rawData.get(i) & 0xffff;
            //Log.w("dct", i+":"+rawData.get(i)+"/"+result);
            int move = rawData.get(i) >> 16;
            if (moveList.size() == 0) moveList.add(move);
            else if (moveList.get(moveList.size() - 1) == move) {
                if (move % 3 == 1) moveList.add(move);
                else {
                    int turn = move / 3;
                    moveList.set(moveList.size() - 1, turn * 3 + 1);
                }
            } else moveList.add(move);
        }
        if (type == GAN_CUBE)
            result = (int) (result / 0.95);
    }

    public String getMoveSequence() {
        StringBuilder sb = new StringBuilder();
        String[] suff = {"", "2", "'"};
        for (int i = 0; i < moveList.size(); i++) {
            int move = moveList.get(i);
            sb.append("URFDLB".charAt(move / 3)).append(suff[move % 3]).append(" ");
        }
        return sb.toString();
    }

    public interface StateChangedCallback {
        void onScrambled(SmartCube cube);
        void onSolved(SmartCube cube);
    }
}
