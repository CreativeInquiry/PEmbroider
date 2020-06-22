float shapeCoordsData[][] = {
  {579, 51}, {712, 59}, {716, 182}, {744, 300}, {748, 345}, 
  {749, 389}, {732, 470}, {716, 510}, {692, 551}, {648, 618}, 
  {585, 670}, {471, 699}, {372, 729}, {282, 711}, {169, 643}, 
  {88, 562}, {50, 226}, {120, 159}, {252, 183}, {244, 350}, 
  {137, 234}, {137, 350}, {323, 487}, {293, 103}, {365, 78}, 
  {508, 459}, {496, 279}, {575, 276}, {665, 382}, {628, 119}, 
  {598, 228}, {474, 154}};
  
ArrayList<PVector> shapeCoords;
ArrayList<PVector> stitches;
ArrayList<PVector> marks;
int frame = 0;
  
PVector hiPt(ArrayList<PVector> poly){
  int mi = 0;
  float my = poly.get(0).y;
  for (int i = 1; i < poly.size(); i++){
    if (poly.get(i).y<my){
      mi = i;
      my = poly.get(i).y;
    }
  }
  return poly.get(mi);
}

PVector loPt(ArrayList<PVector> poly){
  int mi = 0;
  float my = poly.get(0).y;
  for (int i = 1; i < poly.size(); i++){
    if (poly.get(i).y>my){
      mi = i;
      my = poly.get(i).y;
    }
  }
  return poly.get(mi);
}

void maskOut(PGraphics pg, ArrayList<PVector> poly, int col){
  pg.beginDraw();
  pg.fill(col);
  //pg.stroke(col);
  //pg.strokeWeight(3);
  //pg.noStroke();
  pg.beginShape();
  for (int i=0; i<poly.size(); i++) {
    float px = poly.get(i).x;
    float py = poly.get(i).y;
    pg.vertex(px, py);
  }
  pg.endShape();
  pg.endDraw();
}

PVector closestVertex(ArrayList<PVector> poly, PVector p){
  int mi = 0;
  float md = poly.get(0).dist(p);
  for (int i = 1; i < poly.size(); i++){
    float d = poly.get(i).dist(p);
    if (d<md){
      mi = i;
      md = d;
    }
  }
  return poly.get(mi);
}


