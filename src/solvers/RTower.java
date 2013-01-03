package solvers;

import java.util.Random;

public class RTower {
	private static char[][] cpm = new char[40320][4];
	private static char[][] epm = new char[40320][5];
	private static short[][] eom = new short[2187][5];
	private static byte[] cpd = new byte[40320];
	private static byte[] epd = new byte[40320];
	private static byte[] eod = new byte[2187];
	private static StringBuffer sb;
	private static byte[] faces = {3, 3, 1, 1};
	private static String[] turn1 = {"Uw", "R", "F", "L", "B"};
	private static String[] turn2 = {"U", "D", "R", "F"};
    private static String[] suff={"", "2", "'"};
    private static int[] fact={1,1,2,6,24,120,720,5040};
	static void set8Perm(int[] arr, int idx) {
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int p = fact[7-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (val >> v) & 07;
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		arr[7] = val;
	}
	static int get8Perm(int[] arr) {
		int idx = 0;
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int v = arr[i] << 2;
			idx = (8 - i) * idx + ((val >> v) & 07);
			val -= 0x11111110 << v;
		}
		return idx;
	}
	
    public static void cir(int[] arr, int a, int b, int c, int d){
    	int temp=arr[a];
    	arr[a]=arr[b];
    	arr[b]=arr[c];
    	arr[c]=arr[d];
    	arr[d]=temp;
    }
    public static void cir(byte[] arr, int a, int b, int c, int d){
    	byte temp=arr[a];
    	arr[a]=arr[b];
    	arr[b]=arr[c];
    	arr[c]=arr[d];
    	arr[d]=temp;
    }
    private static boolean ini=false;
    public static void init(){
    	if(ini)return;
    	int[] arr = new int[8];
    	int[] idx = {1,0,2,3};
    	for (int i = 0; i < 40320; i++) {
    		for (int j = 0; j < 6; j++) {
    			set8Perm(arr, i);
    			switch(j){
    			case 0:cir(arr, 4, 5, 6, 7);break;	//D
    			case 1:cir(arr, 0, 3, 2, 1);break;	//U
    			case 2:cir(arr, 1, 2, 6, 5);break;	//R
    			case 3:cir(arr, 2, 3, 7, 6);break;	//F
    			case 4:cir(arr, 0, 4, 7, 3);break;	//L
    			case 5:cir(arr, 0, 1, 5, 4);break;	//B
    			}
    			if(j>0)epm[i][j-1]=(char) get8Perm(arr);
    			switch(j){
    			case 2:cir(arr, 1, 2, 6, 5);break;	//R
    			case 3:cir(arr, 2, 3, 7, 6);break;	//F
    			}
    			if(j<4)cpm[i][idx[j]]=(char) get8Perm(arr);
    		}
    	}
    	for (int i = 0; i < 2187; i++) {
    		for (int j = 0; j < 5; j++) {
    			Im.indexToZeroSumOrientation(arr, i, 3, 8);
    			switch(j){
    			case 0:cir(arr, 0, 3, 2, 1);break;	//U
    			case 1:cir(arr, 1, 2, 6, 5);
    				arr[1]=(arr[1]+1)%3;arr[2]=(arr[2]+2)%3;
    				arr[6]=(arr[6]+1)%3;arr[5]=(arr[5]+2)%3;
    				break;	//R
    			case 2:cir(arr, 2, 3, 7, 6);
    				arr[2]=(arr[2]+1)%3;arr[3]=(arr[3]+2)%3;
    				arr[7]=(arr[7]+1)%3;arr[6]=(arr[6]+2)%3;
    				break;	//F
    			case 3:cir(arr, 0, 4, 7, 3);
    				arr[3]=(arr[3]+1)%3;arr[0]=(arr[0]+2)%3;
    				arr[4]=(arr[4]+1)%3;arr[7]=(arr[7]+2)%3;
    				break;	//L
    			case 4:cir(arr, 0, 1, 5, 4);
    				arr[0]=(arr[0]+1)%3;arr[1]=(arr[1]+2)%3;
    				arr[5]=(arr[5]+1)%3;arr[4]=(arr[4]+2)%3;
    				break;	//B
    			}
    			eom[i][j]=(short) Im.zeroSumOrientationToIndex(arr, 3, 8);
    		}
    	}
    	for (int i = 1; i < 40320; i++)
    		cpd[i]=epd[i]=-1;
    	cpd[0]=epd[0]=0;
    	//int nVisited=1;
        for(int d=0; d<13; d++) {
        	//nVisited = 0;
        	for (int i = 0; i < 40320; i++)
        		if (cpd[i] == d)
        			for (int k = 0; k < 4; k++)
        				for(int y = i, m = 0; m < faces[k]; m++) {
        					y = cpm[y][k];
        					if (cpd[y] < 0) {
        						cpd[y] = (byte) (d + 1);
        						//nVisited++;
        					}
        				}
        	//System.out.println(d+1+" "+nVisited);
        }
        for(int d=0; d<7; d++) {
        	//nVisited = 0;
        	for (int i = 0; i < 40320; i++)
        		if (epd[i] == d)
        			for (int k = 0; k < 5; k++)
        				for(int y = i, m = 0; m < 3; m++) {
        					y = epm[y][k];
        					if (epd[y] < 0) {
        						epd[y] = (byte) (d + 1);
        						//nVisited++;
        					}
        				}
        	//System.out.println(d+" "+nVisited);
        }
        for (int i = 1; i < 2187; i++)
    		eod[i]=-1;
        eod[0]=0;
        for(int d=0; d<6; d++) {
        	//nVisited = 0;
        	for (int i = 0; i < 2187; i++) 
        		if (eod[i] == d) {
        			for (int k = 0; k < 5; k++)
        				for(int y = i, m = 0; m < 3; m++) {
        					y = eom[y][k];
        					if (eod[y] < 0) {
        						eod[y] = (byte) (d + 1);
        						//nVisited++;
        					}
        				}
        		}
        	//System.out.println(d+" "+nVisited);
        }
        ini=true;
    }
    
