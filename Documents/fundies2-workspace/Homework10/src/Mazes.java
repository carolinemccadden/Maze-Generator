import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import javalib.impworld.World;
import javalib.impworld.WorldScene;

import javalib.worldimages.LineImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import tester.Tester;

//krezchik
//Konstantin Rezchikov
//cmccadden
//Caroline Mccadden

// an interface to represent a stack or queue
interface ICollection<T> {
  // adds val to the collection
  void add(T val);

  // removes current node from the collection
  T remove();

  // checks if the collection is empty
  boolean isEmpty();
}

// a class to represent a stack
class Stack<T> implements ICollection<T> {
  Deque<T> contents;

  Stack() {
    this.contents = new Deque<T>();
  }

  // adds val to stack
  public void add(T val) {
    this.contents.addAtHead(val);
  }

  // removes current from stack
  public T remove() {
    return this.contents.removeFromHead();
  }

  // checks if stack is empty
  public boolean isEmpty() {
    return this.contents.empty();
  }
}

// a class to represent a queue
class Queue<T> implements ICollection<T> {
  Deque<T> contents;

  Queue() {
    this.contents = new Deque<T>();
  }

  // adds val to stack
  public void add(T val) {
    this.contents.addAtTail(val);
  }

  // removes current from stack
  public T remove() {
    return this.contents.removeFromHead();
  }

  // checks if stack is empty
  public boolean isEmpty() {
    return this.contents.empty();
  }
}

// a class to represent a deque
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // adds at head
  void addAtHead(T val) {
    this.header.addAtHead(val);
  }

  // adds at tail
  void addAtTail(T val) {
    this.header.addAtTail(val);
  }

  // removes from head
  T removeFromHead() {
    return this.header.removeFromHead();
  }

  // removes from tail
  T removeFromTail() {
    return this.header.removeFromTail();
  }

  // checks if empty
  boolean empty() {
    return this.header.empty();
  }
}

// a class to represent a sentinel
class Sentinel<T> extends ANode<T> {
  Sentinel() {
    super(null, null);
    this.next = this;
    this.prev = this;
  }

  // Adds a new pointer to the new node at the head
  void addAtHead(T val) {
    this.next = new Node<T>(val, this.next, this);
  }

  // Adds a new pointer to a new node at the tail
  void addAtTail(T val) {
    this.prev = new Node<T>(val, this, this.prev);
  }

  // Throws an exception if its empty. Else changes the pointers to the tail
  // and returns
  // the data of the node
  T removeFromHead() {
    if (this.empty()) {
      throw new RuntimeException("Cant remove from an empty list");
    }
    else {
      Node<T> node = (Node<T>) this.next;
      this.next.next.prev = this;
      this.next = this.next.next;
      return node.data;
    }
  }

  // Throws an exception if its empty. Else changes the pointers to the tail
  // and returns
  // the data of the node
  T removeFromTail() {
    if (this.empty()) {
      throw new RuntimeException("Cant remove from an empty list");
    }
    else {
      Node<T> node = (Node<T>) this.prev;
      this.prev = this.prev.prev;
      this.prev.prev.next = this;
      return node.data;
    }
  }

  // Checks if 2 ANodes are equal using double dispatch
  boolean equalANode(ANode<T> comp) {
    return comp.equalSentinel(this);
  }

  // Checks if the Sentinel is equal
  boolean equalSentinel(Sentinel<T> comp) {
    return true;
  }
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  abstract boolean equalANode(ANode<T> comp);

  // checks if the list is empty
  boolean empty() {
    return this.next.equalANode(new Sentinel<T>());
  }

  // Double dispatch
  boolean equalSentinel(Sentinel<T> comp) {
    return false;
  }
}

// a class to represent a node
class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    super(null, null);
    this.data = data;
  }

  Node(T data, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    this.data = data;
    if (next == null || prev == null) {
      throw new IllegalArgumentException("You used null");
    }
    next.prev = this;
    prev.next = this;
  }

  // removes this node
  T remove() {
    this.next.prev = this.prev;
    this.prev.next = this.next;
    return this.data;

  }

  // Checks if 2ANodes are equal
  boolean equalANode(ANode<T> comp) {
    return comp.equalANode(this);
  }

}

