import java.util.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/**
 *	Ryan Mould - RM631
 *	Our solution is defeated by the fact that some rectangles are too small to get a point on a line
 *	within them, so we'll scale the whole grid up by a factor of 2. Now our solution suffers from
 *	anything existing at 0 on either axis. But we could deal with this by scaling on ((x,y)+1)*2
 *	So we will, while I believe we're marked with the given set, there doesn't seem to be a reason
 *	not to improve the code to deal with other potential problems.
 */
public class Main {
	
	private static ArrayList<Rectangle> rectangles = new ArrayList<>();
	private static ArrayList<Vertex[]> problems = new ArrayList<>();
	
	public static void main(String args[]) {
		// xml tutorial: https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
		try {
			File xmlFile = new File("puzzle-rm631.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
			
			/****************
				Rectangles
			*****************/
            Node rectangleNodes = doc.getElementsByTagName("rectangles").item(0);
            NodeList rectanglesList = rectangleNodes.getChildNodes();
			
			for (int i = 0; i < rectanglesList.getLength(); i++) {
				Node rectangle = rectanglesList.item(i);
				
				if(rectangle.getNodeType() == Node.ELEMENT_NODE) {
					Element rectangleElement = (Element) rectangle;
					NodeList vertices = rectangleElement.getChildNodes();
					Rectangle rect = new Rectangle();
					
					for(int j = 0; j < vertices.getLength(); j++){
						Node vertex = vertices.item(j);
						
						if(vertex.getNodeType() == Node.ELEMENT_NODE) {
							Element element = (Element) vertex;
							int x = (Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent())+1)*2;
							int y = (Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent())+1)*2;
							Vertex v = new Vertex(x,y);
							rect.addVertex(v);
						}
					}
					rectangles.add(rect);
				}
			}
			System.out.println("rectangles.size(); " + rectangles.size());
			
			/****************
				Puzzles
			*****************/
			
			Node problemNodes = doc.getElementsByTagName("puzzles").item(0);
			NodeList problemsList = problemNodes.getChildNodes();
			
			for (int i = 0; i < problemsList.getLength(); i++) {
				Node problemNode = problemsList.item(i);
				if(problemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element problemElement = (Element) problemNode;
					NodeList vertices = problemElement.getChildNodes();

					Vertex[] problemVertices = new Vertex[2];

					for(int j = 0; j < vertices.getLength(); j++) {
						Node vertex = vertices.item(j);
						
						if(vertex.getNodeType() == Node.ELEMENT_NODE) {
							Element element = (Element) vertex;
							
							if(element.getAttribute("id").equals("start")) {
								int x = (Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent())+1)*2;
								int y = (Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent())+1)*2;
								problemVertices[0] = new Vertex(x,y);
							} else if(element.getAttribute("id").equals("finish")) {
								int x = (Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent())+1)*2;
								int y = (Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent())+1)*2;
								problemVertices[1] = new Vertex(x,y);
							}
						}
					}
					problems.add(problemVertices);
				}
			}
			System.out.println("problems.size() " + problems.size());
			
			for(int i = 0; i < problems.size(); i++) {
				System.out.println("Solving problem: " + i);
				solve(i);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void solve(int problemNo) {
		Vertex[] problem = problems.get(problemNo);
		ArrayList<Vertex> route = new ArrayList<>();
		route = depthLimitedIterativeDeepning(problem[0], problem[1]);
		if(route == null) { System.out.println("route is null!"); } else {
			ArrayList<Vertex> routeFixed = new ArrayList<>();
			for(Vertex v:route) {
				routeFixed.add(new Vertex((v.get_x()-1)/2, (v.get_y()-1)/2));
			}
			solutionToFile(problemNo, routeFixed);
		}
	}

	private static ArrayList<Vertex> depthLimitedIterativeDeepning(Vertex start, Vertex finish) {
		for(int depth = 1; depth < 30; depth++) {
			System.out.println("We are " + depth + " deep!");
			ArrayList<Vertex> route = depthLimitedSearch(start, finish, depth);
			if(route != null) {
				return route;
			}
		}
		return null;
	}
	
	private static ArrayList<Vertex> depthLimitedSearch(Vertex config, Vertex finish, int depth) {
		if(depth == 0) {
			if(config.equals(finish)) {
				ArrayList<Vertex> route = new ArrayList<Vertex>();
				route.add(config);
				return route;
			} else {
				return null;
			}
		} else if(config.equals(finish)) {
			ArrayList<Vertex> route = new ArrayList<Vertex>();
			route.add(config);
			return route;
		} else {
			ArrayList<Vertex> nexts = nextConfigs(config, finish);
			for(Vertex next:nexts) {
				ArrayList<Vertex> route = depthLimitedSearch(next, finish, depth-1);
				if(route != null) {
					route.add(config);
					return route;
				}
			}
			return null;
		}
	}
	
	/**
	 *	For every point (ie. all the vertices of all the rectangles)
	 *	run vertexIntersect(Vertex u, Vertex v1, Vertex v2)
	 *	u is the Vertex v, v1 and v2 and two vertices that form a line
	 */
	private static ArrayList<Vertex> nextConfigs(Vertex v, Vertex finish) {
		ArrayList<Vertex> configs = new ArrayList<>();
		// If we can go to the finish point from v, lets do it!
		if(isValid(v, finish)) {
			configs.add(finish);
			return configs;
		}
		
		/**
		 *	The rectangle vertices are the only possible states
		 *	So lets check which points are valid and return them
		 */
		for(Rectangle r:rectangles) {
			Vertex[] vertices = r.getVertices();
			for(int i = 0; i < vertices.length; i++) {
				if(isValid(v, vertices[i])) {
					configs.add(vertices[i]);
				}
			}
		}
		
		return configs;
	}
	
	/**
	 *	Check if we can get from v1 to v2 without passing through a rect
	 */
	private static boolean isValid(Vertex v1, Vertex v2) {
		// Lets not go to the vertex where we already are...
		if(v1.equals(v2)) {
			return false;
		}
		for(Rectangle r:rectangles) {
			Vertex[] vertices = r.getVertices();
			/*
				First check if are line v1->v2 crosses through
				one of the lines of one the rectangles
			*/
			for(int i = 0; i < vertices.length; i++) {
				if(Vertex.linesIntersect(v1, v2, vertices[i], vertices[(i+1)%4])) {
					//System.out.println("v1: " + v1 + "v2: " + v2 + " | " + "rectangle[" + i + "]" + vertices[i] + " | " + vertices[(i+1)%4]);
					return false;
				}
			}
			/*
				Now we need to deal with some of the edge cases
				Those cases are where we cross a rectangle
				without entirely cutting a line, ie. we go from 
				one corner of a rect to another through the middle
				of the rect
			*/
			Vertex[] points = getPointsOnLine(v1,v2);
			for(int i = 0; i < points.length; i++) {
				if(checkInside(points[i], vertices)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 *	Since we're playing with AI and not with graphing, a pre-written algorithm
	 *	has been used here for getting the points on a graph between two points.
	 *	Code adapted from https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
	 */
	private static Vertex[] getPointsOnLine(Vertex v1, Vertex v2) {
		ArrayList<Vertex> results = new ArrayList<>();
		
		int x1, y1, x2, y2;
		x1 = v1.get_x();
		y1 = v1.get_y();
		x2 = v2.get_x();
		y2 = v2.get_y();
		
		int d = 0;
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2-y1);
		
		int dx2 = 2 * dx;
		int dy2 = 2 * dy;
		
		int ix = x1 < x2 ? 1 : -1;
        int iy = y1 < y2 ? 1 : -1;
		
		int x = x1;
		int y = y1;
		
		int i = 0;
		if (dx >= dy) {
            while (true) {
                if (x == x2)
                    break;
				if(i!=0) { Vertex v = new Vertex(x, y); results.add(v); }
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
				i++;
            }
        } else {
            while (true) {
                if (y == y2)
                    break;
				if(i!=0) { Vertex v = new Vertex(x, y); results.add(v); }
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
				i++;
            }
        }
		Vertex[] resultsArr = new Vertex[results.size()];
		for(int j = 0; j < results.size(); j++) {
			resultsArr[j] = results.get(j);
		}
		return resultsArr;
	}
	
	/** 
	 *	return true if we're inside a the given rectangle
	 */ 
	private static boolean checkInside(Vertex v, Vertex[] rectV) {
		if(v.equals(rectV[0]) || v.equals(rectV[1]) || v.equals(rectV[2]) || v.equals(rectV[3])) return false;
		for(int i = 0; i < rectV.length; i++) {
			if(Vertex.vertexIntersect(v, rectV[i], rectV[(i+1)%4])) return false;
		}
		
		/**
		 *	We can see at: https://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle
		 *	That we can check if we're inside a rectangle by saying we have point P
		 *	And rectangle ABCD and then checking if ABCD = SUM(APD, DPC, CPB, PBA)
		 *	So lets do that
		 */
		
		int x1, y1, x2, y2, x3, y3, x4, y4, x, y;
		// A
		x1 = rectV[0].get_x();
		y1 = rectV[0].get_y();
		// B
		x2 = rectV[3].get_x();
		y2 = rectV[3].get_y();
		// C
		x3 = rectV[2].get_x();
		y3 = rectV[2].get_y();
		// D
		x4 = rectV[1].get_x();
		y4 = rectV[1].get_y();
		// P
		x = v.get_x();
		y = v.get_y();
		
		float ABCD = (float)Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0)+(float)Math.abs((x1 * (y4 - y3) + x4 * (y3 - y1) + x3 * (y1 - y4)) / 2.0);
		float APD = (float)Math.abs((x1 * (y - y3) + x * (y3 - y1) + x4 * (y1 - y)) / 2.0);
		float DPC = (float)Math.abs((x4 * (y - y3) + x * (y3 - y4) + x3 * (y4 - y)) / 2.0);
		float CPB = (float)Math.abs((x3 * (y - y2) + x * (y2 - y3) + x2 * (y3 - y)) / 2.0);
		float PBA = (float)Math.abs((x * (y2 - y1) + x2 * (y1 - y) + x1 * (y - y2)) / 2.0);
		
		//System.out.println(ABCD + ", " + APD + ", " + DPC + ", " + CPB + ", " + PBA);
		return (ABCD == (APD + DPC + CPB + PBA));
	}
	
	/**
		Each solutionshould be written to a separate output file and placed in the submission directory detailed below (not asubdirectory). For example, for problem 0, you will need to generate0.txtwhich contains either:(8, 23) (11, 23) (10, 19) (6, 18) (13, 10) (12, 10)or any other sequence of line segments which connects coordinate(8,23)to(12,10).Note that a single space separates each coordinate and the coordinates are themselves formatted witha single space following the comma
	 */
	private static void solutionToFile(int solutionNo, ArrayList<Vertex> sol) {
		if(sol !=  null) {	
			System.out.println("Solution is being written to file!");
			System.out.println("sol size: " + sol.size());
			Collections.reverse(sol); // Reverse the list so that we don't go from end to start
			Writer writer = null;
			try {
				writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(solutionNo + ".txt"),
							"utf-8")
					);
				String result = "";
				for(Vertex v:sol) {
					result += v.toString() + " ";
				}
				writer.write(result);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(writer!=null) { writer.close(); }
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Couldn't close the writer!");
				}
			}
		} else {
			System.out.println("THE SOLUTION IS NULL, SOLUTION NOT BEING WRITTEN TO FILE");
		}
	}
	
}