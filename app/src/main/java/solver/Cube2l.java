package solver;

public class Cube2l {
    //private static byte[][] Cnk=new byte[8][8];
    private static short[][] cpm = new short[1680][6];
    private static short[][] com = new short[5670][6];
    private static byte[] cpd = new byte[1680];
    private static byte[] cod = new byte[5670];

    private static boolean ini = false;
    private static void init() {
        if (ini) return;
        int d;
        for (int i = 0; i < 70; i++) {
            for (int j = 0; j < 81; j++) {
                for (int k = 0; k < 6; k++) {
                    d = getmv(i, j, k);
                    com[i * 81 + j][k] = (short) (((d >> 7) / 24) * 81 + (d & 127));
                    if (j < 24) cpm[i * 24 + j][k] = (short) (d >> 7);
                }
            }
        }

        for (int i = 0; i < 1680; i++) cpd[i] = -1;
        cpd[0] = cpd[18] = cpd[16] = cpd[9] = 0;	//1656, 1665, 1672
        Utils.createPrun(cpd, 4, cpm, 3);

        for (int i = 0; i < 5670; i++) cod[i] = -1;
        cod[0] = 0;
        Utils.createPrun(cod, 5, com, 3);
        ini = true;
    }

    static {
        init();
    }

    private static int getmv(int c, int po, int v) {
        int[] n = new int[8], s = new int[4], y = new int[4];
        Cross.idxToPerm(s, po);
        Utils.idxToOri(y, po, 4, false);
        int q = 4, t;
        for (t = 0; 8 > t; t++)
            if (c >= Utils.Cnk[7 - t][q]) {
                c -= Utils.Cnk[7 - t][q--];
                n[t] = s[q] << 3 | y[3 - q];
            }
            else n[t] = -3;
        switch (v) {
            case 0: //U
                Utils.circle(n, 0, 3, 2, 1);
                break;
            case 1: //D
                Utils.circle(n, 4, 5, 6, 7);
                break;
            case 2: //L
                c = n[0]; n[0] = n[4] + 1; n[4] = n[7] + 2; n[7] = n[3] + 1; n[3] = c + 2;
                break;
            case 3: //R
                c = n[1]; n[1] = n[2] + 2; n[2] = n[6] + 1; n[6] = n[5] + 2; n[5] = c + 1;
                break;
            case 4: //F
                c = n[2]; n[2] = n[3] + 2; n[3] = n[7] + 1; n[7] = n[6] + 2; n[6] = c + 1;
                break;
            case 5: //B
                c = n[0]; n[0] = n[1] + 2; n[1] = n[5] + 1; n[5] = n[4] + 2; n[4] = c + 1;
                break;
        }
        c = po = 0; q = 4;
        for (t = 0; 8 > t; t++)
            if (0 <= n[t]) {
                c += Utils.Cnk[7 - t][q--];
                s[q] = n[t] >> 3;
                po += (n[t] & 7) % 3;
                po *= 3;
            }
        int i = Cross.permToIdx(s);
        return 24 * c + i << 7 | po / 3;
    }

    private static String[] turn = {"U", "D", "L", "R", "F", "B"};
    private static String[] suff = {"", "2", "'"};
    private static StringBuilder sb;
    private static boolean search(int cp, int co, int depth, int lm) {
        if (depth == 0) return (cp == 0 || cp == 18 || cp == 16 || cp == 9) && co == 0;
        if (cpd[cp] > depth || cod[co] > depth) return false;
        for (int i = 0; i < 6; i++)
            if (i != lm) {
                int y = cp, s = co;
                for (int j = 0; j < 3; j++) {
                    y = cpm[y][i]; s = com[s][i];
                    if (search(y, s, depth - 1, i)) {
                        sb.insert(0, " " + turn[i] + suff[j]);
                        return true;
                    }
                }
            }
        return false;
    }

    private static String[] moveIdx = {"UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFLRUD", "FBLRDU"};
    private static String[] color = {"D: ", "U: ", "L: ", "R: ", "F: ", "B: "};
    private static String[] rotIdx = {"", "z2", "z'", "z", "x'", "x"};
    private static short[] scp = {0, 1679, 665, 1030, 1446, 233}, sco = {0, 5589, 2239, 3470, 4912, 781};
    private static byte[][] oriIdx = {{0, 1, 2, 3, 4, 5}, {1, 0, 3, 2, 5, 4}, {3, 2, 0, 1, 3, 3}, {2, 3, 1, 0, 2, 2}, {5, 5, 5, 5, 0, 1}, {4, 4, 4, 4, 1, 0}};
    private static String solve(String scramble, int face) {
        String[] scr = scramble.split(" ");
        int[] cp = new int[6], co = new int[6];
        for (int y = 0; y < 6; y++) {
            cp[y] = scp[oriIdx[face][y]]; co[y] = sco[oriIdx[face][y]];
            for (int d = 0; d < scr.length; d++)
                if (scr[d].length() != 0) {
                    int o = moveIdx[y].indexOf(scr[d].charAt(0));
                    cp[y] = cpm[cp[y]][o]; co[y] = com[co[y]][o];
                    if (scr[d].length() > 1) {
                        co[y] = com[co[y]][o]; cp[y] = cpm[cp[y]][o];
                        if (scr[d].charAt(1) == '\'') {
                            co[y] = com[co[y]][o]; cp[y] = cpm[cp[y]][o];
                        }
                    }
                }
        }
        sb = new StringBuilder();
        for (int d = 0; ; d++)
            for (int idx = 0; idx < 6; idx++)
                if (search(cp[idx], co[idx], d, -1))
                    return "\n" + color[face] + rotIdx[idx] + sb.toString();
    }

    public static String solveFirstLayer(String scramble, int face) {
        if (face == 0) return "";
        if (face > 6) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                s.append(solve(scramble, i));
            }
            return s.toString();
        }
        return solve(scramble, face - 1);
    }
}
