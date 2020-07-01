# PEmbroider API Reference

### `PEmbroiderGraphics(PApplet _app, int w, int h)`
```
The constructor for PEmbroiderGraphics object.
```
|parameter|description|
|---|---|
|`_app`|```the running PApplet instance: in Processing, just pass the keyword ```|
|`w`|```width ```|
|`h`|```height ```|




-----------------


### `void setPath(String _path)`
```
Set the output file path.
```
|parameter|description|
|---|---|
|`_path`|```output file path. The format will be automatically inferred from the extension ```|




-----------------


### `void clear()`
```
Clear all current drawings 
```
|parameter|description|
|---|---|




-----------------


### `void fill(int r, int g, int b)`
```
Change fill color 
```
|parameter|description|
|---|---|
|`r`|```red color 0-255 ```|
|`g`|```green color 0-255 ```|
|`b`|```blue color 0-255 ```|


**return** `stroke `



-----------------


### `void fill(int gray)`
```
Change fill color 
```
|parameter|description|
|---|---|
|`gray`|```grayscale value 0-255 ```|




-----------------


### `void noFill()`
```
Disable filling shapes 
```
|parameter|description|
|---|---|




-----------------


### `void stroke(int r, int g, int b)`
```
Change stroke color 
```
|parameter|description|
|---|---|
|`r`|```red color 0-255 ```|
|`g`|```green color 0-255 ```|
|`b`|```blue color 0-255 ```|


**return** `fill `



-----------------


### `void stroke(int gray)`
```
Change stroke color 
```
|parameter|description|
|---|---|
|`gray`|```grayscale value 0-255 ```|




-----------------


### `void noStroke()`
```
Disable outlining shapes 
```
|parameter|description|
|---|---|




-----------------


### `void strokeWeight(float d)`
```
Change width of stroke 
```
|parameter|description|
|---|---|
|`d`|```the stroke weight to use ```|




-----------------


### `void strokeJoin(int j)`
```
Change stroke join (turning point) style 
```
|parameter|description|
|---|---|
|`j`|```Same as Processing strokejoin, this can be ROUND, MITER, BEVEL etc. ```|


**return** `strokeCap `



-----------------


### `void strokeCap(int j)`
```
Change stroke cap (end point) style 
```
|parameter|description|
|---|---|
|`j`|```Same as Processing strokeCap, this can be ROUND, SQUARE, PROJECT etc. ```|


**return** `strokejoin `



-----------------


### `void ellipseMode(int mode)`
```
Modifies the location from which ellipses are drawn by changing the way in which parameters given to ellipse() are intepreted. also governs circle()
```
|parameter|description|
|---|---|
|`j`|```Same as Processing ellipseMode, this can be RADIUS, CENTER, CORNER, CORNERS etc. ```|


**return** `rectMode `



-----------------


### `void rectMode(int mode)`
```
Modifies the location from which rectangles are drawn by changing the way in which parameters given to rect() are intepreted.
```
|parameter|description|
|---|---|
|`j`|```Same as Processing rectMode, this can be RADIUS, CENTER, CORNER, CORNERS etc. ```|


**return** `ellipseMode `



-----------------


### `void bezierDetail(int n)`
```
Change number of steps bezier curve is interpolated.
```
|parameter|description|
|---|---|
|`n`|```higher this number, smoother the bezier ```|




-----------------


### `void hatchMode(int mode)`
```
Change hatching pattern
```
|parameter|description|
|---|---|
|`mode`|```this can be one of PARALLEL, CROSS, CONCENTRIC, SPIRAL, PERLIN, VECFIELD, DRUNK ```|




-----------------


### `void strokeMode(int mode)`
```
Change outline drawing method
```
|parameter|description|
|---|---|
|`mode`|```this can be either PERPENDICULAR or TANGENT ```|




-----------------


### `void strokeMode(int mode, int tanMode)`
```
Change outline drawing method
```
|parameter|description|
|---|---|
|`mode`|```this can be either PERPENDICULAR or TANGENT ```|
|`tanMode`|```this can be one of COUNT (stroke weight used as line count), WEIGHT (honour stroke weight setting over spacing) or SPACING (honour spacing over stroke weight) ```|




-----------------


### `void hatchAngle(float ang)`
```
Change angle of parallel hatch lines
```
|parameter|description|
|---|---|
|`ang`|```the angle from +x in radians ```|


**return** `hatchAngleDeg `

**return** `hatchAngles `



-----------------


### `void hatchAngles(float ang1, float ang2)`
```
Change angles of parallel and cross hatching lines
```
|parameter|description|
|---|---|
|`ang1`|```the angle from +x in radians (for parallel hatches and the first direction of cross hatching) ```|
|`ang2`|```the angle from +x in radians (for second direction of cross hatching) ```|


**return** `hatchAngle `

**return** `hatchAnglesDeg `



-----------------


### `void hatchAngleDeg(float ang)`
```
Change angle of parallel hatch lines
```
|parameter|description|
|---|---|
|`ang`|```the angle from +x in degrees ```|


**return** `hatchAngle `

**return** `hatchAnglesDeg `



-----------------


### `void hatchAnglesDeg(float ang1, float ang2)`
```
Change angles of parallel and cross hatching lines
```
|parameter|description|
|---|---|
|`ang1`|```the angle from +x in degrees (for parallel hatches and the first direction of cross hatching) ```|
|`ang2`|```the angle from +x in degrees (for second direction of cross hatching) ```|


**return** `hatchAngles `

**return** `hatchAngleDeg `



-----------------


