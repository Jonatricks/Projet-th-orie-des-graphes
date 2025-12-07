import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graphe {
    List<Sommet> sommets = new ArrayList<>();
    Map<String, Sommet> mapSommets = new HashMap<>(); // Pour accès rapide par nom
    Map<Sommet, List<Arete>> adjacence = new HashMap<>();

    public void ajouterSommet(Sommet s) {
        sommets.add(s);
        mapSommets.put(s.nom, s);
        adjacence.putIfAbsent(s, new ArrayList<>());
    }

    public Sommet getSommet(String nom) { return mapSommets.get(nom); }

    public void ajouterArete(Sommet a, Sommet b, double poids) {
        adjacence.get(a).add(new Arete(a, b, poids));
        adjacence.get(b).add(new Arete(b, a, poids)); // HO1 : Non orienté
    }

    public int getDegre(Sommet s) { return adjacence.get(s).size(); }
    public List<Arete> getVoisins(Sommet s) { return adjacence.get(s); }

    public boolean sontVoisins(Sommet s1, Sommet s2) {
        for (Arete a : adjacence.get(s1)) {
            if (a.arrivee == s2) return true;
        }
        return false;
    }

    public void afficherGraphe() {
        System.out.println("Graphe (" + sommets.size() + " sommets) :");
        for (Sommet s : sommets) {
            System.out.print("  " + s.nom + " (Charge: " + s.dechets + "t) -> ");
            for (Arete a : adjacence.get(s)) System.out.print(a.arrivee.nom + "(" + (int)a.poids + ") ");
            System.out.println();
        }
    }
}