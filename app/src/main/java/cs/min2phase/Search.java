/**
 Copyright (C) 2012  Shuang Chen

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cs.min2phase;

import java.util.Random;

/**
 * Rubik's Cube Solver.<br>
 * A much faster and smaller implemention of Two-Phase Algorithm.<br>
 * Symmetry is used to reduce memory used.<br>
 * Total Memory used is about 1MB.<br>
 * @author Shuang Chen
 */
public class Search {

    static final boolean USE_TWIST_FLIP_PRUN = false;
    static final boolean USE_FULL_PRUN = false;

    //Options for research purpose.
    static final boolean TRY_PRE_MOVE = true;
    static final boolean TRY_INVERSE = true;
    static final boolean TRY_THREE_AXES = true;

    private static final int PRE_IDX_MAX = TRY_PRE_MOVE ? 9 : 1;

    static boolean inited = false;

    private int[] move = new int[31];

    private int[][] twist = new int[6][PRE_IDX_MAX];
    private int[][] flip = new int[6][PRE_IDX_MAX];
    private int[][] slice = new int[6][PRE_IDX_MAX];

    private int[][] corn0 = new int[6][PRE_IDX_MAX];
    private int[][] ud8e0 = new int[6][PRE_IDX_MAX];
    private int[][] prun = new int[6][PRE_IDX_MAX];


    private byte[] f = new byte[54];

    private int conjMask;
    private int urfIdx;
    private int preIdx;
    private int length1;
    private int depth1;
    private int maxDep2;
    private int sol;
    private String solution;
    private long probe;
    private long probeMax;
    private long probeMin;
    private int verbose;
    private CubieCube cc = new CubieCube();

    private boolean isRecovery = false;

    /**
     *     Verbose_Mask determines if a " . " separates the phase1 and phase2 parts of the solver string like in F' R B R L2 F .
     *     U2 U D for example.<br>
     */
    public static final int USE_SEPARATOR = 0x1;

    /**
     *     Verbose_Mask determines if the solution will be inversed to a scramble/state generator.
     */
    public static final int INVERSE_SOLUTION = 0x2;

    /**
     *     Verbose_Mask determines if a tag such as "(21f)" will be appended to the solution.
     */
    public static final int APPEND_LENGTH = 0x4;

    /**
     *     Verbose_Mask determines if guaranteeing the solution to be optimal.
     */
    public static final int OPTIMAL_SOLUTION = 0x8;

    public static String[] rotateStr = {"", "Fw", "Fw'", "Fw Uw", "Fw Uw2", "Fw Uw'", "Fw' Uw", "Fw' Uw2", "Fw' Uw'", "Rw", "Rw2", "Rw'",
            "Rw Uw", "Rw Uw2", "Rw Uw'", "Rw2 Uw", "Rw2 Uw2", "Rw2 Uw'", "Rw' Uw", "Rw' Uw2", "Rw' Uw'", "Uw", "Uw2", "Uw'"};
    public static char[] rotateIdx = {0, 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'D', 'D', 'D'};

    public synchronized String solution(String facelets, boolean rotate, Random r) {
        String sol = solution(facelets, 21, 10000, 0, 2);
        if (!rotate) return sol;
        String[] moves = sol.split(" ");
        char lastMove = moves[moves.length - 1].charAt(0);
        int rot;
        do {
            rot = r.nextInt(24);
        } while (rotateIdx[rot] == lastMove);
        return sol + rotateStr[rot];
    }

