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
		
		Scanner sc = new Scanner( System.in );
		System.out.print("Seconds to think: ");
		int seconds = sc.nextInt();
		
		mainBoard.displayBoard();

		while (!mainBoard.gameEnd()) {
			displayCurrentTurn();
			if (mainBoard.currentPlayer() == 1) {
				mainBoard = mainBoard.move(requestMove());
			} else {
				mmBotPlay(mainBoard, seconds);
				//mainBoard = mainBoard.move(requestMove());
			}
		}
		
		
		System.out.println("===========================");
		System.out.println("         Game Over!        ");
		System.out.println("   Score  B: " + mainBoard.bPoints() + "    W: " + mainBoard.wPoints());
		if (mainBoard.bPoints() > mainBoard.wPoints()) {
			System.out.println("          B Wins           ");	
		} else if (mainBoard.wPoints() > mainBoard.bPoints()) {
			System.out.println("          W Wins           ");
		} else {
			System.out.println("          Tie                ");
		}
		System.out.println("===========================");
		System.out.println();
		mainBoard.displayBoard();
	}
	
	private static void mmBotPlay(Board board, int seconds) {
		final long start = System.nanoTime();
		long timeTaken;
		int[] bestMove;
		Board tmpBoard;
		
		if (board.possibleMoves().size() == 1) {
			mainBoard = mainBoard.move(board.possibleMoves().get(0));
			timeTaken = System.nanoTime() - start;
			System.out.println("Bot took " + timeTaken/1000000 + "ms");
			return;
		}
		int maxDepth = 60 - board.currentTurn();
		Board bestBoard = minimax(growTree(board, seconds), maxDepth, (board.currentPlayer() == 1));
		//bestBoard.displayBoard();
		
		tmpBoard = bestBoard;
		while (tmpBoard.prevBoard() != board) {
			tmpBoard = tmpBoard.prevBoard();
		}
		bestMove = tmpBoard.prevMove();
		
		System.out.println("Minimax plays: " + coordToPosition(bestMove) + " after checking to turn " + bestBoard.currentTurn());
		timeTaken = System.nanoTime() - start;
		System.out.println("Minimax Bot took " + timeTaken/1000000 + "ms");
		mainBoard = mainBoard.move(bestMove);
		mainBoard.clearVisited();
	}
	
	private static Board growTree(Board board, int seconds) {
		Queue<Board> queue = new LinkedList<Board>();
		long start = System.currentTimeMillis();
		long end = start + seconds*1000; // 1000 ms/sec
		boolean continueRun = true;
		
		queue.add(board);
		
		while(continueRun && !queue.isEmpty()) {
			Board newBoard = queue.remove();
			newBoard.getChildren();
			Board child = newBoard.getUnvistedChild();
			while(child != null) {
				child.visited = true;
				queue.add(child);;
				child = newBoard.getUnvistedChild();
			}
			
			continueRun = System.currentTimeMillis() < end;
		}
		/*
		Board lastBoard = board;

		while (lastBoard.hasChildren()) {
			lastBoard = lastBoard.children.get(0);
		}
		System.out.println("Last turn: " + lastBoard.currentTurn());
		*/
		return board;
	}
	
	// convenience method to call it easily
	private static Board minimax(Board board, int depth, boolean player1) {
		Board alpha = new Board(), beta = new Board();
		alpha.negativeInfinity = true;
		beta.positiveInfinity = true;
		return minimax(board, depth, alpha, beta, player1);
	}
	
	// with alphabeta pruning
	private static Board minimax(Board board, int depth, Board alpha, Board beta, boolean player1) {
		if (depth == 0 || !board.hasChildren()) {
			//System.out.println("Reached turn: " + board.currentTurn());
			return board;
		}
		
		Board bestBoard = new Board(), v;
		
		if (player1) {
			bestBoard.negativeInfinity = true;
			for (Board child: board.getChildren()) {
				v = minimax(child, depth-1, alpha, beta, false);	

				alpha = max(alpha, v);
				if (beta.bPoints() <= alpha.bPoints()) {
					break; // beta cut
				}
				bestBoard = max(bestBoard, v);
			}	
			return bestBoard;
			
		} else {
			bestBoard.positiveInfinity = true;	
			for (Board child: board.getChildren()) {
				v = minimax(child, depth-1, alpha, beta, true);	
				
				beta = min(beta, v);
				if (beta.bPoints() <= alpha.bPoints()) {
					break; // alpha cut
				}
				bestBoard = min(bestBoard, v);	
			}
		}
		
		return bestBoard;
	}

	private static Board min(Board a, Board b) {
		if (a.bPoints() < b.bPoints()) {
			return a;
		} else {
			return b;
		}
	}
	
	private static Board max(Board a, Board b) {
		if (a.bPoints() > b.bPoints()) {
			return a;
		} else {
			return b;
		}
	}

	private static void rBotPlay(Board board, int seconds) {
		final long botStartTime = System.nanoTime();
		long timeTaken;
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
		boolean continueRun = true;
		
		while(continueRun && !queue.isEmpty()) {
			Board newBoard = queue.remove();
			newBoard.getChildren();
			Board child = newBoard.getUnvistedChild();
			while(child != null) {
				child.visited = true;
				queue.add(child);
				boards.add(child);
				child = newBoard.getUnvistedChild();
			}
			
			continueRun = System.currentTimeMillis() < end;
		}
		
		//System.out.println("Got " + boards.size() + " boards in " + seconds + "s");
		
		int lastTurn = boards.get(0).currentTurn();
		for (Board tmpBoard: boards) {
			if (tmpBoard.currentTurn() > lastTurn) {
				lastTurn = tmpBoard.currentTurn();
			}
		}

		// find complete tree of boards at the 2nd last turn
		if (lastTurn < 60) {
			lastTurn--;	
		}
		System.out.println("Furthest move: " + lastTurn);

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
			double ratio = (double)sum[i][0] / sum[i][1];
			ratios.add(ratio);
			System.out.println("Move " + coordToPosition(possibleBoards.get(i).prevMove()) + " b: " + sum[i][0] + " w: " + sum[i][1] + " r: " + ratio);
		}
		
		
		if (board.currentPlayer() == 1) {
			bestMove = possibleBoards.get(ratios.indexOf(Collections.max(ratios))).prevMove();
		} else {
			bestMove = possibleBoards.get(ratios.indexOf(Collections.min(ratios))).prevMove();
		}
		
		System.out.println("Ratio Bot plays " + coordToPosition(bestMove) + " after checking " + endBoards.size() + " boards");
		timeTaken = System.nanoTime() - botStartTime;
		System.out.println("Ratio Bot took " + timeTaken/1000000 + "ms");
		
		mainBoard = mainBoard.move(bestMove);
		mainBoard.clearVisited();
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
