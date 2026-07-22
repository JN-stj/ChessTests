
package Piece;

import jpchess.Board;
import jpchess.Type;


public class Cavalier extends Piece {
    
    public Cavalier(int color, int col, int row) {
        super(color, col, row);
        
        type = Type.CAVALIER;
        
         if(color == Board.WHITE) {
            image = getImage("/res/CavalierBlanc");
        }
        else {
            image = getImage("/res/CavalierNoir");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        
        if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
            if(isValidSquare(targetCol, targetRow)){
                return true;
            }
        }
        return false;
    }
    
}
