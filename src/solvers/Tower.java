package solvers;

import java.util.Random;

public class Tower {
	protected static char[][] cpm = new char[40320][6];
    private static byte[][] epm = new byte[24][6];
    protected static byte[] cpd = new byte[40320];
    private static byte[] epd = new byte[24];
    private static StringBuffer sb;
    private static byte[] faces = {3, 3, 1, 1, 1, 1};
    private static String[] turn = {"U", "D", "L", "R", "F", "B"};
    private static String[] suff={"'", "2", ""};
    
    private static boolean ini0=false;
    public static void init0(){
    	if(ini0)return;
    	int[] arr = new int[8];
    	for (int i = 0; i < 40320; i++) {
    		for (int j = 0; j < 6; j++) {
    			Im.set8Perm(arr, i);
    			switch(j){
    			case 0:Im.cir(arr, 0, 3, 2, 1);break;	//U
    			case 1:Im.cir(arr, 4, 5, 6, 7);break;	//D
    			case 2:Im.cir2(arr, 0, 7, 3, 4);break;	//L
    			case 3:Im.cir2(arr, 1, 6, 2, 5);break;	//R
    			case 4:Im.cir2(arr, 3, 6, 2, 7);break;	//F
    			case 5:Im.cir2(arr, 0, 5, 1, 4);break;	//B
    			}
    			cpm[i][j]=(char) Im.get8Perm(arr);
    		}
    	}
    	
    	for (int i = 1; i < 40320; i++)
    		cpd[i]=-1;
    	cpd[0]=0;
    	//int nVisited;
        for(int d=0; d<13; d++) {
        	//nVisited = 0;
        	for (int i = 0; i < 40320; i++)
        		if (cpd[i] == d)
        			for (int k = 0; k < 6; k++)
        				for(int y = i, m = 0; m < faces[k]; m++) {
        					y = cpm[y][k];
        					if (cpd[y] < 0) {
        						cpd[y] = (byte) (d + 1);
        						//nVisited++;
        					}
        				}
        	//System.out.println(distance+1+" "+nVisited);
        }
        ini0=true;
    }
    
    private static boolean ini=false;
    private static void init() {
    	if(ini)return;
    	init0();
    	int[] arr = new int[4];
    	for(int i=0; i<24; i++) {
    		for (int j = 0; j < 6; j++) {
    			if(j<2)epm[i][j]=(byte) i;
    			else {
    				Im.indexToPermutation(arr, i, 4);
    				switch(j){
    				case 2:Im.cir(arr, 0, 3);break;	//L
    				case 3:Im.cir(arr, 1, 2);break;	//R
    				case 4:Im.cir(arr, 3, 2);break;	//F
    				case 5:Im.cir(arr, 1, 0);break;	//B
    				}
    				epm[i][j]=(byte) Im.permutationToIndex(arr, 4);
    			}
    		}
    	}
    	
    	for (int i = 1; i < 24; i++)
        	epd[i]=-1;
        epd[0]=0;
        //int nVisited = 1;
        for (int d = 0; d < 4; d++) {
        	//nVisited = 0;
        	for (int i = 0; i < 24; i++)
        		if (epd[i] == d)
        			for (int k = 2; k < 6; k++) {
        				byte next = epm[i][k];
        				if (epd[next] < 0) {
        					epd[next] = (byte) (d + 1);
        					//nVisited++;
        				}
        			}
        	//System.out.println(distance+" "+nVisited);
        }
        ini=true;
    }
    
    private static boolean search(int cp, int ep, int depth, int lastFace) {
    	if (depth == 0) return cp==0 && ep==0;
    	if (cpd[cp] > depth || epd[ep] > depth) return false;
    	int y, s;
    	for (int i = 0; i < 6; i++) {
    		if (i != lastFace) {
    			y = cp; s = ep;
    			for(int k = 0; k < faces[i]; k++){
    				y = cpm[y][i]; s = epm[s][i];
    				if(search(y, s, depth - 1, i)){
    					sb.append(turn[i]+(i<2?suff[k]:"2")+" ");
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    public static String solve(Random r){
    	init();
    	//Random r=new Random();
    	int cp=r.nextInt(40320);
    	//int cp=IndexMapping.permutationToIndex(new byte[]{0, 1, 2, 3, 4, 5, 6, 7});
    	int ep=r.nextInt(24);
    	//int ep=IndexMapping.permutationToIndex(new byte[]{0, 1, 2, 3});
    	sb=new StringBuffer();
    	
    	for (int depth = 0;!search(cp, ep, depth, -1); depth++);
    	
    	return sb.toString();
    }
    
    private static byte[] img=new byte[32];
    private static void initColor(){
		img=new byte[]{
			    3,3,
			    3,3,
			5,5,4,4,2,2,1,1,
			5,5,4,4,2,2,1,1,
			5,5,4,4,2,2,1,1,
			    0,0,
			    0,0
		};
	}
    
    private static void move(int turn){
    	switch(turn){
    	case 0:	//U
    		Im.cir(img,0,2,3,1);
    		Im.cir(img,5,7,9,11);
    		Im.cir(img,4,6,8,10);break;
    	case 1:	//D
    		Im.cir(img,28,30,31,29);
    		Im.cir(img,27,25,23,21);
    		Im.cir(img,26,24,22,20);break;
    	case 2:	//L
    		Im.cir2(img,0,28,2,30);
    		Im.cir2(img,4,21,5,20);
    		Im.cir2(img,12,13,14,19);
    		Im.cir2(img,6,27,22,11);break;
    	case 3:	//R
    		Im.cir2(img,1,29,3,31);
    		Im.cir2(img,8,25,9,24);
    		Im.cir2(img,16,17,15,18);
    		Im.cir2(img,7,26,23,10);break;
    	case 4:	//F
    		Im.cir2(img,2,29,3,28);
    		Im.cir2(img,6,23,7,22);
    		Im.cir2(img,14,15,13,16);
    		Im.cir2(img,5,24,21,8);break;
    	case 5:	//B
    		Im.cir2(img, 0,31,1,30);
    		Im.cir2(img, 10,27,11,26);
    		Im.cir2(img, 18,19,17,12);
    		Im.cir2(img, 9,20,25,4);break;
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
