import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SuppressWarnings("serial")
public class GraphicsController extends JPanel {

	// Constants for math with graphics
	public static final int HUD_BAR_HEIGHT = 20;
	public static final int WHITE_HUD_START_Y = 556;
	public static final int TIMER_START_X = 5;
	public static final int BLACK_TIMER_START_Y = 15;
	public static final int WHITE_TIMER_START_Y = 548;
	public static final int LOST_PIECE_START_X = 175;
	public static final int LOST_PIECE_X_DIFFERENCE = 15;
	public static final int LOST_PIECE_Y_WHITE = 3;
	public static final int LOST_PIECE_Y_BLACK = 535;

	// Image file names
	private static final String ACTIVE_PAWN_WHITE_FILENAME = "images/pawn_white.png";
	private static final String ACTIVE_KNIGHT_WHITE_FILENAME = "images/knight_white.png";
	private static final String ACTIVE_BISHOP_WHITE_FILENAME = "images/bishop_white.png";
	private static final String ACTIVE_ROOK_WHITE_FILENAME = "images/rook_white.png";
	private static final String ACTIVE_QUEEN_WHITE_FILENAME = "images/queen_white.png";
	private static final String ACTIVE_KING_WHITE_FILENAME = "images/king_white.png";
	private static final String[] ACTIVE_WHITE_FILENAMES = new String[] { ACTIVE_PAWN_WHITE_FILENAME,
			ACTIVE_KNIGHT_WHITE_FILENAME, ACTIVE_BISHOP_WHITE_FILENAME, ACTIVE_ROOK_WHITE_FILENAME,
			ACTIVE_QUEEN_WHITE_FILENAME, ACTIVE_KING_WHITE_FILENAME };
	private static final String ACTIVE_PAWN_BLACK_FILENAME = "images/pawn_black.png";
	private static final String ACTIVE_KNIGHT_BLACK_FILENAME = "images/knight_black.png";
	private static final String ACTIVE_BISHOP_BLACK_FILENAME = "images/bishop_black.png";
	private static final String ACTIVE_ROOK_BLACK_FILENAME = "images/rook_black.png";
	private static final String ACTIVE_QUEEN_BLACK_FILENAME = "images/queen_black.png";
	private static final String ACTIVE_KING_BLACK_FILENAME = "images/king_black.png";
	private static final String[] ACTIVE_BLACK_FILENAMES = new String[] { ACTIVE_PAWN_BLACK_FILENAME,
			ACTIVE_KNIGHT_BLACK_FILENAME, ACTIVE_BISHOP_BLACK_FILENAME, ACTIVE_ROOK_BLACK_FILENAME,
			ACTIVE_QUEEN_BLACK_FILENAME, ACTIVE_KING_BLACK_FILENAME };
	private static final String CAPTURED_PAWN_WHITE_FILENAME = "images/pawn_white_lost.png";
	private static final String CAPTURED_KNIGHT_WHITE_FILENAME = "images/knight_white_lost.png";
	private static final String CAPTURED_BISHOP_WHITE_FILENAME = "images/bishop_white_lost.png";
	private static final String CAPTURED_ROOK_WHITE_FILENAME = "images/rook_white_lost.png";
	private static final String CAPTURED_QUEEN_WHITE_FILENAME = "images/queen_white_lost.png";
	private static final String[] CAPTURED_WHITE_FILENAMES = new String[] { CAPTURED_PAWN_WHITE_FILENAME,
			CAPTURED_KNIGHT_WHITE_FILENAME, CAPTURED_BISHOP_WHITE_FILENAME, CAPTURED_ROOK_WHITE_FILENAME,
			CAPTURED_QUEEN_WHITE_FILENAME };
	private static final String CAPTURED_PAWN_BLACK_FILENAME = "images/pawn_black_lost.png";
	private static final String CAPTURED_KNIGHT_BLACK_FILENAME = "images/knight_black_lost.png";
	private static final String CAPTURED_BISHOP_BLACK_FILENAME = "images/bishop_black_lost.png";
	private static final String CAPTURED_ROOK_BLACK_FILENAME = "images/rook_black_lost.png";
	private static final String CAPTURED_QUEEN_BLACK_FILENAME = "images/queen_black_lost.png";
	private static final String[] CAPTURED_BLACK_FILENAMES = new String[] { CAPTURED_PAWN_BLACK_FILENAME,
			CAPTURED_KNIGHT_BLACK_FILENAME, CAPTURED_BISHOP_BLACK_FILENAME, CAPTURED_ROOK_BLACK_FILENAME,
			CAPTURED_QUEEN_BLACK_FILENAME };

	// Loaded image files
	private static boolean hasLoadedImages = false;
	private static HashMap<Piece.PieceType, BufferedImage> capturedImagesWhite;
	private static HashMap<Piece.PieceType, BufferedImage> capturedImagesBlack;
	private static HashMap<Piece.PieceType, BufferedImage> activeImagesWhite;
	private static HashMap<Piece.PieceType, BufferedImage> activeImagesBlack;
	private static BufferedImage boardImage;

	// Instance variables
	private Board board;
	private String blackTimer;
	private String whiteTimer;
	
	
	/**
	 * Loads the board image, as well as the active and captured piece images, into memory.
	 * Uses the passed object's class to get the resources.
	 * 
	 * @param object The object to use to get the resources. Must not be null.
	 */
	public static void loadImages(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("The object must not be null.");
		}
		
