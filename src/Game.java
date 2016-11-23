import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Game implements MouseListener, Runnable, ActionListener {

	// constants
	public static final int WIDTH = 512;
	public static final int HEIGHT = 596;
	public static final String TITLE = "Chess by Jack Stenglein";

	// instance variables
	private static GraphicsController graphicsController_;
	private Board board_;
	private AI ai_;
	private boolean isSinglePlayer;
	private Timer timer_;
	private Space selectedSpace_;
	private ArrayList<Move> possibleMoves_;
	private Move enPassantMove_;
	private boolean isWhiteTurn_;
	private boolean isGamePlaying_;
	private boolean isGameOver_;
	private Thread thread;
	private boolean isRunning;
	private int threadDelay = 17;
	private int startTime_;
	private int timeBack_;

	// creates the frame and instantiates a new Game object
	public static void main(String[] args) {
		final Game game = new Game();
		graphicsController_.renderPieces();
		game.start();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					setUpJFrame(game);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	// creates the board in its initial position
	// creates a new GraphicsController
	public Game() {
		isWhiteTurn_ = true;
		isGamePlaying_ = false;
		isGameOver_ = false;
		startTime_ = 300000;
		board_ = new Board();
		board_.setPieces(createPieces(false), createPieces(true));
		ai_ = new AI(board_, false, 0);
		isSinglePlayer = true;
		timer_ = new Timer(startTime_);
		graphicsController_ = new GraphicsController(board_);
	}

	public void newGame() {
		// Reset all game variables
		isWhiteTurn_ = true;
		isGamePlaying_ = false;
		isGameOver_ = false;
		timer_.setTime(startTime_);

		// Reset game objects
		board_ = new Board();
		board_.setPieces(createPieces(false), createPieces(true));
		graphicsController_.setBoard(board_);
		graphicsController_.renderPieces();
	}

	private ArrayList<Piece> createPieces(boolean isWhite) {
		ArrayList<Piece> piecesToCreate = new ArrayList<Piece>();

		// create the pawns
		piecesToCreate.addAll(createPawns(isWhite));

		int row;
		if (isWhite)
			row = 7;
		else
			row = 0;

		// create the rooks
		Piece piece = new Piece(Piece.TYPE_ROOK, isWhite, row, 0);
		piecesToCreate.add(piece);
		board_.getSpace(row, 0).setPiece(piece);

		piece = new Piece(Piece.TYPE_ROOK, isWhite, row, 7);
		piecesToCreate.add(piece);
		board_.getSpace(row, 7).setPiece(piece);

		// create the knights
		piece = new Piece(Piece.TYPE_KNIGHT, isWhite, row, 1);
		piecesToCreate.add(piece);
		board_.getSpace(row, 1).setPiece(piece);

		piece = new Piece(Piece.TYPE_KNIGHT, isWhite, row, 6);
		piecesToCreate.add(piece);
		board_.getSpace(row, 6).setPiece(piece);

		// create the bishops
		piece = new Piece(Piece.TYPE_BISHOP, isWhite, row, 2);
		piecesToCreate.add(piece);
		board_.getSpace(row, 2).setPiece(piece);

		piece = new Piece(Piece.TYPE_BISHOP, isWhite, row, 5);
		piecesToCreate.add(piece);
		board_.getSpace(row, 5).setPiece(piece);

		// create the queen
		piece = new Piece(Piece.TYPE_QUEEN, isWhite, row, 3);
		piecesToCreate.add(piece);
		board_.getSpace(row, 3).setPiece(piece);

		// create the king
		piece = new Piece(Piece.TYPE_KING, isWhite, row, 4);
		piecesToCreate.add(piece);
		board_.getSpace(row, 4).setPiece(piece);

		return piecesToCreate;
	}

	private ArrayList<Piece> createPawns(boolean isWhite) {
		ArrayList<Piece> pawns = new ArrayList<Piece>();

		int row;
		if (isWhite)
			row = 6;
		else
			row = 1;

		for (int c = 0; c <= Board.MAX_COL; c++) {
			Piece pawn = new Piece(Piece.TYPE_PAWN, isWhite, row, c);
			pawns.add(pawn);
			board_.getSpace(row, c).setPiece(pawn);
		}

		return pawns;
	}

	public void start() {
		thread = new Thread(this);
		// System.out.println("\n \n");

		if (!thread.isAlive())
			thread.start();
	}

	public void run() {
		try {
			while (true) {
				thread.sleep(threadDelay);

				if (isGamePlaying_) {
					timer_.decrementTime(threadDelay, isWhiteTurn_);

					if (timer_.getRemainingTime(isWhiteTurn_) <= 0) {
						if (isWhiteTurn_)
							gameOver("Black wins by timeout!");
						else
							gameOver("White wins by timeout!");
					}
				}

				graphicsController_.renderTimer(timer_.getRemainingTime(false), timer_.getRemainingTime(true));
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		if (!isGamePlaying_)
			System.out.println("GAME OVER");
	}

	/**
	 * Helper method that displays a game over message with an option to start a new game or quit.
	 * <br>pre: message != null
	 * @param message
	 */
	private void gameOver(String message) {
		
		//check precondition
		if(message == null)
			throw new IllegalArgumentException("message may not be null.");
		
		isGameOver_ = true;
		isGamePlaying_ = false;
		Object[] options = { "New Game", "Close" };
		int selectedValue = JOptionPane.showOptionDialog(null, message, "Gave Over", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

		if (selectedValue == 0)
			newGame();
	}

	// switches the turn from black to white and vice versa
	// pre: none
	private void switchTurns() {
		timer_.incrementTime(timeBack_, isWhiteTurn_);
		isWhiteTurn_ = !isWhiteTurn_;
		if (MoveValidator.isCheckMate(isWhiteTurn_, board_)) {
			if (isWhiteTurn_)
				gameOver("Black wins by checkmate!");
			else
				gameOver("White wins by checkmate!");
		} else if (isSinglePlayer && isWhiteTurn_ == ai_.isWhite()) {
			System.out.println("AI is making the next move: ");
			Move move = ai_.getNextMove(ai_.getNextPiece());
			System.out.println(move);
			board_.makeOfficialMove(move);
			switchTurns();
		}
	}

	/**
	 * Five cases for a user clicking the mouse: <br>
	 * 1: They click a possible move <br>
	 * -Piece is moved <br>
	 * -Selected space/move options disappear
	 * 
	 * <br>
	 * 2: They click a space with a piece for the first time and it is that
	 * color's turn <br>
	 * -Selected space and move options appear for that piece <br>
	 * -They go away for other pieces
	 * 
	 * <br>
	 * 3: They click a space with a piece for the second time <br>
	 * -Selected space and move options disappear for that piece
	 * 
	 * <br>
	 * 4: They click an empty space that is not a move option <br>
	 * -Selected space and move options go away <br>
	 * -Nothing else appears
	 */
	@Override
	public void mouseClicked(MouseEvent event) {
		System.out.println("Mouse clicked: (" + event.getX() + ", " + event.getY() + ")");

		if (!isGameOver_) {
			int row = getRowOnBoard(event.getY());
			int col = getColOnBoard(event.getX());
			// System.out.println("Mouse clicked: (" + row + ", " + col + ")");

			if (row != -1 && col != -1) {
				Space clickedSpace = board_.getSpace(row, col);
				Piece piece = clickedSpace.getPiece();

				/*
				 * if (piece != null) { System.out.println("Clicked piece: " +
				 * piece); System.out.println("Attacked pieces: " +
				 * MoveValidator.findAttackedPieces(piece, board_));
				 * System.out.println("Attacking pieces: " +
				 * MoveValidator.findPiecesAttackingPiece(piece, board_));
				 * System.out.println("Find defending pieces: " +
				 * MoveValidator.findDefendingPieces(piece, board_)); }
				 */

				// CASE 1
				Move selectedMove = null;
				if (possibleMoves_ != null) {
					for (int i = 0; i < possibleMoves_.size(); i++)
						if (possibleMoves_.get(i).containsDestination(clickedSpace)) {
							selectedMove = possibleMoves_.get(i);
							break;
						}
				}

				if (selectedMove != null) {
					board_.makeOfficialMove(selectedMove);
					deselectAllSpaces();
					switchTurns();
					possibleMoves_ = null;
				}
				// CASE 2
				else if (piece != null && clickedSpace != selectedSpace_
						&& ((piece.isWhite() && isWhiteTurn_) || (!piece.isWhite() && !isWhiteTurn_))) {
					// System.out.println("CASE 2");
					if (!isGamePlaying_)
						isGamePlaying_ = true;

					deselectAllSpaces();
					selectedSpace_ = clickedSpace;
					selectedSpace_.setSelected(true);
					highlightPossibleMoves(piece);
				} else {
					// CASE 3 and CASE 4 covered here
					// System.out.println("CASE 3/4");
					deselectAllSpaces();
				}
			}

			graphicsController_.renderPieces();
		}
	}

	// finds the possible moves for a piece
	// and highlights them for the next rendering
	// pre: piece cannot be null
	private void highlightPossibleMoves(Piece piece) {
		// check precondition
		if (piece == null)
			throw new IllegalArgumentException("Piece cannot be null");

		possibleMoves_ = MoveValidator.findLegalMoves(piece, board_, true);
		// System.out.println("Available moves pre check: " + possibleMoves_);

		for (int i = 0; i < possibleMoves_.size(); i++) {
			Move move = possibleMoves_.get(i);
			if (move.isCapture())
				move.getDestination().setTakingMove(true);
			else
				move.getDestination().setPossibleMove(true);
		}

		/*
		 * if (piece.getType() == Piece.TYPE_PAWN) { enPassantMove_ =
		 * MoveValidator.findAvailableEnPassantMove(piece, board_);
		 * 
		 * if (enPassantMove_ != null &&
		 * MoveValidator.isCheckAfterMove(enPassantMove_, board_))
		 * enPassantMove_ = null; else enPassantMove_.setTakingMove(true); }
		 */
	}

	// unhighlights all of the highlighted spaces
	// pre: none
	private void deselectAllSpaces() {
		if (selectedSpace_ != null) {
			selectedSpace_.setSelected(false);
			selectedSpace_ = null;
		}

		if (possibleMoves_ != null)
			for (int i = 0; i < possibleMoves_.size(); i++) {
				possibleMoves_.get(i).getDestination().setPossibleMove(false);
				possibleMoves_.get(i).getDestination().setTakingMove(false);
				possibleMoves_.remove(i);
				i--;
			}

		if (enPassantMove_ != null) {
			enPassantMove_.getDestination().setTakingMove(false);
			enPassantMove_ = null;
		}
	}

	// returns the row on the board that was clicked
	// pre: none
	private int getRowOnBoard(int yPos) {
		int row = -1;

		if (yPos - 68 < 512 && yPos - 68 > 0)
			row = (yPos - 68) / 64;

		return row;
	}

	// returns the col on the board that was clicked
	// pre: none
	private int getColOnBoard(int xPos) {
		int col = -1;

		if (xPos < 512)
			col = xPos / 64;

		return col;
	}

	/**
	 * Performs the various actions for the JMenuBarItems.
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("New Game"))
			newGame();
		else if (event.getActionCommand().equals("Single Player"))
			isSinglePlayer = true;
		else if (event.getActionCommand().equals("Two Player"))
			isSinglePlayer = false;
		else if (event.getActionCommand().equals("Quit"))
			System.exit(0);
		else if (event.getActionCommand().equals("5 min") && !isGamePlaying_) {
			startTime_ = 5 * 60 * 1000;
			timer_.setTime(startTime_);
			timeBack_ = 0;
		} else if (event.getActionCommand().equals("10 min") && !isGamePlaying_) {
			startTime_ = 10 * 60 * 1000;
			timer_.setTime(startTime_);
			timeBack_ = 0;
		} else if (event.getActionCommand().equals("30 min") && !isGamePlaying_) {
			startTime_ = 30 * 60 * 1000;
			timer_.setTime(startTime_);
			timeBack_ = 0;
		} else if (event.getActionCommand().equals("1 hour") && !isGamePlaying_) {
			startTime_ = 60 * 60 * 1000;
			timer_.setTime(startTime_);
			timeBack_ = 0;
		} else if (event.getActionCommand().equals("2 min | 1 sec back") && !isGamePlaying_) {
			startTime_ = 2 * 60 * 1000;
			timer_.setTime(startTime_);
			timeBack_ = 1 * 1000;
		} else if (event.getActionCommand().equals("3 min | 2 sec back") && !isGamePlaying_) {
			startTime_ = 3 * 60 * 1000;
			timer_.setTime(startTime_);
			timeBack_ = 2 * 1000;
		} else if (event.getActionCommand().equals("5 min | 5 sec back") && !isGamePlaying_) {
			startTime_ = 5 * 60 * 1000;
			timer_.setTime(startTime_);
			timeBack_ = 5 * 1000;
		} else if (event.getActionCommand().equals("Custom") && !isGamePlaying_)
			getCustomTime();
	}

	/**
	 * Helper method that allows the user to enter in a custom time for the
	 * game. <br>
	 * pre: none <br>
	 * post: set the starting time and amount of time back per move to the
	 * user's selection, if one is made
	 */
	private void getCustomTime() {
		// create and display a new multi input pane
		// if the user selects 'OK', set the new startTime and timeBack values
		JOptionPaneMultiInput timerInputPane = new JOptionPaneMultiInput();
		if (timerInputPane.display() == JOptionPane.OK_OPTION) {
			startTime_ = timerInputPane.getStartTime();
			timer_.setTime(startTime_);
			timeBack_ = timerInputPane.getTimeBack();
		}
	}

	/**
	 * Helper method that sets up the JFrame and JMenuBar for the specified
	 * game.<br>
	 * pre: game != null
	 * 
	 * @param game
	 *            The game to make the JFrame and JMenuBar for. May not be null.
	 * @throws Exception
	 */
	private static void setUpJFrame(Game game) throws Exception {

		// check precondition
		if (game == null)
			throw new IllegalArgumentException("Game may not be null.");

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// create the JFrame with the class constant dimensions
		// and title
		JFrame frame = new JFrame();
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		frame.setPreferredSize(dimension);
		frame.setMinimumSize(dimension);
		frame.setMaximumSize(dimension);
		frame.setResizable(false);
		frame.addMouseListener(game);
		frame.setTitle(TITLE);
		frame.add(graphicsController_);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.requestFocus();

		// Create the menu bar
		JMenuBar menuBar = new JMenuBar();

		/*
		 * GAME MENU: Contains options to start a new game, switch between
		 * single and two player and quit.
		 */
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGameItem = new JMenuItem("New Game");
		newGameItem.addActionListener(game);

		JMenu gameModeMenu = new JMenu("Mode");
		JMenuItem singlePlayerItem = new JMenuItem("Single Player");
		singlePlayerItem.addActionListener(game);
		JMenuItem twoPlayerItem = new JMenuItem("Two Player");
		twoPlayerItem.addActionListener(game);

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(game);

		gameModeMenu.add(singlePlayerItem);
		gameModeMenu.add(twoPlayerItem);
		gameMenu.add(newGameItem);
		gameMenu.add(gameModeMenu);
		gameMenu.add(quitItem);
		menuBar.add(gameMenu);
		// END GAME MENU

		/*
		 * TIMER MENU: Contains various presets, as well as a custom option that
		 * allows the user to enter a specific time.
		 */
		JMenu timerMenu = new JMenu("Timer");
		JMenu presetMenu = new JMenu("Presets");
		JMenuItem min5Item = new JMenuItem("5 min");
		min5Item.addActionListener(game);
		JMenuItem min10Item = new JMenuItem("10 min");
		min10Item.addActionListener(game);
		JMenuItem min30Item = new JMenuItem("30 min");
		min30Item.addActionListener(game);
		JMenuItem hourItem = new JMenuItem("1 hour");
		hourItem.addActionListener(game);
		JMenuItem min2sec1Item = new JMenuItem("2 min | 1 sec back");
		min2sec1Item.addActionListener(game);
		JMenuItem min3sec2Item = new JMenuItem("3 min | 2 sec back");
		min3sec2Item.addActionListener(game);
		JMenuItem min5sec5Item = new JMenuItem("5 min | 5 sec back");
		min5sec5Item.addActionListener(game);
		JMenuItem customItem = new JMenuItem("Custom");
		customItem.addActionListener(game);
		presetMenu.add(min5Item);
		presetMenu.add(min10Item);
		presetMenu.add(min30Item);
		presetMenu.add(hourItem);
		presetMenu.add(min2sec1Item);
		presetMenu.add(min3sec2Item);
		presetMenu.add(min5sec5Item);
		timerMenu.add(presetMenu);
		timerMenu.add(customItem);
		menuBar.add(timerMenu);
		// END TIMER MENU

		// Create and add the computer menu
		JMenu computerMenu = new JMenu("Computer");
		menuBar.add(computerMenu);

		frame.setJMenuBar(menuBar);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Nothing to do
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Nothing to do
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Nothing to do
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Nothing to do
	}
}