### `void hatchSpacing(float d)`
```
Changes the spacing between hatching lines: a.k.a sparsity or anti-density
```
|parameter|description|
|---|---|
|`d`|```the spacing in pixels ```|


**return** `strokeSpacing `



-----------------


### `void strokeSpacing(float d)`
```
Changes the spacing between stroke lines: a.k.a sparsity or anti-density
```
|parameter|description|
|---|---|
|`d`|```the spacing in pixels ```|


**return** `hatchSpacing `



-----------------


### `void hatchScale(float s)`
```
Changes the scaling for perlin noise hatching
```
|parameter|description|
|---|---|
|`s`|```the scale ```|




-----------------


### `void hatchBackend(int mode)`
```
Switches the algorithms used to compute the drawing
```
|parameter|description|
|---|---|
|`mode`|```one of ADAPTIVE (use most appropriate method for each situation according to Lingdong) FORCE_VECTOR (uses vector math whenever possible) FORCE_RASTER (first render shapes as raster and re-extract the structures, generally more robust) ```|




-----------------


### `void setVecField(VectorField vf)`
```
Set the vector field used for vector field hatching
```
|parameter|description|
|---|---|
|`vf`|```a vector field defination ```|




-----------------


### `void stitchLength(float x)`
```
Set the desirable stitch length. Stitches will try their best to be around this length, but actual length will vary slightly for best result
```
|parameter|description|
|---|---|
|`x`|```the desirable stitch length ```|


**return** `minSitchLength `

**return** `setStitch `



-----------------


### `void minStitchLength(float x)`
```
Set the minimum stitch length. Drawings with higher precision than this will be resampled down to have at least this stitch length
```
|parameter|description|
|---|---|
|`x`|```the minimum stitch length ```|


**return** `stichLength `

**return** `setStitch `



-----------------


### `void setStitch(float msl, float sl, float rn)`
```
Set stitch properties
```
|parameter|description|
|---|---|
|`msl`|```minimum stitch length ```|
|`sl`|```desirable stitch length ```|
|`rn`|```resample noise ```|




-----------------


### `void setRenderOrder(int mode)`
```
Set render order: render strokes over fill, or other way around?
```
|parameter|description|
|---|---|
|`mode`|```this can either be STROKE_OVER_FILL or FILL_OVER_STROKE ```|




-----------------


### `void toggleResample(boolean b)`
```
Turn resampling on and off. For embroidery machines, you might want it; for plotters you probably won't need it.
```
|parameter|description|
|---|---|
|`b`|```true for on, false for off ```|




-----------------


### `PVector centerpoint(ArrayList< PVector > poly)`
```
Averages a bunch of points
```
|parameter|description|
|---|---|
|`poly`|```a bunch of points ```|


**return** `a vector holding the average value `



-----------------


### `PVector centerpoint(ArrayList< ArrayList< PVector >> poly, int whatever)`
```
Averages a bunch of bunches of points
```
|parameter|description|
|---|---|
|`poly`|```a bunch of bunches of points ```|
|`whatever`|```literally pass in whatever. It is necessary because of type erasure in Java, which basically means Java cannot tell the difference between List<T> and List<List<T>> in arguments, so a difference in number of arguments is required for overloading to work ```|


**return** `a vector holding the average value `



-----------------


### `ArrayList<PVector> segmentIntersectPolygon(PVector p0, PVector p1, ArrayList< PVector > poly)`
```
Find intersection between a segment and a polygon. Intersections returned are sorted from one endpoint of the segment to the other endpoint
```
|parameter|description|
|---|---|
|`p0`|```first endpoint of first segment ```|
|`p1`|```second endpoint of first segment ```|
|`poly`|```the polygon ```|


**return** `a list of vectors holding the intersection points sorted from one endpoint to the other `



-----------------


### `ArrayList<PVector> segmentIntersectPolygons(PVector p0, PVector p1, ArrayList< ArrayList< PVector >> polys)`
```
Find intersection between a segment and several polygons. Intersections returned are sorted from one endpoint of the segment to the other endpoint
```
|parameter|description|
|---|---|
|`p0`|```first endpoint of first segment ```|
|`p1`|```second endpoint of first segment ```|
|`poly`|```several polygons ```|


**return** `a list of vectors holding the intersection points sorted from one endpoint to the other `



-----------------


### `boolean pointInPolygon(PVector p, ArrayList< PVector > poly, int trials)`
```
Check if a point is inside a polygon.
```
|parameter|description|
|---|---|
|`p`|```the point in question ```|
|`poly`|```the polygon ```|
|`trials`|```try a couple times to be sure, otherwise we might encounter degenerate cases ```|


**return** `true means inside, false means outside `



-----------------


### `boolean pointInPolygon(PVector p, ArrayList< PVector > poly)`
```
Check if a point is inside a polygon. Dumbed down version of pointinPolygon(3) where I pick the number of trials for you
```
|parameter|description|
|---|---|
|`p`|```the point in question ```|
|`poly`|```the polygon ```|


**return** `true means inside, false means outside `



-----------------


### `PVector randomPointInPolygon(ArrayList< PVector > poly, int trials)`
```
Generate a random point that is inside a polygon
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`trials`|```number of times we try before giving up ```|


**return** `either null or a point inside the polygon. If it is null, it either means the polygon has zero or almost zero area, or that the number of trials specified is not large enough to find one `



-----------------


### `PVector randomPointInPolygon(ArrayList< PVector > poly)`
```
Generate a random point that is inside a polygon Dumbed down version of randomPointInPolygon(2) where I pick the number of trials for you
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|