    /**
     * Computes the solver string for a given cube.
     *
     * @param facelets
     *      is the cube definition string format.<br>
     * The names of the facelet positions of the cube:
     * <pre>
     *             |************|
     *             |*U1**U2**U3*|
     *             |************|
     *             |*U4**U5**U6*|
     *             |************|
     *             |*U7**U8**U9*|
     *             |************|
     * ************|************|************|************|
     * *L1**L2**L3*|*F1**F2**F3*|*R1**R2**F3*|*B1**B2**B3*|
     * ************|************|************|************|
     * *L4**L5**L6*|*F4**F5**F6*|*R4**R5**R6*|*B4**B5**B6*|
     * ************|************|************|************|
     * *L7**L8**L9*|*F7**F8**F9*|*R7**R8**R9*|*B7**B8**B9*|
     * ************|************|************|************|
     *             |************|
     *             |*D1**D2**D3*|
     *             |************|
     *             |*D4**D5**D6*|
     *             |************|
     *             |*D7**D8**D9*|
     *             |************|
     * </pre>
     * A cube definition string "UBL..." means for example: In position U1 we have the U-color, in position U2 we have the
     * B-color, in position U3 we have the L color etc. according to the order U1, U2, U3, U4, U5, U6, U7, U8, U9, R1, R2,
     * R3, R4, R5, R6, R7, R8, R9, F1, F2, F3, F4, F5, F6, F7, F8, F9, D1, D2, D3, D4, D5, D6, D7, D8, D9, L1, L2, L3, L4,
     * L5, L6, L7, L8, L9, B1, B2, B3, B4, B5, B6, B7, B8, B9 of the enum constants.
     *
     * @param maxDepth
     *      defines the maximal allowed maneuver length. For random cubes, a maxDepth of 21 usually will return a
     *      solution in less than 0.02 seconds on average. With a maxDepth of 20 it takes about 0.1 seconds on average to find a
     *      solution, but it may take much longer for specific cubes.
     *
     * @param probeMax
     *      defines the maximum number of the probes of phase 2. If it does not return with a solution, it returns with
     *      an error code.
     *
     * @param probeMin
     *      defines the minimum number of the probes of phase 2. So, if a solution is found within given probes, the
     *      computing will continue to find shorter solution(s). Btw, if probeMin > probeMax, probeMin will be set to probeMax.
     *
     * @param verbose
     *      determins the format of the solution(s). see USE_SEPARATOR, INVERSE_SOLUTION, APPEND_LENGTH, OPTIMAL_SOLUTION
     *
     * @return The solution string or an error code:<br>
     *      Error 1: There is not exactly one facelet of each colour<br>
     *      Error 2: Not all 12 edges exist exactly once<br>
     *      Error 3: Flip error: One edge has to be flipped<br>
     *      Error 4: Not all corners exist exactly once<br>
     *      Error 5: Twist error: One corner has to be twisted<br>
     *      Error 6: Parity error: Two corners or two edges have to be exchanged<br>
     *      Error 7: No solution exists for the given maxDepth<br>
     *      Error 8: Probe limit exceeded, no solution within given probMax
     */
    public synchronized String solution(String facelets, int maxDepth, long probeMax, long probeMin, int verbose) {
        int check = Util.toCubieCube(facelets, cc);
        if (check != 0) {
            return "Error " + Math.abs(check);
        }
        this.sol = maxDepth + 1;
        this.probe = 0;
        this.probeMax = probeMax;
        this.probeMin = Math.min(probeMin, probeMax);
        this.verbose = verbose;
        this.solution = null;
        this.isRecovery = false;

        Tools.init();

        initSearch();

        return (verbose & OPTIMAL_SOLUTION) == 0 ? search() : searchOpt();
    }

    public synchronized String solution(String facelects) {
        return solution(facelects, 21, 10000, 100, 2);
    }

    private void initSearch() {
        conjMask = (TRY_INVERSE ? 0 : 0x38) | (TRY_THREE_AXES ? 0 : 0x36);
        CubieCube pc = new CubieCube();

        for (int i = 0; i < 6; i++) {

            for (int j = 0; j < PRE_IDX_MAX; j++) {
                CubieCube.CornMult(CubieCube.preList[j], cc, pc);
                CubieCube.EdgeMult(CubieCube.preList[j], cc, pc);
                twist[i][j] = pc.getTwistSym();
                flip[i][j] = pc.getFlipSym();
                slice[i][j] = pc.getUDSlice();
                corn0[i][j] = pc.getCPermSym();
                ud8e0[i][j] = pc.getU4Comb() << 16 | pc.getD4Comb();
                prun[i][j] = -1;
            }

            cc.URFConjugate();
            if (i % 3 == 2) {
                cc.invCubieCube();
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < i; j++) { //If S_i^-1 * C * S_i == C, It's unnecessary to compute it again.
                if (twist[i][0] == twist[j][0] && flip[i][0] == flip[j][0] && slice[i][0] == slice[j][0]
                        && corn0[i][0] == corn0[j][0] && ud8e0[i][0] == ud8e0[j][0]) {
                    conjMask |= 1 << i;
                    break;
                }
            }
            if ((conjMask & (1 << i)) != 0) {
                continue;
            }
            for (int j = 0; j < PRE_IDX_MAX; j++) {
                fillprun(i, j);
            }
        }
    }

