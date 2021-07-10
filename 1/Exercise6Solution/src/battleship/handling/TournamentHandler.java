package battleship.handling;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import battleship.players.Player;
import battleship.utilities.Coordinates;
import battleship.utilities.Input;
import battleship.utilities.StringManipulation;

public class TournamentHandler {

    private static Constructor<?> constructorForOpponent1;
    private static Constructor<?> constructorForOpponent2;
    private static String opponent1Name = "The Spanish Armada";
    private static String opponent2Name = "Perfidious Albion";
    // we swap the beginning team with each round to avoid an advantage for the beginning player
    private static boolean  swapBeginningTeam = true;
    private static Player	playerOne;
    private static Player	playerTwo;
    // used to access the statistics maps as the objects are freshly initialized each round, but the strings are stable
    private static String playerOneKey;
    private static String playerTwoKey;
    private static boolean	gameIsOver;

    private static boolean tournamentStatisticsInitialized = false;
    private static boolean waitAfterEveryTournamentRound = false;
    private static int tournamentRound = 0;
    private static int tournamentRoundsCancelled = 0;
    private static Map<String, Integer> tournamentVictories = new HashMap<String, Integer>();
    private static Map<String, Integer> tournamentShotsFired = new HashMap<String, Integer>();
    private static Map<String, Integer> tournamentShotsHit = new HashMap<String, Integer>();

    /**
     * If your console output is too wide, you can do any of the following:
     * 
     * 1. Right-click the console, click "Preferences", or
     * 2. (Windows) Go through Window -> Preferences -> Run/Debug -> Console, or
     * 3. (Mac) Go through Preferences -> Run/Debug:
     * 
     * Lower "Displayed tab width". A value of 2 is recommended. 	 
     */

