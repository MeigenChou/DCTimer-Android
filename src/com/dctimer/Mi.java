package com.dctimer;

import java.util.Random;

import com.dctimer.db.*;

import min2phase.Search3;
import min2phase.Tools;
import static min2phase.Util.rotateStr;
import scramblers.*;
import solvers.*;
import sq12phase.*;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;

public class Mi {
	static final int TYPE_SQ1 = 1;
	static final int TYPE_CLK = 12;
	static final int TYPE_MINX = 18;
	static final int TYPE_PYR = 17;
	static final int TYPE_SKW = 16;
	static final int TYPE_FLPY = 13;
	static final int TYPE_DMN = 14;
	static final int TYPE_TOW = 15;
	static final int TYPE_RTW = 19;
	static final int TYPE_334 = 20;
	static final int TYPE_335 = 21;
	static final int TYPE_336 = 22;
	static final int TYPE_337 = 23;
	static final int TYPE_GEAR = 24;
	static final int TYPE_15P = 25;
	static final int TYPE_HLC = 26;
	static final int TYPE_UFO = 27;
	
	static String sc;
	static int viewType;
	public static int scrLen = 0;
	static threephase.Search cube4 = new threephase.Search();
	static Random r = new Random();
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
	public static String[] rot5Str = {"", "3Fw", "3Fw'", "3Fw 3Uw", "3Fw 3Uw2", "3Fw 3Uw'", "3Fw' 3Uw", "3Fw' 3Uw2", "3Fw' 3Uw'", "3Rw", "3Rw2", "3Rw'",
		"3Rw 3Uw", "3Rw 3Uw2", "3Rw 3Uw'", "3Rw2 3Uw", "3Rw2 3Uw2", "3Rw2 3Uw'", "3Rw' 3Uw", "3Rw' 3Uw2", "3Rw' 3Uw'", "3Uw", "3Uw2", "3Uw'"};

