package S3;


/*************************************************************************
 *************************************************************************/

import java.util.Arrays;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.StdOut;

public class KdTree {
	
	private Node root;
	
	private static class Node {
		   private Point2D p;
		   private RectHV rect;
		   private boolean xOry;
		 
		   private Node right;
		   private Node left;
		   
		   public Node(Point2D p, boolean xOry, RectHV rect) {
	            this.p = p;
	            this.xOry = xOry;
	            this.right = null;
	            this.left = null;
	            this.rect = rect; 
		}
	}
    // construct an empty set of points
    public KdTree() {	
    	root = null;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    private int size = 0;
    // number of points in the set
    public int size() {
        return size;
    }
    
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
    		RectHV emptyRect = new RectHV(0,0,1,1);
    		root = insert(p, root, true, emptyRect);
    }
    
    private Node insert(Point2D p, Node temp, boolean trueXFalseY,RectHV newRect) { 
    	//Create new Node and update size 
    	if (temp == null) {
    		size++;
    		return new Node (p ,trueXFalseY, newRect);
    	}
    	//Make sure we don't insert two equal Nodes
    	if(temp.p.equals(p)) {
    		return temp;
    	}
    	//Compare the x coordinates for we are on a vertical line
    	//We recursively return the tree path left if less and right if more
    	if(trueXFalseY) {
    		
    		if(Point2D.X_ORDER.compare(p, temp.p) <  0) {
    			RectHV emptyRect = new RectHV(temp.rect.xmin(), temp.rect.ymin(), temp.p.x(), temp.rect.ymax());
    			temp.left = insert(p, temp.left, !trueXFalseY, emptyRect);
    		}
    		else if (Point2D.X_ORDER.compare(p, temp.p) >=  0){
    			RectHV emptyRect = new RectHV(temp.p.x(), temp.rect.ymin(), temp.rect.xmax(), temp.rect.ymax());
    			temp.right = insert(p, temp.right, !trueXFalseY, emptyRect);
    		}
    	}
    	//Compare the y coordinates for we are on a horizontal line
    	//Use same logic as before
    	else {
    		if(Point2D.Y_ORDER.compare(p, temp.p) <  0) {
    			RectHV emptyRect = new RectHV(temp.rect.xmin(), temp.rect.ymin(), temp.rect.xmax(), temp.p.y());
    			temp.left = insert(p, temp.left, !trueXFalseY, emptyRect);
    		}
    		else if(Point2D.Y_ORDER.compare(p, temp.p) >= 0){
    			RectHV emptyRect = new RectHV(temp.rect.xmin(), temp.p.y(), temp.rect.xmax(), temp.rect.ymax());
    			temp.right = insert(p, temp.right, !trueXFalseY, emptyRect);
    		}
    	}
    	return temp;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return contains(p, root, true);
    }
    
    private boolean contains(Point2D p, Node temp, boolean trueXFalseY) {
    	//Ended in null node tree does not contain p 
    	if(temp == null) {
    		return false;
    	}
    	//We found p so we return true
    	if(temp.p.equals(p)) {
    		return true;
    	}
    	//recursively go throw the tree like in insert
    	if(trueXFalseY) {
       		if(Point2D.X_ORDER.compare(p, temp.p) <  0) {
    			return contains(p, temp.left, !trueXFalseY);
    		}
    		else {
    			return contains(p, temp.right, !trueXFalseY);
    		}
    	}
    	else {
    		if(Point2D.Y_ORDER.compare(p, temp.p) <  0) {
    			return contains(p, temp.left, !trueXFalseY);
    		}
    		else {
    			return contains(p, temp.right, !trueXFalseY);
    		}
    	}
    }

    // draw all of the points to standard draw
    public void draw() {
    	StdDraw.setPenRadius(0.01);
    	StdDraw.setPenColor(StdDraw.BLACK);
    	StdDraw.rectangle(0.5, 0.5, 0.5, 0.5);
    	draw(root);
    }
    
