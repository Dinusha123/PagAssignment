package bookstore.service

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import java.sql.{Connection, PreparedStatement}

import bookstore.DbConnection
import bookstore.model.Book
import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import net.liftweb.json._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class BookService {

  val query = "SELECT * FROM pagero.books ORDER BY id DESC";
  val gson = new Gson
  var jsonString = ""
  var connection:Connection = _

  /**
    * Getting book list
    * @return jason string of entity list
    */
  def getBooks(): mutable.MutableList[Book] = {

    var bookList= mutable.MutableList[Book]()

    bookList = getResultSet(query)

    bookList

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
  def bookInfoById(bookId: String): Book =
  {
    toInt(bookId) match {
      case Success(bookId) =>

        var bookList= mutable.MutableList[Book]()
        bookList = getResultSet(query)
        val book = bookList.filter(_.id==bookId )
        book(0)

      case Failure(s) => null
    }
  }

  /**
    * This method is used to insert new book records
    * @param jsonString
    * @return String
    */
  def addBook(exchange: HttpExchange): String =
  {
    var response: String = "Book added successfully"

    implicit val formats = DefaultFormats

    println("Add Book method")

    jsonString  =  scala.io.Source.fromInputStream(exchange.getRequestBody).mkString
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
//        response = "Book not added..."
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

  /**
    * This method is used to convert an object to
    * a bitearray
    * @param value
    * @return Array[Byte]
    */
  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close()
    stream.toByteArray
  }

  /**
    * This method is used to deserialise the given byte array
    * @param bytes
    * @return
    */
  def deserialise(bytes: Array[Byte]): Any = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject
    ois.close()
    value
  }

  def toBytes(obj: AnyRef): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)

    oos.writeObject(obj)
    val bytes = baos.toByteArray

    oos.close()
    baos.close()

    bytes
  }

  def fromBytes[T](bytes: Array[Byte]): T = {
    val bis = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bis)

    val obj = ois.readObject().asInstanceOf[T]

    bis.close()
    ois.close()

    obj
  }


}
