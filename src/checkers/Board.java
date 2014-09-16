/*
 * Welcome to Checkers!
 * 
 * Undo button?
 * Save button?
 * -load button
 * 
 * Made by: Colin King
 * March 7-9, 2013
 */
package checkers;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Board extends JFrame{
    
    private char[][] board;
    private final char TEAMONECHAR = 'X', TEAMONEKING = 'x';
    private final char TEAMTWOCHAR = 'O', TEAMTWOKING = 'o';
    private final char EMPTYSPACE = '-';
    private JFrame window;
    private boolean pieceSelected, turnOne, drawn, captured, canJump;
    private Image image;
    private ArrayList<Integer> moves, mustMove;
    //selected[0 & 1] = x & y of piece to be moved
    //selected[2 & 3] = x & y of space to move to
    private int[] selected;
    private Graphics g;
    private int move, capturesOne, capturesTwo, clicks;
    private final Color DARK = new Color(156, 102, 31);
    private final Color LIGHT = new Color(227, 168, 105);
    private final Color TEAMONECOLOR = Color.RED;
    private final Color TEAMTWOCOLOR = Color.GRAY;
    
    private final int slideWindowY = 60, slideWindowX = 2; 
    /* Change this to change the size of the screen if it doesn't fit
     * precondition: 500 <= blockWidth
     */
    
    private final int blockWidth = 700/8;
    private Image restart, king, exit, checkers;
    
    public Board()
    {
        board = new char[][]{{TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE},
                             {EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR},
                             {TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE},
                             {EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE},
                             {EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE},
                             {EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR},
                             {TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE,},
                             {EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR}};
//        board = new char[][]{{TEAMONECHAR,EMPTYSPACE,TEAMTWOKING,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE},
//                             {EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE},
//                             {EMPTYSPACE,EMPTYSPACE,TEAMTWOKING,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE},
//                             {EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE},
//                             {EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE},
//                             {EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE},
//                             {EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE},
//                             {EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE,EMPTYSPACE}};
        pieceSelected = false;
        drawn = false;
        captured = false;
        turnOne = true;
        moves = new ArrayList<>();
        mustMove = new ArrayList<>();
        selected = new int[4];
        move = capturesOne = capturesTwo = 0;
        try
        {
            checkers = ImageIO.read(new File("checkers.png"));
            restart = ImageIO.read(new File("restart.png"));
            king = ImageIO.read(new File("kingBig.png"));
            exit = ImageIO.read(new File("exit.png"));
        }
        catch(IOException i)
        {
            System.err.println("Error: " + i.getMessage());
        }
        this.setTitle("Checkers");
        this.setSize(8 * blockWidth + 2 * slideWindowX, 8 * blockWidth + slideWindowY);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.GRAY);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.addMouseListener(new ML());
    }
    public void move(int rowOfM, int colOfM, int rowOfP, int colOfP)
    {
        char temp = board[rowOfP][colOfP];
        if(temp == TEAMONECHAR && rowOfM == 7)
        {
            board[rowOfM][colOfM] = TEAMONEKING;
        }
        else if(temp == TEAMTWOCHAR && rowOfM == 0)
        {
            board[rowOfM][colOfM] = TEAMTWOKING;
        }
        else
        {
            board[rowOfM][colOfM] = temp;
        }
        board[rowOfP][colOfP] = EMPTYSPACE;
    }
    
    public void select(int x, int y)
    {
        if(x >= 6 * blockWidth && x <= 6 * blockWidth + 100 && y <= 0 && y >= -30)
        {
            System.exit(0);
        }
        else if(x >= 4 * blockWidth && x <= 4 * blockWidth + 100 && y <= 0 && y >= -30)
        {
            end();
            return;
        }
        x /= blockWidth;
        y /= blockWidth;
//        if(move > 0 && x == selected[0] && y == selected[1])
//        {
//            moves.clear();
//            turnOne = !turnOne;
//            pieceSelected = false;
//            move = 0;
//            repaint();
//        }
        if(move == 0 && x == selected[0] && y == selected[1])
        {
            moves.clear();
            pieceSelected = false;
            selected[0] = -1;
            selected[1] = -1;
            repaint();
        }
        else if(turnOne && (board[x][y] == TEAMONECHAR || board[x][y] == TEAMONEKING) || !turnOne && (board[x][y] == TEAMTWOCHAR || board[x][y] == TEAMTWOKING))
        {
            selected[0] = x;
            selected[1] = y;
            pieceSelected = true;
            mustMove.clear();
            moves.clear();
            findMoves(selected[0], selected[1], findJumps(), true);
            repaint();
        }
        else if(board[x][y] == EMPTYSPACE) //moves if viable
        {
            selected[2] = x;
            selected[3] = y;
            if(viableMove())
            {
                switch(selected[3] - selected[1])
                {
                    case 2: //moving up
                        if(turnOne)
                        {
                            if(selected[2] - selected[0] == 2) //if the move is to the right of the piece
                            {
                                board[selected[0] + 1][selected[1] + 1] = EMPTYSPACE;
                            }
                            else
                            {
                                board[selected[0] - 1][selected[1] + 1] = EMPTYSPACE;
                            }
                            capturesOne++;
                        }
                        else
                        {
                            if(selected[2] - selected[0] == 2)
                            {
                                board[selected[0] + 1][selected[1] + 1] = EMPTYSPACE;
                            }
                            else
                            {
                                board[selected[0] - 1][selected[1] + 1] = EMPTYSPACE;
                            }
                            capturesTwo++;
                        }
                        captured = true;
                        break;
                    case -2:
                        if(turnOne)
                        {
                            if(selected[2] - selected[0] == 2)
                            {
                                board[selected[0] + 1][selected[1] - 1] = EMPTYSPACE;
                            }
                            else
                            {
                                board[selected[0] - 1][selected[1] - 1] = EMPTYSPACE;
                            }
                            capturesOne++;
                        }
                        else
                        {
                            if(selected[2] - selected[0] == 2)
                            {
                                board[selected[0] + 1][selected[1] - 1] = EMPTYSPACE;
                            }
                            else
                            {
                                board[selected[0] - 1][selected[1] - 1] = EMPTYSPACE;
                            }
                            capturesTwo++;
                        }
                        captured = true;
                }
                move(selected[2], selected[3], selected[0], selected[1]);
                move++;
                moves.clear();
                selected[0] = selected[2];
                selected[1] = selected[3];
                if(captured)
                {
                    findMoves(selected[0],selected[1], false, true);
                }
                if(!captured || moves.isEmpty())
                {
                    moves.clear();
                    turnOne = !turnOne;
                    pieceSelected = false;
                    move = 0;
                }
                captured = false;
                findJumps();
                repaint();
                if(!hasMoves())
                {
                    draw();
                }
                if(capturesOne == 12)
                {
                    win();
                }
                if(capturesTwo == 12)
                {
                    win();
                }
            }
        }
    }
    public void findMoves(int x, int y, boolean hasJumps, boolean clear)
    {
        if(clear){moves.clear();}
        if(turnOne)
        {
            if(move == 0 && !hasJumps)
            {
                if(x < 7 && y < 7)
                {
                    if(board[x+1][y+1] == EMPTYSPACE)
                    {
                        moves.add(x+1);
                        moves.add(y+1);
                    }
                }
                if(x < 7 && y > 0)
                {
                    if(board[x+1][y-1] == EMPTYSPACE)
                    {
                        moves.add(x+1);
                        moves.add(y-1);
                    }
                }
            }
            if(x < 6 && y < 6)
            {
                if((board[x+1][y+1] == TEAMTWOCHAR || board[x+1][y+1] == TEAMTWOKING) && board[x+2][y+2] == EMPTYSPACE)
                {
                    moves.add(x+2);
                    moves.add(y+2);
                }
            }
            if(x < 6 && y > 1)
            {
                if((board[x+1][y-1] == TEAMTWOCHAR || board[x+1][y-1] == TEAMTWOKING) && board[x+2][y-2] == EMPTYSPACE)
                {
                    moves.add(x+2);
                    moves.add(y-2);
                }
            }
            if(board[x][y] == TEAMONEKING)
            {
                if(move == 0 && !hasJumps)
                {
                    if(x > 0 && y < 7)
                    {
                        if(board[x-1][y+1] == EMPTYSPACE)
                        {
                            moves.add(x-1);
                            moves.add(y+1);
                        }
                    }
                    if(x > 0 && y > 0)
                    {
                        if(board[x-1][y-1] == EMPTYSPACE)
                        {
                            moves.add(x-1);
                            moves.add(y-1);
                        }
                    }
                }
                if(x > 1 && y < 6)
                {
                    if((board[x-1][y+1] == TEAMTWOCHAR || board[x-1][y+1] == TEAMTWOKING) && board[x-2][y+2] == EMPTYSPACE)
                    {
                        moves.add(x-2);
                        moves.add(y+2);
                    }
                }
                if(x > 1 && y > 1)
                {
                    if((board[x-1][y-1] == TEAMTWOCHAR || board[x-1][y-1] == TEAMTWOKING) && board[x-2][y-2] == EMPTYSPACE)
                    {
                        moves.add(x-2);
                        moves.add(y-2);
                    }
                }
            }
        }
        else
        {
            if(move == 0 && !hasJumps)
            {
                if(x > 0 && y < 7)
                {
                    if(board[x-1][y+1] == EMPTYSPACE)
                    {
                        moves.add(x-1);
                        moves.add(y+1);
                    }
                }
                if(x > 0 && y > 0)
                {
                    if(board[x-1][y-1] == EMPTYSPACE)
                    {
                        moves.add(x-1);
                        moves.add(y-1);
                    }
                }
            }
            if(x > 1 && y < 6)
            {
                if((board[x-1][y+1] == TEAMONECHAR || board[x-1][y+1] == TEAMONEKING) && board[x-2][y+2] == EMPTYSPACE)
                {
                    moves.add(x-2);
                    moves.add(y+2);
                }
            }
            if(x > 1 && y > 1)
            {
                if((board[x-1][y-1] == TEAMONECHAR || board[x-1][y-1] == TEAMONEKING) && board[x-2][y-2] == EMPTYSPACE)
                {
                    moves.add(x-2);
                    moves.add(y-2);
                }
            }
            if(board[x][y] == TEAMTWOKING)
            {
                if(move == 0 && !hasJumps)
                {
                    if(x < 7 && y < 7)
                    {
                        if(board[x+1][y+1] == EMPTYSPACE)
                        {
                            moves.add(x+1);
                            moves.add(y+1);
                        }
                    }
                    if(x < 7 && y > 0)
                    {
                        if(board[x+1][y-1] == EMPTYSPACE)
                        {
                            moves.add(x+1);
                            moves.add(y-1);
                        }
                    }
                }
                if(x < 6 && y < 6)
                {
                    if((board[x+1][y+1] == TEAMONECHAR || board[x+1][y+1] == TEAMONEKING) && board[x+2][y+2] == EMPTYSPACE)
                    {
                        moves.add(x+2);
                        moves.add(y+2);
                    }
                }
                if(x < 6 && y > 1)
                {
                    if((board[x+1][y-1] == TEAMONECHAR || board[x+1][y-1] == TEAMONEKING) && board[x+2][y-2] == EMPTYSPACE)
                    {
                        moves.add(x+2);
                        moves.add(y-2);
                    }
                }
            }
        }
    }
    public boolean viableMove()
    {
        for(int i = 0; i < moves.size(); i+=2)
        {
            if(moves.get(i) == selected[2] && moves.get(i + 1) == selected[3]) return true;
        }
        return false;
    }
    public boolean hasMoves()
    {
        for(int r = 0; r < 8; r++)
        {
            for(int c = 0; c < 8; c++)
            {
                char temp = board[r][c];
                if(turnOne && (temp == TEAMONECHAR || temp == TEAMONEKING))
                {
                    findMoves(r, c, false, false);
                }
                else if(!turnOne && (temp == TEAMTWOCHAR || temp == TEAMTWOKING))
                {
                    findMoves(r,c, false, false);
                }
                
            }
        }
        return !moves.isEmpty();
    }
    public boolean findJumps()
    {
        mustMove.clear();
        for(int x = 0; x < 8; x++)
        {
            for(int y = 0; y < 8; y++)
            {
                canJump = false;
                if(turnOne && (board[x][y] == TEAMONECHAR || board[x][y] == TEAMONEKING))
                {
                    if(x < 6 && y < 6)
                    {
                        if((board[x+1][y+1] == TEAMTWOCHAR || board[x+1][y+1] == TEAMTWOKING) && board[x+2][y+2] == EMPTYSPACE)
                        {
                            moves.add(x+2);
                            moves.add(y+2);
                            canJump = true;
                        }
                    }
                    if(x < 6 && y > 1)
                    {
                        if((board[x+1][y-1] == TEAMTWOCHAR || board[x+1][y-1] == TEAMTWOKING) && board[x+2][y-2] == EMPTYSPACE)
                        {
                            moves.add(x+2);
                            moves.add(y-2);
                            canJump = true;
                        }
                    }
                    if(board[x][y] == TEAMONEKING)
                    {
                        if(x > 1 && y < 6)
                        {
                            if((board[x-1][y+1] == TEAMTWOCHAR || board[x-1][y+1] == TEAMTWOKING) && board[x-2][y+2] == EMPTYSPACE)
                            {
                                moves.add(x-2);
                                moves.add(y+2);
                                canJump = true;
                            }
                        }
                        if(x > 1 && y > 1)
                        {
                            if((board[x-1][y-1] == TEAMTWOCHAR || board[x-1][y-1] == TEAMTWOKING) && board[x-2][y-2] == EMPTYSPACE)
                            {
                                moves.add(x-2);
                                moves.add(y-2);
                                canJump = true;
                            }
                        }
                    }
                }
                else if(!turnOne && (board[x][y] == TEAMTWOCHAR || board[x][y] == TEAMTWOKING))
                {
                    if(x > 1 && y < 6)
                    {
                        if((board[x-1][y+1] == TEAMONECHAR || board[x-1][y+1] == TEAMONEKING) && board[x-2][y+2] == EMPTYSPACE)
                        {
                            moves.add(x-2);
                            moves.add(y+2);
                            canJump = true;
                        }
                    }
                    if(x > 1 && y > 1)
                    {
                        if((board[x-1][y-1] == TEAMONECHAR || board[x-1][y-1] == TEAMONEKING) && board[x-2][y-2] == EMPTYSPACE)
                        {
                            moves.add(x-2);
                            moves.add(y-2);
                            canJump = true;
                        }
                    }
                    if(board[x][y] == TEAMTWOKING)
                    {
                        if(x < 6 && y < 6)
                        {
                            if((board[x+1][y+1] == TEAMONECHAR || board[x+1][y+1] == TEAMONEKING) && board[x+2][y+2] == EMPTYSPACE)
                            {
                                moves.add(x+2);
                                moves.add(y+2);
                                canJump = true;
                            }
                        }
                        if(x < 6 && y > 1)
                        {
                            if((board[x+1][y-1] == TEAMONECHAR || board[x+1][y-1] == TEAMONEKING) && board[x+2][y-2] == EMPTYSPACE)
                            {
                                moves.add(x+2);
                                moves.add(y-2);
                                canJump = true;
                            }
                        }
                    }
                }
                if(canJump)
                {
                    mustMove.add(x);
                    mustMove.add(y);
                }
            }
        }
        return !moves.isEmpty();
    }
    @Override
    public void paint(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(slideWindowX, slideWindowY - 35, 8 * blockWidth, 35);
        if(turnOne){g.setColor(TEAMONECOLOR);}
        else{g.setColor(TEAMTWOCOLOR);}
        g.fillRect(slideWindowX, slideWindowY - 35, 8 * blockWidth, 30);
        g.setColor(Color.BLACK);
        g.fillRect(6 * blockWidth + slideWindowX, slideWindowY - 30, 100, 20);
        g.fillRect(4 * blockWidth + slideWindowX, slideWindowY - 30, 100, 20);
        g.setColor(Color.RED);
        g.fillRect(6 * blockWidth + 3 + slideWindowX, slideWindowY - 27, 94, 14);
        g.fillRect(4 * blockWidth + 3 + slideWindowX, slideWindowY - 27, 94, 14);
        g.drawImage(restart, slideWindowX + 4 * blockWidth + 17, slideWindowY - 27, null);
        g.drawImage(exit, slideWindowX + 6 * blockWidth + 15, slideWindowY - 27, null);
        g.drawImage(checkers, slideWindowX + blockWidth, slideWindowY - 30, null);
        for(int x = slideWindowX; x < 8 * blockWidth + slideWindowX; x+=blockWidth)
        {
            for(int y = slideWindowY; y < 8 * blockWidth + slideWindowY; y+=blockWidth)
            {
                for(int i = 0; i < mustMove.size(); i+=2)
                {
                    if(mustMove.get(i) == x / blockWidth && mustMove.get(i + 1) == y / blockWidth)
                    {
                        g.setColor(new Color(255, 102, 10));
                        g.fillRect(x, y, blockWidth, blockWidth);
                        g.setColor(DARK);
                        g.fillRect(x + 5, y + 5, blockWidth - 10, blockWidth - 10);
                        drawn = true;
                    }
                }
                if(pieceSelected)
                {
                    for(int i = 0; i < moves.size(); i+=2)
                    {
                        if(moves.get(i) == x / blockWidth && moves.get(i + 1) == y / blockWidth)
                        {
                            g.setColor(Color.BLUE);
                            g.fillRect(x, y, blockWidth, blockWidth);
                            g.setColor(DARK);
                            g.fillRect(x + 5, y + 5, blockWidth - 10, blockWidth - 10);
                            drawn = true;
                        }
                    }
                    if(selected[0] == x / blockWidth && selected[1] == y / blockWidth)
                    {
                        g.setColor(Color.GREEN);
                        g.fillRect(x, y, blockWidth, blockWidth);
                        g.setColor(DARK);
                        g.fillRect(x + 5, y + 5, blockWidth - 10, blockWidth - 10);
                        drawn = true;
                    }
                }
                if(!drawn)
                {
                    if(((x + y) / blockWidth) % 2 == 0)
                    {
                        g.setColor(DARK);
                        g.fillRect(x, y, blockWidth, blockWidth);
                    }
                    else
                    {
                        g.setColor(LIGHT);
                        g.fillRect(x, y, blockWidth, blockWidth);
                    }
                }
                switch(board[x / blockWidth][y / blockWidth])
                {
                    case TEAMONEKING:
                        g.setColor(Color.BLACK);
                        g.fillOval(x + blockWidth / 4 - 4, y + blockWidth / 4 - 4, blockWidth / 2 + 8, blockWidth / 2 + 8);
                        g.setColor(TEAMONECOLOR);
                        g.fillOval(x + blockWidth / 4, y + blockWidth / 4, blockWidth / 2, blockWidth / 2);
                        g.drawImage(king, x + blockWidth/4 + slideWindowX, y + blockWidth/4, blockWidth / 2 - 5, blockWidth / 2, null);
                        break;
                    case TEAMONECHAR:
                        g.setColor(Color.BLACK);
                        g.fillOval(x + blockWidth / 4 - 4, y + blockWidth / 4 - 4, blockWidth / 2 + 8, blockWidth / 2 + 8);
                        g.setColor(TEAMONECOLOR);
                        g.fillOval(x + blockWidth / 4, y + blockWidth / 4, blockWidth / 2, blockWidth / 2);
                        break;
                    case TEAMTWOKING:
                        g.setColor(Color.BLACK);
                        g.fillOval(x + blockWidth / 4 - 4, y + blockWidth / 4 - 4, blockWidth / 2 + 8, blockWidth / 2 + 8);
                        g.setColor(TEAMTWOCOLOR);
                        g.fillOval(x + blockWidth / 4, y + blockWidth / 4, blockWidth / 2, blockWidth / 2);
                        g.drawImage(king, x + blockWidth/4 + slideWindowX, y + blockWidth/4, blockWidth / 2 - 5, blockWidth / 2, null);
                        break;
                    case TEAMTWOCHAR:
                        g.setColor(Color.BLACK);
                        g.fillOval(x + blockWidth / 4 - 4, y + blockWidth / 4 - 4, blockWidth / 2 + 8, blockWidth / 2 + 8);
                        g.setColor(TEAMTWOCOLOR);
                        g.fillOval(x + blockWidth / 4, y + blockWidth / 4, blockWidth / 2, blockWidth / 2);
                        break;
                }
                drawn = false;
            }
        }
    }
    public void win()
    {
        String winner;
        if(!turnOne){winner = "Player One";}
        else{winner = "Player Two";}
        JOptionPane.showMessageDialog(this, winner + " wins!");
        end();
    }
    public void draw()
    {
        JOptionPane.showMessageDialog(this, "It is a draw!");
        end();
    }
    public void end()
    {
        board = new char[][]{{TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE},
                             {EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR},
                             {TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE, TEAMONECHAR, EMPTYSPACE},
                             {EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE},
                             {EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE, EMPTYSPACE},
                             {EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR},
                             {TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE,},
                             {EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOKING, EMPTYSPACE, TEAMTWOCHAR, EMPTYSPACE, TEAMTWOCHAR}};
        move = capturesOne = capturesTwo = 0;
        pieceSelected = drawn = false;
        turnOne = true;
        mustMove.clear();
        moves.clear();
        repaint();
    }
    public static void main(String[] args) {
        Board go = new Board();
    }
    
    //////////////////Subclass/////////////////
    public class ML extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
//            clicks++;
//            System.out.println("X: " + e.getX() + " Y: " + e.getY() + " C: " + clicks);
            select(e.getX() - slideWindowX, e.getY() - slideWindowY);
        }
    }
    
}
