import java.util.*;
import java.awt.Point;

public class Main {
    static int tileLimit;
    private static int counter;
    static double oddsOfTileSpawn = 0.95;
    static List<DualCard> usableCards;
    static Map<String, DualCard> allCards;

    private static Point lastCardAdded;
    private static List<String> chanceEvents;

    public static void main(String[] args) {
        Scanner consoleInput = new Scanner(System.in);

        // Get Which Mode
        System.out.print("Max tiles (-1 if no limit)? ");
        tileLimit = consoleInput.nextInt();
        System.out.println("What game mode?");
        System.out.println(" 1: See Map");
        System.out.println(" 2: Explore Map");
        System.out.println(" 3: See and Explore Map");
        System.out.print("Mode: ");
        int gameMode = consoleInput.nextInt();
        boolean isTileLimit = true;
        if (tileLimit == -1) {
            isTileLimit = false;
        }

        // Generate Map
        CardDisplay[][] tileMap = generateRandomDungeon(isTileLimit, 10);

        // Display Dungeon
        if (gameMode == 1 || gameMode == 3) {
            display2dArray(tileMap);
        }

        // Traverse Dungeon
        if (gameMode == 2 || gameMode == 3) {
            exploreMap(tileMap, consoleInput, lastCardAdded);
        }
    }

    /**
     * A helper method to main that creates the random dungeon
     * @param isTileLimit - holds whether there is a tile limit to the dungeon (do you cap the amount of tiles to use?)
     * @return a 2D CardDisplay array containing the dungeon
     */
    private static CardDisplay[][] generateRandomDungeon(boolean isTileLimit, int size) {
        usableCards = new ArrayList<>();
        allCards = new HashMap<>();
        CardDisplay[][] tileMap = new CardDisplay[size][size];
        Queue<PointDir> coorToAdd = new ArrayDeque<>();
        addCardHardCoded();
        primeTheWhileLoop(coorToAdd, tileMap);
        counter = 1;
        lastCardAdded = new Point((tileMap[0].length / 2), (tileMap.length / 2));

        while ((!isTileLimit || (counter < tileLimit)) && !coorToAdd.isEmpty() && !usableCards.isEmpty()) {
            PointDir thisPointDir = coorToAdd.remove();
            Point coor = thisPointDir.getCoor();
            char directionOfNewCard = thisPointDir.getDirection();

            addCardToRoom(coor, coorToAdd, tileMap, directionOfNewCard);
        }

        return tileMap;
    }

    /**
     * A helper method that holds all the random events that can happen in each room
     */
    private static void addChanceEventsHardCoded() {
        chanceEvents = new ArrayList<>();
        chanceEvents.add("All gold earned is doubled!");
        chanceEvents.add("All monsters will attack the person with the least health!");
        chanceEvents.add("All monsters will now regenerate once " +  (int)(bellCurve() * 7 + 2)  + " turns have passed!");
        chanceEvents.add("When players are attacked, they have the option to either take health damage, or lose 2 gold per heart");
        chanceEvents.add("If a player kills a monster back to back between player rounds, add " + (int)(bellCurve() * 4 + 1) + " energy!");
        chanceEvents.add("All Skills perform now cost " + (int)(bellCurve() * 3 + 1) + " less energy than normal!");
        chanceEvents.add("If any monster has more than 3 hp, split this into two separate monsters with their health sum to the original amount (Continue until no dice left if applicable)!");
        chanceEvents.add("A shockwave occurs. Divide the room into 4 even sections, in the form of a wave. Start at the end of the room, and work your ways up. For each section, roll a dice if the number is >= 4, then all living things in the section take 2 damage. Move this in a wave pattern once.");
        chanceEvents.add("All monsters will attack the person with the most gold!");
        chanceEvents.add("All monster kills now double energy earned!");
        chanceEvents.add("Every other round, roll a dice. If a 6 is rolled, everyone takes damage!");
        chanceEvents.add("You see a treasure chest disappearing. As you rush towards it, you chance is low. You must roll a 5 or 6 two times in a row. If so, earn 5 gold!");
        chanceEvents.add("When you kill a enemy, roll a dice. If a 6 is rolled, enemies additionally drop a random item from the item shop for free!");
    }