**return** `either null or a point inside the polygon. If it is null, it either means the polygon has zero or almost zero area, or that the default number of trials is not large enough to find one `



-----------------


### `void pushPolyline(ArrayList< PVector > poly, int color, float resampleRandomizeOffset)`
```
Add a polyline to the global array of all polylines drawn Applying transformation matrices and resampling All shape drawing routines go through this function for finalization
```
|parameter|description|
|---|---|
|`poly`|```a polyline ```|
|`color`|```the color of the polyline (0xRRGGBB) ```|
|`resampleRandomOffset`|```whether to add a random offset during resample step to prevent alignment patterns ```|




-----------------


### `void pushPolyline(ArrayList< PVector > poly, int color)`
```
Simplified version for pushPolyline(3) where resampleRandomizeOffset is set to false 
```
|parameter|description|
|---|---|
|`poly`|```a polyline ```|
|`color`|```the color of the polyline (0xRRGGBB) ```|




-----------------


### `ArrayList<PVector> offsetPolyline(ArrayList< PVector > poly, float d)`
```
offset a polyline by certain amount (for outlining strokes, naive implementation) 
```
|parameter|description|
|---|---|
|`poly`|```a polyline ```|
|`d`|```offset amount, positive/negative for inward/outward ```|




-----------------


### `ArrayList<ArrayList<PVector> > selfIntersectPolygon(ArrayList< PVector > poly)`
```
Turn a self-intersecting polygon to a list of non-self-intersecting polygons 
```
|parameter|description|
|---|---|
|`poly`|```a polygon ```|




-----------------


### `boolean polygonOrientation(ArrayList< PVector > poly)`
```
Check the winding orientation of polygon, clockwise or anti-clockwise 
```
|parameter|description|
|---|---|
|`poly`|```a polygon ```|


**return** `whether the polygon is positively oriented `



-----------------


### `float pointDistanceToLine(PVector p, PVector p0, PVector p1)`
```
Calculate distance between point and line 
```
|parameter|description|
|---|---|
|`p`|```the point in question ```|
|`p0`|```a point on the line ```|
|`p1`|```a different point on the line ```|


**return** `distance from point to line `



-----------------


### `ArrayList<ArrayList<PVector> > insetPolygon(ArrayList< PVector > poly, float d)`
```
Inset a polygon (making it slightly smaller fitting in the original polygon) Inverse of offsetPolygon, alias for offsetPolygon(poly, -d); 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```the amount of inset ```|


**return** `a list of polygons. When the given polygon is big at both ends and small in the middle, the inset might break into multiple polygons `



-----------------


### `ArrayList<ArrayList<PVector> > offsetPolygon(ArrayList< PVector > poly, float d)`
```
Offset a polygon (making it slightly smaller fitting in the original polygon, or slightly bigger wrapping the original polygon) 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```the amount of offset. Use negative value for insetting ```|


**return** `a list of polygons. When the given polygon is big at both ends and small in the middle, the inset might break into multiple polygons `



-----------------


### `int rotatePolygonToMatch(ArrayList< PVector > poly0, ArrayList< PVector > poly1)`
```
When the general shapes of two polygons are similar, this function finds the index offset of vertices for the first polygon so that the vertices best match those of the second polygon with one-to-one correspondence. The number of vertices needs to be the same for the polygons 
```
|parameter|description|
|---|---|
|`poly0`|```the first polygon ```|
|`poly1`|```the second polygon ```|


**return** `the optimal index offset `



-----------------


### `ArrayList<ArrayList<PVector> > strokePolygonTangent(ArrayList< PVector > poly, int n, float d)`
```
Draw stroke (outline) for a polygon using the TANGENT style (using vector math) 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`n`|```number of strokes ```|
|`d`|```spacing between the strokes ```|


**return** `an array of polylines `



-----------------


### `ArrayList<ArrayList<PVector> > strokePolyTangentRaster(ArrayList< ArrayList< PVector >> polys, int n, float d, int cap, int join, boolean close)`
```
Draw stroke (outline) for (a) poly(gon/line)(s) using the TANGENT style (using raster algorithms) 
```
|parameter|description|
|---|---|
|`polys`|```a set of polyline/polygons, those inside another and have a backward winding will be considered holes ```|
|`n`|```number of strokes ```|
|`d`|```spacing between the strokes ```|
|`cap`|```stroke cap, one of the Processing stroke caps, e.g. ROUND ```|
|`join`|```stroke join, one of the Processing stroke joins, e.g. MITER ```|
|`close`|```whether the polyline is considered as closed (polygon) or open (polyline) ```|


**return** `an array of polylines `



-----------------


### `ArrayList<ArrayList<PVector> > strokePolyNormal(ArrayList< PVector > poly, float d, float s, boolean close)`
```
Draw stroke (outline) for a poly(gon/line) using the PERPENDICULAR (a.k.a normal, as in "normal map", not "normal person") style (using vector math) 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```weight of stroke ```|
|`s`|```spacing between the strokes ```|
|`close`|```whether the polyline is considered as closed (polygon) or open (polyline) ```|


**return** `an array of polylines `



-----------------


### `void _stroke(ArrayList< ArrayList< PVector >> polys, boolean close)`
```
draws stroke (outline) for (a) poly(gon/line)(s) using current global settings by the user. returns nothing, because it draws to the design directly. 
```
|parameter|description|
|---|---|
|`polys`|```a set of polyline/polygons, those inside another and have a backward winding will be considered holes ```|
|`close`|```whether the polyline is considered as closed (polygon) or open (polyline) ```|




-----------------


