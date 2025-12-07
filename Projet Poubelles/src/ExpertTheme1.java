import java.util.*;

public class ExpertTheme1 {

    // --- PROBLÉMATIQUE 1 : DIJKSTRA (Multi-destinations) ---
    public static void plusCourtChemin(Graphe g, Sommet depart, List<Sommet> destinations) {
        Map<Sommet, Double> distances = new HashMap<>();
        Map<Sommet, Sommet> predecesseurs = new HashMap<>();
        PriorityQueue<Sommet> file = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        // Initialisation
        for (Sommet s : g.sommets) {
            distances.put(s, Double.MAX_VALUE);
        }
        distances.put(depart, 0.0);
        file.add(depart);

        // Calcul Dijkstra complet (depuis le départ vers TOUS les sommets accessibles)
        // On ne s'arrête pas à la première destination trouvée car on en cherche plusieurs
        while (!file.isEmpty()) {
            Sommet u = file.poll();

            if (distances.get(u) == Double.MAX_VALUE) break; // Sommets restants inaccessibles

            for (Arete a : g.getVoisins(u)) {
                Sommet v = a.arrivee;
                double nouvelleDistance = distances.get(u) + a.poids;

                if (nouvelleDistance < distances.get(v)) {
                    distances.put(v, nouvelleDistance);
                    predecesseurs.put(v, u);
                    // Mise à jour priorité
                    file.remove(v);
                    file.add(v);
                }
            }
        }

        // Reconstruction et Affichage pour CHAQUE destination demandée
        System.out.println("  [DIJKSTRA] Calcul des chemins depuis " + depart.nom + " :");

        for (Sommet arrivee : destinations) {
            if (arrivee == null) continue;

            if (distances.get(arrivee) == Double.MAX_VALUE) {
                System.out.println("    -> Vers " + arrivee.nom + " : Inaccessible.");
                continue;
            }

            // Reconstruire le chemin pour cette destination
            List<Sommet> chemin = new ArrayList<>();
            Sommet etape = arrivee;
            while (etape != null) {
                chemin.add(0, etape);
                etape = predecesseurs.get(etape);
            }

            // Affichage
            System.out.print("    -> Vers " + arrivee.nom + " (" + distances.get(arrivee) + "m) : ");
            for (int i = 0; i < chemin.size(); i++) {
                System.out.print(chemin.get(i).nom + (i < chemin.size() - 1 ? " -> " : ""));
            }
            System.out.println();
        }
    }

    // --- PROBLÉMATIQUE 2 : EULER ---
    public static void analyserFaisabilite(Graphe g) {
        List<String> impairs = new ArrayList<>();
        for (Sommet s : g.sommets) {
            if (g.getDegre(s) % 2 != 0) impairs.add(s.nom);
        }

        if (impairs.isEmpty()) {
            System.out.println("  [OK] Le graphe est EULERIEN (0 sommets impairs).");
            System.out.println("  -> Le camion peut faire une boucle parfaite sans repasser deux fois sur la même rue.");
        } else if (impairs.size() == 2) {
            System.out.println("  [ATTENTION] Le graphe est SEMI-EULERIEN (2 sommets impairs : " + impairs + ").");
            System.out.println("  -> Un chemin existe, mais le camion ne peut pas revenir à son point de départ sans repasser par une rue.");
        } else {
            System.out.println("  [ALERTE] Le graphe n'est PAS eulérien (" + impairs.size() + " sommets impairs : " + impairs + ").");
            System.out.println("  -> Il faut appliquer le POSTIER CHINOIS (CPP) pour optimiser les doublons d'arêtes.");
        }
    }
}
