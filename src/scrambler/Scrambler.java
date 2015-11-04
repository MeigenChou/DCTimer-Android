package scrambler;

import java.util.Random;

import solver.*;
import cs.sq12phase.FullCube;
import cs.min2phase.Tools;
import android.graphics.*;
import android.graphics.Paint.Align;

import com.dctimer.Configs;
import com.dctimer.DCTimer;

public class Scrambler {
	public static final int TYPE_SQ1 = 1;
	public static final int TYPE_CLK = 12;
	public static final int TYPE_MINX = 18;
	public static final int TYPE_PYR = 17;
	public static final int TYPE_SKW = 16;
	public static final int TYPE_FLP = 13;
	public static final int TYPE_DMN = 14;
	public static final int TYPE_TOW = 15;
	public static final int TYPE_RTW = 19;
	public static final int TYPE_334 = 20;
	public static final int TYPE_335 = 21;
	public static final int TYPE_336 = 22;
	public static final int TYPE_337 = 23;
	public static final int TYPE_GEAR = 24;
	public static final int TYPE_15P = 25;
	public static final int TYPE_HLCT = 26;
	public static final int TYPE_UFO = 27;
	
	public String crntScr;	// 当前打乱
	private static short[][] defScrLen = {
		{0, 15, 15, 0, 0, 0, 0, 0, 0, 0},
		{25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0},
		{40, 40, 40, 8, 40, 0},
		{60, 60, 8},
		{80, 80, 80, 8},
		{100, 100, 100, 8},
		{70, 70},
		{0, 15},
		{40, 20, 0, 0},
		{0, 0, 0, 0},
		{0, 15},
		{15, 25, 0, 25, 0, 0, 0, 40, 25, 40, 40, 120, 140, 140, 140},
		{25, 25},
		{0, 10},
		{25, 25, 25},
		{80, 80},
		{25, 40, 20, 20, 25, 25},
		{0, 0, 0, 25, 25, 25, 0, 15},
		{30, 25},
		{30, 20},
		{0, 0, 0, 0, 5},
		{0, 0, 60, 0, 0, 0, 0, 0, 70, 0, 0, 0, 0, 80, 100, 0, 60, 5},
	};
	public static String[] rotateStr = {"", "3Fw", "3Fw'", "3Fw 3Uw", "3Fw 3Uw2", "3Fw 3Uw'", "3Fw' 3Uw", "3Fw' 3Uw2", "3Fw' 3Uw'", "3Rw", "3Rw2", "3Rw'",
		"3Rw 3Uw", "3Rw 3Uw2", "3Rw 3Uw'", "3Rw2 3Uw", "3Rw2 3Uw2", "3Rw2 3Uw'", "3Rw' 3Uw", "3Rw' 3Uw2", "3Rw' 3Uw'", "3Uw", "3Uw2", "3Uw'"};
	public static int[] rotateIdx = {-1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0};
	private static String[] cubesuff = {"", "2", "'"};
	
	private DCTimer dct;
	public String sc;
	public int viewType;
	public static int scrLen = 0;
	private Cube222 cube2 = new Cube222();
	private cs.min2phase.Search cube3 = new cs.min2phase.Search();
	private cs.threephase.Search cube4 = new cs.threephase.Search();
	private cs.sq12phase.Search cubesq = new cs.sq12phase.Search();
	private Pyraminx pyram = new Pyraminx();
	private Skewb skewb = new Skewb();
	private static Random r = new Random();
	
	public Scrambler(DCTimer dct) {
		this.dct = dct;
	}
	
