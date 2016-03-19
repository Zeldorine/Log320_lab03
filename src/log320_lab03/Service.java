package log320_lab03;

import java.util.Comparator;
import static log320_lab03.Client.BLANC;
import static log320_lab03.Client.CASE_VIDE;
import static log320_lab03.Client.NOIR;

/**
 *
 * @author Zeldorine
 */
public class Service {

    public static int getLigne(char ligne) {
        switch (ligne) {
            case 'A':
                return 0;
            case 'B':
                return 1;
            case 'C':
                return 2;
            case 'D':
                return 3;
            case 'E':
                return 4;
            case 'F':
                return 5;
            case 'G':
                return 6;
            case 'H':
                return 7;
            default:
                return -1;
        }
    }

    public static char getLigne(int ligne) {
        switch (ligne) {
            case 0:
                return 'A';
            case 1:
                return 'B';
            case 2:
                return 'C';
            case 3:
                return 'D';
            case 4:
                return 'E';
            case 5:
                return 'F';
            case 6:
                return 'G';
            case 7:
                return 'H';
            default:
                return 'Z';
        }
    }

    public static int getOppositeColor(int couleur) {
        return couleur == BLANC ? NOIR : BLANC;
    }

    public static int obtenirLigneCorrespondante(int ligne) {
        switch (ligne) {
            case 8:
                return 1;
            case 7:
                return 2;
            case 6:
                return 3;
            case 5:
                return 4;
            case 4:
                return 5;
            case 3:
                return 6;
            case 2:
                return 7;
            case 1:
                return 8;
            default:
                return 0;
        }
    }

    public static Comparator<Move> getComparateurMove() {
        return new Comparator<Move>() {
            @Override
            public int compare(Move o1, Move o2) {
                // Integer.c
                return (o2.valeur - o1.valeur);
            }
        };
    }

    public static void effectuerMove(int[][] board, String move, int couleur) {
        String depart;
        String arrivee;
        if (move.contains("-")) {
            String[] s = move.split("-");
            depart = s[0].trim();
            arrivee = s[1].trim();
        } else {
            depart = move.substring(0, 2);
            arrivee = move.substring(2, 4);
        }

        board[Service.getLigne(depart.charAt(0))][(int) depart.charAt(1) - 49] = 0;
        board[Service.getLigne(arrivee.charAt(0))][(int) arrivee.charAt(1) - 49] = couleur;
    }

    public static int nbPieces(int[][] board) {
        int nb = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != CASE_VIDE) {
                    nb++;
                }
            }
        }

        return nb;
    }

    public static boolean plateauFinal(int[][] board) {
        for (int i = 0; i < 8; i++) {
            if (board[i][0] == NOIR || board[i][7] == BLANC) {
                return true;
            }
        }

        int[] nbPions = nbBlancNoir(board);
        if (nbPions[0] == 0 || nbPions[1] == 0) {
            return true;
        }

        return false;
    }

    public static int quiGagne(int[][] board) {
        for (int i = 0; i < 8; i++) {
            if (board[i][0] == NOIR) {
                return NOIR;
            } else if (board[i][7] == BLANC) {
                return BLANC;
            }
        }

        return 0;
    }

    public static int[] nbBlancNoir(int[][] board) {
        int nbBlanc = 0;
        int nbNoir = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == BLANC) {
                    nbBlanc++;
                } else if (board[i][j] == NOIR) {
                    nbNoir++;
                }
            }
        }

        return new int[]{nbBlanc, nbNoir};
    }

    public static int[][] copieBoard(int[][] board) {
        int[][] copie = new int[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copie[i][j] = board[i][j];
            }
        }

        return copie;
    }

    public static long GetHashValue(int[][] board, int couleur) {
        int CurrentPiece;
        long HashValue = 0;
        int index = 0;

        for (short i = 0; i < 8; i++) {
            for (short j = 0; j < 8; j++) {
                CurrentPiece = board[i][j];
                if (CurrentPiece != CASE_VIDE) {
                    int val = CurrentPiece == BLANC ? 0 : 1;
                    HashValue ^= (TableTransposition.instance.PRNArray[val * 64 + index]);
                }
                index++;
            }
        }
        if (couleur == NOIR) {
            HashValue ^= TableTransposition.instance.PRNArray[128];
        }

        return HashValue;
    }
}
