package solvers;

import java.util.Random;

public class Domino {
	private static char[][] epm = new char[40320][5];
	private static byte[] epd = new byte[40320];
	private static StringBuffer sb;
    private static byte[] faces = {3, 1, 1, 1, 1};
    private static byte[] idx = {0, 2, 3, 4, 5};
    private static String[] turn = {"U", "L", "R", "F", "B"};
    private static String[] suff = {"'", "2", ""};

    private static boolean ini=false;
    private static void init(){
    	if(ini)return;
    	Tower.init0();
    	int[] arr = new int[8];
    	for (int i = 0; i < 40320; i++) {
    		for (int j = 0; j < 5; j++) {
    			Tl.set8Perm(arr, i);
    			switch(j){
    			case 0:Tl.cir(arr, 0, 3, 2, 1);break;	//U
    			//case 1:Tl.cir(arr, 4, 5, 6, 7);break;	//D
    			case 1:Tl.cir(arr, 3, 7);break;	//L
    			case 2:Tl.cir(arr, 1, 5);break;	//R
    			case 3:Tl.cir(arr, 2, 6);break;	//F
    			case 4:Tl.cir(arr, 0, 4);break;	//B
    			}
    			epm[i][j]=(char) Tl.get8Perm(arr);
    		}
    	}
    	for (int i = 1; i < 40320; i++)
    		epd[i]=-1;
    	epd[0]=0;
    	
        //int nVisited = 1;
        for (int d = 0; d < 11; d++) {
        	//nVisited = 0;
        	for (int i = 0; i < 40320; i++)
        		if (epd[i] == d)
        			for (int k = 0; k < 5; k++)
        				for(int y = i, m = 0; m < faces[k]; m++) {
        					y = epm[y][k];
        					if (epd[y] < 0) {
        						epd[y] = (byte) (d + 1);
        						//nVisited++;
        					}
        				}
        	//System.out.println(d+1+" "+nVisited);
        }
    	ini=true;
    }
    private static boolean search(int cp, int ep, int depth, int lastFace){
    	if (depth == 0) return cp == 0 && ep == 0;
    	if (Tower.cpd[cp] > depth || epd[ep] > depth) return false;
    	int y, s;
    	for (int i = 0; i < 5; i++)
    		if(i != lastFace){
    			y = cp; s = ep;
    			for(int k = 0; k < faces[i]; k++){
    				y = Tower.cpm[y][idx[i]]; s = epm[s][i];
    				if(search(y, s, depth - 1, i)){
    					sb.append(turn[i]+(i<1?suff[k]:"2")+" ");
    					return true;
    				}
    			}
    		}
    	return false;
    }
    public static String solve(Random r) {
    	init();
    	int cp = r.nextInt(40320);
    	int ep = r.nextInt(40320);
    	
    	sb=new StringBuffer();
    	for (int depth = 0; !search(cp, ep, depth, -1); depth++);
    	return sb.toString();
    }
    
    private static byte[] img=new byte[42];
    private static void initColor(){
		img=new byte[]{
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
    private static void move(int turn){
    	switch(turn){
    	case 0:	//U
    		Tl.cir(img,0,6,8,2);
    		Tl.cir(img,1,3,7,5);
    		Tl.cir(img,9,12,15,18);
    		Tl.cir(img,10,13,16,19);
    		Tl.cir(img,11,14,17,20);
    		break;
    	case 1:	//D
    		Tl.cir(img,33,39,41,35);
    		Tl.cir(img,34,36,40,38);
    		Tl.cir(img,30,27,24,21);
    		Tl.cir(img,31,28,25,22);
    		Tl.cir(img,32,29,26,23);
    		break;
    	case 2:	//L
    		Tl.cir2(img,9,23,11,21);
    		Tl.cir2(img,10,22,3,36);
    		Tl.cir2(img,0,33,6,39);
    		Tl.cir2(img,20,24,32,12);
    		break;
    	case 3:	//R
    		Tl.cir2(img,15,29,17,27);
    		Tl.cir2(img,16,28,5,38);
    		Tl.cir2(img,8,41,2,35);
    		Tl.cir2(img,14,30,26,18);
    		break;
    	case 4:	//F
    		Tl.cir2(img,12,26,14,24);
    		Tl.cir2(img,13,25,7,34);
    		Tl.cir2(img,6,35,8,33);
    		Tl.cir2(img,11,27,15,23);
    		break;
    	case 5:	//B
    		Tl.cir2(img,18,32,20,30);
    		Tl.cir2(img,19,31,1,40);
    		Tl.cir2(img,2,39,0,41);
    		Tl.cir2(img,17,21,29,9);
    		break;
    	}
    }
    private static String moveIdx="UDLRFB";
    public static byte[] image(String scr){
    	initColor();
    	String[] s=scr.split(" ");
    	for(int i=0; i<s.length; i++)
    		if(s[i].length()>0){
    			int mov = moveIdx.indexOf(s[i].charAt(0));
    			move(mov);
    			if(s[i].length()>1 && mov<2){
    				move(mov);
    				if(s[i].charAt(1)=='\'')move(mov);
    			}
    		}
    	return img;
    }
}
