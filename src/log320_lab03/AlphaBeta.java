package log320_lab03;

import java.util.ArrayList;
import java.util.HashMap;
import static log320_lab03.Client.BLANC;
import static log320_lab03.Client.COULEUR_JOUEUR;
import static log320_lab03.Client.NOIR;
import log320_lab03.EntreeTableTransposition.EntreeTableTranspositionType;
import static log320_lab03.FonctionEvaluation.valeurGagnant;

/**
 *
 * @author Zeldorine
 */
public class AlphaBeta {

    public static int nbNoeud = 0;
    public static SimpleStopwatch timer = new SimpleStopwatch();
    public static int profondeurMax = 7;
    public static int tempsPourJouer = 4900;
    private static final int tempsFin = -9999999;
    private static final int infini = 500000;
    private static boolean stop = false;

    public static Move AlphaBetaIteratif(int[][] ExamineBoard, int couleur, boolean verbose) {
        timer.Reset();
        timer.Start();
        stop = false;
        TableTransposition.instance.Table = new HashMap<>();

        Move meilleurMove = null;
        Move dernierMove = null;
        int profondeur;

        for (profondeur = 1; profondeur < profondeurMax; profondeur++) {
            dernierMove = AlphaBetaRoot(ExamineBoard, profondeur, couleur);

            if (timer.getTime() >= tempsPourJouer || dernierMove == null) {
                stop = true;
                break;
            }
            meilleurMove = dernierMove;
        }

        if (verbose) {
            if (stop) {
                System.out.println("BREAK");
            }
            System.out.println("Profondeur recherche = " + profondeur);
            System.out.println("Nombre de noeud cherche = " + nbNoeud);
        }

        return meilleurMove;
    }

    public static Move AlphaBetaRoot(int[][] ExamineBoard, int profondeur, int couleur) {
        int meilleurValeur = -infini;
        boolean premierAppel = true;
        nbNoeud = 0;
        profondeur--;

        ArrayList<Move> moves = FonctionEvaluation_NEW.coupPossible(ExamineBoard, couleur);
        int totalMoves = moves.size();
        Move meilleurMove = moves.get(0);

        for (int i = 0; i < totalMoves; i++) {
            if (timer.getTime() >= tempsPourJouer) {
                stop = true;
                return null;
            }

            Move moveAEvaluer = moves.get(i);
            int valeur;
            if (premierAppel) {
                valeur = -AlphaBeta(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -infini, -meilleurValeur, ObtenirCouleurOppose(couleur));
            } else {
                valeur = -AlphaBeta(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -meilleurValeur - 1, -meilleurValeur, ObtenirCouleurOppose(couleur));
                if (valeur > meilleurValeur) {
                    valeur = -AlphaBeta(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -infini, -meilleurValeur, ObtenirCouleurOppose(couleur));
                }
            }

            //valeur = miniMax(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), Integer.MIN_VALUE, Integer.MAX_VALUE, COULEUR_JOUEUR);
            if (valeur > meilleurValeur) {
                meilleurValeur = valeur;
                meilleurMove = new Move(moveAEvaluer.board, moveAEvaluer.move, valeur);
                premierAppel = false;
                Client.bestMoveG = meilleurMove;
            }

        }

        if (timer.getTime() >= tempsPourJouer) {
            stop = true;
            return null;
        }
        return meilleurMove;
    }

    private static int AlphaBeta(Move ExamineBoard, int profondeur, int Alpha, int Beta, int couleur) {
        if (timer.getTime() >= tempsPourJouer) {
            stop = true;
            return tempsFin;
        }

        if (profondeur == 0 || Service.plateauFinal(ExamineBoard.board)) {
            int value = ExamineBoard.valeur + profondeur;
            return value;
        }

        nbNoeud++;

        ArrayList<Move> moves = FonctionEvaluation_NEW.coupPossible(ExamineBoard.board, couleur);
        int totalMoves = moves.size();

        if (totalMoves == 0) {
            return ExamineBoard.valeur;
        }

        profondeur--;
        int Best = -FonctionEvaluation.valeurGagnant - 1;
        Move moveAEvaluer;

        for (int i = 0; i < totalMoves; i++) {
            moveAEvaluer = moves.get(i);
            int value = -AlphaBeta(moveAEvaluer, profondeur, -Beta, -Alpha, ObtenirCouleurOppose(couleur));

            if (value > Best) {
                Best = value;
            }
            if (Best > Alpha) {
                Alpha = Best;
            }
            if (Best >= Beta) {
                break;
            }
        }

        return Best;
    }

