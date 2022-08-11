package solver;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import static solver.Utils.Cnk;
import static solver.Utils.suff;
import static solver.Utils.getPruning;
import static solver.Utils.setPruning;

public class Cross {
    private static short[][] epm = new short[11880][6], eom = new short[7920][6];
    private static byte[] epd = new byte[11880], eod = new byte[7920];
    private static byte[] eofd = new byte[7920];
    private static int[] ed = new int[23760];
    private static byte[][] fcm = new byte[24][6], fem = new byte[24][6];
    private static byte[][] fecd = new byte[4][576];
    private static int[] seq = new int[20];
    private static ArrayList<String> solutions;
    public static boolean ini, inif;
    private static String[] color = {"D", "U", "L", "R", "F", "B"};
    private static String[][] moveStr = {
            { "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFLRUD", "FBLRDU" },
            { "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFRLDU", "FBRLUD" },
            { "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFUDRL", "FBUDLR" },
            { "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFDULR", "FBDURL" },
            { "UDLRFB", "DULRBF", "RLBFUD", "LRFBUD", "BFLRUD", "FBRLUD" },
            { "UDLRFB", "DULRBF", "RLFBDU", "LRBFDU", "BFRLDU", "FBLRDU" }
    };
    private static String[][] rotateStr = {
            { "", "z2", "z'", "z", "x'", "x" }, { "z2", "", "z", "z'", "x", "x'" },
            { "z", "z'", "", "z2", "y", "y'" }, { "z'", "z", "z2", "", "y'", "y" },
            { "x", "x'", "y'", "y", "", "y2" }, { "x'", "x", "y", "y'", "y2", "" }
    };
    private static String[] sideStr = {"D(FB)", "D(LR)", "U(FB)", "U(LR)",
            "L(FB)", "L(UD)", "R(FB)", "R(UD)", "F(UD)", "F(LR)", "B(UD)", "B(LR)"};
    //private static String[] turn = { "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFLRUD", "FBLRDU" };

    public static void circle(int[] ary, int a, int b, int c, int d, int ori) {
        int t = ary[a];
        ary[a] = ary[d] ^ ori;
        ary[d] = ary[c] ^ ori;
        ary[c] = ary[b] ^ ori;
        ary[b] = t ^ ori;
    }

    private static void idxToComb(int[] arr, int[] s, int c, int o) {
        int q = 4;
        for (int i = 0; i < 12; i++)
            if (c >= Cnk[11 - i][q]) {
                c -= Cnk[11 - i][q--];
                arr[i] = s[q] << 1 | o & 1;
                o >>= 1;
            } else arr[i] = -1;
    }

    private static void idxToComb(int[] arr, int[] s, int c, int o, int[] map) {
        int q = 4;
        for (int t = 0; t < 12; t++)
            if (c >= Cnk[11 - t][q]) {
                c -= Cnk[11 - t][q--];
                arr[t] = map[s[q]] << 1 | o & 1;
                o >>= 1;
            } else arr[t] = -1;
    }

    private static int getmv(int c, int po, int f) {
        int[] arr = new int[12], ps = new int[4];
        Utils.idxToPerm(ps, po, 4, false);
        idxToComb(arr, ps, c, po);
        edgemv(arr, f);
        c = po = 0;
        int q = 4;
        for (int t = 0; t < 12; t++)
            if (arr[t] >= 0) {
                c += Cnk[11 - t][q--];
                ps[q] = arr[t] >> 1;
                po |= (arr[t] & 1) << 3 - q;
            }
        int i = Utils.permToIdx(ps, 4, false);//permToIdx(pm);
        return 24 * c + i << 4 | po;
    }

    static void edgemv(int[] arr, int m) {
        switch (m) {
            case 0: circle(arr, 0,  1, 2,  3, 0); break;
            case 1: circle(arr, 4,  7, 6,  5, 0); break;
            case 2: circle(arr, 2,  9, 6, 10, 0); break;
            case 3: circle(arr, 0, 11, 4,  8, 0); break;
            case 4: circle(arr, 1,  8, 5,  9, 1); break;
            case 5: circle(arr, 3, 10, 7, 11, 1); break;
        }
    }

