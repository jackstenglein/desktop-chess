import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.*;
import java.awt.BasicStroke;

public class GraphicsController extends JPanel {

	public static final int HUD_BAR_HEIGHT = 20;
	public static final int LOST_PIECE_START_X = 200;

	private Image boardImage_;

	// black images
	private Image blackPawnLostImage_;
	private Image blackKnightLostImage_;
	private Image blackBishopLostImage_;
	private Image blackRookLostImage_;
	private Image blackQueenLostImage_;

	// white images
	private Image whitePawnLostImage_;
	private Image whiteKnightLostImage_;
	private Image whiteBishopLostImage_;
	private Image whiteRookLostImage_;
	private Image whiteQueenLostImage_;

	private BufferedImage[] whiteImages_;
	private BufferedImage[] blackImages_;

	private Board board_;
	private String blackTimer_;
	private String whiteTimer_;

	public GraphicsController(Board board) {
		board_ = board;
		whiteImages_ = new BufferedImage[6];
		blackImages_ = new BufferedImage[6];

		try {
			whiteImages_[0] = ImageIO.read(this.getClass().getResource("images/pawn_white.png"));
			whiteImages_[1] = ImageIO.read(this.getClass().getResource("images/knight_white.png"));
			whiteImages_[2] = ImageIO.read(this.getClass().getResource("images/bishop_white.png"));
			whiteImages_[3] = ImageIO.read(this.getClass().getResource("images/rook_white.png"));
			whiteImages_[4] = ImageIO.read(this.getClass().getResource("images/queen_white.png"));
			whiteImages_[5] = ImageIO.read(this.getClass().getResource("images/king_white.png"));

			blackImages_[0] = ImageIO.read(this.getClass().getResource("images/pawn_black.png"));
			blackImages_[1] = ImageIO.read(this.getClass().getResource("images/knight_black.png"));
			blackImages_[2] = ImageIO.read(this.getClass().getResource("images/bishop_black.png"));
			blackImages_[3] = ImageIO.read(this.getClass().getResource("images/rook_black.png"));
			blackImages_[4] = ImageIO.read(this.getClass().getResource("images/queen_black.png"));
			blackImages_[5] = ImageIO.read(this.getClass().getResource("images/king_black.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		System.out.println("Class loarder: " + classLoader);
		InputStream inputBoard = classLoader.getResourceAsStream("images/board.png");
		// black lost pieces
		InputStream inputBlackPawn = classLoader.getResourceAsStream("images/pawn_black_lost.png");
		InputStream inputBlackKnight = classLoader.getResourceAsStream("images/knight_black_lost.png");
		InputStream inputBlackBishop = classLoader.getResourceAsStream("images/bishop_black_lost.png");
		InputStream inputBlackRook = classLoader.getResourceAsStream("images/rook_black_lost.png");
		InputStream inputBlackQueen = classLoader.getResourceAsStream("images/queen_black_lost.png");
		// white lost pieces
		InputStream inputWhitePawn = classLoader.getResourceAsStream("images/pawn_white_lost.png");
		InputStream inputWhiteKnight = classLoader.getResourceAsStream("images/knight_white_lost.png");
		InputStream inputWhiteBishop = classLoader.getResourceAsStream("images/bishop_white_lost.png");
		InputStream inputWhiteRook = classLoader.getResourceAsStream("images/rook_white_lost.png");
		InputStream inputWhiteQueen = classLoader.getResourceAsStream("images/queen_white_lost.png");

		try {
			boardImage_ = ImageIO.read(inputBoard);
			// black lost pieces
			blackPawnLostImage_ = ImageIO.read(inputBlackPawn);
			blackKnightLostImage_ = ImageIO.read(inputBlackKnight);
			blackBishopLostImage_ = ImageIO.read(inputBlackBishop);
			blackRookLostImage_ = ImageIO.read(inputBlackRook);
			blackQueenLostImage_ = ImageIO.read(inputBlackQueen);
			// white lost pieces
			whitePawnLostImage_ = ImageIO.read(inputWhitePawn);
			whiteKnightLostImage_ = ImageIO.read(inputWhiteKnight);
			whiteBishopLostImage_ = ImageIO.read(inputWhiteBishop);
			whiteRookLostImage_ = ImageIO.read(inputWhiteRook);
			whiteQueenLostImage_ = ImageIO.read(inputWhiteQueen);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBoard(Board board) {
		board_ = board;
	}

	public void renderPieces() {
		repaint();
	}

	public void renderTimer(int blackTimeLeft, int whiteTimeLeft) {
		// System.out.println("Render timer");
		getTimerValues(blackTimeLeft, whiteTimeLeft);
		repaint();
	}

	private void getTimerValues(int blackTimeLeft, int whiteTimeLeft) {
		blackTimer_ = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(blackTimeLeft),
				TimeUnit.MILLISECONDS.toSeconds(blackTimeLeft)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(blackTimeLeft)));

		whiteTimer_ = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(whiteTimeLeft),
				TimeUnit.MILLISECONDS.toSeconds(whiteTimeLeft)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(whiteTimeLeft)));

		// System.out.println("White timer: " + whiteTimer_);
	}

	public void paintComponent(Graphics graphic) {
		super.paintComponent(graphic);

		graphic.drawImage(boardImage_, 0, HUD_BAR_HEIGHT, null);
		paintHUDBars(graphic);
		paintBlackLostPieces(graphic);
		paintWhiteLostPieces(graphic);
		paintSpaces(graphic);
		paintPieces(board_.getBlackPieces(), graphic);
		paintPieces(board_.getWhitePieces(), graphic);
	}

	private void paintHUDBars(Graphics graphic) {
		// Paint the black HUD bar
		graphic.setColor(Color.LIGHT_GRAY);
		graphic.fillRect(0, 0, 512, HUD_BAR_HEIGHT);
		graphic.setColor(Color.BLACK);
		graphic.drawString("Time Remaining: " + blackTimer_, 5, 15);

		// paint the white HUD bar
		graphic.setColor(Color.WHITE);
		graphic.fillRect(0, 576 - HUD_BAR_HEIGHT, 512, HUD_BAR_HEIGHT);
		graphic.setColor(Color.BLACK);
		graphic.drawString("Time Remaining: " + whiteTimer_, 5, 548);
	}

	private void paintBlackLostPieces(Graphics graphic) {
		int blackLostPawns = board_.getBlackPawnsLost();
		for (int i = 0; i < blackLostPawns; i++) {
			graphic.drawImage(blackPawnLostImage_, LOST_PIECE_START_X + 15 * i, 3, null);
		}

		int blackLostKnights = board_.getBlackKnightsLost();
		for (int i = 0; i < blackLostKnights; i++) {
			graphic.drawImage(blackKnightLostImage_, LOST_PIECE_START_X + 15 * blackLostPawns + 15 * i, 3, null);
		}

		int blackLostBishops = board_.getBlackBishopsLost();
		for (int i = 0; i < blackLostBishops; i++) {
			graphic.drawImage(blackBishopLostImage_,
					LOST_PIECE_START_X + 15 * blackLostPawns + 15 * blackLostKnights + 17 * i, 3, null);
		}

		int blackLostRooks = board_.getBlackRooksLost();
		for (int i = 0; i < blackLostRooks; i++) {
			graphic.drawImage(blackRookLostImage_,
					LOST_PIECE_START_X + 15 * blackLostPawns + 15 * blackLostKnights + 17 * blackLostBishops + 15 * i,
					3, null);
		}

		int blackLostQueens = board_.getBlackQueensLost();
		for (int i = 0; i < blackLostQueens; i++) {
			graphic.drawImage(blackQueenLostImage_, LOST_PIECE_START_X + 15 * blackLostPawns + 15 * blackLostKnights
					+ 17 * blackLostBishops + 15 * blackLostRooks + 15 * i, 3, null);
		}
	}

	private void paintWhiteLostPieces(Graphics graphic) {
		int whiteLostPawns = board_.getWhitePawnsLost();
		for (int i = 0; i < whiteLostPawns; i++) {
			graphic.drawImage(whitePawnLostImage_, LOST_PIECE_START_X + 15 * i, 536, null);
		}

		int whiteLostKnights = board_.getWhiteKnightsLost();
		for (int i = 0; i < whiteLostKnights; i++) {
			graphic.drawImage(whiteKnightLostImage_, LOST_PIECE_START_X + 15 * whiteLostPawns + 15 * i, 536, null);
		}

		int whiteLostBishops = board_.getWhiteBishopsLost();
		for (int i = 0; i < whiteLostBishops; i++) {
			graphic.drawImage(whiteBishopLostImage_,
					LOST_PIECE_START_X + 15 * whiteLostPawns + 15 * whiteLostKnights + 17 * i, 536, null);
		}

		int whiteLostRooks = board_.getWhiteRooksLost();
		for (int i = 0; i < whiteLostRooks; i++) {
			graphic.drawImage(whiteRookLostImage_,
					LOST_PIECE_START_X + 15 * whiteLostPawns + 15 * whiteLostKnights + 17 * whiteLostBishops + 15 * i,
					536, null);
		}

		int whiteLostQueens = board_.getWhiteQueensLost();
		for (int i = 0; i < whiteLostQueens; i++) {
			graphic.drawImage(whiteQueenLostImage_, LOST_PIECE_START_X + 15 * whiteLostPawns + 15 * whiteLostKnights
					+ 17 * whiteLostBishops + 15 * whiteLostRooks + 15 * i, 536, null);
		}
	}

	private void paintPieces(ArrayList<Piece> piecesToDraw, Graphics graphic) {
		for (int i = 0; i < piecesToDraw.size(); i++) {
			Piece piece = piecesToDraw.get(i);
			if (!piece.isCaptured()) {
				BufferedImage image;
				if (piece.isWhite())
					image = whiteImages_[piece.getType() - 1];
				else
					image = blackImages_[piece.getType() - 1];

				int row = piece.getRow();
				int col = piece.getCol();
				int width = image.getWidth(null);
				int height = image.getHeight(null);

				int x = (col * 64) + (64 - width) / 2;
				int y = (row * 64) + (64 - height) / 2;

				graphic.drawImage(image, x, y + HUD_BAR_HEIGHT, null);
			}
		}
	}

	private void paintSpaces(Graphics graphic) {
		Graphics2D g2 = (Graphics2D) graphic;
		float thickness = 2;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(thickness));

		for (int r = 0; r <= Board.MAX_ROW; r++) {
			for (int c = 0; c <= Board.MAX_COL; c++) {
				Space space = board_.getSpace(r, c);
				if (space.isSelected()) {
					graphic.setColor(Color.GREEN);
					graphic.drawRect(space.getCol() * 64, space.getRow() * 64 + HUD_BAR_HEIGHT, 64, 64);
				} else if ((space.isPossibleMove() && space.getPiece() != null) || space.isTakingMove()) {
					graphic.setColor(Color.RED);
					graphic.drawRect(space.getCol() * 64, space.getRow() * 64 + HUD_BAR_HEIGHT, 64, 64);
				} else if (space.isPossibleMove()) {
					graphic.setColor(Color.BLUE);
					graphic.drawRect(space.getCol() * 64, space.getRow() * 64 + HUD_BAR_HEIGHT, 64, 64);
				}
			}
		}

		g2.setStroke(oldStroke);
	}

}
