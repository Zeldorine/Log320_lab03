package log320_lab03;

import static log320_lab03.Client.BLANC;
import static log320_lab03.Client.CASE_VIDE;
import static log320_lab03.Client.COULEUR_JOUEUR;
import static log320_lab03.Client.NOIR;

/**
 *
 * @author Zeldorine
 */
public class FonctionEvaluation {

    private static final int MULTIPLICATEUR = 3;
    public static final int GAGNANT = 500000;
    private static final short PION_PROCHE_GAGNER = 10000;
    private static final short PION = 1300;
    private static final short PION_DANGER = 10;
    private static final short PION_FORT_DANGER = 100;
    private static final short PION_SE_FAIT_MANGER = 50;
    private static final short PION_CONNECTE_HORIZONTAL = 35;
    private static final short PION_CONNECTE_VERTICAL = 15;
    private static final short TROU_COLONNE = 20;
    private static final short PION_MAISON = 10;

    public static int GetValue(int[][] board, int couleur) {
        int valeur = 0;
        boolean BlancGagne = false;
        boolean noirGagne = false;

        int totalBlancPions = 0;
        int totalNoirPions = 0;

        for (int i = 0; i < 8; i++) {
            int totalNoirColonne = 0;
            int totalBlancColonne = 0;

            for (int j = 0; j < 8; j++) {
                int pion = board[i][j];

                if (pion == Client.CASE_VIDE) {
                    continue;
                }

                if (pion == Client.BLANC) {
                    totalBlancPions++;
                    totalBlancColonne++;
                    valeur += GetPieceValeur(board, i, j, couleur);
                    if (j == 7) {
                        BlancGagne = true;
                    } else if (j == 6) {
                        boolean dangerDiago1 = false;
                        boolean dangerDiago2 = false;
                        if (i > 0) {
                            dangerDiago1 = (board[i - 1][7] == CASE_VIDE);
                        }
                        if (i < 7) {
                            dangerDiago2 = (board[i + 1][7] == CASE_VIDE);
                        }
                        if (!(dangerDiago1 && dangerDiago2)) {
                            valeur += PION_PROCHE_GAGNER;
                        }
                    } else if (j == 0) {
                        valeur += PION_MAISON;
                    }
                } else {
                    totalNoirPions++;
                    totalNoirColonne++;
                    valeur -= GetPieceValeur(board, i, j, couleur);
                    if (j == 0) {
                        noirGagne = true;
                    } else if (j == 1) {
                        boolean dangerDiago1 = false;
                        boolean dangerDiago2 = false;
                        if (i > 0) {
                            dangerDiago1 = (board[i - 1][0] == CASE_VIDE);
                        }
                        if (i < 7) {
                            dangerDiago2 = (board[i + 1][0] == CASE_VIDE);
                        }
                        if (!(dangerDiago1 && dangerDiago2)) {
                            valeur -= PION_PROCHE_GAGNER;
                        }
                    } else if (j == 7) {
                        valeur -= PION_MAISON;
                    }
                }
            }

            if (totalBlancColonne == 0) {
                valeur -= TROU_COLONNE;
            }
            if (totalNoirColonne == 0) {
                valeur += TROU_COLONNE;
            }
        }

        if (totalBlancPions == 0) {
            noirGagne = true;
        } else {
            valeur += totalBlancPions;
        }

        if (totalNoirPions == 0) {
            BlancGagne = true;
        } else {
            valeur -= totalNoirPions;
        }

        if (BlancGagne) {
            valeur += GAGNANT;
        } else if (noirGagne) {
            valeur -= GAGNANT;
        }

        // Pour NegaMax
        if (Service.getOppositeColor(couleur) == NOIR) {
            valeur = -valeur;
        }

        return valeur;
    }

    private static int GetPieceValeur(int[][] board, int ligne, int colonne, int joueur) {
        int valeur = PION;
        int couleur = board[ligne][colonne];
        int mange = mange(board, ligne, colonne, joueur);

        if (connecteHorizontal(board, ligne, colonne)) {
            valeur += PION_CONNECTE_HORIZONTAL;
        }
        if (connecteVertical(board, ligne, colonne)) {
            valeur += PION_CONNECTE_VERTICAL;
        }

        if (mange > 0) {
            valeur -= PION_SE_FAIT_MANGER;
        }

        int multiplicateur = 1;
        
        if (couleur == BLANC) {
            if (COULEUR_JOUEUR == NOIR) {
                multiplicateur = MULTIPLICATEUR;
            }
            if (ligne == 5) {
                valeur += PION_DANGER * multiplicateur;
            } else if (ligne == 6) {
                valeur += PION_FORT_DANGER * multiplicateur;
            }
        } else {
            if (COULEUR_JOUEUR == BLANC) {
                multiplicateur = MULTIPLICATEUR;
            }
            if (ligne == 2) {
                valeur += PION_DANGER * multiplicateur;
            } else if (ligne == 1) {
                valeur += PION_FORT_DANGER * multiplicateur;
            }
        }

        if (couleur == BLANC) {
            valeur += (ligne) * PION_DANGER;
        } else {
            valeur += (7 - ligne) * PION_DANGER;
        }

        valeur += Service.nbMovesPossible(board, ligne, colonne);

        return valeur;
    }

    private static boolean connecteHorizontal(int[][] board, int i, int j) {
        if (i > 0) {
            if (board[i - 1][j] == board[i][j]) {
                return true;
            }
        }

        if (i < 7) {
            if (board[i + 1][j] == board[i][j]) {
                return true;
            }
        }

        return false;
    }

    private static boolean connecteVertical(int[][] board, int i, int j) {
        if (j > 0) {
            if (board[i][j - 1] == board[i][j]) {
                return true;
            }
        }

        if (j < 7) {
            if (board[i][j + 1] == board[i][j]) {
                return true;
            }
        }

        return false;
    }

    private static int mange(int[][] board, int i, int j, int joueur) {
        int attaque = 0;

        if (board[i][j] == BLANC) {
            int jplus1 = j + 1;
            int iplus1 = i + 1;
            int imoins1 = i - 1;

            if (imoins1 >= 0 && (jplus1) < 8) {
                if (board[imoins1][jplus1] == NOIR && joueur == BLANC) {
                    attaque += PION_SE_FAIT_MANGER;
                }
            }

            if (iplus1 < 8 && (jplus1) < 8) {
                if (board[iplus1][jplus1] == NOIR && joueur == BLANC) {
                    attaque += PION_SE_FAIT_MANGER;
                }
            }
        } else {
            int jmoins1 = j - 1;
            int iplus1 = i + 1;
            int imoins1 = i - 1;

            if (imoins1 >= 0 && jmoins1 >= 0) {
                if (board[imoins1][jmoins1] == BLANC && joueur == NOIR) {
                    attaque += PION_SE_FAIT_MANGER;
                }
            }

            if (iplus1 < 8 && jmoins1 >= 0) {
                if (board[iplus1][jmoins1] == BLANC && joueur == NOIR) {
                    attaque += PION_SE_FAIT_MANGER;
                }
            }
        }

        return attaque;
    }
}
