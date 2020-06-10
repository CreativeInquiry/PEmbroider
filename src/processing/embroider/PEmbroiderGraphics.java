package processing.embroider;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

//import processing.awt.PGraphicsJava2D;
import processing.core.*;

/**
 * This is a template class and can be used to start a new processing Library.
 * Make sure you rename this class as well as the name of the example package
 * 'template' to your own Library naming convention.
 * 
 * (the tag example followed by the name of an example included in folder
 * 'examples' will automatically include the example in the javadoc.)
 *
 */

@SuppressWarnings("unchecked") // shut up
public class PEmbroiderGraphics {
	PApplet app;
	//public PGraphics preview;
	public int width = 100;
	public int height = 100;
	public String path = "output.dst";
	ArrayList<PMatrix2D> matStack;
	public ArrayList<ArrayList<PVector>> polylines;
	public ArrayList<Integer> colors;
	public ArrayList<ArrayList<PVector>> polyBuff;
	public PGraphics rasterBuff;
	public ArrayList<Integer> cullGroups;

	boolean isFill = false;
	boolean isStroke = true;
	public int currentFill = 0xFF000000;
	public int currentStroke = 0xFF000000;
	public int currentCullGroup = 0;

	public int beginCullIndex = 0;

	static public final int NONE      =0;

	//hatch modes
	static public final int PARALLEL  =1;
	static public final int CROSS     =2;
	static public final int CONCENTRIC=3;
	static public final int SPIRAL    =4;
	static public final int PERLIN    =5;
	static public final int VECFIELD  =6;
	static public final int DRUNK     =7;
	
	//stroke modes
	static public final int PERPENDICULAR    =10;
	static public final int TANGENT   =11;
	
	static public final int COUNT       =12;
	static public final int WEIGHT      =13;
	static public final int SPACING     =14;

	static public final int ADAPTIVE = 20;
	static public final int FORCE_VECTOR = 21;
	static public final int FORCE_RASTER = 22;
	
	static public final int STROKE_OVER_FILL = 31;
	static public final int FILL_OVER_STROKE = 32;
	
	public int ELLIPSE_MODE = PConstants.CORNER;
	public int RECT_MODE = PConstants.CORNER;
	public int BEZIER_DETAIL = 40;
	public int CIRCLE_DETAIL = 32;
	public int HATCH_MODE = 1;
	public float HATCH_ANGLE  =  PApplet.QUARTER_PI;
	public float HATCH_ANGLE2 = -PApplet.QUARTER_PI;
	public float HATCH_SPACING = 4;
	public float HATCH_SCALE = 1;
	public int HATCH_BACKEND = ADAPTIVE;
	
	public int STROKE_MODE = PERPENDICULAR;
	public int STROKE_TANGENT_MODE = WEIGHT;
	public int STROKE_WEIGHT = 1;
	public float STROKE_SPACING = 4;
	public int STROKE_JOIN = PConstants.ROUND;
	public int STROKE_CAP = PConstants.ROUND;
	
	public boolean FIRST_STROKE_THEN_FILL = false;
	public boolean NO_RESAMPLE = false;
	
	public VectorField HATCH_VECFIELD;

	public float STITCH_LENGTH = 10;
	public float RESAMPLE_NOISE = 0.5f;
	public float MIN_STITCH_LENGTH = 4;
	
	public int[] FONT = PEmbroiderFont.SIMPLEX;
	public PFont TRUE_FONT;
	public float FONT_SCALE = 1f;
	public int FONT_ALIGN = PConstants.LEFT;
	public int FONT_ALIGN_VERTICAL = PConstants.BASELINE;
	
	boolean randomizeOffsetEvenOdd = false;
	float randomizeOffsetPrevious = 0.0f;
	
	static String logPrefix = "[PEmbroider] ";

	public PEmbroiderGraphics(PApplet _app, int w, int h) {
		app = _app;
		width = w;
		height = h;
		// preview = app.createGraphics(w, h);
		matStack = new ArrayList<PMatrix2D>();
		matStack.add(new PMatrix2D());
		polylines = new ArrayList<ArrayList<PVector>>();
		polyBuff = new ArrayList<ArrayList<PVector>> ();
		colors = new ArrayList<Integer>();
		cullGroups = new ArrayList<Integer>();
	}

	public void setPath(String _path) {
		path = _path;
	}
	
	public void clear() {
		polylines.clear();
		colors.clear();
		cullGroups.clear();
	}

	/* STYLE SETTING */
	public void fill(int r, int g, int b) {
		isFill = true;
		currentFill = 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
	}
	public void fill(int gray) {
		fill(gray,gray,gray);
	}

	public void noFill() {
		isFill = false;
	}
	
	/** Change stroke color
	 *  @param r red color 0-255
	 *  @param g green color 0-255
	 *  @param b blue color 0-255
	 *  @see   fill
	 */
	public void stroke(int r, int g, int b) {
		isStroke = true;
		currentStroke = 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
	}
	public void stroke(int gray) {
		stroke(gray,gray,gray);
	}

	public void noStroke() {
		isStroke = false;
	}
	
	public void strokeWeight(int d) {
		STROKE_WEIGHT = d;
	}

	public void strokeJoin(int j) {
		STROKE_JOIN = j;
	}
	
	public void strokeCap(int j) {
		STROKE_CAP = j;
	}
	
	public void ellipseMode(int mode) {
		ELLIPSE_MODE = mode;
	}
	public void rectMode(int mode) {
		RECT_MODE = mode;
	}
	public void bezierDetail(int n) {
		BEZIER_DETAIL = n;
	}
	
	public void hatchMode(int mode) {
		HATCH_MODE = mode;
	}
	public void strokeMode(int mode) {
		STROKE_MODE = mode;
	}
	public void strokeMode(int mode, int tanMode) {
		STROKE_MODE = mode;
		STROKE_TANGENT_MODE = tanMode;
	}
	public void hatchAngle(float ang) {
		HATCH_ANGLE = ang;
	}
	public void hatchAngles(float ang1, float ang2) {
		HATCH_ANGLE  = ang1;
		HATCH_ANGLE2 = ang2;
	}
	public void hatchAngleDeg(float ang) {
		hatchAngle(PApplet.radians(ang));
	}
	public void hatchAnglesDeg(float ang1, float ang2) {
		hatchAngles(PApplet.radians(ang1),PApplet.radians(ang2));
	}
	public void hatchSpacing(float d) {
		HATCH_SPACING = d;
	}
	public void strokeSpacing(float d) {
		STROKE_SPACING = d;
	}
	
	public void hatchScale(float s) {
		HATCH_SCALE = s;
	}
	public void hatchBackend(int mode) {
		HATCH_BACKEND = mode;
	}
	public void setVecField(VectorField vf) {
		HATCH_VECFIELD = vf;
	}
	
	public void stitchLength(float x) {
		STITCH_LENGTH = x;
	}
	public void minStitchLength(float x) {
		MIN_STITCH_LENGTH = x;
	}
	public void setStitch(float msl, float sl, float rn) {
		MIN_STITCH_LENGTH = msl;
		STITCH_LENGTH = sl;
		RESAMPLE_NOISE = rn;
	}
	
	public void setRenderOrder(int mode) {
		if (mode == STROKE_OVER_FILL) {
			FIRST_STROKE_THEN_FILL = false;
		}else if (mode == FILL_OVER_STROKE) {
			FIRST_STROKE_THEN_FILL = true;
		}
	}
	public void toggleResample(boolean b) {
		NO_RESAMPLE = !b;
	}
	
	/* MATH */
	public static float det(PVector r1, PVector r2, PVector r3) {
		float a = r1.x; float b = r1.y; float c = r1.z;
		float d = r2.x; float e = r2.y; float f = r2.z;
		float g = r3.x; float h = r3.y; float i = r3.z;
		return a*e*i + b*f*g + c*d*h - c*e*g - b*d*i - a*f*h;
	}


	public static PVector segmentIntersect3D (PVector p0, PVector p1, PVector q0, PVector q1){
		// returns lerp params, not actual point!

		PVector d0 = PVector.sub(p1,p0);
		PVector d1 = PVector.sub(q1,q0);

		PVector vc = d0.cross(d1);
		float vcn = vc.magSq();

		if (vcn == 0) {
			return null;
		}

		float t = det(PVector.sub(q0,p0), d1, vc)/vcn;
		float s = det(PVector.sub(q0,p0), d0, vc)/vcn;

		if (t < 0 || t > 1 || s < 0 || s > 1) {
			return null;
		}
		return new PVector(t,s);
	}

	public PVector centerpoint(ArrayList<PVector> poly) {
		float x=0;
		float y=0;
		for (int i = 0; i < poly.size(); i++) {
			x += poly.get(i).x;
			y += poly.get(i).y;
		}
		return new PVector(x/(float)poly.size(),y/(float)poly.size());
	}
	
	public PVector centerpoint(ArrayList<ArrayList<PVector>> poly, int whatever) {
		float x=0;
		float y=0;
		for (int i = 0; i < poly.size(); i++) {
			for (int j = 0; j < poly.get(i).size(); j++) {
				x += poly.get(i).get(j).x;
				y += poly.get(i).get(j).y;
			}
		}
		return new PVector(x/(float)poly.size(),y/(float)poly.size());
	}

	public class BBox{
		public float x;
		public float y;
		public float w;
		public float h;

		public BBox(float _x, float _y, float _w, float _h){
			x = _x;
			y = _y;
			w = _w;
			h = _h;
		}
		public BBox(PVector p, PVector q) {
			x = p.x;
			y = p.y;
			w = q.x-p.x;
			h = q.y-p.y;
		}
		public BBox(ArrayList<PVector> poly) {
			float xmin = Float.POSITIVE_INFINITY;
			float ymin = Float.POSITIVE_INFINITY;
			float xmax = Float.NEGATIVE_INFINITY;
			float ymax = Float.NEGATIVE_INFINITY;
			for (int i = 0; i < poly.size(); i++) {
				xmin = Math.min(xmin,poly.get(i).x);
				ymin = Math.min(ymin,poly.get(i).y);
				xmax = Math.max(xmax,poly.get(i).x);
				ymax = Math.max(ymax,poly.get(i).y);

			}
			x = xmin;
			y = ymin;
			w = xmax-xmin;
			h = ymax-ymin;
		}
		public BBox(ArrayList<ArrayList<PVector>> polys, int whatever) {
			float xmin = Float.POSITIVE_INFINITY;
			float ymin = Float.POSITIVE_INFINITY;
			float xmax = Float.NEGATIVE_INFINITY;
			float ymax = Float.NEGATIVE_INFINITY;
			for (int i = 0; i < polys.size(); i++) {
				for (int j = 0; j < polys.get(i).size(); j++) {
					xmin = Math.min(xmin,polys.get(i).get(j).x);
					ymin = Math.min(ymin,polys.get(i).get(j).y);
					xmax = Math.max(xmax,polys.get(i).get(j).x);
					ymax = Math.max(ymax,polys.get(i).get(j).y);
				}
			}
			x = xmin;
			y = ymin;
			w = xmax-xmin;
			h = ymax-ymin;
		}
	}

	public class BCircle {
		public float x;
		public float y;
		public float r;

		public BCircle(float _x, float _y, float _r){
			x = _x;
			y = _y;
			r = _r;
		}
		public BCircle(ArrayList<PVector> poly) {
			float rmax = 0;
			PVector c = centerpoint(poly);
			for (int i = 0; i < poly.size(); i++) {
				rmax = Math.max(rmax, c.dist(poly.get(i)));
			}
			x = c.x;
			y = c.y;
			r = rmax;
		}
		public BCircle(ArrayList<ArrayList<PVector>> poly, int whatever) {
			float rmax = 0;
			PVector c = centerpoint(poly,0);
			for (int i = 0; i < poly.size(); i++) {
				for (int j = 0; j < poly.get(i).size(); j++) {
					rmax = Math.max(rmax, c.dist(poly.get(i).get(j)));
				}
			}
			x = c.x;
			y = c.y;
			r = rmax;
		}
	}

	public ArrayList<PVector> segmentIntersectPolygon(PVector p0, PVector p1, ArrayList<PVector> poly) {
		ArrayList<PVector> isects = new ArrayList<PVector>();
		ArrayList<Float> iparams = new ArrayList<Float>();

		for (int i = 0; i < poly.size(); i++) {
			PVector v0 = poly.get(i);
			PVector v1 = poly.get((i+1)%poly.size());
			PVector o = segmentIntersect3D(p0,p1,v0,v1);
			if (o != null) {

				iparams.add(o.x);
			}
		}
		float[] iparamsArr = new float[iparams.size()];
		for (int i = 0; i < iparams.size(); i++) {
			iparamsArr[i] = (float)iparams.get(i);
		}
		iparamsArr = PApplet.sort(iparamsArr);

		for (int i = 0; i < iparams.size(); i++) {
			PVector _p0 = p0.copy();
			PVector _p1 = p1.copy();

			isects.add( _p0.mult(1-iparamsArr[i]).add(_p1.mult(iparamsArr[i]))   );
		}
		return isects;
	}
	