    public synchronized String next(long probeMax, long probeMin, int verbose) {
        this.probe = 0;
        this.probeMax = probeMax;
        this.probeMin = Math.min(probeMin, probeMax);
        this.solution = null;
        this.isRecovery = (this.verbose & OPTIMAL_SOLUTION) == (verbose & OPTIMAL_SOLUTION);
        this.verbose = verbose;
        return (verbose & OPTIMAL_SOLUTION) == 0 ? search() : searchOpt();
    }

    public static boolean isInited() {
        return inited;
    }

    public long numberOfProbes() {
        return probe;
    }

    public synchronized static void init() {
        if (inited) {
            return;
        }
        //CubieCube.initMove();
        CubieCube.initSym();
        CubieCube.initFlipSym2Raw();
        CubieCube.initTwistSym2Raw();
        CubieCube.initPermSym2Raw();

        CoordCube.initFlipMove();
        CoordCube.initTwistMove();
        CoordCube.initUDSliceMoveConj();

        CoordCube.initCPermMove();
        CoordCube.initEPermMove();
        CoordCube.initMPermMoveConj();
        CoordCube.initCombMoveConj();

        CoordCube.initMEPermPrun();
        CoordCube.initMCPermPrun();
        CoordCube.initPermCombPrun();

        if (USE_FULL_PRUN) {
            CubieCube.initUDSliceFlipSym2Raw();
            CoordCube.initUDSliceFlipMove();
            CoordCube.initTwistMoveConj();
            CoordCube.initUDSliceFlipTwistPrun();
        } else {
            if (USE_TWIST_FLIP_PRUN) {
                CoordCube.initTwistFlipPrun();
            }
            CoordCube.initSliceTwistPrun();
            CoordCube.initSliceFlipPrun();
        }

        inited = true;
    }

//    int verify(String facelets) {
//        int count = 0x000000;
//        try {
//            String center = new String(
//                    new char[] {
//                            facelets.charAt(4),
//                            facelets.charAt(13),
//                            facelets.charAt(22),
//                            facelets.charAt(31),
//                            facelets.charAt(40),
//                            facelets.charAt(49)
//                    }
//            );
//            for (int i = 0; i < 54; i++) {
//                f[i] = (byte) center.indexOf(facelets.charAt(i));
//                if (f[i] == -1) {
//                    return -1;
//                }
//                count += 1 << (f[i] << 2);
//            }
//        } catch (Exception e) {
//            return -1;
//        }
//        if (count != 0x999999) {
//            return -1;
//        }
//        Util.toCubieCube(f, cc);
//        return cc.verify();
//    }

    private void fillprun(int i, int j) {
        if (USE_FULL_PRUN) {
            prun[i][j] = CoordCube.getUDSliceFlipTwistPrun(twist[i][j] >> 3, twist[i][j] & 7, flip[i][j] >> 3, flip[i][j] & 7, slice[i][j] & 0x1ff);
        } else {
            prun[i][j] = Math.max(
                    Math.max(
                            CoordCube.getPruning(CoordCube.UDSliceTwistPrun,
                                    (twist[i][j] >> 3) * 495 + CoordCube.UDSliceConj[slice[i][j] & 0x1ff][twist[i][j] & 7]),
                            CoordCube.getPruning(CoordCube.UDSliceFlipPrun,
                                    (flip[i][j] >> 3) * 495 + CoordCube.UDSliceConj[slice[i][j] & 0x1ff][flip[i][j] & 7])),
                    USE_TWIST_FLIP_PRUN ? CoordCube.getPruning(CoordCube.TwistFlipPrun,
                            (twist[i][j] >> 3) << 11 | CubieCube.FlipS2RF[flip[i][j] & 0xfff8 | CubieCube.Sym8MultInv[flip[i][j] & 7][twist[i][j] & 7]]) : 0);
        }
    }

