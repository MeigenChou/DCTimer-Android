package solver;

import android.util.Log;

import java.util.Arrays;

import static solver.Utils.suff;

public class Cube2Layer {
    private static short[][] cpm3 = new short[210][3];
    private static short[][] com3 = new short[945][3];
    private static short[][] cpm4 = new short[840][3];
    private static short[][] com4 = new short[2835][3];
    private static byte[] prun3 = new byte[5670];
    private static byte[] prun4 = new byte[68040];
    private static int[] seq = new int[8];
    private static int[][] solved = {
            {38948, 39758, 40001, 40811, 52702, 53107, 53836, 54241, 66096, 66906, 67149, 67959},
            {39094, 39447, 40176, 40633, 52488, 53366, 53609, 54351, 66326, 66715, 67444, 67865},
            {38880, 39742, 39985, 40743, 52718, 53055, 53784, 54257, 66148, 66974, 67217, 68011}
    };

    private static void init() {
        for (int i = 0; i < 35; i++) {
            for (int j = 0; j < 27; j++)
                for (int k = 0; k < 3; k++) {
                    int d = getmv(i, 3, j, k);
                    int p = d >> 7;
                    com3[i * 27 + j][k] = (short) (p / 6 * 27 + (d & 127));
                    if (j < 6)
                        cpm3[i * 6 + j][k] = (short) p;
                }
            for (int j = 0; j < 81; j++)
                for (int k = 0; k < 3; k++) {
                    int d = getmv(i, 4, j, k);
                    int p = d >> 7;
                    com4[i * 81 + j][k] = (short) (p / 24 * 81 + (d & 127));
                    if (j < 24)
                        cpm4[i * 24 + j][k] = (short) p;
                }
        }
        for (int i = 0; i < 5670; i++) prun3[i] = -1;
        prun3[0] = prun3[7 * 162] = prun3[14 * 162] = 0;
        int c = 3;
        for (int d = 0; d < 6; d++) {
            //c = 0;
            for (int i = 0; i < 210; i++)
                for (int j = 0; j < 27; j++)
                    if (prun3[i * 27 + j] == d)
                        for (int l = 0; l < 3; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = com3[x / 6 * 27 + y % 27][l] % 27;
                                x = cpm3[x][l];
                                if (prun3[x * 27 + y] < 0) {
                                    prun3[x * 27 + y] = (byte) (d + 1);
                                    c++;
                                }
                            }
                        }
            Log.w("dct", d+1+"\t"+c);
        }

        for (int i = 0; i < 68040; i++) prun4[i] = -1;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 12; j++)
                prun4[solved[i][j]] = 0;
        c = 36;
        for (int d = 0; d < 6; d++) {
            //c = 0;
            for (int i = 0; i < 840; i++)
                for (int j = 0; j < 81; j++)
                    if (prun4[i * 81 + j] == d)
                        for (int l = 0; l < 3; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = com4[x / 24 * 81 + y % 81][l] % 81;
                                x = cpm4[x][l];
                                if (prun4[x * 81 + y] < 0) {
                                    prun4[x * 81 + y] = (byte) (d + 1);
                                    c++;
                                }
                            }
                        }
            Log.w("dct", d+1+"\t"+c);
        }
    }

    static {
        init();
    }

    private static int getmv(int c, int k, int po, int f) {
        int[] n = new int[7], s = new int[4], o = new int[4];
        Utils.idxToPerm(s, po, k, false);
        Utils.idxToOri(o, po, k, false);
        int q = k, t;
        for (t = 0; t < 7; t++)
            if (c >= Utils.Cnk[6 - t][q]) {
                c -= Utils.Cnk[6 - t][q--];
                n[t] = s[q] << 3 | o[k - 1 - q];
            }
            else n[t] = -3;
        switch (f) {
            case 0: //U
                Utils.circle(n, 0, 1, 3, 2); break;
            case 1: //R
                Utils.circle(n, 0, 4, 5, 1, new int[] {2, 1, 2, 1});
                break;
            case 2: //F
                Utils.circle(n, 0, 2, 6, 4, new int[] {1, 2, 1, 2});
                break;
        }
        c = po = 0; q = k;
        for (t = 0; t < 7; t++)
            if (n[t] >= 0) {
                c += Utils.Cnk[6 - t][q--];
                s[q] = n[t] >> 3;
                po += (n[t] & 7) % 3;
                po *= 3;
            }
        int i = Utils.permToIdx(s, k, false);
        return Utils.fact[k] * c + i << 7 | po / 3;
    }

    private static boolean search3(int scp, int sco, int cp, int co, int d, int lm) {
        if (d == 0) return cp == scp && co == sco;
        if (prun3[cp * 27 + co % 27] > d) return false;
        for (int i = 0; i < 3; i++)
            if (i != lm) {
                int x = cp, y = co;
                for (int j = 0; j < 3; j++) {
                    x = cpm3[x][i]; y = com3[y][i];
                    if (search3(scp, sco, x, y, d - 1, i)) {
                        seq[d] = i * 3 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static boolean search4(int face, int cp, int co, int d, int lm) {
        if (d == 0) return check(face, cp * 81 + co % 81);
        if (prun4[cp * 81 + co % 81] > d) return false;
        for (int i = 0; i < 3; i++)
            if (i != lm) {
                int x = cp, y = co;
                for (int j = 0; j < 3; j++) {
                    x = cpm4[x][i]; y = com4[y][i];
                    if (search4(face, x, y, d - 1, i)) {
                        seq[d] = i * 3 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static boolean check(int face, int c) {
        if (prun4[c] != 0) return false;
        int idx = face == 1 ? 0 : (face == 3 ? 1 : 2);
        return Arrays.binarySearch(solved[idx], c) >= 0;
    }

    private static int[] solvedCp = {0, 816, 42, 648, 480, 84}, solvedCo = {0, 2754, 189, 2187, 1620, 378};
    private static String[] color = {"D: ", "U: ", "L: ", "R: ", "F: ", "B: "};
    private static String solve(String scramble, int face) {
        String[] s = scramble.split(" ");
        int cp = solvedCp[face], co = solvedCo[face];
        for (int d = 0; d < s.length; d++)
            if (s[d].length() != 0) {
                int m = "URF".indexOf(s[d].charAt(0));
                int n = 1;
                if (s[d].length() > 1) {
                    n++;
                    if (s[d].charAt(1) == '\'') n++;
                }
                for (int i = 0; i < n; i++) {
                    if (face == 0 || face == 2 || face == 5) {
                        cp = cpm3[cp][m]; co = com3[co][m];
                    } else {
                        cp = cpm4[cp][m]; co = com4[co][m];
                    }
                }
            }
        if (face == 0 || face == 2 || face == 5) {
            for (int d = 0; d < 8; d++)
                if (search3(solvedCp[face], solvedCo[face], cp, co, d, -1)) {
                    return move2str(face, d);
                }
        } else for (int d = 0; d < 8; d++)
            if (search4(face, cp, co, d, -1)) {
                return move2str(face, d);
            }
        return "\nerror";
    }

    private static String move2str(int face, int d) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(color[face]);
        for (int i = d; i > 0; i--)
            sb.append("URF".charAt(seq[i] / 3)).append(suff[seq[i] % 3]).append(" ");
        return sb.toString();
    }

    public static String solveFirstLayer(String scramble, int face) {
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 6; i++) {
            if (((face >> i) & 1) != 0)
                sb.append(solve(scramble, i));
        }
        return sb.toString();
    }
}
