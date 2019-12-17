package solver;

import java.util.Random;

import static solver.Utils.swap;

public class HalfTurn {
    private static byte[][] cpm = new byte[24][6];
    private static byte[][][] epm = new byte[3][24][6];
    private static byte[][] cpd = new byte[24][24];
    private static byte[][][] epd = new byte[24][24][24];
    private static int[] seq = new int[21];

    static {
        int[] temp = new int[4];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 6; j++) {
                Utils.idxToPerm(temp, i, 4, false);
                switch (j) {
                    case 0: swap(temp, 0, 1); break;
                    case 1: swap(temp, 2, 3); break;
                    case 2: swap(temp, 0, 2); break;
                    case 3: swap(temp, 1, 3); break;
                    case 4: swap(temp, 1, 2); break;
                    case 5: swap(temp, 0, 3); break;
                }
                cpm[i][j] = (byte) Utils.permToIdx(temp, 4, false);
                Utils.idxToPerm(temp, i, 4, false);
                switch (j) {
                    case 0: swap(temp, 0, 1); break;
                    case 1: swap(temp, 2, 3); break;
                    case 2: swap(temp, 0, 3); break;
                    case 3: swap(temp, 1, 2); break;
                }
                epm[0][i][j] = (byte) Utils.permToIdx(temp, 4, false);
                Utils.idxToPerm(temp, i, 4, false);
                switch (j) {
                    case 0: swap(temp, 0, 1); break;
                    case 1: swap(temp, 2, 3); break;
                    case 4: swap(temp, 0, 3); break;
                    case 5: swap(temp, 1, 2); break;
                }
                epm[1][i][j] = (byte) Utils.permToIdx(temp, 4, false);
                Utils.idxToPerm(temp, i, 4, false);
                switch (j) {
                    case 2: swap(temp, 0, 3); break;
                    case 3: swap(temp, 1, 2); break;
                    case 4: swap(temp, 0, 1); break;
                    case 5: swap(temp, 2, 3); break;
                }
                epm[2][i][j] = (byte) Utils.permToIdx(temp, 4, false);
            }
        }
        for (int i = 0; i < 24; i++)
            for (int j = 0; j < 24; j++)
                cpd[i][j] = -1;
        cpd[0][0] = 0;
        int d = 0, c = 1;
        while (c > 0) {
            c = 0;
            for (int i = 0; i < 24; i++)
                for (int j = 0; j < 24; j++)
                    if (cpd[i][j] == d)
                        for (int k = 0; k < 6; k++) {
                            byte p = cpm[i][k], q = cpm[j][k];
                            if (cpd[p][q] == -1) {
                                cpd[p][q] = (byte) (d + 1);
                                c++;
                            }
                        }
            d++;
            //System.out.println(d+" "+c);
        }
        for (int i = 0; i < 24; i++)
            for (int j = 0; j < 24; j++)
                for (int k = 0; k < 24; k++)
                    epd[i][j][k] = -1;
        epd[0][0][0] = 0;
        d = 0; c = 1;
        while (c > 0) {
            c = 0;
            for (int i = 0; i < 24; i++)
                for (int j = 0; j < 24; j++)
                    for (int k = 0; k < 24; k++)
                        if (epd[i][j][k] == d)
                            for (int l = 0; l < 6; l++) {
                                byte p = epm[0][i][l], q = epm[1][j][l], r = epm[2][k][l];
                                if (epd[p][q][r] == -1) {
                                    epd[p][q][r] = (byte) (d + 1);
                                    c++;
                                }
                            }
            d++;
            //System.out.println(d+" "+c);
        }
    }

    private static String[] turn = {"U", "D", "F", "B", "L", "R"};
    private static boolean search(int cp1, int cp2, int ep1, int ep2, int ep3, int d, int l) {
        if (d == 0) return cp1 == 0 && cp2 == 0 && ep1 == 0 && ep2 == 0 && ep3 == 0;
        if (cpd[cp1][cp2] > d || epd[ep1][ep2][ep3] > d) return false;
        for (int n = 0; n < 6; n++) {
            if (n != l) {
                int c1n = cpm[cp1][n], c2n = cpm[cp2][n],
                        e1n = epm[0][ep1][n], e2n = epm[1][ep2][n], e3n = epm[2][ep3][n];
                if (search(c1n, c2n, e1n, e2n, e3n, d - 1, n)) {
                    seq[d] = n;
                    //sb.insert(0, turn[n] + "2 ");
                    return true;
                }
            }
        }
        return false;
    }

    public static String scramble() {
        int cp1, cp2;
        Random r = new Random();
        do {
            cp1 = r.nextInt(24);
            cp2 = r.nextInt(24);
        } while (cpd[cp1][cp2] < 0);
        int ep1, ep2, ep3;
        do {
            ep1 = r.nextInt(24);
            ep2 = r.nextInt(24);
            ep3 = r.nextInt(24);
        } while (epd[ep1][ep2][ep3] < 0);
        for (int d = 0; d < 20; d++) {
            if (search(cp1, cp2, ep1, ep2, ep3, d, -1)) {
                if (d < 2) return scramble();
                if (d < 4) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= d; i++) {
                    sb.append(turn[seq[i]]).append("2 ");
                }
                return sb.toString();
            }
        }
        return "error";
    }
}
