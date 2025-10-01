import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Змейка на двоих");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new SnakeGamePanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class SnakeGamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int CELL_SIZE = 25;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final int PANEL_WIDTH = WIDTH * CELL_SIZE;
    private static final int PANEL_HEIGHT = HEIGHT * CELL_SIZE;
    private final LinkedList<Point> snake1 = new LinkedList<>();
    private final LinkedList<Point> snake2 = new LinkedList<>();
    private Point food;
    private String direction1 = "RIGHT";
    private String direction2 = "LEFT";
    private boolean gameOver1 = false;
    private boolean gameOver2 = false;
    private final Timer timer;
    private int score1 = 0;
    private int score2 = 0;

    public SnakeGamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        initGame();
        timer = new Timer(120, this);
        timer.start();
    }

    private void initGame() {
        snake1.clear();
        snake2.clear();
        // Первый игрок стартует слева
        snake1.add(new Point(5, 10));
        snake1.add(new Point(4, 10));
        snake1.add(new Point(3, 10));
        // Второй игрок стартует справа
        snake2.add(new Point(14, 10));
        snake2.add(new Point(15, 10));
        snake2.add(new Point(16, 10));
        spawnFood();
        direction1 = "RIGHT";
        direction2 = "LEFT";
        gameOver1 = false;
        gameOver2 = false;
        score1 = 0;
        score2 = 0;
    }

    private void spawnFood() {
        Random rand = new Random();
        while (true) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            Point p = new Point(x, y);
            if (!snake1.contains(p) && !snake2.contains(p)) {
                food = p;
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Рисуем поле
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= WIDTH; i++)
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, PANEL_HEIGHT);
        for (int i = 0; i <= HEIGHT; i++)
            g.drawLine(0, i * CELL_SIZE, PANEL_WIDTH, i * CELL_SIZE);
        // Рисуем еду
        g.setColor(Color.RED);
        g.fillOval(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        // Рисуем змейку 1
        for (int i = 0; i < snake1.size(); i++) {
            if (i == 0) g.setColor(Color.GREEN);
            else g.setColor(new Color(0, 180, 0));
            Point p = snake1.get(i);
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        // Рисуем змейку 2
        for (int i = 0; i < snake2.size(); i++) {
            if (i == 0) g.setColor(Color.BLUE);
            else g.setColor(new Color(0, 0, 180));
            Point p = snake2.get(i);
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        // Рисуем счет
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Игрок 1 (зелёный): " + score1, 10, 22);
        g.drawString("Игрок 2 (синий): " + score2, 10, 44);
        // Рисуем сообщение о конце игры
        if (gameOver1 && gameOver2) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Игра окончена!", PANEL_WIDTH / 2 - 120, PANEL_HEIGHT / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Нажмите Enter для рестарта", PANEL_WIDTH / 2 - 120, PANEL_HEIGHT / 2 + 40);
            g.drawString("Счет 1: " + score1 + " | Счет 2: " + score2, PANEL_WIDTH / 2 - 120, PANEL_HEIGHT / 2 + 70);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver1 || !gameOver2) {
            moveSnakes();
        }
        repaint();
    }

    private void moveSnakes() {
        if (!gameOver1) moveSnake(snake1, direction1, snake2, true);
        if (!gameOver2) moveSnake(snake2, direction2, snake1, false);
    }

    private void moveSnake(LinkedList<Point> snake, String direction, LinkedList<Point> otherSnake, boolean isFirst) {
        Point head = snake.getFirst();
        Point newHead = new Point(head.x, head.y);
        switch (direction) {
            case "UP": newHead.y--; break;
            case "DOWN": newHead.y++; break;
            case "LEFT": newHead.x--; break;
            case "RIGHT": newHead.x++; break;
        }
        // Проверка на столкновение со стеной, собой или другой змейкой
        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT ||
            snake.contains(newHead) || otherSnake.contains(newHead)) {
            if (isFirst) gameOver1 = true; else gameOver2 = true;
            return;
        }
        snake.addFirst(newHead);
        if (newHead.equals(food)) {
            if (isFirst) score1++; else score2++;
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Рестарт
        if (gameOver1 && gameOver2 && e.getKeyCode() == KeyEvent.VK_ENTER) {
            initGame();
            timer.start();
            repaint();
            return;
        }
        // Управление первым игроком (стрелки)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (!direction1.equals("DOWN")) direction1 = "UP";
                break;
            case KeyEvent.VK_DOWN:
                if (!direction1.equals("UP")) direction1 = "DOWN";
                break;
            case KeyEvent.VK_LEFT:
                if (!direction1.equals("RIGHT")) direction1 = "LEFT";
                break;
            case KeyEvent.VK_RIGHT:
                if (!direction1.equals("LEFT")) direction1 = "RIGHT";
                break;
        }
        // Управление вторым игроком (WASD)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                if (!direction2.equals("DOWN")) direction2 = "UP";
                break;
            case KeyEvent.VK_S:
                if (!direction2.equals("UP")) direction2 = "DOWN";
                break;
            case KeyEvent.VK_A:
                if (!direction2.equals("RIGHT")) direction2 = "LEFT";
                break;
            case KeyEvent.VK_D:
                if (!direction2.equals("LEFT")) direction2 = "RIGHT";
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
}