	public ArrayList<PVector> segmentIntersectPolygons(PVector p0, PVector p1, ArrayList<ArrayList<PVector>> polys) {
		ArrayList<PVector> isects = new ArrayList<PVector>();
		ArrayList<Float> iparams = new ArrayList<Float>();

		for (int i = 0; i < polys.size(); i++) {
			for (int j = 0; j < polys.get(i).size(); j++) {
				PVector v0 = polys.get(i).get(j);
				PVector v1 = polys.get(i).get((j+1)%polys.get(i).size());
				PVector o = segmentIntersect3D(p0,p1,v0,v1);
				if (o != null) {
					iparams.add(o.x);
				}
			}
		}
		float[] iparamsArr = new float[iparams.size()];
		for (int i = 0; i < iparams.size(); i++) {
			iparamsArr[i] = (float)iparams.get(i);
		}
		iparamsArr = PApplet.sort(iparamsArr);

		for (int i = 0; i < iparams.size(); i++) {
			PVector _p0 = p0.copy();
			PVector _p1 = p1.copy();

			isects.add( _p0.mult(1-iparamsArr[i]).add(_p1.mult(iparamsArr[i]))   );
		}
		return isects;
	}

	public boolean pointInPolygon(PVector p, ArrayList<PVector> poly, int trials){
		BCircle bcirc = new BCircle(poly);
		int avg = 0;
		for (int i = 0; i < trials; i++) {
			float a = app.random(0,PApplet.PI*2);
			float x = bcirc.x + (bcirc.r*2f) * PApplet.cos(a);
			float y = bcirc.y + (bcirc.r*2f) * PApplet.sin(a);
			if (segmentIntersectPolygon(p,new PVector(x,y),poly).size() % 2 == 1) {
				avg ++;
			}
		}
		if (avg > (float)(trials)/2f) {
			return true;
		}
		return false;
	}
	public boolean pointInPolygon(PVector p, ArrayList<PVector> poly) {
		return pointInPolygon(p,poly,3);
	}

	public PVector randomPointInPolygon(ArrayList<PVector> poly, int trials) {

		BBox bb = new BBox(poly);

		for (int i = 0; i < trials; i++) {

			float x = bb.x+app.random(0,bb.w);
			float y = bb.y+app.random(0,bb.h);

			PVector p = new PVector(x,y);
			if (pointInPolygon(p,poly,1)) {
				return p;
			}
		}

		return null;
	}
	public PVector randomPointInPolygon(ArrayList<PVector> poly) {
		return randomPointInPolygon(poly,9999);
	}


	public void pushPolyline(ArrayList<PVector> poly, int color, float resampleRandomizeOffset) {
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		for (int i = 0; i < poly.size(); i++) {
			poly2.add(poly.get(i).copy());
			for (int j = 0; j < matStack.size(); j++) {
				poly2.set(i, matStack.get(j).mult(poly2.get(i), null));
			}
		}
		colors.add(color);
		if (NO_RESAMPLE) {
		    polylines.add(poly2);
		}else {
			polylines.add(resample(poly2,MIN_STITCH_LENGTH,STITCH_LENGTH,RESAMPLE_NOISE,resampleRandomizeOffset));
		}
		
		cullGroups.add(currentCullGroup);
	}
	public void pushPolyline(ArrayList<PVector> poly, int color) {
		pushPolyline(poly,color,0);
	}

	/* SHAPE IMPLEMENTATION */

	public ArrayList<PVector> offsetPolyline(ArrayList<PVector> poly, float d){
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		for (int i = 0; i < poly.size(); i++) {
			PVector p = poly.get(i);
			PVector p0 = poly.get((i-1+poly.size()) % poly.size());
			PVector p1 = poly.get((i+1)%poly.size());
			float a0 = PApplet.atan2(p0.y-p.y,p0.x-p.x);
			float a1 = PApplet.atan2(p1.y-p.y,p1.x-p.x);
			if (i == 0) {
				a0 = a1 - PApplet.PI;
			}else if (i == poly.size()-1){
				a1 = a0 + PApplet.PI;
			}
			float a = (a1+a0)/2;
			float d2 = d / (PApplet.sin((a1-a0)/2));

			float x = p.x + d2*PApplet.cos(a);
			float y = p.y + d2*PApplet.sin(a);
			poly2.add(new PVector(x,y));
		}
		for (int i = 1; i < poly2.size()-2; i++) {
			int i0 = (i-1+poly.size()) % poly.size();
			int i1 = i;
			int i2 = (i+1)%poly2.size();
			int i3 = (i+2)%poly2.size();

			PVector p0 = poly2.get(i0);
			PVector p1 = poly2.get(i1);
			PVector p2 = poly2.get(i2);
			PVector p3 = poly2.get(i3);
			if (p0 == p3) {
				continue;
			}
			PVector o = segmentIntersect3D(p0,p1,p2,p3);
			if (o != null){
				PVector x = p0.copy().mult(1-o.x).add(p1.copy().mult(o.x));
				poly2.set(i1, x);
				poly2.set(i2, x);

			}
		}
		ArrayList<PVector> poly3 = new ArrayList<PVector>();
		for (int i = 0; i < poly2.size(); i++) {
			if (poly2.get(i) != poly2.get((i-1+poly2.size())%poly2.size())) {
				poly3.add(poly2.get(i));
			}
		}
		return poly3;
	}


	public ArrayList<ArrayList<PVector>> selfIntersectPolygon(ArrayList<PVector> poly){
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		if (poly.size() < 3) {
			return polys;
		}
		for (int i = 0; i < poly.size(); i++) {
			int i1 = (i+1)%poly.size();

			for (int j = 2; j < poly.size(); j++) {
				int i2 = (i+j)%poly.size();
				int i3 = (i+j+1)%poly.size();

				PVector p0 = poly.get(i);
				PVector p1 = poly.get(i1);
				PVector p2 = poly.get(i2);
				PVector p3 = poly.get(i3);
				if (p0 == p3) {
					continue;
				}
				PVector o = segmentIntersect3D(p0,p1,p2,p3);
				if (o != null){

					PVector x = p0.copy().mult(1-o.x).add(p1.copy().mult(o.x));
					ArrayList<PVector> poly1 = new ArrayList<PVector>();
					ArrayList<PVector> poly2 = new ArrayList<PVector>();
					for (int k = 0; k < poly.size(); k++) {

						PVector p = poly.get((i+k)%poly.size());
						if (1 <= k && k <= j) {
							poly1.add(p);
						}else {
							poly2.add(p);
						}
					}

					poly1.add(0,x);
					poly2.add(1,x);
					ArrayList<ArrayList<PVector>> polys1 = selfIntersectPolygon(poly1);
					ArrayList<ArrayList<PVector>> polys2 = selfIntersectPolygon(poly2);
					polys1.addAll(polys2);
					//	    			PApplet.println(polys1.size());
					return polys1;
				}
			}
		}

		polys.add(poly);
		return polys;
	}


	public boolean polygonOrientation(ArrayList<PVector> poly) {
		//https://en.wikipedia.org/wiki/Curve_orientation
		if (poly.size() < 3) {
			return true;
		}

		float ymax = Float.NEGATIVE_INFINITY;
		float ymin = Float.NEGATIVE_INFINITY;
		int amax = 0;
		int amin = 0;
		for (int i = 0; i < poly.size(); i++) {
			if (poly.get(i).y > ymax) {
				ymax = poly.get(i).y;
				amax = i;
			}
			if (poly.get(i).y < ymin) {
				ymin = poly.get(i).y;
				amin = i;
			}
		}
		PVector a = poly.get((amax-1+poly.size()) % poly.size());
		PVector b = poly.get(amax);
		PVector c = poly.get((amax+1)%poly.size());
		float d = (b.x-a.x)*(c.y-a.y)-(c.x-a.x)*(b.y-a.y);

		if (d == 0) {
			// colinear
			a = poly.get((amin-1+poly.size()) % poly.size());
			b = poly.get(amin);
			c = poly.get((amin+1)%poly.size());
			d = (b.x-a.x)*(c.y-a.y)-(c.x-a.x)*(b.y-a.y);

			int cnt = 0;
			while (d == 0 && cnt < poly.size()) {
				// until not colinear
				amin ++;
				cnt ++;

				a = poly.get((amin-1+poly.size()) % poly.size());

				b = poly.get(amin%poly.size());

				c = poly.get((amin+1)%poly.size());

				d = (b.x-a.x)*(c.y-a.y)-(c.x-a.x)*(b.y-a.y);

			}

			return d > 0;
		}

		return d > 0;
	}

	public float pointDistanceToLine(PVector p, PVector p0, PVector p1) {
		//https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
		float x0 = p.x;
		float y0 = p.y;
		float x1 = p0.x;
		float y1 = p0.y;
		float x2 = p1.x;
		float y2 = p1.y;
		return PApplet.abs((y2-y1)*x0-(x2-x1)*y0+x2*y1-y2*x1)/PApplet.sqrt(PApplet.sq(y2-y1)+PApplet.sq(x2-x1));
	}
	
	public static float pointDistanceToSegment(PVector p, PVector p0, PVector p1) {
		// https://stackoverflow.com/a/6853926
		float x = p.x;
		float y = p.y;
		float x1 = p0.x;
		float y1 = p0.y;
		float x2 = p1.x;
		float y2 = p1.y;
		float A = x - x1;
		float B = y - y1;
		float C = x2 - x1;
		float D = y2 - y1;
		float dot = A*C+B*D;
		float len_sq = C*C+D*D;
		float param = -1;
		if (len_sq != 0) {
			param = dot / len_sq;
		}
		float xx; float yy;
		if (param < 0) {
			xx = x1;
			yy = y1;
		}else if (param > 1) {
			xx = x2;
			yy = y2;
		}else {
			xx = x1 + param*C;
			yy = y1 + param*D;
		}
		float dx = x - xx;
		float dy = y - yy;
		return PApplet.sqrt(dx*dx+dy*dy);
	}
	
	
	public ArrayList<ArrayList<PVector>> insetPolygon(ArrayList<PVector> poly, float d){
		return offsetPolygon(poly,-d);
	}
	
