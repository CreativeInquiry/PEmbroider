

/** Finding contours in binary images and approximating polylines.
 *  Implements the same algorithms as OpenCV's findContours and approxPolyDP.
 *  <p>
 *  Made possible with support from The Frank-Ratchye STUDIO For Creative Inquiry
 *  At Carnegie Mellon University. http://studioforcreativeinquiry.org/
 *  @author Lingdong Huang
 */
public class PContour{
  
  static final int N_PIXEL_NEIGHBOR = 8;

  // give pixel neighborhood counter-clockwise ID's for
  // easier access with findContour algorithm
  int[] neighborIDToIndex(int i, int j, int id){
    if (id == 0){return new int[]{i,j+1};}
    if (id == 1){return new int[]{i-1,j+1};}
    if (id == 2){return new int[]{i-1,j};}
    if (id == 3){return new int[]{i-1,j-1};}
    if (id == 4){return new int[]{i,j-1};}
    if (id == 5){return new int[]{i+1,j-1};}
    if (id == 6){return new int[]{i+1,j};}
    if (id == 7){return new int[]{i+1,j+1};}
    return null;
  }
  int neighborIndexToID(int i0, int j0, int i, int j){
    int di = i - i0;
    int dj = j - j0;
    if (di == 0 && dj == 1){return 0;}
    if (di ==-1 && dj == 1){return 1;}
    if (di ==-1 && dj == 0){return 2;}
    if (di ==-1 && dj ==-1){return 3;}
    if (di == 0 && dj ==-1){return 4;}
    if (di == 1 && dj ==-1){return 5;}
    if (di == 1 && dj == 0){return 6;}
    if (di == 1 && dj == 1){return 7;}
    return -1;
  }

  // first counter clockwise non-zero element in neighborhood
  int[] ccwNon0(int[] F, int w, int h, int i0, int j0, int i, int j, int offset){
    int id = neighborIndexToID(i0,j0,i,j);
    for (int k = 0; k < N_PIXEL_NEIGHBOR; k++){
      int kk = (k+id+offset + N_PIXEL_NEIGHBOR*2) % N_PIXEL_NEIGHBOR;
      int[] ij = neighborIDToIndex(i0,j0,kk);
      if (F[ij[0]*w+ij[1]]!=0){
        return ij;
      }
    }
    return null;
  }

  // first clockwise non-zero element in neighborhood
  int[] cwNon0(int[] F, int w, int h, int i0, int j0, int i, int j, int offset){
    int id = neighborIndexToID(i0,j0,i,j);
    for (int k = 0; k < N_PIXEL_NEIGHBOR; k++){
      int kk = (-k+id-offset + N_PIXEL_NEIGHBOR*2) % N_PIXEL_NEIGHBOR;
      int[] ij = neighborIDToIndex(i0,j0,kk);
      if (F[ij[0]*w+ij[1]]!=0){
        return ij;
      }
    }
    return null;
  }

  /** Data structure for an integer-aligned coordinate on bitmap image*/
  public class Point{
    public int x;
    public int y;
    public Point(int _x, int _y){
      x = _x;
      y = _y;
    }
    public Point(Point p){
      x = p.x;
      y = p.y;
    }
  }

  /** Data structure for a contour, 
    * encodes vertices as well as hierarchical relationship to other contours
    */
  public class Contour{
    /** Vertices */
    public ArrayList<Point> points;
    /** Unique ID, starts from 2 */
    public int id;
    /** ID of parent contour, 0 means top-level contour */
    public int parent;
    /** Is this contour a hole (as opposed to outline) */
    public boolean isHole;
  }

