package Weight;

import java.io.*;
import java.net.Socket;

public class WeightConnector {
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;

    public WeightConnector() throws Exception {
        final String LOCALHOST = "127.0.0.1";
        final String VAEGT = "169.254.2.3" ;
        sock = new Socket(VAEGT, 8000);
        System.out.println("Forbinder til vægt...");
    }

    public void callWeight(String command) throws IOException {
        out = new PrintWriter(sock.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out.println(command);
        System.out.println(command);
        System.out.println(in.readLine());
        System.out.println();
    }

    private void SCallWeight(String command) throws IOException {
        out = new PrintWriter(sock.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out.println(command);
    }


    // Viser vægt i kg
    public double commandS() throws IOException {
        double val;
        SCallWeight("S");
        String input = listen("SS");
        System.out.println();
        input = input.replace("\"", "");
        input = input.replace("SS", "");
        input = input.replace("kg", "");
        System.out.println("test: " + input);
        val = Double.valueOf(input);
        return val;
    }

    // Tarerer vægten
    public double commandT() throws IOException {
        double val;
        SCallWeight("T");
        String input = listen("TS");
        System.out.println();
        input = input.replace("\"", "");
        input = input.replace("TS", "");
        input = input.replace("kg", "");
        System.out.println("test: " + input);
        val = Double.valueOf(input);
        return val;
    }
    /*
    // Skriver "output" i displayet
    public void commandD(String output) throws IOException {
        callWeight("D " + "\"" + output + "\"" + " crlf");
    }
    */

    // Viser vægten igen (bruges efter der er skrevet et output i displayet)
    public void commandDW() throws IOException {
        callWeight("DW crlf");
    }
    /*
    // Skriver "output" i et andet display
    public void commandP111(String output) throws IOException {
        if (output.length() <= 30)
            callWeight("P111 " + "\"" + output + "\"" + " crlf");
        else
            System.out.println("FEJL, for langt output");
    }
    */

    // Skriver "output" og "output2" i to displays og venter på inputs
    public String commandRM20(String output1, String output2) throws IOException {
        callWeight("RM20 8 \"" + output1 + "\" \"" + output2 + "\" \"&3\"");
        String input = listen("RM20A","RM20C");
        System.out.println(input);

        return input;
    }
    public String listen(String lookingFor) throws IOException {
        String input;
        while (true) {
            input = in.readLine();
            System.out.println(input);
            input = input.replace(" ", "");
            if (input.contains(lookingFor)) {
                return input;
            }
        }
    }
    /*
    public boolean listenB(String lookingFor) throws IOException {
        String input;
        boolean found;
        while (true) {
            input = in.readLine();
            System.out.println(input);
            input = input.replace(" ", "");
            if (input.contains(lookingFor)) {
                found = true;
                return found;
            }
        }
    }
    */
    public String listen(String lookingFor, String Else) throws IOException {
        String input;
        while (true) {
            input = in.readLine();
            System.out.println(input);
            input = input.replace(" ", "");
            if (input.contains(lookingFor)) {
                return input;
            }
            else if(input.contains(Else)){
                return input;
            }
        }
    }
}
