/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

import java.util.Random;

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean couldNotPay;
    private boolean alreadyDugForGold;
    private String treasure;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, String treasure) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.treasure = treasure;


        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public String getLatestNews() {
        return printMessage;
    }
    public String getTreasure() {
        return treasure;
    }

    public boolean getCouldNotPay() { return couldNotPay; }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            alreadyDugForGold = false;
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    public void digForGold() {
        Random rand = new Random();
        int random1;
        random1 = rand.nextInt(2);

        if (!alreadyDugForGold) {
            if (hunter.hasItemInKit("shovel")) {
                if (random1 == 0) {
                    System.out.println("You dug but only found dirt");
                } else if (random1 == 1) {
                    random1 = (int) (Math.random() * 8) + 12;
                    System.out.println("You dug up " + random1 + " gold!");
                    hunter.changeGold(random1);
                }
                alreadyDugForGold = true;
            } else {
                System.out.println("Yoiu can't dig for gold without a shovel");
            }
        } else {
            System.out.println("You already dug for gold in this town.");
        }


    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        int easyMultiplier = 1;
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (TreasureHunter.getDifficulty().equals("easy")) {
            easyMultiplier = 2;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = Colors.RED + "You couldn't find any trouble" + Colors.RESET;
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if ((Math.random() * easyMultiplier) > noTroubleChance || TreasureHunter.getDifficulty().equals("samurai")) {
                if (TreasureHunter.getDifficulty().equals("samurai")) {
                    printMessage += Colors.RED + ("\nOh...a samurai...take my money!\n");
                }
                printMessage += Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                printMessage += Colors.RED + "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                if (hunter.getGold() - goldDiff >= 0) {
                    hunter.changeGold(-goldDiff);
                    printMessage += Colors.RED + "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                } else {
                    System.out.println();

                    //hunter.changeGold(-goldDiff);
                    printMessage += Colors.RED + "\nYou lost the brawl and could not pay the " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                    couldNotPay = true;
                }
            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .166) {
            return new Terrain(Colors.CYAN + "Mountains" + Colors.RESET, "Rope");
        } else if (rnd < .332) {
            return new Terrain(Colors.CYAN +"Ocean" + Colors.RESET, "Boat");
        } else if (rnd < .498) {
            return new Terrain(Colors.CYAN + "Plains" + Colors.RESET, "Horse");
        } else if (rnd < .664) {
            return new Terrain(Colors.CYAN + "Desert" + Colors.RESET, "Water");
        } else if (rnd < .83) {
            return new Terrain(Colors.CYAN + "Marsh" + Colors.RESET, "Boots");
        } else {
            return new Terrain(Colors.CYAN + "Jungle" + Colors.RESET, "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (TreasureHunter.getDifficulty().equals("easy")) {
            return false;
        } else {
            double rand = Math.random();
            return (rand < 0.5);
        }
    }
}