    private static void init() {
        if (ini)
            return;
        int a, b, c, d, e, f;
        for (a = 0; a < 495; a++) {
            for (b = 0; b < 24; b++) {
                for (c = 0; c < 6; c++) {
                    d = getmv(a, b, c);
                    epm[24 * a + b][c] = (short) (d >> 4);
                    if (b < 16)
                        eom[16 * a + b][c] = (short) ((d / 384) << 4 | d & 15);
                }
            }
        }
        for (a = 0; a < 11880; a++)
            epd[a] = -1;
        epd[69 * 24] = 0;
        Utils.createPrun(epd, 6, epm, 3);
        for (a = 0; a < 7920; a++)
            eod[a] = eofd[a] = -1;
        eod[69 * 16] = 0;
        Utils.createPrun(eod, 7, eom, 3);
        for (a = 0; a < 495; a++)
            eofd[a << 4] = 0;
        Utils.createPrun(eofd, 4, eom, 3);

        //xcross
        byte[][] p = {
                {1, 0, 3, 0, 0, 4}, {2, 1, 1, 5, 1, 0}, {3, 2, 2, 1, 6, 2}, {0, 3, 7, 3, 2, 3},
                {4, 7, 0, 4, 4, 5}, {5, 4, 5, 6, 5, 1}, {6, 5, 6, 2, 7, 6}, {7, 6, 4, 7, 3, 7}
        };
        byte[][] o = {
                {0, 0, 1, 0, 0, 2}, {0, 0, 0, 2, 0, 1}, {0, 0, 0, 1, 2, 0}, {0, 0, 2, 0, 1, 0},
                {0, 0, 2, 0, 0, 1}, {0, 0, 0, 1, 0, 2}, {0, 0, 0, 2, 1, 0}, {0, 0, 1, 0, 2, 0}
        };
        for (a = 0; a < 8; a++)
            for (b = 0; b < 3; b++)
                for (c = 0; c < 6; c++)
                    fcm[a * 3 + b][c] = (byte) (p[a][c] * 3 + (o[a][c] + b) % 3);
        p = new byte[][] {
                {0, 0, 7, 0, 0, 8}, {1, 1, 1, 9, 1, 4}, {2, 2, 2, 5, 10, 2}, {3, 3, 11, 3, 6, 3},
                {5, 4, 4, 4, 4, 0}, {6, 5, 5, 1, 5, 5}, {7, 6, 6, 6, 2, 6}, {4, 7, 3, 7, 7, 7},
                {8, 11, 8, 8, 8, 1}, {9, 8, 9, 2, 9, 9}, {10, 9, 10, 10, 3, 10}, {11, 10, 0, 11, 11, 11}
        };
        o = new byte[][] {
                {0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 0, 0}
        };
        for (a = 0; a < 12; a++)
            for (b = 0; b < 2; b++)
                for (c = 0; c < 6; c++)
                    fem[a * 2 + b][c] = (byte) (p[a][c] * 2 + (o[a][c] ^ b));
        for (f = 0; f < 4; f++) {
            for (a = 0; a < 576; a++) fecd[f][a] = -1;
            fecd[f][f * 51 + 12] = 0;
            for (d = 0; d < 6; d++) {
                int cc = 0;
                for (a = 0; a < 576; a++)
                    if (fecd[f][a] == d)
                        for (b = 0; b < 6; b++)
                            for (e = a, c = 0; c < 3; c++) {
                                e = 24 * fem[e / 24][b] + fcm[e % 24][b];
                                if (fecd[f][e] == -1) {
                                    fecd[f][e] = (byte) (d + 1);
                                    cc++;
                                }
                            }
                //Log.w("dct", d+1+"\t"+cc);
            }
        }
        ini = true;
    }

    private static boolean idacross(int ep, int eo, int d, int lm) {
        if (d == 0) return ep == 1656 && eo == 1104;
        if (epd[ep] > d || eod[eo] > d) return false;
        for (int i = 0; i < 6; i++)
            if (i != lm) {
                int epx = ep, eox = eo;
                for (int j = 0; j < 3; j++) {
                    epx = epm[epx][i]; eox = eom[eox][i];
                    if (idacross(epx, eox, d - 1, i)) {
                        seq[d] = i * 3 + j;
                        //sb.insert(0, " " + turn[face][i] + suff[j]);
                        return true;
                    }
                }
            }
        return false;
    }

    private static void idacross(int ep, int eo, int d, int lm, int face, int[] path) {
        if (d == 0) {
            if (ep == 1656 && eo == 1104) {
                StringBuilder sb = new StringBuilder(rotateStr[0][face]);
                int qtm = 0;
                for (int i = path.length - 1; i > 0; i--) {
                    sb.append(' ').append(moveStr[0][0].charAt(path[i] / 3)).append(suff[path[i] % 3]);
                    if (path[i] % 3 == 1) qtm += 2;
                    else qtm++;
                }
                sb.append("\t").append(path.length - 1).append("f, ").append(qtm).append("q");
                solutions.add(sb.toString());
            }
            return;
        }
        if (epd[ep] > d || eod[eo] > d) return;
        for (int i = 0; i < 6; i++)
            if (i != lm && !(i/2 == lm/2 && i < lm)) {
                int epx = ep, eox = eo;
                for (int j = 0; j < 3; j++) {
                    epx = epm[epx][i]; eox = eom[eox][i];
                    path[d] = i * 3 + j;
                    idacross(epx, eox, d - 1, i, face, path);
                }
            }
    }

