package com.dctimer;

import java.util.Random;

import min2phase.*;
import scramblers.*;
import solvers.*;
import sq12phase.*;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;

public class Mi {
	//public static long stime=0L;
	public static int[] bavg = {0, 0};
	public static int[] bidx = {0, 0};
	public static int sesMean = -1;
	public static int sesSD;
	public static int minIdx, maxIdx;
	public static String sc;
	public static int viewType;
	public static int scrLen = 0;
	private static short[][] defScrLen = {
		{0, 15, 15, 0, 0, 0, 0, 0, 0, 0},
		{25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0},
		{40, 40, 40, 8, 40},
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
	};

	public static String SetScr(int n, boolean ch) {
		String[][][] turns;
		String[][] turn2;
		String[] end, suff0 = {""}, csuff = {"","2","'"}, suff;
		String scr;
		if(ch)scrLen = defScrLen[n>>5][n&31];
		switch(n) {
		case 0: //2阶
			scr = Cube222.randomState(); viewType = 2;
			if(DCTimer.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, DCTimer.stSel[6]); break;
		case 1:
			scr = cube(2); viewType = 2;
			if(DCTimer.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, DCTimer.stSel[6]); break;
		case 2:
			scr = OtherScr.megascramble(new String[][][]{{{"U","D"}}, {{"R","L"}}, {{"F","B"}}}, csuff); viewType = 2;
			if(DCTimer.stSel[6]>0) sc = "\n" + Cube2bl.cube2layer(scr, DCTimer.stSel[6]); break;
		case 3:
			scr = Cube222.randomCLL(); viewType = 2; break;
		case 4:
			scr = Cube222.randomEG1(); viewType = 2; break;
		case 5:
			scr = Cube222.randomEG2(); viewType = 2; break;
		case 6:
			scr=Cube222.randomXLL();viewType=2;break;
		case 7:
			scr=Cube222.egScr(DCTimer.egtype, DCTimer.egolls); viewType=2; break;
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
			scr=cube(3);viewType=3;
			if(DCTimer.stSel[5]==1)sc="\n"+Cross.cross(scr, DCTimer.spSel[1], DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==2)sc="\n"+Cross.xcross(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==3)sc="\n"+EOline.eoLine(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==4)sc="\n"+PetrusxRoux.roux(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==5)sc="\n"+PetrusxRoux.petrus(scr, DCTimer.spSel[3]);
			break;
		case 33:
			scr = new Search().solution(Tools.randomCube(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;
			if(DCTimer.stSel[5]==1)sc="\n"+Cross.cross(scr, DCTimer.spSel[1], DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==2)sc="\n"+Cross.xcross(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==3)sc="\n"+EOline.eoLine(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==4)sc="\n"+PetrusxRoux.roux(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==5)sc="\n"+PetrusxRoux.petrus(scr, DCTimer.spSel[3]);
			break;
		case 34:
			scr = new Search().solution(Tools.randomCrossSolved(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 35:
			scr = new Search().solution(Tools.randomLastLayer(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 36:
			scr = new Search().solution(Tools.randomPLL(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 37:
			scr = new Search().solution(Tools.randomCornerSolved(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;
			if(DCTimer.stSel[5]==1)sc="\n"+Cross.cross(scr, DCTimer.spSel[1], DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==2)sc="\n"+Cross.xcross(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==3)sc="\n"+EOline.eoLine(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==4)sc="\n"+PetrusxRoux.roux(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==5)sc="\n"+PetrusxRoux.petrus(scr, DCTimer.spSel[3]);
			break;
		case 38:
			scr = new Search().solution(Tools.randomEdgeSolved(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 39:
			scr = new Search().solution(Tools.randomLastSlot(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 40:
			scr = new Search().solution(Tools.randomZBLastLayer(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 41:
			scr = new Search().solution(Tools.randomEdgeOfLastLayer(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 42:
			scr = new Search().solution(Tools.randomCornerOfLastLayer(), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 43:
			switch ((int)(Math.random()*4)) {
			case 0:
				scr=new Search().solution(Tools.randomState(new byte[]{0,1,2,3,4,5,6,7}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 21, 20000, 200, 2);
				break;
			case 1:
				scr=new Search().solution(Tools.randomState(new byte[]{3,2,6,7,0,1,5,4}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 21, 20000, 200, 2)+"x'";
				break;
			case 2:
				scr=new Search().solution(Tools.randomState(new byte[]{7,6,5,4,3,2,1,0}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 21, 20000, 200, 2)+"x2";
				break;
			default:
				scr=new Search().solution(Tools.randomState(new byte[]{4,5,1,0,7,6,2,3}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 21, 20000, 200, 2)+"x";
				break;
			}
			viewType=scr.startsWith("Error")?0:3;break;
		case 44:
			switch ((int)(Math.random()*4)) {
			case 0:
				scr=new Search().solution(Tools.randomState(new byte[]{-1,-1,-1,-1,4,5,6,7}, new byte[]{-1,-1,-1,-1,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 21, 20000, 200, 2);
				break;
			case 1:
				scr=new Search().solution(Tools.randomState(new byte[]{3,2,-1,-1,0,1,-1,-1}, new byte[]{2,1,-1,-1,1,2,-1,-1}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 21, 20000, 200, 2)+"x'";
				break;
			case 2:
				scr=new Search().solution(Tools.randomState(new byte[]{7,6,5,4,-1,-1,-1,-1}, new byte[]{0,0,0,0,-1,-1,-1,-1}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 21, 20000, 200, 2)+"x2";
				break;
			default:
				scr=new Search().solution(Tools.randomState(new byte[]{-1,-1,1,0,-1,-1,2,3}, new byte[]{-1,-1,2,1,-1,-1,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 21, 20000, 200, 2)+"x";
				break;
			}
			viewType=scr.startsWith("Error")?0:3;break;
		case 45:
			scr = new Search().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 46:
			scr = new Search().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_RANDOM), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 47:
			scr = new Search().solution(Tools.randomState(Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_SOLVED, Tools.STATE_SOLVED), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 48:
			scr = new Search().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_SOLVED), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 49:
			scr = new Search().solution(Tools.randomState(Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 50:
			scr = new Search().solution(Tools.randomState(Tools.STATE_SOLVED, Tools.STATE_RANDOM, Tools.STATE_SOLVED, Tools.STATE_RANDOM), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 51:
			scr = new Search().solution(Tools.randomEasyCross(scrLen), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;
			if(DCTimer.stSel[5]==1)sc="\n"+Cross.cross(scr, DCTimer.spSel[1], DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==2)sc="\n"+Cross.xcross(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==3)sc="\n"+EOline.eoLine(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==4)sc="\n"+PetrusxRoux.roux(scr, DCTimer.spSel[3]);
			else if(DCTimer.stSel[5]==5)sc="\n"+PetrusxRoux.petrus(scr, DCTimer.spSel[3]);
			break;
		case 52:
			scr = new Search().solution(Tools.randomState(Tools.STATE_SOLVED, new byte[]{-1,-1,-1,-1,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,5,6,7,8,9,10,11}, Tools.STATE_SOLVED), 21, 20000, 200, 2);
			viewType=scr.startsWith("Error")?0:3;break;
		case 64: //4阶
			scr=cube(4);viewType=4;break;
		case 65:
			turn2=new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=4;break;
		case 66:
			scr=OtherScr.yj4x4();viewType=4;break;
		case 67:
			end=new String[]{"Bw2 Rw'", "Bw2 U2 Rw U2 Rw U2 Rw U2 Rw"};
			scr=OtherScr.edgescramble("Rw Bw2", end, new String[]{"Uw"});viewType=4;
			break;
		case 68:
			scr=OtherScr.megascramble(new String[][]{{"U","u"},{"R","r"}}, csuff);
			viewType=4;break;
		case 96: //5阶
			scr=cube(5);viewType=5;break;
		case 97:
			turn2=new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=5;break;
		case 98:
			end=new String[]{"B' Bw' R' Rw'", "B' Bw' R' U2 Rw U2 Rw U2 Rw U2 Rw"};
			scr=OtherScr.edgescramble("Rw R Bw B", end, new String[]{"Uw","Dw"});viewType=5;
			break;
		case 128: //6阶
			scr=cube(6);viewType=6;break;
		case 129:
			turn2=new String[][]{{"U","D","u","d","3u"},{"R","L","r","l","3r"},{"F","B","f","b","3f"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=6;break;
		case 130:
			turn2=new String[][]{{"U","D","U²","D²","U³"},{"R","L","R²","L²","R³"},{"F","B","F²","B²","F³"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=6;break;
		case 131:
			end=new String[]{"3b' b' 3r' r'","3b' b' 3r' U2 r U2 r U2 r U2 r","3b' b' r' U2 3r U2 3r U2 3r U2 3r","3b' b' r2 U2 3R U2 3R U2 3R U2 3R"};
			scr=OtherScr.edgescramble("3r r 3b b", end, new String[]{"u","3u","d"});viewType=6;
			break;
		case 160: //7阶
			scr=cube(7);viewType=7;break;
		case 161:
			turn2=new String[][]{{"U","D","u","d","3u","3d"},{"R","L","r","l","3r","3l"},{"F","B","f","b","3f","3b"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=7;break;
		case 162:
			turn2=new String[][]{{"U","D","U²","D²","U³","D³"},{"R","L","R²","L²","R³","L³"},{"F","B","F²","B²","F³","B³"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=7;break;
		case 163:
			end=new String[]{"3b' b' 3r' r'","3b' b' 3r' U2 r U2 r U2 r U2 r","3b' b' r' U2 3r U2 3r U2 3r U2 3r","3b' b' r2 U2 3R U2 3R U2 3R U2 3R"};
			scr=OtherScr.edgescramble("3r r 3b b", end, new String[]{"u","3u","3d","d"});viewType=7;
			break;
		case 192: //五魔
			scr=Megaminx.scramblestring();viewType=18;break;
		case 193:
			scr=OtherScr.oldminxscramble();viewType=0;break;
		case 224: //金字塔
			scr=Pyraminx.scramble();viewType=17;break;
		case 225:
			turn2=new String[][]{{"U"},{"L"},{"R"},{"B"}};
			suff=new String[]{"","'"};
			String[][] ss={{"","b ","b' "},{"","l ","l' "},{"","u ","u' "},{"","r ","r' "}};
			int cnt=0;
			int[] rnd=new int[4];
			for(int i=0;i<4;i++) {
				rnd[i]=(int) (Math.random()*3);
				if(rnd[i]>0) cnt++;
				if(cnt>=scrLen)break;
			}
			scrLen -= cnt;
			scr=ss[0][rnd[0]]+ss[1][rnd[1]]+ss[2][rnd[2]]+ss[3][rnd[3]]
					+OtherScr.megascramble(turn2, suff);
			scrLen += cnt;
			viewType=17;
			break;
		case 256:  //SQ1
			scr=SQ1.scramblestring();
			if(DCTimer.stSel[12]>0) sc=" " + (DCTimer.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType=1; break;
		case 257:
			scr=OtherScr.sq1_scramble(0);
			if(DCTimer.stSel[12]>0) sc=" " + (DCTimer.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType=1; break;
		case 258:
			scr = new SqSearch().solution(FullCube.randomCube(new Random().nextInt(3678)));
			if(DCTimer.stSel[12]>0) sc=" " + (DCTimer.stSel[12]==1 ? Sq1Shape.solveTrn(scr) : Sq1Shape.solveTws(scr));
			viewType=1; break;
		case 259:
			scr = new SqSearch().solution(FullCube.randomCube(1037));
			viewType=1; break;
		case 288:	//魔表
			scr=Clock.scramble();viewType=12;break;
		case 289:
			scr=Clock.scrambleOld(false);viewType=12;break;
		case 290:
			scr=Clock.scrambleOld(true);viewType=12; break;
		case 291:
			scr=Clock.scrambleEpo(); viewType=12;break;
		case 320:	//Skewb
			scr=Skewb.solve(new Random());viewType=16;break;
		case 321:
			turn2=new String[][]{{"R"},{"U"},{"L"},{"B"}};
			suff=new String[]{"","'"};
			scr=OtherScr.megascramble(turn2, suff);
			viewType=16;break;
		case 352:	//MxNxL
			turn2=new String[][]{{"R","L"},{"U","D"}};
			scr=OtherScr.megascramble(turn2, new String[]{"2"});viewType=13;break;
		case 353:
			turn2=new String[][]{{"R","L"},{"U","D"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=0;break;
		case 354:
			Floppy.init();
			scr=Floppy.solve(new Random());viewType=13;break;
		case 355:
			turns=new String[][][]{{{"R2","L2","R2 L2"}},{{"U","U'","U2"}},{{"F2","B2","F2 B2"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=14;break;
		case 356:
			scr=Domino.solve(new Random());viewType=14;break;
		case 357:
			scr=Tower.solve(new Random());viewType=15;break;
		case 358:
			scr=RTower.solve();viewType=0; break;
		case 359:
			turns=new String[][][]{{{"U","U'","U2", "u", "u'", "u2", "U u", "U u'", "U u2", "U' u", "U' u'", "U' u2", "U2 u", "U2 u'", "U2 u2"}},{{"R2","L2","M2"}},{{"F2","B2","S2"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=0;break;
		case 360:
			turns=new String[][][]{{{"U","U'","U2"}, {"D", "D'", "D2"}},{{"R2","R2"},{"L2","L2"}},{{"F2","F2"},{"B2","B2"}}};
			scr=OtherScr.megascramble(turns, suff0)+"/ "+Cube.scramblestring(3, 25);viewType=0;
			break;
		case 361:
			turns=new String[][][]{{{"U","U'","U2","u","u'","u2","U u","U u'","U u2","U' u","U' u'","U' u2","U2 u","U2 u'","U2 u2","3u","3u'","3u2","U 3u","U' 3u","U2 3u","u 3u","u' 3u","u2 3u","U u 3u","U u' 3u","U u2 3u","U' u 3u","U' u' 3u","U' u2 3u","U2 u 3u","U2 u' 3u","U2 u2 3u","U 3u'","U' 3u'","U2 3u'","u 3u'","u' 3u'","u2 3u'","U u 3u'","U u' 3u'","U u2 3u'","U' u 3u'","U' u' 3u'","U' u2 3u'","U2 u 3u'","U2 u' 3u'","U2 u2 3u'","U 3u2","U' 3u2","U2 3u2","u 3u2","u' 3u2","u2 3u2","U u 3u2","U u' 3u2","U u2 3u2","U' u 3u2","U' u' 3u2","U' u2 3u2","U2 u 3u2","U2 u' 3u2","U2 u2 3u2"}},{{"R2","L2","M2"}},{{"F2","B2","S2"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=0;break;
		case 362:
			turns=new String[][][]{{{"U","U'","U2","u","u'","u2","U u","U u'","U u2","U' u","U' u'","U' u2","U2 u","U2 u'","U2 u2"}, {"D","D'","D2","d","d'","d2","D d","D d'","D d2","D' d","D' d'","D' d2","D2 d","D2 d'","D2 d2"}},{{"R2","R2"},{"L2","L2"}},{{"F2","F2"},{"B2","B2"}}};
			scr=OtherScr.megascramble(turns, suff0)+"/ "+Cube.scramblestring(3, 25);viewType=0;
			break;
		case 363:
			scr=cube(8);viewType=8;break;
		case 364:
			scr=cube(9);viewType=9;break;
		case 365:
			scr=cube(10);viewType=10;break;
		case 366:
			scr=cube(11);viewType=11;break;
		case 384:	//Cmetrick
			turns=new String[][][]{{{"U<","U>","U2"},{"E<","E>","E2"},{"D<","D>","D2"}},{{"R^","Rv","R2"},{"M^","Mv","M2"},{"L^","Lv","L2"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=0;break;
		case 385:
			turns=new String[][][]{{{"U<","U>","U2"},{"D<","D>","D2"}},{{"R^","Rv","R2"},{"L^","Lv","L2"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=0;break;
		case 416:	//齿轮
			scr=Gear.solve(new Random());viewType=0;break;
		case 417:
			turn2=new String[][]{{"U"},{"R"},{"F"}};
			suff=new String[]{"","2","3","4","5","6","'","2'","3'","4'","5'"};
			scr=OtherScr.megascramble(turn2, suff);
			viewType=0;break;
		case 448:	//Siamese Cube
			turn2=new String[][]{{"U","u"},{"R","r"}};
			scr=OtherScr.megascramble(turn2, csuff)+"z2 "+OtherScr.megascramble(turn2, csuff);viewType=0;
			break;
		case 449:
			turn2=new String[][]{{"R","r"},{"U"}};
			scr=OtherScr.megascramble(turn2, csuff)+"z2 "+OtherScr.megascramble(turn2, csuff);viewType=0;
			break;
		case 450:
			turn2=new String[][]{{"U"},{"R"},{"F"}};
			scr=OtherScr.megascramble(turn2, csuff)+"z2 y "+OtherScr.megascramble(turn2, csuff);viewType=0;
			break;
		case 480:	//15puzzles
			scr=OtherScr.do15puzzle(false);viewType=0;break;
		case 481:
			scr=OtherScr.do15puzzle(true);viewType=0;break;
		case 512:	//Other
			scr=LatchCube.scramble();viewType=0;break;
		case 513:
			scr=OtherScr.helicubescramble();viewType=0;break;
		case 514:
			int i=0;
			StringBuffer sb=new StringBuffer();
			while (i<scrLen) {
				int rndu = (int)(Math.random()*12)-5;
				int rndd = (int)(Math.random()*12)-5;
				if (rndu != 0 || rndd != 0) {
					i++;
					sb.append( "(" + rndu + "," + rndd + ") / ");
				}
			}
			scr=sb.toString();viewType=0;break;
		case 515:
			scr=OtherScr.ssq1t_scramble();viewType=0;break;
		case 516:
			turns=new String[][][]{{{"A"}},{{"B"}},{{"C"}},{{"U","U'","U2'","U2","U3"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=0;break;
		case 517:
			turn2=new String[][]{{"U","D"},{"F","B"},{"L","BR"},{"R","BL"}};
			suff=new String[]{"","'"};
			scr=OtherScr.megascramble(turn2, suff);viewType=0;break;
		case 544:	//3x3x3 subsets
			//			turn2=new String[][]{{"U"},{"R"}};
			//			scr=OtherScr.megascramble(turn2, csuff);
			scr=CubeRU.solve(new Random()); viewType=3;break;
		case 545:
			//			turn2=new String[][]{{"U"},{"L"}};
			//			scr=OtherScr.megascramble(turn2, csuff);
			scr=CubeRU.solve(new Random()).replace('R', 'L');
			viewType=3;break;
		case 546:
			//turn2=new String[][]{{"U"},{"M"}};
			//scr=OtherScr.megascramble(turn2, csuff);
			scr=RouxMU.solve(new Random()); viewType=3;break;
		case 547:
			turn2=new String[][]{{"U"},{"R"},{"F"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=3;break;
		case 548:
			turn2=new String[][]{{"R","L"},{"U"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=3;break;
		case 549:
			turn2=new String[][]{{"R","r"},{"U"}};
			scr=OtherScr.megascramble(turn2, csuff);viewType=3;break;
		case 550:
			//			turn2=new String[][]{{"U","D"},{"R","L"},{"F","B"}};
			//			suff=new String[]{"2"};
			//			scr=OtherScr.megascramble(turn2, suff, 25);
			scr=HalfTurn.solve(new Random());
			viewType=3;break;
		case 551:	//LSLL
			turns=new String[][][]{{{"R U R'","R U2 R'","R U' R'"}},{{"F' U F","F' U2 F","F' U' F"}},{{"U","U2","U'"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=3;break;
		case 576:	//Bandaged Cube
			scr=OtherScr.bicube();viewType=0;break;
		case 577:
			scr=OtherScr.sq1_scramble(2);viewType=1;break;
		case 608:	//五魔子集
			turn2=new String[][]{{"U"},{"R"}};
			scr=OtherScr.megascramble(turn2, new String[]{"", "2", "'", "2'"});viewType=0;break;
		case 609:
			turns=new String[][][]{{{"R U R'","R U2 R'","R U' R'","R U2' R'"}},{{"F' U F","F' U2 F","F' U' F","F' U2' F"}},{{"U","U2","U'","U2'"}}};
			scr=OtherScr.megascramble(turns, suff0);viewType=0;break;
		case 640:	//连拧
			scr="2) "+Cube222.randomState()+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 200, 0)
			+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40);
			viewType=0;break;
		case 641:
			scr="2) "+Cube222.randomState()+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 200, 0)
			+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40)
			+"\n5) "+OtherScr.megascramble(new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}}, csuff, 60);
			viewType=0;break;
		case 642:
			scr="2) "+Cube222.randomState()+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 200, 0)
			+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40)
			+"\n5) "+OtherScr.megascramble(new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}}, csuff, 60)
			+"\n6) "+OtherScr.megascramble(new String[][]{{"U","D","u","d","3u"},{"R","L","r","l","3r"},{"F","B","f","b","3f"}}, csuff, 80);
			viewType=0;break;
		case 643:
			scr="2) "+Cube222.randomState()+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 200, 0)
			+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40)
			+"\n5) "+OtherScr.megascramble(new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}}, csuff, 60)
			+"\n6) "+OtherScr.megascramble(new String[][]{{"U","D","u","d","3u"},{"R","L","r","l","3r"},{"F","B","f","b","3f"}}, csuff, 80)
			+"\n7) "+OtherScr.megascramble(new String[][]{{"U","D","u","d","3u","3d"},{"R","L","r","l","3r","3l"},{"F","B","f","b","3f","3b"}}, csuff, 100);
			viewType=0;break;
		case 644:
			StringBuffer scrb=new StringBuffer();
			for(int j=0; j<scrLen; j++) {
				scrb.append(j+1+") "+new Search().solution(Tools.randomCube(), 21, 20000, 0, 0));
				if(j<scrLen-1)scrb.append("\n");
			}
			scr=scrb.toString();viewType=0;break;
		default:
			scr = "";
		}
		return scr;
	}

	private static String cube(int n) {
		return Cube.scramblestring(n, scrLen);
	}

	public static void drawScr(int sel2, int width, Paint p, Canvas c) {
		int[] colors={DCTimer.share.getInt("csn1", Color.YELLOW), DCTimer.share.getInt("csn2", Color.BLUE), DCTimer.share.getInt("csn3", Color.RED),
				DCTimer.share.getInt("csn4", Color.WHITE), DCTimer.share.getInt("csn5", 0xff009900), DCTimer.share.getInt("csn6", 0xffff8026)};
		//2阶
		if(viewType==2) {
			Cube.parse(2);
			byte[] imst = OtherScr.imagestr(DCTimer.crntScr);
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
		//五魔
		else if(viewType==18) {
			float edgeFrac = (float) ((1+Math.sqrt(5))/4);
			float centerFrac = 0.5F;
			if(DCTimer.stSel[7]==0)colors=new int[] {Color.WHITE,Color.rgb(0, 0, 136),Color.rgb(0, 136, 0),Color.rgb(0, 255, 255),Color.rgb(136, 34, 34),Color.rgb(136, 170, 255),
					Color.RED,Color.BLUE,Color.rgb(255, 0, 255),Color.GREEN,Color.rgb(255, 136, 0),Color.YELLOW};
			else colors=new int[] {Color.WHITE,Color.RED,Color.rgb(0, 161, 0),Color.rgb(123, 0, 123),Color.YELLOW,Color.BLUE,
					Color.rgb(255, 255, 132),Color.rgb(66, 221, 255),Color.rgb(255, 127, 38),Color.GREEN,Color.rgb(255, 128, 255),Color.GRAY};
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
			int d=0;
			float d2x=(float) (majorR*(1-centerFrac)/2/Math.tan(Math.PI/5));
			byte[] img=Megaminx.image();
			p.setStyle(Paint.Style.FILL);
			for(int side=0;side<12;side++) {
				float a=trans[side][1]+trans[side][3]*c18(trans[side][4])*pentR;
				float b=trans[side][2]+trans[side][3]*s18(trans[side][4])*pentR;
				float[][] arys;
				for(int i=0;i<5;i++) {
					aryx=new float[]{0,d2x,0,-d2x};
					aryy=new float[]{-majorR,-majorR*(1+centerFrac)/2,-majorR*centerFrac,-majorR*(1+centerFrac)/2};
					arys=rotate(a, b, aryx, aryy, 72*i+trans[side][0]);
					drawPolygon(p,c,colors[img[d++]],arys[0],arys[1],true);
				}
				for(int i=0;i<5;i++) {
					aryx=new float[]{c18(-1)*majorR-d2x,d2x,0,s18(4)*centerFrac*majorR};
					aryy=new float[]{s18(-1)*majorR - majorR + majorR*(1+centerFrac)/2,-majorR*(1+centerFrac)/2,-majorR*centerFrac,-c18(4)*centerFrac*majorR};
					arys=rotate(a, b, aryx, aryy, 72*i+trans[side][0]);
					drawPolygon(p,c,colors[img[d++]],arys[0],arys[1],true);
				}
				aryx=new float[]{s18(0)*centerFrac*majorR,s18(4)*centerFrac*majorR,s18(8)*centerFrac*majorR,s18(12)*centerFrac*majorR,s18(16)*centerFrac*majorR};
				aryy=new float[]{-c18(0)*centerFrac*majorR,-c18(4)*centerFrac*majorR,-c18(8)*centerFrac*majorR,-c18(12)*centerFrac*majorR,-c18(16)*centerFrac*majorR};
				arys=rotate(a, b, aryx, aryy, trans[side][0]);
				drawPolygon(p,c,colors[img[d++]],arys[0],arys[1],true);
			}
			p.setTextAlign(Align.CENTER);
			p.setTextSize((float) (width * 0.0593));
			c.drawText("U", (float)(width*0.262), (float)(width*0.367), p);
			c.drawText("F", (float)(width*0.262), (float)(width*0.535), p);
		}
		//金字塔
		else if(viewType==17) {	//TODO
			byte[] imst;
			if(!DCTimer.isInScr && sel2==0) imst = Pyraminx.imageString();
			else imst = Pyraminx.imageString(DCTimer.crntScr);
			int b=(width*3/4-15)/6, a=(int)(b*2/Math.sqrt(3)), d=(int) ((width-a*6-21)/2);
			colors = new int[]{DCTimer.share.getInt("csp1", Color.RED), DCTimer.share.getInt("csp2", 0xff009900),
					DCTimer.share.getInt("csp3", Color.BLUE), DCTimer.share.getInt("csp4", Color.YELLOW)};
			float[] arx,ary;
			byte[] layout = {
					1,2,1,2,1,0,2,0,1,2,1,2,1,
					0,1,2,1,0,2,1,2,0,1,2,1,0,
					0,0,1,0,2,1,2,1,2,0,1,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,1,2,1,2,1,0,0,0,0,
					0,0,0,0,0,1,2,1,0,0,0,0,0,
					0,0,0,0,0,0,1,0,0,0,0,0,0};
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
						if(y<3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{y*b+3+y,y*b+3+y,(y+1)*b+3+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
						else if(y>3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{(y-1)*b+9+y,(y-1)*b+9+y,y*b+9+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
					}
					else if(layout[y*13+x] == 2) {
						if(y<3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{(y+1)*b+3+y,(y+1)*b+3+y,y*b+3+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
						else if(y>3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{y*b+9+y,y*b+9+y,(y-1)*b+9+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
					}
				}
		}
		//SQ1
		else if(viewType==1) {
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
			temp = new byte[top_side.length+bot_side.length];
			for(int i=0; i<top_side.length; i++) temp[i] = top_side[i];
			for(int i=top_side.length; i<top_side.length+bot_side.length; i++) temp[i] = bot_side[i-top_side.length];
			byte[] eido = temp;
			StringBuffer a2 = new StringBuffer(), b2 = new StringBuffer(), c2 = new StringBuffer();
			for(int j=0; j<16; j++) {
				a2.append(ty[eido[j]]);
				b2.append(tb[eido[j]]);
				c2.append(col[eido[j]]);
			}
			String stickers = b2.append(c2).toString();
			String a = a2.toString();
			float z=1.366F; // sqrt(2)/sqrt(1^2+tan(15degrees)^2)
			float[] arrx, arry;
			float sidewid = 10.98F;
			int cx = 55, cy = 50;
			float rd = (cx - 16) / z;
			float w = (sidewid + rd) / rd;	// ratio btw total piece width and rd
			float[] ag = new float[24];
			float[] ag2 = new float[24];
			int foo;
			for(foo=0; foo<24; foo++) {
				ag[foo] = (float) ((17F-foo*2)*Math.PI/12);
				a = a.concat("xxxxxxxxxxxxxxxx");
			}
			for(foo=0; foo<24; foo++) {
				ag2[foo] = (float) ((19F-foo*2)*Math.PI/12);
				a = a.concat("xxxxxxxxxxxxxxxx");
			}
			float h = sin1(1,ag,rd)*w*z - sin1(1,ag,rd)*z;
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
				drawPolygon(p,c,Color.BLACK,width,arrx,arry);
				arrx=new float[]{cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(9,ag,rd)*w*z};
				arry=new float[]{cy+sin1(9,ag,rd)*w*z-h, cy-sin1(11,ag,rd)*w*z-h, cy-sin1(11,ag,rd)*w*z, cy+sin1(9,ag,rd)*w*z};
				drawPolygon(p,c,colors[4],width,arrx,arry);
				cy += 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],width,arrx,arry);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(11,ag,rd)*w*z + h, cy-sin1(11,ag,rd)*w*z};
				drawPolygon(p,c,colors[2],width,arrx,arry);
				cy -= 10;
			}
			int sc = 0;
			for(foo=0; sc<12; foo++) {
				if (a.length()<=foo) sc = 12;
				if (a.charAt(foo)=='x') sc++;
				if (a.charAt(foo)=='c') {
					arrx=new float[]{cx, cx+cos1(sc,ag,rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+2, ag, rd)};
					arry=new float[]{cy, cy-sin1(sc,ag,rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+2, ag, rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],width,arrx,arry);
					arrx=new float[]{cx+cos1(sc, ag, rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+1, ag, rd)*w*z, cx+cos1(sc, ag, rd)*w};
					arry=new float[]{cy-sin1(sc, ag, rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+1, ag, rd)*w*z, cy-sin1(sc, ag, rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(16+sc)-48],width,arrx,arry);
					arrx=new float[]{cx+cos1(sc+2, ag, rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+1, ag, rd)*w*z, cx+cos1(sc+2, ag, rd)*w};
					arry=new float[]{cy-sin1(sc+2, ag, rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+1, ag, rd)*w*z, cy-sin1(sc+2, ag, rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(17+sc)-48],width,arrx,arry);
					sc+=2;
				}
				if (a.charAt(foo)=='e') {
					arrx=new float[]{cx, cx+cos1(sc,ag,rd), cx+cos1(sc+1,ag,rd)};
					arry=new float[]{cy, cy-sin1(sc,ag,rd), cy-sin1(sc+1,ag,rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],width,arrx,arry);
					arrx=new float[]{cx+cos1(sc,ag,rd), cx+cos1(sc+1,ag,rd), cx+cos1(sc+1,ag,rd)*w, cx+cos1(sc,ag,rd)*w};
					arry=new float[]{cy-sin1(sc,ag,rd), cy-sin1(sc+1,ag,rd), cy-sin1(sc+1,ag,rd)*w, cy-sin1(sc,ag,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(16+sc)-48],width,arrx,arry);
					sc +=1;
				}
			}
			cx *= 3;
			cy += 10;
			if(mis) {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(7,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(4,ag,rd)*w*z, cy+sin1(7,ag,rd)*w*z, cy+sin1(10,ag,rd)*w*z};
				drawPolygon(p,c,Color.BLACK,width,arrx,arry);
				cy -= 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],width,arrx,arry);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(10,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],width,arrx,arry);
				cy += 10;
			}
			else {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(6,ag,rd)*w, cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(0,ag,rd)*w};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(4,ag,rd)*w*z, cy+sin1(6,ag,rd)*w, cy-sin1(9,ag,rd)*w*z, cy+sin1(11,ag,rd)*w*z, cy+sin1(0,ag,rd)*w};
				drawPolygon(p,c,Color.BLACK,width,arrx,arry);
				arrx=new float[]{cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(9,ag,rd)*w*z};
				arry=new float[]{cy-sin1(9,ag,rd)*w*z-10, cy+sin1(11,ag,rd)*w*z-10, cy+sin1(11,ag,rd)*w*z, cy-sin1(9,ag,rd)*w*z};
				drawPolygon(p,c,colors[4],width,arrx,arry);
				cy -= 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],width,arrx,arry);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(11,ag,rd)*w*z+10, cy+sin1(11,ag,rd)*w*z};
				drawPolygon(p,c,colors[2],width,arrx,arry);
				cy += 10;
			}
			sc = 0;
			for(sc=0; sc<12; foo++) {
				if (a.length()<=foo) sc = 12;
				if (a.charAt(foo)=='x') sc++;
				if (a.charAt(foo)=='c') {
					arrx=new float[]{cx, cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd)*z, cx+cos1(sc+2,ag2,rd)};
					arry=new float[]{cy, cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd)*z, cy-sin1(sc+2,ag2,rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],width,arrx,arry);
					arrx=new float[]{cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd)*z, cx+cos1(sc+1,ag2,rd)*w*z, cx+cos1(sc,ag2,rd)*w};
					arry=new float[]{cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd)*z, cy-sin1(sc+1,ag2,rd)*w*z, cy-sin1(sc,ag2,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(28+sc)-48],width,arrx,arry);
					arrx=new float[]{cx+cos1(sc+2,ag2,rd), cx+cos1(sc+1,ag2,rd)*z, cx+cos1(sc+1,ag2,rd)*w*z, cx+cos1(sc+2,ag2,rd)*w};
					arry=new float[]{cy-sin1(sc+2,ag2,rd), cy-sin1(sc+1,ag2,rd)*z, cy-sin1(sc+1,ag2,rd)*w*z, cy-sin1(sc+2,ag2,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(29+sc)-48],width,arrx,arry);
					sc +=2;
				}
				if (a.charAt(foo)=='e') {
					arrx=new float[]{cx, cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd)};
					arry=new float[]{cy, cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],width,arrx,arry);
					arrx=new float[]{cx+cos1(sc,ag2,rd), cx+cos1(sc+1,ag2,rd), cx+cos1(sc+1,ag2,rd)*w, cx+cos1(sc,ag2,rd)*w};
					arry=new float[]{cy-sin1(sc,ag2,rd), cy-sin1(sc+1,ag2,rd), cy-sin1(sc+1,ag2,rd)*w, cy-sin1(sc,ag2,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(28+sc)-48],width,arrx,arry);
					sc +=1;
				}
			}
		}
		//魔表
		else if(viewType==12) {
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
			drawPeg(p, c, width, cx - face_dist/2, cy - face_dist/2, 1-pegs[0]);
			drawPeg(p, c, width, cx + face_dist/2, cy - face_dist/2, 1-pegs[1]);
			drawPeg(p, c, width, cx - face_dist/2, cy + face_dist/2, 1-pegs[2]);
			drawPeg(p, c, width, cx + face_dist/2, cy + face_dist/2, 1-pegs[3]);
			cx = 165;
			p.setColor(0xff2a2a2a);
			drawSideBackground(p, c, width, cx, cy, 53, 29, 19);
			p.setColor(0xff88aaff);
			drawSideBackground(p, c, width, cx, cy, 52, 29, 18);
			for (int y = cy - face_dist; y <= cy + face_dist; y += face_dist)
				for (int x = cx - face_dist; x <= cx + face_dist; x += face_dist)
					drawClockFace(p, c, width, x, y, 0xff3366ff, posit[i++]);
			drawPeg(p, c, width, cx + face_dist/2, cy - face_dist/2, pegs[0]);
			drawPeg(p, c, width, cx - face_dist/2, cy - face_dist/2, pegs[1]);
			drawPeg(p, c, width, cx + face_dist/2, cy + face_dist/2, pegs[2]);
			drawPeg(p, c, width, cx - face_dist/2, cy + face_dist/2, pegs[3]);
		}
		//1x3x3
		else if(viewType==13) {
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
		else if(viewType==14) {
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
		else if(viewType==15) {
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
		//Skewb
		else if(viewType==16) {
			byte[] imst=Skewb.image(DCTimer.crntScr);
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
			int a=(width-19)/(viewType*4), i, j, d=0, b=viewType;
			byte[] imst;
			if(DCTimer.isInScr) {
				Cube.parse(viewType);
				imst = OtherScr.imagestr(DCTimer.crntScr);
			}
			else if(viewType == 3) {
				Cube.parse(3);
				imst = OtherScr.imagestr(DCTimer.crntScr);
			}
			else if(viewType > 7) imst = Cube.imagestring();
			else if(sel2 == 0) imst = Cube.imagestring();
			else {
				Cube.parse(viewType);
				imst = OtherScr.imagestr(DCTimer.crntScr);
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
					if(j>=b*3)     c.drawRect(stx+19+j*a, sty+7+(i+b)*a, stx+18+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b*2)c.drawRect(stx+13+j*a, sty+7+(i+b)*a, stx+12+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b)  c.drawRect(stx+ 7+j*a, sty+7+(i+b)*a, stx+ 6+(j+1)*a, sty+6+(i+1+b)*a, p);
					else           c.drawRect(stx+ 1+j*a, sty+7+(i+b)*a, stx   +(j+1)*a, sty+6+(i+1+b)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>=b*3)     c.drawRect(stx+19+j*a, sty+7+(i+b)*a, stx+18+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b*2)c.drawRect(stx+13+j*a, sty+7+(i+b)*a, stx+12+(j+1)*a, sty+6+(i+1+b)*a, p);
					else if(j>=b)  c.drawRect(stx+ 7+j*a, sty+7+(i+b)*a, stx+ 6+(j+1)*a, sty+6+(i+1+b)*a, p);
					else           c.drawRect(stx+ 1+j*a, sty+7+(i+b)*a, stx   +(j+1)*a, sty+6+(i+1+b)*a, p);
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
	protected static void drawPolygon(Paint p, Canvas c, int cl, float[] arx, float[] ary, boolean stoke) {
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

	public static String contime(int hour, int min, int sec, int msec) {
		StringBuilder time=new StringBuilder();
		if(hour==0) {
			if(min==0) time.append(""+sec);
			else {
				if(sec<10)time.append(""+min+":0"+sec);
				else time.append(""+min+":"+sec);
			}
		}
		else {
			time.append(""+hour);
			if(min<10)time.append(":0"+min);
			else time.append(":"+min);
			if(sec<10)time.append(":0"+sec);
			else time.append(":"+sec);
		}
		if(DCTimer.stSel[2]==1) {
			if(msec<10)time.append(".00"+msec);
			else if(msec<100)time.append(".0"+msec);
			else time.append("."+msec);}
		else {
			if(msec<10)time.append(".0"+msec);
			else time.append("."+msec);
		}
		return time.toString();
	}
	public static String distime(int i) {
		boolean m = i<0;
		if(m)i = -i;
		//if(i==0)return "DNF";
		//if(DCTimer.stSel[2]==0)i+=5;
		int msec=i%1000;
		if(DCTimer.stSel[2]==0)msec/=10;
		int sec=DCTimer.clkform?(i/1000)%60:i/1000;
		int min=DCTimer.clkform?(i/60000)%60:0;
		int hour=DCTimer.clkform?i/3600000:0;
		return (m?"-":"")+contime(hour, min, sec, msec);
	}
	private static String distime2(int i) {
		boolean m = i < 0;
		i = Math.abs(i) + 5;
		int ms=(i%1000)/100;
		int s=DCTimer.clkform?(i/1000)%60:i/1000;
		int mi=DCTimer.clkform?(i/60000)%60:0;
		int h=DCTimer.clkform?(i/3600000):0;
		return (m?"-":"")+(h>0?h+":":"")+(h>0?(mi<10?"0"+mi+":":mi+":"):(mi>0?mi+":":""))+(((h>0 || mi>0) && s<10)?"0":"")+s+"."+ms;
	}
	public static String distime(int idx, boolean b) {
		if(idx<0)return "N/A";
		if(idx>=DCTimer.resl)return "";
		int i=DCTimer.rest[idx];
		if(DCTimer.resp[idx]==2) {
			if(b)return "DNF ("+distime(i)+")";
			else return "DNF";
		}
		else if(DCTimer.resp[idx]==1)
			return distime(i+2000)+"+";
		else return distime(i);
	}

	public static String avg(int n, int i, int l) {
		if(i<n-1) {bidx[l]=-1;return "N/A";}
		int nDnf=0, cavg;
		int trim = (int) Math.ceil(n/20.0);
		double sum = 0;
		for(int j=i-n+1; j<=i; j++)
			if(DCTimer.resp[j]==2) {
				nDnf++;
				if(nDnf>trim) {
					cavg=Integer.MAX_VALUE;
					if(i<n)bavg[l]=Integer.MAX_VALUE;
					return "DNF";
				}
			}
		if(n<20) {
			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;
			for (int j=i-n+1;j<=i;j++)
				if(DCTimer.resp[j]!=2) {
					int time = DCTimer.rest[j]+DCTimer.resp[j]*2000;
					if(time>max) max = time;
					if(time<min) min = time;
					if(DCTimer.stSel[2]==1) sum+=time;
					else sum+=time/10;
				}
			if(nDnf!=0) max = 0;
			if(DCTimer.stSel[2]==1)sum-=min+max;
			else sum-=min/10+max/10;
			cavg=(int) (sum/(n-2)+0.5);
		}
		else {
			int[] data=new int[n-nDnf];
			int len=0;
			for(int j=i-n+1;j<=i;j++)
				if(DCTimer.resp[j]!=2) data[len++]=DCTimer.rest[j]+DCTimer.resp[j]*2000;
			quickSort(data, 0, n-nDnf-1);
			for(int j=trim;j<n-trim;j++) {
				if(DCTimer.stSel[2]==1)sum+=data[j];
				else sum+=data[j]/10;
			}
			cavg=(int) (sum/(n-2*trim)+0.5);
		}
		if(DCTimer.stSel[2]==0)cavg*=10;
		if(i==n-1) {bavg[l]=cavg;bidx[l]=i;}
		if(cavg<=bavg[l]) {bavg[l]=cavg;bidx[l]=i;}
		return distime(cavg);
	}
	private static void quickSort(int[] a, int lo, int hi) {
		if(lo >= hi) return;
		int pivot = a[lo], i = lo, j = hi;
		while(i < j) {
			while(i<j && a[j]>=pivot) j--;
			a[i] = a[j];
			while(i<j && a[i]<=pivot) i++;
			a[j] = a[i];
		}
		a[i] = pivot;
		quickSort(a, lo, i-1);
		quickSort(a, i+1, hi);
	}
	public static String mean(int n, int i, int l) {
		if(i<n-1) {bidx[l]=-1;return "N/A";}
		int cavg;
		double sum=0;
		for(int j=i-n+1;j<=i;j++) {
			if(DCTimer.resp[j]==2) {
				cavg=Integer.MAX_VALUE;
				if(i==n-1)bavg[l]=Integer.MAX_VALUE;
				return "DNF";
			}
			else {
				int time = DCTimer.rest[j]+DCTimer.resp[j]*2000;
				if(DCTimer.stSel[2]==1)sum += time;
				else sum+=time/10;
			}
		}
		cavg=(int) (sum/n+0.5);
		if(DCTimer.stSel[2]==0)cavg*=10;
		if(i==n-1) {bavg[l]=cavg;bidx[l]=i;}
		if(cavg<=bavg[l]) {bavg[l]=cavg;bidx[l]=i;}
		return distime(cavg);
	}
	public static String sesMean() {
		double sum=0,sum2=0;
		maxIdx=-1; minIdx=-1; sesMean=-1;
		int n=DCTimer.resl;
		if(n==0)return "0/0): N/A (N/A)";
		for(int i=0;i<DCTimer.resl;i++) {
			if(DCTimer.resp[i]==2)n--;
			else {
				int time = DCTimer.rest[i]+DCTimer.resp[i]*2000;
				if(maxIdx==-1)maxIdx=i;
				else if(time>DCTimer.rest[maxIdx]+DCTimer.resp[maxIdx]*2000)maxIdx=i;
				if(minIdx==-1)minIdx=i;
				else if(time<=DCTimer.rest[minIdx]+DCTimer.resp[minIdx]*2000)minIdx=i;
				if(DCTimer.stSel[2]==1)sum+=time;
				else sum+=time/10;
				if(DCTimer.stSel[2]==1)sum2+=Math.pow(time, 2);
				else sum2+=Math.pow(time/10, 2);
			}
		}
		if(n==0)return "0/"+DCTimer.resl+"): N/A (N/A)";
		sesMean=(int)(sum/n+0.5);
		if(DCTimer.stSel[2]==0)sesMean*=10;
		sesSD=(int)(Math.sqrt((sum2-sum*sum/n)/n)+(DCTimer.stSel[2]==1?0:0.5));
		return ""+n+"/"+DCTimer.resl+"): "+distime(sesMean)+" ("+standDev(sesSD)+")";
	}
	public static String sesAvg() {
		int n=DCTimer.resl;
		if(n<3)return "N/A";
		int[] data = new int[n];
		int count = 0;
		int trim=(int) Math.ceil(n/20.0);
		for(int i=0;i<DCTimer.resl;i++) {
			if(DCTimer.resp[i]==2) {
				n--;
				if(n<DCTimer.resl-trim)return "DNF";
			}
			else data[count++]=DCTimer.rest[i]+DCTimer.resp[i]*2000;
		}
		double sum = 0, sum2 = 0;
		heapsort(data, n-1);
		for(int j=trim;j<DCTimer.resl-trim;j++) {
			if(DCTimer.stSel[2]==1)sum+=data[j];
			else sum+=data[j]/10;
			if(DCTimer.stSel[2]==1)sum2+=Math.pow(data[j], 2);
			else sum2+=Math.pow(data[j]/10, 2);
		}
		int num = DCTimer.resl-2*trim;
		int savg=(int) (sum/num+0.5);
		if(DCTimer.stSel[2]==0)savg*=10;
		int ssd=(int)(Math.sqrt((sum2-sum*sum/num)/num)+(DCTimer.stSel[2]==1?0:0.5));
		return distime(savg)+" (σ = "+standDev(ssd)+")";
	}
	public static void adjust(int[] num, int s, int t) {
		int i = s;
		int x = num[s];
		for (int j = 2 * i; j <= t; j = 2 * j) {
			if (j < t && num[j] < num[j + 1])
				j = j + 1;// 找出较大者把较大者给num[i]
			if (x > num[j])
				break;
			num[i] = num[j];
			i = j;
		}
		num[i] = x;
	}
	public static void heapsort(int[] num, int n) {
		// 初始建堆从n/2开始向根调整
		int i;
		for (i = n / 2; i >= 1; i--) {
			adjust(num, i, n);//初始堆过程
		}
		for (i = n; i > 1; i--) {
			num[0] = num[i];// 将堆顶元素与第n,n-1,.....2个元素相交换
			num[i] = num[1];
			num[1] = num[0];// 从num[1]到num[i-1]调整成新堆
			adjust(num, 1, i - 1);
		}
	}
	
	public static String mulMean(int p) {
		double sum=0;
		int n=0;
		if(n==DCTimer.resl)return "-";
		for(int i=0;i<DCTimer.resl;i++) {
			if(DCTimer.mulp[p][i]!=0) {
				if(DCTimer.stSel[2]==1)sum+=(double)DCTimer.mulp[p][i];
				else sum+=DCTimer.mulp[p][i]/10;
				n++;
			}
		}
		if(n==0)return "-";
		int m=(int)(sum/n+0.5);
		if(DCTimer.stSel[2]==0)m*=10;
		return distime(m);
	}
	public static String standDev(int i) {
		if(i<0)return "N/A";
		if(DCTimer.stSel[2]==1)i=(i+5)/10;
		StringBuffer s=new StringBuffer(i/100+".");
		if(i%100<10)s.append("0");
		s.append(""+i%100);
		return s.toString();
	}

	public static float s18(int i) {
		return (float) Math.sin(Math.PI*i/10);
	}
	public static float c18(int i) {
		return (float) Math.cos(Math.PI*i/10);
	}
	public static float[][] rotate(float a, float b, float[] x, float[] y, int i) {
		float[][] ary=new float[2][x.length];
		for(int j=0;j<x.length;j++) {
			ary[0][j]=(float) (x[j]*Math.cos(Math.toRadians(i))-y[j]*Math.sin(Math.toRadians(i))+a);
			ary[1][j]=(float) (x[j]*Math.sin(Math.toRadians(i))+y[j]*Math.cos(Math.toRadians(i))+b);
		}
		return ary;
	}
	public static float cos1(int index, float[] ag, float rd) {
		return (float) (Math.cos(ag[index])*rd);
	}
	public static float sin1(int index, float[] ag, float rd) {
		return (float) (Math.sin(ag[index])*rd);
	}
	public static byte[] rd(byte[] arr) {
		byte[] out = new byte[arr.length];
		int j=0;
		for (int i=0; i<arr.length; i++)
			if(i==0 || arr[i]!=arr[i-1])
				out[j++] = arr[i];
		byte[] temp = new byte[j];
		for(int i=0; i<j; i++)temp[i]=out[i];
		return temp;
	}

	public static String convStr(String s) {
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
	public static int convTime(String s) {
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
	public static void drawHist(int width, Paint p, Canvas c) {
		int[] bins = new int[14];
		int start;
		int end;
		if(DCTimer.resl==0 || minIdx==-1 || maxIdx==-1) {
			start = 13000;
			end = 27000;
		} else {
			int max = DCTimer.rest[maxIdx]+DCTimer.resp[maxIdx]*2000;
			int min = DCTimer.rest[minIdx]+DCTimer.resp[minIdx]*2000;
			int divi = getDivision((max - min) / 14);
			int mean = (min & max) + ((min ^ max) >> 1);
			mean = ((mean + divi / 2) / divi) * divi;
			start = mean - divi * 7;
			end = mean + divi * 7;
		}
		for (int i = 0; i < bins.length; i++)
			bins[i] = 0;
		for (int i = 0; i < DCTimer.resl; i++) {
			if(DCTimer.resp[i]!=2) {
				int time=DCTimer.rest[i]+DCTimer.resp[i]*2000;
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
			c.drawText(distime2(value), wBase-5, y, p);
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

	public static void drawGraph(int width, Paint p, Canvas c) {
		int up, down, mean;
		int blk, divi;
		if(DCTimer.resl==0 || minIdx==-1 || minIdx==maxIdx) {
			up = 20000;
			down = 12000;
			mean = 16000;
			blk = 8;
			divi = 1000;
		} else {
			int max = DCTimer.rest[maxIdx]+DCTimer.resp[maxIdx]*2000;
			int min = DCTimer.rest[minIdx]+DCTimer.resp[minIdx]*2000;
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
			mean = sesMean;
			blk = (up - down) / divi;
		}
		System.out.println(up+" "+down+" "+divi+" "+blk);
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
			c.drawText(distime2(value), wBase-4, y, p);
		}
		int count = 0;
		for(int i=0; i<DCTimer.resl; i++) 
			if(DCTimer.resp[i] != 2) count++;
		float rsp = (float) ((double)(width - 8 - wBase) / (count-1));
		count = 0;
		float lastx = -1, lasty = -1;
		for(int i=0; i<DCTimer.resl; i++) {
			if(DCTimer.resp[i] != 2) {
				int time = DCTimer.rest[i] + DCTimer.resp[i] * 2000;
				float x = (float) (wBase + 4.0 + (count++) * rsp);
				y = (float) ((double)(up - time) / divi * wBar + wBase/9.);
				//c.drawRect(x-2, y-2, x+2, y-2, p);
				c.drawCircle(x, y, 3, p);
				if(lastx!=-1) c.drawLine(lastx, lasty, x, y, p);
				lastx = x; lasty = y;
			}
		}
	}
	public static int getSessionType(long sesType, int idx) {
		return (int) ((sesType >> (7*idx)) & 0x7f);
	}
}
