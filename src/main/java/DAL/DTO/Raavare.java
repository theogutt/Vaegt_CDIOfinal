package DAL.DTO;

public class Raavare {
    private int id = 0;
    private String navn = "";

    public Raavare(){ }

    public Raavare(int raavareID, String raavareNavn){
        this.id = raavareID;
        this.navn = raavareNavn;
    }

    // getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }
}
