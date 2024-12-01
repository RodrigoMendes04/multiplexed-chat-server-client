    import java.io.*;
    import java.net.*;
    import java.util.*;

    public class ChatServer {
        private static final Map<String, ClientHandler> clients = new HashMap<>();
        private static final Map<String, Set<ClientHandler>> rooms = new HashMap<>();

        public static void main(String[] args) throws IOException {
            if (args.length != 1) {
                System.out.println("Usage: java ChatServer <port>");
                return;
            }

            int port = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        }

        private static class ClientHandler extends Thread {
            private Socket socket;
            private BufferedReader in;
            private PrintWriter out;
            private String name;
            private String room;

            public ClientHandler(Socket socket) throws IOException {
                this.socket = socket;
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.name = null;
                this.room = null;
            }

            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        handleMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (name != null) {
                        clients.remove(name);
                        if (room != null) {
                            rooms.get(room).remove(this);
                            broadcast(room, "LEFT " + name);
                        }
                    }
                }
            }

            private void handleMessage(String message) {
                if (message.startsWith("/")) {
                    handleCommand(message);
                } else if (room != null) {
                    broadcast(room, "MESSAGE " + name + " " + message);
                } else {
                    out.println("ERROR");
                }
            }

            private void handleCommand(String command) {
                String[] parts = command.split(" ", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1] : null;

                switch (cmd) {
                    case "/nick":
                        if (room == null) {
                            handleNick(arg);
                        } else {
                            out.println("ERROR");
                        }
                        break;
                    case "/newnick":
                        if (room != null) {
                            handleNick(arg);
                        } else {
                            out.println("ERROR");
                        }
                        break;
                    case "/join":
                        handleJoin(arg);
                        break;
                    case "/leave":
                        handleLeave();
                        break;
                    case "/bye":
                        handleBye();
                        break;
                    case "/priv":
                        handlePriv(arg);
                        break;
                    default:
                        out.println("ERROR");
                }
            }

            private void handleNick(String newName) {
                if (newName == null || clients.containsKey(newName)) {
                    out.println("ERROR");
                } else {
                    if (name != null) {
                        clients.remove(name);
                        if (room != null) {
                            broadcast(room, "NEWNICK " + name + " " + newName);
                        }
                    }
                    clients.put(newName, this);
                    name = newName;
                    out.println("OK");
                }
            }

            private void handleJoin(String newRoom) {
                if (newRoom == null) {
                    out.println("ERROR");
                    return;
                }
                if (room != null) {
                    rooms.get(room).remove(this);
                    broadcast(room, "LEFT " + name);
                }
                room = newRoom;
                rooms.computeIfAbsent(room, k -> new HashSet<>()).add(this);
                out.println("OK");
                broadcast(room, "JOINED " + name);
            }

            private void handleLeave() {
                if (room != null) {
                    rooms.get(room).remove(this);
                    broadcast(room, "LEFT " + name);
                    room = null;
                    out.println("OK");
                } else {
                    out.println("ERROR");
                }
            }

            private void handleBye() {
                out.println("BYE");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void handlePriv(String arg) {
                if (arg == null) {
                    out.println("ERROR");
                    return;
                }
                String[] parts = arg.split(" ", 2);
                String targetName = parts[0];
                String message = parts.length > 1 ? parts[1] : null;

                ClientHandler target = clients.get(targetName);
                if (target == null || message == null) {
                    out.println("ERROR");
                } else {
                    target.out.println("PRIVATE " + name + " " + message);
                    out.println("OK");
                }
            }

            private void broadcast(String room, String message) {
                for (ClientHandler client : rooms.get(room)) {
                    client.out.println(message);
                }
            }
        }
    }