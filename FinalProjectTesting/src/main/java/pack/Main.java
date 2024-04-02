package pack;
import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class GameState {
    Point player1Position = new Point(0, 0);
    Point player2Position = new Point(9, 9);
    Set<Point> treasures = new HashSet<>();
    private int currentPlayerTurn = 1;

    public GameState() {
        treasures.add(new Point(2, 3));
        treasures.add(new Point(5, 5));
        treasures.add(new Point(7, 8));
    }

    public boolean isTreasure(int x, int y) {
        return treasures.contains(new Point(x, y));
    }

    public void movePlayer(int x, int y) {
        Point playerPosition = currentPlayerTurn == 1 ? player1Position : player2Position;
        Point newPosition = new Point(x, y);

        // Check grid boundaries
        if (x < 0 || x >= 10 || y < 0 || y >= 10) return;

        // Prevent players from moving onto each other's position
        if (newPosition.equals(player1Position) || newPosition.equals(player2Position)) return;

        playerPosition.setLocation(x, y);

        // Switch turns after a move
        currentPlayerTurn = currentPlayerTurn == 1 ? 2 : 1;
        treasures.remove(newPosition); // Collect treasure if present
    }

    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }
}

class TravelingSalesmanGame extends JFrame {
    private final int WINDOW_WIDTH = 800; // Window width
    private final int WINDOW_HEIGHT = 800; // Window height
    private final int GRID_SIZE = 10; // Grid size for the map, 10x10
    private final int CELL_SIZE = 80; // Each cell size
    private JLabel statusLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);


    public TravelingSalesmanGame() {
        GamePanel gamePanel = new GamePanel(statusLabel);
        setTitle("Traveling Salesman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 850); // Adjusted for the status label
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.SOUTH);

        gamePanel.requestFocusInWindow(); // Ensure the GamePanel has focus to receive key events
    }


    // Inside the TravelingSalesmanGame class

    public class GamePanel extends JPanel {
        private GameState gameState = new GameState();
        private JLabel statusLabel;

        public GamePanel(JLabel statusLabel) {
            this.statusLabel = statusLabel;
            setFocusable(true);
            requestFocusInWindow();
            setupKeyListener();
        }

        private void setupKeyListener() {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    handleKeyPress(e);
                    updateStatus(); // Update the status label after handling the key press
                }
            });
        }


        private void handleKeyPress(KeyEvent e) {
            int x = gameState.getCurrentPlayerTurn() == 1 ? gameState.player1Position.x : gameState.player2Position.x;
            int y = gameState.getCurrentPlayerTurn() == 1 ? gameState.player1Position.y : gameState.player2Position.y;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    y--;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    y++;
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    x--;
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    x++;
                    break;
                default:
                    return; // Ignore other keys
            }

            updateStatus();

            gameState.movePlayer(x, y);
            repaint(); // Redraw after movement
        }

        public void updateStatus() {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Player " + gameState.getCurrentPlayerTurn() + "'s Turn");
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGrid(g);
            drawPlayer(g, gameState.player1Position, Color.RED, "P1");
            drawPlayer(g, gameState.player2Position, Color.BLUE, "P2");
        }

        // Drawing method updated to include player labels for clarity
        private void drawPlayer(Graphics g, Point position, Color color, String label) {
            g.setColor(color);
            int cellSize = 80;
            g.fillOval(position.x * cellSize + 10, position.y * cellSize + 10, cellSize - 20, cellSize - 20);
            g.setColor(Color.BLACK);
            g.drawString(label, position.x * cellSize + 35, position.y * cellSize + 45);
        }


        private void drawGrid(Graphics g) {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    int x = i * CELL_SIZE;
                    int y = j * CELL_SIZE;
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE); // Draw cell borders
                    if (gameState.isTreasure(i, j)) {
                        g.setColor(Color.GREEN); // Color cell green if it contains a treasure
                        g.fillRect(x + 1, y + 1, CELL_SIZE - 1, CELL_SIZE - 1);
                        g.setColor(Color.BLACK); // Reset color for subsequent drawing
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TravelingSalesmanGame frame = new TravelingSalesmanGame();
            frame.setVisible(true);
        });

    }
}

