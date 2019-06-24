import java.util.*;
import java.io.*;
import java.awt.Color;  
import java.awt.Graphics;
import javax.swing.JFrame;

public class JourneyPlanner {
    public static void main(String args[]) {
		String start = "rye"; 		/* default start state */
		String finish = "tent";		/* default finish state */

		if (args.length >= 1) {
			start = args[0];
		}
		if (args.length >= 2) {
			finish = args[1];
		}
			
		JourneyPlanner planner = new JourneyPlanner();
		LinkedList<String> route = planner.iterativeDeepening(start, finish);	
		System.out.println("route = " + route);
    }
	
    public List<String> nextConfigs(String state) {  
		switch (state) {
			case "ash":
			return Arrays.asList("chart", "fav", "folk", "harr", "hy", "nr", "rye", "tent");
			case "bar":
			return Arrays.asList("cant", "dov", "folk");
			case "cant":
			return Arrays.asList("bar", "chart", "fav", "sand", "st", "whit");
			case "chart":
			return Arrays.asList("ash", "cant", "harr");
			case "cran":
			return Arrays.asList("hast", "maid");
			case "deal":
			return Arrays.asList("dov", "sand");
			case "dov":
			return Arrays.asList("bar", "deal", "folk", "sand");
			case "dung":
			return Arrays.asList();
			case "fav":
			return Arrays.asList("ash", "cant", "whit");
			case "folk":
			return Arrays.asList("ash", "bar", "dov", "hy");
			case "gill":
			return Arrays.asList("graves", "sit");
			case "graves":
			return Arrays.asList("gill", "maid");
			case "harr":
			return Arrays.asList("ash", "chart", "maid");
			case "hast":
			return Arrays.asList("cran", "rye", "tent");
			case "hb":
			return Arrays.asList("mar", "st", "whit");
			case "hy":
			return Arrays.asList("ash", "folk", "nr");
			case "maid":
			return Arrays.asList("cran", "graves", "harr", "sit", "tent");
			case "mar":
			return Arrays.asList("hb", "rams", "st");
			case "nr":
			return Arrays.asList("ash", "hy", "rye");
			case "rams":
			return Arrays.asList("mar", "sand", "st");
			case "rye":
			return Arrays.asList("ash", "hast", "nr", "tent");
			case "sand":
			return Arrays.asList("cant", "deal", "dov", "rams");
			case "sheer":
			return Arrays.asList("sit");
			case "sit":
			return Arrays.asList("gill", "maid", "sheer");
			case "st":
			return Arrays.asList("cant", "hb", "mar", "rams");
			case "tent":
			return Arrays.asList("ash", "hast", "maid", "rye");
			case "whit":
			return Arrays.asList("cant", "fav", "hb");
			default:
			return null;
		}
    }
    
    public LinkedList<String> iterativeDeepening(String first, String last) {
        for (int depth = 1; true; depth++) {
             LinkedList<String> route = depthFirst(first, last, depth);
             if (route != null) return route;
        }
    }

    private LinkedList<String> depthFirst(String first, String last, int depth)
    {   
		if (depth == 0) {
			return null;
		}
		else if (first.equals(last)) {    
			LinkedList<String> route = new LinkedList<String>();
			route.add(first);
			return route;
		} else {   
			List<String> nexts = nextConfigs(first);
			for (String next:nexts) {   
				LinkedList<String> route = depthFirst(next, last, depth - 1);
				if (route != null) {
					route.addFirst(first);
					return route;
				}
			}
			return null;
		}
	}
}
