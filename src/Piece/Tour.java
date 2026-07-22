
package Piece;

import jpchess.Board;
import jpchess.Type;


public class Tour extends Piece {
    
    public Tour(int color, int col, int row) {
        super(color, col, row);
        
        type = Type.TOUR;
        
         if(color == Board.WHITE) {
            image = getImage("/res/TourBlanc");
        }
        else {
            image = getImage("/res/TourNoir");
        }
    }
    public boolean canMove(int targetCol, int targetRow)  {
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
        if(targetCol == preCol || targetRow == preRow) {
            if(isValidSquare(targetCol,targetRow) && pieceIsOnStraightLine(targetCol,targetRow)== false) {
                return true;
            }
            
        }
    }
        return false;
}
}