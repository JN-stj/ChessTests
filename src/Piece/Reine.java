
package Piece;

import jpchess.Board;
import jpchess.Type;


public class Reine extends Piece {
    
    public Reine(int color, int col, int row) {
        super(color, col, row);
        
        type = Type.REINE;
        
         if(color == Board.WHITE) {
            image = getImage("/res/ReineBlanc");
        }
        else {
            image = getImage("/res/ReineNoir");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol, targetRow) == false){
            
            if(targetCol == preCol || targetRow == preRow) {
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                    return true;
                }
            }
    }
        return false;
}
}