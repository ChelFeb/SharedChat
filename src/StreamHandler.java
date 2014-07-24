import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class StreamHandler implements Runnable {
    private static final String PATH_TO_HTML = "/home/chelfeb/IdeaProjects/SharedChatProject/chat.html";
    private static final String PATH_TO_DATA = "/home/chelfeb/IdeaProjects/SharedChatProject/chat.data";

    private Socket s;
    private InputStream is;
    private OutputStream os;
    private static Map<Long, String> chatData;

    static {
        chatData = new TreeMap<Long, String>();
        chatData.put(2232L, "Message 1");
        chatData.put(32323L, "Message 2");
        chatData.put(42345324L, "Message 3");
    }

    public StreamHandler(Socket s) throws IOException {
        this.s = s;
        this.is = s.getInputStream();
        this.os = s.getOutputStream();
    }

    @Override
    public void run() {
        try {
            readInputHeaders();
            writeResponse(generateHtmlTable(chatData));
        } catch (Throwable t) {
                /*do nothing*/
        } finally {
            try {
                s.close();
            } catch (Throwable t) {
                    /*do nothing*/
            }
        }
        System.err.println("Client processing finished");
    }

    public String readHtmlFromDisk() {
        StringBuffer sb = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(new File(PATH_TO_HTML));
            while (fis.available() > 0) {
                sb.append((char) fis.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /*
        Метод Получает html из диска, добавляет в таблицу данный из map
     */
    public String generateHtmlTable(Map<Long, String> map) {
        String html = readHtmlFromDisk();
        StringBuffer sb = new StringBuffer(html);
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            pairs.getKey();
            pairs.getValue();
            sb.append("<tr><td>");
            sb.append(pairs.getKey());
            sb.append("</td><td>");
            sb.append(pairs.getValue());
            ;
            sb.append("</td></tr>");
        }
        sb.append("</table></body></html>");

        return sb.toString();
    }

    //Оборачиваем ответ в HTTP header и отправляем его
    private void writeResponse(String s) throws Throwable {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Server: SimpleServer/2014-07-24\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + s.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        String result = response + s;
        os.write(result.getBytes());
        os.flush();
    }

    //читаем HTTP запрос
    private void readInputHeaders() throws Throwable {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while (true) {
            String s = br.readLine();
            if (s == null || s.trim().length() == 0) {
                break;
            }
        }
    }
}
