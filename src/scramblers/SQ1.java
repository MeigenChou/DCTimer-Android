package scramblers;

public class SQ1 {
	private static int seqlen=40;
	private static int[] seq;    // move sequences
	private static byte[] posit;    // piece array
	private static StringBuffer sb;
	public static int count;
	private static void scramble(){
		posit=new byte[]{0,0,1,2,2,3,4,4,5,6,6,7,8,9,9,10,11,11,12,13,13,14,15,15};
		int i,j,ls,f;
		ls=-1;
		seq=new int[40];
		f=0;
		for(i=0; i<seqlen; i++){
			do{
				if(ls==0){
					j=(int)(Math.random()*22)-11;
					if(j>=0) j++;
				}else if(ls==1){
					j=(int)(Math.random()*12)-11;
				}else if(ls==2){
					j=0;
				}else{
					j=(int)(Math.random()*23)-11;
				}
				// if past second twist, restrict bottom layer
			}while( (f>1 && j>=-6 && j<0) || domove((byte)j) );
			if(j>0) ls=1;
			else if(j<0) ls=2;
			else { ls=0; f++; }
			seq[i]=j;
		}
	}
	public static String scramblestring(){
		scramble();
		count=0;
		sb=new StringBuffer();
		int i,k,l=-1;
		for(i=0; i<seq.length; i++){
			k=seq[i];
			if(k==0){
				if(l==-1) sb.append("(0,0) / ");
				if(l==1) sb.append("0) / ");
				if(l==2) sb.append(") / ");
				l=0;
				count++;
			}else if(k>0){
				sb.append("(" + (k>6?k-12:k)+",");
				l=1;
			}else if(k<0){
				if(l<=0) sb.append("(0,");
				sb.append(k<=-6?k+12:k);
				l=2;
			}
		}
		if(l==1) sb.append("0");
		if(l!=0) sb.append(")");
		return sb.toString();
	}
	private static boolean domove(byte m){
		int i;
		byte c,f=m;
		byte[] t=new byte[12];
		//do move f
		if( f==0 ){
			for(i=0; i<6; i++){
				c=posit[i+12];
				posit[i+12]=posit[i+6];
				posit[i+6]=c;
			}
		}else if(f>0){
			f=(byte) (12-f);
			if( posit[f]==posit[f-1] ) return true;
			if( f<6 && posit[f+6]==posit[f+5] ) return true;
			if( f>6 && posit[f-6]==posit[f-7] ) return true;
			if( f==6 && posit[0]==posit[11] ) return true;
			for(i=0;i<12;i++) t[i]=posit[i];
			c=f;
			for(i=0;i<12;i++){
				posit[i]=t[c];
				if(c==11)c=0; else c++;
			}
		}else if(f<0){
			f=(byte) -f;
			if( posit[f+12]==posit[f+11] ) return true;
			if( f<6 && posit[f+18]==posit[f+17] ) return true;
			if( f>6 && posit[f+6]==posit[f+5] ) return true;
			if( f==6 && posit[12]==posit[23] ) return true;
			for(i=0;i<12;i++) t[i]=posit[i+12];
			c=f;
			for(i=0;i<12;i++){
				posit[i+12]=t[c];
				if(c==11)c=0; else c++;
			}
		}
		return false;
	}
	public static byte[] imagestr(){
		return posit;
	}
}
