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
	}
	
	
	// change to scan method ( if 0 then look at the row, column then diagonal )
	/*
	private static void possibleMoves(char color) {
		int player = colorToPlayer(color);
		int opponent = opponentPlayer(color);
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (boardArray[i][j] == opponent) {
					for (int k=j; k<size; k++) { // look for any of player tile in the same row
						if (boardArray[i][k] == player) {
							int[] move = {i,j-1};
							possibleMoves.add(move);
						}
					}
				}
			}
		}
		for (int[] moves: possibleMoves) {
			System.out.println(moves[0] + ", " + moves[1]);
		}
	}
	*/
	
	private static int[] positionToNum(String position) {
		int [] positionArray = { position.charAt(0) - 'A', position.charAt(1) - 49}; 
		return  positionArray;
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
		boardArray[positionToNum(position)[0]][positionToNum(position)[1]] = player;	
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
