package Weight;

import DAL.DAO.IDAO;
import DAL.DAO.ProduktBatchDAO;
import DAL.DAO.UserDAO;
import DAL.DTO.ProduktBatch;
import DAL.DTO.User;
import Weight.WeightConnector;

import java.io.IOException;
import java.sql.SQLException;

public class WeightController {

    public static void main(String[]args) throws Exception {
        WeightController weightController = new WeightController();
        weightController.afvejning();
    }
    private UserDAO userDAO = new UserDAO();
    private ProduktBatchDAO produktBatchDAO = new ProduktBatchDAO();
    private WeightConnector v;

    public WeightController() throws Exception {
        v = new WeightConnector();
    }
    public void afvejning() throws IOException, SQLException, IDAO.DALException {
        String input;
        boolean nextStep = false;

        do {
            input = v.commandRM20("INDTAST ID", "");
            int Id = inputToInt(input);
            User user = userDAO.get(Id);
            if (user.getNavn() != null) {
                nextStep = true;
                input = v.commandRM20(user.getNavn(), "Er dette dit navn? y/n");
                if (input.equals("y")) {}
                else{nextStep=false;}
                }
            else{v.commandRM20("Laboratørnummeret er forkert",""); }
        }
        while(nextStep==false);

        nextStep=false;
        do {
                input = v.commandRM20("INDTAST PRODUKTBATCHID", "");
                int Id = inputToInt(input);
                ProduktBatch produktBatch = produktBatchDAO.get(Id);
                if (produktBatch.getSlutDato() != null) {
                    nextStep = true;
                }
            else{v.commandRM20("ProduktBatchet eksisterer ikke",""); }
        }while(nextStep==false);

        do{

        }while(true);

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
        num = Integer.valueOf(input.replace("RM20 A ", ""));
        return num;
    }
}
