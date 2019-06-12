package DAL.DTO;

public class User {
    private int id;
    private String navn, ini, cpr;
    private boolean aktiv;

    public User(){ }

    public User(int id, String navn, String ini, String cpr, boolean aktiv){
        this.id = id;
        this.navn = navn;
        this.ini = ini;
        this.cpr = cpr;
        this.aktiv = aktiv;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

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

    public String getIni() {
        return ini;
    }

    public void setIni(String ini) {
        this.ini = ini;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public boolean isAktiv(){
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }
}
