package com.jmentor.jtictactoegui;

import javax.swing.JApplet;
import java.awt.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Rectangle;
import javax.swing.JLabel;
import java.awt.event.*;
import java.net.*; 
import com.jmentor.jqlearn.QLearn;
import com.jmentor.jqlearn.QMinimax;
import com.jmentor.jtictactoe.Random;
import com.jmentor.jtictactoe.Game;
import com.jmentor.jtictactoe.Board;
import com.jmentor.jtictactoe.Minimax;

public class tictactest extends JApplet
{
  JButton NewGameButton = new JButton();
  JButton jButton1 = new JButton();
  JLabel [] jLabel = new JLabel[Board.BOXSIZE];
  JButton jButton2 = new JButton();
  JLabel jPlayer1Wins = new JLabel();
  JLabel jPlayer2Wins = new JLabel();
  JLabel jPlayerTies = new JLabel();
  JLabel jPlayer1Type = new JLabel();
  JLabel jPlayer2Type = new JLabel();
  JLabel jNumGames = new JLabel();
  JLabel jPlayer1Info = new JLabel();
  JLabel jPlayer2Info = new JLabel();
  JComboBox jPlayer1Combo = new JComboBox();
  JComboBox jPlayer2Combo = new JComboBox();
  Board board = new Board();
  Minimax minimax = new Minimax( 4 );
  QLearn qlearn = new QLearn();
  QMinimax qminimax = new QMinimax( 4, qlearn );
  Random random = new Random();
  Game game = new Game();
  Image xImage;
  Image oImage;
  MediaTracker mt; 
  URL base; 
  boolean isPlaying = true;
  int player1Type;
  int player2Type;
  int player;
  boolean normalInit = true;
  boolean weightsLoaded = false;
  JLabel jInfo = new JLabel();

  final int NO_OWNER = 0;
  final int O_SQUARE = 1;
  final int X_SQUARE = 2;

  final int HUMAN    = 0;
  final int MINIMAX  = 1;
  final int QLEARN   = 2;
  final int QMINIMAX = 3;
  final int RANDOM   = 4;

  String [] playerTypes = 
    { "Human", 
      "Minimax", 
      "QLearn", 
      "QMinimax", 
      "Random" };


  public tictactest()
  {
  }

  void initGameBoard()
  {
     board.ClearAll();
     for (int i = 0; i < Board.BOXSIZE; i++)
     {
        jLabel[i].setIcon(null);
        jLabel[i].updateUI();
        jLabel[i].setBorder(BorderFactory.createLineBorder(Color.black, 1));
     }
     player = O_SQUARE;

     player1Type = jPlayer1Combo.getSelectedIndex();
     jPlayer1Info.setText("Player1: " + playerTypes[player1Type] );

     player2Type = jPlayer2Combo.getSelectedIndex();
     jPlayer2Info.setText("Player2: " + playerTypes[player2Type] );

     jInfo.setText("Weights loaded = " + weightsLoaded);
  }

