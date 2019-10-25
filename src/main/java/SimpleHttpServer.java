import bookstore.controller.BookController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;

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
            writeResponse(httpExchange,response,200);
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response, int statusCode) throws IOException {

        httpExchange.sendResponseHeaders(statusCode, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}

