package com.example.tictacttoe;

import android.os.Build;
import android.util.Pair;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.lang.annotation.Retention;
import static com.example.tictacttoe.TicTacToe.BoardState.SPACE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class TicTacToe {

    static final int BOARD_ROW = 3;
    static final int BOARD_COLUMN = 3;
    private int board[][];

    private BoardPlayer playerToMove = BoardPlayer.PLAYER_X;

    @Nullable
    private TicTacToeListener ticTacToeListener;
    private int numberOfMoves = 0;

    public TicTacToe() {
        initGame();
    }

    public void setTicTacToeListener(@Nullable TicTacToeListener ticTacToeListener) {
        this.ticTacToeListener = ticTacToeListener;
    }

    boolean isValidMove(int x, int y) {
        return board[x][y] == SPACE;
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public boolean moveAt(@IntRange(from = 0, to = 2) int x, @IntRange (from = 0, to = 2) int y) {
        if (x < 0 || x > BOARD_ROW - 1 || y < 0 || y > BOARD_COLUMN - 1) {
            throw new IllegalArgumentException(String.format("Coordinates %d and %d are not valid, valid set [0,1,2]", x, y));
        }
        if (!isValidMove(x, y)) {
            return false;
        }
        numberOfMoves++;
        if (ticTacToeListener != null) {
            ticTacToeListener.movedAt(x, y, playerToMove.move);
        }
        board[x][y] = playerToMove.move;
        Pair<Boolean, SquareCoordinates[]> won = hasWon(x, y, playerToMove);
        if (won.first && ticTacToeListener != null) {
            ticTacToeListener.gameWonBy(playerToMove, won.second);
        }
        else if (numberOfMoves == BOARD_COLUMN * BOARD_ROW && ticTacToeListener != null) {
            ticTacToeListener.gameEndsWithATie();
        }
        changeTurnToNextPlayer();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    private Pair<Boolean, SquareCoordinates[]> hasWon(int x, int y, BoardPlayer playerToMove) {
        SquareCoordinates[] winCoordinates = new SquareCoordinates[3];
        boolean hasWon = checkRow(x, y, playerToMove.move, winCoordinates)
                || checkColumn(x, y, playerToMove.move, winCoordinates)
                || checkDiagonals(x, y, playerToMove.move, winCoordinates);
        return Pair.create(hasWon, winCoordinates);
    }

    private boolean checkDiagonals(int x, int y, int move, SquareCoordinates[] winCoordinates) {
        if ((board[0][0] == move && board[1][1] == move && board[2][2] == move)) {
            winCoordinates[0] = new SquareCoordinates(0, 0);
            winCoordinates[1] = new SquareCoordinates(1, 1);
            winCoordinates[2] = new SquareCoordinates(2, 2);
            return true;
        }
        else if ((board[0][2] == move && board[1][1] == move && board[2][0] == move)) {
            winCoordinates[0] = new SquareCoordinates(0, 2);
            winCoordinates[1] = new SquareCoordinates(1, 1);
            winCoordinates[2] = new SquareCoordinates(2, 0);
            return true;
        }
        return false;
    }

    private boolean checkColumn(int x, int y, int movetoCheck, SquareCoordinates[] winCoordinates) {
        for (int i = 0; i < BOARD_ROW; i++) {
            if (board[i][y] != movetoCheck) {
                return false;
            }
        }
        for (int i = 0; i < winCoordinates.length; i++) {
            winCoordinates[i] = new SquareCoordinates(i, y);
        }
        return true;
    }

    private boolean checkRow(int x, int y, int movetoCheck, SquareCoordinates[] winCoordinates) {
        for (int i = 0; i < BOARD_ROW; i++) {
            if (board[x][i] != movetoCheck) {
                return false;
            }
        }
        for (int i = 0; i < winCoordinates.length; i++) {
            winCoordinates[i] = new SquareCoordinates(x, i);
        }
        return true;
    }

    private void changeTurnToNextPlayer() {
        if (playerToMove.equals(BoardPlayer.PLAYER_X)) {
            playerToMove = BoardPlayer.PLAYER_O;
        }
        else {
            playerToMove = BoardPlayer.PLAYER_X;
        }
    }

    public BoardPlayer getPlayerToMove() {
        return playerToMove;
    }

    private void initGame() {
        board = new int[BOARD_ROW][BOARD_COLUMN];
        playerToMove = BoardPlayer.PLAYER_X;
        numberOfMoves = 0;
    }

    void resetGame() {
        initGame();
    }

    @BoardState
    int getMoveAt(int x, int y) {
        if (board[x][y] == SPACE) {
            return SPACE;
        }
        else if (board[x][y] == BoardState.MOVE_O) {
            return BoardState.MOVE_O;
        }
        else {
            return BoardState.MOVE_X;
        }
    }

    @Retention(SOURCE)
    @IntDef({SPACE, BoardState.MOVE_X, BoardState.MOVE_O})
    public @interface BoardState {
        int SPACE = 0;
        int MOVE_X = 1;
        int MOVE_O = 2;
    }

    public enum BoardPlayer {
        PLAYER_X(BoardState.MOVE_X), PLAYER_O(BoardState.MOVE_O);
        public int move = SPACE;

        BoardPlayer(int move) {
            this.move = move;
        }
    }

    public interface TicTacToeListener {
        void gameWonBy(BoardPlayer boardPlayer, SquareCoordinates winPoints[]);

        void gameEndsWithATie();

        void movedAt(int x, int y, int move);
    }

    // todo use this for passing coordinates
    public static final class SquareCoordinates {
        public final int i; // holds the row index of a Square on Board
        public final int j; // holds the column index of a Square on Board

        public SquareCoordinates(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SquareCoordinates that = (SquareCoordinates) o;

            if (i != that.i) {
                return false;
            }
            return j == that.j;
        }

        @Override
        public int hashCode() {
            int result = i;
            result = 31 * result + j;
            return result;
        }
    }


}
