import java.util.*;

public class ProjetGraphes {

    public static void main(String[] args) {
        // 1. CRÉATION DE LA VILLE (Le graphe unique A-J)
        Graphe ville = initialiserVilleTest();

        System.out.println("=== 1. ANALYSE DE LA STRUCTURE DE LA VILLE ===");
        ville.afficherGraphe();

        // 2. APPEL DE L'EXPERT THEME 1 (Maxime & Toi)

        // PROB 1 : Plus Court Chemin (Dijkstra)
        System.out.println("\n=== 2. OPTIMISATION RAMASSAGE (Thème 1 - Prob 1 : Dijkstra) ===");
        Sommet depDijkstra = ville.getSommet("A");
        List<Sommet> destinations = Arrays.asList(
                ville.getSommet("F"),
                ville.getSommet("E"),
                ville.getSommet("J")
        );
        if (depDijkstra != null) {
            ExpertTheme1.plusCourtChemin(ville, depDijkstra, destinations);
        } else {
            System.out.println("Erreur : Sommet de départ A introuvable.");
        }
        // Analyse Eulérienne (Prob 2)
        System.out.println("\n=== 2. ANALYSE DES TOURNEES (Thème 1 - Prob 2) ===");
        ExpertTheme1.analyserFaisabilite(ville);

        // 3. APPEL DE L'EXPERT THEME 2 (Nouveau !)
        // Calcul de la tournée du camion (Voyageur de Commerce)
        System.out.println("\n=== 3. OPTIMISATION POINTS DE COLLECTE (Thème 2 - TSP) ===");
        // On suppose un départ du point B (souvent le dépôt ou point central)
        Sommet depart = ville.getSommet("B");
        if (depart != null) {
            ExpertTheme2.calculerTournee(ville, depart);
        } else {
            System.out.println("Erreur : Le point de départ 'B' n'existe pas.");
        }

        // 4. APPEL DE L'EXPERT THEME 3 (Toi)
        // Planification des jours de passage
        System.out.println("\n=== 4. PLANIFICATION DES JOURS (Thème 3) ===");
        ExpertTheme3.genererCalendrier(ville);
    }

    // Méthode utilitaire avec ton graphe et les poids de déchets pour le Thème 3
    private static Graphe initialiserVilleTest() {
        Graphe gr = new Graphe();

        // Création des sommets A à J avec les données de déchets (Tonnes)
        // (Valeurs tirées du rapport pour faire fonctionner la logique HO2)
        Sommet a = new Sommet("A", 1.2);
        Sommet b = new Sommet("B", 1.8);
        Sommet c = new Sommet("C", 0.7);
        Sommet d = new Sommet("D", 2.3);
        Sommet e = new Sommet("E", 1.9);
        Sommet f = new Sommet("F", 2.6);
        Sommet g = new Sommet("G", 0.9);
        Sommet h = new Sommet("H", 1.4);
        Sommet i = new Sommet("I", 0.8);
        Sommet j = new Sommet("J", 1.1);

        gr.ajouterSommet(a); gr.ajouterSommet(b); gr.ajouterSommet(c);
        gr.ajouterSommet(d); gr.ajouterSommet(e); gr.ajouterSommet(f);
        gr.ajouterSommet(g); gr.ajouterSommet(h); gr.ajouterSommet(i);
        gr.ajouterSommet(j);

        // Ajout des arêtes selon ton schéma (HO1 : Non orienté)
        gr.ajouterArete(a, h, 90);

        // Connexions B
        gr.ajouterArete(b, c, 30);
        gr.ajouterArete(b, f, 30);
        gr.ajouterArete(b, i, 50);

        // C (vers D)
        gr.ajouterArete(c, d, 50);

        // D (vers E, H, I)
        gr.ajouterArete(d, e, 50);
        gr.ajouterArete(d, h, 100);
        gr.ajouterArete(d, i, 60);

        // E (vers F)
        gr.ajouterArete(e, f, 80);

        // F (vers G, J)
        gr.ajouterArete(f, g, 70);
        gr.ajouterArete(f, j, 60);

        // H (vers J)
        gr.ajouterArete(h, j, 50);

        // G (vers H)
        gr.ajouterArete(g, h, 40);

        return gr;
    }
}