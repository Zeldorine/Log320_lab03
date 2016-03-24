package log320_lab03;

/**
 *
 * @author Zeldorine
 */
public class Move {

    // public String move;
    public int departX;
    public int departY;
    public int arriveeX;
    public int arriveeY;
    public int valeur;
    public int[][] board;
    public long hash;

    public Move(int[][] board, int departX, int departY, int arriveeX, int arriveeY, int valeur, long hash) {
        this.board = board;
        this.departX = departX;
        this.departY = departY;
        this.arriveeX = arriveeX;
        this.arriveeY = arriveeY;
        this.valeur = valeur;
        this.hash = hash;
    }

    @Override
    public String toString() {
        return /*move + */" -> " + valeur;
    }
}