    private static boolean idaeofc(int ep, int eo, int eof, int d, int lm) {
        if (d == 0) return ep == 1656 && eo == 1104 && (eof & 15) == 0;
        if (epd[ep] > d || eod[eo] > d || eofd[eof] > d) return false;
        for (int i = 0; i < 6; i++)
            if (i != lm) {
                int epx = ep, eox = eo, eofx = eof;
                for (int j = 0; j < 3; j++) {
                    epx = epm[epx][i]; eox = eom[eox][i]; eofx = eom[eofx][i];
                    if (idaeofc(epx, eox, eofx, d-1, i)) {
                        seq[d] = i * 3 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static boolean idaxcross(int ep, int eo, int co, int feo, int slot, int d, int lm) {
        if (d == 0) return ep == 1656 && eo == 1104 && co == (slot + 4) * 3 && feo == slot * 2;
        if (epd[ep] > d || eod[eo] > d || fecd[slot][feo * 24 + co] > d) return false;
        for (int i = 0; i < 6; i++)
            if (i != lm) {
                int cox = co, epx = ep, eox = eo, fx = feo;
                for (int j = 0; j < 3; j++) {
                    cox = fcm[cox][i]; fx = fem[fx][i];
                    epx = epm[epx][i]; eox = eom[eox][i];
                    if (idaxcross(epx, eox, cox, fx, slot, d - 1, i)) {
                        seq[d] = i * 3 + j;
                        //sb.insert(0, " " + turn[0][i] + suff[j]);
                        return true;
                    }
                }
            }
        return false;
    }

    private static void idaxcross(int ep, int eo, int co, int feo, int slot, int d, int lm, int face, int[] path) {
        if (d == 0) {
            if (ep == 1656 && eo == 1104 && co == (slot + 4) * 3 && feo == slot * 2) {
                StringBuilder sb = new StringBuilder(rotateStr[0][face]);
                int qtm = 0;
                for (int i = path.length - 1; i > 0; i--) {
                    sb.append(' ').append(moveStr[0][0].charAt(path[i] / 3)).append(suff[path[i] % 3]);
                    if (path[i] % 3 == 1) qtm += 2;
                    else qtm++;
                }
                sb.append("\t").append(path.length - 1).append("f, ").append(qtm).append("q");
                solutions.add(sb.toString());
            }
            return;
        }
        if (epd[ep] > d || eod[eo] > d || fecd[slot][feo * 24 + co] > d) return;
        for (int i = 0; i < 6; i++)
            if (i != lm && !(i/2 == lm/2 && i < lm)) {
                int cox = co, epx = ep, eox = eo, fx = feo;
                for (int j = 0; j < 3; j++) {
                    cox = fcm[cox][i]; fx = fem[fx][i];
                    epx = epm[epx][i]; eox = eom[eox][i];
                    path[d] = i * 3 + j;
                    idaxcross(epx, eox, cox, fx, slot, d - 1, i, face, path);
                }
            }
    }

    private static String cross(String scramble, int side, int face) {
        String[] s = scramble.split(" ");
        int ep = 1656, eo = 1104;
        for (int i = 0; i < s.length; i++)
            if (s[i].length() != 0) {
                int m = moveStr[side][face].indexOf(s[i].charAt(0));
                eo = eom[eo][m]; ep = epm[ep][m];
                if (s[i].length() > 1) {
                    eo = eom[eo][m];
                    ep = epm[ep][m];
                    if (s[i].charAt(1) == '\'') {
                        eo = eom[eo][m];
                        ep = epm[ep][m];
                    }
                }
            }
        //sb = new StringBuilder();
        for (int d = 0; d < 9; d++) {
            if (idacross(ep, eo, d, -1)) {
                StringBuilder sb = new StringBuilder(rotateStr[side][face]);
                for (int i = d; i > 0; i--)
                    sb.append(' ').append(moveStr[0][side].charAt(seq[i] / 3)).append(suff[seq[i] % 3]);
                return sb.toString();
            }
        }
        return "error";
    }

    public static String solveCross(String scramble, int face) {
        init();
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 6; i++)
            if (((face >> i) & 1) != 0) {
                sb.append("\nCross(").append(color[i]).append("): ");
                sb.append(cross(scramble, 0, i));
            }
        return sb.toString();
    }

    public static String solveCross(String scramble) {
        init();
        //String[] s = scramble.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int face = 0; face < 6; face++) {
            sb.append(color[face]).append(": ");
            sb.append(cross(scramble, 0, face)).append("\n");
        }
        return sb.toString();
    }

    public static String solveCrossf(String scramble) {
        init();
        String[] s = scramble.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int face = 0; face < 6; face++) {
            int ep = 1656, eo = 1104;
            for (int i = 0; i < s.length; i++)
                if (s[i].length() != 0) {
                    int m = moveStr[0][face].indexOf(s[i].charAt(0));
                    eo = eom[eo][m]; ep = epm[ep][m];
                    if (s[i].length() > 1) {
                        eo = eom[eo][m]; ep = epm[ep][m];
                        if (s[i].charAt(1) == '\'') {
                            eo = eom[eo][m]; ep = epm[ep][m];
                        }
                    }
                }
            solutions = new ArrayList<>();
            for (int d = 0; d < 9; d++) {
                int[] path = new int[d + 1];
                idacross(ep, eo, d, -1, face, path);
                if (solutions.size() > 0) {
                    sb.append(color[face]).append(":\n");
                    for (String sol : solutions) {
                        int idx = sol.indexOf('\t');
                        sb.append("  ").append(sol.substring(0, idx)).append("\n");
                    }
                    sb.append("\n");
                    break;
                }
            }
        }
        return sb.toString();
    }

    private static String xcross(String scramble, int face) {
        String[] s = scramble.split(" ");
        int[] co = new int[4], feo = new int[4];
        for (int i = 0; i < 4; i++) {
            co[i] = (i + 4) * 3;
            feo[i] = i * 2;
        }
        int ep = 1656, eo = 1104;
        for (int d = 0; d < s.length; d++)
            if (s[d].length() != 0) {
                int m = moveStr[0][face].indexOf(s[d].charAt(0));
                for (int i = 0; i < 4; i++) {
                    co[i] = fcm[co[i]][m];
                    feo[i] = fem[feo[i]][m];
                }
                ep = epm[ep][m]; eo = eom[eo][m];
                if (s[d].length() > 1) {
                    for (int i = 0; i < 4; i++) {
                        co[i] = fcm[co[i]][m];
                        feo[i] = fem[feo[i]][m];
                    }
                    eo = eom[eo][m]; ep = epm[ep][m];
                    if (s[d].charAt(1) == '\'') {
                        for (int i = 0; i < 4; i++) {
                            co[i] = fcm[co[i]][m];
                            feo[i] = fem[feo[i]][m];
                        }
                        eo = eom[eo][m]; ep = epm[ep][m];
                    }
                }
            }
        for (int d = 0; d < 11; d++)
            for (int slot = 0; slot < 4; slot++)
                if (idaxcross(ep, eo, co[slot], feo[slot], slot, d, -1)) {
                    StringBuilder sb = new StringBuilder(rotateStr[0][face]);
                    for (int i = d; i > 0; i--)
                        sb.append(' ').append(moveStr[0][0].charAt(seq[i] / 3)).append(suff[seq[i] % 3]);
                    return sb.toString();
                }
        return "error";
    }

    public static String solveXcross(String scramble, int face) {
        init();
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 6; i++)
            if (((face >> i) & 1) != 0) {
                sb.append("\nXCross(").append(color[i]).append("): ");
                sb.append(xcross(scramble, i));
            }
        return sb.toString();
    }

