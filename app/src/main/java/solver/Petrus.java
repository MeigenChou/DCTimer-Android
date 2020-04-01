package solver;

import android.util.Log;

import static solver.Utils.suff;

public class Petrus {
    static short[][] epm = new short[1320][6];
    static short[][] eom = new short[1760][6];
    private static byte[][] com = new byte[24][6];
    private static short[][] epm2 = new short[132][6];
    private static short[][] eom2 = new short[264][6];

    private static byte[] epd = new byte[1320];
    private static byte[] eod = new byte[1760];
    private static byte[] ed2 = new byte[528];

    private static int[] moves2 = {0, 3, 4};

    private static int edgemv(int c, int po, int k, int f) {
        int[] n = new int[12], s = new int[3];
        Utils.idxToPerm(s, po, k, false);
        int t, q = k;
        for (t = 0; t < 12; t++)
            if (c >= Utils.Cnk[11 - t][q]) {
                c -= Utils.Cnk[11 - t][q--];
                n[t] = s[q] << 1 | po & 1;
                po >>= 1;
            } else n[t] = -1;
        Cross.edgemv(n, f);
        c = po = 0; q = k;
        for (t = 0; t < 12; t++)
            if (n[t] >= 0) {
                c += Utils.Cnk[11 - t][q--];
                s[q] = n[t] >> 1;
                po |= (n[t] & 1) << (k - 1 - q);
            }
        int p = Utils.permToIdx(s, k, false);
        return Utils.fact[k] * c + p << 3 | po;
    }

    private static boolean ini = false;
    public static void init() {
        if (ini) return;
        int i, j;
        for (i = 0; i < 220; i++)
            for (j = 0; j < 8; j++)
                for (int k = 0; k < 6; k++) {
                    int d = edgemv(i, j, 3, k);
                    if (j < 6) epm[i * 6 + j][k] = (short) (d >> 3);
                    eom[i * 8 + j][k] = (short) ((d / 48) << 3 | d & 7);
                }
        ini = true;
    }

    private static boolean inip1 = false;
    private static void initp1() {
        if (inip1) return;
        init();
        int i, j;
        byte[][] p = {
                { 1, 0, 3, 0, 0, 4 }, { 2, 1, 1, 5, 1, 0 }, { 3, 2, 2, 1, 6, 2 }, { 0, 3, 7, 3, 2, 3 },
                { 4, 7, 0, 4, 4, 5 }, { 5, 4, 5, 6, 5, 1 }, { 6, 5, 6, 2, 7, 6 }, { 7, 6, 4, 7, 3, 7 }
        };
        byte[][] o = {
                { 0, 0, 1, 0, 0, 2 }, { 0, 0, 0, 2, 0, 1 }, { 0, 0, 0, 1, 2, 0 }, { 0, 0, 2, 0, 1, 0 },
                { 0, 0, 2, 0, 0, 1 }, { 0, 0, 0, 1, 0, 2 }, { 0, 0, 0, 2, 1, 0 }, { 0, 0, 1, 0, 2, 0 }
        };
        for (i = 0; i < 8; i++)
            for (j = 0; j < 3; j++)
                for (int k = 0; k < 6; k++)
                    com[i * 3 + j][k] = (byte) (p[i][k] * 3 + (o[i][k] + j) % 3);
        for (i = 0; i < 1320; i++) epd[i] = -1;
        epd[17 * 6] = 0;
        Utils.createPrun(epd, 5, epm, 3);
        for (i = 0; i < 1760; i++) eod[i] = -1;
        eod[17 * 8] = 0;
        Utils.createPrun(eod, 5, eom, 3);
        inip1 = true;
    }

