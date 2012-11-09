package scramblers;

public class Clock {
	private static byte[][] moves={{1,1,1,1,1,1,0,0,0,  -1,0,-1,0,0,0,0,0,0},
		{0,1,1,0,1,1,0,1,1,  -1,0,0,0,0,0,-1,0,0},
		{0,0,0,1,1,1,1,1,1,  0,0,0,0,0,0,-1,0,-1},
		{1,1,0,1,1,0,1,1,0,  0,0,-1,0,0,0,0,0,-1},
		{0,0,0,0,0,0,1,0,1,  0,0,0,-1,-1,-1,-1,-1,-1},
		{1,0,0,0,0,0,1,0,0,  0,-1,-1,0,-1,-1,0,-1,-1},
		{1,0,1,0,0,0,0,0,0,  -1,-1,-1,-1,-1,-1,0,0,0},
		{0,0,1,0,0,0,0,0,1,  -1,-1,0,-1,-1,0,-1,-1,0},
		{0,1,1,1,1,1,1,1,1,  -1,0,0,0,0,0,-1,0,-1},
		{1,1,0,1,1,1,1,1,1,  0,0,-1,0,0,0,-1,0,-1},
		{1,1,1,1,1,1,1,1,0,  -1,0,-1,0,0,0,0,0,-1},
		{1,1,1,1,1,1,0,1,1,  -1,0,-1,0,0,0,-1,0,0},
		{1,1,1,1,1,1,1,1,1,  -1,0,-1,0,0,0,-1,0,-1},
		{1,0,1,0,0,0,1,0,1,  -1,-1,-1,-1,-1,-1,-1,-1,-1}};
	private static StringBuffer sb;
	private static byte[] posit = new byte[18];
	private static byte[] pegs = new byte[4];
	private static void prtrndpin(){
		for(int i=0; i<4; i++){
			pegs[i] = (byte)(Math.random()*2);
			if(pegs[i]==0)sb.append("U");else sb.append("d");
		}
	}
	public static String scramble(){
		byte[] seq=new byte[14];
		byte i,j;
		for(i=0;i<18;i++)posit[i]=0;
		for(i=0; i<14; i++){
			seq[i] = (byte)((Math.random()*12)-5);
		}
		for( i=0; i<14; i++){
			for( j=0; j<18; j++){
				posit[j]+=seq[i]*moves[i][j];
			}
		}
		for( j=0; j<18; j++){
			posit[j]%=12;
			while( posit[j]<=0 ) posit[j]+=12;
		}
		sb=new StringBuffer();
		sb.append("UUdd u="+seq[0]+",d="+seq[4]+" / ");
		sb.append("dUdU u="+seq[1]+",d="+seq[5]+" / ");
		sb.append("ddUU u="+seq[2]+",d="+seq[6]+" / ");
		sb.append("UdUd u="+seq[3]+",d="+seq[7]+" / ");
		sb.append("dUUU u="+seq[8]+" / ");
		sb.append("UdUU u="+seq[9]+" / ");
		sb.append("UUUd u="+seq[10]+" / ");
		sb.append("UUdU u="+seq[11]+" / ");
		sb.append("UUUU u="+seq[12]+" / ");
		sb.append("dddd d="+seq[13]+" / ");
		prtrndpin();;
		return sb.toString();
	}
	public static byte[] posit(){
		return posit;
	}
	public static byte[] pegs(){
		return pegs;
	}
}