  /**
   * Find contours in a binary image
   * <p>
   * Implements Suzuki, S. and Abe, K.
   * Topological Structural Analysis of Digitized Binary Images by Border Following.
   * <p>
   * See source code for step-by-step correspondence to the paper's algorithm
   * description.
   * @param  F    The bitmap, stored in 1-dimensional row-major form. 
   *              0=background, 1=foreground, will be modified by the function
   *              to hold semantic information
   * @param  w    Width of the bitmap
   * @param  h    Height of the bitmap
   * @return      An array of contours found in the image.
   * @see         Contour
   */
  public ArrayList<Contour> findContours(int[] F, int w, int h) {
    // Topological Structural Analysis of Digitized Binary Images by Border Following.
    // Suzuki, S. and Abe, K., CVGIP 30 1, pp 32-46 (1985)
    int nbd = 1;
    int lnbd = 1;

    ArrayList<Contour> contours = new ArrayList<Contour>();
    
    // Without loss of generality, we assume that 0-pixels fill the frame 
    // of a binary picture
    for (int i = 1; i < h-1; i++){
      F[i*w] = 0; F[i*w+w-1]=0;
    }
    for (int i = 0; i < w; i++){
      F[i] = 0; F[w*h-1-i]=0;
    }

    //Scan the picture with a TV raster and perform the following steps 
    //for each pixel such that fij # 0. Every time we begin to scan a 
    //new row of the picture, reset LNBD to 1.
    for (int i = 1; i < h-1; i++) {
      lnbd = 1;

      for (int j = 1; j < w-1; j++) {
        
        int i2 = 0, j2 = 0;
        if (F[i*w+j] == 0) {
          continue;
        }
        //(a) If fij = 1 and fi, j-1 = 0, then decide that the pixel 
        //(i, j) is the border following starting point of an outer 
        //border, increment NBD, and (i2, j2) <- (i, j - 1).
        if (F[i*w+j] == 1 && F[i*w+(j-1)] == 0) {
          nbd ++;
          i2 = i;
          j2 = j-1;
          
          
        //(b) Else if fij >= 1 and fi,j+1 = 0, then decide that the 
        //pixel (i, j) is the border following starting point of a 
        //hole border, increment NBD, (i2, j2) <- (i, j + 1), and 
        //LNBD + fij in case fij > 1.  
        } else if (F[i*w+j]>=1 && F[i*w+j+1] == 0) {
          nbd ++;
          i2 = i;
          j2 = j+1;
          if (F[i*w+j]>1) {
            lnbd = F[i*w+j];
          }
          
          
        } else {
          //(c) Otherwise, go to (4).
          //(4) If fij != 1, then LNBD <- |fij| and resume the raster
          //scan from pixel (i,j+1). The algorithm terminates when the
          //scan reaches the lower right corner of the picture
          if (F[i*w+j]!=1){lnbd = Math.abs(F[i*w+j]);}
          continue;
          
        }
        //(2) Depending on the types of the newly found border 
        //and the border with the sequential number LNBD 
        //(i.e., the last border met on the current row), 
        //decide the parent of the current border as shown in Table 1.
        // TABLE 1
        // Decision Rule for the Parent Border of the Newly Found Border B
        // ----------------------------------------------------------------
        // Type of border B'
        // \    with the sequential
        //     \     number LNBD
        // Type of B \                Outer border         Hole border
        // ---------------------------------------------------------------     
        // Outer border               The parent border    The border B'
        //                            of the border B'
        //
        // Hole border                The border B'      The parent border
        //                                               of the border B'
        // ----------------------------------------------------------------
        
        Contour B = new Contour();
        B.points = new ArrayList<Point>();
        B.points.add(new Point(j,i));
        B.isHole = (j2 == j+1);
        B.id = nbd;
        contours.add(B);

        Contour B0 = new Contour();
        for (int c = 0; c < contours.size(); c++){
          if (contours.get(c).id == lnbd){
            B0 = contours.get(c);
            break;
          }
        }
        if (B0.isHole){
          if (B.isHole){
            B.parent = B0.parent;
          }else{
            B.parent = lnbd;
          }
        }else{
          if (B.isHole){
            B.parent = lnbd;
          }else{
            B.parent = B0.parent;
          }
        }
        
        //(3) From the starting point (i, j), follow the detected border: 
        //this is done by the following substeps (3.1) through (3.5).
        
        //(3.1) Starting from (i2, j2), look around clockwise the pixels 
        //in the neigh- borhood of (i, j) and tind a nonzero pixel. 
        //Let (i1, j1) be the first found nonzero pixel. If no nonzero 
        //pixel is found, assign -NBD to fij and go to (4).
        int i1 = -1, j1 = -1;
        int[] i1j1 = cwNon0(F,w,h,i,j,i2,j2,0);
        if (i1j1 == null){
          F[i*w+j] = -nbd;
          //go to (4)
          if (F[i*w+j]!=1){lnbd = Math.abs(F[i*w+j]);}
          continue;
        }
        i1 = i1j1[0]; j1 = i1j1[1];
        
        // (3.2) (i2, j2) <- (i1, j1) ad (i3,j3) <- (i, j).
        i2 = i1;
        j2 = j1;
        int i3 = i;
        int j3 = j;
        
        
        while (true){
          //(3.3) Starting from the next elementof the pixel (i2, j2) 
          //in the counterclock- wise order, examine counterclockwise 
          //the pixels in the neighborhood of the current pixel (i3, j3) 
          //to find a nonzero pixel and let the first one be (i4, j4).
          
          int[] i4j4 = ccwNon0(F,w,h,i3,j3,i2,j2,1);
          int i4 = i4j4[0];
          int j4 = i4j4[1];
          
          contours.get(contours.size()-1).points.add(new Point(j4,i4));
          
          //(a) If the pixel (i3, j3 + 1) is a O-pixel examined in the
          //substep (3.3) then fi3, j3 <-  -NBD.
          if (F[i3*w+j3+1] == 0){
            F[i3*w+j3] = -nbd;
            
          //(b) If the pixel (i3, j3 + 1) is not a O-pixel examined 
          //in the substep (3.3) and fi3,j3 = 1, then fi3,j3 <- NBD.
          }else if (F[i3*w+j3] == 1){
            F[i3*w+j3] = nbd;
          }else{
            //(c) Otherwise, do not change fi3, j3.
          }
          
          //(3.5) If (i4, j4) = (i, j) and (i3, j3) = (i1, j1) 
          //(coming back to the starting point), then go to (4);
          if (i4 == i && j4 == j && i3 == i1 && j3 == j1){
            if (F[i*w+j]!=1){lnbd = Math.abs(F[i*w+j]);}
            break;
            
          //otherwise, (i2, j2) + (i3, j3),(i3, j3) + (i4, j4), 
          //and go back to (3.3).
          }else{
            i2 = i3;
            j2 = j3;
            i3 = i4;
            j3 = j4;
          }
        }
      }
    }
    return contours;
  }