	public String getScramble(int n, boolean ch) {
		String[] suff = {"", "'"};
		String scr;
		StringBuffer sb;
		if(ch) {
			if(n < 0) scrLen = defScrLen[21][n&31];
			else scrLen = defScrLen[n>>5][n&31];
		}
		switch(n) {
		case -32:	//三速
		case -27:	//三单
		case -26:	//最少步
		case -25:	//脚拧
			scr = cube333();
			viewType = scr.startsWith("Error") ? 0 : 3;
			extSol3(Configs.stSel[5], scr);
			break;
		case -31:	//四速
			scr = cube4.randomState(false);
			viewType = scr.startsWith("Error") ? 0 : 4;
			break;
		case -30:	//五速
			scr = cube(5); viewType = 5; break;
		case -29:	//二阶
			scr = cube2.scramble(4); viewType = 2; break;
		case -28:	//三盲
			String cube = Tools.randomCube();
			scr = cube3.solution(cube, true, r);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case -24:	//五魔
			scr = Megaminx.scramblestring(); viewType = TYPE_MINX; break;
		case -23:	//金字塔
			scr = pyram.scramble(6); viewType = TYPE_PYR; break;
		case -22:	//SQ1
			scr = cubesq.scramble(FullCube.randomCube(), 11); viewType = TYPE_SQ1; break;
		case -21:	//魔表
			scr = Clock.scramble(); viewType = TYPE_CLK; break;
		case -20:	//斜转
			scr = skewb.scramble(7); viewType = TYPE_SKW; break;
		case -19:	//六阶
			scr = megascramble(new String[][]{{"U","D","Uw","Dw","3Uw"}, {"R","L","Rw","Lw","3Rw"}, {"F","B","Fw","Bw","3Fw"}}, cubesuff);
			viewType = 6; break;
		case -18:	//七阶
			scr = megascramble(new String[][]{{"U","D","Uw","Dw","3Uw","3Dw"}, {"R","L","Rw","Lw","3Rw","3Lw"}, {"F","B","Fw","Bw","3Fw","3Bw"}}, cubesuff);
			viewType = 7; break;
		case -17:	//四盲
			scr = cube4.randomState(true); viewType = 4; break;
		case -16:	//五盲
			scr = cube(5);
			String[] moves = scr.split(" ");
			String lm = moves[moves.length-1];
			int idx;
			if(lm.startsWith("Bw")) idx = 2;
			else if(lm.startsWith("Lw")) idx = 1;
			else if(lm.startsWith("Dw")) idx = 0;
			else idx = 4;
			int rot;
			do {
				rot = r.nextInt(24);
			} while(idx == rotateIdx[rot]);
			scr += " " + rotateStr[rot];
			viewType = 5; break;
		case -15:	//多盲
			sb = new StringBuffer();
			for(int j=1; j<=scrLen; j++) {
				String face = Tools.randomCube();
				sb.append(j + ") " + cube3.solution(face, true, r));
				if(j < scrLen) sb.append("\n");
			}
			scr = sb.toString(); viewType = 0; break;
		case 0: //2阶
			scr = cube2.randomState();
			viewType = 2;
			if(Configs.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, Configs.stSel[6]);
			break;
		case 1:
			scr = cube(2);
			viewType = 2;
			if(Configs.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, Configs.stSel[6]);
			break;
		case 2:
			scr = megascramble(new String[][][]{{{"U","D"}}, {{"R","L"}}, {{"F","B"}}}, cubesuff);
			viewType = 2;
			if(Configs.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, Configs.stSel[6]);
			break;
		case 3:
			scr = cube2.randomCLL(); viewType = 2; break;
		case 4:
			scr = cube2.randomEG1(); viewType = 2; break;
		case 5:
			scr = cube2.randomEG2(); viewType = 2; break;
		case 6:
			scr = cube2.randomXLL(); viewType = 2; break;
		case 7:
			scr = cube2.egScr(Configs.egtype, Configs.egolls); viewType = 2; break;
		case 8:
			scr = cube2.randomTCLL(1); viewType = 2; break;
		case 9:
			scr = cube2.randomTCLL(2); viewType = 2; break;
		case 10:
			scr = cube2.randomTEG1(1); viewType = 2; break;
		case 11:
			scr = cube2.randomTEG1(2); viewType = 2; break;
		case 12:
			scr = cube2.randomTEG2(1); viewType = 2; break;
		case 13:
			scr = cube2.randomTEG2(2); viewType = 2; break;
		case 32: //3阶
			scr = cube(3); viewType = 3;
			extSol3(Configs.stSel[5], scr);
			break;
		case 33:
			scr = cube333();
			viewType = scr.startsWith("Error") ? 0 : 3;
			extSol3(Configs.stSel[5], scr);
			break;
		case 34:
			scr = cube3.solution(Tools.randomCrossSolved(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 35:
			scr = cube3.solution(Tools.randomLastLayer(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 36:
			scr = cube3.solution(Tools.randomPLL(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 37:
			scr = cube3.solution(Tools.randomCornerSolved(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3;
			extSol3(Configs.stSel[5], scr);
			break;
		case 38:
			scr = cube3.solution(Tools.randomEdgeSolved(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 39:
			scr = cube3.solution(Tools.randomLastSlot(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 40:
			scr = cube3.solution(Tools.randomZBLastLayer(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 41:
			scr = cube3.solution(Tools.randomEdgeOfLastLayer(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 42:
			scr = cube3.solution(Tools.randomCornerOfLastLayer(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 43:
			switch (r.nextInt(4)) {
			case 0:
				scr = cube3.solution(Tools.randomState(new byte[]{0,1,2,3,4,5,6,7}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 21, 10000, 100, 2);
				break;
			case 1:
				scr = cube3.solution(Tools.randomState(new byte[]{3,2,6,7,0,1,5,4}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 21, 10000, 100, 2) + "x'";
				break;
			case 2:
				scr = cube3.solution(Tools.randomState(new byte[]{7,6,5,4,3,2,1,0}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 21, 10000, 100, 2) + "x2";
				break;
			default:
				scr = cube3.solution(Tools.randomState(new byte[]{4,5,1,0,7,6,2,3}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 21, 10000, 100, 2) + "x";
				break;
			}
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 44:
			switch (r.nextInt(4)) {
			case 0:
				scr = cube3.solution(Tools.randomState(new byte[]{-1,-1,-1,-1,4,5,6,7}, new byte[]{-1,-1,-1,-1,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 21, 10000, 100, 2);
				break;
			case 1:
				scr = cube3.solution(Tools.randomState(new byte[]{3,2,-1,-1,0,1,-1,-1}, new byte[]{2,1,-1,-1,1,2,-1,-1}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 21, 10000, 100, 2) + "x'";
				break;
			case 2:
				scr = cube3.solution(Tools.randomState(new byte[]{7,6,5,4,-1,-1,-1,-1}, new byte[]{0,0,0,0,-1,-1,-1,-1}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 21, 10000, 100, 2) + "x2";
				break;
			default:
				scr = cube3.solution(Tools.randomState(new byte[]{-1,-1,1,0,-1,-1,2,3}, new byte[]{-1,-1,2,1,-1,-1,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 21, 10000, 100, 2) + "x";
				break;
			}
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 45:
			scr = cube3.solution(Tools.randomEdgePerm(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 46:
			scr = cube3.solution(Tools.randomEdgeOri(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 47:
			scr = cube3.solution(Tools.randomCornerPerm(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 48:
			scr = cube3.solution(Tools.randomCornerOri(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 49:
			scr = cube3.solution(Tools.randomPermutation(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 50:
			scr = cube3.solution(Tools.randomOrientation(), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 51:
			scr = cube3.solution(Tools.randomEasyCross(scrLen), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; 
			extSol3(Configs.stSel[5], scr);
			break;
		case 52:
			scr = cube3.solution(Tools.randomState(Tools.STATE_SOLVED, new byte[]{-1,-1,-1,-1,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,5,6,7,8,9,10,11}, Tools.STATE_SOLVED), 21, 10000, 100, 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 64: //4阶
			scr = cube(4); viewType = 4; break;
		case 65:
			scr = cube444(); viewType = 4; break;
		case 66:
			scr = yj4x4(); viewType = 4; break;
		case 67:
			scr = edgescramble("Rw Bw2", new String[]{"Bw2 Rw'", "Bw2 U2 Rw U2 Rw U2 Rw U2 Rw"}, new String[]{"Uw"});
			viewType = 4; break;
		case 68:
			scr = megascramble(new String[][]{{"U","u"},{"R","r"}}, cubesuff);
			viewType = 4; break;
		case 69:	//TODO
			scr = cube4.randomState(false); viewType = 4; break;
		case 96: //5阶
			scr = cube(5); viewType = 5; break;
		case 97:
			scr = cube555(); viewType = 5; break;
		case 98:
			scr = edgescramble("Rw R Bw B", new String[] {"B' Bw' R' Rw'", "B' Bw' R' U2 Rw U2 Rw U2 Rw U2 Rw"}, new String[] {"Uw", "Dw"});
			viewType = 5; break;
		case 128: //6阶
			scr = cube(6); viewType = 6; break;
		case 129:
			scr = cube666(); viewType = 6; break;
		case 130:
			scr = megascramble(new String[][] {{"U","D","U\u00B2","D\u00B2","U\u00B3"}, {"R","L","R\u00B2","L\u00B2","R\u00B3"}, {"F","B","F\u00B2","B\u00B2","F\u00B3"}}, cubesuff);
			viewType = 6; break;
		case 131:
			scr = edgescramble("3r r 3b b", new String[]{"3b' b' 3r' r'", "3b' b' 3r' U2 r U2 r U2 r U2 r", "3b' b' r' U2 3r U2 3r U2 3r U2 3r", "3b' b' r2 U2 3R U2 3R U2 3R U2 3R "},
					new String[]{"u","3u","d"});
			viewType = 6; break;
		case 160: //7阶
			scr = cube(7); viewType = 7; break;
		case 161:
			scr = cube777(); viewType = 7; break;
		case 162:
			scr = megascramble(new String[][]{{"U","D","U\u00B2","D\u00B2","U\u00B3","D\u00B3"}, {"R","L","R\u00B2","L\u00B2","R\u00B3","L\u00B3"}, {"F","B","F\u00B2","B\u00B2","F\u00B3","B\u00B3"}}, cubesuff);
			viewType = 7; break;
		case 163:
			scr = edgescramble("3r r 3b b", new String[]{"3b' b' 3r' r'", "3b' b' 3r' U2 r U2 r U2 r U2 r", "3b' b' r' U2 3r U2 3r U2 3r U2 3r", "3b' b' r2 U2 3R U2 3R U2 3R U2 3R"},
					new String[]{"u","3u","3d","d"});
			viewType = 7; break;
		case 192: //五魔
			scr = Megaminx.scramblestring(); viewType = TYPE_MINX; break;
		case 193:
			scr = oldminxscramble(); viewType = 0; break;
		case 224: //金字塔
			scr = pyram.scramble(); viewType = TYPE_PYR; break;
		case 225:
			String[][] ss = {{"","b ","b' "}, {"","l ","l' "}, {"","u ","u' "}, {"","r ","r' "}};
			int cnt = 0;
			int[] rnd = new int[4];
			for(int i=0; i<4; i++) {
				rnd[i] = r.nextInt(3);
				if(rnd[i] > 0) cnt++;
				if(cnt >= scrLen) break;
			}
			scrLen -= cnt;
			scr = ss[0][rnd[0]] + ss[1][rnd[1]] + ss[2][rnd[2]] + ss[3][rnd[3]] + megascramble(new String[][]{{"U"}, {"L"}, {"R"}, {"B"}}, suff);
			scrLen += cnt;
			viewType = TYPE_PYR;
			break;
		case 256:  //SQ1
			scr = SQ1.scramblestring();
			if(Configs.stSel[12] > 0) sc = " " + (Configs.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType = TYPE_SQ1; break;
		case 257:
			scr = sq1_scramble(0);
			if(Configs.stSel[12] > 0) sc = " " + (Configs.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType = TYPE_SQ1; break;
		case 258:
			scr = cubesq.solution(FullCube.randomCube());
			if(Configs.stSel[12] > 0) sc = " " + (Configs.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType = TYPE_SQ1; break;
		case 259:
			scr = cubesq.solution(FullCube.randomCube(1037)); viewType = TYPE_SQ1; break;
		case 288:	//魔表
			scr = Clock.scramble(); viewType = TYPE_CLK; break;
		case 289:
			scr = Clock.scrambleOld(false); viewType = TYPE_CLK; break;
		case 290:
			scr = Clock.scrambleOld(true); viewType = TYPE_CLK; break;
		case 291:
			scr = Clock.scrambleEpo(); viewType = TYPE_CLK; break;
		case 320:	//Skewb
			scr = skewb.scramble(); viewType = TYPE_SKW; break;
		case 321:
			scr = megascramble(new String[][]{{"R"}, {"U"}, {"L"}, {"B"}}, suff);
			viewType = TYPE_SKW; break;
		case 352:	//MxNxL
			scr = megascramble(new String[][]{{"R","L"}, {"U","D"}}, new String[]{"2"}); viewType = TYPE_FLP; break;
		case 353:
			scr = megascramble(new String[][]{{"R","L"}, {"U","D"}}, cubesuff); viewType = 0; break;
		case 354:
			scr = Floppy.solve(r); viewType = TYPE_FLP; break;
		case 355:
			scr = megascramble(new String[][][]{{{"R2","L2","R2 L2"}}, {{"U","U'","U2"}}, {{"F2","B2","F2 B2"}}}, null);
			viewType = 14; break;
		case 356:
			scr = Domino.scramble(r); viewType = TYPE_DMN; break;
		case 357:
			scr = Tower.solve(r); viewType = TYPE_TOW; break;
		case 358:
			scr = RTower.scramble(r); viewType = 0; break;
		case 359:	//334
			scr = megascramble(new String[][][]{{{"U", "U'", "U2"}, {"u", "u'", "u2"}}, {{"R2","L2","M2"}}, {{"F2","B2","S2"}}}, null);
			viewType = 0; break;
		case 360:	//335
			scr = megascramble(new String[][][]{{{"U","U'","U2"}, {"D", "D'", "D2"}}, {{"R2"}, {"L2"}}, {{"F2"}, {"B2"}}}, null)
					+ "/ " + Cube.scramblestring(3, 25);
			viewType = 0; break;
		case 361:	//336
			scr = megascramble(new String[][][]{{{"U", "U'", "U2"}, {"u", "u'", "u2"}, {"3u", "3u'", "3u2"}}, {{"R2","L2","M2"}}, {{"F2","B2","S2"}}}, null);
			viewType = 0; break;
		case 362:	//337
			scr = megascramble(new String[][][]{{{"U", "U'", "U2"}, {"u", "u'", "u2"}, {"D", "D'", "D2"}, {"d", "d'", "d2"}}, {{"R2"}, {"L2"}}, {{"F2"}, {"B2"}}}, null)
					+ "/ " + Cube.scramblestring(3, 25);
			viewType = 0; break;
		case 363:
			scr = cube(8); viewType = 8; break;
		case 364:
			scr = cube(9); viewType = 9; break;
		case 365:
			scr = cube(10); viewType = 10; break;
		case 366:
			scr = cube(11); viewType = 11; break;
		case 384:	//Cmetrick
			scr = megascramble(new String[][][]{{{"U<","U>","U2"}, {"E<","E>","E2"}, {"D<","D>","D2"}}, {{"R^","Rv","R2"}, {"M^","Mv","M2"}, {"L^","Lv","L2"}}}, null);
			viewType = 0; break;
		case 385:
			scr = megascramble(new String[][][]{{{"U<","U>","U2"}, {"D<","D>","D2"}}, {{"R^","Rv","R2"}, {"L^","Lv","L2"}}}, null);
			viewType = 0; break;
		case 416:	//齿轮
			scr = Gear.solve(r); viewType = 0; break;
		case 417:
			scr = megascramble(new String[][]{{"U"}, {"R"}, {"F"}}, new String[]{"", "2", "3", "4", "5", "6", "'", "2'", "3'", "4'", "5'"});
			viewType = 0; break;
		case 448:	//Siamese Cube
			String[][] turn = {{"U","u"}, {"R","r"}};
			scr = megascramble(turn, cubesuff) + "z2 " + megascramble(turn, cubesuff);
			viewType = 0; break;
		case 449:
			turn = new String[][]{{"R","r"}, {"U"}};
			scr = megascramble(turn, cubesuff) + "z2 " + megascramble(turn, cubesuff);
			viewType = 0; break;
		case 450:
			turn = new String[][]{{"U"}, {"R"}, {"F"}};
			scr = megascramble(turn, cubesuff) + "z2 y " + megascramble(turn, cubesuff);
			viewType = 0; break;
		case 480:	//15puzzles
			scr = do15puzzle(false); viewType = 0; break;
		case 481:
			scr = do15puzzle(true); viewType = 0; break;
		case 512:	//Other
			scr = LatchCube.scramble(); viewType = 0; break;
		case 513:
			scr = helicubescramble(); viewType = 0; break;
		case 514:	//Sq2
			int i = 0;
			sb = new StringBuffer();
			while (i < scrLen) {
				int rndu = r.nextInt(12) - 5;
				int rndd = r.nextInt(12) - 5;
				if (rndu != 0 || rndd != 0) {
					i++;
					sb.append( "(" + rndu + "," + rndd + ") / ");
				}
			}
			scr = sb.toString(); viewType = 0; break;
		case 515:	//super sq1
			scr = ssq1t_scramble(); viewType = 0; break;
		case 516:	//UFO
			scr = megascramble(new String[][][]{{{"A"}}, {{"B"}}, {{"C"}}, {{"U","U'","U2'","U2","U3"}}}, null);
			viewType = 0; break;
		case 517:	//FTO
			scr = megascramble(new String[][]{{"U","D"}, {"F","B"}, {"L","BR"}, {"R","BL"}}, suff); viewType = 0; break;
		case 544:	//3x3x3 subsets
			//turn2 = new String[][]{{"U"}, {"R"}};
			//scr = megascramble(turn2, csuff);
			scr = CubeRU.solve(r); viewType = 3; break;
		case 545:
			//turn2 = new String[][]{{"U"}, {"L"}};
			//scr = megascramble(turn2, csuff);
			scr = CubeRU.solve(r).replace('R', 'L');
			viewType = 3; break;
		case 546:
			//turn2 = new String[][]{{"U"}, {"M"}};
			//scr = megascramble(turn2, csuff);
			scr = RouxMU.solve(r); viewType = 3; break;
		case 547:
			scr = megascramble(new String[][]{{"U"}, {"R"}, {"F"}}, cubesuff); viewType = 3; break;
		case 548:
			scr = megascramble(new String[][]{{"R", "L"}, {"U"}}, cubesuff); viewType = 3; break;
		case 549:
			scr = megascramble(new String[][]{{"R", "r"}, {"U"}}, cubesuff); viewType = 3; break;
		case 550:
			//turn2 = new String[][]{{"U","D"}, {"R","L"}, {"F","B"}};
			//suff = new String[]{"2"};
			//scr = megascramble(turn2, suff, 25);
			scr = HalfTurn.solve(r); viewType = 3; break;
		case 551:	//LSLL
			scr = megascramble(new String[][][]{{{"R U R'","R U2 R'","R U' R'"}}, {{"F' U F","F' U2 F","F' U' F"}}, {{"U","U2","U'"}}}, null);
			viewType = 3; break;
		case 576:	//Bandaged Cube
			scr = bicube(); viewType = 0; break;
		case 577:
			scr = sq1_scramble(2); viewType = TYPE_SQ1; break;
		case 608:	//五魔子集
			scr = megascramble(new String[][]{{"U"}, {"R"}}, new String[]{"", "2", "2'", "'"}); viewType = 0; break;
		case 609:
			scr = megascramble(new String[][][]{{{"R U R'","R U2 R'","R U' R'","R U2' R'"}}, {{"F' U F","F' U2 F","F' U' F","F' U2' F"}}, {{"U","U2","U'","U2'"}}}, null); viewType = 0; break;
		case 640:	//连拧
			scr = "2) " + cube2.randomState() + "\n3) " + cube333() + "\n4) " + cube444();
			viewType = 0; break;
		case 641:
			scr = "2) " + cube2.randomState() + "\n3) " + cube333() + "\n4) " + cube444() + "\n5) " + cube555();
			viewType = 0; break;
		case 642:
			scr = "2) " + cube2.randomState() + "\n3) " + cube333() + "\n4) " + cube444()
				+"\n5) " + cube555() + "\n6) " + cube666();
			viewType = 0; break;
		case 643:
			scr = "2) " + cube2.randomState() + "\n3) " + cube333() + "\n4) " + cube444()
				+"\n5) " + cube555() + "\n6) " + cube666() + "\n7) " + cube777();
			viewType = 0; break;
		case 644:
			sb = new StringBuffer();
			for(int j=1; j<=scrLen; j++) {
				sb.append(j + ") " + cube333());
				if(j < scrLen) sb.append("\n");
			}
			scr = sb.toString(); viewType = 0; break;
		default:
			scr = " ";
		}
		return scr;
	}
	
	private String cube(int n) {
		return Cube.scramblestring(n, scrLen);
	}
	
	private String cube333() {
		return cube3.solution(Tools.randomCube(), 21, 10000, 100, 2);
	}
	
	private String cube444() {
		return megascramble(new String[][]{{"U", "D", "u"}, {"R", "L", "r"}, {"F", "B", "f"}}, cubesuff, 40);
	}
	
	private String cube555() {
		return megascramble(new String[][]{{"U", "D", "u", "d"}, {"R", "L", "r", "l"}, {"F", "B", "f", "b"}}, cubesuff, 60);
	}
	
	private String cube666() {
		return megascramble(new String[][]{{"U", "D", "u", "d", "3u"}, {"R", "L", "r", "l", "3r"}, {"F", "B", "f", "b", "3f"}}, cubesuff, 80);
	}
	
	private String cube777() {
		return megascramble(new String[][]{{"U","D","u","d","3u","3d"}, {"R","L","r","l","3r","3l"}, {"F","B","f","b","3f","3b"}}, cubesuff, 100);
	}
	
	private String rndEl(String[] x) {
		return x[r.nextInt(x.length)];
	}

	private String edgescramble(String start, String[] end, String[] moves) {
		int u = 0, d = 0;
		int[] movemis = new int[moves.length];
		String[][] triggers = {{"R","R'"}, {"R'","R"}, {"L","L'"}, {"L'","L"}, {"F'","F"}, {"F","F'"}, {"B","B'"}, {"B'","B"}};
		String[] ud = {"U","D"};
		String ss = start;
		String v = "";
		// initialize move misalignments
		for (int i=0; i<moves.length; i++)
			movemis[i] = 0;
		for (int i=0; i<scrLen; i++) {
			// apply random moves
			boolean done = false;
			while (!done) {
				v = "";
				for (int j=0; j<moves.length; j++) {
					int x = r.nextInt(4);
					movemis[j] += x;
					if (x!=0) {
						done = true;
						v += " " + moves[j] + cubesuff[x-1];
					}
				}
			}
			ss += v;
			// apply random trigger, update U/D
			int trigger = r.nextInt(8);
			int layer = r.nextInt(2);
			int turn = r.nextInt(3);
			ss += " " + triggers[trigger][0] + " " + ud[layer] + cubesuff[turn] + " " + triggers[trigger][1];
			if (layer==0) u += turn+1;
			if (layer==1) d += turn+1;
		}
		// fix everything
		for (int i=0; i<moves.length; i++) {
			int x = 4 - (movemis[i] % 4);
			if (x < 4)
				ss += " " + moves[i] + cubesuff[x-1];
		}
		u = 4 - (u % 4); d = 4 - (d % 4);
		if (u < 4)
			ss += " U" + cubesuff[u-1];
		if (d < 4)
			ss += " D" + cubesuff[d-1];
		ss += " " + rndEl(end);
		return ss;
	}
	
	private String megascramble(String[][] turns, String[] suffixes, int len) {
		int[] donemoves = new int[turns[0].length];
		int lastaxis = -1;
		int j, k;
		StringBuffer scr = new StringBuffer();
		for(j=0; j<len; j++) {
			int done = 0;
			do{
				int first = r.nextInt(turns.length);
				int second = r.nextInt(turns[first].length);
				if(first != lastaxis || donemoves[second] == 0) {
					if(first == lastaxis) {
						donemoves[second] = 1;
						if(suffixes == null) scr.append(turns[first][second] + " ");
						else scr.append(turns[first][second] + rndEl(suffixes) + " ");
					} else {
						for(k=0; k<turns[first].length; k++)
							donemoves[k] = 0;
						lastaxis = first;
						donemoves[second] = 1;
						if(suffixes == null) scr.append(turns[first][second] + " ");
						else scr.append(turns[first][second] + rndEl(suffixes) + " ");
					}
					done = 1;
				}
			} while(done == 0);
		}
		return scr.toString();
	}
	
	private String megascramble(String[][] turns, String[] suffixes) {
		return megascramble(turns, suffixes, scrLen);
	}
	
	private String megascramble(String[][][] turns, String[] suffixes) {
		int[] donemoves = new int[turns[0].length];
		int lastaxis;
		int j, k;
		StringBuilder scr = new StringBuilder();
		lastaxis = -1;
		for(j=0; j<scrLen; j++) {
			int done = 0;
			do{
				int first = r.nextInt(turns.length);
				int second = r.nextInt(turns[first].length);
				if (first != lastaxis) {
					for(k=0; k<turns[first].length; k++)
						donemoves[k] = 0;
					lastaxis = first;
				}
				if (donemoves[second] == 0) {
					donemoves[second] = 1;
					if(suffixes == null) scr.append(rndEl(turns[first][second]) + " ");
					else scr.append(rndEl(turns[first][second]) + rndEl(suffixes) + " ");
					done = 1;
				}
			} while(done == 0);
		}
		return scr.toString();
	}
	
	private String helicubescramble() {
		int j, k;
		String[] faces = {"UF", "UR", "UB", "UL", "FR", "BR", "BL", "FL", "DF", "DR", "DB", "DL"};
		int[] used = new int[12];
		// adjacency table
		String[] adj = {"010110010000", "101011000000", "010101100000", "101000110000", "110000001100", "011000000110", "001100000011", "100100001001", "000010010101", "000011001010", "000001100101", "000000111010"};
		// now generate the scramble(s)
		String s = "";
		for(j=0; j<12; j++)
			used[j] = 0;
		for(j=0; j<scrLen; j++) {
			boolean done = false;
			do {
				int face = r.nextInt(12);
				if (used[face] == 0) {
					s += faces[face] + " ";
					for(k=0; k<12; k++) {
						if (adj[face].charAt(k)=='1')
							used[k] = 0;
					}
					used[face] = 1;
					done = true;
				}
			} while (!done);
		}
		return s;
	}

//	private String gigascramble(){
//		int i,j;
//		String s="";
//		String[] El={"+","++","-","--"};
//		String[] minxsuff={"","2","'","2'"};
//		for(i=0;i<Math.ceil(30);i++){
//			for(j=0;j<10;j++){
//				s+=(j%2==0?(Math.random()>0.5?"R":"r"):(Math.random()>0.5?"D":"d"))+rndEl(El)+" ";
//			}
//			s+="y"+rndEl(minxsuff)+" ";
//		}
//		return s;
//	}
	
	private String ssq1t_scramble() {
		byte[][][] seq = new byte[2][scrLen*2][2];
		int i;
		sq1_getseq(seq, 0);
		byte[][] s = seq[0], t = seq[1];
		StringBuffer u = new StringBuffer();
		//int[][] temp={{0,0}};
		if (s[0][0] == 7) {
			for(i=0; i<scrLen; i++) {
				s[i*2][0] = s[i*2+1][0];
				s[i*2][1] = s[i*2+1][1];
			}
		}
		if (t[0][0] == 7) {
			for(i=0; i<scrLen; i++) {
				t[i*2][0] = t[i*2+1][0];
				t[i*2][1] = t[i*2+1][1];
			}
		}
		for(i=0; i<scrLen; i++) {
			u.append("(" + s[2*i][0] + "," + t[2*i][0] + "," + t[2*i][1] + "," + s[2*i][1] + ") / ");
		}
		return u.toString();
	}
	
	private void sq1_getseq(byte[][][] seq, int type) {
		for(int n=0; n<seq.length; n++) {
			byte[] p = {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
			int cnt = 0;
			int seql = 0;
			while (cnt < scrLen) {
				byte x = (byte) (r.nextInt(12) - 5);
				byte y = (type==2) ? 0 : (byte) (r.nextInt(12) - 5);
				int size = (x==0 ? 0 : 1) + (y==0 ? 0 : 1);
				if ((cnt + size <= scrLen || type != 1) && (size > 0 || cnt == 0)) {
					if (sq1_domove(p, x, y)) {
						if (type == 1) cnt += size;
						if (size > 0) seq[n][seql++] = new byte[]{x,y};
						if (cnt < scrLen || type != 1) {
							cnt++;
							seq[n][seql++] = new byte[]{7,0};
							sq1_domove(p, 7, 0);
						}
					}
				}
			}
		}
	}
	
	private boolean sq1_domove(byte[] p, int x, int y) {
		int i, temp;
		byte[] px, py;
		if (x == 7) {
			for (i=0; i<6; i++) {
				temp = p[i+6];
				p[i+6] = p[i+12];
				p[i+12] = (byte) temp;
			}
			return true;
		} else {
			if (p[(17-x)%12]!=0 || p[(11-x)%12]!=0 || p[12+(17-y)%12]!=0 || p[12+(11-y)%12]!=0) {
				return false;
			} else {
				// do the move itself
				px = new byte[12];
				py = new byte[12];
				for (int j=0; j<12; j++) px[j] = p[j];
				for (int j=12; j<24; j++) py[j-12] = p[j];
				for (i=0; i<12; i++) {
					p[i] = px[(12+i-x)%12];
					p[i+12] = py[(12+i-y)%12];
				}
				return true;
			}
		}
	}
	
	private String yj4x4() {
		// the idea is to keep the fixed center on U and do Rw or Lw, Fw or Bw, to not disturb it
		String[][] turns = {{"U","D"}, {"R","L","r"}, {"F","B","f"}};
		int[] donemoves = new int[3];
		int lastaxis, fpos = 0, // 0 = Ufr, 1 = Ufl, 2 = Ubl, 3 = Ubr
				j, k;
		StringBuffer s = new StringBuffer();
		lastaxis = -1;
		for(j=0; j<scrLen; j++) {
			int done = 0;
			do{
				int first = r.nextInt(turns.length);
				int second = r.nextInt(turns[first].length);
				if(first != lastaxis || donemoves[second] == 0) {
					if(first == lastaxis) {
						donemoves[second] = 1;
						int rs = r.nextInt(3);
						if(first == 0 && second == 0)
							fpos = (fpos + 4 + rs) % 4;
						if(first == 1 && second == 2) { // r or l
							if(fpos == 0 || fpos == 3) s.append("l" + cubesuff[rs] + " ");
							else s.append("r" + cubesuff[rs] + " ");
						} else if(first == 2 && second == 2) { // f or b
							if(fpos == 0 || fpos == 1) s.append("b" + cubesuff[rs] + " ");
							else s.append("f" + cubesuff[rs] + " ");
						} else {
							s.append(turns[first][second] + cubesuff[rs] + " ");
						}
					}else{
						for(k=0; k<turns[first].length; k++)
							donemoves[k] = 0;
						lastaxis = first;
						donemoves[second] = 1;
						int rs = r.nextInt(cubesuff.length);
						if(first == 0 && second == 0)
							fpos = (fpos + 4 + rs) % 4;
						if(first == 1 && second == 2) { // r or l
							if(fpos == 0 || fpos == 3) s.append("l" + cubesuff[rs] + " ");
							else s.append("r" + cubesuff[rs] + " ");
						} else if(first == 2 && second == 2) { // f or b
							if(fpos == 0 || fpos == 1) s.append("b" + cubesuff[rs] + " ");
							else s.append("f" + cubesuff[rs] + " ");
						} else {
							s.append(turns[first][second] + cubesuff[rs] + " ");
						}
					}
					done = 1;
				}
			} while(done == 0);
		}
		return s.toString();
	}
	
	private String oldminxscramble() {
		int j, k;
		String[] minxsuff = {"","2","2'","'"};
		String[] faces = {"F", "B", "U", "D", "L", "DBR", "DL", "BR", "DR", "BL", "R", "DBL"};
		int[] used = new int[12];
		// adjacency table
		String[] adj = {"001010101010", "000101010101", "100010010110", "010001101001", "101000100101", "010100011010", "100110001001", "011001000110", "100101100010", "011010010001", "101001011000", "010110100100"};
		// now generate the scramble(s)
		StringBuffer s = new StringBuffer();
		for(j=0; j<12; j++)
			used[j] = 0;
		for(j=0; j<scrLen; j++) {
			boolean done = false;
			do {
				int face = r.nextInt(12);
				if (used[face] == 0) {
					s.append(faces[face] + rndEl(minxsuff) + " ");
					for(k=0; k<12; k++) {
						if (adj[face].charAt(k)=='1')
							used[k] = 0;
					}
					used[face] = 1;
					done = true;
				}
			} while (!done);
		}
		return s.toString();
	}
	
	private String sq1_scramble(int type) {
		byte[][][] seq = new byte[1][scrLen*2][2];
		int i;
		byte[] k;
		sq1_getseq(seq, type);
		StringBuffer s = new StringBuffer();
		for(i=0; i<seq[0].length; i++) {
			k = seq[0][i];
			if(k[0] == 7) s.append("/ ");
			else s.append("(" + k[0] + "," + k[1] + ") ");
		}
		return s.toString();
	}
	
	private String do15puzzle(boolean mirrored) {
		String[] moves;
		if(mirrored) moves = new String[]{"U", "L", "R", "D"};
		else moves = new String[]{"D", "R", "L", "U"};
		int[][] effect = {{0,-1}, {1,0}, {-1,0}, {0,1}};
		int x=0, y=3, k, m, lastm=5;
		boolean done;
		StringBuffer s = new StringBuffer();
		for(k=0; k<scrLen; k++) {
			done = false;
			while(!done) {
				m = r.nextInt(4);
				if (x+effect[m][0]>=0 && x+effect[m][0]<=3 && y+effect[m][1]>=0 && y+effect[m][1]<=3 && m+lastm != 3) {
					done = true;
					x += effect[m][0];
					y += effect[m][1];
					s.append(moves[m] + " ");
					lastm = m;
				}
			}
		}
		return s.toString();
	}
	
	private static int[][] d = {{0,1,2,5,8,7,6,3,4}, {6,7,8,13,20,19,18,11,12}, {0,3,6,11,18,17,16,9,10}, {8,5,2,15,22,21,20,13,14}};
	private static int[] start = {1, 1, 2, 3, 3, 2, 4, 4, 0, 5, 6, 7, 8, 9, 10, 10, 5, 6, 7, 8, 9, 11, 11};
	private static String[] move = {"U", "F", "L", "R"};

	private boolean canMove(int face) {
		int[] u = new int[8];
		int ulen=0, i, j, done, z=0;
		for (i=0; i<9; i++) {
			done = 0;
			for (j=0; j<ulen; j++) {
				if (u[j] == start[d[face][i]]) done = 1;
			}
			if (done == 0) {
				u[ulen++] = start[d[face][i]];
				if (start[d[face][i]] == 0) z = 1;
			}
		}
		return (ulen==5 && z==1);
	}
	
	private void doMove(int face, int amount) {
		for (int i=0; i<amount; i++) {
			int t = start[d[face][0]];
			start[d[face][0]] = start[d[face][6]];
			start[d[face][6]] = start[d[face][4]];
			start[d[face][4]] = start[d[face][2]];
			start[d[face][2]] = t;
			t = start[d[face][7]];
			start[d[face][7]] = start[d[face][5]];
			start[d[face][5]] = start[d[face][3]];
			start[d[face][3]] = start[d[face][1]];
			start[d[face][1]] = t;
		}
	}
	
	private String bicube() {
		StringBuffer sb = new StringBuffer();
		int[][] arr = new int[scrLen][];
		int[] poss;
		int arrlen=0, done, i, j, x=0, y=0;
		while (arrlen < scrLen) {
			poss = new int[] {1, 1, 1, 1};
			for (j=0; j<4; j++) {
				if (poss[j]==1 && !canMove(j))
					poss[j]=0;
			}
			done = 0;
			while (done==0) {
				x = r.nextInt(4);
				if (poss[x] == 1) {
					y = r.nextInt(3) + 1;
					doMove(x, y);
					done = 1;
				}
			}
			arr[arrlen++] = new int[] {x, y};
			if (arrlen >= 2) {
				if (arr[arrlen-1][0] == arr[arrlen-2][0]) {
					arr[arrlen-2][1] = (arr[arrlen-2][1] + arr[arrlen-1][1]) % 4;
					arrlen--;//arr = arr.slice(0,arr.length - 1);
				}
			}
			if (arrlen >= 1) {
				if (arr[arrlen-1][1] == 0) {
					arrlen--;//arr = arr.slice(0,arr.length - 1);
				}
			}
		}
		for (i=0; i<scrLen; i++) {
			sb.append(move[arr[i][0]] + cubesuff[arr[i][1]-1] + " ");
		}
		return sb.toString();
	}
	
	public void extSol3(int type, String scr) {
		switch (type) {
		case 1:
			sc = "\n"+Cross.cross(scr, Configs.solSel[0], Configs.solSel[1]);
			break;
		case 2:
			sc = "\n"+Cross.xcross(scr, Configs.solSel[1]);
			break;
		case 3:
			sc = "\n"+EOline.eoLine(scr, Configs.solSel[1]);
			break;
		case 4:
			sc = "\n"+PetrusxRoux.roux(scr, Configs.solSel[1]);
			break;
		case 5:	
			sc = "\n"+PetrusxRoux.petrus(scr, Configs.solSel[1]);
			break;
		}
	}
	
	public void drawScr(int sel2, int width, Paint p, Canvas c) {
		int[] colors={dct.share.getInt("csn1", Color.YELLOW), dct.share.getInt("csn2", Color.BLUE), dct.share.getInt("csn3", Color.RED),
				dct.share.getInt("csn4", Color.WHITE), dct.share.getInt("csn5", 0xff009900), dct.share.getInt("csn6", 0xffff8026)};
		//2阶
		if(viewType==2) {
			Cube.parse(2);
			byte[] imst = imagestr(crntScr, 2);
			int a=width/10, i, j, d=0, stx=(width-8*a-19)/2, sty=(width*3/4-6*a-14)/2;
			for(i=0; i<2; i++)
				for(j=0; j<2; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+2)*a, sty+1+i*a, stx+6+(j+3)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+2)*a, sty+1+i*a, stx+6+(j+3)*a, sty+(i+1)*a, p);
				}
			for(i=0;i<2;i++)
				for(j=0;j<8;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>=6)     c.drawRect(stx+19+j*a, sty+7+(i+2)*a, stx+18+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>=4)c.drawRect(stx+13+j*a, sty+7+(i+2)*a, stx+12+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>=2)c.drawRect(stx+ 7+j*a, sty+7+(i+2)*a, stx+ 6+(j+1)*a, sty+6+(i+3)*a, p);
					else         c.drawRect(stx+ 1+j*a, sty+7+(i+2)*a, stx   +(j+1)*a, sty+6+(i+3)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>=6)     c.drawRect(stx+19+j*a, sty+7+(i+2)*a, stx+18+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>=4)c.drawRect(stx+13+j*a, sty+7+(i+2)*a, stx+12+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>=2)c.drawRect(stx+ 7+j*a, sty+7+(i+2)*a, stx+ 6+(j+1)*a, sty+6+(i+3)*a, p);
					else         c.drawRect(stx+ 1+j*a, sty+7+(i+2)*a, stx   +(j+1)*a, sty+6+(i+3)*a, p);
				}
			for(i=0;i<2;i++)
				for(j=0;j<2;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+2)*a, sty+13+(i+4)*a, stx+6+(j+3)*a, sty+12+(i+5)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+2)*a, sty+13+(i+4)*a, stx+6+(j+3)*a, sty+12+(i+5)*a, p);
				}
		}
		else if(viewType == TYPE_MINX) {
			float edgeFrac = (float) ((1+Math.sqrt(5))/4);
			float centerFrac = 0.5F;
			if(Configs.stSel[7]==0)
				colors = new int[] {Color.WHITE, 0xff880088, 0xff008800, 0xff88ddff, 0xff882222, Color.BLUE,
					Color.RED, 0xffff8800, Color.GREEN, 0xffff44ff, 0xff000088, Color.YELLOW};
			else colors = new int[] {Color.WHITE, Color.RED, 0xff008800, 0xff880088, Color.YELLOW, Color.BLUE,
					0xffffff88, 0xff88ddff, 0xffff8800, Color.GREEN, 0xffff44ff, Color.GRAY};
			float scale = (float) (width / 350.);
			int dx = (int) ((width - 350 * scale) / 2);
			int dy = (int) ((width * 0.75 - 180 * scale) / 2);
			float majorR = 36 * scale;
			float minorR = majorR * edgeFrac;
			float pentR = minorR * 2;
			float cx1 = 92 * scale + dx;
			float cy1 = 80 * scale + dy;
			float cx2 = cx1 + c18(1)*3*pentR;
			float cy2 = cy1 + s18(1)*1*pentR;
			float[] aryx, aryy;
			int[][] trans = {
					{0, (int)cx1, (int)cy1, 0, 0},
					{36, (int)cx1, (int)cy1, 1, 1},
					{36+72*1, (int)cx1, (int)cy1, 1, 5},
					{36+72*2, (int)cx1, (int)cy1, 1, 9},
					{36+72*3, (int)cx1, (int)cy1, 1, 13},
					{36+72*4, (int)cx1, (int)cy1, 1, 17},
					{0, (int)cx2, (int)cy2, 1, 7},
					{-72*1, (int)cx2, (int)cy2, 1, 3},
					{-72*2, (int)cx2, (int)cy2, 1, 19},
					{-72*3, (int)cx2, (int)cy2, 1, 15},
					{-72*4, (int)cx2, (int)cy2, 1, 11},
					{36+72*2, (int)cx2, (int)cy2, 0, 0}
			};
			int d = 0;
			float d2x = (float) (majorR*(1-centerFrac)/2/Math.tan(Math.PI/5));
			byte[] img = Megaminx.state;
			p.setStyle(Paint.Style.FILL);
			for(int side=0; side<12; side++) {
				float a = trans[side][1]+trans[side][3]*c18(trans[side][4])*pentR;
				float b = trans[side][2]+trans[side][3]*s18(trans[side][4])*pentR;
				float[][] arys;
				for(int i=0; i<5; i++) {
					aryx = new float[]{0, d2x, 0, -d2x};
					aryy = new float[]{-majorR, -majorR*(1+centerFrac)/2, -majorR*centerFrac, -majorR*(1+centerFrac)/2};
					arys = rotate(a, b, aryx, aryy, 72*i+trans[side][0]);
					drawPolygon(p, c, colors[img[d++]], arys[0], arys[1], true);
				}
				for(int i=0; i<5; i++) {
					aryx = new float[]{c18(-1)*majorR-d2x, d2x, 0, s18(4)*centerFrac*majorR};
					aryy = new float[]{s18(-1)*majorR-majorR+majorR*(1+centerFrac)/2, -majorR*(1+centerFrac)/2, -majorR*centerFrac, -c18(4)*centerFrac*majorR};
					arys = rotate(a, b, aryx, aryy, 72*i+trans[side][0]);
					drawPolygon(p, c, colors[img[d++]], arys[0], arys[1], true);
				}
				aryx = new float[]{s18(0)*centerFrac*majorR, s18(4)*centerFrac*majorR, s18(8)*centerFrac*majorR, s18(12)*centerFrac*majorR, s18(16)*centerFrac*majorR};
				aryy = new float[]{-c18(0)*centerFrac*majorR, -c18(4)*centerFrac*majorR, -c18(8)*centerFrac*majorR, -c18(12)*centerFrac*majorR, -c18(16)*centerFrac*majorR};
				arys = rotate(a, b, aryx, aryy, trans[side][0]);
				drawPolygon(p, c, colors[img[d++]], arys[0], arys[1], true);
			}
			p.setStyle(Paint.Style.FILL);
			p.setTextAlign(Align.CENTER);
			p.setTextSize((float) (width * 0.0593));
			c.drawText("U", (float)(width*0.262), (float)(width*0.367), p);
			c.drawText("F", (float)(width*0.262), (float)(width*0.535), p);
		}
		else if(viewType == TYPE_PYR) {
			byte[] imst = pyram.imageString(crntScr);
			int b = (width * 3 / 4 - 15) / 6;
			int a = (int) (b * 2 / Math.sqrt(3));
			int d = (int) ((width - a * 6 - 21) / 2);
			colors = new int[]{dct.share.getInt("csp1", Color.RED), dct.share.getInt("csp2", 0xff009900),
					dct.share.getInt("csp3", Color.BLUE), dct.share.getInt("csp4", Color.YELLOW)};
			float[] arx, ary;
			byte[] layout = {
					1, 2, 1, 2, 1, 0, 2, 0, 1, 2, 1, 2, 1,
					0, 1, 2, 1, 0, 2, 1, 2, 0, 1, 2, 1, 0,
					0, 0, 1, 0, 2, 1, 2, 1, 2, 0, 1, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 1, 2, 1, 2, 1, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0
			};
			int[] pos = {
					d,d+a/2,d+a,d+3*a/2,d+2*a,d+5*a/2,d+7+5*a/2,d+7+3*a,d+14+3*a,d+14+7*a/2,d+14+4*a,d+14+9*a/2,d+14+5*a,
					d+14+11*a/2,d+a/2,d+a,d+3*a/2,d+2*a,d+7+2*a,d+7+5*a/2,d+7+3*a,d+7+7*a/2,d+14+7*a/2,d+14+4*a,d+14+9*a/2,d+14+5*a,
					0,0,d+a,d+3*a/2,d+7+3*a/2,d+7+2*a,d+7+5*a/2,d+7+3*a,d+7+7*a/2,d+7+4*a,d+14+4*a,d+14+9*a/2,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,d+7+3*a/2,d+7+2*a,d+7+5*a/2,d+7+3*a,d+7+7*a/2,d+7+4*a,0,0,0,
					0,0,0,0,0,d+7+2*a,d+7+5*a/2,d+7+3*a,d+7+7*a/2,0,0,0,0,
					0,0,0,0,0,0,d+7+5*a/2,d+7+3*a,0,0,0,0,0};
			for(int y=0; y<7; y++) 
				for(int x=0; x<13; x++) {
					if(layout[y*13+x] == 1) {
						if(y < 3) {
							arx = new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary = new float[]{y*b+3+y,y*b+3+y,(y+1)*b+3+y};
							drawPolygon(p, c, colors[imst[y*13+x]], arx, ary, true);
						}
						else if(y > 3) {
							arx = new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary = new float[]{(y-1)*b+9+y,(y-1)*b+9+y,y*b+9+y};
							drawPolygon(p, c, colors[imst[y*13+x]], arx, ary, true);
						}
					}
					else if(layout[y*13+x] == 2) {
						if(y < 3) {
							arx = new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary = new float[]{(y+1)*b+3+y,(y+1)*b+3+y,y*b+3+y};
							drawPolygon(p, c, colors[imst[y*13+x]], arx, ary, true);
						}
						else if(y > 3) {
							arx = new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary = new float[]{y*b+9+y,y*b+9+y,(y-1)*b+9+y};
							drawPolygon(p, c, colors[imst[y*13+x]], arx, ary, true);
						}
					}
				}
		}
		//SQ1
		else if(viewType == TYPE_SQ1) {
			String[] tb = {"3","3","3","3","3","3","3","3","0","0","0","0","0","0","0","0"};
			String[] ty = {"c","e","c","e","c","e","c","e","e","c","e","c","e","c","e","c"};
			String[] col = {"51","1","12","2","24","4","45","5","5","54","4","42","2","21","1","15"};
			colors = new int[]{dct.share.getInt("csq1", Color.YELLOW), dct.share.getInt("csq6", 0xffff8026), dct.share.getInt("csq2", Color.BLUE),
					dct.share.getInt("csq4", Color.WHITE), dct.share.getInt("csq3", Color.RED), dct.share.getInt("csq5", 0xff009900)};
			byte[] img = SQ1.imagestr(crntScr.split(" "));
			boolean mis = SQ1.mi;
			byte[] temp = new byte[12];
			for(int i=0; i<12; i++) temp[i] = img[i];
			byte[] top_side = rd(temp);
			for(int i=0; i<6; i++) temp[i] = img[i+18];
			for(int i=6; i<12; i++) temp[i] = img[i+6];
			byte[] bot_side = rd(temp);
			temp = new byte[top_side.length + bot_side.length];
			for(int i=0; i<top_side.length; i++) temp[i] = top_side[i];
			for(int i=top_side.length; i<top_side.length+bot_side.length; i++) temp[i] = bot_side[i - top_side.length];
			byte[] eido = temp;
			StringBuffer a2 = new StringBuffer(), b2 = new StringBuffer(), c2 = new StringBuffer();
			for(int j=0; j<16; j++) {
				a2.append(ty[eido[j]]);
				b2.append(tb[eido[j]]);
				c2.append(col[eido[j]]);
			}
			String stickers = b2.append(c2).toString();
			String a = a2.toString();
			float z=1.366F; // sqrt(2)/sqrt(1^2+tan(15°)^2)
			float[] arrx, arry;
			float sidewid = 10.98F;
			int cx = 55, cy = 50;
			float rd = (cx - 16) / z;
			float w = (sidewid + rd) / rd;	// ratio btw total piece width and rd
			float[] ag = new float[24];
			float[] ag2 = new float[24];
			int foo;
			for(foo=0; foo<24; foo++) {
				ag[foo] = (float) ((17F - foo*2) * Math.PI / 12);
				a = a.concat("xxxxxxxxxxxxxxxx");
			}
			for(foo=0; foo<24; foo++) {
				ag2[foo] = (float) ((19F - foo * 2) * Math.PI / 12);
				a = a.concat("xxxxxxxxxxxxxxxx");
			}
			float h = sin1(1, ag, rd) * w * z - sin1(1, ag, rd) * z;
			if(mis) {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(7,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(4,ag,rd)*w*z, cy-sin1(7,ag,rd)*w*z, cy-sin1(10,ag,rd)*w*z};
				drawPolygon(p, c, Color.BLACK, width, arrx, arry);
				cy += 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*w*z};
				drawPolygon(p, c, colors[5], width, arrx, arry);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(10,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*w*z};
				drawPolygon(p, c, colors[5], width, arrx, arry);
				cy -= 10;
			}
			else {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(6,ag,rd)*w, cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(0,ag,rd)*w};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(4,ag,rd)*w*z, cy-sin1(6,ag,rd)*w, cy+sin1(9,ag,rd)*w*z, cy-sin1(11,ag,rd)*w*z, cy-sin1(0,ag,rd)*w};
				drawPolygon(p, c, Color.BLACK, width, arrx, arry);
				arrx=new float[]{cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(9,ag,rd)*w*z};
				arry=new float[]{cy+sin1(9,ag,rd)*w*z-h, cy-sin1(11,ag,rd)*w*z-h, cy-sin1(11,ag,rd)*w*z, cy+sin1(9,ag,rd)*w*z};
				drawPolygon(p, c, colors[4], width, arrx, arry);
				cy += 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*w*z};
				drawPolygon(p, c, colors[5], width, arrx, arry);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(11,ag,rd)*w*z + h, cy-sin1(11,ag,rd)*w*z};
				drawPolygon(p, c, colors[2], width, arrx, arry);
				cy -= 10;
			}
			int sc = 0;
			for(foo=0; sc<12; foo++) {
				if (a.length()<=foo) sc = 12;
				if (a.charAt(foo)=='x') sc++;
				if (a.charAt(foo)=='c') {
					arrx=new float[]{cx, cx+cos1(sc,ag,rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+2, ag, rd)};
					arry=new float[]{cy, cy-sin1(sc,ag,rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+2, ag, rd)};
					drawPolygon(p, c, colors[(int)stickers.charAt(foo)-48], width, arrx, arry);
					arrx=new float[]{cx+cos1(sc, ag, rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+1, ag, rd)*w*z, cx+cos1(sc, ag, rd)*w};
					arry=new float[]{cy-sin1(sc, ag, rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+1, ag, rd)*w*z, cy-sin1(sc, ag, rd)*w};
					drawPolygon(p, c, colors[(int)stickers.charAt(16+sc)-48], width, arrx, arry);
					arrx=new float[]{cx+cos1(sc+2, ag, rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+1, ag, rd)*w*z, cx+cos1(sc+2, ag, rd)*w};
					arry=new float[]{cy-sin1(sc+2, ag, rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+1, ag, rd)*w*z, cy-sin1(sc+2, ag, rd)*w};
					drawPolygon(p, c, colors[(int)stickers.charAt(17+sc)-48], width, arrx, arry);
					sc+=2;
				}
				if (a.charAt(foo)=='e') {
					arrx=new float[]{cx, cx+cos1(sc,ag,rd), cx+cos1(sc+1,ag,rd)};
					arry=new float[]{cy, cy-sin1(sc,ag,rd), cy-sin1(sc+1,ag,rd)};
					drawPolygon(p, c, colors[(int)stickers.charAt(foo)-48], width, arrx, arry);
					arrx=new float[]{cx+cos1(sc,ag,rd), cx+cos1(sc+1,ag,rd), cx+cos1(sc+1,ag,rd)*w, cx+cos1(sc,ag,rd)*w};
					arry=new float[]{cy-sin1(sc,ag,rd), cy-sin1(sc+1,ag,rd), cy-sin1(sc+1,ag,rd)*w, cy-sin1(sc,ag,rd)*w};
					drawPolygon(p, c, colors[(int)stickers.charAt(16+sc)-48], width, arrx, arry);
					sc +=1;
				}
			}
			cx *= 3;
			cy += 10;
			if(mis) {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(7,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(4,ag,rd)*w*z, cy+sin1(7,ag,rd)*w*z, cy+sin1(10,ag,rd)*w*z};
				drawPolygon(p, c, Color.BLACK, width, arrx, arry);
				cy -= 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p, c, colors[5], width, arrx, arry);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(10,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p, c, colors[5], width, arrx, arry);
				cy += 10;
			}
			else {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(6,ag,rd)*w, cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(0,ag,rd)*w};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(4,ag,rd)*w*z, cy+sin1(6,ag,rd)*w, cy-sin1(9,ag,rd)*w*z, cy+sin1(11,ag,rd)*w*z, cy+sin1(0,ag,rd)*w};
				drawPolygon(p, c, Color.BLACK, width, arrx, arry);
				arrx=new float[]{cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(9,ag,rd)*w*z};
				arry=new float[]{cy-sin1(9,ag,rd)*w*z-10, cy+sin1(11,ag,rd)*w*z-10, cy+sin1(11,ag,rd)*w*z, cy-sin1(9,ag,rd)*w*z};
				drawPolygon(p, c, colors[4], width, arrx, arry);
				cy -= 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p, c, colors[5], width, arrx, arry);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(11,ag,rd)*w*z+10, cy+sin1(11,ag,rd)*w*z};
				drawPolygon(p, c, colors[2], width, arrx, arry);
				cy += 10;
			}
			sc = 0;
			for(sc=0; sc<12; foo++) {
				if (a.length()<=foo) sc = 12;
				if (a.charAt(foo)=='x') sc++;
				if (a.charAt(foo)=='c') {
					arrx=new float[]{cx, cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd)*z, cx+cos1(sc+2,ag2,rd)};
					arry=new float[]{cy, cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd)*z, cy-sin1(sc+2,ag2,rd)};
					drawPolygon(p, c, colors[(int)stickers.charAt(foo)-48], width, arrx, arry);
					arrx=new float[]{cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd)*z, cx+cos1(sc+1,ag2,rd)*w*z, cx+cos1(sc,ag2,rd)*w};
					arry=new float[]{cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd)*z, cy-sin1(sc+1,ag2,rd)*w*z, cy-sin1(sc,ag2,rd)*w};
					drawPolygon(p, c, colors[(int)stickers.charAt(28+sc)-48], width, arrx, arry);
					arrx=new float[]{cx+cos1(sc+2,ag2,rd), cx+cos1(sc+1,ag2,rd)*z, cx+cos1(sc+1,ag2,rd)*w*z, cx+cos1(sc+2,ag2,rd)*w};
					arry=new float[]{cy-sin1(sc+2,ag2,rd), cy-sin1(sc+1,ag2,rd)*z, cy-sin1(sc+1,ag2,rd)*w*z, cy-sin1(sc+2,ag2,rd)*w};
					drawPolygon(p, c, colors[(int)stickers.charAt(29+sc)-48], width, arrx, arry);
					sc +=2;
				}
				if (a.charAt(foo)=='e') {
					arrx=new float[]{cx, cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd)};
					arry=new float[]{cy, cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd)};
					drawPolygon(p, c, colors[(int)stickers.charAt(foo)-48], width, arrx, arry);
					arrx=new float[]{cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd), cx+cos1(sc+1,ag2,rd)*w, cx+cos1(sc,ag2,rd)*w};
					arry=new float[]{cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd), cy-sin1(sc+1,ag2,rd)*w, cy-sin1(sc,ag2,rd)*w};
					drawPolygon(p, c, colors[(int)stickers.charAt(28+sc)-48], width, arrx, arry);
					sc +=1;
				}
			}
		}
		else if(viewType == TYPE_CLK) {
			byte[] posit = Clock.posit;
			int face_dist = 30;
			int cx = 55;
			int cy = 55;
			p.setColor(0xff2a2a2a);
			drawSideBackground(p, c, width, cx, cy, 53, 29, 19);
			p.setColor(0xff3366ff);
			drawSideBackground(p, c, width, cx, cy, 52, 29, 18);
			int i = 0;
			for(int y=cy-face_dist; y<=cy+face_dist; y+=face_dist)
				for(int x=cx-face_dist; x<=cx+face_dist; x+=face_dist)
					drawClockFace(p, c, width, x, y, 0xff88aaff, posit[i++]);
			byte[] pegs = Clock.pegs;
			drawPeg(p, c, width, cx-face_dist/2, cy-face_dist/2, 1-pegs[0]);
			drawPeg(p, c, width, cx+face_dist/2, cy-face_dist/2, 1-pegs[1]);
			drawPeg(p, c, width, cx-face_dist/2, cy+face_dist/2, 1-pegs[2]);
			drawPeg(p, c, width, cx+face_dist/2, cy+face_dist/2, 1-pegs[3]);
			cx = 165;
			p.setColor(0xff2a2a2a);
			drawSideBackground(p, c, width, cx, cy, 53, 29, 19);
			p.setColor(0xff88aaff);
			drawSideBackground(p, c, width, cx, cy, 52, 29, 18);
			for (int y=cy-face_dist; y<=cy+face_dist; y+=face_dist)
				for (int x=cx-face_dist; x<=cx+face_dist; x+=face_dist)
					drawClockFace(p, c, width, x, y, 0xff3366ff, posit[i++]);
			drawPeg(p, c, width, cx+face_dist/2, cy-face_dist/2, pegs[0]);
			drawPeg(p, c, width, cx-face_dist/2, cy-face_dist/2, pegs[1]);
			drawPeg(p, c, width, cx+face_dist/2, cy+face_dist/2, pegs[2]);
			drawPeg(p, c, width, cx-face_dist/2, cy+face_dist/2, pegs[3]);
		}
		//1x3x3
		else if(viewType == TYPE_FLP) {
			byte[] imst = Floppy.image(crntScr);
			int a = (width-19)/8, i, j, d = 0;
			int stx = (width-8*a-19)/2, sty = (width*3/4-5*a-14)/2;
			p.setStyle(Paint.Style.FILL);
			colors = new int[] {0xFF4B4D4B, 0xFFFFEF33, 0xFF33B9FF, 0xFFC8CCC8, 0xFFFF0026, 0xFF99FF99};
			for(i=0; i<3; i++) {
				p.setStyle(Paint.Style.FILL);
				p.setColor(colors[imst[d++]]);
				c.drawRect(stx+7+(i+1)*a, sty+1, stx+6+(i+2)*a, sty+a, p);
				p.setStyle(Paint.Style.STROKE);
				p.setColor(Color.BLACK);
				c.drawRect(stx+7+(i+1)*a, sty+1, stx+6+(i+2)*a, sty+a, p);
			}
			for(i=0; i<3; i++) {
				for(j=0; j<8; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>4)     c.drawRect(stx+19+j*a, sty+7+(i+1)*a, stx+18+(j+1)*a, sty+6+(i+2)*a, p);
					else if(j>3)c.drawRect(stx+13+j*a, sty+7+(i+1)*a, stx+12+(j+1)*a, sty+6+(i+2)*a, p);
					else if(j>0)c.drawRect(stx+ 7+j*a, sty+7+(i+1)*a, stx+ 6+(j+1)*a, sty+6+(i+2)*a, p);
					else        c.drawRect(stx+ 1+j*a, sty+7+(i+1)*a, stx   +(j+1)*a, sty+6+(i+2)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>4)     c.drawRect(stx+19+j*a, sty+7+(i+1)*a, stx+18+(j+1)*a, sty+6+(i+2)*a, p);
					else if(j>3)c.drawRect(stx+13+j*a, sty+7+(i+1)*a, stx+12+(j+1)*a, sty+6+(i+2)*a, p);
					else if(j>0)c.drawRect(stx+ 7+j*a, sty+7+(i+1)*a, stx+ 6+(j+1)*a, sty+6+(i+2)*a, p);
					else        c.drawRect(stx+ 1+j*a, sty+7+(i+1)*a, stx   +(j+1)*a, sty+6+(i+2)*a, p);
				}
			}
			for(i=0;i<3;i++) {
				p.setStyle(Paint.Style.FILL);
				p.setColor(colors[imst[d++]]);
				c.drawRect(stx+7+(i+1)*a, sty+13+4*a, stx+6+(i+2)*a, sty+12+5*a, p);
				p.setStyle(Paint.Style.STROKE);
				p.setColor(Color.BLACK);
				c.drawRect(stx+7+(i+1)*a, sty+13+4*a, stx+6+(i+2)*a, sty+12+5*a, p);
			}
		}
		//2x3x3
		else if(viewType == TYPE_DMN) {
			byte[] imst=Domino.image(crntScr);
			int a = (width-19)/12, i, j, d = 0;
			int stx = (width-12*a-19)/2, sty = (width*3/4-8*a+2-14)/2;
			p.setStyle(Paint.Style.FILL);
			for(i=0; i<3; i++)
				for(j=0; j<3; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+3)*a, sty+1+i*a, stx+6+(j+4)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+3)*a, sty+1+i*a, stx+6+(j+4)*a, sty+(i+1)*a, p);
				}
			for(i=0; i<2; i++)
				for(j=0; j<12; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>=9)     c.drawRect(stx+19+j*a, sty+7+(i+3)*a, stx+18+(j+1)*a, sty+6+(i+4)*a, p);
					else if(j>=6)c.drawRect(stx+13+j*a, sty+7+(i+3)*a, stx+12+(j+1)*a, sty+6+(i+4)*a, p);
					else if(j>=3)c.drawRect(stx+ 7+j*a, sty+7+(i+3)*a, stx+ 6+(j+1)*a, sty+6+(i+4)*a, p);
					else         c.drawRect(stx+ 1+j*a, sty+7+(i+3)*a, stx   +(j+1)*a, sty+6+(i+4)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>=9)     c.drawRect(stx+19+j*a, sty+7+(i+3)*a, stx+18+(j+1)*a, sty+6+(i+4)*a, p);
					else if(j>=6)c.drawRect(stx+13+j*a, sty+7+(i+3)*a, stx+12+(j+1)*a, sty+6+(i+4)*a, p);
					else if(j>=3)c.drawRect(stx+ 7+j*a, sty+7+(i+3)*a, stx+ 6+(j+1)*a, sty+6+(i+4)*a, p);
					else         c.drawRect(stx+ 1+j*a, sty+7+(i+3)*a, stx   +(j+1)*a, sty+6+(i+4)*a, p);
				}
			for(i=0;i<3;i++)
				for(j=0;j<3;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+3)*a, sty+13+(i+5)*a, stx+6+(j+4)*a, sty+12+(i+6)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+3)*a, sty+13+(i+5)*a, stx+6+(j+4)*a, sty+12+(i+6)*a, p);
				}
		}
		//2x2x3
		else if(viewType == TYPE_TOW) {
			byte[] imst=Tower.image(crntScr);
			int a=(width*3/4-14)/7, i, j, d = 0;
			int stx = (width-8*a-19)/2, sty = (width*3/4-7*a-14)/2;
			p.setStyle(Paint.Style.FILL);
			colors=new int[]{0xFF4B4D4B,0xFFFFEF33,0xFF33B9FF,0xFFC8CCC8,0xFFFF0026,0xFF99FF99};
			for(i=0; i<2; i++)
				for(j=0; j<2; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+2)*a, sty+1+i*a, stx+6+(j+3)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+2)*a, sty+1+i*a, stx+6+(j+3)*a, sty+(i+1)*a, p);
				}
			for(i=0; i<3; i++)
				for(j=0; j<8; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>5)     c.drawRect(stx+19+j*a, sty+7+(i+2)*a, stx+18+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>3)c.drawRect(stx+13+j*a, sty+7+(i+2)*a, stx+12+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>1)c.drawRect(stx+ 7+j*a, sty+7+(i+2)*a, stx+ 6+(j+1)*a, sty+6+(i+3)*a, p);
					else        c.drawRect(stx+ 1+j*a, sty+7+(i+2)*a, stx   +(j+1)*a, sty+6+(i+3)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>5)     c.drawRect(stx+19+j*a, sty+7+(i+2)*a, stx+18+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>3)c.drawRect(stx+13+j*a, sty+7+(i+2)*a, stx+12+(j+1)*a, sty+6+(i+3)*a, p);
					else if(j>1)c.drawRect(stx+ 7+j*a, sty+7+(i+2)*a, stx+ 6+(j+1)*a, sty+6+(i+3)*a, p);
					else        c.drawRect(stx+ 1+j*a, sty+7+(i+2)*a, stx   +(j+1)*a, sty+6+(i+3)*a, p);
				}
			for(i=0;i<2;i++)
				for(j=0; j<2; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+2)*a, sty+13+(5+i)*a, stx+6+(j+3)*a, sty+12+(6+i)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+2)*a, sty+13+(5+i)*a, stx+6+(j+3)*a, sty+12+(6+i)*a, p);
				}
		}
		else if(viewType == TYPE_SKW) {
			byte[] imst = skewb.image(crntScr);
			colors = new int[] {dct.share.getInt("csw4", Color.WHITE), dct.share.getInt("csw6", 0xffff8026), dct.share.getInt("csw5", 0xff009900),
					dct.share.getInt("csw3", Color.RED), dct.share.getInt("csw2", Color.BLUE), dct.share.getInt("csw1", Color.YELLOW)};
			int b = width / 4, a = (int) (b / 2 * Math.sqrt(3));
			int stx = (width - 4*a) / 2, sty = (width*3/4 - 3*b) / 2, i, d = 0;
			float e = (float) (3 / Math.sqrt(3)), f = (float) (3 * Math.sqrt(3));
			float[] dx = {a*2, a*3-6, a+6, a*2, 3, a-3, 3, a-3, a+3, a*2-3, a+3, a*2-3,
					a*2+3, a*3-3, a*2+3, a*3-3, a*3+3, a*4-3, a*3+3, a*4-3, a+3, a*2-3, a+3, a*2-3};
			float[] dy = {e*2, b/2, b/2, b-e*2, f, b/2+e, b-e, b*3/2-f, b/2+f, b+e, b*3/2-e, b*2-f,
					b+e, b/2+f, b*2-f, b*3/2-e, b/2+e, f, b*3/2-f, b-e, b*3/2+f, b*2+e, b*5/2-e, b*3-f};
			p.setStyle(Paint.Style.FILL);
			for(i=0; i<6; i++) {
				drawPolygon(p,c,colors[imst[d++]],
						new float[]{stx+dx[i*4], stx+(dx[i*4]+dx[i*4+1])/2, stx+(dx[i*4]+dx[i*4+2])/2},
						new float[]{sty+dy[i*4], sty+(dy[i*4]+dy[i*4+1])/2, sty+(dy[i*4]+dy[i*4+2])/2}, true);
				drawPolygon(p,c,colors[imst[d++]],
						new float[]{stx+dx[i*4+1], stx+(dx[i*4]+dx[i*4+1])/2, stx+(dx[i*4+1]+dx[i*4+3])/2},
						new float[]{sty+dy[i*4+1], sty+(dy[i*4]+dy[i*4+1])/2, sty+(dy[i*4+1]+dy[i*4+3])/2}, true);
				drawPolygon(p,c,colors[imst[d++]],
						new float[]{stx+(dx[i*4]+dx[i*4+2])/2, stx+(dx[i*4]+dx[i*4+1])/2, stx+(dx[i*4+1]+dx[i*4+3])/2, stx+(dx[i*4+2]+dx[i*4+3])/2},
						new float[]{sty+(dy[i*4]+dy[i*4+2])/2, sty+(dy[i*4]+dy[i*4+1])/2, sty+(dy[i*4+1]+dy[i*4+3])/2, sty+(dy[i*4+2]+dy[i*4+3])/2}, true);
				drawPolygon(p,c,colors[imst[d++]],
						new float[]{stx+dx[i*4+2], stx+(dx[i*4]+dx[i*4+2])/2, stx+(dx[i*4+2]+dx[i*4+3])/2},
						new float[]{sty+dy[i*4+2], sty+(dy[i*4]+dy[i*4+2])/2, sty+(dy[i*4+2]+dy[i*4+3])/2}, true);
				drawPolygon(p,c,colors[imst[d++]],
						new float[]{stx+dx[i*4+3], stx+(dx[i*4+3]+dx[i*4+2])/2, stx+(dx[i*4+1]+dx[i*4+3])/2},
						new float[]{sty+dy[i*4+3], sty+(dy[i*4+3]+dy[i*4+2])/2, sty+(dy[i*4+1]+dy[i*4+3])/2}, true);
			}
		}
		else {
			int a = (width-19)/(viewType*4), i, j, d = 0, b = viewType;
			byte[] imst;
			if(Configs.isInScr) {
				Cube.parse(viewType);
				imst = imagestr(crntScr, viewType);
			}
			else if(viewType == 3) {
				Cube.parse(3);
				imst = imagestr(crntScr, viewType);
			}
			else if(viewType > 7) imst = Cube.imagestring();
			else if(sel2 == 0) imst = Cube.imagestring();
			else {
				Cube.parse(viewType);
				imst = imagestr(crntScr, viewType);
			}
			int stx = (width-4*a*b-19)/2, sty = (width*3/4-3*a*b-14)/2;
			p.setStyle(Paint.Style.FILL);
			for(i=0; i<b; i++)
				for(j=0;j<b;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+b)*a, sty+1+i*a, stx+6+(j+1+b)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+b)*a, sty+1+i*a, stx+6+(j+1+b)*a, sty+(i+1)*a, p);
				}
			for(i=0; i<b; i++)
				for(j=0; j<b*4; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>=b*3)      c.drawRect(stx+19+j*a, sty+7+(i+b)*a, stx+18+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b*2) c.drawRect(stx+13+j*a, sty+7+(i+b)*a, stx+12+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b)   c.drawRect(stx+ 7+j*a, sty+7+(i+b)*a, stx+ 6+(j+1)*a, sty+6+(i+1+b)*a, p);
					else            c.drawRect(stx+ 1+j*a, sty+7+(i+b)*a, stx   +(j+1)*a, sty+6+(i+1+b)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>=b*3)      c.drawRect(stx+19+j*a, sty+7+(i+b)*a, stx+18+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b*2) c.drawRect(stx+13+j*a, sty+7+(i+b)*a, stx+12+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b)   c.drawRect(stx+ 7+j*a, sty+7+(i+b)*a, stx+ 6+(j+1)*a, sty+6+(i+1+b)*a, p);
					else            c.drawRect(stx+ 1+j*a, sty+7+(i+b)*a, stx   +(j+1)*a, sty+6+(i+1+b)*a, p);
				}
			for(i=0; i<b; i++)
				for(j=0; j<b; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+7+(j+b)*a, sty+13+(i+2*b)*a, stx+6+(j+1+b)*a, sty+12+(i+1+2*b)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+7+(j+b)*a, sty+13+(i+2*b)*a, stx+6+(j+1+b)*a, sty+12+(i+1+2*b)*a, p);
				}
		}
	}
	
