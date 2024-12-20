import javax.swing.*;
import java.awt.*;

// Frame class for the GUI
class MazeFrame extends JFrame {
    private MazePanel mazePanel;
    private JButton solveButton;
    private JComboBox<String> algorithmChoice;
    private JButton resetAllButton;
    private JButton resetPathButton;
    private JButton randomMaze;
    private JSlider speedSlider;
    private boolean pathOnly;

    public MazeFrame() {
        setTitle("Maze Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mazePanel = new MazePanel();
        add(mazePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        algorithmChoice = new JComboBox<>(new String[]{"BFS", "DFS"});
        solveButton = new JButton("Solve");
        resetAllButton = new JButton("Reset All");
        resetPathButton = new JButton("Reset Path");
        randomMaze = new JButton("Random Maze");

        // Add speed slider
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 200, 100);
        speedSlider.setInverted(false);
        speedSlider.setPreferredSize(new Dimension(100, 40));
        JLabel speedLabel = new JLabel("Speed: ");

        controlPanel.add(new JLabel("Algorithm: "));
        controlPanel.add(algorithmChoice);
        controlPanel.add(solveButton);
        controlPanel.add(resetAllButton);
        controlPanel.add(resetPathButton);
        controlPanel.add(randomMaze);
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);

        add(controlPanel, BorderLayout.SOUTH);

        solveButton.addActionListener(e -> {
            solveButton.setEnabled(false);
            resetPathButton.setEnabled(false);
            resetAllButton.setEnabled(false);
            randomMaze.setEnabled(false);
            new Thread(() -> {
                solveMaze();
                solveButton.setEnabled(true);
                resetAllButton.setEnabled(true);
                resetPathButton.setEnabled(true);
                randomMaze.setEnabled(true);
            }).start();
        });
        resetAllButton.addActionListener(e -> {
            pathOnly = false;
            mazePanel.resetMaze(pathOnly);
        });
        resetPathButton.addActionListener(e -> {
            pathOnly = true;
            mazePanel.resetMaze(pathOnly);
        });
        randomMaze.addActionListener(e -> {
            mazePanel.generateRandomMaze();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void solveMaze() {
        String algorithm = (String) algorithmChoice.getSelectedItem();
        int offset = 201;
        if (algorithm.equals("BFS")) {
            mazePanel.solveMazeBFS(offset - speedSlider.getValue());
        } else {
            mazePanel.solveMazeDFS(offset - speedSlider.getValue());
        }
    }
}