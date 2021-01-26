package cs.sq12phase;

import android.util.Log;

import solver.Utils;

class Square {
    int edgeperm;		//number encoding the edge permutation 0-40319
    int cornperm;		//number encoding the corner permutation 0-40319
    boolean topEdgeFirst;	//true if top layer starts with edge left of seam
    boolean botEdgeFirst;	//true if bottom layer starts with edge right of seam
    int ml;			//shape of middle layer (+/-1, or 0 if ignored)

    static byte[] SquarePrun = new byte[40320 * 2];			//pruning table; #twists to solve corner|edge permutation

    static char[] sqTwistMove = new char[40320];			//transition table for twists
    static char[] sqTopMove = new char[40320];			//transition table for top layer turns
    static char[] sqBottomMove = new char[40320];			//transition table for bottom layer turns

    static boolean ini = false;

    public static void init() {
        if (ini) {
            return;
        }
        int[] pos = new int[8];
        int temp;

        for (int i = 0; i < 40320; i++) {
            //twist
            Utils.set8Perm(pos, 8, i);

            temp = pos[2]; pos[2] = pos[4]; pos[4] = temp;
            temp = pos[3]; pos[3] = pos[5]; pos[5] = temp;
            sqTwistMove[i] = (char) Utils.get8Perm(pos, 8);

            //top layer turn
            Utils.set8Perm(pos, 8, i);
            temp = pos[0]; pos[0] = pos[1]; pos[1] = pos[2]; pos[2] = pos[3]; pos[3] = temp;
            sqTopMove[i] = (char) Utils.get8Perm(pos, 8);

            //bottom layer turn
            Utils.set8Perm(pos, 8, i);
            temp = pos[4]; pos[4] = pos[5]; pos[5] = pos[6]; pos[6] = pos[7]; pos[7] = temp;
            sqBottomMove[i] = (char) Utils.get8Perm(pos, 8);
        }

        for (int i = 0; i < 40320 * 2; i++) {
            SquarePrun[i] = -1;
        }
        SquarePrun[0] = 0;
        int depth = 0;
        int done = 1;
        while (done < 40320 * 2) {
            boolean inv = depth >= 11;
            int find = inv ? -1 : depth;
            int check = inv ? depth : -1;
            ++depth;
            OUT:
            for (int i = 0; i < 40320 * 2; i++) {
                if (SquarePrun[i] != find) {
                    continue;
                }
                int perm = i >> 1;
                int ml = i & 1;

                //try twist
                int idx = sqTwistMove[perm] << 1 | (1 - ml);
                if (SquarePrun[idx] == check) {
                    ++done;
                    SquarePrun[inv ? i : idx] = (byte) (depth);
                    if (inv) continue;
                }

                //try turning top layer
                for (int m = 0; m < 4; m++) {
                    perm = sqTopMove[perm];
                    idx = perm << 1 | ml;
                    if (SquarePrun[idx] == check) {
                        ++done;
                        SquarePrun[inv ? i : idx] = (byte) (depth);
                        if (inv) continue OUT;
                    }
                }
                //try turning bottom layer
                for (int m = 0; m < 4; m++) {
                    perm = sqBottomMove[perm];
                    idx = perm << 1 | ml;
                    if (SquarePrun[idx] == check) {
                        ++done;
                        SquarePrun[inv ? i : idx] = (byte) (depth);
                        if (inv) continue OUT;
                    }
                }
            }
            //Log.w("dct", "sq "+depth+"\t"+done);
        }
        ini = true;
    }
}

