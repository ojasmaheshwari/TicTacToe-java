import java.util.Scanner;

enum Winner {
	PrimaryPlayer,
	SecondaryPlayer,
	None
}

enum Turn {
	PrimaryPlayer,
	SecondaryPlayer
}

enum Symbol {
	Cross,
	Circle,
	None
}

class WinData {
	public boolean isWon;
	public Winner winner;

	public WinData(final boolean isWon, final Symbol sym) {
		this.isWon = isWon;
		switch(sym) {
			case Cross:
				this.winner = Winner.PrimaryPlayer;
				break;
			case Circle:
				this.winner = Winner.SecondaryPlayer;
				break;
			default:
				this.winner = Winner.None;
				break;
		}
	}
	public WinData(final boolean isWon, final Winner winner) {
		this.isWon = isWon;
		this.winner = winner;
	}
}

class Board {
	private int size;
	private Symbol[][] arr;
	private int filledPlaces = 0;

	public Board(final int size) {
		this.size = size;
		arr = new Symbol[size][size];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				arr[i][j] = Symbol.None;
			}
		}
	}

	public void print() {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				char ch = ' ';
				if (arr[i][j] == null)
					arr[i][j] = Symbol.None;
				switch (arr[i][j]) {
					case Cross:
						ch = 'x';
						break;
					case Circle:
						ch = 'o';
						break;
					case None:
						ch = '_';
						break;
					default:
						// Not possible
						break;
				}
				System.out.print(ch + " ");
			}
			System.out.println();
		}
	}

	public void set(final int x, final int y, Symbol sym) {
		if (arr[x][y] == Symbol.None) {
			arr[x][y] = sym;
			filledPlaces++;
		}
		else {
			System.out.println("Invalid move, your chance is skipped");
		}
	}

	public Symbol get(final int x, final int y) {
		return arr[x][y];
	}

	public int getSize() {
		return size*size;
	}

	public boolean isFull() {
		return filledPlaces == getSize();
	}

	public Symbol[][] getArr() {
		return arr;
	}

}

class Player {
	public Symbol sym;
	private Scanner inputScanner = new Scanner(System.in);
	private int moveX, moveY;

	private void takeInput() {
		System.out.println("Enter coordinates: ");
		moveX = inputScanner.nextInt();
		moveY = inputScanner.nextInt();
	}

	private boolean checkBounds() {
		return (moveX >= 0 && moveX < 3 && moveY >= 0 && moveY < 3);
	}

	public void move(Board board) {
		takeInput();
		if (checkBounds())
			board.set(moveX - 1, moveY - 1, sym);
		else
			System.out.println("Invalid move, your chance is skipped");
	}
}

class Game {
	private boolean isRunning = false;
	private Board board;
	private Winner winner = Winner.None;
	private Turn turn = Turn.PrimaryPlayer;
	private Player primary = new Player(), secondary = new Player();

	private WinData checkRows(Board board) {
		Symbol[][] arr = board.getArr();

		for (int i = 0; i < arr.length; i++) {
			boolean allSame = true;
			Symbol[] row = arr[i];
			for (int j = 1; j < row.length; j++) {
				if (row[j] != row[j-1] || row[j] == Symbol.None || row[j-1] == Symbol.None) {
					allSame = false;
					break;
				}
			}
			if (allSame) return new WinData(true, row[0]);
		}
		return new WinData(false, Winner.None);
	}

	private WinData checkColumns(Board board) {
		Symbol[][] arr = board.getArr();

		for (int i = 0; i < arr.length; i++) {
			boolean allSame = true;
			for (int j = 1; j < arr[i].length; j++) {
				if (arr[j][i] != arr[j - 1][i] || arr[j][i] == Symbol.None || arr[j-1][i] == Symbol.None) {
					allSame = false;
					break;
				}
			}
			if (allSame) return new WinData(true, arr[0][i]);
		}
		return new WinData(false, Winner.None);
	}

	private WinData checkDiagonals(Board board) {
		Symbol[][] arr = board.getArr();

		boolean allSame_primary = true;
		for (int i = 1; i < arr.length; i++) {
			if (arr[i][i] != arr[i-1][i-1] || arr[i][i] == Symbol.None || arr[i-1][i-1] == Symbol.None) {
				allSame_primary = false;
				break;
			}
		}
		if (allSame_primary) return new WinData(true, arr[0][0]);

		boolean allSame_secondary = true;
		Symbol firstValue = arr[0][arr.length - 1];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i][arr.length - 1 - i] == Symbol.None || arr[i][arr.length - 1 - i] != firstValue) {
				allSame_secondary = false;
				break;
			}
		}
		if (allSame_secondary) return new WinData(true, firstValue);

		return new WinData(false, Winner.None);
	}

	private void checkWin() {
		WinData rows = checkRows(board), cols = checkColumns(board), diags = checkDiagonals(board);

		if (rows.winner != Winner.None) winner = rows.winner;
		if (cols.winner != Winner.None) winner = cols.winner;
		if (diags.winner != Winner.None) winner = diags.winner;
	}

	public Game() {
		board = new Board(3);
		isRunning = true;
		primary.sym = Symbol.Cross;
		secondary.sym = Symbol.Circle;
	}

	public void run() {
		while (isRunning) {
			board.print();

			switch(turn) {
				case PrimaryPlayer:
					System.out.println("Player 1s turn");
					primary.move(board);
					break;
				case SecondaryPlayer:
					System.out.println("Player 2s turn");
					secondary.move(board);
					break;
				default:
					// Not possible
					break;
			}

			checkWin();
			if (winner != Winner.None) {
				board.print();
				String winnerName = (winner == Winner.PrimaryPlayer ? "Player 1" : "Player 2");
				System.out.println("Game Over, " + winnerName + " won!");
				isRunning = false;
			}
			if (board.isFull()) {
				board.print();
				System.out.println("Game Over, Nobody won, you both lose");
				isRunning = false;
			}
			turn = (turn == Turn.PrimaryPlayer ? Turn.SecondaryPlayer : Turn.PrimaryPlayer);
		}
	}
}

class Main {
	public static void main(String[] args) {
		Game game = new Game();
		game.run();
	}
}
