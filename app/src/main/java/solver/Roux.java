package solver;

public class Roux {
    private static byte[][] cpm = new byte[56][6];
    private static short[][] com = new short[252][6];

    private static short[][] epm2 = new short[504][3];
    private static short[][] eom2 = new short[672][3];
    private static byte[][] cpm2 = new byte[30][3];
    private static byte[][] com2 = new byte[135][3];

    private static byte[][] ed = new byte[220][48];
    private static byte[][] cd = new byte[28][18];

    private static String[] seq = new String[20];

    private static boolean inir = false;
    private static void initr() {
        if (inir) return;
        Petrus.init();
        int i, j;
        for (i = 0; i < 28; i++)
            for (j = 0; j < 9; j++)
                for (int k = 0; k < 6; k++) {
                    int d = cornmv(i, j, j, k);
                    if (j < 2) cpm[(i << 1) + j][k] = (byte) (d / 9);
                    com[i * 9 + j][k] = (short)(d / 18 * 9 + d % 9);
                }
        for (i = 0; i < 84; i++)
            for (j = 0; j < 8; j++)
                for (int k = 0; k < 3; k++) {
                    int d = edgemv2(i, j, j, k);
                    if (j < 6) epm2[i * 6 + j][k] = (short) (d >> 3);
                    eom2[i * 8 + j][k] = (short) (((d >> 3) / 6) << 3 | d & 7);
                }
        for (i = 0; i < 220; i++)
            for (j = 0; j < 48; j++)
                ed[i][j] = -1;
        ed[52][0] = 0;
        for (int d = 0; d < 7; d++) {
            //c = 0;
            for (i = 0; i < 220; i++)
                for (j = 0; j < 48; j++)
                    if (ed[i][j] == d)
                        for (int l = 0; l < 6; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = Petrus.epm[x * 6 + (y >> 3)][l] % 6 << 3 | Petrus.eom[x << 3 | y & 7][l] & 7;
                                x = Petrus.epm[x * 6 + (y >> 3)][l] / 6;
                                if (ed[x][y] < 0) {
                                    ed[x][y] = (byte) (d + 1);
                                    //c++;
                                }
                            }
                        }
            //System.out.println(d+" "+c);
        }
        for (i = 0; i < 28; i++)
            for (j = 0; j < 18; j++)
                cd[i][j] = -1;
        cd[25][0] = 0;
        for (int d = 0; d < 4; d++) {
            //c = 0;
            for (i = 0; i < 28; i++)
                for (j = 0; j < 18; j++)
                    if (cd[i][j] == d)
                        for (int l = 0; l < 6; l++) {
                            int x = i, y = j;
                            for (int m = 0; m < 3; m++) {
                                y = (cpm[(x << 1) + y / 9][l] & 1) * 9 + com[x * 9 + y % 9][l] % 9;
                                x = cpm[(x << 1) + y / 9][l] >> 1;
                                if (cd[x][y] < 0) {
                                    cd[x][y] = (byte) (d + 1);
                                    //c++;
                                }
                            }
                        }
            //System.out.println(d+" "+c);
        }
        inir = true;
    }

    private static void cir(int[] d, int[] s, int f, int h, int l, int n) {
        int q = d[f]; d[f] = d[h]; d[h] = d[l]; d[l] = d[n]; d[n] = q;
        int t = s[f]; s[f] = s[h]; s[h] = s[l]; s[l] = s[n]; s[n] = t;
    }

    private static int cornmv(int c, int p, int o, int f) {
        int[] n = new int[8], s = new int[4], u = new int[8];
        int q, t;
        s[0] = p % 2; s[1] = 1-s[0]; s[2] = o/3; s[3] = o % 3;
        q = 2;
        for (t = 7; t >= 0; t--)
            if (c >= Utils.Cnk[t][q]) {
                c -= Utils.Cnk[t][q--];
                n[t] = s[q];
                u[t] = s[q + 2];
            } else n[t] = -1;
        switch (f) {
            case 0:
                cir(n, u, 0, 3, 2, 1); break;
            case 1:
                cir(n, u, 4, 5, 6, 7); break;
            case 2:
                cir(n, u, 0, 4, 7, 3);
                u[0] += 2; u[3]++; u[4]++; u[7] += 2;
                break;
            case 3:
                cir(n, u, 1, 2, 6, 5);
                u[1]++; u[2] += 2; u[5] += 2; u[6]++;
                break;
            case 4:
                cir(n, u, 2,3,7,6);
                u[2]++; u[3] += 2; u[6] += 2; u[7]++;
                break;
            case 5:
                cir(n, u, 0,1,5,4);
                u[0]++; u[1] += 2; u[4] += 2; u[5]++;
                break;
        }
        c = 0; q = 2;
        for (t = 7; t >= 0; t--)
            if (0 <= n[t]) {
                c += Utils.Cnk[t][q--];
                s[q] = n[t];
                s[q + 2] = u[t] % 3;
            }
        return (c * 2 + s[0]) * 9 + s[2] * 3 + s[3];
    }

    private static int edgemv2(int c, int p, int o, int f) {
        int[] n = new int[9], s = new int[3];
        int q, t, v;
        for (q = 1; q <= 3; q++) {
            t = p % q;
            p = p / q;
            for (v = q - 2; v >= t; v--)
                s[v + 1] = s[v];
            s[t] = 3 - q;
        }
        q = 3;
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
        c = 0; q = 3;
        for (t = 0; t < 9; t++)
            if (n[t] >= 0) {
                c += Utils.Cnk[8 - t][q--];
                s[q] = n[t] >> 1;
                o |= (n[t] & 1) << 2 - q;
            }
        p = 0;
        for (q = 0; q < 3; q++) {
            for (v = t = 0; v < 3 && s[v] != q; v++)
                if (s[v] > q) t++;
            p = p * (3 - q) + t;
        }
        return 6 * c + p << 3 | o;
    }

    private static boolean idaRoux1(int cp, int co, int ep, int eo, int depth, int lm) {
        if (depth == 0) return cp == 50 && co == 225 && ep == 312 && eo == 416;
        if (ed[ep / 6][(ep % 6) << 3 | eo & 7] > depth || cd[cp >> 1][(cp & 1) * 9 + co % 9] > depth) return false;
        //if (pd[ep][cp]>depth || od[eo][co]>depth)return false;
        for (int i = 0; i < 6; i++)
            if (i != lm)
                for (int d = cp, w = co, y = ep, s = eo, j = 0; j < 3; j++) {
                    d = cpm[d][i]; w = com[w][i];
                    y = Petrus.epm[y][i]; s = Petrus.eom[s][i];
                    if (idaRoux1(d, w, y, s, depth - 1, i)) {
                        seq[depth] = turn[i] + suff[j];
                        //sb.insert(0, " " + turn[5][i] + suff[j]);
                        return true;
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
    private static String[] turn = { "U", "D", "L", "R", "F", "B" };
    private static String[] suff = {"", "2", "'"};

    private static String[] sideStr = {"LU", "LD", "FU", "FD","RU", "RD", "BU", "BD"};
    private static String[] rotateIdx = {"", "y", "z2", "y'"}; //"z", "z'", "", "z2", "y", "y'"
    private static String[] rotateIdx2 = {"", " x2", " x'", " x"};
    private static int[] scp = {50, 7, 49, 12}, sco = {225, 27, 221, 61};
    private static int[] sep = {312, 887, 860, 825}, seo = {416, 1176, 1144, 1096};
    private static int[][] oriIdx = {{1, 0, 2, 3}, {0, 1, 3, 2}, {2, 3, 0, 1}, {3, 2, 1, 0}};

    private static String roux1(String scramble, int side) {
        String[] s = scramble.split(" ");
        int[] cp = new int[4], co = new int[4], ep = new int[4], eo = new int[4], o = new int[4];
        for (int i = 0; i < 4; i++) {
            cp[i] = scp[oriIdx[side % 2][i]];
            co[i] = sco[oriIdx[side % 2][i]];
            ep[i] = sep[oriIdx[side % 2][i]];
            eo[i] = seo[oriIdx[side % 2][i]];
        }
        for (int d = 0; d < s.length; d++) {
            if (0 != s[d].length()) {
                for (int i = 0; i < 4; i++) {
                    o[i] = moveIdx[side / 2][i].indexOf(s[d].charAt(0));
                    cp[i] = cpm[cp[i]][o[i]]; co[i] = com[co[i]][o[i]];
                    ep[i] = Petrus.epm[ep[i]][o[i]]; eo[i] = Petrus.eom[eo[i]][o[i]];
                }
                if (s[d].length() > 1) {
                    for (int i = 0; i < 4; i++) {
                        cp[i] = cpm[cp[i]][o[i]]; co[i] = com[co[i]][o[i]];
                        ep[i] = Petrus.epm[ep[i]][o[i]]; eo[i] = Petrus.eom[eo[i]][o[i]];
                    }
                    if (s[d].charAt(1) == '\'')
                        for (int i = 0; i < 4; i++) {
                            cp[i] = cpm[cp[i]][o[i]]; co[i] = com[co[i]][o[i]];
                            ep[i] = Petrus.epm[ep[i]][o[i]]; eo[i] = Petrus.eom[eo[i]][o[i]];
                        }
                }
            }
        }
        //sb = new StringBuilder();
        for (int d = 0; d < 13; d++) {
            //System.out.print(d+" ");
            for (int idx = 0; idx < 4; idx++)
                if (idaRoux1(cp[idx], co[idx], ep[idx], eo[idx], d, -1)) {
                    StringBuilder sb = new StringBuilder("\n");
                    sb.append(sideStr[side]).append(": ").append(rotateIdx[side / 2]).append(rotateIdx2[idx]);
                    for (int i = d; i > 0; i--)
                        sb.append(' ').append(seq[i]);
                    return sb.toString();
                }
        }
        return "\nerror";
    }

    public static String solveRoux1(String scramble, int face) {
        initr();
        StringBuilder s = new StringBuilder("\n");
        for (int i = 0; i < 8; i++) {
            if (((face >> i) & 1) != 0)
                s.append(roux1(scramble, i));
        }
        return s.toString();
    }
}
