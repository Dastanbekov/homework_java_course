import java.util.*;

class Hero {
    String name;
    int coins;
    int energy;
    String[] inventory;
    int invCount = 0;
    public Hero(String name, int coins, int energy) {
        this.name = name;
        this.coins = coins;
        this.energy = energy;
        this.inventory = new String[10];
    }
    public void printStatus() {
        System.out.printf("=== Hero Status ===\nName: %s | Coins: %d | Energy: %d\nInventory: %s\n", name, coins, energy, Arrays.toString(Arrays.copyOf(inventory, invCount)));
    }
    public void addItem(String item) {
        if (invCount < inventory.length) inventory[invCount++] = item;
    }
    public boolean hasItem(String item) {
        for (int i = 0; i < invCount; i++) if (inventory[i].equals(item)) return true;
        return false;
    }
    public void run() {
        int loss = new Random().nextInt(3) + 1;
        energy -= loss;
        coins += 2;
        System.out.printf("%s пробежал: -%d энергии, +2 монеты\n", name, loss);
    }
    public void work() {
        coins += 10;
        energy -= 2;
        System.out.printf("%s работал: +10 монет, -2 энергии\n", name);
    }
    public void rest() {
        int before = energy;
        energy = Math.min(20, energy + 5);
        System.out.printf("%s отдыхал: +%d энергии\n", name, energy - before);
    }
    public boolean buy(String item, int price) {
        if (coins >= price) {
            coins -= price;
            addItem(item);
            System.out.printf("%s купил %s за %d монет\n", name, item, price);
            return true;
        } else {
            System.out.println("Недостаточно монет!");
            return false;
        }
    }
}

class Shop {
    String[] items = {"Bread", "Potion", "Sword"};
    int[] prices = {5, 12, 50};
    public void printCatalog() {
        System.out.printf("%-10s | %5s\n", "Item", "Price");
        System.out.println("-----------+-------");
        for (int i = 0; i < items.length; i++) {
            System.out.printf("%-10s | %5d\n", items[i], prices[i]);
        }
    }
    public boolean sell(Hero hero, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= items.length) return false;
        return hero.buy(items[itemIndex], prices[itemIndex]);
    }
}

public class Homework1 {
    public static void main(String[] args) {
        // Exercise 1
        String[] items = {"Bread", "Potion", "Sword"};
        int[] prices = {5, 12, 50};
        int coins = 42;
        System.out.printf("Hero: Alex, Coins: %d\n", coins);
        System.out.printf("%-10s | %5s | %3s\n", "Item", "Price", "Can Buy");
        System.out.println("-----------+-------+---------");
        for (int i = 0; i < items.length; i++) {
            System.out.printf("%-10s | %5d | %3d\n", items[i], prices[i], coins / prices[i]);
        }
        System.out.println();

        // Exercise 2
        int wantIdx = 1; // Покупаем Potion
        int price = prices[wantIdx];
        if (coins < price) {
            System.out.println("Not enough");
        } else if (coins == price) {
            System.out.println("Enough for exact purchase");
        } else {
            if (coins >= 2 * price) {
                int discount = (int)(price * 0.8);
                System.out.printf("20%% discount! New price: %d\n", discount);
            } else {
                System.out.println("Can buy, no discount");
            }
        }
        System.out.println();

        // Exercise 3
        int total = 0;
        int months = 0;
        System.out.printf("Month | Coins total\n------+------------\n");
        while (total < 50) {
            months++;
            total += 7;
            if (months % 4 == 0) total += 10;
            System.out.printf("%2d    | %3d\n", months, total);
        }
        System.out.printf("Нужно месяцев: %d\n\n", months);

        // Exercise 4
        int energy = 20;
        int lap = 0;
        Random rand = new Random();
        while (energy > 0) {
            lap++;
            int loss = rand.nextInt(3) + 1;
            energy -= loss;
            System.out.printf("Lap %d | -%d energy | Energy left: %d\n", lap, loss, energy);
            if (lap % 5 == 0) {
                energy += 2;
                System.out.printf("Lap %d | Adrenaline boost! +2 energy | Energy left: %d\n", lap, energy);
            }
        }
        System.out.println();

        // Exercise 5, 6
        Hero alex = new Hero("Alex", 42, 20);
        alex.printStatus();
        alex.addItem("Bread");
        alex.addItem("Potion");
        System.out.println("Has bread? " + alex.hasItem("Bread"));
        alex.run();
        alex.work();
        alex.rest();
        alex.buy("Sword", 50);
        alex.printStatus();
        System.out.println();

        // Exercise 7
        Shop shop = new Shop();
        shop.printCatalog();
        shop.sell(alex, 0);
        shop.sell(alex, 1);
        shop.sell(alex, 2);
        alex.printStatus();
        System.out.println();

        // Exercise 8
        Hero mila = new Hero("Mila", 40, 20);
        Random r = new Random();
        for (int round = 1; round <= 10; round++) {
            if (r.nextBoolean()) {
                alex.run();
            } else {
                alex.work();
            }
            if (r.nextBoolean()) {
                mila.rest();
            } else {
                shop.sell(mila, r.nextInt(3));
            }
        }
        alex.printStatus();
        mila.printStatus();
        String winner = (alex.invCount > mila.invCount) ? alex.name : mila.name;
        int winCoins = (alex.invCount > mila.invCount) ? alex.coins : mila.coins;
        int winItems = Math.max(alex.invCount, mila.invCount);
        System.out.printf("Winner: %s with %d items and %d coins!\n\n", winner, winItems, winCoins);

        // Exercise 9
        Hero player = new Hero("Alex", 0, 10);
        Shop questShop = new Shop();
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Shop Catalog ===");
        questShop.printCatalog();
        while (!(player.hasItem("Sword") && player.invCount >= 3)) {
            player.printStatus();
            System.out.println("Choose action: 1=Run 2=Work 3=Rest 4=Buy 5=Inventory");
            int act = sc.nextInt();
            switch (act) {
                case 1: player.run(); break;
                case 2: player.work(); break;
                case 3: player.rest(); break;
                case 4:
                    questShop.printCatalog();
                    System.out.print("Enter item index: ");
                    int idx = sc.nextInt();
                    questShop.sell(player, idx);
                    break;
                case 5:
                    System.out.println("Inventory: " + Arrays.toString(Arrays.copyOf(player.inventory, player.invCount)));
                    break;
            }
        }
        System.out.println("Поздравляем! Вы купили Sword of Victory и минимум 2 других предмета!");
    }
}