	public ArrayList<ArrayList<PVector>> offsetPolygon(ArrayList<PVector> poly, float d){
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		if (poly.size() < 3) {
			return polys;
		}
		boolean hasSharpAngle = false;
		for (int i = 0; i < poly.size(); i++) {
			PVector a = poly.get(i);
			PVector b = poly.get((i+1)%poly.size());
			PVector c = poly.get((i+2)%poly.size());
			PVector u = b.copy().sub(a);
			PVector v = c.copy().sub(b);
			float ang = PApplet.abs(PVector.angleBetween(u, v));
			if (ang > PConstants.PI) {
				ang = PConstants.TWO_PI - ang;
			}
			if (ang > PConstants.PI * 0.9f) {
				hasSharpAngle = true;
			}
		}
		
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		for (int i = 0; i < poly.size(); i++) {
			
			PVector p = poly.get(i);
			PVector p0 = poly.get((i-1+poly.size()) % poly.size());
			PVector p1 = poly.get((i+1)%poly.size());
			
			boolean ok = true;
			
			for (int j = i+1; j < poly.size(); j++) {
				PVector q = poly.get(j);
				
				if (PApplet.abs(p.y-q.y) <= 1 && PApplet.abs(p.x-q.x) <= 1) {
					ok = false;
					break;
				}
				
			}
			if (!ok) {
				continue;
			}
//			for (int j = 0; j < poly.size(); j++) {
//				if (i == j || (j + 1)%poly.size() == i) {
//					continue;
//				}
//				PVector q0 = poly.get(j);
//				PVector q1 = poly.get((j+1)%poly.size());
//				float dl = pointDistanceToSegment(p,q0,q1);
////				PApplet.println(dl);
//				if (dl<1) {
//					ok = false;
//					break;
//				}
//			}
//			if (!ok) {
//				continue;
//			}
			float a0 = PApplet.atan2(p0.y-p.y,p0.x-p.x);
			float a1 = PApplet.atan2(p1.y-p.y,p1.x-p.x);
			float a = (a1+a0)/2;
			float d2 = d / (PApplet.sin((a1-a0)/2));

			float x = p.x + d2*PApplet.cos(a);
			float y = p.y + d2*PApplet.sin(a);
			
			PVector pp = new PVector(x,y);

			if (hasSharpAngle) {
				if (d < 0) {
					if (!pointInPolygon(pp,poly)) {
						continue;
					}
				}else if (d > 0) {
					if (pointInPolygon(pp,poly)) {
						continue;
					}
				}
				
				for (int j = 0; j < poly.size(); j++) {
					if (pp.dist(poly.get(i))<PApplet.abs(d)-0.1f) {
						ok = false;
						break;
					}
				}
				if (!ok) {
					continue;
				}
				for (int j = 0; j < poly.size(); j++) {
					PVector q0 = poly.get(j);
					PVector q1 = poly.get((j+1)%poly.size());
					float dl = pointDistanceToSegment(pp,q0,q1);
	//				PApplet.println(dl);
					if (dl<PApplet.abs(d)-0.1f) {
						ok = false;
						break;
					}
				}
				if(!ok) {
					continue;
				}
			}
			poly2.add(pp);
		}
		polys.add(poly2);

		
		polys = selfIntersectPolygon(poly2);
		if (polys.size() <= 0) {
			return polys;
			// no escape for size==1, shape could be totally "inside-out",
			// with no intersection, but should still be discarded.
		}
		BBox bb = new BBox(poly);
		bb.x -= PApplet.abs(d)*2;
		bb.y -= PApplet.abs(d)*2;
		bb.w += 4*PApplet.abs(d);
		bb.h += 4*PApplet.abs(d);
		PGraphics pg = app.createGraphics(PApplet.ceil(bb.w), PApplet.ceil(bb.h));
		pg.beginDraw();
		pg.background(d<0?0:255);
		pg.translate(-bb.x,-bb.y);
		pg.fill(255);
		pg.noStroke();
		pg.beginShape();
		for (int i = 0; i < poly.size(); i++) {
			pg.vertex(poly.get(i).x,poly.get(i).y);
		}
		pg.endShape(PConstants.CLOSE);
		pg.strokeJoin(PConstants.MITER);
		pg.noFill();
		pg.stroke(0);
		pg.strokeWeight(PApplet.abs(d*2));
		pg.beginShape();
		for (int i = 0; i < poly.size(); i++) {
			pg.vertex(poly.get(i).x,poly.get(i).y);
		}
		pg.endShape(PConstants.CLOSE);
		pg.endDraw();
		
		for (int i = polys.size()-1; i>=0; i--) {
			int trials = 99;
			boolean ok = false;
			for (int j = 0; j < 5; j++) {
				PVector p = randomPointInPolygon(polys.get(i),trials);

				if (p == null) {
					continue;
				}
				int c = pg.get((int)(p.x-bb.x),(int)(p.y-bb.y));
				int r = c >> 16 & 0xFF;
			//    			pg.beginDraw();pg.noStroke();pg.fill(0,255*i,255*(1-i));pg.circle(p.x-bb.x,p.y-bb.y,2f);pg.endDraw();
				if (r >= 128) {
					ok = true;
					break;
	
				}
				trials*=10;
			}
			if (!ok) {
				polys.remove(i);
			}
		}
//		    	app.tint(255,100);
//		    	app.image(pg,bb.x,bb.y);
		return polys;
	}

	public int rotatePolygonToMatch(ArrayList<PVector> poly0, ArrayList<PVector> poly1) {
		// polygons must have same number of vertices!
		// and rightly oriented!

		int md = 0;
		float ml = Float.POSITIVE_INFINITY;
			for (int d = 0; d < poly1.size(); d++) {
				float l = 0;
				for (int i = 0; i < poly0.size(); i++) {

						PVector p0 = poly0.get(i);
						PVector p1 = poly1.get((i+d) % poly1.size());
						l += p0.dist(p1);
				}

				if (l < ml) {
					md = d;
					ml = l;
				}
			}

		return md;
	}
	
	public ArrayList<ArrayList<PVector>> strokePolygonTangent(ArrayList<PVector> poly, int n, float d) {
		if (!polygonOrientation(poly)) {
			poly = new ArrayList<PVector>(poly);
			Collections.reverse(poly);
		}
		
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		int hn = n/2;
		
		boolean stop1 = false;
		boolean stop2 = false;
		for (int i = 0; i < hn; i++) {
			float dd = d;
			if (i == 0) {
				if (n % 2 == 1) {
					polys.add(poly);
				}else {
					dd = dd/2;
				}
			}

			if (!stop1) {
				ArrayList<ArrayList<PVector>> polys1;
				if (i == 0 && n % 2 == 0) {
					polys1 = offsetPolygon(poly,dd);
					
				}else {
					polys1 = offsetPolygon(polys.get(0),dd);
				}
				polys.addAll(0,polys1);
				if (polys1.size() != 1) {
					stop1 = true;
				}
			}
			
			if (!stop2) {
				ArrayList<ArrayList<PVector>> polys2;
				if (i == 0 && n % 2 == 0) {
					polys2 = insetPolygon(poly,dd);
				}else {
					polys2 = insetPolygon(polys.get(polys.size()-1),dd);
				}
				polys.addAll(polys2);
			
				if (polys2.size() != 1) {
					stop2 = true;
				}
			}
		}
		for (int i = 0; i < polys.size(); i++) {
			if (polys.get(i).size() > 0) {
				polys.get(i).add(polys.get(i).get(0));
			}
		}
		return polys;
	}
	
	public ArrayList<ArrayList<PVector>> strokePolyTangentRaster(ArrayList<ArrayList<PVector>> polys, int n, float d, int cap, int join, boolean close) {
		BBox bb = new BBox(polys,0);
		float scl = 1;
		if (d <= 4) {
			scl = 2;
		}
		
		bb.x -= n*d;
		bb.y -= n*d;
		bb.w += n*d*2;
		bb.h += n*d*2;
		
		PGraphics pg = app.createGraphics((int)(bb.w*scl), (int)(bb.h*scl));
		ArrayList<ArrayList<PVector>> polys2 = new ArrayList<ArrayList<PVector>>();
		
		int hn = n/2;
		
		for (int k = 0; k < hn; k++) {
			float dd = d;
			if (k == 0) {
				if (n % 2 == 1) {
					for (int i = 0; i < polys.size(); i++) {
						ArrayList<PVector> poly = new ArrayList<PVector>(polys.get(i));
						if (close && poly.size() > 1) {
							poly.add(poly.get(0));
						}
						polys2.add(poly);
					}
				}else {
				}
			}
			pg.beginDraw();
			pg.background(0);
			pg.stroke(255);
			pg.strokeWeight(scl*(dd*2*(k+1)-((n%2)==0?dd:0)));
			pg.strokeCap(cap);
			pg.strokeJoin(join);
			pg.noFill();
			pg.beginShape();

			for (int i = 0; i < polys.size(); i++) {
				if (i > 0) {
					pg.beginContour();
				}
				for (int j = 0; j < polys.get(i).size(); j++) {
					pg.vertex((polys.get(i).get(j).x -bb.x)*scl,(polys.get(i).get(j).y -bb.y)*scl);
				}
				if (i > 0) {
					pg.endContour();
				}
			}
			if (close) {
				pg.endShape(PConstants.CLOSE);
			}else {
				pg.endShape();
			}
			pg.endDraw();
			ArrayList<ArrayList<PVector>> polys3 = PEmbroiderTrace.findContours(pg);
			
			for (int i = polys3.size()-1; i >= 0; i--) {
				if (polys3.get(i).size() < 2) {
					polys3.remove(i);
					continue;
				}
				for (int j = 0; j < polys3.get(i).size(); j++) {
					polys3.get(i).get(j).div(scl).add(new PVector(bb.x,bb.y));
				}
//				polys3.set(i,resampleHalfKeepCorners(resampleHalf(resampleHalf(polys3.get(i))),0.1f));
				polys3.set(i, PEmbroiderTrace.approxPolyDP(polys3.get(i), 1));
				if (polys3.get(i).get(polys3.get(i).size()-1).dist(polys3.get(i).get(0))<d*2) {
					polys3.get(i).add(polys3.get(i).get(0));
				}
			}
			
			polys2.addAll(polys3);
			

//			app.tint(255,/100f);
//			app.image(pg,bb.x,bb.y);
		}
//		
		return polys2;
	}
	
	public ArrayList<ArrayList<PVector>> strokePolyNormal(ArrayList<PVector> poly, float d, float s, boolean close){
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		
		BBox bb = new BBox(poly);
		bb.x -= d*2;
		bb.y -= d*2;
		bb.w += d*4;
		bb.h += d*4;
		
		PGraphics pg = app.createGraphics((int)bb.w, (int)bb.h);
		pg.beginDraw();
		pg.background(0);
		pg.stroke(255);
		pg.strokeWeight(s*0.8f);
		pg.translate(-bb.x,-bb.y);
		pg.strokeCap(PConstants.SQUARE);
		for (int i = 0; i < poly.size()-(close?0:1); i++) {
			
			PVector p0 = poly.get(i);
			PVector p1 = poly.get((i+1)%poly.size());
			
			float a0 = PApplet.atan2(p1.y-p0.y,p1.x-p0.x);
			float a1 = a0 + PApplet.HALF_PI;
			
			float l = p0.dist(p1);
			int n = PApplet.ceil(l / s);
			
			for (int j = 0; j < n; j++) {
				float t = (float)j/(float)n;
				PVector p = p0.copy().lerp(p1, t);
				float x0 = p.x - d*PApplet.cos(a1);
				float y0 = p.y - d*PApplet.sin(a1);
				float x1 = p.x + d*PApplet.cos(a1);
				float y1 = p.y + d*PApplet.sin(a1);
				boolean lastOn = false;
				int m = PApplet.ceil(d);
				int mmm = PApplet.min(20,m/3);
				
				pg.beginShape();
				for (int k = 0; k < m; k++) {
					float u = (float)k/(float)(m-1);
					float x2 = x0 * (1-u) + x1 * u;
					float y2 = y0 * (1-u) + y1 * u;
					if (k == m-1 && lastOn) {
						if (polys.get(polys.size()-1).size() < mmm) {
							polys.remove(polys.size()-1);
						}else {
							polys.get(polys.size()-1).add(new PVector(x2,y2));
						    pg.vertex(x2,y2);
						}
						continue;
					}
					if ((pg.get((int)(x2-bb.x),(int)(y2-bb.y))>>16&0xFF)<127) {
						if (!lastOn) {
							ArrayList<PVector> pp = new ArrayList<PVector>();
							pp.add(new PVector(x2,y2));
							polys.add(pp);
							pg.endShape();
							pg.beginShape();
							pg.vertex(x2,y2);
						}else {
							polys.get(polys.size()-1).add(new PVector(x2,y2));
						}
						lastOn = true;
					}else {
						if (lastOn) {
							if (polys.get(polys.size()-1).size() < mmm) {//PApplet.min(3,m)) {
								polys.remove(polys.size()-1);
							}else {
								polys.get(polys.size()-1).add(new PVector(x2,y2));
								pg.vertex(x2,y2);
							}
						}
						lastOn = false;
					}
				}
				pg.endShape();
			}
			
		}
		int mm = PApplet.ceil(PApplet.PI*(d*2)/s);
		for (int i = 0; i < poly.size(); i++) {
			PVector p0 = poly.get(i);
			float x0 = p0.x;
			float y0 = p0.y;
			
			for (int j = 0; j < mm; j++) {
				float a = (float)j/(float)mm*PApplet.TWO_PI;
				float x1 = p0.x - d*PApplet.cos(a);
				float y1 = p0.y - d*PApplet.sin(a);
				boolean lastOn = false;
				
				int m = PApplet.ceil(d);
				int mmm = PApplet.min(10,m/3);
				
				pg.beginShape();
				for (int k = 0; k < m; k++) {
					float u = (float)k/(float)(m-1);
					float x2 = x0 * (1-u) + x1 * u;
					float y2 = y0 * (1-u) + y1 * u;
					if (k == m-1 && lastOn) {
						if (polys.get(polys.size()-1).size() < mmm) {
							polys.remove(polys.size()-1);
						}else {
							polys.get(polys.size()-1).add(new PVector(x2,y2));
						    pg.vertex(x2,y2);
						}
						continue;
					}
					if ((pg.get((int)(x2-bb.x),(int)(y2-bb.y))>>16&0xFF)<127) {
						if (!lastOn) {
							ArrayList<PVector> pp = new ArrayList<PVector>();
							pp.add(new PVector(x2,y2));
							polys.add(pp);
							pg.endShape();
							pg.beginShape();
							pg.vertex(x2,y2);
						}else {
							polys.get(polys.size()-1).add(new PVector(x2,y2));
						}
						lastOn = true;
					}else {
						if (lastOn) {
							if (polys.get(polys.size()-1).size() < mmm) {//PApplet.min(3,m)) {
								polys.remove(polys.size()-1);
							}else {
								polys.get(polys.size()-1).add(new PVector(x2,y2));
								pg.vertex(x2,y2);
							}
						}
						lastOn = false;
					}
				}
				pg.endShape();
				pg.endShape();
			}
		}
		
		
		pg.endDraw();
		for (int i = polys.size()-1; i >= 0; i--) {
			if (polys.get(i).size() < 2) {
				polys.remove(i);
				continue;
			}
			if (polys.get(i).get(0).dist(polys.get(i).get(polys.get(i).size()-1))<2) {
				polys.remove(i);
				continue;
			}
			for (int j = polys.get(i).size()-2; j > 0; j--) {
				polys.get(i).remove(j);
			}
		}
//		app.image(pg,0,0);
		return polys;
	}
	
	