### `ArrayList<ArrayList<PVector> > hatchInset(ArrayList< PVector > poly, float d, int maxIter)`
```
hatch a polygon with CONCENTRIC (a.k.a inset) mode (using vector math). a simplified version of hatchInset(4) where orientation of polygon is always checked 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```hatch spacing ```|
|`maxIter`|```maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchInset(ArrayList< PVector > poly, float d, int maxIter, boolean checkOrientation)`
```
hatch a polygon with CONCENTRIC (a.k.a inset) mode (using vector math). 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```hatch spacing ```|
|`maxIter`|```maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required ```|
|`checkOrientation`|```make sure the polygon is rightly oriented, this should be on for user provided input, otherwise it might hatch outwards ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchSpiral(ArrayList< PVector > poly, float d, int maxIter, boolean reverse)`
```
hatch a polygon with SPIRAL mode (using vector math). a simplified version of hatchSpiral(4) where orientation of polygon is always checked 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```hatch spacing ```|
|`maxIter`|```maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchSpiral(ArrayList< PVector > poly, float d, int maxIter, boolean checkOrientation, boolean reverse)`
```
hatch a polygon with SPIRAL mode (using vector math). 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```hatch spacing ```|
|`maxIter`|```maximum number of iterations to do inset. The larger the polygon and smaller the spacing, the more iterations is required ```|
|`checkOrientation`|```make sure the polygon is rightly oriented, this should be on for user provided input, otherwise it might hatch outwards ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchPerlin(ArrayList< PVector > poly, float d, float len, float scale, int maxIter)`
```
Hatch a polygon with PERLIN mode. The vector frontend to perlinField 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`d`|```hatch spacing ```|
|`len`|```the length of a step when walking the vector field ```|
|`scale`|```scale of the perlin noise ```|
|`maxIter`|```maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchCustomField(ArrayList< PVector > poly, VectorField vf, float d, float len, int maxIter)`
```
Hatch a polygon with custom vector field The vector frontend to customField 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`vf`|```the vector field ```|
|`d`|```hatch spacing ```|
|`len`|```the length of a step when walking the vector field ```|
|`maxIter`|```maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchParallel(ArrayList< PVector > poly, float ang, float d)`
```
Hatch a polygon with PARALLEL mode (using vector math) 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`ang`|```the angle of hatch lines ```|
|`d`|```hatch spacing ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchParallelComplex(ArrayList< ArrayList< PVector >> polys, float ang, float d)`
```
Hatch a complex polygon (with holes) with PARALLEL mode (using vector math) 
```
|parameter|description|
|---|---|
|`polys`|```a set of polygons, those inside another will be considered holes. Holes are determined according to the even-odd rule ```|
|`ang`|```the angle of hatch lines ```|
|`d`|```hatch spacing ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchDrunkWalk(ArrayList< PVector > poly, int rad, int maxIter)`
```
Hatch a polygon with the DRUNK style (not a particularly useful style, just to showcase how easy it is to add a new one). The vector implementation 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`rad`|```max size of each drunken step in each direction ```|
|`maxIter`|```maximum number of iterations to do the drunk walk ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<ArrayList<PVector> > hatchDrunkWalkRaster(PImage im, int rad, int maxIter)`
```
Hatch a polygon with the DRUNK style (not a particularly useful style, just to showcase how easy it is to add a new one). The raster implementation 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|
|`rad`|```max size of each drunken step in each direction ```|
|`maxIter`|```maximum number of iterations to do the drunk walk ```|


**return** `the hatching as an array of polys `



-----------------


### `ArrayList<PVector> resample(ArrayList< PVector > poly, float minLen, float maxLen, float randomize, float randomizeOffset)`
```
Resample a polyline to make it stitchable 
```
|parameter|description|
|---|---|
|`poly`|```the polyline ```|
|`minLen`|```minimum length of each segment, segment shorter than this will be downsampled ```|
|`maxLen`|```maximum length of each segment (not counting the randomization added on top, specified by randomize) ```|
|`randomize`|```amount of randomization in stitch length to avoid alignment patterns. 0 for none ```|
|`randomizeOffset`|```amount of randomization to add to the offset of the first stitch in every polyline. 0 for none ```|


**return** `the resampled polyline `



-----------------


### `ArrayList<PVector> resampleN(ArrayList< PVector > poly, int n)`
```
Resample a polyline to make it have N vertices. Can be an upsample or a downsample. Each resultant segment will be the same length, +/- floating point percision errors 
```
|parameter|description|
|---|---|
|`poly`|```the polyline ```|
|`n`|```the desired number of vertices ```|


**return** `the resampled polyline `



-----------------


### `ArrayList<PVector> resampleNKeepVertices(ArrayList< PVector > poly, int n)`
```
Resample a polyline to make it have N vertices, while keeping important turning points. Can be an upsample or a downsample. Each resultant segment have approximately same length, but compromising to the preserving of corners 
```
|parameter|description|
|---|---|
|`poly`|```the polyline ```|
|`n`|```the desired number of vertices ```|


**return** `the resampled polyline `



-----------------


### `ArrayList<ArrayList<PVector> > resampleCrossIntersection(ArrayList< ArrayList< PVector >> polys, float angle, float spacing, float len, float offsetFactor, float randomize)`
```
Resample a set of (parallel) polylines by intersecting them with another bunch of parallel lines and using the intersections to break them up. Useful for parallel hatching. 
```
|parameter|description|
|---|---|
|`polys`|```the polylines ```|
|`angle`|```the angle of the original polylines ```|
|`spacing`|```the spacing of the original polylines ```|
|`len`|```the desired length of each segment after resampling ```|


**return** `the resampled polylines `



-----------------


### `void hatchRaster(PImage im, float x, float y)`
```
Hatch a raster image with global user settings. Returns nothing because the result is directly pushed to the design. 
```
|parameter|description|
|---|---|
|`im`|```a processing image, PGraphics also qualify ```|
|`x`|```x coordinate of upper left corner to start drawing ```|
|`y`|```y coordinate of upper left corner to start drawing ```|




-----------------


### `void hatchRaster(PImage im)`
```
Hatch a raster image with global user settings. Returns nothing because the result is directly pushed to the design. Simplified version of hatchRaster(3), draws at 0,0 
```
|parameter|description|
|---|---|
|`im`|```a processing image, PGraphics also qualify ```|




-----------------


### `void ellipse(float a, float b, float c, float d)`
```
Draw a ellipse 
```
|parameter|description|
|---|---|
|`a`|```the first parameter, the meaning of which depends on ellipseMode ```|
|`b`|```the second parameter, the meaning of which depends on ellipseMode ```|
|`c`|```the third parameter, the meaning of which depends on ellipseMode ```|
|`d`|```the fourth parameter, the meaning of which depends on ellipseMode ```|




-----------------


### `void rect(float a, float b, float c, float d)`
```
Draw a rectangle 
```
|parameter|description|
|---|---|
|`a`|```the first parameter, the meaning of which depends on rectMode ```|
|`b`|```the second parameter, the meaning of which depends on rectMode ```|
|`c`|```the third parameter, the meaning of which depends on rectMode ```|
|`d`|```the fourth parameter, the meaning of which depends on rectMode ```|




-----------------


### `void line(float x0, float y0, float x1, float y1)`
```
Draw a line 
```
|parameter|description|
|---|---|
|`x0`|```x coordinate of first endpoint ```|
|`y0`|```y coordinate of first endpoint ```|
|`x1`|```x coordinate of second endpoint ```|
|`y1`|```y coordinate of second endpoint ```|




-----------------


### `void quad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3)`
```
Draw a quad 
```
|parameter|description|
|---|---|
|`x0`|```x coordinate of first vertex ```|
|`y0`|```y coordinate of first vertex ```|
|`x1`|```x coordinate of second vertex ```|
|`y1`|```y coordinate of second vertex ```|
|`x2`|```x coordinate of third vertex ```|
|`y2`|```y coordinate of third vertex ```|
|`x3`|```x coordinate of fourth vertex ```|
|`y3`|```y coordinate of fourth vertex ```|




-----------------


### `void triangle(float x0, float y0, float x1, float y1, float x2, float y2)`
```
Draw a triangle 
```
|parameter|description|
|---|---|
|`x0`|```x coordinate of first vertex ```|
|`y0`|```y coordinate of first vertex ```|
|`x1`|```x coordinate of second vertex ```|
|`y1`|```y coordinate of second vertex ```|
|`x2`|```x coordinate of third vertex ```|
|`y2`|```y coordinate of third vertex ```|




-----------------


### `void beginShape()`
```
Begin drawing a polygon/polyline. use vertex() to add vertices to it. 
```
|parameter|description|
|---|---|




-----------------


### `void vertex(float x, float y)`
```
Add vertex to the current polygon/polyline. This must be preceded by beginShape() 
```
|parameter|description|
|---|---|
|`x`|```x coordinate of the vertex ```|
|`y`|```y coordinate of the vertex ```|




-----------------


### `void bezierVertex(float x1, float y1, float x2, float y2, float x3, float y3)`
```
Add a cubic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex() An alias of cubicVertex 
```
|parameter|description|
|---|---|
|`x1`|```x coordinate of first control point ```|
|`y1`|```y coordinate of first control point ```|
|`x2`|```x coordinate of second control point ```|
|`y2`|```y coordinate of second control point ```|
|`x3`|```x coordinate of the end point ```|
|`y3`|```y coordinate of the end point ```|




-----------------


### `void rationalVertex(float x1, float y1, float x2, float y2, float w)`
```
Add a rational quadratic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex() 
```
|parameter|description|
|---|---|
|`x1`|```x coordinate of first control point ```|
|`y1`|```y coordinate of first control point ```|
|`x2`|```x coordinate of second control point ```|
|`y2`|```y coordinate of second control point ```|
|`w`|```weight of the rational bezier curve, higher the pointier ```|




-----------------


### `void quadraticVertex(float x1, float y1, float x2, float y2)`
```
Add a quadratic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex() 
```
|parameter|description|
|---|---|
|`x1`|```x coordinate of first control point ```|
|`y1`|```y coordinate of first control point ```|
|`x2`|```x coordinate of second control point ```|
|`y2`|```y coordinate of second control point ```|




-----------------


### `void cubicVertex(float x1, float y1, float x2, float y2, float x3, float y3)`
```
Add a cubic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex() 
```
|parameter|description|
|---|---|
|`x1`|```x coordinate of first control point ```|
|`y1`|```y coordinate of first control point ```|
|`x2`|```x coordinate of second control point ```|
|`y2`|```y coordinate of second control point ```|
|`x3`|```x coordinate of the end point ```|
|`y3`|```y coordinate of the end point ```|




-----------------


### `void quarticVertex(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)`
```
Add a quartic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex() 
```
|parameter|description|
|---|---|
|`x1`|```x coordinate of first control point ```|
|`y1`|```y coordinate of first control point ```|
|`x2`|```x coordinate of second control point ```|
|`y2`|```y coordinate of second control point ```|
|`x3`|```x coordinate of third control point ```|
|`y3`|```y coordinate of third control point ```|
|`x4`|```x coordinate of the end point ```|
|`y4`|```y coordinate of the end point ```|




-----------------


### `void quinticVertex(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float x5, float y5)`
```
Add a quintic bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex() 
```
|parameter|description|
|---|---|
|`x1`|```x coordinate of first control point ```|
|`y1`|```y coordinate of first control point ```|
|`x2`|```x coordinate of second control point ```|
|`y2`|```y coordinate of second control point ```|
|`x3`|```x coordinate of third control point ```|
|`y3`|```y coordinate of third control point ```|
|`x4`|```x coordinate of fourth control point ```|
|`y4`|```y coordinate of fourth control point ```|
|`x5`|```x coordinate of the end point ```|
|`y5`|```y coordinate of the end point ```|




-----------------


### `void highBezierVertex(ArrayList< PVector > poly)`
```
Add a higher-order bezier vertex to the current polygon/polyline. This must be preceded by beginShape() and at least a vertex() 
```
|parameter|description|
|---|---|
|`poly`|```control points and end point ```|




-----------------


### `void endShape(boolean close)`
```
End drawing a polygon. at this moment the polygon will be actually drawn to the design 
```
|parameter|description|
|---|---|
|`close`|```whether or not to close the polyline (forming polygon) ```|




-----------------


### `void endShape()`
```
Alias for endShape(bool) that does not close the shape 
```
|parameter|description|
|---|---|




-----------------


### `void endShape(int close)`
```
Alias for endShape(bool) that closes the shape 
```
|parameter|description|
|---|---|
|`close`|```pass anything, but use CLOSE for readability ```|




-----------------


### `void beginContour()`
```
Begin a contour within the current polygon, if this contour has negative winding and is inside the current polygon or another contour defined between beginShape() and endShape() that is not a hole, this will be a hole 
```
|parameter|description|
|---|---|




-----------------


### `void endContour()`
```
Done with adding a contour 
```
|parameter|description|
|---|---|




-----------------


### `void circle(float x, float y, float r)`
```
Draw a circle, a.k.a ellipse with equal radius in each dimension 
```
|parameter|description|
|---|---|
|`x`|```the first parameter, meaning of which depends on ellipseMode ```|
|`y`|```the second parameter, meaning of which depends on ellipseMode ```|
|`r`|```the third parameter, meaning of which depends on ellipseMode ```|




-----------------


### `void pushMatrix()`
```
Save the current state of transformation 
```
|parameter|description|
|---|---|




-----------------


### `void popMatrix()`
```
Restore previous state of transformatino 
```
|parameter|description|
|---|---|




-----------------


### `void translate(float x, float y)`
```
Translate subsequent drawing calls 
```
|parameter|description|
|---|---|
|`x`|```x offset to translate ```|
|`y`|```y offset to translate ```|




-----------------


### `void rotate(float a)`
```
Rotate subsequent drawing calls 
```
|parameter|description|
|---|---|
|`a`|```angle in radians to rotate ```|




-----------------


### `void shearX(float x)`
```
Shear subsequent drawing calls along x-axis 
```
|parameter|description|
|---|---|
|`x`|```shear amount ```|




-----------------


### `void shearY(float x)`
```
Shear subsequent drawing calls along y-axis 
```
|parameter|description|
|---|---|
|`x`|```shear amount ```|




-----------------


### `void scale(float x)`
```
Scale subsequent drawing calls proportionally 
```
|parameter|description|
|---|---|
|`x`|```multiplier on both axes ```|




-----------------


### `void scale(float x, float y)`
```
Scale subsequent drawing calls disproportionally 
```
|parameter|description|
|---|---|
|`x`|```multiplier on x axis ```|
|`y`|```multiplier on y axis ```|




-----------------


### `void visualize(boolean color, boolean stitches, boolean route, int nStitches)`
```
Visualize the current design on the main Processing canvas 
```
|parameter|description|
|---|---|
|`color`|```whether to visualize color, if false, will use random colors; if stitches argument is true, this will have no effect and black will always be used for visibility ```|
|`stitches`|```whether to visualize stitches, i.e. little dots on end of segments ```|
|`route`|```whether to visualize the path between polylines that will be taken by embroidery machine/plotter. To be able to not see a mess when enabling this option, try optimize() ```|




-----------------


### `void visualize()`
```
Visualize the current design on the main Processing canvas, using default set of options 
```
|parameter|description|
|---|---|




-----------------


### `void beginDraw()`
```
Supposed to initialize something, but as we currently don't need to, this is a NOP 
```
|parameter|description|
|---|---|




-----------------


### `void endDraw()`
```
Save the drawing to file 
```
|parameter|description|
|---|---|




-----------------


### `ArrayList<PVector> resampleDouble(ArrayList< PVector > poly)`
```
Double the number of vertices in the polyline by spliting every segment by half 
```
|parameter|description|
|---|---|
|`poly`|```the polyline ```|


**return** `the resampled polyline `



-----------------


### `ArrayList<PVector> resampleHalf(ArrayList< PVector > poly)`
```
Half the number of vertices in the polyline by joining every two segments 
```
|parameter|description|
|---|---|
|`poly`|```the polyline ```|


**return** `the resampled polyline `



-----------------


### `ArrayList<PVector> resampleHalfKeepCorners(ArrayList< PVector > poly, float maxTurn)`
```
Approximately halfing the number of vertices in the polyline but preserves important turning points 
```
|parameter|description|
|---|---|
|`poly`|```the polyline ```|
|`maxTurn`|```amount of turning for a vertex to be considered important ```|


**return** `the resampled polyline `



-----------------


### `void beginCull()`
```
Begin culling shapes. Culled shapes occlude each other 
```
|parameter|description|
|---|---|




-----------------


### `void endCull()`
```
End culling shapes. Culled shapes occlude each other 
```
|parameter|description|
|---|---|




-----------------


### `void image(PImage im, int x, int y, int w, int h)`
```
Draw an image 
```
|parameter|description|
|---|---|
|`im`|```a PImage, PGraphics also qualify ```|
|`x`|```left ```|
|`y`|```top ```|
|`w`|```width ```|
|`h`|```height ```|




-----------------


### `ArrayList<ArrayList<PVector> > perlinField(PImage mask, float d, float perlinScale, float deltaX, int minVertices, int maxVertices, int maxIter)`
```
Hatch an image using perlin noise fill 
```
|parameter|description|
|---|---|
|`mask`|```a binary image/graphics, white means on, black means off ```|
|`d`|```spacing between strokes ```|
|`perlinScale`|```perlin noise scale ```|
|`deltaX`|```step size of the walk in vector field ```|
|`minVertices`|```strokes with too few segments (those tiny ones) will be discarded ```|
|`maxVertices`|```maximum number of vertices for each stroke ```|
|`maxIter`|```maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners ```|




-----------------


### `ArrayList<ArrayList<PVector> > customField(PImage mask, VectorField vf, float d, int minVertices, int maxVertices, int maxIter)`
```
Hatch an image using custom vector field fill 
```
|parameter|description|
|---|---|
|`mask`|```a binary image/graphics, white means on, black means off ```|
|`d`|```spacing between strokes ```|
|`minVertices`|```strokes with too few segments (those tiny ones) will be discarded ```|
|`maxVertices`|```maximum number of vertices for each stroke ```|
|`maxIter`|```maximum number of iterations (i.e. seeds to begin walking from). if the shape of polygon is weird, more seeds is needed to reach all the corners ```|




-----------------


### `ArrayList<ArrayList<PVector> > hatchParallelRaster(PImage mask, float ang, float d, float step)`
```
Hatch an image using PARALLEL fill (with raster algorithms) 
```
|parameter|description|
|---|---|
|`mask`|```a binary image/graphics, white means on, black means off ```|
|`ang`|```angle of parallel lines in radians ```|
|`d`|```spacing between strokes ```|
|`step`|```step size, smaller the more accurate ```|


**return** `the hatching as a list of polylines `



-----------------


### `float lerp360(float h0, float h1, float t)`
```
Lerp a number around 360 degrees, meaning lerp360(358,2) gives 0 instead of 180 Useful for lerping hues and angles 
```
|parameter|description|
|---|---|
|`h0`|```the first number ```|
|`h1`|```the second number ```|
|`t`|```the interpolation parameter ```|


**return** `the lerped number `



-----------------


### `ArrayList<PVector> smoothen(ArrayList< PVector > poly, float w, int n)`
```
Smoothen a polyline with rational quadratic bezier (thus upsampling) 
```
|parameter|description|
|---|---|
|`poly`|```the polyline ```|
|`w`|```weight of the rational bezier, higher the pointier ```|
|`n`|```number of segments for each segment on the polyline ```|


**return** `the smoothed polyline `



-----------------


### `float [] perfectDistanceTransform(ArrayList< ArrayList< PVector >> polys, int w, int h)`
```
Computes the accurate distance transform by actually mesuring distances (slow). See PEmbroiderTrace for the fast approximation 
```
|parameter|description|
|---|---|
|`polys`|```the polylines to consider ```|
|`w`|```width of the output image ```|
|`n`|```height of the output image ```|


**return** `the distance transform stored in row-major array of floats `



-----------------


### `void clip(ArrayList< ArrayList< PVector >> polys, PImage mask)`
```
Clip polylines with a mask 
```
|parameter|description|
|---|---|
|`polys`|```the polylines to consider ```|
|`mask`|```a mask, black means off, white means on ```|




-----------------


### `ArrayList<ArrayList<PVector> > isolines(PImage im, float d)`
```
Compute isolines for a grayscale image using findContours on multiple thresholds. 
```
|parameter|description|
|---|---|
|`im`|```the input grayscale image ```|
|`d`|```luminance distance between adjacent thresholds ```|


**return** `an array of isolines as polylines `



-----------------


### `boolean polygonContain(ArrayList< PVector > poly0, ArrayList< PVector > poly1)`
```
Check if a polygon completely contains another 
```
|parameter|description|
|---|---|
|`poly0`|```the polygon that is supposed to contain the other one ```|
|`poly1`|```the polygon that is supposed to be contained by the other one ```|


**return** `true for containment, false for not `



-----------------


### `float polygonArea(ArrayList< PVector > poly)`
```
Compute area of polygon 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|


