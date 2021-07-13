package solver;

import java.util.Arrays;
import java.util.Random;

public class Skewb {
    static short[][] ctm = new short[360][4];
    static short[][] cpm = new short[12][4];
    static short[][] com = new short[2187][4];
    static byte[] ctd = new byte[360];
    static byte[] cd = new byte[12 * 2187];
    static int[] seq = new int[12];
    private static String[] suff = {"'", ""};
    static Random r = new Random();

    static void init() {
        /* center
         * 		  0
         *	4	1	2	3
         * 		  5
         */
        int[] arr = new int[8];
        for (int i = 0; i < 360; i++)
            for (int j = 0; j < 4; j++) {
                Utils.idxToPerm(arr, i, 6, true);
                switch (j) {
                    case 0: Utils.circle(arr, 2, 5, 3); break;	//R
                    case 1: Utils.circle(arr, 0, 3, 4); break;	//U
                    case 2: Utils.circle(arr, 1, 4, 5); break;	//L
                    case 3: Utils.circle(arr, 0, 1, 2); break;	//F
                }
                //if (i == 0) System.out.println(j+": "+ Arrays.toString(arr));
                ctm[i][j] = (short) Utils.permToIdx(arr, 6, true);
            }
        /* corner
         *      4
         *  0	    1
         *  	3
         *  5	    6
         *      2
         */
        for (int i=0; i<12; i++) {
            for (int j = 0; j < 4; j++) {
                Utils.idxToPerm(arr, i, 4, true);
                switch (j) {
                    case 0: Utils.circle(arr, 1, 2, 3); break;  //R
                    case 1: Utils.circle(arr, 0, 1, 3); break;  //U
                    case 2: Utils.circle(arr, 2, 0, 3); break;  //L
                    case 3: Utils.circle(arr, 0, 2, 1); break;  //F
                }
                cpm[i][j] = (short) Utils.permToIdx(arr, 4, true);
            }
        }
        for (int i = 0; i < 2187; i++) {
            for (int j = 0; j < 4; j++) {
                Utils.idxToOri(arr, i, 8, true);
                switch (j) {
                    case 0: //R
                        Utils.circle(arr, 1, 2, 3);
                        arr[6]++; arr[1] += 2; arr[2] += 2; arr[3] += 2;
                        break;
                    case 1: //U
                        Utils.circle(arr, 0, 1, 3);
                        arr[4]++; arr[0] += 2; arr[1] += 2; arr[3] += 2;
                        break;
                    case 2: //L
                        Utils.circle(arr, 2, 0, 3);
                        arr[5]++; arr[0] += 2; arr[2] += 2; arr[3] += 2;
                        break;
                    case 3: //F
                        Utils.circle(arr, 0, 2, 1);
                        arr[7]++; arr[0] += 2; arr[1] += 2; arr[2] += 2;
                        break;
                }
                com[i][j] = (short) Utils.oriToIdx(arr, 8, true);
            }
        }

        for (int i = 0; i < 360; i++) ctd[i] = -1;
        ctd[0] = 0;
        Utils.createPrun(ctd, 5, ctm, 2);

        Arrays.fill(cd, (byte) -1);
        cd[0] = 0;
        Utils.createPrun(cd, 7, com, cpm, 2);
    }

    static {
        init();
    }

    static boolean search(int ct, int cp, int co, int d, int l) {
        if (d == 0) return ct == 0 && co == 0 && cp == 0;
        if (ctd[ct] > d || cd[co * 12 + cp] > d) return false;
        for (int k = 0; k < 4; k++) {
            if (k != l) {
                int p = ct, q = cp, r = co;
                for (int m = 0; m < 2; m++) {
                    p = ctm[p][k]; q = cpm[q][k]; r = com[r][k];
                    //System.out.println(p+", "+q+", "+r+": "+search(p, q, r, d-1, k));
                    if (search(p, q, r, d-1, k)) {
                        seq[d] = k << 1 | m;
                        //sol.append("RULB".charAt(k)).append(suff[m]).append(' ');
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String scramble() {
        int ct = r.nextInt(360), cp, co;
        do {
            cp = r.nextInt(12);
            co = r.nextInt(2187);
        } while (cd[co * 12 + cp] < 0);
        for (int d = 0; d < 12; d++)
            if (search(ct, cp, co, d, -1)) {
                if (d < 2) return scramble();
                if (d < 5) {
                    continue;
                }
                return move2fcn(d);
//                StringBuilder sol = new StringBuilder();
//                for (int i = 1; i <= d; i++)
//                    sol.append("RULF".charAt(seq[i] >> 1)).append(suff[seq[i] & 1]).append(" ");
//                return sol.toString();
            }
        return "error";
    }

    private static String scramble(int minLen) {
        int ct = r.nextInt(360), cp, co;
        do {
            cp = r.nextInt(12);
            co = r.nextInt(2187);
        } while (cd[co * 12 + cp] < 0);
        for (int d = 0; d < 12; d++)
            if (search(ct, cp, co, d, -1)) {
                if (d < minLen) return "error";
                if (d < 11) {
                    search(ct, cp, co, 11, -1);
                }
                return move2fcn(11);
            }
        return "error";
    }

    public static String scrambleWCA() {
        String scr;
        do {
            scr = scramble(7);
        } while (scr.equals("error"));
        return scr;
    }

    public static String scrambleL2L() {
        int[] arr = new int[6], arr2 = new int[4];
        Utils.idxToPerm(arr, r.nextInt(60), 5, true);
        arr[5] = 5;
        int ct = Utils.permToIdx(arr, 6, true);
        int cp = 0, co;
        do {
            //cp = r.nextInt(12);
            Utils.idxToOri(arr2, r.nextInt(27), 4, true);
            arr = new int[] {arr2[0], arr2[1], 0, 0, arr2[2], 0, 0, arr2[3]};
            co = Utils.oriToIdx(arr, 8, true);
        } while (cd[co * 12 + cp] < 0);
        for (int d = 0; d < 12; d++)
            if (search(ct, cp, co, d, -1)) {
                if (d < 2) return scramble();
                if (d < 5) {
                    continue;
                }
                return move2fcn(d);
            }
        return "error";
    }

    static String move2fcn(int d) {
        String[] move2str = { "R", "U", "L", "B" };
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= d; i++) {
            int mov = seq[i] >> 1;
            int pow = seq[i] & 1;
            if (mov == 3) {
                for (int j=0; j<=pow; j++) {
                    String temp = move2str[2];
                    move2str[2] = move2str[1];
                    move2str[1] = move2str[0];
                    move2str[0] = temp;
                }
            }
            sb.append(move2str[mov]).append(suff[pow]).append(" ");
        }
        return sb.toString();
    }
}
