import bookstore.model.Book;
import bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONException;
import org.json.JSONObject;
import scala.collection.immutable.List;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import sun.rmi.runtime.Log;

import static com.sun.xml.internal.ws.model.RuntimeModeler.RESPONSE;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // test
        server.createContext("/store", new MyHandler());

        // view all books
        server.createContext("/store/books", new BooksController());

        // get info of a book
        server.createContext("/store/booksz", new BookInfoController());

        // add new book
        server.createContext("/store/", new AddBookController());



        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            Book book = new Book("Hound Of The Baskervills","Sir Author Connal Doil",2000.00,"New release 2019");
//            String response = "This is the response ..Book store";
            String response = book.name();
            writeResponse(httpExchange,response);
        }
    }

    static class BooksController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            BookService bookService = new BookService();

//            List<Book> bookList = bookService.getBookList();
            String response = bookService.getList();
            writeResponse(httpExchange,response);
        }
    }

    static class BookInfoController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            StringBuilder response = new StringBuilder();
            Map <String,String>parms = SimpleHttpServer.queryToMap(httpExchange.getRequestURI().getQuery());

            System.out.println("book id :"+parms.get("id"));


            BookService bookService = new BookService();
            String book = bookService.bookInfoByName();
            writeResponse(httpExchange,book);

        }
    }

    static class AddBookController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            BookService bookService = new BookService();
            String response = "";

            String method = httpExchange.getRequestMethod();

            if("POST".equals(method)){
                StringBuilder body = new StringBuilder();
                try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), UTF_8)) {
                    char[] buffer = new char[256];
                    int read;
                    while ((read = reader.read(buffer)) != -1) {
                        body.append(buffer, 0, read);
//                    System.out.println(buffer);
                    }
                    bookService.addBook(body.toString());

                    response = "Book added successfully";

                }catch (JSONException err){

                    System.out.println("Error"+err.toString());
                    response = "Book not added";
                }
            }

            writeResponse(httpExchange,response);

        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {

        httpExchange.sendResponseHeaders(200, response.length());
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

