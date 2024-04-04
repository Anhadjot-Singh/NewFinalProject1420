package pack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

class Building {
    Color color;
    Point position;

    public Building(Color color, Point position) {
        this.color = color;
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public Point getPosition() {
        return position;
    }
}

class Castle extends Building {
    public Castle(Point position) {
        super(Color.YELLOW, position);
    }
}

class Treasure extends Building {
    public Treasure(Point position) {
        super(Color.GREEN, position);
    }
}

class Wall extends Building {
    public Wall(Point position) {
        super(Color.BLACK, position);
    }
}

class Market extends Building {
    public Market(Point position) {
        super(Color.ORANGE, position);
    }
}

class LostItem extends Building {
    private boolean hasGivenMoney = false;

    public LostItem(Point position) {
        super(Color.BLUE, position);
    }

    public boolean hasGivenMoney() {
        return hasGivenMoney;
    }

    public void giveMoney() {
        this.hasGivenMoney = true;
    }
}



class Trap extends Building {
    public Trap(Point position) {
        super(Color.RED, position);
    }
}

class GameState {
    Point player1Position = new Point(0, 0);
    Point player2Position = new Point(9, 9);
    Set<Building> buildings = new HashSet<>();
    int currentPlayerTurn = 1;
    int player1Money = 100;
    int player2Money = 100;
    int player1Power = 10;
    int player2Power = 10;
    int movesLeft = 0;
    private JLabel walletDisplay = new JLabel("Player 1: $100 | Player 2: $100", SwingConstants.CENTER);
    private JLabel powerDisplay = new JLabel("Player 1 Power: 10 | Player 2 Power: 10", SwingConstants.CENTER);

    public void playerEarnsMoney(int playerNumber, int amount) {
        if (playerNumber == 1) {
            player1Money += amount;
        } else if (playerNumber == 2) {
            player2Money += amount;
        }
        updateDisplays();
    }

    public void playerUsesPower(int playerNumber, int powerUsed) {
        if (playerNumber == 1) {
            player1Power -= powerUsed;
        } else if (playerNumber == 2) {
            player2Power -= powerUsed;
        }
        updateDisplays();
    }


    public GameState(JLabel walletDisplay, JLabel powerDisplay) {
        this.walletDisplay = walletDisplay;
        this.powerDisplay = powerDisplay;
        initializeBuildings();
    }

    public void updateDisplays() {
        SwingUtilities.invokeLater(() -> {
            walletDisplay.setText(String.format("Player 1: $%d | Player 2: $%d", player1Money, player2Money));
            powerDisplay.setText(String.format("Player 1 Power: %d | Player 2 Power: %d", player1Power, player2Power));
        });
    }


    private void initializeBuildings() {
        buildings.add(new Castle(new Point(4, 4)));
        // Assuming addRandomBuildings method properly initializes other buildings
        addRandomBuildings(Treasure.class, 8);
        addRandomBuildings(Wall.class, 5);
        addRandomBuildings(Market.class, 5);
        addRandomBuildings(LostItem.class, 13);
        addRandomBuildings(Trap.class, 5);
    }

