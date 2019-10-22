package bookstore
import java.sql.{Connection, DriverManager}

object DbConnection extends App{

  def getConnection(): Connection = {
    var connection:Connection = null

    val driver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://10.2.2.163/pagero_assignment?autoReconnect=true"
    val username = "postgres"
    val password = "password"

    

    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    connection
  }





}
