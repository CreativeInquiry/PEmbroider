// Basic TSP implementation: Greedy + 2-Opt
// https://en.wikipedia.org/wiki/Travelling_salesman_problem
// https://en.wikipedia.org/wiki/2-opt

package processing.embroider;
import processing.core.*;
import java.util.*; 


public class PEmbroiderTSP {
	public static final String logPrefix = "[PEmbroider Optimize] ";
	public static boolean silent = false;
	
	public static class Edge{
		int i0; //index
		int i1;
		boolean r0; //left or right?
		boolean r1;
		float d; //distance
		PVector p0;
		PVector p1;
	}
	public static Edge reverseEdge(Edge e1){
		Edge e2 = new Edge();
		e2.i0 = e1.i1;
		e2.i1 = e1.i0;
		e2.r0 = e1.r1;
		e2.r1 = e1.r0;
		e2.p0 = e1.p1;
		e2.p1 = e1.p0;
		e2.d = e1.d;
		return e2;
	}
	public static void reverseEdges(List<Edge> edges){
		  Collections.reverse(edges);
		  for (int i = 0; i < edges.size(); i++){
		    edges.set(i,reverseEdge(edges.get(i)));
		  }
	}
	private static void log(String x) {
		if (!silent) {
			System.out.println(logPrefix+x);
		}
	}
	
	public static ArrayList<Edge> NN(ArrayList<ArrayList<PVector>> polylines, int i0){

		ArrayList<Edge> edges = new ArrayList<Edge>();
		
		boolean r0 = true;
		PVector p = polylines.get(i0).get(polylines.get(i0).size()-1);
		boolean[] mask = new boolean[polylines.size()];
		
		for (int i = 0; i < mask.length; i++){
			mask[i] = true;
		}
		mask[i0]=false;
		

		while (edges.size() < polylines.size()-1){
			int minI = 0;
			float minD = Float.POSITIVE_INFINITY;
			boolean minR = false;
			PVector minP = new PVector();
			PVector minQ = new PVector();
			for (int i = 0; i < polylines.size(); i++){
				if (!mask[i]){
					continue;
				}
				PVector q0 = polylines.get(i).get(0);
				PVector q1 = polylines.get(i).get(polylines.get(i).size()-1);
				float d0 = q0.dist(p);
				if (d0 < minD){
					minI = i;
					minD = d0;
					minR = false;
					minP = q0;
					minQ = q1;
				}
				float d1 = q1.dist(p);
				if (d1 < minD){
					minI = i;
					minD = d1;
					minR = true;
					minP = q1;
					minQ = q0;
				}
			}
			Edge e = new Edge();
			e.i0 = i0;
			e.i1 = minI;
			e.r0 = r0;
			e.r1 = minR;
			e.p0 = p;
			e.p1 = minP;
			e.d = minD;
			mask[minI] = false;
			edges.add(e);
			p = minQ;
			i0 = minI;
			r0 = !minR;
		}

		return edges;
	}


	public static void opt2(ArrayList<Edge> edges, int maxIter){
		int it = 0;
		while (it < maxIter){
			it ++;
			boolean change = false;
			for (int i = 0; i < edges.size(); i++){
				for (int j = i+1; j < edges.size(); j++){
					Edge e0 = edges.get(i);
					Edge e1 = edges.get(j);
					PVector o = PEmbroiderGraphics.segmentIntersect3D( e0.p0, e0.p1,e1.p0, e1.p1);
					if (o != null){
						Edge f0 = new Edge();
						Edge f1 = new Edge();

						f0.i0 = e0.i0;
						f0.r0 = e0.r0;
						f0.p0 = e0.p0;

						f0.i1 = e1.i0;
						f0.r1 = e1.r0;
						f0.p1 = e1.p0;

						f1.i0 = e0.i1;
						f1.r0 = e0.r1;
						f1.p0 = e0.p1;

						f1.i1 = e1.i1;
						f1.r1 = e1.r1;
						f1.p1 = e1.p1;

						f0.d = f0.p0.dist(f0.p1);
						f1.d = f1.p0.dist(f1.p1);

						edges.set(i,f0);
						edges.set(j,f1);

						reverseEdges(edges.subList(i+1, j));
						change = true;

					}
				}
			}
			if (!change){
				break;
			}
		}
	}
	