	public void _stroke(ArrayList<ArrayList<PVector>> polys, boolean close) {
		if (STROKE_WEIGHT <= 1) {
			if (close) {
				for (int i = 0; i < polys.size(); i++) {
					if (polys.get(i).size() > 0) {
						polys.get(i).add(polys.get(i).get(0));
					}
				}
			}
			for (int i = 0; i < polys.size(); i++) {
				pushPolyline(polys.get(i),currentStroke);
			}
		}else {
			if (STROKE_MODE == TANGENT) {
				int cnt = STROKE_WEIGHT;
				float spa = STROKE_SPACING;
				if (STROKE_WEIGHT > 1) {
					if (STROKE_TANGENT_MODE == WEIGHT) {
						cnt = (int)PApplet.ceil((float)STROKE_WEIGHT/(float)STROKE_SPACING)+1;
						spa = (float)STROKE_WEIGHT/(float)cnt;
					}else if (STROKE_TANGENT_MODE == SPACING) {
						cnt = (int)PApplet.ceil((float)STROKE_WEIGHT/(float)STROKE_SPACING)+1;
					}
				}

				ArrayList<ArrayList<PVector>> polys2 = strokePolyTangentRaster(polys,cnt,spa,STROKE_CAP,STROKE_JOIN,close);
				for (int i = 0; i < polys2.size(); i++) {
					
					pushPolyline(polys2.get(i),currentStroke,0f);
				}
					
			}else if (STROKE_MODE == PERPENDICULAR) {
				
				for (int i = 0; i < polys.size(); i++) {
					ArrayList<ArrayList<PVector>> polys2 = strokePolyNormal(polys.get(i),(float)STROKE_WEIGHT/2.0f,STROKE_SPACING,close);
					for (int j = 0; j < polys2.size(); j++) {
						pushPolyline(polys2.get(j),currentStroke,0f);
					}
				}
			}
			
		}
	}
	
	
	public ArrayList<ArrayList<PVector>> hatchInset(ArrayList<PVector> poly, float d, int maxIter){
		return hatchInset(poly,d,maxIter,true);
	}

	public ArrayList<ArrayList<PVector>> hatchInset(ArrayList<PVector> poly, float d, int maxIter, boolean checkOrientation){
		if (!polygonOrientation(poly) && checkOrientation) {
			poly = new ArrayList<PVector>(poly);
			Collections.reverse(poly);
		}

		ArrayList<ArrayList<PVector>> polys = new  ArrayList<ArrayList<PVector>> ();

		if (maxIter <= 0) {
			return polys;
		}
		if (!polygonOrientation(poly)) {
			poly = new ArrayList<PVector>(poly);
			Collections.reverse(poly);
		}

		polys = insetPolygon(poly,d);
		ArrayList<ArrayList<PVector>> sub = new ArrayList<ArrayList<PVector>>();

		for (int i = 0; i < polys.size(); i++) {
			sub.addAll(hatchInset(polys.get(i),d,maxIter-1,false));
		}

		polys.addAll(sub);
		return polys;
	}

	public ArrayList<ArrayList<PVector>> hatchSpiral(ArrayList<PVector> poly, float d, int maxIter){
		return hatchSpiral(poly,d,maxIter,true);
	}

	public ArrayList<ArrayList<PVector>> hatchSpiral(ArrayList<PVector> poly, float d, int maxIter, boolean checkOrientation){

		if (!polygonOrientation(poly) && checkOrientation) {
			poly = new ArrayList<PVector>(poly);
			Collections.reverse(poly);
		}

		float l = 0;
		for (int i = 0; i < poly.size()-1; i++) {
			l += poly.get(i).dist(poly.get(i+1));
		}

		int n = 2*((int)Math.ceil((float)l / (float)STITCH_LENGTH));

		ArrayList<ArrayList<PVector>> polys = insetPolygon(poly,d);
		ArrayList<ArrayList<PVector>> polys2 = new ArrayList<ArrayList<PVector>>();
		polys2.add(poly);

		int it = 0;
		while (polys.size() == 1 && it < maxIter) {
			polys2.addAll(polys);
			polys = insetPolygon(polys.get(0),d);
			
//			if (polys.size()>0 && polys.get(0).size() - polys2.get(polys2.size()-1).size() > 0) {
				//wth!
//				break;
//			}
			it ++;
		}

		for (int i = polys2.size()-1; i >= 0; i--) {
			if (polys2.get(i).size() <= 2) {
				polys2.remove(i);
				continue;
			}
			polys2.get(i).add(polys2.get(i).get(0));
			polys2.set(i,resampleN(polys2.get(i),n));
		}
		ArrayList<PVector> spiral = new ArrayList<PVector>();
		for (int i = 0; i < polys2.size(); i++) {
			for (int j = 0; j < n; j++) {
				PVector p0 = polys2.get(i).get(j);
				PVector p1;

				if (i == polys2.size()-1) {
					if (polys.size() >= 1 && polys2.size() >= 1 && i > 0) {
						PVector a = p0.copy().sub(polys2.get(i-1).get(Math.max(j-1,0)));
						PVector b = p0.copy().sub(polys2.get(i-1).get(Math.max(j-0,0)));
						PVector c = a.mult(0.5f).add(b.mult(0.5f));
						p1 = p0.copy().add(c);
					}else {
						p1 = p0.copy();
					}
				}else {
					p1 = polys2.get(i+1).get(j);
				}
				float t = (float)j/(float)n;
				spiral.add(p0.copy().mult(1-t).add(p1.copy().mult(t)));
			}
		}

		ArrayList<ArrayList<PVector>> spirals = new ArrayList<ArrayList<PVector>>();
		if (spiral.size() >= 2) {
			spirals.add(spiral);
		}

		for (int i = 0; i < polys.size(); i++) {
			spirals.addAll(hatchSpiral(polys.get(i),d,maxIter,false));
		}

		return spirals;
	}

	public ArrayList<ArrayList<PVector>> hatchPerlin(ArrayList<PVector> poly, float d, float len, float scale, int maxIter) {

		BBox bb = new BBox(poly);


		PGraphics pg = app.createGraphics((int)bb.w,(int)bb.h);
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		pg.fill(255);
		pg.beginShape();
		for (int i = 0; i < poly.size(); i++) {
			pg.vertex(poly.get(i).x-bb.x, poly.get(i).y-bb.y);
		}
		pg.endShape();
		pg.endDraw();

		ArrayList<ArrayList<PVector>> polys = perlinField(pg, d, 0.01f*scale, len, 3, 100, maxIter);

		for (int i = 0; i < polys.size(); i++) {
			for (int j = 0; j < polys.get(i).size(); j++) {
				polys.get(i).get(j).add(new PVector(bb.x,bb.y));
			}
		}
		return polys;

	}

	public ArrayList<ArrayList<PVector>> hatchCustomField(ArrayList<PVector> poly, VectorField vf, float d, float len, int maxIter) {

		BBox bb = new BBox(poly);

		PGraphics pg = app.createGraphics((int)bb.w,(int)bb.h);
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		pg.fill(255);
		pg.beginShape();
		for (int i = 0; i < poly.size(); i++) {
			pg.vertex(poly.get(i).x-bb.x, poly.get(i).y-bb.y);
		}
		pg.endShape();
		pg.endDraw();

		ArrayList<ArrayList<PVector>> polys = customField(pg, vf, d, 3, 100, maxIter);

		for (int i = 0; i < polys.size(); i++) {
			for (int j = 0; j < polys.get(i).size(); j++) {
				polys.get(i).get(j).add(new PVector(bb.x,bb.y));
			}
		}
		return polys;

	}


	public ArrayList<ArrayList<PVector>> hatchParallel(ArrayList<PVector> poly, float ang, float d) {
		ArrayList<ArrayList<PVector>> hatch = new ArrayList<ArrayList<PVector>>();
		BCircle bcirc = new BCircle(poly);
		bcirc.r *= 1.05;

		float x0 = bcirc.x - bcirc.r * PApplet.cos(ang);
		float y0 = bcirc.y - bcirc.r * PApplet.sin(ang);

		float x1 = bcirc.x + bcirc.r * PApplet.cos(ang);
		float y1 = bcirc.y + bcirc.r * PApplet.sin(ang);

		float l = new PVector(x0,y0).dist(new PVector(x1,y1));

		int n = (int)Math.ceil(l/d);

		//		ArrayList<PVector> rad = new ArrayList<PVector>();
		//		rad.add(new PVector(x0,y0));
		//		rad.add(new PVector(x1,y1));
		//		pushPolyline(rad,currentFill);

		for (int i = 0; i < n; i++) {
			float t = (float)i/(float)(n-1);
			float x = x0 * (1-t) + x1 * t;
			float y = y0 * (1-t) + y1 * t;


			float px = x + bcirc.r * PApplet.cos(ang-PConstants.HALF_PI);
			float py = y + bcirc.r * PApplet.sin(ang-PConstants.HALF_PI);

			float qx = x + bcirc.r * PApplet.cos(ang+PConstants.HALF_PI);
			float qy = y + bcirc.r * PApplet.sin(ang+PConstants.HALF_PI);


			ArrayList<PVector> ps = segmentIntersectPolygon(new PVector(px,py), new PVector(qx,qy), poly);

			for (int j = 0; j < ps.size()-1; j+=2) {
				ArrayList<PVector> seg = new ArrayList<PVector>();
				seg.add(ps.get(j));
				seg.add(ps.get(j+1));
				hatch.add(seg);
			}
		}

		return hatch;
	}
	
	public ArrayList<ArrayList<PVector>> hatchParallelComplex(ArrayList<ArrayList<PVector>> polys, float ang, float d) {
		ArrayList<ArrayList<PVector>> hatch = new ArrayList<ArrayList<PVector>>();
		BCircle bcirc = new BCircle(polys,0);
		bcirc.r *= 1.05;

		float x0 = bcirc.x - bcirc.r * PApplet.cos(ang);
		float y0 = bcirc.y - bcirc.r * PApplet.sin(ang);

		float x1 = bcirc.x + bcirc.r * PApplet.cos(ang);
		float y1 = bcirc.y + bcirc.r * PApplet.sin(ang);

		float l = new PVector(x0,y0).dist(new PVector(x1,y1));

		int n = (int)Math.ceil(l/d);

		for (int i = 0; i < n; i++) {
			float t = (float)i/(float)(n-1);
			float x = x0 * (1-t) + x1 * t;
			float y = y0 * (1-t) + y1 * t;

			float px = x + bcirc.r * PApplet.cos(ang-PConstants.HALF_PI);
			float py = y + bcirc.r * PApplet.sin(ang-PConstants.HALF_PI);

			float qx = x + bcirc.r * PApplet.cos(ang+PConstants.HALF_PI);
			float qy = y + bcirc.r * PApplet.sin(ang+PConstants.HALF_PI);

			ArrayList<PVector> ps = segmentIntersectPolygons(new PVector(px,py), new PVector(qx,qy), polys);
			if (ps.size() % 2 != 0) {
				//holy shit!
				continue;
			}
			
			for (int j = 0; j < ps.size(); j+=2) {
				ArrayList<PVector> seg = new ArrayList<PVector>();
				seg.add(ps.get(j));
				seg.add(ps.get(j+1));
				hatch.add(seg);
			}
		}

		return hatch;
	}
	
	public ArrayList<ArrayList<PVector>> hatchDrunkWalk(ArrayList<PVector> poly, int rad, int maxIter){
		// this function is for 1 simple polygon (no holes, no multiple polygons)
		
		ArrayList<ArrayList<PVector>> hatch = new ArrayList<ArrayList<PVector>>(); // this is the set of polylines representing the hatches
		hatch.add(new ArrayList<PVector>()); // typically there are >1 polylines, but in this simple example we just need 1, so we add that 1 here
		
		PVector p = randomPointInPolygon(poly); // find a starting point that's inside the polygon
		for (int i = 0; i < maxIter; i++) { // start fumbling
			hatch.get(0).add(p);  // add the point to the polyline
			PVector q = new PVector();
			do {
			  q.x = p.x + app.random(-rad,rad); // for simplicity; technically we should compute from polar coordinates
			  q.y = p.y + app.random(-rad,rad); // app.random() is equivalent random() in a Processing sketch, app refers to the PApplet object
			}while(!pointInPolygon(q,poly));
			p = q;
		}
		return hatch; // done! now go register this hatch method in hatch()
	}
	
