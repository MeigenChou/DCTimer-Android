package solvers;

public class Cube2layer {
	public static class State {
		public int[] cp;
        public int[] co;
        
        public State(int[] cornersPermutation, int[] cornersOrientation) {
            cp = cornersPermutation;
            co = cornersOrientation;
        }
        
        public State multiply(State move) {
            // corners
        	int[] cornersPermutation = new int[8];
        	int[] cornersOrientation = new int[8];

            for (int i = 0; i < 8; i++) {
                cornersPermutation[i] = cp[move.cp[i]];
                cornersOrientation[i] = (co[move.cp[i]] + move.co[i]) % 3;
            }

            return new State(cornersPermutation, cornersOrientation);
        }
	}
	
	private static State[] moves;
	static {
		State moveU = new State(new int[] { 3, 0, 1, 2, 4, 5, 6, 7 }, new int[] { 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveD = new State(new int[] { 0, 1, 2, 3, 5, 6, 7, 4 }, new int[] { 0, 0, 0, 0, 0, 0, 0, 0 });
        State moveL = new State(new int[] { 4, 1, 2, 0, 7, 5, 6, 3 }, new int[] { 2, 0, 0, 1, 1, 0, 0, 2 });
        State moveR = new State(new int[] { 0, 2, 6, 3, 4, 1, 5, 7 }, new int[] { 0, 1, 2, 0, 0, 2, 1, 0 });
        State moveF = new State(new int[] { 0, 1, 3, 7, 4, 5, 2, 6 }, new int[] { 0, 0, 1, 2, 0, 0, 2, 1 });
        State moveB = new State(new int[] { 1, 5, 2, 3, 0, 4, 6, 7 }, new int[] { 1, 2, 0, 0, 2, 1, 0, 0 });
        
        moves = new State[]{moveU, moveD, moveL, moveR, moveF, moveB};
	}
	
	private static short[][] cpm = new short[1680][6];
	private static short[][] com = new short[5670][6];
	private static byte[] cpd = new byte[1680];
	private static byte[] cod = new byte[5670];
	
	private static boolean ini = false;
	private static void init() {
		if(ini) return;
		for(int i = 0; i < 70; i++) {
			for(int j = 0; j < 81; j++) {
				State state = cornersToState(new int[]{i, j, j});
				for(int k = 0; k < 6; k++){
					int[] cns = stateToCorners(state.multiply(moves[k])); 
					com[i*81+j][k] = (short) (cns[0] * 81 + cns[2]);
					if(j<24)cpm[i*24+j][k] = (short) (cns[0] * 24 + cns[1]);
				}
			}
		}
		for(int i = 0; i< 1680; i++) cpd[i]=-1;
		cpd[1656] = 0;
		int d=0;
		for(d = 0; d < 4; d++){
			for (int i = 0; i < 1680; i++)
				if (cpd[i] == d)
					for (int j = 0; j < 6; j++)
						for(int y = i, k = 0; k < 3; k++){
							y = cpm[y][j];
							if (cpd[y] < 0)
								cpd[y] = (byte) (d + 1);
						}
		}
		for(int i = 0; i < 5670; i++) cod[i]=-1;
		cod[5589] = 0;
		for(d = 0; d < 5; d++){
			for (int i = 0; i < 5670; i++)
				if (cod[i] == d)
					for (int j = 0; j < 6; j++)
						for(int y = i, k = 0; k < 3; k++){
							y = com[y][j];
							if (cod[y] < 0)
								cod[y] = (byte) (d + 1);
						}
		}
		ini = true;
	}
	
	private static int[] stateToCorners(State state) {
		boolean[] selCorners = {
			false, false, false, false,
			true,  true,  true,  true,
		};
		byte[] cornersMapping = {-1, -1, -1, -1, 0, 1, 2, 3};
		boolean[] cornersCombination = new boolean[8];
		for (int l = 0; l < 8; l++)
			cornersCombination[l] = selCorners[state.cp[l]];
		int cci = Im.combinationToIndex(cornersCombination, 4);
		
		int[] cp = new int[4];
		int[] co = new int[4];
		int next = 0;
		for (int i = 0; i < 8; i++)
			if (cornersCombination[i]) {
				cp[next] = cornersMapping[state.cp[i]];
				co[next++] = state.co[i];
			}
		int cpi = Im.permutationToIndex(cp, 4);
		int coi = Im.orientationToIndex(co, 3, 4);
		return new int[]{cci, cpi, coi};
	}
	
	private static State cornersToState(int[] indices) {
		boolean[] cb = new boolean[8];
		Im.indexToCombination(cb, indices[0], 4, 8);
		int[] p = new int[4];
		Im.indexToPermutation(p, indices[1], 4);
		int[] o = new int[4];
		Im.indexToOrientation(o, indices[2], 3, 4);
		byte[] selectedCorners = {4, 5, 6, 7};
		int nextSelCornerIndex = 0;
		int[] cp = new int[8];
		int[] co = new int[8];
		for (int i = 0; i < 8; i++) {
			if (cb[i]) {
				cp[i] = selectedCorners[p[nextSelCornerIndex]];
				co[i] = o[nextSelCornerIndex++];
			}
			else cp[i] = co[i] = 0;
		}
		return new State(cp, co);
	}
	
	private static String[] turn={"U","D","L","R","F","B"};
	private static String[] suff={"","2","'"};
	private static StringBuffer sb;
	private static boolean search(int cp, int co, int depth, int lm) {
		if (depth == 0) return cp==1656 && co==5589;
		if (cpd[cp] > depth || cod[co] > depth) return false;
		for (int i = 0; i < 6; i++)
			if(i!=lm){
				int y=cp, s=co;
				for(int j=0;j<3;j++){
					y=cpm[y][i];s=com[s][i];
					if(search(y, s, depth-1, i)){
						sb.insert(0, " "+turn[i]+suff[j]);
						return true;
					}
				}
			}
		return false;
	}
	private static String[] moveIdx={"UDLRFB","DURLFB","RLUDFB","LRDUFB","BFLRUD","FBLRDU"};
	private static String[] color={"D: ","U: ","L: ","R: ","F: ","B: "};
	private static String[] rotIdx={"","z2","z'","z","x'","x"};
	private static short[] scp={1656, 23, 665, 1030, 217, 1457}, sco={5589, 0, 2255, 3454, 797, 4928};
	private static byte[][] oriIdx = {{0,1,2,3,4,5},{1,0,3,2,5,4},
		{3,2,0,1,3,3},{2,3,1,0,2,2},{5,5,5,5,0,1},{4,4,4,4,1,0}};
	public static String solve(String s, int face){
		String[] scr=s.split(" ");
		int[] cp=new int[6], co=new int[6];
		for(int y=0;y<6;y++){
			cp[y]=scp[oriIdx[face][y]]; co[y]=sco[oriIdx[face][y]];
			for(int d=0;d<scr.length;d++)
				if(0!=scr[d].length()){
					int o=moveIdx[y].indexOf(scr[d].charAt(0));
					cp[y]=cpm[cp[y]][o];co[y]=com[co[y]][o];
					if(1<scr[d].length()) {
						if(scr[d].charAt(1)=='2'){
							co[y]=com[co[y]][o];cp[y]=cpm[cp[y]][o];
						} else {
							co[y]=com[com[co[y]][o]][o];cp[y]=cpm[cpm[cp[y]][o]][o];
						}
					}
				}
		}
		sb=new StringBuffer();
		for(int d=0; ;d++)
			for(int idx=0; idx<6; idx++)
				if(search(cp[idx],co[idx],d,-1))
					return("\n"+color[face]+rotIdx[idx]+sb.toString());
	}
	
	public static String cube2layer(String scr, int face){
		init();
		if(face>0 && face<7)return solve(scr, face-1);
		StringBuffer s = new StringBuffer();
		for(int i=0; i<6; i++)s.append(solve(scr, i));
		return s.toString();
	}
}
