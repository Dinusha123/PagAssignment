import bookstore.model.Book;
import bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import scala.collection.immutable.List;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

public class controller {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // test
        server.createContext("/store", new MyHandler());

        // view all books
        server.createContext("/store/books", new BooksController());

        // get info of a book
        server.createContext("/store/books/name", new BookInfoController());

        // get info of a book
        server.createContext("/store/books/add", new AddBookController());



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

//            Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
//            System.out.println("param A=" + params.get("A"));

//            Headers map = httpExchange.getRequestHeaders();
//            Headers rmap = httpExchange.getResponseHeaders();
//            URI uri = httpExchange.getRequestURI();


            BookService bookService = new BookService();
            String book = bookService.bookInfoByName();
            writeResponse(httpExchange,book);

        }
    }

    static class AddBookController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            BookService bookService = new BookService();
            bookService.addBook();
            writeResponse(httpExchange,"Added");

        }
    }




    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

//    public static  Map<String, String> queryToMap(String query) {
//        Map<String, String> result = new HashMap<>();
//        for (String param : query.split("&")) {
//            String[] entry = param.split("=");
//            if (entry.length > 1) {
//                result.put(entry[0], entry[1]);
//            }else{
//                result.put(entry[0], "");
//            }
//        }
//        return result;
//    }

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

