import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Game extends JPanel{
	private static final int SIZE = 4;
	private static final Color BG_COLOR = new Color(0xbbada0);
	private static final String FONT_NAME = "Times New Roman";
	private static final int TILE_SIZE = 64;
	private static final int TILES_MARGIN = 16;
	private static final int GOAL = 2048;
	private static final int MIN = 2;
	private static final int MIN2 = 4;

	private Tile[] tiles;
	private boolean win;
	private boolean lose;
	private int score;

	public Game() {
		win = false;
		lose = false;
		score = 0;
		this.setPreferredSize(new Dimension(340, 400));
		this.setFocusable(true);
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					reset();
				} 
				if (!canMove()) {
					lose = true;
				}
				if (!win && !lose) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						left();						
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						right();						
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						down();
					} else if (e.getKeyCode() == KeyEvent.VK_UP) {
						up();
					}
				}

				if (!win && !canMove()) {
					lose = true;
				}

				repaint();
			}
		});
		reset();

	}

	public void reset() {
		score = 0; 
		win = false;
		lose = false;
		tiles = new Tile[SIZE * SIZE];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new Tile();
		}
		addTile();
		addTile();
	}

	public void left() {
		boolean needAdd = false;
		for (int i = 0; i < SIZE; i++) {
			Tile[] line = getLine(i);
			Tile[] merged = mergeLine(moveLine(line));
			setLine(i, merged);
			if (!needAdd && !compare(line, merged)) {
				needAdd = true;
			}
		}

		if (needAdd) {
			addTile();
		}
	}

	public void right() {
		tiles = rotate(180);
		left();
		tiles = rotate(180);
	}

	public void up() {
		tiles = rotate(270);
		left();
		tiles = rotate(90);
	}

	public void down() {
		tiles = rotate(90);
		left();
		tiles = rotate(270);
	}

	private Tile tileAt(int x, int y) {
		return tiles[x + y * SIZE];
	}

	private void addTile() {
		List<Tile> list = availableSpace();
		if (!availableSpace().isEmpty()) {
			int ind = (int)(Math.random() * list.size()) % list.size();
			Tile empty = list.get(ind);
			empty.setVal(Math.random() < 0.9 ? MIN : MIN2); 
		}
	}

	private List<Tile> availableSpace() {
		final List<Tile> list = new ArrayList<Tile>(SIZE * SIZE);
		for (Tile t : tiles) {
			if (t.isEmpty()) {
				list.add(t);
			}
		}
		return list;
	}

	private boolean isFull() {
		return availableSpace().size() == 0;
	}

	public boolean canMove() {
		if (!isFull()) {
			return true;
		} 
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				Tile t = tileAt(i,j);
				if ((i < SIZE - 1 && t.getVal() == tileAt(i + 1, j).getVal()) 
						|| (j < SIZE - 1 && t.getVal() == tileAt(i, j + 1).getVal())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean compare(Tile[] line1, Tile[] line2) {
		if (line1 == line2) {
			return true;
		} else if (line1.length != line2.length) {
			return false;
		}

		for (int i = 0; i < line1.length; i++) {
			if (line1[i].getVal() != line2[i].getVal()) {
				return false;
			}
		}
		return true;
	}

	private Tile[] rotate(int angle) {
		Tile[] newTiles = new Tile[SIZE * SIZE];
		int offsetX = SIZE - 1;
		int offsetY = SIZE - 1;
		if (angle == 90) {
			offsetY = 0;
		} else if (angle == 270) {
			offsetX = 0;
		}

		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				int newX = (x * cos) - (y * sin) + offsetX;
				int newY = (x * sin) + (y * cos) + offsetY;
				newTiles[(newX) + (newY) * SIZE] = tileAt(x, y);
			}
		}
		return newTiles;
	}

	private Tile[] moveLine(Tile[] oldLine) {
		LinkedList<Tile> l = new LinkedList<Tile>();
		for (int i = 0; i < 4; i++) {
			if (!oldLine[i].isEmpty()) {
				l.addLast(oldLine[i]);
			}
		}
		if (l.size() == 0) {
			return oldLine;
		} else {
			Tile[] newLine = new Tile[4];
			ensureSize(l, 4);
			for (int i = 0; i < 4; i++) {
				newLine[i] = l.removeFirst();
			}
			return newLine;
		}
	}

	private Tile[] mergeLine(Tile[] oldLine) {
		LinkedList<Tile> list = new LinkedList<Tile>();
		for (int i = 0; i < SIZE && !oldLine[i].isEmpty(); i++) {
			int num = oldLine[i].getVal();
			if (i < SIZE - 1 && oldLine[i].getVal() == oldLine[i + 1].getVal()) {
				num *= 2; // point calculator
				score += num;
				if (num == GOAL) {
					win = true;
				}
				i++;
			}
			list.add(new Tile(num));
		}
		if (list.size() == 0) {
			return oldLine;
		} else {
			ensureSize(list, 4);
			return list.toArray(new Tile[4]);
		}
	}

	private static void ensureSize(java.util.List<Tile> l, int s) {
		while (l.size() != s) {
			l.add(new Tile());
		}
	}

	private Tile[] getLine(int index) {
		Tile[] result = new Tile[4];
		for (int i = 0; i < 4; i++) {
			result[i] = tileAt(i, index);
		}
		return result;
	}

	private void setLine(int index, Tile[] re) {
		System.arraycopy(re, 0, tiles, index * 4, 4);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				drawTile(g, tiles[x + y * 4], x, y);
			}
		}
	}

	private void drawTile(Graphics g2, Tile tile, int x, int y) {
		Graphics2D g = ((Graphics2D) g2);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		int value = tile.getVal();
		int xOffset = offsetCoors(x);
		int yOffset = offsetCoors(y);
		g.setColor(tile.getBackground());
		g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
		g.setColor(tile.getForeground());
		final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
		final Font font = new Font(FONT_NAME, Font.BOLD, size);
		g.setFont(font);

		String s = String.valueOf(value);
		final FontMetrics fm = getFontMetrics(font);

		final int w = fm.stringWidth(s);
		final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

		if (value != 0)
			g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);

		if (win || lose) {
			g.setColor(new Color(255, 255, 255, 30));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(new Color(78, 139, 202));
			g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
			if (win) {
				g.drawString("You won!", 68, 150);
			}
			if (lose) {
				g.drawString("Game over!", 50, 130);
				g.drawString("You lose!", 64, 200);
			}
			if (win || lose) {
				g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
				g.setColor(new Color(128, 128, 128, 128));
				g.drawString("Press ESC to play again", 80, getHeight() - 40);
			}
		}
		g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
		g.drawString("Score: " + score, 200, 350);

	}

	private static int offsetCoors(int arg) {
		return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
	}

	public static void main(String[] args) {
		JFrame game = new JFrame();
		game.setTitle("" + GOAL);
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		game.setSize(340, 400);
		game.setResizable(false);

		game.add(new Game());

		game.setLocationRelativeTo(null);
		game.setVisible(true);
	}
}
