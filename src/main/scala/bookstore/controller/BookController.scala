package bookstore.controller
import java.net.URLDecoder
import java.util.logging.Logger
import bookstore.service.BookService
import com.google.gson.Gson
import com.sun.net.httpserver.{HttpExchange, HttpHandler}
import scala.collection.JavaConversions._
import scala.util.parsing.json.JSONArray


class BookController extends HttpHandler{

  val logger = Logger.getLogger(this.getClass.toString())
  var hits = 0
  val gson = new Gson
  var bookService: BookService = new BookService
  var statusCode = 200

  override def handle(exchange: HttpExchange) {

    val uri = exchange.getRequestURI()
    val requestedMethod = exchange.getRequestMethod
    var response = ""

    if ("GET" == requestedMethod) {

      if(null == uri.getQuery){

        val bookList = bookService.getBooks()
        response =gson.toJson(bookList)

      }else{

        val params = parseParams(exchange)

        try{
          val book = bookService.bookInfoById(params("id"))

          if(book == null) response ="Please enter number as id"  else
            response =gson.toJson(book)

        } catch {
          case e => e.printStackTrace
            response = "Wrong id"
            statusCode = 400
        }
      }

    }else if("POST" == requestedMethod){
      response = bookService.addBook(exchange)
    }

    exchange.sendResponseHeaders(statusCode, 0)
    val os = exchange.getResponseBody()
    os.write(response.getBytes())
    os.flush()
    os.close()

    hits = hits + 1
  }

  /**
    * This method is used to get parameters
    * @param exchange
    * @return map
    */
  def parseParams(exchange: HttpExchange) = {
    exchange.getRequestURI().getQuery
      .split("&").toList
      .foldRight(Map.empty[String, String])((qp, map) => {
        val p = qp.split("=").map { x => URLDecoder.decode(x, "UTF-8") }
        map + (p(0) -> p(1))
      })
  }

  /**
    * This method is used to get the headers separately
    * @param exchange
    * @return
    */

  def parseHeaders(exchange: HttpExchange) = {
    exchange.getRequestHeaders
      .map { h =>
        val values = h._2.toList
        if (values.size > 1)
          (h._1, JSONArray(values))
        else
          (h._1, values(0))
      }
      .toMap
  }

}