	public ArrayList<ArrayList<PVector>> hatchDrunkWalkRaster(PImage im, int rad, int maxIter){
		// for polygons already rendered as an image. this is usually more robust for complex polygons and is the default most of the time
		
		ArrayList<ArrayList<PVector>> contours = PEmbroiderTrace.findContours(im); // find the polygons in the raster image
		ArrayList<ArrayList<PVector>> hatch = new ArrayList<ArrayList<PVector>>(); // this is the set of polylines representing the hatches
		
		im.loadPixels();
		for (int k = 0; k < contours.size(); k++) {
			
			// first find a starting point that's not in a hole
			int trial = 100;
			PVector p = new PVector(); int j;
			for (j = 0; j < trial; j++) {
				p = randomPointInPolygon(contours.get(k));
				if ((im.pixels[(int)p.y*im.width+(int)p.x]>>16&0xFF)>0x7F) {// found white pixel
					break;
				}
			}
			if (j == trial) {
				continue; // couldn't find a white pixel, probably the entire polygon is just a big hole!
			}
			hatch.add(new ArrayList<PVector>());
			for (int i = 0; i < maxIter; i++) { // start fumbling
				hatch.get(hatch.size()-1).add(p);  // add the point to the polyline
				PVector q = new PVector();
				do {
				  q.x = p.x + app.random(-rad,rad); // for simplicity; technically we should compute from polar coordinates
				  q.y = p.y + app.random(-rad,rad); // app.random() is equivalent random() in a Processing sketch, app refers to the PApplet object
				}while((im.pixels[(int)q.y*im.width+(int)q.x]>>16&0xFF)<0x7F); // keep fumbling around until white pixel is found
				p = q;
			}
		}
		return hatch; // done! now go register this hatch method in hatchRaster()
	}
	
	public ArrayList<PVector> resample(ArrayList<PVector> poly, float minLen, float maxLen, float randomize, float randomizeOffset) {
		float maxTurn = 0.2f;
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		if (poly.size() > 0) {
			poly2.add(poly.get(0));
		}else {
			return poly;
		}

		float clen = 0;
		for (int i = 0; i < poly.size()-1; i++) {

			PVector p0 = poly.get(i);
			PVector p1 = poly.get(i+1);

			float l = p0.dist(p1);
			

			
			if (l + clen < minLen && i != poly.size()-2) {
				PVector a = poly.get(i);
				PVector b = poly.get((i+1)%poly.size());
				PVector c = poly.get((i+2)%poly.size());
				PVector u = b.copy().sub(a);
				PVector v = c.copy().sub(b);
				float ang = PApplet.abs(PVector.angleBetween(u, v));
				if (ang<maxTurn) {
					clen += l;
					continue;
				}
			}

			clen = 0;
			if (l<maxLen) {
				poly2.add(p1);
				continue;
			}

			if (i == 0 && randomizeOffset > 0) {
				float rr = app.random(0f,1f);
//				float rr = 0.5f - randomizeOffsetPrevious;
//				randomizeOffsetPrevious = rr;
				float r = (Math.max(1,maxLen*randomizeOffset*rr))/l;
//				    			PApplet.println(r,randomizeOffset);
				PVector p = p0.copy().mult(1-r).add(p1.copy().mult(r));
				poly2.add(p);
				p0 = p;
				l = p0.dist(p1);
				if (l < maxLen) {
					poly2.add(p1);
					continue;
				}
			}
//			PVector p2 = poly.get(i+1).copy();
//			
//			if (i == poly.size()-1 && randomizeOffset > 0) {
//				float rr = app.random(0f,1f);
////				float rr = 0.5f - randomizeOffsetPrevious;
//				float r = (Math.max(1,maxLen*randomizeOffset*rr))/l;
////				    			PApplet.println(r,randomizeOffset);
//				PVector p = p0.copy().mult(r).add(p1.copy().mult(1-r));
////				poly2.add(p);
//				p1 = p;
//			}
//			
//			randomizeOffsetEvenOdd = !randomizeOffsetEvenOdd;

			int n = (int)Math.ceil(l/maxLen);
//			if (!randomizeOffsetEvenOdd) {
//				n = n + 2;
//			}else {
//				n = Math.max(0, n - 2);
//			}
			if (n > 1) {
				float d = l/(float)n;
				float[] lin = new float[n-1];
				for (int j = 1; j < n; j++) {
//					float rr = app.random(-1f,1f);
					float rr = 0;
					if (app.random(0f,1f)<0.5f) {
						rr = app.randomGaussian()-1;
					}else {
						rr = app.randomGaussian()+1;
					}
					rr = Math.min(Math.max(rr, -1), 1);
					lin[j-1] = (float)j/(float)n + rr*randomize*(1f/(float)n)*0.5f;
				}
				for (int j = 0; j < n-1; j++) {
					poly2.add(p0.copy().mult(1-lin[j]).add(p1.copy().mult(lin[j])));
				}
			}
			poly2.add(p1);
			
//			if (i == poly.size()-1 && randomizeOffset > 0) {
//				
//				poly2.add(p2);
//			}
		}
		
		return poly2;
	}

	public ArrayList<PVector> resampleN(ArrayList<PVector> poly, int n){
		if (poly.size() <= 0) {
			return poly;
		}

		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		float l = 0;
		for (int i = 0; i < poly.size()-1; i++) {
			l += poly.get(i).dist(poly.get(i+1));
		}
		float d = l/(float)n;
		float clen = 0;

		poly2.add(poly.get(0));

		for (int i = 0; i < poly.size()-1; i++) {
			PVector p0 = poly.get(i);
			PVector p1 = poly.get(i+1);
			float d0 = p0.dist(p1);
			float pc = (d-clen)/(d0);

			PVector p2 = p0.copy().mult(1-pc).add(p1.copy().mult(pc));

			float d1 = d0;
			while (pc < 1) {
				poly2.add(p2);
				d1 = d1-(d-clen);
				clen = 0;
				pc = d/d1;
				p2 = p2.copy().mult(1-pc).add(p1.copy().mult(pc));

			}
			clen += d1;
		}    	
		if (poly2.size() < n) {
			poly2.add(poly.get(poly.size()-1));
		}
		if (poly2.size() > n) {
//			PApplet.println("???",poly2.size(),n);
			float ml = Float.POSITIVE_INFINITY;
			int mi = -1;
			for (int i = 0; i < poly2.size()-1; i++) {
				float ll = poly2.get(i).dist(poly2.get(i+1));
				if (ll < ml) {
					ml = ll;
					mi = i;
				}
			}
			if (mi != -1) {
				poly2.remove(mi);
			}
		}
		return poly2;
	}

	public ArrayList<PVector> resampleNKeepVertices(ArrayList<PVector> poly, int n){
		if (poly.size() <= 0) {
			return poly;
		}

		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		ArrayList<Float> dists = new ArrayList<Float>();
		ArrayList<Integer> ns = new ArrayList<Integer>();

		float l = 0;
		for (int i = 0; i < poly.size()-1; i++) {
			float d = poly.get(i).dist(poly.get(i+1));
			l += d;
			dists.add(d);
		}

		int nr = n;
		for (int i = 0; i < poly.size()-2; i++) {
			int n0 = (int)((float)n * dists.get(i)/l);
			nr -= n0;
			ns.add(n0);
		}

		ns.add(nr);
		poly2.add(poly.get(0));
		for (int i = 0; i < poly.size()-1; i++) {
			int n0 = ns.get(i);
			for (int j = 0; j < n0; j++) {
				float t = (float)(j+1)/(float)n0;
				PVector p = poly.get(i).copy().mult(1-t).add(poly.get(i+1).copy().mult(t));
				poly2.add(p);
			}
		}

		PApplet.println(poly2.size(),n);
		return poly2;
	}
	
	public ArrayList<ArrayList<PVector>> resampleCrossIntersection(ArrayList<ArrayList<PVector>> polys, float angle, float spacing, float len){
//		for (int i = 0; i < polys.size(); i++) {
//			for (int j = 0; j < polys.get(i).size(); j++) {
//				PApplet.println(polys.get(i).get(j));
//				
//			}
//		}
		float base = len/2;
		float relang = PApplet.atan2(spacing, base);
//		float d = PApplet.sqrt(base*base+spacing*spacing);
		float d = len * PApplet.cos(PApplet.HALF_PI-relang);	
		
		PApplet.println("computed cross ang",relang,"spacing",d);
		
		float ang = angle - relang;
		
		BCircle bcirc = new BCircle(polys,0);
		bcirc.r *= 1.05;

		float x0 = bcirc.x - bcirc.r * PApplet.cos(ang);
		float y0 = bcirc.y - bcirc.r * PApplet.sin(ang);

		float x1 = bcirc.x + bcirc.r * PApplet.cos(ang);
		float y1 = bcirc.y + bcirc.r * PApplet.sin(ang);

		float l = new PVector(x0,y0).dist(new PVector(x1,y1));

		int n = (int)Math.ceil(l/d);

		ArrayList<ArrayList<PVector>> crosslines = new ArrayList<ArrayList<PVector>>();
		
		for (int i = 0; i < n; i++) {
			float t = (float)i/(float)(n-1);
			float x = x0 * (1-t) + x1 * t;
			float y = y0 * (1-t) + y1 * t;

			float px = x + bcirc.r * PApplet.cos(ang-PConstants.HALF_PI);
			float py = y + bcirc.r * PApplet.sin(ang-PConstants.HALF_PI);

			float qx = x + bcirc.r * PApplet.cos(ang+PConstants.HALF_PI);
			float qy = y + bcirc.r * PApplet.sin(ang+PConstants.HALF_PI);

//			PApplet.println(x0,y0,x1,y1,px,py,qx,qy);
			
			ArrayList<PVector> crsl = new ArrayList<PVector>();
			crsl.add(new PVector(px,py));
			crsl.add(new PVector(qx,qy));
			crosslines.add(crsl);
		}
		
		
		ArrayList<ArrayList<PVector>> result = new ArrayList<ArrayList<PVector>>();
		
		for (int i = 0; i < polys.size(); i++) {
			if (polys.get(i).size() < 2) {
				continue;
			}

			ArrayList<PVector> resamped = new ArrayList<PVector>();
			
			for (int j = 0; j < polys.get(i).size()-1; j++) {
				
				PVector a = polys.get(i).get(j);
				PVector b = polys.get(i).get(j+1);
				
				resamped.add(a);
				
				ArrayList<Float> iparams = new ArrayList<Float>();
				for (int k = 0; k < crosslines.size(); k++) {
					PVector p = crosslines.get(k).get(0);
					PVector q = crosslines.get(k).get(1);
					PVector o = segmentIntersect3D(a,b,p,q);
					if (o != null) {
						iparams.add(o.x);
					}
				}
				float[] iparamsArr = new float[iparams.size()];
				for (int k = 0; k < iparams.size(); k++) {
					iparamsArr[k] = (float)iparams.get(k);
				}
				iparamsArr = PApplet.sort(iparamsArr);
				
				for (int k = 0; k < iparams.size(); k++) {
					PVector _p0 = a.copy();
					PVector _p1 = b.copy();
					resamped.add( _p0.mult(1-iparamsArr[k]).add(_p1.mult(iparamsArr[k])) );
				}
				resamped.add(b);
			}
			
			result.add(resamped);
			
		}
		return result;
	}


