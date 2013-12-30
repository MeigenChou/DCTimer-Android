package solvers;

import java.util.Random;

public class Skewb {
	private static short[][] ctm = new short[360][4];
	private static byte[][] cpm = new byte[36][4];
	private static byte[][] com = new byte[81][4];
	private static byte[][] fcm = new byte[27][4];
	private static byte[] ctd = new byte[360];
	private static byte[][][] cd = new byte[36][81][27];
	
	private static boolean ini = false;
	private static void init() {
		if(ini) return;
		// move tables
		/* center
		 * 		  0
		 *	4	1	2	3
		 * 		  5
		 */
		int[] arr = new int[6];
		for (int i=0; i<360; i++)
			for (int j=0; j<4; j++) {
				Im.idxToEperm(arr, i, 6);
				switch(j) {
				case 0: Im.cir(arr, 2, 5, 3); break;	//R
				case 1: Im.cir(arr, 0, 3, 4); break;	//U
				case 2: Im.cir(arr, 1, 4, 5); break;	//L
				case 3: Im.cir(arr, 3, 5, 4); break;	//B
				}
				ctm[i][j] = (short) Im.epermToIdx(arr, 6);
			}
		/* corner permutation
		 *	0		1
		 *		3
		 *		2
		 *
		 *		0
		 *	1		2
		 */
		arr = new int[4];
		int[] arr2 = new int[3];
		for (int i=0; i<12; i++)
			for (int j=0; j<3; j++) {
				for (int k=0; k<4; k++) {
					Im.idxToEperm(arr, i, 4);
					Im.idxToEperm(arr2, j, 3);
					switch (k) {
					case 0: Im.cir(arr, 1, 2, 3); break;	//R
					case 1: Im.cir(arr, 0, 1, 3); break;	//U
					case 2: Im.cir(arr, 2, 0, 3); break;	//L
					case 3: Im.cir(arr2, 0, 2, 1); break;	//B
					}
					cpm[i*3+j][k] = (byte) (Im.epermToIdx(arr, 4)*3+Im.epermToIdx(arr2, 3));
				}
			}
		//corner orientation
		for (int i=0; i<81; i++) {
			for (int j=0; j<4; j++) {
				Im.idxToOri(arr, i, 3, 4);
				switch(j) {
				case 0: Im.cir(arr, 1, 2, 3);	//R
					arr[1]+=2; arr[2]+=2; arr[3]+=2; break;
				case 1: Im.cir(arr, 0, 1, 3);	//U
					arr[0]+=2; arr[1]+=2; arr[3]+=2; break;
				case 2: Im.cir(arr, 2, 0, 3);	//L
					arr[0]+=2; arr[2]+=2; arr[3]+=2; break;
				case 3: arr[3]++; break;	//B
				}
				com[i][j] = (byte) Im.oriToIdx(arr, 3, 4);
			}
		}
		for (int i=0; i<27; i++) {
			for (int j=0; j<4; j++) {
				Im.idxToOri(arr, i, 3, 3);
				switch(j) {
				case 0: arr[2]++; break;	//R
				case 1: arr[0]++; break;	//U
				case 2: arr[1]++; break;	//L
				case 3: Im.cir(arr, 0, 2, 1);
					arr[0]+=2; arr[1]+=2; arr[2]+=2; break;	//B
				}
				fcm[i][j] = (byte) Im.oriToIdx(arr, 3, 3);
			}
		}
		
		// distance table
		for (int i = 0; i < 360; i++)
			ctd[i] = -1;
		ctd[0] = 0;
		//int c = 1;
		for(int depth = 0; depth < 5; depth++) {
			for (int i = 0; i < 360; i++)
					if (ctd[i] == depth)
						for (int m = 0; m < 4; m++) {
							int p = i;
							for(int n = 0; n < 2; n++){
								p = ctm[p][m];
								if (ctd[p] == -1) {
									ctd[p] = (byte) (depth + 1);
									//c++;
								}
							}
						}
			//System.out.println(depth+1+" "+c);
		}
		for (int i=0; i<36; i++)
			for (int j=0; j<81; j++)
				for(int k=0; k<27; k++)
					cd[i][j][k] = -1;
		cd[0][0][0] = 0;
		//c = 1;
		for(int d=0; d<7; d++) {
			for (int i=0; i<36; i++)
				for(int j=0; j<81; j++)
					for(int k=0; k<27; k++)
						if (cd[i][j][k] == d)
							for (int m=0; m<4; m++) {
								int p = i, q = j, r = k;
								for(int n=0; n<2; n++) {
									p = cpm[p][m];
									q = com[q][m];
									r = fcm[r][m];
									if (cd[p][q][r] == -1) {
										cd[p][q][r] = (byte) (d + 1);
										//c++;
									}
								}
							}
			//System.out.println(depth+1+" "+c);
		}
		ini = true;
	}
	
