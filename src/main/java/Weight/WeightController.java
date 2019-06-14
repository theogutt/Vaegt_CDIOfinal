package Weight;

import DAL.DAO.*;
import DAL.DTO.*;
import java.io.IOException;
import java.sql.SQLException;

public class WeightController {

    public static void main(String[]args) throws Exception {
        WeightController weightController = new WeightController();
        weightController.afvejning();
    }
    private RaavareBatchDAO raavareBatchDAO = new RaavareBatchDAO();
    private RaavareDAO raavareDAO = new RaavareDAO();
    private ReceptDAO receptDAO = new ReceptDAO();
    private ProduktBatchKompDAO produktBatchKompDAO = new ProduktBatchKompDAO();
    private UserDAO userDAO = new UserDAO();
    private ProduktBatchDAO produktBatchDAO = new ProduktBatchDAO();
    private WeightConnector v;

    public WeightController() throws Exception {
        v = new WeightConnector();
    }
    public void afvejning() throws IOException, SQLException, IDAO.DALException {
        RaavareBatch raavareBatch;
        ProduktBatchKomp produktBatchKomp;
        ProduktBatch produktBatch;
        User user;
        double netto = 0;
        double tara = 0;
        double brutto;
        String input;
        boolean nextStep = false;

        do {
            input = v.commandRM20("INDTAST ID", "");
            int Id = inputToInt(input);
            System.out.println(Id);
            user = userDAO.get(Id);
            if (user.getNavn() != null) {
                nextStep = true;
                input = v.commandRM20(user.getNavn(), "Er dette dit navn? y/n");
                String y = inputToString(input);
                if (y.equals("y")) {}
                else{v.commandRM20("Prøv igen","");
                    nextStep=false;}
                }
            else{v.commandRM20("Laboratørnummeret er forkert",""); }
        }
        while(nextStep==false);

        nextStep=false;
        do {
                input = v.commandRM20("INDTAST PRODUKTBATCHID", "");
                int Id = inputToInt(input);
                produktBatch = produktBatchDAO.get(Id);
                if (produktBatch.getSlutDato() != null) {
                    nextStep = true;
                }
            else{
                v.commandRM20("ProduktBatchet eksisterer ikke","");
            }
        }while(nextStep==false);

        do {
            int Id = inputToInt(input);
            produktBatch = produktBatchDAO.get(Id);
            Recept recept = receptDAO.get(produktBatch.getReceptId());
            input = v.commandRM20(recept.getNavn()," Skal produceres");
            String ok = inputToString(input);
            if(ok.equals("")) {
                produktBatch.setBatchStatus(2);//går ud fra at 2  betyder "under produktion"
                produktBatchDAO.update(produktBatch);
            }
            input = v.commandRM20("PLACER BEHOLDER", "TRYK OK");
            ok = inputToString(input);
            if(ok.equals("")) {
                tara=v.commandS();
            }
            input = v.commandRM20("Tarer", "TRYK OK");
            ok = inputToString(input);
            if(ok.equals("")){
                v.commandT();
                recept = receptDAO.get(produktBatch.getReceptId());
                ReceptKomp[] receptKomps = recept.getIndholdsListe();
                for(int i = 0; i<recept.getIndholdsListe().length;i++) {
                    System.out.println("test, inde i loop");
                    String navn = raavareDAO.get(receptKomps[i].getRaavareId()).getNavn();
                    v.commandRM20(navn,"Skal afvejes");
                    input = v.commandRM20("Indtast råvareBatchNummer","");
                    int råvareBatchId = inputToInt(input);
                    input = v.commandRM20("PLACER NETTO","");
                    ok = inputToString(input);
                    if (ok.equals(""))
                        netto = v.commandS();
                    //tolerance
                    double øvreGrænse = (100.0+receptKomps[i].getTolerance())*receptKomps[i].getNonNetto() / 100;
                    double nedreGrænse = (100.0-receptKomps[i].getTolerance())*receptKomps[i].getNonNetto() / 100;
                    if(netto>=nedreGrænse && netto<=øvreGrænse){
                        raavareBatch = raavareBatchDAO.get(råvareBatchId);
                        raavareBatch.setMaengde(raavareBatch.getMaengde()-netto);
                        raavareBatchDAO.update(raavareBatch);
                        produktBatchKomp = new ProduktBatchKomp(produktBatch.getId(),råvareBatchId,user.getId(),tara,netto);
                        //bruttokontrol
                        v.commandRM20("Fjern råvare og beholder","Tryk ok");
                        brutto = v.commandS();
                        v.commandT();
                        if(netto+tara+brutto==0){
                            produktBatchKompDAO.create(produktBatchKomp);
                        }
                        else{
                            v.commandRM20("Bruttokontrol mislykkedes","Tryk ok");
                        }
                    }
                    else{
                        v.commandRM20("Tolerancen er ikke overholdt","");
                    }
                }
            }
            nextStep=true;
        }while(nextStep==false);
        do{
            produktBatch.setBatchStatus(3);
            produktBatchDAO.update(produktBatch);
            nextStep=true;
        }while(nextStep==false);
    }
    /*
    public void testProcedure() throws IOException {
        String userInput;
        // Trin 1: Vægten beder om, at der indtastes operatørnummer
        // Trin 2: Operatøren indtaster sit brugernummer (område 11-99)
        do {
            do {
                userInput = v.commandRM20("INDTAST OPERATØRNUMMER", "11-99");
            } while (!inputEquals(userInput, 12));

            // Trin 3 & 4: Operatørens navn findes i databasen og vises på vægten
            userInput = v.commandRM20("//indsæt navn", "Er dette dit navn? y/n");
        } while(!inputEquals(userInput, "Y"));

        // Trin 5 & 6: Vægten beder om, at der indtastes batch nummer (område 1000-9999)
        do {
c        } while (!inputEquals(userInput, 1234));

        // Trin 7 & 8: Operatøren instrueres om, at vægten skal være ubelastet
        // Trin 9: Vægten tareres
        v.commandRM20("FJERN ALT FRA VÆGTEN", "Tryk OK");
        v.commandT();

        // Trin 10 & 11: Operatøren instrueres om, at placere tara (tom beholder)  på vægten
        v.commandRM20("PLACER TARA (TOM BEHOLDER)", "Tryk OK");

        // Trin 12: Tara’s vægt registreres
        v.commandS();

        // Trin 13: Vægten tareres
        v.commandT();

        // Trin 14 & 15: Operatøren instrueres i at placere netto (beholder med produkt)  på vægten
        v.commandRM20("PLACER NETTO PÅ VÆGT", "Tryk OK");

        // Trin 16: Nettovægt registreres
        v.commandS();

        // Trin 17: Vægten tareres
        v.commandT();

        // Trin 18 & 19: Operatøren instrueres i at fjerne brutto fra vægten
        v.commandRM20("Fjern brutto fra vægten", "Tryk OK");

        // Trin 20: Bruttovægt registreres (negativ)
        v.commandS();

        // Trin 22: Operatøren kvitterer
        v.commandRM20("Godkend", "OK");

        // Trin 23: Vægten tareres
        v.commandT();
    }
     */
    private int inputToInt(String input){
        int num;
        input=input.replace("\"","");
        input=input.replace("\"","");
        input=input.replace("\"","");
        num = Integer.valueOf(input.replace("RM20 A ", ""));
        return num;
    }
    private String inputToString(String input){
        String num;
        input=input.replace("\"","");
        input=input.replace("\"","");
        input=input.replace("\"","");
        num = input.replace("RM20 A ", "");
        return num;
    }
    private boolean inputEquals(String input, String match){
        return input.equalsIgnoreCase("RM20 A \"" + match +"\"");
    }

    private boolean inputEquals(String input, int match){
        return input.equalsIgnoreCase("RM20 A \"" + match +"\"");
    }
}
