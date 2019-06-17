package Weight;

import DAL.DAO.*;
import DAL.DTO.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeightController {

    public static void main(String[] args) throws Exception {
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

    private WeightController() throws Exception {
        v = new WeightConnector();
    }

    private void afvejning() throws IOException, SQLException, IDAO.DALException {
        RaavareBatch raavareBatch;
        ProduktBatchKomp produktBatchKomp = new ProduktBatchKomp();
        ProduktBatch produktBatch;
        User user;
        double netto = 0, tara = 0, brutto;
        double oevreGraense, nedreGraense;
        String input, ok, raavareNavn;
        boolean nextStep = false, nextRaastof;
        int raavareBatchId, produktBatchId, brugerId, raavareId;

        do {
            // Tjekker bruger
            input = v.commandRM20("INDTAST DIT ID", "");
            brugerId = inputToInt(input);
            user = userDAO.get(brugerId);
            if (userExsists(user)) {
                nextStep = true;
                input = v.commandRM20(user.getNavn(), "Er dette dit navn? TRYK OK | Hvis nej TRYK 0+OK");
                ok = inputToString(input);
                if (!ok.equals("")) {
                    v.commandRM20("Prøv igen", "");
                    nextStep = false;
                }
            } else {
                v.commandRM20("Laboratørnummeret er forkert", "");
            }
        }
        while (!nextStep);

        // Tjekker produktBatch
        nextStep = false;
        do {
            input = v.commandRM20("INDTAST PRODUKTBATCHID", "");
            produktBatchId = inputToInt(input);
            produktBatch = produktBatchDAO.get(produktBatchId);
            if (produktBatchExsists(produktBatch)) {
                nextStep = true;
            } else {
                v.commandRM20("ProduktBatchet eksisterer ikke", "");
            }
        } while (!nextStep);

        // Fortæller bruger råstof og sætter produktBatch
        produktBatchId = inputToInt(input);
        produktBatch = produktBatchDAO.get(produktBatchId);
        Recept recept = receptDAO.get(produktBatch.getReceptId());
        input = v.commandRM20(recept.getNavn(), " Skal produceres");
        ok = inputToString(input);
        if (ok.equals("")) {
            produktBatch.setBatchStatus(1);
            produktBatchDAO.update(produktBatch);
        }
        recept = receptDAO.get(produktBatch.getReceptId());
        ReceptKomp[] receptKomps = recept.getIndholdsListe();

        // Styrer afvejning
        for (int i = 0; i < receptKomps.length; i++) {
            nextRaastof = true;
            // Laver første taraering
            input = v.commandRM20("PLACER BEHOLDER", "TRYK OK");
            ok = inputToString(input);
            if (ok.equals("")) {
                tara = v.commandS();
            }
            v.commandT();

            // Setter råvarebatch, og starter afvejning
            raavareNavn = raavareDAO.get(receptKomps[i].getRaavareId()).getNavn();
            raavareId = raavareDAO.get(receptKomps[i].getRaavareId()).getId();
            v.commandRM20("TRYK OK, når du har hentet", raavareNavn);
            while (true) {
                input = v.commandRM20("Indtast råvareBatchNummer for", raavareNavn);
                raavareBatchId = inputToInt(input);
                raavareBatch = raavareBatchDAO.get(raavareBatchId);
                if (raavareBatch.getRaavareId() != raavareId)
                    v.commandRM20("Dette råvareBatch er ikke " + raavareNavn,"");
                else
                    break;
            }
            input = v.commandRM20("PLACER NETTO i form af", raavareNavn);
            ok = inputToString(input);
            if (ok.equals("")) {
                netto = v.commandS();
                v.commandT();
            }

            // Udregner tolerance
            oevreGraense = (100.0 + receptKomps[i].getTolerance()) * receptKomps[i].getNonNetto() / 100;
            nedreGraense = (100.0 - receptKomps[i].getTolerance()) * receptKomps[i].getNonNetto() / 100;
            if (netto >= nedreGraense && netto <= oevreGraense) {
                raavareBatch.setMaengde(raavareBatch.getMaengde() - netto);

                // Laver bruttokontrol
                v.commandRM20("Fjern råvare og beholder", "Tryk ok");
                brutto = v.commandS();
                v.commandT();
                if (netto + tara + brutto < 0.01 && netto + tara + brutto > -0.01) {
                    produktBatchKomp = new ProduktBatchKomp(produktBatch.getId(), raavareBatchId, user.getId(), tara, netto);
                    v.commandRM20("Bruttokontrol lykkedes", "Tryk ok");
                } else {
                    v.commandRM20("Bruttokontrol mislykkedes", "Tryk ok");
                    nextRaastof = false;
                }
            } else {
                v.commandRM20("Tolerancen er ikke overholdt", "Tryk ok");
                nextRaastof = false;
            }
            if (!nextRaastof) {
                i--;
                v.commandRM20("Grundet fejl afvej samme råstof igen", "Tryk ok");
                v.commandT();
            }
            else{
                raavareBatchDAO.update(raavareBatch);
                produktBatchKompDAO.create(produktBatchKomp);
            }
            if(i==2-receptKomps.length){
                v.commandRM20("For næste afvejning", "Tryk ok");
            }
        }

        // Afslutter afvejning
        produktBatch.setBatchStatus(2);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        produktBatch.setSlutDato(dateFormat.format(date));
        produktBatchDAO.update(produktBatch);
        v.commandRM20("Afvejning udført", "");
    }

    private int inputToInt(String input) {
        int num;
        input = input.replace(" ", "");
        input = input.replace("\"", "");
        num = Integer.valueOf(input.replace("RM20A", ""));
        return num;
    }

    private String inputToString(String input) {
        String str;
        input = input.replace(" ", "");
        input = input.replace("\"", "");
        str = input.replace("RM20A", "");
        return str;
    }
    private boolean userExsists(User user) throws SQLException, IDAO.DALException {
        boolean result = false;
        User[] array = userDAO.getList();
        for (User currUser : array){
            if(user.getId() == currUser.getId()){
                result = true;
                break;
            }
        }
        return result;
    }
    private boolean produktBatchExsists(ProduktBatch produktBatch) throws SQLException{
        boolean result = false;
        ProduktBatch[] array = produktBatchDAO.getList();
        for (ProduktBatch currProduktBatch : array){
            if(produktBatch.getId() == currProduktBatch.getId()){
                result = true;
                break;
            }
        }
        return result;
    }
}
