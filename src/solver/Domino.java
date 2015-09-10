package solver;

import java.util.Random;

public class Domino {
	private static char[][] cpm = new char[40320][5];
	private static char[][] epm = new char[40320][5];
	private static byte[] cpd = new byte[40320];
	private static byte[] epd = new byte[40320];
    private static byte[] faces = {3, 1, 1, 1, 1};
    private static String[] turn = {"U", "L", "R", "F", "B"};
    private static String[] suff = {"'", "2", ""};
    private static int[] seq = new int[20];
    
    private static boolean ini = false;
    private static void init() {
    	if(ini) return;
    	int[] arr = new int[8];
    	for (int i=0; i<40320; i++) {
    		for (int j=0; j<5; j++) {
    			Mapping.set8Perm(arr, i);
    			switch(j) {
    			case 0: Mapping.cir(arr, 0, 3, 2, 1); break;	//U
    			case 1: Mapping.cir2(arr, 0, 7, 3, 4); break;	//L
    			case 2: Mapping.cir2(arr, 1, 6, 2, 5); break;	//R
    			case 3: Mapping.cir2(arr, 3, 6, 2, 7); break;	//F
    			case 4: Mapping.cir2(arr, 0, 5, 1, 4); break;	//B
    			}
    			cpm[i][j] = (char) Mapping.get8Perm(arr);
    			Mapping.set8Perm(arr, i);
    			switch(j) {
    			case 0: Mapping.cir(arr, 0, 3, 2, 1); break;	//U
    			case 1: Mapping.cir(arr, 3, 7); break;	//L
    			case 2: Mapping.cir(arr, 1, 5); break;	//R
    			case 3: Mapping.cir(arr, 2, 6); break;	//F
    			case 4: Mapping.cir(arr, 0, 4); break;	//B
    			}
    			epm[i][j] = (char) Mapping.get8Perm(arr);
    		}
    	}
    	
    	for (int i=1; i<40320; i++)
    		cpd[i] = epd[i] = -1;
    	cpd[0] = epd[0] = 0;
    	//int n = 1;
        for (int d=0; d<13; d++) {
        	for (int i=0; i<40320; i++)
        		if (cpd[i] == d)
        			for (int k=0; k<5; k++)
        				for(int y=i, m=0; m<faces[k]; m++) {
        					y = cpm[y][k];
        					if (cpd[y] < 0) {
        						cpd[y] = (byte) (d + 1);
        						//n++;
        					}
        				}
        	//System.out.println(d+1+" "+n);
        }
        //n = 1;
        for (int d = 0; d < 11; d++) {
        	for (int i=0; i<40320; i++)
        		if (epd[i] == d)
        			for (int k=0; k<5; k++)
        				for(int y=i, m=0; m<faces[k]; m++) {
        					y = epm[y][k];
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
    	if (d == 0) return cp == 0 && ep == 0;
    	if (cpd[cp] > d || epd[ep] > d) return false;
    	int y, s;
    	for (int i=0; i<5; i++)
    		if(i != lf) {
    			y = cp; s = ep;
    			for (int k=0; k<faces[i]; k++) {
    				y = cpm[y][i]; s = epm[s][i];
    				if (search(y, s, d - 1, i)) {
    					seq[d] = i * 3 + (i<1 ? k : 1);
    					//sb.append(turn[i]+(i<1?suff[k]:"2")+" ");
    					return true;
    				}
    			}
    		}
    	return false;
    }
    
    public static String scramble(Random r) {
    	init();
    	int cp = r.nextInt(40320);
    	int ep = r.nextInt(40320);
    	
    	for (int d = 0; ; d++) {
    		if(search(cp, ep, d, -1)) {
    			StringBuffer s = new StringBuffer();
    			for(int i=1; i<=d; i++) {
    				s.append(turn[seq[i]/3] + suff[seq[i]%3] + " ");
    			}
    			return s.toString();
    		}
    	}
    }
    
    private static byte[] img = new byte[42];
    private static void initColor() {
		img = new byte[] {
			      3,3,3,
			      3,3,3,
			      3,3,3,
			5,5,5,4,4,4,2,2,2,1,1,1,
			5,5,5,4,4,4,2,2,2,1,1,1,
			      0,0,0,
			      0,0,0,
			      0,0,0
		};
	}
    private static void move(int turn) {
    	switch (turn) {
    	case 0:	//U
    		Mapping.cir(img,0,6,8,2);
    		Mapping.cir(img,1,3,7,5);
    		Mapping.cir(img,9,12,15,18);
    		Mapping.cir(img,10,13,16,19);
    		Mapping.cir(img,11,14,17,20);
    		break;
    	case 1:	//D
    		Mapping.cir(img,33,39,41,35);
    		Mapping.cir(img,34,36,40,38);
    		Mapping.cir(img,30,27,24,21);
    		Mapping.cir(img,31,28,25,22);
    		Mapping.cir(img,32,29,26,23);
    		break;
    	case 2:	//L
    		Mapping.cir2(img,9,23,11,21);
    		Mapping.cir2(img,10,22,3,36);
    		Mapping.cir2(img,0,33,6,39);
    		Mapping.cir2(img,20,24,32,12);
    		break;
    	case 3:	//R
    		Mapping.cir2(img,15,29,17,27);
    		Mapping.cir2(img,16,28,5,38);
    		Mapping.cir2(img,8,41,2,35);
    		Mapping.cir2(img,14,30,26,18);
    		break;
    	case 4:	//F
    		Mapping.cir2(img,12,26,14,24);
    		Mapping.cir2(img,13,25,7,34);
    		Mapping.cir2(img,6,35,8,33);
    		Mapping.cir2(img,11,27,15,23);
    		break;
    	case 5:	//B
    		Mapping.cir2(img,18,32,20,30);
    		Mapping.cir2(img,19,31,1,40);
    		Mapping.cir2(img,2,39,0,41);
    		Mapping.cir2(img,17,21,29,9);
    		break;
    	}
    }
    private static String moveIdx = "UDLRFB";
    public static byte[] image(String scr) {
    	initColor();
    	String[] s = scr.split(" ");
    	for(int i=0; i<s.length; i++)
    		if(s[i].length() > 0) {
    			int mov = moveIdx.indexOf(s[i].charAt(0));
    			move(mov);
    			if (s[i].length()>1 && mov<2) {
    				move(mov);
    				if(s[i].charAt(1)=='\'') move(mov);
    			}
    		}
    	return img;
    }
}
