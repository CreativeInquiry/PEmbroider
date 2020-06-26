package processing.embroider;

import java.util.ArrayList;
import processing.core.*;

public class PEmbroiderHatchSatin {
	public static PEmbroiderGraphics G;
	 public static void setGraphics(PEmbroiderGraphics _G) {
		 G = _G;
	 }

	static class Pt{
		int x;
		int y;
		Pt(int _x, int _y){
			x = _x;
			y = _y;
		}
		Pt(Pt p){
			x = p.x;
			y = p.y;
		}
		public String toString(){
			return "("+x+","+y+")";
		}
	}

	static class Im{
		int[] data;
		int w;
		int h;
		Im (int _w, int _h){
			data = new int[_w*_h];
			w = _w;
			h = _h;
		}
		Im (PImage im){
			data = new int[im.width*im.height];
			im.loadPixels();
			for (int i = 0; i < im.width*im.height; i++){
				data[i] = ((im.pixels[i]&255) > 128) ? 1 : 0;
			}
			w = im.width;
			h = im.height;
		}
		Im (Im im){
			w = im.w;
			h = im.h;
			data = new int[w*h];
			for (int i = 0; i < w*h; i++){
				data[i] = im.data[i];
			}
		}
		int get(int x, int y){
			if (x < 0 || x >= w || y < 0 || y >= h){
				return 0;
			}
			return data[y*w+x];
		}
		int get(Pt p){
			return get(p.x,p.y);
		}
		boolean isOn(int x, int y){
			return get(x,y)>0;
		}
		boolean isOn(Pt p){
			return isOn(p.x,p.y);
		}
		void set(int x, int y, int v){
			data[y*w+x] = v;
		}
		void set(Pt p, int v){
			set(p.x,p.y,v);
		}
		PImage toPImage(){
			PImage im = G.app.createImage(w,h,PConstants.RGB);
			im.loadPixels();
			for (int i = 0; i < data.length; i++){
				int g = (data[i]*127+128) & 255;
				im.pixels[i] = (g << 16) | (g << 8) | (g);
			}
			im.updatePixels();
			return im;
		}

	}

	static Pt hiPt(Im im){
		for (int i = 0; i < im.h; i++){
			for (int j = 0; j < im.w; j++){
				if (im.isOn(j,i)){
					return new Pt(j,i);
				}
			}
		}
		return null;
	}

	static Pt loPt(Im im){
		for (int i = im.h-1; i>=0; i--){
			for (int j = 0; j < im.w; j++){
				if (im.isOn(j,i)){
					return new Pt(j,i);
				}
			}
		}
		return null;
	}

	static boolean floodfill(Im prev, Im src, Im dst, Pt p0, int[] areaOut){
		boolean t = false;
		if (prev.get(p0) == -1){
			t = true;
		}
		if (src.get(p0) != 1){
			return t;
		}
		if (dst.get(p0) == 1){
			return t;
		}
		dst.set(p0,1);
		src.set(p0,0);

		if (areaOut != null){
			areaOut[0]++;
		}

		t |= floodfill(prev,src,dst,new Pt(p0.x-1,p0.y),areaOut);
		t |= floodfill(prev,src,dst,new Pt(p0.x+1,p0.y),areaOut);
		t |= floodfill(prev,src,dst,new Pt(p0.x,p0.y-1),areaOut);
		t |= floodfill(prev,src,dst,new Pt(p0.x,p0.y+1),areaOut);

		return t;
	}

	static boolean floodfillQ(Im prev, Im src, Im dst, Pt p0, int[] areaOut){
		boolean t = false;
		if (prev.get(p0) == -1){
			t = true;
		}
		if (src.get(p0) != 1){
			return t;
		}
		if (dst.get(p0) == 1){
			return t;
		}
		ArrayList<Pt> Q = new ArrayList<Pt>();
		Q.add(new Pt(p0));
		while (Q.size()>0){
			if (areaOut != null){
				areaOut[0]++;
			}

			Pt n = Q.get(0);
			Q.remove(0);
			Pt l = new Pt(n.x-1,n.y);
			Pt r = new Pt(n.x+1,n.y);
			Pt u = new Pt(n.x,n.y-1);
			Pt d = new Pt(n.x,n.y+1);
			if (src.get(l)==1){
				dst.set(l,1);
				src.set(l,0);
				Q.add(l);

			}
			if (src.get(r)==1){
				dst.set(r,1);
				src.set(r,0);
				Q.add(r);
			}
			if (src.get(u)==1){
				dst.set(u,1);
				src.set(u,0);
				Q.add(u);
			}
			if (src.get(d)==1){
				dst.set(d,1);
				src.set(d,0);
				Q.add(d);
			}
			if (prev.get(l) == -1 || 
					prev.get(r) == -1 || 
					prev.get(u) == -1 || 
					prev.get(d) == -1 ){
				t = true;
			}
		}
		return t;
	}


