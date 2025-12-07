import java.util.*;

// Classe représentant un sommet (point de collecte ou dépôt)
class Sommet {
    String name;
    int capacity; // contenance du point (quantité de déchets)

    Sommet(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}

// Classe représentant une arête entre deux sommets avec un poids (distance)
class Arete {
    Sommet u, v;
    double weight;

    Arete(Sommet u, Sommet v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }
}

// Classe représentant le graphe (sommets, arêtes et distances)
class Graphe {
    private Map<String, Integer> indexMap;
    public List<Sommet> sommets;
    private double[][] dist;

    public Graphe(int n) {
        indexMap = new HashMap<>();
        sommets = new ArrayList<>();
        dist = new double[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(i == j) dist[i][j] = 0;
                else dist[i][j] = Double.POSITIVE_INFINITY;
            }
        }
    }

    // Ajouter un sommet au graphe
    public void addSommet(String name, int capacity) {
        if(indexMap.containsKey(name)) return;
        int index = sommets.size();
        indexMap.put(name, index);
        sommets.add(new Sommet(name, capacity));
    }

    // Ajouter une arête (non orientée) entre deux sommets
    public void addEdge(String name1, String name2, double weight) {
        int i = indexMap.get(name1);
        int j = indexMap.get(name2);
        dist[i][j] = weight;
        dist[j][i] = weight; // graphe non orienté
    }

    // Récupérer un sommet par son index
    public Sommet getSommet(int index) {
        return sommets.get(index);
    }

    // Récupérer un sommet par son nom
    public Sommet getSommet(String name) {
        int idx = indexMap.get(name);
        return sommets.get(idx);
    }

    // Récupérer l'index d'un sommet
    public int getIndex(Sommet s) {
        return indexMap.get(s.name);
    }

    // Récupérer la distance entre deux sommets
    public double getDistance(Sommet s1, Sommet s2) {
        int i = indexMap.get(s1.name);
        int j = indexMap.get(s2.name);
        return dist[i][j];
    }

    // Récupérer la distance entre deux indices
    public double getDistance(int i, int j) {
        return dist[i][j];
    }

    // Nombre de sommets dans le graphe
    public int size() {
        return sommets.size();
    }
}

// Classe représentant une tournée (chemin avec distance et charge)
class Tournee {
    List<Sommet> sommets; // liste ordonnée de points (incluant dépôt en début et fin)
    double distance;      // distance totale de la tournée
    int load;             // charge totale (somme des contenances des points de collecte)

    // Constructeur qui calcule distance et charge à partir du graphe et de la liste de sommets
    Tournee(Graphe g, List<Sommet> sommets) {
        this.sommets = new ArrayList<>(sommets);
        this.distance = 0;
        this.load = 0;
        // Calcul de la distance totale
        for(int i = 0; i < sommets.size() - 1; i++) {
            Sommet s1 = sommets.get(i);
            Sommet s2 = sommets.get(i + 1);
            this.distance += g.getDistance(s1, s2);
        }
        // Calcul de la charge en sommant les capacités (dépôt exclus)
        for(Sommet s : sommets) {
            if (!s.name.equals("D")) {
                this.load += s.capacity;
            }
        }
    }
}

