package reversi;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {
	private int size = 8;
	private int[][] boardArray;
	private int currentPlayer, currentTurn;
	private Board prevBoard;
	private int[] prevMove;
	public boolean visited = false;
	public ArrayList<Board> children = new ArrayList<Board>();
	
	/*
	 * Black = 1, White = 2
	 */
	
	public Board() {
		boardArray = new int[size][size];
		for (int i=0; i<size; i++) {
			Arrays.fill(boardArray[i], 0);
		}
	}
	
	public Board(int[][] boardArray, int currentPlayer, int currentTurn, int[] prevMove, Board prevBoard) {
		this.boardArray = boardArray;
		this.currentPlayer = currentPlayer;
		this.currentTurn = currentTurn;
		this.prevMove = prevMove;
		this.prevBoard = prevBoard;
	}
	
	public void newGame() {
		int[] D4 = { 3, 3 };
		int[] D5 = { 4, 3 };
		int[] E4 = { 3, 4 };
		int[] E5 = { 4, 4 };
		
		currentTurn = 1;
		currentPlayer = 1;
		
		place(2, D4);
		place(1, D5);
		place(1, E4);
		place(2, E5);
	}
	
	public ArrayList<Board> getChildren() {
		if (children.isEmpty()) {
			for (int[] coord: possibleMoves()) {
				children.add(move(coord));
			}	
		}
		return children;
	}
	
	public Board getUnvistedChild() {
		for (Board child: children) {
			if (!child.visited) {
				return child;
			}
		}
		
		return null;
	}
	
	public void clearVisited() {
		this.visited = false;
		for (Board child: children) {
			child.visited = false;
		}
	}

	public int currentPlayer() {
		return currentPlayer;
	}
	
	public int currentTurn() {
		return currentTurn;
	}
	
	public Board prevBoard() {
		return prevBoard;
	}
	
	public int[] prevMove() {
		return prevMove;
	}
	
	public int bPoints() {
		int bPoints = 0;
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (boardArray[i][j] == 1) {
					bPoints++;
				}
			}
		}	
		
		return bPoints;
	}
	
	public int wPoints() {
		int wPoints = 0;
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (boardArray[i][j] == 2) {
					wPoints++;
				}
			}
		}	
		
		return wPoints;
	}
	
	public Board move(int[] coord) {
		int nextPlayer;
		int row = coord[0];
		int col = coord[1];
		int opponent = opponent();
		int[][] newBoardArray = copyBoardArray();
		int[][] adjacent = adjacentTiles(row, col);
		place(currentPlayer, coord, newBoardArray);
		
		for (int i=0; i<8; i++) {
			int adjRow = adjacent[i][0];
			int adjCol = adjacent[i][1];
			// check if it is still within the board
			if (adjRow >= 0 && adjRow < 8 && adjCol >= 0 && adjCol < 8) {
				if (newBoardArray[adjRow][adjCol] == opponent) {
					// search for the player tile in the same direction and mark if found
					int[] search = searchInDirection(row, col, i, currentPlayer);
					int targetRow = search[0];
					int targetCol = search[1];
					
					if (search[0] != -1 && search[1] != -1) {
						int j;
						switch (i) {
							case 0:
								j = --row;
								while (j != targetRow) {
									newBoardArray[j][col] = currentPlayer;
									j--;
								}
								break;
								
							case 1:
								j = 1;
								while (row-j != targetRow && col+j != targetCol) {
									newBoardArray[row-j][col+j] = currentPlayer;
									j++;
								}
								break;
								
							case 2:
								j = ++col;
								while (j != targetCol) {
									newBoardArray[row][j] = currentPlayer;
									j++;
								}
								break;
								
							case 3:
								j = 1;
								while (row+j != targetRow && col+j != targetCol) {
									newBoardArray[row+j][col+j] = currentPlayer;
									j++;
								}
								break;
								
							case 4:
								j = ++row;
								while (j != targetRow) {
									newBoardArray[j][col] = currentPlayer;
									j++;
								}
								break;
								
							case 5:
								j = 1;
								while (row+j != targetRow && col-j != targetCol) {
									newBoardArray[row+j][col-j] = currentPlayer;
									j++;
								}
								break;
								
							case 6:
								j = --col;
								while (j != targetCol) {
									newBoardArray[row][j] = currentPlayer;
									j--;
								}
								break;
								
							case 7:
								j = 1;
								while (row-j != targetRow && col-j != targetCol) {
									newBoardArray[row-j][col-j] = currentPlayer;
									j++;
								}
								break;
								
							default: break;
						}
					}
				}
			}
		}	
		
		Board newBoard = new Board(newBoardArray, opponent(), currentTurn+1, coord, this);
		if (newBoard.possibleMoves().isEmpty()) {
			Board nextBoard = new Board(newBoardArray, currentPlayer, currentTurn+2, null, newBoard);
			return nextBoard;
		} else {
			return newBoard;	
		}
	}
	
	public void displayPossibleMoves() {
		int[][] possibleMovesBoard = copyBoardArray();
        
		for (int[] coord: possibleMoves()) {
			possibleMovesBoard[coord[0]][coord[1]] = 3;
		}
		
		displayBoard(possibleMovesBoard);
	}
	
	public ArrayList<int[]> possibleMoves() {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		int opponent = opponent();
		
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (boardArray[i][j] == 0) {
					// generate adjacent tiles and look for opponent piece
					int[][] adjacent = adjacentTiles(i, j);
					for (int k=0; k<8; k++) {
						int adjRow = adjacent[k][0];
						int adjCol = adjacent[k][1];
						// check if it is still within the board
						if (adjRow >= 0 && adjRow < 8 && adjCol >= 0 && adjCol < 8) {
							if (boardArray[adjRow][adjCol] == opponent) {
								// search for the player tile in the same direction and mark if found
								int[] search = searchInDirection(i, j, k, currentPlayer);
								if (search[0] != -1 && search[1] != -1) {
									int[] move = { i, j };
									if (!moves.contains(move)) {
										moves.add(move);	
									}
								}
							}
						}
					}
				}
			}
		}
		
		return moves;
	}
	
	// assuming clockwise N, NE, E, SE, S, SW, W, NW
	private int[] searchInDirection(int row, int col, int direction, int player) {
		int[] coord = { -1, -1 };
		int i;
		switch (direction) {
		case 0:
			for (i=--row; i>=0; i--) {
				if (boardArray[i][col] == 0) {
					return coord;
				}
				
				if (boardArray[i][col] == player) {
					coord[0] = i;
					coord[1] = col;
					return coord;
				}
				
			}
			break;
			
		case 1:
			i = 1;
			while (row-i >= 0 && col+i < 8) {
				if (boardArray[row-i][col+i] == 0) {
					return coord;
				}
				
				if (boardArray[row-i][col+i] == player) {
					coord[0] = row-i;
					coord[1] = col+i;
					return coord;
				}
				i++;
			}
			break;
			
		case 2:
			for (i=++col; i<8; i++) {
				if (boardArray[row][i] == 0) {
					return coord;
				}
				
				if (boardArray[row][i] == player) {
					coord[0] = row;
					coord[1] = i;
					return coord;
				}
			}
			break;
			
		case 3:
			i = 1;
			while (row+i < 8 && col+i < 8) {
				if (boardArray[row+i][col+i] == 0) {
					return coord;
				}
				
				if (boardArray[row+i][col+i] == player) {
					coord[0] = row+i;
					coord[1] = col+i;
					return coord;
				}
				i++;
			}
			break;
			
		case 4:
			for (i=++row; i<8; i++) {
				if (boardArray[i][col] == 0) {
					return coord;
				}
				
				if (boardArray[i][col] == player) {
					coord[0] = i;
					coord[1] = col;
					return coord;
				}
			}
			break;
			
		case 5:
			i = 1;
			while (row+i < 8 && col-i >= 0) {
				if (boardArray[row+i][col-i] == 0) {
					return coord;
				}
				
				if (boardArray[row+i][col-i] == player) {
					coord[0] = row+i;
					coord[1] = col-i;
					return coord;
				}
				i++;
			}
			break;
			
		case 6:
			for (i=--col; i>=0; i--) {
				if (boardArray[row][i] == 0) {
					return coord;
				}
				
				if (boardArray[row][i] == player) {
					coord[0] = row;
					coord[1] = i;
					return coord;
				}
			}
			break;
			
		case 7:
			i = 1;
			while (row-i >= 0 && col-i >= 0) {
				if (boardArray[row-i][col-i] == 0) {
					return coord;
				}
				
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
	
	private int opponent() {
		if (currentPlayer == 1) {
			return 2;
		} else {
			return 1;
		}
	}
	
	private int[][] adjacentTiles(int row, int col) {
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
	
	private int[][] copyBoardArray() {
        int[][] newBoardArray = new int[size][];
        for (int i = 0; i < size; i++) {
        	newBoardArray[i] = Arrays.copyOf(boardArray[i], size);
        }
        return newBoardArray;
	}
	
	public void place(int player, int[] coord) {
		place(player, coord, boardArray);
	}
	
	public void place(int player, int[] coord, int[][] board) {
		int row = coord[0];
		int col = coord[1];
		board[row][col] = player;
	}
	
	public void displayBoard() {
		displayBoard(boardArray);
	}
	
	private void displayBoard(int[][] board) {
		System.out.print("   ");
		for (int i=1; i<=size; i++) {
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
						System.out.print(" x ");
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
	
	public boolean gameEnd() {
		for (int i=0; i<size; i++) {
			for (int j=0; j<size; j++) {
				if (boardArray[i][j] == 0) {
					return false;
				}
			}
		}
		
		return true;
	}

}
