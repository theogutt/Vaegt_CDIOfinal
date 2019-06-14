package Weight;

import DAL.DAO.*;
import DAL.DTO.*;

import java.io.IOException;
import java.sql.SQLException;

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
        RaavareBatch raavareBatch = new RaavareBatch();
        ProduktBatchKomp produktBatchKomp = new ProduktBatchKomp();
        ProduktBatch produktBatch;
        User user;
        double netto = 0, tara = 0, brutto;
        double øvreGrænse, nedreGrænse;
        String input, ok, råvareNavn;
        boolean nextStep = false, nextRåstof;
        int råvareBatchId, produktBatchId, brugerId, id;

        do {
            // Tjekker bruger
            input = v.commandRM20("INDTAST ID", "");
            brugerId = inputToInt(input);
            System.out.println(brugerId);
            user = userDAO.get(brugerId);
            if (user.getNavn() != null) {
                nextStep = true;
                input = v.commandRM20(user.getNavn(), "Er dette dit navn? TRYK OK");
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
            if (produktBatch.getSlutDato() != null) {
                nextStep = true;
            } else {
                v.commandRM20("ProduktBatchet eksisterer ikke", "");
            }
        } while (!nextStep);

        // Fortæller bruger råstof og sætter produktBatch
        id = inputToInt(input);
        produktBatch = produktBatchDAO.get(id);
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
        for (int i = 0; i < recept.getIndholdsListe().length; i++) {
            nextRåstof = true;
            // Laver første taraering
            input = v.commandRM20("PLACER BEHOLDER", "TRYK OK");
            ok = inputToString(input);
            if (ok.equals("")) {
                tara = v.commandS();
            }
            input = v.commandRM20("Tarer", "TRYK OK");
            ok = inputToString(input);
            if (ok.equals("")) {
                v.commandT();
            }

            // Setter råvarebatch, og starter afvejning
            råvareNavn = raavareDAO.get(receptKomps[i].getRaavareId()).getNavn();
            v.commandRM20(råvareNavn, "Skal afvejes");
            input = v.commandRM20("Indtast råvareBatchNummer", "");
            råvareBatchId = inputToInt(input);
            input = v.commandRM20("PLACER NETTO", "");
            ok = inputToString(input);
            if (ok.equals("")) {
                netto = v.commandS();
                v.commandT();
            }

            // Udregner tolerance
            øvreGrænse = (100.0 + receptKomps[i].getTolerance()) * receptKomps[i].getNonNetto() / 100;
            nedreGrænse = (100.0 - receptKomps[i].getTolerance()) * receptKomps[i].getNonNetto() / 100;
            if (netto >= nedreGrænse && netto <= øvreGrænse) {
                raavareBatch = raavareBatchDAO.get(råvareBatchId);
                raavareBatch.setMaengde(raavareBatch.getMaengde() - netto);
                produktBatchKomp = new ProduktBatchKomp(produktBatch.getId(), råvareBatchId, user.getId(), tara, netto);

                // Laver bruttokontrol
                v.commandRM20("Fjern råvare og beholder", "Tryk ok");
                brutto = v.commandS();
                v.commandT();
                if (netto + tara + brutto == 0) {
                    v.commandRM20("Bruttokontrol lykkes", "Tryk ok");
                } else {
                    v.commandRM20("Bruttokontrol mislykkedes", "Tryk ok");
                    nextRåstof = false;
                }
            } else {
                v.commandRM20("Tolerancen er ikke overholdt", "Tryk ok");
                nextRåstof = false;
            }
            if (!nextRåstof) {
                i--;
                v.commandRM20("Grundet fejl afvej samme råstof igen", "Tryk ok");
                v.commandT();
            }
            else{
                raavareBatchDAO.update(raavareBatch);
                produktBatchKompDAO.create(produktBatchKomp);
            }
        }

        // Afslutter afvejning
        produktBatch.setBatchStatus(3);
        produktBatchDAO.update(produktBatch);
        v.commandRM20("Afvejning udført", "");
    }

    private int inputToInt(String input) {
        int num;
        input = input.replace("\"", "");
        num = Integer.valueOf(input.replace("RM20 A ", ""));
        return num;
    }

    private String inputToString(String input) {
        String num;
        input = input.replace("\"", "");
        num = input.replace("RM20 A ", "");
        return num;
    }
}
