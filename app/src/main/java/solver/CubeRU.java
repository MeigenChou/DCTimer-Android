package solver;

import android.util.Log;

import java.util.Random;

import static solver.Utils.permutationSign;
import static solver.Utils.suff;
import static solver.Utils.suffInv;

public class CubeRU {
    private static short[][] cpm = new short[720][2];
    private static short[][] epm = new short[5040][2];
    private static short[][] com = new short[243][2];
    private static byte[] cd = new byte[720 * 243];
    private static byte[] epd = new byte[5040];
    private static int[] seq = new int[21];

    static {
        long time = System.currentTimeMillis();
        int[] arr = new int[7];
        for (int i = 0; i < 720; i++) {
            for (int j = 0; j < 2; j++) {
                Utils.idxToPerm(arr, i, 6, false);
                if (j == 0) Utils.circle(arr, 0, 3, 2, 1);
                else Utils.circle(arr, 1, 2, 4, 5);
                cpm[i][j] = (short) Utils.permToIdx(arr, 6, false);
            }
        }
        for (int i = 0; i < 243; i++) {
            for (int j = 0; j < 2; j++) {
                Utils.idxToOri(arr, i, 6, true);
                if (j == 0) Utils.circle(arr, 0, 3, 2, 1);
                else {
                    Utils.circle(arr, 1, 2, 4, 5, new int[] {1, 2, 1, 2});
                    //arr[1] = (arr[1] + 1) % 3; arr[2] = (arr[2] + 2) % 3;
                    //arr[4] = (arr[4] + 1) % 3; arr[5] = (arr[5] + 2) % 3;
                }
                com[i][j] = (short) Utils.oriToIdx(arr, 6, true);
            }
        }
        for (int i = 0; i < 5040; i++) {
            for (int j = 0; j < 2; j++) {
                Utils.idxToPerm(arr, i, 7, false);
                if (j == 0) Utils.circle(arr, 0, 3, 2, 1);
                else Utils.circle(arr, 1, 6, 5, 4);
                epm[i][j] = (short) Utils.permToIdx(arr, 7, false);
            }
        }
        for (int i = 1; i < 174960; i++) cd[i] = -1;
        cd[0] = 0;
        Utils.createPrun(cd, 14, cpm, com, 3);
        for (int i = 1; i < 5040; i++) epd[i] = -1;
        epd[0] = 0;
        Utils.createPrun(epd, 11, epm, 3);
        time = System.currentTimeMillis() - time;
        Log.w("dct", "init "+time+"ms");
    }

    private static String[] turn = {"U", "R"};
    private static String[] turnlu = {"U", "L"};
    private static boolean search(int cp, int co, int ep, int depth, int lm) {
        if (depth == 0) return cp == 0 && co == 0 && ep == 0;
        if (cd[cp * 243 + co] > depth || epd[ep] > depth) return false;
        for (int i = 0; i < 2; i++) {
            if (i != lm) {
                int d = cp, w = co, y = ep;
                for (int j = 0; j < 3; j++) {
                    d = cpm[d][i]; w = com[w][i]; y = epm[y][i];
                    if (search(d, w, y, depth - 1, i)) {
                        seq[depth] = i * 3 + j;
                        //sb.insert(0, turn[i] + suff[j]+" ");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String scramble(boolean lu) {
        int cp, co, ep;
        int[] c = new int[6], e = new int[7];
        Random r = new Random();
        do {
            do {
                cp = r.nextInt(720);
                co = r.nextInt(243);
            }
            while (cd[cp * 243 + co] < 0);
            ep = r.nextInt(5040);
            Utils.idxToPerm(c, cp, 6, false);
            Utils.idxToPerm(e, ep, 7, false);
        } while (permutationSign(c) != permutationSign(e));
        for (int d = 0; d < 21; d++) {
            if (search(cp, co, ep, d, -1)) {
                if (d < 2) return scramble(lu);
                if (d < 4) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= d; i++) {
                    if (lu) sb.append(turnlu[seq[i] / 3]).append(suff[seq[i] % 3]).append(" ");
                    else sb.append(turn[seq[i] / 3]).append(suffInv[seq[i] % 3]).append(" ");
                }
                return sb.toString();
            }
        }
        return "error";
    }
}
