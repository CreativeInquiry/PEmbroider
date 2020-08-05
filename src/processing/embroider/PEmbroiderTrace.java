package processing.embroider;

import java.util.ArrayList;

import processing.core.*;


public class PEmbroiderTrace {

	static String logPrefix = "[PEmbroider Trace] ";


	public static class DistanceTransform{
		// ported from JS, from https://github.com/parmanoir/Meijster-distance
		// beware! curly-braces-less blocks everywhere

		// Euclidean
		public static float EDT_f(float x, float i, float g_i) {
			return (x - i) * (x - i) + g_i * g_i;
		}
		public static float EDT_Sep(float i, float u, float g_i, float g_u) {
			return PApplet.floor((u * u - i * i + g_u * g_u - g_i * g_i) / (2 * (u - i)));
		}
		//	Meijster distance
		public static float[] getDistTransform(boolean[] booleanImage, int m, int n) {
			// First phase
			float infinity = m + n;
			boolean[] b = booleanImage;
			float[] g = new float[m * n];
			for (int x = 0; x < m; x++) {
				if (b[x + 0 * m])
					g[x + 0 * m] = 0;
				else
					g[x + 0 * m] = infinity;
				// Scan 1
				for (int y = 1; y < n; y++) {
					if (b[x + y * m])
						g[x + y * m] = 0;
					else
						g[x + y * m] = 1 + g[x + (y - 1) * m];
				}
				// Scan 2
				for (int y = n - 1; y >= 0; y--) {
					if (x+(y+1)*m < g.length) // my hack
						if (g[x + (y + 1) * m] < g[x + y * m])
							g[x + y * m] = 1 + g[x + (y + 1) * m];
				}
			}

			// Second phase
			float[] dt = new float[m * n];
			int[] s = new int[m];
			float[] t = new float[m];
			int q = 0;
			float w;
			for (int y = 0; y < n; y++) {
				q = 0;
				s[0] = 0;
				t[0] = 0;

				// Scan 3
				for (int u = 1; u < m; u++) {
					while (q >= 0 && EDT_f(t[q], s[q], g[s[q] + y * m]) > EDT_f(t[q], u, g[u + y * m]))
						q--;
					if (q < 0) {
						q = 0;
						s[0] = u;
					} else {
						w = 1 + EDT_Sep(s[q], u, g[s[q] + y * m], g[u + y * m]);
						if (w < m) {
							q++;
							s[q] = u;
							t[q] = w;
						}
					}
				}
				// Scan 4
				for (int u = m - 1; u >= 0; u--) {
					float d = EDT_f(u, s[q], g[s[q] + y * m]);

					d = PApplet.floor(PApplet.sqrt(d));
					dt[u + y * m] = d;
					if (u == t[q])
						q--;
				}
			}
			return dt;
		}
	}
	
	public static ArrayList<ArrayList<PVector>> findContours_naive(boolean[] im, int w, int h){
		ArrayList<PVector> edges = new ArrayList<PVector>();

		for (int i = 0; i < w * h; i++){
			int x = (int)(i%w);
			int y = (int)(i/w);
			if (x == 0 || y == 0 || x == w-1 || y == h -1){
				continue;
			}
			boolean c0 = im[i-w-1];
			boolean c1 = im[i-w  ];
			boolean c2 = im[i-w+1];
			boolean c3 = im[i-1         ];
			boolean c4 = im[i           ];
			boolean c5 = im[i+1         ];
			boolean c6 = im[i+w-1];
			boolean c7 = im[i+w  ];
			boolean c8 = im[i+w+1];

			if (c4 && (!c0 || !c1 || !c2 || !c3 || !c5 || !c6 || !c7 || !c8)){
				edges.add(new PVector(x,y));
				// im.pixels[i] = color(255,0,0);
			}
		}
		System.out.println(logPrefix+edges.size()+" edge pixels.");


		ArrayList<ArrayList<PVector>> contours = new ArrayList<ArrayList<PVector>>();
		ArrayList<ArrayList<PVector>> wip      = new ArrayList<ArrayList<PVector>>();
		if (edges.size() == 0) {
			return contours;
		}
		
		wip.add(new ArrayList<PVector>());
		wip.get(0).add(edges.remove(0));

		int d = 1;
		int maxd = 4;
		while (edges.size() > 0){
			for (int i = wip.size()-1; i >= 0; i--){
				boolean ok = false;

				for (int j = edges.size()-1; j >= 0; j--){
					if (PApplet.abs(wip.get(i).get(wip.get(i).size()-1).x - edges.get(j).x) +
							PApplet.abs(wip.get(i).get(wip.get(i).size()-1).y - edges.get(j).y) <=d){
						wip.get(i).add(edges.remove(j));
						ok = true;
					}
				}
				if (wip.size() >= 3 &&
						PApplet.abs(wip.get(i).get(wip.get(i).size()-1).x - wip.get(i).get(0).x) +
						PApplet.abs(wip.get(i).get(wip.get(i).size()-1).y - wip.get(i).get(0).y) <=d){
					ok = true;
					contours.add(wip.remove(i));
					wip.add(new ArrayList<PVector>());
					if (edges.size() > 0){
						wip.get(wip.size()-1).add(edges.remove(0));
					}
				}
				if (! ok){
					if (d < maxd){
						d++;
					}else{
						if (wip.get(i).size() > 1){
							contours.add(wip.remove(i));
						}else{
							wip.remove(i);
						}
						wip.add(new ArrayList<PVector>());
						if (edges.size() > 0){
							wip.get(wip.size()-1).add(edges.remove(0));
						}
					}
				}else if (d > 1){
					d--;
				}
			}
		}
		for (int i = 0; i < wip.size(); i++){
			if (wip.get(i).size() > 1){
				contours.addAll(wip);
			}
		}
		System.out.println(logPrefix+contours.size()+" contours.");
		return contours;
	}
	