	private static StringBuffer sb;
	private static String[] turn = {"R", "U", "L", "B"};
	private static String[] suff = {"'", ""};
	private static boolean search(int ct, int cp, int co, int fco, int d, int l) {
		if (d==0) return ctd[ct] == 0 && cd[cp][co][fco] == 0;
		if (ctd[ct] > d || cd[cp][co][fco] > d) return false;
		for (int k=0; k<4; k++)
			if (k != l) {
				int p = ct, q = cp, r = co, s = fco;
				for(int m=0; m<2; m++) {
					p = ctm[p][k]; q = cpm[q][k];
					r = com[r][k]; s = fcm[s][k];
					if(search(p, q, r, s, d-1, k)) {
						sb.append(turn[k] + suff[m] + " ");
						return true;
					}
				}
			}
		return false;
	}
	public static String solve(Random r) {
		init();
		int ct = r.nextInt(360), cp, co, fco;
		do{
			cp = r.nextInt(36);
			co = r.nextInt(27);
			fco = r.nextInt(27);
		}
		while (cd[cp][co][fco] < 0);
		
		sb = new StringBuffer();
		for(int d=0; d<13; d++) 
			if(search(ct, cp, co, fco, d, -1)) 
				break;
		return sb.toString();
	}

	/*
	 *				0		1
	 *					2
	 *				3		4
	 *	5		6	10		11	15		16	20		21
	 *		7			12			17			22
	 *	8		9	13		14	18		19	23		24
	 *				25		26
	 *					27
	 *				28		29
	 */
	private static byte[] img = new byte[30];
	private static void initColor() {
		for (int i=0; i<5; i++)
			for (byte j=0; j<6; j++) img[j*5+i] = j;
	}
	private static void cir3(byte[] arr, int a, int b, int c) {
		byte temp = arr[a];
		arr[a] = arr[b];
		arr[b] = arr[c];
		arr[c] = temp;
	}
	private static void move(int turn) {
		switch(turn) {
		case 0:	//R
			cir3(img, 17, 27, 22);
			cir3(img, 19, 29, 23);
			cir3(img, 1, 14, 8);
			cir3(img, 20, 18, 28);
			cir3(img, 16, 26, 24);
			break;
		case 1:	//U
			cir3(img, 2, 22, 7);
			cir3(img, 0, 21, 5);
			cir3(img, 3, 20, 8);
			cir3(img, 10, 16, 28);
			cir3(img, 6, 1, 24);
			break;
		case 2:	//L
			cir3(img, 12, 7, 27);
			cir3(img, 13, 9, 25);
			cir3(img, 3, 24, 18);
			cir3(img, 10, 8, 26);
			cir3(img, 6, 28, 14);
			break;
		case 3:	//B
			cir3(img, 22, 27, 7);
			cir3(img, 24, 28, 8);
			cir3(img, 0, 19, 13);
			cir3(img, 5, 23, 25);
			cir3(img, 21, 29, 9);
			break;
		}
	}
	private static String moveIdx="RULB";
	public static byte[] image(String scr) {
    	initColor();
    	String[] s = scr.split(" ");
    	for (int i=0; i<s.length; i++)
    		if (s[i].length() > 0) {
    			int mov = moveIdx.indexOf(s[i].charAt(0));
    			move(mov);
    			if(s[i].length() > 1)
    				move(mov);
    		}
    	return img;
    }
}
