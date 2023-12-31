import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private static String difficulty;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        difficulty = "normal";
    }

    public static String getDifficulty() {
        return difficulty;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }



    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to TREASURE HUNTER!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 10);

        System.out.print("Easy, normal, or hard mode? (e/n/h): ");
        difficulty = SCANNER.nextLine().toLowerCase();
        String[] itemsToPopulate = {"water", "rope", "machete", "boots", "horse", "boat", "shovel"};
        int[] prices = Shop.getPriceList(difficulty);
        if (difficulty.equals("e")) {
            difficulty = "easy";
            hunter.changeGold(10);
        } else if (difficulty.equals("m")) {
            difficulty = "normal";
        } else if (difficulty.equals("h")) {
            difficulty = "hard";
        } else if (difficulty.equals("test")) {
            difficulty = "test";
            hunter.changeGold(150);
            for (int i = 0; i < prices.length; i++) {
                hunter.buyItem(itemsToPopulate[i], prices[i]);
            }
        } else if (difficulty.equals("s")) {
            difficulty = "samurai";
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        switch (difficulty) {
            // in hard mode, you get less money back when you sell items
            case "hard": markdown = 0.25;
            // and the town is "tougher"
            toughness = 0.75;
            break;

            case "easy": markdown = 1;
            break;

            default: markdown = 0.5;
            toughness = 0.4;
            break;
        }

        String[] treasures = {"Crown", "Trophy", "Gem", "Dust"};
        String treasure = treasures[(int) (Math.random() * 4)];

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness, treasure, false);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        if (currentTown.getCouldNotPay()) {
            System.out.println(Colors.RED + "You lose.");
            processChoice("x");
        }
        while (!choice.equals("x") && !currentTown.getCouldNotPay() && hunter.getTreasureCounter() < 3) {
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter);
            System.out.println(currentTown);
            System.out.println("(B)uy something at the shop.");
            System.out.println("(S)ell something at the shop.");
            System.out.println("(M)ove on to a different town.");
            System.out.println("(L)ook for trouble!");
            System.out.println("(D)ig for gold");
            System.out.println("(H)unt for treasure!");
            System.out.println("Give up the hunt and e(X)it.");
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }

    }
    private void treasureSearch() {
        if (!currentTown.getTreasure().equals("Dust")) {
            if (!hunter.hasTreasure(currentTown.getTreasure())) {
                hunter.addTreasure(currentTown.getTreasure());
            } else {
                System.out.println("You already have this treasure you don't need it.");
            }
        } else {
            System.out.println("You do not need this dust.");
        }
        System.out.println(hunter.getTreasureCounter());
        currentTown.setTreasureFound(true);
        if (hunter.getTreasureCounter() == 3) {
            System.out.println("You found a " + Colors.BLUE + currentTown.getTreasure() + Colors.RESET + "!");
            System.out.println(Colors.GREEN + "Congratulations you have found the last of the three treasures, you win!" + Colors.RESET);
            processChoice("x");
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("x") || hunter.getGold() < 0) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else if (choice.equals("d")) {
            currentTown.digForGold();
        } else if (choice.equals("h")) {
            if (currentTown.getTreasureFound()) {
                System.out.println("You have already searched this town");
            } else if (hunter.getTreasureCounter() < 3) {
                System.out.println("You found a " + currentTown.getTreasure() + "!");
                treasureSearch();
            }
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
    }
}