    private String search() {
        for (length1 = isRecovery ? length1 : 0; length1 < sol; length1++) {
            maxDep2 = Math.min(12, sol - length1);
            for (urfIdx = isRecovery ? urfIdx : 0; urfIdx < 6; urfIdx++) {
                if ((conjMask & (1 << urfIdx)) != 0) {
                    continue;
                }
                for (preIdx = isRecovery ? preIdx : 0; preIdx < PRE_IDX_MAX; preIdx++) {
                    if (preIdx != 0 && preIdx % 2 == 0) {
//                        assert (twist[urfIdx][preIdx] == twist[urfIdx][preIdx - 1]
//                                && flip[urfIdx][preIdx] == flip[urfIdx][preIdx - 1]
//                                && (slice[urfIdx][preIdx] & 0x1ff) == (slice[urfIdx][preIdx - 1] & 0x1ff));
                        continue;
                    }
                    depth1 = length1 - (preIdx == 0 ? 0 : 1);
                    if ((prun[urfIdx][preIdx] <= depth1) &&
                            phase1(twist[urfIdx][preIdx] >> 3, twist[urfIdx][preIdx] & 7,
                                    flip[urfIdx][preIdx] >> 3, flip[urfIdx][preIdx] & 7,
                                    slice[urfIdx][preIdx] & 0x1ff, prun[urfIdx][preIdx], depth1, -1) == 0) {
                        return solution == null ? "Error 8" : solution;
                    }
                }
            }
        }
        return solution == null ? "Error 7" : solution;
    }

    /**
     * @return
     *      0: Found or Probe limit exceeded
     *      1: Try Next Power
     *      2: Try Next Axis
     */
    private int phase1(int twist, int tsym, int flip, int fsym, int slice, int prun, int maxl, int lm) {
        if (twist == 0 && flip == 0 && slice == 0 && maxl < 5) {
            if (maxl == 0) {
                int ret = initPhase2();
                if (ret == 0 || preIdx == 0) {
                    return ret;
                }
                preIdx++;
                ret = Math.min(initPhase2(), ret);
                preIdx--;
                return ret;
            } else {
                return 1;
            }
        }

        for (int axis = 0; axis < 18; axis += 3) {
            if (axis == lm || axis == lm - 9 || (isRecovery && axis < move[depth1 - maxl] - 2)) {
                continue;
            }
            for (int power = 0; power < 3; power++) {
                int m = axis + power;

                if (isRecovery && m != move[depth1 - maxl]) {
                    continue;
                }

                int slicex = CoordCube.UDSliceMove[slice][m] & 0x1ff;

                int twistx = CoordCube.TwistMove[twist][CubieCube.Sym8Move[tsym][m]];
                int tsymx = CubieCube.Sym8Mult[twistx & 7][tsym];
                twistx >>= 3;

                int flipx = CoordCube.FlipMove[flip][CubieCube.Sym8Move[fsym][m]];
                int fsymx = CubieCube.Sym8Mult[flipx & 7][fsym];
                flipx >>= 3;

                int prunx;

                if (USE_FULL_PRUN) {
                    prunx = CoordCube.getUDSliceFlipTwistPrun(twistx, tsymx, flipx, fsymx, slicex, prun);
                    // prun = CoordCube.getUDSliceFlipTwistPrun(twistx, tsymx, flipx, fsymx, slicex);
                    if (prunx > maxl) {
                        break;
                    } else if (prunx == maxl) {
                        continue;
                    }
                } else {
                    if (USE_TWIST_FLIP_PRUN) {
                        prunx = CoordCube.getPruning(CoordCube.TwistFlipPrun,
                                twistx << 11 | CubieCube.FlipS2RF[flipx << 3 | CubieCube.Sym8MultInv[fsymx][tsymx]]);
                        if (prunx > maxl) {
                            break;
                        } else if (prunx == maxl) {
                            continue;
                        }
                    }

                    prunx = CoordCube.getPruning(CoordCube.UDSliceTwistPrun,
                            twistx * 495 + CoordCube.UDSliceConj[slicex][tsymx]);
                    if (prunx > maxl) {
                        break;
                    } else if (prunx == maxl) {
                        continue;
                    }

                    prunx = CoordCube.getPruning(CoordCube.UDSliceFlipPrun,
                            flipx * 495 + CoordCube.UDSliceConj[slicex][fsymx]);
                    if (prunx > maxl) {
                        break;
                    } else if (prunx == maxl) {
                        continue;
                    }
                }

                move[depth1 - maxl] = m;
                int ret = phase1(twistx, tsymx, flipx, fsymx, slicex, prunx, maxl - 1, axis);
                if (ret == 0) {
                    return 0;
                } else if (ret == 2) {
                    break;
                }
            }
        }
        return 1;
    }

