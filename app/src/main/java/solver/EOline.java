package solver;

import static solver.Utils.suff;
import static solver.Utils.turn;

public class EOline {
    private static short[][] eom = new short[2048][6];
    private static short[][] epm = new short[132][6];
    private static byte[] eod = new byte[2048];
    private static byte[] epd = new byte[132];
    private static int[] seq = new int[10];

    static {
        int[] arr = new int[12];
        for (int i = 0; i < 2048; i++) {
            for (int j = 0; j < 6; j++) {
                Utils.idxToFlip(arr, i, 12, true);
                Cross.edgemv(arr, j);
                eom[i][j] = (short) Utils.flipToIdx(arr, 12, true);
            }
        }
        for (int i = 0; i < 66; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 6; k++)
                    epm[i * 2 + j][k] = (short) getEpm(i, j, k);
        for (int i = 1; i < 2048; i++) eod[i] = -1;
        eod[0] = 0;
        Utils.createPrun(eod, 7, eom, 3);
        for (int i = 0; i < 132; i++) epd[i] = -1;
        epd[106] = 0;
        Utils.createPrun(epd, 4, epm, 3);
    }

    private static int getEpm(int eci, int epi, int k) {
        int[] combination = new int[12];
        Utils.idxToComb(combination, eci, 2, 12);
        int[] permutation = new int[2];
        Utils.idxToPerm(permutation, epi, 2, false);
        byte[] selectedEdges = {8, 10};
        int next = 0;
        int[] ep = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        for (int i = 0; i < 12; i++)
            if (combination[i] != 0) ep[i] = selectedEdges[permutation[next++]];
        switch (k) {
            case 0: Utils.circle(ep, 4, 7,  6,  5); break;
            case 1: Utils.circle(ep, 8, 9, 10, 11); break;
            case 2: Utils.circle(ep, 7, 3, 11,  2); break;
            case 3: Utils.circle(ep, 5, 1,  9,  0); break;
            case 4: Utils.circle(ep, 6, 2, 10,  1); break;
            case 5: Utils.circle(ep, 4, 0,  8,  3); break;
        }
        byte[] edgesMapping = {0, 1, 2, 3};
        int[] ec = new int[12];
        for (int i = 0; i < 12; i++)
            ec[i] = ep[i] > 0 ? 1 : 0;
        eci = Utils.combToIdx(ec, 2, 12);
        int[] edgesPermutation = new int[2];
        next = 0;
        for (int i = 0; i < 12; i++)
            if (ec[i] != 0) edgesPermutation[next++] = ep[i] > -1 ? edgesMapping[ep[i] - 8] : -1;
        epi = Utils.permToIdx(edgesPermutation, 2, false);
        return eci * 2 + epi;
    }

    private static String[] sideStr = {"DF DB", "DL DR", "UF UB", "UL UR",
            "LF LB", "LU LD", "RF RB", "RU RD", "FU FD", "FL FR", "BU BD", "BL BR"};
    protected static String[] moveStr = {"UDLRFB", "UDFBRL", "DURLFB", "DUFBLR",
            "RLUDFB", "RLFBDU", "LRDUFB", "LRFBUD", "BFLRUD", "BFUDRL", "FBLRDU", "FBDURL"};
    protected static String[] rotateStr = {"", "y", "z2", "z2 y", "z'", "z' y", "z", "z y", "x'", "x' y", "x", "x y"};

    private static boolean search(int eo, int ep, int depth, int l) {
        if (depth == 0) return eo == 0 && ep == 106;
        if (eod[eo] > depth || epd[ep] > depth) return false;
        for (int i = 0; i < 6; i++)
            if (i != l) {
                int x = eo, y = ep;
                for (int j = 0; j < 3; j++) {
                    x = eom[x][i];
                    y = epm[y][i];
                    if (search(x, y, depth - 1, i)) {
                        seq[depth] = i * 3 + j;
                        //sb.insert(0, " " + turn[i] + suff[j]);
                        return true;
                    }
                }
            }
        return false;
    }

    private static String eoline(String scramble, int face) {
        String[] s = scramble.split(" ");
        int ep = 106, eo = 0;
        for (int d = 0; d < s.length; d++) {
            if (s[d].length() > 0) {
                int o = moveStr[face].indexOf(s[d].charAt(0));
                ep = epm[ep][o]; eo = eom[eo][o];
                if (s[d].length() > 1) {
                    eo = eom[eo][o]; ep = epm[ep][o];
                    if (s[d].charAt(1) == '\'') {
                        eo = eom[eo][o]; ep = epm[ep][o];
                    }
                }
            }
        }
        for (int d = 0; d < 10; d++) {
            if (search(eo, ep, d, -1)) {
                StringBuilder sb = new StringBuilder();
                for (int j = d; j > 0; j--)
                    sb.append(' ').append(turn[seq[j] / 3]).append(suff[seq[j] % 3]);
                return "\n" + sideStr[face] + ": " + rotateStr[face] + sb.toString();
            }
        }
        return "\nerror";
    }

    public static String solveEoline(String scramble, int face) {
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 6; i++) {
            if (((face >> i) & 1) != 0)
                sb.append(eoline(scramble, i * 2)).append(eoline(scramble, i * 2 + 1));
        }
        return sb.toString();
    }
}