    private void addRandomBuildings(Class<? extends Building> buildingClass, int count) {
        Random random = new Random();
        while (count > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            Point position = new Point(x, y);

            if (position.equals(player1Position) || position.equals(player2Position)) continue;

            boolean isOverlapping = buildings.stream().anyMatch(b -> b.getPosition().equals(position));
            if (!isOverlapping) {
                try {
                    buildings.add(buildingClass.getConstructor(Point.class).newInstance(position));
                    count--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isBuilding(int x, int y) {
        return buildings.stream().anyMatch(b -> b.getPosition().equals(new Point(x, y)));
    }

    public int rollDice() {
        Random random = new Random();
        movesLeft = random.nextInt(6) + 1; // Dice roll between 1 and 6
        return movesLeft;
    }

    public void interactWithBuilding(Building building) {
        if(building instanceof Market){
            interactWithMarket(currentPlayerTurn);
        }
    }

    private void interactWithMarket(int playerNumber) {
        Object[] options = {"$50 for 20 Power", "$30 for 10 Power", "$10 for 5 Power", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "Select a weapon to buy:",
                "Market", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[3]);
        int moneyCost = 0;
        int powerGain = 0;
        switch (choice) {
            case 0: // $50 for 20 power
                moneyCost = 50;
                powerGain = 20;
                break;
            case 1: // $30 for 10 power
                moneyCost = 30;
                powerGain = 10;
                break;
            case 2: // $10 for 5 power
                moneyCost = 10;
                powerGain = 5;
                break;
            default: // Cancel or close
                return;
        }

        // Check if Player 1 is making a purchase
        if (playerNumber == 1) {
            if (player1Money >= moneyCost) {
                player1Money -= moneyCost;
                player1Power += powerGain;
                JOptionPane.showMessageDialog(null, "Player 1, weapon purchased! Power increased by " + powerGain + " points.");
            } else {
                JOptionPane.showMessageDialog(null, "Player 1, not enough money for this purchase.");
            }
        }
        // Check if Player 2 is making a purchase
        else if (playerNumber == 2) {
            if (player2Money >= moneyCost) {
                player2Money -= moneyCost;
                player2Power += powerGain;
                JOptionPane.showMessageDialog(null, "Player 2, weapon purchased! Power increased by " + powerGain + " points.");
            } else {
                JOptionPane.showMessageDialog(null, "Player 2, not enough money for this purchase.");
            }
        }
        updateDisplays();
    }

    public boolean movePlayer(int dx, int dy) {
        if (movesLeft <= 0) return false;

        Point playerPosition = currentPlayerTurn == 1 ? player1Position : player2Position;
        Point newPosition = new Point(playerPosition.x + dx, playerPosition.y + dy);

        if (newPosition.x < 0 || newPosition.x >= 10 || newPosition.y < 0 || newPosition.y >= 10 || isWall(newPosition.x, newPosition.y)) return false;

        playerPosition.setLocation(newPosition);
        movesLeft--;

        Building building = getBuildingAtPosition(newPosition.x, newPosition.y);
        if (building != null) {
            interactWithBuilding(building);
        }

        if (player1Position.equals(player2Position)) {
            conductBattle();
        }
        if (movesLeft == 0) {
            currentPlayerTurn = currentPlayerTurn == 1 ? 2 : 1;
        }

        return true;
    }

    private boolean isWall(int x, int y) {
        return buildings.stream().anyMatch(b -> b instanceof Wall && b.getPosition().equals(new Point(x, y)));
    }

    private void conductBattle() {
        if (player1Power > player2Power) {
            player2Position.setLocation(9, 9);
            player2Power = Math.max(player2Power - 5, 0);
            JOptionPane.showMessageDialog(null, "Player 1 wins the battle! Player 2 loses power and is moved back.");
        } else if (player2Power > player1Power) {
            player1Position.setLocation(0, 0);
            player1Power = Math.max(player1Power - 5, 0);
            JOptionPane.showMessageDialog(null, "Player 2 wins the battle! Player 1 loses power and is moved back.");
        } else {
            // Optional: handle a draw condition
            JOptionPane.showMessageDialog(null, "The battle is a draw! Both players hold their ground.");
        }

        updateDisplays();
    }

    private void applyBattleOutcome(int winner, int loser) {
        if (winner == 1) {
            player1Money += 10; // Example money transfer
            player2Money -= 10;
            player2Position.setLocation(9, 9); // Reset loser position
        } else {
            player2Money += 10; // Example money transfer
            player1Money -= 10;
            player1Position.setLocation(0, 0); // Reset loser position
        }
    }

    // Helper method to get the building at a specific position
    private Building getBuildingAtPosition(int x, int y) {
        for (Building building : buildings) {
            if (building.getPosition().equals(new Point(x, y))) {
                return building;
            }
        }
        return null;
    }


    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public boolean isCurrentPlayerTurn() {
        return movesLeft > 0;
    }
}

class Dice extends JLabel {
    public Dice() {
        super("Roll Dice", SwingConstants.CENTER);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int roll = new Random().nextInt(6) + 1;
                setText("Dice: " + roll);
            }
        });
    }
}

class TravelingSalesmanGame extends JFrame {
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 850;
    private final int GRID_SIZE = 10;
    private final int CELL_SIZE = 75;
    private JLabel statusLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);
    private JLabel walletDisplay = new JLabel("Player 1: $0 | Player 2: $0", SwingConstants.CENTER);
    private JLabel powerDisplay = new JLabel("Player 1 Power: 0 | Player 2 Power: 0", SwingConstants.CENTER);
    private Dice dice = new Dice();
    private GameState gameState;

