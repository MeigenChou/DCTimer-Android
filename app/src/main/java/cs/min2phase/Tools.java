package cs.min2phase;

import java.util.Random;
import java.io.*;

import solver.Cross;
import solver.Utils;

import com.dctimer.APP;

import static solver.Utils.read;
import static solver.Utils.write;

/**
 * Some useful functions.
 */
public class Tools {
    private static Random gen = new Random();

    protected Tools() {}

    public static void init() {
        if (Search.inited) {
            return;
        }
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(APP.dataPath + "twophase.dat"));
            read(CubieCube.FlipS2R, in);
            read(CubieCube.TwistS2R, in);
            read(CubieCube.EPermS2R, in);
            read(CubieCube.MtoEPerm, in);
            in.read(CubieCube.Perm2Comb);
            //in.readFully(CubieCube.Perm2Comb);
            read(CoordCube.TwistMove, in);
            read(CoordCube.FlipMove, in);
            read(CoordCube.UDSliceMove, in);
            read(CoordCube.UDSliceConj, in);
            read(CoordCube.CPermMove, in);
            read(CoordCube.EPermMove, in);
            read(CoordCube.MPermMove, in);
            read(CoordCube.MPermConj, in);
            read(CoordCube.CCombMove, in);
            read(CoordCube.CCombConj, in);
            read(CoordCube.MCPermPrun, in);
            read(CoordCube.MEPermPrun, in);
            read(CoordCube.EPermCCombPrun, in);
            if (Search.USE_FULL_PRUN) {
                read(CubieCube.UDSliceFlipS2R, in);
                read(CubieCube.TwistS2RF, in);
                read(CoordCube.TwistConj, in);
                read(CubieCube.FlipSlice2UDSliceFlip, in);
                read(CoordCube.UDSliceFlipTwistPrun, in);
            } else {
                read(CoordCube.UDSliceTwistPrun, in);
                read(CoordCube.UDSliceFlipPrun, in);
                if (Search.USE_TWIST_FLIP_PRUN) {
                    read(CubieCube.FlipS2RF, in);
                    read(CoordCube.TwistFlipPrun, in);
                }
            }
            in.close();
            //CubieCube.initMove();
            CubieCube.initSym();
        } catch (Exception e) {
            Search.init();
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(APP.dataPath + "twophase.dat"));
                write(CubieCube.FlipS2R, out);                  //          672
                write(CubieCube.TwistS2R, out);                 // +        648
                write(CubieCube.EPermS2R, out);                 // +      5,536
                write(CubieCube.MtoEPerm, out);                 // +     80,640
                out.write(CubieCube.Perm2Comb);                 // +      2,768
                write(CoordCube.TwistMove, out);                // +     11,664
                write(CoordCube.FlipMove, out);                 // +     12,096
                write(CoordCube.UDSliceMove, out);              // +     17,820
                write(CoordCube.UDSliceConj, out);              // +      7,920
                write(CoordCube.CPermMove, out);                // +     99,648
                write(CoordCube.EPermMove, out);                // +     55,360
                write(CoordCube.MPermMove, out);                // +        480
                write(CoordCube.MPermConj, out);                // +        768
                write(CoordCube.CCombMove, out);                // +      1,400
                write(CoordCube.CCombConj, out);                // +      2,240
                write(CoordCube.MCPermPrun, out);               // +     33,216
                write(CoordCube.MEPermPrun, out);               // +     33,216
                write(CoordCube.EPermCCombPrun, out);           // +     96,880
                if (Search.USE_FULL_PRUN) {
                    write(CubieCube.UDSliceFlipS2R, out);       // +    257,720
                    write(CubieCube.TwistS2RF, out);            // +      5,184
                    write(CoordCube.TwistConj, out);            // +     69,984
                    write(CubieCube.FlipSlice2UDSliceFlip, out);// +    665,280
                    write(CoordCube.UDSliceFlipTwistPrun, out); // + 35,227,104
                } else {                                        // = 36,688,244 Bytes
                    write(CoordCube.UDSliceTwistPrun, out);     // +     80,192
                    write(CoordCube.UDSliceFlipPrun, out);      // +     83,160
                    if (Search.USE_TWIST_FLIP_PRUN) {           // =    626,324 Bytes
                        write(CubieCube.FlipS2RF, out);         // +      5,376
                        write(CoordCube.TwistFlipPrun, out);    // +    331,776
                    }                                           // =    963,476 Bytes
                }
            } catch (Exception e2) { e2.printStackTrace(); }
        }
        Search.inited = true;
    }

    /**
     * Set Random Source.
     * @param gen new random source.
     */
    public static void setRandomSource(Random gen) {
        Tools.gen = gen;
    }

    /**
     * Generates a random cube.<br>
     *
     * The random source can be set by {@link cs.min2phase.Tools#setRandomSource(java.util.Random)}
     *
     * @return A random cube in the string representation. Each cube of the cube space has almost (depends on randomSource) the same probability.
     *
     * @see cs.min2phase.Tools#setRandomSource(java.util.Random)
     * @see cs.min2phase.Search#solution(java.lang.String facelets, int maxDepth, long timeOut, long timeMin, int verbose)
     */
    public static String randomCube() {
        return randomState(STATE_RANDOM, STATE_RANDOM, STATE_RANDOM, STATE_RANDOM);
    }

    private static int resolveOri(int[] arr, int base) {
        int sum = 0, idx = 0, lastUnknown = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == -1) {
                arr[i] = gen.nextInt(base);
                lastUnknown = i;
            }
            sum += arr[i];
        }
        if (sum % base != 0 && lastUnknown != -1) {
            arr[lastUnknown] = (30 + arr[lastUnknown] - sum) % base;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            idx *= base;
            idx += arr[i];
        }
        return idx;
    }

    private static int countUnknown(int[] arr) {
        if (arr == STATE_SOLVED) {
            return 0;
        }
        int cnt = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == -1) {
                cnt++;
            }
        }
        return cnt;
    }

    private static int resolvePerm(int[] arr, int cntU, int parity) {
        if (arr == STATE_SOLVED) {
            return 0;
        } else if (arr == STATE_RANDOM) {
            return parity == -1 ? gen.nextInt(2) : parity;
        }
        byte[] val = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != -1) {
                val[arr[i]] = -1;
            }
        }
        int idx = 0;
        for (int i = 0; i < arr.length; i++) {
            if (val[i] != -1) {
                int j = gen.nextInt(idx + 1);
                byte temp = val[i];
                val[idx++] = val[j];
                val[j] = temp;
            }
        }
        int last = -1;
        for (idx = 0; idx < arr.length && cntU > 0; idx++) {
            if (arr[idx] == -1) {
                if (cntU == 2) {
                    last = idx;
                }
                arr[idx] = val[--cntU];
            }
        }
        int p = Util.getNParity(Util.getNPerm(arr, arr.length), arr.length);
        if (p == 1 - parity && last != -1) {
            int temp = arr[idx - 1];
            arr[idx - 1] = arr[last];
            arr[last] = temp;
        }
        return p;
    }

    public static final int[] STATE_RANDOM = null;
    public static final int[] STATE_SOLVED = new int[0];

    public static String randomState(int[] cp, int[] co, int[] ep, int[] eo) {
        int parity;
        int cntUE = ep == STATE_RANDOM ? 12 : countUnknown(ep);
        int cntUC = cp == STATE_RANDOM ? 8 : countUnknown(cp);
        int cpVal, epVal;
        if (cntUE < 2) {    //ep != STATE_RANDOM
            if (ep == STATE_SOLVED) {
                epVal = parity = 0;
            } else {
                parity = resolvePerm(ep, cntUE, -1);
                epVal = Util.getNPerm(ep, 12);
            }
            if (cp == STATE_SOLVED) {
                cpVal = 0;
            } else if (cp == STATE_RANDOM) {
                do {
                    cpVal = gen.nextInt(40320);
                } while (Util.getNParity(cpVal, 8) != parity);
            } else {
                resolvePerm(cp, cntUC, parity);
                cpVal = Util.getNPerm(cp, 8);
            }
        } else {    //ep != STATE_SOLVED
            if (cp == STATE_SOLVED) {
                cpVal = parity = 0;
            } else if (cp == STATE_RANDOM) {
                cpVal = gen.nextInt(40320);
                parity = Util.getNParity(cpVal, 8);
            } else {
                parity = resolvePerm(cp, cntUC, -1);
                cpVal = Util.getNPerm(cp, 8);
            }
            if (ep == STATE_RANDOM) {
                do {
                    epVal = gen.nextInt(479001600);
                } while (Util.getNParity(epVal, 12) != parity);
            } else {
                resolvePerm(ep, cntUE, parity);
                epVal = Util.getNPerm(ep, 12);
            }
        }
        return Util.toFaceCube(
                new CubieCube(
                        cpVal,
                        co == STATE_RANDOM ? gen.nextInt(2187) : (co == STATE_SOLVED ? 0 : resolveOri(co, 3)),
                        epVal,
                        eo == STATE_RANDOM ? gen.nextInt(2048) : (eo == STATE_SOLVED ? 0 : resolveOri(eo, 2))));
    }


    public static String randomLastLayer() {
        return randomState(
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0 },
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0, 0, 0,  0,  0 });
    }

    public static String randomPLL() {
        return randomState(
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7 },
                STATE_SOLVED,
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11 },
                STATE_SOLVED);
    }

    public static String randomLastSlot() {
        return randomState(
                new int[] { -1, -1, -1, -1, -1, 5, 6, 7 },
                new int[] { -1, -1, -1, -1, -1, 0, 0, 0 },
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7, -1, 9, 10, 11 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0, -1, 0,  0,  0 });
    }

    public static String randomZBLastLayer() {
        return randomState(
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0 },
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11 },
                STATE_SOLVED);
    }

    public static String randomZBLastSlot() {
        return randomState(
                new int[] { -1, -1, -1, -1, -1, 5, 6, 7 },
                new int[] { -1, -1, -1, -1, -1, 0, 0, 0 },
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7, -1, 9, 10, 11 },
                STATE_SOLVED);
    }

    public static String randomZZLastLayer() {
        int auf = gen.nextInt(4);
        return randomState(
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0 },
                auf == 0 ? new int[] { -1, 1, -1, 3, 4, 5, 6, 7, 8, 9, 10, 11 } :
                    auf == 1 ? new int[] { -1, 3, -1, 1, 4, 5, 6, 7, 8, 9, 10, 11 } :
                    auf == 2 ? new int[] { 1, -1, 3, -1, 4, 5, 6, 7, 8, 9, 10, 11 } :
                    new int[] { 3, -1, 1, -1, 4, 5, 6, 7, 8, 9, 10, 11 },
                STATE_SOLVED);
    }

    public static String randomCornerOfLastLayer() {
        return randomState(
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0 },
                STATE_SOLVED, STATE_SOLVED);
    }

    public static String randomEdgeOfLastLayer() {
        return randomState(STATE_SOLVED, STATE_SOLVED,
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0 });
    }

    public static String randomCrossSolved() {
        return randomState(STATE_RANDOM, STATE_RANDOM,
                new int[] { -1, -1, -1, -1, 4, 5, 6, 7, -1, -1, -1, -1 },
                new int[] { -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1 });
    }

    public static String random3Corner() {
        int[] c = new int[8];
        Utils.idxToComb(c, gen.nextInt(56), 3, 8);
        int[] cp = new int[8], co = new int[8];
        for (int i = 0; i < 8; i++) {
            if (c[i] != 0) co[i] = cp[i] = -1;
            else {
                cp[i] = i;
                co[i] = 0;
            }
        }
        return randomState(cp, co, STATE_SOLVED, STATE_SOLVED);
    }

    public static String random3Edge() {
        int[] e = new int[12];
        Utils.idxToComb(e, gen.nextInt(220), 3, 12);
        int[] ep = new int[12], eo = new int[12];
        for (int i = 0; i < 12; i++) {
            if (e[i] != 0) eo[i] = ep[i] = -1;
            else {
                ep[i] = i;
                eo[i] = 0;
            }
        }
        return randomState(STATE_SOLVED, STATE_SOLVED, ep, eo);
    }

    public static String randomEdgeSolved() {
        return randomState(STATE_RANDOM, STATE_RANDOM, STATE_SOLVED, STATE_SOLVED);
    }

    public static String randomCornerSolved() {
        return randomState(STATE_SOLVED, STATE_SOLVED, STATE_RANDOM, STATE_RANDOM);
    }

    public static String randomEdgePerm() {
        return randomState(STATE_SOLVED, STATE_SOLVED, STATE_RANDOM, STATE_SOLVED);
    }

    public static String randomEdgeOri() {
        return randomState(STATE_SOLVED, STATE_SOLVED, STATE_SOLVED, STATE_RANDOM);
    }

    public static String randomCornerPerm() {
        return randomState(STATE_RANDOM, STATE_SOLVED, STATE_SOLVED, STATE_SOLVED);
    }

    public static String randomCornerOri() {
        return randomState(STATE_SOLVED, STATE_RANDOM, STATE_SOLVED, STATE_SOLVED);
    }

    public static String randomPermutation() {
        return randomState(STATE_RANDOM, STATE_SOLVED, STATE_RANDOM, STATE_SOLVED);
    }

    public static String randomOrientation() {
        return randomState(STATE_SOLVED, STATE_RANDOM, STATE_SOLVED, STATE_RANDOM);
    }

    public static String superFlip() {
        return Util.toFaceCube(new CubieCube(0, 0, 0, 2047));
    }

    public static String randomEasyCross(int depth) {
        int[][] e = Cross.easyCross(depth);
        return randomState(STATE_RANDOM, STATE_RANDOM, e[0], e[1]);
    }


    public static String fromScramble(int[] scramble) {
        CubieCube c1 = new CubieCube();
        CubieCube c2 = new CubieCube();
        CubieCube tmp;
        for (int i = 0; i < scramble.length; i++) {
            CubieCube.CornMult(c1, CubieCube.moveCube[scramble[i]], c2);
            CubieCube.EdgeMult(c1, CubieCube.moveCube[scramble[i]], c2);
            tmp = c1; c1 = c2; c2 = tmp;
        }
        return Util.toFaceCube(c1);
    }

    public static String fromScramble(String s) {
        int[] arr = new int[s.length()];
        int j = 0;
        int axis = -1;
        for (int i = 0, length = s.length(); i < length; i++) {
            switch (s.charAt(i)) {
                case 'U':   axis = 0;   break;
                case 'R':   axis = 3;   break;
                case 'F':   axis = 6;   break;
                case 'D':   axis = 9;   break;
                case 'L':   axis = 12;  break;
                case 'B':   axis = 15;  break;
                case ' ':
                    if (axis != -1) {
                        arr[j++] = axis;
                    }
                    axis = -1;
                    break;
                case '2':   axis++; break;
                case '\'':  axis += 2; break;
                default:    continue;
            }

        }
        if (axis != -1) arr[j++] = axis;
        int[] ret = new int[j];
        while (--j >= 0) {
            ret[j] = arr[j];
        }
        return fromScramble(ret);
    }

    /**
     * Check whether the cube definition string s represents a solvable cube.
     *
     * @param facelets is the cube definition string , see {@link cs.min2phase.Search#solution(java.lang.String facelets, int maxDepth, long timeOut, long timeMin, int verbose)}
     * @return 0: Cube is solvable<br>
     *         -1: There is not exactly one facelet of each colour<br>
     *         -2: Not all 12 edges exist exactly once<br>
     *         -3: Flip error: One edge has to be flipped<br>
     *         -4: Not all 8 corners exist exactly once<br>
     *         -5: Twist error: One corner has to be twisted<br>
     *         -6: Parity error: Two corners or two edges have to be exchanged
     */
    public static int verify(String facelets) {
        return Util.toCubieCube(facelets, new CubieCube());
    }
}
