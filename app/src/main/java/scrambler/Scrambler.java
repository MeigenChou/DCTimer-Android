package scrambler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import solver.*;
import cs.min2phase.Tools;

import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.util.Log;

import com.dctimer.APP;

import static scrambler.MegaScramble.*;

public class Scrambler {
    public static final int TYPE_SQ1 = 1;
    public static final int TYPE_CLK = 12;
    public static final int TYPE_MEGA = 18;
    public static final int TYPE_PYR = 17;
    public static final int TYPE_SKW = 16;
    public static final int TYPE_133 = 13;
    public static final int TYPE_233 = 14;
    public static final int TYPE_223 = 15;
    public static final int TYPE_224 = 19;
    public static final int TYPE_334 = 20;
    public static final int TYPE_335 = 21;
    public static final int TYPE_336 = 22;
    public static final int TYPE_337 = 23;
    public static final int TYPE_GEAR = 24;
    public static final int TYPE_15P = 25;
    public static final int TYPE_15PB = 26;
    public static final int TYPE_HLCT = 27;
    public static final int TYPE_UFO = 28;
    public static final int TYPE_REDI = 29;
    public static final int TYPE_REL = 30;
    public static final int TYPE_8PZ = 31;

    public static final int SCRAMBLE_NONE = 0;
    public static final int SCRAMBLING = 1;
    public static final int SCRAMBLING_NEXT = 2;
    public static final int SCRAMBLE_DONE = 3;

    private int category;
    private String scramble;	// 当前打乱
    private List<String> scrambleList;
    private String cubeState;
    private int scrambleIdx = 0;
    private String hint;
    private static short[][] defaultLength = {
            {0, 15, 15, 0, 0, 0, 0, 0, 0, 0, 0},   //2x2
            {25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0},    //3x3
            {40, 40, 40, 8, 40, 0}, //4x4
            {60, 60, 8},    //5x5
            {80, 80, 80, 8},    //6x6
            {100, 100, 100, 8}, //7x7
            {70, 70},   //mega
            {0, 15, 0},    //pyr
            {40, 20, 0, 0, 0},  //sq1
            {0, 0, 0, 0},   //clock
            {0, 15},    //skewb
            {0, 25, 0, 0, 0, 40, 25, 40, 40, 40, 120, 140, 140, 140},   //lmn
            {25, 25},   //cmetrick
            {0, 10},    //gear
            {25, 25, 25},   //siamese
            {80, 80},   //15puzzle
            {25, 40, 20, 20, 25, 25, 8, 40, 0},    //other
            {0, 0, 0, 25, 25, 25, 0, 15},   //3x3 subsets
            {30, 25},   //bandage
            {30, 20},   //mega subsets
            {5, 0, 0, 0, 0, 0, 0},  //relay
            {0, 0, -60, 0, 0, 0, 0, 0, -70, 0, 0, 0, 0, -80, -100, 0, -60, 5},  //wca
    };
    private static String[] rotate5 = {"", "3Fw", "3Fw'", "3Fw 3Uw", "3Fw 3Uw2", "3Fw 3Uw'", "3Fw' 3Uw", "3Fw' 3Uw2", "3Fw' 3Uw'", "3Rw", "3Rw2", "3Rw'",
            "3Rw 3Uw", "3Rw 3Uw2", "3Rw 3Uw'", "3Rw2 3Uw", "3Rw2 3Uw2", "3Rw2 3Uw'", "3Rw' 3Uw", "3Rw' 3Uw2", "3Rw' 3Uw'", "3Uw", "3Uw2", "3Uw'"};
    private static int[] rotateIdx = {-1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0};
    static String[] cubesuff = {"", "2", "'"};
    static String[] cubesuff2 = {"", "'"};

    private SharedPreferences pref;
    public int imageType;
    public int scrambleLen = 0;
    private cs.min2phase.Search cube3 = new cs.min2phase.Search();
    private cs.threephase.Search cube4 = new cs.threephase.Search();
    private cs.sq12phase.Search cubesq = new cs.sq12phase.Search();
    private Megaminx megaminx = new Megaminx();
    private Clock clock = new Clock();
    private Random r = new Random();

    public Scrambler(SharedPreferences sp) {
        this.pref = sp;
        //scrambleList = new ArrayList<>();
    }

    public String getScramble() {
        return scramble;
    }

    public String getScrambleWithHint(boolean landscape) {
        if (scramble == null) return "error";
        if (landscape) return scramble.replace("\n", " ") + hint;
        return scramble + hint;
    }

