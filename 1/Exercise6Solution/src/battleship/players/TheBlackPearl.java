package battleship.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battleship.handling.Settings;
import battleship.ships.Ship;
import battleship.tiles.Fleet.Orientation;
import battleship.tiles.ShipTile;
import battleship.utilities.Coordinates;

/**
 * This class lets you make your own AI for a computer-controlled player.
 * Read the notes below to find out what you should rewrite.
 */
public class TheBlackPearl extends ComputerOpponent {
	
    //store the tile has been fired or don't need to be fired
    protected List<Coordinates> rejectedCoordinates;
    //store the former firing state to guide the next 
    protected boolean lastFireHitted;
    protected Coordinates lastHitted;
    
    private Orientation layoutOrientation[][];
    private Coordinates layoutCoordinates[][];

    
    /**
     * This is this class' constructor.
     * @param name The player's name.
     */
    public TheBlackPearl(String name) {
        super(name); // This calls the base class' constructor, which calls its base class' constructor. You should keep this line.
        this.lastFireHitted = false;
        this.rejectedCoordinates = new ArrayList<Coordinates>();
        
        this.lastHitted    = new Coordinates(0,0);
        this.layoutOrientation = new Orientation[][] {
        	{Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.VERTICAL,Orientation.HORIZONTAL,Orientation.VERTICAL,
        		Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.VERTICAL,Orientation.HORIZONTAL},
        	{Orientation.HORIZONTAL,Orientation.VERTICAL,Orientation.VERTICAL,Orientation.VERTICAL,Orientation.HORIZONTAL,
            	Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.VERTICAL},
        	{Orientation.VERTICAL,Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.VERTICAL,Orientation.HORIZONTAL,
                	Orientation.HORIZONTAL,Orientation.HORIZONTAL,Orientation.VERTICAL,Orientation.VERTICAL,Orientation.VERTICAL}};
        this.layoutCoordinates = new Coordinates[][] {
        	{new Coordinates(0,6),new Coordinates(0,3),new Coordinates(8,4),new Coordinates(1,0),new Coordinates(5,1),
        		new Coordinates(2,8),new Coordinates(6,0),new Coordinates(8,2),new Coordinates(6,5),new Coordinates(7,9)},
        	{new Coordinates(2,0),new Coordinates(1,1),new Coordinates(5,2),new Coordinates(3,2),new Coordinates(7,2),
            	new Coordinates(1,6),new Coordinates(8,4),new Coordinates(6,6),new Coordinates(1,8),new Coordinates(5,8)},
        	{new Coordinates(9,4),new Coordinates(1,0),new Coordinates(0,9),new Coordinates(6,3),new Coordinates(1,5),
                	new Coordinates(0,7),new Coordinates(1,3),new Coordinates(4,3),new Coordinates(4,6),new Coordinates(6,7)}};
    }

    /**
	 toString() overrides an object's behavior when it is cast to or used as a string.
     */
    @Override
    public String toString() {
        return name + " (Custom AI)";
    }

    // ------------------------------------------------------------------------------------------------
    // In runPlayerTypeSpecificShipPlacement() you can change how the AI places its ships.
    // ------------------------------------------------------------------------------------------------

    /**
     * This method handles the placement of all ships available to this player.
     * It overrides ComputerOpponent's default behavior, which is to place all ships randomly.
     * If you want to keep that behaviour, you can call super.placeShipsRandomly().
     * 
     * If you want to write your own ship placement procedure, these are the rules:
     * 1. You must place all the ships contained in the player's fleet's ship list. You can get this list by calling fleet.getShips().
     * 2. You must place every ship exactly once. You must not place any ship twice, or leave any ships unplaced.
     * 3. Ships may not overlap.
     * 4. Ships must be entirely within the playing field. To get the playing field's extents, get Settings.PLAYING_FIELD_HORIZONTAL_SIZE and Settings.PLAYING_FIELD_VERTICAL_SIZE. 
     *
     * Breaking any of these rules will throw an exception and abort the game.
     * 
     * To actually place a ship, call placeShip(Ship ship, Coordinates coordinates, int length, Orientation orientation).
     * To check whether a spot is free, call fleet.locationIsFree(Coordinates coordinates, int length, Orientation orientation).
     */
    @Override
    public void runPlayerTypeSpecificShipPlacement() throws Exception {
    	int placedShip5 = 0;
    	int placedShip4 = 1;
    	int placedShip3 = 3;
    	int placedShip2 = 6;
    	Random random = new Random();
    	int laylout_th = random.nextInt(3); 
    	Orientation orientation;
    	Coordinates coordinate ;
    	//System.out.print("placing ship!!");
        for(Ship ship :this.fleet.getShips()) {
        	int shipLength = ship.length();
        	System.out.print(shipLength);
        	switch(shipLength){
        	case(2):{
        	    orientation = this.layoutOrientation[laylout_th][placedShip2];
        		coordinate  = this.layoutCoordinates[laylout_th][placedShip2];
        		this.fleet.placeShip(ship, coordinate, shipLength, orientation);
        		placedShip2++;
        		continue;
    		}
    		case(3):{
        		orientation = this.layoutOrientation[laylout_th][placedShip3];
        		coordinate  = this.layoutCoordinates[laylout_th][placedShip3];
        		this.fleet.placeShip(ship, coordinate, shipLength, orientation);
        		placedShip3++;
        		continue;
    		}
    		case(4):{
        		orientation = this.layoutOrientation[laylout_th][placedShip4];
        		coordinate  = this.layoutCoordinates[laylout_th][placedShip4];
        		this.fleet.placeShip(ship, coordinate, shipLength, orientation);
        		placedShip4++;
        		continue;
    		}
    		case(5):{
        		orientation = this.layoutOrientation[laylout_th][placedShip5];
        		coordinate  = this.layoutCoordinates[laylout_th][placedShip5];
        		this.fleet.placeShip(ship, coordinate, shipLength, orientation);
        		placedShip5++;
        		continue;
    		}
    		default:
    			continue;
        	}
        	//System.out.print(orientation);
        	//System.out.print("placing ship");
        }
    }

