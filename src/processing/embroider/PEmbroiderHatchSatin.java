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
		for (int i = 0; i < src.h; i++){
			leftmost.y --;
			if (leftmost.y < 0 || 
					cache.get(leftmost) == 1 || 
					cache.get(leftmost.x-1,leftmost.y+1) == 1 ||
					cache.get(leftmost.x-1,leftmost.y+1) == 1 
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
	

	static ArrayList<Pt> boustrophedonStitches(Im prevIm, Im im, Pt p0, int d){
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
		int dx = 1;

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
						//println(touch,area[0],at,touch || area[0] > at,q0);
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
								pts.addAll(boustrophedonStitches(im,mask,q0,-d));
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
							pts.addAll(boustrophedonStitches(im,mask,q0,d));
						}
						aboveOn = -1;
					}
				}
			}


			p.x += dx;
			if (!im.isOn(p)){
				int lineEnd = p.x;
				if (invBranch){
					Pt q0 = new Pt(p.x,p.y+d);
					while(!im.isOn(q0)){
						q0.x-=dx;
					}
					Im mask = new Im(im.w,im.h);
					floodfillQ(prevIm,im,mask,q0,null);

					if (d > 0){
						q0 = loPt(mask);
					}else{
						q0 = hiPt(mask);
					}
					if (q0 != null){
						pts.addAll(boustrophedonStitches(im,mask,q0,-d));
					}
					p.x = belowOn;
					//p.y += d;

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
						q0.x-=dx;
					}
					Im mask = new Im(im.w,im.h);
					floodfillQ(prevIm,im,mask,q0,null);

					if (d > 0){
						q0 = hiPt(mask);
					}else{
						q0 = loPt(mask);
					}
					if (q0 != null){
						pts.addAll(boustrophedonStitches(im,mask,q0,d));
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
				while (!im.isOn(p) && 0 < p.x && p.x < im.w){
					p.x -= dx;
				}
				while (im.isOn(p)){
					p.x+=dx;
				}
				if (0 < p.x && p.x < im.w){
					walk.add(new Pt((lineStart+lineEnd)/2,p.y));
				}
				lineStart = p.x;
				invBranch = false;
				dx = -dx;
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
	static void removeNpxHolesAndIslands(Im im, int n){
		for (int i = 0; i < im.h; i++){
			for (int j = 0; j < im.w; j++){
				int x = j;
				if (im.get(x-1,i) == 1) {
					while (im.get(x,i) == 0 && im.get(x,i-1)==1 && im.get(x,i+1)==1){
						x++;
						if ( im.get(x+1,i) == 1) {
							break;
						}
					}
					if (x < j+n) {
						for (int k = j; k < x; k++) {
							im.set(k,i,1);
						}
					}
				}else {
					while (im.get(x,i) == 1 && im.get(x,i-1)==0 && im.get(x,i+1)==0){
						x++;
						if ( im.get(x+1,i) == 0) {
							break;
						}
					}
					if (x < j+n) {
						for (int k = j; k < x; k++) {
							im.set(k,i,0);
						}
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
		ArrayList<Pt> pts;
		if (G.SATIN_MODE != PEmbroiderGraphics.BOUSTROPHEDON) {
			pts = satinStitches(cpy,src,hiPt(im),1);
		}else {
			pts = boustrophedonStitches(cpy,src,hiPt(im),1);
		}
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

	public static ArrayList<ArrayList<PVector>> hatchSatinRaster(PImage im, float d, int n){
		PGraphics pg = G.app.createGraphics((int)PApplet.ceil(im.width/2f), (int)PApplet.ceil(im.height/d));
		float sx = (float)im.width/(float)pg.width;
		float sy = (float)im.height/(float)pg.height;
		pg.beginDraw();
		pg.image(im,0,0,pg.width,pg.height);
		pg.endDraw();
		
		Im srcImg = new Im(pg);
//		remove1pxHolesAndIslands(srcImg);
		removeNpxHolesAndIslands(srcImg,3);
		bridgeHoles(srcImg);
//		G.app.image(srcImg.toPImage(),0,0);
//		srcImg.toPImage().save("/Users/studio/Downloads/hsar.png");
		ArrayList<ArrayList<Pt>> pts = satinStitchesMultiple(srcImg);
		ArrayList<ArrayList<PVector>> ret = new ArrayList<ArrayList<PVector>>();
		for (int i = 0; i < pts.size(); i++) {
			ArrayList<ArrayList<PVector>> p;
			if (G.SATIN_MODE != PEmbroiderGraphics.BOUSTROPHEDON) {
				p = resampleSatinStitches(pts.get(i), n);
			}else {
				p = resampleBoustrophedonStitches(pts.get(i), n);
			}
			for (int j = p.size()-1; j >= 0; j--) {
				if (p.get(j).size()<=2) {
					p.remove(j);
					continue;
				}
				for (int k = 0; k < p.get(j).size(); k++) {
					p.get(j).get(k).x = (p.get(j).get(k).x+0.5f)*sx;
					p.get(j).get(k).y = (p.get(j).get(k).y+0.5f)*sy;
				}
			}
			ret.addAll(p);
		}
		return ret;
	}
	public static ArrayList<ArrayList<PVector>> hatchSatinAngledRaster(PImage im, float ang, float d, int n){
		if (PApplet.abs(ang) == 0.00001f) {
			hatchSatinRaster(im,d,n);
		}
		im.loadPixels();
		int xmin = 0;
		int xmax = im.width;
		int ymin = 0;
		int ymax = im.height;
		for (int i = 0; i < im.height; i++){
			for (int j = 0; j < im.width; j++){
				if ((im.pixels[i*im.width+j]&255) > 128){
					xmin = PApplet.min(j,xmin);
					ymin = PApplet.min(i,ymin);
					xmax = PApplet.max(j,xmax);
					ymax = PApplet.max(i,ymax);
				}
			}
		}
		int rw = xmax-xmin;
		int rh = ymax-ymin;
		
		float a0 = PApplet.atan2(rh,rw);
		
		float diag = (float)Math.hypot(rw/2, rh/2);
		float hh = PApplet.max(PApplet.abs(PApplet.sin(ang-a0)),PApplet.abs(PApplet.sin(ang+a0)))*diag;
		float ww = PApplet.max(PApplet.abs(PApplet.cos(ang-a0)),PApplet.abs(PApplet.cos(ang+a0)))*diag;
		
		int w = (int)PApplet.ceil(ww*2)+4;
		int h = (int)PApplet.ceil(hh*2)+4;
		int px = (w-im.width)/2;
		int py = (h-im.height)/2;
		PGraphics pg = G.app.createGraphics(w, h);
		pg.beginDraw();
		pg.background(0);
		pg.translate(w/2, h/2);
		pg.rotate(ang);
		pg.translate(-im.width/2, -im.height/2);
		pg.image(im,0,0);
		pg.endDraw();
//		G.app.image(pg,0,0);
//		pg.save("/Users/studio/Downloads/hsar.png");
		
		float costh = PApplet.cos(-ang);
		float sinth = PApplet.sin(-ang);
		ArrayList<ArrayList<PVector>> pts = hatchSatinRaster(pg,d,n);
		for (int i = 0; i < pts.size(); i++) {
			for (int j = 0; j < pts.get(i).size(); j++) {
				float dx = pts.get(i).get(j).x-w/2;
				float dy = pts.get(i).get(j).y-h/2;
		        pts.get(i).get(j).x = -px+w/2 + (dx * costh - dy * sinth);
		        pts.get(i).get(j).y = -py+h/2 + (dx * sinth + dy * costh);
			}
		}
		return pts;
	}
	
	public static ArrayList<ArrayList<PVector>> resampleSatinStitches(ArrayList<Pt> pts, int n){
		
		ArrayList<ArrayList<PVector>> ret = new ArrayList<ArrayList<PVector>>();

		for (int i = 0; i < pts.size(); i++) {
			if (i == 0) {
				ret.add(new ArrayList<PVector>());
				ret.get(0).add(new PVector(pts.get(i).x, pts.get(i).y));
				continue;
			}
			if (i != pts.size()-1 && pts.get(i).y == pts.get(i-1).y && pts.get(i).y == pts.get(i+1).y && PApplet.abs(pts.get(i).x-pts.get(i-1).x) == 1 && pts.get(i+1).x-pts.get(i).x == pts.get(i).x-pts.get(i-1).x) {
				int hn = (int)PApplet.ceil(G.SATIN_RESAMPLING_OFFSET_FACTOR * ((float)pts.get(i).y * 2) * (float)n);
				if ((pts.get(i).x+hn) % n == 0) {
					ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
				}
			}else if (PApplet.abs(pts.get(i).y - pts.get(i-1).y) == 1 && pts.get(i-1).x-pts.get(i).x > 2) {
//
				for (int j = pts.get(i-1).x-1; j > pts.get(i).x; j--) {
					int hn = (int)PApplet.ceil(G.SATIN_RESAMPLING_OFFSET_FACTOR * ((float)pts.get(i).y * 2 + 1) * (float)n);
					if ((j+hn)%n == 0) {
						float t = 0.5f;
						if (G.SATIN_MODE != PEmbroiderGraphics.SIGSAG) {
							t = (float)(j-pts.get(i).x)/(float)(pts.get(i-1).x-pts.get(i).x);
						}
						float y = (float)pts.get(i).y * (1-t) + (float)pts.get(i-1).y * t;
						ret.get(ret.size()-1).add(new PVector(j,y));
					}
				}
				ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
			}else if (i != pts.size()-1 && PApplet.abs(pts.get(i).y - pts.get(i-1).y) == 1 && pts.get(i+1).y - pts.get(i).y == pts.get(i).y - pts.get(i-1).y) {
				if (pts.get(i).y % 2 == 0) {
					ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
				}
			}else if (PApplet.abs(pts.get(i).y - pts.get(i-1).y) > 8 || PApplet.abs(pts.get(i).x - pts.get(i-1).x) > 8){
//				ret.add(new ArrayList<PVector>());
				ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
			}else {
				ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
			}
		}
		
		return ret;
	}
	
	public static ArrayList<ArrayList<PVector>> resampleBoustrophedonStitches(ArrayList<Pt> pts, int n){
		
		ArrayList<ArrayList<PVector>> ret = new ArrayList<ArrayList<PVector>>();

		for (int i = 0; i < pts.size(); i++) {
			if (i == 0) {
				ret.add(new ArrayList<PVector>());
				ret.get(0).add(new PVector(pts.get(i).x, pts.get(i).y));
				continue;
			}
			if (i != pts.size()-1 && pts.get(i).y == pts.get(i-1).y && pts.get(i).y == pts.get(i+1).y && PApplet.abs(pts.get(i).x-pts.get(i-1).x) == 1 && pts.get(i+1).x-pts.get(i).x == pts.get(i).x-pts.get(i-1).x) {
				int hn = (int)PApplet.ceil(G.SATIN_RESAMPLING_OFFSET_FACTOR * (float)pts.get(i).y * (float)n);
				
				if ((pts.get(i).x+hn) % n == 0) {
					ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
				}
				
			}else if (i != pts.size()-1 && PApplet.abs(pts.get(i).y - pts.get(i-1).y) == 1 && pts.get(i+1).y - pts.get(i).y == pts.get(i).y - pts.get(i-1).y) {
				if (pts.get(i).y % 2 == 0) {
					ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
				}
			}else if (PApplet.abs(pts.get(i).y - pts.get(i-1).y) > 8 || PApplet.abs(pts.get(i).x - pts.get(i-1).x) > 8){
//				ret.add(new ArrayList<PVector>());
				ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
			}else {
				ret.get(ret.size()-1).add(new PVector(pts.get(i).x, pts.get(i).y));
			}
		}
		
		return ret;
	}

}