	public byte[] imagestr(String scr, int size) {
		switch (size) {
		case 3:
			scr = scr.replace("M'", "r R'").replace("M2", "r2 R2").replace("M", "r' R")
				.replace("x'", "r' L").replace("x2", "r2 L2").replace("x", "r L'");
			break;
		case 4:
			scr = scr.replace("x'", "r' l").replace("x2", "r2 l2").replace("x", "r l'")
				.replace("y'", "u' d").replace("y2", "u2 d2").replace("y", "u d'")
				.replace("z'", "f' b").replace("z2", "f2 b2").replace("z", "f b'");
			break;
		case 6:
			scr = scr.replace("3R ", "3r r' ");
			break;
		}
		String[] s = scr.split(" ");
		int k, len = s.length;
		if(len > 0) {
			char[] seq = new char[len];
			int count = 0;
			for(int i=0; i<len; i++) {
				k = 0;
				if(s[i].length() > 0) {
					switch(s[i].charAt(0)) {
					case '5': k=4; break;
					case '4': k=3; break;
					case '3': k=2; break;
					case '2': k=1; break;
					case 'R': seq[count]=16; break;
					case 'L': seq[count]=4; break;
					case 'U': seq[count]=12; break;
					case 'D': seq[count]=0; break;
					case 'F': seq[count]=20; break;
					case 'B': seq[count]=8; break;
					case 'r': seq[count]=16; k=1; break;
					case 'l': seq[count]=4; k=1; break;
					case 'u': seq[count]=12; k=1; break;
					case 'd': seq[count]=0; k=1; break;
					case 'f': seq[count]=20; k=1; break;
					case 'b': seq[count]=8; k=1; break;
					}
					if(s[i].length() > 1) {
						switch(s[i].charAt(1)) {
						case '\'': seq[count]+=2; break;
						case '2': seq[count]++; break;
						case 0xb3: k=2; break;
						case 0xb2: k=1; break;
						case 'w': seq[count]+=24; break;
						case 'r':
						case 'R': seq[count]=16; break;
						case 'l':
						case 'L': seq[count]=4; break;
						case 'u':
						case 'U': seq[count]=12; break;
						case 'd':
						case 'D': seq[count]=0; break;
						case 'f':
						case 'F': seq[count]=20; break;
						case 'b':
						case 'B': seq[count]=8; break;
						}
						if(s[i].length() > 2) {
							switch(s[i].charAt(2)){
							case '\'': seq[count]+=2; break;
							case '2': seq[count]++; break;
							}
							if(s[i].length() > 3) {
								switch(s[i].charAt(3)){
								case '\'': seq[count]+=2; break;
								case '2': seq[count]++; break;
								}
							}
						}
					}
					seq[count++] += k * 24;
				}
			}
			if(count > 0) {
				Cube.seq = new int[count];
				for(int i=0; i<count; i++)
					Cube.seq[i] = seq[i];
			}
		}
		return Cube.imagestring();
	}
	