    // ------------------------------------------------------------------------------------------------
    // In the methods below you can let your AI decide its next move.
    // ------------------------------------------------------------------------------------------------

    /**
     * This method is called whenever your turn begins, but before being prompted to make your shot.
     * You could use it to print some information about your AI's state.
     */
    @Override
    public void yourTurnHasBegun() {
        // TODO Auto-generated method stub
    }

    @Override
    public Coordinates promptToFireShot() throws Exception{
    	
    	Random random = new Random();
        Coordinates coordinates;
        int nearby_x = 0;
        int nearby_y = 0;
		int delta_x[] = {-1,0,1,0};
		int delta_y[] = {0,-1,0,1};
		int jump_index[] = {2,3,0,1};

        List<Coordinates> rejectedCoordinates = new ArrayList<Coordinates>();
             
        for (int attempts = Settings.PLAYING_FIELD_HORIZONTAL_SIZE * Settings.PLAYING_FIELD_VERTICAL_SIZE * 10; attempts > 0; attempts--) {
        	//first test the ship has been hitted but not be shunked
            for(int x=0; x<Settings.PLAYING_FIELD_HORIZONTAL_SIZE; x++) {
            	for(int y=0; y<Settings.PLAYING_FIELD_VERTICAL_SIZE; y++) {
            		if(resultObservations[x][y] == ResultObservation.HIT) {
            			for(int i=0; i<4; i++) {
            				nearby_x = x + delta_x[i];
            				nearby_y = y + delta_y[i];
            				if(Coordinates.coordinateIsWithinPlayingField(nearby_x, nearby_y)) {
            					if(resultObservations[nearby_x][nearby_y] == null) {
            						return new Coordinates(nearby_x, nearby_y);
            					}
            					else if(resultObservations[nearby_x][nearby_y] == ResultObservation.MISS)
            						continue;
            					else if(resultObservations[nearby_x][nearby_y] == ResultObservation.HIT) {
            						int anotherNearby_x = x + delta_x[jump_index[i]];
            						int anotherNearby_y = y + delta_y[jump_index[i]];
            						if(Coordinates.coordinateIsWithinPlayingField(anotherNearby_x, anotherNearby_y) && 
            								resultObservations[anotherNearby_x][anotherNearby_y] == null)
            							return new Coordinates(anotherNearby_x, anotherNearby_y);
            						else
            							break;
            					}
            				}
            			}	
            		}
            	}
            }
            //search randomly in not rejected region and white place
            coordinates = new Coordinates(
                    random.nextInt(Settings.PLAYING_FIELD_HORIZONTAL_SIZE),
                    random.nextInt(Settings.PLAYING_FIELD_VERTICAL_SIZE));
            
            // divide the layout into black-white board
            boolean[][] blackWhiteFleetJudge = new boolean [Settings.PLAYING_FIELD_VERTICAL_SIZE][Settings.PLAYING_FIELD_HORIZONTAL_SIZE];
            for(int rowOfBWF = 0; rowOfBWF < Settings.PLAYING_FIELD_VERTICAL_SIZE; rowOfBWF++ )
            	for(int colOfBWF = 0; colOfBWF < Settings.PLAYING_FIELD_HORIZONTAL_SIZE; colOfBWF++) {
            		if( (rowOfBWF + colOfBWF) %2 != 0 )
            			blackWhiteFleetJudge[rowOfBWF][colOfBWF] = false;
            		else
            			blackWhiteFleetJudge[rowOfBWF][colOfBWF] = true;
            	}
            
            
            boolean coordinatesHaveNotYetBeenFiredUpon = resultObservations[coordinates.x][coordinates.y] == null 
            		&& blackWhiteFleetJudge[coordinates.x][coordinates.y] == true;
            
            boolean fireAtTheseCoordinates = coordinatesHaveNotYetBeenFiredUpon;
            if (!coordinatesHaveNotYetBeenFiredUpon) {
                rejectedCoordinates.add(coordinates);
                attempts--;
            }
            if (fireAtTheseCoordinates)
                return coordinates;
        }
        throw new Exception("Attempts depleted, no firing solution found.");
    	
        //return fireAtRandomTarget();
    }


