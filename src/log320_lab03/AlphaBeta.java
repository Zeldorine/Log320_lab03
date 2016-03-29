package log320_lab03;

import java.util.ArrayList;
import log320_lab03.EntreeTableTransposition.EntreeTableTranspositionType;
import static log320_lab03.FonctionEvaluation.GAGNANT;

/**
 *
 * @author Zeldorine
 */
public class AlphaBeta {

    public static int nbNoeud = 0;
    public static SimpleStopwatch timer = new SimpleStopwatch();
    public static int profondeurMax = 8;
    public static int tempsPourJouer = 4950;
    private static final int tempsFin = -9999999;
    private static final int infini = 500000;

    public static Move AlphaBetaIteratif(int[][] ExamineBoard, int couleur, boolean verbose, ArrayList<Move> coupsPossible) {
       // timer.Reset();
       // timer.Start();

        Move meilleurMove = null;
        Move dernierMove = null;
        int profondeur;

        for (profondeur = 1; profondeur < profondeurMax; profondeur++) { // Approfondissement iteratif
            dernierMove = AlphaBetaRoot(ExamineBoard, profondeur, couleur, coupsPossible);

            if (timer.getTime() >= tempsPourJouer || dernierMove == null) {
                break;
            }

            meilleurMove = dernierMove;
        }

        /*if (verbose) {
            System.out.println("Profondeur recherche = " + profondeur);
            System.out.println("Nombre de noeud cherche = " + nbNoeud);
         */
        return meilleurMove;
    }

    // Utilisation de Negamax
    public static Move AlphaBetaRoot(int[][] ExamineBoard, int profondeur, int couleur, ArrayList<Move> coupsPossible) {
        int meilleurValeur = -infini;
        boolean premierAppel = true;
        // nbNoeud = 0;
        profondeur--;

        int totalMoves = coupsPossible.size();
        Move meilleurMove = coupsPossible.get(0);

        for (int i = 0; i < totalMoves; i++) {
            if (timer.getTime() >= tempsPourJouer) {
                return meilleurMove;
            }

            Move moveAEvaluer = coupsPossible.get(i);
            int valeur;
            if (premierAppel) {
                valeur = -AlphaBetaTT(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -infini, -meilleurValeur, Service.getOppositeColor(couleur));
            } else {
                valeur = -AlphaBetaTT(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -meilleurValeur - 1, -meilleurValeur, Service.getOppositeColor(couleur));
                if (valeur > meilleurValeur) {// Effet d'horizon avec selective deeping
                    valeur = -AlphaBetaTT(moveAEvaluer, modifierProdonfeur(profondeur, totalMoves), -infini, -meilleurValeur, Service.getOppositeColor(couleur));
                }
            }

            if (valeur > meilleurValeur) {
                meilleurValeur = valeur;
                meilleurMove = new Move(moveAEvaluer.board,
                        moveAEvaluer.departX, moveAEvaluer.departY, moveAEvaluer.arriveeX, moveAEvaluer.arriveeY, meilleurValeur, moveAEvaluer.hash);
                premierAppel = false;
            }
        }

        if (timer.getTime() >= tempsPourJouer) {
            return null;
        }

        return meilleurMove;
    }

    private static int AlphaBetaTT(Move ExamineBoard, int profondeur, int Alpha, int Beta, int couleur) {
        //nbNoeud++;

        if (timer.getTime() >= tempsPourJouer) {
            return tempsFin;
        }

        long HashValue = ExamineBoard.hash;
        EntreeTableTransposition entree = null;
        boolean contient = TableTransposition.instance.Table.containsKey(HashValue);
        if (contient) {
            entree = TableTransposition.instance.Table.get(HashValue);
        }

        if (contient && entree != null && entree.profondeur >= profondeur) {
            EntreeTableTranspositionType entreType = entree.Type;

            if (entreType == EntreeTableTranspositionType.valeurExacte) {
                return entree.valeur;
            }
            if (entreType == EntreeTableTranspositionType.limiteInferieure) {
                Alpha = Math.max(Alpha, entree.valeur);
            } else if (entreType == EntreeTableTranspositionType.limiteSuperieure) {
                Beta = Math.min(Beta, entree.valeur);
            }
            if (Alpha >= Beta) {
                return entree.valeur;
            }
        }
        if (profondeur == 0 || Service.plateauFinal(ExamineBoard.board)) {
            int value = ExamineBoard.valeur + profondeur;
            if (value <= Alpha) {
                TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(value, EntreeTableTranspositionType.limiteSuperieure, profondeur));
            } else if (value >= Beta) {
                TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(value, EntreeTableTranspositionType.limiteInferieure, profondeur));
            } else {
                TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(value, EntreeTableTranspositionType.valeurExacte, profondeur));
            }
            return value;
        }

        if (timer.getTime() >= tempsPourJouer) {
            return tempsFin;
        }

        ArrayList<Move> moves = FonctionEvaluation_NEW.coupPossible(ExamineBoard.board, couleur);
        int totalMoves = moves.size();

        if (totalMoves == 0) {
            return ExamineBoard.valeur;
        }

        profondeur--;
        int meilleur = -GAGNANT - 1;//Recherche aspirante avec fenetre null

        Move moveAEvaluer;
        for (int i = 0; i < totalMoves; i++) {
            if (timer.getTime() >= tempsPourJouer) {
                return tempsFin;
            }

            moveAEvaluer = moves.get(i);
            int valeur = -AlphaBetaTT(moveAEvaluer, profondeur, -Beta, -Alpha, Service.getOppositeColor(couleur));
            Alpha = meilleur = Math.max(meilleur, valeur);
            if (meilleur >= Beta) {
                break;
            }
        }

        if (meilleur <= Alpha) {
            TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(meilleur, EntreeTableTranspositionType.limiteSuperieure, profondeur));
        } else if (meilleur >= Beta) {
            TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(meilleur, EntreeTableTranspositionType.limiteInferieure, profondeur));
        } else {
            TableTransposition.instance.sauveEntree(HashValue, new EntreeTableTransposition(meilleur, EntreeTableTranspositionType.valeurExacte, profondeur));
        }

        return meilleur;
    }

    private static int modifierProdonfeur(int profondeur, int movesPossible) {
        if (movesPossible < 9) {
            profondeur += 5;
        }
        return profondeur;
    }
}
