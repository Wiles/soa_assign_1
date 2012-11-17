package ca.setc.soa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: samuel
 * Date: 11/15/12
 * Time: 8:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoaSocketListener extends Thread {

    private Socket socket = null;

    public SoaSocketListener(Socket socket)
    {
        super("SoaSocketListener");
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            String inputLine, outputLine;
            SoaProtocol sp = new SoaProtocol();
            outputLine = sp.processInput(null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                outputLine = sp.processInput(inputLine);
                out.println(outputLine);
            }

            //TODO

            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