	static boolean floodBridgeHoleAt(Im src, Im cache, Pt p0){

		Im dst = new Im(src.w,src.h);
		ArrayList<Pt> Q = new ArrayList<Pt>();
		ArrayList<Pt> P = new ArrayList<Pt>();
		Q.add(new Pt(p0));
		dst.set(p0,1);
		Pt leftmost = new Pt(p0);
		while (Q.size()>0){

			Pt n = Q.get(0);
			Q.remove(0);
			P.add(new Pt(n));
			//if (n.x < leftmost.x){
			//  leftmost.x = n.x;
			//  leftmost.y = n.y;
			//}
			if (n.x <= 0 || n.x >= src.w-1 || n.y <= 0 || n.y >= src.h-1){
				for (int i = 0; i < P.size(); i++){
					cache.set(P.get(i),1);
				}
				return false;
			}
			if (cache.get(n) == 1){
				for (int i = 0; i < P.size(); i++){
					cache.set(P.get(i),1);
				}
				return false;
			}
			Pt l = new Pt(n.x-1,n.y);
			Pt r = new Pt(n.x+1,n.y);
			Pt u = new Pt(n.x,n.y-1);
			Pt d = new Pt(n.x,n.y+1);
			if (dst.get(l)==0 && src.get(l)==0){
				dst.set(l,1);
				Q.add(l);
			}
			if (dst.get(r)==0 && src.get(r)==0){
				dst.set(r,1);
				Q.add(r);
			}
			if (dst.get(u)==0 && src.get(u)==0){
				dst.set(u,1);
				Q.add(u);
			}
			if (dst.get(d)==0 && src.get(d)==0){
				dst.set(d,1);
				Q.add(d);
			}
		}
		for (int i = 0; i < src.w; i++){
			leftmost.x --;
			if (leftmost.x < 0 || 
					cache.get(leftmost) == 1 || 
					cache.get(leftmost.x+1,leftmost.y-1) == 1 ||
					cache.get(leftmost.x+1,leftmost.y+1) == 1 
					){
				break;
			}
			cache.set(leftmost,1);
			src.set(leftmost,0);
		}
		for (int i = 0; i < P.size(); i++){
			cache.set(P.get(i),1);
		}
		return true;
	}



	static int findArea(Im im){
		int a = 0;
		for (int i = 0; i < im.data.length; i++){
			if (im.data[i] > 0){
				a ++;
			}
		}
		return a;  
	}

	static ArrayList<Pt> satinStitches(Im prevIm, Im im, Pt p0, int d){
		//im.toPImage().save(random(1)+".png");

		ArrayList<Pt> pts = new ArrayList<Pt>();
		ArrayList<Pt> walk = new ArrayList<Pt>();
		pts.add(new Pt(p0));
		im.set(p0,-1);

		Pt p = new Pt(p0);
		int belowOn = -1;
		int belowOff = -1;
		int aboveOn = -1;

		int lineStart = p.x;

		boolean invBranch = false;

		for (int i = 0; i < Integer.MAX_VALUE; i++){

			if (im.get(p.x,p.y) != 0){
				if (im.isOn(p.x,p.y+d)){
					if (belowOn < 0){
						belowOn = p.x;
						//fill(0,255,64);
						//rect(p.x/reso,p.y/reso,1/reso,1/reso);
						//save("???.png"); 
					}else if (belowOff >= 0 && !invBranch){
						Pt q0 = new Pt(belowOn,p.y+d);
						Im mask = new Im(im.w,im.h);

						Im old = new Im(im);

						int[] area = {0};
						boolean touch = floodfillQ(prevIm,im,mask,q0,area);
						int at = findArea(im);
						//println(touch,area[0],at);
						if (touch || area[0] > at){
							invBranch = true;
							im = old;
						}else{

							if (d > 0){
								q0 = loPt(mask);
							}else{
								q0 = hiPt(mask);
							}
							if (q0!=null){
								pts.addAll(satinStitches(im,mask,q0,-d));
							}
							//pts.addAll(satinStitches(im,new Pt(p),abs(d)));
							//break;

							belowOn = -1;
							belowOff = -1;

						}
					}
				}else{
					if (belowOn >= 0 && belowOff < 0){
						belowOff = p.x;
					}
				}

				if (im.isOn(p.x,p.y-d)){
					if (aboveOn < 0){
						aboveOn = p.x;
					}
				}else{
					if (aboveOn >= 0){
						Pt q0 = new Pt(aboveOn,p.y-d);
						Im mask = new Im(im.w,im.h);
						floodfillQ(prevIm,im,mask,q0,null);

						if (d > 0){
							q0 = hiPt(mask);
						}else{
							q0 = loPt(mask);
						}
						if (q0 != null){
							pts.addAll(satinStitches(im,mask,q0,d));
						}
						aboveOn = -1;
					}
				}
			}


			p.x += 1;
			if (!im.isOn(p)){
				int lineEnd = p.x;
				if (invBranch){
					Pt q0 = new Pt(p.x,p.y+d);
					while(!im.isOn(q0)){
						q0.x--;
					}
					Im mask = new Im(im.w,im.h);
					floodfillQ(prevIm,im,mask,q0,null);

					if (d > 0){
						q0 = loPt(mask);
					}else{
						q0 = hiPt(mask);
					}
					if (q0 != null){
						pts.addAll(satinStitches(im,mask,q0,-d));
					}
					p.x = belowOn;
					p.y += d;

					while (im.isOn(p)){
						p.x -= 1;
					}

					belowOn = -1;
					aboveOn = -1;
					belowOff = -1;
					invBranch = false;

					continue;
				}else if (aboveOn >= 0){
					Pt q0 = new Pt(p.x,p.y-d);
					while(!im.isOn(q0)){
						q0.x--;
					}
					Im mask = new Im(im.w,im.h);
					floodfillQ(prevIm,im,mask,q0,null);

					if (d > 0){
						q0 = hiPt(mask);
					}else{
						q0 = loPt(mask);
					}
					if (q0 != null){
						pts.addAll(satinStitches(im,mask,q0,d));
					}

					belowOn = -1;
					aboveOn = -1;
					belowOff = -1;

				}
				p.y += d;
				belowOn = -1;
				belowOff = -1;
				aboveOn = -1;
				if (p.y >= im.h || p.y < 0){
					break;
				}
				while (im.isOn(p)){
					p.x+=1;
				}
				boolean once = false;
				for (int j = 0; j < im.w; j++){
					p.x -= 1;
					boolean on = im.isOn(p);
					if (on && !once){
						once = true;
					}
					if (!on && once){

						walk.add(new Pt((lineStart+lineEnd)/2,p.y));
						//walk.add(new Pt(p.x+2,p.y));
						lineStart = p.x+1;
						invBranch = false;
						break;
					}
				}
			}else{
				pts.add(new Pt(p));
				im.set(p,-1);
			}
		}
		for (int i = 0; i < walk.size(); i++){
			pts.add(0,walk.get(i));
		}
		return pts;
	}


