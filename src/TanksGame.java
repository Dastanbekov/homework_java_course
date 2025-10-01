import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class TanksGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Танчики на двоих");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new TanksPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class TanksPanel extends JPanel implements ActionListener, KeyListener {
    public static final int CELL_SIZE = 30;
    public static final int WIDTH = 20;
    public static final int HEIGHT = 15;
    private static final int PANEL_WIDTH = WIDTH * CELL_SIZE;
    private static final int PANEL_HEIGHT = HEIGHT * CELL_SIZE;
    private final Timer timer;
    private final Timer luckyBoxTimer;
    private Tank tank1, tank2;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Rectangle> walls = new ArrayList<>();
    private final Set<LuckyBox> luckyBoxes = new HashSet<>();
    private boolean gameOver = false;
    private String winner = "";
    private final Random rand = new Random();
    private long freezeEnd1 = 0;
    private long freezeEnd2 = 0;

    public TanksPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(this);
        initGame();
        timer = new Timer(50, this);
        timer.start();
        luckyBoxTimer = new Timer(5000, e -> spawnLuckyBox());
        luckyBoxTimer.start();
    }

    private void initGame() {
        tank1 = new Tank(2, HEIGHT / 2, Color.GREEN, "RIGHT");
        tank2 = new Tank(WIDTH - 3, HEIGHT / 2, Color.BLUE, "LEFT");
        bullets.clear();
        walls.clear();
        luckyBoxes.clear();
        // Примитивные стены
        for (int i = 5; i < 15; i++) {
            walls.add(new Rectangle(i, 7, 1, 1));
        }
        gameOver = false;
        winner = "";
        tank1.resetPowerups();
        tank2.resetPowerups();
    }

    private void spawnLuckyBox() {
        if (luckyBoxes.size() > 3) return;
        int x, y;
        boolean valid;
        int attempts = 0;
        do {
            x = rand.nextInt(WIDTH);
            y = rand.nextInt(HEIGHT);
            valid = true;
            for (Rectangle wall : walls) {
                if (x >= wall.x && x < wall.x + wall.width && y >= wall.y && y < wall.y + wall.height) {
                    valid = false;
                    break;
                }
            }
            if ((x == tank1.x && y == tank1.y) || (x == tank2.x && y == tank2.y)) valid = false;
            for (LuckyBox box : luckyBoxes) {
                if (box.x == x && box.y == y) valid = false;
            }
            attempts++;
            if (attempts > 100) break; // чтобы не зависнуть
        } while (!valid);
        // Исправлено: теперь freeze всегда в пуле
        String[] types = {"RPG", "SHIELD", "FREEZE"};
        String type = types[rand.nextInt(types.length)];
        luckyBoxes.add(new LuckyBox(x, y, type));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Рисуем стены
        g.setColor(Color.GRAY);
        for (Rectangle wall : walls) {
            g.fillRect(wall.x * CELL_SIZE, wall.y * CELL_SIZE, wall.width * CELL_SIZE, wall.height * CELL_SIZE);
        }
        // Рисуем лаки-боксы
        for (LuckyBox box : luckyBoxes) {
            if (box.type.equals("RPG")) g.setColor(Color.MAGENTA);
            else if (box.type.equals("SHIELD")) g.setColor(Color.CYAN);
            g.fillRect(box.x * CELL_SIZE + 5, box.y * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(box.type, box.x * CELL_SIZE + 2, box.y * CELL_SIZE + CELL_SIZE - 6);
        }
        // Рисуем танки
        tank1.draw(g);
        tank2.draw(g);
        // Рисуем пули
        for (Bullet b : bullets) {
            b.draw(g);
        }
        // Сообщение о победе
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Победил: " + winner, PANEL_WIDTH / 2 - 120, PANEL_HEIGHT / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Нажмите Enter для рестарта", PANEL_WIDTH / 2 - 120, PANEL_HEIGHT / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            moveBullets();
            checkLuckyBoxPickup();
        }
        repaint();
    }

    private void checkLuckyBoxPickup() {
        List<LuckyBox> toRemove = new ArrayList<>();
        for (LuckyBox box : luckyBoxes) {
            if (tank1.x == box.x && tank1.y == box.y) {
                tank1.applyPowerup(box.type);
                if (box.type.equals("FREEZE")) freezeEnd2 = System.currentTimeMillis() + 3000;
                toRemove.add(box);
            }
            if (tank2.x == box.x && tank2.y == box.y) {
                tank2.applyPowerup(box.type);
                if (box.type.equals("FREEZE")) freezeEnd1 = System.currentTimeMillis() + 3000;
                toRemove.add(box);
            }
        }
        luckyBoxes.removeAll(toRemove);
    }

    private void moveBullets() {
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : bullets) {
            b.move();
            // Проверка выхода за границы
            if (b.x < 0 || b.x >= WIDTH || b.y < 0 || b.y >= HEIGHT) {
                toRemove.add(b);
                continue;
            }
            // Проверка столкновения с танками
            if (b.fromTank != tank1 && b.collidesWithTank(tank1)) {
                if (tank1.shield) {
                    tank1.shield = false;
                    toRemove.add(b);
                } else {
                    gameOver = true;
                    winner = "Синий";
                    timer.stop();
                    luckyBoxTimer.stop();
                    return;
                }
            }
            if (b.fromTank != tank2 && b.collidesWithTank(tank2)) {
                if (tank2.shield) {
                    tank2.shield = false;
                    toRemove.add(b);
                } else {
                    gameOver = true;
                    winner = "Зелёный";
                    timer.stop();
                    luckyBoxTimer.stop();
                    return;
                }
            }
            // Проверка столкновения со стеной
            boolean hitWall = false;
            for (Rectangle wall : walls) {
                if (b.collidesWithWall(wall)) {
                    if (b.rpg) {
                        toRemove.add(b);
                        walls.remove(wall);
                        hitWall = true;
                        break;
                    } else {
                        toRemove.add(b);
                        hitWall = true;
                        break;
                    }
                }
            }
            if (hitWall) continue;
        }
        bullets.removeAll(toRemove);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            initGame();
            timer.start();
            luckyBoxTimer.start();
            repaint();
            return;
        }
        long now = System.currentTimeMillis();
        // Управление танком 1 (WASD, Q)
        if (!gameOver && now > freezeEnd1) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    tank1.move(0, -1, walls, tank2);
                    tank1.direction = "UP";
                    break;
                case KeyEvent.VK_S:
                    tank1.move(0, 1, walls, tank2);
                    tank1.direction = "DOWN";
                    break;
                case KeyEvent.VK_A:
                    tank1.move(-1, 0, walls, tank2);
                    tank1.direction = "LEFT";
                    break;
                case KeyEvent.VK_D:
                    tank1.move(1, 0, walls, tank2);
                    tank1.direction = "RIGHT";
                    break;
                case KeyEvent.VK_Q:
                    bullets.add(tank1.shoot());
                    break;
            }
        }
        // Управление танком 2 (стрелки, Ь)
        if (!gameOver && now > freezeEnd2) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    tank2.move(0, -1, walls, tank1);
                    tank2.direction = "UP";
                    break;
                case KeyEvent.VK_DOWN:
                    tank2.move(0, 1, walls, tank1);
                    tank2.direction = "DOWN";
                    break;
                case KeyEvent.VK_LEFT:
                    tank2.move(-1, 0, walls, tank1);
                    tank2.direction = "LEFT";
                    break;
                case KeyEvent.VK_RIGHT:
                    tank2.move(1, 0, walls, tank1);
                    tank2.direction = "RIGHT";
                    break;
                case KeyEvent.VK_M:
                    bullets.add(tank2.shoot());
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}

