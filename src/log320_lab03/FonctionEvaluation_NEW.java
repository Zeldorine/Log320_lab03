package log320_lab03;

import java.util.ArrayList;
import java.util.Collections;
import static log320_lab03.Client.BLANC;
import static log320_lab03.Client.CASE_VIDE;
import static log320_lab03.Client.NOIR;

/**
 * Utilise la classe FonctionEvaluation.GetValue(board, couleur)
 *
 * @author Zeldorine
 */
public class FonctionEvaluation_NEW {

    private static int[][] board;
    private static int nombreDeMoves = 22;

    public static ArrayList<Move> coupPossible(int[][] board, int couleur) {
        FonctionEvaluation_NEW.board = board;
        ArrayList<Move> coups = new ArrayList(nombreDeMoves);

        if (couleur == BLANC) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] == BLANC) {
                        int jplus1 = j + 1;
                        int iplus1 = i + 1;
                        int imoins1 = i - 1;

                        if ((jplus1) < 8 && board[i][jplus1] == CASE_VIDE) {
                            ajouterCoup(coups, i, j, i, jplus1, BLANC);
                        }

                        if (imoins1 >= 0 && (jplus1) < 8 && board[imoins1][jplus1] != BLANC) {
                            ajouterCoup(coups, i, j, imoins1, jplus1, BLANC);
                        }

                        if (iplus1 < 8 && (jplus1) < 8 && board[iplus1][jplus1] != BLANC) {
                            ajouterCoup(coups, i, j, iplus1, jplus1, BLANC);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int j = 7; j >= 0; j--) {
                    if (board[i][j] == NOIR) {
                        int jmoins1 = j - 1;
                        int iplus1 = i + 1;
                        int imoins1 = i - 1;

                        if ((jmoins1) >= 0 && board[i][jmoins1] == CASE_VIDE) {
                            ajouterCoup(coups, i, j, i, jmoins1, NOIR);
                        }

                        if (imoins1 >= 0 && jmoins1 >= 0 && board[imoins1][jmoins1] != NOIR) {
                            ajouterCoup(coups, i, j, imoins1, jmoins1, NOIR);
                        }

                        if (iplus1 < 8 && jmoins1 >= 0 && board[iplus1][jmoins1] != NOIR) {
                            ajouterCoup(coups, i, j, iplus1, jmoins1, NOIR);
                        }
                    }
                }
            }
        }

        nombreDeMoves = coups.size() + 5;
        
        // Ordonner les coups pour le killer Move
        Collections.sort(coups, Service.getComparateurMove(couleur));
        return coups;
    }

    private static void ajouterCoup(ArrayList coups, int departX, int departY, int arriveeX, int arriveeY, int couleur) {
        int[][] tmpBoard = Service.copieBoard(board);
        Service.effectuerMove(tmpBoard, departX, departY, arriveeX, arriveeY, couleur);

        coups.add(new Move(tmpBoard, departX, departY, arriveeX, arriveeY, FonctionEvaluation.GetValue(tmpBoard, couleur), Service.GetHashValue(tmpBoard, couleur)));
    }
}