  float pointDistanceToSegment(Point p, Point p0, Point p1) {
    // https://stackoverflow.com/a/6853926
    float x = p.x;   float y = p.y;
    float x1 = p0.x; float y1 = p0.y;
    float x2 = p1.x; float y2 = p1.y;
    float A = x - x1; float B = y - y1; float C = x2 - x1; float D = y2 - y1;
    float dot = A*C+B*D;
    float len_sq = C*C+D*D;
    float param = -1;
    if (len_sq != 0) {
      param = dot / len_sq;
    }
    float xx; float yy;
    if (param < 0) {
      xx = x1; yy = y1;
    }else if (param > 1) {
      xx = x2; yy = y2;
    }else {
      xx = x1 + param*C;
      yy = y1 + param*D;
    }
    float dx = x - xx;
    float dy = y - yy;
    return (float)Math.sqrt(dx*dx+dy*dy);
  }

  /**
   * Simplify contour by removing definately extraneous vertices, 
   * without modifying shape of the contour.
   * @param  polyline  The vertices
   * @return           A simplified copy
   * @see              approxPolyDP
   */
  public ArrayList<Point> approxPolySimple(ArrayList<Point> polyline){
    float epsilon = 0.1f;
    if (polyline.size() <= 2){
      return polyline;
    }
    ArrayList<Point> ret = new ArrayList<Point>();
    ret.add(new Point(polyline.get(0)));
      
    for (int i = 1; i < polyline.size()-1; i++){
      float d = pointDistanceToSegment(polyline.get(i), 
                                       polyline.get(i-1), 
                                       polyline.get(i+1));
      if (d > epsilon){
        ret.add(new Point(polyline.get(i)));
      }   
    }
    ret.add(new Point(polyline.get(polyline.size()-1)));
    return ret;
  }

  /**
   * Simplify contour using Douglas Peucker algorithm.
   * <p>   
   * Implements David Douglas and Thomas Peucker, 
   * "Algorithms for the reduction of the number of points required to 
   * represent a digitized line or its caricature", 
   * The Canadian Cartographer 10(2), 112–122 (1973)
   * @param  polyline  The vertices
   * @param  epsilon   Maximum allowed error
   * @return           A simplified copy
   * @see              approxPolySimple
   */
  public ArrayList<Point> approxPolyDP(ArrayList<Point> polyline, float epsilon){
    // https://en.wikipedia.org/wiki/Ramer–Douglas–Peucker_algorithm
    // David Douglas & Thomas Peucker, 
    // "Algorithms for the reduction of the number of points required to 
    // represent a digitized line or its caricature", 
    // The Canadian Cartographer 10(2), 112–122 (1973)
    
    if (polyline.size() <= 2){
      return polyline;
    }
    float dmax   = 0;
    int argmax = -1;
    for (int i = 1; i < polyline.size()-1; i++){
      float d = pointDistanceToSegment(polyline.get(i), 
                                       polyline.get(0), 
                                       polyline.get(polyline.size()-1));
      if (d > dmax){
        dmax = d;
        argmax = i;
      }  
    }
    ArrayList<Point> ret = new ArrayList<Point>();
    if (dmax > epsilon){
      ArrayList<Point> L = approxPolyDP(new ArrayList<Point>(polyline.subList(0,argmax+1)),epsilon);
      ArrayList<Point> R = approxPolyDP(new ArrayList<Point>(polyline.subList(argmax,polyline.size())),epsilon);
      ret.addAll(L.subList(0,L.size()-1));
      ret.addAll(R);
    }else{
      ret.add(new Point(polyline.get(0)));
      ret.add(new Point(polyline.get(polyline.size()-1)));
    }
    return ret;
  }
}
