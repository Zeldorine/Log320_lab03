package log320_lab03;

import java.util.ArrayList;
import java.util.HashMap;
import static log320_lab03.Client.BLANC;
import static log320_lab03.Client.COULEUR_JOUEUR;
import static log320_lab03.Client.NOIR;
import log320_lab03.EntreeTableTransposition.BoardTTEntryType;
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
                valeur = -AlphaBetaTT(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -infini, -meilleurValeur, ObtenirCouleurOppose(couleur));
            } else {
                valeur = -AlphaBetaTT(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -meilleurValeur - 1, -meilleurValeur, ObtenirCouleurOppose(couleur));
                if (valeur > meilleurValeur) {
                    valeur = -AlphaBetaTT(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -infini, -meilleurValeur, ObtenirCouleurOppose(couleur));
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

    private static int AlphaBetaTT(Move ExamineBoard, int Depth, int Alpha, int Beta, int couleur) {
        if (timer.getTime() >= tempsPourJouer) {
            stop = true;
            return tempsFin;
        }

        long HashValue = Service.GetHashValue(ExamineBoard.board, couleur);
        EntreeTableTransposition TTEntry = null;
        boolean Contains = TableTransposition.instance.Table.containsKey(HashValue);
        if (Contains) {
            //System.out.println("Hash contains");
            TTEntry = TableTransposition.instance.Table.get(HashValue);
        }

        if (Contains && TTEntry != null && TTEntry.Depth >= Depth) {
            BoardTTEntryType TTEntryType = TTEntry.Type;

            if (TTEntryType == BoardTTEntryType.ExactValue) // stored value is exact
            {
                return TTEntry.Value;
            }
            if (TTEntryType == BoardTTEntryType.Lowerbound && TTEntry.Value > Alpha) {
                Alpha = TTEntry.Value; // update lowerbound alpha if needed
            } else if (TTEntryType == BoardTTEntryType.Upperbound && TTEntry.Value < Beta) {
                Beta = TTEntry.Value; // update upperbound beta if needed
            }
            if (Alpha >= Beta) {
                return TTEntry.Value; // if lowerbound surpasses upperbound
            }
        }
        if (Depth == 0 || Service.plateauFinal(ExamineBoard.board)) {
            int value = ExamineBoard.valeur + Depth; // add depth (since it's inverse)
            if (value <= Alpha) // a lowerbound value
            {
                TableTransposition.instance.StoreEntry(HashValue, new EntreeTableTransposition(value, BoardTTEntryType.Lowerbound, Depth));
            } else if (value >= Beta) // an upperbound value
            {
                TableTransposition.instance.StoreEntry(HashValue, new EntreeTableTransposition(value, BoardTTEntryType.Upperbound, Depth));
            } else // a true minimax value
            {
                TableTransposition.instance.StoreEntry(HashValue, new EntreeTableTransposition(value, BoardTTEntryType.ExactValue, Depth));
            }
            return value;
        }

        nbNoeud++;

        if (timer.getTime() >= tempsPourJouer) {
            stop = true;
            return tempsFin;
        }

        ArrayList<Move> Successors = FonctionEvaluation_NEW.coupPossible(ExamineBoard.board, couleur);
        int totalBoards = Successors.size();

        if (totalBoards == 0) {
            return ExamineBoard.valeur;
        }

        Depth--;
        int Best = -valeurGagnant - 1;

        Move BoardToEvaluate;
        for (int i = 0; i < totalBoards; i++) {
            if (timer.getTime() >= tempsPourJouer) {
                stop = true;
                return tempsFin;
            }

            BoardToEvaluate = Successors.get(i);
            int value = -AlphaBetaTT(BoardToEvaluate, Depth, -Beta, -Alpha, Service.getOppositeColor(couleur));

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

        if (Best <= Alpha) // a lowerbound value
        {
            TableTransposition.instance.StoreEntry(HashValue, new EntreeTableTransposition(Best, BoardTTEntryType.Lowerbound, Depth));
        } else if (Best >= Beta) // an upperbound value
        {
            TableTransposition.instance.StoreEntry(HashValue, new EntreeTableTransposition(Best, BoardTTEntryType.Upperbound, Depth));
        } else // a true minimax value
        {
            TableTransposition.instance.StoreEntry(HashValue, new EntreeTableTransposition(Best, BoardTTEntryType.ExactValue, Depth));
        }
        return Best;
    }
}