    public static String solveXcross(String scramble) {
        init();
        StringBuilder sb = new StringBuilder();
        for (int face = 0; face < 6; face++) {
            sb.append(color[face]).append(": ");
            sb.append(xcross(scramble, face)).append("\n");
        }
        return sb.toString();
    }

    public static String solveXcrossf(String scramble) {
        init();
        String[] s = scramble.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int face = 0; face < 6; face++) {
            int[] co = new int[4], feo = new int[4];
            for (int i = 0; i < 4; i++) {
                co[i] = (i + 4) * 3;
                feo[i] = i * 2;
            }
            int ep = 1656, eo = 1104;
            for (int d = 0; d < s.length; d++)
                if (s[d].length() != 0) {
                    int m = moveStr[0][face].indexOf(s[d].charAt(0));
                    for (int i = 0; i < 4; i++) {
                        co[i] = fcm[co[i]][m];
                        feo[i] = fem[feo[i]][m];
                    }
                    ep = epm[ep][m]; eo = eom[eo][m];
                    if (s[d].length() > 1) {
                        for (int i = 0; i < 4; i++) {
                            co[i] = fcm[co[i]][m];
                            feo[i] = fem[feo[i]][m];
                        }
                        eo = eom[eo][m]; ep = epm[ep][m];
                        if (s[d].charAt(1) == '\'') {
                            for (int i = 0; i < 4; i++) {
                                co[i] = fcm[co[i]][m];
                                feo[i] = fem[feo[i]][m];
                            }
                            eo = eom[eo][m]; ep = epm[ep][m];
                        }
                    }
                }
            solutions = new ArrayList<>();
            for (int d = 0; d < 11; d++) {
                for (int slot = 0; slot < 4; slot++) {
                    int[] path = new int[d + 1];
                    idaxcross(ep, eo, co[slot], feo[slot], slot, d, -1, face, path);
                }
                if (solutions.size() > 0) {
                    sb.append(color[face]).append(":\n");
                    for (String sol : solutions) {
                        int idx = sol.indexOf('\t');
                        sb.append("  ").append(sol.substring(0, idx)).append("\n");
                    }
                    sb.append("\n");
                    break;
                }
            }
        }
        return sb.toString();
    }

    public static String solveEofc(String scramble, int side) {
        init();
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 6; i++) {
            if (((side >> i) & 1) != 0)
                sb.append(eofc(scramble, i * 2)).append(eofc(scramble, i * 2 + 1));
        }
        return sb.toString();
    }

    public static String eofc(String scramble, int side) {
        String[] s = scramble.split(" ");
        int ep = 1656, eo = 1104, eof = 0;
        for (int i = 0; i < s.length; i++)
            if (s[i].length() != 0) {
                int m = EOline.moveStr[side].indexOf(s[i].charAt(0));
                eo = eom[eo][m]; ep = epm[ep][m]; eof = eom[eof][m];
                if (s[i].length() > 1) {
                    eo = eom[eo][m]; ep = epm[ep][m]; eof = eom[eof][m];
                    if (s[i].charAt(1) == '\'') {
                        eo = eom[eo][m]; ep = epm[ep][m]; eof = eom[eof][m];
                    }
                }
            }
        for (int d = 0; d < 13; d++) {
            //Log.w("dct", ""+d);
            if (idaeofc(ep, eo, eof, d, -1)) {
                StringBuilder sb = new StringBuilder("\n");
                sb.append(sideStr[side]).append(": ").append(EOline.rotateStr[side]);
                for (int i = d; i > 0; i--)
                    sb.append(' ').append(moveStr[0][0].charAt(seq[i] / 3)).append(suff[seq[i] % 3]);
                return sb.toString();
            }
        }
        return "\nerror";
    }

    public static int[][] easyCross(int depth) {
        if (!inif) {
            init();
            long t = System.currentTimeMillis();
            for (int i = 0; i < 23760; i++) ed[i] = -1;
            setPruning(ed, 494 * 384, 0);
            int c = 1;
            for (int d = 0; d < 8; d++) {
                // c=0;
                for (int i = 0; i < 190080; i++)
                    if (getPruning(ed, i) == d)
                        for (int m = 0; m < 6; m++) {
                            int x = i;
                            for (int n = 0; n < 3; n++) {
                                int p = epm[x >> 4][m];
                                int o = eom[x / 384 << 4 | (x & 15)][m];
                                x = p << 4 | (o & 15);
                                if (getPruning(ed, x) == 0xf) {
                                    setPruning(ed, x, d + 1);
                                    c++;
                                }
                            }
                        }
                Log.w("dct", d+1+"\t"+c);
            }
            t = System.currentTimeMillis() - t;
            //Log.w("dct", t+"ms init");
            inif = true;
        }
        Random r = new Random();
        int i;// = r.nextInt(190080);
        if (depth == 0) i = 494 * 384;
        else do {
            i = r.nextInt(190080);
        } while (getPruning(ed, i) > depth);
        int comb = i / 384;
        int perm = (i >> 4) % 24;
        int ori = i & 15;
        int[] c = new int[12];
        int[] p = new int[4];
        Utils.idxToPerm(p, perm, 4, false);
        idxToComb(c, p, comb, ori, new int[] {3, 2, 1, 0});
        int[][] arr = new int[2][12];
        for (i = 0; i < 12; i++) {
            if (c[i] < 0)
                arr[0][i] = arr[1][i] = -1;
            else {
                arr[0][i] = c[i] >> 1;
                arr[1][i] = c[i] & 1;
            }
        }
        return arr;
    }
}