    private String searchOpt() {
        int maxprun1 = 0;
        int maxprun2 = 0;
        for (int i = 0; i < 6; i++) {
            if (prun[i][0] == -1) {
                fillprun(i, 0);
            }
            if (i < 3) {
                maxprun1 = Math.max(maxprun1, prun[i][0]);
            } else {
                maxprun2 = Math.max(maxprun2, prun[i][0]);
            }
        }
        urfIdx = maxprun2 > maxprun1 ? 3 : 0;
        preIdx = 0;
        for (length1 = isRecovery ? length1 : 0; length1 < sol; length1++) {
            if (prun[0][0] <= length1 && prun[1][0] <= length1 && prun[2][0] <= length1 &&
                    phase1opt(twist[0 + urfIdx][0] >> 3, twist[0 + urfIdx][0] & 7, flip[0 + urfIdx][0] >> 3, flip[0 + urfIdx][0] & 7, slice[0 + urfIdx][0] & 0x1ff, prun[0 + urfIdx][0],
                            twist[1 + urfIdx][0] >> 3, twist[1 + urfIdx][0] & 7, flip[1 + urfIdx][0] >> 3, flip[1 + urfIdx][0] & 7, slice[1 + urfIdx][0] & 0x1ff, prun[1 + urfIdx][0],
                            twist[2 + urfIdx][0] >> 3, twist[2 + urfIdx][0] & 7, flip[2 + urfIdx][0] >> 3, flip[2 + urfIdx][0] & 7, slice[2 + urfIdx][0] & 0x1ff, prun[2 + urfIdx][0],
                            length1, -1) == 0) {
                return solution == null ? "Error 8" : solution;
            }
        }
        return solution == null ? "Error 7" : solution;
    }