// a class to use compare on edges
class CompareEdges implements Comparator<Edge> {
  // checks if edge1 comes before edge2
  public int compare(Edge edge1, Edge edge2) {
    if (edge1.weight < edge2.weight) {
      return -1;
    }
    else if (edge1.weight == edge2.weight) {
      return 0;
    }
    else {
      return 1;
    }

  }
}

// a class to represent a vertex
class Vertex {
  Vertex top;
  Vertex bottom;
  Vertex right;
  Vertex left;
  Vertex prev;
  Posn pos;
  boolean isVisited;
  boolean isPath;

  Vertex(Posn pos) {
    this.pos = pos;
    this.isVisited = false;
    this.isPath = false;
    this.prev = this;
  }

  // draws the walls of this vertex
  void draw(WorldScene w) {
    if (pos.equals(new Posn(0, 0))) {
      w.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.GREEN), pos.x * 40 + 20,
          pos.y * 40 + 20);
    }
    else if (pos.x == (MazeWorld.mazeWidth / 4 - 1) && pos.y == (MazeWorld.mazeHeight / 4 - 1)) {
      w.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.RED), pos.x * 40 + 20,
          pos.y * 40 + 20);
    }
    else {
      if (isPath) {
        w.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, Color.BLUE), pos.x * 40 + 20,
            pos.y * 40 + 20);
      }
      else {
        if (!isVisited) {
          w.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, MazeWorld.background),
              pos.x * 40 + 20, pos.y * 40 + 20);
        }

        if (isVisited) {
          w.placeImageXY(new RectangleImage(40, 40, OutlineMode.SOLID, MazeWorld.visited),
              pos.x * 40 + 20, pos.y * 40 + 20);
        }
      }

    }

    if (top == null) {
      w.placeImageXY(new LineImage(new Posn(40, 0), MazeWorld.walls), pos.x * 40 + 20, pos.y * 40);
    }

    if (left == null) {
      w.placeImageXY(new LineImage(new Posn(0, 40), MazeWorld.walls), pos.x * 40, pos.y * 40 + 20);
    }

    if (bottom == null) {
      w.placeImageXY(new LineImage(new Posn(40, 0), MazeWorld.walls), pos.x * 40 + 20,
          pos.y * 40 + 40);
    }

    if (right == null) {
      w.placeImageXY(new LineImage(new Posn(0, 40), MazeWorld.walls), pos.x * 40 + 40,
          pos.y * 40 + 20);
    }
  }

  // adds neighbors to this vertex
  void addNeighbor(Vertex neighbor) {
    if (this.pos.x < neighbor.pos.x && this.pos.y == neighbor.pos.y) {
      right = neighbor;
    }
    else if (this.pos.x > neighbor.pos.x && this.pos.y == neighbor.pos.y) {
      left = neighbor;
    }
    else if (this.pos.x == neighbor.pos.x && this.pos.y < neighbor.pos.y) {
      bottom = neighbor;
    }
    else {
      top = neighbor;
    }
  }
}

// a class to represent an edge
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }
}

// a class to represent a graph
class Graph {
  ArrayList<ArrayList<Vertex>> allVertices;

  Graph(ArrayList<ArrayList<Vertex>> allVertices) {
    this.allVertices = allVertices;
  }

  // creates a graph with based on the height and width of the maze with null
  // wall values
  Graph initGraph() {
    for (int i = 0; i < MazeWorld.mazeWidth / 4; i++) {
      ArrayList<Vertex> temporary = new ArrayList<Vertex>();
      for (int j = 0; j < MazeWorld.mazeHeight / 4; j++) {
        temporary.add(new Vertex(new Posn(i, j)));
      }
      this.allVertices.add(temporary);
    }
    return this;
  }