**return** `the area `



-----------------


### `void optimize(int trials, int maxIter)`
```
Run TSP on polylines in the current design to optimize for connective path length 
```
|parameter|description|
|---|---|
|`trials`|```number of trials. the more times you try, the higher chance you'll get a better result ```|
|`maxIter`|```maximum number of iterations to run 2-Opt. ```|




-----------------


### `void optimize()`
```
Simplified version of optimize(2) where the trial and maxIter is picked for you 
```
|parameter|description|
|---|---|




-----------------


### `void textAlign(int align)`
```
Change horizontal alignment of text 
```
|parameter|description|
|---|---|
|`align`|```alignment mode, one of LEFT, CETER, RIGHT ```|




-----------------


### `void textAlign(int halign, int valign)`
```
Change horizontal and vertical alignment of text 
```
|parameter|description|
|---|---|
|`halign`|```horizontal alignment mode, one of LEFT, CETER, RIGHT ```|
|`valign`|```vertical alignment mode, one of TOP, CENTER, BASELINE, BOTTOM ```|




-----------------


### `void textSize(float size)`
```
Change size of text 
```
|parameter|description|
|---|---|
|`size`|```the desired size of text ```|




-----------------


### `void textFont(int[] font)`
```
Change font of text. Notice this function is overloaded with one version for PFont and one for hershey font, with identical API This one is for the hershey font 
```
|parameter|description|
|---|---|
|`font`|```the desired hershey font ```|




