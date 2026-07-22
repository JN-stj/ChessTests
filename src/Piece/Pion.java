
package Piece;
import jpchess.Board;
import jpchess.Type;

public class Pion extends Piece {
    
    public Pion(int color, int col, int row) {
        super(color, col, row);
        
        type = Type.PION;
        
        if(color == Board.WHITE) {
            image = getImage("/res/PionBlanc");
        }
        else {
            image = getImage("/res/PionNoir");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            
            int moveValue;
            if(color == Board.WHITE) {
                moveValue = -1;
            }
            else {
                moveValue = 1;
            }
            //check hitting piece
            hittingP = getHittingP(targetCol, targetRow);
            // 1 square movement
            if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
                return true;
            }
            // 2 square movement
            if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved == false &&
                    pieceIsOnStraightLine(targetCol, targetRow) == false) {
                return true;
            }
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP !=null &&
                    hittingP.color != color) {
                return true;
            }
            
            //En passant
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
                for(Piece piece : Board.simPieces) {
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped == true) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