  // creates a list of all the edges in this map
  ArrayList<Edge> findEdges(int x) {
    ArrayList<Edge> l = new ArrayList<Edge>();
    Random r = new Random();
    for (int i = 0; i < this.allVertices.size(); i++) {
      for (int j = 0; j < this.allVertices.get(0).size() - 1; j++) {
        if (x == 1) {
          l.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i).get(j + 1),
              r.nextInt(500) + 200));
        }
        else {
          l.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i).get(j + 1),
              r.nextInt(500)));
        }
      }
    }

    for (int i = 0; i < this.allVertices.size() - 1; i++) {
      for (int j = 0; j < this.allVertices.get(0).size(); j++) {
        if (x == 2) {
          l.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i + 1).get(j),
              r.nextInt(500) + 200));
        }
        else {
          l.add(new Edge(this.allVertices.get(i).get(j), this.allVertices.get(i + 1).get(j),
              r.nextInt(500)));
        }
      }
    }

    return l;

  }
}

// the world class
class MazeWorld extends World {
  // width of maze
  static final int mazeWidth = 100;
  // height of maze
  static final int mazeHeight = 60;
  // color of background
  static final Color background = Color.WHITE;
  // color of walls
  static final Color walls = Color.BLACK;
  // color of visited vertices
  static final Color visited = Color.CYAN;
  // list of all edges in tree
  ArrayList<Edge> edgesInTree;
  // list of edges that are in maze after kruskal's algorithm
  ArrayList<Edge> usableEdges1;
  // second list for animation purposes
  ArrayList<Edge> usableEdges2;
  // list of visited vertices
  ArrayList<Vertex> visitedList;
  // list of vertices in the correct path
  ArrayList<Vertex> pathList;
  // the current vertex
  Vertex currentVertex;
  // the maze itself
  Graph map;
  // ticks for animation
  int ticks;
  int ticks2;
  // number of wrong moves
  int wrong;

  MazeWorld() {
    this.initWorld();
  }

  // initializes data
  void initWorld() {
    map = new Graph(new ArrayList<ArrayList<Vertex>>());
    visitedList = new ArrayList<Vertex>();
    pathList = new ArrayList<Vertex>();
    map = map.initGraph();
    edgesInTree = map.findEdges(0);
    usableEdges1 = new ArrayList<Edge>();
    usableEdges2 = new ArrayList<Edge>();
    currentVertex = map.allVertices.get(0).get(0);
    visitedList.add(currentVertex);
    this.kruskal();
    ticks = 0;
    ticks2 = 0;
    wrong = 0;

    this.displayEdges();

  }

  // initializes data for vertical bias
  void initVWorld() {
    map = new Graph(new ArrayList<ArrayList<Vertex>>());
    visitedList = new ArrayList<Vertex>();
    pathList = new ArrayList<Vertex>();
    map = map.initGraph();
    edgesInTree = map.findEdges(2);
    usableEdges1 = new ArrayList<Edge>();
    usableEdges2 = new ArrayList<Edge>();
    currentVertex = map.allVertices.get(0).get(0);
    visitedList.add(currentVertex);
    this.kruskal();
    ticks = 0;
    ticks2 = 0;
    wrong = 0;

    this.displayEdges();

  }

  // initializes data for horizontal bias
  void initHWorld() {
    map = new Graph(new ArrayList<ArrayList<Vertex>>());
    visitedList = new ArrayList<Vertex>();
    pathList = new ArrayList<Vertex>();
    map = map.initGraph();
    edgesInTree = map.findEdges(1);
    usableEdges1 = new ArrayList<Edge>();
    usableEdges2 = new ArrayList<Edge>();
    currentVertex = map.allVertices.get(0).get(0);
    visitedList.add(currentVertex);
    this.kruskal();
    ticks = 0;
    ticks2 = 0;
    wrong = 0;

    this.displayEdges();

  }

  // adds edges to vertices
  void displayEdges() {
    for (Edge e : usableEdges1) {
      e.from.addNeighbor(e.to);
      e.to.addNeighbor(e.from);
    }
  }

