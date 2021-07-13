package solver;

import java.util.Random;

public class Pyraminx {
    private static int[] colmap = new int[91];
    private static byte[] perm = new byte[360];	// pruning table for edge permutation
    private static byte[] twst = new byte[2592];	// pruning table for edge orientation+twist
    private static short[][] permmv = new short[360][4];	// transition table for edge permutation
    private static short[][] twstmv = new short[81][4];	// transition table for corner orientation
    private static short[][] flipmv = new short[32][4];	// transition table for edge orientation
    private static String[] turn = {"L", "R", "B", "U"};
    private static String[] suff = {"'", ""};
    private static String[] tips = {"l", "r", "b", "u"};
    //private static int[] seq = new int[12];
    private static int[] img = new int[91];
    private static Random r = new Random();

    static {
        calcperm();
    }

    public static String scramble() {
        String scramble;
        do {
            int t = r.nextInt(2592), q = r.nextInt(360);
            scramble = scramble(q, t);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scrambleL4E() {
        String scramble;
        do {
            int[] ps = {0, 1, 2, 3, 4, 5};
            Utils.idxToPerm(ps, r.nextInt(12), 4, true);
            int[] ts = new int[6];
            Utils.idxToFlip(ts, r.nextInt(8), 4, true);
            int p = Utils.permToIdx(ps, 6, true);
            int t = r.nextInt(3) * 864 + Utils.flipToIdx(ts, 6, true);
            scramble = scramble(p, t);
        } while (scramble.equals("error"));
        return scramble;
    }

    private static String scramble(int p, int t) {
        int[] seq = new int[12];
        for (int l = 0; l < 12; l++)
            if (search(p, t, l, -1, seq)) {
                if (l < 2) return "error";
                if (l < 5) {
                    continue;
                }
                StringBuilder sol = new StringBuilder();
                for (int i = 1; i <= l; i++)
                    sol.append(turn[seq[i] >> 1]).append(suff[seq[i] & 1]).append(" ");
                for (int i = 0; i < 4; i++) {
                    int j = r.nextInt(3);
                    if (j < 2)
                        sol.append(tips[i]).append(suff[j]).append(" ");
                }
                return sol.toString();
            }
        return "error";
    }

    public static String scrambleWCA() {
        String scr;
        do {
            scr = scramble(6);
        } while (scr.equals("error"));
        return scr;
    }

    private static String scramble(int minLen) {
        int a = 0;
        int[] tip = new int[4];
        for (int i = 0; i < 4; i++) {
            tip[i] = r.nextInt(3);
            if (tip[i] < 2)
                a++;
        }
        int t = r.nextInt(2592), q = r.nextInt(360);
        int[] seq = new int[12];
        for (int l = 0; l < 12; l++) {
            if (search(q, t, l, -1, seq)) {
                if (l + a < minLen) return "error";
                if (l < 11) {
                    //sol = new StringBuilder();
                    search(q, t, 11, -2, seq);
                }
                StringBuilder sol = new StringBuilder();
                int last = -1;
                for (int i = 1; i <= 11; i++) {
                    if (last == seq[i] / 2) return "error";
                    sol.append(turn[seq[i] >> 1]).append(suff[seq[i] & 1]).append(" ");
                    last = seq[i] >> 1;
                }
                for (int i = 0; i < 4; i++) {
                    if (tip[i] < 2)
                        sol.append(tips[i]).append(suff[tip[i]]).append(' ');
                }
                return sol.toString();
            }
        }
        return "error";
    }

    private static boolean search(int p, int t, int l, int lm, int[] seq) {
        //searches for solution, from position p|t, in l moves exactly. last move was lm, current depth=d
        if (l == 0) return p == 0 && t == 0;
        if (perm[p] > l || twst[t] > l) return false;
        int q, s, a, m;
        if (lm == -2) {
            int n = r.nextInt(8);
            m = n / 2;
            n %= 2;
            q = p; s = t;
            for (a = 0; a <= n; a++) {
                q = permmv[q][m];
                s = twstmv[s >> 5][m] << 5 | flipmv[s & 31][m];
            }
            if (search(q, s, l - 1, m, seq)) {
                seq[l] = m << 1 | n;
                return true;
            }
        } else for (m = 0; m < 4; m++) {
            if (m != lm) {
                q = p; s = t;
                for (a = 0; a < 2; a++) {
                    q = permmv[q][m];
                    s = twstmv[s >> 5][m] << 5 | flipmv[s & 31][m];
                    if (search(q, s, l - 1, m, seq)) {
                        seq[l] = m << 1 | a;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void calcperm() {
        int c, q, l, m, p, r;
        //calculate solving arrays
        //first permutation
        //initialise arrays
        for (p = 0; p < 360; p++) {
            perm[p] = -1;
            for (m = 0; m < 4; m++)
                permmv[p][m] = (short) getprmmv(p, m);
        }
        //fill it
        perm[0] = 0;
        Utils.createPrun(perm, 5, permmv, 2);

        //then twist && flip
        //initialise arrays
        for (p = 0; p < 81; p++)
            for (m = 0; m < 4; m++) {
                twstmv[p][m] = (short) gettwsmv(p, m);
                if (p < 32) flipmv[p][m] = (short) getflpmv(p, m);
            }
        //fill it
        for (p = 0; p < 2592; p++) twst[p] = -1;
        twst[0] = 0;
        Utils.createPrun(twst, 7, twstmv, flipmv, 2);
    }

    private static int getprmmv(int p, int m) {
        //given position p<360 and move m<4, return new position number
        //convert number into array
        int[] ps = new int[6];
        //edge permutation
        Utils.idxToPerm(ps, p, 6, true);
        //perform move on array
        if (m == 0) {
            Utils.circle(ps, 1, 5, 2);  //L
        } else if (m == 1) {
            Utils.circle(ps, 0, 2, 4);  //R
        } else if (m == 2) {
            Utils.circle(ps, 3, 4, 5);  //B
        } else if (m == 3) {
            Utils.circle(ps, 0, 3, 1);  //U
        }
        //convert array back to number
        return(Utils.permToIdx(ps, 6, true));
    }

    private static int getflpmv(int p, int m) {
        //given orientation p<32 and move m<4, return new position number
        //convert number into array;
        int[] ps = new int[6];
        //edge orientation
        Utils.idxToFlip(ps, p, 6, true);
        //perform move on array
        switch (m) {
            case 0:	//L
                Utils.circle(ps, 1, 5, 2);
                ps[2] ^= 1; ps[5] ^= 1;
                break;
            case 1:	//R
                Utils.circle(ps, 0, 2, 4);
                ps[0] ^= 1; ps[2] ^= 1;
                break;
            case 2:	//B
                Utils.circle(ps, 3, 4, 5);
                ps[3] ^= 1; ps[4] ^= 1;
                break;
            case 3:	//U
                Utils.circle(ps, 0, 3, 1);
                ps[1] ^= 1; ps[3] ^= 1;
                break;
        }
        return Utils.flipToIdx(ps, 6, true);
    }

    private static int gettwsmv(int p, int m) {
        //given orientation p<81 and move m<4, return new position number
        //convert number into array;
        int[] ps = new int[4];
        //corner orientation
        Utils.idxToOri(ps, p, 4, false);
        //perform move on array
        switch (m) {
            case 0:	//L
                ps[1]++; if (ps[1] == 3) ps[1] = 0;
                break;
            case 1:	//R
                ps[2]++; if (ps[2] == 3) ps[2] = 0;
                break;
            case 2:	//B
                ps[3]++; if (ps[3] == 3) ps[3] = 0;
                break;
            case 3:	//U
                ps[0]++; if (ps[0] == 3) ps[0] = 0;
                break;
        }
        //convert array back to number
        //corner orientation
        return(Utils.oriToIdx(ps, 4, false));
    }

    private static void picmove(int type, int direction) {
        switch (type) {
            case 0: // L
                rotate3(14, 58, 18, direction);
                rotate3(15, 57, 31, direction);
                rotate3(16, 70, 32, direction);
            case 4: // l
                rotate3(30, 28, 56, direction);
                break;
            case 1: // R
                rotate3(32, 72, 22, direction);
                rotate3(33, 59, 23, direction);
                rotate3(20, 58, 24, direction);
            case 5: // r
                rotate3(34, 60, 36, direction);
                break;
            case 2: // B
                rotate3(14, 10, 72, direction);
                rotate3( 1, 11, 71, direction);
                rotate3( 2, 24, 70, direction);
            case 6: // b
                rotate3( 0, 12, 84, direction);
                break;
            case 3: // U
                rotate3( 2, 18, 22, direction);
                rotate3( 3, 19,  9, direction);
                rotate3(16, 20, 10, direction);
            case 7: // u
                rotate3( 4,  6,  8, direction);
                break;
        }
    }

    private static void rotate3(int v1, int v2, int v3, int clockwise) {
        if (clockwise == 2) {
            Utils.circle(colmap, v3, v2, v1);
        } else {
            Utils.circle(colmap, v1, v2, v3);
        }
    }

    public static int[] image(String scr) {
        String[] s = scr.split(" ");
        colmap = new int[] {
                1, 1, 1, 1, 1, 0, 2, 0, 3, 3, 3, 3, 3,
                0, 1, 1, 1, 0, 2, 2, 2, 0, 3, 3, 3, 0,
                0, 0, 1, 0, 2, 2, 2, 2, 2, 0, 3, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0
        };
        int turn, suff;
        for (int i = 0; i < s.length; i++) {
            suff = s[i].length();
            if (suff > 0) {
                turn = "LRBUlrbu".indexOf(s[i].charAt(0));
                picmove(turn, suff);
            }
        }
        int d = 0;
        for (int x = 0; x < 91; x++)
            img[d++] = colmap[x] - 1;
        return img;
    }
}
