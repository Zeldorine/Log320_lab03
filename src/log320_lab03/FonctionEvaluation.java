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

    public static final int valeurGagnant = 500000;
    public static final short valeurPionProcheGagner = 10000;
    public static final short valeurPion = 1300;
    public static final short valeurPionDanger = 10;
    public static final short valeurPionFortDanger = 100;
    public static final short valeurPionAttaque = 50;
    public static final short valeurPionProtege = 65;
    public static final short valeurPionConnecteHorizontal = 35;
    public static final short valeurPionConnecteVertical = 15;
    public static final short valeurTrouPionColonne = 20;
    public static final short valeurPionMaison = 10;

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
                    //if (square == Client.NOIR) {
                    //if (square == COULEUR_JOUEUR) {
                    totalBlancPions++;
                    totalBlancColonne++;
                    //value += GetPieceValue(board, i, j, Client.COULEUR_JOUEUR);
                    valeur += GetPieceValeur(board, i, j);
                    //value += GetPieceValue(board, i, j);
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
                            valeur += valeurPionProcheGagner;
                        }
                    } else if (j == 0) {
                        valeur += valeurPionMaison;
                    }
                } else {
                    totalNoirPions++;
                    totalNoirColonne++;
                    // value -= GetPieceValue(board, i, j, couleurEnnemie);
                    //value -= GetPieceValue(board, i, j, NOIR);
                    valeur -= GetPieceValeur(board, i, j);
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
                            valeur -= valeurPionProcheGagner;
                        }
                    } else if (j == 7) {
                        valeur -= valeurPionMaison;
                    }
                }
            }

            if (totalBlancColonne == 0) {
                if (COULEUR_JOUEUR == BLANC) {
                    valeur -= valeurTrouPionColonne;
                } else {
                    valeur += valeurTrouPionColonne;
                }
                // valeur -= PieceColumnHoleValue;
            }
            if (totalNoirColonne == 0) {
                if (COULEUR_JOUEUR == BLANC) {
                    valeur += valeurTrouPionColonne;
                } else {
                    valeur -= valeurTrouPionColonne;
                }
                // valeur += PieceColumnHoleValue;
            }
        }

        if (totalBlancPions == 0) {
            noirGagne = true;
        }
        if (totalNoirPions == 0) {
            BlancGagne = true;
        }

        if (BlancGagne) {
            // System.out.println("Blanc gagne");
            //if (COULEUR_JOUEUR == BLANC) {
            valeur += valeurGagnant;
            //} else {
            //  valeur -= valeurGagnant;
            //}
        } else if (noirGagne) {
            //System.out.println("Blanc gagne");
            //if (COULEUR_JOUEUR == NOIR) {
            //  valeur += valeurGagnant;
            //} else {
            valeur -= valeurGagnant;
            //}
        }

        /* if(COULEUR_JOUEUR == BLANC){
            valeur = -valeur;
        }*/
        if (ObtenirCouleurOppose(couleur) == NOIR) {
            valeur = -valeur;
        }
        return valeur;
    }

    private static int GetPieceValeur(int[][] board, int ligne, int colonne) {
        int valeur = valeurPion;
        int couleur = board[ligne][colonne];
        boolean protege = protege(board, ligne, colonne);
        boolean mange = mange(board, ligne, colonne);

        // add connections value
        if (connecteHorizontal(board, ligne, colonne)) {
            valeur += valeurPionConnecteHorizontal;
        }
        if (connecteVertical(board, ligne, colonne)) {
            valeur += valeurPionConnecteVertical;
        }

        // add to the value the protected value
        if (protege) {
            valeur += valeurPionProtege;
        }

        // evaluate attack
        if (mange) {
            //Value -= PieceAttackValue;
            valeur += valeurPionAttaque;
            if (protege == false) {
                valeur -= valeurPionAttaque;
            }
        } else if (protege == true) {// Gagne les noir
            //  if (couleur == BLANC) {// Gagne les noir
            if (couleur == BLANC) {
                if (ligne == 5) {
                    valeur += valeurPionDanger;
                } else if (ligne == 6) {
                    valeur += valeurPionFortDanger;
                }
            } else if (ligne == 2) {
                valeur += valeurPionDanger;
            } else if (ligne == 1) {
                valeur += valeurPionFortDanger;
            }
        }

        // danger value
        // if (couleur == BLANC) {// Gagne les noir
        if (couleur == BLANC) {
            valeur += ligne * valeurPionDanger;
        } else {
            valeur += (8 - ligne) * valeurPionDanger;
        }
        //compter les move valide pour la piece
        //valeur += Piece.ValidMoves.Count;
        return valeur;
    }

    private static boolean connecteVertical(int[][] board, int i, int j) {
        if (i > 1) {
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

    private static int GetPieceValeur(int[][] board, int ligne, int colonne, int couleurEnnemie) {
        int valeur = valeurPion;
        int couleur = board[ligne][colonne];
        boolean protege = protege(board, ligne, colonne);
        boolean mange = mange(board, ligne, colonne);

        // add connections value
        if (connecteHorizontal(board, ligne, colonne)) {
            valeur += valeurPionConnecteHorizontal;
        }
        if (connecteVertical(board, ligne, colonne)) {
            valeur += valeurPionConnecteVertical;
        }

        // add to the value the protected value
        if (protege) {
            valeur += valeurPionProtege;
        }

        // evaluate attack
        if (mange) {
            valeur -= valeurPionAttaque;
            if (protege == false) {
                valeur -= valeurPionAttaque;
            }
        } else if (protege == true) {// Gagne les noir
            // pawns at the end that are not attacked are worth more points
            if (couleur == couleurEnnemie) {
                if (ligne == 5) {
                    valeur += valeurPionDanger;
                } else if (ligne == 6) {
                    valeur += valeurPionFortDanger;
                }
            } else if (ligne == 2) {
                valeur += valeurPionDanger;
            } else if (ligne == 1) {
                valeur += valeurPionFortDanger;
            }
        }

        // danger value
        if (couleur == BLANC) {// Gagne les noir
            valeur += ligne * valeurPionDanger;
        } else {
            valeur += (8 - ligne) * valeurPionDanger;
        }

        //compter les move valide pour la piece
        //valeur += Piece.ValidMoves.Count;
        return valeur;
    }

    private static boolean connecteHorizontal(int[][] board, int i, int j) {
        if (j > 1) {
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

    private static int ObtenirCouleurOppose(int couleur) {
        if (couleur == NOIR) {
            return BLANC;
        } else {
            return NOIR;
        }
    }

    private static boolean protege(int[][] board, int i, int j) {
        if (board[i][j] == BLANC) {
            int jplus1 = j - 1;
            int iplus1 = i - 1;
            int imoins1 = i + 1;

            if (imoins1 < 8 && (jplus1) >= 0 && board[imoins1][jplus1] == BLANC) {
                return true;
            }

            if (iplus1 >= 0 && (jplus1) >= 0 && board[iplus1][jplus1] == BLANC) {
                return true;
            }

        } else {
            int jmoins1 = j + 1;
            int iplus1 = i - 1;
            int imoins1 = i + 1;

            if (imoins1 < 8 && jmoins1 < 8 && board[imoins1][jmoins1] == NOIR) {
                return true;
            }

            if (iplus1 >= 0 && jmoins1 < 8 && board[iplus1][jmoins1] == NOIR) {
                return true;
            }
        }

        return false;
    }

    private static boolean mange(int[][] board, int i, int j) {
        if (board[i][j] == BLANC) {
            int jplus1 = j + 1;
            int iplus1 = i + 1;
            int imoins1 = i - 1;

            if (imoins1 >= 0 && (jplus1) < 8 && board[imoins1][jplus1] == NOIR) {
                return true;
            }

            if (iplus1 < 8 && (jplus1) < 8 && board[iplus1][jplus1] != NOIR) {
                return true;
            }
        } else {
            int jmoins1 = j - 1;
            int iplus1 = i + 1;
            int imoins1 = i - 1;

            if (imoins1 >= 0 && jmoins1 >= 0 && board[imoins1][jmoins1] == BLANC) {
                return true;
            }

            if (iplus1 < 8 && jmoins1 >= 0 && board[iplus1][jmoins1] == BLANC) {
                return true;
            }
        }

        return false;
    }
}