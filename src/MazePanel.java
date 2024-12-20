import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

// Panel class for maze display
class MazePanel extends JPanel {
    private static final int CELL_SIZE = 20;
    private static final int ROWS = 30;
    private static final int COLS = 50;

    private Cell[][] maze;
    private Point start;
    private Point end;
    private Random random;

    public MazePanel() {
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        initializeMaze();
        random = new Random();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / CELL_SIZE;
                int row = e.getY() / CELL_SIZE;
                if (isValidPosition(row, col)) {
                    Cell cell = maze[row][col];
                    if (!cell.isStart() && !cell.isEnd()) {
                        cell.toggleWall();
                        repaint();
                    }
                }
            }
        });
    }

    private void initializeMaze() {
        maze = new Cell[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                maze[i][j] = new Cell(i, j);
            }
        }

        start = new Point(1, 1);
        end = new Point(ROWS - 2, COLS - 2);
        maze[start.x][start.y].setStart(true);
        maze[end.x][end.y].setEnd(true);
    }

    public void resetMaze(boolean pathOnly) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (pathOnly) maze[i][j].resetPath();
                else maze[i][j].resetAll();
            }
        }
        maze[start.x][start.y].setStart(true);
        maze[end.x][end.y].setEnd(true);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                maze[i][j].draw(g, j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void solveMazeBFS(int delay) {
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>();
        boolean[][] visited = new boolean[ROWS][COLS];

        queue.offer(start);
        visited[start.x][start.y] = true;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                reconstructPath(parentMap);
                return;
            }

            for (int[] dir : directions) {
                int newRow = current.x + dir[0];
                int newCol = current.y + dir[1];
                Point next = new Point(newRow, newCol);

                if (isValidPosition(newRow, newCol) && !visited[newRow][newCol] && !maze[newRow][newCol].isWall()) {
                    queue.offer(next);
                    visited[newRow][newCol] = true;
                    parentMap.put(next, current);
                    if (!maze[newRow][newCol].isEnd()) {
                        maze[newRow][newCol].setVisited(true);
                        repaint();
                        sleep(delay);
                    }
                }
            }
        }
    }

    public void solveMazeDFS(int delay) {
        Stack<Point> stack = new Stack<>();
        Map<Point, Point> parentMap = new HashMap<>();
        boolean[][] visited = new boolean[ROWS][COLS];

        stack.push(start);
        visited[start.x][start.y] = true;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!stack.isEmpty()) {
            Point current = stack.pop();

            if (current.equals(end)) {
                reconstructPath(parentMap);
                return;
            }

            for (int[] dir : directions) {
                int newRow = current.x + dir[0];
                int newCol = current.y + dir[1];
                Point next = new Point(newRow, newCol);

                if (isValidPosition(newRow, newCol) && !visited[newRow][newCol] && !maze[newRow][newCol].isWall()) {
                    stack.push(next);
                    visited[newRow][newCol] = true;
                    parentMap.put(next, current);
                    if (!maze[newRow][newCol].isEnd()) {
                        maze[newRow][newCol].setVisited(true);
                        repaint();
                        sleep(delay);
                    }
                }
            }
        }
    }

    private void reconstructPath(Map<Point, Point> parentMap) {
        Point current = end;
        while (parentMap.containsKey(current)) {
            Point parent = parentMap.get(current);
            if (!parent.equals(start)) {
                maze[parent.x][parent.y].setPath(true);
                repaint();
                sleep(50); // Slower animation for path reconstruction
            }
            current = parent;
        }
    }

    public void generateRandomMaze() {
        resetMaze(false);

        // Initialize all cells as walls
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                maze[i][j].setWall(true);
            }
        }

        // Recursive backtracking to generate maze
        Stack<Point> stack = new Stack<>();
        boolean[][] visited = new boolean[ROWS][COLS];

        // Start from the start point
        Point current = new Point(1, 1);
        visited[current.x][current.y] = true;
        maze[current.x][current.y].setWall(false);
        stack.push(current);

        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}}; // Down, Right, Up, Left

        while (!stack.isEmpty()) {
            current = stack.peek();

            // Get unvisited neighbors
            List<Integer> unvisitedDirs = new ArrayList<>();
            for (int i = 0; i < directions.length; i++) {
                int newRow = current.x + directions[i][0];
                int newCol = current.y + directions[i][1];

                if (isValidPosition(newRow, newCol) && !visited[newRow][newCol]) {
                    unvisitedDirs.add(i);
                }
            }

            if (!unvisitedDirs.isEmpty()) {
                // Choose random direction
                int dirIndex = unvisitedDirs.get(random.nextInt(unvisitedDirs.size()));
                int[] dir = directions[dirIndex];

                // Create path to next cell
                int newRow = current.x + dir[0];
                int newCol = current.y + dir[1];

                // Carve passage
                maze[current.x + dir[0]/2][current.y + dir[1]/2].setWall(false);
                maze[newRow][newCol].setWall(false);

                visited[newRow][newCol] = true;
                stack.push(new Point(newRow, newCol));
            } else {
                stack.pop();
            }
        }

        // Set start and end points
        maze[start.x][start.y].setWall(false);
        maze[start.x][start.y].setStart(true);
        maze[end.x][end.y].setWall(false);
        maze[end.x][end.y].setEnd(true);

        // Ensure there's a path near start and end
        maze[start.x][start.y + 1].setWall(false);
        maze[end.x][end.y - 1].setWall(false);

        repaint();
    }
}