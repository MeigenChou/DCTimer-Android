package solver;

import java.util.Random;

public class Tower {
	protected static char[][] cpm = new char[40320][4];
	protected static byte[][] epm = new byte[6][4];
    protected static byte[] cpd = new byte[40320];
    protected static byte[] epd = new byte[6];
    private static StringBuffer sb;
    private static byte[] faces = {3, 1, 1, 3};
    private static String[] turn = {"U", "R", "F", "D"};
    private static String[] suff = {"'", "2", ""};
    
    private static boolean ini = false;
    public static void init() {
    	if(ini) return;
    	int[] arr = new int[8];
    	/*	0	1
		 *	3	2
		 *
		 *	4	5
		 *	7	6
		 */
		for (int i=0; i<40320; i++) {
			for (int j=0; j<4; j++) {
				Mapping.set8Perm(arr, i);
				switch(j) {
				case 0: Mapping.cir(arr, 0, 3, 2, 1); break;	//U
				case 1: Mapping.cir(arr, 1, 2, 5, 6); break;	//R2
				case 2: Mapping.cir(arr, 2, 3, 4, 5); break;	//F2
				case 3: Mapping.cir(arr, 4, 7, 6, 5); break;	//D
				}
				cpm[i][j] = (char) Mapping.get8Perm(arr);
			}
		}
		for (int i=1; i<40320; i++) cpd[i] = -1;
		cpd[0] = 0;
		//int n = 1;
		for(int d=0; d<13; d++) {
			for (int i=0; i<40320; i++)
				if (cpd[i] == d)
					for (int k=0; k<4; k++)
						for(int y=i, m=0; m<faces[k]; m++) {
							y = cpm[y][k];
							if (faces[k] == 1) y = cpm[y][k];
							if (cpd[y] < 0) {
								cpd[y] = (byte) (d + 1);
								//n++;
							}
						}
			//System.out.println(d+1+" "+n);
		}
		/*	-	0
		 *	2	1
		 */
		for (int i=0; i<6; i++) {
			for (int j=0; j<4; j++) {
				Mapping.idxToPerm(arr, i, 3);
				switch (j) {
				case 1: Mapping.cir(arr, 0, 1); break;	//R2
				case 2: Mapping.cir(arr, 1, 2); break;	//F2
				}
				epm[i][j] = (byte) Mapping.permToIdx(arr, 3);
			}
		}
		for (int i=1; i<6; i++) epd[i] = -1;
		epd[0] = 0;
		//n = 1;
		for(int d=0; d<3; d++) {
			for (int i=0; i<6; i++)
				if (epd[i] == d)
					for (int k=1; k<3; k++) {
						int y = epm[i][k];
						if (epd[y] < 0) {
							epd[y] = (byte) (d + 1);
							//n++;
						}
					}
			//System.out.println(d+1+" "+n);
		}
        ini = true;
    }
    
    private static boolean search(int cp, int ep, int d, int lf) {
    	if (d == 0) return cp==0 && ep==0;
    	if (cpd[cp] > d || epd[ep] > d) return false;
    	int y, s;
    	for (int i=0; i<4; i++) {
    		if (i != lf) {
    			y = cp; s = ep;
    			for(int k=0; k<faces[i]; k++) {
    				y = cpm[y][i];
    				if(faces[i] == 1) y = cpm[y][i];
    				s = epm[s][i];
    				if(search(y, s, d - 1, i)) {
    					sb.append(turn[i] + (faces[i]==1 ? "2" : suff[k]) + " ");
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    public static String solve(Random r) {
    	init();
    	int cp = r.nextInt(40320);
    	int ep = r.nextInt(6);
    	sb = new StringBuffer();
    	for (int depth = 0; !search(cp, ep, depth, -1); depth++);
    	return sb.toString();
    }
    
    private static byte[] img = new byte[32];
    private static void initColor() {
		img = new byte[] {
			    3,3,
			    3,3,
			5,5,4,4,2,2,1,1,
			5,5,4,4,2,2,1,1,
			5,5,4,4,2,2,1,1,
			    0,0,
			    0,0
		};
	}
    
    private static void move(int turn) {
    	switch (turn) {
    	case 0:	//U
    		Mapping.cir(img,0,2,3,1);
    		Mapping.cir(img,5,7,9,11);
    		Mapping.cir(img,4,6,8,10); break;
    	case 1:	//R
    		Mapping.cir2(img,1,29,3,31);
    		Mapping.cir2(img,8,25,9,24);
    		Mapping.cir2(img,16,17,15,18);
    		Mapping.cir2(img,7,26,23,10); break;
    	case 2:	//F
    		Mapping.cir2(img,2,29,3,28);
    		Mapping.cir2(img,6,23,7,22);
    		Mapping.cir2(img,14,15,13,16);
    		Mapping.cir2(img,5,24,21,8); break;
    	case 3:	//D
    		Mapping.cir(img,28,30,31,29);
    		Mapping.cir(img,27,25,23,21);
    		Mapping.cir(img,26,24,22,20); break;
    	}
    }
    private static String moveIdx = "URFD";
    public static byte[] image(String scr) {
    	initColor();
    	String[] s = scr.split(" ");
    	for(int i=0; i<s.length; i++)
    		if(s[i].length() > 0) {
    			int mov = moveIdx.indexOf(s[i].charAt(0));
    			move(mov);
    			if(s[i].length()>1 && faces[mov]!=1) {
    				move(mov);
    				if(s[i].charAt(1)=='\'') move(mov);
    			}
    		}
    	return img;
    }
}
