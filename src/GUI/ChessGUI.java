package GUI;

import GUI.PiecesIcons.Icons;
import GUI.TileColors.BlackAndWhite;
import GUI.TileColors.BlueAndWhite;
import GUI.TileColors.Colors;
import GUI.TileColors.GreenAndWhite;
import GUI.TileColors.Wood;
import Game.*;
import Pieces.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.Timer;

/**
 *
 * @author Ido
 */
public class ChessGUI extends javax.swing.JFrame {

    Component[] cs = new Component[64];
    /**
     * Stores {@code Components} which should be treated as {@code JLabels}.
     */
    Component[][] TileMatrix;

    BlackPlayer blackPlayer = new BlackPlayer();
    WhitePlayer whitePlayer = new WhitePlayer();
    /**
     * the instance of the Board class for this JFrame.
     */
    public Board board = new Board(blackPlayer, whitePlayer);

    Colors myColors = new BlackAndWhite();

    /**
     * An {@code ArrayList} that stores "snapshots" of the gameBoard. used to
     * keep track of the moves in the game.
     */
    ArrayList<Piece[][]> history = new ArrayList<>();

    Timer trailingRed;
    Timer colorSwap;

    Timer BlackClock, WhiteClock;

    /**
     * Used to determine which player's turn it is.
     */
    PlayerType turn;

    /**
     * used to keep track of which piece is currently clicked on.
     */
    PieceType focusedPiece;

    int focusedPieceX, focusedPieceY;
    /**
     * Stores a piece's possible tile paths. used in {@code showAvailablePaths}
     * to display the possible paths on the GUI board in red.
     *
     * @see showAvailablePaths
     */
    boolean[][] PiecePossibleTiles;
    boolean HistoryCheckBox;

    /**
     * this boolean means that White is checked
     */
    public static boolean WhiteCheck;
    /**
     * this boolean means that Black is checked
     */
    public static boolean BlackCheck;

    public int BlackSeconds = 0, BlackMinutes = 0, WhiteSeconds = 0, WhiteMinutes = 0;

    /**
     * Creates new form ChessGUI
     */
    public ChessGUI() {
        initComponents();

        jFrameMainMenu.setSize(1069, 800);
        jFrameMainMenu.setLocationRelativeTo(null);
        jFrameMainMenu.setVisible(true);

        jLabelBlackClock.setVisible(false);
        jLabelWhiteClock.setVisible(false);
        jLabelTurnMarker.setVisible(false);
        jButtonShowHistory.setVisible(false);

        this.setLocationRelativeTo(null);
        turn = PlayerType.White;

        for (int i = 0; i < cs.length; i++) {
            cs[i] = jPanel1.getComponent(i);
        }

        TileMatrix = Board.ArrayToMatrix(cs);

        history.add(Board.copyValueOfGameboard(board.gameBoard));

        //<editor-fold defaultstate="collapsed" desc="Victory Timers declaration and implementation">
        trailingRed = new Timer(80, new ActionListener() {
            int x = 0, y = 0;
            final boolean RIGHT = false, LEFT = true;
            boolean Direction = RIGHT;

            @Override
            public void actionPerformed(ActionEvent ae) {
                resetTileBackground();
                outerloop:
                for (int i = y; i < TileMatrix.length; i++) {
                    for (int j = x; j < TileMatrix[i].length; j++) {
                        LightUpTile(j, i);
                        if (x == 0 && y == 7) {
                            y = 0;
                            Direction = RIGHT;
                        }
                        if (Direction == RIGHT) {
                            if (x < TileMatrix.length - 1) {
                                x++;
                            } else if (x < TileMatrix.length) {
                                y++;
                                Direction = LEFT;
                            }
                        } else if (Direction == LEFT) {
                            if (x > 0) {
                                x--;
                            } else if (x == 0) {
                                y++;
                                Direction = RIGHT;
                            }
                        }
                        break outerloop;
                    }
                }
                System.out.println("running");
            }
        });
        colorSwap = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                swapColors();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Player Timers declaration and implementation">
        BlackClock = new Timer(1000, new ActionListener() {

            DecimalFormat format = new DecimalFormat("00");

            @Override
            public void actionPerformed(ActionEvent ae) {
                BlackSeconds++;
                if (BlackSeconds == 60) {
                    BlackMinutes++;
                }
                jLabelBlackClock.setText(format.format(BlackMinutes) + ":" + format.format(BlackSeconds));
            }
        });
        WhiteClock = new Timer(1000, new ActionListener() {

            DecimalFormat format = new DecimalFormat("00");

            @Override
            public void actionPerformed(ActionEvent ae) {
                WhiteSeconds++;
                if (WhiteSeconds == 60) {
                    WhiteMinutes++;
                }
                jLabelWhiteClock.setText(format.format(WhiteMinutes) + ":" + format.format(WhiteSeconds));
            }
        });
        //</editor-fold>

    }