		activeImagesWhite = new HashMap<Piece.PieceType, BufferedImage>(Piece.PieceType.values().length);
		activeImagesBlack = new HashMap<Piece.PieceType, BufferedImage>(Piece.PieceType.values().length);
		capturedImagesWhite = new HashMap<Piece.PieceType, BufferedImage>(Piece.PieceType.values().length - 1);
		capturedImagesBlack = new HashMap<Piece.PieceType, BufferedImage>(Piece.PieceType.values().length - 1);

		try {
			Piece.PieceType[] types = Piece.PieceType.values();
			for (int i = 0; i < types.length - 1; i++) {
				activeImagesWhite.put(types[i], ImageIO.read(object.getClass().getResource(ACTIVE_WHITE_FILENAMES[i])));
				activeImagesBlack.put(types[i], ImageIO.read(object.getClass().getResource(ACTIVE_BLACK_FILENAMES[i])));
				capturedImagesWhite.put(types[i], ImageIO.read(object.getClass().getResource(CAPTURED_WHITE_FILENAMES[i])));
				capturedImagesBlack.put(types[i], ImageIO.read(object.getClass().getResource(CAPTURED_BLACK_FILENAMES[i])));
			}
			activeImagesWhite.put(types[types.length - 1], ImageIO.read(object.getClass().getResource(ACTIVE_WHITE_FILENAMES[types.length - 1])));
			activeImagesBlack.put(types[types.length - 1], ImageIO.read(object.getClass().getResource(ACTIVE_BLACK_FILENAMES[types.length - 1])));
			boardImage = ImageIO.read(object.getClass().getResource("images/board.png"));
			hasLoadedImages = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the captured image associated with the given piece type and color.
	 * Returns null for PieceType.KING, as a king cannot be captured.
	 * <br>pre: GraphicsController.loadImages() must be called at least once before calling this method.
	 * 
	 * @param type The type of the piece.
	 * @param isWhite A boolean indicating whether the image should be for white or black pieces.
	 * @return The captured image associated with the piece type and color.
	 */
	public static Image getCapturedImage(Piece.PieceType type, boolean isWhite) {
		if (!hasLoadedImages) {
			throw new IllegalStateException("GraphicsController.loadImages() must be called before invoking getCapturedImage()");
		}
		return isWhite ? capturedImagesWhite.get(type) : capturedImagesBlack.get(type);
	}
	
	/**
	 * Returns the active image associated with the given piece type and color.
	 * <br>pre: GraphicsController.loadImages() must be called at least once before calling this method.
	 * 
	 * @param type The type of the piece.
	 * @param isWhite A boolean indicating whether the image should be for white or black pieces.
	 * @return The active image associated with the piece type and color.
	 */
	public static BufferedImage getActiveImage(Piece.PieceType type, boolean isWhite) {
		if (!hasLoadedImages) {
			throw new IllegalStateException("GraphicsController.loadImages() must be called before invoking getActiveImage()");
		}
		return isWhite ? activeImagesWhite.get(type) : activeImagesBlack.get(type);
	}

	/**
	 * Creates a new GraphicsController object that paints the specified board.
	 * 
	 * @param board The board to paint. Can be null.
	 */
	public GraphicsController(Board board) {
		setBoard(board);
	}

	/**
	 * Sets which board object the GraphicController will paint.
	 * 
	 * @param board The board to paint. Can be null.
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Repaints the board, without calculating new timer values.
	 */
	public void renderPieces() {
		repaint();
	}

	/**
	 * Repaints the board, after calculating new timer values.
	 * 
	 * @param blackTimeLeft The time left for black.
	 * @param whiteTimeLeft The time left for white.
	 */
	public void renderTimer(int blackTimeLeft, int whiteTimeLeft) {
		getTimerValues(blackTimeLeft, whiteTimeLeft);
		repaint();
	}

	/**
	 * Converts an amount of time in milliseconds to minutes and seconds.
	 * 
	 * @param blackTimeLeft The time left for black.
	 * @param whiteTimeLeft The time left for white.
	 */
	private void getTimerValues(int blackTimeLeft, int whiteTimeLeft) {
		blackTimer = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(blackTimeLeft),
				TimeUnit.MILLISECONDS.toSeconds(blackTimeLeft)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(blackTimeLeft)));

		whiteTimer = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(whiteTimeLeft),
				TimeUnit.MILLISECONDS.toSeconds(whiteTimeLeft)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(whiteTimeLeft)));
	}

	/**
	 * Paints the HUD bars and the board.
	 * 
	 * @param graphic The graphics context in which to draw. Cannot be null.
	 */
	public void paintComponent(Graphics graphic) {
		if (graphic == null) {
			throw new IllegalArgumentException("The graphics context cannot be  null.");
		}
		super.paintComponent(graphic);
		graphic.drawImage(boardImage, 0, HUD_BAR_HEIGHT, null);
		paintHUDBars(graphic);
		board.paint(graphic);
	}

	/**
	 * Paints the HUD bars for both white and black.
	 * 
	 * @param graphic The graphics context in which to draw the bars. Must not be null.
	 */
	private void paintHUDBars(Graphics graphic) {
		// Paint the black HUD bar
		graphic.setColor(Color.LIGHT_GRAY);
		graphic.fillRect(0, 0, Game.WIDTH, HUD_BAR_HEIGHT);
		graphic.setColor(Color.BLACK);
		graphic.drawString("Time Remaining: " + blackTimer, TIMER_START_X, BLACK_TIMER_START_Y);

		// paint the white HUD bar
		graphic.setColor(Color.WHITE);
		graphic.fillRect(0, WHITE_HUD_START_Y, Game.WIDTH, HUD_BAR_HEIGHT);
		graphic.setColor(Color.BLACK);
		graphic.drawString("Time Remaining: " + whiteTimer, TIMER_START_X, WHITE_TIMER_START_Y);
	}
}