ArrayList<PVector> satinStitch(ArrayList<PVector> poly, float d, PVector p0){
  ArrayList<PVector> ret = new ArrayList<PVector>();
  PGraphics pg = createGraphics(width,height);
  pg.beginDraw();
  pg.background(0);
  //pg.fill(255,0,0);
  //pg.circle(p0.x,p0.y,20);
  maskOut(pg,poly,255);
  pg.endDraw();
  //pg.save("dump/"+random(100)+"c"+d+"p"+p0.x+"&"+p0.y+".png");
  
  BBox bb = new BBox(poly);
  ArrayList<PVector> walk = new ArrayList<PVector>();

  int n = ceil(bb.h/abs(d))*10000;
  PVector p = p0.copy();
  for (int i = 0; i < n; i++){
    p.y += d;
    
    pg.beginDraw();
    boolean isin = (pg.get(round(p.x),round(p.y))&255)==255;
    if (!isin){
      //println(p.x);
      boolean ok = false;
      
      for (int k = 0; k < 10; k++){
        PVector pl = p.copy();
        PVector pr = p.copy();
        boolean inl = false;
        boolean inr = false;
        int maxTrials = (k+1)*100;
        for (int trial = 0; trial < maxTrials; trial++){
          if (pl.x > 0){
            pl.x-=1;
            inl = (pg.get(round(pl.x),round(pl.y))&255)==255;
          }
          if (pr.x < pg.width-1){
            pr.x+=1;
            inr = (pg.get(round(pr.x),round(pr.y))&255)==255;
          }
          //marks.add(new PVector(pl.x,pl.y,2));
          //marks.add(new PVector(pr.x,pr.y,2));
          if (inl){
            p = pl;
            p.x-=4;
            ok = true;
            break;
          }
          if (inr){
            p = pr;
            p.x+=4;
            ok = true;
            break;
          }
        }
        if (ok){
          if (k < 3){
            isin = true;
          }
          break;
        }
        p.y += d > 0 ? 1 : -1;
      }
      //println(inl,inr,p.x);
    }
    pg.endDraw();
    
    
    marks.add(new PVector(p.x,p.y,2));
    walk.add(p.copy());
    
    PVector q0 = new PVector(p.x-bb.w*2,p.y);
    PVector q1 = new PVector(p.x+bb.w*2,p.y);
    ArrayList<PVector> rs0 = segmentIntersectPolygon(p,q0,poly);
    ArrayList<PVector> rs1 = segmentIntersectPolygon(p,q1,poly);
    if (rs0.size() %2 == 0 && rs1.size() %2==0){
      if (isin){
        println("BOOM!!!");
        p.y -= d;
        p.y += d > 0 ? 1 : -1;
        continue;
      }
      break;
    }
    PVector r0;
    PVector r1;
    if (rs0.size() > 0 && rs1.size() > 0){
      r0 = rs0.get(0);
      r1 = rs1.get(0);
    }else{
      println("BANG!!!");
      continue;
    }
      
    pg.beginDraw();
    pg.strokeCap(PROJECT);
    pg.strokeWeight(abs(d));
    pg.stroke(0);
    pg.line(r0.x,r0.y,r1.x,r1.y);
    pg.endDraw();
    p.x += ((r0.x+r1.x)/2-p.x);
    
    ret.add(r0);
    ret.add(r1);
    
    ArrayList<ArrayList<PVector>> contours = findContours(pg);
    ArrayList<Float> areas = new ArrayList<Float>();
    for (int j = 0; j < contours.size(); j++){
      areas.add(abs(polygonArea(contours.get(j))));
    }
    for (int j = contours.size()-1; j >= 0; j--){
      if (areas.get(j)<abs(d*d) || new BBox(contours.get(j)).h <= d){
        contours.remove(j);
        areas.remove(j);
      }
    }
    //println(contours.size());
    if (contours.size()==2){
  
      ArrayList<PVector> branch;
      ArrayList<PVector> trunk;

      BBox bb0 = new BBox(contours.get(0));
      BBox bb1 = new BBox(contours.get(1));
      boolean bo0 = (d > 0) ? (bb0.y+bb0.h < p.y) : (bb0.y > p.y);
      boolean bo1 = (d > 0) ? (bb1.y+bb1.h < p.y) : (bb1.y > p.y);
      if (bo0 && !bo1){
        branch = contours.get(0);
        trunk = contours.get(1);
      }else if (bo1 && !bo0){
        branch = contours.get(1);
        trunk = contours.get(0);
      }else if (areas.get(0)>areas.get(1)){
        trunk = contours.get(0);
        branch = contours.get(1);
      }else{
        trunk = contours.get(1);
        branch = contours.get(0);
      }
      BBox bbb = new BBox(branch);
      float bbbhi = bbb.y;
      float bbblo = bbb.y+bbb.h;
 
      PVector b0;
      float nd;
      if (abs(p.y-bbbhi)<abs(p.y-bbblo)){
        b0 = loPt(branch);
        nd = -abs(d);
      }else{
        b0 = hiPt(branch);
        nd = abs(d);
      }
      marks.add(new PVector(b0.x,b0.y,6));
      ret.addAll(satinStitch(branch,nd,b0));
      poly = trunk;
      
      //pg.save("dump/"+random(100)+"a.png");
      maskOut(pg,branch,0);
      //pg.save("dump/"+random(100)+"b.png");
      
      PVector u0 = new PVector(p.x+(r0.x-p.x)*0.5,p.y);
      PVector u1 = new PVector(p.x+(r1.x-p.x)*0.5,p.y);
      pg.beginDraw();
      if ((pg.get(round(u0.x),round(u0.y))&255)>128){
        p = u0;
      }else{
        p = u1;
      }
      pg.endDraw();
      //p.x += (r1.x-p.x)/2;
      //p.x += (r0.x-p.x)/2;
      //p = closestVertex(trunk,p);
      //p.x += 10;
      //p.y += 10;
      marks.add(new PVector(p.x,p.y,12));
    }else if (contours.size() == 1){
      BBox bbb = new BBox(contours.get(0));
      boolean bo0 = (d > 0 && (bbb.y+bbb.h) < p.y+abs(d)*1.2);
      boolean bo1 = (d < 0 && (bbb.y) > p.y-abs(d)*1.2);

      if ( bo0 || bo1){
        ArrayList<PVector> branch = contours.get(0);
        maskOut(pg,branch,0);
        float nd;
        PVector b0;
        if (bo0){
          nd = abs(d);
          b0 = hiPt(branch);
        }else{
          nd = -abs(d);
          b0 = loPt(branch);
        }
        ret.addAll(satinStitch(branch,nd,b0));
        break;
      }
    }else if (contours.size() > 2){
      println("NOOO!!!");
      for (int k = 0; k < contours.size(); k++){
        PVector b0 = hiPt(contours.get(k));
        ret.addAll(satinStitch(contours.get(k),abs(d),b0));
      }
      break;
    }
  }
  for (int i = 0; i < walk.size(); i++){
    ret.add(0,walk.get(i));
  }
  //image(pg,0,0);
  return ret;
}

