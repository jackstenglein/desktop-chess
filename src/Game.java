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
	public static final int DEFAULT_GAME_TIME = 300_000; // 5 minutes
	public static final int BOARD_SIDE_LENGTH = 512;
	public static final int SPACE_SIDE_LENGTH = 64;
	
	// menu options
	public static final String NEW_GAME_OPTION = "New Game";
	public static final String SINGLE_PLAYER_OPTION = "Single Player";
	public static final String TWO_PLAYER_OPTION = "Two Player";
	public static final String QUIT_OPTION = "Quit";
	public static final String FIVE_MIN_OPTION = "5 min";
	public static final String TEN_MIN_OPTION = "10 min";
	public static final String THIRTY_MIN_OPTION = "30 min";
	public static final String ONE_HOUR_OPTION = "1 hour";
	public static final String TWO_BY_ONE_OPTION = "2 min | 1 sec back";
	public static final String THREE_BY_TWO_OPTION = "3 min | 2 sec back";
	public static final String FIVE_BY_FIVE_OPTION = "5 min | 5 sec back";
	public static final String CUSTOM_TIME_OPTION = "Custom";

	// instance variables
	private static GraphicsController graphicsController;
	private Board board;
	private AI ai;
	private boolean isSinglePlayer;
	private Timer timer;
	private Space selectedSpace;
	private ArrayList<Move> possibleMoves;
	private Move enPassantMove;
	private boolean isWhiteTurn;
	private boolean isGamePlaying;
	private boolean isGameOver;
	private Thread thread;
	private boolean isRunning;
	private int threadDelay = 17;
	private int startTime;
	private int timeBack;

	// creates the frame and instantiates a new Game object
	public static void main(String[] args) {
		final Game game = new Game();
		graphicsController.renderPieces();
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

	/**
	 * Creates a new Game object with default values.
	 */
	public Game() {
		isWhiteTurn = true;
		isGamePlaying = false;
		isGameOver = false;
		startTime = DEFAULT_GAME_TIME;
		board = new Board();
		board.setPieces(createPieces(false), createPieces(true));
		ai = new AI(board, false, 0);
		isSinglePlayer = true;
		timer = new Timer(startTime);
		graphicsController = new GraphicsController(board);
	}

	/**
	 * Resets the game state variables and objects.
	 */
	public void newGame() {
		// Reset all game variables
		isWhiteTurn = true;
		isGamePlaying = false;
		isGameOver = false;
		timer.setTime(startTime);

		// Reset game objects
		board = new Board();
		board.setPieces(createPieces(false), createPieces(true));
		graphicsController.setBoard(board);
		graphicsController.renderPieces();
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
		board.getSpace(row, 0).setPiece(piece);

		piece = new Piece(Piece.TYPE_ROOK, isWhite, row, 7);
		piecesToCreate.add(piece);
		board.getSpace(row, 7).setPiece(piece);

		// create the knights
		piece = new Piece(Piece.TYPE_KNIGHT, isWhite, row, 1);
		piecesToCreate.add(piece);
		board.getSpace(row, 1).setPiece(piece);

		piece = new Piece(Piece.TYPE_KNIGHT, isWhite, row, 6);
		piecesToCreate.add(piece);
		board.getSpace(row, 6).setPiece(piece);

		// create the bishops
		piece = new Piece(Piece.TYPE_BISHOP, isWhite, row, 2);
		piecesToCreate.add(piece);
		board.getSpace(row, 2).setPiece(piece);

		piece = new Piece(Piece.TYPE_BISHOP, isWhite, row, 5);
		piecesToCreate.add(piece);
		board.getSpace(row, 5).setPiece(piece);

		// create the queen
		piece = new Piece(Piece.TYPE_QUEEN, isWhite, row, 3);
		piecesToCreate.add(piece);
		board.getSpace(row, 3).setPiece(piece);

		// create the king
		piece = new Piece(Piece.TYPE_KING, isWhite, row, 4);
		piecesToCreate.add(piece);
		board.getSpace(row, 4).setPiece(piece);

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
			board.getSpace(row, c).setPiece(pawn);
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

				if (isGamePlaying) {
					timer.decrementTime(threadDelay, isWhiteTurn);

					if (timer.getRemainingTime(isWhiteTurn) <= 0) {
						if (isWhiteTurn)
							gameOver("Black wins by timeout!");
						else
							gameOver("White wins by timeout!");
					}
				}

				graphicsController.renderTimer(timer.getRemainingTime(false), timer.getRemainingTime(true));
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		if (!isGamePlaying)
			System.out.println("GAME OVER");
	}

	/**
	 * Displays a game over message with an option to start a new game or quit.
	 * <br>pre: message != null
	 * @param message
	 */
	private void gameOver(String message) {
		
		//check precondition
		if(message == null)
			throw new IllegalArgumentException("message may not be null.");
		
		isGameOver = true;
		isGamePlaying = false;
		Object[] options = { NEW_GAME_OPTION, "Close" };
		int selectedValue = JOptionPane.showOptionDialog(null, message, "Gave Over", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

		if (selectedValue == 0)
			newGame();
	}

	/** 
	 * Switches the turn from black to white and vice versa.
	 * <br>pre: none
	 */
	private void switchTurns() {
		timer.incrementTime(timeBack, isWhiteTurn);
		isWhiteTurn = !isWhiteTurn;
		if (MoveValidator.isCheckMate(isWhiteTurn, board)) {
			if (isWhiteTurn) {
				gameOver("Black wins by checkmate!");
			} else {
				gameOver("White wins by checkmate!");
			}
		} else if (isSinglePlayer && isWhiteTurn == ai.isWhite()) {
			//System.out.println("AI choice for next move: ");
			Move move = ai.getNextMove();
			//System.out.println(move);
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

		if (!isGameOver) {
			int row = getRowOnBoard(event.getY());
			int col = getColOnBoard(event.getX());
			// System.out.println("Mouse clicked: (" + row + ", " + col + ")");

			if (row != -1 && col != -1) {
				Space clickedSpace = board.getSpace(row, col);
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
				if (possibleMoves != null) {
					for (int i = 0; i < possibleMoves.size(); i++)
						if (possibleMoves.get(i).containsDestination(clickedSpace)) {
							selectedMove = possibleMoves.get(i);
							break;
						}
				}

				if (selectedMove != null) {
					board.makeOfficialMove(selectedMove);
					deselectAllSpaces();
					switchTurns();
					possibleMoves = null;
				}
				// CASE 2
				else if (piece != null && clickedSpace != selectedSpace
						&& ((piece.isWhite() && isWhiteTurn) || (!piece.isWhite() && !isWhiteTurn))) {
					// System.out.println("CASE 2");
					if (!isGamePlaying) {
						isGamePlaying = true;
					}

					deselectAllSpaces();
					selectedSpace = clickedSpace;
					selectedSpace.setSelected(true);
					highlightPossibleMoves(piece);
				} else {
					// CASE 3 and CASE 4 covered here
					// System.out.println("CASE 3/4");
					deselectAllSpaces();
				}
			}

			graphicsController.renderPieces();
		}
	}

	/**
	 * Finds the possible moves for a piece and highlights
	 * them for the next rendering.
	 * @param piece The piece to find moves for. Cannot be null.
	 * <br>pre: piece != null
	 */
	private void highlightPossibleMoves(Piece piece) {
		if (piece == null) {
			throw new IllegalArgumentException("Piece cannot be null");
		}

		possibleMoves = MoveValidator.findLegalMoves(piece, board, true);

		for (int i = 0; i < possibleMoves.size(); i++) {
			Move move = possibleMoves.get(i);
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

	/**
	 * Removes highlighting from all spaces.
	 * <br>pre: none
	 */
	private void deselectAllSpaces() {
		if (selectedSpace != null) {
			selectedSpace.setSelected(false);
			selectedSpace = null;
		}

		if (possibleMoves != null) {
			for (int i = 0; i < possibleMoves.size(); i++) {
				possibleMoves.get(i).getDestination().setPossibleMove(false);
				possibleMoves.get(i).getDestination().setTakingMove(false);
				possibleMoves.remove(i);
				i--;
			}
		}

		if (enPassantMove != null) {
			enPassantMove.getDestination().setTakingMove(false);
			enPassantMove = null;
		}
	}

	/**
	 * Returns the row on the board that was clicked.
	 * @param yPos The y-position in pixels of the click.
	 * @return The corresponding row on the board, or -1 if 
	 * the click did not occur on the board.
	 */
	private int getRowOnBoard(int yPos) {
		int row = -1;
		if (yPos - 68 < BOARD_SIDE_LENGTH && yPos - 68 > 0) {
			row = (yPos - 68) / SPACE_SIDE_LENGTH;
		}
		return row;
	}

	/**
	 * Returns the column on the board that was clicked.
	 * @param xPos The x-position in pixels of the click.
	 * @return The corresponding row on the board, or -1 if
	 * the click did not occur on the board.
	 */
	private int getColOnBoard(int xPos) {
		int col = -1;
		if (xPos >= 0 && xPos < 512) {
			col = xPos / 64;
		}
		return col;
	}

	/**
	 * Performs the various actions for the JMenuBarItems.
	 */
	public void actionPerformed(ActionEvent event) {
		final int SECONDS_PER_MINUTE = 60;
		final int MILLISECONDS_PER_SECOND = 1000;
		
		if (event.getActionCommand().equals(NEW_GAME_OPTION)) {
			newGame();
		} else if (event.getActionCommand().equals(SINGLE_PLAYER_OPTION)) {
			isSinglePlayer = true;
		} else if (event.getActionCommand().equals(TWO_PLAYER_OPTION)) {
			isSinglePlayer = false;
		} else if (event.getActionCommand().equals(QUIT_OPTION)) {
			System.exit(0);
		} else if (event.getActionCommand().equals(FIVE_MIN_OPTION) && !isGamePlaying) {
			startTime = 5 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
			timer.setTime(startTime);
			timeBack = 0;
		} else if (event.getActionCommand().equals(TEN_MIN_OPTION) && !isGamePlaying) {
			startTime = 10 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
			timer.setTime(startTime);
			timeBack = 0;
		} else if (event.getActionCommand().equals(THIRTY_MIN_OPTION) && !isGamePlaying) {
			startTime = 30 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
			timer.setTime(startTime);
			timeBack = 0;
		} else if (event.getActionCommand().equals(ONE_HOUR_OPTION) && !isGamePlaying) {
			startTime = 60 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
			timer.setTime(startTime);
			timeBack = 0;
		} else if (event.getActionCommand().equals(TWO_BY_ONE_OPTION) && !isGamePlaying) {
			startTime = 2 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
			timer.setTime(startTime);
			timeBack = 1 * MILLISECONDS_PER_SECOND;
		} else if (event.getActionCommand().equals(THREE_BY_TWO_OPTION) && !isGamePlaying) {
			startTime = 3 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
			timer.setTime(startTime);
			timeBack = 2 * MILLISECONDS_PER_SECOND;
		} else if (event.getActionCommand().equals(FIVE_BY_FIVE_OPTION) && !isGamePlaying) {
			startTime = 5 * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
			timer.setTime(startTime);
			timeBack = 5 * MILLISECONDS_PER_SECOND;
		} else if (event.getActionCommand().equals(CUSTOM_TIME_OPTION) && !isGamePlaying) {
			getCustomTime();
		}
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
			startTime = timerInputPane.getStartTime();
			timer.setTime(startTime);
			timeBack = timerInputPane.getTimeBack();
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
		frame.add(graphicsController);
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
		JMenuItem newGameItem = new JMenuItem(NEW_GAME_OPTION);
		newGameItem.addActionListener(game);

		JMenu gameModeMenu = new JMenu("Mode");
		JMenuItem singlePlayerItem = new JMenuItem(SINGLE_PLAYER_OPTION);
		singlePlayerItem.addActionListener(game);
		JMenuItem twoPlayerItem = new JMenuItem(TWO_PLAYER_OPTION);
		twoPlayerItem.addActionListener(game);

		JMenuItem quitItem = new JMenuItem(QUIT_OPTION);
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
		JMenuItem min5Item = new JMenuItem(FIVE_MIN_OPTION);
		min5Item.addActionListener(game);
		JMenuItem min10Item = new JMenuItem(TEN_MIN_OPTION);
		min10Item.addActionListener(game);
		JMenuItem min30Item = new JMenuItem(THIRTY_MIN_OPTION);
		min30Item.addActionListener(game);
		JMenuItem hourItem = new JMenuItem(ONE_HOUR_OPTION);
		hourItem.addActionListener(game);
		JMenuItem min2sec1Item = new JMenuItem(TWO_BY_ONE_OPTION);
		min2sec1Item.addActionListener(game);
		JMenuItem min3sec2Item = new JMenuItem(THREE_BY_TWO_OPTION);
		min3sec2Item.addActionListener(game);
		JMenuItem min5sec5Item = new JMenuItem(FIVE_BY_FIVE_OPTION);
		min5sec5Item.addActionListener(game);
		JMenuItem customItem = new JMenuItem(CUSTOM_TIME_OPTION);
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
