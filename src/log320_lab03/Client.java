package log320_lab03;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class Client {

    private static int MAX_TIME = 4975;
    private static Socket MyClient;
    private static BufferedInputStream input;
    private static BufferedOutputStream output;
    private static int[][] board = new int[8][8];
    private static final int BLANC = 2;
    private static final int NOIR = 4;
    private static int COULEUR_JOUEUR;
    private static int COULEUR_ENNEMIE;
    private static Move firstBestMove;
    private static Move bestMoveG;
    private static boolean stop;
    private static ArrayList<Move> coupsPossible;
    private static Timer timer = new Timer();
    private static TimerTask task = new TimerTaskImpl();

    /**
     * Blanc joue en premier 5 seconde pour jouer
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            MyClient = new Socket("localhost", 8888);
            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            while (1 == 1) {
                stop = false;
                task.cancel();
                timer.purge();
                char cmd = (char) input.read();
                task = new TimerTaskImpl();
                switch (cmd) {
                    // Debut de la partie en joueur blanc
                    case '1':
                        Chrono.start();
                        timer.schedule(task, MAX_TIME);
                        initBlanc();
                        Chrono.stop();
                        break;
                    // Debut de la partie en joueur Noir
                    case '2':
                        Chrono.start();
                        initNoir();
                        Chrono.stop();
                        break;
                    // Le serveur demande le prochain coup
                    case '3':
                        Chrono.start();
                        timer.schedule(task, MAX_TIME);

                        byte[] aBuffer = new byte[16];
                        input.read(aBuffer, 0, input.available());
                        String s = new String(aBuffer);
                        effectuerMove(board, s, COULEUR_ENNEMIE);

                        //afficherPlateau();
                        jouer();

                        Chrono.stop();
                        break;
                    // Le dernier coup est invalide
                    case '4':
                        Chrono.start();
                        System.out.println("Coup invalide");
                        // timer.schedule(new TimerTaskImpl(), 4900);
                        // coupInvalide();
                        Chrono.stop();
                        break;
                    default:
                        break;
                }

                System.out.format("Temps d'execution : %.13f secondes \n", Chrono.getTime());
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void jouer() throws IOException {
        coupsPossible = coupPossible(board, COULEUR_JOUEUR);
        //afficherCoupValide();
        bestMoveG = null;
        firstBestMove = coupsPossible.get(0);// replace by min-max

        bestMoveG = rechercheMeilleurMove(copieBoard(board), coupsPossible);
        //bestMove = alphaBeta(true, copieBoard(board), 0, 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
        // effectuerMove(board, bestMove.move, COULEUR_JOUEUR);
        if (!stop) {
            if (bestMoveG == null) {
                bestMoveG = firstBestMove;
            }

            output.write(bestMoveG.move.getBytes(), 0, bestMoveG.move.length());
            output.flush();

            System.out.println("[NOT][HURRY] Coup jouer : " + bestMoveG.toString());
            effectuerMove(board, bestMoveG.move, COULEUR_JOUEUR);
        }

        //afficherPlateau();
    }

    private static Move rechercheMeilleurMove(int[][] tmpboard, ArrayList<Move> coups) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int bestScore = -Integer.MAX_VALUE;

        Move bestMove = null;
        int size = coups.size() <= 7 ? coups.size() : 7;

        for (int i = 0; i < size; i++) {
            Move child = coups.get(i);

            if (bestMove == null) {
                bestMove = child;
            }

            alpha = Math.max(alpha, miniMax(true, tmpboard, child, 11, alpha, beta));

            if (alpha > bestScore) {
                bestMove = child;
                bestScore = alpha;
            }
        }

        return bestMove;
    }

    private static int miniMax(boolean joueur, int[][] tmpboard, Move currentNode, int maxProfondeur, int alpha, int beta) {
        if (plateauFinal() || stop || maxProfondeur <= 0) {
            return currentNode.valeur;
        }

        ArrayList<Move> coups = coupPossible(tmpboard, joueur ? COULEUR_JOUEUR : COULEUR_ENNEMIE);
        int size = coups.size() <= 7 ? coups.size() : 7;

        if (joueur) {
            int currentAlpha = Integer.MIN_VALUE;
            for (int i = 0; i < size; i++) {
                Move child = coups.get(i);
                int[][] undoBoard = copieBoard(tmpboard);
                effectuerMove(undoBoard, child.move, COULEUR_JOUEUR);
                currentAlpha = Math.max(currentAlpha, miniMax(!joueur, undoBoard, child, maxProfondeur - 1, alpha, beta));
                alpha = Math.max(alpha, currentAlpha);
                if (alpha >= beta) {
                    return alpha;
                }
            }
            return currentAlpha;
        }

        int currentBeta = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            Move child = coups.get(i);
            int[][] undoBoard = copieBoard(tmpboard);
            effectuerMove(undoBoard, child.move, COULEUR_ENNEMIE);
            currentBeta = Math.min(currentBeta, miniMax(!joueur, undoBoard, child, maxProfondeur - 1, alpha, beta));
            beta = Math.min(beta, currentBeta);
            if (beta <= alpha) {
                return beta;
            }
        }
        return currentBeta;
    }

    /* private static int alphaBeta(boolean joueur, int[][] tmpboard, int maxProfondeur, int alpha, int beta) {
        // System.out.println("profondeur : " + profondeur);

        ArrayList<Move> coups = coupPossible(tmpboard, COULEUR_JOUEUR);

        if (plateauFinal() || stop || maxProfondeur<=0) {
            return coups.get(0).valeur;
        }

        Move returnMove;
        bestMove = null;
        int size = coups.size() <= 10 ? coups.size() : 10;

        if (joueur) {
            // joueur courant
            for (int i = 0; i < size; i++) {
                Move move = coups.get(i);
                int[][] undoBoard = copieBoard(tmpboard);
                effectuerMove(undoBoard, move.move, COULEUR_JOUEUR);
                returnMove = alphaBeta(!joueur, undoBoard, profondeur + 1, maxProfondeur, alpha, beta);
                // annulerMove(tmpboard, move, COULEUR_JOUEUR);
                if (bestMove == null || bestMove.valeur < returnMove.valeur) {
                    bestMove = returnMove;
                    bestMove.move = move.move;
                }

                if (returnMove.valeur > alpha) {
                    alpha = returnMove.valeur;
                    bestMove = returnMove;
                }
                if (beta <= alpha) {
                    bestMove.valeur = beta;
                    // bestMove.move = null;
                    return bestMove;
                }
            }
        } else {
            // adversaire
            for (int i = 0; i < size; i++) {
                Move move = coups.get(i);
                int[][] undoBoard = copieBoard(tmpboard);
                effectuerMove(undoBoard, move.move, COULEUR_JOUEUR);
                returnMove = alphaBeta(!joueur, undoBoard, profondeur + 1, maxProfondeur, alpha, beta);

                if (bestMove == null || bestMove.valeur < returnMove.valeur) {
                    bestMove = returnMove;
                    bestMove.move = move.move;
                }

                if (returnMove.valeur < beta) {
                    beta = returnMove.valeur;
                    bestMove = returnMove;
                }
                if (beta <= alpha) {
                    bestMove.valeur = alpha;
                    //bestMove.move = null;
                    return bestMove;
                }
            }
        }

        return bestMove;
    }*/
    private static int[][] copieBoard(int[][] board) {
        int[][] copie = new int[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copie[i][j] = board[i][j];
            }
        }

        return copie;
    }

    private static boolean plateauFinal() {
        for (int i = 0; i < 8; i++) {
            if (board[i][0] == NOIR || board[i][7] == BLANC) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    private static class TimerTaskImpl extends TimerTask {

        @Override
        public void run() {
            try {
                if (bestMoveG == null) {
                    bestMoveG = firstBestMove;
                }

                output.write(bestMoveG.move.getBytes(), 0, bestMoveG.move.length());
                output.flush();
                stop = true;
                System.out.println("HURRY UP - Coup jouer : " + bestMoveG.toString());
                effectuerMove(board, bestMoveG.move, COULEUR_JOUEUR);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void afficherCoupValide() {
        for (Move move : coupsPossible) {
            System.out.println(move.toString());
        }
    }

    // TODO : Trier les coups pour avoir le meilleur choix en premier
    private static ArrayList<Move> coupPossible(int[][] board, int couleur) {
        ArrayList<Move> coups = new ArrayList(25);

        if (couleur == BLANC) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] == BLANC) {
                        int jplus1 = j + 1;
                        int iplus1 = i + 1;
                        int imoins1 = i - 1;

                        if ((jplus1) < 8 && board[i][jplus1] != BLANC && board[i][jplus1] != NOIR) {
                            ajouterCoup(coups, i, j, i, jplus1, false, BLANC);
                        }

                        if (imoins1 >= 0 && (jplus1) < 8 && board[imoins1][jplus1] != BLANC) {
                            boolean mange = board[imoins1][jplus1] == NOIR;
                            ajouterCoup(coups, i, j, imoins1, jplus1, mange, BLANC);
                        }

                        if (iplus1 < 8 && (jplus1) < 8 && board[iplus1][jplus1] != BLANC) {
                            boolean mange = board[iplus1][jplus1] == NOIR;
                            ajouterCoup(coups, i, j, iplus1, jplus1, mange, BLANC);
                        }
                    }
                }
            }
        } else if (couleur == NOIR) {
            for (int i = 0; i < 8; i++) {
                for (int j = 7; j >= 0; j--) {
                    if (board[i][j] == NOIR) {
                        int jmoins1 = j - 1;
                        int iplus1 = i + 1;
                        int imoins1 = i - 1;

                        if ((jmoins1) >= 0 && board[i][jmoins1] != NOIR && board[i][jmoins1] != BLANC) {
                            ajouterCoup(coups, i, j, i, jmoins1, false, NOIR);
                        }

                        if (imoins1 >= 0 && jmoins1 >= 0 && board[imoins1][jmoins1] != NOIR) {
                            boolean mange = board[imoins1][jmoins1] == BLANC;
                            ajouterCoup(coups, i, j, imoins1, jmoins1, mange, NOIR);
                        }

                        if (iplus1 < 8 && jmoins1 >= 0 && board[iplus1][jmoins1] != NOIR) {
                            boolean mange = board[iplus1][jmoins1] == BLANC;
                            ajouterCoup(coups, i, j, iplus1, jmoins1, mange, NOIR);
                        }
                    }
                }
            }
        }

        Collections.sort(coups, getComparateurMove(couleur));
        return coups;
    }

    private static Comparator<Move> getComparateurMove(int couleur) {
        return new Comparator<Move>() {
            @Override
            public int compare(Move o1, Move o2) {
                int multiplicateur = couleur == COULEUR_ENNEMIE ? -1 : 1;
                return (o2.valeur - o1.valeur) * multiplicateur;
            }
        };
    }

    private static void ajouterCoup(ArrayList coups, int departX, int departY, int arriveeX, int arriveeY, boolean mange, int couleur) {
        StringBuilder tmp = new StringBuilder();

        int value = 0;
        int ligne = arriveeY + 1;

        if (couleur == NOIR) {
            ligne = obtenirLigneCorrespondante(ligne);
        }

        int multiplicateur = couleur == COULEUR_ENNEMIE ? -1 : 1;
        tmp.append(getLigne(departX));
        tmp.append(departY + 1);
        tmp.append(getLigne(arriveeX));
        tmp.append(arriveeY + 1);

        if (ligne == 8) {
            value = 1000000;
        } else if (mange) {
            value = ligne * ligne * 200; // A Evaluer pour obtenir les coups
        } else {
            value = ligne * ligne * 50;
        }

        coups.add(new Move(tmp.toString(), value * multiplicateur));
    }

    private static int obtenirLigneCorrespondante(int ligne) {
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

    private static void effectuerMove(int[][] board, String move, int couleur) {
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

        board[getLigne(depart.charAt(0))][(int) depart.charAt(1) - 49] = 0;
        board[getLigne(arrivee.charAt(0))][(int) arrivee.charAt(1) - 49] = couleur;
    }

    private static int getLigne(char ligne) {
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

    private static char getLigne(int ligne) {
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

    private static void coupInvalide() throws IOException {
        System.out.println("Coup invalide, entrez un nouveau coup : ");
        jouer();
    }

    private static void initBlanc() throws IOException {
        byte[] aBuffer = new byte[1024];

        int size = input.available();
        input.read(aBuffer, 0, size);
        String s = new String(aBuffer).trim();

        String[] boardValues = s.split(" ");
        int x = 0, y = 0;
        for (String boardValue : boardValues) {
            board[x][y] = Integer.parseInt(boardValue);
            x++;
            if (x == 8) {
                x = 0;
                y++;
            }
        }

        System.out.println("Nouvelle partie! Vous jouez blanc, entrez votre premier coup : ");

        COULEUR_JOUEUR = BLANC;
        COULEUR_ENNEMIE = NOIR;

        jouer();
    }

    private static void initNoir() throws IOException {
        System.out.println("Nouvelle partie! Vous jouez noir, attendez le coup des blancs");
        byte[] aBuffer = new byte[1024];
        int size = input.available();
        input.read(aBuffer, 0, size);
        String s = new String(aBuffer).trim();
        String[] boardValues = s.split(" ");

        int x = 0, y = 0;
        for (String boardValue : boardValues) {
            board[x][y] = Integer.parseInt(boardValue);
            x++;
            if (x == 8) {
                x = 0;
                y++;
            }
        }

        COULEUR_JOUEUR = NOIR;
        COULEUR_ENNEMIE = BLANC;
    }

    private static void afficherPlateau() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
            }
            System.out.print("\n");
        }

        System.out.print("\n");
    }

    private static class Move {

        public String move;
        public int valeur;

        public Move(String move, int valeur) {
            this.move = move;
            this.valeur = valeur;
        }

        @Override
        public String toString() {
            return move + " -> " + valeur;
        }
    }

    public static class Chrono {

        private static long startTime, endTime;

        public static void start() {
            startTime = System.nanoTime();

        }

        public static void stop() {
            endTime = System.nanoTime();
        }

        public static float getTime() {
            return ((float) (endTime - startTime)) / 1000000000.0f;
        }
    }
}
