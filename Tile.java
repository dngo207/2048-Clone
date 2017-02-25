import java.awt.Color;

public class Tile {
	private int value;

	public Tile(int num) {
		value = num;
	}

	public Tile() {
	}

	public boolean isEmpty() {
		return value == 0;
	}

	public void setVal(int x) {
		value = x;
	}

	public int getVal() {
		return value;
	}

	public Color getForeground() {
		return value < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
	}

	public Color getBackground() {
		if (value == 2) {
			return new Color(0xeee4da);
		} else if (value == 4) {
			return new Color(0xede0c8);
		} else if (value == 8) {
			return new Color(0xf2b179);
		} else if (value == 16){
			return new Color(0xf59563);
		} else if (value == 32) {
			return new Color(0xf67c5f);
		} else if (value == 64) {
			return new Color(0xf65e3b);
		} else if (value == 128) {
			return new Color(0xedcf72);
		} else if (value == 256) {
			 return new Color(0xedcc61);
		} else if (value == 512) {
			return new Color(0xedc850);
		} else if (value == 1024) {
			return new Color(0xedc53f);
		} else if (value == 2048) {
			return new Color(0xedc22e);
		} 
		return new Color(0xcdc1b4);
	}
}