-----------------


### `void textFont(PFont font)`
```
Change font of text. Notice this function is overloaded with one version for PFont and one for hershey font, with identical API This one is for PFont 
```
|parameter|description|
|---|---|
|`font`|```the desired PFont ```|




-----------------


### `void text(String str, float x, float y)`
```
Draw some text 
```
|parameter|description|
|---|---|
|`str`|```the string containing the text to be drawn ```|
|`x`|```x coordinate, meaning of which depends on textAlign ```|
|`y`|```y coordinate, meaning of which depends on textAlign ```|




-----------------


### `static float det(PVector r1, PVector r2, PVector r3)`
```
Compute the determinant of a 3x3 matrix as 3 row vectors.
```
|parameter|description|
|---|---|
|`r1`|```row 1 ```|
|`r2`|```row 2 ```|
|`r3`|```row 3 ```|


**return** `the determinant `



-----------------


### `static PVector segmentIntersect3D(PVector p0, PVector p1, PVector q0, PVector q1)`
```
Intersect two segments in 3D, returns lerp params instead of actual points, because the latter can cheaply be derived from the former; more expensive the other way around. Also works for 2D.
```
|parameter|description|
|---|---|
|`p0`|```first endpoint of first segment ```|
|`p1`|```second endpoint of first segment ```|
|`q0`|```first endpoint of second segment ```|
|`q1`|```second endpoint of second segment ```|


