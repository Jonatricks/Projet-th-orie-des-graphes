public class Sommet {
    String nom;
    double dechets; // Ajout pour Thème 3

    public Sommet(String nom, double dechets) {
        this.nom = nom;
        this.dechets = dechets;
    }
    @Override public String toString() { return nom; }
}