package onelinergraph;

import java.util.*;


public class OneLinerGraph{
  public class Node{
    public int x;
    public int y;
    public ArrayList<Edge> edges;
    public boolean visited;
  }
  public class Edge{
    public Node left;
    public Node right;
    public int pid;
    public boolean visited;
  }
  public class EdgeVisit{
    public boolean reversed;
    public Edge edge;
  }
  public ArrayList<Node> nodes;
  public ArrayList<Edge> edges;
  public ArrayList<ArrayList<int[]>> polylines;
  
  public OneLinerGraph (ArrayList<ArrayList<int[]>> _polylines){
    if (nodes != null){
      nodes.clear();
    }else{
      nodes = new ArrayList<Node>();
    }
    if (edges != null){
      edges.clear();
    }else{
      edges = new ArrayList<Edge>();
    }
    
    polylines = _polylines;
    for (int i = 0; i < polylines.size(); i++){
      if (polylines.get(i).size() < 2){
        continue;
      }
      int[] head = polylines.get(i).get(0);
      int[] tail = polylines.get(i).get(polylines.get(i).size()-1);
      
      Node hn = gewNode(head[0],head[1]);
      Node tn = gewNode(tail[0],tail[1]);
      
      Edge e = new Edge();
      e.left = hn;
      e.right = tn;
      e.pid = i;
      
      hn.edges.add(e);
      tn.edges.add(e);
      edges.add(e);
      
    }
  }
  
  Node gewNode(int x, int y){
    for (int j = 0; j < nodes.size(); j++){
      if (Math.abs(nodes.get(j).x - x)<1 && Math.abs(nodes.get(j).y - y)<1){
        return nodes.get(j);
      }
    }
    Node n = new Node();
    n.x = x;
    n.y = y;
    n.edges = new ArrayList<Edge>();
    n.visited = false;
    nodes.add(n);
    return n;
  }
  
  ArrayList<EdgeVisit> visitNode(Node node){
    ArrayList<EdgeVisit> path = new ArrayList<EdgeVisit>();
    if (node.visited){
      return path;
    }
    node.visited = true;
    for (int i = 0; i < node.edges.size(); i++){
      Edge e = node.edges.get(i);
      boolean dir = e.right == node;
      
      Node nxt = e.left == node ? e.right : e.left;
      if (!nxt.visited || !e.visited){
        e.visited = true;
        EdgeVisit ev = new EdgeVisit();
        ev.reversed = dir;
        ev.edge = e;
        path.add(ev);

        path.addAll(visitNode(nxt));

        EdgeVisit rev = new EdgeVisit();
        rev.reversed = !ev.reversed;
        rev.edge = ev.edge;
        path.add(rev);
      }
    }
    return path;
  }
  
  public ArrayList<int[]> solve(){
    ArrayList<int[]> path = new ArrayList<int[]>();
    Integer start = null;
    for (int i = 0; i < nodes.size(); i++){
      if (!nodes.get(i).visited){
        start = i;
        break;
      }
    }
    if (start == null){
      return null;
    }
    ArrayList<EdgeVisit> visits = visitNode(nodes.get(start));
    for (int i = 0; i < visits.size(); i++){
      EdgeVisit ev = visits.get(i);
      ArrayList<int[]> p = polylines.get(ev.edge.pid);
      if (ev.reversed){
        Collections.reverse(p);
        path.addAll(p);
        Collections.reverse(p);
      }else{
        path.addAll(p);
      }
    }
    return path;
  }
  
  
  public ArrayList<ArrayList<int[]>> multiSolve(){
    ArrayList<ArrayList<int[]>> paths = new ArrayList<ArrayList<int[]>>();
    ArrayList<int[]> sol;
    while (true){
      sol = solve();
      if (sol == null){
        break;
      }
      paths.add(sol);
    }
    return paths;
  }
}