void rotatePolygon(ArrayList<PVector> poly, float ang){
  PVector c = centerpoint(poly);
  for (int i = 0; i < poly.size(); i++){
    PVector p = poly.get(i);
    float a0 = atan2(p.y-c.y,p.x-c.x);
    float a = a0 + ang;
    float d = p.dist(c);
    poly.get(i).x = c.x+cos(a)*d;
    poly.get(i).y = c.y+sin(a)*d;
  }
}


void setup(){
  size(1000,1000);
  
  shapeCoords = new ArrayList<PVector>();
  for (int i=0; i<shapeCoordsData.length; i++) {
    float px = shapeCoordsData[i][0];
    float py = shapeCoordsData[i][1]+100;
    shapeCoords.add(new PVector(px, py));
  }
    
  marks = new ArrayList<PVector>();
  
  //rotatePolygon(shapeCoords,0.1f);
  
  PVector p0 = hiPt(shapeCoords);
  stitches = satinStitch(shapeCoords,8,p0);
    
}


void draw(){
  //marks.clear();
  
  
  
  //PVector p0 = hiPt(shapeCoords);
  //stitches = satinStitch(shapeCoords,8,p0);
  
  background(128);
  fill(255);
  stroke(0);
  beginShape();
  for (int i=0; i<shapeCoords.size(); i++) {
    float px = shapeCoords.get(i).x;
    float py = shapeCoords.get(i).y;
    vertex(px, py);
  }
  endShape(CLOSE);
  

  
  stroke(255,64,0);
  noFill();
  beginShape();
  //for (int i=0; i< frame%stitches.size(); i++) {
  for (int i=0; i< min(frame,stitches.size()); i++) {
  //for (int i = 0; i < stitches.size(); i++){
    float px = stitches.get(i).x;
    float py = stitches.get(i).y;
    vertex(px, py);
  }
  endShape();
  
  stroke(64,0,255);
  for (int i = 0; i < marks.size(); i++){
    circle(marks.get(i).x,marks.get(i).y,marks.get(i).z);
  }

  //save("dump/"+nf(299,4)+".png");
  
  //rotatePolygon(shapeCoords,0.1f);
  frame ++;
}

void keyPressed(){
  //frame=0;
}
