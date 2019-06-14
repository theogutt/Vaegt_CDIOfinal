package Weight;

import java.io.*;
import java.net.Socket;

public class WeightConnector {
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;

    public WeightConnector() throws Exception {
        sock = new Socket("127.0.0.1", 8000);
        System.out.println("Forbinder til vægt...");
        //out = new PrintWriter(sock.getOutputStream(), true);
        //in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    public void callWeight(String command) throws IOException {
        out = new PrintWriter(sock.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

        out.println(command);
        System.out.println(command);

        System.out.println(in.readLine());

        System.out.println();
    }
    public void SCallWeight(String command) throws IOException {
        out = new PrintWriter(sock.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

        out.println(command);
    }


    // Viser vægt i kg
    public Double commandS() throws IOException {
        double val;
        SCallWeight("S crlf");
        String input = in.readLine();
        System.out.println();
        input=input.replace("\"","");
        input = input.replace("S S     ","");
        input = input.replace(" kg","");
        System.out.println("test: "+input);
        val = Double.valueOf(input);
        return val;
    }

    // Tarerer vægten
    public void commandT() throws IOException {
        callWeight("T crlf");
    }

    // Skriver "output" i displayet
    public void commandD(String output) throws IOException {
        callWeight("D " + "\"" + output + "\"" + " crlf");
    }

    // Viser vægten igen (bruges efter der er skrevet et output i displayet)
    public void commandDW() throws IOException {
        callWeight("DW crlf");
    }

    // Skriver "output" i et andet display
    public void commandP111(String output) throws IOException {
        if (output.length() <= 30)
            callWeight("P111 " + "\"" + output + "\"" + " crlf");
        else
            System.out.println("FEJL, for langt output");
    }

    // Skriver "output" og "output2" i to displays og venter på inputs
    public String commandRM20(String output1, String output2) throws IOException {
        callWeight("RM20 8 \"" + output1 + "\" \"" + output2 + "\" \"&3\"" + " crlf");

        String input = in.readLine();
        System.out.println(input);

        return input;
    }
}
