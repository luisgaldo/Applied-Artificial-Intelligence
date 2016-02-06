package reversi;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	public static int size = 8;
	public static int[][] boardArray = new int[size][size];

	public static void main(String[] args) {
		// initialize new board
		for (int i=0; i<size; i++) {
			Arrays.fill(boardArray[i], 0);
		}
		displayBoard();
		
		place('W', "D4");
		place('B', "D5");
		place('B', "E4");
		place('W', "E5");
		
		displayBoard();
		
		
		displayPossibleMoves('B');
		
		System.out.println("===========================");
		System.out.println("      Current turn: B      ");
		System.out.println("===========================");
		System.out.println();
		
		
		System.out.println("Move: C4, points: " + play('B',"C4"));
		displayBoard();
		
		System.out.println("===========================");
		System.out.println("      Current turn: W      ");
		System.out.println("===========================");
		System.out.println();
		
		displayPossibleMoves('W');
		
	}
	
	private static int play(char color, String position) {
		int opponent = opponentPlayer(color);
		int player = colorToPlayer(color);
		int[] coord = positionToNum(position);
		int row = coord[0];
		int col = coord[1];
		int points = 0;
		
		boardArray[row][col] = player;
		int[][] adjacent = adjacentTiles(row, col);
		for (int k=0; k<8; k++) {
			int adjRow = adjacent[k][0];
			int adjCol = adjacent[k][1];
			// check if it is still within the board
			if (adjRow > 0 && adjRow < 8 && adjCol > 0 && adjCol < 8) {
				if (boardArray[adjRow][adjCol] == opponent) {
					points += convertPieces(row, col, k, player);
				}
			}
		}	
		return points;
	}

	private static ArrayList<int[]> possibleMoves(char color) {
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		int opponent = opponentPlayer(color);
		int player = colorToPlayer(color);
		
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (boardArray[i][j] == 0) {
					// generate adjacent tiles and look for opponent piece
					int[][] adjacent = adjacentTiles(i, j);
					for (int k=0; k<8; k++) {
						int adjRow = adjacent[k][0];
						int adjCol = adjacent[k][1];
						// check if it is still within the board
						if (adjRow > 0 && adjRow < 8 && adjCol > 0 && adjCol < 8) {
							if (boardArray[adjRow][adjCol] == opponent) {
								// search for the player tile in the same direction and mark if found
								int[] search = searchInDirection(i, j, k, player);
								if (search[0] != -1 && search[1] != -1) {
									int[] move = { i, j };
									possibleMoves.add(move);
								}
							}
						}
					}
				}
			}
		}
		
		return possibleMoves;
	}
	
	private static int[][] adjacentTiles(int row, int col) {
		int[][] adj = new int[8][2];
		
		int[] N = 	{ row-1, col };
		int[] NE = 	{ row-1, col+1 };
		int[] E = 	{ row, col+1 };
		int[] SE = 	{ row+1, col+1 };
		int[] S = 	{ row+1, col };
		int[] SW = 	{ row+1, col-1 };
		int[] W = 	{ row, col-1 };
		int[] NW = 	{ row-1, col-1 };
		
		adj[0] = N;
		adj[1] = NE;
		adj[2] = E;
		adj[3] = SE;
		adj[4] = S;
		adj[5] = SW;
		adj[6] = W;
		adj[7] = NW;
		
		return adj;
	}
	
	// assuming clockwise N, NE, E, SE, S, SW, W, NW
		private static int convertPieces(int row, int col, int direction, int player) {
			int i;
			int converted = 0;
			switch (direction) {
			case 0:
				i = ++row;
				while (i >= 0 && boardArray[i][col] != player && boardArray[i][col] != 0) {
					boardArray[i][col] = player;
					converted++;
					i--;
				}
				break;
				
			case 1:
				i = 1;
				while (row-i >= 0 && col+i < 8 && boardArray[row-i][col+i] != player && boardArray[row-i][col+i] != 0) {
					boardArray[row-i][col+i] = player;
					converted++;
					i++;
				}
				break;
				
			case 2:
				i = ++col;
				while (i < 8 && boardArray[row][i] != player && boardArray[row][i] != 0) {
					boardArray[row][i] = player;
					converted++;
					i++;
				}
				break;
				
			case 3:
				i = 1;
				while (row+i < 8 && col+i < 8 && boardArray[row+i][col+i] != player && boardArray[row+i][col+i] != 0) {
					boardArray[row+i][col+i] = player;
					converted++;
					i++;
				}
				break;
				
			case 4:
				i = ++row;
				while (i < 8 && boardArray[i][col] != player && boardArray[i][col] != 0) {
					boardArray[i][col] = player;
					converted++;
					i++;
				}
				break;
				
			case 5:
				i = 1;
				while (row+i < 8 && col-i >= 0 && boardArray[row+i][col-i] != player) {
					boardArray[row+i][col-i] = player;
					converted++;
					i++;
				}
				break;
				
			case 6:
				i = ++col;
				while (i >= 0 && boardArray[row][i] != player && boardArray[row][i] != 0) {
					boardArray[row][i] = player;
					converted++;
					i--;
				}
				break;
				
			case 7:
				i = 1;
				while (row-i >= 0 && col-i >= 0 && boardArray[row-i][col-i] != player && boardArray[row-i][col-i] != 0) {
					boardArray[row-i][col-i] = player;
					converted++;
					i++;
				}
				break;
				
			default: break;
			}
			return converted;
		}
	
	// assuming clockwise N, NE, E, SE, S, SW, W, NW
	private static int[] searchInDirection(int row, int col, int direction, int player) {
		int[] coord = { -1, -1 };
		int i;
		switch (direction) {
		case 0:
			for (i=row; i>=0; i--) {
				if (boardArray[i][col] == player) {
					coord[0] = i;
					coord[1] = col;
					return coord;
				}
			}
			break;
			
		case 1:
			i = 0;
			while (row-i >= 0 && col+i < 8) {
				if (boardArray[row-i][col+i] == player) {
					coord[0] = row-i;
					coord[1] = col+i;
					return coord;
				}
				i++;
			}
			break;
			
		case 2:
			for (i=col; i<8; i++) {
				if (boardArray[row][i] == player) {
					coord[0] = row;
					coord[1] = i;
					return coord;
				}
			}
			break;
			
		case 3:
			i = 0;
			while (row+i < 8 && col+i < 8) {
				if (boardArray[row+i][col+i] == player) {
					coord[0] = row+i;
					coord[1] = col+i;
					return coord;
				}
				i++;
			}
			break;
			
		case 4:
			for (i=row; i<8; i++) {
				if (boardArray[i][col] == player) {
					coord[0] = i;
					coord[1] = col;
					return coord;
				}
			}
			break;
			
		case 5:
			i = 0;
			while (row+i < 8 && col-i >= 0) {
				if (boardArray[row+i][col-i] == player) {
					coord[0] = row+i;
					coord[1] = col-i;
					return coord;
				}
				i++;
			}
			break;
			
		case 6:
			for (i=col; i>=0; i--) {
				if (boardArray[row][i] == player) {
					coord[0] = row;
					coord[1] = i;
					return coord;
				}
			}
			break;
			
		case 7:
			i = 0;
			while (row-i >= 0 && col-i >= 0) {
				if (boardArray[row-i][col-i] == player) {
					coord[0] = row-i;
					coord[1] = col-i;
					return coord;
				}
				i++;
			}
			break;
			
		default: break;
		}

		return coord;
	}
	
	/*
	 * Return row by col no matter the input
	 * Always regard Alphabet as column and number as row regardless of the order
	 */
	private static int[] positionToNum(String position) {
		char c1 = position.charAt(0);
		char c2 = position.charAt(1);
		int row = -1, col = -1;
		
		if (Character.isDigit(c1) && Character.isLetter(c2)) {
			row = c1 - 49;
			col = c2 - 'A';
		} else if (Character.isLetter(c1) && Character.isDigit(c2)) {
			row = c1 - 'A';
			col = c2 - 49;
		}
		
		// error check
		if (row < 0 || row > 8 || col < 0 || row > 8) {
			System.out.println("Invalid position");
		}
		
		int [] positionArray = { col, row }; 
		return  positionArray;
	}
	
	private static String numToPosition(int row, int col) {
		String position = "Invalid Coord";
		
		if (row >= 0 && row < 8 && col >= 0 && col < 8) {
			col += 65;
	        char letter = (char) col;
	        position = letter + Integer.toString(row+1);
		}
		
		return position;
	}
	
	private static int colorToPlayer(char color) {
		switch(color) {
			case 'B':
				return 1;
				
			case 'W':
				return 2;
				
			default:
				System.out.println("Invalid color");
				return -1;
		}
	}
	
	private static int opponentPlayer(char color) {
		switch(color) {
		case 'B':
			return 2;
			
		case 'W':
			return 1;
			
		default:
			System.out.println("Invalid color");
			return -1;
		}
	}

	private static void place(char color, int[] position) {
		int player = colorToPlayer(color);
		boardArray[position[0]][position[1]] = player;	
	}
	
	
	// should change back to row by column (easier to avoid mistakes when working with 2d array)
	private static void place(char color, String position) {
		int player = colorToPlayer(color);
		int[] coord = positionToNum(position); 
		boardArray[coord[0]][coord[1]] = player;	
	}
	
	private static void displayPossibleMoves(char color) {
		ArrayList<int[]> possibleMoves = possibleMoves(color);
		
		// create a deep-copy so it doesn't affect the actual board
        final int[][] UIboardArray = new int[size][];
        for (int i = 0; i < size; i++) {
        	UIboardArray[i] = Arrays.copyOf(boardArray[i], size);
        }
        
		for (int[] coord: possibleMoves) {
			UIboardArray[coord[0]][coord[1]] = 3;
		}
		displayBoard(UIboardArray);
	}
	
	private static void displayBoard(int[][] board) {
		System.out.print("   ");
		for (int i=1; i<=8; i++) {
			System.out.print(" " + String.valueOf((char)(i + 64)) + " ");
		}
		System.out.println();
		
		for (int i=0; i<size; i++) {
			System.out.print(" " + (i+1) + " ");
			for (int j=0; j<size; j++) {
				switch(board[i][j]) {
					case 1:
						System.out.print(" B ");
						break;
						
					case 2:
						System.out.print(" W ");
						break;
						
					case 3:
						System.out.print(" X ");
						break;
					
					case 0:
					default:
						System.out.print(" - ");
						break;
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	private static void displayBoard() {
		System.out.print("   ");
		for (int i=1; i<=8; i++) {
			System.out.print(" " + String.valueOf((char)(i + 64)) + " ");
		}
		System.out.println();
		
		for (int i=0; i<size; i++) {
			System.out.print(" " + (i+1) + " ");
			for (int j=0; j<size; j++) {
				switch(boardArray[i][j]) {
					case 1:
						System.out.print(" B ");
						break;
						
					case 2:
						System.out.print(" W ");
						break;
						
					case 3:
						System.out.print(" X ");
						break;
					
					case 0:
					default:
						System.out.print(" - ");
						break;
				}
			}
			System.out.println();
		}
		System.out.println();
	}

}
