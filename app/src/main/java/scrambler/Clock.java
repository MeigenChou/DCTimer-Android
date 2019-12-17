package scrambler;

public class Clock {
    private static String[] turns = {"UR", "DR", "DL", "UL", "U", "R", "D", "L", "ALL"};
    private static int[][] moves = {
            {0,1,1,0,1,1,0,0,0,  -1, 0, 0, 0, 0, 0, 0, 0, 0},// UR
            {0,0,0,0,1,1,0,1,1,   0, 0, 0, 0, 0, 0,-1, 0, 0},// DR
            {0,0,0,1,1,0,1,1,0,   0, 0, 0, 0, 0, 0, 0, 0,-1},// DL
            {1,1,0,1,1,0,0,0,0,   0, 0,-1, 0, 0, 0, 0, 0, 0},// UL
            {1,1,1,1,1,1,0,0,0,  -1, 0,-1, 0, 0, 0, 0, 0, 0},// U
            {0,1,1,0,1,1,0,1,1,  -1, 0, 0, 0, 0, 0,-1, 0, 0},// R
            {0,0,0,1,1,1,1,1,1,   0, 0, 0, 0, 0, 0,-1, 0,-1},// D
            {1,1,0,1,1,0,1,1,0,   0, 0,-1, 0, 0, 0, 0, 0,-1},// L
            {1,1,1,1,1,1,1,1,1,  -1, 0,-1, 0, 0, 0,-1, 0,-1},// A
    };
    private static int[][] movesJaap = {
            {1,1,1,1,1,1,0,0,0,  -1, 0,-1, 0, 0, 0, 0, 0, 0},//UUdd
            {0,1,1,0,1,1,0,1,1,  -1, 0, 0, 0, 0, 0,-1, 0, 0},//dUdU
            {0,0,0,1,1,1,1,1,1,   0, 0, 0, 0, 0, 0,-1, 0,-1},//ddUU
            {1,1,0,1,1,0,1,1,0,   0, 0,-1, 0, 0, 0, 0, 0,-1},//UdUd
            {0,0,0,0,0,0,1,0,1,   0, 0, 0,-1,-1,-1,-1,-1,-1},
            {1,0,0,0,0,0,1,0,0,   0,-1,-1, 0,-1,-1, 0,-1,-1},
            {1,0,1,0,0,0,0,0,0,  -1,-1,-1,-1,-1,-1, 0, 0, 0},
            {0,0,1,0,0,0,0,0,1,  -1,-1, 0,-1,-1, 0,-1,-1, 0},
            {0,1,1,1,1,1,1,1,1,  -1, 0, 0, 0, 0, 0,-1, 0,-1},//dUUU
            {1,1,0,1,1,1,1,1,1,   0, 0,-1, 0, 0, 0,-1, 0,-1},//UdUU
            {1,1,1,1,1,1,1,1,0,  -1, 0,-1, 0, 0, 0, 0, 0,-1},//UUUd
            {1,1,1,1,1,1,0,1,1,  -1, 0,-1, 0, 0, 0,-1, 0, 0},//UUdU
            {1,1,1,1,1,1,1,1,1,  -1, 0,-1, 0, 0, 0,-1, 0,-1},//UUUU
            {1,0,1,0,0,0,1,0,1,  -1,-1,-1,-1,-1,-1,-1,-1,-1},//dddd
    };
    private static int[] idx = {1, 3, 2, 0};
    private static int[] epoIdx = {12, 8, 1, 5, 11, 0, 4, 10, 3, 7, 9, 2, 6, 13};
    public int[] posit = new int[18];
    public int[] pegs = new int[4];

    public String scramble() {
        for (int i = 0; i < 18; i++) posit[i] = 0;
        StringBuilder scramble = new StringBuilder();
        int[] positCopy = new int[18];
        for (int x = 0; x < 9; x++) {
            int turn = (int) (Math.random() * 12 - 5);
            for (int j = 0; j < 18; j++) {
                positCopy[j] += turn * moves[x][j];
            }
            boolean clockwise = turn >= 0;
            turn = Math.abs(turn);
            scramble.append(turns[x]).append(turn).append(clockwise ? "+" : "-").append(" ");
        }
        scramble.append("y2 ");
        for (int i = 0; i < 9; i++) {
            posit[i] = positCopy[i + 9];
            posit[i + 9] = positCopy[i];
        }
        for (int x = 4; x < 9; x++) {
            int turn = (int) (Math.random() * 12 - 5);
            for (int j = 0; j < 18; j++) {
                posit[j] += turn * moves[x][j];
            }
            boolean clockwise = ( turn >= 0 );
            turn = Math.abs(turn);
            scramble.append(turns[x]).append(turn).append(clockwise ? "+" : "-").append(" ");
        }
        for (int j = 0; j < 18; j++) {
            posit[j] %= 12;
            while (posit[j] <= 0) posit[j] += 12;
        }
        boolean isFirst = true;
        for (int x = 0; x < 4; x++) {
            pegs[idx[x]] = (int) (Math.random() * 2);
            if (pegs[idx[x]] == 0) {
                if (!isFirst) scramble.append(" ");
                scramble.append(turns[x]);
                isFirst = false;
            }
        }
        return scramble.toString();
    }