  // calls methods for based on key pressed
  public void onKeyEvent(String key) {
    if (key.equals("n")) {
      this.initWorld();
    }

    if (key.equals("right")) {
      this.rightKey();
    }

    if (key.equals("left")) {
      this.leftKey();
    }

    if (key.equals("up")) {
      this.upKey();
    }

    if (key.equals("down")) {
      this.downKey();
    }

    if (key.equals("b")) {
      this.bfs(currentVertex,
          map.allVertices.get(MazeWorld.mazeWidth / 4 - 1).get(MazeWorld.mazeHeight / 4 - 1));
      wrong = visitedList.size() - pathList.size();
    }

    if (key.equals("d")) {
      this.dfs(currentVertex,
          map.allVertices.get(MazeWorld.mazeWidth / 4 - 1).get(MazeWorld.mazeHeight / 4 - 1));
      wrong = visitedList.size() - pathList.size();
    }
    if (key.equals("v")) {
      this.initVWorld();
    }
    if (key.equals("h")) {
      this.initHWorld();
    }

  }

  // breadth first search
  boolean bfs(Vertex from, Vertex to) {
    return searchHelp(from, to, new Queue<Vertex>());
  }

  // depth first search
  boolean dfs(Vertex from, Vertex to) {
    return searchHelp(from, to, new Stack<Vertex>());
  }

  // helps the search methods
  boolean searchHelp(Vertex from, Vertex to, ICollection<Vertex> worklist) {

    // Initialize the worklist with the from vertex
    worklist.add(from);
    // As long as the worklist isn't empty...
    while (!worklist.isEmpty()) {
      Vertex next = worklist.remove();
      visitedList.add(next);
      currentVertex = next;

      if (next.equals(to)) {

        return true; // Success!
      }
      else {
        if (next.left != null && !visitedList.contains(next.left)) {
          worklist.add(next.left);
          next.left.prev = next;
        }
        if (next.right != null && !visitedList.contains(next.right)) {
          worklist.add(next.right);
          next.right.prev = next;
        }
        if (next.top != null && !visitedList.contains(next.top)) {
          worklist.add(next.top);
          next.top.prev = next;
        }
        if (next.bottom != null && !visitedList.contains(next.bottom)) {
          worklist.add(next.bottom);
          next.bottom.prev = next;
        }
      }
    }
    // We haven't found the to vertex, and there are no more to try
    return false;
  }

  // displays correct path
  void displayPath() {
    Vertex current = map.allVertices.get(MazeWorld.mazeWidth / 4 - 1)
        .get(MazeWorld.mazeHeight / 4 - 1);
    while (current.prev != current) {
      // current.isPath = true;
      pathList.add(current);
      current = current.prev;
    }

  }

  // moves currentVertex to the right and appropriately colors vertices
  void rightKey() {
    if (currentVertex.right != null) {
      if (visitedList.contains(currentVertex.right)) {
        visitedList.remove(currentVertex);
        currentVertex.isVisited = false;
        currentVertex = currentVertex.right;
        wrong = wrong + 1;
        this.makeScene();
      }
      else {
        currentVertex = currentVertex.right;
        visitedList.add(currentVertex);
        currentVertex.isVisited = true;
        this.makeScene();
      }
    }
  }

  // moves currentVertex to the left and appropriately colors vertices
  void leftKey() {
    if (currentVertex.left != null) {
      if (visitedList.contains(currentVertex.left)) {
        visitedList.remove(currentVertex);
        currentVertex.isVisited = false;
        currentVertex = currentVertex.left;
        wrong = wrong + 1;
        this.makeScene();
      }
      else {
        currentVertex = currentVertex.left;
        visitedList.add(currentVertex);
        currentVertex.isVisited = true;
        this.makeScene();
      }
    }
  }

  // moves currentVertex up and appropriately colors vertices
  void upKey() {
    if (currentVertex.top != null) {
      if (visitedList.contains(currentVertex.top)) {
        visitedList.remove(currentVertex);
        currentVertex.isVisited = false;
        currentVertex = currentVertex.top;
        wrong = wrong + 1;
        this.makeScene();
      }
      else {
        currentVertex = currentVertex.top;
        visitedList.add(currentVertex);
        currentVertex.isVisited = true;
        this.makeScene();
      }
    }
  }

  // moves currentVertex down and appropriately colors vertices
  void downKey() {
    if (currentVertex.bottom != null) {
      if (visitedList.contains(currentVertex.bottom)) {
        visitedList.remove(currentVertex);
        currentVertex.isVisited = false;
        currentVertex = currentVertex.bottom;
        wrong = wrong + 1;
        this.makeScene();
      }
      else {
        currentVertex = currentVertex.bottom;
        visitedList.add(currentVertex);
        currentVertex.isVisited = true;
        this.makeScene();
      }
    }
  }

