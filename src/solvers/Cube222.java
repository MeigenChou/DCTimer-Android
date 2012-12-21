package solvers;

import java.util.Random;

public class Cube222 {
	private static byte[] perm=new byte[5040];
	private static byte[] twst=new byte[729];
	private static short[][] permmv=new short[5040][3];
	private static short[][] twstmv=new short[729][3];
	
	private static String[] turn={"U","R","F"};
	private static String[] suff={"'","2",""};
	private static boolean ini=false;
	private static StringBuffer sol;
	
	private static Random r = new Random();
	
	public static int[] randomState(){
		int q=r.nextInt(5040);
		int t=r.nextInt(729);
		return new int[]{q, t};
	}
	
	private static void randomState(int[] q, int[] t, int[] r) {
		int len = r.length;
		byte[] cnst = {6, 24, 9, 27};
		int[] idx=new int[len], temp=new int[len];
		for(int i=0; i<len; i++) temp[i] = q[r[i]];
		Im.indexToPermutation(idx, (int)(Math.random()*cnst[len-3]), len);
		for(int i=0; i<len; i++)q[r[i]] = temp[idx[i]];
		Im.indexToZeroSumOrientation(idx, (int)(Math.random()*cnst[len-1]), 3, len);
		for(int i=0; i<len; i++)t[r[i]] = idx[i];
	}
	
	public static int[] randomCLL(){
		int i = r.nextInt(12);
		int iq, it;
		do {
			randomLastLayer(i);
			iq = prmToIdx(p); it = twsToIdx(t);
		} while (iq==0 && it==0);
		return new int[]{iq, it};
	}
	
