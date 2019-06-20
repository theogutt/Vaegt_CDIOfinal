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
        while(true) {
            RaavareBatch raavareBatch;
            ProduktBatchKomp produktBatchKomp = new ProduktBatchKomp();
            ProduktBatch produktBatch;
            Recept recept;
            User user;
            double netto = 0, tara = 0, brutto;
            double oevreGraense, nedreGraense;
            String input, ok, raavareNavn;
            boolean nextStep = false, nextRaastof;
            int raavareBatchId, produktBatchId, brugerId, raavareId;
            double raavareTole;
            boolean underProduktion = false;

            do {
                // Tjekker bruger
                input = v.commandRM20("INDTAST ID", "");
                brugerId = inputToInt(input);
                user = userDAO.get(brugerId);
                if (userExsists(user)) {
                    nextStep = true;
                    input = v.commandRM20(user.getNavn(), "dit navn? TRYK OK");
                    ok = inputToString(input);
                    if (ok.equals("")) {
                    } else if (!ok.equals("ditnavn?TRYKOK") || ok.equals("C")) {
                        v.commandRM20("Proev igen", "");
                        nextStep = false;
                    }
                } else {
                    v.commandRM20("Laboratoernummeret", "er forkert");
                }
            }
            while (!nextStep);

            // Tjekker produktBatch
            nextStep = false;
            do {
                do {
                    input = v.commandRM20("INDTAST PRODUKTBATCHID", "");
                    produktBatchId = inputToInt(input);
                    produktBatch = produktBatchDAO.get(produktBatchId);
                    if (produktBatchExsists(produktBatch)) {
                        nextStep = true;
                        if (produktBatch.getBatchStatus() == 2) {
                            v.commandRM20("ProduktBatchet", "er pt. aflsuttet");
                            nextStep = false;
                        } else if (produktBatch.getBatchStatus() == 1) {
                            v.commandRM20("ProduktBatchet er pt.", "under produktion");
                            underProduktion = true;
                        }
                    } else {
                        v.commandRM20("ProduktBatchet", "eksisterer ikke");
                    }
                } while (!nextStep);
                nextStep = false;
                // Fortæller bruger råstof og sætter produktBatch

                produktBatchId = inputToInt(input);
                produktBatch = produktBatchDAO.get(produktBatchId);
                recept = receptDAO.get(produktBatch.getReceptId());
                input = v.commandRM20(recept.getNavn(), " Skal produceres");
                ok = inputToString(input);
                if (ok.equals("Skalproduceres") || ok.equals("")) {
                    nextStep = true;
                    produktBatch.setBatchStatus(1);
                    produktBatchDAO.update(produktBatch);
                } else if (ok.equals("C") || ok.equals("")) {
                }
            } while (!nextStep);
            recept = receptDAO.get(produktBatch.getReceptId());
            ReceptKomp[] receptKomps = recept.getIndholdsListe();
            int n = 0;
            if (underProduktion == true) {
                n = produktBatchKompDAO.getList(produktBatchId).length;
                System.out.println("n=" + n);
            }
            // Styrer afvejning
            for (int i = n; i < receptKomps.length; i++) {
                nextRaastof = true;
                // Laver første taraering
                input = v.commandRM20("PLACER BEHOLDER", "TRYK OK");
                ok = inputToString(input);
                if (ok.equals("TRYKOK") || ok.equals("")) {

                    while (true) {
                        v.commandDW();
                        input = v.commandRM20(String.valueOf(v.commandS()), "cancel=vej igen");
                        ok = inputToString(input);
                        if (ok.equals("cancel=vejigen") || ok.equals("")) {
                            break;
                        }
                    }

                    tara = v.commandS();
                    v.commandT();
                }

                // Setter råvarebatch, og starter afvejning
                raavareNavn = raavareDAO.get(receptKomps[i].getRaavareId()).getNavn();
                raavareId = raavareDAO.get(receptKomps[i].getRaavareId()).getId();
                v.commandRM20("hent", raavareNavn);
                while (true) {
                    input = v.commandRM20("skriv RBnr for", raavareNavn);
                    raavareBatchId = inputToInt(input);
                    raavareBatch = raavareBatchDAO.get(raavareBatchId);
                    raavareTole = (100.0 - receptKomps[i].getTolerance()) * receptKomps[i].getNonNetto() / 100;
                    if (raavareBatch.getRaavareId() != raavareId)
                        v.commandRM20("RB er ikke ", raavareNavn);
                    else if (raavareTole > raavareBatchDAO.get(raavareId).getMaengde())
                        v.commandRM20("for lidt RB", "til produktet");
                    else
                        break;
                }
                input = v.commandRM20("PLACER NETTO i form af", raavareNavn);
                ok = inputToString(input);
                if (ok.equals(raavareNavn) || ok.equals("")) {
                    while (true) {
                        v.commandDW();
                        input = v.commandRM20(String.valueOf(v.commandS()), "cancel=vej igen");
                        ok = inputToString(input);
                        if (ok.equals("cancel=vejigen") || ok.equals("")) {
                            break;
                        }
                    }
                    netto = v.commandS();
                    v.commandT();
                }

                // Udregner tolerance
                oevreGraense = (100.0 + receptKomps[i].getTolerance()) * receptKomps[i].getNonNetto() / 100;
                nedreGraense = (100.0 - receptKomps[i].getTolerance()) * receptKomps[i].getNonNetto() / 100;
                if (netto >= nedreGraense && netto <= oevreGraense) {
                    raavareBatch.setMaengde(raavareBatch.getMaengde() - netto);

                    // Laver bruttokontrol
                    v.commandRM20("Fjern brutto", "Tryk ok");
                    brutto = v.commandS();
                    v.commandT();
                    System.out.println("brutto = " + brutto + "netto = " + netto + "tara = " + tara);
                    if (netto + tara + brutto < 0.01 && netto + tara + brutto > -0.01) {
                        produktBatchKomp = new ProduktBatchKomp(produktBatch.getId(), raavareBatchId, user.getId(), tara, netto);
                        v.commandRM20("Bruttokontrol lykkedes", "Tryk ok");
                    } else {
                        v.commandRM20("Bruttokontrol fejlet", "Tryk ok");
                        nextRaastof = false;
                    }
                } else {
                    v.commandRM20("Tolerance", "overskredet");
                    nextRaastof = false;
                }
                if (!nextRaastof) {
                    i--;
                    v.commandRM20("afvej igen", "Tryk ok");
                    v.commandT();
                } else {
                    raavareBatchDAO.update(raavareBatch);
                    produktBatchKompDAO.create(produktBatchKomp);
                }
                if (i == 2 - receptKomps.length) {
                    v.commandRM20("naeste afvejning", "Tryk ok");
                }
            }

            // Afslutter afvejning
            produktBatch.setBatchStatus(2);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            produktBatch.setSlutDato(dateFormat.format(date));
            produktBatchDAO.update(produktBatch);
            v.commandRM20("Afvejning udfoert", "");
            v.callWeight("Q crlf");
            input = v.commandRM20("Ny vejning=OK", "Afslut=cancel");
            ok = inputToString(input);
            if(ok.equals("Afslut=cancel")||ok.equals("")){

            }
            else{
                break;
            }
        }
    }

    private int inputToInt(String input) {
        if(input.equals("RM20A\"\"")){return -99;}
        if(input.equals("RM20C")){return -99;}
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
            if(user.getId() == currUser.getId()&&currUser.isAktiv()==true){
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