    /**
     * Displays all events in association to discovering a new room
     * @param isBossRoom - holds whether the room entered is the boos room
     */
    private static void displayNewRoomEvent(boolean isBossRoom) {
        if (isBossRoom) {
            System.out.println("Boss Room Discovered!!!!");
        }

        Random r = new Random();


        String enemiesMessage;
        int numberOfGreen = Math.max(1, (int)Math.round(bellCurve() * 6));
        int numberOfBlue = Math.max(1, (int)Math.round((bellCurve() - 0.2) * 5));
        enemiesMessage = "There are " + numberOfGreen + " green enemies and " + numberOfBlue + " blue enemies!";
        System.out.println(enemiesMessage);

        if (isBossRoom) {
            System.out.println("A boss monster appears");

            if (r.nextDouble() > 0.9) {
                System.out.println("Chance Event: The monster mutates, create another monster (another dice)!");
            }
        }

        if (r.nextDouble() >= 0.5) {
            System.out.print("Chance Event: ");
            int randomIndex = (int)Math.floor(Math.random() * chanceEvents.size());
            System.out.println(chanceEvents.get(randomIndex));
        }
    }

    /**
     * A helper method that returns a discrete gaussian distribution
     * @return a double between the range of [0.0, 1.0)
     */
    private static double bellCurve() {
        double sum = 0.0;
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            sum += r.nextDouble();
        }
        return (sum / 10.0);
    }

    /**
     * A helper method that contains all the information for all the cards in the Deck Box Dungeon Set
     */
    private static void addCardHardCoded() {

        List<Card> alphaList = new ArrayList<>();
        alphaList.add(new Card(" 1", true, false, false, 'A'));
        alphaList.add(new Card(" 2", true, false, false, 'A'));
        alphaList.add(new Card(" 3", true, false, false, 'A'));
        alphaList.add(new Card(" 4", true, false, false, 'A'));
        alphaList.add(new Card(" 5", true, false, false, 'A'));
        alphaList.add(new Card(" 6", true, true, true, 'A'));
        alphaList.add(new Card(" 7", true, false, false, 'A'));
        alphaList.add(new Card(" 8", true, true, true, 'A'));
        alphaList.add(new Card(" 9", true, true, true, 'A'));
        alphaList.add(new Card("10", true, true, true, 'A'));
        alphaList.add(new Card("11", true, false, false, 'A'));
        alphaList.add(new Card("12", false, true, false, 'A'));
        alphaList.add(new Card("13", true, false, true, 'A'));
        alphaList.add(new Card("14", true, false, false, 'A'));
        alphaList.add(new Card("15", true, true, true, 'A'));

        List<Card> betaList = new ArrayList<>();
        betaList.add(new Card(" 1", false, false, false, 'B'));
        betaList.add(new Card(" 2", false, false, false, 'B'));
        betaList.add(new Card(" 3", true, false, true, 'B'));
        betaList.add(new Card(" 4", false, true, true, 'B'));
        betaList.add(new Card(" 5", false, true, true, 'B'));
        betaList.add(new Card(" 6", true, true, true, 'B'));
        betaList.add(new Card(" 7", false, true, false, 'B'));
        betaList.add(new Card(" 8", true, false, false, 'B'));
        betaList.add(new Card(" 9", false, true, false, 'B'));
        betaList.add(new Card("10", true, false, false, 'B'));
        betaList.add(new Card("11", false, true, true, 'B'));
        betaList.add(new Card("12", true, true, false, 'B'));
        betaList.add(new Card("13", true, false, false, 'B'));
        betaList.add(new Card("14", true, true, true, 'B'));
        betaList.add(new Card("15", false, true, false, 'B'));



        for (int i = 0; i < alphaList.size(); i++) {
            usableCards.add(new DualCard(alphaList.get(i), betaList.get(i)));
        }

        for (int i = 0; i < usableCards.size(); i++) {
            allCards.put(usableCards.get(i).getAlpha().getName(), usableCards.get(i));
        }
    }


    /**
     * Adds a new card to the dungeon
     * @param coor - the coordinates of the new room
     * @param coorToAdd - the stack of coordinates to potentially add to the dungeon (contains coordinate and direction of card)
     * @param tileMap - the tile map of the dungeon
     * @param directionOfHead - the direction of the new cards to be added 'u' 'd' 'l' or 'r' for up, down, left, or right
     */
    private static void addCardToRoom(Point coor, Queue<PointDir> coorToAdd, CardDisplay[][] tileMap, char directionOfHead) {
        Card cardToAdd = isPlaceable(coor, tileMap, directionOfHead);

        if (cardToAdd != null) {
            counter++;
            tileMap[coor.y][coor.x] = new CardDisplay(cardToAdd.getName(), directionOfHead, cardToAdd.getType());
            lastCardAdded = coor;
            addCoorNeighborsToStack(coorToAdd, coor.x, coor.y, tileMap, cardToAdd.globalDirectionsGivenDirection(directionOfHead));//cardToAdd.direction);
        }

    }

    /**
     * Displays the grid of the dungeon
     * @param tileMap - the tile map of the dungeon
     */
    public static void display2dArray(CardDisplay[][] tileMap) {

        for (int y = 0; y < tileMap.length; y++) {
            for (int x = 0; x < tileMap[y].length; x++) {
                if (tileMap[y][x] == null) {
                    System.out.print("         ");
                } else {
                    System.out.print(tileMap[y][x] + " ");
                }

            }
            System.out.println();
        }
        System.out.println();
        System.out.println("====================");
        System.out.println();


    }

    /**
     * Prepares the while loop by adding the starting room, and any new entrances leading out of the room
     * @param coorToAdd - the coordinate stack of spots to fill
     * @param tileMap - the tile map
     */
    public static void primeTheWhileLoop(Queue<PointDir> coorToAdd, CardDisplay[][] tileMap) {
        Point coor = new Point((tileMap[0].length / 2), (tileMap.length / 2));
        int whichIndex = (int)Math.floor(Math.random() * usableCards.size());
        Card whichRoom;
        DualCard startCardDual = usableCards.remove(whichIndex);
        if (new Random().nextBoolean()) {
            whichRoom = startCardDual.getAlpha();
        } else {
            whichRoom = startCardDual.getBeta();
        }

        tileMap[coor.y][coor.x] = new CardDisplay(whichRoom.getName(), 'u', whichRoom.getType());
        addCoorNeighborsToStack(coorToAdd, coor.x, coor.y, tileMap, whichRoom.globalDirectionsGivenDirection('u'));
    }

    /**
     * Returns a Card with given rotation and direction that can be place at the given coordinates coor given the current
     * dungeon state, if no cards available meet the constraints (any pathways and adjacent cards), then return null
     * @param coor - the coordinates of the new room
     * @param tileMap - the tile map of the dungeon
     * @param directionOfHead - the direction of the card to be place. This is in reference to the array on the card, with ^ meaning up
     * @return a Card that can be placed at the given loaction
     */
    public static Card isPlaceable(Point coor, CardDisplay[][] tileMap, char directionOfHead) {
        if (Math.random() > (1 - oddsOfTileSpawn) && isBounded(tileMap, coor.x, coor.y) && tileMap[coor.y][coor.x] == null) {
            boolean[] allConstraints = globalDirectionsGivenDirectionInverse(directionOfHead, getAllNeighborsThatExist(coor, tileMap));

            List<Integer> allCardsThatMeetReqIndex = new ArrayList<>();
            List<Character> allCardsThatMeetReqType = new ArrayList<>();
            for (int i = 0; i < usableCards.size(); i++) {
                if (new Random().nextBoolean()) {
                    if (usableCards.get(i).getAlpha().meetsMinimumConstraints(allConstraints)) {
                        allCardsThatMeetReqIndex.add(i);
                        allCardsThatMeetReqType.add('A');
                    } else if (usableCards.get(i).getBeta().meetsMinimumConstraints(allConstraints)) {
                        allCardsThatMeetReqIndex.add(i);
                        allCardsThatMeetReqType.add('B');
                    }
                } else {
                    if (usableCards.get(i).getBeta().meetsMinimumConstraints(allConstraints)) {
                        allCardsThatMeetReqIndex.add(i);
                        allCardsThatMeetReqType.add('B');
                    } else if (usableCards.get(i).getAlpha().meetsMinimumConstraints(allConstraints)) {
                        allCardsThatMeetReqIndex.add(i);
                        allCardsThatMeetReqType.add('A');
                    }
                }


            }

            if (allCardsThatMeetReqIndex.isEmpty()) {
                return null;
            }

            int randomIndex = (int)Math.floor(Math.random() * allCardsThatMeetReqIndex.size());

            int indexOfAllDualCards = allCardsThatMeetReqIndex.get(randomIndex);
            DualCard returnedDC = usableCards.remove(indexOfAllDualCards);

            if (allCardsThatMeetReqType.get(randomIndex).equals('B')) {
                return returnedDC.getBeta();
            }
            return returnedDC.getAlpha();

        }
        return null;
    }

    /**
     * returns a boolean that contain the cardinal direction ['u', 'r' 'd' 'l'] stating whether there is an entrance
     * on that side of the spot
     * @param coor - the coordinates of the desired information
     * @param tileMap - the tile map of the dungeon
     * @return an array of length 4 that contain whether the assoicated direction has an entrance (the ordering is
     *         [up, right, down, left]
     */
    private static boolean[] getAllNeighborsThatExist(Point coor, CardDisplay[][] tileMap) {
        boolean[] returnBooleanArray = new boolean[4];
        int x = coor.x;
        int y = coor.y;

        if (isBounded(tileMap, x, y + 1) && (tileMap[y + 1][x] != null)) {
            DualCard neighborDualCard = allCards.get(tileMap[y+1][x].getPureName());
            Card neighborCard;

            if (tileMap[y+1][x].getType() == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y+1][x].getDirection());
            returnBooleanArray[2] = globalValuesAfterReturn[0];
        }
        if (isBounded(tileMap, x, y - 1) && (tileMap[y - 1][x] != null)) {
            DualCard neighborDualCard = allCards.get(tileMap[y-1][x].getPureName());
            Card neighborCard;

            if (tileMap[y-1][x].getType() == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y-1][x].getDirection());
            returnBooleanArray[0] = globalValuesAfterReturn[2];
        }
        if (isBounded(tileMap, x + 1, y) && (tileMap[y][x + 1] != null)) {
            DualCard neighborDualCard = allCards.get(tileMap[y][x+1].getPureName());
            Card neighborCard;

            if (tileMap[y][x+1].getType() == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y][x+1].getDirection());
            returnBooleanArray[1] = globalValuesAfterReturn[3];
        }
        if (isBounded(tileMap, x - 1, y) && tileMap[y][x - 1] != null) {
            DualCard neighborDualCard = allCards.get(tileMap[y][x-1].getPureName());
            Card neighborCard;
            if (tileMap[y][x-1].getType() == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y][x-1].getDirection());
            returnBooleanArray[3] = globalValuesAfterReturn[1];
        }

        return returnBooleanArray;
    }


    /**
     * returns a boolean stating whether a coordinate is in bounds
     */
    private static boolean isBounded(CardDisplay[][] tileMap, int x, int y) {
        return y >= 0 && y < tileMap.length && x >= 0 && x < tileMap[0].length;
    }

    /**
     * returns a boolean stating whether a coordinate is in bounds
     */
    private static boolean isBounded(Character[][] tileMap, int x, int y) {
        return y >= 0 && y < tileMap.length && x >= 0 && x < tileMap[0].length;
    }

    /**
     * Returns a boolean array of the pathways a card needs to have, starting from a relative rotaion of the
     * head, to a global rotation (the card now facing up).
     * To be used in conjunction when creating a list of cards that are able to be places in the dungeon with both
     * adjacent neighbors and placing this potential card rotated.
     * @param newDirOfCard - The current relative rotation of the potential card
     * @param direction - An array of length 4 contain whether there are pathways in said direction, relative to the
     *                    card in place (NOT globally)
     * @return a boolean array of length 4 with the global rotation (upwards) of needed open pathways for the card
     */
    private static boolean[] globalDirectionsGivenDirectionInverse(char newDirOfCard, boolean[] direction) {
        boolean[] returnDirection = new boolean[]{direction[0], direction[1], direction[2], direction[3]};

        if (newDirOfCard == 'l') {
            rightShift(returnDirection);
            return returnDirection;
        }
        if (newDirOfCard == 'r') {
            leftShift(returnDirection);
            return returnDirection;
        }
        if (newDirOfCard == 'd') {
            rightShift(returnDirection);
            rightShift(returnDirection);
            return returnDirection;
        }

        return returnDirection;
    }

    /**
     * takes the boolean array of pathways, and "rotates" the card to the left by 90 degrees.
     * For example, a card with sole pathway leading upwards would now have this pathway leading left, as the card
     * has now "rotated" 90 degrees to the left
     * @param returnDirection the pathways that are open [up, right, down, left]
     */
    private static void leftShift(boolean[] returnDirection) {
        boolean firstValue = returnDirection[0];

        for (int i = 1; i < returnDirection.length; i++) {
            returnDirection[i-1] = returnDirection[i];
        }

        returnDirection[returnDirection.length - 1] = firstValue;
    }

    /**
     * takes the boolean array of pathways, and "rotates" the card to the right by 90 degrees.
     * For example, a card with sole pathway leading upwards would now have this pathway leading right, as the card
     * has now "rotated" 90 degrees to the right
     * @param returnDirection the pathways that are open [up, right, down, left]
     */
    private static void rightShift(boolean[] returnDirection) {
        boolean lastValue = returnDirection[returnDirection.length - 1];

        for (int i = returnDirection.length - 1; i >= 1; i--) {
            returnDirection[i] = returnDirection[i-1];
        }

        returnDirection[0] = lastValue;
    }

    /**
     * Takes a given position on the tile dungeon map, and adds the positions adjacent to this spot to possibly add to
     * the dungeon, assuming that there are no cards already in this spot
     * @param coorToAdd - The stack of positions in which to add cards to generate the dungeon
     * @param x         - the x coordinate of the center position to which all adjacent spots will be added to the stack of
     *      *             coordinates for the dungeon
     * @param y         - the y coordinate of the center position to which all adjacent spots will be added to the stack of
     *      *             coordinates for the dungeon
     * @param tileMap   - the tile map of the dungeon
     * @param direction - the relative direction of pathways for the card at position x, y
     */
    public static void addCoorNeighborsToStack(Queue<PointDir> coorToAdd, int x, int y, CardDisplay[][] tileMap, boolean[] direction) {

        if(Math.random() > (1 - oddsOfTileSpawn) && direction[1] && isBounded(tileMap, x + 1, y) && tileMap[y][x + 1] == null) {
            coorToAdd.add(new PointDir(new Point(x + 1, y), 'r'));
        }
        if(Math.random() > (1 - oddsOfTileSpawn) && direction[3] && isBounded(tileMap, x - 1, y) && tileMap[y][x - 1] == null) {
            coorToAdd.add(new PointDir(new Point(x - 1, y), 'l'));
        }
        if(Math.random() > (1 - oddsOfTileSpawn) && direction[2] && isBounded(tileMap, x, y + 1) && tileMap[y + 1][x] == null) {
            coorToAdd.add(new PointDir(new Point(x, y + 1), 'd'));
        }
        if(Math.random() > (1 - oddsOfTileSpawn) && direction[0] && isBounded(tileMap, x, y - 1) && tileMap[y - 1][x] == null) {
            coorToAdd.add(new PointDir(new Point(x, y - 1), 'u'));
        }


    }

    /**
     * Takes a complete, generated tilemap, and lets the user explore the map, one room at a time from the starting position
     * until the boss room is found, or the user has died
     * @param completeTileMap - the generate map
     * @param consoleInput - user input
     * @param bossRoom - The coordinates of the boss room
     */
    private static void exploreMap(CardDisplay[][] completeTileMap, Scanner consoleInput, Point bossRoom) {
        Map<String, Point> nameToCoor = new HashMap<>();
        Set<Point> discoveredPlaces = new HashSet<>();
        Character[][] currentExploration = new Character[completeTileMap.length][completeTileMap[0].length];

        addChanceEventsHardCoded();

        Point startPos = new Point((currentExploration[0].length / 2), (currentExploration.length / 2));
        discoveredPlaces.add(startPos);

        nameToCoor.put(completeTileMap[startPos.y][startPos.x].getName(), startPos);
        currentExploration[startPos.y][startPos.x] = 'D';
        while (!discoveredPlaces.contains(bossRoom)) {
            displayCurrentMapKnowledge(currentExploration, completeTileMap);
            if (discoveredPlaces.size() > 1) {
                displayNewRoomEvent(false);
            }
            int currentKnowledgeSize = discoveredPlaces.size();
            while (currentKnowledgeSize == discoveredPlaces.size()) {
                System.out.print("What is the name of the room from which you want to explore? ");
                String roomName = consoleInput.next();
                if (nameToCoor.containsKey(roomName)) {
                    System.out.print("What global direction, relative to the previous room, is your new room (possible directions: u, d, l, r)? ");
                    String relativePos = consoleInput.next();
                    Point coor = nameToCoor.get(roomName);
                    if (relativePos.equals("u")) {
                        if (isBounded(completeTileMap, coor.x, coor.y - 1) && completeTileMap[coor.y - 1][coor.x] != null && !nameToCoor.containsKey(completeTileMap[coor.y - 1][coor.x].getPureName())) {
                            Point newRoom = new Point(coor.x,coor.y  - 1);
                            currentExploration[newRoom.y][newRoom.x] = 'D';
                            discoveredPlaces.add(newRoom);
                            nameToCoor.put(completeTileMap[coor.y - 1][coor.x].getName(), newRoom);
                        } else {
                            System.out.println("Room cannot be added");
                        }
                    } else if (relativePos.equals("d")) {
                        if (isBounded(completeTileMap, coor.x, coor.y + 1) && completeTileMap[coor.y + 1][coor.x] != null && !nameToCoor.containsKey(completeTileMap[coor.y + 1][coor.x].getPureName())) {
                            Point newRoom = new Point(coor.x,coor.y + 1);
                            currentExploration[newRoom.y][newRoom.x] = 'D';
                            discoveredPlaces.add(newRoom);
                            nameToCoor.put(completeTileMap[coor.y + 1][coor.x].getName(), newRoom);
                        } else {
                            System.out.println("Room cannot be added");
                        }
                    } else if (relativePos.equals("l")) {
                        if (isBounded(completeTileMap, coor.x - 1, coor.y) && completeTileMap[coor.y][coor.x - 1] != null && !nameToCoor.containsKey(completeTileMap[coor.y][coor.x - 1].getPureName())) {
                            Point newRoom = new Point(coor.x - 1,coor.y);
                            currentExploration[newRoom.y][newRoom.x] = 'D';
                            discoveredPlaces.add(newRoom);
                            nameToCoor.put(completeTileMap[coor.y][coor.x - 1].getName(), newRoom);
                        } else {
                            System.out.println("Room cannot be added");
                        }
                    } else if (relativePos.equals("r")) {
                        if (isBounded(completeTileMap, coor.x + 1, coor.y) && completeTileMap[coor.y][coor.x + 1] != null && !nameToCoor.containsKey(completeTileMap[coor.y][coor.x + 1].getPureName())) {
                            Point newRoom = new Point(coor.x + 1,coor.y);
                            currentExploration[newRoom.y][newRoom.x] = 'D';
                            discoveredPlaces.add(newRoom);
                            nameToCoor.put(completeTileMap[coor.y][coor.x + 1].getName(), newRoom);
                        } else {
                            System.out.println("Room cannot be added");
                        }
                    } else {
                        System.out.println("Error: An unacceptable input was provided");
                        System.out.println();
                    }
                } else {
                    System.out.println("Error: Room Name doesn't exist.");
                    System.out.println();
                }
            }
        }

        displayCurrentMapKnowledge(currentExploration, completeTileMap);

        System.out.println();
        System.out.println();
        displayNewRoomEvent(true);
    }

    /**
     * Displays the current explored map
     * @param currentMapKnowledge - A 2d "tilemap" of relative places explored, and neighbor paths
     * @param completeTileMap  - The complete tile map
     */
    private static void displayCurrentMapKnowledge(Character[][] currentMapKnowledge, CardDisplay[][] completeTileMap) {
        for (int y = 0; y < currentMapKnowledge.length; y++) {
            for (int x = 0; x < currentMapKnowledge[y].length; x++) {
                if (currentMapKnowledge[y][x] != null && currentMapKnowledge[y][x] == 'D') {
                    System.out.print(completeTileMap[y][x] + " ");
                } else if (completeTileMap[y][x] != null && nextToDiscoveredRoom(x, y, currentMapKnowledge)) {
                    System.out.print("[   ?   ]");
                } else {
                    System.out.print("         ");
                }

            }
            System.out.println();
        }
        System.out.println();
        System.out.println("====================");
        System.out.println();
    }

    /**
     * returns a boolean determining whether the user is next to a room that has been discovered
     * @param x - the x position of the room to check
     * @param y - the y position of the room to check
     * @param currentMapKnowledge - current map of explored and adjacent tiles
     * @return
     */
    private static boolean nextToDiscoveredRoom(int x, int y, Character[][] currentMapKnowledge) {
        if (isBounded(currentMapKnowledge, x + 1, y) && currentMapKnowledge[y][x + 1] != null && currentMapKnowledge[y][x + 1] == 'D') {
            return true;
        }

        if (isBounded(currentMapKnowledge, x - 1, y) && currentMapKnowledge[y][x - 1] != null && currentMapKnowledge[y][x - 1] == 'D') {
            return true;
        }

        if (isBounded(currentMapKnowledge, x, y + 1) && currentMapKnowledge[y + 1][x] != null && currentMapKnowledge[y + 1][x] == 'D') {
            return true;
        }

        if (isBounded(currentMapKnowledge, x, y - 1) && currentMapKnowledge[y - 1][x] != null && currentMapKnowledge[y - 1][x] == 'D') {
            return true;
        }


        return false;
    }
}