	public static int[] randomEG1() {
		int i = 8;//r.nextInt(12);
		//int j=0;
		int iq, it;
		do {
			randomLastLayer(i);
			//Log.v("p", p[0]+" "+p[1]+" "+p[2]+" "+p[3]+" "+p[4]+" "+p[5]+" "+p[6]);
			switch(i) {
			case 0:
				switch((int)(Math.random()*2)) {
				case 0: p[4]=5; p[5]=4; break;
				case 1: p[4]=6; p[6]=4; break;
				}
				break;
			case 1:
				switch((int)(Math.random()*2)) {
				case 0: p[2]=3; p[3]=2; t[2]=1; t[3]=2; break;
				case 1: p[2]=6; p[6]=2; t[2]=1; t[6]=2; break;
				}
				break;
			case 2:
				switch((int)(Math.random()*2)) {
				case 0: p[1]=3; p[3]=1; t[1]=2; t[3]=1;break;
				case 1: p[1]=5; p[5]=1; t[1]=2; t[5]=1;break;
				}
				break;
			case 3:
				switch((int)(Math.random()*4)) {
				case 0: Tl.cir(p, 0, 1); break;
				case 1: Tl.cir(p, 0, 2); break;
				case 2: Tl.cir(p, 2, 3); break;
				case 3: Tl.cir(p, 3, 1); break;
				}
				break;
			case 4:
			case 8:
			case 10:
				
				break;
			}
			//Log.v("p", p[0]+" "+p[1]+" "+p[2]+" "+p[3]+" "+p[4]+" "+p[5]+" "+p[6]);
			iq = prmToIdx(p); it = twsToIdx(t);
		} while (iq==0 && it==0);
		return new int[]{iq, it};
	}
	private static int[] p, t;
	public static void randomLastLayer(int type){
		p=new int[]{0,1,2,3,4,5,6};
		t=new int[]{0,0,0,0,0,0,0};
		switch(type){
		case 0:randomState(p, t, new int[]{0,1,2,3});break;
		case 1:randomState(p, t, new int[]{0,1,4,5});break;
		case 2:randomState(p, t, new int[]{0,2,4,6});break;
		case 3:randomState(p, t, new int[]{4,5,6});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=2; p[1]=0; p[2]=3; p[3]=1;break;
			case 2: p[0]=3; p[1]=2; p[2]=1; p[3]=0;break;
			case 3: p[0]=1; p[1]=3; p[2]=0; p[3]=2;break;
			}
			break;
		case 4:randomState(p, t, new int[]{2,3,6});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=4; p[1]=0; p[4]=5; p[5]=1;
				t[0]=2; t[1]=1; t[4]=1; t[5]=2; break;
			case 2: p[0]=5; p[1]=4; p[4]=1; p[5]=0; break;
			case 3: p[0]=1; p[1]=5; p[4]=0; p[5]=4;
				t[0]=2; t[1]=1; t[4]=1; t[5]=2; break;
			}
			break;
		case 5:randomState(p, t, new int[]{1,3,5});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=2; p[2]=6; p[4]=0; p[6]=4;
				t[0]=1; t[2]=2; t[4]=2; t[6]=1; break;
			case 2: p[0]=6; p[2]=4; p[4]=2; p[6]=0; break;
			case 3: p[0]=4; p[2]=0; p[4]=6; p[6]=2;
				t[0]=1; t[2]=2; t[4]=2; t[6]=1; break;
			}
			break;
		case 6: p=new int[]{1,4,3,5,0,6,2}; t[0]=2; t[2]=1; t[4]=1; t[6]=2;
			randomState(p, t, new int[]{1,3,5});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=3; p[2]=2; p[4]=1; p[6]=0; break;
			case 2: p[0]=2; p[2]=0; p[4]=3; p[6]=1; break;
			case 3: p[0]=0; p[2]=1; p[4]=2; p[6]=3; break;
			}
			break;
		case 7: p=new int[]{2,3,5,4,0,1,6}; t[0]=1; t[1]=2; t[4]=2; t[5]=1;
			randomState(p, t, new int[]{2,3,6});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=0; p[1]=2; p[4]=1; p[5]=3; break;
			case 2: p[0]=1; p[1]=0; p[4]=3; p[5]=2; break;
			case 3: p[0]=3; p[1]=1; p[4]=2; p[5]=0; break;
			}
			break;
		case 8: p=new int[]{4,5,0,1,2,3,6}; t[0]=1; t[1]=2; t[2]=2; t[3]=1;
			randomState(p, t, new int[]{4,5,6});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=5; p[1]=1; p[2]=4; p[3]=0;
				t[0]=2; t[1]=1; t[2]=1; t[3]=2; break;
			case 2: p[0]=1; p[1]=0; p[2]=5; p[3]=4;break;
			case 3: p[0]=0; p[1]=4; p[2]=1; p[3]=5;
				t[0]=2; t[1]=1; t[2]=1; t[3]=2; break;
			}
			break;
		case 9: p=new int[]{1,2,0,3,5,6,4};
			randomState(p, t, new int[]{1,3,5});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=0; p[2]=4; p[4]=1; p[6]=5;
				t[0]=1; t[2]=2; t[4]=2; t[6]=1; break;
			case 2: p[0]=4; p[2]=5; p[4]=0; p[6]=1; break;
			case 3: p[0]=5; p[2]=1; p[4]=4; p[6]=0;
				t[0]=1; t[2]=2; t[4]=2; t[6]=1; break;
			}
			break;
		case 10: p=new int[]{4,0,6,2,1,3,5}; t[0]=2; t[1]=1; t[2]=1; t[3]=2;
			randomState(p, t, new int[]{4,5,6});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=0; p[1]=2; p[2]=4; p[3]=6;
				t[0]=1; t[1]=2; t[2]=2; t[3]=1; break;
			case 2: p[0]=2; p[1]=6; p[2]=0; p[3]=4; break;
			case 3: p[0]=6; p[1]=4; p[2]=2; p[3]=0;
				t[0]=1; t[1]=2; t[2]=2; t[3]=1; break;
			}
			break;
		case 11: p=new int[]{2,0,1,3,6,4,5};
			randomState(p, t, new int[]{2,3,6});
			switch((int)(Math.random()*4)) {
			case 1: p[0]=6; p[1]=2; p[4]=4; p[5]=0;
				t[0]=2; t[1]=1; t[4]=1; t[5]=2; break;
			case 2: p[0]=4; p[1]=6; p[4]=0; p[5]=2; break;
			case 3: p[0]=0; p[1]=4; p[4]=2; p[5]=6;
				t[0]=2; t[1]=1; t[4]=1; t[5]=2; break;
			}
			break;
		}
	}	
	public static String solve(int[] state){
		if(!ini){
			calcperm();
			ini=true;
		}
		sol=new StringBuffer();
		for(int l=0;!search(0,state[0],state[1],l,-1);l++);
		return sol.toString();
	}
	
	private static boolean search(int d,int q,int t,int l,int lm){
		//searches for solution, from position q|t, in l moves exactly. last move was lm, current depth=d
		if(l==0)return q==0 && t==0;
		if(perm[q]>l || twst[t]>l) return(false);
		int p,s,a,m;
		for(m=0;m<3;m++){
			if(m!=lm){
				p=q; s=t;
				for(a=0;a<3;a++){
					p=permmv[p][m];
					s=twstmv[s][m];
					if(search(d+1,p,s,l-1,m)){
						sol.append(turn[m]+suff[a]+" ");
						return(true);
					}
				}
			}
		}
		return(false);
	}
	private static void calcperm(){
		//calculate solving arrays
		//first permutation
		for(int p=0;p<5040;p++){
			perm[p]=-1;
			for(int m=0;m<3;m++)
				permmv[p][m]=getprmmv(p,m);
		}

		perm[0]=0;
		for(int l=0;l<=6;l++){
			//n=0;
			for(int p=0;p<5040;p++)
				if(perm[p]==l)
					for(int m=0;m<3;m++){
						int q=p;
						for(int c=0;c<3;c++){
							q=permmv[q][m];
							if(perm[q]==-1) {
								perm[q]=(byte) (l+1);
								//n++;
							}
						}
					}
		}

		//then twist
		for(int p=0;p<729;p++){
			twst[p]=-1;
			for(int m=0;m<3;m++)
				twstmv[p][m]=gettwsmv(p,m);
		}

		twst[0]=0;
		for(int l=0;l<=5;l++){
			//n=0;
			for(int p=0;p<729;p++)
				if(twst[p]==l)
					for(int m=0;m<3;m++){
						int q=p;
						for(int c=0;c<3;c++){
							q=twstmv[q][m];
							if(twst[q]==-1) {
								twst[q]=(byte) (l+1);
								//n++;
							}
						}
					}
		}
		//remove wait sign
	}
	
	private static void idxToPrm(int[] ps, int p){
		int q=p;
		for(int a=1;a<=7;a++){
			int b=q%a;
			q=(q-b)/a;
			for(int c=a-1;c>=b;c--) ps[c+1]=ps[c];
			ps[b]=7-a;
		}
	}
	private static int prmToIdx(int[] ps){
		int q=0;
		for(int a=0;a<7;a++){
			int b=0;
			for(int c=0;c<7;c++){
				if(ps[c]==a)break;
				if(ps[c]>a)b++;
			}
			q=q*(7-a)+b;
		}
		return q;
	}
	private static short getprmmv(int p,int m){
		//given position p<5040 and move m<3, return new position number
		//convert number into array;
		int[] ps=new int[8];
		idxToPrm(ps, p);
		//perform move on array
		int c;
		if(m==0){
			c=ps[0];ps[0]=ps[1];ps[1]=ps[3];ps[3]=ps[2];ps[2]=c;//U
		}else if(m==1){
			c=ps[0];ps[0]=ps[4];ps[4]=ps[5];ps[5]=ps[1];ps[1]=c;//R
		}else if(m==2){
			c=ps[0];ps[0]=ps[2];ps[2]=ps[6];ps[6]=ps[4];ps[4]=c;//F
		}
		return (short) prmToIdx(ps);
	}
	
	private static void idxToTws(int[] ps, int p){
		int q=p, d=0;
		for(int a=0;a<=5;a++){
			int c=q/3;
			int b=q-3*c;
			q=c;
			ps[a]=b;
			d-=b;if(d<0)d+=3;
		}
		ps[6]=d;
	}
	private static int twsToIdx(int[] ps){
		int q=0;
		for(int a=5;a>=0;a--){
			q=q*3+(ps[a]%3);
		}
		return q;
	}
	private static short gettwsmv(int p,int m){
		//given orientation p<729 and move m<3, return new orientation number
		//convert number into array;
		int[] ps=new int[7];
		idxToTws(ps, p);
		//perform move on array
		int c;
		if(m==0){
			c=ps[0];ps[0]=ps[1];ps[1]=ps[3];ps[3]=ps[2];ps[2]=c;//U
		}else if(m==1){
			c=ps[0];ps[0]=ps[4];ps[4]=ps[5];ps[5]=ps[1];ps[1]=c;//R
			ps[0]+=2; ps[1]++; ps[5]+=2; ps[4]++;
		}else if(m==2){
			c=ps[0];ps[0]=ps[2];ps[2]=ps[6];ps[6]=ps[4];ps[4]=c;//F
			ps[2]+=2; ps[0]++; ps[4]+=2; ps[6]++;
		}
		//convert array back to number
		return (short) twsToIdx(ps);
	}
}
