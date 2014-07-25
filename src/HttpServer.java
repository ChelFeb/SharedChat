import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private final static int SERVER_PORT = 8080;

    public void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        while (true) {
            //метод accept заставляет программу ждать подключений по указаному порту
            //после успешного подключения создается Socket объект
            Socket s = serverSocket.accept();
//            System.err.println("Client accepted");
            // Создаем и запускаем новый поток, реализация в методе run класса SocketProcessor
            new Thread(new StreamHandler(s)).start();
        }
    }


    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer();
        server.startServer();
    }
}
