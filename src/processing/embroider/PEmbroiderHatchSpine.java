package processing.embroider;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class PEmbroiderHatchSpine{
	public static PEmbroiderGraphics G;
	 public static void setGraphics(PEmbroiderGraphics _G) {
		 G = _G;
	 }
	 public static void hatchSpine(PImage mask) {
		 hatchSpine(mask,G.HATCH_SPACING);
	 }
	 public static void hatchSpineSmooth(PImage mask) {
		 hatchSpineSmooth(mask,G.HATCH_SPACING);
	 }
	 public static void hatchSpineVF(PImage mask) {
		 hatchSpineVF(mask,G.HATCH_SPACING);
	 }
	 public static void hatchSpine(PImage mask, float d) {
		 int w = mask.width;
		 int h = mask.height;
		 boolean[] im = new boolean[w*h];


		 mask.loadPixels();
		 for (int i = 0; i < mask.pixels.length; i++) {
			 im[i] = (mask.pixels[i]>>16&0xFF)>127;
		 }
		 int[] iim = new int[w*h];
		 for (int i = 0; i < iim.length; i++) {
			 iim[i] = im[i]?1:0;
		 }
		 ArrayList<ArrayList<PVector>> contours0 = PEmbroiderTrace.findContours(iim,w,h);
		 for (int i = 0; i < contours0.size(); i++) {
			 contours0.set(i,PEmbroiderTrace.approxPolyDP(contours0.get(i),2));
			 //pushPolyline(contours.get(i),currentStroke,0);
		 }

		 PEmbroiderTrace.thinning(im, w, h);
		 im = PEmbroiderTrace.dilate5(im,w,h);
//		 im = PEmbroiderTrace.dilate5(im,w,h);
//		 im = PEmbroiderTrace.dilate5(im,w,h);

		 for (int i = 0; i < iim.length; i++) {
			 iim[i] = im[i]?1:0;
		 }
		 ArrayList<ArrayList<PVector>> contours = PEmbroiderTrace.findContours(iim,w,h);
		 for (int i = 0; i < contours.size(); i++) {
			 contours.set(i,PEmbroiderTrace.approxPolyDP(contours.get(i),1));
//			 pushPolyline(contours.get(i),currentStroke,0);
		 }


		 PGraphics pg = G.app.createGraphics(w,h);
		 pg.beginDraw();
		 pg.background(0);

		 pg.image(mask,0,0);
		 pg.filter(PConstants.INVERT);

//		 pg.stroke(0);
//		 pg.fill(255);
//		 pg.strokeWeight(5);
//		 
//		 for (int i = 0; i < contours.size(); i++) {
//			 pg.beginShape();
//			 for (int j = 0; j < contours.get(i).size(); j++) {
//				 pg.vertex(contours.get(i).get(j).x,contours.get(i).get(j).y);
//			 }
//			 pg.endShape(PConstants.CLOSE);
//		 }
//		 
		 pg.stroke(255);
		 pg.noFill();
		 pg.strokeWeight(d*1.2f);

		 ArrayList<PVector> P = new ArrayList<PVector>();
		 ArrayList<PVector> Q = new ArrayList<PVector>();
		 ArrayList<PVector> V = new ArrayList<PVector>();

		 ArrayList<ArrayList<PVector>> hatches = new ArrayList<ArrayList<PVector>>();
		 for (int i = 0; i < contours0.size(); i++) {
			 ArrayList<PVector> poly = contours0.get(i);

			 for (int j = 0; j < poly.size(); j++) {

				 PVector p  = poly.get(j);
				 PVector p1 = poly.get((j+1)%poly.size());

				 float a0 = PApplet.atan2(p1.y-p.y,p1.x-p.x);
				 float a1 = a0 - PApplet.HALF_PI;


				 float x = p.x;
				 float y = p.y;


				 float l = p.dist(p1);
				 int n = PApplet.ceil(l / d);

				 float dd = l/(float)n;

				 float dx = dd * PApplet.cos(a0);
				 float dy = dd * PApplet.sin(a0);

				 float vx = dd*PApplet.cos(a1);
				 float vy = dd*PApplet.sin(a1);

				 for (int k = 0; k < n; k++) {
					x += dx;
					y += dy;
					P.add(new PVector(x,y));
					Q.add(new PVector(x,y));
					V.add(new PVector(vx,vy));
				 }
			 }
		 }


		 for (int i = 0; i < 50; i++) {
			 for (int j = P.size()-1; j >= 0; j--) {
				 float x = P.get(j).x;
				 float y = P.get(j).y;
				 P.get(j).add(V.get(j).copy().mult(i==0?1:1));
				 if((pg.get((int)P.get(j).x, (int)P.get(j).y)>>16&0xFF)>127) {
					 if (i > 0) {
						 ArrayList<PVector> H = new ArrayList<PVector>();
						 H.add(Q.get(j));
						 H.add(new PVector(x,y));
						 hatches.add(H);
					 }

					 P.remove(j);
					 Q.remove(j);
					 V.remove(j);
					 continue;
				 }

				 pg.line(x,y,P.get(j).x, P.get(j).y);

			 }
		 }

//		 pg.filter(PConstants.ERODE);
//		 pg.filter(PConstants.ERODE);
//		 pg.filter(PConstants.ERODE);
//		 pg.filter(PConstants.ERODE);
//		 pg.filter(PConstants.ERODE);
//		 pg.filter(PConstants.ERODE);
//		 pg.filter(PConstants.ERODE);
//		 pg.filter(PConstants.ERODE);
//		 d*=0.8;
//		 pg.strokeWeight(d*1f);
//		 pg.image(mask,0,0);
//		 pg.filter(PConstants.INVERT);

		 for (int i = 0; i < contours.size(); i++) {
			 ArrayList<PVector> poly = contours.get(i);

			 for (int j = 0; j < poly.size(); j++) {

				 PVector p  = poly.get(j);
				 PVector p1 = poly.get((j+1)%poly.size());

				 float a0 = PApplet.atan2(p1.y-p.y,p1.x-p.x);
				 float a1 = a0 + PApplet.HALF_PI;


				 float x = p.x;
				 float y = p.y;


				 float l = p.dist(p1);
				 int n = PApplet.ceil(l / d);

				 float dd = l/(float)n;

				 float dx = dd * PApplet.cos(a0);
				 float dy = dd * PApplet.sin(a0);

				 float vx = dd*PApplet.cos(a1);
				 float vy = dd*PApplet.sin(a1);

				 for (int k = 0; k < n; k++) {
					x += dx;
					y += dy;
					P.add(new PVector(x,y));
					Q.add(new PVector(x,y));
					V.add(new PVector(vx,vy));
				 }
			 }
		 }

		 for (int i = 0; i < 50; i++) {
			 for (int j = P.size()-1; j >= 0; j--) {
				 float x = P.get(j).x;
				 float y = P.get(j).y;
				 P.get(j).add(V.get(j));
				 if((pg.get((int)P.get(j).x, (int)P.get(j).y)>>16&0xFF)>127) {
					 if (i > 0 /*&& P.get(j).dist(Q.get(j))>0*/) {
						 ArrayList<PVector> H = new ArrayList<PVector>();
						 H.add(Q.get(j));
						 H.add(new PVector(x,y));
						 hatches.add(H);
					 }
//					 P.get(j).add(V.get(j));
//					 Q.set(j,P.get(j));
//					 V.remove(j);
					 P.remove(j);
					 Q.remove(j);
					 V.remove(j);

					 continue;
				 }
				 if (x < 0 || x > w || y < 0 || y > h) {
					 P.remove(j);
					 Q.remove(j);
					 V.remove(j);
					 continue;
				 }
				 pg.line(x,y,P.get(j).x, P.get(j).y);

			 }
		 }

		 pg.stroke(255,0,0);

//		 pg.filter(PConstants.INVERT);
//		 pg.filter(PConstants.DILATE);
//		 pg.filter(PConstants.DILATE);
//		 pg.filter(PConstants.DILATE);
		 pg.endDraw();

//		 app.image(pg,0,0);

//		 ArrayList<ArrayList<PVector>> hpr = hatchParallelRaster(pg,PConstants.HALF_PI,2,2);
//		 hatches.addAll(hpr);

		 for (int i = 0; i < hatches.size(); i++) {
			 G.pushPolyline(hatches.get(i),G.currentStroke,0);
		 }

	}
	 public static void hatchSpineSmooth(PImage mask, float d) {
		 int w = mask.width;
		 int h = mask.height;
		 boolean[] im = new boolean[w*h];
		 
		 
		 mask.loadPixels();
		 for (int i = 0; i < mask.pixels.length; i++) {
			 im[i] = (mask.pixels[i]>>16&0xFF)>127;
		 }

		 int[] iim = new int[w*h];
		 for (int i = 0; i < iim.length; i++) {
			 iim[i] = im[i]?1:0;
		 }
		 
		
		 ArrayList<ArrayList<PVector>> contours0 = PEmbroiderTrace.findContours(iim,w,h);
		 for (int i = 0; i < contours0.size(); i++) {
			 contours0.set(i,PEmbroiderTrace.approxPolyDP(contours0.get(i),1));
			 G.pushPolyline(contours0.get(i),G.currentStroke,0);
			 contours0.set(i,PEmbroiderTrace.approxPolyDP(contours0.get(i),3));
//			 pushPolyline(contours0.get(i),currentStroke,0);
			 contours0.set(i,G.smoothen(contours0.get(i),0.5f,200));
//			 pushPolyline(contours0.get(i),currentStroke,0);
			 contours0.set(i, G.resample(contours0.get(i),d,d,0,0));
//			 pushPolyline(contours0.get(i),currentStroke,0);
		 }
		 
		 for (int i = 0; i < mask.pixels.length; i++) {
			 im[i] = (mask.pixels[i]>>16&0xFF)>127;
		 }
		 
		 PEmbroiderTrace.thinning(im, w, h);
		 im = PEmbroiderTrace.dilate5(im,w,h);
		 
		 for (int i = 0; i < iim.length; i++) {
			 iim[i] = im[i]?1:0;
		 }
		 ArrayList<ArrayList<PVector>> contours = PEmbroiderTrace.findContours(iim,w,h);
		 for (int i = 0; i < contours.size(); i++) {
			 contours.set(i,PEmbroiderTrace.approxPolyDP(contours.get(i),1));
//			 contours.set(i,smoothen(contours.get(i),0.5f,200));
//			 pushPolyline(contours.get(i),currentStroke,0);
//			 contours.set(i, resample(contours.get(i),d,d,0,0));
//			 pushPolyline(contours.get(i),currentStroke,0);
			 
//			 pushPolyline(contours.get(i),currentStroke,0);
		 }
		 
		 PGraphics pg = G.app.createGraphics(w,h);
		 pg.beginDraw();
		 pg.background(255);

		 pg.image(mask,0,0);
		 pg.filter(PConstants.INVERT);

		
		 pg.fill(0);
		 pg.noStroke();
		 pg.stroke(0);
		 pg.strokeWeight(5);
		 
		 for (int i = 0; i < contours0.size(); i++) {
			 pg.beginShape();
			 for (int j = 0; j < contours0.get(i).size(); j++) {
				 pg.vertex(contours0.get(i).get(j).x,contours0.get(i).get(j).y);
			 }
			 pg.endShape(PConstants.CLOSE);
		 }
		 
		 
		 pg.noStroke();
		 pg.fill(255);
		 for (int i = 0; i < iim.length; i++) {
			 if (iim[i]>0) {
				 pg.rect(i%w,i/w,1,1);
			 }
		 }
		 
		 pg.filter(PConstants.ERODE);
		 

		 pg.stroke(255);
		 pg.noFill();
		 pg.strokeWeight(1);
		 
		 ArrayList<PVector> P = new ArrayList<PVector>();
		 ArrayList<PVector> Q = new ArrayList<PVector>();
		 ArrayList<PVector> V = new ArrayList<PVector>();
		 
		 ArrayList<ArrayList<PVector>> hatches = new ArrayList<ArrayList<PVector>>();
		 for (int i = 0; i < contours0.size(); i++) {
			 ArrayList<PVector> poly = contours0.get(i);

			 for (int j = 0; j < poly.size(); j++) {

				 PVector p  = poly.get(j);
				 PVector p1 = poly.get((j+1)%poly.size());

				 float a0 = PApplet.atan2(p1.y-p.y,p1.x-p.x);
				 float a1 = a0 - PApplet.HALF_PI;


				 float x = (p.x+p1.x)/2f;
				 float y = (p.y+p1.y)/2f;

				 float vx = 2*PApplet.cos(a1);
				 float vy = 2*PApplet.sin(a1);
				 P.add(new PVector(x,y));
				 Q.add(new PVector(x,y));
				 V.add(new PVector(vx,vy));

			 }
		 }

		 
		 for (int i = 0; i < 500; i++) {
			 for (int j = P.size()-1; j >= 0; j--) {
				 float x = P.get(j).x;
				 float y = P.get(j).y;
				 P.get(j).add(V.get(j).copy().mult(i==0?1:1));
				 if((pg.get((int)P.get(j).x, (int)P.get(j).y)>>16&0xFF)>127) {
					 if (i > 0) {
						 ArrayList<PVector> H = new ArrayList<PVector>();
						 H.add(Q.get(j).add(V.get(j).copy().mult(-3)));
						 H.add(new PVector(x+V.get(j).x,y+V.get(j).y));
						 hatches.add(H);
					 }
					 
					 P.remove(j);
					 Q.remove(j);
					 V.remove(j);
					 continue;
				 }

	 
				 pg.line(x,y,P.get(j).x, P.get(j).y);
				
			 }
		 }
		 
		 pg.filter(PConstants.DILATE);
		 pg.filter(PConstants.DILATE);
		 pg.filter(PConstants.DILATE);

		 
		 pg.endDraw();
		 
//		 app.image(pg,0,0);
		 pg.filter(PConstants.INVERT);
		 
		 ArrayList<ArrayList<PVector>> hpr = G.hatchParallelRaster(pg,PConstants.HALF_PI,d,1);
		 for (int i = 0; i < hpr.size(); i++) {
			 hpr.set(i, G.resample(hpr.get(i),6,6,0,0));
			 if (hpr.get(i).size() < 2) {
				 continue;
			 }
			 PVector p0 = hpr.get(i).get(0);
			 PVector p1 = hpr.get(i).get(1);
			 PVector p2 = hpr.get(i).get(hpr.get(i).size()-2);
			 PVector p3 = hpr.get(i).get(hpr.get(i).size()-1);
			 
			 hpr.get(i).get(0).add(p0.copy().sub(p1));
			 hpr.get(i).get(hpr.get(i).size()-1).add(p3.copy().sub(p2));
		 }
		 hatches.addAll(hpr);
		 
		 G.clip(hatches,mask);
		 
		 for (int i = 0; i < hatches.size(); i++) {
			 G.pushPolyline(hatches.get(i),G.currentStroke,0);
		 }
//		 app.tint(255,100);
//		 app.image(pg,0,0);
	 }
	 public static void hatchSpineVF(PImage mask, float d) {
		 hatchSpineVF(mask,d,2000);
	 }
	 
	 public static void hatchSpineVF(PImage mask, float d, int maxVertices) {

		 int w = mask.width;
		 int h = mask.height;
		 boolean[] im = new boolean[w*h];


		 mask.loadPixels();
		 for (int i = 0; i < mask.pixels.length; i++) {
			 im[i] = (mask.pixels[i]>>16&0xFF)<127;
		 }

		 boolean[] jm = new boolean[w*h];
		 for (int i = 0; i < im.length; i++) {
			 jm[i] = (mask.pixels[i]>>16&0xFF)>127;
		 }
		 PEmbroiderTrace.thinning(jm, w, h);
		 jm = PEmbroiderTrace.dilate5(jm, w, h);
//		 jm = PEmbroiderTrace.dilate5(jm, w, h);
		 
		 int[] ijm = new int[w*h];
		 for (int i = 0; i < im.length; i++) {
			 ijm[i] = jm[i]?1:0;
		 }
		 
		 ArrayList<ArrayList<PVector>> contours = PEmbroiderTrace.findContours(ijm,w,h);
		 for (int i = 0; i < contours.size(); i++) {
			 contours.set(i,PEmbroiderTrace.approxPolyDP(contours.get(i),1));
		 }
		 
		 
//		 for (int i = 0; i < im.length; i++) {
//			 im[i] = !im[i];
//		 }
//		 im = PEmbroiderTrace.dilate5(im, w, h);
//		 im = PEmbroiderTrace.dilate5(im, w, h);
//		 im = PEmbroiderTrace.dilate5(im, w, h);
//		 im = PEmbroiderTrace.dilate5(im, w, h);
//		 for (int i = 0; i < im.length; i++) {
//			 im[i] = !im[i];
//		 }
		 
//		 float[] dt2 = perfectDistanceTransform(contours,w,h);
		 
		 int[] iim = new int[w*h];
		 for (int i = 0; i < iim.length; i++) {
			 
			 iim[i] = im[i]?0:1;
			 
		 }
		 ArrayList<ArrayList<PVector>> contours0 = PEmbroiderTrace.findContours(iim,w,h);
		 for (int i = 0; i < contours0.size(); i++) {
			 contours0.set(i,PEmbroiderTrace.approxPolyDP(contours0.get(i),2));
			 G.pushPolyline(contours0.get(i),G.currentStroke,0);
		 }
		 
		 float[] dt = G.perfectDistanceTransform(contours0,w,h);
		 
		 ArrayList<PVector> P = new ArrayList<PVector>();
		 ArrayList<Float> Q = new ArrayList<Float>();
		 ArrayList<PVector> V = new ArrayList<PVector>();
		 for (int i = 0; i < contours.size(); i++) {
			 ArrayList<PVector> poly = contours.get(i);

			 for (int j = 0; j < poly.size(); j++) {

				 PVector p  = poly.get(j);
				 PVector p1 = poly.get((j+1)%poly.size());

				 float a0 = PApplet.atan2(p1.y-p.y,p1.x-p.x);
				 float a1 = a0 + PApplet.HALF_PI;
				 
				 float x = p.x;
				 float y = p.y;

				 float vx = 2*PApplet.cos(a1);
				 float vy = 2*PApplet.sin(a1);
				 
				 float l = p.dist(p1);
				 int n = PApplet.ceil(l / d);

				 float dd = l/(float)n;

				 float dx = dd * PApplet.cos(a0);
				 float dy = dd * PApplet.sin(a0);

				 for (int k = 0; k < n; k++) {
					x += dx;
					y += dy;
					P.add(new PVector(x,y));
					Q.add(-1f);
					V.add(new PVector(vx,vy));
				 }
			 }
		 }

		 for (int i = 0; i < contours0.size(); i++) {
			 ArrayList<PVector> poly = contours0.get(i);

			 for (int j = 0; j < poly.size(); j++) {

				 PVector p  = poly.get(j);
				 PVector p1 = poly.get((j+1)%poly.size());

				 float a0 = PApplet.atan2(p1.y-p.y,p1.x-p.x);

				 float a1 = a0 - PApplet.HALF_PI;

				 float x = p.x;
				 float y = p.y;
				 
				 float vx = 2*PApplet.cos(a1);
				 float vy = 2*PApplet.sin(a1);

				 float l = p.dist(p1);
				 int n = PApplet.ceil(l / d);

				 float dd = l/(float)n;

				 float dx = dd * PApplet.cos(a0);
				 float dy = dd * PApplet.sin(a0);

				 for (int k = 0; k < n; k++) {
					x += dx;
					y += dy;
					P.add(new PVector(x,y));
					Q.add(1f);
					V.add(new PVector(vx,vy));
				 }
			 }
		 }

		 for (int i = 0; i < jm.length; i++) {
			 dt[i] = im[i]?0:dt[i];
		 }
//		 dt = PEmbroiderTrace.blur5(dt, w, h);
//		 dt2 = PEmbroiderTrace.blur5(dt2, w, h);
//			for (int i = 0; i < jm.length; i++) {
//				 app.noStroke();
//				 app.fill(dt[i]*2);
//				 app.rect(i%w,i/w,1,1);
//			}
//		 int maxVertices = 2000;
		 int minVertices = 3;
		 ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		 PGraphics pg2 = G.app.createGraphics(mask.width,mask.height);
		 pg2.beginDraw();
		 pg2.background(255);
		 pg2.image(mask,0,0);
		 pg2.noFill();
		 pg2.stroke(0);
		 pg2.strokeWeight(d*0.8f);
		 pg2.strokeJoin(PConstants.ROUND);

		 for (int i = 0; i < P.size(); i++) {
			 float x = P.get(i).x;
			 float y = P.get(i).y;
			 float[] ddt = Q.get(i)<0?dt:dt;
//			 G.app.rect(x,y,2,2);
			 ArrayList<PVector> poly = new ArrayList<PVector>();
			 int hate = 0;
			 for (int j = 0; j < maxVertices; j++) {
				 poly.add(new PVector(x,y));
				 PVector v;
				 int ii = (int)y;
				 int jj = (int)x;
				 if (j >= 4) {
					 try {
	
						 PVector u = new PVector(ddt[ii*w+jj]-ddt[ii*w+jj-1],ddt[ii*w+jj]-ddt[ii*w+jj-w]);
						 u.normalize();
						 v= u.mult(2).mult(Q.get(i));
					 }catch(Exception e) {
						 v = new PVector(2,2);
					 }
				 }else {
					 v = V.get(i);
				 }
				 x += v.x;
				 y += v.y;
				 if (Q.get(i)<0 && j==0) {
					 poly.add(0,new PVector(x-v.x*2,y-v.y*2));
				 }
				 if (x < 0 || x >= pg2.width || y < 0 || y >= pg2.height) {
					 break;
				 }

				 if ((pg2.get((int)x,(int)y)>>16&0xFF)<128) {
					 hate++;
				 }
				 if (jm[ii*w+jj]) {
//					 break;
				 }
				 if (Q.get(i) < 0 && hate > 0) {
					 break;
				 }
				 if (hate >= 2) {
					 break;
				 }
			 }
			 if (poly.size() < minVertices || poly.get(0).dist(poly.get(poly.size()-1))<5) {
				 continue;
			 }
			 pg2.beginShape();
			 for (int j = 0; j < poly.size();j ++) {
				 pg2.vertex(poly.get(j).x, poly.get(j).y);
			 }
			 pg2.endShape();
			 polys.add(poly);

		 }
		 pg2.endDraw();
		 pg2.filter(PConstants.ERODE);
		 pg2.filter(PConstants.ERODE);
		 pg2.filter(PConstants.DILATE);
		 G.clip(polys, mask);
//		 pg2.filter(PConstants.ERODE);
		 for (int i = 0; i < polys.size(); i++) {
			 for (int j = polys.get(i).size()-2; j >=0; j--) {
				 if ((j < polys.get(i).size()-3 && polys.get(i).get(j).dist(polys.get(i).get(j+3))<1) || (j < polys.get(i).size()-2 && polys.get(i).get(j).dist(polys.get(i).get(j+2))<1) || polys.get(i).get(j).dist(polys.get(i).get(j+1))<1) {
//					 polys.get(i).remove(j+1);
					 polys.get(i).remove(j);
				 }
			 }
			 
			 G.pushPolyline(polys.get(i),G.currentStroke,0);
		 }
		 ArrayList<ArrayList<PVector>> cpr = PEmbroiderTrace.findContours(pg2);
		 for (int i = 0; i < cpr.size(); i++) {
			 cpr.set(i,PEmbroiderTrace.approxPolyDP(cpr.get(i),1));
			 G.pushPolyline(cpr.get(i),G.currentStroke,0);
		 }
//		 ArrayList<ArrayList<PVector>> hpr = G.hatchParallelRaster(pg2,PConstants.QUARTER_PI,d,2);
//		 for (int i = 0; i < hpr.size(); i++) {
//			 G.pushPolyline(hpr.get(i),G.currentStroke,0);
//		 }
		 
//		 float[] pdt = perfectDistanceTransform(contours0,w,h);
//		 

//		G.app.image(pg2, 0, 0);	
	 }
}