	public static float sumLength(ArrayList<Edge> edges) {
		float l = 0;
		for (int i = 0; i < edges.size(); i++) {
			l += edges.get(i).d;
		}
		return l;
	
	}
	public static float sumLengthPolylines(ArrayList<ArrayList<PVector>> polylines) {
		float l = 0;
		for (int i = 1; i < polylines.size(); i++) {
			if (polylines.get(i-1).size() > 0 && polylines.get(i).size() > 0) {
				l += polylines.get(i-1).get(polylines.get(i-1).size()-1).dist(polylines.get(i).get(0));
			}
		}
		return l;
	
	}
	
	public static ArrayList<ArrayList<PVector>> solve(ArrayList<ArrayList<PVector>> polylines) {
		return solve(polylines,5,999);
	}
	
	public static ArrayList<ArrayList<PVector>> solve(ArrayList<ArrayList<PVector>> polylines, int trials, int maxIter) {

		if (polylines.size() < 2) {
			return polylines;
		}
		ArrayList<ArrayList<PVector>> polylines2 = new ArrayList<ArrayList<PVector>>();
		ArrayList<ArrayList<PVector>> polylines3 = new ArrayList<ArrayList<PVector>>();
		for (int i = 0; i < polylines.size(); i++) {
			if (polylines.get(i).size() > 0) {
				polylines2.add(polylines.get(i));
			}
		}
		log("Input:"+sumLengthPolylines(polylines2));
		
		float minL = Float.POSITIVE_INFINITY;
		ArrayList<Edge> minE = new ArrayList<Edge>();
		for (int i = 0; i < trials; i++) {
			int b = 0;
			float ymin = Float.POSITIVE_INFINITY;
			float xmin = Float.POSITIVE_INFINITY;
			int yam = 0;
			int xam = 0;
			for (int ii = 0; ii < polylines2.size(); ii++) {
				for (int j = 0; j < polylines2.get(ii).size(); j++) {
					if (j != 0 && j != polylines.get(ii).size()-1) {
						continue;
					}
					PVector p = polylines2.get(ii).get(j);
					if (p.y < ymin) {
						yam = ii;
						ymin = p.y;
					}
					if (p.x < xmin) {
						xam = ii;
						xmin = p.x;
					}
				}
			}
			
			if (i == 0) {
				b = 0;
			}else if (i == 1) {
				b = yam;
			}else if (i == 2) {
				b = xam;
			}else {
				b = (int)(Math.random()*polylines2.size());
			}
			
			ArrayList<Edge> edges = NN(polylines2,b);

			float l0 = sumLength(edges);

			opt2(edges,maxIter);

			float l1 = sumLength(edges);

			log("Trial:"+i+"\tNN:"+l0+"\t2-Opt:"+l1);
			if (l1 < minL) {
				minL = l1;
				minE = edges;
			}
		}
		log("Final:"+minL);

		int next = -1;
		int zero = -1;
		boolean nr = false;
		
		while (minE.size()>0) {
			boolean ok = false;
			boolean end = false;
			for (int i = minE.size()-1; i >= 0; i--) {

				Edge e = minE.get(i);
				if (next == -1 || e.i0 == next) {
					ArrayList<PVector> p = polylines2.get(e.i0);
					if (!e.r0) {
						Collections.reverse(p);
					}
					ok = true;
					end = true;
					polylines3.add(p);
					next = e.i1;
					nr = e.r1;
					if (zero == -1) {
						zero = e.i0;
					}
					minE.remove(i);
					break;
				}
				if (e.i1 == zero) {
					ArrayList<PVector> p = polylines2.get(e.i0);
					if (!e.r0) {
						Collections.reverse(p);
					}
					ok = true;
					polylines3.add(0,p);
					zero = e.i0;
					
					minE.remove(i);
				}
			}
			if (!ok) {
				break;
			}
			if (!end) {
				ArrayList<PVector> q = polylines2.get(next);
				if (nr) {
					Collections.reverse(q);
				}
				polylines3.add(q);
			}
		}

		
		return polylines3;
	
	}

}
