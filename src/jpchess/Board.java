
package jpchess;

import Piece.Cavalier;
import Piece.Fou;
import Piece.Piece;
import Piece.Pion;
import Piece.Reine;
import Piece.Roi;
import Piece.Tour;
import java.awt.Color;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import static jpchess.Type.TOUR;


public class Board extends JPanel implements Runnable {
    
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    ChessBoard board = new ChessBoard();
    Mouse mouse = new Mouse();
    
    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece>promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;
    
    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;
    
    //BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;
    
    public Board() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        
        setPieces();
        copyPieces(pieces, simPieces);
    }
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void setPieces(){
      
        //WHITE TEAM
        
        pieces.add(new Cavalier(WHITE, 6, 7));
        pieces.add(new Cavalier(WHITE, 1, 7));
      pieces.add(new Tour(WHITE, 0, 7));
       pieces.add(new Tour(WHITE, 7, 7));
       pieces.add(new Fou(WHITE, 2, 7));
        pieces.add(new Fou(WHITE, 5, 7));
        pieces.add(new Roi(WHITE, 4, 7));
       pieces.add(new Reine(WHITE, 3, 7));
        pieces.add(new Pion(WHITE, 0, 6));
        pieces.add(new Pion(WHITE, 1, 6));
        pieces.add(new Pion(WHITE, 2, 6));
        pieces.add(new Pion(WHITE, 3, 6));
        pieces.add(new Pion(WHITE, 4, 6));
        pieces.add(new Pion(WHITE, 5, 6));
        pieces.add(new Pion(WHITE, 6, 6));
        pieces.add(new Pion(WHITE, 7, 6));
        
        
         //BLACK TEAM
      
        pieces.add(new Cavalier(BLACK, 6, 0));
        pieces.add(new Cavalier(BLACK, 1, 0));
        pieces.add(new Tour(BLACK, 0, 0));
        pieces.add(new Tour(BLACK, 7, 0));
        pieces.add(new Fou(BLACK, 2, 0));
        pieces.add(new Fou(BLACK, 5, 0));
        pieces.add(new Roi(BLACK, 3, 0));
        pieces.add(new Reine(BLACK, 4, 0));
        pieces.add(new Pion(BLACK, 0, 1));
        pieces.add(new Pion(BLACK, 1, 1));
        pieces.add(new Pion(BLACK, 2, 1));
       pieces.add(new Pion(BLACK, 3, 1));
        pieces.add(new Pion(BLACK, 4, 1));
        pieces.add(new Pion(BLACK, 5, 1));
        pieces.add(new Pion(BLACK, 6, 1));
        pieces.add(new Pion(BLACK, 7, 1));
    }
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }
    
    @Override
    public void run() {
        
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        while (gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;
            
            if (delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
        
    }
    private void update(){
        
        if(promotion) {
           promoting(); 
        }
        else if(gameover == false && stalemate == false){
              ///Mouse Buttons Pressed///
        if(mouse.pressed) {
            if(activeP == null){
               /// If the activeP is null, check if you can pick up a piece
                for (Piece piece : simPieces){
                    //If the mouse is on an ally piece, pick it up as the activeP
                    if(piece.color == currentColor &&
                            piece.col == mouse.x/ChessBoard.SQUARE_SIZE &&
                            piece.row == mouse.y/ChessBoard.SQUARE_SIZE){
                        activeP = piece;
                    }
                }
            }
            else{
                simulate();
            }
        }
        if(mouse.pressed == false){
            if(activeP != null){
                
               if(validSquare) {
                   
                   
                   copyPieces(simPieces, pieces);
                   activeP.updatePosition();
                   if(castlingP != null) {
                       castlingP.updatePosition();
                   }
                   
                   if(isRoiInCheck() && isCheckmate()) {
                      gameover = true;
                   }
                   else if (isStalemate() && isRoiInCheck() == false) {
                       stalemate = true;
                   }
                   else{
                       if(canPromote()) {
                       promotion = true; 
                   
                   }
                      else {
                       
                   
                      changePlayer(); 
                      
                   }
                   }
               }
               else {
                   copyPieces(pieces, simPieces);
                   activeP.resetPosition();
                     activeP = null;
               }
            }
        } 
        }
     
        
        }  
    private void simulate() {
        
        canMove = false;
        validSquare = false;
        
        copyPieces(pieces, simPieces);
        
        if(castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
        
        activeP.x = mouse.x - ChessBoard.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - ChessBoard.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);
        
        // CHeck if the piece is hovering over a reachable square
        if(activeP.canMove(activeP.col, activeP.row)){
            canMove = true;
            
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            if(isIllegal(activeP) == false && opponentCanCaptureRoi() == false) {
            validSquare = true;
        }
    }
    }
    private boolean isIllegal(Piece roi) {
        if(roi.type == Type.ROI) {
            for(Piece piece : simPieces) {
                if(piece != roi && piece.color != roi.color && piece.canMove(roi.col, roi.row)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean opponentCanCaptureRoi() {
        
        Piece roi = getRoi(false);
        
        for(Piece piece : simPieces) {
            if(piece.color != roi.color && piece.canMove(roi.col, roi.row)) {
                return true;
            }
        }
        return false;
    }
    private boolean isRoiInCheck() {
        
        Piece roi = getRoi(true);
        
        if(activeP.canMove(roi.col, roi.row)) {
            checkingP = activeP;
            return true;
        }
        else{
            checkingP = null;
        }
        
        return false;
        
    }
    private Piece getRoi(boolean opponent) {
        
        Piece roi = null;
        
        for(Piece piece : simPieces) {
            if(opponent) {
                if(piece.type == Type.ROI && piece.color != currentColor) {
                   roi = piece; 
                }
            }
            else{
                if(piece.type == Type.ROI && piece.color == currentColor) {
                    roi = piece;
                }
            }
        }
        return roi;
    }
    private boolean isCheckmate() {
        
        Piece roi = getRoi(true);
        
        if(roiCanMove(roi)){
            return false;
        }
        else {
           
            int colDiff = Math.abs(checkingP.col - roi.col);
            int rowDiff = Math.abs(checkingP.row - roi.row);
            
            if(colDiff == 0) {
                
                if(checkingP.row < roi.row) {
                    
                    for(int row = checkingP.row; row < roi.row; row++) {
                        for(Piece piece : simPieces) {
                            if(piece != roi && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.row > roi.row) {
                    
                     for(int row = checkingP.row; row > roi.row; row--) {
                        for(Piece piece : simPieces) {
                            if(piece != roi && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if(rowDiff == 0) {
                
                if(checkingP.col < roi.col) {
                    
                     for(int col = checkingP.col; col < roi.col; col++) {
                        for(Piece piece : simPieces) {
                            if(piece != roi && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.col > roi.col) {
                      for(int col = checkingP.col; col > roi.col; col--) {
                        for(Piece piece : simPieces) {
                            if(piece != roi && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
        }
            else if(colDiff == rowDiff){
                
                if(checkingP.row < roi.row) {
                    
                    if(checkingP.col < roi.col){
                       
                        for(int col = checkingP.col, row = checkingP.row; col < roi.col; col++, row++) {
                            for(Piece piece : simPieces) {
                                if(piece!= roi && piece.color != currentColor && piece.canMove(col,row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > roi.col){
                        
                          for(int col = checkingP.col, row = checkingP.row; col > roi.col; col--, row++) {
                            for(Piece piece : simPieces) {
                                if(piece!= roi && piece.color != currentColor && piece.canMove(col,row)) {
                                    return false;
                                }
                            }
                        }
                }
                }
                if(checkingP.row > roi.row) {
                    
                    if(checkingP.col < roi.col) {
                        
                          for(int col = checkingP.col, row = checkingP.row; col < roi.col; col++, row--) {
                            for(Piece piece : simPieces) {
                                if(piece!= roi && piece.color != currentColor && piece.canMove(col,row)) {
                                    return false;
                                }
                            }
                        }
                    }
                        if(checkingP.col > roi.col) {
                            
                              for(int col = checkingP.col, row = checkingP.row; col > roi.col; col--, row--) {
                            for(Piece piece : simPieces) {
                                if(piece!= roi && piece.color != currentColor && piece.canMove(col,row)) {
                                    return false;
                                }
                            }
                        }
                        }
                    }
                }
        else {
                
                }
            }
        return true;
    }
    private boolean roiCanMove(Piece roi){
    
    if(isValidMove(roi, -1,-1)){return true;}
    if(isValidMove(roi, 0, -1)){return true;}
    if(isValidMove(roi, 1, -1)){return true;}
    if(isValidMove(roi, -1, 0)){return true;}
    if(isValidMove(roi, 1, -0)){return true;}
    if(isValidMove(roi, -1, 1)){return true;}
    if(isValidMove(roi, 0, -1)){return true;}
    if(isValidMove(roi, 1, 1)) {return true;}
        
        return false;
}
    private boolean isValidMove(Piece roi, int colPlus, int rowPlus){
        
        boolean isValidMove = false;
        
        roi.col += colPlus;
        roi.row += rowPlus;
        
        if(roi.canMove(roi.col, roi.row)) {
            
            if(roi.hittingP != null) {
                simPieces.remove(roi.hittingP.getIndex());
            }
            if(isIllegal(roi) == false) {
                isValidMove = true;
            }
        }
        
        roi.resetPosition();
        copyPieces(pieces, simPieces);
        return isValidMove;
        
    }
    private boolean isStalemate() {
        
        int count = 0;
        
        for(Piece piece : simPieces) {
            if(piece.color != currentColor) {
                count++;
            }
        }
        
        if(count ==1) {
            if(roiCanMove(getRoi(true)) == false) {
                return true;
            }
        }
        return false;
    }
    private void checkCastling() {
        
        if(castlingP != null) {
            if(castlingP.col == 0) {
                castlingP.col += 3;
            }
            else if(castlingP.col == 7) {
                castlingP.col -=2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    private void changePlayer() {
        
        if(currentColor == WHITE) {
            currentColor = BLACK;
            
            for(Piece piece : pieces) {
                if(piece.color == BLACK) {
                    piece.twoStepped = false;
                }
            }
         }
        else {
            currentColor = WHITE;
            
            for(Piece piece : pieces) {
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }
    private boolean canPromote() {
        
        if(activeP.type == Type.PION) {
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Tour(currentColor, 9, 2));
                promoPieces.add(new Cavalier(currentColor, 9, 3));
                promoPieces.add(new Fou(currentColor, 9, 4));
                promoPieces.add(new Reine(currentColor, 9, 5));
                return true;
            }
        }
        
        return false;
    }
    
    private void promoting() {
        
        if(mouse.pressed) {
            for(Piece piece : promoPieces) {
                if(piece.col == mouse.x/ChessBoard.SQUARE_SIZE && piece.row == mouse.y/ChessBoard.SQUARE_SIZE) {
                  switch(piece.type) {
                      case TOUR: simPieces.add(new Tour(currentColor, activeP.col, activeP.row));break;
                      case CAVALIER: simPieces.add(new Cavalier(currentColor, activeP.col, activeP.row));break;
                      case FOU: simPieces.add(new Fou(currentColor, activeP.col, activeP.row));break;
                      case REINE: simPieces.add(new Reine(currentColor, activeP.col, activeP.row));break;
                      default: break;
                  } 
                  simPieces.remove(activeP.getIndex());
                  copyPieces(simPieces, pieces);
                  activeP = null;
                  promotion = false;
                  changePlayer();
                }
            }
        }
        
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        board.draw(g2);
        
        for(Piece p : simPieces){
            p.draw(g2);
        }
        
        if(activeP != null){
        if(canMove) {
           if(isIllegal(activeP) ||  opponentCanCaptureRoi()){
              g2.setColor(Color.RED);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(activeP.col*ChessBoard.SQUARE_SIZE, activeP.row*ChessBoard.SQUARE_SIZE,
                ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));  
           }
           else {
            g2.setColor(Color.CYAN);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(activeP.col*ChessBoard.SQUARE_SIZE, activeP.row*ChessBoard.SQUARE_SIZE,
                ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        }
        activeP.draw(g2); 
        
        
        
    }
       g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
       g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
       g2.setColor(Color.white);
       
       if(promotion) {
           g2.drawString("Promote to:", 840, 150);
           for(Piece piece : promoPieces) {
               g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                       ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, null);
           }
       }
       else {
           if(currentColor == WHITE) {
           g2.drawString("White's turn", 840, 550);
           if(checkingP != null && checkingP.color == BLACK) {
               g2.setColor(Color.red);
               g2.drawString("Le Roi", 840, 650);
               g2.drawString("est en échec!", 840, 700);
           }
       }
       else {
           g2.drawString("Black's turn", 840, 250); 
           if(checkingP != null && checkingP.color == WHITE) {
               g2.setColor(Color.red);
               g2.drawString("Le Roi", 840, 100);
               g2.drawString("est en échec!", 840, 150);
       }
       }
       
    }
       
       if(gameover) {
           String s = "";
           if(currentColor == WHITE) {
               s = "Checkmate!! White Wins";
           }
           else {
               s = "Checkmate!! Black Wins";
           }
           g2.setFont(new Font("Arial", Font.PLAIN, 90));
           g2.setColor(Color.green);
           g2.drawString(s, 50, 420);
       }
       if(stalemate) {
           g2.setFont(new Font("Arial", Font.PLAIN, 90));
           g2.setColor(Color.lightGray);
           g2.drawString("Stalemate...", 100, 420);
       }
    }
}