  public void init()
  {
     // initialize the MediaTracker 
     mt = new MediaTracker(this); 
  
     // The try-catch is necessary when the URL isn't valid 
     // Of course this one is valid, since it is generated by 
     // Java itself. 
     if (normalInit) {

        try { 
            // getDocumentbase gets the applet path. 
            base = getDocumentBase(); 
        } catch (Exception e) {} 
  
        // Here we load the image. 
        // Only Gif and JPG are allowed. Transparent gif also. 
        xImage = getImage(base, "deploy/cross.gif");
        oImage = getImage(base, "deploy/not.gif");

     } else {

        xImage = Toolkit.getDefaultToolkit().getImage("deploy/cross.gif");
        oImage = Toolkit.getDefaultToolkit().getImage("deploy/not.gif");
     }
  
     // tell the MediaTracker to keep an eye on this image, and give it ID 1; 
     mt.addImage(xImage,1); 
     mt.addImage(oImage,2);

    // now tell the mediaTracker to stop the applet execution 
    // (in this example don't paint) until the images are fully loaded. 
    // must be in a try catch block. 
    try { 
       mt.waitForAll(); 
    } catch (InterruptedException  e) {} 

    // Load the weights
    try
    {
       qlearn.load("deploy/qweights");
       weightsLoaded = true;
    } catch(Exception e)
    {
      System.out.println( e );
    }
 
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
   
  public void start()
  {
  }

  public void stop()
  {
  }

  public void destroy()
  {
  }

  private void jbInit() throws Exception
  {
    Container contentPane = getContentPane();
    JTabbedPane tp = new JTabbedPane();
    JPanel panelOne = new JPanel();
    JPanel panelTwo = new JPanel();

    // Setup Panel One
    panelOne.setLayout(null);

    // Set up an array for loop processing.
    for (int i = 0; i < Board.BOXSIZE; i++)
    {
       jLabel[i] = new JLabel();
    }
    
    jButton1.setText("New Game");
    jButton1.setBounds(new Rectangle(10, 10, 130, 25));
    jButton1.addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
           initGameBoard();
           isPlaying = true;

           if (player1Type != HUMAN) {

              // Make the move.
              int computer;

              switch( player1Type ) {
              case MINIMAX:
                 computer = minimax.Move(board, player);
                 break;

              case QLEARN:
                 computer = qlearn.Move(board, player);
                 break;

              case QMINIMAX:
                 computer = qminimax.Move(board, player);
                 break;

              case RANDOM:
                 computer = random.Move(board, player);
                 break;

              default:
                 computer = minimax.Move(board, player);
                 break;
              };

              if (board.Get(computer) == O_SQUARE) {
                jLabel[computer].setIcon(new ImageIcon(oImage));
              } else if (board.Get(computer) == X_SQUARE) {
                jLabel[computer].setIcon(new ImageIcon(xImage));
              } else {
                jLabel[computer].setIcon(null);
                jLabel[computer].setBorder(BorderFactory.createLineBorder(Color.black, 1));
              }
              jLabel[computer].updateUI();

              // Switch player.
              player = OP(player);
           }
        }
      });

    jButton2.setText("Autoplay 100");
    jButton2.setBounds(new Rectangle(145, 10, 130, 25));
    jButton2.addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
           final SwingWorker worker = new SwingWorker()
           {
             public Object construct() {

               for (int i = 0; i < 100; i++)
               {
                  initGameBoard();
                  isPlaying = true;

                  while (isPlaying)
                  {
                     //int which = minimax.evaluateRandomMove(board.Get(());
                     int which = 0;
                     label_mousePressed(null, jLabel[which], which);
                     repaint();
                     try {
                        wait(100); 
                     } catch(Exception ex) {
                       System.out.println(ex);
                     }
                  }
               }
               initGameBoard();
               isPlaying = true;

               return null;
            }
          };
          worker.start();
        }
      });

    jLabel[0].setBounds(new Rectangle(10, 45, 50, 50));
    jLabel[0].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[0].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[0].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[0], 0);
        }
      });

    jLabel[1].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[1].setBounds(new Rectangle(65, 45, 50, 50));
    jLabel[1].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[1].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[1], 1);
        }
      });

    jLabel[2].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[2].setBounds(new Rectangle(120, 45, 50, 50));
    jLabel[2].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[2].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[2], 2);
        }
      });

    jLabel[3].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[3].setBounds(new Rectangle(10, 100, 50, 50));
    jLabel[3].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[3].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[3], 3);
        }
      });

    jLabel[4].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[4].setBounds(new Rectangle(65, 100, 50, 50));
    jLabel[4].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[4].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[4], 4);
        }
      });

    jLabel[5].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[5].setBounds(new Rectangle(120, 100, 50, 50));
    jLabel[5].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[5].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[5], 5);
        }
      });

    jLabel[6].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[6].setBounds(new Rectangle(10, 155, 50, 50));
    jLabel[6].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[6].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[6], 6);
        }
      });

    jLabel[7].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[7].setBounds(new Rectangle(65, 155, 50, 50));
    jLabel[7].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[7].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[7], 7);
        }
      });

    jLabel[8].setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jLabel[8].setBounds(new Rectangle(120, 155, 50, 50));
    jLabel[8].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel[8].addMouseListener(new java.awt.event.MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          label_mousePressed(e, jLabel[8], 8);
        }
      });

    jPlayer1Wins.setBounds(new Rectangle(10, 230, 150, 16));
    jPlayer2Wins.setBounds(new Rectangle(10, 250, 150, 16));
    jPlayerTies.setBounds(new Rectangle(10, 270, 150, 16));
    jNumGames.setBounds(new Rectangle(10, 290, 150, 16));
    jPlayer1Info.setBounds(new Rectangle(200, 12, 150, 16));
    jPlayer2Info.setBounds(new Rectangle(200, 32, 150, 16));

    jPlayer1Wins.setText("Player1 Wins: " + game.Player1_Wins);
    jPlayer2Wins.setText("Player2 Wins: " + game.Player2_Wins);
    jPlayerTies.setText("Player Ties: " + game.Num_Ties);
    jNumGames.setText("Number Games: " + game.Num_Games);
    
    panelOne.add(jPlayer2Info, null);
    panelOne.add(jPlayer1Info, null);
    panelOne.add(jNumGames, null);
    panelOne.add(jPlayerTies, null);
    panelOne.add(jPlayer2Wins, null);
    panelOne.add(jPlayer1Wins, null);
    panelOne.add(jLabel[8], null);
    panelOne.add(jLabel[7], null);
    panelOne.add(jLabel[6], null);
    panelOne.add(jLabel[5], null);
    panelOne.add(jLabel[4], null);
    panelOne.add(jLabel[3], null);
    panelOne.add(jLabel[2], null);
    panelOne.add(jLabel[1], null);
    panelOne.add(jLabel[0], null);
    panelOne.add(jButton1, null);
    // panelOne.add(jButton2, null);

    // Setup Panel Two
    jPlayer1Type.setBounds(new Rectangle(10, 10, 130, 25));
    jPlayer1Type.setText("Player1 Type:");
    jPlayer1Combo.setBounds(new Rectangle(10, 40, 130, 25));
    for (int i = 0; i < playerTypes.length; i++) {
       jPlayer1Combo.addItem(playerTypes[i]);
    }
    jPlayer1Combo.setSelectedIndex( HUMAN );

    jPlayer1Combo.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
           if (jPlayer1Combo.getSelectedIndex() == HUMAN) {
              if (jPlayer2Combo.getSelectedIndex() == HUMAN) {
                 jPlayer2Combo.setSelectedIndex( MINIMAX );
              }
           } else {
              jPlayer2Combo.setSelectedIndex( HUMAN );
           }
           if (!isPlaying) {
              player1Type = jPlayer1Combo.getSelectedIndex();
              jPlayer1Info.setText("Player1: " + playerTypes[player1Type] );

              player2Type = jPlayer2Combo.getSelectedIndex();
              jPlayer2Info.setText("Player2: " + playerTypes[player2Type] );
           }
        }
      });

    jPlayer2Type.setBounds(new Rectangle(150, 10, 130, 25));
    jPlayer2Type.setText("Player2 Type:");
    jPlayer2Combo.setBounds(new Rectangle(150, 40, 130, 25));
    for (int i = 0; i < playerTypes.length; i++) {
       jPlayer2Combo.addItem(playerTypes[i]);
    }
    jPlayer2Combo.setSelectedIndex( MINIMAX );

    jPlayer2Combo.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
           if (jPlayer2Combo.getSelectedIndex() == HUMAN) {
              if (jPlayer1Combo.getSelectedIndex() == HUMAN) {
                 jPlayer1Combo.setSelectedIndex( MINIMAX );
              }
           } else {
              jPlayer1Combo.setSelectedIndex( HUMAN );
           }
           if (!isPlaying) {
              player1Type = jPlayer1Combo.getSelectedIndex();
              jPlayer1Info.setText("Player1: " + playerTypes[player1Type] );

              player2Type = jPlayer2Combo.getSelectedIndex();
              jPlayer2Info.setText("Player2: " + playerTypes[player2Type] );
           }
        }
      });

    jInfo.setBounds(new Rectangle(10, 250, 150, 16));

    panelTwo.setLayout(null);
    panelTwo.add(jPlayer1Type, null);
    panelTwo.add(jPlayer1Combo, null);
    panelTwo.add(jPlayer2Type, null);
    panelTwo.add(jPlayer2Combo, null);
    panelTwo.add(jInfo, null);

    // Setup the panels.
    tp.add(panelOne, "Play game");
    tp.add(panelTwo, "Setup game");
    contentPane.setLayout(new BorderLayout());
    contentPane.add(tp);

    // Initialize the game board.
    initGameBoard();
  }

  public int OP( int value )
  {
     int opp = 0;

     if (value == 1) {
        opp = 2;
     } else if (value == 2) {
        opp = 1;
     } else {
        System.out.println("Error in OP");
     }
     return( opp );
  }

  void label_mousePressed(MouseEvent e, JLabel label, int which)
  {
     if (isPlaying)
     {
       if (board.Get(which) == NO_OWNER)
       {
         // Make the move.
         board.Set(which, player);
         board.IncrementMove();

         if (board.Get(which) == O_SQUARE) {
           label.setIcon(new ImageIcon(oImage));
         } else if (board.Get(which) == X_SQUARE) {
           label.setIcon(new ImageIcon(xImage));
         } else {
           label.setIcon(null);
           label.setBorder(BorderFactory.createLineBorder(Color.black, 1));
         }
         label.updateUI();

         // Switch player.
         player = OP(player);

         if (board.Win(X_SQUARE)) {
           isPlaying = false;
           game.Player1_Wins++;
           game.Num_Games++;
         } else if (board.Win(O_SQUARE)) {
           isPlaying = false;
           game.Player2_Wins++;
           game.Num_Games++;
         } else if (board.Tie()) {
           isPlaying = false;
           game.Num_Ties++;
           game.Num_Games++;
         }

         /* Let the computer move */
         if (isPlaying) {

           // Make the move.
           int computer;
           int pType;

           // Figure out which player.
           if (player1Type == HUMAN) {
              pType = player2Type;
           } else {
              pType = player1Type;
           }

           switch( pType ) {
           case MINIMAX:
              computer = minimax.Move(board, player);
              break;

           case QLEARN:
              computer = qlearn.Move(board, player);
              break;

           case QMINIMAX:
              computer = qminimax.Move(board, player);
              break;

           case RANDOM:
              computer = random.Move(board, player);
              break;

           default:
              computer = minimax.Move(board, player);
              break;
           };

           if (board.Get(computer) == O_SQUARE) {
             jLabel[computer].setIcon(new ImageIcon(oImage));
           } else if (board.Get(computer) == X_SQUARE) {
             jLabel[computer].setIcon(new ImageIcon(xImage));
           } else {
             jLabel[computer].setIcon(null);
             jLabel[computer].setBorder(BorderFactory.createLineBorder(Color.black, 1));
           }
           jLabel[computer].updateUI();

           // Switch player.
           player = OP(player);

           if (board.Win(X_SQUARE)) {
             isPlaying = false;
             game.Player1_Wins++;
             game.Num_Games++;
           } else if (board.Win(O_SQUARE)) {
             isPlaying = false;
             game.Player2_Wins++;
             game.Num_Games++;
           } else if (board.Tie()) {
             isPlaying = false;
             game.Num_Ties++;
             game.Num_Games++;
           }
         }

         jPlayer1Wins.setText("Player1 Wins: " + game.Player1_Wins);
         jPlayer2Wins.setText("Player2 Wins: " + game.Player2_Wins);
         jPlayerTies.setText("Player Ties: " + game.Num_Ties);
         jNumGames.setText("Number Games: " + game.Num_Games);
      }
    }
  }

  public static void main(String[] args)
  {
    tictactest applet = new tictactest();
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(applet, BorderLayout.CENTER);
    frame.setTitle("Tictactest 1.0, Copyright 2002 Richard Abbuhl");
    applet.normalInit = false;
    applet.init();
    applet.start();
    frame.setSize(400, 400);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    frame.setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  static  
  {
    try
    {
      // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch(Exception e) {}
  }
}