    /**
     * removes the existing MouseListeners of the JLabels in "TileMatrix" and
     * adds new ones depending on the content of the Tile.
     *
     * @param i y position of the click
     * @param j x position of the click
     */
    private void addMouseListeners(int i, int j) {
        final int iIndex = i, jIndex = j;
        if (board.gameBoard[i][j] != null) {
            if (((JLabel)TileMatrix[i][j]).getMouseListeners().length > 0) {
                MouseListener[] mls = ((JLabel)TileMatrix[i][j]).getMouseListeners();
                ((JLabel)TileMatrix[i][j]).removeMouseListener(mls[0]);
            }
            ((JLabel)TileMatrix[i][j]).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TileClickedOn(iIndex, jIndex);
                }
            });
        } else {
            if (((JLabel)TileMatrix[i][j]).getMouseListeners().length > 0) {
                MouseListener[] mls = ((JLabel)TileMatrix[i][j]).getMouseListeners();
                ((JLabel)TileMatrix[i][j]).removeMouseListener(mls[0]);
            }
            ((JLabel)TileMatrix[i][j]).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TileClickedOn(iIndex, jIndex);
                }
            });
        }
    }

    /**
     * Called when a JLabel is clicked on
     *
     * @param iIndex the y position of the Tile
     * @param jIndex the x position of the Tile
     */
    private void TileClickedOn(int iIndex, int jIndex) {

        if //<editor-fold defaultstate="collapsed" desc="(Tile with a piece in it)">
                (board.gameBoard[iIndex][jIndex] != null) {

            if //<editor-fold defaultstate="collapsed" desc="(same turn-color click = displays paths)">
                    (board.gameBoard[iIndex][jIndex].getPlayerColor().equals(turn)) {
                focusedPiece = board.gameBoard[iIndex][jIndex].getPieceType(); //sets "focusedPiece" to the filled tile's piece.
                focusedPieceX = jIndex;
                focusedPieceY = iIndex;
                PiecePossibleTiles = board.gameBoard[iIndex][jIndex].drawPath(board.gameBoard); //fills the moves-boolean-matrix
                showAvailablePaths(PiecePossibleTiles); //displays the bool matrix on the GUI board in red.
            } //</editor-fold>
            else if //<editor-fold defaultstate="collapsed" desc="(opposite turn-color click = eating an enemy)">
                    (focusedPiece != null && PiecePossibleTiles[iIndex][jIndex] == true) {
                boolean KingEaten = (board.gameBoard[iIndex][jIndex].getPieceType().equals(PieceType.King));
                PlayerType DeadKingColor;

                if (BlackCheck && board.gameBoard[focusedPieceY][focusedPieceX]
                        .getPlayerColor() == PlayerType.Black) {
                    Victory();
                } else if (WhiteCheck && board.gameBoard[focusedPieceY][focusedPieceX]
                        .getPlayerColor() == PlayerType.White) {
                    Victory();
                }

                board.gameBoard[iIndex][jIndex] = null; //nullifies the clicked tile
                board.gameBoard[iIndex][jIndex] = board.gameBoard[focusedPieceY][focusedPieceX]; //copies the focused tile over to the clicked tile
                board.gameBoard[focusedPieceY][focusedPieceX].setLocation(jIndex, iIndex); //sets the instance piece's x & y fields to the selected tile location indexes.
                board.gameBoard[focusedPieceY][focusedPieceX] = null; // nullifies the focused piece from the focused location

                history.add(Board.copyValueOfGameboard(board.gameBoard));

                resetTileBackground(); // sets GUI board to black & white

                if (board.gameBoard[iIndex][jIndex].getPieceType().equals(PieceType.Pawn) && !KingEaten) {
                    if (board.gameBoard[iIndex][jIndex].getPlayerColor() == PlayerType.Black) {
                        if (iIndex == 7) {
                            PawnEdge(blackPlayer, jIndex, iIndex);
                        }
                    } else {
                        if (iIndex == 0) {
                            PawnEdge(whitePlayer, jIndex, iIndex);
                        }
                    }
                }

                for (int i = 0; i < board.gameBoard.length; i++) {
                    for (int j = 0; j < board.gameBoard[0].length; j++) {
                        drawBoard(board.gameBoard, i, j);
                    }
                }

                focusedPiece = null;
                focusedPieceX = -1;
                focusedPieceY = -1;
                if (turn == PlayerType.Black) {
                    turn = PlayerType.White;
                    if (BlackClock.isRunning()) {
                        BlackClock.stop();
                    }
                    WhiteClock.start();
                } else {
                    turn = PlayerType.Black;
                    if (WhiteClock.isRunning()) {
                        WhiteClock.stop();
                    }
                    BlackClock.start();
                }

                if (KingEaten) {
                    DeadKingColor = board.gameBoard[iIndex][jIndex].getPlayerColor();
                    Victory();
                    jLabelTurnMarker.setVisible(false);
                }
                if (jFrameHistory.isVisible()) { //refresh history list
                    jButtonShowHistoryActionPerformed(null);
                }

                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        addMouseListeners(i, j);
                    }
                }

                checkForCheckmate(board.gameBoard);
            }
            //</editor-fold>

        } //</editor-fold>
        else //<editor-fold defaultstate="collapsed" desc="(Empty tile)">
        {
            if (focusedPiece != null) {

                if //<editor-fold defaultstate="collapsed" desc="( ) { moves a piece }">
                        (PiecePossibleTiles[iIndex][jIndex]) {

                    if (BlackCheck && board.gameBoard[focusedPieceY][focusedPieceX]
                            .getPlayerColor() == PlayerType.Black) {
                        Victory();
                    } else if (WhiteCheck && board.gameBoard[focusedPieceY][focusedPieceX]
                            .getPlayerColor() == PlayerType.White) {
                        Victory();
                    }

                    board.gameBoard[iIndex][jIndex] = board.gameBoard[focusedPieceY][focusedPieceX];
                    board.gameBoard[focusedPieceY][focusedPieceX].setLocation(jIndex, iIndex);
                    board.gameBoard[focusedPieceY][focusedPieceX] = null;

                    resetTileBackground();

                    if (board.gameBoard[iIndex][jIndex].getPieceType().equals(PieceType.Pawn)) {
                        if (board.gameBoard[iIndex][jIndex].getPlayerColor() == PlayerType.Black) {
                            if (iIndex == 7) {
                                PawnEdge(blackPlayer, jIndex, iIndex);
                            }
                        } else {
                            if (iIndex == 0) {
                                PawnEdge(whitePlayer, jIndex, iIndex);

                            }
                        }
                    }

                    for (int i = 0; i < board.gameBoard.length; i++) {
                        for (int j = 0; j < board.gameBoard[0].length; j++) {
                            drawBoard(board.gameBoard, i, j);
                        }
                    }
                    if (turn == PlayerType.Black) {
                        turn = PlayerType.White;
                        if (BlackClock.isRunning()) {
                            BlackClock.stop();
                        }
                        WhiteClock.start();
                    } else {
                        turn = PlayerType.Black;
                        if (WhiteClock.isRunning()) {
                            WhiteClock.stop();
                        }
                        BlackClock.start();
                    }
                    focusedPiece = null;
                    focusedPieceX = -1;
                    focusedPieceY = -1;
                    history.add(Board.copyValueOfGameboard(board.gameBoard));

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            addMouseListeners(i, j);
                        }
                    }

                    if (jFrameHistory.isVisible()) { //refresh history list
                        jButtonShowHistoryActionPerformed(null);
                    }
                    if (!checkForCheckmate(board.gameBoard) && (BlackCheck || WhiteCheck)) {
                        Victory();
                    }
                }
                //</editor-fold>

            }
        }
        //</editor-fold>

        updateTurnMarker();

    }

    void LightUpTile(int x, int y) {
        if (TileMatrix[y][x].getBackground().equals(Color.WHITE)) {
            TileMatrix[y][x].setBackground(myColors.redOnBrightColor);
        } else if (TileMatrix[y][x].getBackground().equals(Color.BLACK)) {
            TileMatrix[y][x].setBackground(myColors.redOnDarkColor);
        }
    }
    /**
     * An instance of the {@code Icons} class which stores all the ImageIcons
     * for the pieces.
     *
     * @see Icons
     */
    Icons pi = new Icons();

    /**
     * Redraws the Icons on the GUI board in accordance to the gameBoard.
     *
     * @param b the gameBoard
     * @param i y position of the tile
     * @param j x position of the tile
     */
    private void drawBoard(Piece[][] b, int i, int j) {
        if (b[i][j] != null) {
            ((JLabel)TileMatrix[i][j]).setIcon(b[i][j].getPieceImage());
        } else {
            ((JLabel)TileMatrix[i][j]).setIcon(null);
        }
    }

    /**
     * Used to display with red tiles on the GUI board where a piece can move
     * to.
     *
     * @param paths boolean matrix of possible paths.
     */
    public void showAvailablePaths(boolean[][] paths) {
        resetTileBackground();
        for (int i = 0; i < TileMatrix.length; i++) {
            for (int j = 0; j < TileMatrix[i].length; j++) {
                if (paths[i][j]) {
                    //TileMatrix[i][j].setBackground(RedOnBlack);
                    if (TileMatrix[i][j].getBackground().equals(myColors.darkColor)) {
                        TileMatrix[i][j].setBackground(myColors.redOnDarkColor);
                    } else {
                        TileMatrix[i][j].setBackground(myColors.redOnBrightColor);
                    }
                }
            }
        }
    }

    private void updateTurnMarker() {
        String text = (turn.equals(PlayerType.Black)) ? "Black Turn" : "White Turn";
        jLabelTurnMarker.setText(text);
        jLabelTurnMarker.setOpaque(true);

        Color background = (turn.equals(PlayerType.Black)) ? Color.black : Color.white;
        Color foreground = (turn.equals(PlayerType.Black)) ? Color.white : Color.black;

        jLabelTurnMarker.setBackground(background);
        jLabelTurnMarker.setForeground(foreground);

        if (!(colorSwap.isRunning() || trailingRed.isRunning())) {
            if (BlackCheck) {
                jLabelCheck.setText("Black is checked!");
            } else if (WhiteCheck) {
                jLabelCheck.setText("White is checked!");
            }
        }
    }

    /**
     * resets the GUI game board Tiles' background to normal, black-white chess
     * board
     */
    public void resetTileBackground() {
        for (int i = 0; i < TileMatrix.length; i++) {
            for (int j = 0; j < TileMatrix[i].length; j++) {
                if ((i + j) % 2 == 0) {
                    TileMatrix[i][j].setBackground(myColors.brightColor);
                } else {
                    TileMatrix[i][j].setBackground(myColors.darkColor);
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFramePromotion = new javax.swing.JFrame();
        jLabel1 = new javax.swing.JLabel();
        jButtonRevive = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelSelectPawn = new javax.swing.JLabel();
        jLabelSelectQueen = new javax.swing.JLabel();
        jLabelSelectKnight = new javax.swing.JLabel();
        jLabelSelectRook = new javax.swing.JLabel();
        jLabelSelectBishop = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jFrameHistory = new javax.swing.JFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListHistory = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jButtonRestore = new javax.swing.JButton();
        jFrameMainMenu = new javax.swing.JFrame();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanelBack = new javax.swing.JPanel();
        jLabelBackground = new javax.swing.JLabel();
        jPanelFore = new javax.swing.JPanel();
        jCheckBoxHistory = new javax.swing.JCheckBox();
        jButtonStart = new javax.swing.JButton();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuAbout = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        boardTile1 = new javax.swing.JLabel();
        boardTile2 = new javax.swing.JLabel();
        boardTile3 = new javax.swing.JLabel();
        boardTile4 = new javax.swing.JLabel();
        boardTile5 = new javax.swing.JLabel();
        boardTile6 = new javax.swing.JLabel();
        boardTile7 = new javax.swing.JLabel();
        boardTile8 = new javax.swing.JLabel();
        boardTile9 = new javax.swing.JLabel();
        boardTile10 = new javax.swing.JLabel();
        boardTile11 = new javax.swing.JLabel();
        boardTile12 = new javax.swing.JLabel();
        boardTile13 = new javax.swing.JLabel();
        boardTile14 = new javax.swing.JLabel();
        boardTile15 = new javax.swing.JLabel();
        boardTile16 = new javax.swing.JLabel();
        boardTile17 = new javax.swing.JLabel();
        boardTile18 = new javax.swing.JLabel();
        boardTile19 = new javax.swing.JLabel();
        boardTile20 = new javax.swing.JLabel();
        boardTile21 = new javax.swing.JLabel();
        boardTile22 = new javax.swing.JLabel();
        boardTile23 = new javax.swing.JLabel();
        boardTile24 = new javax.swing.JLabel();
        boardTile25 = new javax.swing.JLabel();
        boardTile26 = new javax.swing.JLabel();
        boardTile27 = new javax.swing.JLabel();
        boardTile28 = new javax.swing.JLabel();
        boardTile29 = new javax.swing.JLabel();
        boardTile30 = new javax.swing.JLabel();
        boardTile31 = new javax.swing.JLabel();
        boardTile32 = new javax.swing.JLabel();
        boardTile33 = new javax.swing.JLabel();
        boardTile34 = new javax.swing.JLabel();
        boardTile35 = new javax.swing.JLabel();
        boardTile36 = new javax.swing.JLabel();
        boardTile37 = new javax.swing.JLabel();
        boardTile38 = new javax.swing.JLabel();
        boardTile39 = new javax.swing.JLabel();
        boardTile40 = new javax.swing.JLabel();
        boardTile41 = new javax.swing.JLabel();
        boardTile42 = new javax.swing.JLabel();
        boardTile43 = new javax.swing.JLabel();
        boardTile44 = new javax.swing.JLabel();
        boardTile45 = new javax.swing.JLabel();
        boardTile46 = new javax.swing.JLabel();
        boardTile47 = new javax.swing.JLabel();
        boardTile48 = new javax.swing.JLabel();
        boardTile49 = new javax.swing.JLabel();
        boardTile50 = new javax.swing.JLabel();
        boardTile51 = new javax.swing.JLabel();
        boardTile52 = new javax.swing.JLabel();
        boardTile53 = new javax.swing.JLabel();
        boardTile54 = new javax.swing.JLabel();
        boardTile55 = new javax.swing.JLabel();
        boardTile56 = new javax.swing.JLabel();
        boardTile57 = new javax.swing.JLabel();
        boardTile58 = new javax.swing.JLabel();
        boardTile59 = new javax.swing.JLabel();
        boardTile60 = new javax.swing.JLabel();
        boardTile61 = new javax.swing.JLabel();
        boardTile62 = new javax.swing.JLabel();
        boardTile63 = new javax.swing.JLabel();
        boardTile64 = new javax.swing.JLabel();
        jButtonShowHistory = new javax.swing.JButton();
        jLabelCheck = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabelBlackClock = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelTurnMarker = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabelWhiteClock = new javax.swing.JLabel();
        jMenuBarGame = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuReturnToMenu = new javax.swing.JMenu();
        jMenuResetGame = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItemBlackWhite = new javax.swing.JMenuItem();
        jMenuItemBlueWhite = new javax.swing.JMenuItem();
        jMenuItemGreenWhite = new javax.swing.JMenuItem();
        jMenuItemWood = new javax.swing.JMenuItem();
        jMenuHistoryOption = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuAbout2 = new javax.swing.JMenu();

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Choose a Piece to Promote");

        jButtonRevive.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButtonRevive.setText("Promote!");

        jPanel2.setLayout(new java.awt.GridLayout(2, 5));

        jLabelSelectPawn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSelectPawn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSelectPawnMouseClicked(evt);
            }
        });
        jPanel2.add(jLabelSelectPawn);

        jLabelSelectQueen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSelectQueen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSelectQueenMouseClicked(evt);
            }
        });
        jPanel2.add(jLabelSelectQueen);

        jLabelSelectKnight.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSelectKnight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSelectKnightMouseClicked(evt);
            }
        });
        jPanel2.add(jLabelSelectKnight);

        jLabelSelectRook.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSelectRook.setToolTipText("");
        jLabelSelectRook.setIconTextGap(0);
        jLabelSelectRook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSelectRookMouseClicked(evt);
            }
        });
        jPanel2.add(jLabelSelectRook);

        jLabelSelectBishop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSelectBishop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSelectBishopMouseClicked(evt);
            }
        });
        jPanel2.add(jLabelSelectBishop);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton1.setText("Pawn");
        jRadioButton1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jRadioButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(jRadioButton1);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton2.setText("Queen");
        jRadioButton2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jRadioButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(jRadioButton2);

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton3.setText("Knight");
        jRadioButton3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jRadioButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(jRadioButton3);

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton4.setText("Rook");
        jRadioButton4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jRadioButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(jRadioButton4);

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton5.setText("Bishop");
        jRadioButton5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jRadioButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(jRadioButton5);

        javax.swing.GroupLayout jFramePromotionLayout = new javax.swing.GroupLayout(jFramePromotion.getContentPane());
        jFramePromotion.getContentPane().setLayout(jFramePromotionLayout);
        jFramePromotionLayout.setHorizontalGroup(
            jFramePromotionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFramePromotionLayout.createSequentialGroup()
                .addContainerGap(112, Short.MAX_VALUE)
                .addGroup(jFramePromotionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFramePromotionLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFramePromotionLayout.createSequentialGroup()
                        .addComponent(jButtonRevive, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(200, 200, 200))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFramePromotionLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118))))
        );
        jFramePromotionLayout.setVerticalGroup(
            jFramePromotionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFramePromotionLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(jButtonRevive, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        jFrameHistory.setFocusable(false);
        jFrameHistory.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jFrameHistoryWindowClosing(evt);
            }
        });

        jListHistory.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jListHistory.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Move 1", "Move 2", "Move 3", "Move 4", "Move 5", "Move 6", "Move 7" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListHistory.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListHistory.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListHistoryValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListHistory);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Move History:");

        jButtonRestore.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonRestore.setText("Restore Move");
        jButtonRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRestoreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jFrameHistoryLayout = new javax.swing.GroupLayout(jFrameHistory.getContentPane());
        jFrameHistory.getContentPane().setLayout(jFrameHistoryLayout);
        jFrameHistoryLayout.setHorizontalGroup(
            jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameHistoryLayout.createSequentialGroup()
                .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFrameHistoryLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jFrameHistoryLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                            .addComponent(jButtonRestore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jFrameHistoryLayout.setVerticalGroup(
            jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrameHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonRestore, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        jFrameMainMenu.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jFrameMainMenu.setResizable(false);

        jLabelBackground.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/chess.png"))); // NOI18N

        jCheckBoxHistory.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCheckBoxHistory.setText("Enable History?");
        jCheckBoxHistory.setToolTipText("If checked, allows to see previous moves and restore them.");
        jCheckBoxHistory.setFocusable(false);
        jCheckBoxHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxHistoryActionPerformed(evt);
            }
        });

        jButtonStart.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButtonStart.setText("Start");
        jButtonStart.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonStart.setFocusable(false);
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelForeLayout = new javax.swing.GroupLayout(jPanelFore);
        jPanelFore.setLayout(jPanelForeLayout);
        jPanelForeLayout.setHorizontalGroup(
            jPanelForeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForeLayout.createSequentialGroup()
                .addGap(416, 416, 416)
                .addComponent(jButtonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelForeLayout.setVerticalGroup(
            jPanelForeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelForeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(90, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelBackLayout = new javax.swing.GroupLayout(jPanelBack);
        jPanelBack.setLayout(jPanelBackLayout);
        jPanelBackLayout.setHorizontalGroup(
            jPanelBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBackLayout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jLabelBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE))
            .addComponent(jPanelFore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelBackLayout.setVerticalGroup(
            jPanelBackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBackLayout.createSequentialGroup()
                .addComponent(jLabelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelFore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanelBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane1.setLayer(jPanelBack, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jMenuAbout.setText("About");
        jMenuAbout.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuAboutMouseClicked(evt);
            }
        });
        jMenuBar.add(jMenuAbout);

        jFrameMainMenu.setJMenuBar(jMenuBar);

        javax.swing.GroupLayout jFrameMainMenuLayout = new javax.swing.GroupLayout(jFrameMainMenu.getContentPane());
        jFrameMainMenu.getContentPane().setLayout(jFrameMainMenuLayout);
        jFrameMainMenuLayout.setHorizontalGroup(
            jFrameMainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        jFrameMainMenuLayout.setVerticalGroup(
            jFrameMainMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setLayout(new java.awt.GridLayout(8, 8));

        boardTile1.setBackground(new java.awt.Color(255, 255, 255));
        boardTile1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile1.setOpaque(true);
        jPanel1.add(boardTile1);

        boardTile2.setBackground(new java.awt.Color(0, 0, 0));
        boardTile2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile2.setOpaque(true);
        jPanel1.add(boardTile2);

        boardTile3.setBackground(new java.awt.Color(255, 255, 255));
        boardTile3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile3.setOpaque(true);
        jPanel1.add(boardTile3);

        boardTile4.setBackground(new java.awt.Color(0, 0, 0));
        boardTile4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile4.setOpaque(true);
        jPanel1.add(boardTile4);

        boardTile5.setBackground(new java.awt.Color(255, 255, 255));
        boardTile5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile5.setOpaque(true);
        jPanel1.add(boardTile5);

        boardTile6.setBackground(new java.awt.Color(0, 0, 0));
        boardTile6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile6.setOpaque(true);
        jPanel1.add(boardTile6);

        boardTile7.setBackground(new java.awt.Color(255, 255, 255));
        boardTile7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile7.setOpaque(true);
        jPanel1.add(boardTile7);

        boardTile8.setBackground(new java.awt.Color(0, 0, 0));
        boardTile8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile8.setOpaque(true);
        jPanel1.add(boardTile8);

        boardTile9.setBackground(new java.awt.Color(0, 0, 0));
        boardTile9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile9.setOpaque(true);
        jPanel1.add(boardTile9);

        boardTile10.setBackground(new java.awt.Color(255, 255, 255));
        boardTile10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile10.setOpaque(true);
        jPanel1.add(boardTile10);

        boardTile11.setBackground(new java.awt.Color(0, 0, 0));
        boardTile11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile11.setOpaque(true);
        jPanel1.add(boardTile11);

        boardTile12.setBackground(new java.awt.Color(255, 255, 255));
        boardTile12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile12.setOpaque(true);
        jPanel1.add(boardTile12);

        boardTile13.setBackground(new java.awt.Color(0, 0, 0));
        boardTile13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile13.setOpaque(true);
        jPanel1.add(boardTile13);

        boardTile14.setBackground(new java.awt.Color(255, 255, 255));
        boardTile14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile14.setOpaque(true);
        jPanel1.add(boardTile14);

        boardTile15.setBackground(new java.awt.Color(0, 0, 0));
        boardTile15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile15.setOpaque(true);
        jPanel1.add(boardTile15);

        boardTile16.setBackground(new java.awt.Color(255, 255, 255));
        boardTile16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile16.setOpaque(true);
        jPanel1.add(boardTile16);

        boardTile17.setBackground(new java.awt.Color(255, 255, 255));
        boardTile17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile17.setOpaque(true);
        jPanel1.add(boardTile17);

        boardTile18.setBackground(new java.awt.Color(0, 0, 0));
        boardTile18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile18.setOpaque(true);
        jPanel1.add(boardTile18);

        boardTile19.setBackground(new java.awt.Color(255, 255, 255));
        boardTile19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile19.setOpaque(true);
        jPanel1.add(boardTile19);

        boardTile20.setBackground(new java.awt.Color(0, 0, 0));
        boardTile20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile20.setOpaque(true);
        jPanel1.add(boardTile20);

        boardTile21.setBackground(new java.awt.Color(255, 255, 255));
        boardTile21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile21.setOpaque(true);
        jPanel1.add(boardTile21);

        boardTile22.setBackground(new java.awt.Color(0, 0, 0));
        boardTile22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile22.setOpaque(true);
        jPanel1.add(boardTile22);

        boardTile23.setBackground(new java.awt.Color(255, 255, 255));
        boardTile23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile23.setOpaque(true);
        jPanel1.add(boardTile23);

        boardTile24.setBackground(new java.awt.Color(0, 0, 0));
        boardTile24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile24.setOpaque(true);
        jPanel1.add(boardTile24);

        boardTile25.setBackground(new java.awt.Color(0, 0, 0));
        boardTile25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile25.setOpaque(true);
        jPanel1.add(boardTile25);

        boardTile26.setBackground(new java.awt.Color(255, 255, 255));
        boardTile26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile26.setOpaque(true);
        jPanel1.add(boardTile26);

        boardTile27.setBackground(new java.awt.Color(0, 0, 0));
        boardTile27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile27.setOpaque(true);
        jPanel1.add(boardTile27);

        boardTile28.setBackground(new java.awt.Color(255, 255, 255));
        boardTile28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile28.setOpaque(true);
        jPanel1.add(boardTile28);

        boardTile29.setBackground(new java.awt.Color(0, 0, 0));
        boardTile29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile29.setOpaque(true);
        jPanel1.add(boardTile29);

        boardTile30.setBackground(new java.awt.Color(255, 255, 255));
        boardTile30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile30.setOpaque(true);
        jPanel1.add(boardTile30);

        boardTile31.setBackground(new java.awt.Color(0, 0, 0));
        boardTile31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile31.setOpaque(true);
        jPanel1.add(boardTile31);

        boardTile32.setBackground(new java.awt.Color(255, 255, 255));
        boardTile32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile32.setOpaque(true);
        jPanel1.add(boardTile32);

        boardTile33.setBackground(new java.awt.Color(255, 255, 255));
        boardTile33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile33.setOpaque(true);
        jPanel1.add(boardTile33);

        boardTile34.setBackground(new java.awt.Color(0, 0, 0));
        boardTile34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile34.setOpaque(true);
        jPanel1.add(boardTile34);

        boardTile35.setBackground(new java.awt.Color(255, 255, 255));
        boardTile35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile35.setOpaque(true);
        jPanel1.add(boardTile35);

        boardTile36.setBackground(new java.awt.Color(0, 0, 0));
        boardTile36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile36.setOpaque(true);
        jPanel1.add(boardTile36);

        boardTile37.setBackground(new java.awt.Color(255, 255, 255));
        boardTile37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile37.setOpaque(true);
        jPanel1.add(boardTile37);

        boardTile38.setBackground(new java.awt.Color(0, 0, 0));
        boardTile38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile38.setOpaque(true);
        jPanel1.add(boardTile38);

        boardTile39.setBackground(new java.awt.Color(255, 255, 255));
        boardTile39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile39.setOpaque(true);
        jPanel1.add(boardTile39);

        boardTile40.setBackground(new java.awt.Color(0, 0, 0));
        boardTile40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile40.setOpaque(true);
        jPanel1.add(boardTile40);

        boardTile41.setBackground(new java.awt.Color(0, 0, 0));
        boardTile41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile41.setOpaque(true);
        jPanel1.add(boardTile41);

        boardTile42.setBackground(new java.awt.Color(255, 255, 255));
        boardTile42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile42.setOpaque(true);
        jPanel1.add(boardTile42);

        boardTile43.setBackground(new java.awt.Color(0, 0, 0));
        boardTile43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile43.setOpaque(true);
        jPanel1.add(boardTile43);

        boardTile44.setBackground(new java.awt.Color(255, 255, 255));
        boardTile44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile44.setOpaque(true);
        jPanel1.add(boardTile44);

        boardTile45.setBackground(new java.awt.Color(0, 0, 0));
        boardTile45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile45.setOpaque(true);
        jPanel1.add(boardTile45);

        boardTile46.setBackground(new java.awt.Color(255, 255, 255));
        boardTile46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile46.setOpaque(true);
        jPanel1.add(boardTile46);

        boardTile47.setBackground(new java.awt.Color(0, 0, 0));
        boardTile47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile47.setOpaque(true);
        jPanel1.add(boardTile47);

        boardTile48.setBackground(new java.awt.Color(255, 255, 255));
        boardTile48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile48.setOpaque(true);
        jPanel1.add(boardTile48);

        boardTile49.setBackground(new java.awt.Color(255, 255, 255));
        boardTile49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile49.setOpaque(true);
        jPanel1.add(boardTile49);

        boardTile50.setBackground(new java.awt.Color(0, 0, 0));
        boardTile50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile50.setOpaque(true);
        jPanel1.add(boardTile50);

        boardTile51.setBackground(new java.awt.Color(255, 255, 255));
        boardTile51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile51.setOpaque(true);
        jPanel1.add(boardTile51);

        boardTile52.setBackground(new java.awt.Color(0, 0, 0));
        boardTile52.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile52.setOpaque(true);
        jPanel1.add(boardTile52);

        boardTile53.setBackground(new java.awt.Color(255, 255, 255));
        boardTile53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile53.setOpaque(true);
        jPanel1.add(boardTile53);

        boardTile54.setBackground(new java.awt.Color(0, 0, 0));
        boardTile54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile54.setOpaque(true);
        jPanel1.add(boardTile54);

        boardTile55.setBackground(new java.awt.Color(255, 255, 255));
        boardTile55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile55.setOpaque(true);
        jPanel1.add(boardTile55);

        boardTile56.setBackground(new java.awt.Color(0, 0, 0));
        boardTile56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile56.setOpaque(true);
        jPanel1.add(boardTile56);

        boardTile57.setBackground(new java.awt.Color(0, 0, 0));
        boardTile57.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile57.setToolTipText("");
        boardTile57.setOpaque(true);
        jPanel1.add(boardTile57);

        boardTile58.setBackground(new java.awt.Color(255, 255, 255));
        boardTile58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile58.setOpaque(true);
        jPanel1.add(boardTile58);

        boardTile59.setBackground(new java.awt.Color(0, 0, 0));
        boardTile59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile59.setOpaque(true);
        jPanel1.add(boardTile59);

        boardTile60.setBackground(new java.awt.Color(255, 255, 255));
        boardTile60.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile60.setOpaque(true);
        jPanel1.add(boardTile60);

        boardTile61.setBackground(new java.awt.Color(0, 0, 0));
        boardTile61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile61.setOpaque(true);
        jPanel1.add(boardTile61);

        boardTile62.setBackground(new java.awt.Color(255, 255, 255));
        boardTile62.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile62.setOpaque(true);
        jPanel1.add(boardTile62);

        boardTile63.setBackground(new java.awt.Color(0, 0, 0));
        boardTile63.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile63.setOpaque(true);
        jPanel1.add(boardTile63);

        boardTile64.setBackground(new java.awt.Color(255, 255, 255));
        boardTile64.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        boardTile64.setOpaque(true);
        jPanel1.add(boardTile64);

        jButtonShowHistory.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonShowHistory.setText("Show History");
        jButtonShowHistory.setFocusable(false);
        jButtonShowHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowHistoryActionPerformed(evt);
            }
        });

        jLabelCheck.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabelCheck.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCheck.setAutoscrolls(true);

        jPanel3.setLayout(new java.awt.GridLayout(5, 0));

        jLabelBlackClock.setBackground(new java.awt.Color(0, 0, 0));
        jLabelBlackClock.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelBlackClock.setForeground(new java.awt.Color(255, 255, 255));
        jLabelBlackClock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelBlackClock.setOpaque(true);
        jPanel3.add(jLabelBlackClock);
        jPanel3.add(jSeparator1);

        jLabelTurnMarker.setBackground(new java.awt.Color(255, 255, 255));
        jLabelTurnMarker.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelTurnMarker.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTurnMarker.setText("White Turn");
        jLabelTurnMarker.setOpaque(true);
        jPanel3.add(jLabelTurnMarker);
        jPanel3.add(jSeparator2);

        jLabelWhiteClock.setBackground(new java.awt.Color(255, 255, 255));
        jLabelWhiteClock.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelWhiteClock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWhiteClock.setOpaque(true);
        jPanel3.add(jLabelWhiteClock);

        jMenu1.setText("Options");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jMenu1MouseEntered(evt);
            }
        });

        jMenuReturnToMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/back.png"))); // NOI18N
        jMenuReturnToMenu.setText("Return to Menu");
        jMenuReturnToMenu.setFocusable(false);
        jMenuReturnToMenu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuReturnToMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuReturnToMenuMouseClicked(evt);
            }
        });
        jMenu1.add(jMenuReturnToMenu);

        jMenuResetGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/refresh.png"))); // NOI18N
        jMenuResetGame.setText("Restart Game");
        jMenuResetGame.setFocusable(false);
        jMenuResetGame.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuResetGame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuResetGameMouseClicked(evt);
            }
        });
        jMenu1.add(jMenuResetGame);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/brush.png"))); // NOI18N
        jMenu4.setText("Change Color");
        jMenu4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jMenuItemBlackWhite.setBackground(new java.awt.Color(0, 0, 0));
        jMenuItemBlackWhite.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItemBlackWhite.setForeground(new java.awt.Color(255, 255, 255));
        jMenuItemBlackWhite.setText("Black and White");
        jMenuItemBlackWhite.setOpaque(true);
        jMenuItemBlackWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBlackWhiteActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemBlackWhite);

        jMenuItemBlueWhite.setBackground(new java.awt.Color(27, 82, 128));
        jMenuItemBlueWhite.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItemBlueWhite.setForeground(new java.awt.Color(255, 255, 255));
        jMenuItemBlueWhite.setText("Blue and White");
        jMenuItemBlueWhite.setOpaque(true);
        jMenuItemBlueWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBlueWhiteActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemBlueWhite);

        jMenuItemGreenWhite.setBackground(new java.awt.Color(30, 97, 66));
        jMenuItemGreenWhite.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItemGreenWhite.setForeground(new java.awt.Color(255, 255, 255));
        jMenuItemGreenWhite.setText("Green and White");
        jMenuItemGreenWhite.setOpaque(true);
        jMenuItemGreenWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGreenWhiteActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemGreenWhite);

        jMenuItemWood.setBackground(new java.awt.Color(100, 41, 16));
        jMenuItemWood.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItemWood.setForeground(new java.awt.Color(255, 255, 255));
        jMenuItemWood.setText("Wooden");
        jMenuItemWood.setOpaque(true);
        jMenuItemWood.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWoodActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemWood);

        jMenu1.add(jMenu4);

        jMenuHistoryOption.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/history.png"))); // NOI18N
        jMenuHistoryOption.setText("Enable History Button");
        jMenuHistoryOption.setFocusable(false);
        jMenuHistoryOption.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuHistoryOption.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuHistoryOptionMouseClicked(evt);
            }
        });
        jMenu1.add(jMenuHistoryOption);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/clock.png"))); // NOI18N
        jMenu2.setText("Reset Timers");
        jMenu2.setFocusable(false);
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenu2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/OtherImages/back.png"))); // NOI18N
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jMenu2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jMenu2MouseExited(evt);
            }
        });
        jMenu1.add(jMenu2);

        jMenuBarGame.add(jMenu1);

        jMenuAbout2.setText("About");
        jMenuAbout2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuAbout2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuAboutMouseClicked(evt);
            }
        });
        jMenuBarGame.add(jMenuAbout2);

        setJMenuBar(jMenuBarGame);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonShowHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                        .addContainerGap(20, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jButtonShowHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(22, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Displays a new frame in which you select the piece to promote the pawn
     * to. then it calls {@code RevivePiece}.
     *
     * @param player
     * @param x
     * @param y
     */
    void PawnEdge(final Player player, final int x, final int y) {
        jFramePromotion.setSize(585, 390);
        if (player.getPlayerColor() == PlayerType.Black) {
            jLabelSelectPawn.setIcon(pi.BlackPawn);
            jLabelSelectQueen.setIcon(pi.BlackQueen);
            jLabelSelectBishop.setIcon(pi.BlackBishop);
            jLabelSelectKnight.setIcon(pi.BlackKnight);
            jLabelSelectRook.setIcon(pi.BlackRook);
        } else {
            jLabelSelectPawn.setIcon(pi.WhitePawn);
            jLabelSelectQueen.setIcon(pi.WhiteQueen);
            jLabelSelectBishop.setIcon(pi.WhiteBishop);
            jLabelSelectKnight.setIcon(pi.WhiteKnight);
            jLabelSelectRook.setIcon(pi.WhiteRook);
        }
        buttonGroup1.clearSelection();
        jFramePromotion.setVisible(true);

        jButtonRevive.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JRadioButton[] jrbs = {jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4, jRadioButton5};
                if (buttonGroup1.getSelection() != null) {
                    for (int i = 0; i < jrbs.length; i++) {
                        if (jrbs[i].isSelected()) {
                            jFramePromotion.setVisible(false);
                            switch (i) {
                                case 0:
                                    RevivePawn(PieceType.Pawn, player, x, y);
                                    break;
                                case 1:
                                    RevivePawn(PieceType.Queen, player, x, y);
                                    break;
                                case 2:
                                    RevivePawn(PieceType.Knight, player, x, y);
                                    break;
                                case 3:
                                    RevivePawn(PieceType.Rook, player, x, y);
                                    break;
                                case 4:
                                    RevivePawn(PieceType.Bishop, player, x, y);
                                    break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(jFramePromotion, "Please select a piece type.",
                            "No Piece Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    /**
     * Removes the {@code Pawn} from the edge and creates a new
     * {@code  Piece}-child at that location.
     *
     * @param piece the type of piece
     * @param player which player possesses the pawn
     * @param x the x location
     * @param y the y location
     * @see Pawn
     * @see Piece
     */
    void RevivePawn(PieceType piece, Player player, int x, int y) {
        board.gameBoard[y][x] = null;
        switch (piece) {
            case Bishop:
                board.gameBoard[y][x] = new Bishop(x, y, player, board);
                break;
            case Knight:
                board.gameBoard[y][x] = new Knight(x, y, player, board);
                break;
            case Pawn:
                board.gameBoard[y][x] = new Pawn(x, y, player, board);
                break;
            case Queen:
                board.gameBoard[y][x] = new Queen(x, y, player, board);
                break;
            case Rook:
                board.gameBoard[y][x] = new Rook(x, y, player, board);
                break;
        }

        for (int i = 0; i < board.gameBoard.length; i++) {
            for (int j = 0; j < board.gameBoard[0].length; j++) {
                drawBoard(board.gameBoard, i, j);
                addMouseListeners(y, x);
            }
        }
        checkForCheckmate(board.gameBoard);
        updateTurnMarker();
    }

    private void jLabelSelectPawnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSelectPawnMouseClicked
        jRadioButton1.setSelected(true);
    }//GEN-LAST:event_jLabelSelectPawnMouseClicked

    private void jLabelSelectQueenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSelectQueenMouseClicked
        jRadioButton2.setSelected(true);
    }//GEN-LAST:event_jLabelSelectQueenMouseClicked

    private void jLabelSelectKnightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSelectKnightMouseClicked
        jRadioButton3.setSelected(true);
    }//GEN-LAST:event_jLabelSelectKnightMouseClicked

    private void jLabelSelectRookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSelectRookMouseClicked
        jRadioButton4.setSelected(true);
    }//GEN-LAST:event_jLabelSelectRookMouseClicked

    private void jLabelSelectBishopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSelectBishopMouseClicked
        jRadioButton5.setSelected(true);
    }//GEN-LAST:event_jLabelSelectBishopMouseClicked

    PlayerType PlayerTimerStopped;

    private void jButtonShowHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowHistoryActionPerformed
        try {
            if (trailingRed.isRunning()) {
                trailingRed.stop();
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        try {
            if (colorSwap.isRunning()) {
                colorSwap.stop();
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        resetTileBackground();

        jFrameHistory.setSize(269, 400);
        jFrameHistory.setVisible(true);
        jFrameHistory.setLocation(this.getX() + this.getWidth(), this.getY());

        DefaultListModel<String> dlm = new DefaultListModel<>();
        dlm.addElement("Start-Board");
        for (int i = 1; i < history.size(); i++) {
            dlm.addElement("Move " + i);
        }
        jListHistory.setModel(dlm);

        if (WhiteClock.isRunning()) {
            PlayerTimerStopped = PlayerType.White;
            WhiteClock.stop();
        } else if (BlackClock.isRunning()) {
            PlayerTimerStopped = PlayerType.Black;
            BlackClock.stop();
        }

    }//GEN-LAST:event_jButtonShowHistoryActionPerformed

    private void jListHistoryValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListHistoryValueChanged
        int index = jListHistory.getSelectedIndex();
        if (index != -1) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    drawBoard(history.get(index), i, j);
                    try {
                        TileMatrix[i][j].removeMouseListener(TileMatrix[i][j].getMouseListeners()[0]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            }
        }
    }//GEN-LAST:event_jListHistoryValueChanged

    private void jFrameHistoryWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jFrameHistoryWindowClosing
        // TODO add your handling code here:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                drawBoard(board.gameBoard, i, j);
                addMouseListeners(i, j);
            }
        }
        if (PlayerTimerStopped != null) {
            if (PlayerTimerStopped == PlayerType.Black) {
                BlackClock.start();
            } else if (PlayerTimerStopped == PlayerType.White) {
                WhiteClock.start();
            }

//            if (jListHistory.getSelectedIndex() != -1) {
//                if ((jListHistory.getSelectedIndex() + 1) % 2 == 0) {
//                    turn = PlayerType.Black;
//                } else {
//                    turn = PlayerType.White;
//                }
//                updateTurnMarker();
//            }
        }

    }//GEN-LAST:event_jFrameHistoryWindowClosing

    private void jButtonRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRestoreActionPerformed
        if (!jListHistory.isSelectionEmpty()) {
            final int SELECTIONINDEX = jListHistory.getSelectedIndex();
            board.gameBoard = null;
            board.gameBoard = Board.copyValueOfGameboard(history.get(SELECTIONINDEX));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board.gameBoard[i][j] != null) {
                        board.gameBoard[i][j].setLocation(j, i);
                    }
                    drawBoard(board.gameBoard, i, j);
                    addMouseListeners(i, j);
                }
            }

            if (jListHistory.getSelectedIndex() != -1) {
                System.out.println(jListHistory.getSelectedIndex());
                if ((jListHistory.getSelectedIndex() + 1) % 2 == 0) {
                    turn = PlayerType.Black;

                } else {
                    turn = PlayerType.White;
                }
                updateTurnMarker();
            }

            for (int i = history.size() - 1; i > SELECTIONINDEX; i--) {
                history.remove(i);
            }
            DefaultListModel<String> dlm = new DefaultListModel<>();
            for (int i = 1; i < history.size(); i++) {
                dlm.addElement("Move " + i);
            }
            jListHistory.setModel(dlm);