    public void draw(Node temp) {
    	if(temp == null) {
    		return;
    	}
    	if(temp.xOry) {
        	StdDraw.setPenColor(StdDraw.RED);
        	StdDraw.line(temp.p.x(), temp.rect.ymin(), temp.p.x(), temp.rect.ymax());
        	StdDraw.setPenColor(StdDraw.BLACK);
        	StdDraw.point(temp.p.x(), temp.p.y());
    	}
    	else {
        	StdDraw.setPenColor(StdDraw.BLUE);
        	StdDraw.line(temp.rect.xmin(), temp.p.y(), temp.rect.xmax(), temp.p.y());
        	StdDraw.setPenColor(StdDraw.BLACK);
        	StdDraw.point(temp.p.x(), temp.p.y());
    	}
    	draw(temp.left);
    	draw(temp.right);
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
    	SET<Point2D> inRect = new SET<Point2D>();
    	range(rect, root, inRect);
    	return inRect;
    }
    
    private void range(RectHV rect,Node temp, SET<Point2D> inRect) {
    	if(temp == null) {
    		return;
    	}
    	//If the rectangle intersect with the node rectangle we must look at it's children 
    	if(rect.intersects(temp.rect)) {
    		range(rect, temp.left, inRect);
    		range(rect, temp.right, inRect);
    	}
    	//After we have collected all the rectangles that intersect with rect
    	//We check if their points are contained in the rect then add them to the SET
    	if(rect.contains(temp.p)) {
    		inRect.add(temp.p);
    	}
    }
    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
    	Point2D nearest = new Point2D(0, 0);
    	//Empty tree we return an empty Point2D
    	if(root == null) {
    		return nearest;
    	}
    	//At this moment we say that root.p is the nearest point
    	nearest = root.p;
        return nearest(p, root, nearest);
    }
    
    private Point2D nearest(Point2D p, Node temp, Point2D nearest) {
    	//No more points to check, return the best one
    	if(temp == null) {
    		return nearest;
    	}
    	//If temp.rect is closer then nearest in a particular node
    	//It is worth checking into else not
    	if(temp.rect.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
    		//If the point is closer than nearest we update nearest to that point
	    	if(temp.p.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
	    		nearest = temp.p;
	    	}
	    	nearest = nearest(p, temp.left, nearest);
	    	nearest = nearest(p, temp.right, nearest);
    	}
    	return nearest;

    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int nrOfRecangles = in.readInt();
        int nrOfPointsCont = in.readInt();
        int nrOfPointsNear = in.readInt();
        RectHV[] rectangles = new RectHV[nrOfRecangles];
        Point2D[] pointsCont = new Point2D[nrOfPointsCont];
        Point2D[] pointsNear = new Point2D[nrOfPointsNear];
        for (int i = 0; i < nrOfRecangles; i++) {
            rectangles[i] = new RectHV(in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsCont; i++) {
            pointsCont[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsNear; i++) {
            pointsNear[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        KdTree set = new KdTree();
        for (int i = 0; i < 100000; i++) {
            double x = in.readDouble(), y = in.readDouble();
            set.insert(new Point2D(x, y));
        }
        for (int i = 0; i < nrOfRecangles; i++) {
            // Query on rectangle i, sort the result, and print
            Iterable<Point2D> ptset = set.range(rectangles[i]);
            int ptcount = 0;
            for (Point2D p : ptset)
                ptcount++;
            Point2D[] ptarr = new Point2D[ptcount];
            int j = 0;
            for (Point2D p : ptset) {
                ptarr[j] = p;
                j++;
            }
            Arrays.sort(ptarr);
            out.println("Inside rectangle " + (i + 1) + ":");
            for (j = 0; j < ptcount; j++)
                out.println(ptarr[j]);
        }
        out.println("Contain test:");
        for (int i = 0; i < nrOfPointsCont; i++) {
            out.println((i + 1) + ": " + set.contains(pointsCont[i]));
        }
        Stopwatch stopwatch1 = new Stopwatch();
        out.println("Nearest test:");
        for (int i = 0; i < nrOfPointsNear; i++) {
            out.println((i + 1) + ": " + set.nearest(pointsNear[i]));
        }
        StdOut.println(stopwatch1.elapsedTime() );
        out.println();
    }
}