class LuckyBox {
    int x, y;
    String type;
    public LuckyBox(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}

class Tank {
    int x, y;
    Color color;
    String direction;
    boolean rpg = false;
    boolean shield = false;
    int rpgSize = 1;
    public Tank(int x, int y, Color color, String direction) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.direction = direction;
    }
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x * TanksPanel.CELL_SIZE, y * TanksPanel.CELL_SIZE, TanksPanel.CELL_SIZE, TanksPanel.CELL_SIZE);
        // Дуло
        g.setColor(Color.WHITE);
        int cx = x * TanksPanel.CELL_SIZE + TanksPanel.CELL_SIZE / 2;
        int cy = y * TanksPanel.CELL_SIZE + TanksPanel.CELL_SIZE / 2;
        int len = TanksPanel.CELL_SIZE / 2;
        switch (direction) {
            case "UP":
                g.drawLine(cx, cy, cx, cy - len);
                break;
            case "DOWN":
                g.drawLine(cx, cy, cx, cy + len);
                break;
            case "LEFT":
                g.drawLine(cx, cy, cx - len, cy);
                break;
            case "RIGHT":
                g.drawLine(cx, cy, cx + len, cy);
                break;
        }
        // Эффекты
        if (rpg) {
            g.setColor(Color.MAGENTA);
            g.drawRect(x * TanksPanel.CELL_SIZE + 2, y * TanksPanel.CELL_SIZE + 2, TanksPanel.CELL_SIZE - 4, TanksPanel.CELL_SIZE - 4);
        }
        if (shield) {
            g.setColor(Color.CYAN);
            g.drawOval(x * TanksPanel.CELL_SIZE + 2, y * TanksPanel.CELL_SIZE + 2, TanksPanel.CELL_SIZE - 4, TanksPanel.CELL_SIZE - 4);
        }
    }
    public void move(int dx, int dy, List<Rectangle> walls, Tank other) {
        int nx = x + dx;
        int ny = y + dy;
        if (nx < 0 || nx >= TanksPanel.WIDTH || ny < 0 || ny >= TanksPanel.HEIGHT) return;
        for (Rectangle wall : walls) {
            if (nx >= wall.x && nx < wall.x + wall.width && ny >= wall.y && ny < wall.y + wall.height) return;
        }
        if (nx == other.x && ny == other.y) return;
        x = nx;
        y = ny;
    }
    public Bullet shoot() {
        int bx = x, by = y;
        switch (direction) {
            case "UP": by--; break;
            case "DOWN": by++; break;
            case "LEFT": bx--; break;
            case "RIGHT": bx++; break;
        }
        Bullet b = new Bullet(bx, by, direction, this, rpg, rpg ? 3 : 1);
        rpg = false;
        rpgSize = 1;
        return b;
    }
    public void applyPowerup(String type) {
        if (type.equals("RPG")) {
            rpg = true;
            rpgSize = 3;
        }
        if (type.equals("SHIELD")) shield = true;
    }
    public void resetPowerups() {
        rpg = false;
        shield = false;
        rpgSize = 1;
    }
}

