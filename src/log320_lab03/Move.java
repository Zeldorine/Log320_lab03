package log320_lab03;

/**
 *
 * @author Zeldorine
 */
public class Move {

    public String move;
    public int valeur;
    public int[][] board;

    public Move(int[][] board, String move, int valeur) {
        this.board = board;
        this.move = move;
        this.valeur = valeur;
    }

    @Override
    public String toString() {
        return move + " -> " + valeur;
    }

    @Override
    public Move clone() {
        return new Move(Service.copieBoard(board), move, valeur);
    }
}