    /**
     * Initializes the game and runs it until one player wins.
     * 
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {

        // ------------------------------------------------
        // Prepare utilities.
        // ------------------------------------------------

        Input.initialize();
        
        // ------------------------------------------------
        // Offer a dialogue that lets the user select the number of players,
        // and whether the game should wait for input before starting a new turn. 
        // ------------------------------------------------
        
        
        System.out.println("Do you want see the final playing field for each tournament round before continuing by pressing RETURN? (true/false)");
        waitAfterEveryTournamentRound = Input.nextBoolean();
        Input.nextLine(); // eat up the line end after the boolean value

        // We use Java reflection to construct a class and class instance 
        // from a user entered string, this has not been covered in class 
        do {
            System.out.println("Enter a class name for player 1 or RETURN for the default (CustomComputerOpponent):");
            String clazzName = Input.nextLine();
            if (clazzName == null || clazzName.trim().isEmpty()) {
                clazzName = "battleship.players.CustomComputerOpponent"; 
            } else {
                clazzName = "battleship.players." + clazzName;
                System.out.println("Enter a name (String) for player 1:");
                opponent1Name = Input.nextLine();                
            }
            try {
                Class<?> clazz = Class.forName(clazzName);
                constructorForOpponent1 = clazz.getConstructor(String.class);
                playerOne = (Player)constructorForOpponent1.newInstance(new Object[] { opponent1Name });
            } catch (ClassNotFoundException e) {
                // err and ask user again
                System.err.println("Class " + clazzName + " could not be loaded! ");
                System.err.println(e.getMessage());
            }
        } while (playerOne == null);

        do {
            System.out.println("Enter a class name for player 2 or RETURN for the default (CustomComputerOpponent):");
            String clazzName = Input.nextLine();
            if (clazzName == null || clazzName.trim().isEmpty()) {
                clazzName = "battleship.players.CustomComputerOpponent"; 
            } else {
                clazzName = "battleship.players." + clazzName;
                System.out.println("Enter a name (String) for player 2:");
                opponent2Name = Input.nextLine();                
            }
            try {
                Class<?> clazz = Class.forName(clazzName);
                constructorForOpponent2 = clazz.getConstructor(String.class);
                playerTwo = (Player)constructorForOpponent2.newInstance(new Object[] { opponent2Name });
            } catch (ClassNotFoundException e) {
                // err and ask user again
                System.err.println("Class " + clazzName + " could not be loaded! ");
                System.err.println(e.getMessage());
            }
        } while (playerTwo == null);
        runTournament();
    }

    private static void runTournament () throws Exception {
        for (int i = 0; i < 100; i++) {
            runOneTournamentRound();
            if (waitAfterEveryTournamentRound) {
                printTournamentState();
                Input.waitForAnyInput();
            }
        }

        printTournamentState();
        String winner;
        if (tournamentVictories.get(playerOneKey) == tournamentVictories.get(playerTwoKey))
            winner = null;
        else 
            winner = (tournamentVictories.get(playerOneKey) > tournamentVictories.get(playerTwoKey)) ? playerOneKey : playerTwoKey;

        drawAFancyLine(60, 0, 1);
        if (winner == null) {
            System.out.println(StringManipulation.TAB + StringManipulation.TAB + "It's a draw!");
        } else {
            System.out.println(StringManipulation.TAB + StringManipulation.TAB + "This makes " + winner + " the winner!");
        }
        drawAFancyLine(60, 0, 1);
    }

    private static void printTournamentState() {
        String playerOneKey = playerOne.toString();
        String playerTwoKey = playerTwo.toString();

        drawAFancyLine();

        System.out.println(playerOne + " VS " + playerTwo + "! Tournament round " + tournamentRound + (tournamentRoundsCancelled > 0 ? " (" + tournamentRoundsCancelled + " cancelled)" : "") + ".");
        System.out.println("Victories: " + StringManipulation.TAB + tournamentVictories.get(playerOneKey) + " to " + tournamentVictories.get(playerTwoKey) + ".");
        System.out.println("Shots fired: " + StringManipulation.TAB + tournamentShotsFired.get(playerOneKey) + " to " + tournamentShotsFired.get(playerTwoKey) + "." );
        System.out.println("Shots hit: " + StringManipulation.TAB + tournamentShotsHit.get(playerOneKey) + " to " + tournamentShotsHit.get(playerTwoKey) + "." );

        double playerOneAccuracy = (double) tournamentShotsHit.get(playerOneKey) / (double) tournamentShotsFired.get(playerOneKey);
        double playerTwoAccuracy = (double) tournamentShotsHit.get(playerTwoKey) / (double) tournamentShotsFired.get(playerTwoKey);

        System.out.println("Accuracy: " + Math.round(100 * playerOneAccuracy) + "% to " + Math.round(100 * playerTwoAccuracy) + "%.");

        drawAFancyLine();
    }

    private static void runOneTournamentRound() throws Exception {
        tournamentRound++;
        gameIsOver = false;

        if (swapBeginningTeam) {
            playerOne = (Player)constructorForOpponent1.newInstance(new Object[] { opponent1Name });
            playerTwo = (Player)constructorForOpponent2.newInstance(new Object[] { opponent2Name });
            swapBeginningTeam = false;  
        } else {
            playerTwo = (Player)constructorForOpponent1.newInstance(new Object[] { opponent1Name });
            playerOne = (Player)constructorForOpponent2.newInstance(new Object[] { opponent2Name });
            swapBeginningTeam = true;
        }
        playerOneKey = playerOne.toString();
        playerTwoKey = playerTwo.toString();
        if (!tournamentStatisticsInitialized) {
            // access to the map is by the players' string as the objects change with each run!
            tournamentVictories.put(playerOneKey, 0);
            tournamentVictories.put(playerTwoKey, 0);
            tournamentShotsFired.put(playerOneKey, 0);
            tournamentShotsFired.put(playerTwoKey, 0);
            tournamentShotsHit.put(playerOneKey, 0);
            tournamentShotsHit.put(playerTwoKey, 0);
            tournamentStatisticsInitialized = true;
        }
        String exceptionMessage = "";
        try {
            playerOne.placeShips();
        } catch (Exception e) {
            exceptionMessage = "Tournament round " + tournamentRound + " had to be cancelled. Player " + playerOne + " could not place the ships and looses. ";
            // opponent wins
            tournamentVictories.put(playerTwoKey, tournamentVictories.get(playerTwoKey) + 1);
        }
        try {
            if (exceptionMessage.equals(""))
                playerTwo.placeShips();
        } catch (Exception e) {
            exceptionMessage = "Tournament round " + tournamentRound + " had to be cancelled. Player " + playerTwo + " could not place the ships and looses. ";
            // opponent wins
            tournamentVictories.put(playerOneKey, tournamentVictories.get(playerOneKey) + 1);
        }
        try {
            if (exceptionMessage.equals(""))
                playUntilTheEnd();
        } catch (Exception e) {
            exceptionMessage = "Tournament round " + tournamentRound + " had to be cancelled due to unforseen problems. " + e.getMessage();
            tournamentRoundsCancelled++;
        }
        if (!exceptionMessage.equals("")) {
            drawAFancyLine(60, 0, 1);
            System.out.println(exceptionMessage);
            drawAFancyLine(60, 0 , 1);
            return;
        }

        if (playerOne.isDefeated() && !playerTwo.isDefeated()) 
            tournamentVictories.put(playerTwoKey, tournamentVictories.get(playerTwoKey) + 1);
        else if (playerTwo.isDefeated() && !playerOne.isDefeated())
            tournamentVictories.put(playerOneKey, tournamentVictories.get(playerOneKey) + 1);

        tournamentShotsFired.put(playerOneKey, tournamentShotsFired.get(playerOneKey) + playerOne.getShotsFired());
        tournamentShotsFired.put(playerTwoKey, tournamentShotsFired.get(playerTwoKey) + playerTwo.getShotsFired());
        tournamentShotsHit.put(playerOneKey, tournamentShotsHit.get(playerOneKey) + playerOne.getHits());
        tournamentShotsHit.put(playerTwoKey, tournamentShotsHit.get(playerTwoKey) + playerTwo.getHits());
    }


    // ----------------------------------------------------------------------------------------------------
    // Running the game
    // ----------------------------------------------------------------------------------------------------

    /**
     * Makes the players play turn by turn until either one of them loses.
     */
    private static void playUntilTheEnd() throws Exception {
        System.out.println("Player " + playerOne + " starts...");
        for (int turns = 0; turns < Settings.MAXIMUM_PLAYABLE_TURNS; turns++) {
            if (!gameIsOver)
                playOneTurn();
            else
                return;
        }
        throw new Exception("Maximum number of turns (" + Settings.MAXIMUM_PLAYABLE_TURNS + ") exceeded!");
    }

