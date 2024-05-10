import chess.*;
import server.ServerFacade;
import ui.PreloginUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        String serverURL = "http://localhost:8080";
        ServerFacade server = new ServerFacade(serverURL);
        PreloginUI preloginUI = new PreloginUI(server, serverURL);
        preloginUI.run();
    }
}