    // ------------------------------------------------------------------------------------------------
    // The methods below here are called when either player has fired a shot.
    // Use these to see how your strategy is working out, and to adjust it.
    // The calls to base functions (super.something) feed information to the base player class' results observation.
    // You don't have to use it, but be aware:
    // Without calling these base functions at the appropriate times, the default results observation will not work and you have to make your own.
    // ------------------------------------------------------------------------------------------------

    @Override
    public void youHaveBeenMissed(Coordinates coordinates) {
    	// TODO Auto-generated method stub
    }

    @Override
    public void youHaveBeenHit(Coordinates coordinates, Ship ship) {
        // TODO Auto-generated method stub
    }

    @Override
    public void yourShipHasBeenSunk(Coordinates lastHit, Ship ship) {
        // TODO Auto-generated method stub
    }

    @Override
    public void youHaveMissed(Coordinates coordinates) {
        super.youHaveMissed(coordinates);
        if(this.lastFireHitted) {
        	this.lastFireHitted = true;
        }
        else {
        	this.lastFireHitted = false;
        	this.lastHitted = coordinates;
        }        
        this.rejectedCoordinates.add(coordinates);
        System.out.print("###MISS###");
    }

    @Override
    public void youHaveHitYourTarget(Coordinates coordinates) {
        super.youHaveHitYourTarget(coordinates);
        this.lastFireHitted = true;
        this.lastHitted = coordinates;
        this.rejectedCoordinates.add(coordinates);
        System.out.print("+++HIT+++");
    }

    @Override
    public void youHaveSunkAnEnemyShip(Coordinates lastHit, Ship ship) {
        super.youHaveSunkAnEnemyShip(lastHit, ship);
        Coordinates coordinates;
        int nearby_x = 0;
        int nearby_y = 0;
		int delta_x[] = {-1,0,1,0};
		int delta_y[] = {0,-1,0,1};
        rejectedCoordinates.add(lastHit);
        for(ShipTile tile : ship.getTiles()) {
        	int x = tile.getHorizontalIndex();
        	int y = tile.getVerticalIndex();
        	for(int attempts=0; attempts <4 ; attempts++) {
        		nearby_x = x + delta_x[attempts];
        		nearby_y = y + delta_y[attempts];
        		if(Coordinates.coordinateIsWithinPlayingField(nearby_x, nearby_y)) {
    				coordinates = new Coordinates(nearby_x, nearby_y);
	                boolean hasNotBeenRejected = resultObservations[coordinates.x][coordinates.y] == null;    				
	                //boolean hasNotBeenRejected = !rejectedCoordinates.contains(coordinates);
    				if(hasNotBeenRejected)
    					resultObservations[coordinates.x][coordinates.y] = ResultObservation.MISS;
    					//rejectedCoordinates.add(coordinates);
    			}		
        	}
        }
        this.lastFireHitted = false;
        System.out.print("---SUNK---");
    }
        		

    @Override
    public void youKeepFiringAtASunkShip(Coordinates lastHit, Ship ship) {
        // TODO Auto-generated method stub
    }

    @Override
    public void youKeepFiringAtTheSameHole(Coordinates lastHit) {
        // TODO Auto-generated method stub
    }

    @Override
    public void youKeepFiringAtNothing(Coordinates lastHit) {
        // TODO Auto-generated method stub
    }

    @Override
    public void theEnemyKeepsFiringAtASunkShip(Coordinates lastHit, Ship ship) {
        // TODO Auto-generated method stub
    }

    @Override
    public void theEnemyKeepsFiringAtTheSameHole(Coordinates lastHit, Ship ship) {
        // TODO Auto-generated method stub
    }

    @Override
    public void theEnemyKeepsFiringAtNothing(Coordinates lastHit) {
        // TODO Auto-generated method stub
    }
}