    /**
     * Makes both players play one turn.
     */
    private static void playOneTurn() throws Exception {		
        playerOne.yourTurnHasBegun();
        openFire(playerOne);
        endTheGameIfEitherPlayerIsDefeated();

        if (!gameIsOver) {
            playerTwo.yourTurnHasBegun();
            openFire(playerTwo);
            endTheGameIfEitherPlayerIsDefeated();
        }
    }

    private static void endTheGameIfEitherPlayerIsDefeated() {
        if (playerOne.isDefeated() || playerTwo.isDefeated()) {
            gameIsOver = true;
            proclaimTheWinner();
        }
    }

    private static void drawTheFleets() {
        // Add empty lines to visually separate the playing field from other messages.
        drawAFancyLine(60, 0, 1);
        System.out.println(
                "Fleet strength: " + playerOne + " at " + playerOne.currentFleetStrength() + "/"
                        + playerOne.originalFleetStrength() + ", " + playerTwo + " at "
                        + playerTwo.currentFleetStrength() + "/" + playerTwo.originalFleetStrength() + ".");
        System.out.println("Player " + playerOne + ": " + playerOne.getAccuracyInformation());
        System.out.println("Player " + playerTwo + ": " + playerTwo.getAccuracyInformation());    
        drawAFancyLine(60, 0, 1);

        String headlinePlayer = playerOne + "'s fleet ";
        for (int i = 0; i < 9; i++)
            headlinePlayer += StringManipulation.TAB;
        headlinePlayer += playerTwo + "'s fleet " ;
        System.out.println(headlinePlayer);

        String headline = StringManipulation.generateFleetRepresentationHeadingLine() 
                + StringManipulation.TAB + StringManipulation.TAB
                + StringManipulation.generateFleetRepresentationHeadingLine();
        System.out.println(headline);

        for (int lineNumber = 0; lineNumber < Settings.PLAYING_FIELD_VERTICAL_SIZE; lineNumber++) {
            String line = playerOne.generateOneLineOfTheVisualFleetRepresentation(lineNumber, true)
                    + StringManipulation.TAB + StringManipulation.TAB
                    + playerTwo.generateOneLineOfTheVisualFleetRepresentation(lineNumber, true);
            System.out.println(line);
        }
    }

    private static void proclaimTheWinner() {
        String message = "";
        if (playerOne.isDefeated() && !playerTwo.isDefeated()) 
            message = "RULE THE WAVES! " + playerTwo + " has won the battle!";
        else if (playerTwo.isDefeated() && !playerOne.isDefeated())
            message = "RULE THE WAVES! " + playerOne + " has won the battle!";
        else if (playerOne.isDefeated() && playerTwo.isDefeated())
            message = "Both fleets have been sunk. The fight is a draw!";
        else if (!playerOne.isDefeated() && !playerTwo.isDefeated())
            message = "Both fleets have depleted their munitions! The fight is a draw!";

        if (waitAfterEveryTournamentRound)
            drawTheFleets();

        drawAFancyLine(60, 0, 1);
        System.out.println(message);
        drawAFancyLine(60, 0, 1);
    }

    public static void drawAFancyLine() {
        drawAFancyLine(60, 1, 1);
    }

    public static void drawAFancyLine(int length, int blankSurroundingLines, int fancyLines) {
        String fancyLine = "";
        for (int i = 0; i < length; i++)
            fancyLine += "~";
        for (int i = 0; i < blankSurroundingLines; i++)
            System.out.println();
        for (int i = 0; i < fancyLines; i++)
            System.out.println(fancyLine);
        for (int i = 0; i < blankSurroundingLines; i++)
            System.out.println();
    }


    // ----------------------------------------------------------------------------------------------------
    // Player Actions
    // ----------------------------------------------------------------------------------------------------

    private static void openFire (Player attacker) throws Exception {
        fireAtCoordinates (attacker, attacker.promptToFireShot());
    }

    private static void fireAtCoordinates(Player attacker, Coordinates coordinates) {
        Player defender = attacker == playerOne ? playerTwo : playerOne;
        defender.receiveFireAtCoordinates(attacker, coordinates);
    }
}
