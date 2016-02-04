package reversi;

import java.util.Arrays;

public class Main {
	public static int size = 8;
	public static int[][] boardArray = new int[size][size];

	public static void main(String[] args) {
		// initialize new board
		for (int i=0; i<boardArray.length; i++) {
			Arrays.fill(boardArray[i], 0);
		}
		
		displayBoard();
		
		place("W", "D4");
		place("B", "D5");
		place("B", "E4");
		place("W", "E5");
		
		displayBoard();
	}
	
	private static void place(String team, String position) {
		int color;
		switch(team) {
			case "B":
				color = 1;
				break;
				
			case "W":
				color = 2;
				break;
				
			default:
				System.out.println("Invalid Team");
				return;
		}
		
		int col = position.charAt(0) - 'A';
		int row = position.charAt(1) - 49;
		
		boardArray[row][col] = color;
		
	}

	private static void displayBoard() {
		System.out.print("   ");
		for (int i=1; i<=8; i++) {
			System.out.print(" " + String.valueOf((char)(i + 64)) + " ");
		}
		System.out.println();
		
		for (int i=0; i<boardArray.length; i++) {
			System.out.print(" " + (i+1) + " ");
			for (int j=0; j<boardArray[i].length; j++) {
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