    public String scrambleJaap(boolean concise) {
        int[] seq = new int[14];
        int i, j;
        for (i = 0; i < 18; i++) posit[i] = 0;
        for (i = 0; i < 14; i++) {
            seq[i] = (int) ((Math.random() * 12) - 5);
        }
        for (i = 0; i < 14; i++) {
            for (j = 0; j < 18; j++) {
                posit[j] += seq[i] * movesJaap[i][j];
            }
        }
        for (j = 0; j < 18; j++) {
            posit[j] %= 12;
            while (posit[j] <= 0) posit[j] += 12;
        }
        StringBuilder sb = new StringBuilder();
        if (concise) {
            for (i = 0; i < 4; i++)
                sb.append("(").append(seq[i]).append(", ").append(seq[i + 4]).append(") / ");
            for (i = 8; i < 14; i++)
                sb.append("(").append(seq[i]).append(") / ");
        } else {
            sb.append("UUdd u=").append(seq[0]).append(",d=").append(seq[4]).append(" / ");
            sb.append("dUdU u=").append(seq[1]).append(",d=").append(seq[5]).append(" / ");
            sb.append("ddUU u=").append(seq[2]).append(",d=").append(seq[6]).append(" / ");
            sb.append("UdUd u=").append(seq[3]).append(",d=").append(seq[7]).append(" / ");
            sb.append("dUUU u=").append(seq[8]).append(" / ");
            sb.append("UdUU u=").append(seq[9]).append(" / ");
            sb.append("UUUd u=").append(seq[10]).append(" / ");
            sb.append("UUdU u=").append(seq[11]).append(" / ");
            sb.append("UUUU u=").append(seq[12]).append(" / ");
            sb.append("dddd d=").append(seq[13]).append(" / ");
        }
        for (i = 0; i < 4; i++) {
            pegs[i] = (int) (Math.random() * 2);
            if (pegs[i] == 0) sb.append("U");
            else sb.append("d");
        }
        return sb.toString();
    }

    public String scrambleEpo() {
        int[] seq = new int[14];
        int i, j;
        for (i = 0; i < 18; i++) posit[i] = 0;
        for (i = 0; i < 14; i++) {
            seq[i] = (int) ((Math.random() * 12) - 5);
        }
        for (i = 0; i < 14; i++) {
            for (j = 0; j < 18; j++) {
                posit[j] += seq[i] * movesJaap[epoIdx[i]][j];
            }
        }
        for (j = 0; j < 18; j++) {
            posit[j] %= 12;
            while (posit[j] <= 0) posit[j] += 12;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("UUUU u=").append(seq[0]).append(" / ");
        sb.append("dUUU u=").append(seq[1]).append(" / ");
        sb.append("dUdU u=").append(seq[2]).append(",d=").append(seq[3]).append(" / ");
        sb.append("UUdU u=").append(seq[4]).append(" / ");
        sb.append("UUdd u=").append(seq[5]).append(",d=").append(seq[6]).append(" / ");
        sb.append("UUUd u=").append(seq[7]).append(" / ");
        sb.append("UdUd u=").append(seq[8]).append(",d=").append(seq[9]).append(" / ");
        sb.append("UdUU u=").append(seq[10]).append(" / ");
        sb.append("ddUU u=").append(seq[11]).append(",d=").append(seq[12]).append(" / ");
        sb.append("dddd d=").append(seq[13]).append(" / ");
        for (i = 0; i < 4; i++) {
            pegs[i] = (int) (Math.random() * 2);
            if (pegs[i] == 0) sb.append("U");
            else sb.append("d");
        }
        return sb.toString();
    }

    public int[] getPosit() {
        return posit;
    }

    public int[] getPegs() {
        return pegs;
    }
}