**return** `vec where vec.x is the lerp param for first segment, and vec.y is the lerp param for the second segment `



-----------------


### `static float pointDistanceToSegment(PVector p, PVector p0, PVector p1)`
```
Calculate distance between point and a segment (when projection is not on the line, the distance becomes that to one of the endpoints) 
```
|parameter|description|
|---|---|
|`p`|```the point in question ```|
|`p0`|```first endpoint of the segment ```|
|`p1`|```second endpoint of the segment ```|


**return** `distance from point to segment `



-----------------


### `static Object deepClone(Object object)`
```
Deep clone any object 
```
|parameter|description|
|---|---|
|`object`|```some object @reutrn the clone ```|




-----------------


### `void _ellipse(float cx, float cy, float rx, float ry)`
```
Draw an ellipse using global settings. The unambigous backend for user-facing ellipse(), the ambigous one which can be affected by ellipseMode(). Returns nothing because the result is directly pushed to the design. 
```
|parameter|description|
|---|---|
|`cx`|```the x coordinate of the center ```|
|`cy`|```the y coordinate of the center ```|
|`rx`|```the radius in the x axis ```|
|`ry`|```the radius in the y axis ```|




-----------------


### `void _rect(float x, float y, float w, float h)`
```
Draw a rectangle using global settings. The unambigous backend for user-facing rect(), the ambigous one which can be affected by rectMode(). Returns nothing because the result is directly pushed to the design. 
```
|parameter|description|
|---|---|
|`x`|```left ```|
|`y`|```top ```|
|`w`|```width ```|
|`h`|```height ```|




