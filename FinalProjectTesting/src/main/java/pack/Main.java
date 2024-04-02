package pack;
import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

class GameState {
    Point player1Position = new Point(0, 0); // Starting position for player 1
    Point player2Position = new Point(9, 9); // Starting position for player 2
    Set<Point> treasures = new HashSet<>();

    public GameState() {
        // Initialize treasures at predefined locations for demonstration
        treasures.add(new Point(2, 3));
        treasures.add(new Point(5, 5));
        treasures.add(new Point(7, 8));
    }

    public boolean isTreasure(int x, int y) {
        return treasures.contains(new Point(x, y));
    }

    public void movePlayer(int playerNum, int x, int y) {
        Point playerPosition = playerNum == 1 ? player1Position : player2Position;
        // Ensure new position is within grid boundaries
        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            playerPosition.setLocation(x, y);
        }
        treasures.remove(new Point(x, y)); // Assume treasures can be on edge and collected
    }

    // Add this in GameState after moving a player
    if (player1Position.equals(player2Position)) {
        // Example reaction: Move Player 2 back to start position if they collide
        if (playerNum == 1)



            class TravelingSalesmanGame extends JFrame {
        private final int WINDOW_WIDTH = 800; // Window width
        private final int WINDOW_HEIGHT = 800; // Window height
        private final int GRID_SIZE = 10; // Grid size for the map, 10x10
        private final int CELL_SIZE = 80; // Each cell size


        public TravelingSalesmanGame() {
            setTitle("Traveling Salesman Game");
            setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            setLocationRelativeTo(null); // Center the window

            add(new GamePanel());
        }

        // Inside the TravelingSalesmanGame class

        private class GamePanel extends JPanel {
            GameState gameState = new GameState();

            // Inside the GamePanel class
            public GamePanel() {
                setFocusable(true); // Important to make the panel focusable to receive key events
                requestFocusInWindow();
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        handleKeyPress(e);
                    }
                });
            }

            private void handleKeyPress(KeyEvent e) {
                int keyCode = e.getKeyCode();
                // Player 1 controls
                if (keyCode == KeyEvent.VK_W)
                    gameState.movePlayer(1, gameState.player1Position.x, gameState.player1Position.y - 1);
                else if (keyCode == KeyEvent.VK_S)
                    gameState.movePlayer(1, gameState.player1Position.x, gameState.player1Position.y + 1);
                else if (keyCode == KeyEvent.VK_A)
                    gameState.movePlayer(1, gameState.player1Position.x - 1, gameState.player1Position.y);
                else if (keyCode == KeyEvent.VK_D)
                    gameState.movePlayer(1, gameState.player1Position.x + 1, gameState.player1Position.y);

                // Player 2 controls
                if (keyCode == KeyEvent.VK_UP)
                    gameState.movePlayer(2, gameState.player2Position.x, gameState.player2Position.y - 1);
                else if (keyCode == KeyEvent.VK_DOWN)
                    gameState.movePlayer(2, gameState.player2Position.x, gameState.player2Position.y + 1);
                else if (keyCode == KeyEvent.VK_LEFT)
                    gameState.movePlayer(2, gameState.player2Position.x - 1, gameState.player2Position.y);
                else if (keyCode == KeyEvent.VK_RIGHT)
                    gameState.movePlayer(2, gameState.player2Position.x + 1, gameState.player2Position.y);

                repaint(); // Redraw the panel with updated positions
            }


            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
                drawPlayer(g, gameState.player1Position, Color.RED);
                drawPlayer(g, gameState.player2Position, Color.BLUE);
            }

            private void drawPlayer(Graphics g, Point position, Color color) {
                g.setColor(color);
                int x = position.x * CELL_SIZE;
                int y = position.y * CELL_SIZE;
                g.fillOval(x + 20, y + 20, CELL_SIZE - 40, CELL_SIZE - 40);
                g.setColor(Color.BLACK); // Reset color for grid drawing
            }


            private void drawGrid(Graphics g) {
                // Same grid drawing code as before, with added checks for treasures
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        int x = i * CELL_SIZE;
                        int y = j * CELL_SIZE;
                        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                        if (gameState.isTreasure(i, j)) {
                            g.setColor(Color.GREEN);
                            g.fillRect(x + 1, y + 1, CELL_SIZE - 1, CELL_SIZE - 1);
                            g.setColor(Color.BLACK);
                        }
                    }
                }
            }


            public static void main(String[] args) {
                SwingUtilities.invokeLater(() -> {
                    TravelingSalesmanGame game = new TravelingSalesmanGame();
                    game.setVisible(true);
                });
            }
        }
    }
}