    /**
     * @return
     *      0: Found or Probe limit exceeded
     *      1: Try Next Power
     *      2: Try Next Axis
     */
    private int phase1opt(
            int ud_twist, int ud_tsym, int ud_flip, int ud_fsym, int ud_slice, int ud_prun,
            int rl_twist, int rl_tsym, int rl_flip, int rl_fsym, int rl_slice, int rl_prun,
            int fb_twist, int fb_tsym, int fb_flip, int fb_fsym, int fb_slice, int fb_prun,
            int maxl, int lm) {

        if (ud_twist == 0 && ud_flip == 0 && ud_slice == 0 && maxl < 5) {
            maxDep2 = maxl + 1;
            depth1 = length1 - maxl;
            return initPhase2() == 0 ? 0 : 1;
        }

        for (int axis = 0; axis < 18; axis += 3) {
            if (axis == lm || axis == lm - 9 || (isRecovery && axis < move[length1 - maxl] - 2)) {
                continue;
            }
            for (int power = 0; power < 3; power++) {
                int m = axis + power;

                if (isRecovery && m != move[length1 - maxl]) {
                    continue;
                }

                // UD Axis
                int ud_slicex = CoordCube.UDSliceMove[ud_slice][m] & 0x1ff;
                int ud_twistx = CoordCube.TwistMove[ud_twist][CubieCube.Sym8Move[ud_tsym][m]];
                int ud_tsymx = CubieCube.Sym8Mult[ud_twistx & 7][ud_tsym];
                ud_twistx >>= 3;
                int ud_flipx = CoordCube.FlipMove[ud_flip][CubieCube.Sym8Move[ud_fsym][m]];
                int ud_fsymx = CubieCube.Sym8Mult[ud_flipx & 7][ud_fsym];
                ud_flipx >>= 3;

                int ud_prunx = 0;
                if (USE_FULL_PRUN) {
                    ud_prunx = CoordCube.getUDSliceFlipTwistPrun(ud_twistx, ud_tsymx, ud_flipx, ud_fsymx, ud_slicex, ud_prun);
                    if (ud_prunx > maxl) {
                        break;
                    } else if (ud_prunx == maxl) {
                        continue;
                    }
                } else {
                    if (USE_TWIST_FLIP_PRUN) {
                        ud_prunx = CoordCube.getPruning(CoordCube.TwistFlipPrun,
                                ud_twistx << 11 | CubieCube.FlipS2RF[ud_flipx << 3 | CubieCube.Sym8MultInv[ud_fsymx][ud_tsymx]]);
                        if (ud_prunx > maxl) {
                            break;
                        } else if (ud_prunx == maxl) {
                            continue;
                        }
                    }
                    ud_prunx = Math.max(ud_prunx, CoordCube.getPruning(CoordCube.UDSliceTwistPrun,
                            ud_twistx * 495 + CoordCube.UDSliceConj[ud_slicex][ud_tsymx]));
                    if (ud_prunx > maxl) {
                        break;
                    } else if (ud_prunx == maxl) {
                        continue;
                    }
                    ud_prunx = Math.max(ud_prunx, CoordCube.getPruning(CoordCube.UDSliceFlipPrun,
                            ud_flipx * 495 + CoordCube.UDSliceConj[ud_slicex][ud_fsymx]));
                    if (ud_prunx > maxl) {
                        break;
                    } else if (ud_prunx == maxl) {
                        continue;
                    }
                }

                // RL Axis
                m = CubieCube.urfMove[2][m];
                int rl_slicex = CoordCube.UDSliceMove[rl_slice][m] & 0x1ff;
                int rl_twistx = CoordCube.TwistMove[rl_twist][CubieCube.Sym8Move[rl_tsym][m]];
                int rl_tsymx = CubieCube.Sym8Mult[rl_twistx & 7][rl_tsym];
                rl_twistx >>= 3;
                int rl_flipx = CoordCube.FlipMove[rl_flip][CubieCube.Sym8Move[rl_fsym][m]];
                int rl_fsymx = CubieCube.Sym8Mult[rl_flipx & 7][rl_fsym];
                rl_flipx >>= 3;

                int rl_prunx = 0;
                if (USE_FULL_PRUN) {
                    rl_prunx = CoordCube.getUDSliceFlipTwistPrun(rl_twistx, rl_tsymx, rl_flipx, rl_fsymx, rl_slicex, rl_prun);
                    if (rl_prunx > maxl) {
                        break;
                    } else if (rl_prunx == maxl) {
                        continue;
                    }
                } else {
                    if (USE_TWIST_FLIP_PRUN) {
                        rl_prunx = CoordCube.getPruning(CoordCube.TwistFlipPrun,
                                rl_twistx << 11 | CubieCube.FlipS2RF[rl_flipx << 3 | CubieCube.Sym8MultInv[rl_fsymx][rl_tsymx]]);
                        if (rl_prunx > maxl) {
                            break;
                        } else if (rl_prunx == maxl) {
                            continue;
                        }
                    }
                    rl_prunx = Math.max(rl_prunx, CoordCube.getPruning(CoordCube.UDSliceTwistPrun,
                            rl_twistx * 495 + CoordCube.UDSliceConj[rl_slicex][rl_tsymx]));
                    if (rl_prunx > maxl) {
                        break;
                    } else if (rl_prunx == maxl) {
                        continue;
                    }
                    rl_prunx = Math.max(rl_prunx, CoordCube.getPruning(CoordCube.UDSliceFlipPrun,
                            rl_flipx * 495 + CoordCube.UDSliceConj[rl_slicex][rl_fsymx]));
                    if (rl_prunx > maxl) {
                        break;
                    } else if (rl_prunx == maxl) {
                        continue;
                    }
                }

                // FB Axis
                m = CubieCube.urfMove[2][m];
                int fb_slicex = CoordCube.UDSliceMove[fb_slice][m] & 0x1ff;
                int fb_twistx = CoordCube.TwistMove[fb_twist][CubieCube.Sym8Move[fb_tsym][m]];
                int fb_tsymx = CubieCube.Sym8Mult[fb_twistx & 7][fb_tsym];
                fb_twistx >>= 3;
                int fb_flipx = CoordCube.FlipMove[fb_flip][CubieCube.Sym8Move[fb_fsym][m]];
                int fb_fsymx = CubieCube.Sym8Mult[fb_flipx & 7][fb_fsym];
                fb_flipx >>= 3;

                int fb_prunx = 0;
                if (USE_FULL_PRUN) {
                    fb_prunx = CoordCube.getUDSliceFlipTwistPrun(fb_twistx, fb_tsymx, fb_flipx, fb_fsymx, fb_slicex, fb_prun);
                } else {
                    if (USE_TWIST_FLIP_PRUN) {
                        fb_prunx = CoordCube.getPruning(CoordCube.TwistFlipPrun,
                                fb_twistx << 11 | CubieCube.FlipS2RF[fb_flipx << 3 | CubieCube.Sym8MultInv[fb_fsymx][fb_tsymx]]);
                        if (fb_prunx > maxl) {
                            break;
                        } else if (fb_prunx == maxl) {
                            continue;
                        }
                    }
                    fb_prunx = Math.max(fb_prunx, CoordCube.getPruning(CoordCube.UDSliceTwistPrun,
                            fb_twistx * 495 + CoordCube.UDSliceConj[fb_slicex][fb_tsymx]));
                    if (fb_prunx > maxl) {
                        break;
                    } else if (fb_prunx == maxl) {
                        continue;
                    }
                    fb_prunx = Math.max(fb_prunx, CoordCube.getPruning(CoordCube.UDSliceFlipPrun,
                            fb_flipx * 495 + CoordCube.UDSliceConj[fb_slicex][fb_fsymx]));
                }
                int inc_prun = ud_prunx == rl_prunx && rl_prunx == fb_prunx && fb_prunx != 0 ? 1 : 0;

                if (fb_prunx + inc_prun > maxl) {
                    break;
                } else if (fb_prunx + inc_prun == maxl) {
                    continue;
                }

                m = CubieCube.urfMove[2][m];

                move[length1 - maxl] = m;
                int ret = phase1opt(
                        ud_twistx, ud_tsymx, ud_flipx, ud_fsymx, ud_slicex, ud_prunx,
                        rl_twistx, rl_tsymx, rl_flipx, rl_fsymx, rl_slicex, rl_prunx,
                        fb_twistx, fb_tsymx, fb_flipx, fb_fsymx, fb_slicex, fb_prunx,
                        maxl - 1, axis);
                if (ret == 0) {
                    return 0;
                } else if (ret == 2) {
                    break;
                }
            }
        }
        return 1;
    }

