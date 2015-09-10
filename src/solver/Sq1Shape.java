package solver;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sq1Shape {
    private static int[] halfLayer = {0x15, 0x17, 0x1B, 0x1D, 0x1F, 0x2B, 0x2D, 0x2F, 
		0x35, 0x37, 0x3B, 0x3D, 0x3F};
    static int[] shapeIdx = new int[3678];
    static final int ID = 7191405;
    static byte[] prunTrn = new byte[3678];
    static byte[] prunTws = new byte[3678];
    static int[] sol = new int[16];
    static int sollen;
    
    private static int getShape2Idx(int shp) {
		return Arrays.binarySearch(shapeIdx, shp);
	}
    
    private static int rotate(int layer) {
        return ((layer << 1) & 0xFFE) | ((layer >> 11) & 1);
    }

    private static int getTop(int index) {
        return index & 0xFFF;
    }

    private static int getBottom(int index) {
        return (index >> 12) & 0xFFF;
    }
    
    private static int rotateTop(int idx) {
        return (getBottom(idx) << 12) | rotate(getTop(idx));
    }

    private static int rotateBottom(int idx) {
        return (rotate(getBottom(idx)) << 12) | getTop(idx);
    }

    private static int twist(int idx) {
        int newTop = (getTop(idx) & 0xF80) | (getBottom(idx) & 0x7F);
        int newBottom = (getBottom(idx) & 0xF80) | (getTop(idx) & 0x7F);
        return (newBottom << 12) | newTop;
    }
    
    private static boolean isTwistable(int idx) {
        int top = getTop(idx);
        int bottom = getBottom(idx);
        return (top & (1 << 0)) != 0 &&
               (top & (1 << 6)) != 0 &&
               (bottom & (1 << 0)) != 0 &&
               (bottom & (1 << 6)) != 0;
    }
    
    public static int applyMove(int state, String move) {
        //State state = this;
        if (move.equals("/")) {
            state = twist(state);
        } else {
            Pattern p = Pattern.compile("\\((-?\\d+),(-?\\d+)\\)");
            Matcher matcher = p.matcher(move);
            matcher.find();
            int top = Integer.parseInt(matcher.group(1));
            for (int i = 0; i < top + 12; i++) {
                state = rotateTop(state);
            }
            int bottom = Integer.parseInt(matcher.group(2));
            for (int i = 0; i < bottom + 12; i++) {
                state = rotateBottom(state);
            }
        }
        return state;
    }
    
    public static int applySequence(String[] sequence) {
        int state = ID;
        for (String move : sequence) {
            state = applyMove(state, move);
        }
        return state;
    }
    
    private static boolean ini = false;
    static void init() {
    	if(ini) return;
    	int count = 0;
		for (int i=0; i<28561; i++) {
			int dr = halfLayer[i % 13];
			int dl = halfLayer[i / 13 % 13];
			int ur = halfLayer[i / 13 / 13 % 13];
			int ul = halfLayer[i / 13 / 13 / 13];
			int value = ul<<18|ur<<12|dl<<6|dr;
			if (Integer.bitCount(value) == 16) {
				shapeIdx[count++] = value;
			}
		}
        ini = true;
    }
    
    private static boolean inif = false;
    static void initf() {
    	if(inif) return;
    	init();
    	for (int i = 0; i < 3678; i++) {
            prunTrn[i] = -1;
        }
        prunTrn[getShape2Idx(ID)] = 0;
        for(int d=0; d<14; d++) {
            for (int i = 0; i < 3678; i++) {
                if (prunTrn[i] == d) {
                    int state = shapeIdx[i];
                    // twist
                    if (isTwistable(state)) {
                        int next = twist(state);
                        int temp=getShape2Idx(next);
                        if (prunTrn[temp] == -1) {
                            prunTrn[temp] = (byte) (d + 1);
                        }
                    }
                    // rotate top
                    int nextTop = shapeIdx[i];
                    for (int j = 0; j < 11; j++) {
                        nextTop = rotateTop(nextTop);
                        if(isTwistable(nextTop)){
                        	int temp=getShape2Idx(nextTop);
                        	if (prunTrn[temp] == -1) {
                        		prunTrn[temp] = (byte) (d + 1);
                        	}
                        }
                    }
                    // rotate bottom
                    int nextBottom = shapeIdx[i];
                    for (int j = 0; j < 11; j++) {
                        nextBottom = rotateBottom(nextBottom);
                        if(isTwistable(nextBottom)){
                        	int temp=getShape2Idx(nextBottom);
                        	if (prunTrn[temp] == -1) {
                        		prunTrn[temp] = (byte) (d + 1);
                        	}
                        }
                    }
                }
            }
        }
    	inif = true;
    }
    
    private static boolean init = false;
    static void initt() {
    	if(init) return;
    	init();
    	for(int i=0; i<3678; i++) {
    		prunTws[i] = -1;
    	}
    	prunTws[1170] = prunTws[1192] = prunTws[2640] = prunTws[2662] = 0;
    	//twist
    	for(int d=0; d<7; d++) {
    		//int count = 0;
    		for(int i=0; i<3678; i++)
    			if(prunTws[i] == d) {
    				int next = twist(shapeIdx[i]);
    				if(prunTws[getShape2Idx(next)] == -1) {
    					prunTws[getShape2Idx(next)] = (byte) (d+1);
    					//count++;
    					for(int a=0; a<13; a++) {
    						for(int b=0; b<13; b++) {
    							if(isTwistable(next)) {
    								int temp = getShape2Idx(next);
    								if(prunTws[temp] == -1) {
    									prunTws[temp] = (byte) (d+1);
    									//count++;
    								}
    							}
    							next = rotateBottom(next);
    						}
    						next = rotateTop(next);
    					}
    				}
    			}
    		//System.out.println(d+1+" "+count);
    	}
    	init = true;
    }

    static boolean searchTws(int shape, int d, int lm) {
    	if(d==0) return shape == ID;//prunTws[getShape2Idx(shape)] == 0;
    	if(prunTws[getShape2Idx(shape)] > d) return false;
    	//top move
    	for(int i=0; i<12; i++) {
    		if(i!=0) sol[sollen++] = i;
    		//bottom move
    		for(int j=0; j<12; j++) {
    			if(j!=0) sol[sollen++] = -j;
    			//twist
    			if((lm!=0 || (i!=0 || j!=0)) && isTwistable(shape)) {
    				int next = twist(shape);
    				sol[sollen++] = 0;
    				if(searchTws(next, d-1, 0)) {
    					return true;
    				}
    				sollen--;
    			}
    			if(j!=0) sollen--;
				shape = rotateBottom(shape);
    		}
    		if(i!=0) sollen--;
    		shape = rotateTop(shape);
    	}
    	return false;
    }
    
    static String move2string() {
    	StringBuilder sb = new StringBuilder();
    	int top = 0, bottom = 0;
    	for(int i=0; i<sollen; i++) {
    		int val = sol[i];
    		if (val > 0) {
    			top = (val > 6) ? (val-12) : val;
    		} else if (val < 0) {
    			bottom = (val < -6) ? (-12-val) : -val;
    		} else {
    			if (top == 0 && bottom == 0) {
					sb.append(" / ");
				} else {
					sb.append("("+top+","+bottom+") / ");
				}
				top = bottom = 0;
    		}
    	}
    	if(top!=0 || bottom!=0) {
    		sb.append('(').append(top).append(",").append(bottom).append(")");
    	}
    	return sb.toString();
    }
    
    public static String solveTrn(String scr) {
    	initf();
    	int state = applySequence(scr.split(" "));
    	StringBuffer sequence = new StringBuffer();
    	while (prunTrn[getShape2Idx(state)] > 0) {
    		// twist
            if (isTwistable(state)) {
                int next = twist(state);
                if (prunTrn[getShape2Idx(next)] == prunTrn[getShape2Idx(state)] - 1) {
                    sequence.append("/ ");
                    state = next;
                }
            }
            // rotate top
            int x = 0;
            int nextTop = state;
            for (int i = 0; i < 12; i++) {
            	int temp=getShape2Idx(nextTop);
                if (temp>=0 && prunTrn[temp] == prunTrn[getShape2Idx(state)] - 1) {
                    x = i;
                    state = nextTop;
                    break;
                }
                nextTop = rotateTop(nextTop);
            }
            // rotate bottom
            int y = 0;
            int nextBottom = state;
            for (int j = 0; j < 12; j++) {
            	int temp=getShape2Idx(nextBottom);
                if (temp>=0 && prunTrn[temp] == prunTrn[getShape2Idx(state)] - 1) {
                    y = j;
                    state = nextBottom;
                    break;
                }
                nextBottom = rotateBottom(nextBottom);
            }
            if (x != 0 || y != 0) {
                sequence.append("(" + (x <= 6 ? x : x - 12) + "," + (y <= 6 ? y : y - 12) + ") ");
            }
    	}
    	return sequence.toString();
    }
    
    public static String solveTws(String scr) {
    	initt();
    	int state = applySequence(scr.split(" "));
    	sollen = 0;
    	for(int d=0; ; d++) {
    		if(searchTws(state, d, -1))
    			return move2string();
    	}
    }
}
