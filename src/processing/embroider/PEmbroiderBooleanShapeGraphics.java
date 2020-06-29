package processing.embroider;

public class PEmbroiderBooleanShapeGraphics extends processing.awt.PGraphicsJava2D {
	public static final int UNION        = 1;
	public static final int OR           = 1;
	public static final int XOR          = 2;
	public static final int SYMMETRIC_DIFFERENCE = 2;
	public static final int INTERSECTION = 3;
	public static final int AND          = 3;
	public static final int SUBTRACT     = 4;
	public static final int DIFFERENCE   = 4;
	

	public PEmbroiderBooleanShapeGraphics (int width, int height){
		super();
		setSize(width,height);
		beginDraw();
		noStroke();
		background(0);
		fill(255);
	}
	
	public void operator(int mode){
		filter(THRESHOLD);
		blendMode(BLEND);
		if (mode == OR) {
			fill(255);
		}else if (mode == SUBTRACT) {
			fill(0);
		}else if (mode == XOR) {
			fill(255);
			blendMode(processing.core.PConstants.DIFFERENCE);
		}else if (mode == AND) {
			processing.core.PImage im = get();
			background(0);
			tint(100);
			image(im,0,0);
			fill(255,100);
			blendMode(processing.core.PConstants.ADD);
		}
	}
	public void union() {
		operator(UNION);
	}
	public void or() {
		operator(OR);
	}
	public void xor() {
		operator(XOR);
	}
	public void and() {
		operator(AND);
	}
	public void subtract() {
		operator(SUBTRACT);
	}
	public void difference() {
		operator(DIFFERENCE);
	}
	public void intersection() {
		operator(INTERSECTION);
	}
	public void symmetricDifference() {
		operator(SYMMETRIC_DIFFERENCE);
	}
	
	public void beginOps() {
		
	}
	
	public void endOps() {
		filter(THRESHOLD);
		endDraw();
	}
}
