package reversi;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static Board mainBoard = new Board();

	public static void main(String[] args) {
		mainBoard.newGame();
		mainBoard.displayBoard();
		
		while (mainBoard.bPoints() + mainBoard.wPoints() < 64) {
			displayCurrentTurn();
			mainBoard = mainBoard.move(requestMove());	
		}
		
		
		/*
		int[] C4 = { 3, 2 };
		Board newBoard = mainBoard.move(C4);
		newBoard.displayBoard();
		System.out.println("This board W Points: " + newBoard.wPoints());
		System.out.println(newBoard.currentTurn());
		*/
		
		/*
		for (int i=0; i<size; i++) {
			Arrays.fill(boardArray[i], 0);
		}
		
		place('W', "D4");
		place('B', "D5");
		place('B', "E4");
		place('W', "E5");
		
		displayBoard();
		
		while (bPoints + wPoints < 64) {
			displayCurrentTurn();
			//botPlay();
			//requestMove();
			
			if (currentColor == 'B') {
				requestMove();	
			} else {
				botPlay();
			}
		}
		
		displayBoard();
		System.out.println("Game Over!");
		if (bPoints > wPoints) {
			System.out.println("B Wins!");
		} else if ( wPoints > bPoints) {
			System.out.println("W Wins!");
		} else {
			System.out.println("It's a Tie, nobody wins!");
		}
		System.out.println("===========================");
		System.out.println("        Turn " + currentTurn);
		System.out.println("   Score B: " + bPoints + "    W: " + wPoints);
		System.out.println("===========================");
		System.out.println();
		*/
	}
	
	/*
	private static void requestMove() {
		Scanner sc = new Scanner( System.in );		
		ArrayList<String> possibleMovesPosition = possibleMovesCoordToPosition(possibleMovesCoord(currentColor));
		
		if (possibleMovesPosition.size() < 1) {
			System.out.print(currentColor + " Move: SKIP");
		} else {
			System.out.print(currentColor + " Move: ");
			String position = sc.nextLine().toUpperCase();
			
			while (!possibleMovesPosition.contains(position)) {
				System.out.println("Invalid move!");
				System.out.print(currentColor + " Move: ");
				position = sc.nextLine();
			}
			endTurn(makeMove(currentColor, position));
		}
		System.out.println();
	}
	*/
	
	/*
	private static void botPlay() {
		ArrayList<int[]> possibleMoves = possibleMovesCoord(currentColor);
		int[] bestMove = possibleMoves.get(0);
		for (int[] coord: possibleMoves) {
			if (pointsForMove(currentColor, coord[0], coord[1]) > pointsForMove(currentColor, bestMove[0], bestMove[1])) {
				bestMove = coord;
			}
		}
		System.out.println(currentColor + " Move (bot): " + numToPosition(bestMove[0], bestMove[1]));
		endTurn(makeMove(currentColor, bestMove[0], bestMove[1]));	
	}
	
	private static void pickBestMove(int player, int[][] board) {
		char color = playerToColor(player);
		ArrayList<int[]> possibleMoves = possibleMovesCoord(color);
		int[] bestMove = possibleMoves.get(0);
		
		for (int[] coord: possibleMoves) {
			if (pointsForMove(color, coord[0], coord[1]) > pointsForMove(color, bestMove[0], bestMove[1])) {
				bestMove = coord;
			}
		}
		
		makeMove(color, bestMove[0], bestMove[1], board);
		pickBestMove(opponentPlayer(player), board);
	}
	*/
	
	private static int[] requestMove() {
		Scanner sc = new Scanner( System.in );
		char currentColor = playerToColor(mainBoard.currentPlayer());
		ArrayList<int[]> possibleMovesCoord = mainBoard.possibleMoves();
		ArrayList<String> possibleMoves = new ArrayList<String>();
		
		for (int[] move: possibleMovesCoord) {
			String position = coordToPosition(move);
			if (!possibleMoves.contains(position)) {
				possibleMoves.add(position);	
			}
		}
		
		System.out.print("Possible Moves(" + possibleMoves.size() + "): ");
		for (String move: possibleMoves) {
			System.out.print(move + " ");
		}
		System.out.println();
		System.out.println("---------------------------");
		
		System.out.print(currentColor + " Move: ");
		String playerMove = sc.nextLine().toUpperCase();
		
		while (!possibleMoves.contains(playerMove)) {
			System.out.println("Invalid move!");
			System.out.print(currentColor + " Move: ");
			playerMove = sc.nextLine().toUpperCase();
		}
		
		System.out.println();
		
		return positionToCoord(playerMove);
	}
	
	private static void displayCurrentTurn() {
		char currentColor = playerToColor(mainBoard.currentPlayer());
		
		System.out.println("===========================");
		System.out.println("        Turn " + mainBoard.currentTurn() + ": " + currentColor + "     ");
		System.out.println("   Score B: " + mainBoard.bPoints() + "    W: " + mainBoard.wPoints());
		System.out.println("===========================");
		System.out.println();
		
		mainBoard.displayPossibleMoves();
	}
	
	private static char playerToColor(int player) {
		if (player == 1) {
			return 'B';
		} else if (player == 2) {
			return 'W';
		} else {
			System.out.println("Invalid color");
			return 'I';
		}
	}
	
	private static String coordToPosition(int[] coord) {
		String position = "Invalid Coord";
		int row = coord[0];
		int col = coord[1];
		
		if (row >= 0 && row < 8 && col >= 0 && col < 8) {
			col += 65;
	        char letter = (char) col;
	        position = letter + Integer.toString(row+1);
		}
		
		return position;
	}
	
	private static int[] positionToCoord(String position) {
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
		
		int [] coord = { col, row }; 
		return  coord;
	}

}
