
package Piece;

import jpchess.Board;
import jpchess.Type;


public class Fou extends Piece {
    
    public Fou(int color, int col, int row) {
        super(color, col, row);
        
        type = Type.FOU;
        
         if(color == Board.WHITE) {
            image = getImage("/res/FouBlanc");
        }
        else {
            image = getImage("/res/FouNoir");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
       if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow)== false) {
           
           if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){
               if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                   return true;
               }
           }
       }
       return false;
    }
    
}