    /**
     * @return
     *      0: Found or Probe limit exceeded
     *      1: Try Next Power
     *      2: Try Next Axis
     */
    private int initPhase2() {
        isRecovery = false;
        if (probe >= (solution == null ? probeMax : probeMin)) {
            return 0;
        }
        ++probe;
        int cidx = corn0[urfIdx][preIdx] >> 4;
        int csym = corn0[urfIdx][preIdx] & 0xf;
        int mid = slice[urfIdx][preIdx];
        for (int i = 0; i < depth1; i++) {
            int m = move[i];
            cidx = CoordCube.CPermMove[cidx][CubieCube.SymMove[csym][m]];
            csym = CubieCube.SymMult[cidx & 0xf][csym];
            cidx >>= 4;

            int cx = CoordCube.UDSliceMove[mid & 0x1ff][m];
            mid = Util.permMult[mid >> 9][cx >> 9] << 9 | cx & 0x1ff;
        }
        mid >>= 9;
        int prun = CoordCube.getPruning(CoordCube.MCPermPrun, cidx * 24 + CoordCube.MPermConj[mid][csym]);
        if (prun >= maxDep2) {
            return prun > maxDep2 ? 2 : 1;
        }

        int u4e = ud8e0[urfIdx][preIdx] >> 16;
        int d4e = ud8e0[urfIdx][preIdx] & 0xffff;
        for (int i = 0; i < depth1; i++) {
            int m = move[i];

            int cx = CoordCube.UDSliceMove[u4e & 0x1ff][m];
            u4e = Util.permMult[u4e >> 9][cx >> 9] << 9 | cx & 0x1ff;

            cx = CoordCube.UDSliceMove[d4e & 0x1ff][m];
            d4e = Util.permMult[d4e >> 9][cx >> 9] << 9 | cx & 0x1ff;
        }

        int edge = CubieCube.MtoEPerm[494 - (u4e & 0x1ff) + (u4e >> 9) * 70 + (d4e >> 9) * 1680];
        int esym = edge & 0xf;
        edge >>= 4;

        prun = Math.max(CoordCube.getPruning(CoordCube.MEPermPrun, edge * 24 + CoordCube.MPermConj[mid][esym]), prun);
        if (prun >= maxDep2) {
            return prun > maxDep2 ? 2 : 1;
        }

        int lm = 10;
        if (depth1 >= 2 && move[depth1 - 1] / 3 % 3 == move[depth1 - 2] / 3 % 3) {
            lm = Util.std2ud[Math.max(move[depth1 - 1], move[depth1 - 2]) / 3 * 3 + 1];
        } else if (depth1 >= 1) {
            lm = Util.std2ud[move[depth1 - 1] / 3 * 3 + 1];
            if (move[depth1 - 1] > Util.Fx3) {
                lm = -lm;
            }
        }

        int depth2;
        for (depth2 = maxDep2 - 1; depth2 >= prun; depth2--) {
            int ret = phase2(edge, esym, cidx, csym, mid, depth2, depth1, lm);
            if (ret < 0) {
                break;
            }
            depth2 = depth2 - ret;
            sol = depth1 + depth2;
            if (preIdx != 0) {
                //assert depth2 > 0; //If depth2 == 0, the solution is optimal. In this case, we won't try preScramble to find shorter solutions.
                int axisPre = Util.preMove[preIdx] / 3;
                int axisLast = move[sol - 1] / 3;
                if (axisPre == axisLast) {
                    int pow = (Util.preMove[preIdx] % 3 + move[sol - 1] % 3 + 1) % 4;
                    move[sol - 1] = axisPre * 3 + pow;
                } else if (depth2 > 1 &&
                        axisPre % 3 == axisLast % 3 &&
                        move[sol - 2] / 3 == axisPre) {
                    int pow = (Util.preMove[preIdx] % 3 + move[sol - 2] % 3 + 1) % 4;
                    move[sol - 2] = axisPre * 3 + pow;
                } else {
                    move[sol++] = Util.preMove[preIdx];
                }
            }
            solution = solutionToString();
        }

        if (depth2 != maxDep2 - 1) { //At least one solution has been found.
            maxDep2 = Math.min(12, sol - length1);
            return probe >= probeMin ? 0 : 1;
        } else {
            return 1;
        }
    }

