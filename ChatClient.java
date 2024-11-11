import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {
    private static BufferedReader in;
    private static PrintWriter out;
    private static JTextArea messageArea;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java ChatClient <server> <port>");
            return;
        }

        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);

        Socket socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        JFrame frame = new JFrame("Chat Client");
        messageArea = new JTextArea(8, 40);
        messageArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JTextField textField = new JTextField(40);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(new Runnable() {
            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        processMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void processMessage(String message) {
        if (message.startsWith("MESSAGE ")) {
            String[] parts = message.split(" ", 3);
            messageArea.append(parts[1] + ": " + parts[2] + "\n");
        } else if (message.startsWith("NEWNICK ")) {
            String[] parts = message.split(" ", 3);
            messageArea.append(parts[1] + " mudou de nome para " + parts[2] + "\n");
        } else if (message.startsWith("JOINED ")) {
            String[] parts = message.split(" ", 2);
            messageArea.append(parts[1] + " entrou na sala\n");
        } else if (message.startsWith("LEFT ")) {
            String[] parts = message.split(" ", 2);
            messageArea.append(parts[1] + " saiu da sala\n");
        } else if (message.startsWith("PRIVATE ")) {
            String[] parts = message.split(" ", 3);
            messageArea.append("Privado de " + parts[1] + ": " + parts[2] + "\n");
        } else {
            messageArea.append(message + "\n");
        }
    }
}