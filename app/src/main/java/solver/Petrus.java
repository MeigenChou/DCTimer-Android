package solver;

public class Petrus {
    public static short[][] epm = new short[1320][6];
    public static short[][] eom = new short[1760][6];
    private static byte[][] com = new byte[24][6];

    private static byte[] epd = new byte[1320];
    private static byte[] eod = new byte[1760];

    private static int edgemv(int c, int p, int o, int f) {
        int[] n = new int[12], s = new int[3];
        int q, t, v;
        for (q = 1; q <= 3; q++) {
            t = p % q;
            p = p / q;
            for (v = q - 2; v >= t; v--)
                s[v + 1] = s[v];
            s[t] = 3 - q;
        }
        q = 3;
        for (t = 0; t < 12; t++)
            if (c >= Utils.Cnk[11 - t][q]) {
                c -= Utils.Cnk[11 - t][q--];
                n[t] = s[q] << 1 | o & 1;
                o >>= 1;
            } else n[t] = -1;
        switch (f) {
            case 0:
                Cross.circle(n, 0, 1, 2, 3, 0); break;
            case 1:
                Cross.circle(n, 11, 10, 9, 8, 0); break;
            case 2:
                Cross.circle(n, 1, 4, 9, 5, 0); break;
            case 3:
                Cross.circle(n, 3, 6, 11, 7, 0); break;
            case 4:
                Cross.circle(n, 0, 7, 8, 4, 1); break;
            case 5:
                Cross.circle(n, 2, 5, 10, 6, 1); break;
        }
        c = 0; q = 3;
        for (t = 0; t < 12; t++)
            if (n[t] >= 0) {
                c += Utils.Cnk[11 - t][q--];
                s[q] = n[t] >> 1;
                o |= (n[t] & 1) << 2 - q;
            }
        p = 0;
        for (q = 0; q < 3; q++) {
            for (v = t = 0; 3 > v && s[v] != q; v++)
                if (s[v] > q) t++;
            p = p * (3 - q) + t;
        }
        return 6 * c + p << 3 | o;
    }

    private static boolean ini = false;
    public static void init() {
        if (ini) return;
        int i, j;
        for (i = 0; i < 220; i++)
            for (j = 0; j < 8; j++)
                for (int k = 0; k < 6; k++) {
                    int d = edgemv(i, j, j, k);
                    if (j < 6) epm[i * 6 + j][k] = (short) (d >> 3);
                    eom[i * 8 + j][k] = (short) (((d >> 3) / 6) << 3 | d & 7);
                }
        ini = true;
    }

    private static boolean inip = false;
    private static void initp() {
        if (inip) return;
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
        epd[132] = 0;
        Utils.createPrun(epd, 5, epm, 3);
        for (i = 0; i < 1760; i++) eod[i] = -1;
        eod[176] = 0;
        Utils.createPrun(eod, 5, eom, 3);
        inip = true;
    }

    private static String[][] turn = {
            { "D", "U", "L", "R", "B", "F" }, { "F", "B", "L", "R", "D", "U" },
            { "D", "U", "F", "B", "L", "R" }, { "D", "U", "R", "L", "F", "B" },
            { "U", "D", "F", "B", "R", "L" }, { "U", "D", "L", "R", "F", "B" },
            { "U", "D", "R", "L", "B", "F" }, { "U", "D", "B", "F", "L", "R" }
    };
    private static String[] suff = {"", "2", "'"};
    //private static StringBuilder sb = new StringBuilder();
    private static String[] seq = new String[13];
    private static boolean idaPetrus(int co, int ep, int eo, int depth, int lm, int block) {
        if (depth == 0) return co == 12 && ep == 132 && eo == 176;
        if (epd[ep] > depth || eod[eo] > depth) return false;
        for (int i = 0; i < 6; i++)
            if (i != lm) {
                int w = co, y = ep, s = eo;
                for (int j = 0; j < 3; j++) {
                    w = com[w][i];
                    y = epm[y][i];
                    s = eom[s][i];
                    if (idaPetrus(w, y, s, depth - 1, i, block)) {
                        seq[depth] = turn[block][i] + suff[j];
                        return true;
                    }
                }
            }
        return false;
    }

    private static String[] moveIdx = {"DULRBF", "FBLRDU", "DUFBLR", "DURLFB",
            "UDFBRL", "UDLRFB", "UDRLBF", "UDBFLR"};
    private static String[] blks = {"ULF:", "ULB:", "URF:", "URB:", "DLF:", "DLB:", "DRF:", "DRB:"};
    private static String petrus(String scramble, int block) {
        String[] scr = scramble.split(" ");
        int co = 12, ep = 132, eo = 176;
        for (int d = 0; d < scr.length; d++)
            if (0 != scr[d].length()) {
                int o = moveIdx[block].indexOf(scr[d].charAt(0));
                co = com[co][o]; ep = epm[ep][o]; eo = eom[eo][o];
                if (scr[d].length() > 1) {
                    co = com[co][o]; eo = eom[eo][o]; ep = epm[ep][o];
                    if (scr[d].charAt(1) == '\'') {
                        co = com[co][o]; eo = eom[eo][o]; ep = epm[ep][o];
                    }
                }
            }
        //sb = new StringBuilder();
        for (int d = 0; d < 12; d++) {
            if (idaPetrus(co, ep, eo, d, -1, block)) {
                StringBuilder sb = new StringBuilder("\n");
                sb.append(blks[block]);
                for (int i = d; i > 0; i--)
                    sb.append(' ').append(seq[i]);
                return sb.toString();
            }
        }
        return "\nerror";
    }

    public static String solvePetrus(String scramble, int face) {
        initp();
        StringBuilder s = new StringBuilder("\n");
        for (int i = 0; i < 8; i++) {
            if (((face >> i) & 1) != 0) s.append(petrus(scramble, i));
        }
        return s.toString();
    }
}