  // applies Kruskal's algorithm
  void kruskal() {
    HashMap<Vertex, Vertex> representatives = new HashMap<Vertex, Vertex>();
    edgesInTree.sort(new CompareEdges());
    initHashMap(representatives);
    while (usableEdges2.size() < ((map.allVertices.size() * map.allVertices.get(0).size()) - 1)) {
      if (find(representatives, edgesInTree.get(0).from) == find(representatives,
          edgesInTree.get(0).to)) {
        edgesInTree.remove(0);
      }
      else {
        union(representatives, edgesInTree.get(0).from, edgesInTree.get(0).to);
        usableEdges2.add(edgesInTree.get(0));
        edgesInTree.remove(0);
      }
    }
  }

  // initializes the hashmap
  void initHashMap(HashMap<Vertex, Vertex> hmap) {
    for (ArrayList<Vertex> a : map.allVertices) {
      for (Vertex v : a) {
        hmap.put(v, v);
      }
    }
  }

  // changes p1's representative to p2
  void union(HashMap<Vertex, Vertex> hmap, Vertex v1, Vertex v2) {
    hmap.replace(find(hmap, v1), v2);
  }

  // finds the representative of v
  Vertex find(HashMap<Vertex, Vertex> map, Vertex v) {
    if (map.get(v) == v) {
      return v;
    }
    else {
      return find(map, map.get(v));
    }
  }

  // animates map
  public void onTick() {
    if (ticks < visitedList.size()) {
      visitedList.get(ticks).isVisited = true;
      ticks = ticks + 1;
    }
    else {
      this.displayPath();
      if (ticks2 < pathList.size()) {
        pathList.get(ticks2).isPath = true;
        ticks2 = ticks2 + 1;
      }

    }

    if (usableEdges2.size() > 0) {
      usableEdges1.add(usableEdges2.get(0));
      usableEdges2.remove(0);
      this.displayEdges();
    }

  }

  // renders
  public WorldScene makeScene() {

    WorldScene empty = this.getEmptyScene();
    for (ArrayList<Vertex> a : map.allVertices) {
      for (Vertex v : a) {
        v.draw(empty);
        if (map.allVertices.get(MazeWorld.mazeWidth / 4 - 1)
            .get(MazeWorld.mazeHeight / 4 - 1).isVisited) {
          empty.placeImageXY(
              new TextImage("You won! Wrong moves: " + wrong + ".", 50, Color.MAGENTA),
              ((MazeWorld.mazeWidth * 10) / 2 - 1), ((MazeWorld.mazeHeight * 10) / 2 - 1));
        }

        if (v.equals(currentVertex)) {
          empty.placeImageXY(new RectangleImage(20, 20, OutlineMode.SOLID, Color.BLUE),
              v.pos.x * 40 + 20, v.pos.y * 40 + 20);

        }
      }
    }

    return empty;
  }

}

class MazeExamples {
  Vertex v1 = new Vertex(new Posn(0, 0));
  Vertex v2 = new Vertex(new Posn(5, 10));
  Vertex v3 = new Vertex(new Posn(20, 25));
  Vertex v4 = new Vertex(new Posn(0, 20));
  ArrayList<Vertex> lov1 = new ArrayList<Vertex>();
  ArrayList<Vertex> lov2 = new ArrayList<Vertex>();

  Edge e1 = new Edge(v1, v2, 100);
  Edge e2 = new Edge(v3, v4, 50);
  ArrayList<Edge> loe1 = new ArrayList<Edge>();
  MazeWorld m = new MazeWorld();
  MazeWorld world = new MazeWorld();
  HashMap<Vertex, Vertex> map = new HashMap<Vertex, Vertex>();
  Stack<String> stack = new Stack<String>();
  Deque<String> deque = new Deque<String>();
  Queue<String> queue = new Queue<String>();
  CompareEdges compareedges = new CompareEdges();
  ArrayList<ArrayList<Vertex>> doublearray = new ArrayList<ArrayList<Vertex>>();

