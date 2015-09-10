package solver;

import java.util.Random;

public class CubeRU {
	private static short[][] cpm=new short[720][2];
	private static short[][] epm=new short[5040][2];
	private static short[][] com=new short[243][2];
	private static byte[][] cd=new byte[720][243];
	private static byte[] epd=new byte[5040];
	private static void move(int[] arr, int a, int b, int c, int d){
		int temp=arr[a]; arr[a]=arr[b]; arr[b]=arr[c]; arr[c]=arr[d]; arr[d]=temp;
	}
	private static boolean ini=false;
	private static void init(){
		if(ini)return;
		int[] arr = new int[6];
		for(int i=0; i<720; i++){
			for(int j=0; j<2; j++){
				Mapping.idxToPerm(arr, i, 6);
				if(j==0)move(arr, 0, 3, 2, 1);
				else move(arr, 1, 2, 4, 5);
				cpm[i][j]=(short) Mapping.permToIdx(arr, 6);
			}
		}
		for(int i=0; i<243; i++){
			for(int j=0; j<2; j++){
				Mapping.idxToZori(arr, i, 3, 6);
				if(j==0)move(arr, 0, 3, 2, 1);
				else {
					move(arr, 1, 2, 4, 5);
					arr[1]=(arr[1]+1)%3;arr[2]=(arr[2]+2)%3;
					arr[4]=(arr[4]+1)%3;arr[5]=(arr[5]+2)%3;
				}
				com[i][j]=(short) Mapping.zoriToIdx(arr, 3, 6);
			}
		}
		arr = new int[7];
		for(int i=0; i<5040; i++){
			for(int j=0; j<2; j++){
				Mapping.idxToPerm(arr, i, 7);
				if(j==0)move(arr, 0, 3, 2, 1);
				else move(arr, 1, 6, 5, 4);
				epm[i][j]=(short) Mapping.permToIdx(arr, 7);
			}
		}
		for(int i=0; i<720; i++)
			for(int j=0; j<243; j++) cd[i][j]=-1;
		cd[0][0]=0;
		int d=0;
		int c=1;
		while(c>0){
			c=0;
			for(int i=0; i<720; i++){
				for(int j=0; j<243; j++) {
					if(cd[i][j]==d){
						for (int k=0; k<2; k++) {
							for(int y=i, s=j, l=0; l<3; l++){
								y=cpm[y][k]; s=com[s][k];
								if(cd[y][s]<0){
									cd[y][s]=(byte) (d+1);
									c++;
								}
							}
						}
					}
				}
			}
			d++;
			//System.out.println(d+" "+c);
		}
		for(int i=0; i<5040; i++) epd[i]=-1;
		epd[0]=0;
		d=0;
		c=1;
		while(c>0){
			c=0;
			for(int i=0; i<5040; i++){
				if(epd[i]==d){
					for (int j=0; j<2; j++) {
						for(int y=i, k=0; k<3; k++){
							y=epm[y][j];
							if(epd[y]<0){
								epd[y]=(byte) (d+1);
								c++;
							}
						}
					}
				}
			}
			d++;
			//System.out.println(d+" "+c);
		}
		
		ini=true;
	}
	
    private static String[] turn={"U","R"};
	private static String[] suff={"","2","'"};
	private static StringBuffer sb=new StringBuffer();
    private static boolean search(int cp, int co, int ep, int depth, int lm) {
    	if (depth == 0) return cp==0 && co==0 && ep==0;
    	if (cd[cp][co]>depth || epd[ep] > depth) return false;
    	for (int i = 0; i < 2; i++) {
    		if(i!=lm){
    			int d=cp, w=co, y=ep;
    			for(int j=0;j<3;j++){
    				d=cpm[d][i];w=com[w][i];y=epm[y][i];
    				if(search(d, w, y, depth-1, i)){
    					sb.insert(0, turn[i]+suff[j]+" ");
						return true;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    public static String solve(Random r){
    	init();
    	int cp, co, ep;
    	int[] c=new int[6], e=new int[7];
    	do{
    		do {
    			cp=r.nextInt(720);
    			co=r.nextInt(243);
    		}
    		while (cd[cp][co]<0);
    		ep=r.nextInt(5040);
    		Mapping.idxToPerm(c, cp, 6);
    		Mapping.idxToPerm(e, ep, 7);
    	} while(permutationSign(c)!=permutationSign(e));
    	
    	sb=new StringBuffer();
    	for(int d=0; !search(cp, co, ep, d, -1); d++);
    	return sb.toString();
    }
    
    protected static boolean permutationSign(int[] permutation) {
        int nInversions = 0;
        for (int i = 0; i < permutation.length; i++) {
            for (int j = i + 1; j < permutation.length; j++) {
                if (permutation[i] > permutation[j]) {
                    nInversions++;
                }
            }
        }
        return nInversions % 2 == 0;
    }
}
