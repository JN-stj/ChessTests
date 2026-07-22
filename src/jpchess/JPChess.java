
package jpchess;

import javax.swing.*;



public class JPChess {

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("A Chess Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        Board b = new Board();
        frame.add(b);
        frame.pack();
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        b.launchGame();
    }

}