	private void drawSideBackground(Paint p, Canvas c, int width, int cx, int cy, int clock_radius,
			int face_background_dist, int face_background_radius) {
		drawCircle(p, c, width, cx, cy, clock_radius);
		drawCircle(p, c, width, cx-face_background_dist, cy-face_background_dist, face_background_radius);
		drawCircle(p, c, width, cx-face_background_dist, cy+face_background_dist, face_background_radius);
		drawCircle(p, c, width, cx+face_background_dist, cy-face_background_dist, face_background_radius);
		drawCircle(p, c, width, cx+face_background_dist, cy+face_background_dist, face_background_radius);
	}
	
	private void drawCircle(Paint p, Canvas c, int w, int cx, int cy, int rad) {
		float[] scaledPoint = scalePoint(w, cx, cy);
		c.drawCircle(scaledPoint[0], scaledPoint[1], scaledPoint[2]*rad, p);
	}
	private float[] scalePoint(int width, float cx, float cy) {
		float scale = width/220F;
		float x = cx*scale + (width - (220 * scale))/2;
		float y = cy*scale + (width*3/4 - (110 * scale))/2;
		return new float[] {x, y, scale};
	}
	private void drawClockFace(Paint p, Canvas cv, int w, int cx, int cy, int color, int hour) {
		float[] scaled = scalePoint(w, cx, cy);
		//p.setColor(Color.BLACK);
		//drawCircle(p, cv, w, cx, cy, 12);
		p.setColor(color);
		drawCircle(p, cv, w, cx, cy, 11);
		p.setColor(Color.RED);
		drawCircle(p, cv, w, cx, cy, 3);
		float[] arx={scalePoint(w, cx, cy - 10)[0], scalePoint(w, cx + 3, cy - 1)[0], scalePoint(w, cx - 3, cy - 1)[0]},
				ary={scalePoint(w, cx, cy - 10)[1], scalePoint(w, cx + 3, cy - 1)[1], scalePoint(w, cx - 3, cy - 1)[1]};
		p.setColor(color);
		cv.save();
		for(int i=0; i<12; i++) {
			drawCircle(p, cv, w, cx-13, cy, 1);
			cv.rotate(30, scaled[0], scaled[1]);
		}
		cv.restore();
		cv.save();
		cv.rotate(30*hour, scaled[0], scaled[1]);
		drawPolygon(p, cv, Color.RED, arx, ary, false);
		cv.restore();
		p.setColor(Color.YELLOW);
		drawCircle(p, cv, w, cx, cy, 2);
		arx=new float[]{scalePoint(w, cx, cy - 8)[0], scalePoint(w, cx + 2, (float)(cy - 0.5))[0], scalePoint(w, cx - 2, (float)(cy - 0.5))[0]};
		ary=new float[]{scalePoint(w, cx, cy - 8)[1], scalePoint(w, cx + 2, (float)(cy - 0.5))[1], scalePoint(w, cx - 2, (float)(cy - 0.5))[1]};
		cv.save();
		cv.rotate(30*hour, scaled[0], scaled[1]);
		drawPolygon(p, cv, Color.YELLOW, arx, ary, false);
		cv.restore();
	}
	
