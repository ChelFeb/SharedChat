import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class StreamHandler implements Runnable {

    private static final String PATH_TO_HTML = "./chat.html";
    private static final String PATH_TO_DATA = "./chat.data";

    private Socket s;
    private InputStream is;
    private OutputStream os;
    private static Map<Long, String> chatData;

    static {
        chatData = new TreeMap<Long, String>();
    }

    public StreamHandler(Socket s) throws IOException {
        this.s = s;
        this.is = s.getInputStream();
        this.os = s.getOutputStream();
    }

    @Override
    public void run() {
        System.err.println("Stream " + Thread.currentThread().getName() + " have run");
        try {
            addMessage(readInputMessage());
            writeResponse(generateHtmlTable(chatData));
            while (true) {
                Thread.sleep(2000);
                refreshChat();
            }
        } catch (Throwable t) {
                /*do nothing*/
        }
        finally {
            try {
                s.close();
            } catch (Throwable t) {
                    /*do nothing*/
            }
        }
        System.err.println("Stream " + Thread.currentThread().getName() + " have finished");
    }

    public String readHtmlFromDisk() {
        StringBuilder sb = new StringBuilder();
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
        Метод Получает html из диска, добавляет в таблицу данные из map
     */
    public String generateHtmlTable(Map<Long, String> map) throws Throwable {
        String html = readHtmlFromDisk();
        StringBuilder sb = new StringBuilder(html);
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            pairs.getKey();
            pairs.getValue();
            sb.append("<tr><td>");
            sb.append(new Date((Long) pairs.getKey()));
            sb.append("</td><td>");
            sb.append(pairs.getValue());
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

    private void refreshChat() throws Throwable {
        writeResponse(generateHtmlTable(chatData));
    }

    //Парсим Http запрос, вытаскиваем с него message
    private String readInputMessage() throws Throwable {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String s = br.readLine();
            sb.append(s);
            if (s == null || s.trim().length() == 0) {
                break;
            }
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '=') {
                while (sb.charAt(i) != 'H' & sb.charAt(i + 1) != 'T' & sb.charAt(i + 2) != 'T' & sb.charAt(i + 3) != 'P') {
                    if (sb.charAt(i + 1) == '+'){
                        sb.setCharAt(i + 1, ' ');
                    }
                    out.append(sb.charAt(i + 1));
                    i++;
                }
                break;
            }
        }
        return out.toString();
    }

    public void addMessage(String msg) {
        chatData.put(new Date().getTime(), msg);
    }
}