    public void setScramble(String scramble) {
        this.scramble = scramble;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getScrambleLen() {
        return scrambleLen;
    }

    public void setScrambleLen(int len) {
        scrambleLen = len;
    }

    public String getCubeState() {
        return cubeState;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public int getScrambleListSize() {
        return scrambleList.size();
    }

    private static String[][] scrambleIndicator = {
            {"(2x2)", "(3x3)", "(4x4)", "(5x5)", "(6x6)", "(7x7)", "(3OH)", "(3FT)", "(Pyraminx)", "(Square-1)", "(Skewb)", "(Clock)", "(Megaminx)"},
            {"(2x2)", "(3x3)", "(4x4)", "(5x5)", "(3OH)", "(Pyraminx)", "(Square-1)", "(Skewb)", "(Clock)", "(Megaminx)"},
    };

    public String getScrambleWithIndicator(boolean landscape) {
        if (scrambleIdx >= scrambleList.size()) scrambleIdx = scrambleList.size() - 1;
        String scramble = scrambleList.get(scrambleIdx);
        if (landscape) scramble = scramble.replace("\n", " ");
        if (category > 640 && category < 646) {
            return scrambleIndicator[0][scrambleIdx] + "\n" + scramble;
        } else if (category == 646) {
            return scrambleIndicator[1][scrambleIdx] + "\n" + scramble;
        }
        return "(" + (scrambleIdx + 1) + "/" + scrambleList.size() + ")\n" + scramble;
    }

    public String getNextScramble(boolean landscape) {
        scrambleIdx++;
        if (scrambleIdx >= scrambleList.size()) scrambleIdx = 0;
        return getScrambleWithIndicator(landscape);
    }

    public String getLastScramble(boolean landscape) {
        scrambleIdx--;
        if (scrambleIdx < 0) scrambleIdx = scrambleList.size() - 1;
        return getScrambleWithIndicator(landscape);
    }

    public void updateHint(int idx) {
        if (idx == 0)
            hint = "";
        else if (is333Scramble()) {
            switch (idx) {
                case 1:
                    hint = Cross.solveCross(scramble, APP.solverType[1]);
                    break;
                case 2:
                    hint = Cross.solveXcross(scramble, APP.solverType[1]);
                    break;
                case 3:
                    hint = EOline.solveEoline(scramble, APP.solverType[1]);
                    break;
                case 4:
                    hint = Roux.solveRoux1(scramble, APP.solverType[3]);
                    break;
                case 5:
                    hint = Petrus.solvePetrus(scramble, APP.solverType[2]);
                    break;
                case 6:
                    hint = Cross.solveEofc(scramble, APP.solverType[1]);
                    break;
            }
        } else if (isSqScramble()) {
            hint = Sq1Shape.solve(idx, scramble);
        } else if (is222Scramble()) {
            if (idx == 1)
                hint = Cube2Face.solveFace(scramble, APP.solverType[4]);
            else hint = Cube2Layer.solveFirstLayer(scramble, APP.solverType[4]);
        } else if (isPyrScramble()) {
            hint = PyraminxV.solveV(scramble, idx);
        }
    }

    public void generateScramble(int category, boolean resetLength) {
        this.category = category;
        String scr;
        StringBuilder sb;
        scrambleList = new ArrayList<>();
        hint = "";
        cubeState = "";
        scrambleIdx = 0;
        if (resetLength) {
            if (category < 0) scrambleLen = defaultLength[21][category & 31];
            else scrambleLen = defaultLength[category >> 5][category & 31];
        }
        switch (category) {
            case -32:	//三速
            case -27:	//三单
            case -25:	//脚拧
                scr = scramble333();
                imageType = 3;
                scrambleList.add(scr);
                hint = solve333(scr);
                break;
            case -31:	//四速
                scr = cube4.randomState(false);
                imageType = scr.startsWith("Error") ? 0 : 4;
                scrambleList.add(scr);
                break;
            case -30:	//五速
                scr = scrambleCube(5); imageType = 5;
                scrambleList.add(scr);
                break;
            case -29:	//二阶
                scr = Cube222.scrambleWCA(); imageType = 2;
                scrambleList.add(scr);
                hint = solve222(scr);
                break;
            case -28:	//三盲
                String cube = Tools.randomCube();
                scr = cube3.solution(cube, true, r);
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case -26:	//最少步
                char axis1, axis2, axis3, axis4;
                do {
                    scr = scramble333();
                    String[] seq = scr.split(" ");
                    if (seq.length < 2) {
                        axis1 = axis2 = 'F';
                        axis3 = axis4 = 'R';
                    } else {
                        axis1 = seq[0].charAt(0);
                        axis2 = seq[1].charAt(0);
                        axis3 = seq[seq.length - 1].charAt(0);
                        axis4 = seq[seq.length - 2].charAt(0);
                    }
                } while (axis1 == 'F' || (axis1 == 'B' && axis2 == 'F') || axis3 == 'R' || (axis3 == 'L' && axis4 == 'R'));
                scr = "R' U' F " + scr + "R' U' F";
                imageType = 3;
                scrambleList.add(scr);
                break;
            case -24:	//五魔
                scr = megaminx.scramblestring(Math.abs(scrambleLen)); imageType = TYPE_MEGA;
                scrambleList.add(scr);
                break;
            case -23:	//金字塔
                scr = Pyraminx.scrambleWCA(); imageType = TYPE_PYR;
                scrambleList.add(scr);
                if (APP.solvePyr > 0) hint = PyraminxV.solveV(scr, APP.solvePyr);
                break;
            case -22:	//SQ1
                scr = cubesq.scrambleWCA(); imageType = TYPE_SQ1;
                scrambleList.add(scr);
                hint = Sq1Shape.solve(APP.solveSq1, scr);
                break;
            case -21:	//魔表
                scr = clock.scramble(); imageType = TYPE_CLK;
                scrambleList.add(scr);
                break;
            case -20:	//斜转
                scr = Skewb.scrambleWCA(); imageType = TYPE_SKW;
                scrambleList.add(scr);
                break;
            case -19:	//六阶
                scr = megascramble(new String[][] {{"U", "D", "Uw", "Dw", "3Uw"}, {"R", "L", "Rw", "Lw", "3Rw"}, {"F", "B", "Fw", "Bw", "3Fw"}}, cubesuff, Math.abs(scrambleLen));
                imageType = 6;
                scrambleList.add(scr);
                break;
            case -18:	//七阶
                scr = megascramble(new String[][] {{"U", "D", "Uw", "Dw", "3Uw", "3Dw"}, {"R", "L", "Rw", "Lw", "3Rw", "3Lw"}, {"F", "B", "Fw", "Bw", "3Fw", "3Bw"}}, cubesuff, Math.abs(scrambleLen));
                imageType = 7;
                scrambleList.add(scr);
                break;
            case -17:	//四盲
                scr = cube4.randomState(true); imageType = 4;
                scrambleList.add(scr);
                break;
            case -16:	//五盲
                scr = scrambleCube(5);
                String[] moves = scr.split(" ");
                String lm = moves[moves.length - 1];
                int idx;
                if (lm.startsWith("Bw")) idx = 2;
                else if (lm.startsWith("Lw")) idx = 1;
                else if (lm.startsWith("Dw")) idx = 0;
                else idx = 4;
                int rot;
                do {
                    rot = r.nextInt(24);
                } while (idx == rotateIdx[rot]);
                scr += " " + rotate5[rot];
                imageType = 5;
                scrambleList.add(scr);
                break;
            case -15:	//多盲
                sb = new StringBuilder();
                for (int j = 1; j <= scrambleLen; j++) {
                    String face = Tools.randomCube();
                    String sol = cube3.solution(face, true, r);
                    sb.append(j).append(") ").append(sol);
                    scrambleList.add(sol);
                    if (j < scrambleLen) sb.append("\n");
                }
                scr = sb.toString(); imageType = TYPE_REL; break;
            case 0: //2阶
                scr = Cube222.scramble();
                imageType = 2;
                scrambleList.add(scr);
                hint = solve222(scr);
                break;
            case 1:
                scr = scrambleCube(2);
                imageType = 2;
                scrambleList.add(scr);
                hint = solve222(scr);
                break;
            case 2:
                scr = megascramble(new String[][][] {{{"U", "D"}}, {{"R", "L"}}, {{"F", "B"}}}, cubesuff, scrambleLen);
                imageType = 2;
                scrambleList.add(scr);
                //hint = "\n" + Cube2l.solveFirstLayer(scr, APP.solve222);
                break;
            case 3:
            case 4:
            case 5:
                scr = Cube222.scrambleEG(category - 3); imageType = 2;
                scrambleList.add(scr);
                break;
            case 6:
                scr = Cube222.scramblePBL(); imageType = 2;
                scrambleList.add(scr);
                break;
            case 7:
                scr = Cube222.scrambleEG(APP.egtype, APP.egolls); imageType = 2;
                scrambleList.add(scr);
                break;
            case 8:
                scr = Cube222.scrambleTCLL(1); imageType = 2;
                scrambleList.add(scr);
                break;
            case 9:
                scr = Cube222.scrambleTCLL(2); imageType = 2;
                scrambleList.add(scr);
                break;
            case 10:
                scr = Cube222.scrambleNobar(); imageType = 2;
                scrambleList.add(scr);
                break;
            case 11:
                scr = Cube222.scrambleTEG1(1); imageType = 2;
                scrambleList.add(scr);
                break;
            case 12:
                scr = Cube222.scrambleTEG1(2); imageType = 2;
                scrambleList.add(scr);
                break;
            case 13:
                scr = Cube222.scrambleTEG2(1); imageType = 2;
                scrambleList.add(scr);
                break;
            case 14:
                scr = Cube222.scrambleTEG2(2); imageType = 2;
                scrambleList.add(scr);
                break;
            case 32: //3阶
                scr = scrambleCube(3); imageType = 3;
                scrambleList.add(scr);
                hint = solve333(scr);
                break;
            case 33:
                scr = scramble333();
                imageType = 3;
                scrambleList.add(scr);
                hint = solve333(scr);
                Log.w("dct", "求解 "+hint);
                break;
            case 34:    //F2L
                scr = cube3.solution(cubeState = Tools.randomCrossSolved());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 35:    //OLL
                scr = cube3.solution(cubeState = Tools.randomLastLayer());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 36:    //PLL
                do {
                    scr = cube3.solution(cubeState = Tools.randomPLL());
                } while (scr.length() < 6);
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 37:
                scr = cube3.solution(cubeState = Tools.randomCornerSolved());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 38:
                scr = cube3.solution(cubeState = Tools.randomEdgeSolved());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 39:
                scr = cube3.solution(cubeState = Tools.randomLastSlot());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 40:
                scr = cube3.solution(cubeState = Tools.randomZBLastLayer());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 41:
                scr = cube3.solution(cubeState = Tools.randomEdgeOfLastLayer());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 42:
                scr = cube3.solution(cubeState = Tools.randomCornerOfLastLayer());
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 43:
                switch (r.nextInt(4)) {
                    case 0:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{0, 1, 2, 3, 4, 5, 6, 7}, new int[]{0, 0, 0, 0, 0, 0, 0, 0}, new int[]{-1, -1, -1, -1, 4, -1, 6, -1, 8, 9, 10, 11}, new int[]{-1, -1, -1, -1, 0, -1, 0, -1, 0, 0, 0, 0}));
                        break;
                    case 1:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{3, 2, 6, 7, 0, 1, 5, 4}, new int[]{2, 1, 2, 1, 1, 2, 1, 2}, new int[]{11, -1, 10, -1, 8, -1, 9, -1, 0, 2, -1, -1}, new int[]{0, -1, 0, -1, 0, -1, 0, -1, 0, 0, -1, -1})) + "x'";
                        break;
                    case 2:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{7, 6, 5, 4, 3, 2, 1, 0}, new int[]{0, 0, 0, 0, 0, 0, 0, 0}, new int[]{4, -1, 6, -1, -1, -1, -1, -1, 11, 10, 9, 8}, new int[]{0, -1, 0, -1, -1, -1, -1, -1, 0, 0, 0, 0})) + "x2";
                        break;
                    default:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{4, 5, 1, 0, 7, 6, 2, 3}, new int[]{2, 1, 2, 1, 1, 2, 1, 2}, new int[]{8, -1, 9, -1, 11, -1, 10, -1, -1, -1, 2, 0}, new int[]{0, -1, 0, -1, 0, -1, 0, -1, -1, -1, 0, 0})) + "x";
                        break;
                }
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 44:
                switch (r.nextInt(4)) {
                    case 0:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{-1, -1, -1, -1, 4, 5, 6, 7}, new int[]{-1, -1, -1, -1, 0, 0, 0, 0}, new int[]{-1, -1, -1, -1, 4, -1, 6, -1, 8, 9, 10, 11}, new int[]{-1, -1, -1, -1, 0, -1, 0, -1, 0, 0, 0, 0}));
                        break;
                    case 1:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{3, 2, -1, -1, 0, 1, -1, -1}, new int[]{2, 1, -1, -1, 1, 2, -1, -1}, new int[]{11, -1, 10, -1, 8, -1, 9, -1, 0, 2, -1, -1}, new int[]{0, -1, 0, -1, 0, -1, 0, -1, 0, 0, -1, -1})) + "x'";
                        break;
                    case 2:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{7, 6, 5, 4, -1, -1, -1, -1}, new int[]{0, 0, 0, 0, -1, -1, -1, -1}, new int[]{4, -1, 6, -1, -1, -1, -1, -1, 11, 10, 9, 8}, new int[]{0, -1, 0, -1, -1, -1, -1, -1, 0, 0, 0, 0})) + "x2";
                        break;
                    default:
                        scr = cube3.solution(cubeState = Tools.randomState(new int[]{-1, -1, 1, 0, -1, -1, 2, 3}, new int[]{-1, -1, 2, 1, -1, -1, 1, 2}, new int[]{8, -1, 9, -1, 11, -1, 10, -1, -1, -1, 2, 0}, new int[]{0, -1, 0, -1, 0, -1, 0, -1, -1, -1, 0, 0})) + "x";
                        break;
                }
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 45:
                scr = cube3.solution(cubeState = Tools.randomEdgePerm());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 46:
                scr = cube3.solution(cubeState = Tools.randomEdgeOri());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 47:
                scr = cube3.solution(cubeState = Tools.randomCornerPerm());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 48:
                scr = cube3.solution(cubeState = Tools.randomCornerOri());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 49:
                scr = cube3.solution(cubeState = Tools.randomPermutation());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 50:
                scr = cube3.solution(cubeState = Tools.randomOrientation());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 51:
                scr = cube3.solution(cubeState = Tools.randomEasyCross(scrambleLen));
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                hint = solve333(scr);
                break;
            case 52:    //2gll
                scr = cube3.solution(cubeState = Tools.randomState(Tools.STATE_SOLVED, new int[]{-1, -1, -1, -1, 0, 0, 0, 0}, new int[]{-1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11}, Tools.STATE_SOLVED));
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 53:    //zbls
                scr = cube3.solution(cubeState = Tools.randomZBLastSlot());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 54:    //zzll
                scr = cube3.solution(cubeState = Tools.randomZZLastLayer());
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 55:    //3循环棱块
                do {
                    scr = cube3.solution(cubeState = Tools.random3Edge());
                } while (scr.length() < 6);
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 56:    //3循环角块
                do {
                    scr = cube3.solution(cubeState = Tools.random3Corner());
                } while (scr.length() < 6);
                imageType = scr.startsWith("Error") ? 0 : 3;
                scrambleList.add(scr);
                break;
            case 64: //4阶
                scr = scrambleCube(4); imageType = 4;
                scrambleList.add(scr);
                break;
            case 65:
                scr = megascramble(new String[][] {{"U", "D", "u"}, {"R", "L", "r"}, {"F", "B", "f"}}, cubesuff, scrambleLen); imageType = 4;
                scrambleList.add(scr);
                break;
            case 66:
                scr = yj4x4(scrambleLen); imageType = 4;
                scrambleList.add(scr);
                break;
            case 67:
                scr = edgescramble("Rw Bw2", new String[] {"Bw2 Rw'", "Bw2 U2 Rw U2 Rw U2 Rw U2 Rw"}, new String[] {"Uw"}, scrambleLen);
                imageType = 4;
                scrambleList.add(scr);
                break;
            case 68:
                scr = megascramble(new String[][] {{"U", "u"}, {"R", "r"}}, cubesuff, scrambleLen);
                imageType = 4;
                scrambleList.add(scr);
                break;
            case 69:	//TODO
                scr = cube4.randomCenter(); imageType = 4;
                scrambleList.add(scr);
                break;
            case 96: //5阶
                scr = scrambleCube(5); imageType = 5;
                scrambleList.add(scr);
                break;
            case 97:
                scr = megascramble(new String[][] {{"U", "D", "u", "d"}, {"R", "L", "r", "l"}, {"F", "B", "f", "b"}}, cubesuff, scrambleLen); imageType = 5;
                scrambleList.add(scr);
                break;
            case 98:
                scr = edgescramble("Rw R Bw B", new String[] {"B' Bw' R' Rw'", "B' Bw' R' U2 Rw U2 Rw U2 Rw U2 Rw"}, new String[] {"Uw", "Dw"}, scrambleLen);
                imageType = 5; break;
            case 128: //6阶
                scr = scrambleCube(6); imageType = 6;
                scrambleList.add(scr);
                break;
            case 129:
                scr = megascramble(new String[][] {{"U", "D", "u", "d", "3u"}, {"R", "L", "r", "l", "3r"}, {"F", "B", "f", "b", "3f"}}, cubesuff, scrambleLen); imageType = 6;
                scrambleList.add(scr);
                break;
            case 130:
                scr = megascramble(new String[][] {{"U", "D", "U\u00B2", "D\u00B2", "U\u00B3"}, {"R", "L", "R\u00B2", "L\u00B2", "R\u00B3"}, {"F", "B", "F\u00B2", "B\u00B2", "F\u00B3"}}, cubesuff, scrambleLen);
                imageType = 6;
                scrambleList.add(scr);
                break;
            case 131:
                scr = edgescramble("3r r 3b b", new String[] {"3b' b' 3r' r'", "3b' b' 3r' U2 r U2 r U2 r U2 r", "3b' b' r' U2 3r U2 3r U2 3r U2 3r", "3b' b' r2 U2 3r U2 3r U2 3r U2 3r U2 r"},
                        new String[] {"u", "3u", "d"}, scrambleLen);
                imageType = 6;
                scrambleList.add(scr);
                break;
            case 160: //7阶
                scr = scrambleCube(7); imageType = 7;
                scrambleList.add(scr);
                break;
            case 161:
                scr = megascramble(new String[][] {{"U", "D", "u", "d", "3u", "3d"}, {"R", "L", "r", "l", "3r", "3l"}, {"F", "B", "f", "b", "3f", "3b"}}, cubesuff, scrambleLen); imageType = 7;
                scrambleList.add(scr);
                break;
            case 162:
                scr = megascramble(new String[][] {{"U", "D", "U\u00B2", "D\u00B2", "U\u00B3", "D\u00B3"}, {"R", "L", "R\u00B2", "L\u00B2", "R\u00B3", "L\u00B3"}, {"F", "B", "F\u00B2", "B\u00B2", "F\u00B3", "B\u00B3"}}, cubesuff, scrambleLen);
                imageType = 7;
                scrambleList.add(scr);
                break;
            case 163:
                scr = edgescramble("3r r 3b b", new String[] {"3b' b' 3r' r'", "3b' b' 3r' U2 r U2 r U2 r U2 r", "3b' b' r' U2 3r U2 3r U2 3r U2 3r", "3b' b' r2 U2 3r U2 3r U2 3r U2 3r U2 r"},
                        new String[] {"u", "3u", "3d", "d"}, scrambleLen);
                imageType = 7;
                scrambleList.add(scr);
                break;
            case 192: //五魔
                scr = megaminx.scramblestring(scrambleLen); imageType = TYPE_MEGA;
                scrambleList.add(scr);
                break;
            case 193:
                scr = oldminxscramble(scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 224: //金字塔
                scr = Pyraminx.scramble(); imageType = TYPE_PYR;
                scrambleList.add(scr);
                if (APP.solvePyr > 0) hint = PyraminxV.solveV(scr, APP.solvePyr);
                break;
            case 225:
                String[][] ss = {{"", "b ", "b' "}, {"", "l ", "l' "}, {"", "u ", "u' "}, {"", "r ", "r' "}};
                int cnt = 0;
                int[] rnd = new int[4];
                for (int i = 0; i < 4; i++) {
                    rnd[i] = r.nextInt(3);
                    if (rnd[i] > 0) cnt++;
                    if (cnt >= scrambleLen) break;
                }
                scrambleLen -= cnt;
                scr = ss[0][rnd[0]] + ss[1][rnd[1]] + ss[2][rnd[2]] + ss[3][rnd[3]] + megascramble(new String[][] {{"U"}, {"L"}, {"R"}, {"B"}}, cubesuff2, scrambleLen);
                scrambleLen += cnt;
                imageType = TYPE_PYR;
                scrambleList.add(scr);
                if (APP.solvePyr > 0) hint = PyraminxV.solveV(scr, APP.solvePyr);
                break;
            case 226:   //L4E
                scr = Pyraminx.scrambleL4E();
                imageType = TYPE_PYR;
                scrambleList.add(scr);
                break;
            case 256:  //SQ1
                scr = SQ1.scramblestring(scrambleLen);
                imageType = TYPE_SQ1;
                scrambleList.add(scr);
                hint = Sq1Shape.solve(APP.solveSq1, scr);
                break;
            case 257:
                scr = sq1Scramble(0, scrambleLen);
                imageType = TYPE_SQ1;
                scrambleList.add(scr);
                hint = Sq1Shape.solve(APP.solveSq1, scr);
                break;
            case 258:
                scr = cubesq.scramble();
                imageType = TYPE_SQ1;
                scrambleList.add(scr);
                hint = Sq1Shape.solve(APP.solveSq1, scr);
                break;
            case 259:
                scr = cubesq.scramble(1037);
                imageType = TYPE_SQ1;
                scrambleList.add(scr);
                break;
            case 260:
                scr = cubesq.scramblePLL(r);
                imageType = TYPE_SQ1;
                scrambleList.add(scr);
                break;
            case 288:	//魔表
                scr = clock.scramble();
                imageType = TYPE_CLK;
                scrambleList.add(scr);
                break;
            case 289:
                scr = clock.scrambleJaap(false);
                imageType = TYPE_CLK;
                scrambleList.add(scr);
                break;
            case 290:
                scr = clock.scrambleJaap(true);
                imageType = TYPE_CLK;
                scrambleList.add(scr);
                break;
            case 291:
                scr = clock.scrambleEpo();
                imageType = TYPE_CLK;
                scrambleList.add(scr);
                break;
            case 320:	//Skewb
                scr = Skewb.scramble();
                imageType = TYPE_SKW;
                scrambleList.add(scr);
                break;
            case 321:
                scr = megascramble(new String[][] {{"R"}, {"U"}, {"L"}, {"B"}}, cubesuff2, scrambleLen);
                imageType = TYPE_SKW;
                scrambleList.add(scr);
                break;
            case 352:	//MxNxL
                scr = Floppy.scramble(); imageType = TYPE_133;
                scrambleList.add(scr);
                break;
            case 353:
                scr = megascramble(new String[][] {{"R", "L"}, {"U", "D"}}, cubesuff, scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 354:
                scr = Domino.scramble(); imageType = TYPE_233;
                scrambleList.add(scr);
                break;
            case 355:
                scr = Tower.scramble(); imageType = TYPE_223;
                scrambleList.add(scr);
                break;
            case 356:
                scr = RTower.scramble(); imageType = 0;
                scrambleList.add(scr);
                break;
            case 357:	//334
                scr = megascramble(new String[][][] {{{"U", "U'", "U2"}, {"u", "u'", "u2"}}, {{"R2", "L2", "M2"}}, {{"F2", "B2", "S2"}}}, null, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 358:	//335
                scr = megascramble(new String[][][] {{{"U", "U'", "U2"}, {"D", "D'", "D2"}}, {{"R2"}, {"L2"}}, {{"F2"}, {"B2"}}}, null, scrambleLen)
                        + "/ " + scramble333();
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 359:	//336
                scr = megascramble(new String[][][] {{{"U", "U'", "U2"}, {"u", "u'", "u2"}, {"3u", "3u'", "3u2"}}, {{"R2", "L2", "M2"}}, {{"F2", "B2", "S2"}}}, null, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 360:	//337
                scr = megascramble(new String[][][] {{{"U", "U'", "U2"}, {"u", "u'", "u2"}, {"D", "D'", "D2"}, {"d", "d'", "d2"}}, {{"R2"}, {"L2"}}, {{"F2"}, {"B2"}}}, null, scrambleLen)
                        + "/ " + scramble333();
                imageType = 0;
                scrambleList.add(scr);
                break;
//            case 361:   //339
//                scr = megascramble(new String[][][] {{{"U", "U'", "U2"}, {"u", "u'", "u2"}, {"3u", "3u'", "3u2"}, {"4u", "4u'", "4u2"}, {"4d", "4d'", "4d2"}, {"3d", "3d'", "3d2"}, {"d", "d'", "d2"}, {"D", "D'", "D2"}}, {{"R2"}, {"L2"}}, {{"F2"}, {"B2"}}}, null)
//                    + "/ " + Cube.scramblestring(3, 25);
//                imageType = 0; break;
            case 361:   //446
                scr = megascramble(new String[][][] {{{"U", "U'", "U2"}, {"D", "D'", "D2"}}, {{"R2", "r2", "L2"}}, {{"F2", "f2", "B2"}}}, null, scrambleLen)
                    + "/ " + megascramble(new String[][] {{"U", "u", "D"}, {"R", "r", "L"}, {"F", "f", "B"}}, cubesuff, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 362:   //8x8
                scr = scrambleCube(8); imageType = 8;
                scrambleList.add(scr);
                break;
            case 363:   //9x9
                scr = scrambleCube(9); imageType = 9;
                scrambleList.add(scr);
                break;
            case 364:   //10x10
                scr = scrambleCube(10); imageType = 10;
                scrambleList.add(scr);
                break;
            case 365:   //11x11
                scr = scrambleCube(11); imageType = 11;
                scrambleList.add(scr);
                break;
            case 384:	//Cmetrick
                scr = megascramble(new String[][][] {{{"U<", "U>", "U2"}, {"E<", "E>", "E2"}, {"D<", "D>", "D2"}}, {{"R^", "Rv", "R2"}, {"M^", "Mv", "M2"}, {"L^", "Lv", "L2"}}}, null, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 385:
                scr = megascramble(new String[][][] {{{"U<", "U>", "U2"}, {"D<", "D>", "D2"}}, {{"R^", "Rv", "R2"}, {"L^", "Lv", "L2"}}}, null, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 416:	//齿轮
                scr = Gear.scramble(); imageType = 0;
                scrambleList.add(scr);
                break;
            case 417:
                scr = megascramble(new String[][] {{"U"}, {"R"}, {"F"}}, new String[] {"", "2", "3", "4", "5", "6", "'", "2'", "3'", "4'", "5'"}, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 448:	//Siamese Cube
                String[][] turn = {{"U", "u"}, {"R", "r"}};
                scr = megascramble(turn, cubesuff, scrambleLen) + "z2 " + megascramble(turn, cubesuff, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 449:
                turn = new String[][] {{"R", "r"}, {"U"}};
                scr = megascramble(turn, cubesuff, scrambleLen) + "z2 " + megascramble(turn, cubesuff, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 450:
                turn = new String[][] {{"U"}, {"R"}, {"F"}};
                scr = megascramble(turn, cubesuff, scrambleLen) + "z2 y " + megascramble(turn, cubesuff, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 480:	//15 puzzle
                scr = do15puzzle(false, scrambleLen); imageType = TYPE_15P;
                scrambleList.add(scr);
                break;
            case 481:
                scr = do15puzzle(true, scrambleLen); imageType = TYPE_15PB;
                scrambleList.add(scr);
                break;
            case 512:	//Other
                scr = LatchCube.scramble(scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 513:
                scr = helicubescramble(scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 514:	//Sq2
                int i = 0;
                sb = new StringBuilder();
                while (i < scrambleLen) {
                    int rndu = r.nextInt(12) - 5;
                    int rndd = r.nextInt(12) - 5;
                    if (rndu != 0 || rndd != 0) {
                        i++;
                        sb.append('(').append(rndu).append(',').append(rndd).append(") / ");
                    }
                }
                scr = sb.toString(); imageType = 0;
                scrambleList.add(scr);
                break;
            case 515:	//super sq1
                scr = ssq1tScramble(scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 516:	//UFO
                scr = megascramble(new String[][][] {{{"A"}}, {{"B"}}, {{"C"}}, {{"U", "U'", "U2'", "U2", "U3"}}}, null, scrambleLen);
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 517:	//FTO
                scr = megascramble(new String[][] {{"U", "D"}, {"F", "B"}, {"L", "BR"}, {"R", "BL"}}, cubesuff2, scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 518:   //Redi
                scr = rediScramble(cubesuff2, scrambleLen);
                imageType = TYPE_REDI;
                scrambleList.add(scr);
                break;
            case 519:   //Master pyraminx
                ss = new String[][] {{"", "l ", "l' "}, {"", "r ", "r' "}, {"", "b ", "b' "}, {"", "u ", "u' "}};
                rnd = new int[4];
                for (i = 0; i < 4; i++) {
                    rnd[i] = r.nextInt(3);
                }
                scr = megascramble(new String[][] {{"U", "Uw"}, {"L", "Lw"}, {"R", "Rw"}, {"B", "Bw"}}, cubesuff2, scrambleLen) + ss[0][rnd[0]] + ss[1][rnd[1]] + ss[2][rnd[2]] + ss[3][rnd[3]];
                imageType = 0;
                scrambleList.add(scr);
                break;
            case 520:   //8 puzzle
                scr = EightPuzzle.scramble(r);
                imageType = TYPE_8PZ;
                scrambleList.add(scr);
                break;
            case 543:   //Gigaminx
                scr = gigascramble(scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 544:	//3x3x3 subsets
                //scr = megascramble(new String[][] {{"U"}, {"R"}}, csuff);
                scr = CubeRU.scramble(false); imageType = 3;
                scrambleList.add(scr);
                break;
            case 545:
                //scr = megascramble(new String[][] {{"U"}, {"L"}, csuff);
                scr = CubeRU.scramble(true); imageType = 3;
                scrambleList.add(scr);
                break;
            case 546:
                //scr = megascramble(new String[][] {{"U"}, {"M"}, csuff);
                scr = RouxMU.scramble(r); imageType = 3;
                scrambleList.add(scr);
                break;
            case 547:
                scr = megascramble(new String[][] {{"U"}, {"R"}, {"F"}}, cubesuff, scrambleLen); imageType = 3;
                scrambleList.add(scr);
                break;
            case 548:
                scr = megascramble(new String[][] {{"R", "L"}, {"U"}}, cubesuff, scrambleLen); imageType = 3;
                scrambleList.add(scr);
                break;
            case 549:
                scr = megascramble(new String[][] {{"R", "r"}, {"U"}}, cubesuff, scrambleLen); imageType = 3;
                scrambleList.add(scr);
                break;
            case 550:
                //turn2 = new String[][] {{"U", "D"}, {"R", "L"}, {"F", "B"}};
                //scr = megascramble(turn2, new String[] {"2"}, 25);
                scr = HalfTurn.scramble(r); imageType = 3;
                scrambleList.add(scr);
                break;
            case 551:	//LSLL
                scr = megascramble(new String[][][] {{{"R U R'", "R U2 R'", "R U' R'"}}, {{"F' U F", "F' U2 F", "F' U' F"}}, {{"U", "U2", "U'"}}}, null, scrambleLen);
                imageType = 3;
                scrambleList.add(scr);
                break;
            case 576:	//Bandaged Cube
                scr = bicube(scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 577:
                scr = sq1Scramble(2, scrambleLen); imageType = TYPE_SQ1; break;
            case 608:	//五魔子集
                scr = megascramble(new String[][] {{"U"}, {"R"}}, new String[] {"", "2", "2'", "'"}, scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 609:
                scr = megascramble(new String[][][] {{{"R U R'", "R U2 R'", "R U' R'", "R U2' R'"}}, {{"F' U F", "F' U2 F", "F' U' F", "F' U2' F"}}, {{"U", "U2", "U'", "U2'"}}}, null, scrambleLen); imageType = 0;
                scrambleList.add(scr);
                break;
            case 640:	//连拧
                sb = new StringBuilder();
                for (int j = 1; j <= scrambleLen; j++) {
                    String sol = scramble333();
                    sb.append(j).append(") ").append(sol);
                    scrambleList.add(sol);
                    if (j < scrambleLen) sb.append("\n");
                }
                scr = sb.toString();
                imageType = TYPE_REL; break;
            case 641:
                String s2 = Cube222.scramble();
                String s3 = scramble333();
                String s4 = scramble444();
                scr = "2) " + s2 + "\n3) " + s3 + "\n4) " + s4;
                scrambleList.add(s2);
                scrambleList.add(s3);
                scrambleList.add(s4);
                imageType = TYPE_REL; break;
            case 642:
                s2 = Cube222.scramble();
                s3 = scramble333();
                s4 = scramble444();
                String s5 = scramble555();
                scr = "2) " + s2 + "\n3) " + s3 + "\n4) " + s4 + "\n5) " + s5;
                scrambleList.add(s2);
                scrambleList.add(s3);
                scrambleList.add(s4);
                scrambleList.add(s5);
                imageType = TYPE_REL; break;
            case 643:
                s2 = Cube222.scramble();
                s3 = scramble333();
                s4 = scramble444();
                s5 = scramble555();
                String s6 = scramble666();
                scr = "2) " + s2 + "\n3) " + s3 + "\n4) " + s4 +"\n5) " + s5 + "\n6) " + s6;
                scrambleList.add(s2);
                scrambleList.add(s3);
                scrambleList.add(s4);
                scrambleList.add(s5);
                scrambleList.add(s6);
                imageType = TYPE_REL; break;
            case 644:
                s2 = Cube222.scramble();
                s3 = scramble333();
                s4 = scramble444();
                s5 = scramble555();
                s6 = scramble666();
                String s7 = scramble777();
                scr = "2) " + s2 + "\n3) " + s3 + "\n4) " + s4 +"\n5) " + s5 + "\n6) " + s6 + "\n7) " + s7;
                scrambleList.add(s2);
                scrambleList.add(s3);
                scrambleList.add(s4);
                scrambleList.add(s5);
                scrambleList.add(s6);
                scrambleList.add(s7);
                imageType = TYPE_REL; break;
            case 645:
                s2 = Cube222.scramble();
                s3 = scramble333();
                s4 = scramble444();
                s5 = scramble555();
                s6 = scramble666();
                s7 = scramble777();
                String oh = scramble333();
                String ft = scramble333();
                String pyr = Pyraminx.scramble();
                String sq1 = cubesq.scramble();
                String skw = Skewb.scramble();
                String clk = clock.scramble();
                String mega = megaminx.scramblestring(70);
                scr = "2x2) " + s2 + "\n3x3) " + s3 + "\n4x4) " + s4 +"\n5x5) " + s5 + "\n6x6) " + s6 + "\n7x7) " + s7 + "\n3OH) " + oh + "\n3FT) " + ft + "\nPyra) " + pyr + "\nSQ1) " + sq1 + "\nSkewb) " + skw + "\nClock) " + clk + "\nMega) " + mega;
                scrambleList.add(s2);
                scrambleList.add(s3);
                scrambleList.add(s4);
                scrambleList.add(s5);
                scrambleList.add(s6);
                scrambleList.add(s7);
                scrambleList.add(oh);
                scrambleList.add(ft);
                scrambleList.add(pyr);
                scrambleList.add(sq1);
                scrambleList.add(skw);
                scrambleList.add(clk);
                scrambleList.add(mega);
                imageType = TYPE_REL; break;
            case 646:
                s2 = Cube222.scramble();
                s3 = scramble333();
                s4 = scramble444();
                s5 = scramble555();
                oh = scramble333();
                pyr = Pyraminx.scramble();
                sq1 = cubesq.scramble();
                skw = Skewb.scramble();
                clk = clock.scramble();
                mega = megaminx.scramblestring(70);
                scr = "2x2) " + s2 + "\n3x3) " + s3 + "\n4x4) " + s4 +"\n5x5) " + s5 + "\n3OH) " + oh + "\nPyra) " + pyr + "\nSQ1) " + sq1 + "\nSkewb) " + skw + "\nClock) " + clk + "\nMega) " + mega;
                scrambleList.add(s2);
                scrambleList.add(s3);
                scrambleList.add(s4);
                scrambleList.add(s5);
                scrambleList.add(oh);
                scrambleList.add(pyr);
                scrambleList.add(sq1);
                scrambleList.add(skw);
                scrambleList.add(clk);
                scrambleList.add(mega);
                imageType = TYPE_REL; break;
            default:
                scr = "";
        }
        scramble = scr;
    }

    private String scrambleCube(int n) {
        return Cube.scramblestring(n, Math.abs(scrambleLen));
    }

    private String scramble333() {
        cubeState = Tools.randomCube();
        String scramble;
        do {
            scramble = cube3.solution(cubeState, 21, 50000, 50, 2);
        } while (scramble.startsWith("Error"));
        return scramble;
    }

    private String scramble444() {
        return megascramble(new String[][] {{"U", "D", "Uw"}, {"R", "L", "Rw"}, {"F", "B", "Fw"}}, cubesuff, 40);
    }

    private String scramble555() {
        return megascramble(new String[][] {{"U", "D", "Uw", "Dw"}, {"R", "L", "Rw", "Lw"}, {"F", "B", "Fw", "Bw"}}, cubesuff, 60);
    }

    private String scramble666() {
        return megascramble(new String[][] {{"U", "D", "Uw", "Dw", "3Uw"}, {"R", "L", "Rw", "Lw", "3Rw"}, {"F", "B", "Fw", "Bw", "3Fw"}}, cubesuff, 80);
    }

    private String scramble777() {
        return megascramble(new String[][] {{"U", "D", "Uw", "Dw", "3Uw", "3Dw"}, {"R", "L", "Rw", "Lw", "3Rw", "3Lw"}, {"F", "B", "Fw", "Bw", "3Fw", "3Bw"}}, cubesuff, 100);
    }

    public boolean is333Scramble() {
        int idx = category >> 5;
        int sub = category & 0x1f;
        return (idx == -1 && (sub == 0 || sub == 5 || sub == 7)) ||
                (idx == 1 && (sub == 0 || sub == 1 || sub == 19));
    }

    public boolean isSqScramble() {
        int idx = category >> 5;
        int sub = category & 0x1f;
        return (idx == 8 && sub < 3) || (idx == -1 && sub == 10);
    }

    public boolean is222Scramble() {
        int idx = category >> 5;
        int sub = category & 0x1f;
        return (idx == 0 && sub < 2) || (idx == -1 && sub == 3);
    }

    public boolean is444Scramble() {
        int idx = category >> 5;
        int sub = category & 0x1f;
        return (idx == -1 && (sub == 1 || sub == 15)) || (idx == 2 && sub == 5);
    }

    public boolean isPyrScramble() {
        int idx = category >> 5;
        int sub = category & 0x1f;
        return (idx == -1 && sub == 9) || (idx == 7 && sub < 2);
    }

    public String solve333(String scramble) {
        switch (APP.solve333) {
            case 1:
                return Cross.solveCross(scramble, APP.solverType[1]);
            case 2:
                return Cross.solveXcross(scramble, APP.solverType[1]);
            case 3:
                return EOline.solveEoline(scramble, APP.solverType[1]);
            case 4:
                return Roux.solveRoux1(scramble, APP.solverType[3]);
            case 5:
                return Petrus.solvePetrus(scramble, APP.solverType[2]);
            case 6:
                return Cross.solveEofc(scramble, APP.solverType[1]);
            default:
                return "";
        }
    }

    public String solve222(String scramble) {
        switch (APP.solve222) {
            case 1:
                return Cube2Face.solveFace(scramble, APP.solverType[4]);
            case 2:
                return Cube2Layer.solveFirstLayer(scramble, APP.solverType[4]);
            default:
                return "";
        }
    }

    public void drawScramble(int width, Paint p, Canvas c) {
        int[] colors = {pref.getInt("csn1", Color.YELLOW), pref.getInt("csn2", Color.BLUE), pref.getInt("csn3", Color.RED),
                pref.getInt("csn4", Color.WHITE), pref.getInt("csn5", 0xff009900), pref.getInt("csn6", 0xffff9900)};
        if (imageType == TYPE_MEGA) {   //五魔
            drawMega(width, p, c);
        } else if (imageType == TYPE_PYR) { //金字塔
            drawPyra(scramble, width, p, c);
        } else if (imageType == TYPE_SQ1) { //SQ1
            drawSQ1(scramble, width, p, c);
        } else if (imageType == TYPE_CLK) { //魔表
            drawClock(width, p, c);
        } else if (imageType == TYPE_133) { //1x3x3
            int[] img = Floppy.image(scramble);
            int a = (width * 92 / 100) / 8, i, j, d = 0;
            int stx = (width * 94 / 100 - 8 * a) / 2, sty = (width * 71 / 100 - 5 * a) / 2;
            int sp = width / 50;
            p.setStyle(Paint.Style.FILL);
            //colors = new int[] {0xFF4B4D4B, 0xFFFFEF33, 0xFF33B9FF, 0xFFC8CCC8, 0xFFFF0026, 0xFF99FF99};
            for (i = 0; i < 3; i++) {
                p.setStyle(Paint.Style.FILL);
                p.setColor(colors[img[d++]]);
                c.drawRect(stx + sp + (i + 1) * a, sty, stx + sp + (i + 2) * a, sty + a, p);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.BLACK);
                c.drawRect(stx + sp + (i + 1) * a, sty, stx + sp + (i + 2) * a, sty + a, p);
            }
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 8; j++) {
                    int offset = 0;
                    if (j > 4) offset = sp * 3;
                    else if (j > 3) offset = sp * 2;
                    else if (j > 0) offset = sp;
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(colors[img[d++]]);
                    c.drawRect(stx + offset + j * a, sty + sp + (i + 1) * a, stx + offset + (j + 1) * a, sty + sp + (i + 2) * a, p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(Color.BLACK);
                    c.drawRect(stx + offset + j * a, sty + sp + (i + 1) * a, stx + offset + (j + 1) * a, sty + sp + (i + 2) * a, p);
                }
            }
            for (i = 0; i < 3; i++) {
                p.setStyle(Paint.Style.FILL);
                p.setColor(colors[img[d++]]);
                c.drawRect(stx + sp + (i + 1) * a, sty + sp * 2 + 4 * a, stx + sp + (i + 2) * a, sty + sp * 2 + 5 * a, p);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.BLACK);
                c.drawRect(stx + sp + (i + 1) * a, sty + sp * 2 + 4 * a, stx + sp + (i + 2) * a, sty + sp * 2 + 5 * a , p);
            }
        } else if (imageType == TYPE_233) { //2x3x3
            int[] img = Domino.image(scramble);
            int a = (width * 92 / 100) / 12, i, j, d = 0;
            int stx = (width * 94 / 100 - 12 * a) / 2, sty = (width * 71 / 100 - 8 * a) / 2;
            int sp = width / 50;
            p.setStyle(Paint.Style.FILL);
            for (i = 0; i < 3; i++)
                for (j = 0; j < 3; j++) {
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(colors[img[d++]]);
                    c.drawRect(stx + sp + (j + 3) * a, sty + i * a, stx + sp + (j + 4) * a, sty + (i + 1) * a, p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(Color.BLACK);
                    c.drawRect(stx + sp + (j + 3) * a, sty + i * a, stx + sp + (j + 4) * a, sty + (i + 1) * a, p);
                }
            for (i = 0; i < 2; i++)
                for (j = 0; j < 12; j++) {
                    int offset = 0;
                    if (j >= 9) offset = sp * 3;
                    else if (j >= 6) offset = sp * 2;
                    else if (j >= 3) offset = sp;
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(colors[img[d++]]);
                    c.drawRect(stx + offset + j * a, sty + sp + (i + 3) * a, stx + offset + (j + 1) * a, sty + sp + (i + 4) * a, p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(Color.BLACK);
                    c.drawRect(stx + offset + j * a, sty + sp + (i + 3) * a, stx + offset + (j + 1) * a, sty + sp + (i + 4) * a, p);
                }
            for (i = 0; i < 3; i++)
                for (j = 0; j < 3; j++) {
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(colors[img[d++]]);
                    c.drawRect(stx + sp + (j + 3) * a, sty + sp * 2 + (i + 5) * a, stx + sp + (j + 4) * a, sty + sp * 2 + (i + 6) * a, p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(Color.BLACK);
                    c.drawRect(stx + sp + (j + 3) * a, sty + sp * 2 + (i + 5) * a, stx + sp + (j + 4) * a, sty + sp * 2 + (i + 6) * a, p);
                }
        } else if (imageType == TYPE_223) { //2x2x3
            int[] img = Tower.image(scramble);
            int a = width / 10, i, j, d = 0;
            int stx = (width * 94 / 100 - 8 * a) / 2, sty = (width * 71 / 100 - 7 * a) / 2;
            int sp = width / 50;
            p.setStyle(Paint.Style.FILL);
            //colors=new int[] {0xFF4B4D4B,0xFFFFEF33,0xFF33B9FF,0xFFC8CCC8,0xFFFF0026,0xFF99FF99};
            for (i = 0; i < 2; i++)
                for (j = 0; j < 2; j++) {
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(colors[img[d++]]);
                    c.drawRect(stx + sp + (j + 2) * a, sty + i * a, stx + sp + (j + 3) * a, sty + (i + 1) * a, p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(Color.BLACK);
                    c.drawRect(stx + sp + (j + 2) * a, sty + 1 + i * a, stx + sp + (j + 3) * a, sty + (i + 1) * a, p);
                }
            for (i = 0; i < 3; i++)
                for (j = 0; j < 8; j++) {
                    int offset = 0;
                    if (j > 5) offset = sp * 3;
                    else if (j > 3) offset = sp * 2;
                    else if (j > 1) offset = sp;
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(colors[img[d++]]);
                    c.drawRect(stx + offset + j * a, sty + sp + (i + 2) * a, stx + offset + (j + 1) * a, sty + sp + (i + 3) * a, p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(Color.BLACK);
                    c.drawRect(stx + offset + j * a, sty + sp + (i + 2) * a, stx + offset + (j + 1) * a, sty + sp + (i + 3) * a, p);
                }
            for (i = 0; i < 2;i++)
                for (j = 0; j < 2; j++) {
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(colors[img[d++]]);
                    c.drawRect(stx + sp + (j + 2) * a, sty + sp * 2 + (5 + i) * a, stx + sp + (j + 3) * a, sty + sp * 2 + (6 + i) * a, p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(Color.BLACK);
                    c.drawRect(stx + sp + (j + 2) * a, sty + sp * 2 + (5 + i) * a, stx + sp + (j + 3) * a, sty + sp * 2 + (6 + i) * a, p);
                }
        } else if (imageType == TYPE_SKW) { //斜转
            int[] img = Skewb.image(scramble);
            if (img == null) return;
            drawSkewb(img, width, p, c);
        } else if (imageType == TYPE_15P || imageType == TYPE_15PB) {   //15 puzzle
            int[] img = FifteenPuzzle.image(scramble, imageType == TYPE_15P);
            int wid = width / 6;
            int stx = (width - wid * 4) / 2;
            int sty = (width * 3 / 4 - wid * 4) / 2;
            p.setTextSize(wid * 0.6f);
            p.setTextAlign(Align.CENTER);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int num = img[i * 4 + j];
                    if (num != 15) {
                        p.setStyle(Paint.Style.FILL);
                        if (num == 10 || num == 11 || num == 14)
                            p.setColor(0xffaaaa66);
                        else if (num == 5 || num == 6 || num == 7 || num == 9 || num == 13)
                            p.setColor(0xff9999ff);
                        else p.setColor(0xffff6666);
                        c.drawRoundRect(new RectF(stx + wid * j + wid * 0.03f, sty + wid * i + wid * 0.03f, stx + wid * (j + 1) - wid * 0.03f, sty + wid * (i + 1) - wid * 0.03f), wid * 0.1f, wid * 0.1f, p);
                        //c.drawRect(wid * j, wid * i, wid * (j + 1), wid * (i + 1), p);
                        p.setColor(0xffffffff);
                        c.drawText(String.valueOf(num + 1), stx + wid * (j + 0.5f), sty + wid * (i + 0.72f), p);
                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(0xdd000000);
                        c.drawRoundRect(new RectF(stx + wid * j + wid * 0.03f, sty + wid * i + wid * 0.03f, stx + wid * (j + 1) - wid * 0.03f, sty + wid * (i + 1) - wid * 0.03f), wid * 0.1f, wid * 0.1f, p);
                    }
                }
            }
        } else if (imageType == TYPE_REDI) {//Redi cube
            int[] img = Redi.image(scramble);
            int a = (width * 92 / 100) / 12, d = 0;
            int sp = width / 50;
            int imageWid = a * 12 + sp * 3, imageHei = a * 9 + sp * 2;
            int stx = (width - imageWid) / 2, sty = (width * 3 / 4 - imageHei) / 2;
            //p.setStyle(Paint.Style.FILL);
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    if (i == 1 || j == 1) {
                        float[] arx = { stx + sp + (j + 3) * a, stx + sp + (j + 4) * a, stx + sp + (j + 4) * a, stx + sp + 4.5f * a, stx + sp + (j + 3) * a },
                                ary = { sty + i * a, sty + (i + 1) * a, sty + (i + 1) * a, sty + 1.5f * a, sty + i * a };
                        if (i == 1) {
                            if (j == 0) {
                                arx[1] = arx[0]; arx[4] = arx[2];
                            } else {
                                arx[0] = arx[1]; arx[2] = arx[4];
                            }
                        }
                        if (j == 1) {
                            if (i == 0) {
                                ary[1] = ary[0]; ary[4] = ary[2];
                            } else {
                                ary[0] = ary[1]; ary[2] = ary[4];
                            }
                        }
                        if (i != 1 || j != 1) drawPolygon(p, c, colors[img[d++]], arx, ary, true);
                    } else {
                        p.setStyle(Paint.Style.FILL);
                        p.setColor(colors[img[d++]]);
                        c.drawRect(stx + sp + (j + 3) * a, sty + i * a, stx + sp + (j + 4) * a, sty + (i + 1) * a, p);
                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(Color.BLACK);
                        c.drawRect(stx + sp + (j + 3) * a, sty + i * a, stx + sp + (j + 4) * a, sty + (i + 1) * a, p);
                    }
                }
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 12; j++) {
                    if (i == 1 || j % 3 == 1) {
                        float[] arx = { stx + j * a + sp * (j / 3), stx + (j + 1) * a + sp * (j / 3), stx + (j + 1) * a + sp * (j / 3), stx + (j / 3 * 3 + 1.5f) * a + sp * (j / 3), stx + j * a + sp * (j / 3) },
                                ary = { sty + sp + (i + 3) * a, sty + sp + (i + 4) * a, sty + sp + (i + 4) * a, sty + sp + 4.5f * a, sty + sp + (i + 3) * a };
                        if (i == 1) {
                            if (j % 3 == 0) {
                                arx[1] = arx[0]; arx[4] = arx[2];
                            } else {
                                arx[0] = arx[1]; arx[2] = arx[4];
                            }
                        }
                        if (j % 3 == 1) {
                            if (i == 0) {
                                ary[1] = ary[0]; ary[4] = ary[2];
                            } else {
                                ary[0] = ary[1]; ary[2] = ary[4];
                            }
                        }
                        if (i != 1 || j % 3 != 1) drawPolygon(p, c, colors[img[d++]], arx, ary, true);
                    } else {
                        p.setStyle(Paint.Style.FILL);
                        p.setColor(colors[img[d++]]);
                        c.drawRect(stx + j * a + sp * (j / 3), sty + sp + (i + 3) * a, stx + (j + 1) * a + sp * (j / 3), sty + sp + (i + 4) * a, p);
                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(Color.BLACK);
                        c.drawRect(stx + j * a + sp * (j / 3), sty + sp + (i + 3) * a, stx + (j + 1) * a + sp * (j / 3), sty + sp + (i + 4) * a, p);
                    }
                }
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    if (i == 1 || j == 1) {
                        float[] arx = { stx + sp + (j + 3) * a, stx + sp + (j + 4) * a, stx + sp + (j + 4) * a, stx + sp + 4.5f * a, stx + sp + (j + 3) * a },
                                ary = { sty + sp * 2 + (i + 6) * a, sty + sp * 2 + (i + 7) * a, sty + sp * 2 + (i + 7) * a, sty + sp * 2 + 7.5f * a, sty + sp * 2 + (i + 6) * a };
                        if (i == 1) {
                            if (j == 0) {
                                arx[1] = arx[0]; arx[4] = arx[2];
                            } else {
                                arx[0] = arx[1]; arx[2] = arx[4];
                            }
                        }
                        if (j == 1) {
                            if (i == 0) {
                                ary[1] = ary[0]; ary[4] = ary[2];
                            } else {
                                ary[0] = ary[1]; ary[2] = ary[4];
                            }
                        }
                        if (i != 1 || j != 1) drawPolygon(p, c, colors[img[d++]], arx, ary, true);
                    } else {
                        p.setStyle(Paint.Style.FILL);
                        p.setColor(colors[img[d++]]);
                        c.drawRect(stx + sp + (j + 3) * a, sty + sp * 2 + (i + 6) * a, stx + sp + (j + 4) * a, sty + sp * 2 + (i + 7) * a, p);
                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(Color.BLACK);
                        c.drawRect(stx + sp + (j + 3) * a, sty + sp * 2 + (i + 6) * a, stx + sp + (j + 4) * a, sty + sp * 2 + (i + 7) * a, p);
                    }
                }
        } else if (imageType == TYPE_REL) { //连拧
            if (category == -15 || category == 640)
                drawNNN(3, scrambleList.get(scrambleIdx), colors, width, p, c);
            else if (category == 646) {
                if (scrambleIdx < 4) {
                    drawNNN(scrambleIdx + 2, scrambleList.get(scrambleIdx), colors, width, p, c);
                } else if (scrambleIdx == 4) {
                    drawNNN(3, scrambleList.get(scrambleIdx), colors, width, p, c);
                } else if (scrambleIdx == 5) {
                    drawPyra(scrambleList.get(scrambleIdx), width, p, c);
                } else if (scrambleIdx == 6) {
                    drawSQ1(scrambleList.get(scrambleIdx), width, p, c);
                } else if (scrambleIdx == 7) {
                    int[] img = Skewb.image(scrambleList.get(scrambleIdx));
                    if (img == null) return;
                    drawSkewb(img, width, p, c);
                } else if (scrambleIdx == 8) {
                    drawClock(width, p, c);
                } else if (scrambleIdx == 9) {
                    drawMega(width, p, c);
                }
            } else if (scrambleIdx < 6)
                drawNNN(scrambleIdx + 2, scrambleList.get(scrambleIdx), colors, width, p, c);
            else if (scrambleIdx == 6 || scrambleIdx == 7)
                drawNNN(3, scrambleList.get(scrambleIdx), colors, width, p, c);
            else if (scrambleIdx == 8) {
                drawPyra(scrambleList.get(scrambleIdx), width, p, c);
            } else if (scrambleIdx == 9) {
                drawSQ1(scrambleList.get(scrambleIdx), width, p, c);
            } else if (scrambleIdx == 10) {
                int[] img = Skewb.image(scrambleList.get(scrambleIdx));
                if (img == null) return;
                drawSkewb(img, width, p, c);
            } else if (scrambleIdx == 11) {
                drawClock(width, p, c);
            } else if (scrambleIdx == 12) {
                drawMega(width, p, c);
            }
        } else if (imageType == TYPE_8PZ) {
            int[] img = EightPuzzle.image(scramble);
            int wid = width / 5;
            int stx = (width - wid * 3) / 2;
            int sty = (width * 3 / 4 - wid * 3) / 2;
            p.setTextSize(wid * 0.6f);
            p.setTextAlign(Align.CENTER);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int num = img[i * 3 + j];
                    if (num != 8) {
                        p.setStyle(Paint.Style.FILL);
                        if (num == 4 || num == 5 || num == 7)
                            p.setColor(0xff44aa55);
                        else p.setColor(0xffff3333);
                        c.drawRoundRect(new RectF(stx + wid * j + wid * 0.03f, sty + wid * i + wid * 0.03f, stx + wid * (j + 1) - wid * 0.03f, sty + wid * (i + 1) - wid * 0.03f), wid * 0.1f, wid * 0.1f, p);
                        //c.drawRect(wid * j, wid * i, wid * (j + 1), wid * (i + 1), p);
                        p.setColor(0xffffffff);
                        c.drawText(String.valueOf(num + 1), stx + wid * (j + 0.5f), sty + wid * (i + 0.72f), p);
                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(0xdd000000);
                        c.drawRoundRect(new RectF(stx + wid * j + wid * 0.03f, sty + wid * i + wid * 0.03f, stx + wid * (j + 1) - wid * 0.03f, sty + wid * (i + 1) - wid * 0.03f), wid * 0.1f, wid * 0.1f, p);
                    }
                }
            }
        } else {  //NxNxN
            drawNNN(imageType, scramble, colors, width, p, c);
        }
    }

    private void drawNNN(int size, String scramble, int[] colors, int width, Paint p, Canvas c) {
        int[] img;
        if (APP.isImportScr) {
            img = Cube.image(scramble, size);
        } else if (size == 3 || size == 2) {
            img = Cube.image(scramble, size);
        } else if (size > 7) img = Cube.image();
        else if ((category & 0x1f) == 0) img = Cube.image();
        else img = Cube.image(scramble, size);
        int a = (width * 92 / 100) / (size * 4), i, j, d = 0, b = size;
        int sp = width / 50;
        int imageWid = 4 * a * b + sp * 3, imageHei = 3 * a * b + sp * 2;
        int stx = (width - imageWid) / 2, sty = (width * 3 / 4 - imageHei) / 2;
        //int stx = 0, sty = 0;
        //p.setStyle(Paint.Style.FILL);
        for (i = 0; i < b; i++)
            for (j = 0; j < b; j++) {
                p.setStyle(Paint.Style.FILL);
                p.setColor(colors[img[d++]]);
                c.drawRect(stx + sp + (j + b) * a, sty + i * a, stx + sp +(j + 1 + b) * a, sty + (i + 1) * a, p);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.BLACK);
                c.drawRect(stx + sp + (j + b) * a, sty + i * a, stx + sp + (j + 1 + b ) * a, sty + (i + 1) * a, p);
            }
        for (i = 0; i < b; i++)
            for (j = 0; j < b * 4; j++) {
                p.setStyle(Paint.Style.FILL);
                p.setColor(colors[img[d++]]);
                int offset = 0;
                if (j >= b * 3) offset = sp * 3;
                else if (j >= b * 2) offset = sp * 2;
                else if (j >= b) offset = sp;
                c.drawRect(stx + j * a + offset, sty + sp + (i + b) * a, stx + (j + 1) * a + offset, sty + sp + (i + 1 + b) * a, p);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.BLACK);
                c.drawRect(stx + j * a + offset, sty + sp + (i + b) * a, stx + (j + 1) * a + offset, sty + sp + (i + 1 + b) * a, p);
            }
        for (i = 0; i < b; i++)
            for (j = 0; j < b; j++) {
                p.setStyle(Paint.Style.FILL);
                p.setColor(colors[img[d++]]);
                c.drawRect(stx + sp + (j + b) * a, sty + sp * 2 + (i + 2 * b) * a, stx + sp + (j + 1 + b) * a, sty + sp * 2 + (i + 1 + 2 * b) * a, p);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.BLACK);
                c.drawRect(stx + sp + (j + b) * a, sty + sp * 2 + (i + 2 * b) * a, stx + sp + (j + 1 + b) * a, sty + sp * 2 + (i + 1 + 2 * b) * a, p);
            }
    }

    private void drawPyra(String scramble, int width, Paint p, Canvas c) {
        int[] img = Pyraminx.image(scramble);
        int b = (width * 72 / 100) / 6;
        int a = (int) (b * 2 / Math.sqrt(3));
        int d = (width * 96 / 100 - a * 6) / 2;
        int sp = width / 50;
        int[] colors = {pref.getInt("csp1", Color.RED), pref.getInt("csp2", 0xff009900), pref.getInt("csp3", Color.BLUE), pref.getInt("csp4", Color.YELLOW)};
        int[] layout = {
                1, 2, 1, 2, 1, 0, 2, 0, 1, 2, 1, 2, 1,
                0, 1, 2, 1, 0, 2, 1, 2, 0, 1, 2, 1, 0,
                0, 0, 1, 0, 2, 1, 2, 1, 2, 0, 1, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 2, 1, 2, 1, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0
        };
        int[] pos = {
                d, d + a / 2, d + a, d + 3 * a / 2, d + 2 * a, d + 5 * a / 2,  d + sp + 5 * a / 2, d + sp + 3 * a,  d + sp * 2 + 3 * a, d + sp * 2 + 7 * a / 2, d + sp * 2 + 4 * a, d + sp * 2 + 9 * a / 2, d + sp * 2 + 5 * a, d + sp * 2 + 11 * a / 2,
                d + a / 2, d + a, d + 3 * a / 2, d + 2 * a,  d + sp + 2 * a, d + sp + 5 * a / 2, d + sp + 3 * a, d + sp + 7 * a / 2,  d + sp * 2 + 7 * a / 2, d + sp * 2 + 4 * a, d + sp * 2 + 9 * a / 2, d + sp * 2 + 5 * a,
                0, 0,  d + a, d + 3 * a / 2,  d + sp + 3 * a / 2, d + sp + 2 * a, d + sp + 5 * a / 2, d + sp + 3 * a, d + sp + 7 * a / 2, d + sp + 4 * a,  d + sp * 2 + 4 * a, d + sp * 2 + 9 * a / 2,  0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0,  d + sp + 3 * a / 2, d + sp + 2 * a, d + sp + 5 * a / 2, d + sp + 3 * a, d + sp + 7 * a / 2, d + sp + 4 * a,  0, 0, 0,
                0, 0, 0, 0, 0,  d + sp + 2 * a, d + sp + 5 * a / 2, d + sp + 3 * a, d + sp + 7 * a / 2,  0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,  d + sp + 5 * a / 2, d + sp + 3 * a,  0, 0, 0, 0, 0};
        for (int y = 0; y < 7; y++)
            for (int x = 0; x < 13; x++) {
                if (layout[y * 13 + x] == 1) {
                    if (y < 3) {
                        float[] arx = {pos[y * 13 + x] + x, pos[y * 13 + x] + a + x, pos[y * 13 + x + 1] + x};
                        float[] ary = {y * b + 3 + y, y * b + 3 + y, (y + 1) * b + 3 + y};
                        drawPolygon(p, c, colors[img[y * 13 + x]], arx, ary, true);
                    } else if (y > 3) {
                        float[] arx = {pos[y * 13 + x] + x, pos[y * 13 + x] + a + x, pos[y * 13 + x + 1] + x};
                        float[] ary = {(y - 1) * b + 2 + sp + y, (y - 1) * b + 2 + sp + y, y * b + 2 + sp + y};
                        drawPolygon(p, c, colors[img[y * 13 + x]], arx, ary, true);
                    }
                }
                else if (layout[y * 13 + x] == 2) {
                    if (y < 3) {
                        float[] arx = {pos[y * 13 + x] + x, pos[y * 13 + x] + a + x, pos[y * 13 + x + 1] + x};
                        float[] ary = {(y + 1) * b + 3 + y, (y + 1) * b + 3 + y, y * b + 3 + y};
                        drawPolygon(p, c, colors[img[y * 13 + x]], arx, ary, true);
                    } else if (y > 3) {
                        float[] arx = {pos[y * 13 + x] + x, pos[y * 13 + x] + a + x, pos[y * 13 + x + 1] + x};
                        float[] ary = {y * b + 2 + sp + y, y * b + 2 + sp + y, (y - 1) * b + 2 + sp + y};
                        drawPolygon(p, c, colors[img[y * 13 + x]], arx, ary, true);
                    }
                }
            }
    }

    private void drawMega(int width, Paint p, Canvas c) {
        int[] colors;
        if (APP.megaColorScheme == 0)
            colors = new int[] {Color.WHITE, 0xff880088, 0xff008800, 0xff88ddff, 0xff882222, Color.BLUE,
                    Color.RED, 0xffff9900, Color.GREEN, 0xffff44ff, 0xff000088, Color.YELLOW};
        else colors = new int[] {Color.WHITE, Color.RED, 0xff008800, 0xff880088, Color.YELLOW, Color.BLUE,
                0xffffff88, 0xff88ddff, 0xffff9900, Color.GREEN, 0xffff44ff, Color.GRAY};
        float edgeFrac = (float) ((1 + Math.sqrt(5)) / 4);
        float centerFrac = 0.5f;
        float scale = width / 350f;
        int dx = (int) ((width - 350 * scale) / 2);
        int dy = (int) ((width * 0.75 - 180 * scale) / 2);
        float majorR = 36 * scale;
        float minorR = majorR * edgeFrac;
        float pentR = minorR * 2;
        float cx1 = 92 * scale + dx;
        float cy1 = 80 * scale + dy;
        float cx2 = cx1 + c18(1) * 3 * pentR;
        float cy2 = cy1 + s18(1) * 1 * pentR;
        float[] aryx, aryy;
        int[][] trans = {
                {0, (int)cx1, (int)cy1, 0, 0},
                {36, (int)cx1, (int)cy1, 1, 1},
                {36 +72 * 1, (int)cx1, (int)cy1, 1, 5},
                {36 +72 * 2, (int)cx1, (int)cy1, 1, 9},
                {36 +72 * 3, (int)cx1, (int)cy1, 1, 13},
                {36 +72 * 4, (int)cx1, (int)cy1, 1, 17},
                {0, (int)cx2, (int)cy2, 1, 7},
                {-72 * 1, (int)cx2, (int)cy2, 1, 3},
                {-72 * 2, (int)cx2, (int)cy2, 1, 19},
                {-72 * 3, (int)cx2, (int)cy2, 1, 15},
                {-72 * 4, (int)cx2, (int)cy2, 1, 11},
                {36 + 72 * 2, (int)cx2, (int)cy2, 0, 0}
        };
        int d = 0;
        float d2x = (float) (majorR * (1 - centerFrac) / 2 / Math.tan(Math.PI / 5));
        int[] img = megaminx.getState();
        p.setStyle(Paint.Style.FILL);
        for (int side = 0; side < 12; side++) {
            float a = trans[side][1] + trans[side][3] * c18(trans[side][4]) * pentR;
            float b = trans[side][2] + trans[side][3] * s18(trans[side][4]) * pentR;
            float[][] arys;
            for (int i = 0; i < 5; i++) {
                aryx = new float[] {0, d2x, 0, -d2x};
                aryy = new float[] {-majorR, -majorR * (1 + centerFrac) / 2, -majorR * centerFrac, -majorR * (1 + centerFrac) / 2};
                arys = rotate(a, b, aryx, aryy, 72 * i + trans[side][0]);
                drawPolygon(p, c, colors[img[d++]], arys[0], arys[1], true);
            }
            for (int i = 0; i < 5; i++) {
                aryx = new float[] {c18(-1) * majorR - d2x, d2x, 0, s18(4) * centerFrac * majorR};
                aryy = new float[] {s18(-1) * majorR - majorR + majorR * (1 + centerFrac) / 2, -majorR * (1 + centerFrac) / 2, -majorR * centerFrac, -c18(4) * centerFrac * majorR};
                arys = rotate(a, b, aryx, aryy, 72*i+trans[side][0]);
                drawPolygon(p, c, colors[img[d++]], arys[0], arys[1], true);
            }
            aryx = new float[] {s18(0) * centerFrac * majorR, s18(4) * centerFrac * majorR, s18(8) * centerFrac * majorR, s18(12) * centerFrac * majorR, s18(16) * centerFrac * majorR};
            aryy = new float[] {-c18(0) * centerFrac * majorR, -c18(4) * centerFrac * majorR, -c18(8) * centerFrac * majorR, -c18(12) * centerFrac * majorR, -c18(16) * centerFrac * majorR};
            arys = rotate(a, b, aryx, aryy, trans[side][0]);
            drawPolygon(p, c, colors[img[d++]], arys[0], arys[1], true);
        }
        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Align.CENTER);
        p.setTextSize(width * 0.0593f);
        c.drawText("U", width * 0.262f, width * 0.367f, p);
        c.drawText("F", width * 0.262f, width * 0.535f, p);
    }

    private void drawSQ1(String scramble, int width, Paint p, Canvas c) {
        String[] tb = {"3", "3", "3", "3", "3", "3", "3", "3", "0", "0", "0", "0", "0", "0", "0", "0"};
        String[] ty = {"c", "e", "c", "e", "c", "e", "c", "e", "e", "c", "e", "c", "e", "c", "e", "c"};
        String[] col = {"51", "1", "12", "2", "24", "4", "45", "5", "5", "54", "4", "42", "2", "21", "1", "15"};
        int[] colors = {pref.getInt("csq1", Color.WHITE), pref.getInt("csq6", Color.BLUE), pref.getInt("csq2", 0xffff9900),
                pref.getInt("csq4", Color.YELLOW), pref.getInt("csq3", 0xff009900), pref.getInt("csq5", Color.RED)};
        int[] img = SQ1.image(scramble);
        boolean mis = img[24] != 0;
        int[] temp = new int[12];
        for (int i = 0; i < 12; i++) temp[i] = img[i];
        int[] topSide = rd(temp);
        for (int i = 0; i < 6; i++) temp[i] = img[i + 18];
        for (int i = 6; i < 12; i++) temp[i] = img[i + 6];
        int[] botSide = rd(temp);
        temp = new int[topSide.length + botSide.length];
        for (int i = 0; i < topSide.length; i++) temp[i] = topSide[i];
        for (int i = topSide.length; i < topSide.length + botSide.length; i++)
            temp[i] = botSide[i - topSide.length];
        int[] eido = temp;
        StringBuilder a2 = new StringBuilder(), b2 = new StringBuilder(), c2 = new StringBuilder();
        for (int j = 0; j < 16; j++) {
            a2.append(ty[eido[j]]);
            b2.append(tb[eido[j]]);
            c2.append(col[eido[j]]);
        }
        String stickers = b2.append(c2).toString();

        float z = 1.366025f; // sqrt(2)/sqrt(1^2+tan(15°)^2)
        float[] arrx, arry;
        float sidewid = 10.98f;
        int cx = 55, cy = 50;
        float rd = (cx - 16) / z;
        float w = (sidewid + rd) / rd;	// ratio btw total piece width and rd
        float[] ag = new float[24];
        float[] ag2 = new float[24];
        int foo;
        for (foo = 0; foo < 24; foo++) {
            ag[foo] = (float) ((17f - foo * 2) * Math.PI / 12);
            a2.append("xxxxxxxxxxxxxxxx");
        }
        for (foo = 0; foo < 24; foo++) {
            ag2[foo] = (float) ((19f - foo * 2) * Math.PI / 12);
            a2.append("xxxxxxxxxxxxxxxx");
        }
        String a = a2.toString();
        float h = sin1(1, ag, rd) * w * z - sin1(1, ag, rd) * z;
        if (mis) {
            arrx = new float[] {cx + cos1(1, ag, rd) * w * z, cx + cos1(4, ag, rd) * w * z, cx + cos1(7, ag, rd) * w * z, cx + cos1(10, ag, rd) * w * z};
            arry = new float[] {cy - sin1(1, ag, rd) * w * z, cy - sin1(4, ag, rd) * w * z, cy - sin1(7, ag, rd) * w * z, cy - sin1(10, ag, rd) * w * z};
            drawPolygon(p, c, Color.BLACK, width, arrx, arry);
            cy += 10;
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(1, ag, rd) * w * z, cx + cos1(1, ag, rd) * w * z};
            arry = new float[] {cy - sin1(1, ag, rd) * w * z, cy - sin1(1, ag, rd) * z, cy - sin1(1, ag, rd) * z, cy - sin1(1, ag, rd) * w * z};
            drawPolygon(p, c, colors[5], width, arrx, arry);
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(10, ag, rd) * w * z, cx + cos1(10, ag, rd) * w * z};
            arry = new float[] {cy - sin1(1, ag, rd) * w * z, cy - sin1(1, ag, rd) * z, cy - sin1(1, ag, rd) * z, cy - sin1(1, ag, rd) * w * z};
            drawPolygon(p, c, colors[5], width, arrx, arry);
            cy -= 10;
        } else {
            arrx = new float[] {cx + cos1(1, ag, rd) * w * z, cx + cos1(4, ag, rd) * w * z, cx + cos1(6, ag, rd) * w, cx + cos1(9, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z, cx + cos1(0, ag, rd) * w};
            arry = new float[] {cy - sin1(1, ag, rd) * w * z, cy - sin1(4, ag, rd) * w * z, cy - sin1(6, ag, rd) * w, cy + sin1(9, ag, rd) * w * z, cy - sin1(11, ag, rd) * w * z, cy - sin1(0, ag, rd) * w};
            drawPolygon(p, c, Color.BLACK, width, arrx, arry);
            arrx = new float[] {cx + cos1(9, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z, cx + cos1(9, ag, rd) * w * z};
            arry = new float[] {cy + sin1(9, ag, rd) * w * z - h, cy - sin1(11, ag, rd) * w * z - h, cy - sin1(11, ag, rd) * w * z, cy + sin1(9, ag, rd) * w * z};
            drawPolygon(p, c, colors[4], width, arrx, arry);
            cy += 10;
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(1, ag, rd) * w * z, cx + cos1(1, ag, rd) * w * z};
            arry = new float[] {cy - sin1(1, ag, rd) * w * z, cy - sin1(1, ag, rd) * z, cy - sin1(1, ag, rd) * z, cy - sin1(1, ag, rd) * w * z};
            drawPolygon(p, c, colors[5], width, arrx, arry);
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(11, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z};
            arry = new float[] {cy - sin1(1, ag, rd) * w * z, cy - sin1(1, ag, rd) * z, cy - sin1(11, ag, rd) * w * z + h, cy - sin1(11, ag, rd) * w * z};
            drawPolygon(p, c, colors[2], width, arrx, arry);
            cy -= 10;
        }
        int sc = 0;
        for (foo = 0; sc < 12; foo++) {
            if (a.length() <= foo) sc = 12;
            if (a.charAt(foo) == 'x') sc++;
            if (a.charAt(foo) == 'c') {
                arrx = new float[] {cx, cx + cos1(sc, ag, rd), cx + cos1(sc + 1, ag, rd) * z, cx + cos1(sc + 2, ag, rd)};
                arry = new float[] {cy, cy - sin1(sc, ag, rd), cy - sin1(sc + 1, ag, rd) * z, cy - sin1(sc + 2, ag, rd)};
                drawPolygon(p, c, colors[(int) stickers.charAt(foo) - 48], width, arrx, arry);
                arrx = new float[] {cx + cos1(sc, ag, rd), cx + cos1(sc + 1, ag, rd) * z, cx + cos1(sc + 1, ag, rd) * w * z, cx + cos1(sc, ag, rd) * w};
                arry = new float[] {cy - sin1(sc, ag, rd), cy - sin1(sc + 1, ag, rd) * z, cy - sin1(sc + 1, ag, rd) * w * z, cy - sin1(sc, ag, rd) * w};
                drawPolygon(p, c, colors[(int) stickers.charAt(16 + sc) - 48], width, arrx, arry);
                arrx = new float[] {cx + cos1(sc + 2, ag, rd), cx + cos1(sc + 1, ag, rd) * z, cx + cos1(sc + 1, ag, rd) * w * z, cx + cos1(sc + 2, ag, rd) * w};
                arry = new float[] {cy - sin1(sc + 2, ag, rd), cy - sin1(sc + 1, ag, rd) * z, cy - sin1(sc + 1, ag, rd) * w * z, cy - sin1(sc + 2, ag, rd) * w};
                drawPolygon(p, c, colors[(int) stickers.charAt(17 + sc) - 48], width, arrx, arry);
                sc += 2;
            }
            if (a.charAt(foo) == 'e') {
                arrx = new float[] {cx, cx + cos1(sc, ag, rd), cx + cos1(sc + 1, ag, rd)};
                arry = new float[] {cy, cy - sin1(sc, ag, rd), cy - sin1(sc + 1, ag, rd)};
                drawPolygon(p, c, colors[(int) stickers.charAt(foo) - 48], width, arrx, arry);
                arrx = new float[] {cx + cos1(sc, ag, rd), cx + cos1(sc + 1, ag, rd), cx + cos1(sc + 1, ag, rd) * w, cx + cos1(sc, ag, rd) * w};
                arry = new float[] {cy - sin1(sc, ag, rd), cy - sin1(sc + 1, ag, rd), cy - sin1(sc + 1, ag, rd) * w, cy - sin1(sc, ag, rd) * w};
                drawPolygon(p, c, colors[(int) stickers.charAt(16 + sc) - 48], width, arrx, arry);
                sc += 1;
            }
        }
        cx *= 3;
        cy += 10;
        if (mis) {
            arrx = new float[] {cx + cos1(1, ag, rd) * w * z, cx + cos1(4, ag, rd) * w * z, cx + cos1(7, ag, rd) * w * z, cx + cos1(10, ag, rd) * w * z};
            arry = new float[] {cy + sin1(1, ag, rd) * w * z, cy + sin1(4, ag, rd) * w * z, cy + sin1(7, ag, rd) * w * z, cy + sin1(10, ag, rd) * w * z};
            drawPolygon(p, c, Color.BLACK, width, arrx, arry);
            cy -= 10;
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(1, ag, rd) * w * z, cx + cos1(1, ag, rd) * w * z};
            arry = new float[] {cy + sin1(1, ag, rd) * w * z, cy + sin1(1, ag, rd) * z, cy + sin1(1, ag, rd) * z, cy + sin1(1, ag, rd) * w * z};
            drawPolygon(p, c, colors[5], width, arrx, arry);
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(10, ag, rd) * w * z, cx + cos1(10, ag, rd) * w * z};
            arry = new float[] {cy + sin1(1, ag, rd) * w * z, cy + sin1(1, ag, rd) * z, cy + sin1(1, ag, rd) * z, cy + sin1(1, ag, rd) * w * z};
            drawPolygon(p, c, colors[5], width, arrx, arry);
            cy += 10;
        } else {
            arrx = new float[] {cx + cos1(1, ag, rd) * w * z, cx + cos1(4, ag, rd) * w * z, cx + cos1(6, ag, rd) * w, cx + cos1(9, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z, cx + cos1(0, ag, rd) * w};
            arry = new float[] {cy + sin1(1, ag, rd) * w * z, cy + sin1(4, ag, rd) * w * z, cy + sin1(6, ag, rd) * w, cy - sin1(9, ag, rd) * w * z, cy + sin1(11, ag, rd) * w * z, cy + sin1(0, ag, rd) * w};
            drawPolygon(p, c, Color.BLACK, width, arrx, arry);
            arrx = new float[] {cx + cos1(9, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z, cx + cos1(9, ag, rd) * w * z};
            arry = new float[] {cy - sin1(9, ag, rd) * w * z - 10, cy + sin1(11, ag, rd) * w * z - 10, cy + sin1(11, ag, rd) * w * z, cy - sin1(9, ag, rd) * w * z};
            drawPolygon(p, c, colors[4], width, arrx, arry);
            cy -= 10;
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(1, ag, rd) * w * z, cx + cos1(1, ag, rd) * w * z};
            arry = new float[] {cy + sin1(1, ag, rd) * w * z, cy + sin1(1, ag, rd) * z, cy + sin1(1, ag, rd) * z, cy + sin1(1, ag, rd) * w * z};
            drawPolygon(p, c, colors[5], width, arrx, arry);
            arrx = new float[] {cx + cos1(0, ag, rd) * w, cx + cos1(0, ag, rd) * w, cx + cos1(11, ag, rd) * w * z, cx + cos1(11, ag, rd) * w * z};
            arry = new float[] {cy + sin1(1, ag, rd) * w * z, cy + sin1(1, ag, rd) * z, cy + sin1(11, ag, rd) * w * z + 10, cy + sin1(11, ag, rd) * w * z};
            drawPolygon(p, c, colors[2], width, arrx, arry);
            cy += 10;
        }
        //solveStr = 0;
        for (sc = 0; sc < 12; foo++) {
            if (a.length() <= foo) sc = 12;
            if (a.charAt(foo) == 'x') sc++;
            if (a.charAt(foo) == 'c') {
                arrx = new float[] {cx, cx + cos1(sc, ag2, rd), cx + cos1(sc + 1, ag2, rd) * z, cx + cos1(sc + 2, ag2, rd)};
                arry = new float[] {cy, cy - sin1(sc, ag2, rd), cy - sin1(sc + 1, ag2, rd) * z, cy - sin1(sc + 2, ag2, rd)};
                drawPolygon(p, c, colors[(int) stickers.charAt(foo) - 48], width, arrx, arry);
                arrx = new float[] {cx + cos1(sc, ag2, rd), cx + cos1(sc + 1, ag2, rd) * z, cx + cos1(sc + 1, ag2, rd) * w * z, cx + cos1(sc, ag2, rd) * w};
                arry = new float[] {cy - sin1(sc, ag2, rd), cy - sin1(sc + 1, ag2, rd) * z, cy - sin1(sc + 1, ag2, rd) * w * z, cy - sin1(sc, ag2, rd) * w};
                drawPolygon(p, c, colors[(int) stickers.charAt(28 + sc) - 48], width, arrx, arry);
                arrx = new float[] {cx + cos1(sc + 2, ag2, rd), cx + cos1(sc + 1, ag2, rd) * z, cx + cos1(sc + 1, ag2, rd) * w * z, cx + cos1(sc + 2, ag2, rd) * w};
                arry = new float[] {cy - sin1(sc + 2, ag2, rd), cy - sin1(sc + 1, ag2, rd) * z, cy - sin1(sc + 1, ag2, rd) * w * z, cy - sin1(sc + 2, ag2, rd) * w};
                drawPolygon(p, c, colors[(int) stickers.charAt(29 + sc) - 48], width, arrx, arry);
                sc += 2;
            }
            if (a.charAt(foo) == 'e') {
                arrx = new float[] {cx, cx + cos1(sc, ag2, rd), cx + cos1(sc + 1, ag2, rd)};
                arry = new float[] {cy, cy - sin1(sc, ag2, rd), cy - sin1(sc + 1, ag2, rd)};
                drawPolygon(p, c, colors[(int) stickers.charAt(foo) - 48], width, arrx, arry);
                arrx = new float[] {cx + cos1(sc, ag2, rd), cx + cos1(sc + 1, ag2, rd), cx + cos1(sc + 1, ag2, rd) * w, cx + cos1(sc, ag2, rd) * w};
                arry = new float[] {cy - sin1(sc, ag2, rd), cy - sin1(sc + 1, ag2, rd), cy - sin1(sc + 1, ag2, rd) * w, cy - sin1(sc, ag2, rd) * w};
                drawPolygon(p, c, colors[(int) stickers.charAt(28 + sc) - 48], width, arrx, arry);
                sc += 1;
            }
        }
    }

    private void drawClock(int width, Paint p, Canvas c) {
        int[] posit = clock.getPosit();
        int faceDist = 30;
        int cx = 55;
        int cy = 55;
        p.setColor(0xff2a2a2a);
        drawSideBackground(p, c, width, cx, cy, 53, 29, 19);
        p.setColor(0xff3366ff);
        drawSideBackground(p, c, width, cx, cy, 52, 29, 18);
        int i = 0;
        for (int y = cy - faceDist; y <= cy + faceDist; y += faceDist)
            for (int x = cx - faceDist; x <= cx + faceDist; x += faceDist)
                drawClockFace(p, c, width, x, y, 0xff88aaff, posit[i++]);
        int[] pegs = clock.getPegs();
        drawPeg(p, c, width, cx - faceDist / 2, cy - faceDist / 2, 1 - pegs[0]);
        drawPeg(p, c, width, cx + faceDist / 2, cy - faceDist / 2, 1 - pegs[1]);
        drawPeg(p, c, width, cx - faceDist / 2, cy + faceDist / 2, 1 - pegs[2]);
        drawPeg(p, c, width, cx + faceDist / 2, cy + faceDist / 2, 1 - pegs[3]);
        cx = 165;
        p.setColor(0xff2a2a2a);
        drawSideBackground(p, c, width, cx, cy, 53, 29, 19);
        p.setColor(0xff88aaff);
        drawSideBackground(p, c, width, cx, cy, 52, 29, 18);
        for (int y = cy - faceDist; y <= cy + faceDist; y += faceDist)
            for (int x = cx - faceDist; x <= cx + faceDist; x += faceDist)
                drawClockFace(p, c, width, x, y, 0xff3366ff, posit[i++]);
        drawPeg(p, c, width, cx+faceDist / 2, cy - faceDist / 2, pegs[0]);
        drawPeg(p, c, width, cx-faceDist / 2, cy - faceDist / 2, pegs[1]);
        drawPeg(p, c, width, cx+faceDist / 2, cy + faceDist / 2, pegs[2]);
        drawPeg(p, c, width, cx-faceDist / 2, cy + faceDist / 2, pegs[3]);
    }

    private void drawSkewb(int[] img, int width, Paint p, Canvas c) {
        int[] colors = {pref.getInt("csw4", Color.WHITE), pref.getInt("csw6", 0xffff9900), pref.getInt("csw5", 0xff009900),
                pref.getInt("csw3", Color.RED), pref.getInt("csw2", Color.BLUE), pref.getInt("csw1", Color.YELLOW)};
        int b = width / 4, a = (int) (b * Math.sqrt(3) / 2);
        int stx = (width - 4 * a) / 2, sty = (width * 3 / 4 - 3 * b) / 2, i, d = 0;
        int sp = width / 50;
        float e = (float) (sp / Math.sqrt(3) / 2), f = (float) (sp * Math.sqrt(3) / 2);
        float[] dx = {a * 2, a * 3 - sp, a + sp, a * 2, sp / 2, a - sp / 2, sp / 2, a - sp / 2, a + sp / 2, a * 2 - sp / 2, a + sp / 2, a * 2 - sp / 2,
                a * 2 + sp / 2, a * 3 - sp / 2, a * 2 + sp / 2, a * 3 - sp / 2, a * 3 + sp / 2, a * 4 - sp / 2, a * 3 + sp / 2, a * 4 - sp / 2, a + sp / 2, a * 2 - sp / 2, a + sp / 2, a * 2 - sp / 2};
        float[] dy = {e * 2, b / 2, b / 2, b - e * 2, f, b / 2 + e, b - e, b * 3 / 2 - f, b / 2 + f, b + e, b * 3 / 2 - e, b * 2 - f,
                b + e, b / 2 + f, b * 2 - f, b * 3 / 2 - e, b / 2 + e, f, b * 3 / 2 - f, b - e, b * 3 / 2 + f, b * 2 + e, b * 5 / 2 - e, b * 3 - f};
        //p.setStyle(Paint.Style.FILL);
        for (i = 0; i < 6; i++) {
            float dx0 = dx[i * 4], dx1 = dx[i * 4 + 1], dx2 = dx[i * 4 + 2], dx3 = dx[i * 4 + 3];
            float dy0 = dy[i * 4], dy1 = dy[i * 4 + 1], dy2 = dy[i * 4 + 2], dy3 = dy[i * 4 + 3];
            drawPolygon(p, c, colors[img[d++]],
                    new float[] {stx + dx0, stx + (dx0 + dx1) / 2, stx + (dx0 + dx2) / 2},
                    new float[] {sty + dy0, sty + (dy0 + dy1) / 2, sty + (dy0 + dy2) / 2}, true);
            drawPolygon(p, c, colors[img[d++]],
                    new float[] {stx + dx1, stx + (dx0 + dx1) / 2, stx + (dx1 + dx3) / 2},
                    new float[] {sty + dy1, sty + (dy0 + dy1) / 2, sty + (dy1 + dy3) / 2}, true);
            drawPolygon(p, c, colors[img[d++]],
                    new float[] {stx + (dx0 + dx2) / 2, stx + (dx0 + dx1) / 2, stx + (dx1 + dx3) / 2, stx + (dx2 + dx3) / 2},
                    new float[] {sty + (dy0 + dy2) / 2, sty + (dy0 + dy1) / 2, sty + (dy1 + dy3) / 2, sty + (dy2 + dy3) / 2}, true);
            drawPolygon(p, c, colors[img[d++]],
                    new float[] {stx + dx2, stx + (dx0 + dx2) / 2, stx + (dx2 + dx3) / 2},
                    new float[] {sty + dy2, sty + (dy0 + dy2) / 2, sty + (dy2 + dy3) / 2}, true);
            drawPolygon(p, c, colors[img[d++]],
                    new float[] {stx + dx3, stx + (dx3 + dx2) / 2, stx + (dx1 + dx3) / 2},
                    new float[] {sty + dy3, sty + (dy3 + dy2) / 2, sty + (dy1 + dy3) / 2}, true);
        }
    }

    private void drawSideBackground(Paint p, Canvas c, int width, int cx, int cy, int clockRadius,
                                    int faceBackgroundDist, int faceBackgroundRadius) {
        drawCircle(p, c, width, cx, cy, clockRadius);
        drawCircle(p, c, width, cx - faceBackgroundDist, cy - faceBackgroundDist, faceBackgroundRadius);
        drawCircle(p, c, width, cx - faceBackgroundDist, cy + faceBackgroundDist, faceBackgroundRadius);
        drawCircle(p, c, width, cx + faceBackgroundDist, cy - faceBackgroundDist, faceBackgroundRadius);
        drawCircle(p, c, width, cx + faceBackgroundDist, cy + faceBackgroundDist, faceBackgroundRadius);
    }

    private void drawCircle(Paint p, Canvas c, int w, int cx, int cy, int rad) {
        float[] scaledPoint = scalePoint(w, cx, cy);
        c.drawCircle(scaledPoint[0], scaledPoint[1], scaledPoint[2] * rad, p);
    }

    private float[] scalePoint(int width, float cx, float cy) {
        float scale = width / 220F;
        float x = cx * scale + (width - (220 * scale)) / 2;
        float y = cy * scale + (width * 3 / 4 - (110 * scale)) / 2;
        return new float[] {x, y, scale};
    }

    private void drawClockFace(Paint p, Canvas cv, int w, int cx, int cy, int color, int hour) {
        float[] scaled = scalePoint(w, cx, cy);
        //p.setColor(Color.BLACK);
        //drawCircle(p, cv, w, cx, cy, 12);
        p.setColor(color);
        drawCircle(p, cv, w, cx, cy, 11);
        p.setColor(Color.RED);
        drawCircle(p, cv, w, cx, cy, 3);
        float[] arx = {scalePoint(w, cx, cy - 10)[0], scalePoint(w, cx + 3, cy - 1)[0], scalePoint(w, cx - 3, cy - 1)[0]},
                ary = {scalePoint(w, cx, cy - 10)[1], scalePoint(w, cx + 3, cy - 1)[1], scalePoint(w, cx - 3, cy - 1)[1]};
        p.setColor(color);
        cv.save();
        for (int i = 0; i < 12; i++) {
            drawCircle(p, cv, w, cx - 13, cy, 1);
            cv.rotate(30, scaled[0], scaled[1]);
        }
        cv.restore();
        cv.save();
        cv.rotate(30 * hour, scaled[0], scaled[1]);
        drawPolygon(p, cv, Color.RED, arx, ary, false);
        cv.restore();
        p.setColor(Color.YELLOW);
        drawCircle(p, cv, w, cx, cy, 2);
        arx = new float[] {scalePoint(w, cx, cy - 8)[0], scalePoint(w, cx + 2, cy - 0.5f)[0], scalePoint(w, cx - 2, cy - 0.5f)[0]};
        ary = new float[] {scalePoint(w, cx, cy - 8)[1], scalePoint(w, cx + 2, cy - 0.5f)[1], scalePoint(w, cx - 2, cy - 0.5f)[1]};
        cv.save();
        cv.rotate(30 * hour, scaled[0], scaled[1]);
        drawPolygon(p, cv, Color.YELLOW, arx, ary, false);
        cv.restore();
    }

    private void drawPeg(Paint p, Canvas c, int w, int cx, int cy, int pegValue) {
        int color = pegValue == 1 ? Color.YELLOW : 0xff444400;
        p.setColor(0xff2a2a2a);
        drawCircle(p, c, w, cx, cy, 5);
        p.setColor(color);
        drawCircle(p, c, w, cx, cy, 4);
    }

    public static void drawPolygon(Paint p, Canvas c, int cl, float[] arx, float[] ary, boolean stoke) {
        p.setColor(cl);
        Path path = new Path();
        path.moveTo(arx[0], ary[0]);
        for (int idx = 1; idx < arx.length; idx++)
            path.lineTo(arx[idx], ary[idx]);
        path.close();
        p.setStyle(Paint.Style.FILL);
        c.drawPath(path, p);
        if (stoke) {
            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.BLACK);
            c.drawPath(path, p);
        }
    }

    private void drawPolygon(Paint p, Canvas c, int cl, int w, float[] arx, float[] ary) {
        p.setColor(cl);
        Path path = new Path();
        float[] d = scalePoint(w, arx[0], ary[0]);
        path.moveTo(d[0], d[1]);
        for (int idx = 1; idx < arx.length; idx++) {
            d = scalePoint(w, arx[idx], ary[idx]);
            path.lineTo(d[0], d[1]);
        }
        path.close();
        p.setStyle(Paint.Style.FILL);
        c.drawPath(path, p);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        c.drawPath(path, p);
    }

    private float s18(int i) {
        return (float) Math.sin(Math.PI * i / 10);
    }

    private float c18(int i) {
        return (float) Math.cos(Math.PI * i / 10);
    }

    private float[][] rotate(float a, float b, float[] x, float[] y, int i) {
        float[][] ary = new float[2][x.length];
        for (int j = 0; j < x.length; j++) {
            ary[0][j] = (float) (x[j] * Math.cos(Math.toRadians(i)) - y[j] * Math.sin(Math.toRadians(i)) + a);
            ary[1][j] = (float) (x[j] * Math.sin(Math.toRadians(i)) + y[j] * Math.cos(Math.toRadians(i)) + b);
        }
        return ary;
    }

    private float cos1(int index, float[] ag, float rd) {
        return (float) (Math.cos(ag[index]) * rd);
    }

    private float sin1(int index, float[] ag, float rd) {
        return (float) (Math.sin(ag[index]) * rd);
    }

    private int[] rd(int[] arr) {
        int[] out = new int[arr.length];
        int j = 0;
        for (int i = 0; i < arr.length; i++)
            if (i == 0 || arr[i] != arr[i-1])
                out[j++] = arr[i];
        int[] temp = new int[j];
        for (int i = 0; i < j; i++) temp[i] = out[i];
        return temp;
    }
}
