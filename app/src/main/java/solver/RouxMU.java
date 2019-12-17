package solver;

import java.util.Random;

public class RouxMU {
    private static short[][] epm = new short[720][2];
    private static byte[][] eom = new byte[32][2];
    private static byte[][] ctm = {{1, 0}, {2, 1}, {3, 2}, {0, 3}};
    private static byte[][] cpm = {{0, 1}, {1, 2}, {2, 3}, {3, 0}};
    private static byte[][] ed = new byte[720][32];
    private static byte[][] eod = new byte[32][16];
    private static String[] turn = {"M", "U"};
    private static String[] suff = {"'", "2", ""};
    private static int[] seq = new int[21];
    //private static StringBuilder sb;

    static {
        int i, j;
        int[] temp = new int[6];
        for (i = 0; i < 720; i++) {
            for (j = 0; j < 32; j++) ed[i][j] = -1;
            for (j = 0; j < 2; j++) {
                Utils.idxToPerm(temp, i, 6, false);
                switch (j) {
                    case 0: Utils.circle(temp, 0, 4, 5, 2); break;
                    case 1: Utils.circle(temp, 0, 3, 2, 1); break;
                }
                epm[i][j] = (short) Utils.permToIdx(temp, 6, false);
            }
        }
        for (i = 0; i < 32; i++) {
            for (j = 0; j < 2; j++) {
                Utils.idxToFlip(temp, i, 6, true);
                switch (j) {
                    case 0: Utils.circle(temp, 0, 4, 5, 2);
                        temp[0] = 1 - temp[0];
                        temp[2] = 1 - temp[2];
                        temp[4] = 1 - temp[4];
                        temp[5] = 1 - temp[5];
                        break;
                    case 1: Utils.circle(temp, 0, 3, 2, 1); break;
                }
                eom[i][j] = (byte) Utils.flipToIdx(temp, 6, true);
            }
        }
        for (i = 0; i < 32; i++) {
            for (j = 0; j < 16; j++)
                eod[i][j] = -1;
        }
        eod[0][0] = 0;
        ed[0][0] = 0;
        int d;
        for (d = 0; d < 14; d++) {
            //c=0;
            for (i = 0; i < 720; i++)
                for (j = 0; j<32; j++)
                    if (ed[i][j] == d)
                        for (int k = 0; k < 2; k++) {
                            int y = i, s = j;
                            for (int m = 0; m < 3; m++) {
                                y = epm[y][k]; s = eom[s][k];
                                if (ed[y][s] < 0) {
                                    ed[y][s] = (byte) (d + 1);
                                    //c++;
                                }
                            }
                        }
            //System.out.println(d+" "+c);
        }
        for (d = 0; d < 12; d++) {
            //c=0;
            for (i = 0; i < 32; i++)
                for (j = 0; j < 16; j++) {
                    if (eod[i][j] == d)
                        for (int k = 0; k < 2; k++) {
                            int y = i, t = j;
                            for (int m = 0; m < 3; m++) {
                                y = eom[y][k];
                                t = ctm[t / 4][k] * 4 + cpm[t % 4][k];
                                if (eod[y][t] < 0) {
                                    eod[y][t] = (byte) (d + 1);
                                    //c++;
                                }
                            }
                        }
                }
            //System.out.println(d+" "+c);
        }
    }

    private static boolean search(int ep, int eo, int ct, int cp, int d, int lf) {
        if (d == 0) return cp == 0 && ep == 0 && ct == 0 && eo == 0;
        if (ed[ep][eo] > d || eod[eo][ct * 4 + cp] > d) return false;
        for (int i = 0; i < 2; i++) {
            if (i != lf) {
                int y = cp, s = ep, u = ct, t = eo;
                for (int k = 0; k < 3; k++) {
                    y = cpm[y][i]; s = epm[s][i];
                    u = ctm[u][i]; t = eom[t][i];
                    if (search(s, t, u, y, d - 1, i)) {
                        seq[d] = i * 3 + k;
                        //sb.insert(0, turn[i] + suff[k] + " ");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean ctcpSign(int ct, int cp) {
        boolean a = ct % 2 == 0;
        boolean b = cp % 2 == 0;
        return a == b;
    }

    public static String scramble() {
        int ep, ct, cp;
        int[] p = new int[6];
        Random r = new Random();
        do {
            ep = r.nextInt(720);
            ct = r.nextInt(4);
            cp = r.nextInt(4);
            Utils.idxToPerm(p, ep, 6, false);
        } while (Utils.permutationSign(p) != ctcpSign(ct,cp));
        int eo = r.nextInt(32);
        for (int d = 0; ; d++) {
            if (search(ep, eo, ct, cp, d, -1)) {
                if (d < 2) return scramble();
                if (d < 4) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < d; i++)
                    sb.append(turn[seq[i] / 3]).append(suff[seq[i] % 3]).append(" ");
                return sb.toString();
            }
        }
    }
}
