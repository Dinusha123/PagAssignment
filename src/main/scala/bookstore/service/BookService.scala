package bookstore.service
import java.sql.{Connection, PreparedStatement}

import bookstore.DbConnection
import bookstore.model.Book
import com.google.gson.Gson
import net.liftweb.json._

class BookService {
  private val  book1: Book = new Book("Test Book 1","Test Author 1",1000,"test description 1")
  private val  book2: Book = new Book("Test Book 2","Test Author 2",2000,"test description 2")
  private val  book3: Book = new Book("Test Book 3","Test Author 3",3000,"test description 3")
  private val  book4: Book = new Book("Test Book 4","Test Author 4",15000,"test description 4")
  private val  book5: Book = new Book("Test Book 5","Test Author 5",1600,"test description 5")

  private var books: List[Book] = book1:: book2:: book3:: book4:: book5:: Nil

  def getList(): String = {

    var list = ""
    list = getBookNames(books)
    list
  }


  //getting name list
  def getBookNames(books: List[Book]): String = {

    var bookList: List[Book] = Nil
    val query = "SELECT * FROM pagero.books";
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

  //getting info of a book
  def bookInfoByName(bookId: Int): String =
  {
    var bookList: List[Book] = Nil
    val query = "SELECT * FROM pagero.books";
    bookList = getResultSet(query)
    var jsonString = ""
    val gson = new Gson

    try{
      jsonString = gson.toJson(bookList(0))
    }catch {
      case e => e.printStackTrace
    }

    jsonString
  }

//  insert book data
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

    var conn:Connection = null
    conn = DbConnection.getConnection();

    val preparedStmt: PreparedStatement = conn.prepareStatement(insertSql)
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

  def getResultSet(query: String): List[Book]= {

    // there's probably a better way to do this
    var connection:Connection = null

    var bookList: List[Book] = Nil

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
        val name = resultSet.getString("name")
        val author = resultSet.getString("author")
        val price = resultSet.getString("price")
        val description = resultSet.getString("description")
        println("name, author,price,desc = " + name + ", " + author+" ," +price+ ","+author)

        //creating book objects
        var  book: Book = new Book(resultSet.getString("name"),
                                    resultSet.getString("author"),
                                    resultSet.getDouble("price"),
                                    resultSet.getString("description"))


        bookList = book::Nil
      }

    } catch {
      case e => e.printStackTrace
    }
    connection.close()

    bookList.foreach( x => print(x.name+" | "))
    bookList
  }


}
