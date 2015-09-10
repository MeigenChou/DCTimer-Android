package solver;

import java.util.Random;

public class RTower {
	private static short[][] epm = new short[5040][3];
	private static short[][] eom = new short[729][3];
	private static byte[] epd = new byte[5040];
	private static byte[] eod = new byte[729];
	private static byte[] faces = {3, 1, 1, 3};
	private static String[] turn1 = {"Uw", "R", "F"};
	private static String[] turn2 = {"U", "R", "F", "D"};
	private static String[] suff = {"'", "2", ""};
	private static int[] sol = new int[25];
	private static int len1, len2;
	private static int ep, eo, cp;
	
	private static boolean ini = false;
	public static void init() {
		if(ini) return;
		Tower.init();
		int[] arr = new int[8];
		
		/*	0	1
		 *	3	2
		 *
		 *	4	5
		 *	-	6
		 */
		for (int i=0; i<5040; i++) {
			for (int j=0; j<3; j++) {
				Mapping.set7Perm(arr, i);
				switch (j) {
				case 0: Mapping.cir(arr, 0, 3, 2, 1); break;	//Uw
				case 1: Mapping.cir(arr, 1, 2, 5, 6); break;	//R
				case 2: Mapping.cir(arr, 2, 3, 4, 5); break;	//F
				}
				epm[i][j] = (short) Mapping.get7Perm(arr);
			}
		}
		for (int i=0; i<729; i++) {
			for (int j=0; j<3; j++) {
				Mapping.idxToZori(arr, i, 3, 7);
				switch(j) {
				case 0: Mapping.cir(arr, 0, 3, 2, 1); break;	//Uw
				case 1: Mapping.cir(arr, 1, 2, 5, 6);
					arr[1]+=2; arr[2]++; arr[5]+=2; arr[6]++;
					break;	//R
				case 2:Mapping.cir(arr, 2, 3, 4, 5);
					arr[2]+=2; arr[3]++; arr[4]+=2; arr[5]++;
					break;	//F
				}
				eom[i][j] = (short) Mapping.zoriToIdx(arr, 3, 7);
			}
		}
		
		
		//n = 1;
		for (int i=1; i<5040; i++) {
			Mapping.set7Perm(arr, i);
			if(checkEp1(arr)) {
				epd[i] = 0;
				//n++;
			}
			else epd[i] = -1;
		}
		//System.out.println("0 "+n);
		epd[0] = 0;
		for(int d=0; d<4; d++) {
			for (int i=0; i<5040; i++)
				if (epd[i] == d)
					for (int k=0; k<3; k++)
						for(int y=i, m=0; m<3; m++) {
							y = epm[y][k];
							if (epd[y] < 0) {
								epd[y] = (byte) (d + 1);
								//n++;
							}
						}
			//System.out.println(d+1+" "+n);
		}
		
		for (int i=1; i<729; i++) eod[i]=-1;
		eod[0] = 0;
		//n = 1;
		for(int d=0; d<6; d++) {
			//nVisited = 0;
			for (int i=0; i<729; i++) 
				if (eod[i] == d) {
					for (int k=0; k<3; k++)
						for(int y=i, m=0; m<3; m++) {
							y = eom[y][k];
							if (eod[y] < 0) {
								eod[y] = (byte) (d + 1);
								//n++;
							}
						}
				}
			//System.out.println(d+1+" "+n);
		}
		ini = true;
	}
	
	static boolean checkEp1(int[] arr) {
		if(arr[0] != 0) return false;
		for(int i=1; i<4; i++) {
			if(arr[i] + arr[7-i] != 7) return false;
		}
		return true;
	}
	
	static int getMi(int ep) {
		int[] arr = new int[7];
		Mapping.set7Perm(arr, ep);
		for(int i=0; i<3; i++) {
			if(arr[i+1] < 4) arr[i] = arr[i+1];
			else arr[i] = 6 - arr[i+1];
		}
		return Mapping.permToIdx(arr, 3);
	}
	
	public static String scramble(Random r) {
		init();
		eo = r.nextInt(729);
		ep = r.nextInt(5040);
		cp = r.nextInt(40320);
		for (len1=0; ; len1++) {
			if(search1(ep, eo, len1, -1)) {
        		StringBuffer sb = new StringBuffer();
				for(int i=len1+1; i<=len2+len1; i++) 
					sb.append(turn2[sol[i]/3] + suff[sol[i]%3] + " ");
				for(int i=1; i<=len1; i++)
					sb.append(turn1[sol[i]/3] + suff[sol[i]%3] + " ");
				return sb.toString();
			}
		}
	}

