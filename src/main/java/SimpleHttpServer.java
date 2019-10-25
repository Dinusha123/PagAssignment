import bookstore.controller.BookController;
import bookstore.service.BookService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONException;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // info
        server.createContext("/info", new MyHandler());

        // book controller
        server.createContext("/store/books", new BookController());

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String response = "use store/books?id=6  to get info of a book \n\n"
                    +"sample data for json string  for POST method store/books \n\n{\n" +
                    "\t\"name\":\"Test Book\",\n" +
                    "\t\"author\":\"Test Author\",\n" +
                    "\t\"price\": 15000,\n" +
                    "\t\"description\": \" Test description \"\n" +
                    "}" ;
            writeResponse(httpExchange,response);
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {

        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

}