	static String setScr(int n, boolean ch) {
		String[][][] turns;
		String[][] turn2;
		String[] end, suff0 = {""}, csuff = {"","2","'"}, suff;
		String scr;
		StringBuffer sb;
		if(ch) {
			if(n < 0) scrLen = defScrLen[21][n&31];
			else scrLen = defScrLen[n>>5][n&31];
		}
		switch(n) {
		case -32:	//三速
		case -27:	//三单
		case -26:	//三少
		case -25:	//三脚
			scr = cube333(); viewType = scr.startsWith("Error") ? 0 : 3;
			break;
		case -31:	//四速
			scr = cube4.randomState(false); viewType = 4; break;
		case -30:	//五速
			scr = cube(5); viewType = 5; break;
		case -29:	//二阶
			scr = Cube222.scramble(4); viewType = 2; break;
		case -28:	//三盲
			int rotate = r.nextInt(24);
			String cube = Tools.randomState(null, null, null, null, rotate);
			scr = new Search3().solution(cube, rotate, 2) + rotateStr[rotate];
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case -24:	//五魔
			scr = Megaminx.scramblestring(); viewType = TYPE_MINX; break;
		case -23:	//金字塔
			scr = Pyraminx.scramble(7); viewType = TYPE_PYR; break;
		case -22:	//SQ1
			scr = new SqSearch().scramble(FullCube.randomCube(), 11); viewType = TYPE_SQ1; break;
		case -21:	//魔表
			scr = Clock.scramble(); viewType = TYPE_CLK; break;
		case -20:	//斜转
			scr = Skewb.scramble(7); viewType = TYPE_SKW; break;
		case -19:	//六阶
			turn2 = new String[][]{{"U","D","Uw","Dw","3Uw"}, {"R","L","Rw","Lw","3Rw"}, {"F","B","Fw","Bw","3Fw"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 6; break;
		case -18:	//七阶
			turn2 = new String[][]{{"U","D","Uw","Dw","3Uw","3Dw"}, {"R","L","Rw","Lw","3Rw","3Lw"}, {"F","B","Fw","Bw","3Fw","3Bw"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 7; break;
		case -17:	//四盲
			scr = cube4.randomState(true); viewType = 4; break;
		case -16:	//五盲
			scr = cube(5) + " " + rot5Str[r.nextInt(24)]; viewType = 5; break;
		case -15:	//多盲
			sb = new StringBuffer();
			for(int j=1; j<=scrLen; j++) {
				int rot = r.nextInt(24);
				String face = Tools.randomState(null, null, null, null, rot);
				sb.append(j + ") " + new Search3().solution(face, rot, 50, 2) + rotateStr[rot]);
				if(j < scrLen) sb.append("\n");
			}
			scr = sb.toString(); viewType = 0; break;
		case 0: //2阶
			scr = Cube222.randomState();
			viewType = 2;
			if(DCTimer.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, DCTimer.stSel[6]);
			break;
		case 1:
			scr = cube(2);
			viewType = 2;
			if(DCTimer.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, DCTimer.stSel[6]);
			break;
		case 2:
			scr = OtherScr.megascramble(new String[][][]{{{"U","D"}}, {{"R","L"}}, {{"F","B"}}}, csuff);
			viewType = 2;
			if(DCTimer.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, DCTimer.stSel[6]);
			break;
		case 3:
			scr = Cube222.randomCLL(); viewType = 2; break;
		case 4:
			scr = Cube222.randomEG1(); viewType = 2; break;
		case 5:
			scr = Cube222.randomEG2(); viewType = 2; break;
		case 6:
			scr = Cube222.randomXLL(); viewType = 2; break;
		case 7:
			scr = Cube222.egScr(DCTimer.egtype, DCTimer.egolls); viewType = 2; break;
		case 8:
			scr = Cube222.randomTCLL(1); viewType = 2; break;
		case 9:
			scr = Cube222.randomTCLL(2); viewType = 2; break;
		case 10:
			scr = Cube222.randomTEG1(1); viewType = 2; break;
		case 11:
			scr = Cube222.randomTEG1(2); viewType = 2; break;
		case 12:
			scr = Cube222.randomTEG2(1); viewType = 2; break;
		case 13:
			scr = Cube222.randomTEG2(2); viewType = 2; break;
		case 32: //3阶
			scr = cube(3); viewType = 3;
			if(DCTimer.stSel[5]==1) sc = "\n"+Cross.cross(scr, DCTimer.solSel[0], DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==2) sc = "\n"+Cross.xcross(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==3) sc = "\n"+EOline.eoLine(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==4) sc = "\n"+PetrusxRoux.roux(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==5) sc = "\n"+PetrusxRoux.petrus(scr, DCTimer.solSel[1]);
			break;
		case 33:
			scr = cube333(); viewType = scr.startsWith("Error") ? 0 : 3;
			if(DCTimer.stSel[5]==1) sc = "\n"+Cross.cross(scr, DCTimer.solSel[0], DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==2) sc = "\n"+Cross.xcross(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==3) sc = "\n"+EOline.eoLine(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==4) sc = "\n"+PetrusxRoux.roux(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==5) sc = "\n"+PetrusxRoux.petrus(scr, DCTimer.solSel[1]);
			break;
		case 34:
			scr = new Search3().solution(Tools.randomCrossSolved(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 35:
			scr = new Search3().solution(Tools.randomLastLayer(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 36:
			scr = new Search3().solution(Tools.randomPLL(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 37:
			scr = new Search3().solution(Tools.randomCornerSolved(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3;
			if(DCTimer.stSel[5]==1) sc = "\n"+Cross.cross(scr, DCTimer.solSel[0], DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==2) sc = "\n"+Cross.xcross(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==3) sc = "\n"+EOline.eoLine(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==4) sc = "\n"+PetrusxRoux.roux(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==5) sc = "\n"+PetrusxRoux.petrus(scr, DCTimer.solSel[1]);
			break;
		case 38:
			scr = new Search3().solution(Tools.randomEdgeSolved(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 39:
			scr = new Search3().solution(Tools.randomLastSlot(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 40:
			scr = new Search3().solution(Tools.randomZBLastLayer(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 41:
			scr = new Search3().solution(Tools.randomEdgeOfLastLayer(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 42:
			scr = new Search3().solution(Tools.randomCornerOfLastLayer(), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 43:
			switch (r.nextInt(4)) {
			case 0:
				scr = new Search3().solution(Tools.randomState(new byte[]{0,1,2,3,4,5,6,7}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 2);
				break;
			case 1:
				scr = new Search3().solution(Tools.randomState(new byte[]{3,2,6,7,0,1,5,4}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 2) + "x'";
				break;
			case 2:
				scr = new Search3().solution(Tools.randomState(new byte[]{7,6,5,4,3,2,1,0}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 2) + "x2";
				break;
			default:
				scr = new Search3().solution(Tools.randomState(new byte[]{4,5,1,0,7,6,2,3}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 2) + "x";
				break;
			}
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 44:
			switch (r.nextInt(4)) {
			case 0:
				scr = new Search3().solution(Tools.randomState(new byte[]{-1,-1,-1,-1,4,5,6,7}, new byte[]{-1,-1,-1,-1,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 2);
				break;
			case 1:
				scr = new Search3().solution(Tools.randomState(new byte[]{3,2,-1,-1,0,1,-1,-1}, new byte[]{2,1,-1,-1,1,2,-1,-1}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 2) + "x'";
				break;
			case 2:
				scr = new Search3().solution(Tools.randomState(new byte[]{7,6,5,4,-1,-1,-1,-1}, new byte[]{0,0,0,0,-1,-1,-1,-1}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 2) + "x2";
				break;
			default:
				scr = new Search3().solution(Tools.randomState(new byte[]{-1,-1,1,0,-1,-1,2,3}, new byte[]{-1,-1,2,1,-1,-1,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 2) + "x";
				break;
			}
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 45:
			scr = new Search3().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 46:
			scr = new Search3().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_RANDOM), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 47:
			scr = new Search3().solution(Tools.randomState(Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_SOLVED), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 48:
			scr = new Search3().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_SOLVED), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 49:
			scr = new Search3().solution(Tools.randomState(Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 50:
			scr = new Search3().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_RANDOM), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 51:
			scr = new Search3().solution(Tools.randomEasyCross(scrLen), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; 
			if(DCTimer.stSel[5]==1) sc = "\n"+Cross.cross(scr, DCTimer.solSel[0], DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==2) sc = "\n"+Cross.xcross(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==3) sc = "\n"+EOline.eoLine(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==4) sc = "\n"+PetrusxRoux.roux(scr, DCTimer.solSel[1]);
			else if(DCTimer.stSel[5]==5) sc = "\n"+PetrusxRoux.petrus(scr, DCTimer.solSel[1]);
			break;
		case 52:
			scr = new Search3().solution(Tools.randomState(Tools.STATE_SOLVED, new byte[]{-1,-1,-1,-1,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,5,6,7,8,9,10,11}, Tools.STATE_SOLVED), 2);
			viewType = scr.startsWith("Error") ? 0 : 3; break;
		case 64: //4阶
			scr = cube(4); viewType = 4; break;
		case 65:
			turn2 = new String[][]{{"U","D","u"}, {"R","L","r"}, {"F","B","f"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 4; break;
		case 66:
			scr = OtherScr.yj4x4(); viewType = 4; break;
		case 67:
			end = new String[]{"Bw2 Rw'", "Bw2 U2 Rw U2 Rw U2 Rw U2 Rw"};
			scr = OtherScr.edgescramble("Rw Bw2", end, new String[]{"Uw"}); viewType = 4; break;
		case 68:
			scr = OtherScr.megascramble(new String[][]{{"U","u"},{"R","r"}}, csuff);
			viewType = 4; break;
		case 69:	//TODO
			scr = cube4.randomState(false); viewType = 4; break;
		case 96: //5阶
			scr = cube(5); viewType = 5; break;
		case 97:
			turn2 = new String[][]{{"U","D","u","d"}, {"R","L","r","l"}, {"F","B","f","b"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 5; break;
		case 98:
			end = new String[]{"B' Bw' R' Rw'", "B' Bw' R' U2 Rw U2 Rw U2 Rw U2 Rw"};
			scr = OtherScr.edgescramble("Rw R Bw B", end, new String[]{"Uw", "Dw"}); viewType = 5; break;
		case 128: //6阶
			scr = cube(6); viewType = 6; break;
		case 129:
			turn2 = new String[][]{{"U","D","u","d","3u"}, {"R","L","r","l","3r"}, {"F","B","f","b","3f"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 6; break;
		case 130:
			turn2 = new String[][]{{"U","D","U²","D²","U³"}, {"R","L","R²","L²","R³"}, {"F","B","F²","B²","F³"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 6; break;
		case 131:
			end = new String[]{"3b' b' 3r' r'", "3b' b' 3r' U2 r U2 r U2 r U2 r", "3b' b' r' U2 3r U2 3r U2 3r U2 3r", "3b' b' r2 U2 3R U2 3R U2 3R U2 3R "};
			scr = OtherScr.edgescramble("3r r 3b b", end, new String[]{"u","3u","d"}); viewType = 6; break;
		case 160: //7阶
			scr = cube(7); viewType = 7; break;
		case 161:
			turn2 = new String[][]{{"U","D","u","d","3u","3d"}, {"R","L","r","l","3r","3l"}, {"F","B","f","b","3f","3b"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 7; break;
		case 162:
			turn2 = new String[][]{{"U","D","U²","D²","U³","D³"}, {"R","L","R²","L²","R³","L³"}, {"F","B","F²","B²","F³","B³"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 7; break;
		case 163:
			end = new String[]{"3b' b' 3r' r'", "3b' b' 3r' U2 r U2 r U2 r U2 r", "3b' b' r' U2 3r U2 3r U2 3r U2 3r", "3b' b' r2 U2 3R U2 3R U2 3R U2 3R"};
			scr = OtherScr.edgescramble("3r r 3b b", end, new String[]{"u","3u","3d","d"}); viewType = 7; break;
		case 192: //五魔
			scr = Megaminx.scramblestring(); viewType = TYPE_MINX; break;
		case 193:
			scr = OtherScr.oldminxscramble(); viewType = 0; break;
		case 224: //金字塔
			scr = Pyraminx.scramble(); viewType = TYPE_PYR; break;
		case 225:
			turn2 = new String[][]{{"U"}, {"L"}, {"R"}, {"B"}};
			suff = new String[]{"", "'"};
			String[][] ss = {{"","b ","b' "}, {"","l ","l' "}, {"","u ","u' "}, {"","r ","r' "}};
			int cnt = 0;
			int[] rnd = new int[4];
			for(int i=0; i<4; i++) {
				rnd[i] = r.nextInt(3);
				if(rnd[i] > 0) cnt++;
				if(cnt >= scrLen)break;
			}
			scrLen -= cnt;
			scr = ss[0][rnd[0]] + ss[1][rnd[1]] + ss[2][rnd[2]] + ss[3][rnd[3]] + OtherScr.megascramble(turn2, suff);
			scrLen += cnt;
			viewType = TYPE_PYR;
			break;
		case 256:  //SQ1
			scr = SQ1.scramblestring();
			if(DCTimer.stSel[12] > 0) sc = " " + (DCTimer.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType = TYPE_SQ1; break;
		case 257:
			scr = OtherScr.sq1_scramble(0);
			if(DCTimer.stSel[12] > 0) sc = " " + (DCTimer.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType = TYPE_SQ1; break;
		case 258:
			scr = new SqSearch().solution(FullCube.randomCube());
			if(DCTimer.stSel[12] > 0) sc = " " + (DCTimer.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType = TYPE_SQ1; break;
		case 259:
			scr = new SqSearch().solution(FullCube.randomCube(1037)); viewType = TYPE_SQ1; break;
		case 288:	//魔表
			scr = Clock.scramble(); viewType = TYPE_CLK; break;
		case 289:
			scr = Clock.scrambleOld(false); viewType = TYPE_CLK; break;
		case 290:
			scr = Clock.scrambleOld(true); viewType = TYPE_CLK; break;
		case 291:
			scr = Clock.scrambleEpo(); viewType = TYPE_CLK; break;
		case 320:	//Skewb
			scr = Skewb.scramble(); viewType = TYPE_SKW; break;
		case 321:
			turn2 = new String[][]{{"R"}, {"U"}, {"L"}, {"B"}};
			suff = new String[]{"", "'"};
			scr = OtherScr.megascramble(turn2, suff);
			viewType = TYPE_SKW; break;
		case 352:	//MxNxL
			turn2 = new String[][]{{"R","L"}, {"U","D"}};
			scr = OtherScr.megascramble(turn2, new String[]{"2"}); viewType = TYPE_FLPY; break;
		case 353:
			turn2 = new String[][]{{"R","L"}, {"U","D"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 0; break;
		case 354:
			scr = Floppy.solve(r); viewType = TYPE_FLPY; break;
		case 355:
			turns = new String[][][]{{{"R2","L2","R2 L2"}}, {{"U","U'","U2"}}, {{"F2","B2","F2 B2"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 14; break;
		case 356:
			scr = Domino.scramble(r); viewType = TYPE_DMN; break;
		case 357:
			scr = Tower.solve(r); viewType = TYPE_TOW; break;
		case 358:
			scr = RTower.scramble(r); viewType = 0; break;
		case 359:	//334
			turns = new String[][][]{{{"U", "U'", "U2"}, {"u", "u'", "u2"}}, {{"R2","L2","M2"}}, {{"F2","B2","S2"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 0; break;
		case 360:	//335
			turns = new String[][][]{{{"U","U'","U2"}, {"D", "D'", "D2"}}, {{"R2"}, {"L2"}}, {{"F2"}, {"B2"}}};
			scr = OtherScr.megascramble(turns, suff0) + "/ " + Cube.scramblestring(3, 25); viewType = 0;
			break;
		case 361:	//336
			turns = new String[][][]{{{"U", "U'", "U2"}, {"u", "u'", "u2"}, {"3u", "3u'", "3u2"}}, {{"R2","L2","M2"}}, {{"F2","B2","S2"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 0; break;
		case 362:	//337
			turns = new String[][][]{{{"U", "U'", "U2"}, {"u", "u'", "u2"}, {"D", "D'", "D2"}, {"d", "d'", "d2"}}, {{"R2"}, {"L2"}}, {{"F2"}, {"B2"}}};
			scr = OtherScr.megascramble(turns, suff0) + "/ " + Cube.scramblestring(3, 25); viewType = 0;
			break;
		case 363:
			scr = cube(8); viewType = 8; break;
		case 364:
			scr = cube(9); viewType = 9; break;
		case 365:
			scr = cube(10); viewType = 10; break;
		case 366:
			scr = cube(11); viewType = 11; break;
		case 384:	//Cmetrick
			turns = new String[][][]{{{"U<","U>","U2"}, {"E<","E>","E2"}, {"D<","D>","D2"}}, {{"R^","Rv","R2"}, {"M^","Mv","M2"}, {"L^","Lv","L2"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 0; break;
		case 385:
			turns = new String[][][]{{{"U<","U>","U2"}, {"D<","D>","D2"}}, {{"R^","Rv","R2"}, {"L^","Lv","L2"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 0; break;
		case 416:	//齿轮
			scr = Gear.solve(r); viewType = 0; break;
		case 417:
			turn2 = new String[][]{{"U"}, {"R"}, {"F"}};
			suff = new String[]{"", "2", "3", "4", "5", "6", "'", "2'", "3'", "4'", "5'"};
			scr = OtherScr.megascramble(turn2, suff);
			viewType = 0; break;
		case 448:	//Siamese Cube
			turn2 = new String[][]{{"U","u"}, {"R","r"}};
			scr = OtherScr.megascramble(turn2, csuff) + "z2 " + OtherScr.megascramble(turn2, csuff);
			viewType = 0; break;
		case 449:
			turn2 = new String[][]{{"R","r"}, {"U"}};
			scr = OtherScr.megascramble(turn2, csuff) + "z2 " + OtherScr.megascramble(turn2, csuff);
			viewType = 0; break;
		case 450:
			turn2 = new String[][]{{"U"}, {"R"}, {"F"}};
			scr = OtherScr.megascramble(turn2, csuff) + "z2 y " + OtherScr.megascramble(turn2, csuff);
			viewType = 0; break;
		case 480:	//15puzzles
			scr = OtherScr.do15puzzle(false); viewType = 0; break;
		case 481:
			scr = OtherScr.do15puzzle(true); viewType = 0; break;
		case 512:	//Other
			scr = LatchCube.scramble(); viewType = 0; break;
		case 513:
			scr = OtherScr.helicubescramble(); viewType = 0; break;
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
			scr = OtherScr.ssq1t_scramble(); viewType = 0; break;
		case 516:	//UFO
			turns = new String[][][]{{{"A"}}, {{"B"}}, {{"C"}}, {{"U","U'","U2'","U2","U3"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 0; break;
		case 517:	//FTO
			turn2 = new String[][]{{"U","D"}, {"F","B"}, {"L","BR"}, {"R","BL"}};
			suff = new String[]{"", "'"};
			scr = OtherScr.megascramble(turn2, suff); viewType = 0; break;
		case 544:	//3x3x3 subsets
			//turn2 = new String[][]{{"U"}, {"R"}};
			//scr = OtherScr.megascramble(turn2, csuff);
			scr = CubeRU.solve(r); viewType = 3; break;
		case 545:
			//turn2 = new String[][]{{"U"}, {"L"}};
			//scr = OtherScr.megascramble(turn2, csuff);
			scr = CubeRU.solve(r).replace('R', 'L');
			viewType = 3; break;
		case 546:
			//turn2 = new String[][]{{"U"}, {"M"}};
			//scr = OtherScr.megascramble(turn2, csuff);
			scr = RouxMU.solve(r); viewType = 3; break;
		case 547:
			turn2 = new String[][]{{"U"}, {"R"}, {"F"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 3; break;
		case 548:
			turn2 = new String[][]{{"R", "L"}, {"U"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 3; break;
		case 549:
			turn2 = new String[][]{{"R", "r"}, {"U"}};
			scr = OtherScr.megascramble(turn2, csuff); viewType = 3; break;
		case 550:
			//turn2 = new String[][]{{"U","D"}, {"R","L"}, {"F","B"}};
			//suff = new String[]{"2"};
			//scr = OtherScr.megascramble(turn2, suff, 25);
			scr = HalfTurn.solve(r); viewType = 3; break;
		case 551:	//LSLL
			turns = new String[][][]{{{"R U R'","R U2 R'","R U' R'"}}, {{"F' U F","F' U2 F","F' U' F"}}, {{"U","U2","U'"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 3; break;
		case 576:	//Bandaged Cube
			scr = OtherScr.bicube(); viewType = 0; break;
		case 577:
			scr = OtherScr.sq1_scramble(2); viewType = TYPE_SQ1; break;
		case 608:	//五魔子集
			turn2 = new String[][]{{"U"}, {"R"}};
			scr = OtherScr.megascramble(turn2, new String[]{"", "2", "2'", "'"}); viewType = 0; break;
		case 609:
			turns = new String[][][]{{{"R U R'","R U2 R'","R U' R'","R U2' R'"}}, {{"F' U F","F' U2 F","F' U' F","F' U2' F"}}, {{"U","U2","U'","U2'"}}};
			scr = OtherScr.megascramble(turns, suff0); viewType = 0; break;
		case 640:	//连拧
			scr = "2) " + Cube222.randomState() + "\n3) " + cube333() + "\n4) " + cube4(csuff);
			viewType = 0; break;
		case 641:
			scr = "2) " + Cube222.randomState() + "\n3) " + cube333() + "\n4) " + cube4(csuff) + "\n5) " + cube5(csuff);
			viewType = 0; break;
		case 642:
			scr = "2) " + Cube222.randomState() + "\n3) " + cube333() + "\n4) " + cube4(csuff)
				+"\n5) " + cube5(csuff) + "\n6) " + cube6(csuff);
			viewType = 0; break;
		case 643:
			scr = "2) " + Cube222.randomState() + "\n3) " + cube333() + "\n4) " + cube4(csuff)
				+"\n5) " + cube5(csuff) + "\n6) " + cube6(csuff) + "\n7) " + cube7(csuff);
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

	private static String cube(int n) {
		return Cube.scramblestring(n, scrLen);
	}	
	private static String cube333() {
		return new Search3().solution(Tools.randomCube(), 2);
	}
	private static String cube4(String[] suf) {
		return OtherScr.megascramble(new String[][]{{"U","D","u"}, {"R","L","r"}, {"F","B","f"}}, suf, 40);
	}
	private static String cube5(String[] suf) {
		return OtherScr.megascramble(new String[][]{{"U","D","u","d"}, {"R","L","r","l"}, {"F","B","f","b"}}, suf, 60);
	}
	private static String cube6(String[] suf) {
		return OtherScr.megascramble(new String[][]{{"U","D","u","d","3u"}, {"R","L","r","l","3r"}, {"F","B","f","b","3f"}}, suf, 80);
	}
	private static String cube7(String[] suf) {
		return OtherScr.megascramble(new String[][]{{"U","D","u","d","3u","3d"}, {"R","L","r","l","3r","3l"}, {"F","B","f","b","3f","3b"}}, suf, 100);
	}

	static void drawScr(int sel2, int width, Paint p, Canvas c) {
		int[] colors={DCTimer.share.getInt("csn1", Color.YELLOW), DCTimer.share.getInt("csn2", Color.BLUE), DCTimer.share.getInt("csn3", Color.RED),
				DCTimer.share.getInt("csn4", Color.WHITE), DCTimer.share.getInt("csn5", 0xff009900), DCTimer.share.getInt("csn6", 0xffff8026)};
		//2阶
		if(viewType==2) {
			Cube.parse(2);
			byte[] imst = OtherScr.imagestr(DCTimer.crntScr, 2);
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
			if(DCTimer.stSel[7]==0)
				colors = new int[] {Color.WHITE, 0xff000088, 0xff008800, 0xff00ffff, 0xff882222, 0xff88aaff,
					Color.RED, Color.BLUE, 0xffff00ff, Color.GREEN, 0xffff8800, Color.YELLOW};
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
			byte[] img = Megaminx.image();
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
			p.setTextAlign(Align.CENTER);
			p.setTextSize((float) (width * 0.0593));
			c.drawText("U", (float)(width*0.262), (float)(width*0.367), p);
			c.drawText("F", (float)(width*0.262), (float)(width*0.535), p);
		}
		else if(viewType == TYPE_PYR) {
			byte[] imst = Pyraminx.imageString(DCTimer.crntScr);
			int b = (width * 3 / 4 - 15) / 6;
			int a = (int) (b * 2 / Math.sqrt(3));
			int d = (int) ((width - a * 6 - 21) / 2);
			colors = new int[]{DCTimer.share.getInt("csp1", Color.RED), DCTimer.share.getInt("csp2", 0xff009900),
					DCTimer.share.getInt("csp3", Color.BLUE), DCTimer.share.getInt("csp4", Color.YELLOW)};
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
			colors = new int[]{DCTimer.share.getInt("csq1", Color.YELLOW), DCTimer.share.getInt("csq6", 0xffff8026), DCTimer.share.getInt("csq2", Color.BLUE),
					DCTimer.share.getInt("csq4", Color.WHITE), DCTimer.share.getInt("csq3", Color.RED), DCTimer.share.getInt("csq5", 0xff009900)};
			byte[] img = SQ1.imagestr(DCTimer.crntScr.split(" "));
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
			byte[] posit;
			posit = Clock.posit();
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
			byte[] pegs = Clock.pegs();
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
		else if(viewType == TYPE_FLPY) {
			byte[] imst = Floppy.image(DCTimer.crntScr);
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
			byte[] imst=Domino.image(DCTimer.crntScr);
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
			byte[] imst=Tower.image(DCTimer.crntScr);
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
			byte[] imst = Skewb.image(DCTimer.crntScr);
			colors = new int[] {DCTimer.share.getInt("csw4", Color.WHITE), DCTimer.share.getInt("csw6", 0xffff8026), DCTimer.share.getInt("csw5", 0xff009900),
					DCTimer.share.getInt("csw3", Color.RED), DCTimer.share.getInt("csw2", Color.BLUE), DCTimer.share.getInt("csw1", Color.YELLOW)};
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
			if(DCTimer.isInScr) {
				Cube.parse(viewType);
				imst = OtherScr.imagestr(DCTimer.crntScr, viewType);
			}
			else if(viewType == 3) {
				Cube.parse(3);
				imst = OtherScr.imagestr(DCTimer.crntScr, viewType);
			}
			else if(viewType > 7) imst = Cube.imagestring();
			else if(sel2 == 0) imst = Cube.imagestring();
			else {
				Cube.parse(viewType);
				imst = OtherScr.imagestr(DCTimer.crntScr, viewType);
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
	
	private static void drawSideBackground(Paint p, Canvas c, int width, int cx, int cy, int clock_radius,
			int face_background_dist, int face_background_radius) {
		drawCircle(p, c, width, cx, cy, clock_radius);
		drawCircle(p, c, width, cx-face_background_dist, cy-face_background_dist, face_background_radius);
		drawCircle(p, c, width, cx-face_background_dist, cy+face_background_dist, face_background_radius);
		drawCircle(p, c, width, cx+face_background_dist, cy-face_background_dist, face_background_radius);
		drawCircle(p, c, width, cx+face_background_dist, cy+face_background_dist, face_background_radius);
	}
	private static void drawCircle(Paint p, Canvas c, int w, int cx, int cy, int rad) {
		float[] scaledPoint = scalePoint(w, cx, cy);
		c.drawCircle(scaledPoint[0], scaledPoint[1], scaledPoint[2]*rad, p);
	}
	private static float[] scalePoint(int width, float cx, float cy) {
		float scale = width/220F;
		float x = cx*scale + (width - (220 * scale))/2;
		float y = cy*scale + (width*3/4 - (110 * scale))/2;
		return new float[] {x, y, scale};
	}
	private static void drawClockFace(Paint p, Canvas cv, int w, int cx, int cy, int color, int hour) {
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
	private static void drawPeg(Paint p, Canvas c, int w, int cx, int cy, int pegValue) {
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
	protected static void drawPolygon(Paint p, Canvas c, int cl, int w, float[] arx, float[] ary) {
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

	
	
	private static float s18(int i) {
		return (float) Math.sin(Math.PI*i/10);
	}
	
	private static float c18(int i) {
		return (float) Math.cos(Math.PI*i/10);
	}
	
	private static float[][] rotate(float a, float b, float[] x, float[] y, int i) {
		float[][] ary=new float[2][x.length];
		for(int j=0;j<x.length;j++) {
			ary[0][j]=(float) (x[j]*Math.cos(Math.toRadians(i))-y[j]*Math.sin(Math.toRadians(i))+a);
			ary[1][j]=(float) (x[j]*Math.sin(Math.toRadians(i))+y[j]*Math.cos(Math.toRadians(i))+b);
		}
		return ary;
	}
	
	private static float cos1(int index, float[] ag, float rd) {
		return (float) (Math.cos(ag[index])*rd);
	}
	
	private static float sin1(int index, float[] ag, float rd) {
		return (float) (Math.sin(ag[index])*rd);
	}
	
	private static byte[] rd(byte[] arr) {
		byte[] out = new byte[arr.length];
		int j=0;
		for (int i=0; i<arr.length; i++)
			if(i==0 || arr[i]!=arr[i-1])
				out[j++] = arr[i];
		byte[] temp = new byte[j];
		for(int i=0; i<j; i++)temp[i]=out[i];
		return temp;
	}

	static String convStr(String s) {
		if(s==null || s.length()==0 || s.equals("0"))return "Error";
		StringBuffer sb=new StringBuffer();
		byte dot=0, colon=0;
		int num=0;
		boolean dbc=false;
		for(int i=0;i<s.length();i++) {
			if(Character.isDigit(s.charAt(i))) {sb.append(s.charAt(i)); num++;}
			if(s.charAt(i)=='.' && dot<1) {sb.append('.'); dot++; dbc=true;}
			if(s.charAt(i)==':' && colon<2 && !dbc) {sb.append(':'); colon++;}
		}
		if(num==0)return "Error";
		sb.insert(0, dot+""+colon);
		return sb.toString();
	}
	static int convTime(String s) {
		if(s.charAt(1)=='0')return (int)(Double.parseDouble(s.substring(2))*1000+0.5);
		int hour, min;
		double sec;
		String[] time=s.substring(2).split(":");
		if(s.charAt(1)=='1') {
			hour=0;
			min=time[0].length()==0?0:Integer.parseInt(time[0]);
			if(time.length==1)sec=0;
			else sec=time[1].length()==0?0:Double.parseDouble(time[1]);
		}
		else {
			hour=time[0].length()==0?0:Integer.parseInt(time[0]);
			if(time.length==1)min=0;
			else min=time[1].length()==0?0:Integer.parseInt(time[1]);
			if(time.length<3)sec=0;
			else sec=time[2].length()==0?0:Double.parseDouble(time[2]);
		}
		return (int)((hour*3600+min*60+sec)*1000+0.5);
	}

	private static int getDivision(int dv) {
		int[] ds = {100, 200, 500, 1000, 2000, 5000, 10000, 20000, 30000, 60000, 90000, 120000, 300000, 600000, 1200000, 1800000, 3600000};
		if(dv < ds[0]) return 100;
		for(int i=1; i<17; i++)
			if(ds[i-1] < dv && dv < ds[i]) return ds[i];
		return (dv/1000+1)*1000;
	}
	
	static void drawHist(int width, Paint p, Canvas c) {
		int[] bins = new int[14];
		int start;
		int end;
		if(Session.resl==0 || Statistics.minIdx==-1 || Statistics.maxIdx==-1) {
			start = 13000;
			end = 27000;
		} else {
			int max = Session.rest[Statistics.maxIdx]+Session.resp[Statistics.maxIdx]*2000;
			int min = Session.rest[Statistics.minIdx]+Session.resp[Statistics.minIdx]*2000;
			int divi = getDivision((max - min) / 14);
			int mean = (min & max) + ((min ^ max) >> 1);
			mean = ((mean + divi / 2) / divi) * divi;
			start = mean - divi * 7;
			end = mean + divi * 7;
		}
		for (int i = 0; i < bins.length; i++)
			bins[i] = 0;
		for (int i = 0; i < Session.resl; i++) {
			if(Session.resp[i]!=2) {
				int time=Session.rest[i]+Session.resp[i]*2000;
				if(time >= start && time < end) {
					int bin = (int) (bins.length * (time - start) / (end - start));
					bins[bin]++;
				}
			}
		}
		int wBase = 60*width/288;
		c.drawLine(wBase, 0, wBase, (int)(width*1.2), p);
		float wBar = (float) (width*1.2 / (bins.length+1));
		for (int i = 0; i < bins.length+1; i++) {
			float y = (float) ((i + 0.5) * wBar);
			c.drawLine(wBase - 4, y, wBase + 4, y, p);
		}
		float binInterval = (float)(end - start) / bins.length;
		p.setTextSize(wBase*2/9);
		p.setTextAlign(Align.RIGHT);
		FontMetrics fm = p.getFontMetrics();
		float fontHeight = fm.bottom - fm.top;
		for (int i = 0; i < bins.length+1; i++) {
			int value = (int)(start + i * binInterval);
			float y = (float) ((i + 0.5) * wBar + fontHeight / 2 - fm.bottom);
			c.drawText(distime(value), wBase-5, y, p);
		}
		int maxValue = 0;
		for (int i = 0; i < bins.length; i++) {
			if (bins[i] > maxValue)
				maxValue = bins[i];
		}
		if (maxValue > 0) {
			for (int i = 0; i < bins.length; i++) {
				float y1 = (float) ((i + 0.5) * wBar);
				float y2 = (float) ((i + 1.5) * wBar);
				int height = (int) (bins[i] * (width - wBase - 4) / maxValue);
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.WHITE);
				c.drawRect(wBase + height, y1, wBase, y2, p);
				p.setStyle(Paint.Style.STROKE);
				p.setColor(Color.BLACK);
				c.drawRect(wBase + height, y1, wBase, y2, p);
			}
		}
	}

	static void drawGraph(int width, Paint p, Canvas c) {
		int up, down, mean;
		int blk, divi;
		if(Session.resl==0 || Statistics.minIdx==-1 || Statistics.minIdx==Statistics.maxIdx) {
			up = 20000;
			down = 12000;
			mean = 16000;
			blk = 8;
			divi = 1000;
		} else {
			int max = Session.rest[Statistics.maxIdx]+Session.resp[Statistics.maxIdx]*2000;
			int min = Session.rest[Statistics.minIdx]+Session.resp[Statistics.minIdx]*2000;
			divi = getDivision((max-min)/8);
			mean = (min & max) + ((min ^ max) >> 1);
			mean = ((mean + divi / 2) / divi) * divi;
			up = down = mean;
			while (up < max) {
				up += divi;
			}
			while (down > min) {
				down -= divi;
			}
			mean = Statistics.sesMean;
			blk = (up - down) / divi;
		}
		int wBase = 45 * width / 288;
		p.setTextSize(wBase *2 / 9);
		p.setTextAlign(Align.RIGHT);
		FontMetrics fm = p.getFontMetrics();
		float fontHeight = fm.bottom - fm.top;
		float wBar = (float) ((width * 0.8 - wBase / 4.5) / blk);
		p.setColor(0xff808080);
		for (int i = 0; i < blk+1; i++) {
			float y = (float) (i * wBar + wBase/9.);
			c.drawLine(wBase, y, width, y, p);
		}
		c.drawLine(wBase, (float)(wBase/9.), wBase, (float)(width*0.8-wBase/9.), p);
		p.setColor(0xffff0000);
		float y = (float) ((double)(up - mean) / divi * wBar + wBase/9.);
		c.drawLine(wBase, y, width, y, p);
		p.setColor(0xff000000);
		for (int i = 0; i < blk+1; i++) {
			int value = (int)(up - i * divi);
			y = (float) (i * wBar + wBase/9. + fontHeight / 2 - fm.bottom);
			c.drawText(distime(value), wBase-4, y, p);
		}
		int count = 0;
		for(int i=0; i<Session.resl; i++) 
			if(Session.resp[i] != 2) count++;
		float rsp = (float) ((double)(width - 8 - wBase) / (count-1));
		count = 0;
		float lastx = -1, lasty = -1;
		for(int i=0; i<Session.resl; i++) {
			if(Session.resp[i] != 2) {
				int time = Session.rest[i] + Session.resp[i] * 2000;
				float x = (float) (wBase + 4.0 + (count++) * rsp);
				y = (float) ((double)(up - time) / divi * wBar + wBase/9.);
				//c.drawRect(x-2, y-2, x+2, y-2, p);
				c.drawCircle(x, y, 3, p);
				if(lastx!=-1) c.drawLine(lastx, lasty, x, y, p);
				lastx = x; lasty = y;
			}
		}
	}
	
	private static String distime(int i) {
		boolean m = i < 0;
		i = Math.abs(i) + 5;
		int ms = (i % 1000) / 100;
		int s = i / 1000;
		int mi = 0, h = 0;
		if(DCTimer.stSel[13] < 2) {
			mi = s / 60;
			s %= 60;
			if(DCTimer.stSel[13] < 1) {
				h = mi / 60;
				mi %= 60;
			}
		}
		return (m?"-":"")+(h>0?h+":":"")+(h>0?(mi<10?"0"+mi+":":mi+":"):(mi>0?mi+":":""))+(((h>0 || mi>0) && s<10)?"0":"")+s+"."+ms;
	}
	
	static int getSessionType(long sesType, int idx) {
		return (int) ((sesType >> (7*idx)) & 0x7f);
	}
}
