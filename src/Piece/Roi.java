
package Piece;

import jpchess.Board;
import jpchess.Type;


public class Roi extends Piece {
    
    public Roi(int color, int col, int row) {
        super(color, col, row);
        
        type = Type.ROI;
        
         if(color == Board.WHITE) {
            image = getImage("/res/RoiBlanc");
        }
        else {
            image = getImage("/res/RoiNoir");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        
        if(isWithinBoard(targetCol, targetRow)){
            
            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
                  Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
           
                if(isValidSquare(targetCol, targetRow)){
                  return true;
                }
        } 
         if(moved == false) {   
             
             //right castling
             if(targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine (targetCol, targetRow) == false) {
                 for(Piece piece : Board.simPieces) {
                     if(piece.col == preCol + 3 && piece.row == preRow && piece.moved == false) {
                         Board.castlingP = piece;
                         return true;
                     }
                 }
             }
             //left castling
             if(targetCol == preCol-2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                 Piece p[] = new Piece[2];
                 for(Piece piece : Board.simPieces) {
                     if(piece.col == preCol-3 && piece.row == targetRow) {
                         p[0] = piece;
                     }
                     if(piece.col == preCol-4 && piece.row == targetRow) {
                         p[1] = piece;
                     }
                     
                     if(p[0] == null && p[1] != null && p[1].moved == false) {
                         Board.castlingP = p[1];
                         return true;
                     }
                 }
             }
         }
      }
    return false;
  }
}