    private static boolean search(int ep, int eo, int d, int lf) {
        if (d == 0) return eo == 0 && ep == 0;
        if (epd[ep] > d || eod[eo] > d) return false;
        for (int i = 0; i < 5; i++) {
        	if (i != lf) {
        		int y=eo, s=ep;
        		for(int j=0; j<3; j++){
        			y=eom[y][i]; s=epm[s][i];
        			if (search(s, y, d - 1, i)) {
        				sb.insert(0, turn1[i]+suff[j]+" ");
        				return true;
        			}
        		}
        	}
        }
        return false;
    }
    
    public static String solve() {
		init();
		Random r = new Random();
    	int eo = r.nextInt(2187);
        int ep = r.nextInt(40320);
        int cp = r.nextInt(40320);
        sb=new StringBuffer();
        long tm = System.currentTimeMillis();
        for (int depth = 0; ; depth++) {
        	if(search(ep, eo, depth, -1)) {
        		//System.out.print(sb.toString()+".");
        		return solve2(cp)+(System.currentTimeMillis()-tm);
        	}
        }
    }
    
    private static boolean search2(int cp, int ep, int d, int lf) {
    	if (d == 0) return cp == 0 && ep == 0;
    	if (epd[ep] > d || cpd[cp] > d) return false;
    	for (int i = 0; i < 4; i++) {
    		if (i != lf) {
    			int y = cp, s = ep;
    			for(int k = 0; k < faces[i]; k++){
    				y = cpm[y][i]; if(i>1)s = epm[epm[s][i]][i];
    				if(search2(y, s, d - 1, i)){
    					sb.insert(0, turn2[i]+(i<2?suff[k]:"2")+" ");
    					return true;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    private static String solve2(int cp) {
    	//sb = new StringBuffer();
    	for (int depth = 0; ; depth++) {
    		if(search2(cp, 0, depth, -1)) {
    			return sb.toString();
    		}
    	}
    }
    
    
//	public static void main(String[] args) {
//		long tm = System.currentTimeMillis();
//		init();
//		System.out.println(System.currentTimeMillis()-tm);
//		for(int i=0; i<10; i++)
//		System.out.println(solve());
//	}
}