	private static boolean search1(int ep, int eo, int d, int lf) {
		if (d == 0) return eo == 0 && epd[ep] == 0 && init2();
		if (epd[ep] > d || eod[eo] > d) return false;
		for (int i=0; i<3; i++) {
			if (i != lf) {
				int y = eo, s = ep;
				for (int j=0; j<3; j++) {
					y = eom[y][i]; s = epm[s][i];
					sol[d] = i * 3 + j;
					if (search1(s, y, d - 1, i)) {
						//sb.insert(0, turn1[i]+suff[j]+" ");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	static boolean init2() {
		int epx = ep, cpx = cp;
		for (int i=len1; i>0; i--) {
			int m = sol[i]/3, t = sol[i]%3;
			epx = epm[epx][m]; cpx = Tower.cpm[cpx][m];
			if (t > 0) {
				epx = epm[epx][m]; cpx = Tower.cpm[cpx][m];
				if (t > 1) {
					epx = epm[epx][m]; cpx = Tower.cpm[cpx][m];
				}
			}
		}
		epx = getMi(epx);
		//int lf = sol[1]/3;
		for (len2=0; len2<19-len1; len2++) {
			if (search2(cpx, epx, len2, -1)) {
				return true;
			}
		}
		return false;
	}

	private static boolean search2(int cp, int ep, int d, int lf) {
		if (d == 0) return cp == 0 && ep == 0;
		if (Tower.epd[ep] > d || Tower.cpd[cp] > d) return false;
		for (int i=0; i<4; i++) {
			if (i != lf) {
				int y = cp, s = ep;
				for (int k=0; k<faces[i]; k++) {
					y = Tower.cpm[y][i];
					if(faces[i] == 1) y = Tower.cpm[y][i];
					s = Tower.epm[s][i];
					sol[d + len1] = i * 3 + (faces[i]==1 ? 1 : k);
					if (search2(y, s, d - 1, i)) {
						//sb.insert(0, turn2[i]+(i<2?"2":suff[k])+" ");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/*
	private static byte[] img = new byte[96];
	private static void initColor() {
		img = new byte[] {
						-1,-1,-1,-1,
						-1, 0, 0,-1,
						-1, 0, 0,-1,
						-1,-1,-1,-1,
			-1, 2, 2,-1,-1, 4, 4,-1,-1, 3, 3,-1,-1, 5, 5,-1,
			-1, 2, 2,-1,-1, 4, 4,-1,-1, 3, 3,-1,-1, 5, 5,-1,
			-1, 2, 2,-1,-1, 4, 4,-1,-1, 3, 3,-1,-1, 5, 5,-1,
			-1, 2, 2,-1,-1, 4, 4,-1,-1, 3, 3,-1,-1, 5, 5,-1,
						-1,-1,-1,-1,
						-1, 1, 1,-1,
						-1, 1, 1,-1,
						-1,-1,-1,-1,
		};
	}
	/*			5	6
	 *			9	10
	 *	17	18	21	22	25	26	29	30
	 *	33	34	37	38	41	42	45	46
	 *	49	50	53	54	57	58	61	62
	 *	65	66	69	70	73	74	77	78
	 *			85	86
	 *			89	90
	 
	private static void move(int m) {
		switch (m) {
		case 0: //U
			Mapping.cir(img, 5, 9, 10, 6); Mapping.cir(img, 17, 21, 25, 29);
			Mapping.cir(img, 18, 22, 26, 30); break;
		case 1:	//R
			Mapping.cir(img, 41, 57, 58, 42); Mapping.cir(img, 26, 40, 73, 59);
			Mapping.cir(img, 25, 56, 74, 43);
			Mapping.cir(img, 2, 22, 82, 77); Mapping.cir(img, 6, 38, 86, 61);
			Mapping.cir(img, 10, 54, 90, 45); Mapping.cir(img, 14, 70, 94, 29);
			Mapping.cir(img, 7, 39, 87, 60); Mapping.cir(img, 11, 55, 91, 44);
			break;
		case 2:	//F
			Mapping.cir(img, 37, 53, 54, 38); Mapping.cir(img, 21, 52, 70, 39);
			Mapping.cir(img, 22, 36, 69, 55);
			
			break;
		default:
			break;
		}
	}
	*/
	
}
