class Rectangle {
	private Vertex[] vertices; // Vertex is a supplied class with the ass
	private int numPoints;
	
	public Rectangle(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		vertices = new Vertex[4];
		vertices[0] = new Vertex(x1, y1);
		vertices[1] = new Vertex(x2, y2);
		vertices[2] = new Vertex(x3, y3);
		vertices[3] = new Vertex(x4, y4);
		numPoints = 4;
	}
	
	public Rectangle() {
		vertices = new Vertex[4];
		numPoints = 0;
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}
	
	public void addVertex(Vertex v) {
		vertices[numPoints] = v;
		numPoints++;
	}
}