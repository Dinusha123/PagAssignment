package bookstore.service

import java.sql.{Connection, PreparedStatement}
import bookstore.DbConnection
import bookstore.model.Book
import com.google.gson.Gson
import net.liftweb.json._
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class BookService {

  val query = "SELECT * FROM pagero.books ORDER BY id DESC";
  val gson = new Gson
  var jsonString = ""
  var connection:Connection = null

  /**
    * Getting book list
    * @return jason string of entity list
    */
  def getBooks(): String = {

    var bookList= mutable.MutableList[Book]()

    bookList = getResultSet(query)

    jsonString = gson.toJson(bookList)

    jsonString

  }

  /**
    * This method is used to get the name list of the book entities
    * @return string
    */
  def getBookNames(): String = {

    var bookList= mutable.MutableList[Book]()

    bookList = getResultSet(query)

    var nameList: String = "";
    for(book: Book <- bookList){
      if(nameList.equals("")){
        nameList = nameList+book.name
      }else{
        nameList = nameList+","+book.name
      }
    }

    nameList

  }

  /**
    * This method is used to get the info of a certain book by it's id
    * @param bookId
    * @return json string of a book entity
    */
  def bookInfoById(bookId: String): String =
  {

    toInt(bookId) match {

      case Success(bookId) =>

        var bookList= mutable.MutableList[Book]()
        bookList = getResultSet(query)
        val book = bookList.filter(_.id==bookId )
        try{
          jsonString = gson.toJson(book(0))
          //jsonString = gson.toJson(bookList(5))
        }catch {
          case e => e.printStackTrace
        }

      case Failure(s) => jsonString = "Please enter a number ..."
    }

    jsonString
  }

  /**
    * This method is used to insert new book records
    * @param jsonString
    * @return String
    */
  def addBook(jsonString: String): String =
  {
    var response: String = "Book added successfully"

    implicit val formats = DefaultFormats

    println("Add Book method")
    val newBookJsonString = parse(jsonString)
    val newBook = newBookJsonString.extract[Book]

    // inserting data
    val insertSql = """
                      |insert
                      |into pagero.books (name,author,price,description)
                      |values (?,?,?,?)
                    """.stripMargin

    connection = DbConnection.getConnection();

    val preparedStmt: PreparedStatement = connection.prepareStatement(insertSql)
    preparedStmt.setString (1, newBook.name)
    preparedStmt.setString (2, newBook.author)
    preparedStmt.setDouble(3, newBook.price)
    preparedStmt.setString (4, newBook.description)

    try{
      preparedStmt.execute
    } catch {
      case e => e.printStackTrace
        response = "Book not added..."
    }

    preparedStmt.close()

    response

  }

  /**
    * This is common method which extracts the result set once the query is given as an argument
    * @param query SQL query
    * @return Mutable List of Book objects
    */
  def getResultSet(query: String): mutable.MutableList[Book]= {

    var bookList= mutable.MutableList[Book]()

    try {

      //connection = DriverManager.getConnection(url, username, password)
      connection = DbConnection.getConnection();

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(query)


      val v = new Iterator[String] {
        def hasNext = resultSet.next()
        def next() = resultSet.getString(1)
      }.toStream


      while ( resultSet.next() ) {

        val id = resultSet.getInt("id")
        val name = resultSet.getString("name")
        val author = resultSet.getString("author")
        val price = resultSet.getDouble("price")
        val description = resultSet.getString("description")
        println("Id :"+id+" Name :"+name+" Author :"+author+" Price :"+price+" Description :"+description)

        //creating book objects
        var  book: Book = new Book(id,name,author,price,description)

        bookList += book
      }

    } catch {
      case e => e.printStackTrace
    }
    connection.close()

    bookList.foreach( x => print(x.name+" | "))
    bookList
  }

  /**
    *This method is used to convert string to int and
    *  used Try[T] : Scala Try, Success, Failure
    * @param s
    * @return
    */
  def toInt(s: String): Try[Int] = Try {
    Integer.parseInt(s.trim)
  }


}
