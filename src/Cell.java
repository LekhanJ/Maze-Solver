import java.awt.*;

// Cell class representing each maze cell
class Cell {
    private int row, col;
    private boolean isWall;
    private boolean isStart;
    private boolean isEnd;
    private boolean isPath;
    private boolean isVisited;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void toggleWall() {
        isWall = !isWall;
    }

    public void setWall(boolean wall) {
        isWall = wall;
    }

    public boolean isWall() {
        return isWall;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public void setPath(boolean path) {
        isPath = path;
        isVisited = false; // Clear visited status when setting final path
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public void resetPath() {
        isPath = false;
        isStart = false;
        isEnd = false;
        isVisited = false;
    }

    public void resetAll() {
        isWall = false;
        isPath = false;
        isStart = false;
        isEnd = false;
        isVisited = false;
    }

    public void draw(Graphics g, int x, int y, int size) {
        if (isStart) {
            g.setColor(Color.CYAN);
        } else if (isEnd) {
            g.setColor(Color.RED);
        } else if (isWall) {
            g.setColor(Color.BLACK);
        } else if (isPath) {
            g.setColor(Color.BLUE);
        } else if (isVisited) {
            g.setColor(new Color(144, 238, 144)); // Light green for visited cells
        } else {
            g.setColor(Color.WHITE);
        }

        g.fillRect(x, y, size, size);
        g.setColor(Color.GRAY);
        g.drawRect(x, y, size, size);
    }
}