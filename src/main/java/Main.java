import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final int PORT = 8989;
    private static final Gson GSON = new Gson();

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream())) {
                    BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
                    System.out.println("Сервер запущен на порту " + PORT);
                    String word = in.readLine();
                    System.out.println("Получен запрос на поиск слова '" + word + "'");

                    List<PageEntry> searchResults = engine.search(word);

                    List<Map<String, Object>> formattedResults = new ArrayList<>();
                    for (PageEntry entry : searchResults) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("pdfName", entry.getPdf());
                        result.put("page", entry.getPage());
                        result.put("count", entry.getCount());
                        formattedResults.add(result);
                    }

// Преобразование списка в формат JSON
                    String json = new Gson().toJson(formattedResults);
                    out.println(json);
                    out.flush();

                } catch (IOException e) {
                    System.out.println("Ошибка при обработке запроса");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}