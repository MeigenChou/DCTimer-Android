package solver;

import static solver.Utils.suff;

public class Cube2Face {
    private static short[][] facemv = new short[10626][3];
    private static byte[] prun = new byte[10626];
    private static int[] seq = new int[7];
    private static int[] solved = {1819, 0, 4844, 69, 494, 10625};

    private static void init() {
        int[] arr = new int[24];
        for (int i = 0; i < 10626; i++) {
            prun[i] = -1;
            for (int j = 0; j < 3; j++) {
                Utils.idxToComb(arr, i, 4, 24);
                switch (j) {
                    case 0: //U
                        Utils.circle(arr, 0,  2,  3,  1);
                        Utils.circle(arr, 4, 20, 16,  8);
                        Utils.circle(arr, 5, 21, 17,  9);
                        break;
                    case 1: //R
                        Utils.circle(arr, 4,  6,  7,  5);
                        Utils.circle(arr, 1,  9, 13, 22);
                        Utils.circle(arr, 3, 11, 15, 20);
                        break;
                    case 2: //F
                        Utils.circle(arr, 8, 10, 11,  9);
                        Utils.circle(arr, 2, 19, 13,  4);
                        Utils.circle(arr, 3, 17, 12,  6);
                        break;
                }
                facemv[i][j] = (short) Utils.combToIdx(arr, 4, 24);
            }
        }
        for (int i = 0; i < 6; i++) prun[solved[i]] = 0;
        Utils.createPrun(prun, 5, facemv, 3);
    }

    static {
        init();
    }

    private static boolean search(int face, int d, int lm) {
        if (d == 0) return prun[face] == 0;
        if (prun[face] > d) return false;
        for (int i = 0; i < 3; i++)
            if (i != lm) {
                int x = face;
                for (int j = 0; j < 3; j++) {
                    x = facemv[x][i];
                    if (search(x, d - 1, i)) {
                        seq[d] = i * 3 + j;
                        return true;
                    }
                }
            }
        return false;
    }

    private static String[] color = {"D: ", "U: ", "L: ", "R: ", "F: ", "B: "};
    private static String solve(String scramble, int face) {
        String[] s = scramble.split(" ");
        int fc = solved[face], d;
        for (d = 0; d < s.length; d++)
            if (s[d].length() != 0) {
                int m = "URF".indexOf(s[d].charAt(0));
                fc = facemv[fc][m];
                if (s[d].length() > 1) {
                    fc = facemv[fc][m];
                    if (s[d].charAt(1) == '\'')
                        fc = facemv[fc][m];
                }
            }
        for (d = 0; d < 7; d++)
            if (search(fc, d, -1)) {
                StringBuilder sb = new StringBuilder("\n");
                sb.append(color[face]);
                for (int i = d; i > 0; i--)
                    sb.append("URF".charAt(seq[i] / 3)).append(suff[seq[i] % 3]).append(" ");
                return sb.toString();
            }
        return "error";
    }

    public static String solveFace(String scramble, int face) {
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 6; i++) {
            if (((face >> i) & 1) != 0)
                sb.append(solve(scramble, i));
        }
        return sb.toString();
    }
}