  void testStack(Tester t) {
    t.checkExpect(stack.isEmpty(), true);
    stack.add("hello");
    t.checkExpect(stack.isEmpty(), false);
    t.checkExpect(stack.remove(), "hello");
    t.checkExpect(stack.isEmpty(), true);
  }

  void testDeque(Tester t) {
    t.checkExpect(deque.empty(), true);
    deque.addAtHead("hello");
    deque.addAtTail("good bye");
    t.checkExpect(deque.empty(), false);
    t.checkExpect(deque.removeFromHead(), "hello");
    t.checkExpect(deque.removeFromTail(), "good bye");
    t.checkExpect(deque.empty(), true);

  }

  void testQueue(Tester t) {
    t.checkExpect(queue.isEmpty(), true);
    queue.add("hello");
    queue.add("good bye");
    t.checkExpect(queue.isEmpty(), false);
    t.checkExpect(queue.remove(), "hello");
    t.checkExpect(queue.isEmpty(), false);
    t.checkExpect(queue.remove(), "good bye");
    t.checkExpect(queue.isEmpty(), true);

  }

  void testCompareEdges(Tester t) {
    t.checkExpect(this.compareedges.compare(e1, e2), 1);
    t.checkExpect(this.compareedges.compare(e2, e1), -1);
    t.checkExpect(this.compareedges.compare(e2, e2), 0);
  }

  void testKruskal(Tester t) {
    // testing find
    map.put(v1, v1);
    map.put(v2, v3);
    map.put(v3, v3);
    t.checkExpect(m.find(map, v1), v1);
    t.checkExpect(m.find(map, v2), v3);

    // testing union
    m.union(map, v1, v3);

    t.checkExpect(m.find(map, v1), v3);
    t.checkExpect(m.find(map, v2), v3);

    lov1.add(v1);
    lov1.add(v2);
    lov2.add(v3);
    lov2.add(v4);
    doublearray.add(lov1);
    doublearray.add(lov2);
    t.checkExpect(doublearray.size(), 2);
    t.checkExpect(doublearray.get(0).size(), 2);
    m.edgesInTree = new ArrayList<Edge>();
    Graph graph = new Graph(doublearray);
    m.map = graph;
    ArrayList<Edge> sortedEdges = new ArrayList<Edge>();
    sortedEdges.add(new Edge(v1, v2, 0));
    sortedEdges.add(new Edge(v3, v4, 1));
    sortedEdges.add(new Edge(v1, v3, 2));
    sortedEdges.add(new Edge(v2, v4, 2));
    int k = 0;

    // testing logic of findEdges
    for (int i = 0; i < m.map.allVertices.size(); i++) {
      for (int j = 0; j < m.map.allVertices.get(0).size() - 1; j++) {
        m.edgesInTree
            .add(new Edge(m.map.allVertices.get(i).get(j), m.map.allVertices.get(i).get(j + 1), k));
      }
      k++;
    }

    for (int i = 0; i < m.map.allVertices.size() - 1; i++) {
      for (int j = 0; j < m.map.allVertices.get(0).size(); j++) {
        m.edgesInTree
            .add(new Edge(m.map.allVertices.get(i).get(j), m.map.allVertices.get(i + 1).get(j), k));
      }
      k++;
    }

    t.checkExpect(m.edgesInTree.size(), 4);

    // testing sort
    m.edgesInTree.sort(new CompareEdges());
    t.checkExpect(m.edgesInTree, sortedEdges);

  }

  // testing movement
  void testMove(Tester t) {
    MazeWorld m1 = new MazeWorld();
    MazeWorld m2 = new MazeWorld();
    t.checkExpect(m1.currentVertex.pos.equals(m2.currentVertex.pos), true);
    // shouldn't be able to move left from the start
    m1.onKeyEvent("left");
    t.checkExpect(m1.currentVertex.pos.equals(m2.currentVertex.pos), true);

  }

  void testMaze(Tester t) {
    world.bigBang(MazeWorld.mazeWidth * 10, MazeWorld.mazeHeight * 10, 0.0000001);
  }

}
