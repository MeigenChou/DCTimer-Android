package solver;

import android.util.Log;

import java.util.Random;

public class Skewb {
    private static short[][] ctm = new short[360][4];
    private static short[][] cpm = new short[36][4];
    private static short[][] com = new short[2187][4];
    private static byte[] ctd = new byte[360];
    private static byte[] cd = new byte[2187 * 36];
    private static Random r = new Random();
    private static int[] seq = new int[12];

    static {
        // move tables
		/* center
		 * 		  0
		 *	4	1	2	3
		 * 		  5
		 */
        int[] arr = new int[7];
        for (int i = 0; i < 360; i++)
            for (int j = 0; j < 4; j++) {
                Utils.idxToPerm(arr, i, 6, true);
                switch (j) {
                    case 0: Utils.circle(arr, 2, 5, 3); break;	//R
                    case 1: Utils.circle(arr, 0, 3, 4); break;	//U
                    case 2: Utils.circle(arr, 1, 4, 5); break;	//L
                    case 3: Utils.circle(arr, 3, 5, 4); break;	//B
                }
                ctm[i][j] = (short) Utils.permToIdx(arr, 6, true);
            }
		/* corner permutation
		 *     2-0
		 *  0		1
		 *  	3
		 * 2-1	   2-2
		 *      2
		 */
        int[] arr2 = new int[3];
        for (int i = 0; i < 12; i++)
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 4; k++) {
                    Utils.idxToPerm(arr, i, 4, true);
                    Utils.idxToPerm(arr2, j, 3, true);
                    switch (k) {
                        case 0: Utils.circle(arr, 1, 2, 3); break;	//R
                        case 1: Utils.circle(arr, 0, 1, 3); break;	//U
                        case 2: Utils.circle(arr, 2, 0, 3); break;	//L
                        case 3: Utils.circle(arr2, 0, 2, 1); break;	//B
                    }
                    cpm[i * 3 + j][k] = (short) (Utils.permToIdx(arr, 4, true) * 3 + Utils.permToIdx(arr2, 3, true));
                }
            }
        //corner orientation
		/*
		 *		0
		 *	1		2
		 *		3
		 *	4		5
		 *		6
		 */
        for (int i = 0; i < 2187; i++) {
            for (int j = 0; j < 4; j++) {
                Utils.idxToOri(arr, i, 7, false);
                switch (j) {
                    case 0:
                        Utils.circle(arr, 2, 6, 3); arr[2] += 2; arr[3] += 2; arr[5]++; arr[6] += 2; break;
                    case 1:
                        Utils.circle(arr, 1, 2, 3); arr[0]++; arr[1] += 2; arr[2] += 2; arr[3] += 2; break;
                    case 2:
                        Utils.circle(arr, 1, 3, 6); arr[1] += 2; arr[3] += 2; arr[4]++; arr[6] += 2; break;
                    case 3:
                        Utils.circle(arr, 0, 5, 4); arr[0] += 2; arr[3]++; arr[4] += 2; arr[5] += 2; break;
                }
                com[i][j] = (short) Utils.oriToIdx(arr, 7, false);
            }
        }

        // distance table
        for (int i = 0; i < 360; i++) ctd[i] = -1;
        ctd[0] = 0;
        //int c = 1;
        Utils.createPrun(ctd, 5, ctm, 2);

        for (int i = 0; i < 78732; i++) cd[i] = -1;
        cd[0] = 0;
        Utils.createPrun(cd, 7, com, cpm, 2);
    }

    private static String[] turn = {"R", "U", "L", "B"};
    private static String[] suff = {"'", ""};
    private static boolean search(int ct, int cp, int co, int d, int l) {
        if (d == 0) return ct == 0 && co == 0 && cp == 0;
        if (ctd[ct] > d || cd[co * 36 + cp] > d) return false;
        if (l == -2) {
            int n = r.nextInt(8);
            int k = n / 2;
            n %= 2;
            int p = ct, q = cp, r = co;
            for (int m = 0; m < n; m++) {
                p = ctm[p][k]; q = cpm[q][k]; r = com[r][k];
            }
            if (search(p, q, r, d-1, k)) {
                seq[d] = k << 1 | n;
                //sol.append("RULB".charAt(k)).append(suff[n]).append(' ');
                return true;
            }
        } else for (int k = 0; k < 4; k++)
            if (k != l) {
                int p = ct, q = cp, r = co;
                for (int m = 0; m < 2; m++) {
                    p = ctm[p][k]; q = cpm[q][k]; r = com[r][k];
                    if (search(p, q, r, d-1, k)) {
                        seq[d] = k << 1 | m;
                        //sol.append("RULB".charAt(k)).append(suff[m]).append(' ');
                        return true;
                    }
                }
            }
        return false;
    }

    public static String scramble() {
        int ct = r.nextInt(360), cp, co;
        do {
            cp = r.nextInt(36);
            co = r.nextInt(2187);
        } while (cd[co * 36 + cp] < 0);
        for (int d = 0; d < 12; d++)
            if (search(ct, cp, co, d, -1)) {
                if (d < 2) return scramble();
                if (d < 4) {
                    continue;
                }
                StringBuilder sol = new StringBuilder();
                for (int i = 1; i <= d; i++)
                    sol.append(turn[seq[i] >> 1]).append(suff[seq[i] & 1]).append(" ");
                return sol.toString();
            }
        return "error";
    }

    private static String scramble(int minLen) {
        int ct = r.nextInt(360), cp, co;
        do {
            cp = r.nextInt(36);
            co = r.nextInt(2187);
        } while (cd[co * 36 + cp] < 0);
        for (int d = 0; d < 12; d++)
            if (search(ct, cp, co, d, -1)) {
                if (d < minLen) return "error";
                if (d < 11) {
                    //sol = new StringBuilder();
                    search(ct, cp, co, 11, -2);
                }
                StringBuilder sol = new StringBuilder();
                for (int i = 1; i <= 11; i++)
                    sol.append(turn[seq[i] >> 1]).append(suff[seq[i] & 1]).append(" ");
                return sol.toString();
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

    /*
     *				0		1
     *					2
     *				3		4
     *	5		6	10		11	15		16	20		21
     *		7			12			17			22
     *	8		9	13		14	18		19	23		24
     *				25		26
     *					27
     *				28		29
     */
    private static int[] img = new int[30];
    private static void initColor() {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 6; j++) img[j * 5 + i] = j;
    }

    private static void move(int turn) {
        switch (turn) {
            case 0:	//R
                Utils.circle(img, 17, 27, 22);
                Utils.circle(img, 19, 29, 23);
                Utils.circle(img,  1, 14,  8);
                Utils.circle(img, 20, 18, 28);
                Utils.circle(img, 16, 26, 24);
                break;
            case 1:	//U
                Utils.circle(img,  2, 22,  7);
                Utils.circle(img,  0, 21,  5);
                Utils.circle(img,  3, 20,  8);
                Utils.circle(img, 10, 16, 28);
                Utils.circle(img,  6,  1, 24);
                break;
            case 2:	//L
                Utils.circle(img, 12,  7, 27);
                Utils.circle(img, 13,  9, 25);
                Utils.circle(img,  3, 24, 18);
                Utils.circle(img, 10,  8, 26);
                Utils.circle(img,  6, 28, 14);
                break;
            case 3:	//B
                Utils.circle(img, 22, 27,  7);
                Utils.circle(img, 24, 28,  8);
                Utils.circle(img,  0, 19, 13);
                Utils.circle(img,  5, 23, 25);
                Utils.circle(img, 21, 29,  9);
                break;
        }
    }

    public static int[] image(String scramble) {
        initColor();
        String[] s = scramble.split(" ");
        for (int i = 0; i < s.length; i++)
            if (s[i].length() > 0) {
                int mov = "RULB".indexOf(s[i].charAt(0));
                move(mov);
                if (s[i].length() > 1)
                    move(mov);
            }
        return img;
    }
}
