package DAL.DTO;

public class RaavareBatch {
    private int id, raavareId;
    private double maengde;
    private String leverandoer;

    public RaavareBatch(){ }

    public RaavareBatch(int id, int raavareId, double maengde, String leverandoer){
        this.id = id;
        this.raavareId = raavareId;
        this.maengde = maengde;
        this.leverandoer = leverandoer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRaavareId() {
        return raavareId;
    }

    public void setRaavareId(int raavareId) {
        this.raavareId = raavareId;
    }

    public double getMaengde() {
        return maengde;
    }

    public void setMaengde(double maengde) {
        this.maengde = maengde;
    }

    public String getLeverandoer() {
        return leverandoer;
    }

    public void setLeverandoer(String leverandoer) {
        this.leverandoer = leverandoer;
    }
}
