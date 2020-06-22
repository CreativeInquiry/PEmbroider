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
  
ArrayList<ArrayList<PVector>> findContours(PImage img){
  PContour finder = new PContour();
  img.loadPixels();
  int[] im = new int[img.width*img.height];
  for (int i = 0; i < img.width*img.height; i++){
    if ((img.pixels[i]&255)>128){
      im[i] = 1;
    }else{
      im[i] = 0;
    }
  }
  ArrayList<ArrayList<PVector>> polys = new ArrayList<ArrayList<PVector>>();
  ArrayList<PContour.Contour> contours = finder.findContours(im,img.width,img.height);
  for (int i = 0; i < contours.size(); i++){
    contours.get(i).points = finder.approxPolyDP(contours.get(i).points,2);
    ArrayList<PVector> p = new ArrayList<PVector>();
    for (int j = 0; j < contours.get(i).points.size(); j++){
      p.add(new PVector(contours.get(i).points.get(j).x,contours.get(i).points.get(j).y));
    }
    polys.add(p);
  }
  return polys;
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
PVector centerpoint(ArrayList<PVector> poly) {
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
public boolean pointInPolygon(PVector p, ArrayList<PVector> poly, int trials){
    BCircle bcirc = new BCircle(poly);
    int avg = 0;
    for (int i = 0; i < trials; i++) {
      float a = random(0,PApplet.PI*2);
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
