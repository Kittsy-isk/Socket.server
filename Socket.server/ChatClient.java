import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        System.out.println("Conectando ao servidor " + ip + " na porta " + port);
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // Thread para receber mensagens do servidor
        new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(response)) {
                        System.out.println("Servidor encerrou a conexão.");
                        stopConnection();
                        break;
                    }
                    System.out.println("Servidor diz: " + response);
                }
            } catch (IOException e) {
                System.out.println("Erro na conexão com o servidor: " + e.getMessage());
            }
        }).start();
        
        // Thread para enviar mensagens para o servidor
        new Thread(() -> {
            try {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                while ((userInput = consoleReader.readLine()) != null) {
                    if (clientSocket.isClosed()) {
                        break;
                    }
                    out.println(userInput);
                    if ("exit".equalsIgnoreCase(userInput)) {
                        stopConnection();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler entrada do cliente: " + e.getMessage());
            }
        }).start();
    }

    public void stopConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Conexão encerrada.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Erro ao encerrar conexão: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.print("Digite o endereço IP do servidor: ");
            String ip = consoleReader.readLine();
            
            System.out.print("Digite a porta do servidor: ");
            int port = Integer.parseInt(consoleReader.readLine());
            
            client.startConnection(ip, port);
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}
