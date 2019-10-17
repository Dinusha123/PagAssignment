package bookstore

// Use H2Driver to connect to an H2 database
import java.sql.{Connection, DriverManager}

object DbConnection extends App{

  // connect to the database named "mysql" on the localhost
//  val driver = "org.postgresql.Driver"
//  val url = "jdbc:postgresql://10.2.2.163/trackit_dev?autoReconnect=true"
//  val username = "postgres"
//  val password = "password"

//  // there's probably a better way to do this
//  var connection:Connection = null
//
//  try {
//    // make the connection
//    Class.forName(driver)
//    connection = DriverManager.getConnection(url, username, password)
//
//    // create the statement, and run the select query
//    val statement = connection.createStatement()
//    val resultSet = statement.executeQuery("SELECT name, author FROM pagero.books")
//    while ( resultSet.next() ) {
//      val name = resultSet.getString("name")
//      val author = resultSet.getString("author")
//      println("name, author = " + name + ", " + author)
//    }
//  } catch {
//    case e => e.printStackTrace
//  }
//  connection.close()


  def getConnection(): Connection = {
    // there's probably a better way to do this
    var connection:Connection = null

    val driver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://10.2.2.163/trackit_dev?autoReconnect=true"
    val username = "postgres"
    val password = "password"

    println("url "+url," username "+username, " password "+ password)

    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    connection
  }





}
