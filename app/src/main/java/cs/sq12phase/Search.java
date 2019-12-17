package cs.sq12phase;

import java.io.*;
import java.util.Random;

import com.dctimer.APP;

import solver.Utils;

import static solver.Utils.read;
import static solver.Utils.write;

public class Search {
    private int[] move = new int[50];
    private FullCube c = null;
    private FullCube d = new FullCube("");
    private int length1;
    private int maxlen2;
    private String sol_string;

//	static int getNParity(int idx, int n) {
//		int p = 0;
//		for (int i=n-2; i>=0; i--) {
//			p ^= idx % (n-i);
//			idx /= (n-i);
//		}
//		return p & 1;
//	}

    static {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(APP.dataPath + "sq1.dat"));
            read(Shape.ShapeIdx, in);
            in.read(Shape.ShapePrun);
            in.read(Shape.ShapePrunOpt);
            read(Shape.spTopMove, in);
            read(Shape.spBottomMove, in);
            read(Shape.spTwistMove, in);
            read(Square.sqTwistMove, in);
            read(Square.sqTopMove, in);
            read(Square.sqBottomMove, in);
            in.read(Square.SquarePrun);
            in.close();
        } catch (Exception e) {
            Shape.init();
            Square.init();
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(APP.dataPath + "sq1.dat"));
                write(Shape.ShapeIdx, out);
                out.write(Shape.ShapePrun);
                out.write(Shape.ShapePrunOpt);
                write(Shape.spTopMove, out);
                write(Shape.spBottomMove, out);
                write(Shape.spTwistMove, out);
                write(Square.sqTwistMove, out);
                write(Square.sqTopMove, out);
                write(Square.sqBottomMove, out);
                out.write(Square.SquarePrun);
                out.close();
            } catch (Exception e1) { e.printStackTrace(); }
        }
    }

    private String solution(FullCube c) {
        this.c = c;
        sol_string = null;
        int shape = c.getShapeIdx();
        for (length1 = Shape.ShapePrun[shape]; length1 < 50; length1++) {
            maxlen2 = Math.min(33 - length1, 17);
            if (phase1(shape, Shape.ShapePrun[shape], length1, 0, -1)) {
                break;
            }
        }
        return sol_string;
    }

    public String scramble() {
        String scr;
        do {
            scr = solution(FullCube.randomCube());
        } while (scr.length() < 4);
        return scr;
    }

    public String scramble(int shape) {
        String scr;
        do {
            scr = solution(FullCube.randomCube(shape));
        } while (scr.length() < 4);
        return scr;
    }

    public String scramblePLL(Random r) {
        String scr;
        do {
            int[] corner = {7, 6, 5, 4, 3, 2, 1, 0};
            //Log.w("dct", "corner "+Util.get8Perm(corner));
            for (int i = 0; i < 4; i++) {
                int x = i + r.nextInt(4 - i);
                if (x != i) Utils.swap(corner, i, x);
                x = i + 4 + r.nextInt(4 - i);
                if (x != i + 4) Utils.swap(corner, i + 4, x);
            }
            int[] edge = {7, 6, 5, 4, 3, 2, 1, 0};
            for (int i = 0; i < 4; i++) {
                int x = i + r.nextInt(4 - i);
                if (x != i) Utils.swap(edge, i, x);
                x = i + 4 + r.nextInt(4 - i);
                if (x != i + 4) Utils.swap(edge, i + 4, x);
            }
            scr = solution(FullCube.randomCube(1037, Utils.get8Perm(corner, 8), Utils.get8Perm(edge, 8)));
        } while (scr.length() < 4);
        return scr;
    }

    public String scrambleWCA() {
        String sol;
        do {
            FullCube c = FullCube.randomCube();
            sol = solutionOpt(c, 11);
        } while (sol != null);
        return solution(c);
    }

    private  String solutionOpt(FullCube c, int maxl) {
        this.c = c;
        int shape = c.getShapeIdx();
        for (length1 = Shape.ShapePrunOpt[shape]; length1 <= maxl; length1++) {
            //Log.w("dct", "sq "+length1);
            if (phase1Opt(shape, length1, 0, -1)) {
                return sol_string;
            }
        }
        return null;
    }

    private boolean phase1Opt(int shape, int maxl, int depth, int lm) {
        if (maxl == 0) {
            return isSolvedInPhase1();
        }
        //try each possible move. First twist;
        if (lm != 0) {
            int shapex = Shape.spTwistMove[shape];
            int prunx = Shape.ShapePrunOpt[shapex];
            if (prunx < maxl) {
                move[depth] = 0;
                if (phase1Opt(shapex, maxl - 1, depth + 1, 0))
                    return true;
            }
        }
        //Try top layer
        int shapex = shape;
        if (lm <= 0) {
            int m = 0;
            while (true) {
                m += Shape.spTopMove[shapex];
                shapex = m >> 4;
                m &= 0x0f;
                if (m >= 12)
                    break;
                int prunx = Shape.ShapePrunOpt[shapex];
                if (prunx > maxl) {
                    break;
                } else if (prunx < maxl) {
                    move[depth] = m;
                    if (phase1Opt(shapex, maxl - 1, depth + 1, 1))
                        return true;
                }
            }
        }
        shapex = shape;
        //Try bottom layer
        if (lm <= 1) {
            int m = 0;
            while (true) {
                m += Shape.spBottomMove[shapex];
                shapex = m >> 4;
                m &= 0x0f;
                if (m >= 6)
                    break;
                int prunx = Shape.ShapePrunOpt[shapex];
                if (prunx > maxl) {
                    break;
                } else if (prunx < maxl) {
                    move[depth] = -m;
                    if (phase1Opt(shapex, maxl - 1, depth + 1, 2))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean isSolvedInPhase1() {
        d.copy(c);
        for (int i = 0; i < length1; i++)
            d.doMove(move[i]);
        boolean isSolved = d.ul == 0x011233 && d.ur == 0x455677 && d.dl == 0x998bba && d.dr == 0xddcffe && d.ml == 0;
        if (isSolved)
            sol_string = move2string(length1);
        return isSolved;
    }

    private boolean phase1(int shape, int prunvalue, int maxl, int depth, int lm) {
        if (prunvalue == 0 && maxl < 4) {
            return maxl == 0 && init2();
        }

        //try each possible move. First twist;
        if (lm != 0) {
            int shapex = Shape.spTwistMove[shape];
            int prunx = Shape.ShapePrun[shapex];
            if (prunx < maxl) {
                move[depth] = 0;
                if (phase1(shapex, prunx, maxl - 1, depth + 1, 0)) {
                    return true;
                }
            }
        }

        //Try top layer
        int shapex = shape;
        if (lm <= 0) {
            int m = 0;
            while (true) {
                m += Shape.spTopMove[shapex];
                shapex = m >> 4;
                m &= 0x0f;
                if (m >= 12) {
                    break;
                }
                int prunx = Shape.ShapePrun[shapex];
                if (prunx > maxl) {
                    break;
                } else if (prunx < maxl) {
                    move[depth] = m;
                    if (phase1(shapex, prunx, maxl - 1, depth + 1, 1)) {
                        return true;
                    }
                }
            }
        }

        shapex = shape;
        //Try bottom layer
        if (lm <= 1) {
            int m = 0;
            while (true) {
                m += Shape.spBottomMove[shapex];
                shapex = m >> 4;
                m &= 0x0f;
                if (m >= 6) {
                    break;
                }
                int prunx = Shape.ShapePrun[shapex];
                if (prunx > maxl) {
                    break;
                } else if (prunx < maxl) {
                    move[depth] = -m;
                    if (phase1(shapex, prunx, maxl - 1, depth + 1, 2)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Square sq = new Square();


    private boolean init2() {
        d.copy(c);
        for (int i = 0; i < length1; i++) {
            d.doMove(move[i]);
        }
        d.getSquare(sq);

        int edge = sq.edgeperm;
        int corner = sq.cornperm;
        int ml = sq.ml;

        int prun = Math.max(Square.SquarePrun[sq.edgeperm << 1 | ml], Square.SquarePrun[sq.cornperm << 1 | ml]);

        for (int i = prun; i < maxlen2; i++) {
            if (phase2(edge, corner, sq.topEdgeFirst, sq.botEdgeFirst, ml, i, length1, 0)) {
                sol_string = move2string(i + length1);
                return true;
            }
        }

        return false;
    }

    //int pruncomb[] = new int[100];

    private String move2string(int len) {
        //TODO whether to invert the solution or not should be set by params.
        StringBuilder s = new StringBuilder();
        int top = 0, bottom = 0;
        for (int i = len - 1; i >= 0; i--) {
            int val = move[i];
            if (val > 0) {
                val = 12 - val;
                top = (val > 6) ? (val-12) : val;
            } else if (val < 0) {
                val = 12 + val;
                bottom = (val > 6) ? (val-12) : val;
            } else {
                if (top == 0 && bottom == 0) {
                    s.append(" / ");
                } else {
                    s.append('(').append(top).append(",").append(bottom).append(") / ");
                }
                top = bottom = 0;
            }
        }
        if (top != 0 || bottom != 0) {
            s.append('(').append(top).append(",").append(bottom).append(")");
        }
        return s.toString();// + " (" + len + "t)";
    }

    private boolean phase2(int edge, int corner, boolean topEdgeFirst, boolean botEdgeFirst, int ml, int maxl, int depth, int lm) {
        if (maxl == 0 && !topEdgeFirst && botEdgeFirst/*edge==0 && corner==0 && !topEdgeFirst && botEdgeFirst && ml==0*/) {
            return true;
        }

        //try each possible move. First twist;
        if (lm != 0 && topEdgeFirst == botEdgeFirst) {
            int edgex = Square.sqTwistMove[edge];
            int cornerx = Square.sqTwistMove[corner];

            if (Square.SquarePrun[edgex << 1 | (1 - ml)] < maxl && Square.SquarePrun[cornerx << 1 | (1 - ml)] < maxl) {
                move[depth] = 0;
                if (phase2(edgex, cornerx, topEdgeFirst, botEdgeFirst, 1 - ml, maxl - 1, depth + 1, 0)) {
                    return true;
                }
            }
        }

        //Try top layer
        if (lm <= 0) {
            boolean topEdgeFirstx = !topEdgeFirst;
            int edgex = topEdgeFirstx ? Square.sqTopMove[edge] : edge;
            int cornerx = topEdgeFirstx ? corner : Square.sqTopMove[corner];
            int m = topEdgeFirstx ? 1 : 2;
            int prun1 = Square.SquarePrun[edgex << 1 | ml];
            int prun2 = Square.SquarePrun[cornerx << 1 | ml];
            while (m < 12 && prun1 <= maxl && prun1 <= maxl) {
                if (prun1 < maxl && prun2 < maxl) {
                    move[depth] = m;
                    if (phase2(edgex, cornerx, topEdgeFirstx, botEdgeFirst, ml, maxl - 1, depth + 1, 1)) {
                        return true;
                    }
                }
                topEdgeFirstx = !topEdgeFirstx;
                if (topEdgeFirstx) {
                    edgex = Square.sqTopMove[edgex];
                    prun1 = Square.SquarePrun[edgex << 1 | ml];
                    m += 1;
                } else {
                    cornerx = Square.sqTopMove[cornerx];
                    prun2 = Square.SquarePrun[cornerx << 1 | ml];
                    m += 2;
                }
            }
        }

        if (lm <= 1) {
            boolean botEdgeFirstx = !botEdgeFirst;
            int edgex = botEdgeFirstx ? Square.sqBottomMove[edge] : edge;
            int cornerx = botEdgeFirstx ? corner : Square.sqBottomMove[corner];
            int m = botEdgeFirstx ? 1 : 2;
            int prun1 = Square.SquarePrun[edgex << 1 | ml];
            int prun2 = Square.SquarePrun[cornerx << 1 | ml];
            while (m < (maxl > 6 ? 6 : 12) && prun1 <= maxl && prun1 <= maxl) {
                if (prun1 < maxl && prun2 < maxl) {
                    move[depth] = -m;
                    if (phase2(edgex, cornerx, topEdgeFirst, botEdgeFirstx, ml, maxl - 1, depth + 1, 2)) {
                        return true;
                    }
                }
                botEdgeFirstx = !botEdgeFirstx;
                if (botEdgeFirstx) {
                    edgex = Square.sqBottomMove[edgex];
                    prun1 = Square.SquarePrun[edgex << 1 | ml];
                    m += 1;
                } else {
                    cornerx = Square.sqBottomMove[cornerx];
                    prun2 = Square.SquarePrun[cornerx << 1 | ml];
                    m += 2;
                }
            }
        }
        return false;
    }
}

