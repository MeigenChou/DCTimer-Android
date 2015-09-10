package scrambler;

import java.util.regex.*;

public class SQ1 {
	private static int[] seq;    // move sequences
	private static byte[] posit;    // piece array
	private static StringBuffer sb;
	public static boolean mi;
	
	private static void scramble(){
		posit = new byte[] {0, 0, 1, 2, 2, 3, 4, 4, 5, 6, 6, 7,
				8, 9, 9, 10, 11, 11, 12, 13, 13, 14, 15, 15};
		int i, j, ls=-1, f=0;
		seq = new int[Scrambler.scrLen];
		for(i=0; i<Scrambler.scrLen; i++){
			do{
				if(ls == 0) {
					j = (int)(Math.random()*22)-11;
					if(j >= 0) j++;
				} else if(ls == 1)
					j = (int)(Math.random()*12)-11;
				else if(ls == 2) j = 0;
				else j = (int)(Math.random()*23)-11;
				// if past second twist, restrict bottom layer
			} while((f>1 && j>=-6 && j<0) || domove(j));
			if(j > 0) ls = 1;
			else if(j < 0) ls = 2;
			else {
				ls = 0;
				f++;
			}
			seq[i] = j;
		}
	}
	
	public static String scramblestring(){
		scramble();
		mi = true;
		sb = new StringBuffer();
		int i, k, l=-1;
		for(i=0; i<seq.length; i++){
			k = seq[i];
			if(k == 0) {
				if(l == -1) sb.append("(0,0) / ");
				else if(l == 1) sb.append("0) / ");
				else if(l == 2) sb.append(") / ");
				l=0;
				mi = !mi;
			} else if(k > 0) {
				sb.append("(" + (k>6?k-12:k) + ",");
				l = 1;
			} else if(k < 0) {
				if(l <= 0) sb.append("(0,");
				sb.append(k<=-6 ? k+12 : k);
				l = 2;
			}
		}
		if(l == 1) sb.append("0");
		if(l != 0) sb.append(")");
		return sb.toString();
	}
	
	private static boolean domove(int m){
		int i, c, f = m;
		byte[] t = new byte[12];
		//do move f
		if(f == 0) {
			for(i=0; i<6; i++) {
				c = posit[i + 12];
				posit[i + 12] = posit[i + 6];
				posit[i + 6] = (byte) c;
			}
		} else if(f > 0) {
			f = 12 - f;
			if(posit[f] == posit[f - 1]) return true;
			if(f < 6 && posit[f + 6] == posit[f + 5]) return true;
			if(f > 6 && posit[f - 6] == posit[f - 7]) return true;
			if(f == 6 && posit[0] == posit[11]) return true;
			for(i=0; i<12; i++) t[i] = posit[i];
			c = f;
			for(i=0; i<12; i++) {
				posit[i] = t[c];
				if(c == 11) c = 0;
				else c++;
			}
		} else if(f < 0) {
			f = -f;
			if(posit[f + 12] == posit[f + 11]) return true;
			if(f < 6 && posit[f + 18] == posit[f + 17]) return true;
			if(f > 6 && posit[f + 6] == posit[f + 5]) return true;
			if(f == 6 && posit[12] == posit[23]) return true;
			for(i=0; i<12; i++) t[i] = posit[i + 12];
			c = f;
			for(i=0; i<12; i++) {
				posit[i + 12] = t[c];
				if(c == 11) c = 0;
				else c++;
			}
		}
		return false;
	}
	
	public static byte[] imagestr(String[] scr){
		posit = new byte[]{0, 0, 1, 2, 2, 3, 4, 4, 5, 6, 6, 7,
				8, 9, 9, 10, 11, 11, 12, 13, 13, 14, 15, 15};
		mi = true;
		for(int i=0; i<scr.length; i++) {
			if(scr[i].equals("/")) {
				domove(0);
				mi = !mi;
			} else {
				Pattern p = Pattern.compile("\\((-?\\d+),(-?\\d+)\\)");
	            Matcher matcher = p.matcher(scr[i]);
	            matcher.find();
	            int top = Integer.parseInt(matcher.group(1));
	            if(top > 0) domove(top);
	            else if(top < 0) domove(top + 12);
	            int bottom = Integer.parseInt(matcher.group(2));
	            if(bottom > 0) domove(bottom - 12);
	            else if(bottom < 0) domove(bottom);
			}
		}
		return posit;
	}
}