	void _ellipse(float cx, float cy, float rx, float ry) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		for (int i = 0; i < CIRCLE_DETAIL; i++) {
			float a = ((float)i/(float)CIRCLE_DETAIL)*PConstants.PI*2;
			float x = cx + rx * PApplet.cos(a);
			float y = cy + ry * PApplet.sin(a);
			poly.add(new PVector(x,y));
		}
		polyBuff.clear();
		polyBuff.add(poly);
		endShape(true);
	}
	void _rect(float x, float y, float w, float h) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x,y));
		poly.add(new PVector(x+w,y));
		poly.add(new PVector(x+w,y+h));
		poly.add(new PVector(x,y+h));
		polyBuff.clear();
		polyBuff.add(poly);
		endShape(true);
	}

	/* SHAPE INTERFACE */
	
	public void hatch(ArrayList<PVector> poly) {
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		if (HATCH_MODE == PARALLEL) {
			polys = hatchParallel(poly,HATCH_ANGLE,HATCH_SPACING);
		}else if (HATCH_MODE == CROSS) {
			polys = hatchParallel(poly,HATCH_ANGLE, HATCH_SPACING);
			polys.addAll(hatchParallel(poly,HATCH_ANGLE2,HATCH_SPACING));
		}else if (HATCH_MODE == CONCENTRIC) {
			polys = hatchInset(poly,HATCH_SPACING,9999);
			for (int i = 0; i < polys.size(); i++) {
				polys.get(i).add(polys.get(i).get(0));
			}
		}else if (HATCH_MODE == SPIRAL) {
			polys = hatchSpiral(poly,HATCH_SPACING,9999);
		}else if (HATCH_MODE == PERLIN) {
			polys = hatchPerlin(poly,HATCH_SPACING,STITCH_LENGTH,HATCH_SCALE,9999);
		}else if (HATCH_MODE == VECFIELD) {
			polys = hatchCustomField(poly,HATCH_VECFIELD,HATCH_SPACING,STITCH_LENGTH,9999);
		}else if (HATCH_MODE == DRUNK) {
			polys = hatchDrunkWalk(poly,10,999);
		}
		for (int i = 0; i < polys.size(); i++) {
			pushPolyline(polys.get(i),currentFill,1f);
		}
	}
	
	public void hatchRaster(PImage im, float x, float y) {
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		if (HATCH_MODE == PARALLEL) {
			polys = hatchParallelRaster(im,HATCH_ANGLE,HATCH_SPACING,STITCH_LENGTH/4);
		}else if (HATCH_MODE == CROSS) {
			polys = hatchParallelRaster(im,HATCH_ANGLE,HATCH_SPACING,STITCH_LENGTH/4);
			polys.addAll(hatchParallelRaster(im,HATCH_ANGLE2,HATCH_SPACING,STITCH_LENGTH/4));
			
		}else if (HATCH_MODE == CONCENTRIC) {
			polys = isolines(im,HATCH_SPACING);
		}else if (HATCH_MODE == SPIRAL) {
			polys = isolines(im,HATCH_SPACING);
		}else if (HATCH_MODE == PERLIN) {
			polys = perlinField(im, HATCH_SPACING, 0.01f*HATCH_SCALE, STITCH_LENGTH, 3, 100, 9999);
		}else if (HATCH_MODE == VECFIELD) {
			polys = customField(im,HATCH_VECFIELD,HATCH_SPACING,3,100,9999);
		}else if (HATCH_MODE == DRUNK) {
			polys = hatchDrunkWalkRaster(im,10,999);
		}
		for (int i = 0; i < polys.size(); i++) {
			for (int j = 0; j < polys.get(i).size(); j++) {
				polys.get(i).get(j).x += x;
				polys.get(i).get(j).y += y;
			}
			pushPolyline(polys.get(i),currentFill,1f);
		}
	}
	public void hatchRaster(PImage im) {
		hatchRaster(im,0,0);
	}
	
	
	PVector rationalQuadraticBezier(PVector p0, PVector p1, PVector p2, float w, float t) {
		// intro: https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Rational_B%C3%A9zier_curves
		// ported from: http://okb.glitch.me/Okb.js
		float u = (PApplet.pow (1 - t, 2) + 2 * t * (1 - t) * w + t * t);
		return new PVector(
		          (PApplet.pow(1-t,2)*p0.x+2*t*(1-t)*p1.x*w+t*t*p2.x)/u,
		          (PApplet.pow(1-t,2)*p0.y+2*t*(1-t)*p1.y*w+t*t*p2.y)/u,
		          (PApplet.pow(1-t,2)*p0.z+2*t*(1-t)*p1.z*w+t*t*p2.z)/u
		);
	}
	PVector quadraticBezier(PVector p0, PVector p1, PVector p2, float t) {
		return p0.copy().mult(PApplet.pow(1-t,2)).add(p1.copy().mult(2*(1-t)*t)).add(p2.copy().mult(t*t));
	}
	PVector cubicBezier(PVector p0, PVector p1, PVector p2, PVector p3, float t) {
		return p0.copy().mult(PApplet.pow(1-t, 3)).add(p1.copy().mult(t*3*PApplet.pow(1-t, 2))).add(p2.copy().mult(3*(1-t)*t*t)).add(p3.copy().mult(t*t*t));
	}
	
	PVector highBezier(ArrayList<PVector> P, float t) {
		// ported from: http://okb.glitch.me/Okb.js
		if (P.size() == 1) {
			return P.get(0);
		}else if (P.size() == 2) {
			return P.get(0).copy().lerp(P.get(1), t);
		}else {
			return highBezier(new ArrayList<PVector>(P.subList(0,P.size()-1)),t).lerp(highBezier(new ArrayList<PVector>(P.subList(1, P.size())),t),t);
		}
	}

	public void ellipse(float a, float b, float c, float d) {
		if (ELLIPSE_MODE == PConstants.CORNER) {
			_ellipse(a + c / 2, b + d / 2, c / 2, d / 2);
		} else if (ELLIPSE_MODE == PConstants.CORNERS) {
			_ellipse((a + c) / 2, (b + d) / 2, (c - a) / 2, (d - b) / 2);
		} else if (ELLIPSE_MODE == PConstants.CENTER) {
			_ellipse(a, b, c / 2, d / 2);
		} else if (ELLIPSE_MODE == PConstants.RADIUS) {
			_ellipse(a, b, c, d);
		}
	}


	public void rect(float a, float b, float c, float d) {
		if (ELLIPSE_MODE == PConstants.CORNER) {
			_rect(a, b, c, d);
		} else if (ELLIPSE_MODE == PConstants.CORNERS) {
			_rect(a, b, c-a, d-b);
		} else if (ELLIPSE_MODE == PConstants.CENTER) {
			_rect(a-c/2, b-d/2, c, d);
		} else if (ELLIPSE_MODE == PConstants.RADIUS) {
			_rect(a-c, b-d, c*2, d*2);
		}
	}

	public void line(float x0, float y0, float x1, float y1) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x0,y0));
		poly.add(new PVector(x1,y1));
		polyBuff.clear();
		polyBuff.add(poly);
		endShape();
	}

	public void quad(float x0, float y0, float x1, float y1,
			float x2, float y2, float x3, float y3) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x0,y0));
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		poly.add(new PVector(x3,y3));
		polyBuff.clear();
		polyBuff.add(poly);
		endShape(true);
	}

	public void triangle(float x0, float y0, float x1, float y1,
			float x2, float y2) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x0,y0));
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		
		polyBuff.clear();
		polyBuff.add(poly);
		endShape(true);
	}


	public void beginShape() {
		if (polyBuff == null) {
			polyBuff = new ArrayList<ArrayList<PVector>> ();
		}
		polyBuff.clear();
		polyBuff.add(new ArrayList<PVector>());
	}
	public void vertex(float x, float y) {
		
		polyBuff.get(polyBuff.size()-1).add(new PVector(x,y));
		
	}
	public void bezierVertex(float x1, float y1, float x2, float y2, float x3, float y3) {
		cubicVertex(x1,y1,x2,y2,x3,y3);
	}
	public void rationalVertex(float x1, float y1, float x2, float y2, float w) {
		PVector p0 = polyBuff.get(polyBuff.size()-1).get(polyBuff.get(polyBuff.size()-1).size()-1);
		for (int i = 1; i < BEZIER_DETAIL; i++) {
			float t = (float)i/(float)(BEZIER_DETAIL-1);
			PVector p = rationalQuadraticBezier(p0, new PVector(x1,y1), new PVector(x2,y2), w, t);
			polyBuff.get(polyBuff.size()-1).add(p);
		}
	}
	public void quadraticVertex(float x1, float y1, float x2, float y2) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		highBezierVertex(poly);
	}
	public void cubicVertex(float x1, float y1, float x2, float y2, float x3, float y3) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		poly.add(new PVector(x3,y3));
		highBezierVertex(poly);
	}
	public void quarticVertex(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		poly.add(new PVector(x3,y3));
		poly.add(new PVector(x4,y4));
		highBezierVertex(poly);
	}
	public void quinticVertex(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float x5, float y5) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		poly.add(new PVector(x3,y3));
		poly.add(new PVector(x4,y4));
		poly.add(new PVector(x5,y5));
		highBezierVertex(poly);
	}

	public void highBezierVertex(ArrayList<PVector> poly) {
		PVector p0 = polyBuff.get(polyBuff.size()-1).get(polyBuff.get(polyBuff.size()-1).size()-1);
		for (int i = 1; i < BEZIER_DETAIL; i++) {
			float t = (float)i/(float)(BEZIER_DETAIL-1);
			PVector p;
			if (poly.size() == 2) {
				p = quadraticBezier(p0, poly.get(0), poly.get(1), t);
			}else if (poly.size() == 3) {
				p = cubicBezier(p0, poly.get(0), poly.get(1), poly.get(2), t);
			}else {
				poly.add(0,p0);
				p = highBezier(poly, t);
			}
			polyBuff.get(polyBuff.size()-1).add(p);
		}
	}

	

	public void endShape(boolean close) {
		if (polyBuff.size() == 0) {
			return;
		}
		if (polyBuff.size() == 1 && (HATCH_BACKEND == FORCE_VECTOR || HATCH_MODE == SPIRAL)) {
			if (isStroke && FIRST_STROKE_THEN_FILL) {
				_stroke(polyBuff,close);
			}
			if (isFill) {
				hatch(polyBuff.get(0));
			}
			if (isStroke && !FIRST_STROKE_THEN_FILL) {
				_stroke(polyBuff,close);
//				if (close) {
//					polyBuff.get(0).add(polyBuff.get(0).get(0));
//				}
//				pushPolyline(polyBuff.get(0),currentStroke);
			}
			
		}else {
			if (isStroke && FIRST_STROKE_THEN_FILL) {
//				_stroke(polyBuff,close);
				
				_stroke((ArrayList<ArrayList<PVector>>)deepClone(polyBuff),close);

//				for (int i = 0; i < polyBuff.size(); i++) {
//					if (close) {
//						polyBuff.get(i).add(polyBuff.get(i).get(0));
//					}
//					pushPolyline(polyBuff.get(i),currentStroke);
//				}
			}
			if (isFill) {
				if ((HATCH_MODE == PARALLEL || HATCH_MODE == CROSS) && (HATCH_BACKEND != FORCE_RASTER)) {
					
					ArrayList<ArrayList<PVector>> polys = hatchParallelComplex(polyBuff,HATCH_ANGLE,HATCH_SPACING);
					
					boolean didit = false;
					if (HATCH_MODE == PARALLEL && !NO_RESAMPLE) {
						polys = resampleCrossIntersection(polys,HATCH_ANGLE,HATCH_SPACING,STITCH_LENGTH);
						NO_RESAMPLE = true;
						didit = true;
					}
					
					if (HATCH_MODE == CROSS) {
						polys.addAll(hatchParallelComplex(polyBuff,HATCH_ANGLE2,HATCH_SPACING));
					}
					for (int i = 0; i < polys.size(); i++) {
						pushPolyline(polys.get(i),currentFill,1f);
					}
					if (didit) {
						NO_RESAMPLE = false;
					}
				}else {
					BBox bb = new BBox(polyBuff,0);
					bb.x -= 10;
					bb.y -= 10;
					bb.w += 20;
					bb.h += 20;
					PGraphics pg = app.createGraphics((int)bb.w, (int)bb.h);
					pg.beginDraw();
					pg.background(0);
					pg.noStroke();
					pg.fill(255);
					pg.beginShape();
					for (int i = 0; i < polyBuff.size(); i++) {
						if (i > 0) {
							pg.beginContour();
						}
						for (int j = 0; j < polyBuff.get(i).size(); j++) {
							pg.vertex(polyBuff.get(i).get(j).x -bb.x,polyBuff.get(i).get(j).y -bb.y);
						}
						if (i > 0) {
							pg.endContour();
						}
					}
					pg.endShape();
					pg.endDraw();
					hatchRaster(pg, bb.x, bb.y);
				}
			}
			if (isStroke && !FIRST_STROKE_THEN_FILL) {
				_stroke((ArrayList<ArrayList<PVector>>)deepClone(polyBuff),close);
			}
		}
		currentCullGroup++;

	}
	public void endShape() {
		endShape(false);
	}
	public void endShape(int close) {
		endShape(true);
	}

	public void beginContour() {
		polyBuff.add(new ArrayList<PVector>());
	}
	
	public void endContour() {
		// it's ok
	}
	
	public void circle(float x, float y, float r){
		ellipse(x,y,r,r);
	}

	/* MATRIX WRAPPERS */

	public void pushMatrix() {
		matStack.add(new PMatrix2D());
	}

	public void popMatrix() {
		matStack.remove(matStack.size() - 1);
	}

	public void translate(float x, float y) {
		matStack.get(matStack.size() - 1).translate(x, y);
	}

	public void rotate(float a) {
		matStack.get(matStack.size() - 1).rotate(a);
	}

	public void shearX(float x) {
		matStack.get(matStack.size() - 1).shearX(x);
	}

	public void shearY(float x) {
		matStack.get(matStack.size() - 1).shearY(x);
	}

	public void scale(float x) {
		matStack.get(matStack.size() - 1).scale(x);
	}
	
	public void scale(float x, float y) {
		matStack.get(matStack.size() - 1).scale(x,y);
	}


	/* VISUALIZE */

	public void visualize(boolean color, boolean stitches, boolean route) {
		for (int i = 0; i < polylines.size(); i++) {
			if (color) {
				app.stroke(app.red(colors.get(i)),app.green(colors.get(i)),app.blue(colors.get(i)));	
			}else if (stitches){
				app.stroke(0);
			}else {
				app.stroke(app.random(200),app.random(200),app.random(200));
			}
			for (int j = 0; j < polylines.get(i).size()-1; j++) {
				PVector p0 = polylines.get(i).get(j);
				PVector p1 = polylines.get(i).get(j+1);

				app.strokeWeight(1);
				app.line(p0.x,p0.y,p1.x,p1.y);
			}
		}
		for (int i = 0; i < polylines.size(); i++) {
			if (route) {
				if (i != 0 && polylines.get(i-1).size() > 0 && polylines.get(i).size() > 0) {
					app.stroke(255,0,0);
					app.strokeWeight(1);
					PVector p0 = polylines.get(i-1).get(polylines.get(i-1).size()-1);
					PVector p1 = polylines.get(i).get(0);
					app.line(p0.x,p0.y,p1.x,p1.y);
				}
			}
			if (stitches) {
				for (int j = 0; j < polylines.get(i).size()-1; j++) {
					PVector p0 = polylines.get(i).get(j);
					PVector p1 = polylines.get(i).get(j+1);
					app.noStroke();
					if (j == 0) {
						app.fill(0,255,0);
						app.rect(p0.x-1,p0.y-1,2,2);
					}
					app.fill(255,0,255);
					app.rect(p1.x-1,p1.y-1,2,2);
	
				}
			}
		}
	}
	public void visualize() {
		visualize(false,true,false);
	}



	/* IO */

	public void beginDraw() {

	}

	public void endDraw() {
		if (polylines.size() < 1) {
			return;
		}
		PEmbroiderWriter.write(path, polylines, colors, width, height);
	}


	public ArrayList<PVector> resampleDouble(ArrayList<PVector> poly) {
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		if (poly.size() > 0) {
			poly2.add(poly.get(0));
		}
		for (int i = 0; i < poly.size()-1; i++) {
			PVector p0 = poly.get(i);
			PVector p1 = poly.get(i+1);
			PVector p2 = p0.copy().mult(0.5f).add(p1.copy().mult(0.5f));
			poly2.add(p2);
			poly2.add(p1);
		}
		return poly2;
	}
	public ArrayList<PVector> resampleHalf(ArrayList<PVector> poly) {
		ArrayList<PVector> poly2 = new ArrayList<PVector>();

		for (int i = 0; i < poly.size()-2; i+=2) {
			PVector p1 = poly.get(i);
			poly2.add(p1);
		}
		if (poly2.size()*2 < poly.size()) {
			poly2.add(poly.get(poly.size()-1));
		}
		if (poly2.get(poly2.size()-1) != poly.get(poly.size()-1)) {
			poly2.add(poly.get(poly.size()-1));
		}
		return poly2;
	}
	public ArrayList<PVector> resampleHalfKeepCorners(ArrayList<PVector> poly, float maxTurn){
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		for (int i = 0; i < poly.size(); i++) {
			PVector p1 = poly.get(i);
			if (i % 2 == 0 || i == poly.size()-1) {
				poly2.add(p1);
				continue;
			}
			PVector a = poly.get((i-1+poly.size())%poly.size());
			PVector b = poly.get(i);
			PVector c = poly.get((i+1)%poly.size());
			PVector u = b.copy().sub(a);
			PVector v = c.copy().sub(b);
			float ang = PApplet.abs(PVector.angleBetween(u, v));
			if (ang > PConstants.PI) {
				ang = PConstants.TWO_PI - ang;
			}
			if (ang > maxTurn) {
				poly2.add(p1);
			}
		}
		if (poly2.get(poly2.size()-1) != poly.get(poly.size()-1)) {
			poly2.add(poly.get(poly.size()-1));
		}
		return poly2;
	}


	public void beginCull() {
		beginCullIndex = cullGroups.size();
	}

	public void endCull() {
		if (beginCullIndex >= cullGroups.size()) {
			return;
		}

		ArrayList<ArrayList<ArrayList<PVector>>> groups = new ArrayList<ArrayList<ArrayList<PVector>>>();
		ArrayList<ArrayList<Integer>> groupColors = new ArrayList<ArrayList<Integer>>();

		int last = cullGroups.get(beginCullIndex);
		groups.add(new ArrayList<ArrayList<PVector>>());
		groups.get(groups.size()-1).add(resampleDouble(polylines.get(beginCullIndex)));

		groupColors.add(new ArrayList<Integer>());
		groupColors.get(groupColors.size()-1).add(colors.get(beginCullIndex));

		for (int i = beginCullIndex+1; i < polylines.size(); i++) {

			if (!cullGroups.get(i).equals(last)) {
				groups.add(new ArrayList<ArrayList<PVector>>());
				groupColors.add(new ArrayList<Integer>());
			}
			groups.get(groups.size()-1).add(resampleDouble(polylines.get(i)));
			groupColors.get(groupColors.size()-1).add(colors.get(i));
			last = cullGroups.get(i);
		}


		PGraphics[] channels = new PGraphics[groups.size()];
		for (int i = 0; i < groups.size(); i++) {
			channels[i] = app.createGraphics(width,height);
			channels[i].beginDraw();
			channels[i].background(0);
			channels[i].stroke(255);
			channels[i].strokeWeight(5);
			channels[i].strokeJoin(PConstants.ROUND);
			channels[i].noFill();

			for (int j = 0; j < groups.get(i).size(); j++) {
				channels[i].beginShape();
				for (int k = 0; k < groups.get(i).get(j).size(); k++) {
					channels[i].vertex(groups.get(i).get(j).get(k).x, groups.get(i).get(j).get(k).y);
				}
				channels[i].endShape();
			}
			channels[i].endDraw();
		}

		for (int i = 0; i < groups.size()-1; i++) {

			ArrayList<ArrayList<PVector>> startStubs = new ArrayList<ArrayList<PVector>>();
			ArrayList<ArrayList<PVector>> endStubs   = new ArrayList<ArrayList<PVector>>();
			for (int j = 0; j < groups.get(i).size(); j++) {
				startStubs.add(new ArrayList<PVector>());
				endStubs.add(  new ArrayList<PVector>());
			}

			int j = 0;
			while (j < groups.get(i).size()) {

				for (int k = 0; k < groups.get(i).get(j).size(); k++) {
					PVector p = groups.get(i).get(j).get(k);
					boolean ok = true;
					for (int l = i+1; l < groups.size(); l++) {
						int col = channels[l].get((int)p.x, (int)p.y);
						int r = col >> 16 & 0xFF;
					if (r > 250) {
						ok = false;
						break;
					}
					}
					if (!ok) {
						ArrayList<PVector> lhs = new ArrayList<PVector>(groups.get(i).get(j).subList(0, k));
						ArrayList<PVector> rhs = new ArrayList<PVector>(groups.get(i).get(j).subList(k+1,groups.get(i).get(j).size()));

						ArrayList<PVector> s0 = new ArrayList<PVector>(groups.get(i).get(j).subList(k, PApplet.min(k+1, groups.get(i).get(j).size())));
						ArrayList<PVector> s1 = new ArrayList<PVector>(groups.get(i).get(j).subList(PApplet.max(k-3,0), k+1));

						groups.get(i).remove(j);
						groups.get(i).add(j,rhs); // rhs first
						groups.get(i).add(j,lhs);

						groupColors.get(i).add(j,groupColors.get(i).get(j));

						startStubs.remove(j);
						endStubs.remove(j);


						startStubs.add(j,s1);
						endStubs.add(j,new ArrayList<PVector>());

						startStubs.add(j,new ArrayList<PVector>());
						endStubs.add(j,s0);



						break;
					}
				}
				j++;
			}

			for (int k = groups.get(i).size()-1; k >= 0; k--) {
				if (groups.get(i).get(k).size() < 2) {
					groups.get(i).remove(k);
					groupColors.get(i).remove(k);
					continue;
				}
				startStubs.get(k).addAll(groups.get(i).get(k));
				startStubs.get(k).addAll(endStubs.get(k));
				groups.get(i).set(k, startStubs.get(k));

			}

		}

		ArrayList<ArrayList<PVector>> polylines2 = new ArrayList<ArrayList<PVector>>();
		ArrayList<Integer> colors2 = new ArrayList<Integer>();
		ArrayList<Integer> cullGroups2 = new ArrayList<Integer>();

		for (int i = 0; i < groups.size(); i++) {
			//			PApplet.println(groups.get(i).size());
			for (int j = 0; j < groups.get(i).size(); j++) {
				//				PApplet.println(groups.get(i).get(j));
				polylines2.add(groups.get(i).get(j));
				colors2.add(groupColors.get(i).get(j));
				cullGroups2.add(currentCullGroup);
			}
		}

		for (int i = 0; i < polylines2.size(); i++) {
			polylines2.set(i, resampleHalfKeepCorners(polylines2.get(i),PConstants.PI*0.1f));
		}

		polylines.subList(beginCullIndex,polylines.size()).clear();
		colors.subList(beginCullIndex,colors.size()).clear();
		cullGroups.subList(beginCullIndex,cullGroups.size()).clear();

		polylines.addAll(polylines2);
		colors.addAll(colors2);
		cullGroups.addAll(cullGroups2);

		currentCullGroup ++;

	}


	public void image(PImage im, int x, int y, int w, int h) {
		PGraphics im2  = app.createGraphics(w,h);
		im2.beginDraw();
		im2.image(im,0,0,w,h);
		im2.filter(PConstants.INVERT);
		im2.filter(PConstants.THRESHOLD);
	
		
		ArrayList<ArrayList<PVector>> polys = PEmbroiderTrace.findContours(im2);

		for (int i = polys.size()-1; i >= 0; i--) {
			if (polys.get(i).size() < 3) {
				polys.remove(i);
				continue;
			}

//			polys.set(i,resampleHalf(resampleHalf(resampleHalf(polys.get(i)))));
			polys.set(i, PEmbroiderTrace.approxPolyDP(polys.get(i),1));

		}

	
		if (isStroke && FIRST_STROKE_THEN_FILL) {
			_stroke(polys, true);
		}
		if (isFill) {
			hatchRaster(im2);
		}
		if (isStroke && !FIRST_STROKE_THEN_FILL) {
			_stroke(polys, true);
		}
	}

	public ArrayList<ArrayList<PVector>> perlinField(PImage mask, float d, 
			float perlinScale /*0.01f*/, float deltaX /*20*/, int minVertices /*2*/, int maxVertices /*100*/, int maxIter) {

		class PerlinVectorField implements VectorField{
			public PVector get(float x, float y) {
				float a = app.noise(x*perlinScale,y*perlinScale,1f)*2*PApplet.PI-PApplet.PI;
				float r = app.noise(x*perlinScale,y*perlinScale,2f)*deltaX;
				float dx = PApplet.cos(a)*r;
				float dy = PApplet.sin(a)*r;
				return new PVector(dx,dy);

			}
		}
		PerlinVectorField vf = new PerlinVectorField();
		return customField(mask,vf,d,minVertices,maxVertices,maxIter);
	}

	public interface VectorField{
		PVector get(float x, float y);
	}

	public ArrayList<ArrayList<PVector>> customField(PImage mask, VectorField vf, float d, int minVertices, int maxVertices, int maxIter){
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		PGraphics pg = app.createGraphics(mask.width,mask.height);
		pg.beginDraw();
		pg.background(255);
		pg.image(mask,0,0);
		pg.noFill();
		pg.stroke(0);
		pg.strokeWeight(d);

		for (int i = 0; i < maxIter; i++) {
			float x = app.random(pg.width);
			float y = app.random(pg.height);
			if ((pg.get((int)x,(int)y)>>16&0xFF)<128) {
				continue;
			}
			ArrayList<PVector> poly = new ArrayList<PVector>();
			for (int j = 0; j < maxVertices; j++) {
				poly.add(new PVector(x,y));
				PVector v = vf.get(x, y);
				x += v.x;
				y += v.y;
				if (x < 0 || x >= pg.width || y < 0 || y >= pg.height) {
					break;
				}
				if ((pg.get((int)x,(int)y)>>16&0xFF)<128) {
					break;
				}
			}
			if (poly.size() < minVertices) {
				continue;
			}
			pg.beginShape();
			for (int j = 0; j < poly.size();j ++) {
				pg.vertex(poly.get(j).x, poly.get(j).y);
			}
			pg.endShape();
			polys.add(poly);

		}
		pg.endDraw();

		return polys;
	}
	
	public ArrayList<ArrayList<PVector>> hatchParallelRaster(PImage mask, float ang, float d, float step) {
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		
		float r = PApplet.sqrt(mask.width*mask.width+mask.height*mask.height)*1.05f;
		BCircle bcirc = new BCircle(mask.width/2,mask.height/2,r);

		float x0 = bcirc.x - bcirc.r * PApplet.cos(ang);
		float y0 = bcirc.y - bcirc.r * PApplet.sin(ang);

		float x1 = bcirc.x + bcirc.r * PApplet.cos(ang);
		float y1 = bcirc.y + bcirc.r * PApplet.sin(ang);

		float l = new PVector(x0,y0).dist(new PVector(x1,y1));

		int n = (int)Math.ceil(l/d);
		
		for (int i = 0; i < n; i++) {
			float t = (float)i/(float)(n-1);
			float x = x0 * (1-t) + x1 * t;
			float y = y0 * (1-t) + y1 * t;


			float px = x + bcirc.r * PApplet.cos(ang-PConstants.HALF_PI);
			float py = y + bcirc.r * PApplet.sin(ang-PConstants.HALF_PI);

			float qx = x + bcirc.r * PApplet.cos(ang+PConstants.HALF_PI);
			float qy = y + bcirc.r * PApplet.sin(ang+PConstants.HALF_PI);
			
			int m = (int)PApplet.ceil(bcirc.r / step);
			
			boolean prev = false;
			Float lx = null;
			Float ly = null;
			for (int j = 0; j <= m; j++) {
				float s = (float)j/(float)m;
				float xx = px * (1-s) + qx * s;
				float yy = py * (1-s) + qy * s;
				boolean add = false;
				if (((mask.get((int)xx,(int)yy)>>16)&0xFF) > 127) {
					if (!prev) {
						polys.add(new ArrayList<PVector>());
						add = true;
					}
					prev = true;
				}else {
					if (prev) {
						add = true;
					}
					prev = false;
				}
				if (add) {
					if (lx == null || ly == null) {
						polys.get(polys.size()-1).add(new PVector(xx,yy));
					}else {
						polys.get(polys.size()-1).add(new PVector((lx+xx)/2f,(ly+yy)/2f));
					}
				}
				lx = xx;
				ly = yy;
			}
		}
		return polys;
	}
	
    public float lerp360 (float h0,float h1,float t){
        float[][] methods = new float[][] {
          {Math.abs(h1-h0),     PApplet.map(t,0,1,h0,h1)},
          {Math.abs(h1+360-h0), PApplet.map(t,0,1,h0,h1+360)},
          {Math.abs(h1-360-h0), PApplet.map(t,0,1,h0,h1-360)}
        };
        for (int i = 0; i < methods.length; i++) {
        	boolean best = true;
        	for (int j = 0; j < methods.length; j++) {
        		if (i == j) {
        			continue;
        		}
        		if (methods[j][0]<methods[i][0]) {
        			best = false;
        			break;
        		}
        	}
        	if (best) {
        		PApplet.println(h0,h1,i);
        		return (methods[i][1]+720)%360;
        	}
        }
        return -1; // impossible case, just to shut java up
     }
	
	 public ArrayList<PVector> smoothen(ArrayList<PVector> poly, float w, int n) {
		 if (poly.size()<3) {
			 return poly;
		 }
		 ArrayList<PVector> poly2 = new ArrayList<PVector>();
		 for (int j = 0; j < poly.size(); j++) {
			 PVector p0 = poly.get(j).copy().lerp(poly.get((j+1)%poly.size()),0.5f);
			 PVector p1 = poly.get((j+1)%poly.size());
			 PVector p2 = poly.get((j+1)%poly.size()).copy().lerp(poly.get((j+2)%poly.size()),0.5f);
			 for (int i = 0; i < n; i ++) {
				 float t = (float)i/n;
				 poly2.add(rationalQuadraticBezier(p0,p1,p2,w,t));
			 }
		 }
		 return poly2;
	 }
	 
	 
	 public float[] perfectDistanceTransform(ArrayList<ArrayList<PVector>> polys,int w, int h) {
		 float[] dt = new float[w*h];
		 for (int i = 0; i < h; i++) {
			 for (int j = 0; j < w; j++) {
				 PVector p = new PVector(j,i);
				 float md = w*h;
				 for (int k = 0; k < polys.size(); k++) {
					 for (int l = 0; l < polys.get(k).size(); l++) {
						 PVector p0 = polys.get(k).get(l);
						 PVector p1 = polys.get(k).get((l+1)%polys.get(k).size());
						 float d = pointDistanceToSegment(p,p0,p1);
						 if (d < md) {
							 md = d;
						 }
					 }
				 }
				 dt[i*w+j]=md;
			 }
		 }
		 return dt;
	 }
	 
	 
	 public void clip(ArrayList<ArrayList<PVector>> polys, PImage mask) { 
		 for (int i = 0; i < polys.size(); i++) {
			 polys.set(i,resample(polys.get(i),2,2,0,0));
		 }
		ArrayList<ArrayList<PVector>> startStubs = new ArrayList<ArrayList<PVector>>();
		ArrayList<ArrayList<PVector>> endStubs   = new ArrayList<ArrayList<PVector>>();
		for (int j = 0; j < polys.size(); j++) {
			startStubs.add(new ArrayList<PVector>());
			endStubs.add(  new ArrayList<PVector>());
		}

		mask.loadPixels();
		
		int j = 0;
		while (j < polys.size()) {

			for (int k = 0; k < polys.get(j).size(); k++) {
				PVector p = polys.get(j).get(k);
				boolean ok = true;

				int col = mask.get((int)p.x, (int)p.y);
				int r = col >> 16 & 0xFF;
				if (r < 128) {
						ok = false;
						
				}
				
				if (!ok) {
					ArrayList<PVector> lhs = new ArrayList<PVector>(polys.get(j).subList(0, k));
					ArrayList<PVector> rhs = new ArrayList<PVector>(polys.get(j).subList(k+1,polys.get(j).size()));

					ArrayList<PVector> s0 = new ArrayList<PVector>(polys.get(j).subList(k, PApplet.min(k+1, polys.get(j).size())));
					ArrayList<PVector> s1 = new ArrayList<PVector>(polys.get(j).subList(PApplet.max(k-1,0), k+1));

					polys.remove(j);
					polys.add(j,rhs); // rhs first
					polys.add(j,lhs);

					startStubs.remove(j);
					endStubs.remove(j);

					startStubs.add(j,s1);
					endStubs.add(j,new ArrayList<PVector>());

					startStubs.add(j,new ArrayList<PVector>());
					endStubs.add(j,s0);
					break;
				}
			}
			j++;
		}
		for (int k = polys.size()-1; k >= 0; k--) {
			if (polys.get(k).size() < 2) {
				polys.remove(k);
				continue;
			}
			startStubs.get(k).addAll(polys.get(k));
			startStubs.get(k).addAll(endStubs.get(k));
			polys.set(k, startStubs.get(k));
		}
	 }
	public ArrayList<ArrayList<PVector>> isolines(PImage im, float d){
		ArrayList<ArrayList<ArrayList<PVector>>> isos = PEmbroiderTrace.findIsolines(im, -1, d);
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		for (int i = 0; i < isos.size(); i++) {
			for (int j = 0; j < isos.get(i).size(); j++) {
//				polys.add(resampleHalfKeepCorners(resampleHalf(isos.get(i).get(j)),0.1f));
				polys.add(isos.get(i).get(j));
			}
		}
		return polys;
	}
	
	public boolean polygonContain(ArrayList<PVector> poly0, ArrayList<PVector> poly1) {
		for (int i = 0; i < poly1.size(); i++) {
			if (!pointInPolygon(poly1.get(i),poly0)) {
				return false;
			}
		}
		for (int i = 0; i < poly1.size(); i++) {
			PVector p0 = poly1.get(i);
			PVector p1 = poly1.get((i+1)%poly1.size());
			if (segmentIntersectPolygon(p0,p1,poly0).size() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public float polygonArea(ArrayList<PVector> poly){
		//https://www.mathopenref.com/coordpolygonarea2.html
		int n = poly.size();

		float area = 0;  
		int j = n-1;

		for (int i=0; i<n; i++){
			area +=  (poly.get(j).x+poly.get(i).x) * (poly.get(j).y-poly.get(i).y); 
			j = i;
		}
		return PApplet.abs(area/2);
	}
	


	
	public void optimize(int trials,int maxIter) {
		if (polylines.size() <= 1) {
			return;
		}
		int idx0 = 0;
		for (int i = 1; i <= polylines.size(); i++) {
			if (i == polylines.size() || !colors.get(i).equals(colors.get(i-1))){
				ArrayList<ArrayList<PVector>> p = new ArrayList<ArrayList<PVector>>(polylines.subList(idx0,i));
				p = PEmbroiderTSP.solve(p,trials,maxIter);
				
				polylines.subList(idx0,i).clear();
				polylines.addAll(idx0,p);
				idx0 = i;
			}
		}
	}
	public void optimize() {
		optimize(5,999);
	}

	public void textAlign(int align) {
		FONT_ALIGN = align;
	}
	public void textAlign(int halign, int valign) {
		FONT_ALIGN = halign;
		FONT_ALIGN_VERTICAL = valign;
	}
	
	public void textSize(float size) {
		FONT_SCALE = size;
	}
	
	public void textFont(int[] font) {
		FONT = font;
		TRUE_FONT = null;
	}
	public void textFont(PFont font) {
		TRUE_FONT = font;
		FONT = null;
	}
	

	public void text(String str, float x, float y) {
		if (FONT != null) {
			ArrayList<ArrayList<PVector>> polys = PEmbroiderFont.putText(FONT,str,x,y,FONT_SCALE,FONT_ALIGN);
			for (int i = 0; i < polys.size(); i++) {
				pushPolyline(polys.get(i),0);
			}
		}else if (TRUE_FONT != null) {
			PGraphics pg0 = app.createGraphics(1, 1);
			pg0.beginDraw();
			pg0.textFont(TRUE_FONT);
			pg0.textSize(FONT_SCALE);
			float tw = pg0.textWidth(str);
			float ta = pg0.textAscent();
			float td = pg0.textDescent();
			
//			PApplet.println(tw,ta,td);
			pg0.endDraw();
			
			PGraphics pg = app.createGraphics((int)PApplet.ceil(tw),(int)PApplet.ceil(ta+td));
			pg.beginDraw();
			pg.background(0);
			pg.fill(255);
			pg.noStroke();
			
			pg.textFont(TRUE_FONT);
			pg.textSize(FONT_SCALE);
			pg.textAlign(PConstants.LEFT,PConstants.TOP);
			pg.text(str,0,0);
			pg.endDraw();
//			hatchRaster(pg,x,y);
			ArrayList<ArrayList<PVector>> conts = PEmbroiderTrace.findContours(pg);
			for (int i = conts.size()-1; i >= 0; i--) {
				if (conts.get(i).size() < 2) {
					conts.remove(i);
					continue;
				}
				for (int j = 0; j < conts.get(i).size(); j++) {
					conts.get(i).get(j).add(new PVector(x,y));
				}
//				conts.set(i,resampleHalfKeepCorners(resampleHalf(resampleHalf(conts.get(i))),0.1f));
				conts.set(i, PEmbroiderTrace.approxPolyDP(conts.get(i), 1));
				if (conts.get(i).get(conts.get(i).size()-1).dist(conts.get(i).get(0))<FONT_SCALE/10f) {
					conts.get(i).add(conts.get(i).get(0));
				}
			}

			float dx = 0;
			float dy = 0;
			
			if (FONT_ALIGN == PConstants.RIGHT) {
				dx -= tw;
			}else if (FONT_ALIGN == PConstants.CENTER) {
				dx -= tw/2;
			}
			if (FONT_ALIGN_VERTICAL == PConstants.BASELINE) {
				dy -= ta;
			}else if (FONT_ALIGN_VERTICAL == PConstants.BOTTOM) {
				dy -= ta+td;
			}

			pushMatrix();
			translate(dx,dy);
			
			if (isStroke && FIRST_STROKE_THEN_FILL) {
				
				_stroke((ArrayList<ArrayList<PVector>>)deepClone(conts),true);
			}
			if (isFill) {
				if ((HATCH_MODE == PARALLEL || HATCH_MODE == CROSS) && (HATCH_BACKEND != FORCE_RASTER)) {
					
					ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
					polys = hatchParallelComplex(conts,HATCH_ANGLE,HATCH_SPACING);
					
					if (HATCH_MODE == CROSS) {
						polys.addAll(hatchParallelComplex(conts,HATCH_ANGLE2,HATCH_SPACING));
					}
					
					for (int i = 0; i < polys.size(); i++) {
						pushPolyline(polys.get(i),currentFill,1f);
					}
				}else {
					hatchRaster(pg, x, y);
				}
			}
			if (isStroke && !FIRST_STROKE_THEN_FILL) {
				
				_stroke((ArrayList<ArrayList<PVector>>)deepClone(conts),true);
			}
			
			popMatrix();
		}
		
	}
	 public static Object deepClone(Object object) {
		 //https://www.quora.com/What-is-the-right-way-to-deep-copy-an-object-in-Java-How-do-you-do-it-in-your-code
	     try {
	       ByteArrayOutputStream baos = new ByteArrayOutputStream();
	  
	       ObjectOutputStream oos = new ObjectOutputStream(baos);
	       oos.writeObject(object);
	       ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
	       return ois.readObject();
	     }
	     catch (Exception e) {
	       	e.printStackTrace();
	         return null;
	    }
	 }
	 
	 

	 
	 
	 

}