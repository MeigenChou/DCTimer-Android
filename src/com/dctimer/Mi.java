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
	public static int[] bavg={0, 0};
	public static int[] bidx={0, 0};
	public static int oravg = -1;
	public static int orsdv;
	public static int omin, omax;
	private static boolean ini=false;
	public static String sc;
	public static int viewType;
	public static byte[] img;
	private static boolean mis;
	
	public static String SetScr(int n) {
		String[][][] turns;
		String[][] turn2;
		String[] end,suff0={""},csuff={"","2","'"},suff;
		String scr="", cube;
		switch(n){
		case 0: //2阶
			scr=Cube222.solve(Cube222.randomState());viewType=2;
			if(DCTimer.spSel[2]>0)sc="\n"+Cube2layer.cube2layer(scr, DCTimer.spSel[2]);break;
		case 1:
			scr=Cube.scramblestring(2, 25);viewType=2;
			if(DCTimer.spSel[2]>0)sc="\n"+Cube2layer.cube2layer(scr, DCTimer.spSel[2]);break;
		case 2:
			scr=OtherScr.megascramble(new String[][][]{{{"U","D"}},{{"R","L"}},{{"F","B"}}}, csuff, 25);viewType=2;
			if(DCTimer.spSel[2]>0)sc="\n"+Cube2layer.cube2layer(scr, DCTimer.spSel[2]);break;
		case 3:
			scr=Cube222.solve(Cube222.randomCLL());viewType=2; break;
		case 4:
			scr=Cube222.solve(Cube222.randomEG1());viewType=2; break;
		case 5:
			scr=Cube222.solve(Cube222.randomEG2());viewType=2; break;
		case 16: //3阶
			scr=Cube.scramblestring(3, 25);viewType=3;
			if(DCTimer.spSel[1]==1)sc="\n"+Cross.cross(scr, DCTimer.spSel[9], DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==2)sc="\n"+Cross.xcross(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==3)sc="\n"+EOline.eoLine(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==4)sc="\n"+PetrusxRoux.roux(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==5)sc="\n"+PetrusxRoux.petrus(scr, DCTimer.spSel[10]);
			break;
		case 17:
			scr = new Search().solution(Tools.randomCube(), 21, 100000, 0, 0);
			viewType=scr.startsWith("Error")?0:3;
			if(DCTimer.spSel[1]==1)sc="\n"+Cross.cross(scr, DCTimer.spSel[9], DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==2)sc="\n"+Cross.xcross(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==3)sc="\n"+EOline.eoLine(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==4)sc="\n"+PetrusxRoux.roux(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==5)sc="\n"+PetrusxRoux.petrus(scr, DCTimer.spSel[10]);
			break;
		case 18:
			scr = new Search().solution(Tools.randomCrossSolved(), 21, 100000, 0,0);
			viewType=scr.startsWith("Error")?0:3;break;
		case 19:
			scr = new Search().solution(Tools.randomLastLayer(), 20, 100000, 0,0);
			viewType=scr.startsWith("Error")?0:3;break;
		case 20:
			cube=Tools.randomState(new byte[]{-1, -1, -1, -1, 4, 5, 6, 7}, Tools.STATE_SOLVED, new byte[]{-1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11}, Tools.STATE_SOLVED);
			scr = new Search().solution(cube, 20, 100000, 0,0);
			viewType=scr.startsWith("Error")?0:3;break;
		case 21:
			cube=Tools.randomCornerSolved();
			cube = new Search().solution(cube, 21, 100000, 0,0);
			scr=cube;viewType=scr.startsWith("Error")?0:3;
			if(DCTimer.spSel[1]==1)sc="\n"+Cross.cross(scr, DCTimer.spSel[9], DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==2)sc="\n"+Cross.xcross(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==3)sc="\n"+EOline.eoLine(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==4)sc="\n"+PetrusxRoux.roux(scr, DCTimer.spSel[10]);
			else if(DCTimer.spSel[1]==5)sc="\n"+PetrusxRoux.petrus(scr, DCTimer.spSel[10]);
			break;
		case 22:
			cube=Tools.randomEdgeSolved();
			cube = new Search().solution(cube, 21, 100000, 0,0);
			scr=cube;viewType=scr.startsWith("Error")?0:3;break;
		case 23:
			cube=Tools.randomLastSlot();
			cube = new Search().solution(cube, 21, 100000, 0,0);
			scr=cube;viewType=scr.startsWith("Error")?0:3;break;
		case 24:
			cube=Tools.randomZBLastLayer();
			cube = new Search().solution(cube, 20, 100000, 0,0);
			scr=cube;viewType=scr.startsWith("Error")?0:3;break;
		case 25:
			cube=Tools.randomEdgeOfLastLayer();
			cube = new Search().solution(cube, 21, 100000, 0,0);
			scr=cube;viewType=scr.startsWith("Error")?0:3;break;
		case 26:
			cube=Tools.randomCornerOfLastLayer();
			cube = new Search().solution(cube, 21, 100000, 0,0);
			scr=cube;viewType=scr.startsWith("Error")?0:3;break;
		case 27:
			switch ((int)(Math.random()*4)) {
			case 0:
				scr=new Search().solution(Tools.randomState(new byte[]{0,1,2,3,4,5,6,7}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 21, 100000, 0, 2);
				break;
			case 1:
				scr=new Search().solution(Tools.randomState(new byte[]{3,2,6,7,0,1,5,4}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 21, 100000, 0, 2)+"x'";
				break;
			case 2:
				scr=new Search().solution(Tools.randomState(new byte[]{7,6,5,4,3,2,1,0}, new byte[]{0,0,0,0,0,0,0,0}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 21, 100000, 0, 2)+"x2";
				break;
			default:
				scr=new Search().solution(Tools.randomState(new byte[]{4,5,1,0,7,6,2,3}, new byte[]{2,1,2,1,1,2,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 21, 100000, 0, 2)+"x";
				break;
			}
			viewType=scr.startsWith("Error")?0:3;break;
		case 28:
			switch ((int)(Math.random()*4)) {
			case 0:
				scr=new Search().solution(Tools.randomState(new byte[]{-1,-1,-1,-1,4,5,6,7}, new byte[]{-1,-1,-1,-1,0,0,0,0}, new byte[]{-1,-1,-1,-1,4,-1,6,-1,8,9,10,11}, new byte[]{-1,-1,-1,-1,0,-1,0,-1,0,0,0,0}), 21, 100000, 0, 2);
				break;
			case 1:
				scr=new Search().solution(Tools.randomState(new byte[]{3,2,-1,-1,0,1,-1,-1}, new byte[]{2,1,-1,-1,1,2,-1,-1}, new byte[]{11,-1,10,-1,8,-1,9,-1,0,2,-1,-1}, new byte[]{0,-1,0,-1,0,-1,0,-1,0,0,-1,-1}), 21, 100000, 0, 2)+"x'";
				break;
			case 2:
				scr=new Search().solution(Tools.randomState(new byte[]{7,6,5,4,-1,-1,-1,-1}, new byte[]{0,0,0,0,-1,-1,-1,-1}, new byte[]{4,-1,6,-1,-1,-1,-1,-1,11,10,9,8}, new byte[]{0,-1,0,-1,-1,-1,-1,-1,0,0,0,0}), 21, 100000, 0, 2)+"x2";
				break;
			default:
				scr=new Search().solution(Tools.randomState(new byte[]{-1,-1,1,0,-1,-1,2,3}, new byte[]{-1,-1,2,1,-1,-1,1,2}, new byte[]{8,-1,9,-1,11,-1,10,-1,-1,-1,2,0}, new byte[]{0,-1,0,-1,0,-1,0,-1,-1,-1,0,0}), 21, 100000, 0, 2)+"x";
				break;
			}
			viewType=scr.startsWith("Error")?0:3;break;
		case 32: //4阶
			scr=Cube.scramblestring(4, 40);viewType=4;break;
//		case 33:
//			if(new File("/mnt/sdcard/DCTimer/Center1").exists() && new File("/mnt/sdcard/DCTimer/Edge3").exists()){
//				try{
//					threephase.Search first = new threephase.Search();
//					scr=first.randomState();
//				} catch(Exception e){
//					e.printStackTrace();
//					scr="Error";
//				}
//			}
//			else {
//				InputStream is = null;
//				try{
//			        HttpClient httpclient = new DefaultHttpClient();
//			        HttpPost httppost = new HttpPost("http://yunqi.li:8080/scramble/.json?=444*1");
//			        HttpResponse response = httpclient.execute(httppost);
//			        HttpEntity entity = response.getEntity();
//			        is = entity.getContent();
//			    }catch(Exception e){
//			        scr="Error: Fail to establish http connection.";
//			    }
//				try{
//			        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//			        StringBuilder sb = new StringBuilder();
//			        String line = null;
//			        while ((line = reader.readLine()) != null) {
//			            sb.append(line + "\n");
//			        }
//			        scr=sb.substring(16, sb.length()-162);
//			        is.close();
//			    }catch(Exception e){
//			    	scr="Error: Fail to convert net stream.";
//			    }
//			}
//			viewType=scr.startsWith("Error")?0:4;break;
		case 33:
			turn2=new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}};
			scr=OtherScr.megascramble(turn2, csuff, 40);viewType=4;break;
		case 34:
			scr=OtherScr.yj4x4();viewType=4;break;
		case 35:
			end=new String[]{"Bw2 Rw'", "Bw2 U2 Rw U2 Rw U2 Rw U2 Rw"};
			scr=OtherScr.edgescramble("Rw Bw2", end, new String[]{"Uw"});viewType=4;
			break;
		case 36:
			scr=OtherScr.megascramble(new String[][]{{"U","u"},{"R","r"}}, csuff, 40);
			viewType=4;break;
		case 48: //5阶
			scr=Cube.scramblestring(5, 60);viewType=5;break;
		case 49:
			turn2=new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}};
			scr=OtherScr.megascramble(turn2, csuff, 60);viewType=5;break;
		case 50:
			end=new String[]{"B' Bw' R' Rw'", "B' Bw' R' U2 Rw U2 Rw U2 Rw U2 Rw"};
			scr=OtherScr.edgescramble("Rw R Bw B", end, new String[]{"Uw","Dw"});viewType=5;
			break;
		case 64: //6阶
			scr=Cube.scramblestring(6, 80);viewType=6;break;
		case 65:
			turn2=new String[][]{{"U","D","u","d","3u"},{"R","L","r","l","3r"},{"F","B","f","b","3f"}};
			scr=OtherScr.megascramble(turn2, csuff, 80);viewType=6;break;
		case 66:
			turn2=new String[][]{{"U","D","U²","D²","U³"},{"R","L","R²","L²","R³"},{"F","B","F²","B²","F³"}};
			scr=OtherScr.megascramble(turn2, csuff, 80);viewType=6;break;
		case 67:
			end=new String[]{"3b' b' 3r' r'","3b' b' 3r' U2 r U2 r U2 r U2 r","3b' b' r' U2 3r U2 3r U2 3r U2 3r","3b' b' r2 U2 3R U2 3R U2 3R U2 3R"};
			scr=OtherScr.edgescramble("3r r 3b b", end, new String[]{"u","3u","d"});viewType=6;
			break;
		case 80: //7阶
			scr=Cube.scramblestring(7, 100);viewType=7;break;
		case 81:
			turn2=new String[][]{{"U","D","u","d","3u","3d"},{"R","L","r","l","3r","3l"},{"F","B","f","b","3f","3b"}};
			scr=OtherScr.megascramble(turn2, csuff, 100);viewType=7;break;
		case 82:
			turn2=new String[][]{{"U","D","U²","D²","U³","D³"},{"R","L","R²","L²","R³","L³"},{"F","B","F²","B²","F³","B³"}};
			scr=OtherScr.megascramble(turn2, csuff, 100);viewType=7;break;
		case 83:
			end=new String[]{"3b' b' 3r' r'","3b' b' 3r' U2 r U2 r U2 r U2 r","3b' b' r' U2 3r U2 3r U2 3r U2 3r","3b' b' r2 U2 3R U2 3R U2 3R U2 3R"};
			scr=OtherScr.edgescramble("3r r 3b b", end, new String[]{"u","3u","3d","d"});viewType=7;
			break;
		case 96: //五魔
			scr=Megaminx.scramblestring();viewType=18;break;
		case 97:
			scr=OtherScr.oldminxscramble();viewType=0;break;
		case 112: //金字塔
			scr=Pyraminx.scramble();viewType=17;break;
		case 113:
			turn2=new String[][]{{"U"},{"L"},{"R"},{"B"}};
			suff=new String[]{"!","'"};
			String[][] ss={{"","b ","b' "},{"","l ","l' "},{"","u ","u' "},{"","r ","r' "}};
			scr=OtherScr.megascramble(turn2, suff, 25);
			int cnt=0;
			int[] rnd=new int[4];
			for(int i=0;i<4;i++){
				rnd[i]=(int) (Math.random()*3);
				if(rnd[i]>0) cnt++;
			}
			scr=scr.substring(0,scr.length()-3*cnt);
			scr=ss[0][rnd[0]]+ss[1][rnd[1]]+ss[2][rnd[2]]+ss[3][rnd[3]]+scr;
			scr=scr.replace("!","");
			viewType=17;
			break;
		case 128:  //SQ1
			scr=SQ1.scramblestring();
			if(DCTimer.sqshp) sc=" "+Sq1Shape.solve(scr);
			viewType=1;mis=(SQ1.count%2==0);break;
		case 129:
			scr=OtherScr.sq1_scramble(0);
			if(DCTimer.sqshp) sc=" "+Sq1Shape.solve(scr);
			viewType=0; break;
		case 130:
			if(!ini) {
				Shape.init();Square.init();
				new SqSearch().solution(new FullCube(""));
				ini=true;
			}
			FullCube c=FullCube.randomCube();
			scr=new SqSearch().solution(c);if(DCTimer.sqshp)sc=" "+Sq1Shape.solve(scr);viewType=1;
			img=c.imagestr();mis=(c.ml==0);break;
		case 144:  //魔表
			scr=Clock.scramble();viewType=12;break;
		case 145:
			StringBuffer sclk = new StringBuffer();
			for (int i=0;i<4;i++) sclk.append("(" + (int)((Math.random()*12)-5) + ", " + (int)((Math.random()*12)-5) + ") / ");
			for (int i=0;i<6;i++) sclk.append("(" + (int)((Math.random()*12)-5) + ") / ");
			for (int i=0;i<4;i++) sclk.append(OtherScr.rndEl(new String[]{"d","U"}));
			scr=sclk.toString();viewType=0;
			break;
		case 146:
			scr="UUUU "+OtherScr.c("u")+" / dUUU "+OtherScr.c("u")+" / dUdU "+OtherScr.c("u")+","+OtherScr.c("d")+" / UUdU "
			+OtherScr.c("u")+" / UUdd "+OtherScr.c("u")+","+OtherScr.c("d")+" / UUUd "+OtherScr.c("u")+" / UdUd "+OtherScr.c("u")
			+","+OtherScr.c("d")+" / UdUU "+OtherScr.c("u")+" / ddUU "+OtherScr.c("u")+","+OtherScr.c("d")+" / dddd "+OtherScr.c("d")
			+" / "+OtherScr.c();
			viewType=0;break;
		case 160:	//15puzzles
			scr=OtherScr.do15puzzle(false);viewType=0;break;
		case 161:
			scr=OtherScr.do15puzzle(true);viewType=0;break;
		case 176:	//MxNxL
			turn2=new String[][]{{"R","L"},{"U","D"}};
			scr=OtherScr.megascramble(turn2, new String[]{"2"}, 15);viewType=13;break;
		case 177:
			turn2=new String[][]{{"R","L"},{"U","D"}};
			scr=OtherScr.megascramble(turn2, new String[]{"","2","'"}, 25);viewType=0;break;
		case 178:
			Floppy.init();
			scr=Floppy.solve(new Random());viewType=13;break;
		case 179:
			turns=new String[][][]{{{"R2","L2","R2 L2"}},{{"U","U'","U2"}},{{"F2","B2","F2 B2"}}};
			scr=OtherScr.megascramble(turns, suff0, 25);viewType=14;break;
		case 180:
			scr=Domino.solve(new Random());viewType=14;break;
		case 181:
			scr=Tower.solve(new Random());viewType=15;break;
		case 182:
			scr=RTower.solve();viewType=0; break;
		case 183:
			turns=new String[][][]{{{"U","U'","U2", "u", "u'", "u2", "U u", "U u'", "U u2", "U' u", "U' u'", "U' u2", "U2 u", "U2 u'", "U2 u2"}},{{"R2","L2","M2"}},{{"F2","B2","S2"}}};
			scr=OtherScr.megascramble(turns, suff0, 40);viewType=0;break;
		case 184:
			turns=new String[][][]{{{"U","U'","U2"}, {"D", "D'", "D2"}},{{"R2","R2"},{"L2","L2"}},{{"F2","F2"},{"B2","B2"}}};
			scr=OtherScr.megascramble(turns, suff0, 25)+"/ "+Cube.scramblestring(3, 25);viewType=0;
			break;
		case 185:
			turns=new String[][][]{{{"U","U'","U2","u","u'","u2","U u","U u'","U u2","U' u","U' u'","U' u2","U2 u","U2 u'","U2 u2","3u","3u'","3u2","U 3u","U' 3u","U2 3u","u 3u","u' 3u","u2 3u","U u 3u","U u' 3u","U u2 3u","U' u 3u","U' u' 3u","U' u2 3u","U2 u 3u","U2 u' 3u","U2 u2 3u","U 3u'","U' 3u'","U2 3u'","u 3u'","u' 3u'","u2 3u'","U u 3u'","U u' 3u'","U u2 3u'","U' u 3u'","U' u' 3u'","U' u2 3u'","U2 u 3u'","U2 u' 3u'","U2 u2 3u'","U 3u2","U' 3u2","U2 3u2","u 3u2","u' 3u2","u2 3u2","U u 3u2","U u' 3u2","U u2 3u2","U' u 3u2","U' u' 3u2","U' u2 3u2","U2 u 3u2","U2 u' 3u2","U2 u2 3u2"}},{{"R2","L2","M2"}},{{"F2","B2","S2"}}};
			scr=OtherScr.megascramble(turns, suff0, 40);viewType=0;break;
		case 186:
			turns=new String[][][]{{{"U","U'","U2","u","u'","u2","U u","U u'","U u2","U' u","U' u'","U' u2","U2 u","U2 u'","U2 u2"}, {"D","D'","D2","d","d'","d2","D d","D d'","D d2","D' d","D' d'","D' d2","D2 d","D2 d'","D2 d2"}},{{"R2","R2"},{"L2","L2"}},{{"F2","F2"},{"B2","B2"}}};
			scr=OtherScr.megascramble(turns, suff0, 40)+"/ "+Cube.scramblestring(3, 25);viewType=0;
			break;
		case 187:
			scr=Cube.scramblestring(8, 120);viewType=8;break;
		case 188:
			scr=Cube.scramblestring(9, 140);viewType=9;break;
		case 189:
			scr=Cube.scramblestring(10, 140);viewType=10;break;
		case 190:
			scr=Cube.scramblestring(11, 140);viewType=11;break;
		case 192:	//Cmetrick
			turns=new String[][][]{{{"U<","U>","U2"},{"E<","E>","E2"},{"D<","D>","D2"}},{{"R^","Rv","R2"},{"M^","Mv","M2"},{"L^","Lv","L2"}}};
			scr=OtherScr.megascramble(turns, suff0, 25);viewType=0;break;
		case 193:
			turns=new String[][][]{{{"U<","U>","U2"},{"D<","D>","D2"}},{{"R^","Rv","R2"},{"L^","Lv","L2"}}};
			scr=OtherScr.megascramble(turns, suff0, 25);viewType=0;break;
		case 208:	//齿轮
			scr=Gear.solve(new Random());viewType=0;break;
		case 209:
			turn2=new String[][]{{"U"},{"R"},{"F"}};
			suff=new String[]{"","2","3","4","5","6","'","2'","3'","4'","5'"};
			scr=OtherScr.megascramble(turn2, suff, 10);
			viewType=0;break;
		case 224:	//Siamese Cube
			turn2=new String[][]{{"U","u"},{"R","r"}};
			scr=OtherScr.megascramble(turn2, csuff, 25)+"z2 "+OtherScr.megascramble(turn2, csuff, 25);viewType=0;
			break;
		case 225:
			turn2=new String[][]{{"R","r"},{"U"}};
			scr=OtherScr.megascramble(turn2, csuff, 25)+"z2 "+OtherScr.megascramble(turn2, csuff, 25);viewType=0;
			break;
		case 226:
			turn2=new String[][]{{"U"},{"R"},{"F"}};
			scr=OtherScr.megascramble(turn2, csuff, 25)+"z2 y "+OtherScr.megascramble(turn2, csuff, 25);viewType=0;
			break;
		case 240:	//Skewb
			scr=Skewb.solve(new Random());viewType=16;break;
		case 241:
			turn2=new String[][]{{"R"},{"L"},{"B"},{"D"}};
			suff=new String[]{"","'"};
			scr=OtherScr.megascramble(turn2, suff, 25);
			viewType=16;break;
		case 256:	//Other
			scr=LatchCube.scramble();viewType=0;break;
		case 257:
			scr=OtherScr.helicubescramble();viewType=0;break;
		case 258:
			int i=0;
			StringBuffer sb=new StringBuffer();
			while (i<20) {
				int rndu = (int)(Math.random()*12)-5;
				int rndd = (int)(Math.random()*12)-5;
				if (rndu != 0 || rndd != 0) {
					i++;
					sb.append( "(" + rndu + "," + rndd + ") / ");
				}
			}
			scr=sb.toString();viewType=0;break;
		case 259:
			scr=OtherScr.ssq1t_scramble();viewType=0;break;
		case 260:
			turns=new String[][][]{{{"A"}},{{"B"}},{{"C"}},{{"U","U'","U2'","U2","U3"}}};
			scr=OtherScr.megascramble(turns, suff0, 25);viewType=0;break;
		case 261:
			turn2=new String[][]{{"U","D"},{"F","B"},{"L","BR"},{"R","BL"}};
			suff=new String[]{"","'"};
			scr=OtherScr.megascramble(turn2, suff, 25);viewType=0;break;
		case 272:	//3x3x3 subsets
//			turn2=new String[][]{{"U"},{"R"}};
//			scr=OtherScr.megascramble(turn2, csuff, 25);
			scr=CubeRU.solve(new Random());
			viewType=3;break;
		case 273:
//			turn2=new String[][]{{"U"},{"L"}};
//			scr=OtherScr.megascramble(turn2, csuff, 25);
			scr=CubeRU.solve(new Random()).replace('R', 'L');
			viewType=3;break;
		case 274:
			turn2=new String[][]{{"U"},{"M"}};
			scr=OtherScr.megascramble(turn2, csuff, 25);viewType=3;break;
		case 275:
			turn2=new String[][]{{"U"},{"R"},{"F"}};
			scr=OtherScr.megascramble(turn2, csuff, 25);viewType=3;break;
		case 276:
			turn2=new String[][]{{"R","L"},{"U"}};
			scr=OtherScr.megascramble(turn2, csuff, 25);viewType=3;break;
		case 277:
			turn2=new String[][]{{"R","r"},{"U"}};
			scr=OtherScr.megascramble(turn2, csuff, 25);viewType=3;break;
		case 278:
//			turn2=new String[][]{{"U","D"},{"R","L"},{"F","B"}};
//			suff=new String[]{"2"};
//			scr=OtherScr.megascramble(turn2, suff, 25);
			scr=HalfTurn.solve(new Random());
			viewType=3;break;
		case 279:	//3阶LSLL
			turns=new String[][][]{{{"R U R'","R U2 R'","R U' R'"}},{{"F' U F","F' U2 F","F' U' F"}},{{"U","U2","U'"}}};
			scr=OtherScr.megascramble(turns, suff0, 15);viewType=3;break;
		case 288:	//Bandaged Cube
			scr=OtherScr.bicube();viewType=0;break;
		case 289:
			scr=OtherScr.sq1_scramble(2);viewType=0;break;
		case 304:	//五魔子集
			turn2=new String[][]{{"U"},{"R"}};
			scr=OtherScr.megascramble(turn2, csuff, 25);viewType=0;break;
		case 305:
			turns=new String[][][]{{{"R U R'","R U2 R'","R U' R'","R U2' R'"}},{{"F' U F","F' U2 F","F' U' F","F' U2' F"}},{{"U","U2","U'","U2'"}}};
			scr=OtherScr.megascramble(turns, suff0, 20);viewType=0;break;
		case 320:	//连拧
			scr="2) "+Cube222.solve(Cube222.randomState())+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 0, 0)
				+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40);
			viewType=0;break;
		case 321:
			scr="2) "+Cube222.solve(Cube222.randomState())+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 0, 0)
				+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40)
				+"\n5) "+OtherScr.megascramble(new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}}, csuff, 60);
			viewType=0;break;
		case 322:
			scr="2) "+Cube222.solve(Cube222.randomState())+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 0, 0)
				+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40)
				+"\n5) "+OtherScr.megascramble(new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}}, csuff, 60)
				+"\n6) "+OtherScr.megascramble(new String[][]{{"U","D","u","d","3u"},{"R","L","r","l","3r"},{"F","B","f","b","3f"}}, csuff, 80);
			viewType=0;break;
		case 323:
			scr="2) "+Cube222.solve(Cube222.randomState())+"\n3) "+new Search().solution(Tools.randomCube(), 21, 20000, 0, 0)
				+"\n4) "+OtherScr.megascramble(new String[][]{{"U","D","u"},{"R","L","r"},{"F","B","f"}}, csuff, 40)
				+"\n5) "+OtherScr.megascramble(new String[][]{{"U","D","u","d"},{"R","L","r","l"},{"F","B","f","b"}}, csuff, 60)
				+"\n6) "+OtherScr.megascramble(new String[][]{{"U","D","u","d","3u"},{"R","L","r","l","3r"},{"F","B","f","b","3f"}}, csuff, 80)
				+"\n7) "+OtherScr.megascramble(new String[][]{{"U","D","u","d","3u","3d"},{"R","L","r","l","3r","3l"},{"F","B","f","b","3f","3b"}}, csuff, 100);
			viewType=0;break;
		case 324:
			StringBuffer scrb=new StringBuffer();
			for(int j=0; j<5; j++){
				scrb.append(j+1+") "+new Search().solution(Tools.randomCube(), 22, 20000, 0, 0));
				if(j<4)scrb.append("\n");
			}
			scr=scrb.toString();viewType=0;break;
		}
		return scr;
	}
	public static void drawScr(int sel2, int width, Paint p, Canvas c){
		int[] colors={DCTimer.share.getInt("csn1", 0xffffff00), DCTimer.share.getInt("csn2", 0xff0000ff), DCTimer.share.getInt("csn3", 0xffff0000),
				DCTimer.share.getInt("csn4", 0xffffffff), DCTimer.share.getInt("csn5", 0xff009900), DCTimer.share.getInt("csn6", 0xffff8026)};
		if(viewType==2){
			byte[] imst;
			Cube.parse(2);imst=OtherScr.imagestr(DCTimer.cscrs);
			int a=width/10,i,j,d=0,sty=(int) ((width*0.75-6*a+2)/2);
			for(i=0;i<2;i++)
				for(j=0;j<2;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(3+(j+3)*a, sty+1+i*a, 2+(j+4)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(3+(j+3)*a, sty+1+i*a, 2+(j+4)*a, sty+(i+1)*a, p);
				}
			for(i=0;i<2;i++)
				for(j=0;j<8;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>=6)c.drawRect(7+(j+1)*a, sty+3+(i+2)*a, 6+(j+2)*a, sty+2+(i+3)*a, p);
					else if(j>=4)c.drawRect(5+(j+1)*a, sty+3+(i+2)*a, 4+(j+2)*a, sty+2+(i+3)*a, p);
					else if(j>=2)c.drawRect(3+(j+1)*a, sty+3+(i+2)*a, 2+(j+2)*a, sty+2+(i+3)*a, p);
					else c.drawRect(1+(j+1)*a, sty+3+(i+2)*a, (j+2)*a, sty+2+(i+3)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>=6)c.drawRect(7+(j+1)*a, sty+3+(i+2)*a, 6+(j+2)*a, sty+2+(i+3)*a, p);
					else if(j>=4)c.drawRect(5+(j+1)*a, sty+3+(i+2)*a, 4+(j+2)*a, sty+2+(i+3)*a, p);
					else if(j>=2)c.drawRect(3+(j+1)*a, sty+3+(i+2)*a, 2+(j+2)*a, sty+2+(i+3)*a, p);
					else c.drawRect(1+(j+1)*a, sty+3+(i+2)*a, (j+2)*a, sty+2+(i+3)*a, p);
				}
			for(i=0;i<2;i++)
				for(j=0;j<2;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(3+(j+3)*a, sty+5+(i+4)*a, 2+(j+4)*a, sty+4+(i+5)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(3+(j+3)*a, sty+5+(i+4)*a, 2+(j+4)*a, sty+4+(i+5)*a, p);
				}
		}
		else if(viewType==18) {
			float edgeFrac = (float) ((1+Math.sqrt(5))/4);
			float centerFrac = 0.5F;
			if(DCTimer.spSel[3]==0)colors=new int[] {Color.WHITE,Color.rgb(0, 0, 136),Color.rgb(0, 136, 0),Color.rgb(0, 255, 255),Color.rgb(136, 34, 34),Color.rgb(136, 170, 255),
					Color.RED,Color.BLUE,Color.rgb(255, 0, 255),Color.GREEN,Color.rgb(255, 136, 0),Color.YELLOW};
			else colors=new int[] {Color.WHITE,Color.RED,Color.rgb(0, 161, 0),Color.rgb(123, 0, 123),Color.YELLOW,Color.BLUE,
					Color.rgb(255, 255, 132),Color.rgb(66, 221, 255),Color.rgb(255, 127, 38),Color.GREEN,Color.rgb(255, 128, 255),Color.GRAY};
			int defaultWidth = 350;
			int defaultHeight = 180;
			float scale = (float) Math.min((width+7)/(double)defaultWidth, ((width*0.75)+7)/defaultHeight);
			int dx = (int) (((width+7) - (defaultWidth * scale))/2);
			int dy = (int) (((width*0.75)+7 - (defaultHeight * scale))/2);
			float majorR = 36*scale;
			float minorR = majorR * edgeFrac;
			float pentR = minorR*2;
			float cx1 = 92*scale + dx;
			float cy1 = 80*scale + dy;
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
		}
		else if(viewType==17) {
			byte[] imst;
			if(sel2==0)imst=Pyraminx.imageString();
			else imst=Pyraminx.imageString(DCTimer.cscrs);
			width=(int) (width*0.9);
			int a=(width-2)/6-1,b=(int) (a*Math.sqrt(3)/2),d=(int) ((width/0.9-(a*6+4))/2);
			colors = new int[]{DCTimer.share.getInt("csp1", 0xff009900), DCTimer.share.getInt("csp2", 0xffff0000),
					DCTimer.share.getInt("csp3", 0xffffff00), DCTimer.share.getInt("csp4", 0xff0000ff)};
			float[] arx,ary;
			byte[] layout =
				{1,2,1,2,1,0,2,0,1,2,1,2,1,
					0,1,2,1,0,2,1,2,0,1,2,1,0,
					0,0,1,0,2,1,2,1,2,0,1,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,1,2,1,2,1,0,0,0,0,
					0,0,0,0,0,1,2,1,0,0,0,0,0,
					0,0,0,0,0,0,1,0,0,0,0,0,0};
			int[] pos={d,d+a/2,d+a,d+3*a/2,d+2*a,d+5*a/2,d+2+5*a/2,d+2+3*a,d+4+3*a,d+4+7*a/2,d+4+4*a,d+4+9*a/2,d+4+5*a,
					d+4+11*a/2,d+a/2,d+a,d+3*a/2,d+2*a,d+2+2*a,d+2+5*a/2,d+2+3*a,d+2+7*a/2,d+4+7*a/2,d+4+4*a,d+4+9*a/2,d+4+5*a,
					0,0,d+a,d+3*a/2,d+2+3*a/2,d+2+2*a,d+2+5*a/2,d+2+3*a,d+2+7*a/2,d+2+4*a,d+4+4*a,d+4+9*a/2,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,d+2+3*a/2,d+2+2*a,d+2+5*a/2,d+2+3*a,d+2+7*a/2,d+2+4*a,0,0,0,
					0,0,0,0,0,d+2+2*a,d+2+5*a/2,d+2+3*a,d+2+7*a/2,0,0,0,0,
					0,0,0,0,0,0,d+2+5*a/2,d+2+3*a,0,0,0,0,0};
			for(int y=0;y<7;y++) 
				for(int x=0;x<13;x++){
					if(layout[y*13+x]==1){
						if(y<3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{y*b+y,y*b+y,(y+1)*b+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
						if(y>3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{(y-1)*b+1+y,(y-1)*b+1+y,y*b+1+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
					}
					if(layout[y*13+x]==2){
						if(y<3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{(y+1)*b+y,(y+1)*b+y,y*b+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
						if(y>3) {
							arx=new float[]{pos[y*13+x]+x,pos[y*13+x]+a+x,pos[y*13+x+1]+x};
							ary=new float[]{y*b+1+y,y*b+1+y,(y-1)*b+1+y};
							drawPolygon(p,c,colors[imst[y*13+x]],arx,ary,true);
						}
					}
				}
		}
		else if(viewType==1) {
			String[] tb = {"3","3","3","3","3","3","3","3","0","0","0","0","0","0","0","0"};
			String[] ty = {"c","e","c","e","c","e","c","e","e","c","e","c","e","c","e","c"};
			String[] col = {"51","1","12","2","24","4","45","5","5","54","4","42","2","21","1","15"};
			colors = new int[]{DCTimer.share.getInt("csq1", 0xffffff00), DCTimer.share.getInt("csq6", 0xffff8026), DCTimer.share.getInt("csq2", 0xff0000ff),
					DCTimer.share.getInt("csq4", 0xffffffff), DCTimer.share.getInt("csq3", 0xffff0000), DCTimer.share.getInt("csq5", 0xff009900)};
			byte[] img=new byte[24];
			if(sel2==0)img=SQ1.imagestr();
			else {
				img=Mi.img;
				ty=new String[]{"e","c","e","c","e","c","e","c","e","c","e","c","e","c","e","c"};
				col=new String[]{"2","12","1","51","5","45","4","24", "4","42","5","54","1","15","2","21"};
			}
			byte[] temp=new byte[12];
			for(int i=0;i<12;i++)temp[i]=img[i];
			byte[] top_side=rd(temp);
			for(int i=0;i<6;i++)temp[i]=img[i+18];
			for(int i=6;i<12;i++)temp[i]=img[i+6];
			byte[] bot_side=rd(temp);
			temp=new byte[top_side.length+bot_side.length];
			for(int i=0;i<top_side.length;i++)temp[i]=top_side[i];
			for(int i=top_side.length;i<top_side.length+bot_side.length;i++)temp[i]=bot_side[i-top_side.length];
			byte[] eido=temp;
			StringBuffer a2=new StringBuffer(),b2=new StringBuffer(),c2=new StringBuffer();
			for(int j=0; j<16; j++) {
				a2.append(ty[eido[j]]);
				b2.append(tb[eido[j]]);
				c2.append(col[eido[j]]);
			}
			String stickers=b2.append(c2).toString();
			String a=a2.toString();
			float z=1.366F; // sqrt(2)/sqrt(1^2+tan(15degrees)^2)
			float[] arrx, arry;
			int margin = 1;
			float sidewid=(float) (.15*100/z);
			int cx = (int) Math.min((width+7)/4,((width*0.75)+7)/2);
			int cy = (int) (((width*0.75)+7)/2);
			//int cz = cy/5;
			float rd=(cx-margin-sidewid*z)/z;
			float w = (sidewid+rd)/rd;      // ratio btw total piece width and rd
			float[] ag={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			float[] ag2={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			int foo;
			for(foo=0; foo<24; foo++){
				ag[foo]=(float) ((17F-foo*2)*Math.PI/12);
				a = a.concat("xxxxxxxxxxxxxxxx");
			}
			for(foo=0; foo<24; foo++){
				ag2[foo]=(float) ((19F-foo*2)*Math.PI/12);
				a = a.concat("xxxxxxxxxxxxxxxx");
			}
			float h = sin1(1,ag,rd)*w*z - sin1(1,ag,rd)*z;
			if(mis){
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(7,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(4,ag,rd)*w*z, cy-sin1(7,ag,rd)*w*z, cy-sin1(10,ag,rd)*w*z};
				drawPolygon(p,c,Color.BLACK,arrx,arry,true);
				cy += 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],arrx,arry,true);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(10,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],arrx,arry,true);
				cy -= 10;
			}
			else {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(6,ag,rd)*w, cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(0,ag,rd)*w};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(4,ag,rd)*w*z, cy-sin1(6,ag,rd)*w, cy+sin1(9,ag,rd)*w*z, cy-sin1(11,ag,rd)*w*z, cy-sin1(0,ag,rd)*w};
				drawPolygon(p,c,Color.BLACK,arrx,arry,true);
				arrx=new float[]{cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(9,ag,rd)*w*z};
				arry=new float[]{cy+sin1(9,ag,rd)*w*z-h, cy-sin1(11,ag,rd)*w*z-h, cy-sin1(11,ag,rd)*w*z, cy+sin1(9,ag,rd)*w*z};
				drawPolygon(p,c,colors[4],arrx,arry,true);
				cy += 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*z, cy-sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],arrx,arry,true);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z};
				arry=new float[]{cy-sin1(1,ag,rd)*w*z, cy-sin1(1,ag,rd)*z, cy-sin1(11,ag,rd)*w*z + h, cy-sin1(11,ag,rd)*w*z};
				drawPolygon(p,c,colors[2],arrx,arry,true);
				cy -= 10;
			}
			int sc = 0;
			for(foo=0; sc<12; foo++){
				if (a.length()<=foo) sc = 12;
				if (a.charAt(foo)=='x') sc++;
				if (a.charAt(foo)=='c'){
					arrx=new float[]{cx, cx+cos1(sc,ag,rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+2, ag, rd)};
					arry=new float[]{cy, cy-sin1(sc,ag,rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+2, ag, rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],arrx,arry,true);
					arrx=new float[]{cx+cos1(sc, ag, rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+1, ag, rd)*w*z, cx+cos1(sc, ag, rd)*w};
					arry=new float[]{cy-sin1(sc, ag, rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+1, ag, rd)*w*z, cy-sin1(sc, ag, rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(16+sc)-48],arrx,arry,true);
					arrx=new float[]{cx+cos1(sc+2, ag, rd), cx+cos1(sc+1, ag, rd)*z, cx+cos1(sc+1, ag, rd)*w*z, cx+cos1(sc+2, ag, rd)*w};
					arry=new float[]{cy-sin1(sc+2, ag, rd), cy-sin1(sc+1, ag, rd)*z, cy-sin1(sc+1, ag, rd)*w*z, cy-sin1(sc+2, ag, rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(17+sc)-48],arrx,arry,true);
					sc+=2;
				}
				if (a.charAt(foo)=='e'){
					arrx=new float[]{cx, cx+cos1(sc,ag,rd), cx+cos1(sc+1,ag,rd)};
					arry=new float[]{cy, cy-sin1(sc,ag,rd), cy-sin1(sc+1,ag,rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],arrx,arry,true);
					arrx=new float[]{cx+cos1(sc,ag,rd), cx+cos1(sc+1,ag,rd), cx+cos1(sc+1,ag,rd)*w, cx+cos1(sc,ag,rd)*w};
					arry=new float[]{cy-sin1(sc,ag,rd), cy-sin1(sc+1,ag,rd), cy-sin1(sc+1,ag,rd)*w, cy-sin1(sc,ag,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(16+sc)-48],arrx,arry,true);
					sc +=1;
				}
			}
			cx *= 3;
			cy += 10;
			if(mis){
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(7,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(4,ag,rd)*w*z, cy+sin1(7,ag,rd)*w*z, cy+sin1(10,ag,rd)*w*z};
				drawPolygon(p,c,Color.BLACK,arrx,arry,true);
				cy -= 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],arrx,arry,true);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(10,ag,rd)*w*z, cx+cos1(10,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],arrx,arry,true);
				cy += 10;
			}
			else {
				arrx=new float[]{cx+cos1(1,ag,rd)*w*z, cx+cos1(4,ag,rd)*w*z, cx+cos1(6,ag,rd)*w, cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(0,ag,rd)*w};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(4,ag,rd)*w*z, cy+sin1(6,ag,rd)*w, cy-sin1(9,ag,rd)*w*z, cy+sin1(11,ag,rd)*w*z, cy+sin1(0,ag,rd)*w};
				drawPolygon(p,c,Color.BLACK,arrx,arry,true);
				arrx=new float[]{cx+cos1(9,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z, cx+cos1(9,ag,rd)*w*z};
				arry=new float[]{cy-sin1(9,ag,rd)*w*z-10, cy+sin1(11,ag,rd)*w*z-10, cy+sin1(11,ag,rd)*w*z, cy-sin1(9,ag,rd)*w*z};
				drawPolygon(p,c,colors[4],arrx,arry,true);
				cy -= 10;
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(1,ag,rd)*w*z, cx+cos1(1,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*z, cy+sin1(1,ag,rd)*w*z};
				drawPolygon(p,c,colors[5],arrx,arry,true);
				arrx=new float[]{cx+cos1(0,ag,rd)*w, cx+cos1(0,ag,rd)*w, cx+cos1(11,ag,rd)*w*z, cx+cos1(11,ag,rd)*w*z};
				arry=new float[]{cy+sin1(1,ag,rd)*w*z, cy+sin1(1,ag,rd)*z, cy+sin1(11,ag,rd)*w*z+10, cy+sin1(11,ag,rd)*w*z};
				drawPolygon(p,c,colors[2],arrx,arry,true);
				cy += 10;
			}
			sc = 0;
			for(sc=0; sc<12; foo++){
				if (a.length()<=foo) sc = 12;
				if (a.charAt(foo)=='x') sc++;
				if (a.charAt(foo)=='c'){
					arrx=new float[]{cx, cx+cos2(sc,ag2,rd), cx+cos2(sc+1,ag2,rd)*z, cx+cos2(sc+2,ag2,rd)};
					arry=new float[]{cy, cy-sin2(sc,ag2,rd), cy-sin2(sc+1,ag2,rd)*z, cy-sin2(sc+2,ag2,rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],arrx,arry,true);
					arrx=new float[]{cx+cos2(sc,ag2,rd), cx+cos2(sc+1,ag2,rd)*z, cx+cos2(sc+1,ag2,rd)*w*z, cx+cos2(sc,ag2,rd)*w};
					arry=new float[]{cy-sin2(sc,ag2,rd), cy-sin2(sc+1,ag2,rd)*z, cy-sin2(sc+1,ag2,rd)*w*z, cy-sin2(sc,ag2,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(28+sc)-48],arrx,arry,true);
					arrx=new float[]{cx+cos2(sc+2,ag2,rd), cx+cos2(sc+1,ag2,rd)*z, cx+cos2(sc+1,ag2,rd)*w*z, cx+cos2(sc+2,ag2,rd)*w};
					arry=new float[]{cy-sin2(sc+2,ag2,rd), cy-sin2(sc+1,ag2,rd)*z, cy-sin2(sc+1,ag2,rd)*w*z, cy-sin2(sc+2,ag2,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(29+sc)-48],arrx,arry,true);
					sc +=2;
				}
				if (a.charAt(foo)=='e'){
					arrx=new float[]{cx, cx+cos2(sc,ag2,rd), cx+cos2(sc+1,ag2,rd)};
					arry=new float[]{cy, cy-sin2(sc,ag2,rd), cy-sin2(sc+1,ag2,rd)};
					drawPolygon(p,c,colors[(int)stickers.charAt(foo)-48],arrx,arry,true);
					arrx=new float[]{cx+cos2(sc,ag2,rd), cx+cos2(sc+1,ag2,rd), cx+cos2(sc+1,ag2,rd)*w, cx+cos2(sc,ag2,rd)*w};
					arry=new float[]{cy-sin2(sc,ag2,rd), cy-sin2(sc+1,ag2,rd), cy-sin2(sc+1,ag2,rd)*w, cy-sin2(sc,ag2,rd)*w};
					drawPolygon(p,c,colors[(int)stickers.charAt(28+sc)-48],arrx,arry,true);
					sc +=1;
				}
			}
		}
		else if(viewType==12) {
			byte[] posit = Clock.posit();
			int clock_radius = 52;
			int face_dist = 30;
			int face_background_dist = 29;
			int face_background_radius = 18;
			int cx = 56;
			int cy = 55;
			p.setColor(0xff000000);
			drawSideBackground(p, c, width, cx, cy, clock_radius+3,
					face_background_dist, face_background_radius+3);
			p.setColor(0xff3366ff);
			drawSideBackground(p, c, width, cx, cy, clock_radius,
					face_background_dist, face_background_radius);
			int i = 0;
			for(int y=cy-face_dist; y<=cy+face_dist; y+=face_dist)
				for(int x=cx-face_dist; x<=cx+face_dist; x+=face_dist)
					drawClockFace(p, c, width, x, y, 0xff88aaff, posit[i++]);
			byte[] pegs = Clock.pegs();
			drawPeg(p, c, width, cx - face_dist/2, cy - face_dist/2, 1-pegs[0]);
		  	drawPeg(p, c, width, cx + face_dist/2, cy - face_dist/2, 1-pegs[1]);
		  	drawPeg(p, c, width, cx - face_dist/2, cy + face_dist/2, 1-pegs[2]);
		  	drawPeg(p, c, width, cx + face_dist/2, cy + face_dist/2, 1-pegs[3]);
		  	cx = 167;
		  	p.setColor(0xff000000);
		  	drawSideBackground(p, c, width, cx, cy, clock_radius+3,
					face_background_dist, face_background_radius+3);
		  	p.setColor(0xff88aaff);
			drawSideBackground(p, c, width, cx, cy, clock_radius,
					face_background_dist, face_background_radius);
			for (int y = cy - face_dist; y <= cy + face_dist; y += face_dist)
		  		for (int x = cx - face_dist; x <= cx + face_dist; x += face_dist)
		  			drawClockFace(p, c, width, x, y, 0xff3366ff, posit[i++]);
			drawPeg(p, c, width, cx + face_dist/2, cy - face_dist/2, pegs[0]);
		  	drawPeg(p, c, width, cx - face_dist/2, cy - face_dist/2, pegs[1]);
		  	drawPeg(p, c, width, cx + face_dist/2, cy + face_dist/2, pegs[2]);
		  	drawPeg(p, c, width, cx - face_dist/2, cy + face_dist/2, pegs[3]);
		}
		else if(viewType==13) {
			byte[] imst=Floppy.image(DCTimer.cscrs);
			int a=width/8,i,j,d=0;
			int stx=(width-8*a)/2, sty=(int) ((width*0.75-5*a)/2);
			p.setStyle(Paint.Style.FILL);
			colors=new int[]{0xFF4B4D4B,0xFFFFEF33,0xFF33B9FF,0xFFC8CCC8,0xFFFF0026,0xFF99FF99};
			for(i=0;i<3;i++) {
				p.setStyle(Paint.Style.FILL);
				p.setColor(colors[imst[d++]]);
				c.drawRect(stx+3+(i+1)*a, sty+1, stx+2+(i+2)*a, sty+a, p);
				p.setStyle(Paint.Style.STROKE);
				p.setColor(Color.BLACK);
				c.drawRect(stx+3+(i+1)*a, sty+1, stx+2+(i+2)*a, sty+a, p);
			}
			for(i=0; i<3; i++){
				for(j=0; j<8; j++){
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>4)c.drawRect(stx+7+j*a, sty+3+(i+1)*a, stx+6+(j+1)*a, sty+2+(i+2)*a, p);
					else if(j>3)c.drawRect(stx+5+j*a, sty+3+(i+1)*a, stx+4+(j+1)*a, sty+2+(i+2)*a, p);
					else if(j>0)c.drawRect(stx+3+j*a, sty+3+(i+1)*a, stx+2+(j+1)*a, sty+2+(i+2)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+1)*a, stx+(j+1)*a, sty+2+(i+2)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>4)c.drawRect(stx+7+j*a, sty+3+(i+1)*a, stx+6+(j+1)*a, sty+2+(i+2)*a, p);
					else if(j>3)c.drawRect(stx+5+j*a, sty+3+(i+1)*a, stx+4+(j+1)*a, sty+2+(i+2)*a, p);
					else if(j>0)c.drawRect(stx+3+j*a, sty+3+(i+1)*a, stx+2+(j+1)*a, sty+2+(i+2)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+1)*a, stx+(j+1)*a, sty+2+(i+2)*a, p);
				}
			}
			for(i=0;i<3;i++) {
				p.setStyle(Paint.Style.FILL);
				p.setColor(colors[imst[d++]]);
				c.drawRect(stx+3+(i+1)*a, sty+5+4*a, stx+2+(i+2)*a, sty+4+5*a, p);
				p.setStyle(Paint.Style.STROKE);
				p.setColor(Color.BLACK);
				c.drawRect(stx+3+(i+1)*a, sty+5+4*a, stx+2+(i+2)*a, sty+4+5*a, p);
			}
		}
		else if(viewType==14) {
			byte[] imst=Domino.image(DCTimer.cscrs);
			int a=width/12,i,j,d=0;
			int stx=(width-12*a)/2, sty=(int) ((width*0.75-8*a+2)/2);
			p.setStyle(Paint.Style.FILL);
			for(i=0;i<3;i++)
				for(j=0;j<3;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+3+(j+3)*a, sty+1+i*a, stx+2+(j+4)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+3+(j+3)*a, sty+1+i*a, stx+2+(j+4)*a, sty+(i+1)*a, p);
				}
			for(i=0;i<2;i++)
				for(j=0;j<12;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>=9)c.drawRect(stx+7+j*a, sty+3+(i+3)*a, stx+6+(j+1)*a, sty+2+(i+4)*a, p);
					else if(j>=6)c.drawRect(stx+5+j*a, sty+3+(i+3)*a, stx+4+(j+1)*a, sty+2+(i+4)*a, p);
					else if(j>=3)c.drawRect(stx+3+j*a, sty+3+(i+3)*a, stx+2+(j+1)*a, sty+2+(i+4)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+3)*a, stx+(j+1)*a, sty+2+(i+4)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>=9)c.drawRect(stx+7+j*a, sty+3+(i+3)*a, stx+6+(j+1)*a, sty+2+(i+4)*a, p);
					else if(j>=6)c.drawRect(stx+5+j*a, sty+3+(i+3)*a, stx+4+(j+1)*a, sty+2+(i+4)*a, p);
					else if(j>=3)c.drawRect(stx+3+j*a, sty+3+(i+3)*a, stx+2+(j+1)*a, sty+2+(i+4)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+3)*a, stx+(j+1)*a, sty+2+(i+4)*a, p);
				}
			for(i=0;i<3;i++)
				for(j=0;j<3;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+3+(j+3)*a, sty+5+(i+5)*a, stx+2+(j+4)*a, sty+4+(i+6)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+3+(j+3)*a, sty+5+(i+5)*a, stx+2+(j+4)*a, sty+4+(i+6)*a, p);
				}
		}
		else if(viewType==15) {
			byte[] imst=Tower.image(DCTimer.cscrs);
			int a=width/10,i,j,d=0;
			int stx=(width-8*a)/2, sty=(int) ((width*0.75-7*a)/2);
			p.setStyle(Paint.Style.FILL);
			colors=new int[]{0xFF4B4D4B,0xFFFFEF33,0xFF33B9FF,0xFFC8CCC8,0xFFFF0026,0xFF99FF99};
			for(i=0;i<2;i++)
				for(j=0; j<2; j++){
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+3+(j+2)*a, sty+1+i*a, stx+2+(j+3)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+3+(j+2)*a, sty+1+i*a, stx+2+(j+3)*a, sty+(i+1)*a, p);
				}
			for(i=0; i<3; i++)
				for(j=0; j<8; j++){
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>5)c.drawRect(stx+7+j*a, sty+3+(i+2)*a, stx+6+(j+1)*a, sty+2+(i+3)*a, p);
					else if(j>3)c.drawRect(stx+5+j*a, sty+3+(i+2)*a, stx+4+(j+1)*a, sty+2+(i+3)*a, p);
					else if(j>1)c.drawRect(stx+3+j*a, sty+3+(i+2)*a, stx+2+(j+1)*a, sty+2+(i+3)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+2)*a, stx+(j+1)*a, sty+2+(i+3)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>5)c.drawRect(stx+7+j*a, sty+3+(i+2)*a, stx+6+(j+1)*a, sty+2+(i+3)*a, p);
					else if(j>3)c.drawRect(stx+5+j*a, sty+3+(i+2)*a, stx+4+(j+1)*a, sty+2+(i+3)*a, p);
					else if(j>1)c.drawRect(stx+3+j*a, sty+3+(i+2)*a, stx+2+(j+1)*a, sty+2+(i+3)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+2)*a, stx+(j+1)*a, sty+2+(i+3)*a, p);
				}
			for(i=0;i<2;i++)
				for(j=0; j<2; j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+3+(j+2)*a, sty+5+(5+i)*a, stx+2+(j+3)*a, sty+4+(6+i)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+3+(j+2)*a, sty+5+(5+i)*a, stx+2+(j+3)*a, sty+4+(6+i)*a, p);
				}
		}
		else if(viewType==16) {
			byte[] imst=Skewb.image(DCTimer.cscrs);
			int a=width/8,i,d=0;
			int stx=(width-8*a)/2, sty=(int) ((width*0.75-6*a)/2);
			byte[] dxIdx={3,7,5,3,3,1}, dyIdx={5,3,3,1,3,3};
			p.setStyle(Paint.Style.FILL);
			for(i=0; i<6; i++){
				drawPolygon(p,c,colors[imst[d++]],new float[]{stx+dxIdx[i]+(dxIdx[i]-1)*a, stx+dxIdx[i]+dxIdx[i]*a, stx+dxIdx[i]+(dxIdx[i]-1)*a},
						new float[]{sty+dyIdx[i]+(dyIdx[i]-1)*a, sty+dyIdx[i]+(dyIdx[i]-1)*a, sty+dyIdx[i]+dyIdx[i]*a},true);
				drawPolygon(p,c,colors[imst[d++]],new float[]{stx+dxIdx[i]+dxIdx[i]*a, stx+dxIdx[i]+(dxIdx[i]+1)*a, stx+dxIdx[i]+(dxIdx[i]+1)*a},
						new float[]{sty+dyIdx[i]+(dyIdx[i]-1)*a, sty+dyIdx[i]+(dyIdx[i]-1)*a, sty+dyIdx[i]+dyIdx[i]*a},true);
				drawPolygon(p,c,colors[imst[d++]],new float[]{stx+dxIdx[i]+(dxIdx[i]+1)*a, stx+dxIdx[i]+(dxIdx[i]+1)*a, stx+dxIdx[i]+dxIdx[i]*a},
						new float[]{sty+dyIdx[i]+dyIdx[i]*a, sty+dyIdx[i]+(dyIdx[i]+1)*a, sty+dyIdx[i]+(dyIdx[i]+1)*a},true);
				drawPolygon(p,c,colors[imst[d++]],new float[]{stx+dxIdx[i]+dxIdx[i]*a, stx+dxIdx[i]+(dxIdx[i]-1)*a, stx+dxIdx[i]+(dxIdx[i]-1)*a},
						new float[]{sty+dyIdx[i]+(dyIdx[i]+1)*a, sty+dyIdx[i]+(dyIdx[i]+1)*a, sty+dyIdx[i]+dyIdx[i]*a},true);
				drawPolygon(p,c,colors[imst[d++]],new float[]{stx+dxIdx[i]+dxIdx[i]*a, stx+dxIdx[i]+(dxIdx[i]+1)*a, stx+dxIdx[i]+dxIdx[i]*a, stx+dxIdx[i]+(dxIdx[i]-1)*a},
						new float[]{sty+dyIdx[i]+(dyIdx[i]-1)*a, sty+dyIdx[i]+dyIdx[i]*a, sty+dyIdx[i]+(dyIdx[i]+1)*a, sty+dyIdx[i]+dyIdx[i]*a},true);
			}
		}
		else {
			int a=width/(viewType*4),i,j,d=0,b=viewType;
			byte[] imst;
			if(viewType==3){
				if(DCTimer.spSel[0]==1 && sel2==0)imst=Cube.imagestring();
				else {
					Cube.parse(3);imst=OtherScr.imagestr(DCTimer.cscrs);
				}
			}
			else if(viewType>7) {
				imst=Cube.imagestring();
			}
			else if(sel2==0)imst=Cube.imagestring();
			else {
				Cube.parse(viewType);
				imst=OtherScr.imagestr(DCTimer.cscrs);
			}
			int stx=(width-4*a*b)/2, sty=(int) ((width*0.75-3*a*b+2)/2);
			p.setStyle(Paint.Style.FILL);
			for(i=0;i<b;i++)
				for(j=0;j<b;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+3+(j+b)*a, sty+1+i*a, stx+2+(j+1+b)*a, sty+(i+1)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+3+(j+b)*a, sty+1+i*a, stx+2+(j+1+b)*a, sty+(i+1)*a, p);
				}
			for(i=0;i<b;i++)
				for(j=0;j<b*4;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					if(j>=b*3)c.drawRect(stx+7+j*a, sty+3+(i+b)*a, stx+6+(j+1)*a, sty+2+(i+1+b)*a, p);
					else if(j>=b*2)c.drawRect(stx+5+j*a, sty+3+(i+b)*a, stx+4+(j+1)*a, sty+2+(i+1+b)*a, p);
					else if(j>=b)c.drawRect(stx+3+j*a, sty+3+(i+b)*a, stx+2+(j+1)*a, sty+2+(i+1+b)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+b)*a, stx+(j+1)*a, sty+2+(i+1+b)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					if(j>=b*3)c.drawRect(stx+7+j*a, sty+3+(i+b)*a, stx+6+(j+1)*a, sty+2+(i+1+b)*a, p);
					else if(j>=b*2)c.drawRect(stx+5+j*a, sty+3+(i+b)*a, stx+4+(j+1)*a, sty+2+(i+1+b)*a, p);
					else if(j>=b)c.drawRect(stx+3+j*a, sty+3+(i+b)*a, stx+2+(j+1)*a, sty+2+(i+1+b)*a, p);
					else c.drawRect(stx+1+j*a, sty+3+(i+b)*a, stx+(j+1)*a, sty+2+(i+1+b)*a, p);
				}
			for(i=0;i<b;i++)
				for(j=0;j<b;j++) {
					p.setStyle(Paint.Style.FILL);
					p.setColor(colors[imst[d++]]);
					c.drawRect(stx+3+(j+b)*a, sty+5+(i+2*b)*a, stx+2+(j+1+b)*a, sty+4+(i+1+2*b)*a, p);
					p.setStyle(Paint.Style.STROKE);
					p.setColor(Color.BLACK);
					c.drawRect(stx+3+(j+b)*a, sty+5+(i+2*b)*a, stx+2+(j+1+b)*a, sty+4+(i+1+2*b)*a, p);
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
	private static void drawCircle(Paint p, Canvas c, int w, int cx, int cy, int rad){
		float[] scaledPoint = scalePoint(w, cx, cy);
		c.drawCircle(scaledPoint[0], scaledPoint[1], scaledPoint[2]*rad, p);
	}
	private static float[] scalePoint(int width, float cx, float cy){
		float scale = width/220F;
		float x = cx*scale + (width - (220 * scale))/2;
		float y = cy*scale + (width*3/4 - (110 * scale))/2;
		return new float[]{x, y, scale};
	}
	private static void drawClockFace(Paint p, Canvas cv, int w, int cx, int cy, int color, int hour){
		float[] scaled = scalePoint(w, cx, cy);
		p.setColor(color);
		drawCircle(p, cv, w, cx, cy, 13);
		p.setColor(0xffff0000);
	    drawCircle(p, cv, w, cx, cy, 4);
	    float[] arx={scalePoint(w, cx, cy - 12)[0], scalePoint(w, cx + 4, cy - 1)[0], scalePoint(w, cx - 4, cy - 1)[0]},
	    		ary={scalePoint(w, cx, cy - 12)[1], scalePoint(w, cx + 4, cy - 1)[1], scalePoint(w, cx - 4, cy - 1)[1]};
	    cv.save();
	    cv.rotate(30*hour, scaled[0], scaled[1]);
	    drawPolygon(p, cv, 0xffff0000, arx, ary, false);
	    cv.restore();
	    p.setColor(0xffffff00);
	    drawCircle(p, cv, w, cx, cy, 2);
	    arx=new float[]{scalePoint(w, cx, cy - 8)[0], scalePoint(w, cx + 2, (float)(cy - 0.5))[0], scalePoint(w, cx - 2, (float)(cy - 0.5))[0]};
	    ary=new float[]{scalePoint(w, cx, cy - 8)[1], scalePoint(w, cx + 2, (float)(cy - 0.5))[1], scalePoint(w, cx - 2, (float)(cy - 0.5))[1]};
	    cv.save();
	    cv.rotate(30*hour, scaled[0], scaled[1]);
	    drawPolygon(p, cv, 0xffffff00, arx, ary, false);
	    cv.restore();
	}
	private static void drawPeg(Paint p, Canvas c, int w, int cx, int cy, int pegValue) {
		int pegRadius = 6;
		int color = pegValue==1?0xffffff00:0xff444400;
		p.setColor(0xff000000);
		drawCircle(p, c, w, cx, cy, pegRadius+1);
		p.setColor(color);
		drawCircle(p, c, w, cx, cy, pegRadius);
	}
	protected static void drawPolygon(Paint p, Canvas c, int cl, float[] arx, float[] ary, boolean stoke){
		p.setColor(cl);
		Path path=new Path();
		path.moveTo(arx[0],ary[0]);
		for(int idx=1;idx<arx.length;idx++)path.lineTo(arx[idx], ary[idx]);
		path.close();
		p.setStyle(Paint.Style.FILL);
		c.drawPath(path, p);
		if(stoke){
			p.setStyle(Paint.Style.STROKE);
			p.setColor(Color.BLACK);
			c.drawPath(path, p);
		}
	}
	public static String contime(int hour, int min, int sec, int msec) {
		StringBuffer time=new StringBuffer();
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
		if(DCTimer.spSel[6]==1){
			if(msec<10)time.append(".00"+msec);
			else if(msec<100)time.append(".0"+msec);
			else time.append("."+msec);}
		else {
			if(msec<10)time.append(".0"+msec);
			else time.append("."+msec);
		}
		return time.toString();
	}
	public static String distime(int i){
		if(i<0)return "N/A";
		if(i==0)return "DNF";
		if(DCTimer.spSel[6]==0)i+=5;
		int msec=i%1000;
		if(DCTimer.spSel[6]==0)msec/=10;
		int sec=DCTimer.timmh?(i/1000)%60:i/1000;
		int min=DCTimer.timmh?(i/60000)%60:0;
		int hour=DCTimer.timmh?i/3600000:0;
		return contime(hour, min, sec, msec);
	}
	public static String distime2(int i){
		boolean m = i < 0;
		i = Math.abs(i) + 5;
		int ms=(i%1000)/10;
		int s=DCTimer.timmh?(i/1000)%60:i/1000;
		int mi=DCTimer.timmh?(i/60000)%60:0;
		return (m?"-":"")+(mi>0?mi+":":"")+((mi>0 && s<10)?"0":"")+s+"."+(ms<10?"0":"")+ms;
	}
	public static String distime(int idx, boolean b) {
		if(idx<0)return "N/A";
		int i=DCTimer.rest[idx];
		if(DCTimer.resd[idx]==0){
			if(b)return "DNF ("+distime(i)+")";
			else return "DNF";
		}
		if(DCTimer.resp[idx]==1)i+=2000;
		if(DCTimer.spSel[6]==0)i+=5;
		int msec=i%1000;
		if(DCTimer.spSel[6]==0)msec/=10;
		int sec=DCTimer.timmh?(i/1000)%60:i/1000;
		int min=DCTimer.timmh?(i/60000)%60:0;
		int hour=DCTimer.timmh?i/3600000:0;
		if(DCTimer.resp[idx]==1)return contime(hour, min, sec, msec)+"+";
		else return contime(hour, min, sec, msec);
	}
	public static String avg(int n, int i, int l){
		if(i<n-1){bidx[l]=-1;return "N/A";}
		int min,max,dnf=0,dnidx,cavg;
		int trim=n/20;if(n%20!=0)trim++;
		double sum=0;
		max=min=dnidx=i-n+1;
		boolean m=false;
		for(int j=i-n+1;j<=i;j++){
			if(DCTimer.resd[j]!=0 && !m){min=j;m=true;}
			if(DCTimer.resd[j]==0){max=j;dnidx=j;dnf++;}
		}
		if(dnf>trim){
			cavg=0;if(i<n)bavg[l]=0;return "DNF";
		}
		if(n<20) {
			for (int j=i-n+2;j<=i;j++) {
				if(dnf==0 && DCTimer.rest[j]+DCTimer.resp[j]*2000>DCTimer.rest[max]+DCTimer.resp[max]*2000)max=j;
				if(j!=dnidx && DCTimer.rest[j]+DCTimer.resp[j]*2000<=DCTimer.rest[min]+DCTimer.resp[min]*2000)min=j;
			}
			for (int j=i-n+1;j<=i;j++) {
				if(j!=max && j!=min){
					if(DCTimer.spSel[6]==1)sum+=DCTimer.rest[j]+DCTimer.resp[j]*2000;
					else sum+=(DCTimer.rest[j]+DCTimer.resp[j]*2000+5)/10;
				}
			}
			cavg=(int) (sum/(n-2)+0.5);
		}
		else {
			int[] data=new int[n-dnf];
			int len=0;
			for(int j=i-n+1;j<=i;j++) {
				if(DCTimer.resd[j]!=0) data[len++]=DCTimer.rest[j]+DCTimer.resp[j]*2000;
			}
			quickSort(data, 0, n-dnf-1);
			for(int j=trim;j<n-trim;j++) {
				if(DCTimer.spSel[6]==1)sum+=data[j];
				else sum+=(data[j]+5)/10;
			}
			cavg=(int) (sum/(n-2*trim)+0.5);
		}
		if(i==n-1){bavg[l]=cavg;bidx[l]=i;}
		if(bavg[l]==0){bavg[l]=cavg;bidx[l]=i;}
		else if(cavg<=bavg[l]){bavg[l]=cavg;bidx[l]=i;}
		return DCTimer.spSel[6]==0?distime(cavg*10):distime(cavg);
	}
	public static void quickSort(int[] a, int lo0, int hi0) {
		int lo = lo0, hi = hi0;
		if (lo >= hi) return;
		boolean transfer=true;
		while (lo != hi) {
			if (a[lo] > a[hi]) {
				int temp = a[lo];
				a[lo] = a[hi];
				a[hi] = temp;
				transfer = !transfer;
			}
			if(transfer) hi--;
			else lo++;
		}
		lo--; hi++;
		quickSort(a, lo0, lo);
		quickSort(a, hi, hi0);
	}
	public static String mean(int n, int i, int l){
		if(i<n-1){bidx[l]=-1;return "N/A";}
		int dnf=0,cavg;
		double sum=0;
		for(int j=i-n+1;j<=i;j++){
			if(DCTimer.resd[j]==0)dnf++;
		}
		if(dnf>0){cavg=0;if(i==n-1)bavg[l]=0;return "DNF";}
		for (int j=i-n+1;j<=i;j++) {
			if(DCTimer.spSel[6]==1)sum+=DCTimer.rest[j]+DCTimer.resp[j]*2000;
			else sum+=(DCTimer.rest[j]+DCTimer.resp[j]*2000+5)/10;
		}
		cavg=(int) (sum/n+0.5);
		if(i==n-1){bavg[0]=cavg;bidx[l]=i;}
		if(bavg[l]==0){bavg[l]=cavg;bidx[l]=i;}
		else {if(cavg<=bavg[l]){bavg[l]=cavg;bidx[l]=i;}}
		return DCTimer.spSel[6]==0?distime(cavg*10):distime(cavg);
	}
	public static String average(){
		double sum=0,sum2=0;
		omax=-1; omin=-1; oravg=-1;
		int n=DCTimer.resl;
		if(n==0)return "0/0): N/A (N/A)";
		for(int i=0;i<DCTimer.resl;i++) {
			if(DCTimer.resd[i]==0)n--;
			else {
				if(omax==-1)omax=i;
				else if(DCTimer.rest[i]+DCTimer.resp[i]*2000>DCTimer.rest[omax]+DCTimer.resp[omax]*2000)omax=i;
				if(omin==-1)omin=i;
				else if(DCTimer.rest[i]+DCTimer.resp[i]*2000<=DCTimer.rest[omin]+DCTimer.resp[omin]*2000)omin=i;
				if(DCTimer.spSel[6]==1)sum+=(double)DCTimer.rest[i]+DCTimer.resp[i]*2000;
				else sum+=(DCTimer.rest[i]+DCTimer.resp[i]*2000+5)/10;
				if(DCTimer.spSel[6]==1)sum2+=Math.pow(DCTimer.rest[i]+DCTimer.resp[i]*2000, 2);
				else sum2+=Math.pow((DCTimer.rest[i]+DCTimer.resp[i]*2000+5)/10, 2);
			}
		}
		String avg;
		if(n==0)avg="0/"+DCTimer.resl+"): N/A (N/A)";
		else {
			oravg=(int)(sum/n+0.5);
			orsdv=(int)(Math.sqrt((sum2-sum*sum/n)/n)+(DCTimer.spSel[6]==1?0:0.5));
			avg=""+n+"/"+DCTimer.resl+"): "+(DCTimer.spSel[6]==0?distime(oravg*10):distime(oravg))+" ("+standDev(orsdv)+")";
		}
		return avg;
	}
	public static String mulMean(int p) {
		double sum=0;
		int n=0;
		if(n==DCTimer.resl)return "-";
		for(int i=0;i<DCTimer.resl;i++) {
			if(DCTimer.mulp[p][i]!=0) {
				if(DCTimer.spSel[6]==1)sum+=(double)DCTimer.mulp[p][i];
				else sum+=(DCTimer.mulp[p][i]+5)/10;
				n++;
			}
		}
		if(n==0)return "-";
		int m=(int)(sum/n+0.5);
		String mean = DCTimer.spSel[6]==0?distime(m*10):distime(m);
		return mean;
	}
	public static String standDev(int i) {
		if(i<0)return "N/A";
		if(DCTimer.spSel[6]==1)i=(i+5)/10;
		StringBuffer s=new StringBuffer(""+i/100+".");
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
	public static float cos1(int index, float[] ag, float rd) {return (float) (Math.cos(ag[index])*rd);}
	public static float sin1(int index, float[] ag, float rd) {return (float) (Math.sin(ag[index])*rd);}
	public static float cos2(int index, float[] ag2, float rd) {return (float) (Math.cos(ag2[index])*rd);}
	public static float sin2(int index, float[] ag2, float rd) {return (float) (Math.sin(ag2[index])*rd);}
	public static byte[] rd(byte[] arr)
	{
		byte[] out = new byte[arr.length];
		int j=0;
		for (int i=0; i<arr.length; i++)
		{
			if(i==0 || arr[i]!=arr[i-1])
				out[j++] = arr[i];
		}
		byte[] temp=new byte[j];
		for(int i=0;i<j;i++)temp[i]=out[i];
		return temp;
	}
	public static String convStr(String s){
		if(s.length()==0 || s==null || s.equals("0"))return "Error";
		StringBuffer sb=new StringBuffer();
		byte dot=0, colon=0;
		int num=0;
		boolean dbc=false;
		for(int i=0;i<s.length();i++){
			if(s.charAt(i)>47 && s.charAt(i)<58){sb.append(s.charAt(i));num++;}
			if(s.charAt(i)=='.' && dot<1){sb.append('.');dot++;dbc=true;}
			if(s.charAt(i)==':' && colon<2 && !dbc){sb.append(':');colon++;}
		}
		if(num==0)return "Error";
		sb.insert(0, dot+""+colon);
		return sb.toString();
	}
	public static int convTime(String s){
		if(s.charAt(1)=='0')return (int)(Double.parseDouble(s.substring(2))*1000+0.5);
		int hour, min;
		double sec;
		String[] time=s.substring(2).split(":");
		if(s.charAt(1)=='1'){
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
	public static void drawHist(int width, Paint p, Canvas c) {
		int[] bins=new int[14];
	    long intervalStart;
	    long intervalEnd;
	    if(DCTimer.resl==0){
	    	intervalStart = 17000;
            intervalEnd = 23000;
	    } else {
	    	int stddev = orsdv*(DCTimer.spSel[6]==1?1:10);
	    	long mean = oravg*(DCTimer.spSel[6]==1?1:10);
	    	intervalStart = mean - 3 * Math.max(50, stddev);
	    	intervalEnd = mean + 3 * Math.max(50, stddev);
	    }
	    for (int i = 0; i < bins.length; i++) {
            bins[i] = 0;
        }
	    for (int i = 0; i < DCTimer.resl; i++) {
	    	if(DCTimer.resd[i]!=0) {
	    		int time=DCTimer.rest[i]+DCTimer.resp[i]*2000;
	    		if(time >= intervalStart && time < intervalEnd){
	    			int bin = (int) (bins.length * (time - intervalStart) / (intervalEnd - intervalStart));
	    			bins[bin]++;
	    		}
	    	}
	    }
	    int wBase = 68*width/288;
	    c.drawLine(wBase, 0, wBase, (int)(width*1.2), p);
	    float wBar = (float) (width*1.2 / (bins.length+1));
	    for (int i = 0; i < bins.length+1; i++) {
            float y = (float) ((i + 0.5) * wBar);
            c.drawLine(wBase - 4, y, wBase + 4, y, p);
        }
	    float binInterval = (float)(intervalEnd - intervalStart) / bins.length;
	    p.setTextSize(wBase*2/9);
	    p.setTextAlign(Align.RIGHT);
	    FontMetrics fm = p.getFontMetrics();
	    float fontHeight = fm.bottom - fm.top;
	    for (int i = 0; i < bins.length+1; i++) {
	    	int value = (int)(intervalStart + i * binInterval);
	    	//c.drawText(""+wi, width/2, width/3, p);
	    	//Log.v("wi", ""+wi);
	    	float y = (float) ((i + 0.5) * wBar + fontHeight / 2 - fm.bottom);
	    	c.drawText(distime2(value), wBase-5, y, p);
	    }
	    int maxValue = 0;
	    for (int i = 0; i < bins.length; i++) {
            if (bins[i] > maxValue) {
                maxValue = bins[i];
            }
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
	public static int getSessionType(long sesType, int idx){
		return (int) ((sesType >> (7*idx)) & 0x7f);
	}
}
