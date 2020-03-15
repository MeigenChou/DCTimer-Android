package solver;

import android.util.Log;

import java.util.Arrays;

public class PyraminxV {
    private static short[][] epm = new short[30][4], eom = new short[60][4];
    private static short[][] com = new short[27][4];
    private static byte[] prun = new byte[3240];
    private static String[] moveStr = { "LRBU", "ULBR", "RUBL", "LURB" };
    private static String faceStr = "DLRF";
    private static String[] suff = { "", "'" };
    private static int[] seq = new int[7];
    private static boolean ini;

    private static void init() {
        if (ini) return;
        for (int a = 0; a < 15; a++) {
            for (int b = 0; b < 4; b++) {
                for (int f = 0; f < 4; f++) {
                    int c = getmv(a, b, b, f);
                    eom[4 * a + b][f] = (short) ((c / 8) << 2 | (c & 3));
                    //epm[24 * a + b][f] = (short) (c >> 4);
                    if (b < 2)
                        epm[2 * a + b][f] = (short) (c >> 2);
                        //eom[16 * a + b][f] = (short) (((c >> 4) / 24) << 4 | c & 15);
                }
            }
        }
        int[] tws = new int[3];
        for (int p = 0; p < 27; p++)
            for (int m = 0; m < 4; m++) {
                Utils.idxToOri(tws, p, 3, false);
                switch (m) {
                    case 0:	//L
                        tws[1]++; if (tws[1] == 3) tws[1] = 0;
                        break;
                    case 1:	//R
                        tws[2]++; if (tws[2] == 3) tws[2] = 0;
                        break;
                    case 2:	//B
                        tws[0]++; if (tws[0] == 3) tws[0] = 0;
                        break;
                }
                com[p][m] = (short) Utils.oriToIdx(tws, 3, false);
            }
        Arrays.fill(prun, (byte)-1);
        prun[3 * 8] = prun[4 * 8] = prun[0] = 0; //prun[3 * 8] = prun[4 * 8] =
        int c = 3;
        System.out.println("0\t3");
        for (int d=0; d<6; d++) {
            //int p = 0;
            for (int i=0; i<27; i++)
                for (int j=0; j<30; j++)
                    for (int k=0; k<4; k++)
                        if (prun[i * 120 + j * 4 + k] == d)
                            for (int m = 0; m < 4; m++) {
                                int x = i, y = j, z = k;
                                for (int n = 0; n < 2; n++) {
                                    x = com[x][m];
                                    z = eom[y / 2 * 4 + z % 4][m] % 4;
                                    y = epm[y][m];
                                    //r = ctm[r][m]; s = com[s][m]; t = cpm[t][m];
                                    int next = x * 120 + y * 4 + z;
                                    if (prun[next] < 0) {
                                        prun[next] = (byte) (d + 1);
                                        //p++;
                                        c++;
                                    }
                                }
                            }
            //Log.w("dct", d+1+"\t"+c);
        }
        ini = true;
    }

    private static int getmv(int c, int p, int o, int f) {
        int[] n = new int[6], s = new int[2];
        int q, t;
        Utils.idxToPerm(s, p, 2, false);
        idxToComb(n, s, c, o);
        switch (f) {
            case 0:	//L
                Utils.circle(n, 1, 5, 2);
                n[2] ^= 1; n[5] ^= 1;
                break;
            case 1:	//R
                Utils.circle(n, 0, 2, 4);
                n[0] ^= 1; n[2] ^= 1;
                break;
            case 2:	//B
                Utils.circle(n, 3, 4, 5);
                n[3] ^= 1; n[4] ^= 1;
                break;
            case 3:	//U
                Utils.circle(n, 0, 3, 1);
                n[1] ^= 1; n[3] ^= 1;
                break;
        }
        c = o = 0;
        q = 2;
        for (t = 0; t < 6; t++)
            if (n[t] >= 0) {
                c += Utils.Cnk[5 - t][q--];
                s[q] = n[t] >> 1;
                o |= (n[t] & 1) << 1 - q;
            }
        int i = Utils.permToIdx(s, 2, false);
        return 2 * c + i << 2 | o;
    }

    private static void idxToComb(int[] n, int[] s, int c, int o) {
        int q = 2;
        for (int t = 0; t < 6; t++)
            if (c >= Utils.Cnk[5 - t][q]) {
                c -= Utils.Cnk[5 - t][q--];
                n[t] = s[q] << 1 | o & 1;
                o >>= 1;
            } else n[t] = -1;
    }

//    static int combToIdx(int[] n) {
//        int c = 0, q = 2;
//        for (int t = 0; t < 6; t++)
//            if (n[t] > 0) {
//                c += Utils.Cnk[5 - t][q--];
//            }
//        return c;
//    }

    private static boolean search(int p, int f, int t, int solvedp, int d, int lm) {
        if (d == 0) return p == solvedp && f == solvedp * 2 && t == 0;
        //if (t * 120 + p * 4 + f % 4 >= prun.length) System.out.println("err "+t+","+p+","+f);
        if (prun[t * 120 + p * 4 + f % 4] > d) return false;
        for (int i = 0; i < 4; i++)
            if (i != lm) {
                int x = p, y = f, z = t;
                for (int j = 0; j < 2; j++) {
                    x = epm[x][i];
                    y = eom[y][i];
                    z = com[z][i];
                    if (search(x, y, z, solvedp, d - 1, i)) {
                        seq[d] = i * 2 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static int[] solvedEp = {0, 6, 8};
    private static String solve(String scramble, int face) {
        String[] s = scramble.split(" ");
        int t = 0;
        int[] p = {0, 6, 8}, f = {0, 12, 16};
        for (int i = 0; i < s.length; i++)
            if (s[i].length() > 0) {
                int o = moveStr[face].indexOf(s[i].charAt(0));
                if (o < 0) continue;
                t = com[t][o];
                for (int j=0; j<3; j++) {
                    p[j] = epm[p[j]][o];
                    f[j] = eom[f[j]][o];
                }
                if (s[i].length() > 1) {
                    t = com[t][o];
                    for (int j=0; j<3; j++) {
                        p[j] = epm[p[j]][o];
                        f[j] = eom[f[j]][o];
                    }
                }
            }
        for (int d = 0; d < 7; d++) {
            for (int idx = 0; idx < 3; idx++)
                if (search(p[idx], f[idx], t, solvedEp[idx], d, -1)) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = d; i > 0; i--)
                        sb.append(' ').append(moveStr[face].charAt(seq[i] / 2)).append(suff[seq[i] % 2]);
                    return sb.toString();
                }
        }
        return " error";
    }

    public static String solveV(String scramble, int face) {
        init();
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 4; i++) {
            if (((face >> i) & 1) != 0)
                sb.append('\n').append(faceStr.charAt(i)).append(": ").append(solve(scramble, i));
        }
        return sb.toString();
    }
}
