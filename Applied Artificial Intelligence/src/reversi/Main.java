package reversi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
	public static Board mainBoard = new Board();

	public static void main(String[] args) {
		mainBoard.newGame();
		mainBoard.displayBoard();
		
		while (!mainBoard.gameEnd()) {
			displayCurrentTurn();
			if (mainBoard.currentPlayer() == 1) {
				botPlay(mainBoard, 2);
				//mainBoard = mainBoard.move(requestMove());
			} else {
				botPlay(mainBoard, 1);
			}
		}
		
		System.out.println("===========================");
		System.out.println("         Game Over!        ");
		System.out.println("   Score  B: " + mainBoard.bPoints() + "    W: " + mainBoard.wPoints());
		if (mainBoard.bPoints() > mainBoard.wPoints()) {
			System.out.println("          B Wins           ");	
		} else {
			System.out.println("          W Wins           ");
		}
		System.out.println("===========================");
		System.out.println();
		mainBoard.displayBoard();
	}
	
	private static void botPlay(Board board, int seconds) {
		Queue<Board> queue = new LinkedList<Board>();
		ArrayList<Board> boards = new ArrayList<Board>();
		ArrayList<Board> endBoards = new ArrayList<Board>();
		ArrayList<Board> possibleBoards = board.getChildren();
		int[][] sum = new int[possibleBoards.size()][2];
		ArrayList<Double> ratios = new ArrayList<Double>();
		int[] bestMove = null;
		board.visited = true;
		queue.add(board);
		
		// in case of 1 move
		if (possibleBoards.size() == 1 || board.currentTurn() == 60) {
			mainBoard = possibleBoards.get(0);
			return;
		}
		
		long start = System.currentTimeMillis();
		long end = start + seconds*1000; // 1000 ms/sec

		while(System.currentTimeMillis() < end && !queue.isEmpty()) {
			Board newBoard = queue.remove();
			newBoard.getChildren();
			Board child = newBoard.getUnvistedChild();
			while(child != null) {
				child.visited = true;
				queue.add(child);
				boards.add(child);
				child = newBoard.getUnvistedChild();
			}
		}
		
		//System.out.println("Got " + boards.size() + " boards in " + seconds + "s");
		
		int lastTurn = boards.get(0).currentTurn();
		for (Board tmpBoard: boards) {
			if (tmpBoard.currentTurn() > lastTurn) {
				lastTurn = tmpBoard.currentTurn();
			}
		}

		// find complete tree of boards at the 2nd last turn
		if (board.currentTurn() < 50) {
			lastTurn -= 2;	
		}
		//System.out.println("Furthest move: " + lastTurn);

		for (Board tmpBoard: boards) {
			if (tmpBoard.currentTurn() == lastTurn) {
				endBoards.add(tmpBoard);
			}
		}
		
		//System.out.println("There are " + endBoards.size() + " at turn " + lastTurn);
		
		for (Board endBoard: endBoards) {
			Board tmpBoard = endBoard;
			while (!possibleBoards.contains(tmpBoard.prevBoard())) {
				tmpBoard = tmpBoard.prevBoard();
			}
			
			tmpBoard = tmpBoard.prevBoard();

			sum[possibleBoards.indexOf(tmpBoard)][0] += endBoard.bPoints();
			sum[possibleBoards.indexOf(tmpBoard)][1] += endBoard.wPoints();
		}

		for (int i=0; i<possibleBoards.size(); i++) {
			ratios.add((double)sum[i][0] / (double)sum[i][1]);
		}
		
		
		if (board.currentPlayer() == 1) {
			bestMove = possibleBoards.get(ratios.indexOf(Collections.max(ratios))).prevMove();
		} else {
			bestMove = possibleBoards.get(ratios.indexOf(Collections.min(ratios))).prevMove();
		}
		
		System.out.println("Bot plays " + coordToPosition(bestMove) + " after checking " + endBoards.size());

		mainBoard = mainBoard.move(bestMove);
		board.clearVisited();
	}

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
		System.out.println("   Score  B: " + mainBoard.bPoints() + "    W: " + mainBoard.wPoints());
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
