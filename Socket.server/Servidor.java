import java.io.*;
import java.net.*;

public class Servidor {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running;

    public void start(int port) throws IOException {
        System.out.println("Iniciando servidor na porta " + port);
        serverSocket = new ServerSocket(port);
        running = true;
        
        System.out.println("Aguardando conexão do cliente...");
        clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
        
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // Thread para receber mensagens do cliente
        new Thread(() -> {
            try {
                String inputLine;
                while (running && (inputLine = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(inputLine)) {
                        System.out.println("Cliente solicitou encerramento da conexão.");
                        break;
                    }
                    System.out.println("Cliente diz: " + inputLine);
                }
                stop();
            } catch (IOException e) {
                if (running) {
                    System.out.println("Erro na conexão com o cliente: " + e.getMessage());
                }
            }
        }).start();
        
        // Thread para enviar mensagens para o cliente
        new Thread(() -> {
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                String serverInput;
                while (running && (serverInput = consoleReader.readLine()) != null) {
                    if (clientSocket.isClosed()) {
                        break;
                    }
                    out.println(serverInput);
                    if ("exit".equalsIgnoreCase(serverInput)) {
                        stop();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler entrada do servidor: " + e.getMessage());
            }
        }).start();
    }

    public void stop() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            System.out.println("Servidor encerrado.");
        } catch (IOException e) {
            System.out.println("Erro ao encerrar conexões: " + e.getMessage());
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.print("Digite o endereço IP para vincular (ou deixe em branco para localhost): ");
            String ip = consoleReader.readLine(); // Captura o IP mesmo que não seja usado, para manter a interface
            
            System.out.print("Digite a porta para escutar: ");
            try {
                int port = Integer.parseInt(consoleReader.readLine());
                server.start(port);
            } catch (NumberFormatException e) {
                System.out.println("Porta inválida. Digite um número.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }
}
    

