package processing.embroider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	ArrayList<PVector> curveBuff;
	public PGraphics rasterBuff;
	public ArrayList<Integer> cullGroups;

	boolean isFill = false;
	boolean isStroke = true;
	public int currentFill = 0xFF000000;
	public int currentStroke = 0xFF000000;
	public int currentCullGroup = 0;

	public int 	beginCullIndex = 0;

	static public final float AUTO    =Float.NaN;
	static public final int NONE      =0;

	//hatch modes
	static public final int PARALLEL  =2;
	static public final int CROSS     =1;
	static public final int CONCENTRIC=3;
	static public final int SPIRAL    =4;
	static public final int PERLIN    =5;
	static public final int VECFIELD  =6;
	static public final int SATIN     =7;
	static public final int DRUNK     =8;
	
	//stroke modes
	static public final int PERPENDICULAR = 10;
	static public final int TANGENT       = 11;
	static public final int ANGLED        = 12;
	
	static public final int COUNT       =22;
	static public final int WEIGHT      =23;
	static public final int SPACING     =24;

	static public final int ADAPTIVE     = 30;
	static public final int FORCE_VECTOR = 31;
	static public final int FORCE_RASTER = 32;
	
	static public final int STROKE_OVER_FILL = 41;
	static public final int FILL_OVER_STROKE = 42;
	
	static public final int CW  = 51;
	static public final int CCW = 52;
	static public final int CLOCKWISE  = 51;
	static public final int COUNTERCLOCKWISE = 52;
	
	static public final int ASK = 61;
	static public final int CROP = 62;
	static public final int IGNORE = 63;
	static public final int ABORT = 64;
	static public final int WARN = 100;

	
	static public final int CENTER = 3;
	static public final int INSIDE = -1;
	static public final int OUTSIDE = 1;
	
	static public final int ZIGZAG = 71;
	static public final int SIGSAG = 72;
	static public final int BOUSTROPHEDON = 73;
	
	
	public int ELLIPSE_MODE = PConstants.CENTER;
	public int RECT_MODE = PConstants.CORNER;
	public int BEZIER_DETAIL = 40;
	public int CIRCLE_DETAIL = 32;
	
	public int CATMULLROM_DETAIL = 40;
	public float CATMULLROM_TIGHTNESS = 0.5f;
	
	public int HATCH_MODE = PARALLEL;
	public float HATCH_ANGLE  =  PApplet.QUARTER_PI;
	public float HATCH_ANGLE2 = -PApplet.QUARTER_PI;
	public float HATCH_SPACING = 4;
	public float HATCH_SCALE = 1;
	public int HATCH_BACKEND = ADAPTIVE;
	public int HATCH_SPIRAL_DIRECTION = CW;
	
	public boolean AUTO_HATCH_ANGLE = false;
	
	public int STROKE_MODE = PERPENDICULAR;
	public int STROKE_TANGENT_MODE = WEIGHT;
	public float STROKE_WEIGHT = 1;
	public float STROKE_SPACING = 4;
	public float STROKE_ANGLE = 0;
	public int STROKE_JOIN = PConstants.ROUND;
	public int STROKE_CAP = PConstants.ROUND;
	
	public float STROKE_LOCATION = 0.0f;
	
	public boolean FIRST_STROKE_THEN_FILL = false;
	public boolean NO_RESAMPLE = false;
	public boolean NO_CONNECT = false;
	
	public VectorField HATCH_VECFIELD;

	public float STITCH_LENGTH = 10;
	public float RESAMPLE_NOISE = 0.0f;
	public float MIN_STITCH_LENGTH = 4;
	
	public int[] FONT = PEmbroiderFont.SIMPLEX;
	public PFont TRUE_FONT;
	public float FONT_SCALE = 1f;
	public int FONT_ALIGN = PConstants.LEFT;
	public int FONT_ALIGN_VERTICAL = PConstants.BASELINE;
	
	/**
	 * Anti-alignment for rings of concentric hatching to reduce visual ridges.
	 */
	public float CONCENTRIC_ANTIALIGN = 0.6f;
	
	/**
	 * Maximum turning angle (in radians) for vertices that the resampling algorithm is allowed optimize out.
	 */
	public float RESAMPLE_MAXTURN = 0.2f;
	
	public int OUT_OF_BOUNDS_HANDLER = ASK;
	
	boolean randomizeOffsetEvenOdd = false;
	float randomizeOffsetPrevious = 0.0f;
	
	
	/**
	 * Add staggering to the resampling of parallel hatches, to reduce visual ridges. Recommanded range: (0,1)
	 */
	public float PARALLEL_RESAMPLING_OFFSET_FACTOR = 0.5f;
	
	/**
	 * Add staggering to the resampling of satin hatches, to reduce visual ridges. Recommanded range: (0,1)
	 */
	public float SATIN_RESAMPLING_OFFSET_FACTOR = 0.5f;
	
	/**
	 * Experimental resampling method for crosshatching, that tries un-align stitch placement.
	 */
	public boolean EXPERIMENTAL_CROSS_RESAMPLE = false;
	
	/**
	 * Stroke density of perpendicular stroke caps / stroke joins.
	 */
	public float PERPENDICULAR_STROKE_CAP_DENSITY_MULTIPLIER = 1.0f;
	
	public float CULL_SPACING = 7;
	
	public int SATIN_MODE = ZIGZAG;
	
	/**
	 * When drawing text, optimize the stroke order of each character individually, instead of optimizing once for the whole sentece.
	 */
	public boolean TEXT_OPTIMIZE_PER_CHAR = true;
	
	
	static String logPrefix = "[PEmbroider] ";
	
	public PEmbroiderBooleanShapeGraphics composite;
	
	/**
	* The constructor for PEmbroiderGraphics object.
	*
	* @param  _app the running PApplet instance: in Processing, just pass the keyword 'this'
	* @param  w    width 
	* @param  h    height
	*/
	public PEmbroiderGraphics(PApplet _app, int w, int h) {
		app = _app;
		width = w;
		height = h;
		// preview = app.createGraphics(w, h);
		matStack = new ArrayList<PMatrix2D>();
		matStack.add(new PMatrix2D());
		polylines = new ArrayList<ArrayList<PVector>>();
		polyBuff = new ArrayList<ArrayList<PVector>> ();
		curveBuff = new ArrayList<PVector> ();
		colors = new ArrayList<Integer>();
		cullGroups = new ArrayList<Integer>();
		PEmbroiderHatchSatin.setGraphics(this);
		
	}
	public PEmbroiderGraphics(PApplet _app) {
		this(_app,_app.width,_app.height);
	}

	/**
	* Set the output file path.
	* The filename should include the file type extension. 
	* Supported embroidery formats are: .DST, .EXP, .JEF, .PEC, .PES, .VP3, and .XXX. 
	* Additionally supported formats are: .PDF, .SVG, .TSV, and .GCODE. 
	* 
	* @param  _path output file path. The format will be automatically inferred from the extension.
	*/
	public void setPath(String _path) {
		path = _path;
	}
	
	/**
	* Clear all current drawings. 
	* 
	*/
	public void clear() {
		polylines.clear();
		colors.clear();
		cullGroups.clear();
	}

	/* ======================================== STYLE SETTING ======================================== */
	
	/** Change fill color
	 *  @param r red value, 0-255
	 *  @param g green value, 0-255
	 *  @param b blue value, 0-255
	 *  @see   stroke
	 */
	public void fill(int r, int g, int b) {
		isFill = true;
		currentFill = 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
	}

	/** Change fill color
	 *  @param gray grayscale value, 0-255
	 */
	public void fill(int gray) {
		fill(gray,gray,gray);
	}

	/** Disable filling shapes
	 *
	 */
	public void noFill() {
		isFill = false;
	}
	
	/** Change stroke color
	 *  @param r red value, 0-255
	 *  @param g green value, 0-255
	 *  @param b blue value, 0-255
	 *  @see   fill
	 */
	public void stroke(int r, int g, int b) {
		isStroke = true;
		currentStroke = 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
	}
	
	/** Change stroke color
	 *  @param gray  grayscale value 0-255
	 */
	public void stroke(int gray) {
		stroke(gray,gray,gray);
	}

	/** Disable outlining shapes
	 *
	 */
	public void noStroke() {
		isStroke = false;
	}

	/** Change width of stroke
	 *  @param d  the stroke weight to use
	 */
	public void strokeWeight(float d) {
		STROKE_WEIGHT = Math.max(d,1);
	}

	/** Change stroke join (turning point) style
	 *  @param j  Same as Processing strokeJoin, this can be ROUND, MITER, BEVEL etc.
	 *  @see   strokeCap
	 */
	public void strokeJoin(int j) {
		STROKE_JOIN = j;
	}

	/** Change stroke cap (end point) style
	 *  @param j  Same as Processing strokeCap, this can be ROUND, SQUARE, PROJECT etc.
	 *  @see strokejoin
	 */
	public void strokeCap(int j) {
		STROKE_CAP = j;
	}

	/** Modifies the location from which ellipses are drawn by changing the way in which parameters given to ellipse() are intepreted.
	 *  also governs circle()
	 *  
	 *  @param j  Same as Processing ellipseMode, this can be RADIUS, CENTER, CORNER, CORNERS etc.
	 *  @see   rectMode
	 */
	public void ellipseMode(int mode) {
		ELLIPSE_MODE = mode;
	}

	/** Modifies the location from which rectangles are drawn by changing the way in which parameters given to rect() are intepreted.
	 *  
	 *  @param j  Same as Processing rectMode, this can be RADIUS, CENTER, CORNER, CORNERS etc.
	 *  @see   ellipseMode
	 */
	public void rectMode(int mode) {
		RECT_MODE = mode;
	}

	/** Change number of steps bezier curve is interpolated. 
	 *  
	 *  @param n The higher this number, the smoother the Bezier curve.
	 */
	public void bezierDetail(int n) {
		BEZIER_DETAIL = Math.max(1, n);
	}
	public void curveDetail(int n) {
		CATMULLROM_DETAIL = Math.max(1,n);
	}
	public void curveTightness(float n) {
		CATMULLROM_TIGHTNESS = Math.max(0, n);
	}

	/** Change hatching pattern
	 *  
	 *  @param mode  This can be one of: PARALLEL, CROSS, CONCENTRIC, SPIRAL, PERLIN, VECFIELD, DRUNK	
	 */
	public void hatchMode(int mode) {
		HATCH_MODE = mode;
	}

	/** Change outline drawing method
	 *  
	 *  @param mode  This can be either PERPENDICULAR or TANGENT	
	 */
	public void strokeMode(int mode) {
		STROKE_MODE = mode;
	}

	/** Change outline drawing method
	 *  
	 *  @param mode     This can be either PERPENDICULAR or TANGENT	
	 *  @param tanMode  This can be one of COUNT (stroke weight used as line count), WEIGHT (honour stroke weight setting over spacing) or SPACING (honour spacing over stroke weight)
	 */
	public void strokeMode(int mode, int tanMode) {
		STROKE_MODE = mode;
		STROKE_TANGENT_MODE = tanMode;
	}
	

	/** Set the position of the stroke, relative to the shape's edge
	 *  
	 *  @param x     Float between -1.0 (inside) and 1.0 (outside). 0.0 is centered.	
	 */
	public void strokeLocation(float x) {
		STROKE_LOCATION = PApplet.min(PApplet.max(-1,x),1);
	}
	
	/** Set the position of the stroke, relative to the shape's edge
	 *  
	 *  @param x     This can be one of CENTER, INSIDE, or OUTSIDE 	
	 */
	public void strokeLocation(int mode) {
		if (mode == CENTER) {
			strokeLocation(0.0f);
		}else {
			strokeLocation((float)mode);
		}
	}
	
	/** Change angle of parallel hatch lines
	 *  
	 *  @param ang     the angle from +x in radians
	 *  @see   hatchAngleDeg
	 *  @see   hatchAngles
	 */
	public void hatchAngle(float ang) {
		if (Float.isNaN(ang)) {
			AUTO_HATCH_ANGLE = true;
			return;
		}
		AUTO_HATCH_ANGLE = false;
		HATCH_ANGLE = ang;
	}

	/** Change angles of parallel and cross hatching lines
	 *  
	 *  @param ang1     the angle from +x in radians (for parallel hatches and the first direction of cross hatching)
	 *  @param ang2     the angle from +x in radians (for second direction of cross hatching)
	 *  @see   hatchAngle
	 *  @see   hatchAnglesDeg
	 */
	public void hatchAngles(float ang1, float ang2) {
		if (Float.isNaN(ang1) || Float.isNaN(ang2)) {
			AUTO_HATCH_ANGLE = true;
			return;
		}
		AUTO_HATCH_ANGLE = false;
		HATCH_ANGLE  = ang1;
		HATCH_ANGLE2 = ang2;
	}

	/** Change angle of parallel hatch lines
	 *  
	 *  @param ang     the angle from +x in degrees
	 *  @see   hatchAngle
	 *  @see   hatchAnglesDeg
	 */
	public void hatchAngleDeg(float ang) {
		if (Float.isNaN(ang)) {
			AUTO_HATCH_ANGLE = true;
			return;
		}
		AUTO_HATCH_ANGLE = false;
		hatchAngle(PApplet.radians(ang));
	}

	/** Change angles of parallel and cross hatching lines
	 *  
	 *  @param ang1     the angle from +x in degrees (for parallel hatches and the first direction of cross hatching)
	 *  @param ang2     the angle from +x in degrees (for second direction of cross hatching)
	 *  @see   hatchAngles
	 *  @see   hatchAngleDeg
	 */
	public void hatchAnglesDeg(float ang1, float ang2) {
		if (Float.isNaN(ang1) || Float.isNaN(ang2)) {
			AUTO_HATCH_ANGLE = true;
			return;
		}
		AUTO_HATCH_ANGLE = false;
		hatchAngles(PApplet.radians(ang1),PApplet.radians(ang2));
	}
	
	/** Sets the orientation of the stitches within a stroke
	 *  @param ang     the angle (in radians)
	 */
	public void strokeAngle(float ang) {
		STROKE_ANGLE=ang;
	}

	/** Sets the orientation of the stitches within a stroke
	 *  @param ang     the angle (in degrees) 
	 */
	public void strokeAngleDeg(float ang) {
		STROKE_ANGLE=PApplet.radians(ang);
	}
	
	/** Changes the spacing between hatching lines: a.k.a sparsity or inverse-density
	 *  
	 *  @param d   the spacing in pixels
	 *  @see       strokeSpacing
	 */
	
	public void hatchSpacing(float d) {
		HATCH_SPACING = Math.max(0.1f,d);
	}

	/** Changes the spacing between stroke lines: a.k.a sparsity or inverse-density
	 *  
	 *  @param d   the spacing in pixels
	 *  @see       hatchSpacing
	 */

	public void strokeSpacing(float d) {
		STROKE_SPACING = Math.max(0.1f,d);
	}

	/** Changes the scaling for perlin noise hatching
	 *  
	 *  @param s   the scale
	 */
	public void hatchScale(float s) {
		HATCH_SCALE = Math.max(0.000001f,s);;
	}

	/** Switches the algorithms used to compute the drawing
	 *  
	 *  @param mode   one of ADAPTIVE (use most appropriate method for each situation according to Lingdong) FORCE_VECTOR (uses vector math whenever possible) FORCE_RASTER (first render shapes as raster and re-extract the structures, generally more robust)
	 */
	public void hatchBackend(int mode) {
		HATCH_BACKEND = mode;
	}

	/** Set the vector field used for vector field hatching
	 *  
	 *  @param vf a vector field defination -- simple, just a class with a get(x,y) method
	 */
	public void setVecField(VectorField vf) {
		HATCH_VECFIELD = vf;
	}

	/** Set the desirable stitch length. Stitches will try their best to be around this length, but actual length will vary slightly for best result
	 *  
	 *  @param x the desirable stitch length
	 *  @see minSitchLength
	 *  @see setStitch
	 */
	public void stitchLength(float x) {
		STITCH_LENGTH = Math.max(0.1f,x);
	}

	/** Set the minimum stitch length. Drawings with higher precision than this will be resampled down to have at least this stitch length
	 *  
	 *  @param x the minimum stitch length
	 *  @see stichLength
	 *  @see setStitch
	 */
	public void minStitchLength(float x) {
		MIN_STITCH_LENGTH = Math.max(0f,x);
	}

	/** Set stitch properties
	 *  
	 *  @param msl minimum stitch length
	 *  @param sl  desirable stitch length
	 *  @param rn  resample noise -- to avoid alignment patterns; must be in the range [0...1]
	 */
	public void setStitch(float msl, float sl, float rn) {
		MIN_STITCH_LENGTH = Math.max(0f,msl);
		STITCH_LENGTH = Math.max(0.1f,sl);
		RESAMPLE_NOISE = Math.max(0.0f,Math.min(1f,rn));
	}

	/** Set render order: render strokes over fill, or other way around?
	 *  
	 *  @param mode  This can either be STROKE_OVER_FILL or FILL_OVER_STROKE
	 */
	public void setRenderOrder(int mode) {
		if (mode == STROKE_OVER_FILL) {
			FIRST_STROKE_THEN_FILL = false;
		}else if (mode == FILL_OVER_STROKE) {
			FIRST_STROKE_THEN_FILL = true;
		}
	}

	/** Turn resampling on and off. For embroidery machines, you might want it; for plotters you probably won't need it.
	 *  
	 *  @param b   true for on, false for off
	 */
	public void toggleResample(boolean b) {
		NO_RESAMPLE = !b;
	}
	public void toggleConnectingLines(boolean b) {
		NO_CONNECT = !b;
	}
	
	/** Sets whether SPIRAL hatching proceeds clockwise or counterclockwise
	 *  
	 *  @param mode   This can be CLOCKWISE or COUNTERCLOCKWISE. CW and CCW are also permissible.
	 */
	public void setSpiralDirection(int mode) {
		HATCH_SPIRAL_DIRECTION = mode;
	}
	
	/** Sets the file exporting behavior if a stitch was placed out of bounds.
	 *  
	 *  @param mode   This can be one of WARN, CROP, IGNORE, ASK, or ABORT
	 */
	public void setOutOfBoundsHandler(int mode) {
		OUT_OF_BOUNDS_HANDLER = mode;
	}

	/** Sets how the SATIN hatching mode operates. 
	 *  
	 *  @param mode   This can be one of ZIGZAG, SIGSAG, or BOUSTROPHEDON
	 */
	public void satinMode(int mode) {
		SATIN_MODE = mode;
	}
	
	/* ======================================== MATH ======================================== */
	
	/** Compute the determinant of a 3x3 matrix as 3 row vectors.
	 *  
	 *  @param r1 row 1
	 *  @param r2 row 2
	 *  @param r3 row 3
	 *  @return the determinant
	 */
	public static float det(PVector r1, PVector r2, PVector r3) {
		float a = r1.x; float b = r1.y; float c = r1.z;
		float d = r2.x; float e = r2.y; float f = r2.z;
		float g = r3.x; float h = r3.y; float i = r3.z;
		return a*e*i + b*f*g + c*d*h - c*e*g - b*d*i - a*f*h;
	}

	/** Intersect two segments in 3D, returns lerp params instead of actual points, because the latter can cheaply be derived from the former; more expensive the other way around.
	 *  Also works for 2D.
	 *  
	 *  @param p0 first endpoint of first segment
	 *  @param p1 second endpoint of first segment
	 *  @param q0 first endpoint of second segment
	 *  @param q1 second endpoint of second segment
	 *  @return vec where vec.x is the lerp param for first segment, and vec.y is the lerp param for the second segment
	 */
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

	/** Averages a bunch of points
	 *  
	 *  @param poly a bunch of points
	 *  @return a vector holding the average value
	 */
	public PVector centerpoint(ArrayList<PVector> poly) {
		float x=0;
		float y=0;
		for (int i = 0; i < poly.size(); i++) {
			x += poly.get(i).x;
			y += poly.get(i).y;
		}
		return new PVector(x/(float)poly.size(),y/(float)poly.size());
	}

	/** Averages a bunch of bunches of points
	 *  
	 *  @param poly a bunch of bunches of points
	 *  @param whatever literally pass in whatever. It is necessary because of type erasure in Java, which basically means Java cannot tell the difference between List<T> and List<List<T>> in arguments, so a difference in number of arguments is required for overloading to work
	 *  @return a vector holding the average value
	 */
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

	/** Class for a bounding box
	 * 
	 */
	public class BBox{
		public float x;
		public float y;
		public float w;
		public float h;

		/** Constructor that makes a bounding box from top left corner and dimensions
		 *  
		 *  @param _x left
		 *  @param _y top
		 *  @param _w width
		 *  @param _h height
		 */
		public BBox(float _x, float _y, float _w, float _h){
			x = _x;
			y = _y;
			w = _w;
			h = _h;
		}

		/** Constructor that makes a bounding box from top left corner and bottom right corner
		 *  
		 *  @param p top left corner
		 *  @param p bottom right corner
		 */
		public BBox(PVector p, PVector q) {
			x = p.x;
			y = p.y;
			w = q.x-p.x;
			h = q.y-p.y;
		}

		/** Constructor that makes a bounding box from a bunch of points
		 *  
		 *  @param poly a bunch of points
		 */
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
		/** Constructor that makes a bounding box from a bunch of bunches of points
		 *  
		 *  @param polys a bunch of bunches of points
		 *  @param whatever literally pass in whatever. It is necessary because of type erasure in Java, which basically means Java cannot tell the difference between List<T> and List<List<T>> in arguments, so a difference in number of arguments is required for overloading to work
		 */
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

	/** Class for a bounding circle.
	 * The bounding circle is not the minimal bounding circle for now. It's just some bounding circle.
	 * It works well enough for current use cases, but we might implement minimal bounding circle in the future
	 * 
	 */
	public class BCircle {
		public float x;
		public float y;
		public float r;
		/** Constructor that makes a bounding circle from center and radius
		 *  
		 *  @param _x x coordinate of center
		 *  @param _y y coordinate of center
		 *  @param _r radius
		 */
		public BCircle(float _x, float _y, float _r){
			x = _x;
			y = _y;
			r = _r;
		}
		/** Constructor that makes a bounding circle from a bunch of points
		 *  
		 *  @param poly a bunch of points
		 */
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
		/** Constructor that makes a bounding circle from a bunch of bunches of points
		 *  
		 *  @param polys a bunch of bunches of points
		 *  @param whatever literally pass in whatever. It is necessary because of type erasure in Java, which basically means Java cannot tell the difference between List<T> and List<List<T>> in arguments, so a difference in number of arguments is required for overloading to work
		 */
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
	
	/** Find intersection between a segment and a polygon. Intersections returned are sorted from one endpoint of the segment to the other endpoint
	 *  
	 *  @param p0   first endpoint of first segment
	 *  @param p1   second endpoint of first segment
	 *  @param poly the polygon
	 *  @return a list of vectors holding the intersection points sorted from one endpoint to the other
	 */
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
	
	/** Find intersection between a segment and several polygons. Intersections returned are sorted from one endpoint of the segment to the other endpoint
	 *  
	 *  @param p0   first endpoint of first segment
	 *  @param p1   second endpoint of first segment
	 *  @param poly several polygons
	 *  @return a list of vectors holding the intersection points sorted from one endpoint to the other
	 */
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
	/** Check if a point is inside a polygon.
	 *  
	 *  @param p the point in question
	 *  @param poly the polygon
	 *  @param trials try a couple times to be sure, otherwise we might encounter degenerate cases
	 *  @return true means inside, false means outside
	 */
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
	/** Check if a point is inside a polygon.
	 *  Dumbed down version of pointinPolygon(3) where I pick the number of trials for you
	 *  
	 *  @param p the point in question
	 *  @param poly the polygon
	 *  @return true means inside, false means outside
	 */
	public boolean pointInPolygon(PVector p, ArrayList<PVector> poly) {
		return pointInPolygon(p,poly,3);
	}
	/** Generate a random point that is inside a polygon
	 *  
	 *  @param poly the polygon
	 *  @param trials number of times we try before giving up
	 *  @return either null or a point inside the polygon. If it is null, it either means the polygon has zero or almost zero area, or that the number of trials specified is not large enough to find one
	 */
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
	/** Generate a random point that is inside a polygon
	 *  Dumbed down version of randomPointInPolygon(2) where I pick the number of trials for you
	 *  
	 *  @param poly the polygon
	 *  @return either null or a point inside the polygon. If it is null, it either means the polygon has zero or almost zero area, or that the default number of trials is not large enough to find one
	 */
	public PVector randomPointInPolygon(ArrayList<PVector> poly) {
		return randomPointInPolygon(poly,9999);
	}

	
	//---------------------------------
	// Calculate the orientation angle of the shape
	// by Golan
	float calcPolygonTilt (ArrayList<PVector> poly) {
		
	  // lingdong's hack agianst NaN's
	  ArrayList<PVector> pts = new ArrayList<PVector>();
	  for (int i = 0; i < poly.size(); i++) {
		  pts.add(poly.get(i).copy().add(new PVector(app.random(-0.5f,0.5f),app.random(-0.5f,0.5f))));
	  }
	  // end lingdong's hack
	  
	  PVector centroidPoint = centerpoint(pts);
	  float orientation  = 0.0f; // The angle of the shape's orientation, in radians

	  if (pts != null) {
	    int nPoints = pts.size();
	    if (nPoints > 2) {

	      // arguments: an array of points, the array's width & height, and the location of the center of mass (com).
	      // this function calculates the elements of a point set's tensor matrix,
	      // calls the function calcEigenvector() to get the best eigenvector of this matrix
	      // and returns this eigenVector as a pair of doubles

	      // first we look at all the pixels, determine which ones contribute mass (the black ones),
	      // and accumulate the sums for the tensor matrix
	      float dX, dY; 
	      float XXsum, YYsum, XYsum;
	      XXsum = 0; 
	      YYsum = 0; 
	      XYsum = 0; 

	      for (int j=0; j<nPoints; j++) {
	        PVector pt = (PVector) pts.get(j);
	        dX = pt.x - centroidPoint.x;
	        dY = pt.y - centroidPoint.y;
	        XXsum += dX * dX;
	        YYsum += dY * dY;
	        XYsum += dX * dY;
	      }

	      // here's the tensor matrix. 
	      // watch out for memory leaks. 
	      float matrix2x2[][] = new float[2][2];
	      matrix2x2[0][0] =  YYsum;
	      matrix2x2[0][1] = -XYsum;
	      matrix2x2[1][0] = -XYsum;
	      matrix2x2[1][1] =  XXsum;
//	      PApplet.println(matrix2x2[0][0],matrix2x2[0][1],matrix2x2[1][0],matrix2x2[1][1]);
	      // get the orientation of the bounding box
	      float[] response = calcEigenvector ( matrix2x2 );
	      orientation  = response[0];
	    }
	  }
//	  PApplet.println(centroidPoint, orientation);
	  return (orientation);//+PConstants.TWO_PI)%PConstants.TWO_PI;
	}

	//---------------------------------
	// Internal function; don't worry about it
	// by Golan
	float[] calcEigenvector ( float[][] matrix ) {

	  //this function takes a 2x2 matrix, and returns a pair of angles which are the eigenvectors
	  float A = matrix[0][0]; 
	  float B = matrix[0][1];
	  float C = matrix[1][0];
	  float D = matrix[1][1];

	  float multiPartData[] = new float[2]; // watch out for memory leaks. 

	  // because we assume a 2x2 matrix,
	  // we can solve explicitly for the eigenValues using the Quadratic formula.
	  // the eigenvalues are the roots of the equation  det( lambda * I  - T) = 0
	  float a, b, c, root1, root2;
	  a = 1.0f;
	  b = (0.0f - A) - D;
	  c = (A * D) - (B * C);
	  float Q = (b * b) - (4.0f * a * c);
	  if (Q >= 0) {
	    root1 = ((0.0f - b) + PApplet.sqrt ( Q)) / (2.0f * a);
	    root2 = ((0.0f - b) - PApplet.sqrt ( Q)) / (2.0f * a);

	    // assume x1 and x2 are the elements of the eigenvector.  Then, because Ax1 + Bx2 = lambda * x1, 
	    // we know that x2 = x1 * (lambda - A) / B.
	    float factor2 = ( PApplet.min (root1, root2) - A) / B;

	    // we arbitrarily set x1 = 1.0 and compute the magnitude of the eigenVector with respect to this assumption
	    float magnitude2 = PApplet.sqrt (1.0f + factor2*factor2);

	    // we now find the exact components of the eigenVector by scaling by 1/magnitude
	    if ((magnitude2 == 0) || (Float.isNaN(magnitude2))) {
	      multiPartData[0] = 0;
	      multiPartData[1] = 0;
	    } else {
	      float orientedBoxOrientation = PApplet.atan2 ( (1.0f / magnitude2), (factor2 / magnitude2));
	      float orientedBoxEigenvalue  = PApplet.log (1.0f+root2); // orientedness
	      multiPartData[0] = orientedBoxOrientation;
	      multiPartData[1] = orientedBoxEigenvalue;
	    }
	  } else {
	    multiPartData[0] = 0;
	    multiPartData[1] = 0;
	  }

	  return multiPartData;
	}

	float calcPolygonTiltRaster(PImage im) {
		ArrayList<ArrayList<PVector>> polys = PEmbroiderTrace.findContours(im);
		float ma = -1;
		int mi = -1;
		for (int i = 0; i < polys.size(); i++) {
			BBox bb = new BBox(polys.get(i));
			float ar = bb.w*bb.h;
			if (ar > ma) {
				ma = ar;
				mi = i;
			}
		}
		return calcPolygonTilt(polys.get(mi));
	}
	
	
	
	/** Add a polyline to the global array of all polylines drawn
	 *  Applying transformation matrices and resampling
	 *  All shape drawing routines go through this function for finalization
	 *  
	 *  @param poly                   a polyline
	 *  @param color                  the color of the polyline (0xRRGGBB)
	 *  @param resampleRandomOffset   whether to add a random offset during resample step to prevent alignment patterns
	 */
	public void pushPolyline(ArrayList<PVector> poly, int color, float resampleRandomizeOffset) {
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		for (int i = 0; i < poly.size(); i++) {
			poly2.add(poly.get(i).copy());
			for (int j = matStack.size()-1; j>= 0; j--) {
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
	/** Simplified version for pushPolyline(3) where resampleRandomizeOffset is set to false
	 *  @param poly                   a polyline
	 *  @param color                  the color of the polyline (0xRRGGBB)
	 */
	public void pushPolyline(ArrayList<PVector> poly, int color) {
		pushPolyline(poly,color,0);
	}

	/* ======================================== SHAPE IMPLEMENTATION ======================================== */

	/** offset a polyline by certain amount (for outlining strokes, naive implementation)
	 *  @param poly   a polyline
	 *  @param d      offset amount, positive/negative for inward/outward
	 */
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

	/** Turn a self-intersecting polygon to a list of non-self-intersecting polygons
	 *  @param poly   a polygon
	 */
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

	/** Check the winding orientation of polygon, clockwise or anti-clockwise
	 *	@param poly   a polygon
	 *  @return whether the polygon is positively oriented
	 */
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

	/** Calculate distance between point and line
	 *  @param p   the point in question
	 *  @param p0  a point on the line
	 *  @param p1  a different point on the line
	 *  @return distance from point to line
	 */
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
	
	/** Calculate distance between point and a segment (when projection is not on the line, the distance becomes that to one of the endpoints)
	 *  @param p   the point in question
	 *  @param p0  first endpoint of the segment
	 *  @param p1  second endpoint of the segment
	 *  @return distance from point to segment
	 */
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
	
	/** Inset a polygon (making it slightly smaller fitting in the original polygon)
	 *  Inverse of offsetPolygon, alias for offsetPolygon(poly, -d);
	 *  @param poly  the polygon
	 *  @param d     the amount of inset
	 *  @return a list of polygons. When the given polygon is big at both ends and small in the middle, the inset might break into multiple polygons
	 */
	public ArrayList<ArrayList<PVector>> insetPolygon(ArrayList<PVector> poly, float d){
		return offsetPolygon(poly,-d);
	}
	
	/** Offset a polygon (making it slightly smaller fitting in the original polygon, or slightly bigger wrapping the original polygon)
	 *  @param poly  the polygon
	 *  @param d     the amount of offset. Use negative value for insetting
	 *  @return a list of polygons. When the given polygon is big at both ends and small in the middle, the inset might break into multiple polygons
	 */
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
	public ArrayList<ArrayList<PVector>> insetPolygonsRaster(ArrayList<ArrayList<PVector>> polys, float d) {
		if (polys.size()==0) {
			return new ArrayList<ArrayList<PVector>>();
		}
		BBox bb = new BBox(polys,0);
		
		PGraphics pg = app.createGraphics((int)PApplet.ceil(bb.w),(int)PApplet.ceil(bb.h));
		pg.beginDraw();
		pg.background(0);
		pg.fill(255);
		pg.stroke(0);
		pg.strokeWeight(d*2);
		pg.translate(-bb.x,-bb.y);
		pg.beginShape();
		for (int i = 0; i < polys.get(0).size(); i++) {
			pg.vertex(polys.get(0).get(i).x,polys.get(0).get(i).y);
		}
		for (int j = 1; j < polys.size(); j++) {
			pg.beginContour();
			for (int i = 0; i < polys.get(j).size(); i++) {
				pg.vertex(polys.get(j).get(i).x,polys.get(j).get(i).y);
			}
			pg.endContour();
		}
		pg.endShape(PConstants.CLOSE);
		pg.endDraw();

		ArrayList<ArrayList<PVector>> polys2 = PEmbroiderTrace.findContours(pg);
		
		for (int i = polys2.size()-1; i >= 0; i--) {
			if (polys2.get(i).size() < 2) {
				polys2.remove(i);
				continue;
			}
			for (int j = 0; j < polys2.get(i).size(); j++) {
				polys2.get(i).get(j).add(new PVector(bb.x,bb.y));
			}
			polys2.set(i, PEmbroiderTrace.approxPolyDP(polys2.get(i), 1));
		}
		return polys2;
	}
	
	public ArrayList<ArrayList<PVector>> outsetPolygonsRaster(ArrayList<ArrayList<PVector>> polys, float d) {
		if (polys.size()==0) {
			return new ArrayList<ArrayList<PVector>>();
		}
		BBox bb = new BBox(polys,0);
		bb.x -= d*2;
		bb.y -= d*2;
		bb.w += d*4;
		bb.h += d*4;
		PGraphics pg = app.createGraphics((int)PApplet.ceil(bb.w),(int)PApplet.ceil(bb.h));
		pg.beginDraw();
		pg.background(0);
		pg.fill(255);
		pg.stroke(255);
		pg.strokeWeight(d*2);
		pg.translate(-bb.x,-bb.y);
		pg.beginShape();
		for (int i = 0; i < polys.get(0).size(); i++) {
			pg.vertex(polys.get(0).get(i).x,polys.get(0).get(i).y);
		}
		for (int j = 1; j < polys.size(); j++) {
			pg.beginContour();
			for (int i = 0; i < polys.get(j).size(); i++) {
				pg.vertex(polys.get(j).get(i).x,polys.get(j).get(i).y);
			}
			pg.endContour();
		}
		pg.endShape(PConstants.CLOSE);
		pg.endDraw();
		ArrayList<ArrayList<PVector>> polys2 = PEmbroiderTrace.findContours(pg);
		
		for (int i = polys2.size()-1; i >= 0; i--) {
			if (polys2.get(i).size() < 2) {
				polys2.remove(i);
				continue;
			}
			for (int j = 0; j < polys2.get(i).size(); j++) {
				polys2.get(i).get(j).add(new PVector(bb.x,bb.y));
			}
			polys2.set(i, PEmbroiderTrace.approxPolyDP(polys2.get(i), 1));
		}
		return polys2;
	}


	/** When the general shapes of two polygons are similar, this function finds the index offset of vertices for the first polygon so that the vertices best match those of the second polygon with one-to-one correspondence.
	 *  The number of vertices needs to be the same for the polygons
	 *  @param poly0 the first polygon
	 *  @param poly1 the second polygon
	 *  @return the optimal index offset
	 */
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
	
	/** Draw stroke (outline) for a polygon using the TANGENT style (using vector math)
	 *  @param poly the polygon
	 *  @param n    number of strokes
	 *  @param d    spacing between the strokes
	 *  @return      an array of polylines
	 */
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
	
	/** Draw stroke (outline) for (a) poly(gon/line)(s) using the TANGENT style (using raster algorithms)
	 *  @param polys  a set of polyline/polygons, those inside another and have a backward winding will be considered holes
	 *  @param n      number of strokes
	 *  @param d      spacing between the strokes
	 *  @param cap    stroke cap, one of the Processing stroke caps, e.g. ROUND
	 *  @param join   stroke join, one of the Processing stroke joins, e.g. MITER
	 *  @param close  whether the polyline is considered as closed (polygon) or open (polyline)
	 *  @return       an array of polylines
	 */
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
			
			
			if (CONCENTRIC_ANTIALIGN > 0) {
				for (int i = 0; i < polys3.size(); i++) {
					ArrayList<PVector> pp = polys3.get(i);
					if (k%2 == 0) {
						ArrayList<PVector> qq = new ArrayList<PVector>();
						for (int j = 0; j < pp.size()+1; j++) {
							if (j != pp.size()) {
								PVector a = pp.get((j-1+pp.size())%pp.size());
								PVector b = pp.get(j);
								PVector c = pp.get((j+1)%pp.size());
								PVector u = b.copy().sub(a);
								PVector v = c.copy().sub(b);
								float ang = PApplet.abs(PVector.angleBetween(u, v));
								if (ang > PConstants.PI) {
									ang = PConstants.TWO_PI - ang;
								}
								if (ang > CONCENTRIC_ANTIALIGN) {
									qq.add(b);
								}
							}
							
							PVector p = pp.get(j%pp.size()).copy().mult(0.5f).add(pp.get((j+1)%pp.size()).copy().mult(0.5f));
							qq.add(p);
						}
						polys2.add(qq);
	
						
					}else {
						polys2.add(pp);
					}
				}
			}else {
				polys2.addAll(polys3);
			}

//			app.tint(255,/100f);
//			app.image(pg,bb.x,bb.y);
		}
//		
		return polys2;
	}
	
	/** Draw stroke (outline) for a poly(gon/line) using the PERPENDICULAR (a.k.a normal, as in "normal map", not "normal person") style (using vector math)
	 *  @param poly the polygon
	 *  @param d     weight of stroke
	 *  @param s     spacing between the strokes
	 *  @param close whether the polyline is considered as closed (polygon) or open (polyline)
	 *  @return      an array of polylines
	 */
	public ArrayList<ArrayList<PVector>> strokePolyNormal(ArrayList<PVector> poly, float d, float s, boolean close){
//		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		ArrayList<ArrayList<ArrayList<PVector>>> polyss = new ArrayList<ArrayList<ArrayList<PVector>>>();
		
		BBox bb = new BBox(poly);
		bb.x -= d*2;
		bb.y -= d*2;
		bb.w += d*4;
		bb.h += d*4;
		
		PGraphics pg = app.createGraphics((int)bb.w, (int)bb.h);
		pg.beginDraw();
		pg.background(0);
		pg.stroke(255);
		pg.strokeWeight(s*1f);
		pg.translate(-bb.x,-bb.y);
		pg.strokeCap(PConstants.SQUARE);
		for (int i = 0; i < poly.size()-(close?0:1); i++) {
			ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
			polyss.add(polys);
			PVector p0 = poly.get(i);
			PVector p1 = poly.get((i+1)%poly.size());
			
			float a0 = PApplet.atan2(p1.y-p0.y,p1.x-p0.x);
			float a1 = a0 + PApplet.HALF_PI;
			
			float l = p0.dist(p1);
			int n = PApplet.ceil(l / s);
			if (n == 0) {
				continue;
			}
			for (int j = 0; j < n+1; j++) {
				float t = (float)j/(float)n;
				PVector p = p0.copy().lerp(p1, t);
				float x0 = p.x - d*PApplet.cos(a1);
				float y0 = p.y - d*PApplet.sin(a1);
				float x1 = p.x + d*PApplet.cos(a1);
				float y1 = p.y + d*PApplet.sin(a1);

				boolean lastOn = false;
				int m = PApplet.ceil(d)+1;
				int mmm = PApplet.min(20,m/3);
				
				pg.beginShape();
				for (int k = 0; k < m; k++) {
					float u = (float)k/(float)PApplet.max(1,m-1);
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
		pg.filter(PConstants.DILATE);

		polyss.add( new ArrayList<ArrayList<PVector>>());
		int mm = PApplet.ceil(PApplet.PI*(d*2)/s*PERPENDICULAR_STROKE_CAP_DENSITY_MULTIPLIER);
		
		if (!close) {
			for (int i = 0; i < poly.size(); i++) {
				
				ArrayList<ArrayList<PVector>> polys = polyss.get((i-1+poly.size())%poly.size());
				PVector p0 = poly.get(i);
				
				if ((i == 0 || i == poly.size()-1 ) && poly.size()>1) {
					float a;
					if (i == 0) {
						a = PApplet.atan2(poly.get(0).y-poly.get(1).y, poly.get(0).x-poly.get(1).x);
					}else {
						a = PApplet.atan2(poly.get(poly.size()-1).y-poly.get(poly.size()-2).y, poly.get(poly.size()-1).x-poly.get(poly.size()-2).x);
					}
					for (int j = 0; j < mm/2; j++) {
						float t = (float)j/(float)(mm/2);
						float x1 = p0.x+d*t*PApplet.cos(a);
						float y1 = p0.y+d*t*PApplet.sin(a);
						float cw = d*PApplet.sqrt(1-t*t);
						
						float xa = x1+cw*PApplet.cos(a-PConstants.HALF_PI);
						float ya = y1+cw*PApplet.sin(a-PConstants.HALF_PI);
						
						float xb = x1+cw*PApplet.cos(a+PConstants.HALF_PI);
						float yb = y1+cw*PApplet.sin(a+PConstants.HALF_PI);
						
						ArrayList<PVector> pp = new ArrayList<PVector>();
						pp.add(new PVector(xa,ya));
						pp.add(new PVector(xb,yb));
						polys.add(pp);

					}
					pg.pushStyle();
					pg.fill(255);
					pg.noStroke();
					pg.circle(poly.get(i).x,poly.get(i).y,d*2+2);
					pg.popStyle();				
				}
			}
			
			
		}
		
		
		
		for (int i = 0; i < poly.size(); i++) {
			
			ArrayList<ArrayList<PVector>> polys = polyss.get((i-1+poly.size())%poly.size());
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
				mmm = 0;

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
					if ((pg.get((int)(x2-bb.x),(int)(y2-bb.y))>>16&0xFF)<1) {
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
		
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		for (int _i = 0; _i < polyss.size(); _i++) {
			int i = (_i - 1 + polyss.size())%polyss.size();
			for (int j = 0; j < polyss.get(i).size(); j++) {
				if (j % 2 == 0) {
					Collections.reverse(polyss.get(i).get(j));
				}
				polys.add(polyss.get(i).get(j));
			}
		}
		pg.endDraw();
		float ml = PApplet.min(2,d-1);
		for (int i = polys.size()-1; i >= 0; i--) {
			if (polys.get(i).size() < 2) {
				polys.remove(i);
				continue;
			}
			if (polys.get(i).get(0).dist(polys.get(i).get(polys.get(i).size()-1))<ml) {
				polys.remove(i);
				continue;
			}
			for (int j = polys.get(i).size()-2; j > 0; j--) {
				polys.get(i).remove(j);
			}
		}
		if (!NO_CONNECT) {
			for (int i = 1; i < polys.size(); i++) {
				polys.get(0).addAll(polys.get(i));
			}
			for (int i = polys.size()-1; i>0; i--) {
				polys.remove(i);
			}
		}
//		app.image(pg,0,0);
		return polys;
	}
	
	
	public ArrayList<ArrayList<PVector>> strokePolyNormalAng(ArrayList<PVector> poly, float d, float s, float ang, boolean close){
//		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		ArrayList<ArrayList<ArrayList<PVector>>> polyss = new ArrayList<ArrayList<ArrayList<PVector>>>();
		
		float dd = PApplet.abs(d / PApplet.cos(ang));
		
		BBox bb = new BBox(poly);
		bb.x -= d*2;
		bb.y -= d*2;
		bb.w += d*4;
		bb.h += d*4;
		
		PGraphics pg = app.createGraphics((int)bb.w, (int)bb.h);
		pg.beginDraw();
		pg.background(0);
		pg.stroke(255);
		pg.strokeWeight(s*1.0f);
		pg.translate(-bb.x,-bb.y);
		pg.strokeCap(PConstants.SQUARE);
		for (int i = 0; i < poly.size()-(close?0:1); i++) {
			ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
			polyss.add(polys);
			
			PVector p0 = poly.get(i);
			PVector p1 = poly.get((i+1)%poly.size());
			
			float a0 = PApplet.atan2(p1.y-p0.y,p1.x-p0.x);
			float a1 = a0 + PApplet.HALF_PI + ang;
			
			float l = p0.dist(p1);
			int n = PApplet.ceil(l / s);
			if (n == 0) {
				continue;
			}
			for (int j = 0; j < n+1; j++) {
				float t = (float)j/(float)n;
				PVector p = p0.copy().lerp(p1, t);
				float x0 = p.x - dd*PApplet.cos(a1);
				float y0 = p.y - dd*PApplet.sin(a1);
				float x1 = p.x + dd*PApplet.cos(a1);
				float y1 = p.y + dd*PApplet.sin(a1);

				boolean lastOn = false;
				int m = PApplet.ceil(dd)+1;
				int mmm = PApplet.min(20,m/3);
				
				pg.beginShape();
				for (int k = 0; k < m; k++) {
					float u = (float)k/(float)PApplet.max(1,m-1);
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
		pg.filter(PConstants.DILATE);

		polyss.add( new ArrayList<ArrayList<PVector>>());
		int mm = PApplet.ceil(PApplet.PI*(d*2)/s*PERPENDICULAR_STROKE_CAP_DENSITY_MULTIPLIER);
		
		
		
		if (!close) {
			for (int i = 0; i < poly.size(); i++) {
				
				ArrayList<ArrayList<PVector>> polys = polyss.get((i-1+poly.size())%poly.size());
				PVector p0 = poly.get(i);
				
				if ((i == 0 || i == poly.size()-1 ) && poly.size()>1) {
					float a;
					if (i == 0) {
						a = PApplet.atan2(poly.get(0).y-poly.get(1).y, poly.get(0).x-poly.get(1).x);
					}else {
						a = PApplet.atan2(poly.get(poly.size()-1).y-poly.get(poly.size()-2).y, poly.get(poly.size()-1).x-poly.get(poly.size()-2).x);
					}
					a += ang;
					for (int j = 0; j < mm/2; j++) {
						float t = (float)j/(float)(mm/2);
						if (i == 0) {
							t = 1-t;
						}
						float x1 = p0.x+d*t*PApplet.cos(a);
						float y1 = p0.y+d*t*PApplet.sin(a);
						float cw = d*PApplet.sqrt(1-t*t)/PApplet.abs(PApplet.cos(ang));
						
						float xa = x1+cw*PApplet.cos(a-PConstants.HALF_PI);
						float ya = y1+cw*PApplet.sin(a-PConstants.HALF_PI);
						
						float xb = x1+cw*PApplet.cos(a+PConstants.HALF_PI);
						float yb = y1+cw*PApplet.sin(a+PConstants.HALF_PI);
						
						ArrayList<PVector> pp = new ArrayList<PVector>();
						pp.add(new PVector(xa,ya));
						pp.add(new PVector(xb,yb));
						polys.add(pp);

					}
					pg.pushStyle();
					pg.fill(255);
					pg.noStroke();
					pg.circle(poly.get(i).x,poly.get(i).y,d*2+2);
					pg.popStyle();				
				}
			}
			
			
		}
		
		for (int i = 0; i < poly.size(); i++) {
			ArrayList<ArrayList<PVector>> polys = polyss.get((i-1+poly.size())%poly.size());
			
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
				mmm = 0;
				
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
		
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		for (int _i = 0; _i < polyss.size(); _i++) {
			int i = (_i - 1 + polyss.size())%polyss.size();
			for (int j = 0; j < polyss.get(i).size(); j++) {
				if (j % 2 == 0) {
					Collections.reverse(polyss.get(i).get(j));
				}
				polys.add(polyss.get(i).get(j));
			}
		}
		pg.endDraw();
		float ml = PApplet.min(2,d-1);
		for (int i = polys.size()-1; i >= 0; i--) {
			if (polys.get(i).size() < 2) {
				polys.remove(i);
				continue;
			}
			if (polys.get(i).get(0).dist(polys.get(i).get(polys.get(i).size()-1))<ml) {
				polys.remove(i);
				continue;
			}
			for (int j = polys.get(i).size()-2; j > 0; j--) {
				polys.get(i).remove(j);
			}
		}
		if (!NO_CONNECT) {
			for (int i = 1; i < polys.size(); i++) {
				polys.get(0).addAll(polys.get(i));
			}
			for (int i = polys.size()-1; i>0; i--) {
				polys.remove(i);
			}
		}
//		app.image(pg,0,0);
		return polys;
	}
	
	/** draws stroke (outline) for (a) poly(gon/line)(s) using current global settings by the user.
	 *  returns nothing, because it draws to the design directly.
	 *  @param polys a set of polyline/polygons, those inside another and have a backward winding will be considered holes
	 *  @param close whether the polyline is considered as closed (polygon) or open (polyline)
	 */
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
			if (PApplet.abs(STROKE_LOCATION) > Float.MIN_VALUE && close) {
				float d = STROKE_WEIGHT/2*STROKE_LOCATION;
				if (d > 0) {
					polys = outsetPolygonsRaster(polys,d);
				}else {
					polys = insetPolygonsRaster(polys,PApplet.abs(d));
				}
			}
			if (STROKE_MODE == TANGENT) {
				int cnt = (int)STROKE_WEIGHT;
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
			}else if (STROKE_MODE == ANGLED) {
				for (int i = 0; i < polys.size(); i++) {
					ArrayList<ArrayList<PVector>> polys2 = strokePolyNormalAng(polys.get(i),(float)STROKE_WEIGHT/2.0f,STROKE_SPACING,STROKE_ANGLE,close);
					for (int j = 0; j < polys2.size(); j++) {
						pushPolyline(polys2.get(j),currentStroke,0f);
					}
				}
			}
			
		}
	}
	
	/** 
	 *  hatch a polygon with CONCENTRIC (a.k.a inset) mode (using vector math).
	 *  a simplified version of hatchInset(4) where orientation of polygon is always checked
	 *  @param poly    the polygon
	 *  @param d       hatch spacing
	 *  @param maxIter maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required
	 *  @return        the hatching as an array of polys
	 */
	public ArrayList<ArrayList<PVector>> hatchInset(ArrayList<PVector> poly, float d, int maxIter){
		return hatchInset(poly,d,maxIter,true);
	}

	/** 
	 *  hatch a polygon with CONCENTRIC (a.k.a inset) mode (using vector math).
	 *  @param poly              the polygon
	 *  @param d                 hatch spacing
	 *  @param maxIter           maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required
	 *  @param checkOrientation  make sure the polygon is rightly oriented, this should be on for user provided input, otherwise it might hatch outwards
	 *  @return                  the hatching as an array of polys
	 */
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

	/** 
	 *  hatch a polygon with SPIRAL mode (using vector math).
	 *  a simplified version of hatchSpiral(4) where orientation of polygon is always checked
	 *  @param poly    the polygon
	 *  @param d       hatch spacing
	 *  @param maxIter maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required
	 *  @return        the hatching as an array of polys
	 */
	public ArrayList<ArrayList<PVector>> hatchSpiral(ArrayList<PVector> poly, float d, int maxIter, boolean reverse){
		return hatchSpiral(poly,d,maxIter,true,reverse);
	}
	/** 
	 *  hatch a polygon with SPIRAL mode (using vector math).
	 *  @param poly    the polygon
	 *  @param d       hatch spacing
	 *  @param maxIter maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required
	 *  @param checkOrientation  make sure the polygon is rightly oriented, this should be on for user provided input, otherwise it might hatch outwards
	 *  @return        the hatching as an array of polys
	 */
	public ArrayList<ArrayList<PVector>> hatchSpiral(ArrayList<PVector> poly, float d, int maxIter, boolean checkOrientation, boolean reverse){

		if (!polygonOrientation(poly) && checkOrientation) {
			poly = new ArrayList<PVector>(poly);
			Collections.reverse(poly);
		}

		float l = 0;
		for (int i = 0; i < poly.size()-1; i++) {
			l += poly.get(i).dist(poly.get(i+1));
		}

		int n = 100*((int)Math.ceil((float)l / (float)STITCH_LENGTH));

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
			if (reverse) {
				Collections.reverse(polys2.get(i));
			}
		}
		ArrayList<ArrayList<PVector>> spirals = new ArrayList<ArrayList<PVector>>();
//		spirals.addAll(polys2);
		ArrayList<PVector> spiral = new ArrayList<PVector>();
		for (int i = 0; i < polys2.size(); i++) {
			for (int j = 0; j < n; j++) {
				PVector p0 = polys2.get(i).get(j);
				PVector p1;

				if (i == polys2.size()-1) {
					if (polys.size() >= 1 && polys2.size() >= 1 && i > 0) {
						PVector a = p0.copy().sub(polys2.get(i-1).get(Math.max(j-1,0)));
						PVector b = p0.copy().sub(polys2.get(i-1).get(Math.max(j-1,0)));
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

		
		if (spiral.size() >= 2) {
			spirals.add(spiral);
		}

		for (int i = 0; i < polys.size(); i++) {
			spirals.addAll(hatchSpiral(polys.get(i),d,maxIter,false));
		}

		return spirals;
	}
	
	public ArrayList<ArrayList<PVector>> hatchSpiral_v2(ArrayList<PVector> poly, float d){
		
		ArrayList<ArrayList<PVector>> ret = new ArrayList<ArrayList<PVector>>();
		ArrayList<PVector> spiral = new ArrayList<PVector>();
		
		BBox bb = new BBox(poly);
		bb.x-=2;
		bb.y-=2;
		bb.w+=4;
		bb.h+=4;
		
		bb.w += bb.x;
		bb.h += bb.y;
		bb.x = 0;
		bb.y = 0;
		
		
		PVector c = centerpoint(poly);
		float r = PApplet.max(bb.w,bb.h)*200f;
		
		PGraphics pg = app.createGraphics((int)PApplet.ceil(bb.w), (int)PApplet.ceil(bb.h));

		float dd = d;
		float a = 0;
		for (int k = 0; k < 9999; k++) {
			
			pg.beginDraw();
			pg.pushMatrix();
			pg.background(0);
			pg.stroke(0);
			pg.fill(255);
			pg.strokeWeight(dd);

			pg.beginShape();
			for (int i = 0; i < poly.size(); i++) {
				pg.vertex(poly.get(i).x-bb.x,poly.get(i).y-bb.y);
			}
			pg.endShape(PConstants.CLOSE);
			pg.popMatrix();
			pg.endDraw();
//			app.image(pg,k*100,0);
//			pg.save("/Users/studio/Downloads/hsv2-"+k+".png");
			ArrayList<ArrayList<PVector>> conts = PEmbroiderTrace.findContours(pg);
			for (int i = 0; i < conts.size(); i++) {
				conts.set(i, PEmbroiderTrace.approxPolyDP(conts.get(i),2));
			}
//			PApplet.println(k,conts.size());
			if (conts.size() == 0) {
				break;
			} else if (conts.size() > 1) {
				for (int i = 1; i < conts.size(); i++) {
//					ret.addAll(hatchSpiral_v2(conts.get(i),d));
				}
			}
			
			ArrayList<PVector> ppp = conts.get(0);
			
			ArrayList<PVector> poly2 = new ArrayList<PVector>();
			for (int i = 0; i < ppp.size(); i++) {
				poly2.add(new PVector(ppp.get(i).x+bb.x,ppp.get(i).y+bb.y));
			}
			
			
//			ret.add(poly2);
			
			PVector p = new PVector(c.x+r*PApplet.cos(a),c.y+r*PApplet.sin(a));
			
			ArrayList<PVector> qs = segmentIntersectPolygon(c,p,ppp);
			if (qs.size()>0) {
				PVector q = qs.get(0);
				
//				PApplet.println(q);
//				app.rect(qs.get(0).x,qs.get(0).y,20,20);
				spiral.add(new PVector(q.x+bb.x,q.y+bb.y));
			}else {
//				PApplet.println("???",c,p);
			}
			c = centerpoint(ppp);
			a += 0.1;
			dd += d;
		}
		ret.add(spiral);
		return ret;
	}
	public ArrayList<ArrayList<PVector>> hatchSpiral_v3(ArrayList<PVector> poly, float d, int maxIter, boolean checkOrientation, boolean reverse){
		ArrayList<ArrayList<PVector>> spirals = new ArrayList<ArrayList<PVector>>();
		
		if (!polygonOrientation(poly) && checkOrientation) {
			poly = new ArrayList<PVector>(poly);
			Collections.reverse(poly);
		}

		
		BBox bb = new BBox(poly);
		bb.x-=2;
		bb.y-=2;
		bb.w+=4;
		bb.h+=4;
		
		bb.w += bb.x;
		bb.h += bb.y;
		bb.x = 0;
		bb.y = 0;
		
		PGraphics pg = app.createGraphics((int)PApplet.ceil(bb.w), (int)PApplet.ceil(bb.h));

		float dd = 0;

		
		ArrayList<ArrayList<PVector>> polys2 = new ArrayList<ArrayList<PVector>>();
//		polys2.add(poly);
		
		ArrayList<ArrayList<PVector>> gone = new ArrayList<ArrayList<PVector>>();
		
		for (int k = 0; k < 9999; k++) {
			
			pg.beginDraw();
			pg.pushMatrix();
			pg.background(0);
			pg.stroke(0);
			pg.fill(255);
			pg.strokeWeight(dd);
			pg.strokeJoin(PConstants.ROUND);

			pg.beginShape();
			for (int i = 0; i < poly.size(); i++) {
				pg.vertex(poly.get(i).x-bb.x,poly.get(i).y-bb.y);
			}
			pg.endShape(PConstants.CLOSE);
			
			for (int i = 0; i < gone.size(); i++) {
				pg.noStroke();
				pg.fill(0);
				pg.strokeJoin(PConstants.ROUND);
				pg.beginShape();
				for (int j = 0; j < gone.get(i).size();j ++) {
					pg.vertex(gone.get(i).get(j).x,gone.get(i).get(j).y);
				}
				
				pg.endShape(PConstants.CLOSE);
			}
			
			pg.popMatrix();
			pg.endDraw();
			ArrayList<ArrayList<PVector>> conts = PEmbroiderTrace.findContours(pg);
			for (int i = 0; i < conts.size(); i++) {
				conts.set(i, PEmbroiderTrace.approxPolyDP(conts.get(i),2f));
			}
			if (conts.size() == 0) {
				break;
			}
			
			if (conts.size() > 1) {
				for (int i = 0; i < conts.size(); i++) {
					spirals.addAll(hatchSpiral_v3(conts.get(i),d,maxIter,checkOrientation,reverse));
					gone.add(conts.get(i));
				}
				break;
			}
			polys2.add(conts.get(0));
			if (k == 0) {
				dd += d;
			}else {
				dd += d*2;
			}
		}
		
		float l = 0;
		for (int i = 0; i < poly.size()-1; i++) {
			l += poly.get(i).dist(poly.get(i+1));
		}

		int n = 20*((int)Math.ceil((float)l / (float)STITCH_LENGTH));


		for (int i = polys2.size()-1; i >= 0; i--) {
			if (polys2.get(i).size() <= 2) {
				polys2.remove(i);
				continue;
			}
//			polys2.get(i).add(polys2.get(i).get(0));
			polys2.set(i,resampleN(polys2.get(i),n));
			if (reverse) {
				Collections.reverse(polys2.get(i));
			}
		}
		
//		spirals.addAll(polys2);
		ArrayList<PVector> spiral = new ArrayList<PVector>();
		for (int i = 1; i < polys2.size(); i++) {
			int jo = 0;
			if (i > 0) {
				jo = rotatePolygonToMatch(polys2.get(i-1),polys2.get(i));
			}
			
			for (int j = 0; j < n; j++) {
				PVector p0 = polys2.get(i).get((j+jo)%polys2.get(i).size());
				PVector p1;

				if (i == polys2.size()-1) {
					if (polys2.size() >= 1 && i > 0) {

						p1 = polys2.get(i-1).get(j);
					}else {
						p1 = p0.copy();
					}
				}else {
					p1 = polys2.get(i+1).get(j);
				}
				float t = (float)j/(float)n;
				spiral.add(p0.copy().mult(1-t).add(p1.copy().mult(t)));
			}
//			spirals.add(polys2.get(i));
		}

		
		if (spiral.size() >= 2) {
			spirals.add(spiral);
		}

		return spirals;
	}
	
	public ArrayList<ArrayList<PVector>> hatchSpiral_v4(ArrayList<PVector> poly, float d, int maxIter, boolean checkOrientation, boolean reverse){
		ArrayList<ArrayList<PVector>> spirals = new ArrayList<ArrayList<PVector>>();
		
		if (!polygonOrientation(poly) && checkOrientation) {
			poly = new ArrayList<PVector>(poly);
			Collections.reverse(poly);
		}

		
		BBox bb = new BBox(poly);
		bb.x-=2;
		bb.y-=2;
		bb.w+=4;
		bb.h+=4;
		
		bb.w += bb.x;
		bb.h += bb.y;
		bb.x = 0;
		bb.y = 0;
		
		PGraphics pg = app.createGraphics((int)PApplet.ceil(bb.w), (int)PApplet.ceil(bb.h));

		float dd = 0;

		
		ArrayList<ArrayList<PVector>> polys2 = new ArrayList<ArrayList<PVector>>();
//		polys2.add(poly);
		
		ArrayList<ArrayList<PVector>> gone = new ArrayList<ArrayList<PVector>>();
		
		for (int k = 0; k < 9999; k++) {
			
			pg.beginDraw();
			pg.pushMatrix();
			pg.background(0);
			pg.stroke(0);
			pg.fill(255);
			pg.strokeWeight(dd);
			pg.strokeJoin(PConstants.ROUND);

			pg.beginShape();
			for (int i = 0; i < poly.size(); i++) {
				pg.vertex(poly.get(i).x-bb.x,poly.get(i).y-bb.y);
			}
			pg.endShape(PConstants.CLOSE);
			
			for (int i = 0; i < gone.size(); i++) {
				pg.noStroke();
				pg.fill(0);
				pg.strokeJoin(PConstants.ROUND);
				pg.beginShape();
				for (int j = 0; j < gone.get(i).size();j ++) {
					pg.vertex(gone.get(i).get(j).x,gone.get(i).get(j).y);
				}
				
				pg.endShape(PConstants.CLOSE);
			}
			
			pg.popMatrix();
			pg.endDraw();
			ArrayList<ArrayList<PVector>> conts = PEmbroiderTrace.findContours(pg);
			for (int i = 0; i < conts.size(); i++) {
				conts.set(i, PEmbroiderTrace.approxPolyDP(conts.get(i),2f));
			}
			if (conts.size() == 0) {
				break;
			}
			
			if (conts.size() > 1) {
				for (int i = 0; i < conts.size(); i++) {
					spirals.addAll(hatchSpiral_v4(conts.get(i),d,maxIter,checkOrientation,reverse));
					gone.add(conts.get(i));
				}
				break;
			}
			polys2.add(conts.get(0));
			if (k == 0) {
				dd += d;
			}else {
				dd += d*2;
			}
		}
		
		float l = 0;
		for (int i = 0; i < poly.size()-1; i++) {
			l += poly.get(i).dist(poly.get(i+1));
		}

		int n = 100*((int)Math.ceil((float)l / (float)STITCH_LENGTH));


		for (int i = polys2.size()-1; i >= 0; i--) {
			if (polys2.get(i).size() <= 2) {
				polys2.remove(i);
				continue;
			}
//			polys2.get(i).add(polys2.get(i).get(0));
			polys2.set(i,resampleN(polys2.get(i),n));
			if (reverse) {
				Collections.reverse(polys2.get(i));
			}
		}
		
//		spirals.addAll(polys2);
		ArrayList<PVector> spiral = new ArrayList<PVector>();
		int jo = 0;
		for (int i = 0; i < polys2.size(); i++) {

			if (i > 0) {
				jo += rotatePolygonToMatch(polys2.get(i-1),polys2.get(i));
			}
			
			spiral.add(polys2.get(i).get((i+jo)%polys2.get(i).size()));
			
//			spirals.add(polys2.get(i));
		}

		
		if (spiral.size() >= 2) {
			spirals.add(spiral);
		}

		return spirals;
	}
	
	/** 
	 *  Hatch a polygon with PERLIN mode.
	 *  The vector frontend to perlinField
	 *  @param poly     the polygon
	 *  @param d        hatch spacing
	 *  @param len      the length of a step when walking the vector field
	 *  @param scale    scale of the perlin noise
	 *  @param maxIter  maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners
	 *  @return         the hatching as an array of polys
	 */
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
	/** 
	 *  Hatch a polygon with custom vector field
	 *  The vector frontend to customField
	 *  @param poly     the polygon
	 *  @param vf       the vector field
	 *  @param d        hatch spacing
	 *  @param len      the length of a step when walking the vector field
	 *  @param maxIter  maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners
	 *  @return         the hatching as an array of polys
	 */
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

	/** 
	 *  Hatch a polygon with PARALLEL mode (using vector math)
	 *  @param poly     the polygon
	 *  @param ang      the angle of hatch lines
	 *  @param d        hatch spacing
	 *  @return         the hatching as an array of polys
	 */
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
	
	/** 
	 *  Hatch a complex polygon (with holes) with PARALLEL mode (using vector math)
	 *  @param polys    a set of polygons, those inside another will be considered holes. Holes are determined according to the even-odd rule
	 *  @param ang      the angle of hatch lines
	 *  @param d        hatch spacing
	 *  @return         the hatching as an array of polys
	 */
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
	
	/** 
	 *  Hatch a polygon with the DRUNK style (not a particularly useful style, just to showcase how easy it is to add a new one).
	 *  The vector implementation
	 *  @param poly     the polygon
	 *  @param rad      max size of each drunken step in each direction
	 *  @param maxIter  maximum number of iterations to do the drunk walk
	 *  @return         the hatching as an array of polys
	 */
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
	
	/** 
	 *  Hatch a polygon with the DRUNK style (not a particularly useful style, just to showcase how easy it is to add a new one).
	 *  The raster implementation
	 *  @param poly     the polygon
	 *  @param rad      max size of each drunken step in each direction
	 *  @param maxIter  maximum number of iterations to do the drunk walk
	 *  @return         the hatching as an array of polys
	 */
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
	
	/** 
	 *  Resample a polyline to make it stitchable
	 *  @param poly                the polyline
	 *  @param minLen              minimum length of each segment, segment shorter than this will be downsampled
	 *  @param maxLen              maximum length of each segment (not counting the randomization added on top, specified by randomize) 
	 *  @param randomize           amount of randomization in stitch length to avoid alignment patterns. 0 for none
	 *  @param randomizeOffset     amount of randomization to add to the offset of the first stitch in every polyline. 0 for none
	 *  @return                    the resampled polyline
	 */
	public ArrayList<PVector> resample(ArrayList<PVector> poly, float minLen, float maxLen, float randomize, float randomizeOffset) {
		float maxTurn = RESAMPLE_MAXTURN;
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		if (poly.size() > 0) {
			poly2.add(poly.get(0));
		}else {
			return poly;
		}

		float clen = 0;
//		PApplet.println(minLen);
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

	/** 
	 *  Resample a polyline to make it have N vertices. Can be an upsample or a downsample.
	 *  Each resultant segment will be the same length, +/- floating point percision errors
	 *  @param poly   the polyline
	 *  @param n      the desired number of vertices
	 *  @return       the resampled polyline
	 */
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

	/** 
	 *  Resample a polyline to make it have N vertices, while keeping important turning points. Can be an upsample or a downsample.
	 *  Each resultant segment have approximately same length, but compromising to the preserving of corners
	 *  @param poly   the polyline
	 *  @param n      the desired number of vertices
	 *  @return       the resampled polyline
	 */
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
	
	/** 
	 *  Resample a set of (parallel) polylines by intersecting them with another bunch of parallel lines and using the intersections to break them up.
	 *  Useful for parallel hatching.
	 *  @param polys   the polylines
	 *  @param angle   the angle of the original polylines
	 *  @param spacing the spacing of the original polylines
	 *  @param len     the desired length of each segment after resampling
	 *  @return        the resampled polylines
	 */
	public ArrayList<ArrayList<PVector>> resampleCrossIntersection(ArrayList<ArrayList<PVector>> polys, float angle, float spacing, float len, float offsetFactor /*0.5*/, float randomize){
//		for (int i = 0; i < polys.size(); i++) {
//			for (int j = 0; j < polys.get(i).size(); j++) {
//				PApplet.println(polys.get(i).get(j));
//				
//			}
//		}
		float base = len*offsetFactor;
		float relang = PApplet.atan2(spacing, base);
//		float d = PApplet.sqrt(base*base+spacing*spacing);
		float d = len * PApplet.cos(PApplet.HALF_PI-relang);	
		
//		PApplet.println("computed cross ang",relang,"spacing",d);
		
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
					float t0 = iparamsArr[k];
					float t1 = iparamsArr[PApplet.min(k+1,iparams.size()-1)];
					float tr = app.random(t0,t1);
					float t = PApplet.lerp(t0, tr, randomize);
					resamped.add( _p0.mult(1-t).add(_p1.mult(t)) );
				}
				resamped.add(b);
			}
			
			result.add(resamped);
			
		}
		return result;
	}
	
	public  ArrayList<ArrayList<PVector>> resampleCrossIntersection2(ArrayList<ArrayList<PVector>> polys1, ArrayList<ArrayList<PVector>> polys2, float angle1, float angle2, float spacing, float len, float randomize){
		PVector mid = null;
		float dh = 0f;
		for (int i = 0; i < polys1.size(); i++) {
			for (int j = 0; j < polys2.size()-1; j++) {
				PVector a = polys1.get(i).get(0);
				PVector b = polys1.get(i).get(polys1.get(i).size()-1);
				
				PVector c = polys2.get(j).get(0);
				PVector d = polys2.get(j).get(polys2.get(j).size()-1);
				
				PVector e = polys2.get(j+1).get(0);
				PVector f = polys2.get(j+1).get(polys2.get(j).size()-1);
				
				PVector is0 = segmentIntersect3D(a,b,c,d);
				PVector is1 = segmentIntersect3D(a,b,e,f);
				if (is0 != null && is1 != null) {
					float t = is0.x*0.5f+is1.x*0.5f;
					dh = (is0.x-is1.x)*a.dist(b);
					mid = a.copy().mult(1-t).add(b.copy().mult(t));
					break;
				}
			}
			if (mid != null) {
				break;
			}
		}
		polys1.addAll(polys2);
//		PApplet.println(mid);
		if (mid == null) {
			
			return polys1;
		}
		float rmax = 0;
		for (int i = 0; i < polys1.size(); i++) {
			for (int j = 0; j < polys1.get(i).size(); j++) {
				rmax = Math.max(rmax, mid.dist(polys1.get(i).get(j)));
			}
		}
		for (int i = 0; i < polys2.size(); i++) {
			for (int j = 0; j < polys2.get(i).size(); j++) {
				rmax = Math.max(rmax, mid.dist(polys2.get(i).get(j)));
			}
		}
		float ang = lerp360(angle1*180f/PConstants.PI,angle2*180f/PConstants.PI,0.5f)*PConstants.PI/180f;
		ang += PConstants.HALF_PI;
		
		float bigang = (angle1-angle2+PConstants.PI*100) % PConstants.PI;
		if (bigang < PConstants.HALF_PI) {
			bigang = PConstants.PI - bigang;
		}
		float alpha = bigang/2;
		float d = (dh/2 * PApplet.sin(alpha))*2;
		
		float mult = PApplet.max(1f,PApplet.floor(len/dh));
//		PApplet.println(d);
		d*= mult;
		
		BCircle bcirc = new BCircle(0,0,0);
		bcirc.x = mid.x;
		bcirc.y = mid.y;
		int n = PApplet.ceil(rmax/d);
		bcirc.r = n*d;
		
//		n = 1;
		float x0 = bcirc.x - bcirc.r * PApplet.cos(ang);
		float y0 = bcirc.y - bcirc.r * PApplet.sin(ang);

		float x1 = bcirc.x + bcirc.r * PApplet.cos(ang);
		float y1 = bcirc.y + bcirc.r * PApplet.sin(ang);

		ArrayList<ArrayList<PVector>> crosslines = new ArrayList<ArrayList<PVector>>();
		
		n*=2;
//		n = 1;
		for (int i = 0; i < n; i++) {
			float t = (float)i/(float)PApplet.max(1,n);
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
//		return crosslines;
		
		ArrayList<ArrayList<PVector>> result = new ArrayList<ArrayList<PVector>>();
		
		for (int i = 0; i < polys1.size(); i++) {
			if (polys1.get(i).size() < 2) {
				continue;
			}

			ArrayList<PVector> resamped = new ArrayList<PVector>();
			
			for (int j = 0; j < polys1.get(i).size()-1; j++) {
				
				PVector a = polys1.get(i).get(j);
				PVector b = polys1.get(i).get(j+1);
				
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
					float t0 = iparamsArr[k];
					float t1 = iparamsArr[PApplet.min(k+1,iparams.size()-1)];
					float tr = app.random(t0,t1);
					float t = PApplet.lerp(t0, tr, randomize);
					resamped.add( _p0.mult(1-t).add(_p1.mult(t)) );
				}
				resamped.add(b);
			}
			
			result.add(resamped);
			
		}
		
		return result;
	}


	/** 
	 *  Draw an ellipse using global settings.
	 *  The unambigous backend for user-facing ellipse(), the ambigous one which can be affected by ellipseMode().
	 *  Returns nothing because the result is directly pushed to the design.
	 *  @param cx      the x coordinate of the center
	 *  @param cy      the y coordinate of the center
	 *  @param rx      the radius in the x axis
	 *  @param ry      the radius in the y axis
	 */
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
	
	void _arc(float cx, float cy, float rx, float ry, float start, float stop, int mode) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		for (int i = 0; i < CIRCLE_DETAIL; i++) {
			float a = start+((float)i/(float)(CIRCLE_DETAIL-1))*(stop-start);
			float x = cx + rx * PApplet.cos(a);
			float y = cy + ry * PApplet.sin(a);
			poly.add(new PVector(x,y));
		}
		if (mode == PConstants.CHORD && poly.size()>0) {
			poly.add(poly.get(0));
		}else if (mode == PConstants.PIE) {
			poly.add(new PVector(cx,cy));
			poly.add(poly.get(0));
		}
		polyBuff.clear();
		polyBuff.add(poly);
		endShape(false);
	}
	
	
	/** 
	 *  Draw a rectangle using global settings.
	 *  The unambigous backend for user-facing rect(), the ambigous one which can be affected by rectMode().
	 *  Returns nothing because the result is directly pushed to the design.
	 *  @param x left
	 *  @param y top
	 *  @param w width
	 *  @param h height
	 */
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
	void _roundedRect(float x, float y, float w, float h, float tl, float tr, float br, float bl) {
		int n = 10;
		ArrayList<PVector> poly = new ArrayList<PVector>();
//		poly.add(new PVector(x,y+tl));
		
		for (int i = 0; i < n+1; i++) {
			float a = ((float)i/(float)n)*PApplet.HALF_PI;
			float xx = x+tl-PApplet.cos(a)*tl;
			float yy = y+tl-PApplet.sin(a)*tl;
			poly.add(new PVector(xx,yy));
		}
		
		
//		poly.add(new PVector(x+w-tr,y));
		
		for (int i = 0; i < n+1; i++) {
			float a = ((float)i/(float)n)*PApplet.HALF_PI;
			float xx = x+w-tr+PApplet.sin(a)*tr;
			float yy = y+tr-PApplet.cos(a)*tr;
			poly.add(new PVector(xx,yy));
		}
		
//		poly.add(new PVector(x+w,y+h-br));
		
		for (int i = 0; i < n+1; i++) {
			float a = ((float)i/(float)n)*PApplet.HALF_PI;
			float xx = x+w-br+PApplet.cos(a)*br;
			float yy = y+h-br+PApplet.sin(a)*br;
			poly.add(new PVector(xx,yy));
		}
		
//		poly.add(new PVector(x+bl,y+h));
		
		for (int i = 0; i < n+1; i++) {
			float a = ((float)i/(float)n)*PApplet.HALF_PI;
			float xx = x+bl-PApplet.sin(a)*bl;
			float yy = y+h-bl+PApplet.cos(a)*bl;
			poly.add(new PVector(xx,yy));
		}
		
		
		polyBuff.clear();
		polyBuff.add(poly);
		endShape(true);
	}

	/* ======================================== SHAPE INTERFACE ======================================== */
	
	/** 
	 *  Hatch a polygon with global user settings (using vector math).
	 *  Returns nothing because the result is directly pushed to the design.
	 *  @param poly the polygon
	 */
	float calcAxisAngleForParallel(float ang) {
		return PConstants.HALF_PI-ang;
	}
	
	public void hatch(ArrayList<PVector> poly) {
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		float hatch_angle = HATCH_ANGLE;
		float hatch_angle2 = HATCH_ANGLE2;
		if (AUTO_HATCH_ANGLE) {
			hatch_angle = calcPolygonTilt(poly);
			hatch_angle2 = hatch_angle+PConstants.HALF_PI;
		}
		hatch_angle = calcAxisAngleForParallel(hatch_angle);
		hatch_angle2 = calcAxisAngleForParallel(hatch_angle2);
		if (HATCH_MODE == PARALLEL) {
			polys = hatchParallel(poly,hatch_angle,HATCH_SPACING);
		}else if (HATCH_MODE == CROSS) {
			polys = hatchParallel(poly,hatch_angle, HATCH_SPACING);
			polys.addAll(hatchParallel(poly,hatch_angle2,HATCH_SPACING));
		}else if (HATCH_MODE == CONCENTRIC) {
			polys = hatchInset(poly,HATCH_SPACING,9999);
			for (int i = 0; i < polys.size(); i++) {
				polys.get(i).add(polys.get(i).get(0));
			}
		}else if (HATCH_MODE == SPIRAL) {
//			polys = hatchSpiral(poly,HATCH_SPACING,9999,HATCH_SPIRAL_DIRECTION == CCW);
//			polys = hatchSpiral_v2(poly,HATCH_SPACING);
			polys = hatchSpiral_v3(poly,HATCH_SPACING,9999,true,HATCH_SPIRAL_DIRECTION == CCW);
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
	
	/** 
	 *  Hatch a raster image with global user settings.
	 *  Returns nothing because the result is directly pushed to the design.
	 *  @param im   a processing image, PGraphics also qualify
	 *  @param x    x coordinate of upper left corner to start drawing
	 *  @param y    y coordinate of upper left corner to start drawing
	 */
	public void hatchRaster(PImage im, float x, float y) {
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		
		float hatch_angle = HATCH_ANGLE;
		float hatch_angle2 = HATCH_ANGLE2;
		if (AUTO_HATCH_ANGLE) {
			hatch_angle = calcPolygonTiltRaster(im);
			hatch_angle2 = hatch_angle+PConstants.HALF_PI;
		}
		if (HATCH_MODE != SATIN) {
			hatch_angle = calcAxisAngleForParallel(hatch_angle);
			hatch_angle2 = calcAxisAngleForParallel(hatch_angle2);
		}
		boolean didit = false;
		if (HATCH_MODE == PARALLEL) {
			polys = hatchParallelRaster(im,hatch_angle,HATCH_SPACING,1);
			if (!NO_RESAMPLE) {
				polys = resampleCrossIntersection(polys,hatch_angle,HATCH_SPACING,STITCH_LENGTH/getCurrentScale(hatch_angle+PApplet.HALF_PI), PARALLEL_RESAMPLING_OFFSET_FACTOR, RESAMPLE_NOISE);
				NO_RESAMPLE = true;
				didit = true;
			}
		}else if (HATCH_MODE == CROSS) {
			polys = hatchParallelRaster(im,hatch_angle,HATCH_SPACING,1);
			polys.addAll(hatchParallelRaster(im,hatch_angle2,HATCH_SPACING,1));
			
		}else if (HATCH_MODE == CONCENTRIC) {
			polys = isolines(im,HATCH_SPACING);
		}else if (HATCH_MODE == SPIRAL) {
			polys = isolines(im,HATCH_SPACING);
		}else if (HATCH_MODE == PERLIN) {
			polys = perlinField(im, HATCH_SPACING, 0.01f*HATCH_SCALE, STITCH_LENGTH, 3, 100, 9999);
		}else if (HATCH_MODE == VECFIELD) {
			polys = customField(im,HATCH_VECFIELD,HATCH_SPACING,3,100,9999);
		}else if (HATCH_MODE == SATIN) {
			if (!NO_RESAMPLE) {
				didit = true;
				NO_RESAMPLE = true;
			}
			polys = PEmbroiderHatchSatin.hatchSatinAngledRaster(im,hatch_angle,HATCH_SPACING,PApplet.max(1,(int)PApplet.ceil(STITCH_LENGTH/2)));
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
		if (didit) {
			NO_RESAMPLE = false;
		}
	}
	/** 
	 *  Hatch a raster image with global user settings.
	 *  Returns nothing because the result is directly pushed to the design.
	 *  Simplified version of hatchRaster(3), draws at 0,0
	 *  @param im   a processing image, PGraphics also qualify
	 */
	public void hatchRaster(PImage im) {
		hatchRaster(im,0,0);
	}
	
	/** 
	 *  Compute a point on a rational quadratic bezier curve
	 *  @param p0   first point
	 *  @param p1   control point
	 *  @param p2   last point
	 *  @param w    weight of the rational bezier, higher the weight, pointier the turning
	 *  @param t    the interpolation parameter (generally 0-1)
	 */
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
	/** 
	 *  Compute a point on a quadratic bezier curve
	 *  @param p0   first point
	 *  @param p1   control point
	 *  @param p2   last point
	 *  @param t    the interpolation parameter (generally 0-1)
	 */
	PVector quadraticBezier(PVector p0, PVector p1, PVector p2, float t) {
		return p0.copy().mult(PApplet.pow(1-t,2)).add(p1.copy().mult(2*(1-t)*t)).add(p2.copy().mult(t*t));
	}
	/** 
	 *  Compute a point on a cubic bezier curve
	 *  @param p0   first point
	 *  @param p1   control point
	 *  @param p2   another control point
	 *  @param p3   last point
	 *  @param t    the interpolation parameter (generally 0-1)
	 */
	PVector cubicBezier(PVector p0, PVector p1, PVector p2, PVector p3, float t) {
		return p0.copy().mult(PApplet.pow(1-t, 3)).add(p1.copy().mult(t*3*PApplet.pow(1-t, 2))).add(p2.copy().mult(3*(1-t)*t*t)).add(p3.copy().mult(t*t*t));
	}
	
	/** 
	 *  Compute a point on a higher-order bezier curve
	 *  @param P    points and control points
	 *  @param t    the interpolation parameter (generally 0-1)
	 */
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
	public float catmullromSplineGetT(float t, PVector p0, PVector p1, float alpha){
	    float a = PApplet.pow((p1.x-p0.x), 2.0f) + PApplet.pow((p1.y-p0.y), 2.0f);
	    float b = PApplet.pow(a, alpha * 0.5f);
	   
	    return (b + t);
	}
	public ArrayList<PVector> catmullRomSpline(PVector p0, PVector p1, PVector p2, PVector p3, int numberOfPoints, float alpha){
		//https://en.wikipedia.org/wiki/Centripetal_Catmull–Rom_spline
		ArrayList<PVector> newPoints = new ArrayList<PVector>();

		
		if (p0.x == p1.x && p0.y == p1.y) {
			p0.x += 0.001;
		}
		if (p1.x == p2.x && p1.y == p2.y) {
			p1.x += 0.001;
		}
		if (p2.x == p3.x && p2.y == p3.y) {
			p2.x += 0.001;
		}
		
		float t0 = 0.0f;
		float t1 = catmullromSplineGetT(t0, p0, p1,alpha);
		float t2 = catmullromSplineGetT(t1, p1, p2,alpha);
		float t3 = catmullromSplineGetT(t2, p2, p3,alpha);

		for (float t=t1; t<t2; t+=((t2-t1)/(float)numberOfPoints))
		{
		    PVector A1 = p0.copy().mult((t1-t)/(t1-t0)) .add( p1.copy().mult((t-t0)/(t1-t0)) );
		    PVector A2 = p1.copy().mult((t2-t)/(t2-t1)) .add( p2.copy().mult((t-t1)/(t2-t1)) );
		    PVector A3 = p2.copy().mult((t3-t)/(t3-t2)) .add( p3.copy().mult((t-t2)/(t3-t2)) );
		    
		    PVector B1 = A1.copy().mult((t2-t)/(t2-t0)) .add( A2.copy().mult((t-t0)/(t2-t0)) );
		    PVector B2 = A2.copy().mult((t3-t)/(t3-t1)) .add( A3.copy().mult((t-t1)/(t3-t1)) );
		    PVector C = B1.copy().mult((t2-t)/(t2-t1)) .add( B2.copy().mult((t-t1)/(t2-t1)) );
//		    PApplet.println(C);
		    newPoints.add(C);
		}
		return newPoints;
	}
	/** 
	 *  Draw a ellipse
	 *  @param a   the first parameter, the meaning of which depends on ellipseMode
	 *  @param b   the second parameter, the meaning of which depends on ellipseMode
	 *  @param c   the third parameter, the meaning of which depends on ellipseMode
	 *  @param d   the fourth parameter, the meaning of which depends on ellipseMode
	 */
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

	public void arc(float a, float b, float c, float d, float start, float stop, int mode) {
		if (ELLIPSE_MODE == PConstants.CORNER) {
			_arc(a + c / 2, b + d / 2, c / 2, d / 2, start, stop, mode);
		} else if (ELLIPSE_MODE == PConstants.CORNERS) {
			_arc((a + c) / 2, (b + d) / 2, (c - a) / 2, (d - b) / 2, start, stop, mode);
		} else if (ELLIPSE_MODE == PConstants.CENTER) {
			_arc(a, b, c / 2, d / 2, start, stop, mode);
		} else if (ELLIPSE_MODE == PConstants.RADIUS) {
			_arc(a, b, c, d, start, stop, mode);
		}
	}
	public void arc(float a, float b, float c, float d, float start, float stop) {
		arc(a,b,c,d,start,stop,PConstants.OPEN);
	}

	/** 
	 *  Draw a rectangle
	 *  @param a   the first parameter, the meaning of which depends on rectMode
	 *  @param b   the second parameter, the meaning of which depends on rectMode
	 *  @param c   the third parameter, the meaning of which depends on rectMode
	 *  @param d   the fourth parameter, the meaning of which depends on rectMode
	 */
	public void rect(float a, float b, float c, float d) {
		if (RECT_MODE == PConstants.CORNER) {
			_rect(a, b, c, d);
		} else if (RECT_MODE == PConstants.CORNERS) {
			_rect(a, b, c-a, d-b);
		} else if (RECT_MODE == PConstants.CENTER) {
			_rect(a-c/2, b-d/2, c, d);
		} else if (RECT_MODE == PConstants.RADIUS) {
			_rect(a-c, b-d, c*2, d*2);
		}
	}
	public void square(float a, float b, float c) {
		rect(a,b,c,c);
	}
	
	public void rect(float a, float b, float c, float d, float r) {
		rect(a,b,c,d,r,r,r,r);
	}
	public void rect(float a, float b, float c, float d, float tl, float tr, float br, float bl) {
		if (RECT_MODE == PConstants.CORNER) {
			_roundedRect(a, b, c, d, tl, tr, br, bl);
		} else if (RECT_MODE == PConstants.CORNERS) {
			_roundedRect(a, b, c-a, d-b, tl, tr, br, bl);
		} else if (RECT_MODE == PConstants.CENTER) {
			_roundedRect(a-c/2, b-d/2, c, d, tl, tr, br, bl);
		} else if (RECT_MODE == PConstants.RADIUS) {
			_roundedRect(a-c, b-d, c*2, d*2, tl, tr, br, bl);
		}
	}

	
	/** 
	 *  Draw a line
	 *  @param x0  x coordinate of first endpoint
	 *  @param y0  y coordinate of first endpoint
	 *  @param x1  x coordinate of second endpoint
	 *  @param y1  y coordinate of second endpoint
	 */
	public void line(float x0, float y0, float x1, float y1) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x0,y0));
		poly.add(new PVector(x1,y1));
		polyBuff.clear();
		polyBuff.add(poly);
		endShape();
	}

	/** 
	 *  Draw a quad
	 *  @param x0  x coordinate of first vertex
	 *  @param y0  y coordinate of first vertex
	 *  @param x1  x coordinate of second vertex
	 *  @param y1  y coordinate of second vertex
	 *  @param x2  x coordinate of third vertex
	 *  @param y2  y coordinate of third vertex
	 *  @param x3  x coordinate of fourth vertex
	 *  @param y3  y coordinate of fourth vertex
	 */
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

	/** 
	 *  Draw a triangle
	 *  @param x0  x coordinate of first vertex
	 *  @param y0  y coordinate of first vertex
	 *  @param x1  x coordinate of second vertex
	 *  @param y1  y coordinate of second vertex
	 *  @param x2  x coordinate of third vertex
	 *  @param y2  y coordinate of third vertex
	 */
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

	/** 
	 *  Begin drawing a polygon/polyline. use vertex() to add vertices to it.
	 */
	public void beginShape() {
		if (polyBuff == null) {
			polyBuff = new ArrayList<ArrayList<PVector>> ();
		}
		polyBuff.clear();
		polyBuff.add(new ArrayList<PVector>());
		curveBuff.clear();
	}
	/** 
	 *  Add vertex to the current polygon/polyline. This must be preceded by beginShape()
	 *  @param x   x coordinate of the vertex
	 *  @param y   y coordinate of the vertex
	 */
	public void vertex(float x, float y) {
		
		polyBuff.get(polyBuff.size()-1).add(new PVector(x,y));
		curveBuff.clear();
		
	}
	/** 
	 *  Add a cubic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex()
	 *  An alias of cubicVertex
	 *  @param x1  x coordinate of first control point
	 *  @param y1  y coordinate of first control point
	 *  @param x2  x coordinate of second control point
	 *  @param y2  y coordinate of second control point
	 *  @param x3  x coordinate of the end point
	 *  @param y3  y coordinate of the end point
	 */
	public void bezierVertex(float x1, float y1, float x2, float y2, float x3, float y3) {
		cubicVertex(x1,y1,x2,y2,x3,y3);
		curveBuff.clear();
	}
	public void bezier(float x0, float y0,float x1, float y1, float x2, float y2, float x3, float y3) {
		beginShape();
		vertex(x0,y0);
		bezierVertex(x1,y1,x2,y2,x3,y3);
		endShape();	
	}
	public void curve(float x0, float y0,float x1, float y1, float x2, float y2, float x3, float y3) {
		beginShape();
		curveVertex(x0,y0);
		curveVertex(x1,y1);
		curveVertex(x2,y2);
		curveVertex(x3,y3);
		endShape();
		
	}
	
	/** 
	 *  Add a rational quadratic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex()
	 *  @param x1  x coordinate of first control point
	 *  @param y1  y coordinate of first control point
	 *  @param x2  x coordinate of second control point
	 *  @param y2  y coordinate of second control point
	 *  @param w   weight of the rational bezier curve, higher the pointier
	 */
	public void rationalVertex(float x1, float y1, float x2, float y2, float w) {
		PVector p0 = polyBuff.get(polyBuff.size()-1).get(polyBuff.get(polyBuff.size()-1).size()-1);
		for (int i = 1; i < BEZIER_DETAIL; i++) {
			float t = (float)i/(float)(BEZIER_DETAIL-1);
			PVector p = rationalQuadraticBezier(p0, new PVector(x1,y1), new PVector(x2,y2), w, t);
			polyBuff.get(polyBuff.size()-1).add(p);
		}
		curveBuff.clear();
	}
	/** 
	 *  Add a quadratic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex()
	 *  @param x1  x coordinate of first control point
	 *  @param y1  y coordinate of first control point
	 *  @param x2  x coordinate of second control point
	 *  @param y2  y coordinate of second control point
	 */
	public void quadraticVertex(float x1, float y1, float x2, float y2) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		highBezierVertex(poly);
		curveBuff.clear();
	}
	/** 
	 *  Add a cubic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex()
	 *  @param x1  x coordinate of first control point
	 *  @param y1  y coordinate of first control point
	 *  @param x2  x coordinate of second control point
	 *  @param y2  y coordinate of second control point
	 *  @param x3  x coordinate of the end point
	 *  @param y3  y coordinate of the end point
	 */
	public void cubicVertex(float x1, float y1, float x2, float y2, float x3, float y3) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		poly.add(new PVector(x3,y3));
		highBezierVertex(poly);
		curveBuff.clear();
	}
	/** 
	 *  Add a quartic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex()
	 *  @param x1  x coordinate of first control point
	 *  @param y1  y coordinate of first control point
	 *  @param x2  x coordinate of second control point
	 *  @param y2  y coordinate of second control point
	 *  @param x3  x coordinate of third control point
	 *  @param y3  y coordinate of third control point
	 *  @param x4  x coordinate of the end point
	 *  @param y4  y coordinate of the end point
	 */
	public void quarticVertex(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		poly.add(new PVector(x3,y3));
		poly.add(new PVector(x4,y4));
		highBezierVertex(poly);
		curveBuff.clear();
	}
	/** 
	 *  Add a quintic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex()
	 *  @param x1  x coordinate of first control point
	 *  @param y1  y coordinate of first control point
	 *  @param x2  x coordinate of second control point
	 *  @param y2  y coordinate of second control point
	 *  @param x3  x coordinate of third control point
	 *  @param y3  y coordinate of third control point
	 *  @param x4  x coordinate of fourth control point
	 *  @param y4  y coordinate of fourth control point
	 *  @param x5  x coordinate of the end point
	 *  @param y5  y coordinate of the end point
	 */
	public void quinticVertex(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float x5, float y5) {
		ArrayList<PVector> poly = new ArrayList<PVector>();
		poly.add(new PVector(x1,y1));
		poly.add(new PVector(x2,y2));
		poly.add(new PVector(x3,y3));
		poly.add(new PVector(x4,y4));
		poly.add(new PVector(x5,y5));
		highBezierVertex(poly);
		curveBuff.clear();
	}
	/** 
	 *  Add a higher-order bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex()
	 *  @param poly   control points and end point
	 */
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
		curveBuff.clear();
	}
	
	public void curveVertex(float x, float y) {
		curveBuff.add(new PVector(x,y));

		if (curveBuff.size() < 4) {
			return;
		}
		PVector p0 = curveBuff.get(curveBuff.size()-4);
		PVector p1 = curveBuff.get(curveBuff.size()-3);
		PVector p2 = curveBuff.get(curveBuff.size()-2);
		PVector p3 = curveBuff.get(curveBuff.size()-1);

		ArrayList<PVector> poly = catmullRomSpline(p0,p1,p2,p3,CATMULLROM_DETAIL,CATMULLROM_TIGHTNESS);

		polyBuff.get(polyBuff.size()-1).addAll(poly);
	}
	
	/** 
	 *  End drawing a polygon. at this moment the polygon will be actually drawn to the design
	 *  @param close  whether or not to close the polyline (forming polygon)
	 */
	public void endShape(boolean close) {
		curveBuff.clear();
		if (polyBuff.size() == 0) {
			return;
		}
		if (polyBuff.size() == 1 && polyBuff.get(0).size() == 0) {
			return;
		}
		if (polyBuff.size() == 1 && (HATCH_BACKEND == FORCE_VECTOR || HATCH_MODE == SPIRAL) && HATCH_MODE != SATIN) {
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
//					HATCH_MODE = CROSS;
					float hatch_angle = HATCH_ANGLE;
					float hatch_angle2 = HATCH_ANGLE2;
					if (AUTO_HATCH_ANGLE) {
						hatch_angle = calcPolygonTilt(polyBuff.get(0));
						hatch_angle2 = hatch_angle+PConstants.HALF_PI;
					}
					hatch_angle = calcAxisAngleForParallel(hatch_angle);
					hatch_angle2 = calcAxisAngleForParallel(hatch_angle2);
					
					ArrayList<ArrayList<PVector>> polys = hatchParallelComplex(polyBuff,hatch_angle,HATCH_SPACING);
					
					boolean didit = false;
					if (HATCH_MODE == PARALLEL && !NO_RESAMPLE) {
						polys = resampleCrossIntersection(polys,hatch_angle,HATCH_SPACING,STITCH_LENGTH/getCurrentScale(hatch_angle+PApplet.HALF_PI), PARALLEL_RESAMPLING_OFFSET_FACTOR, RESAMPLE_NOISE);
						NO_RESAMPLE = true;
						didit = true;
					}
					
					if (HATCH_MODE == CROSS) {
						if (EXPERIMENTAL_CROSS_RESAMPLE && !NO_RESAMPLE) {
							
							ArrayList<ArrayList<PVector>> polys2 =hatchParallelComplex(polyBuff,hatch_angle2,HATCH_SPACING);
							polys = resampleCrossIntersection2(polys,polys2,hatch_angle,hatch_angle2,HATCH_SPACING,STITCH_LENGTH/getCurrentScale(hatch_angle+PApplet.HALF_PI), RESAMPLE_NOISE);
							
							NO_RESAMPLE = true;
							didit = true;
						}else {
							polys.addAll(hatchParallelComplex(polyBuff,hatch_angle2,HATCH_SPACING));
						}
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
	
	/** 
	 *  Alias for endShape(bool) that does not close the shape  
	 */
	public void endShape() {
		endShape(false);
	}
	/** 
	 *  Alias for endShape(bool) that closes the shape  
	 *  @param close   pass anything, but use CLOSE for readability 
	 */
	public void endShape(int close) {
		endShape(true);
	}
	/** 
	 *  Begin a contour within the current polygon,
	 *  if this contour has negative winding and is inside the current polygon or another contour defined between beginShape() and endShape() that is not a hole, this will be a hole 
	 */
	public void beginContour() {
		polyBuff.add(new ArrayList<PVector>());
	}
	
	/** 
	 *  Done with adding a contour
	 */
	public void endContour() {
		// it's ok
	}
	/** 
	 *  Draw a circle, a.k.a ellipse with equal radius in each dimension
	 *  @param x  the first parameter, meaning of which depends on ellipseMode
	 *  @param y  the second parameter, meaning of which depends on ellipseMode
	 *  @param r  the third parameter, meaning of which depends on ellipseMode
	 */
	public void circle(float x, float y, float r){
		ellipse(x,y,r,r);
	}

	/*  ======================================== MATRIX WRAPPERS  ======================================== */

	/** 
	 *  Save the current state of transformation
	 */
	public void pushMatrix() {
		matStack.add(new PMatrix2D());
	}
	/** 
	 *  Restore previous state of transformatino
	 */
	public void popMatrix() {
		matStack.remove(matStack.size() - 1);
	}
	/** 
	 *  Translate subsequent drawing calls
	 *  @param x   x offset to translate
	 *  @param y   y offset to translate
	 */
	public void translate(float x, float y) {
		matStack.get(matStack.size() - 1).translate(x, y);
	}
	/** 
	 *  Rotate subsequent drawing calls
	 *  @param a    angle in radians to rotate
	 */
	public void rotate(float a) {
		matStack.get(matStack.size() - 1).rotate(a);
	}
	/** 
	 *  Shear subsequent drawing calls along x-axis
	 *  @param x  shear amount
	 */
	public void shearX(float x) {
		matStack.get(matStack.size() - 1).shearX(x);
	}
	/** 
	 *  Shear subsequent drawing calls along y-axis
	 *  @param x  shear amount
	 */
	public void shearY(float x) {
		matStack.get(matStack.size() - 1).shearY(x);
	}
	/** 
	 *  Scale subsequent drawing calls proportionally
	 *  @param x  multiplier on both axes
	 */
	public void scale(float x) {
		matStack.get(matStack.size() - 1).scale(x);
	}
	/** 
	 *  Scale subsequent drawing calls disproportionally
	 *  @param x  multiplier on x axis
	 *  @param y  multiplier on y axis
	 */
	public void scale(float x, float y) {
		matStack.get(matStack.size() - 1).scale(x,y);
	}


	/* ======================================== VISUALIZE ======================================== */

	/** 
	 *  Visualize the current design on the main Processing canvas
	 *  @param color     whether to visualize color, if false, will use random colors; if stitches argument is true, this will have no effect and black will always be used for visibility
	 *  @param stitches  whether to visualize stitches, i.e. little dots on end of segments
	 *  @param route     whether to visualize the path between polylines that will be taken by embroidery machine/plotter. To be able to not see a mess when enabling this option, try optimize()
	 */
	public void visualize(boolean color, boolean stitches, boolean route, int nStitches) {
		int n = 0;
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
				n++;
				if (n >= nStitches) {
//					PApplet.println(p0,p1);
					break;
				}
			}
			if (n >= nStitches) {
				break;
			}
		}
		n = 0;
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
					n++;
					if (n>=nStitches) {
						break;
					}
					
				}
				if (n >= nStitches) {
					break;
				}
			}
		}
	}
	/** 
	 *  Visualize the current design on the main Processing canvas,
	 *  using default set of options
	 */
	public void visualize() {
		visualize(false,true,false);
	}
	public void visualize(boolean color, boolean stitches, boolean route) {
		visualize(color,stitches,route,Integer.MAX_VALUE);
	}



	/* ======================================== IO ======================================== */

	/**
	 * Supposed to initialize something, but as we currently don't need to, this is a NOP
	 */
	public void beginDraw() {
		// future initialization goes here
	}

	/**
	 * Save the drawing to file
	 */
	public void endDraw() {
		if (polylines.size() < 1) {
			return;
		}
		checkOutOfBounds();
		PEmbroiderWriter.write(path, polylines, colors, width, height, NO_CONNECT);
	}


	/**
	 * Double the number of vertices in the polyline by spliting every segment by half
	 * @param poly the polyline
	 * @return     the resampled polyline
	 */
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
	/**
	 * Half the number of vertices in the polyline by joining every two segments
	 * @param poly the polyline
	 * @return     the resampled polyline
	 */
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
	
	/**
	 * Approximately halfing the number of vertices in the polyline but preserves important turning points
	 * @param poly     the polyline
	 * @param maxTurn  amount of turning for a vertex to be considered important
	 * @return         the resampled polyline
	 */
	public ArrayList<PVector> resampleHalfKeepCorners(ArrayList<PVector> poly, float maxTurn){
		ArrayList<PVector> poly2 = new ArrayList<PVector>();
		if (poly.size() <= 2) {
			poly2.addAll(poly);
			return poly2;
		}
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

	/**
	 * Begin culling shapes. Culled shapes occlude each other
	 */
	public void beginCull() {
		beginCullIndex = cullGroups.size();
	}
	public void beginCull(float sp) {
		beginCullIndex = cullGroups.size();
		CULL_SPACING = sp;
	}
	/**
	 * End culling shapes. Culled shapes occlude each other
	 */
	public void endCull() {
		if (beginCullIndex >= cullGroups.size()) {
			return;
		}
		
		ArrayList<ArrayList<ArrayList<PVector>>> groups = new ArrayList<ArrayList<ArrayList<PVector>>>();
		ArrayList<ArrayList<Integer>> groupColors = new ArrayList<ArrayList<Integer>>();

		int last = cullGroups.get(beginCullIndex);
		groups.add(new ArrayList<ArrayList<PVector>>());
		groups.get(groups.size()-1).add(resampleDouble(resampleDouble(polylines.get(beginCullIndex))));

		groupColors.add(new ArrayList<Integer>());
		groupColors.get(groupColors.size()-1).add(colors.get(beginCullIndex));

		for (int i = beginCullIndex+1; i < polylines.size(); i++) {

			if (!cullGroups.get(i).equals(last)) {
				groups.add(new ArrayList<ArrayList<PVector>>());
				groupColors.add(new ArrayList<Integer>());
			}
			groups.get(groups.size()-1).add(resampleDouble(resampleDouble(polylines.get(i))));
			groupColors.get(groupColors.size()-1).add(colors.get(i));
			last = cullGroups.get(i);
		}


		PGraphics[] channels = new PGraphics[groups.size()];
		for (int i = 0; i < groups.size(); i++) {
			channels[i] = app.createGraphics(width,height);
			channels[i].beginDraw();
			channels[i].background(0);
			channels[i].stroke(255);
			channels[i].strokeWeight(CULL_SPACING);
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
				if (groups.get(i).get(k).size() < 3) {
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
			polylines2.set(i, resampleHalfKeepCorners(resampleHalfKeepCorners(polylines2.get(i),PConstants.PI*0.1f),PConstants.PI*0.1f));
		}

		polylines.subList(beginCullIndex,polylines.size()).clear();
		colors.subList(beginCullIndex,colors.size()).clear();
		cullGroups.subList(beginCullIndex,cullGroups.size()).clear();

		polylines.addAll(polylines2);
		colors.addAll(colors2);
		cullGroups.addAll(cullGroups2);

		currentCullGroup ++;

	}

	/**
	 * Draw an image
	 * @param im   a PImage, PGraphics also qualify
	 * @param x    left
	 * @param y    top
	 * @param w    width
	 * @param h    height
	 */
	public void image(PImage im, int x, int y, int w, int h) {
		PGraphics im2  = app.createGraphics(w,h);
		im2.beginDraw();
		im2.image(im,0,0,w,h);
//		im2.filter(PConstants.INVERT);
		im2.filter(PConstants.THRESHOLD);
		im2.endDraw();
		
		ArrayList<ArrayList<PVector>> polys = PEmbroiderTrace.findContours(im2);

		for (int i = polys.size()-1; i >= 0; i--) {
			if (polys.get(i).size() < 3) {
				polys.remove(i);
				continue;
			}

//			polys.set(i,resampleHalf(resampleHalf(resampleHalf(polys.get(i)))));
			polys.set(i, PEmbroiderTrace.approxPolyDP(polys.get(i),1));

		}

		pushMatrix();
		translate(x,y);
		if (isStroke && FIRST_STROKE_THEN_FILL) {
			_stroke(polys, true);
		}
		if (isFill) {
			if (!isStroke && HATCH_MODE == CONCENTRIC) {
				for (int i = 0; i < polys.size(); i++) {
					pushPolyline(polys.get(i),currentFill);
				}
			}
			hatchRaster(im2);
		}
		if (isStroke && !FIRST_STROKE_THEN_FILL) {
			_stroke(polys, true);
		}
		popMatrix();
		currentCullGroup++;
	}

	public void image(PImage im, int x, int y) {
		image(im, x, y, im.width, im.height);
	}
	
	public void image(PEmbroiderBooleanShapeGraphics im, int x, int y, int w, int h) {
		im.endOps();
		image(im.get(),x,y,w,h);
	}
	public void image(PEmbroiderBooleanShapeGraphics im, int x, int y) {
		im.endOps();
		image(im.get(),x,y);
	}
	public void shape(PShape sh, int x, int y, int w, int h) {
		int pad = 10;
		sh.disableStyle();
		PGraphics pg = app.createGraphics(w+pad*2, h+pad*2);
		pg.beginDraw();
		pg.background(0);
		pg.fill(255);
		pg.noStroke();
		pg.shape(sh,pad,pad,w,h);
//		pg.filter(PConstants.INVERT);
		pg.endDraw();
//		app.image(pg,0,0);
		image(pg,x-pad,y-pad);
	}
	
	public void shape(PShape sh, int x, int y) {
		int w = (int)sh.width;
		int h = (int)sh.height;
		shape(sh,x,y,w,h);
	}
	
	/**
	 * Hatch an image using perlin noise fill
	 * @param mask         a binary image/graphics, white means on, black means off
	 * @param d            spacing between strokes
	 * @param perlinScale  perlin noise scale
	 * @param deltaX       step size of the walk in vector field
	 * @param minVertices  strokes with too few segments (those tiny ones) will be discarded
	 * @param maxVertices  maximum number of vertices for each stroke
	 * @param maxIter      maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners
	 */
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
	/**
	 * Signature for a vector field
	 */
	public interface VectorField{
		/**
		 * get the direction vector at given coordinate
		 * @param x   x coordinate of the sample point
		 * @param y   y coordinate of the sample point
		 */
		PVector get(float x, float y);
	}

	/**
	 * Hatch an image using custom vector field fill
	 * @param mask         a binary image/graphics, white means on, black means off
	 * @param d            spacing between strokes
	 * @param minVertices  strokes with too few segments (those tiny ones) will be discarded
	 * @param maxVertices  maximum number of vertices for each stroke
	 * @param maxIter      maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners
	 */
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
	/**
	 * Hatch an image using PARALLEL fill (with raster algorithms)
	 * @param mask         a binary image/graphics, white means on, black means off
	 * @param ang          angle of parallel lines in radians
	 * @param d            spacing between strokes
	 * @param step         step size, smaller the more accurate
	 * @return             the hatching as a list of polylines
	 */
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
	/**
	 * Lerp a number around 360 degrees, meaning lerp360(358,2) gives 0 instead of 180
	 * Useful for lerping hues and angles
	 * @param h0  the first number
	 * @param h1  the second number
	 * @param t   the interpolation parameter
	 * @return    the lerped number
	 */
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
//        		PApplet.println(h0,h1,i);
        		return (methods[i][1]+720)%360;
        	}
        }
        return -1; // impossible case, just to shut java up
     }
	/**
	 * Smoothen a polyline with rational quadratic bezier (thus upsampling)
	 * @param poly  the polyline
	 * @param w   weight of the rational bezier, higher the pointier
	 * @param n   number of segments for each segment on the polyline
	 * @return    the smoothed polyline
	 */
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
	 
	/**
	 * Computes the accurate distance transform by actually mesuring distances (slow).
	 * See PEmbroiderTrace for the fast approximation
	 * @param polys  the polylines to consider
	 * @param w      width of the output image
	 * @param n      height of the output image
	 * @return       the distance transform stored in row-major array of floats
	 */	 
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
	 
	 /**
	  * Clip polylines with a mask
	  * @param polys  the polylines to consider
	  * @param mask   a mask, black means off, white means on
	  */	
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
	 
	 /**
	  * Compute isolines for a grayscale image using findContours on multiple thresholds.
	  * @param im     the input grayscale image
	  * @param d      luminance distance between adjacent thresholds
	  * @return       an array of isolines as polylines
	  */	
	public ArrayList<ArrayList<PVector>> isolines(PImage im, float d){
		ArrayList<ArrayList<ArrayList<PVector>>> isos = PEmbroiderTrace.findIsolines(im, -1, d);
		ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
		for (int i = 0; i < isos.size(); i++) {
			for (int j = 0; j < isos.get(i).size(); j++) {
//				polys.add(resampleHalfKeepCorners(resampleHalf(isos.get(i).get(j)),0.1f));
				
				
				if (CONCENTRIC_ANTIALIGN > 0) {
		
					ArrayList<PVector> pp = isos.get(i).get(j);
					if (i%2 == 0) {
						ArrayList<PVector> qq = new ArrayList<PVector>();
						for (int k = 0; k < pp.size()+1; k++) {
							if (k != pp.size()) {
								PVector a = pp.get((k-1+pp.size())%pp.size());
								PVector b = pp.get(k);
								PVector c = pp.get((k+1)%pp.size());
								PVector u = b.copy().sub(a);
								PVector v = c.copy().sub(b);
								float ang = PApplet.abs(PVector.angleBetween(u, v));
								if (ang > PConstants.PI) {
									ang = PConstants.TWO_PI - ang;
								}
								if (ang > CONCENTRIC_ANTIALIGN) {
									qq.add(b);
								}
							}
							
							PVector p = pp.get(k%pp.size()).copy().mult(0.5f).add(pp.get((k+1)%pp.size()).copy().mult(0.5f));
							qq.add(p);
						}
						polys.add(qq);

						
					}else {
						polys.add(pp);
					}
				
				}else {
					polys.add(isos.get(i).get(j));
				}
				
							
			}
		}
		return polys;
	}
	 /**
	  * Check if a polygon completely contains another
	  * @param poly0  the polygon that is supposed to contain the other one
	  * @param poly1  the polygon that is supposed to be contained by the other one
	  * @return       true for containment, false for not
	  */	
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
	 /**
	  * Compute area of polygon
	  * @param poly the polygon
	  * @return     the area
	  */	
	public float polygonArea(ArrayList<PVector> poly){
		//https://www.mathopenref.com/coordpolygonarea2.html
		int n = poly.size();

		float area = 0;  
		int j = n-1;

		for (int i=0; i<n; i++){
			area +=  (poly.get(j).x+poly.get(i).x) * (poly.get(j).y-poly.get(i).y); 
			j = i;
		}
		return area/2;
	}
	


	 /**
	  * Run TSP on polylines in the current design to optimize for connective path length
	  * @param trials   number of trials. the more times you try, the higher chance you'll get a better result
	  * @param maxIter  maximum number of iterations to run 2-Opt. 
	  */
	public void optimize(int trials,int maxIter) {
		if (polylines.size() <= 2) {
			return;
		}
		int idx0 = 0;
		for (int i = 1; i <= polylines.size(); i++) {
			if (i == polylines.size() || !colors.get(i).equals(colors.get(i-1))){
				ArrayList<ArrayList<PVector>> p = new ArrayList<ArrayList<PVector>>(polylines.subList(idx0,i));
				PApplet.println(p.size());
				if (p.size() > 2) {
					p = PEmbroiderTSP.solve(p,trials,maxIter);
				}
				PApplet.println(p.size());
				polylines.subList(idx0,i).clear();
				polylines.addAll(idx0,p);
				idx0 = i;
			}
		}
	}
	 /**
	  * Simplified version of optimize(2) where the trial and maxIter is picked for you
	  */
	public void optimize() {
		optimize(5,999);
	}
	
	int optBlockIdx0;
	int optBlockTrials;
	int optBlockMaxIter;
	int optColor;
	/**
	 * Begin a block of stitch order optimization (TSP solver), to be paired with endOptimize()
	 * @param reorderColor number of seconds to try to reorder color to reduce number of color changes without modifying the final look of the design, 0 means no color reordering
	 * @param trials number of trials to run the TSP
	 * @param maxIter number of iterations for each trial of TSP
	 */
	public void beginOptimize(int reorderColor, int trials,int maxIter) {
		optBlockIdx0 = polylines.size();
		optBlockTrials = trials;
		optBlockMaxIter = maxIter;
		optColor = reorderColor;
	}
	/**
	 * Same as beginOptimize(3) with default parameters
	 */
	public void beginOptimize() {
		beginOptimize(0,5,999);
	}
	/**
	 * Close a beginOptimize() block.
	 */
	public void endOptimize() {
		if (polylines.size()-optBlockIdx0 <= 2) {
			return;
		}
		if (optColor > 0) {
			
			ArrayList<ArrayList<PVector>> p = new ArrayList<ArrayList<PVector>>(polylines.subList(optBlockIdx0, polylines.size()));
			ArrayList<Integer> c = new ArrayList<Integer>(colors.subList(optBlockIdx0, colors.size()));

//			PApplet.println("!!!",optBlockIdx0,polylines.size(),p.size(),c.size());
			ArrayList<Integer> indices = reorderColorMonteCarlo(p,c,8,optColor);


			ArrayList<ArrayList<PVector>> p2 = new ArrayList<ArrayList<PVector>>();
			ArrayList<Integer> c2 = new ArrayList<Integer>();
			for (int i = 0; i < indices.size(); i++) {
				p2.add(p.get(indices.get(i)));
				c2.add(c.get(indices.get(i)));
			}
			polylines.subList(optBlockIdx0,polylines.size()).clear();
			polylines.addAll(optBlockIdx0,p2);
			colors.subList(optBlockIdx0,colors.size()).clear();
			colors.addAll(optBlockIdx0,c2);
		}
		int idx0 = optBlockIdx0;
		for (int i = optBlockIdx0+1; i <= polylines.size(); i++) {
			if (i == polylines.size() || !colors.get(i).equals(colors.get(i-1))){
				ArrayList<ArrayList<PVector>> p = new ArrayList<ArrayList<PVector>>(polylines.subList(idx0,i));
				PApplet.println(p.size());
				if (p.size() > 2) {
					p = PEmbroiderTSP.solve(p,optBlockTrials,optBlockMaxIter);
				}
				PApplet.println(p.size());
				polylines.subList(idx0,i).clear();
				polylines.addAll(idx0,p);
				idx0 = i;
			}
		}
	}

	 /**
	  * Change horizontal alignment of text
	  * @param align alignment mode, one of LEFT, CETER, RIGHT
	  */
	public void textAlign(int align) {
		FONT_ALIGN = align;
	}
	 /**
	  * Change horizontal and vertical alignment of text
	  * @param halign horizontal alignment mode, one of LEFT, CETER, RIGHT
	  * @param valign vertical alignment mode, one of TOP, CENTER, BASELINE, BOTTOM
	  */
	public void textAlign(int halign, int valign) {
		FONT_ALIGN = halign;
		FONT_ALIGN_VERTICAL = valign;
	}
	 /**
	  * Change size of text
	  * @param size  the desired size of text
	  */
	public void textSize(float size) {
		FONT_SCALE = size;
	}
	 /**
	  * Change font of text.
	  * Notice this function is overloaded with one version for PFont and one for hershey font, with identical API
	  * This one is for the hershey font
	  * @param font the desired hershey font
	  */
	public void textFont(int[] font) {
		FONT = font;
		TRUE_FONT = null;
	}
	 /**
	  * Change font of text.
	  * Notice this function is overloaded with one version for PFont and one for hershey font, with identical API
	  * This one is for PFont
	  * @param font the desired PFont
	  */
	public void textFont(PFont font) {
		TRUE_FONT = font;
		FONT = null;
	}
	public void mergePolylinesByColor(int i0, int i1) {
		if (!(i1 > i0)) {
			return;
		}
		if (colors.get(i0).equals(colors.get(i0+1))) {
			polylines.get(i0).addAll(polylines.get(i0+1));
			polylines.remove(i0+1);
			cullGroups.remove(i0+1);
			colors.remove(i0+1);
			i1--;
		}else {
			i0++;
		}
		mergePolylinesByColor(i0,i1);
	}
	 /**
	  * Draw some text
	  * @param str  the string containing the text to be drawn
	  * @param x    x coordinate, meaning of which depends on textAlign
	  * @param y    y coordinate, meaning of which depends on textAlign
	  */
	public void text(String str, float x, float y) {
		if (FONT != null) {
			ArrayList<ArrayList<PVector>> polys = PEmbroiderFont.putText(FONT, str, x, y, FONT_SCALE, FONT_ALIGN);
			for (int i = 0; i < polys.size(); i++) {
				pushPolyline(polys.get(i), currentFill);
			}
		} else if (TRUE_FONT != null) {
			PGraphics pg0 = app.createGraphics(1, 1);
			pg0.beginDraw();
			pg0.textFont(TRUE_FONT);
			pg0.textSize(FONT_SCALE);
			float tw = pg0.textWidth(str);
			float ta = pg0.textAscent();
			float td = pg0.textDescent();

//			PApplet.println(tw,ta,td);
			pg0.endDraw();

			PGraphics pg = app.createGraphics((int) PApplet.ceil(tw), (int) PApplet.ceil(ta + td));
			pg.beginDraw();
			pg.background(0);
			pg.fill(255);
			pg.noStroke();

			pg.textFont(TRUE_FONT);
			pg.textSize(FONT_SCALE);
			pg.textAlign(PConstants.LEFT, PConstants.TOP);
			pg.text(str, 0, 0);
			pg.endDraw();
//			hatchRaster(pg,x,y);
			ArrayList<Boolean> isHoles = new ArrayList<Boolean>();
			ArrayList<Integer> parents = new ArrayList<Integer>();
			ArrayList<ArrayList<PVector>> conts = PEmbroiderTrace.findContours(pg,isHoles,parents);

			for (int i = conts.size() - 1; i >= 0; i--) {
//				if (conts.get(i).size() < 2) {
//					conts.remove(i);
//					continue;
//				}
				for (int j = 0; j < conts.get(i).size(); j++) {
					conts.get(i).get(j).add(new PVector(x, y));
				}
//				conts.set(i,resampleHalfKeepCorners(resampleHalf(resampleHalf(conts.get(i))),0.1f));
				conts.set(i, PEmbroiderTrace.approxPolyDP(conts.get(i), 1));
//				if (conts.get(i).get(conts.get(i).size() - 1).dist(conts.get(i).get(0)) < FONT_SCALE / 10f) {
//					conts.get(i).add(conts.get(i).get(0));
//				}
			}

			float dx = 0;
			float dy = 0;

			if (FONT_ALIGN == PConstants.RIGHT) {
				dx -= tw;
			} else if (FONT_ALIGN == PConstants.CENTER) {
				dx -= tw / 2;
			}
			if (FONT_ALIGN_VERTICAL == PConstants.BASELINE) {
				dy -= ta;
			} else if (FONT_ALIGN_VERTICAL == PConstants.BOTTOM) {
				dy -= ta + td;
			}

			

			pushMatrix();
			translate(dx, dy);

			for (int i = 0; i < conts.size(); i++) {
				if (parents.get(i) != -1) {
					continue;
				}
				int lastLen = polylines.size();
				if (TEXT_OPTIMIZE_PER_CHAR) {
					beginOptimize();
				}
				beginShape();
				for (int j = 0; j < conts.get(i).size(); j++) {
					vertex(conts.get(i).get(j).x,conts.get(i).get(j).y);
				}
				for (int k = 0; k < conts.size(); k++) {
					int par = parents.get(k);
					if (par == -1) {
						continue;
					}
					while ( parents.get(par) != -1) {
						par = parents.get(par);
					}
					if (par == i) {
						beginContour();
						
						for (int j = 0; j < conts.get(k).size(); j++) {
							vertex(conts.get(k).get(j).x,conts.get(k).get(j).y);
						}
						
						endContour();
						
						
					}
				}

				endShape(PConstants.CLOSE);
				if (TEXT_OPTIMIZE_PER_CHAR) {
					endOptimize();
					if (!NO_CONNECT) {
						mergePolylinesByColor(lastLen,polylines.size()-1);
					}
				}
			}
			popMatrix();
		}

	}
	 /**
	  * Deep clone any object
	  * @param object  some object
	  * @reutrn        the clone
	  */
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
	 
	 

	 public void printStats() {
		 PApplet.println("total number of polylines: ",polylines.size());
		 int nStitches = 0;
		 float lenPoly = 0;
		 float lenConnect = 0;
		
		 for (int i = 0; i < polylines.size(); i++) {
			 nStitches += polylines.get(i).size();
			 for (int j = 1; j < polylines.get(i).size(); j++) {
				 lenPoly += polylines.get(i).get(j-1).dist(polylines.get(i).get(j));
			 }
		 }
		 PApplet.println("total number of stitches: ",nStitches);

		 for (int i = 1; i < polylines.size(); i++) {
			 if (polylines.get(i-1).size() > 0 && polylines.get(i).size() > 0) {
				 lenConnect += polylines.get(i-1).get(polylines.get(i-1).size()-1).dist(polylines.get(i).get(0));
			 }
		 }

		 PApplet.println("total length of thread in main design: ",lenPoly);
		 PApplet.println("total length of connective threads: ",lenConnect);
		 PApplet.println("total length of thread consumed IRL: ",lenPoly+lenConnect);
	 }
	 
	 public int getNumStitches() {
		 int nStitches = 0;
		 for (int i = 0; i < polylines.size(); i++) {
			 nStitches += polylines.get(i).size();
		 }
		 return nStitches;
	 }
	 
	 
	 int repeatEndStartIndex = 0;
	 float repeatEndX = 2;
	 public void beginRepeatEnd(float x) {
		 repeatEndX = 2;
		 repeatEndStartIndex = polylines.size();
	 }
	 
	 public void endRepeatEnd() {
		 float x = repeatEndX;
		 for (int i = repeatEndStartIndex; i < polylines.size(); i++) {
			 if (polylines.get(i).size() < 2) {
				 continue;
			 }
			 PVector a = polylines.get(i).get(0).copy();
			 float l = polylines.get(i).get(0).dist(polylines.get(i).get(1));
			 float t = x/l;
			 PVector b = polylines.get(i).get(0).copy().mult(1-t).add(polylines.get(i).get(1).copy().mult(t));

//			 a.x += app.random(10);
//			 b.x += app.random(10);
			 
			 polylines.get(i).add(0,b);
			 polylines.get(i).add(0,a);
			 
			 PVector c = polylines.get(i).get(polylines.get(i).size()-1).copy();
			 float m = polylines.get(i).get(polylines.get(i).size()-1).dist(polylines.get(i).get(polylines.get(i).size()-2));
			 float s = x/m;
			 PVector d = polylines.get(i).get(polylines.get(i).size()-1).copy().mult(1-s).add(polylines.get(i).get(polylines.get(i).size()-2).copy().mult(s));
			 polylines.get(i).add(d);
			 
//			 c.x += app.random(10);
//			 d.x += app.random(10);
			 polylines.get(i).add(c);
			 
		 }
	 }
	 
	 public void beginRawStitches() {
		 if (polyBuff == null) {
			 polyBuff = new ArrayList<ArrayList<PVector>> ();
		 }
		 polyBuff.clear();
		 polyBuff.add(new ArrayList<PVector>());
	 }
	 public void rawStitch(float x, float y) {
		 polyBuff.get(0).add(new PVector(x,y));
	 }
	 public void endRawStitches() {
		 if (polyBuff.get(0).size() < 2) {
			 return;
		 }
		 ArrayList<PVector> poly2 = new ArrayList<PVector>();
		 for (int i = 0; i < polyBuff.get(0).size(); i++) {
			 poly2.add(polyBuff.get(0).get(i).copy());
			 for (int j = matStack.size()-1; j>= 0; j--) {
				 poly2.set(i, matStack.get(j).mult(poly2.get(i), null));
			 }
		 }
		 colors.add(currentStroke);
		 polylines.add(poly2);
	 }
	 
	 public float getCurrentScale(float ang) {
		PVector p0 = new PVector(0,0);
		PVector p1 = new PVector(PApplet.cos(ang),PApplet.sin(ang));
		PVector p2 = p0.copy();
		PVector p3 = p1.copy();
		for (int j = 0; j < matStack.size(); j++) {
			p2 = matStack.get(j).mult(p2, null);
			p3 = matStack.get(j).mult(p3, null);
		}
		return p2.dist(p3);
	 }
	 
	 ArrayList<Integer> reorderColorMonteCarlo(ArrayList<ArrayList<PVector>> polys, ArrayList<Integer> cols, float d, float wait) {
		 ArrayList<Integer> ret = new ArrayList<Integer>();
		 
		 class ColorOpt{
			 Solution standard;
			 int w;
			 int h;
			 
			 class Layer{
				 int id;
				 int col;
				 ArrayList<Integer> indices;
				 boolean[] data;
			 }
	
			 class Solution{
				 Layer[] layers;
				 int[] render;
				 int score = Integer.MAX_VALUE;
				 void renderSolution() {

					 render = new int[layers[0].data.length];
					 for (int i = 0; i < layers.length; i++){
						 for (int j = 0; j < layers[i].data.length; j++){
							 if (layers[i].data[j]){
								 
								 render[j] = layers[i].col;

							 }
						 }
					 }
//					 drawRender().save("/Users/studio/Downloads/rcmc-"+hashCode()+".png");
				 }
				 PGraphics drawRender(){
					 PGraphics pg = app.createGraphics(w,h);
					 pg.beginDraw();
					 pg.loadPixels();
					 for (int i = 0; i < render.length; i++){
						 pg.pixels[i] = render[i];
					 }
					 pg.updatePixels();
					 pg.endDraw();
					 return pg;
				 }
				 
				 boolean equalsSolution(Solution sol) {
					 if (render == null){
						 renderSolution();
					 }
					 if (sol.render == null){
						 sol.renderSolution();
					 }
//					 drawRender().save("/Users/studio/Downloads/rcmcA.png");
//					 sol.drawRender().save("/Users/studio/Downloads/rcmcB.png");
					 for (int i = 0; i < render.length; i++){
						 if (render[i] != sol.render[i]){
							 return false;
//							 PApplet.println(i,render[i],sol.render[i]);
						 }
					 }
					 return true;
				 }
				 int countColorChange() {
					  int cc = 0;
					  for (int i = 1; i < layers.length; i++){
					    if (layers[i].col != layers[i-1].col){
					      cc++;
					    }
					  }
					  return cc;
				 }
			 }
			 
			 
			 Layer[] shuffleLayers(Layer[] layers){
				 Layer[] a = new Layer[layers.length];
				 for (int i = 0; i < layers.length; i++){
					 a[i] = layers[i];
				 }
				 for (int i = a.length - 1; i > 0; i--) {
					 int j = (int)PApplet.floor(app.random((i + 1)));
					 Layer x = a[i];
					 a[i] = a[j];
					 a[j] = x;
				 }
				 return a;
			 }
			 void scoreSolution(Solution sol){
				 if (!sol.equalsSolution(standard)){
					 sol.score = Integer.MAX_VALUE;
					 return;
				 }
				 sol.score = sol.countColorChange();
			 }
			 Solution newSolution(){
				 Solution sol = new Solution();
				 sol.layers = shuffleLayers(standard.layers);
				 
//				 PApplet.println(polys.size(), sol.layers.length,sol.countColorChange(),standard.layers.length,standard.countColorChange(),standard.score);
//				 throwNPE();
				 while (sol.countColorChange() >= standard.score){
					 sol.layers = shuffleLayers(standard.layers);
				 }
				 scoreSolution(sol);
				 
				 return sol;
			 }
			 
			 void main() {
				 w = (int)PApplet.ceil(width/d);
				 h = (int)PApplet.ceil(height/d);
				 standard = new Solution();
				 ArrayList<Solution> solutions = new ArrayList<Solution>();
				 
				 ArrayList<PGraphics> pgs = new ArrayList<PGraphics>();
				 ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
				 for (int i = 0; i < polys.size(); i++) {
					 if (i == 0 || (!cols.get(i).equals(cols.get(i-1)))) {
						 PGraphics pg = app.createGraphics(w,h);
						 pg.beginDraw();
						 pg.background(0);
						 pg.endDraw();
						 pgs.add(pg);
						 indices.add(new ArrayList<Integer>());
					 }
					 PGraphics pg = pgs.get(pgs.size()-1);
					 pg.beginDraw();
					 pg.noFill();
					 pg.strokeWeight(1);
					 pg.stroke(255);
					 pg.beginShape();
					 for (int j = 0; j < polys.get(i).size(); j++) {
						 pg.vertex(polys.get(i).get(j).x/d,polys.get(i).get(j).y/d);
					 }
					 pg.endShape();
					 pg.endDraw();
					 indices.get(indices.size()-1).add(i);
				 }
				 standard.layers = new Layer[pgs.size()];
				 for (int i = 0; i < pgs.size(); i++) {
					Layer l = new Layer();
					l.id = i+1;
					PGraphics pg = pgs.get(i);
				    pg.beginDraw();
					pg.loadPixels();
				    l.data = new boolean[pg.pixels.length];
				    for (int j = 0; j < pg.pixels.length; j++){
				      l.data[j] = (pg.pixels[j]&255)>128;
				      
				    }
				    pg.endDraw();
//				    pg.save("/Users/studio/Downloads/rcmc-"+i+".png");
				    l.col = cols.get(indices.get(i).get(0));
				    l.indices = indices.get(i);
				    standard.layers[i] = l;
				 }
				 scoreSolution(standard);

				 standard.drawRender().save("/Users/studio/Downloads/rcmc-"+hashCode()+".png");
				 
				 solutions.add(standard);
				 PApplet.println(logPrefix+" Original color change: "+standard.score+", optimizing...");
				 
				 long startTime = System.currentTimeMillis();
				 for (int i = 0; i < Integer.MAX_VALUE; i++) {
					 Solution sol = newSolution();
					 if (sol.score < solutions.get(solutions.size()-1).score){
						 PApplet.println(logPrefix+" Color opt trial # "+i+", improved color changes to: "+sol.score);
						 solutions.add(sol);
					 }
					 if (i % 100 == 0) {
						 long duration = System.currentTimeMillis()- startTime;
						 float secs = duration/1000;
						 if (secs > wait) {
							 
							 break;
						 }
					 }
				 }
				 Solution best = solutions.get(solutions.size()-1);
				 PApplet.println(logPrefix+" Color opt timed out, best solution: "+best.score+" color changes");

				 for (int i = 0; i < best.layers.length; i++) {
					 for (int j = 0; j < best.layers[i].indices.size(); j++) {
						 ret.add(best.layers[i].indices.get(j));
					 }
				 }
			}

		 }
		 ColorOpt co = new ColorOpt();
		 co.main();
		 return ret;

			
	 }
	 void throwNPE() {// hack to throw null pointer exception
		PGraphics pg = null;
		if (app.random(1)>999) {// silence eclipse warning
			pg = app.createGraphics(0,0);
		}
		pg.rect(0,0,0,0);
		 
	}
	void pause() {
		try {Thread.sleep(Integer.MAX_VALUE);}catch(Exception e) {}
	}
	
	public void removePolylinesVertex(int i, int j) {
		if (j <= 1 || j >= polylines.get(i).size()-2) {
			if (j == 1) {
				polylines.get(i).remove(0);
				polylines.get(i).remove(0);
			}else if (j == polylines.get(i).size()-2) {
				polylines.get(i).remove(polylines.get(i).size()-1);
				polylines.get(i).remove(polylines.get(i).size()-1);
			}else {
				polylines.get(i).remove(j);
			}
			return;
		}
		if (beginCullIndex > i) {
			beginCullIndex ++;
		}
		if (optBlockIdx0 > i) {
			optBlockIdx0 ++;
		}
		if (repeatEndStartIndex > i) {
			repeatEndStartIndex ++;
		}
		polylines.get(i).remove(j);
		ArrayList<PVector> p = new ArrayList<PVector>();
		for (int k = polylines.get(i).size()-1; k >=j; k--) {
			p.add(0,polylines.get(i).get(k));
			polylines.get(i).remove(k);
		}
		polylines.add(i+1,p);
		colors.add(i+1,colors.get(i)+0);
		cullGroups.add(i+1,cullGroups.get(i)+0);
	}
	 
	public void eraser(float x, float y, float d) {
		boolean ok = true;
		boolean brk = false;
		for (int i = 0; i < polylines.size(); i++) {
			for (int j = 0; j < polylines.get(i).size(); j++) {
				if (polylines.get(i).get(j).dist(new PVector(x,y))<=d) {
					removePolylinesVertex(i,j);
					ok = false;
					brk = true;
					break;
				}
			}
			if (brk) {
				break;
			}
		}
		if (!ok) {
			eraser(x,y,d);
		}
	}
	
	public boolean checkOutOfBounds() {
		return checkOutOfBounds(OUT_OF_BOUNDS_HANDLER);
	}
	public boolean checkOutOfBounds(int handler) {
		
		ArrayList<PVector> ps = new ArrayList<PVector>();
		for (int i = 0; i < polylines.size(); i++) {
			for (int j = 0; j < polylines.get(i).size(); j++) {
				PVector p = polylines.get(i).get(j);
				if (p.x<0 || p.x > width || p.y < 0 || p.y > height) {
					ps.add(p.copy());
				}
			}
		}
		if (ps.size() == 0) {
			return true;
		}

		if (handler >= WARN) {
			PApplet.print(logPrefix+"Warning: some stitches are out of bounds:");
			for (int i = 0; i < ps.size(); i++) {
				PVector p = ps.get(i);
				PApplet.print(" ("+p.x+", "+p.y+")");
			}
			PApplet.print("\n");
			handler -= WARN;
			if (handler == CROP) {
				PApplet.println(logPrefix+"Cropping...");
			}else if (handler == IGNORE) {
				PApplet.println(logPrefix+"Ignoring...");
			}else if (handler == ASK) {
				PApplet.println(logPrefix+"Asking...");
			}else if (handler == ABORT) {
				PApplet.println(logPrefix+"Aborting...");
			}
		}
		if (handler == ABORT) {
//			throwNPE();
//			throw new RuntimeException();
			app.exit();
		}else if (handler == IGNORE) {
			return false;
		}else if (handler == CROP) {
			for (int i = 0; i < ps.size(); i++) {
				eraser(ps.get(i).x,ps.get(i).y,1);
			}
		}else if (handler == ASK) {
			Object[] options = {"Abort", "Crop", "Ignore" };
			int op = javax.swing.JOptionPane.showOptionDialog(null, "Some stitches are out of bounds! Please select an option:\n(use setOutOfBoundsHandler to silence this popup)", "Out of Bounds Warning",
					javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			if (op == 0) {
				handler = ABORT;
			}else if (op == 1) {
				handler = CROP;
			}else if (op == 2) {
				handler = IGNORE;
			}
			checkOutOfBounds(handler);
		}
		return false;
	}
	 
	public void beginComposite() {
		if (composite == null) {
			composite = new PEmbroiderBooleanShapeGraphics(width,height);
		}
		composite.background(0);
		composite.operator(PEmbroiderBooleanShapeGraphics.OR);
	}
	public void endComposite() {
		composite.filter(PConstants.THRESHOLD);
		composite.endDraw();
		image(composite,0,0);
		composite = null;
	}
	 

}