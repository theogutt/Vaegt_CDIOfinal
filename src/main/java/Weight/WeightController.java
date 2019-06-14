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
        double netto;
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
                input = v.commandRM20(user.getNavn(), "Er dette dit navn? TRYK OK");
                String y = inputToString(input);
                if (y.equals("")) {}
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
                recept = receptDAO.get(produktBatch.getReceptId());
                ReceptKomp[] receptKomps = recept.getIndholdsListe();
                for(int i = 0; i<recept.getIndholdsListe().length;i++) {
                    input = v.commandRM20("PLACER BEHOLDER", "TRYK OK");
                    ok = inputToString(input);
                    if(ok.equals("")) {
                        tara=v.commandS();
                    }
                    input = v.commandRM20("Tarer", "TRYK OK");
                    ok = inputToString(input);
                    if(ok.equals("")) {
                        v.commandT();
                    }
                    System.out.println("test, inde i loop");
                    String navn = raavareDAO.get(receptKomps[i].getRaavareId()).getNavn();
                    v.commandRM20(navn,"Skal afvejes");
                    input = v.commandRM20("Indtast råvareBatchNummer","");
                    int råvareBatchId = inputToInt(input);
                    v.commandRM20("Tryk ok for at afveje","");
                    netto=v.commandS();
                    v.commandT();
                    //tolerance
                    double øvreGrænse = (100.0+receptKomps[i].getTolerance())*receptKomps[i].getNonNetto()/100;
                    double nedreGrænse = (100.0-receptKomps[i].getTolerance())*receptKomps[i].getNonNetto()/100;
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
            nextStep=true;
        }while(nextStep==false);
        do{
            produktBatch.setBatchStatus(3);
            produktBatchDAO.update(produktBatch);
            v.commandRM20("Afvejning udført","");
            nextStep=true;
        }while(nextStep==false);
    }
   
    private int inputToInt(String input){
        int num;
        input=input.replace("\"","");
        num = Integer.valueOf(input.replace("RM20 A ", ""));
        return num;
    }
    private String inputToString(String input){
        String num;
        input=input.replace("\"","");
        num = input.replace("RM20 A ", "");
        return num;
    }
}