//            if (jListHistory.getSelectedIndex() != -1) {
//                System.out.println(jListHistory.getSelectedIndex());
//                if ((jListHistory.getSelectedIndex() + 1) % 2 == 0) {
//                    turn = PlayerType.Black;
//                } else {
//                    turn = PlayerType.White;
//                }
//                updateTurnMarker();
//            }
            jFrameHistory.dispatchEvent(new WindowEvent(jFrameHistory, WindowEvent.WINDOW_CLOSING));
        } else {
            JOptionPane.showMessageDialog(jFrameHistory, "No state was selected. "
                    + "Please select one from the list");
        }

    }//GEN-LAST:event_jButtonRestoreActionPerformed

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed

        this.setVisible(true);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                drawBoard(board.gameBoard, i, j);
                addMouseListeners(i, j);
            }
        }

        jLabelBlackClock.setVisible(true);
        jLabelWhiteClock.setVisible(true);
        jLabelTurnMarker.setVisible(true);
        if (HistoryCheckBox) {
            jButtonShowHistory.setVisible(true);
        }

        WhiteClock.start();
        jCheckBoxHistory.setVisible(false);
        jButtonStart.setVisible(false);
        jFrameMainMenu.setVisible(false);
    }//GEN-LAST:event_jButtonStartActionPerformed

    private void jCheckBoxHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxHistoryActionPerformed
        HistoryCheckBox = !HistoryCheckBox;
    }//GEN-LAST:event_jCheckBoxHistoryActionPerformed

    private void jMenuAboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuAboutMouseClicked
        jMenuAbout.setSelected(false);
        jMenuAbout2.setSelected(false);
        JOptionPane.showMessageDialog(jFrameMainMenu, "Made by Ido Zaks 2018\nNote that the game doesn't feature castling (הצרחה)");
    }//GEN-LAST:event_jMenuAboutMouseClicked

    private void jMenuReturnToMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuReturnToMenuMouseClicked
        resetGame();
        jCheckBoxHistory.setSelected(false);
        jFrameMainMenu.setVisible(true);
        jButtonStart.setVisible(true);
        jCheckBoxHistory.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jMenuReturnToMenuMouseClicked

    private void jMenuResetGameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuResetGameMouseClicked
        resetGame();
        WhiteClock.start();
    }//GEN-LAST:event_jMenuResetGameMouseClicked

    private void jMenuHistoryOptionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuHistoryOptionMouseClicked
        if (!jButtonShowHistory.isVisible()) {
            jButtonShowHistory.setVisible(true);
            jMenuHistoryOption.setText("Disable History Button");
        } else {
            jButtonShowHistory.setVisible(false);
            jMenuHistoryOption.setText("Enable History Button");
        }
        jMenu1.setPopupMenuVisible(false);
        jMenu1.setSelected(false);
    }//GEN-LAST:event_jMenuHistoryOptionMouseClicked

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        jMenu1.setPopupMenuVisible(false);
        jMenu1.setSelected(false);
        BlackMinutes = 0;
        BlackSeconds = 0;
        WhiteMinutes = 0;
        WhiteSeconds = 0;
        jLabelBlackClock.setText(null);
        jLabelWhiteClock.setText(null);
        if (turn == PlayerType.Black) {
            WhiteClock.stop();
            BlackClock.restart();
        } else {
            WhiteClock.restart();
            BlackClock.stop();
        }

    }//GEN-LAST:event_jMenu2MouseClicked

    private void resetGame() {
        BlackCheck = false;
        WhiteCheck = false;

        myColors = new BlackAndWhite();

        board.gameBoard = null;
        board.gameBoard = new Piece[8][8];
        board.gameBoard = Board.PopulateGameBoard(board, blackPlayer, whitePlayer);
        resetTileBackground();
        turn = PlayerType.White;
        updateTurnMarker();
        for (int i = history.size() - 1; i > 0; i--) {
            history.remove(i);
        }
        if (jFrameHistory.isVisible()) {
            jFrameHistory.setVisible(false);
        }
        trailingRed.stop();
        colorSwap.stop();
        BlackSeconds = 0;
        BlackMinutes = 0;
        WhiteSeconds = 0;
        WhiteMinutes = 0;
        WhiteClock.stop();
        BlackClock.stop();
        jLabelBlackClock.setText(null);
        jLabelWhiteClock.setText(null);
        jLabelCheck.setText(null);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                drawBoard(board.gameBoard, i, j);
                resetTileBackground();
                addMouseListeners(i, j);
            }
        }
        jMenu1.setPopupMenuVisible(false);
        jMenu1.setSelected(false);
    }

    private void jMenu2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseEntered
        jMenu2.setIcon(new ImageIcon(getClass().getResource("/OtherImages/flippedClock.png")));
    }//GEN-LAST:event_jMenu2MouseEntered

    private void jMenu2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseExited
        jMenu2.setIcon(new ImageIcon(getClass().getResource("/OtherImages/clock.png")));
    }//GEN-LAST:event_jMenu2MouseExited

    private void jMenu1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseEntered
        if (!jButtonShowHistory.isVisible()) {
            jMenuHistoryOption.setText("Enable History Button");
        } else {
            jMenuHistoryOption.setText("Disable History Button");
        }
    }//GEN-LAST:event_jMenu1MouseEntered

    private void jMenuItemBlackWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBlackWhiteActionPerformed
        myColors = new BlackAndWhite();
        resetTileBackground();
        focusedPiece = null;
        focusedPieceX = -1;
        focusedPieceY = -1;
    }//GEN-LAST:event_jMenuItemBlackWhiteActionPerformed

    private void jMenuItemBlueWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBlueWhiteActionPerformed
        myColors = new BlueAndWhite();
        resetTileBackground();
        focusedPiece = null;
        focusedPieceX = -1;
        focusedPieceY = -1;
    }//GEN-LAST:event_jMenuItemBlueWhiteActionPerformed

    private void jMenuItemGreenWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGreenWhiteActionPerformed
        myColors = new GreenAndWhite();
        resetTileBackground();
        focusedPiece = null;
        focusedPieceX = -1;
        focusedPieceY = -1;
    }//GEN-LAST:event_jMenuItemGreenWhiteActionPerformed

    private void jMenuItemWoodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWoodActionPerformed
        myColors = new Wood();
        resetTileBackground();
        focusedPiece = null;
        focusedPieceX = -1;
        focusedPieceY = -1;
    }//GEN-LAST:event_jMenuItemWoodActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChessGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChessGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChessGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChessGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChessGUI();
            }
        });
    }

    void swapColors() {
        for (int i = 0; i < TileMatrix.length; i++) {
            for (int j = 0; j < TileMatrix[i].length; j++) {
                if (TileMatrix[i][j].getBackground().equals(myColors.darkColor)) {
                    TileMatrix[i][j].setBackground(myColors.brightColor);
                } else if (TileMatrix[i][j].getBackground().equals(myColors.brightColor)) {
                    TileMatrix[i][j].setBackground(myColors.darkColor);
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel boardTile1;
    private javax.swing.JLabel boardTile10;
    private javax.swing.JLabel boardTile11;
    private javax.swing.JLabel boardTile12;
    private javax.swing.JLabel boardTile13;
    private javax.swing.JLabel boardTile14;
    private javax.swing.JLabel boardTile15;
    private javax.swing.JLabel boardTile16;
    private javax.swing.JLabel boardTile17;
    private javax.swing.JLabel boardTile18;
    private javax.swing.JLabel boardTile19;
    private javax.swing.JLabel boardTile2;
    private javax.swing.JLabel boardTile20;
    private javax.swing.JLabel boardTile21;
    private javax.swing.JLabel boardTile22;
    private javax.swing.JLabel boardTile23;
    private javax.swing.JLabel boardTile24;
    private javax.swing.JLabel boardTile25;
    private javax.swing.JLabel boardTile26;
    private javax.swing.JLabel boardTile27;
    private javax.swing.JLabel boardTile28;
    private javax.swing.JLabel boardTile29;
    private javax.swing.JLabel boardTile3;
    private javax.swing.JLabel boardTile30;
    private javax.swing.JLabel boardTile31;
    private javax.swing.JLabel boardTile32;
    private javax.swing.JLabel boardTile33;
    private javax.swing.JLabel boardTile34;
    private javax.swing.JLabel boardTile35;
    private javax.swing.JLabel boardTile36;
    private javax.swing.JLabel boardTile37;
    private javax.swing.JLabel boardTile38;
    private javax.swing.JLabel boardTile39;
    private javax.swing.JLabel boardTile4;
    private javax.swing.JLabel boardTile40;
    private javax.swing.JLabel boardTile41;
    private javax.swing.JLabel boardTile42;
    private javax.swing.JLabel boardTile43;
    private javax.swing.JLabel boardTile44;
    private javax.swing.JLabel boardTile45;
    private javax.swing.JLabel boardTile46;
    private javax.swing.JLabel boardTile47;
    private javax.swing.JLabel boardTile48;
    private javax.swing.JLabel boardTile49;
    private javax.swing.JLabel boardTile5;
    private javax.swing.JLabel boardTile50;
    private javax.swing.JLabel boardTile51;
    private javax.swing.JLabel boardTile52;
    private javax.swing.JLabel boardTile53;
    private javax.swing.JLabel boardTile54;
    private javax.swing.JLabel boardTile55;
    private javax.swing.JLabel boardTile56;
    private javax.swing.JLabel boardTile57;
    private javax.swing.JLabel boardTile58;
    private javax.swing.JLabel boardTile59;
    private javax.swing.JLabel boardTile6;
    private javax.swing.JLabel boardTile60;
    private javax.swing.JLabel boardTile61;
    private javax.swing.JLabel boardTile62;
    private javax.swing.JLabel boardTile63;
    private javax.swing.JLabel boardTile64;
    private javax.swing.JLabel boardTile7;
    private javax.swing.JLabel boardTile8;
    private javax.swing.JLabel boardTile9;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonRestore;
    private javax.swing.JButton jButtonRevive;
    private javax.swing.JButton jButtonShowHistory;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JCheckBox jCheckBoxHistory;
    private javax.swing.JFrame jFrameHistory;
    private javax.swing.JFrame jFrameMainMenu;
    private javax.swing.JFrame jFramePromotion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelBackground;
    private javax.swing.JLabel jLabelBlackClock;
    private javax.swing.JLabel jLabelCheck;
    private javax.swing.JLabel jLabelSelectBishop;
    private javax.swing.JLabel jLabelSelectKnight;
    private javax.swing.JLabel jLabelSelectPawn;
    private javax.swing.JLabel jLabelSelectQueen;
    private javax.swing.JLabel jLabelSelectRook;
    private javax.swing.JLabel jLabelTurnMarker;
    private javax.swing.JLabel jLabelWhiteClock;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JList jListHistory;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenuAbout;
    private javax.swing.JMenu jMenuAbout2;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuBar jMenuBarGame;
    private javax.swing.JMenu jMenuHistoryOption;
    private javax.swing.JMenuItem jMenuItemBlackWhite;
    private javax.swing.JMenuItem jMenuItemBlueWhite;
    private javax.swing.JMenuItem jMenuItemGreenWhite;
    private javax.swing.JMenuItem jMenuItemWood;
    private javax.swing.JMenu jMenuResetGame;
    private javax.swing.JMenu jMenuReturnToMenu;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelBack;
    private javax.swing.JPanel jPanelFore;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables

    private void Victory() {
        if (BlackCheck) {
            jLabelCheck.setText(null);
            JOptionPane.showMessageDialog(null, new ImageIcon(getClass().getResource("/VictoryIcons/WhiteVictory.png")),
                    "Victory!", JOptionPane.INFORMATION_MESSAGE);
        } else if (WhiteCheck) {
            jLabelCheck.setText(null);
            JOptionPane.showMessageDialog(null, new ImageIcon(getClass().getResource("/VictoryIcons/BlackVictory.png")),
                    "Victory!", JOptionPane.INFORMATION_MESSAGE);
        }
        for (int i = 0; i < TileMatrix.length; i++) {
            for (int j = 0; j < TileMatrix.length; j++) {
                TileMatrix[i][j].removeMouseListener(TileMatrix[i][j].getMouseListeners()[0]);
            }
        }
        if (new Random().nextBoolean()) {
            colorSwap.start();
        } else {
            trailingRed.start();
        }

        BlackClock.stop();
        WhiteClock.stop();
    }

    /**
     * takes a gameBoard and checks if there is a checkmate
     *
     * @param testBoard the tested board.
     * @return
     */
    public boolean checkForCheckmate(Piece[][] testBoard) {
        boolean somethingChecked = false;
        for (int y = 0; y < 8; y++) { // x & y are the King's location on the board.
            for (int x = 0; x < 8; x++) {
                if (testBoard[y][x] != null && testBoard[y][x].getPieceType().equals(PieceType.King)) {
                    for (int i = 0; i < 8; i++) { // i & j represent the varying location of the hypothetical "attacking piece"
                        for (int j = 0; j < 8; j++) {
                            if (testBoard[i][j] != null && testBoard[i][j].isValidPath(x, y, testBoard, false)) {
                                switch (testBoard[y][x].getPlayerColor()) {
                                    case Black:
                                        BlackCheck = true;
                                        somethingChecked = true;
                                        break;
                                    case White:
                                        WhiteCheck = true;
                                        somethingChecked = true;
                                        break;
                                }
                            }
                        }
                    }
                    if (BlackCheck || WhiteCheck) {
                        if (board.PieceCannotMove(y, x)) {
                            if (!board.SomePieceCanDefend(y, x)) {
                                Victory();
                            }
                        }
                    }
                }
            }
        }
        if (!somethingChecked) {
            BlackCheck = false;
            WhiteCheck = false;
            return false;
        } else {
            return true;
        }
    }

}