    private static int miniMax(Move moveCourant, int maxProfondeur, int alpha, int beta, int couleur) {
        if (timer.getTime() >= tempsPourJouer) {
            stop = true;
            return tempsFin;
        }

        if (maxProfondeur == 0 || Service.plateauFinal(moveCourant.board)) {
            int value = moveCourant.valeur;
            return value;
        }

        nbNoeud++;

        ArrayList<Move> moves = FonctionEvaluation_NEW.coupPossible(moveCourant.board, couleur);
        int totalMoves = moves.size();

        if (totalMoves == 0) {
            return moveCourant.valeur;
        }

        maxProfondeur--;
        if (couleur == COULEUR_JOUEUR) {
            int alphaCourant = Integer.MIN_VALUE;
            for (int i = 0; i < totalMoves; i++) {
                if (timer.getTime() >= tempsPourJouer) {
                    stop = true;
                    return tempsFin;
                }

                Move move = moves.get(i);
                alphaCourant = Math.max(alphaCourant, miniMax(move, maxProfondeur, alpha, beta, ObtenirCouleurOppose(couleur)));

                alpha = Math.max(alpha, alphaCourant);
                if (alpha >= beta) {
                    // return alpha;
                    break;
                }
            }
            return alphaCourant;
        }

        int betaCourant = Integer.MAX_VALUE;
        for (int i = 0; i < totalMoves; i++) {
            Move move = moves.get(i);

            if (timer.getTime() >= tempsPourJouer) {
                stop = true;
                return tempsFin;
            }

            betaCourant = Math.min(betaCourant, miniMax(move, maxProfondeur, alpha, beta, ObtenirCouleurOppose(couleur)));

            beta = Math.min(beta, betaCourant);
            if (beta <= alpha) {
                //return beta;
                break;
            }
        }

        return betaCourant;
    }

    private static int ObtenirCouleurOppose(int couleur) {
        if (couleur == NOIR) {
            return BLANC;
        } else {
            return NOIR;
        }
    }

    private static int modifierProdonfeur(int profondeur, int movesPossible) {
        if (movesPossible < 9) {
            profondeur += 2;
        }
        return profondeur;
    }

    private static int AlphaBetaTT(Move ExamineBoard, int profondeur, int Alpha, int Beta, int couleur) {
        if (timer.getTime() >= tempsPourJouer) {
            stop = true;
            return tempsFin;
        }

        long HashValue = Service.GetHashValue(ExamineBoard.board, couleur);
        EntreeTableTransposition entree = null;
        boolean contient = TableTransposition.instance.Table.containsKey(HashValue);
        if (contient) {
            //System.out.println("Hash contains");
            entree = TableTransposition.instance.Table.get(HashValue);
        }

        if (contient && entree != null && entree.profondeur >= profondeur) {
            EntreeTableTranspositionType entreType = entree.Type;

            if (entreType == EntreeTableTranspositionType.valeurExacte) 
            {
                return entree.valeur;
            }
            if (entreType == EntreeTableTranspositionType.limiteInferieure && entree.valeur > Alpha) {
                Alpha = entree.valeur;
            } else if (entreType == EntreeTableTranspositionType.limiteSuperieure && entree.valeur < Beta) {
                Beta = entree.valeur; 
            }
            if (Alpha >= Beta) {
                return entree.valeur; 
            }
        }
        if (profondeur == 0 || Service.plateauFinal(ExamineBoard.board)) {
            int value = ExamineBoard.valeur + profondeur;
            if (value <= Alpha) 
            {
                TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(value, EntreeTableTranspositionType.limiteInferieure, profondeur));
            } else if (value >= Beta) 
            {
                TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(value, EntreeTableTranspositionType.limiteSuperieure, profondeur));
            } else
            {
                TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(value, EntreeTableTranspositionType.valeurExacte, profondeur));
            }
            return value;
        }

        nbNoeud++;

        if (timer.getTime() >= tempsPourJouer) {
            stop = true;
            return tempsFin;
        }

        ArrayList<Move> moves = FonctionEvaluation_NEW.coupPossible(ExamineBoard.board, couleur);
        int totalMoves = moves.size();

        if (totalMoves == 0) {
            return ExamineBoard.valeur;
        }

        profondeur--;
        int meilleur = -valeurGagnant - 1;

        Move moveAEvaluer;
        for (int i = 0; i < totalMoves; i++) {
            if (timer.getTime() >= tempsPourJouer) {
                stop = true;
                return tempsFin;
            }

            moveAEvaluer = moves.get(i);
            int valeur = -AlphaBetaTT(moveAEvaluer, profondeur, -Beta, -Alpha, Service.getOppositeColor(couleur));

            if (valeur > meilleur) {
                meilleur = valeur;
            }
            if (meilleur > Alpha) {
                Alpha = meilleur;
            }
            if (meilleur >= Beta) {
                break;
            }
        }

        if (meilleur <= Alpha) // a lowerbound value
        {
            TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(meilleur, EntreeTableTranspositionType.limiteInferieure, profondeur));
        } else if (meilleur >= Beta) // an upperbound value
        {
            TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(meilleur, EntreeTableTranspositionType.limiteSuperieure, profondeur));
        } else // a true minimax value
        {
            TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(meilleur, EntreeTableTranspositionType.valeurExacte, profondeur));
        }
        return meilleur;
    }
}
