import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClient {

    // Variáveis relacionadas com a interface gráfica --- * NÃO MODIFICAR *
    JFrame frame = new JFrame("Chat Client");
    private JTextField chatBox = new JTextField();
    private JTextArea chatArea = new JTextArea();
    // --- Fim das variáveis relacionadas coma interface gráfica

    // Variáveis adicionais
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Método a usar para acrescentar uma string à caixa de texto
    // * NÃO MODIFICAR *
    public void printMessage(final String message) {
        chatArea.append(message + "\n");
    }

    // Construtor
    public ChatClient(String server, int port) throws IOException {
        // Inicialização da interface gráfica --- * NÃO MODIFICAR *
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatBox);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.setSize(500, 300);
        frame.setVisible(true);
        chatArea.setEditable(false);
        chatBox.setEditable(true);
        chatBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    newMessage(chatBox.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    chatBox.setText("");
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                chatBox.requestFocusInWindow();
            }
        });
        // --- Fim da inicialização da interface gráfica

        // Inicialização da conexão
        socket = new Socket(server, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    // Método invocado sempre que o utilizador insere uma mensagem na caixa de entrada
    public void newMessage(String message) throws IOException {
        out.println(message);
    }

    // Método principal do objecto
    public void run() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            processMessage(message);
        }
    }

    // Processa as mensagens recebidas do servidor
    private void processMessage(String message) {
        if (message.startsWith("MESSAGE ")) {
            String[] parts = message.split(" ", 3);
            printMessage(parts[1] + ": " + parts[2]);
        } else if (message.startsWith("NEWNICK ")) {
            String[] parts = message.split(" ", 3);
            printMessage(parts[1] + " mudou de nome para " + parts[2]);
        } else if (message.startsWith("JOINED ")) {
            String[] parts = message.split(" ", 2);
            printMessage(parts[1] + " entrou na sala");
        } else if (message.startsWith("LEFT ")) {
            String[] parts = message.split(" ", 2);
            printMessage(parts[1] + " saiu da sala");
        } else if (message.startsWith("PRIVATE ")) {
            String[] parts = message.split(" ", 3);
            printMessage("Privado de " + parts[1] + ": " + parts[2]);
        } else {
            printMessage(message);
        }
    }

    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }
}