-----------------


### `float calcAxisAngleForParallel(float ang)`
```
Hatch a polygon with global user settings (using vector math). Returns nothing because the result is directly pushed to the design. 
```
|parameter|description|
|---|---|
|`poly`|```the polygon ```|




-----------------


### `PVector rationalQuadraticBezier(PVector p0, PVector p1, PVector p2, float w, float t)`
```
Compute a point on a rational quadratic bezier curve 
```
|parameter|description|
|---|---|
|`p0`|```first point ```|
|`p1`|```control point ```|
|`p2`|```last point ```|
|`w`|```weight of the rational bezier, higher the weight, pointier the turning ```|
|`t`|```the interpolation parameter (generally 0-1) ```|




-----------------


### `PVector quadraticBezier(PVector p0, PVector p1, PVector p2, float t)`
```
Compute a point on a quadratic bezier curve 
```
|parameter|description|
|---|---|
|`p0`|```first point ```|
|`p1`|```control point ```|
|`p2`|```last point ```|
|`t`|```the interpolation parameter (generally 0-1) ```|




-----------------


### `PVector cubicBezier(PVector p0, PVector p1, PVector p2, PVector p3, float t)`
```
Compute a point on a cubic bezier curve 
```
|parameter|description|
|---|---|
|`p0`|```first point ```|
|`p1`|```control point ```|
|`p2`|```another control point ```|
|`p3`|```last point ```|
|`t`|```the interpolation parameter (generally 0-1) ```|




-----------------


### `PVector highBezier(ArrayList< PVector > P, float t)`
```
Compute a point on a higher-order bezier curve 
```
|parameter|description|
|---|---|
|`P`|```points and control points ```|
|`t`|```the interpolation parameter (generally 0-1) ```|




-----------------


