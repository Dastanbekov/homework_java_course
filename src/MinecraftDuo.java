import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MinecraftDuo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Minecraft на двоих");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new MinecraftPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class MinecraftPanel extends JPanel implements KeyListener {
    private static final int CELL_SIZE = 32;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 15;
    private static final int PANEL_WIDTH = WIDTH * CELL_SIZE;
    private static final int PANEL_HEIGHT = HEIGHT * CELL_SIZE;
    private final int[][] world = new int[WIDTH][HEIGHT]; // 0 - земля, 1 - трава, 2 - камень, 3 - пусто
    private final Player player1;
    private final Player player2;
    private final Random rand = new Random();
    private boolean gameOver = false;
    private String winner = "";

    public MinecraftPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        generateWorld();
        player1 = spawnPlayer(Color.BLUE);
        player2 = spawnPlayer(Color.RED);
        Timer gravityTimer = new Timer(100, e -> {
            applyGravity(player1);
            applyGravity(player2);
            repaint();
        });
        gravityTimer.start();
    }

    private Player spawnPlayer(Color color) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (world[x][y] == 3 && y + 1 < HEIGHT && (world[x][y + 1] == 0 || world[x][y + 1] == 1)) {
                    return new Player(x, y, color);
                }
            }
        }
        return new Player(0, 0, color); // fallback
    }

    private void generateWorld() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (y == HEIGHT - 1) world[x][y] = 2; // камень
                else if (y == HEIGHT - 2) world[x][y] = 0; // земля
                else if (y == HEIGHT - 3) world[x][y] = 1; // трава
                else world[x][y] = rand.nextInt(4) == 0 ? 2 : 3; // немного камня, остальное пусто
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Рисуем мир
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                switch (world[x][y]) {
                    case 0: g.setColor(new Color(139, 69, 19)); break; // земля
                    case 1: g.setColor(new Color(34, 139, 34)); break; // трава
                    case 2: g.setColor(Color.GRAY); break; // камень
                    default: g.setColor(Color.BLACK); // пусто
                }
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        // Рисуем игроков
        drawPlayer(g, player1);
        drawPlayer(g, player2);
        // HP и победа
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Игрок 1: WASD — движение, E — поставить, F — сломать, R — удар, Space — прыжок", 10, 20);
        g.drawString("Игрок 2: стрелки — движение, Num1 — поставить, Num2 — сломать, Num3 — удар, Num0 — прыжок", 10, 40);
        g.drawString("HP 1: " + player1.hp, 10, 70);
        g.drawString("HP 2: " + player2.hp, 200, 70);
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Победил: " + winner, PANEL_WIDTH / 2 - 100, PANEL_HEIGHT / 2);
        }
    }

    private void drawPlayer(Graphics g, Player p) {
        g.setColor(p.color);
        g.fillOval(p.x * CELL_SIZE + 4, p.y * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;
        // Игрок 1 — WASD, E (поставить), F (сломать), R (удар), Space (прыжок)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: movePlayer(player1, 0, -1); break;
            case KeyEvent.VK_S: movePlayer(player1, 0, 1); break;
            case KeyEvent.VK_A: movePlayer(player1, -1, 0); break;
            case KeyEvent.VK_D: movePlayer(player1, 1, 0); break;
            case KeyEvent.VK_E: placeBlock(player1); break;
            case KeyEvent.VK_F: breakBlock(player1); break;
            case KeyEvent.VK_R: attack(player1, player2); break;
            case KeyEvent.VK_SPACE: jump(player1); break;
        }
        // Игрок 2 — стрелки, NumPad1 (поставить), NumPad2 (сломать), NumPad3 (удар), NumPad0 (прыжок)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: movePlayer(player2, 0, -1); break;
            case KeyEvent.VK_DOWN: movePlayer(player2, 0, 1); break;
            case KeyEvent.VK_LEFT: movePlayer(player2, -1, 0); break;
            case KeyEvent.VK_RIGHT: movePlayer(player2, 1, 0); break;
            case KeyEvent.VK_NUMPAD1: placeBlock(player2); break;
            case KeyEvent.VK_NUMPAD2: breakBlock(player2); break;
            case KeyEvent.VK_NUMPAD3: attack(player2, player1); break;
            case KeyEvent.VK_NUMPAD0: jump(player2); break;
        }
        repaint();
    }

    private void movePlayer(Player p, int dx, int dy) {
        int nx = p.x + dx;
        int ny = p.y + dy;
        if (nx < 0 || nx >= WIDTH || ny < 0 || ny >= HEIGHT) return;
        if (world[nx][ny] == 3 && !isPlayerAt(nx, ny)) {
            p.x = nx;
            p.y = ny;
        }
    }

    private void placeBlock(Player p) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int bx = p.x + dx;
                int by = p.y + dy;
                if (bx >= 0 && bx < WIDTH && by >= 0 && by < HEIGHT && world[bx][by] == 3 && !(isPlayerAt(bx, by))) {
                    world[bx][by] = 0; // земля
                    return;
                }
            }
        }
    }

    private void breakBlock(Player p) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int bx = p.x + dx;
                int by = p.y + dy;
                if (bx >= 0 && bx < WIDTH && by >= 0 && by < HEIGHT && world[bx][by] != 3 && !(isPlayerAt(bx, by))) {
                    world[bx][by] = 3; // ломаем
                    return;
                }
            }
        }
    }

    private void attack(Player attacker, Player defender) {
        if (Math.abs(attacker.x - defender.x) <= 1 && Math.abs(attacker.y - defender.y) <= 1) {
            defender.hp--;
            if (defender.hp <= 0) {
                gameOver = true;
                winner = attacker.color == Color.BLUE ? "Игрок 1" : "Игрок 2";
            }
        }
    }

    private void jump(Player p) {
        int ny = p.y - 2;
        if (ny >= 0 && world[p.x][ny] == 3 && !isPlayerAt(p.x, ny)) {
            p.y = ny;
        }
    }

    private void applyGravity(Player p) {
        int ny = p.y + 1;
        if (ny < HEIGHT && world[p.x][ny] == 3 && !isPlayerAt(p.x, ny)) {
            p.y = ny;
        }
    }

    private boolean isPlayerAt(int x, int y) {
        return (player1.x == x && player1.y == y) || (player2.x == x && player2.y == y);
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}

class Player {
    int x, y;
    Color color;
    int hp = 5;
    public Player(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
