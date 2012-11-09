package solvers;

public class Pyraminx {
	private static byte[] colmap=new byte[91];
	private static byte[] colors= {0, 0, 1, 2, 3, 4}; //stores colours used
	private static StringBuffer sb;
	private static short[] posit=new short[36];
	private static short[] perm=new short[720];   // pruning table for edge permutation
	private static int[] twst=new int[2592];   // pruning table for edge orientation+twist
	private static short[][] permmv=new short[720][4]; // transition table for edge permutation
	private static short[][] twstmv=new short[2592][4]; // transition table for edge orientation+twist
	private static byte[] sol=new byte[12];
	private static short sollen;
	private static short[] pcperm=new short[6];
	private static short[] pcori;
	private static String[] scr={"U","L","R","B"};
	private static String[] scr2={"","'"};
	private static boolean ini=false;
	private static byte[] img=new byte[91];
	
	private static void init_colors() {
		colmap=new byte[]{
				1,1,1,1,1,0,2,0,3,3,3,3,3,
				0,1,1,1,0,2,2,2,0,3,3,3,0,
				0,0,1,0,2,2,2,2,2,0,3,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,4,4,4,4,4,0,0,0,0,
				0,0,0,0,0,4,4,4,0,0,0,0,0,
				0,0,0,0,0,0,4,0,0,0,0,0,0};;
	}
	public static String scramble() {
		int i, j;
		initbrd();
		if(!ini){
			calcperm();
			ini=true;
		}
		dosolve();
		sb=new StringBuffer();
		init_colors();
		
		byte[] temp={3,0,1,2};
		for (i=0;i<sollen;i++) {
			sb.append(scr[sol[i]&7]+scr2[sol[i]/8]+" ");
			picmove(temp[sol[i]&7],1+sol[i]/8);
		}
		String[] tips={"l","r","b","u"};
		for (i=0;i<4;i++) {
			j = (int)(Math.random() * 3);
			if (j < 2) {
				sb.append(tips[i] + scr2[j] + " ");
				picmove(4+i,1+j);
			}
		}
		//imageString();
		return sb.toString();
	}
	private static void initbrd(){
		for(int i=0;i<9;i++) {
			posit[i]=0;
			posit[9+i]=1;
			posit[18+i]=2;
			posit[27+i]=3;
		}
		sollen=0;
	}
	private static void dosolve(){
		int a,i,b,c,l,t=0,q=0;
		// Get a random permutation and orientation.
		int parity = 0;
		for(i=0;i<6;i++) pcperm[i] = (short) i;
		for (i=0;i<4;i++) {
			int other = (int)(i + (6-i) * Math.random());
			short temp = pcperm[i];
			pcperm[i] = pcperm[other];
			pcperm[other] = temp;
			if (i != other) parity++;
		}
		if (parity%2 == 1) {
			short temp = pcperm[4];
			pcperm[4] = pcperm[5];
			pcperm[5] = temp;
		}
		parity=0;
		pcori = new short[10];
		for (i=0;i<5;i++) {
			pcori[i] = (short)(2 * Math.random());
			parity += pcori[i];
		}
		pcori[5] = (short) (parity % 2);
		for (i=6;i<10;i++) {
			pcori[i] = (short)(3 * Math.random());
		}

		for(a=0;a<6;a++){
			b=0;
			for(c=0;c<6;c++){
				if(pcperm[c]==a)break;
				if(pcperm[c]>a)b++;
			}
			q=(short) (q*(6-a)+b);
		}
		//corner orientation
		for(a=9;a>=6;a--){
			t=t*3+pcori[a];
		}
		//edge orientation
		for(a=4;a>=0;a--){
			t=t*2+pcori[a];
		}

		// solve it
		if(q!=0 || t!=0){
			for(l=0;l<12;l++){  //allow solutions from 7 through 11 moves
				if(search(q,t,l,-1)) break;
			}
		}
	}
	private static boolean search(int q,int t,int l,int lm){
		//searches for solution, from position q|t, in l moves exactly. last move was lm, current depth=d
		if(l==0)return q==0 && t==0;
		if(perm[q]>l || twst[t]>l) return(false);
		int p,s,a,m;
		for(m=0;m<4;m++){
			if(m!=lm){
				p=q; s=t;
				for(a=0;a<2;a++){
					p=permmv[p][m];
					s=twstmv[s][m];
					sol[sollen++]=(byte) (m+8*a);
					if(search(p,s,l-1,m)) return(true);
					sollen--;
				}
			}
		}
		return(false);
	}
	private static void calcperm(){
		int c,q,l,m,p;
		//calculate solving arrays
		//first permutation
		// initialise arrays
		for(p=0;p<720;p++){
			perm[p]=-1;
			for(m=0;m<4;m++)
				permmv[p][m]=(short) getprmmv(p,m);
		}
		//fill it
		perm[0]=0;
		for(l=0;l<=6;l++){
			for(p=0;p<720;p++)
				if(perm[p]==l)
					for(m=0;m<4;m++){
						q=p;
						for(c=0;c<2;c++){
							q=permmv[q][m];
							if(perm[q]==-1) perm[q]=(short) (l+1);
						}
					}
		}
		//then twist
		// initialise arrays
		for(p=0;p<2592;p++){
			twst[p]=-1;
			for(m=0;m<4;m++)
				twstmv[p][m]=(short) gettwsmv(p,m);
		}
		//fill it
		twst[0]=0;
		for(l=0;l<=5;l++)
			for(p=0;p<2592;p++)
				if(twst[p]==l)
					for(m=0;m<4;m++){
						q=p;
						for(c=0;c<2;c++){
							q=twstmv[q][m];
							if(twst[q]==-1) { twst[q]=l+1;}
						}
					}
	}
	private static int getprmmv(int p,int m){
		//given position p<720 and move m<4, return new position number
		//convert number into array
		int a,b,c,q=p;
		byte[] ps=new byte[7];
		for(a=1;a<=6;a++){
			c=q/a;
			b=q-a*c;
			q=c;
			for(c=a-1;c>=b;c--) ps[c+1]=ps[c];
			ps[b]=(byte) (6-a);
		}
		//perform move on array
		if(m==0){
			cycle3(ps, 0, 3, 1);//U
		}else if(m==1){
			cycle3(ps, 1, 5, 2);//L
		}else if(m==2){
			cycle3(ps, 0, 2, 4);//R
		}else if(m==3){
			cycle3(ps, 3, 4, 5);//B
		}
		//convert array back to number
		q=0;
		for(a=0;a<6;a++){
			b=0;
			for(c=0;c<6;c++){
				if(ps[c]==a)break;
				if(ps[c]>a)b++;
			}
			q=q*(6-a)+b;
		}
		return(q);
	}
	private static int gettwsmv(int p,int m){
		//given position p<2592 and move m<4, return new position number
		//convert number into array;
		int a,d=0,b,c;
		byte[] ps=new byte[10];
		int q=p;
		//first edge orientation
		for(a=0;a<=4;a++){
			ps[a]=(byte) (q&1);
			q>>=1;
		d^=ps[a];
		}
		ps[5]=(byte) d;
		//next corner orientation
		for(a=6;a<=9;a++){
			c=q/3;
			b=q-3*c;
			q=c;
			ps[a]=(byte) b;
		}
		//perform move on array
		if(m==0){
			//U
			ps[6]++; if(ps[6]==3) ps[6]=0;
			cycle3(ps, 0, 3, 1);
			ps[1]^=1;ps[3]^=1;
		}else if(m==1){
			//L
			ps[7]++; if(ps[7]==3) ps[7]=0;
			cycle3(ps, 1, 5, 2);
			ps[2]^=1; ps[5]^=1;
		}else if(m==2){
			//R
			ps[8]++; if(ps[8]==3) ps[8]=0;
			cycle3(ps, 0, 2, 4);
			ps[0]^=1; ps[2]^=1;
		}else if(m==3){
			//B
			ps[9]++; if(ps[9]==3) ps[9]=0;
			cycle3(ps, 3, 4, 5);
			ps[3]^=1; ps[4]^=1;
		}
		//convert array back to number
		q=0;
		//corner orientation
		for(a=9;a>=6;a--){
			q=q*3+ps[a];
		}
		//corner orientation
		for(a=4;a>=0;a--){
			q=q*2+ps[a];
		}
		return(q);
	}
	private static void picmove(int type, int direction){
		switch(type) {
		case 0: // L
			rotate3(14,58,18, direction);
			rotate3(15,57,31, direction);
			rotate3(16,70,32, direction);
			rotate3(30,28,56, direction);
			break;
		case 1: // R
			rotate3(32,72,22, direction);
			rotate3(33,59,23, direction);
			rotate3(20,58,24, direction);
			rotate3(34,60,36, direction);
			break;
		case 2: // B
			rotate3(14,10,72, direction);
			rotate3( 1,11,71, direction);
			rotate3( 2,24,70, direction);
			rotate3( 0,12,84, direction);
			break;
		case 3: // U
			rotate3( 2,18,22, direction);
			rotate3( 3,19, 9, direction);
			rotate3(16,20,10, direction);
			rotate3( 4, 6, 8, direction);
			break;
		case 4: // l
			rotate3(30,28,56, direction);
			break;
		case 5: // r
			rotate3(34,60,36, direction);
			break;
		case 6: // b
			rotate3( 0,12,84, direction);
			break;
		case 7: // u
			rotate3( 4, 6, 8, direction);
			break;
		}
	}
	private static void rotate3(int v1,int v2,int v3,int clockwise)
	{
		if(clockwise == 2) {
			cycle3(colmap, v3, v2, v1);
		} else {
			cycle3(colmap, v1, v2, v3);
		}
	}
	private static void cycle3(byte[] arr,int i1,int i2,int i3) {
		byte c = arr[i1];
		arr[i1] = arr[i2];
		arr[i2] = arr[i3];
		arr[i3] = c;
	}
	public static byte[] imageString() {
		int x,d=0;
		for(x = 0; x < 91; x++)
			img[d++] = colors[colmap[x]];
		return img;
	}
	private static String moveIdx="LRBUlrbu";
	public static byte[] imageString(String scr) {
		String[] s=scr.split(" ");
		init_colors();
		int turn, suff;
		for(int i=0; i<s.length; i++){
			suff=s[i].length();
			if(suff>0){
				turn=moveIdx.indexOf(s[i].charAt(0));
				picmove(turn, suff);
			}
		}
		return imageString();
	}
}
