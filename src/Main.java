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



        usableCards = new ArrayList<>();
        allCards = new HashMap<>();
        CardDisplay[][] tileMap = new CardDisplay[10][10];
        Queue<PointDir> coorToAdd = new ArrayDeque<>();
        addCardHardCoded();
        primeTheWhileLoop(coorToAdd, tileMap);
        counter = 1;
        lastCardAdded = new Point((tileMap[0].length / 2), (tileMap.length / 2));

        while ((!isTileLimit || (isTileLimit && counter < tileLimit)) && !coorToAdd.isEmpty() && !usableCards.isEmpty()) {
            PointDir thisPointDir = coorToAdd.remove();
            Point coor = thisPointDir.coor;
            char directionOfNewCard = thisPointDir.direction;

            addCardToRoom(coor, coorToAdd, tileMap, directionOfNewCard);
        }
        if (gameMode == 1 || gameMode == 3) {
            display2dArray(tileMap);
        }

        if (gameMode == 2 || gameMode == 3) {
            exploreMap(tileMap, consoleInput, lastCardAdded);
        }
    }

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
                        if (isBounded(completeTileMap, coor.x, coor.y - 1) && completeTileMap[coor.y - 1][coor.x] != null && !nameToCoor.containsKey(completeTileMap[coor.y - 1][coor.x].name)) {
                            Point newRoom = new Point(coor.x,coor.y  - 1);
                            currentExploration[newRoom.y][newRoom.x] = 'D';
                            discoveredPlaces.add(newRoom);
                            nameToCoor.put(completeTileMap[coor.y - 1][coor.x].getName(), newRoom);
                        } else {
                            System.out.println("Room cannot be added");
                        }
                    } else if (relativePos.equals("d")) {
                        if (isBounded(completeTileMap, coor.x, coor.y + 1) && completeTileMap[coor.y + 1][coor.x] != null && !nameToCoor.containsKey(completeTileMap[coor.y + 1][coor.x].name)) {
                            Point newRoom = new Point(coor.x,coor.y + 1);
                            currentExploration[newRoom.y][newRoom.x] = 'D';
                            discoveredPlaces.add(newRoom);
                            nameToCoor.put(completeTileMap[coor.y + 1][coor.x].getName(), newRoom);
                        } else {
                            System.out.println("Room cannot be added");
                        }
                    } else if (relativePos.equals("l")) {
                        if (isBounded(completeTileMap, coor.x - 1, coor.y) && completeTileMap[coor.y][coor.x - 1] != null && !nameToCoor.containsKey(completeTileMap[coor.y][coor.x - 1].name)) {
                            Point newRoom = new Point(coor.x - 1,coor.y);
                            currentExploration[newRoom.y][newRoom.x] = 'D';
                            discoveredPlaces.add(newRoom);
                            nameToCoor.put(completeTileMap[coor.y][coor.x - 1].getName(), newRoom);
                        } else {
                            System.out.println("Room cannot be added");
                        }
                    } else if (relativePos.equals("r")) {
                        if (isBounded(completeTileMap, coor.x + 1, coor.y) && completeTileMap[coor.y][coor.x + 1] != null && !nameToCoor.containsKey(completeTileMap[coor.y][coor.x + 1].name)) {
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

    // returns a bell curve between zero and one
    private static double bellCurve() {
        double sum = 0.0;
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            sum += r.nextDouble();
        }
        return (sum / 10.0);
    }

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


    /**
     * Adds Cards to global set
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
            allCards.put(usableCards.get(i).getAlpha().name, usableCards.get(i));
        }
    }


    /**
     * Adds a card to the dungeon
     * @param coor - What are the coordinates of the new room?
     * @param coorToAdd - the stack of coordinates to add to (contains coordinate and direction of card)
     * @param tileMap - the tile map
     * @param directionOfHead - the direction of the new cards to be added 'u' 'd' 'l' or 'r'
     */
    private static void addCardToRoom(Point coor, Queue<PointDir> coorToAdd, CardDisplay[][] tileMap, char directionOfHead) {
        Card cardToAdd = isPlaceable(coor, tileMap, directionOfHead);

        if (cardToAdd != null) {
            counter++;
            tileMap[coor.y][coor.x] = new CardDisplay(cardToAdd.name, directionOfHead, cardToAdd.getType());
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
     * Prepares the while loop by adding the starting room, and any new entrances
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

        tileMap[coor.y][coor.x] = new CardDisplay(whichRoom.name, 'u', whichRoom.getType());
        addCoorNeighborsToStack(coorToAdd, coor.x, coor.y, tileMap, whichRoom.direction);
    }

    /**
     * Returns a Card with given rotation and direction that would be placeable at the given coordinates coor, if not
     * possilbe then return null
     * @param coor
     * @param tileMap
     * @param directionOfHead
     * @return
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
     * returns a boolean that contain the cardinal direciton ['u', 'r' 'd' 'l'] stating whether there is a entrance
     * on that side of the spot
     * @param coor
     * @param tileMap
     * @return
     */
    private static boolean[] getAllNeighborsThatExist(Point coor, CardDisplay[][] tileMap) {
        boolean[] returnBooleanArray = new boolean[4];
        int x = coor.x;
        int y = coor.y;

        if (isBounded(tileMap, x, y + 1) && (tileMap[y + 1][x] != null)) {
            DualCard neighborDualCard = allCards.get(tileMap[y+1][x].name);
            Card neighborCard;

            if (tileMap[y+1][x].type == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            //Card neighborCard = allCards.get(tileMap[y+1][x].name);
            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y+1][x].direction);
            returnBooleanArray[2] = globalValuesAfterReturn[0];
        }
        if (isBounded(tileMap, x, y - 1) && (tileMap[y - 1][x] != null)) {
            DualCard neighborDualCard = allCards.get(tileMap[y-1][x].name);
            Card neighborCard;

            if (tileMap[y-1][x].type == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y-1][x].direction);
            returnBooleanArray[0] = globalValuesAfterReturn[2];
        }
        if (isBounded(tileMap, x + 1, y) && (tileMap[y][x + 1] != null)) {
            DualCard neighborDualCard = allCards.get(tileMap[y][x+1].name);
            Card neighborCard;

            if (tileMap[y][x+1].type == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y][x+1].direction);
            returnBooleanArray[1] = globalValuesAfterReturn[3];
        }
        if (isBounded(tileMap, x - 1, y) && tileMap[y][x - 1] != null) {
            DualCard neighborDualCard = allCards.get(tileMap[y][x-1].name);
            Card neighborCard;
            if (tileMap[y][x-1].type == 'A') {
                neighborCard = neighborDualCard.getAlpha();
            } else {
                neighborCard = neighborDualCard.getBeta();
            }

            boolean[] globalValuesAfterReturn = neighborCard.globalDirectionsGivenDirection(tileMap[y][x-1].direction);
            returnBooleanArray[3] = globalValuesAfterReturn[1];
        }

        return returnBooleanArray;
    }


    /**
     * returns a boolean stating whether a position is in bounds
     */
    private static boolean isBounded(CardDisplay[][] tileMap, int x, int y) {
        return y >= 0 && y < tileMap.length && x >= 0 && x < tileMap[0].length;
    }

    private static boolean isBounded(Character[][] tileMap, int x, int y) {
        return y >= 0 && y < tileMap.length && x >= 0 && x < tileMap[0].length;
    }

    /**
     *
     * @param newDirOfCard
     * @param direction
     * @return
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

    private static void leftShift(boolean[] returnDirection) {
        boolean firstValue = returnDirection[0];

        for (int i = 1; i < returnDirection.length; i++) {
            returnDirection[i-1] = returnDirection[i];
        }

        returnDirection[returnDirection.length - 1] = firstValue;
    }

    private static void rightShift(boolean[] returnDirection) {
        boolean lastValue = returnDirection[returnDirection.length - 1];

        for (int i = returnDirection.length - 1; i >= 1; i--) {
            returnDirection[i] = returnDirection[i-1];
        }

        returnDirection[0] = lastValue;
    }



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
}