class Bullet {
    int x, y;
    String direction;
    Tank fromTank;
    boolean rpg;
    int rpgSize;
    public Bullet(int x, int y, String direction, Tank fromTank, boolean rpg, int rpgSize) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.fromTank = fromTank;
        this.rpg = rpg;
        this.rpgSize = rpgSize;
    }
    public void move() {
        switch (direction) {
            case "UP": y--; break;
            case "DOWN": y++; break;
            case "LEFT": x--; break;
            case "RIGHT": x++; break;
        }
    }
    public void draw(Graphics g) {
        if (rpg) {
            g.setColor(Color.MAGENTA);
            if (direction.equals("LEFT") || direction.equals("RIGHT")) {
                int start = x - (rpgSize / 2);
                for (int i = 0; i < rpgSize; i++) {
                    g.fillRect((start + i) * TanksPanel.CELL_SIZE, y * TanksPanel.CELL_SIZE, TanksPanel.CELL_SIZE, TanksPanel.CELL_SIZE);
                }
            } else {
                int start = y - (rpgSize / 2);
                for (int i = 0; i < rpgSize; i++) {
                    g.fillRect(x * TanksPanel.CELL_SIZE, (start + i) * TanksPanel.CELL_SIZE, TanksPanel.CELL_SIZE, TanksPanel.CELL_SIZE);
                }
            }
        } else {
            g.setColor(Color.ORANGE);
            g.fillOval(x * TanksPanel.CELL_SIZE + 8, y * TanksPanel.CELL_SIZE + 8, 14, 14);
        }
    }
    public boolean collidesWithTank(Tank tank) {
        if (rpg) {
            if (direction.equals("LEFT") || direction.equals("RIGHT")) {
                int start = x - (rpgSize / 2);
                for (int i = 0; i < rpgSize; i++) {
                    if ((start + i) == tank.x && y == tank.y) return true;
                }
            } else {
                int start = y - (rpgSize / 2);
                for (int i = 0; i < rpgSize; i++) {
                    if (x == tank.x && (start + i) == tank.y) return true;
                }
            }
            return false;
        } else {
            return x == tank.x && y == tank.y;
        }
    }
    public boolean collidesWithWall(Rectangle wall) {
        if (rpg) {
            if (direction.equals("LEFT") || direction.equals("RIGHT")) {
                int start = x - (rpgSize / 2);
                for (int i = 0; i < rpgSize; i++) {
                    int bx = start + i;
                    if (bx >= wall.x && bx < wall.x + wall.width && y >= wall.y && y < wall.y + wall.height) {
                        return true;
                    }
                }
            } else {
                int start = y - (rpgSize / 2);
                for (int i = 0; i < rpgSize; i++) {
                    int by = start + i;
                    if (x >= wall.x && x < wall.x + wall.width && by >= wall.y && by < wall.y + wall.height) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return x >= wall.x && x < wall.x + wall.width && y >= wall.y && y < wall.y + wall.height;
        }
    }
}
