import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final int PORT = 8080;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) {
        // Đặt mã hóa mặc định là UTF-8
        System.setProperty("file.encoding", "UTF-8");
        
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server đang chạy trên cổng " + PORT);
            System.out.println("Địa chỉ IP của server: " + getServerIP());
            
            while (true) {
                try {
                    // Chấp nhận kết nối từ client
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client mới kết nối: " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Tạo một luồng mới để xử lý client
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                } catch (IOException e) {
                    System.out.println("Lỗi khi chấp nhận kết nối: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi khởi tạo server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Phương thức để lấy địa chỉ IP của server
    private static String getServerIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Lỗi khi lấy địa chỉ IP: " + e.getMessage());
        }
        return "127.0.0.1";
    }
    
    // Phương thức để gửi tin nhắn đến tất cả client
    public static void broadcast(String message, ClientHandler sender) {
        String time = dateFormat.format(new Date());
        String formattedMessage = "[" + time + "] " + message;
        
        System.out.println(formattedMessage);
        
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(formattedMessage);
            }
        }
    }
    
    // Phương thức để xóa client khỏi danh sách khi ngắt kết nối
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client đã ngắt kết nối. Số client hiện tại: " + clients.size());
    }
    
    // Lớp xử lý từng client
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private String clientAddress;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientAddress = socket.getInetAddress().getHostAddress();
            
            try {
                // Sử dụng UTF-8 cho đọc và ghi
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                
                // Gửi tin nhắn chào mừng đến client
                sendMessage("Chào mừng đến với server! Bạn đã kết nối thành công.");
                System.out.println("Đã gửi tin nhắn chào mừng đến client " + clientAddress);
            } catch (IOException e) {
                System.out.println("Lỗi khởi tạo client handler: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        @Override
        public void run() {
            try {
                String message;
                System.out.println("Đang chờ tin nhắn từ client: " + clientAddress);
                
                // Đọc tin nhắn từ client
                while ((message = reader.readLine()) != null) {
                    System.out.println("Đã nhận tin nhắn từ client " + clientAddress + ": \"" + message + "\"");
                    String fullMessage = "Client " + clientAddress + ": " + message;
                    
                    // Gửi tin nhắn đến tất cả client khác
                    broadcast(fullMessage, this);
                    
                    // Gửi xác nhận đã nhận tin nhắn
                    System.out.println("Gửi xác nhận đến client " + clientAddress);
                    sendMessage("Đã nhận: " + message);
                    
                    // Đảm bảo tin nhắn được gửi
                    writer.flush();
                }
            } catch (IOException e) {
                System.out.println("Lỗi khi đọc tin nhắn từ client: " + e.getMessage());
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        
        // Phương thức gửi tin nhắn đến client
        public void sendMessage(String message) {
            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
                System.out.println("Đã gửi tin nhắn đến client " + clientAddress + ": \"" + message + "\"");
            } catch (IOException e) {
                System.out.println("Lỗi khi gửi tin nhắn đến client: " + e.getMessage());
                e.printStackTrace();
                closeConnection();
            }
        }
        
        // Phương thức đóng kết nối
        private void closeConnection() {
            removeClient(this);
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                System.out.println("Đã đóng kết nối với client " + clientAddress);
            } catch (IOException e) {
                System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
