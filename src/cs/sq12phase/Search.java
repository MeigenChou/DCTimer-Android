package cs.sq12phase;

import java.io.*;

import com.dctimer.DCTimer;

import cs.min2phase.Tools;

public class Search {

	int[] move = new int[50];
	FullCube c = null;
	FullCube d = new FullCube("");
	int length1;
	int maxlen2;
	String sol_string;

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
			InputStream in = new BufferedInputStream(new FileInputStream(DCTimer.dataPath + "sq1.dat"));
			Tools.read(Shape.ShapeIdx, in);
			in.read(Shape.ShapePrun);
			in.read(Shape.ShapePrunOpt);
			Tools.read(Shape.spTopMove, in);
			Tools.read(Shape.spBottomMove, in);
			Tools.read(Shape.spTwistMove, in);
			Tools.read(Square.sqTwistMove, in);
			Tools.read(Square.sqTopMove, in);
			Tools.read(Square.sqBottomMove, in);
			in.read(Square.SquarePrun);
			in.close();
		} catch (Exception e) {
			Shape.init();
			Square.init();
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(DCTimer.dataPath + "sq1.dat"));
				Tools.write(Shape.ShapeIdx, out);
				out.write(Shape.ShapePrun);
				out.write(Shape.ShapePrunOpt);
				Tools.write(Shape.spTopMove, out);
				Tools.write(Shape.spBottomMove, out);
				Tools.write(Shape.spTwistMove, out);
				Tools.write(Square.sqTwistMove, out);
				Tools.write(Square.sqTopMove, out);
				Tools.write(Square.sqBottomMove, out);
				out.write(Square.SquarePrun);
				out.close();
			} catch (Exception e1) { }
		}
	}
	
	public String solution(FullCube c) {
		this.c = c;
		sol_string = null;
		int shape = c.getShapeIdx();
		for (length1=Shape.ShapePrun[shape]; length1<50; length1++) {
			maxlen2 = Math.min(33 - length1, 17);
			if (phase1(shape, Shape.ShapePrun[shape], length1, 0, -1)) {
				break;
			}
		}
		return sol_string;
	}
	
	public String scramble(FullCube c, int minLen) {
		String sol = solutionOpt(c, minLen);
		if(sol == null) return solution(c);
		return scramble(c, minLen);
	}
	
	public String solutionOpt(FullCube c, int maxl) {
        this.c = c;
        sol_string = null;
        int shape = c.getShapeIdx();
        for (length1=Shape.ShapePrunOpt[shape]; length1<=maxl; length1++) {
            if (phase1Opt(shape, Shape.ShapePrunOpt[shape], length1, 0, -1)) {
                break;
            }
        }
        return sol_string;
    }
	
	boolean phase1Opt(int shape, int prunvalue, int maxl, int depth, int lm) {
        if (maxl == 0) {
            return isSolvedInPhase1();
        }
        //try each possible move. First twist;
        if (lm != 0) {
            int shapex = Shape.spTwistMove[shape];
            int prunx = Shape.ShapePrunOpt[shapex];
            if (prunx < maxl) {
                move[depth] = 0;
                if (phase1Opt(shapex, prunx, maxl-1, depth+1, 0))
                    return true;
            }
        }
        //Try top layer
        int shapex = shape;
        if(lm <= 0) {
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
                    if (phase1Opt(shapex, prunx, maxl-1, depth+1, 1))
                        return true;
                }
            }
        }
        shapex = shape;
        //Try bottom layer
        if(lm <= 1) {
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
                    if (phase1Opt(shapex, prunx, maxl-1, depth+1, 2))
                        return true;
                }
            }
        }
        return false;
	}
	
	boolean isSolvedInPhase1() {
        d.copy(c);
        for (int i=0; i<length1; i++)
            d.doMove(move[i]);
        boolean isSolved = d.ul == 0x011233 && d.ur == 0x455677 && d.dl == 0x998bba && d.dr == 0xddcffe && d.ml == 0;
        if (isSolved)
            sol_string = move2string(length1);
        return isSolved;
    }

	boolean phase1(int shape, int prunvalue, int maxl, int depth, int lm) {
		if (prunvalue==0 && maxl<4) {
			return maxl==0 && init2();
		}

		//try each possible move. First twist;
		if (lm != 0) {
			int shapex = Shape.spTwistMove[shape];
			int prunx = Shape.ShapePrun[shapex];
			if (prunx < maxl) {
				move[depth] = 0;
				if (phase1(shapex, prunx, maxl-1, depth+1, 0)) {
					return true;
				}				
			}
		}

		//Try top layer
		int shapex = shape;
		if(lm <= 0){
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
					if (phase1(shapex, prunx, maxl-1, depth+1, 1)) {
						return true;
					}
				}
			}
		}

		shapex = shape;
		//Try bottom layer
		if(lm <= 1){
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
					if (phase1(shapex, prunx, maxl-1, depth+1, 2)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	Square sq = new Square();


	boolean init2() {
		d.copy(c);
		for (int i=0; i<length1; i++) {
			d.doMove(move[i]);
		}
		d.getSquare(sq);

		int edge = sq.edgeperm;
		int corner = sq.cornperm;
		int ml = sq.ml;

		int prun = Math.max(Square.SquarePrun[sq.edgeperm<<1|ml], Square.SquarePrun[sq.cornperm<<1|ml]);

		for (int i=prun; i<maxlen2; i++) {
			if (phase2(edge, corner, sq.topEdgeFirst, sq.botEdgeFirst, ml, i, length1, 0)) {
				sol_string = move2string(i + length1);
				return true;
			}
		}

		return false;
	}

	//int pruncomb[] = new int[100];

	String move2string(int len) {
		//TODO whether to invert the solution or not should be set by params.
		StringBuffer s = new StringBuffer();
		int top = 0, bottom = 0;
		for (int i=len-1; i>=0; i--) {
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
		if (top == 0 && bottom == 0) {
		} else {
			s.append('(').append(top).append(",").append(bottom).append(")");
		}
		return s.toString();// + " (" + len + "t)";
	}

	boolean phase2(int edge, int corner, boolean topEdgeFirst, boolean botEdgeFirst, int ml, int maxl, int depth, int lm) {
		if (maxl == 0 && !topEdgeFirst && botEdgeFirst/*edge==0 && corner==0 && !topEdgeFirst && botEdgeFirst && ml==0*/) {
			return true;
		}

		//try each possible move. First twist;
		if(lm!=0 && topEdgeFirst == botEdgeFirst) {
			int edgex = Square.sqTwistMove[edge];
			int cornerx = Square.sqTwistMove[corner];

			if (Square.SquarePrun[edgex<<1|(1-ml)] < maxl && Square.SquarePrun[cornerx<<1|(1-ml)] < maxl) {
				move[depth] = 0;
				if (phase2(edgex, cornerx, topEdgeFirst, botEdgeFirst, 1-ml, maxl-1, depth+1, 0)) {
					return true;
				}
			}
		}

		//Try top layer
		if (lm <= 0){
			boolean topEdgeFirstx = !topEdgeFirst;
			int edgex = topEdgeFirstx ? Square.sqTopMove[edge] : edge;
			int cornerx = topEdgeFirstx ? corner : Square.sqTopMove[corner];
			int m = topEdgeFirstx ? 1 : 2;
			int prun1 = Square.SquarePrun[edgex<<1|ml];
			int prun2 = Square.SquarePrun[cornerx<<1|ml];
			while (m < 12 && prun1 <= maxl && prun1 <= maxl) {
				if (prun1 < maxl && prun2 < maxl) {
					move[depth] = m;
					if (phase2(edgex, cornerx, topEdgeFirstx, botEdgeFirst, ml, maxl-1, depth+1, 1)) {
						return true;
					}
				}
				topEdgeFirstx = !topEdgeFirstx;
				if (topEdgeFirstx) {
					edgex = Square.sqTopMove[edgex];
					prun1 = Square.SquarePrun[edgex<<1|ml];
					m += 1;
				} else {
					cornerx = Square.sqTopMove[cornerx];
					prun2 = Square.SquarePrun[cornerx<<1|ml];
					m += 2;
				}
			}
		}

		if (lm <= 1){
			boolean botEdgeFirstx = !botEdgeFirst;
			int edgex = botEdgeFirstx ? Square.sqBottomMove[edge] : edge;
			int cornerx = botEdgeFirstx ? corner : Square.sqBottomMove[corner];
			int m = botEdgeFirstx ? 1 : 2;
			int prun1 = Square.SquarePrun[edgex<<1|ml];
			int prun2 = Square.SquarePrun[cornerx<<1|ml];
			while (m < (maxl > 6 ? 6 : 12) && prun1 <= maxl && prun1 <= maxl) {
				if (prun1 < maxl && prun2 < maxl) {
					move[depth] = -m;
					if (phase2(edgex, cornerx, topEdgeFirst, botEdgeFirstx, ml, maxl-1, depth+1, 2)) {
						return true;
					}
				}
				botEdgeFirstx = !botEdgeFirstx;
				if (botEdgeFirstx) {
					edgex = Square.sqBottomMove[edgex];
					prun1 = Square.SquarePrun[edgex<<1|ml];
					m += 1;
				} else {
					cornerx = Square.sqBottomMove[cornerx];
					prun2 = Square.SquarePrun[cornerx<<1|ml];
					m += 2;
				}
			}
		}
		return false;
	}
}