	static void remove1pxHolesAndIslands(Im im){
		for (int i = 0; i < im.h; i++){
			for (int j = 0; j < im.w; j++){
				if (im.get(j,i) == 0){
					if (im.get(j-1,i) == 1
							&&im.get(j+1,i) == 1
							&&im.get(j,i-1) == 1
							&&im.get(j,i+1) == 1
							){
						im.set(j,i,1);
					}
				}else{
					if (im.get(j-1,i) == 0
							&&im.get(j+1,i) == 0
							&&im.get(j,i-1) == 0
							&&im.get(j,i+1) == 0
							){
						im.set(j,i,0);
					}
				}
			}
		}
	}

	static void bridgeHoles(Im im){
		Im cache = new Im(im.w,im.h);
		for (int i = 0; i < im.h; i++){
			boolean seenOn = false;
			for (int j = 0; j < im.w; j++){
				Pt p = new Pt(j,i);
				if (im.get(p) == 1){
					seenOn = true;
				}else{
					if (!seenOn){
						cache.set(p,1);
					}else{
						floodBridgeHoleAt(im,cache,p);
					}
				}
			}
		}
		//cache.toPImage().save("?.png");
	}

	static ArrayList<ArrayList<Pt>> satinStitchesMultiple(Im im){
		Im cpy = new Im(im);
		Im src = new Im(im);
		ArrayList<ArrayList<Pt>> ret = new ArrayList<ArrayList<Pt>>();
		ArrayList<Pt> pts = satinStitches(cpy,src,hiPt(im),1);
		ret.add(pts);
		for (int i = 0; i < pts.size(); i++){
			cpy.set(pts.get(i),0);
		}
		boolean redo = false;
		for (int i = 0; i < cpy.data.length; i++){
			if (cpy.data[i] > 0){
				redo = true;
				break;
			}
		}
		if (redo){
			ret.addAll(satinStitchesMultiple(cpy));
		}
		return ret;
	}

	public static ArrayList<ArrayList<PVector>> hatchSatinRaster(PImage im, float d){
		PGraphics pg = G.app.createGraphics((int)PApplet.ceil(im.width), (int)PApplet.ceil(im.height/d));
		float sx = 1;
		float sy = (float)im.height/(float)pg.height;
		pg.beginDraw();
		pg.image(im,0,0,pg.width,pg.height);
		pg.endDraw();
		
		Im srcImg = new Im(pg);
		remove1pxHolesAndIslands(srcImg);
		bridgeHoles(srcImg);
//		G.app.image(srcImg.toPImage(),0,0);
		ArrayList<ArrayList<Pt>> pts = satinStitchesMultiple(srcImg);
		ArrayList<ArrayList<PVector>> ret = new ArrayList<ArrayList<PVector>>();
		for (int i = 0; i < pts.size(); i++) {
			ArrayList<PVector> p = new ArrayList<PVector>();
			for (int j = 0; j < pts.get(i).size(); j++) {
				p.add(new PVector(((float)pts.get(i).get(j).x+0.5f)*sx, ((float)pts.get(i).get(j).y+0.5f)*sy));
			}
			ret.add(p);
		}
		return ret;
	}


}