	private void drawPeg(Paint p, Canvas c, int w, int cx, int cy, int pegValue) {
		int color = pegValue==1?Color.YELLOW:0xff444400;
		p.setColor(0xff2a2a2a);
		drawCircle(p, c, w, cx, cy, 5);
		p.setColor(color);
		drawCircle(p, c, w, cx, cy, 4);
	}
	
	public static void drawPolygon(Paint p, Canvas c, int cl, float[] arx, float[] ary, boolean stoke) {
		p.setColor(cl);
		Path path=new Path();
		path.moveTo(arx[0],ary[0]);
		for(int idx=1;idx<arx.length;idx++)path.lineTo(arx[idx], ary[idx]);
		path.close();
		p.setStyle(Paint.Style.FILL);
		c.drawPath(path, p);
		if(stoke) {
			p.setStyle(Paint.Style.STROKE);
			p.setColor(Color.BLACK);
			c.drawPath(path, p);
		}
	}
	protected void drawPolygon(Paint p, Canvas c, int cl, int w, float[] arx, float[] ary) {
		p.setColor(cl);
		Path path=new Path();
		float[] d = scalePoint(w, arx[0], ary[0]);
		path.moveTo(d[0], d[1]);
		for(int idx=1;idx<arx.length;idx++) {
			d = scalePoint(w, arx[idx], ary[idx]);
			path.lineTo(d[0], d[1]);
		}
		path.close();
		p.setStyle(Paint.Style.FILL);
		c.drawPath(path, p);
		p.setStyle(Paint.Style.STROKE);
		p.setColor(Color.BLACK);
		c.drawPath(path, p);
	}
	
