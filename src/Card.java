public class Card {

    private final String name;
    private final char type;
    // t r b l
    private final boolean[] direction;


    public Card(String name, boolean up, boolean left, boolean right, char t) {
        this.name = name;
        this.direction = new boolean[]{up, right, true, left};
        this.type = t;
    }




    public boolean[] globalDirectionsGivenDirection(char newDirOfCard) {
        boolean[] returnDirection = new boolean[]{direction[0], direction[1], direction[2], direction[3]};

        if (newDirOfCard == 'l') {
            leftShift(returnDirection);
            return returnDirection;
        }
        if (newDirOfCard == 'r') {
            rightShift(returnDirection);
            return returnDirection;
        }
        if (newDirOfCard == 'd') {
            leftShift(returnDirection);
            leftShift(returnDirection);
            return returnDirection;
        }

        return returnDirection;
    }


    public boolean meetsMinimumConstraints(boolean[] requirements) {
        for (int i = 0; i < requirements.length; i++) {
            if (requirements[i] && !direction[i]) {
                return false;
            }
        }

        return true;
    }
    private void leftShift(boolean[] returnDirection) {
        boolean firstValue = returnDirection[0];

        for (int i = 1; i < returnDirection.length; i++) {
            returnDirection[i-1] = returnDirection[i];
        }

        returnDirection[returnDirection.length - 1] = firstValue;
    }

    private void rightShift(boolean[] returnDirection) {
        boolean lastValue = returnDirection[returnDirection.length - 1];

        for (int i = returnDirection.length - 1; i >= 1; i--) {
            returnDirection[i] = returnDirection[i-1];
        }

        returnDirection[0] = lastValue;
    }

    public char getType(){
        return type;
    }

    public String getName() { return name;}

}

