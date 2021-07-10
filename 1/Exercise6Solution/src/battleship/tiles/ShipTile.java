package battleship.tiles;

import battleship.players.Player;
import battleship.ships.Ship;
import battleship.utilities.Coordinates;

/**
 * Ship tiles function exactly like normal tiles, except for containing 
 * a reference to a ship.
 */
public class ShipTile extends Tile {
    private Ship ship;

    public ShipTile (int horizontalIndex, int verticalIndex, Ship ship, Fleet fleet) {
        this(new Coordinates(horizontalIndex, verticalIndex), ship, fleet);
    }

    public ShipTile(Coordinates coordinates, Ship ship, Fleet fleet) {
        super(coordinates, fleet);
        this.ship = ship;
        this.ship.associateWithTile(this);
    }

    @Override
    
    public void fireAtThisTile(Player observer) {
        if (ship.isSunk()) {
            observer.youKeepFiringAtASunkShip(this.getCoordinates(), ship);
            getPlayer().theEnemyKeepsFiringAtASunkShip(this.getCoordinates(), ship);
        } else if (hasBeenFiredUpon) {
            observer.youKeepFiringAtTheSameHole(getCoordinates());
            System.out.print("DAMNDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
            getPlayer().theEnemyKeepsFiringAtTheSameHole(this.getCoordinates(), ship);
        } else {
            markAsHavingBeenFiredUpon();
            if (ship.isSunk()) {
                observer.youHaveSunkAnEnemyShip(this.getCoordinates(), ship);
                getPlayer().yourShipHasBeenSunk(this.getCoordinates(), ship);
            } else {
                observer.youHaveHitYourTarget(this.getCoordinates());
                getPlayer().youHaveBeenHit(this.getCoordinates(), ship);
            }
        }
    }

    @Override
    String getVisualRepresentation(boolean showShips) {
        if (hasBeenFiredUpon()) {
            return "X";
        } else if (showShips) {
            return ship.getVisualRepresentation();
        } else {
            return super.getVisualRepresentation(showShips); // Passing the parameter actually does nothing, but including it in the declaration of the base function was necessary to make it available in this overriding function.
        }
    }

    @Override
    public Ship getShip() {
        return this.ship;
    }
}
