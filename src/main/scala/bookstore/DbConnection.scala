package bookstore

// Use H2Driver to connect to an H2 database
import java.sql.{Connection, DriverManager}

object DbConnection extends App{
  
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
