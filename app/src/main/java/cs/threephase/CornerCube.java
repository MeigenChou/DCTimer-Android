package cs.threephase;

import static cs.threephase.Moves.*;
import java.util.Random;

import solver.Utils;

class CornerCube {

    /**
     * 18 move cubes
     */
    private static CornerCube[] moveCube = new CornerCube[18];

//	private static final int[] cpmv = {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1,
//										1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1};

    private int[] cp = {0, 1, 2, 3, 4, 5, 6, 7};
    private int[] co = {0, 0, 0, 0, 0, 0, 0, 0};

    CornerCube temps = null;//new CornerCube();

    CornerCube() {
    }

    CornerCube(Random r) {
        this(r.nextInt(40320), r.nextInt(2187));
    }

    CornerCube(int cperm, int twist) {
        this.setCPerm(cperm);
        this.setTwist(twist);
    }

    CornerCube(CornerCube c) {
        copy(c);
    }

    void copy(CornerCube c) {
        System.arraycopy(c.cp, 0, this.cp, 0, 8);
        System.arraycopy(c.co, 0, this.co, 0, 8);
    }

    int getParity() {
        return Util.parity(cp);
    }

    static final byte[][] cornerFacelet = { { U9, R1, F3 }, { U7, F1, L3 }, { U1, L1, B3 }, { U3, B1, R3 },
            { D3, F9, R7 }, { D1, L9, F7 }, { D7, B9, L7 }, { D9, R9, B7 } };

    void fill333Facelet(char[] facelet) {
        for (int corn=0; corn<8; corn++) {
            int j = cp[corn];
            int ori = co[corn];
            for (int n=0; n<3; n++) {
                facelet[cornerFacelet[corn][(n + ori) % 3]] = "URFDLB".charAt(cornerFacelet[j][n]/9);
            }
        }
    }

    /**
     * prod = a * b, Corner Only.
     */
    static void CornMult(CornerCube a, CornerCube b, CornerCube prod) {
        for (int corn=0; corn<8; corn++) {
            prod.cp[corn] = a.cp[b.cp[corn]];
            int oriA = a.co[b.cp[corn]];
            int oriB = b.co[corn];
            int ori = oriA;
            ori += (oriA<3) ? oriB : 6-oriB;
            ori %= 3;
            if ((oriA >= 3) ^ (oriB >= 3)) {
                ori += 3;
            }
            prod.co[corn] = ori;
        }
    }

    void setTwist(int idx) {
        int twst = 0;
        for (int i=6; i>=0; i--) {
            twst += co[i] = (byte) (idx % 3);
            idx /= 3;
        }
        co[7] = (byte) ((15 - twst) % 3);
    }

    private void setCPerm(int idx) {
        Utils.set8Perm(cp, 8, idx);
    }

    void move(int idx) {
        if (temps == null) {
            temps = new CornerCube();
        }
        CornMult(this, moveCube[idx], temps);
        copy(temps);
    }

    static {
        moveCube[0] = new CornerCube(15120, 0);
        moveCube[3] = new CornerCube(21021, 1494);
        moveCube[6] = new CornerCube(8064, 1236);
        moveCube[9] = new CornerCube(9, 0);
        moveCube[12] = new CornerCube(1230, 412);
        moveCube[15] = new CornerCube(224, 137);
        for (int a=0; a<18; a+=3) {
            for (int p=0; p<2; p++) {
                moveCube[a+p+1] = new CornerCube();
                CornMult(moveCube[a+p], moveCube[a], moveCube[a+p+1]);
            }
        }
    }
}
