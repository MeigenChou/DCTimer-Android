package solver;

import android.util.Log;

import java.util.Arrays;

import cs.min2phase.CubieCube;

import static solver.Utils.suff;
import static solver.Utils.turn;

public class Roux {
    private static byte[][] cpm = new byte[56][6];
    private static short[][] com = new short[252][6];

    private static short[][] epm2 = new short[504][3];
    private static short[][] eom2 = new short[672][3];
    private static short[][] cpm2 = new short[30][3];
    private static short[][] com2 = new short[135][3];

    private static byte[] ed = new byte[220 * 48];
    private static byte[] cd = new byte[28 * 18];
    private static byte[] ed2 = new byte[84 * 48];
    private static byte[] cd2 = new byte[15 * 18];

    private static int[] seq = new int[15];

    private static boolean ini1 = false;
    private static boolean ini2 = false;

    private static void initr() {
        if (ini1) return;
        Petrus.init();
        int i, j;
        for (i = 0; i < 28; i++)
            for (j = 0; j < 9; j++)
                for (int k = 0; k < 6; k++) {
                    int d = cornmv(i, j, k);
                    if (j < 2) cpm[i << 1 | j][k] = (byte) (d / 9);
                    com[i * 9 + j][k] = (short) (d / 18 * 9 + d % 9);
                }

        for (i = 0; i < 10560; i++) ed[i] = -1;

        ed[12 * 48] = 0;
        int c = 1;
        for (int d = 0; d < 7; d++) {
            //c = 0;
            for (i = 0; i < 1320; i++)
                for (j = 0; j < 8; j++)
                    if (ed[i << 3 | j] == d)
                        for (int l = 0; l < 6; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = Petrus.eom[(x / 6) << 3 | y & 7][l] & 7;
                                x = Petrus.epm[x][l];
                                if (ed[x << 3 | y] < 0) {
                                    ed[x << 3 | y] = (byte) (d + 1);
                                    c++;
                                }
                            }
                        }
            //Log.w("dct", d+1+"\t"+c);
        }
        for (i = 0; i < 504; i++) cd[i] = -1;
        cd[25 * 18] = 0;
        c = 1;
        for (int d = 0; d < 4; d++) {
            //c = 0;
            for (i = 0; i < 56; i++)
                for (j = 0; j < 9; j++)
                    if (cd[i * 9 + j] == d)
                        for (int l = 0; l < 6; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = com[x / 2 * 9 + y % 9][l] % 9;
                                x = cpm[x][l];
                                if (cd[x * 9 + y] < 0) {
                                    cd[x * 9 + y] = (byte) (d + 1);
                                    c++;
                                }
                            }
                        }
            //Log.w("dct", d+1+"\t"+c);
        }
        ini1 = true;
    }

    private static void initr2() {
        if (ini2) return;
        int i, j;
        for (i = 0; i < 84; i++)
            for (j = 0; j < 8; j++)
                for (int k = 0; k < 3; k++) {
                    int d = edgemv2(i, j, j, k);
                    if (j < 6) epm2[i * 6 + j][k] = (short) (d >> 3);
                    eom2[i * 8 + j][k] = (short) ((d / 48) << 3 | d & 7);
                }
        for (i = 0; i < 15; i++)
            for (j = 0; j < 9; j++)
                for (int k = 0; k < 3; k++) {
                    int d = cornmv2(i, j, k);
                    if (j < 2) cpm2[i * 2 + j][k] = (short) (d / 9);
                    com2[i * 9 + j][k] = (short) (d / 18 * 9 + d % 9);
                }
        for (i = 0; i < 4032; i++) ed2[i] = -1;
        ed2[0] = 0;
        int c = 1;
        for (int d = 0; d < 11; d++) {
            //c = 0;
            for (i = 0; i < 504; i++)
                for (j = 0; j < 8; j++)
                    if (ed2[i * 8 + j] == d)
                        for (int l = 0; l < 3; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = eom2[(x / 6) << 3 | y & 7][l] & 7;
                                x = epm2[x][l];
                                if (ed2[x * 8 + y] < 0) {
                                    ed2[x * 8 + y] = (byte) (d + 1);
                                    c++;
                                }
                            }
                        }
            Log.w("dct", d+1+"\t"+c);
        }
        for (i = 0; i < 270; i++) cd2[i] = -1;
        cd2[14 * 18] = 0;
        c = 1;
        for (int d = 0; d < 7; d++) {
            //c = 0;
            for (i = 0; i < 30; i++)
                for (j = 0; j < 9; j++)
                    if (cd2[i * 9 + j] == d)
                        for (int l = 0; l < 3; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = com2[x / 2 * 9 + y % 9][l] % 9;
                                x = cpm2[x][l];
                                if (cd2[x * 9 + y] < 0) {
                                    cd2[x * 9 + y] = (byte) (d + 1);
                                    c++;
                                }
                            }
                        }
            Log.w("dct", d+1+" "+c);
        }
        ini2 = true;
    }

    private static int cornmv(int c, int po, int f) {
        int[] n = new int[8], s = new int[4];
        getCorner(n, c, po, po);
        switch (f) {
            case 0:
                Utils.circle(n, 0, 3, 2, 1); break;
            case 1:
                Utils.circle(n, 4, 5, 6, 7); break;
            case 2:
                Utils.circle(n, 0, 4, 7, 3, new int[] {2, 1, 2, 1});
                //n[0] += 2; n[4]++; n[7] += 2; n[3]++;
                break;
            case 3:
                Utils.circle(n, 1, 2, 6, 5, new int[] {1, 2, 1, 2});
                //n[1]++; n[2] += 2; n[6]++; n[5] += 2;
                break;
            case 4:
                Utils.circle(n, 2, 3, 7, 6, new int[] {1, 2, 1, 2});
                //n[2]++; n[3] += 2; n[7]++; n[6] += 2;
                break;
            case 5:
                Utils.circle(n, 0, 1, 5, 4, new int[] {1, 2, 1, 2});
                //n[0]++; n[1] += 2; n[5]++; n[4] += 2;
                break;
        }
        c = 0;
        for (int q = 2, t = 7; t >= 0; t--)
            if (n[t] >= 0) {
                c += Utils.Cnk[t][q--];
                s[q] = n[t] >> 3;
                s[q + 2] = (n[t] & 7) % 3;
            }
        return (c * 2 + s[0]) * 9 + s[2] * 3 + s[3];
    }

    private static int edgemv2(int c, int p, int o, int f) {
        int[] n = new int[9], s = new int[3];
        Utils.idxToPerm(s, p, 3, false);
        int t, q = 3;
        for (t = 0; t < 9; t++)
            if (c >= Utils.Cnk[8 - t][q]) {
                c -= Utils.Cnk[8 - t][q--];
                n[t] = s[q] << 1 | o & 1;
                o >>= 1;
            } else n[t] = -1;
        switch (f) {
            case 0: //U
                Cross.circle(n, 0, 1, 2, 3, 0); break;
            case 1: //r
                Cross.circle(n, 0, 2, 4, 5, 1);
            case 2: //R
                Cross.circle(n, 1, 6, 7, 8, 0); break;
        }
        c = o = 0; q = 3;
        for (t = 0; t < 9; t++)
            if (n[t] >= 0) {
                c += Utils.Cnk[8 - t][q--];
                s[q] = n[t] >> 1;
                o |= (n[t] & 1) << 2 - q;
            }
        p = Utils.permToIdx(s, 3, false);
        return 6 * c + p << 3 | o;
    }

    private static int cornmv2(int c, int po, int f) {
        int[] n = new int[6], s = new int[4];
        s[0] = po % 2; s[1] = 1 - s[0];
        s[2] = po / 3; s[3] = po % 3;
        int t, q = 2;
        for (t = 5; t >= 0; t--) {
            if (c >= Utils.Cnk[t][q]) {
                c -= Utils.Cnk[t][q--];
                n[t] = s[q] << 3 | s[q + 2];
            } else n[t] = -3;
        }
        switch (f) {
            case 0: //U
                Utils.circle(n, 0, 3, 2, 1);
                break;
            case 1: //r
            case 2: //R
                Utils.circle(n, 1, 2, 4, 5, new int[] {1, 2, 1, 2});
                //n[1]++; n[2] += 2; n[4]++; n[5] += 2;
                break;
        }
        c = 0; q = 2;
        for (t = 5; t >= 0; t--) {
            if (n[t] >= 0) {
                c += Utils.Cnk[t][q--];
                s[q] = n[t] >> 3;
                s[q + 2] = (n[t] & 7) % 3;
            }
        }
        return (c * 2 + s[0]) * 9 + (s[2] * 3 + s[3]);
    }

    private static boolean idaRoux1(int cp, int co, int ep, int eo, int depth, int lm) {
        if (depth == 0) return cp == 50 && co == 225 && ep == 72 && eo == 96;
        if (ed[ep << 3 | eo & 7] > depth || cd[cp * 9 + co % 9] > depth) return false;
        for (int i = 0; i < 6; i++)
            if (i != lm)
                for (int d = cp, w = co, y = ep, s = eo, j = 0; j < 3; j++) {
                    d = cpm[d][i]; w = com[w][i];
                    y = Petrus.epm[y][i]; s = Petrus.eom[s][i];
                    if (idaRoux1(d, w, y, s, depth - 1, i)) {
                        seq[depth] = i * 3 + j;
                        //sb.insert(0, " " + turn[5][i] + suff[j]);
                        return true;
                    }
                }
        return false;
    }

    private static boolean idaRoux2(int cp, int co, int ep, int eo, int depth, int lm) {
        if (depth == 0) return cp == 0 && co == 0 && ep == 28 && eo == 126;
        if (ed2[ep << 3 | eo & 7] > depth || cd2[cp * 9 + co % 9] > depth) return false;
        for (int i = 0; i < 3; i++)
            if (i != lm) {
                int p = cp, q = co, r = ep, s = eo;
                for (int j = 0; j < 3; j++) {
                    p = cpm2[p][i]; q = com2[q][i];
                    r = epm2[r][i]; s = eom2[s][i];
                    if (idaRoux2(p, q, r, s, depth - 1, i)) {
                        seq[depth] = i * 3 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static String[][] moveIdx = {
            {"UDLRFB", "DULRBF", "BFLRUD", "FBLRDU"},
            {"UDFBRL", "DUFBLR", "LRFBUD", "RLFBDU"},
            {"DURLFB", "UDRLBF", "BFRLDU", "FBRLUD"},
            {"UDBFLR", "DUBFRL", "RLBFUD", "LRBFDU"}
    };

    private static String[] sideStr = {"LU", "LD", "FU", "FD", "RU", "RD", "BU", "BD"};
    private static String[] rotateIdx = {"", "y", "z2", "y'"}; //"z", "z'", "", "z2", "y", "y'"
    private static String[] rotateIdx2 = {"", " x2", " x'", " x"};
    private static int[] scp = {50, 7, 49, 12}, sco = {225, 27, 221, 61};
    private static int[] sep = {72, 518, 580, 575}, seo = {96, 688, 768, 760};
    private static int[][] oriIdx = {{1, 0, 2, 3}, {0, 1, 3, 2}, {2, 3, 0, 1}, {3, 2, 1, 0}};

    private static String roux1(String scramble, int side, boolean solveS2) {
        String[] s = scramble.split(" ");
        int[] cp = new int[4], co = new int[4], ep = new int[4], eo = new int[4];
        for (int i = 0; i < 4; i++) {
            cp[i] = scp[oriIdx[side % 2][i]];
            co[i] = sco[oriIdx[side % 2][i]];
            ep[i] = sep[oriIdx[side % 2][i]];
            eo[i] = seo[oriIdx[side % 2][i]];
        }
        for (int d = 0; d < s.length; d++)
            if (s[d].length() > 0) {
                for (int i = 0; i < 4; i++) {
                    int m = moveIdx[side / 2][i].indexOf(s[d].charAt(0));
                    cp[i] = cpm[cp[i]][m]; co[i] = com[co[i]][m];
                    ep[i] = Petrus.epm[ep[i]][m]; eo[i] = Petrus.eom[eo[i]][m];
                    if (s[d].length() > 1) {
                        cp[i] = cpm[cp[i]][m]; co[i] = com[co[i]][m];
                        ep[i] = Petrus.epm[ep[i]][m]; eo[i] = Petrus.eom[eo[i]][m];
                        if (s[d].charAt(1) == '\'') {
                            cp[i] = cpm[cp[i]][m]; co[i] = com[co[i]][m];
                            ep[i] = Petrus.epm[ep[i]][m]; eo[i] = Petrus.eom[eo[i]][m];
                        }
                    }
                }
            }
        for (int d = 0; d < 10; d++) {
            for (int idx = 0; idx < 4; idx++)
                if (idaRoux1(cp[idx], co[idx], ep[idx], eo[idx], d, -1)) {
                    StringBuilder sb = new StringBuilder("\n");
                    sb.append(sideStr[side]).append(": ").append(rotateIdx[side / 2]).append(rotateIdx2[idx]);
                    for (int i = d; i > 0; i--)
                        sb.append(' ').append(turn[seq[i] / 3]).append(suff[seq[i] % 3]);
                    if (solveS2) {
                        int[] s1 = Arrays.copyOf(seq, d + 1);
                        s1[0] = (side / 2) * 4 + idx;
                        sb.append(roux2(scramble, s1));
                    }
                    return sb.toString();
                }
        }
        return "\nerror";
    }

    private static void getEdge(int[] n, int c, int p, int o) {
        int[] s = new int[3];
        Utils.idxToPerm(s, p, 3, false);
        int t, q = 3;
        for (t = 0; t < 12; t++)
            if (c >= Utils.Cnk[11 - t][q]) {
                c -= Utils.Cnk[11 - t][q--];
                n[t] = s[q] << 1 | o & 1;
                o >>= 1;
            } else n[t] = -1;
    }

    private static void getCorner(int[] n, int c, int p, int o) {
        int[] s = new int[4];
        s[0] = p % 2; s[1] = 1 - s[0]; s[2] = o / 3; s[3] = o % 3;
        int q = 2, t;
        for (t = 7; t >= 0; t--)
            if (c >= Utils.Cnk[t][q]) {
                c -= Utils.Cnk[t][q--];
                n[t] = s[q] << 3 | s[q + 2];
            } else n[t] = -3;
    }

    private static String roux2(String scramble, int[] s1) {
        String[] s = scramble.split(" ");
        for (int d = 0; d < s.length; d++) {
            if (s[d].length() > 0) {
                int m = "URFDLB".indexOf(s[d].charAt(0));

            }
        }
        if (s1[0] > 0) {

        }
        for (int i = s1.length - 1; i > 0; i--) {

        }
        return "\nerror";
    }

    public static String solveRoux1(String scramble, int face) {
        initr();
        StringBuilder s = new StringBuilder("\n");
        //boolean solveS2 = ((face >> 8) & 1) != 0;
        //if (solveS2) initr2();
        for (int i = 0; i < 8; i++) {
            if (((face >> i) & 1) != 0)
                s.append(roux1(scramble, i, false));
        }
        return s.toString();
    }
}
