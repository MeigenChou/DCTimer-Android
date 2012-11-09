package solvers;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sq1Shape {
    private static int[] halflayer = {0x15, 0x17, 0x1B, 0x1D, 0x1F, 0x2B, 0x2D, 0x2F, 
		0x35, 0x37, 0x3B, 0x3D, 0x3F};
    private static int[] ShapeIdx = new int[3678];
    private static final int ID = 7191405;
    private static byte[] distance = new byte[3678];
    
    private static int getShape2Idx(int shp) {
		return Arrays.binarySearch(ShapeIdx, shp);
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
            Matcher matcher = p.matcher(move.toString());
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
    
    private static boolean ini=false;
    static void init() {
    	int count = 0;
		for (int i=0; i<28561; i++) {
			int dr = halflayer[i % 13];
			int dl = halflayer[i / 13 % 13];
			int ur = halflayer[i / 13 / 13 % 13];
			int ul = halflayer[i / 13 / 13 / 13];
			int value = ul<<18|ur<<12|dl<<6|dr;
			if (Integer.bitCount(value) == 16) {
				ShapeIdx[count++] = value;
			}
		}
		
        for (int i = 0; i < 3678; i++) {
            distance[i] = -1;
        }

        distance[getShape2Idx(ID)] = 0;

        for(int depth=0; depth<14; depth++) {
            for (int i = 0; i < 3678; i++) {
                if (distance[i] == depth) {
                    int state = ShapeIdx[i];

                    // twist
                    if (isTwistable(state)) {
                        int next = twist(state);
                        int temp=getShape2Idx(next);
                        if (distance[temp] == -1) {
                            distance[temp] = (byte) (depth + 1);
                        }
                    }

                    // rotate top
                    int nextTop = ShapeIdx[i];
                    for (int j = 0; j < 11; j++) {
                        nextTop = rotateTop(nextTop);
                        if(isTwistable(nextTop)){
                        	int temp=getShape2Idx(nextTop);
                        	if (distance[temp] == -1) {
                        		distance[temp] = (byte) (depth + 1);
                        	}
                        }
                        
                    }

                    // rotate bottom
                    int nextBottom = ShapeIdx[i];
                    for (int j = 0; j < 11; j++) {
                        nextBottom = rotateBottom(nextBottom);
                        if(isTwistable(nextBottom)){
                        	int temp=getShape2Idx(nextBottom);
                        	if (distance[temp] == -1) {
                        		distance[temp] = (byte) (depth + 1);
                        	}
                        }
                        
                    }
                }
            }
        }
        ini=true;
    }

    public static String solve(String scr) {
    	if(!ini)init();
    	
    	int state = applySequence(scr.split(" "));
        StringBuffer sequence = new StringBuffer();

        while (distance[getShape2Idx(state)] > 0) {
            // twist
            if (isTwistable(state)) {
                int next = twist(state);
                if (distance[getShape2Idx(next)] == distance[getShape2Idx(state)] - 1) {
                    sequence.append("/ ");
                    state = next;
                }
            }

            // rotate top
            int x = 0;
            int nextTop = state;
            for (int i = 0; i < 12; i++) {
            	int temp=getShape2Idx(nextTop);
                if (temp>=0 && distance[temp] == distance[getShape2Idx(state)] - 1) {
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
                if (temp>=0 && distance[temp] == distance[getShape2Idx(state)] - 1) {
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
}
