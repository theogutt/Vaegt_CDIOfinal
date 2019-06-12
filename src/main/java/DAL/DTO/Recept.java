package DAL.DTO;

public class Recept {
    private int id;
    private String navn;
    //private ArrayList<ReceptKomp> indholdsListe;
    private ReceptKomp[] indholdsListe;

    public Recept() {
    }
/*
    public Recept(int id, String navn, int[] raavareID, double[] nonNetto, double[] tolerance){
        for (int i = 0; i < raavareID.length; i++){
            indholdsListe.add(new ReceptKomp(raavareID[i], nonNetto[i], tolerance[i]));
        }
        this.id = id;
        this.navn = navn;
    }
*/

    public Recept(int id, String navn, ReceptKomp[] indholdsListe) {
        this.id = id;
        this.navn = navn;
        this.indholdsListe = indholdsListe;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
/*
    public ArrayList<ReceptKomp> getIndholdsListe() {
        return indholdsListe;
    }

    public void setIndholdsListe(ArrayList<ReceptKomp> indholdsListe) {
        this.indholdsListe = indholdsListe;
    }
    */

    public void setIndholdsListe(ReceptKomp[] indholdsListe) {
        this.indholdsListe = indholdsListe;
    }

    public ReceptKomp[] getIndholdsListe() {
        return indholdsListe;
    }

    public String getNavn() {
        return navn;
    }
}