	private float s18(int i) {
		return (float) Math.sin(Math.PI*i/10);
	}
	
	private float c18(int i) {
		return (float) Math.cos(Math.PI*i/10);
	}
	
	private float[][] rotate(float a, float b, float[] x, float[] y, int i) {
		float[][] ary=new float[2][x.length];
		for(int j=0;j<x.length;j++) {
			ary[0][j]=(float) (x[j]*Math.cos(Math.toRadians(i))-y[j]*Math.sin(Math.toRadians(i))+a);
			ary[1][j]=(float) (x[j]*Math.sin(Math.toRadians(i))+y[j]*Math.cos(Math.toRadians(i))+b);
		}
		return ary;
	}
	
	private float cos1(int index, float[] ag, float rd) {
		return (float) (Math.cos(ag[index])*rd);
	}
	
	private float sin1(int index, float[] ag, float rd) {
		return (float) (Math.sin(ag[index])*rd);
	}
	
	private byte[] rd(byte[] arr) {
		byte[] out = new byte[arr.length];
		int j=0;
		for (int i=0; i<arr.length; i++)
			if(i==0 || arr[i]!=arr[i-1])
				out[j++] = arr[i];
		byte[] temp = new byte[j];
		for(int i=0; i<j; i++)temp[i]=out[i];
		return temp;
	}
}