    public TravelingSalesmanGame() {
        setTitle("Traveling Salesman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        setLayout(new BorderLayout()); // Use BorderLayout for the frame

        // Add the labels to the top and bottom of the BorderLayout
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusPanel.add(statusLabel);
        statusPanel.add(walletDisplay);
        add(statusPanel, BorderLayout.NORTH);

        add(powerDisplay, BorderLayout.SOUTH);

        gameState = new GameState(walletDisplay, powerDisplay); // Create GameState with references to the labels

        GamePanel gamePanel = new GamePanel(gameState); // GamePanel needs to be modified to accept GameState
        add(gamePanel, BorderLayout.CENTER); // Add the GamePanel to the center

        // Make sure the frame is visible
        setVisible(true);
    }


    // Inside the TravelingSalesmanGame class

    class GamePanel extends JPanel {
        private GameState gameState;
        public GamePanel(GameState gameState) {
            this.gameState = gameState;
            setFocusable(true);
            requestFocusInWindow();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    handleKeyPress(e);
                }
            });
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
            int dx = 0, dy = 0;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    dx = -1;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    dx = 1;
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    dy = -1;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    dy = 1;
                    break;
                case KeyEvent.VK_SPACE:
                    if (!gameState.isCurrentPlayerTurn()) {
                        int roll = gameState.rollDice();
                        dice.setText("Dice: " + roll); // Update dice roll visually
                        statusLabel.setText("Player " + gameState.getCurrentPlayerTurn() + " rolls " + roll + "! Moves left: " + gameState.movesLeft);
                    }
                    return;
            }

            if (dx != 0 || dy != 0 && gameState.isCurrentPlayerTurn()) {
                if (gameState.movePlayer(dx, dy)) {
                    repaint();
                    statusLabel.setText("Player " + gameState.getCurrentPlayerTurn() + "'s Turn. Moves left: " + gameState.movesLeft);
                    if (gameState.movesLeft == 0) {
                        statusLabel.setText("Player " + gameState.getCurrentPlayerTurn() + "'s Turn. Press SPACE to roll the dice.");
                    }
                }
            }
        }

        public void updateStatus() {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Player " + gameState.getCurrentPlayerTurn() + "'s Turn");
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    int x = i * CELL_SIZE;
                    int y = j * CELL_SIZE;
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                    if (gameState.isBuilding(i, j)) {
                        for (Building building : gameState.buildings) {
                            if (building.getPosition().equals(new Point(i, j))) {
                                g.setColor(building.getColor());
                                g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                                break;
                            }
                        }
                    }
                }
            }
            drawPlayer(g, gameState.player1Position, Color.RED, "P1");
            drawPlayer(g, gameState.player2Position, Color.BLUE, "P2");
        }

        private void drawPlayers(Graphics g) {
            drawPlayer(g, gameState.player1Position, Color.RED, "P1");
            drawPlayer(g, gameState.player2Position, Color.BLUE, "P2");
        }

        private void drawPlayer(Graphics g, Point position, Color color, String label) {
            int x = position.x * CELL_SIZE;
            int y = position.y * CELL_SIZE;
            g.setColor(color);
            g.fillOval(x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20);
            g.setColor(Color.BLACK);
            g.drawString(label, x + (CELL_SIZE / 2) - 5, y + (CELL_SIZE / 2) + 5);
        }

        private void drawBuildings(Graphics g) {
            for (Building building : gameState.buildings) {
                g.setColor(building.getColor());
                int x = building.getPosition().x * CELL_SIZE;
                int y = building.getPosition().y * CELL_SIZE;
                g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
            }
        }


        private void drawGrid(Graphics g) {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    int x = i * CELL_SIZE;
                    int y = j * CELL_SIZE;
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE); // Draw cell borders
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TravelingSalesmanGame game = new TravelingSalesmanGame();
        });
    }
}
