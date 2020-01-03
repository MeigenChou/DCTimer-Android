package solver;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;

public class EightPuzzle {
    private static byte[] prun = new byte[362880];
    private static int[] pz = new int[9];
    static int[] seq = new int[25];
    private static String[] suff = {" ", "2 "};
    private static boolean ini = false;
    private static int[][] moveIdx = {
            {-1, 3, -1, 1}, {-1, 4, 0, 2}, {-1, 5, 1, -1},
            {0, 6, -1, 4}, {1, 7, 3, 5}, {2, 8, 4, -1},
            {3, -1, -1, 7}, {4, -1, 6, 8}, {5, -1, 7, -1}
    };

    static void init() {
        if (ini) return;
        long time = System.currentTimeMillis();
        int[] arr = new int[9];
        Arrays.fill(prun, (byte) -1);
        prun[0] = 0;
        int c = 1;
        for (int d = 0; d < 24; d++) {
            //c = 0;
            for (int i=0; i<362880; i++)
                if (prun[i] == d) {
                    Utils.set11Perm(arr, i, 9);
                    for (int m=0; m<4; m++) {
                        System.arraycopy(arr, 0, pz, 0, 9);
                        for (int n = 0; n < 2; n++) {
                            int next = move(m);
                            if (next == -1) break;
                            next = Utils.get11Perm(pz, 9);
                            if (prun[next] < 0) {
                                prun[next] = (byte) (d + 1);
                                c++;
                            }
                        }
                    }
                }
            //Log.w("dct", d + 1 + "\t" + c);
        }
        time = System.currentTimeMillis() - time;
        Log.w("dct", "init "+time+"ms");
        ini = true;
    }

    private static int move(int m) {
        int pos = 0;
        for (; pos < 9; pos++) {
            if (pz[pos] == 8) break;
        }
        int next = moveIdx[pos][m];
        if (next != -1) {
            Utils.swap(pz, pos, next);
        }
        return next;
    }

    static boolean search(int state, int d) {
        if (d == 0) return state == 0;
        if (prun[state] > d) return false;
        int[] arr = new int[9];
        Utils.set11Perm(arr, state, 9);
        for (int m = 0; m < 4; m++) {
            System.arraycopy(arr, 0, pz, 0, 9);
            for (int n = 0; n < 2; n++) {
                int next = move(m);
                if (next == -1) break;
                next = Utils.get11Perm(pz, 9);
                if (search(next, d - 1)) {
                    seq[d] = m << 1 | n;
                    return true;
                }
            }
        }
        return false;
    }

    public static String scramble(Random r) {
        init();
        int n;
        do {
            n = r.nextInt(362880);
        } while (prun[n] < 4);
        for (int d = 0; d < 24; d++) {
            if (search(n, d)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= d; i++) {
                    sb.append("UDLR".charAt(seq[i] >> 1)).append(suff[seq[i] & 1]);
                }
                return sb.toString();
            }
        }
        return "error";
    }

    public static int[] image(String scramble) {
        for (int i = 0; i < 9; i++) pz[i] = i;
        int blank = 8;
        String[] s = scramble.split(" ");
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() > 0) {
                int move = "DURL".indexOf(s[i].charAt(0));
                int next = moveIdx[blank][move];
                if (next != -1) {
                    Utils.swap(pz, blank, next);
                    blank = next;
                }
                if (s[i].length() > 1 && s[i].charAt(1) == '2') {
                    next = moveIdx[blank][move];
                    if (next != -1) {
                        Utils.swap(pz, blank, next);
                        blank = next;
                    }
                }
            }
        }
        return pz;
    }
}