// Classe principale
public class Main {
    public static void main(String[] args)  {
        // --- Création du graphe de collecte ---
        // 7 sommets : D (dépôt) et P1..P6 (points de collecte)
        Graphe graphe = new Graphe(7);
        graphe.addSommet("D", 0);   // dépôt
        graphe.addSommet("P1", 4);
        graphe.addSommet("P2", 3);
        graphe.addSommet("P3", 3);
        graphe.addSommet("P4", 3);
        graphe.addSommet("P5", 5);
        graphe.addSommet("P6", 6);

        // Arêtes du graphe avec distances (symétriques)
        graphe.addEdge("D", "P1", 8);
        graphe.addEdge("D", "P2", 3);
        graphe.addEdge("D", "P3", 7);
        graphe.addEdge("D", "P4", 9);
        graphe.addEdge("D", "P5", 10);
        graphe.addEdge("D", "P6", 7);
        graphe.addEdge("P1", "P2", 2);
        graphe.addEdge("P1", "P3", 4);
        graphe.addEdge("P1", "P5", 5);
        graphe.addEdge("P2", "P4", 2);
        graphe.addEdge("P2", "P5", 6);
        graphe.addEdge("P3", "P4", 3);
        graphe.addEdge("P3", "P6", 4);
        graphe.addEdge("P4", "P5", 1);
        graphe.addEdge("P5", "P6", 2);

        // --- Approche 1: Algorithme du plus proche voisin (TSP glouton) ---
        List<Sommet> routeNN = nearestNeighborTour(graphe, "D");
        double distNN = computeTourDistance(graphe, routeNN);
        System.out.print("Approche 1 (Plus proche voisin) : ");
        for (Sommet s : routeNN) {
            System.out.print(s.name + " ");
        }
        System.out.println("\nDistance totale : " + distNN);

        // --- Approche 2: MST (Prim) + parcours DFS + optimisation 2-opt ---
        List<Arete> mst = primMST(graphe, graphe.getIndex(graphe.getSommet("D")));
        // Construction de la structure d'adjacence pour l'arbre MST
        List<List<Integer>> treeAdj = new ArrayList<>();
        for (int i = 0; i < graphe.size(); i++) {
            treeAdj.add(new ArrayList<>());
        }
        for (Arete e : mst) {
            int u = graphe.getIndex(e.u);
            int v = graphe.getIndex(e.v);
            treeAdj.get(u).add(v);
            treeAdj.get(v).add(u);
        }
        // Parcours DFS pour obtenir une tournée approximative
        boolean[] visited = new boolean[graphe.size()];
        List<Integer> dfsOrder = new ArrayList<>();
        dfs(treeAdj, graphe.getIndex(graphe.getSommet("D")), visited, dfsOrder);
        // Ajouter le dépôt à la fin pour former le cycle complet
        dfsOrder.add(graphe.getIndex(graphe.getSommet("D")));
        // Conversion des indices en objets Sommet
        List<Sommet> routeMST = new ArrayList<>();
        for (int idx : dfsOrder) {
            routeMST.add(graphe.getSommet(idx));
        }
        // Optimisation 2-opt pour la tournée résultante
        routeMST = twoOptOptimization(graphe, routeMST);
        double distMST = computeTourDistance(graphe, routeMST);
        System.out.print("Approche 2 (MST + DFS + 2-opt) : ");
        for (Sommet s : routeMST) {
            System.out.print(s.name + " ");
        }
        System.out.println("\nDistance totale : " + distMST);

        // --- Route-first, Cluster-second: découpage selon la capacité C=10 ---
        int capacityMax = 10;
        List<Tournee> subTours = splitToursByCapacity(graphe, routeMST, capacityMax);
        // Affichage des sous-tournées obtenues avec leur charge et distance
        double totalSubDist = 0;
        int num = 1;
        for (Tournee tour : subTours) {
            System.out.print("Sous-tourn\u00e9e " + num + " : ");
            for (Sommet s : tour.sommets) {
                System.out.print(s.name + " ");
            }
            System.out.println("\nCharge = " + tour.load + ", Distance = " + tour.distance);
            totalSubDist += tour.distance;
            num++;
        }
        System.out.println("Distance totale des sous-tourn\u00e9es : " + totalSubDist);
    }

    // Algorithme du plus proche voisin pour TSP (glouton)
    static List<Sommet> nearestNeighborTour(Graphe g, String startName) {
        List<Sommet> route = new ArrayList<>();
        Set<Sommet> visited = new HashSet<>();
        Sommet current = g.getSommet(startName);
        route.add(current);
        visited.add(current);
        while (visited.size() < g.size()) {
            Sommet nearest = null;
            double bestDist = Double.POSITIVE_INFINITY;
            for (int i = 0; i < g.size(); i++) {
                Sommet v = g.getSommet(i);
                if (!visited.contains(v)) {
                    double d = g.getDistance(current, v);
                    if (d < bestDist) {
                        bestDist = d;
                        nearest = v;
                    }
                }
            }
            if (nearest == null) break;
            route.add(nearest);
            visited.add(nearest);
            current = nearest;
        }
        // Retour au dépôt pour compléter le cycle
        route.add(g.getSommet(startName));
        return route;
    }

