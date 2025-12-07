import java.util.*;

public class ExpertTheme2 {
    public static void calculerTournee(Graphe g, Sommet depart) {
        List<Sommet> aVisiter = new ArrayList<>(g.sommets);
        aVisiter.remove(depart); // On enlève le départ de la liste des cibles

        List<Sommet> tournee = new ArrayList<>();
        tournee.add(depart);
        Sommet courant = depart;
        double distanceTotale = 0;

        System.out.println("  Calcul de la tournée (Heuristique Plus Proche Voisin)...");

        while (!aVisiter.isEmpty()) {
            Sommet plusProche = null;
            double minDistance = Double.MAX_VALUE;

            // Trouver le plus proche parmi les non-visités
            for (Sommet candidat : aVisiter) {
                double dist = getDistanceDijkstra(g, courant, candidat);
                if (dist < minDistance) {
                    minDistance = dist;
                    plusProche = candidat;
                }
            }

            if (plusProche != null) {
                distanceTotale += minDistance;
                tournee.add(plusProche);
                aVisiter.remove(plusProche);
                courant = plusProche;
            }
        }

        // Retour au dépôt
        double distRetour = getDistanceDijkstra(g, courant, depart);
        distanceTotale += distRetour;
        tournee.add(depart);

        // Affichage
        System.out.print("  >> Ordre de passage : ");
        for (int i = 0; i < tournee.size(); i++) {
            System.out.print(tournee.get(i).nom + (i < tournee.size() - 1 ? " -> " : ""));
        }
        System.out.println("\n  >> Distance Totale Estimée : " + distanceTotale + "m");
    }

    // Helper privé : Dijkstra simplifié pour trouver la distance entre deux nœuds quelconques
    private static double getDistanceDijkstra(Graphe g, Sommet dep, Sommet arr) {
        Map<Sommet, Double> dists = new HashMap<>();
        PriorityQueue<Sommet> q = new PriorityQueue<>(Comparator.comparingDouble(dists::get));

        for(Sommet s : g.sommets) dists.put(s, Double.MAX_VALUE);
        dists.put(dep, 0.0);
        q.add(dep);

        while(!q.isEmpty()){
            Sommet u = q.poll();
            if(u == arr) return dists.get(arr);
            if(dists.get(u) == Double.MAX_VALUE) break;

            for(Arete a : g.getVoisins(u)){
                double newD = dists.get(u) + a.poids;
                if(newD < dists.get(a.arrivee)){
                    dists.put(a.arrivee, newD);
                    q.remove(a.arrivee); q.add(a.arrivee);
                }
            }
        }
        return dists.get(arr);
    }
}