    //-1: no solution found
    // X: solution with X moves shorter than expectation. Hence, the length of the solution is  depth - X
    private int phase2(int eidx, int esym, int cidx, int csym, int mid, int maxl, int depth, int lm) {
        if (eidx == 0 && cidx == 0 && mid == 0) {
            return maxl;
        }
        for (int m = 0; m < 10; m++) {
            if (lm < 0 ? (m == -lm) : Util.ckmv2[lm][m]) {
                continue;
            }
            int midx = CoordCube.MPermMove[mid][m];
            int cidxx = CoordCube.CPermMove[cidx][CubieCube.SymMove[csym][Util.ud2std[m]]];
            int csymx = CubieCube.SymMult[cidxx & 0xf][csym];
            cidxx >>= 4;
            if (CoordCube.getPruning(CoordCube.MCPermPrun,
                    cidxx * 24 + CoordCube.MPermConj[midx][csymx]) >= maxl) {
                continue;
            }
            int eidxx = CoordCube.EPermMove[eidx][CubieCube.SymMoveUD[esym][m]];
            int esymx = CubieCube.SymMult[eidxx & 0xf][esym];
            eidxx >>= 4;

            // if (CoordCube.getPruning(CoordCube.CPermECombPrun,
            //                          cidxx * 70 + CoordCube.ECombConj[CubieCube.Perm2Comb[eidxx]][CubieCube.SymMultInv[csymx][esymx]]) >= maxl) {
            //     continue;
            // }
            if (CoordCube.getPruning(CoordCube.EPermCCombPrun,
                    eidxx * 70 + CoordCube.CCombConj[CubieCube.Perm2Comb[cidxx]][CubieCube.SymMultInv[esymx][csymx]]) >= maxl) {
                continue;
            }
            if (CoordCube.getPruning(CoordCube.MEPermPrun,
                    eidxx * 24 + CoordCube.MPermConj[midx][esymx]) >= maxl) {
                continue;
            }
            int ret = phase2(eidxx, esymx, cidxx, csymx, midx, maxl - 1, depth + 1, (lm < 0 && m + lm == -5) ? -lm : m);
            if (ret >= 0) {
                move[depth] = Util.ud2std[m];
                return ret;
            }
        }
        return -1;
    }

    private String solutionToString() {
        StringBuilder sb = new StringBuilder();
        int urf = (verbose & INVERSE_SOLUTION) != 0 ? (urfIdx + 3) % 6 : urfIdx;
        if (urf < 3) {
            for (int s = 0; s < sol; s++) {
                if ((verbose & USE_SEPARATOR) != 0 && s == depth1) {
                    sb.append(".  ");
                }
                sb.append(Util.move2str[CubieCube.urfMove[urf][move[s]]]).append(' ');
            }
        } else {
            for (int s = sol - 1; s >= 0; s--) {
                sb.append(Util.move2str[CubieCube.urfMove[urf][move[s]]]).append(' ');
                if ((verbose & USE_SEPARATOR) != 0 && s == depth1) {
                    sb.append(".  ");
                }
            }
        }
        if ((verbose & APPEND_LENGTH) != 0) {
            sb.append("(").append(sol).append("f)");
        }
        return sb.toString();
    }
}