    // Calcul de la distance d'une tournée
    static double computeTourDistance(Graphe g, List<Sommet> tour) {
        double distance = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            distance += g.getDistance(tour.get(i), tour.get(i+1));
        }
        return distance;
    }

    // Construction de l'arbre couvrant minimal avec l'algorithme de Prim
    static List<Arete> primMST(Graphe g, int start) {
        int n = g.size();
        boolean[] used = new boolean[n];
        double[] minEdge = new double[n];
        int[] selEdge = new int[n];
        Arrays.fill(minEdge, Double.POSITIVE_INFINITY);
        Arrays.fill(selEdge, -1);
        minEdge[start] = 0;
        List<Arete> mstEdges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!used[v] && (u == -1 || minEdge[v] < minEdge[u])) {
                    u = v;
                }
            }
            used[u] = true;
            // Ajouter l'arête menant au sommet u (sauf pour le premier sommet)
            if (selEdge[u] != -1) {
                Arete e = new Arete(g.getSommet(selEdge[u]), g.getSommet(u), g.getDistance(selEdge[u], u));
                mstEdges.add(e);
            }
            // Mise à jour des distances minEdge pour les voisins de u
            for (int v = 0; v < n; v++) {
                double w = g.getDistance(u, v);
                if (!used[v] && w < minEdge[v]) {
                    minEdge[v] = w;
                    selEdge[v] = u;
                }
            }
        }
        return mstEdges;
    }

    // Parcours DFS pour obtenir un ordre de visite des sommets
    static void dfs(List<List<Integer>> adj, int u, boolean[] visited, List<Integer> order) {
        visited[u] = true;
        order.add(u);
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                dfs(adj, v, visited, order);
            }
        }
    }

    // Optimisation 2-opt pour améliorer la tournée
    static List<Sommet> twoOptOptimization(Graphe g, List<Sommet> tour) {
        int n = tour.size();
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    Sommet A = tour.get(i - 1);
                    Sommet B = tour.get(i);
                    Sommet C = tour.get(j);
                    Sommet D = tour.get(j + 1);
                    double before = g.getDistance(A, B) + g.getDistance(C, D);
                    double after = g.getDistance(A, C) + g.getDistance(B, D);
                    if (after < before) {
                        // Appliquer l'échange 2-opt (inversion du segment)
                        Collections.reverse(tour.subList(i, j + 1));
                        improved = true;
                        break;
                    }
                }
                if (improved) break;
            }
        }
        return tour;
    }

    // Route-first, cluster-second : découpage de la tournée selon la capacité maximale
    static List<Tournee> splitToursByCapacity(Graphe g, List<Sommet> fullTour, int capacityMax) {
        List<Tournee> subTours = new ArrayList<>();
        List<Sommet> currentTour = new ArrayList<>();
        int currentLoad = 0;
        // Parcourir les points de collecte (sauter le dépôt initial et final)
        for (int i = 1; i < fullTour.size() - 1; i++) {
            Sommet v = fullTour.get(i);
            if (currentLoad + v.capacity > capacityMax) {
                // Fermer la tournée actuelle (ajouter retour au dépôt)
                currentTour.add(fullTour.get(0));
                subTours.add(new Tournee(g, currentTour));
                // Démarrer une nouvelle tournée
                currentTour.clear();
                currentLoad = 0;
            }
            if (currentTour.isEmpty()) {
                // Ajouter le dépôt en début de tournée
                currentTour.add(fullTour.get(0));
            }
            currentTour.add(v);
            currentLoad += v.capacity;
        }
        // Clôturer la dernière tournée
        if (!currentTour.isEmpty()) {
            currentTour.add(fullTour.get(0));
            subTours.add(new Tournee(g, currentTour));
        }
        return subTours;
    }
}
