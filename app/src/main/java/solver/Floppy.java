package solver;

import android.util.Log;

import java.util.Random;

public class Floppy {
    private static byte[][] distance = new byte[24][16];
    private static String[] turn = {"U", "R", "D", "L"};
    private static int[] seq = new int[10];
    static int[] arr = new int[4];

    static {
        for (int i = 0; i < 24; i++)
            for (int j = 0; j < 16; j++)
                distance[i][j] = -1;
        distance[0][0] = 0;
        int n = 1;
        for (int depth = 0; depth < 8; depth++) {
            //n = 0;
            for (int i = 0; i < 24; i++)
                for (int j = 0; j < 16; j++)
                    if (distance[i][j] == depth)
                        for (int k = 0; k < 4; k++) {
                            int cp = permMove(i, k);
                            int eo = flipMove(j, k);
                            if (distance[cp][eo] == -1) {
                                distance[cp][eo] = (byte) (depth + 1);
                                n++;
                            }
                        }
            Log.w("dct", depth + 1 + "\t" + n);
        }
    }

    private static int permMove(int cp, int m) {
        Utils.idxToPerm(arr, cp, 4, false);
        switch (m) {
            case 0: Utils.swap(arr, 0, 1); break;	//U2
            case 1: Utils.swap(arr, 1, 2); break;	//R2
            case 2: Utils.swap(arr, 2, 3); break;	//D2
            case 3: Utils.swap(arr, 0, 3); break;	//L2
        }
        return Utils.permToIdx(arr, 4, false);
    }

    private static int flipMove(int eo, int m) {
        Utils.idxToFlip(arr, eo, 4, false);
        arr[m] = 1 - arr[m];
        return Utils.flipToIdx(arr, 4, false);
    }

    private static boolean search(int cp, int eo, int d, int lm) {
        if (d == 0) return cp == 0 && eo == 0;
        if (distance[cp][eo] > d) return false;
        for (int i = 0; i < 4; i++) {
            if (i != lm) {
                int cpi = permMove(cp, i);
                int eoi = flipMove(eo, i);
                if (search(cpi, eoi, d - 1, i)) {
                    seq[d] = i;
                    //sb.insert(0, turn[i] + " ");
                    return true;
                }
            }
        }
        return false;
    }

    public static String scramble() {
        Random r = new Random();
        int cp, eo;
        do {
            cp = r.nextInt(24);
            eo = r.nextInt(16);
        } while (distance[cp][eo] < 2);
        for (int d = 3; d < 9; d++) {
            if (search(cp, eo, d, -1)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= d; i++)
                    sb.append(turn[seq[i]]).append("2 ");
                return sb.toString();
            }
        }
        return "error";
    }

    private static int[] img = new int[30];
    private static void initColor() {
        img = new int[] {
                   3, 3, 3,
                5, 4, 4, 4, 2, 1, 1, 1,
                5, 4, 4, 4, 2, 1, 1, 1,
                5, 4, 4, 4, 2, 1, 1, 1,
                   0, 0, 0
        };
    }

    private static void move(int turn) {
        switch (turn) {
            case 0: //U
                Utils.swap(img,  0,  2,  3,  7);
                Utils.swap(img,  4,  8,  6, 10);
                Utils.swap(img,  5,  9); break;
            case 1:	//R
                Utils.swap(img,  7, 23,  2, 29);
                Utils.swap(img,  6, 24, 22,  8);
                Utils.swap(img, 14, 16); break;
            case 2:	//D
                Utils.swap(img, 27, 29, 19, 23);
                Utils.swap(img, 20, 24, 22, 26);
                Utils.swap(img, 21, 25); break;
            case 3:	//L
                Utils.swap(img,  3, 19,  0, 27);
                Utils.swap(img,  4, 26, 20, 10);
                Utils.swap(img, 12, 18); break;
        }
    }

    public static int[] image(String scr) {
        initColor();
        String[] s = scr.split(" ");
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() > 0)
                move("URDL".indexOf(s[i].charAt(0)));
        }
        return img;
    }
}
