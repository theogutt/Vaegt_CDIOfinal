package DAL.DTO;

public class ReceptKomp {
    private int raavareId;
    private double nonNetto, tolerance;

    public ReceptKomp(){ }

    public ReceptKomp(int raavareId, double nonNetto, double tolerance){
        this.raavareId = raavareId;
        this.nonNetto = nonNetto;
        this.tolerance = tolerance;
    }

    public int getRaavareId() {
        return raavareId;
    }

    public void setRaavareId(int raavareId) {
        this.raavareId = raavareId;
    }

    public double getNonNetto() {
        return nonNetto;
    }

    public void setNonNetto(double nonNetto) {
        this.nonNetto = nonNetto;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }
}
