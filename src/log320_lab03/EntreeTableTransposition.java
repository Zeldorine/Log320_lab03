package log320_lab03;

/**
 *
 * @author Zeldorine
 */
public class EntreeTableTransposition {
    public enum EntreeTableTranspositionType {
        valeurExacte,
        limiteInferieure,
        limiteSuperieure
    }

    public EntreeTableTransposition(int valeurBoard, EntreeTableTranspositionType entreeType, int profondeurRecherche) {
        valeur = valeurBoard;
        Type = entreeType;
        profondeur = profondeurRecherche;
    }

    public int valeur;
    public EntreeTableTranspositionType Type;
    public int profondeur;
}