    private static boolean inip2 = false;
    private static void initp2() {
        if (inip2) return;
        int i, j;
        for (i = 0; i < 66; i++)
            for (j = 0; j < 4; j++)
                for (int k = 0; k < 6; k++) {
                    int d = edgemv(i, j, 2, k);
                    if (j < 2) epm2[i * 2 + j][k] = (short) (d >> 3);
                    eom2[i * 4 + j][k] = (short) ((d / 16) << 2 | d & 3);
                }
        for (i = 0; i < 528; i++) ed2[i] = -1;
        ed2[44 * 8] = ed2[21 * 8] = ed2[17 * 8] = 0;
        int c = 3;
        for (int d = 0; d < 6; d++) {
            //c = 0;
            for (i = 0; i < 132; i++)
                for (j = 0; j < 4; j++)
                    if (ed2[i * 4 + j] == d)
                        for (int l = 0; l < 3; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = eom2[(x / 2) << 2 | y & 3][moves2[l]] & 3;
                                x = epm2[x][moves2[l]];
                                if (ed2[x * 4 + y] < 0) {
                                    ed2[x * 4 + y] = (byte) (d + 1);
                                    c++;
                                }
                            }
                        }
            Log.w("dct", d+1+"\t"+c);
        }
        inip2 = true;
    }


    private static int[] seq = new int[10];
    private static boolean idaPetrus1(int co, int ep, int eo, int depth, int lm, int block) {
        if (depth == 0) return co == 12 && ep == 102 && eo == 136;
        if (epd[ep] > depth || eod[eo] > depth) return false;
        for (int i = 0; i < 6; i++)
            if (i != lm) {
                int w = co, y = ep, s = eo;
                for (int j = 0; j < 3; j++) {
                    w = com[w][i];
                    y = epm[y][i];
                    s = eom[s][i];
                    if (idaPetrus1(w, y, s, depth - 1, i, block)) {
                        seq[depth] = i * 3 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static int[] solvedEp = {88, 42, 34}, solvedEo = {176, 84, 68}, solvedCo = {0, 15, 21};
    private static boolean idaPetrus2(int co, int ep, int eo, int depth, int lm, int idx) {
        if (depth == 0) return ep == solvedEp[idx] && eo == solvedEo[idx] && co == solvedCo[idx];
        if (ed2[ep << 2 | eo & 3] > depth) return false;
        for (int i = 0; i < 3; i++)
            if (i != lm) {
                int x = co, y = ep, s = eo;
                for (int j = 0; j < 3; j++) {
                    x = com[x][moves2[i]];
                    y = epm2[y][moves2[i]];
                    s = eom2[s][moves2[i]];
                    if (idaPetrus2(x, y, s, depth - 1, i, idx)) {
                        seq[depth] = moves2[i] * 3 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static String[] moveIdx = {"DULRBF", "FBLRDU", "DUFBLR", "DURLFB",
            "UDFBRL", "UDLRFB", "UDRLBF", "UDBFLR"};
    private static String[] blks = {"ULF:", "ULB:", "URF:", "URB:", "DLF:", "DLB:", "DRF:", "DRB:"};
    private static String petrus1(String scramble, int block, boolean solveS2) {
        String[] s = scramble.split(" ");
        int co = 12, ep = 102, eo = 136;
        for (int d = 0; d < s.length; d++)
            if (0 != s[d].length()) {
                int o = moveIdx[block].indexOf(s[d].charAt(0));
                co = com[co][o]; ep = epm[ep][o]; eo = eom[eo][o];
                if (s[d].length() > 1) {
                    co = com[co][o]; eo = eom[eo][o]; ep = epm[ep][o];
                    if (s[d].charAt(1) == '\'') {
                        co = com[co][o]; eo = eom[eo][o]; ep = epm[ep][o];
                    }
                }
            }
        for (int d = 0; d < 9; d++) {
            //Log.w("dct", "d "+d);
            if (idaPetrus1(co, ep, eo, d, -1, block)) {
                StringBuilder sb = new StringBuilder("\n");
                sb.append(blks[block]);
                for (int i = d; i > 0; i--)
                    sb.append(' ').append(moveIdx[block].charAt(seq[i] / 3)).append(suff[seq[i] % 3]);
                if (solveS2) sb.append(petrus2(s, block, d));
                return sb.toString();
            }
        }
        return "\nerror";
    }

    static String petrus2(String[] s, int block, int d) {
        int[] co2 = new int[3], ep2 = new int[3], eo2 = new int[3];
        for (int i = 0; i < 3; i++) {
            co2[i] = solvedCo[i];
            ep2[i] = solvedEp[i];
            eo2[i] = solvedEo[i];
        }
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() > 0) {
                int o = moveIdx[block].indexOf(s[i].charAt(0));
                for (int j = 0; j < 3; j++) {
                    co2[j] = com[co2[j]][o];
                    ep2[j] = epm2[ep2[j]][o];
                    eo2[j] = eom2[eo2[j]][o];
                    if (s[i].length() > 1) {
                        co2[j] = com[co2[j]][o];
                        ep2[j] = epm2[ep2[j]][o];
                        eo2[j] = eom2[eo2[j]][o];
                        if (s[i].charAt(1) == '\'') {
                            co2[j] = com[co2[j]][o];
                            ep2[j] = epm2[ep2[j]][o];
                            eo2[j] = eom2[eo2[j]][o];
                        }
                    }
                }
            }
        }
        for (int i = d; i > 0; i--) {
            int m = seq[i] / 3, n = seq[i] % 3;
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k <= n; k++) {
                    co2[j] = com[co2[j]][m];
                    ep2[j] = epm2[ep2[j]][m];
                    eo2[j] = eom2[eo2[j]][m];
                }
            }
        }
        for (int l = 0; l < 10; l++) {
            for (int idx = 0; idx < 3; idx++)
                if (idaPetrus2(co2[idx], ep2[idx], eo2[idx], l, -1, idx)) {
                    StringBuilder sb = new StringBuilder(" /");
                    for (int i = l; i > 0; i--)
                        sb.append(' ').append(moveIdx[block].charAt(seq[i] / 3)).append(suff[seq[i] % 3]);
                    return sb.toString();
                }
        }
        return " / error";
    }

    public static String solvePetrus(String scramble, int block) {
        initp1();
        boolean solveS2 = ((block >> 8) & 1) != 0;
        if (solveS2) initp2();
        StringBuilder s = new StringBuilder("\n");
        for (int i = 0; i < 8; i++) {
            if (((block >> i) & 1) != 0) s.append(petrus1(scramble, i, solveS2));
        }
        return s.toString();
    }
}
