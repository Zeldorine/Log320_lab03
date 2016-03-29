package log320_lab03;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import static log320_lab03.AlphaBeta.timer;

/**
 *
 * @author Zeldorine
 */
public class Client {

    private static Socket MyClient;
    private static BufferedInputStream input;
    private static BufferedOutputStream output;
    private static int[][] board = new int[8][8];
    public static final int CASE_VIDE = 0;
    public static final int BLANC = 2;
    public static final int NOIR = 4;
    public static int COULEUR_JOUEUR;
    public static int COULEUR_ENNEMIE;
    public static Move bestMoveG;

    /**
     * Blanc joue en premier 5 seconde pour jouer Garder un tableua de pion
     * blanc et un tableau de pion noir pour optimisation Verifier les boucles
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            MyClient = new Socket("localhost", 8888);
            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());

            while (1 == 1) {
                char cmd = (char) input.read();
                switch (cmd) {
                    // Debut de la partie en joueur blanc
                    case '1':
                        Chrono.start();
                        timer.Start();
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
                        timer.Start();
                        byte[] aBuffer = new byte[16];
                        input.read(aBuffer, 0, input.available());
                        String s = new String(aBuffer);
                        Service.effectuerMove(board, s, COULEUR_ENNEMIE);

                        jouer();

                        Chrono.stop();
                        break;
                    // Le dernier coup est invalide
                    case '4':
                        Chrono.start();
                        System.out.println("Coup invalide");
                        coupInvalide();
                        Chrono.stop();
                        break;
                    default:
                        System.out.println("cmd = " + cmd);
                        break;
                }

                System.out.format("Temps d'execution : %.13f secondes \n", Chrono.getTime());
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void jouer() throws IOException {
        ArrayList<Move> coupsPossible = FonctionEvaluation_NEW.coupPossible(board, COULEUR_JOUEUR);
        bestMoveG = AlphaBeta.AlphaBetaIteratif(board, COULEUR_JOUEUR, true, coupsPossible);

        if (bestMoveG == null) {
            bestMoveG = coupsPossible.get(0);
        }

        StringBuilder tmp = new StringBuilder();
        tmp.append(Service.getLigne(bestMoveG.departX));
        tmp.append(bestMoveG.departY + 1);
        tmp.append(Service.getLigne(bestMoveG.arriveeX));
        tmp.append(bestMoveG.arriveeY + 1);
        String move = tmp.toString();

        output.write(move.getBytes(), 0, move.length());
        output.flush();

        System.out.println("Coup jouer : " + move + " -> " + bestMoveG.valeur);
        Service.effectuerMove(board, move, COULEUR_JOUEUR);

        bestMoveG = null;
        TableTransposition.instance.Table = new HashMap<>();
        timer.Reset();
    }

    private static void afficherCoupValide(ArrayList<Move> coupsPossible) {
        for (Move move : coupsPossible) {
            System.out.println(move.toString());
        }
    }

    private static void coupInvalide() throws IOException {
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

    private static void afficherPlateau(int[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j]);
            }
            System.out.print("\n");
        }

        System.out.print("\n");

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