	static final int N_PIXEL_NEIGHBOR = 8;

	// give pixel neighborhood counter-clockwise ID's for
	// easier access with findContour algorithm
	static int[] neighborIDToIndex(int i, int j, int id){
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
	static int neighborIndexToID(int i0, int j0, int i, int j){
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
	static int[] ccwNon0(int[] F, int w, int h, int i0, int j0, int i, int j, int offset){
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
	static int[] cwNon0(int[] F, int w, int h, int i0, int j0, int i, int j, int offset){
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
	
	public static ArrayList<ArrayList<PVector>> findContours(int[] F, int w, int h) {
		return findContours(F,w,h,null,null);
	}
	
	public static ArrayList<ArrayList<PVector>> findContours(int[] F, int w, int h, ArrayList<Boolean> oIsHole, ArrayList<Integer> oParent) {
		//ported from https://github.com/LingDong-/PContour/blob/master/src/pcontour/PContour.java

		int nbd = 1;
		int lnbd = 1;

		ArrayList<ArrayList<PVector>> contours = new ArrayList<ArrayList<PVector>>();

		for (int i = 1; i < h-1; i++){
			F[i*w] = 0; F[i*w+w-1]=0;
		}
		for (int i = 0; i < w; i++){
			F[i] = 0; F[w*h-1-i]=0;
		}
		for (int i = 1; i < h-1; i++) {
			lnbd = 1;

			for (int j = 1; j < w-1; j++) {

				int i2 = 0, j2 = 0;
				if (F[i*w+j] == 0) {
					continue;
				}
				if (F[i*w+j] == 1 && F[i*w+(j-1)] == 0) {
					nbd ++;
					i2 = i;
					j2 = j-1;
				} else if (F[i*w+j]>=1 && F[i*w+j+1] == 0) {
					nbd ++;
					i2 = i;
					j2 = j+1;
					if (F[i*w+j]>1) {
						lnbd = F[i*w+j];
					}
				} else {
					if (F[i*w+j]!=1){lnbd = Math.abs(F[i*w+j]);}
					continue;
				}
				int i1 = -1, j1 = -1;
				int[] i1j1 = cwNon0(F,w,h,i,j,i2,j2,0);
				if (i1j1 == null){
					F[i*w+j] = -nbd;
					if (F[i*w+j]!=1){lnbd = Math.abs(F[i*w+j]);}
					continue;
				}
				i1 = i1j1[0]; j1 = i1j1[1];

				i2 = i1;
				j2 = j1;
				int i3 = i;
				int j3 = j;

				contours.add(new ArrayList<PVector>());
				contours.get(contours.size()-1).add(new PVector(j,i));

				
		        boolean isHole = (j2 == j+1);
		        if (oIsHole != null) {
		        	oIsHole.add(isHole);
		        }
		        if (oParent != null) {
		        	int b0 = lnbd-2;
		        	int parent = -1;
		        	if (b0 >= 0) {
			        	if (oIsHole.get(b0)){
			        		if (isHole){
			        			parent = oParent.get(b0);
			        		}else{
			        			parent = b0;
			        		}
			        	}else{
			        		if (isHole){
			        			parent = b0;
			        		}else{
			        			parent = oParent.get(b0);
			        		}
			        	}
		        	}
		        	oParent.add(parent);
		        }
		        
				while (true){
					int[] i4j4 = ccwNon0(F,w,h,i3,j3,i2,j2,1);
					int i4 = i4j4[0];
					int j4 = i4j4[1];

					contours.get(contours.size()-1).add(new PVector(j4,i4));

					if (F[i3*w+j3+1] == 0){
						F[i3*w+j3] = -nbd;

					}else if (F[i3*w+j3] == 1){
						F[i3*w+j3] = nbd;
					}

					if (i4 == i && j4 == j && i3 == i1 && j3 == j1){
						if (F[i*w+j]!=1){lnbd = Math.abs(F[i*w+j]);}
						break;
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

	
	public static ArrayList<ArrayList<PVector>> findContours_naive(PImage im){
		boolean[] bim = new boolean[im.width*im.height];
		im.loadPixels();
		for (int i = 0; i < im.width * im.height; i++){
			bim[i] = (im.pixels[i]>>16&0xFF)>0x7F;
		}
		return findContours_naive(bim,im.width,im.height);
	}
	public static ArrayList<ArrayList<PVector>> findContours(PImage im){
		return findContours(im,null,null);
	}
	public static ArrayList<ArrayList<PVector>> findContours(PImage im, ArrayList<Boolean> oIsHole, ArrayList<Integer> oParent){
		
		int[] bim = new int[im.width*im.height];
		im.loadPixels();
		for (int i = 0; i < im.width * im.height; i++){
			bim[i] = (im.pixels[i]>>16&0xFF)>0x7F?1:0;
		}
		return findContours(bim,im.width,im.height,oIsHole,oParent);
	}
	
	public static ArrayList<PVector> approxPolyDP(ArrayList<PVector> polyline, float epsilon){

		if (polyline.size() <= 2){
			return polyline;
		}
		float dmax   = 0;
		int argmax = -1;
		for (int i = 1; i < polyline.size()-1; i++){
			float d = PEmbroiderGraphics.pointDistanceToSegment(polyline.get(i), 
					polyline.get(0), 
					polyline.get(polyline.size()-1));
			if (d > dmax){
				dmax = d;
				argmax = i;
			}  
		}
		ArrayList<PVector> ret = new ArrayList<PVector>();
		if (dmax > epsilon){
			ArrayList<PVector> L = approxPolyDP(new ArrayList<PVector>(polyline.subList(0,argmax+1)),epsilon);
			ArrayList<PVector> R = approxPolyDP(new ArrayList<PVector>(polyline.subList(argmax,polyline.size())),epsilon);
			ret.addAll(L.subList(0,L.size()-1));
			ret.addAll(R);
		}else{
			ret.add(polyline.get(0).copy());
			ret.add(polyline.get(polyline.size()-1).copy());
		}
		return ret;
	}
	
	public static ArrayList<ArrayList<ArrayList<PVector>>> findIsolines(PImage im, int n, float d) {
		int w = im.width;
		int h = im.height;
		im.loadPixels();
		boolean[] bim = new boolean[w*h];
		for (int i = 0; i < w*h; i++){
			bim[i] = ((im.pixels[i]>>16)&0xFF) < 128;
		}
		
		
		float[] dt = DistanceTransform.getDistTransform(bim,w,h);
		float m = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < w*h; i++){
			m = PApplet.max(m,dt[i]);
		}
		
		if (n == -1) {
			n = (int)((float)m/(float)d);
		}
		
		int[][] iso = new int[n][w*h];
		 
		float[] thresh = new float[n];
		for (int i = 0; i < n; i++) {
			thresh[i] = ((float)(i+1)/(float)(n+1))*m;
		}
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < w*h; i++) {		
				iso[j][i] =  dt[i] > thresh[j]?1:0;
			}
		}
	
		ArrayList<ArrayList<ArrayList<PVector>>> isolines = new ArrayList<ArrayList<ArrayList<PVector>>>();
		for (int j = 0; j < n; j++) {
			ArrayList<ArrayList<PVector>> c = findContours(iso[j],w,h);
			for (int i = 0; i < c.size(); i++) {
				c.set(i,approxPolyDP(c.get(i),1f));
			}
			isolines.add(c);
		}
		
		return isolines;
	}
	
	  // Binary image thinning (skeletonization) in-place.
	  // Implements Zhang-Suen algorithm.
	  // http://agcggs680.pbworks.com/f/Zhan-Suen_algorithm.pdf
	  public static void thinning(boolean[] im, int w, int h){
		  boolean[] prev = new boolean[w*h];
		  boolean diff = true;
		  do {
			  thinningIteration(im,w,h,0);
			  thinningIteration(im,w,h,1);
			  diff = false;
			  for (int i = 0; i < w*h; i++){
				  if (im[i] ^ prev[i]){
					  diff = true;
				  }
				  prev[i] = im[i];
			  }
		  }while (diff);
	  }

	  static void thinningIteration(boolean[] im, int w, int h, int iter) {
		  boolean[] marker = new boolean[w*h];
		  for (int i = 1; i < h-1; i++){
			  for (int j = 1; j < w-1; j++){

				  int p2 = im[(i-1)*w+j]  ?1:0;
				  int p3 = im[(i-1)*w+j+1]?1:0;
				  int p4 = im[(i)*w+j+1]  ?1:0;
				  int p5 = im[(i+1)*w+j+1]?1:0;
				  int p6 = im[(i+1)*w+j]  ?1:0;
				  int p7 = im[(i+1)*w+j-1]?1:0;
				  int p8 = im[(i)*w+j-1]  ?1:0;
				  int p9 = im[(i-1)*w+j-1]?1:0;

				  int A  = ((p2 == 0 && p3 == 1)?1:0) + ((p3 == 0 && p4 == 1)?1:0) + 
						  ((p4 == 0 && p5 == 1)?1:0) + ((p5 == 0 && p6 == 1)?1:0) + 
						  ((p6 == 0 && p7 == 1)?1:0) + ((p7 == 0 && p8 == 1)?1:0) +
						  ((p8 == 0 && p9 == 1)?1:0) + ((p9 == 0 && p2 == 1)?1:0);
				  int B  = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
				  int m1 = iter == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8);
				  int m2 = iter == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8);

				  if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0)
					  marker[i*w+j] = true;
			  }
		  }
		  for (int i = 0; i < h*w; i++){
			  im[i] = im[i] & (!marker[i]);
		  }
	  }
	  
	  public static boolean[] dilate5(boolean[] im, int w, int h){
		  boolean[] ret = new boolean[w*h];
		  for (int i = w+w; i < w*h-w-w; i++){
			  if (i%w<2){continue;}
			  if (i%w>=w-2){continue;}
			  if (im[i]){
				  ret[i-w-w-1] = true; ret[i-w-w] = true; ret[i-w-w+1] = true;
				  ret[i-w-2] = true;  ret[i-w-1] = true; ret[i-w] = true; ret[i-w+1] = true; ret[i-w+2] = true;
				  ret[i-2] = true;    ret[i-1] = true;   ret[i] = true;   ret[i+1] = true;   ret[i+2] = true;
				  ret[i+w-2] = true;  ret[i+w-1] = true; ret[i+w] = true; ret[i+w+1] = true; ret[i+w+2] = true;
				  ret[i+w+w-1] = true;   ret[i+w+w] = true;   ret[i+w+w+1] = true;
			  }
		  }
		  return ret;
	  }
	  public static float[] blur5(float[] im, int w, int h) {
		  float[] ret = new float[w*h];
		  for (int i = w+w; i < w*h-w-w; i++){
			  if (i%w<2){continue;}
			  if (i%w>=w-2){continue;}
			  ret[i] = (
				  im[i-w-w-2] +im[i-w-w-1] +  im[i-w-w] + im[i-w-w+1]+ im[i-w-w+2] +
				  im[i-w-2]   +  im[i-w-1] +    im[i-w] +   im[i-w+1]+   im[i-w+2] +
				  im[i-2]     +    im[i-1] +      im[i] +     im[i+1]+     im[i+2] +
				  im[i+w-2]   +  im[i+w-1] +    im[i+w] +   im[i+w+1]+   im[i+w+2] +
				  im[i+w+w-2] +im[i+w+w-1] +  im[i+w+w] + im[i+w+w+1]+ im[i+w+w+2])/25.0f;
		  }
		  return ret;
	  }
}
