package com.creative

import bookstore.service.BookService
import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

/**
  * @author ${user.name}
  */
object Client {

  private val BOOK_CRUD_QUEUE = "crud";
  private val BOOK_LIST_QUEUE = "list";
  private val BOOK_QUEUE = "set";
  val exchange = ""
  var bookService: BookService = new BookService
  implicit val formats = DefaultFormats

  // initiate rabbitMQ
  val factory = new ConnectionFactory
  factory.setHost("localhost")
  val connection = factory.newConnection
  val channel = connection.createChannel()

  channel.queueDeclare(BOOK_CRUD_QUEUE,false,false,false,null)
  val separateLine = "-------------------------------------------------------------------------------------------------------"

  def main(args : Array[String]) {
    println( "*** Welcome to the Book Store ***\n" )

    optionList()

    def optionList(): Unit ={
      println( "-------------- OPTION LIST ---------------" )
      println( "Enter 1 to enter book details" )
      println( "Enter 2 to get the details of a book by id" )
      println( "Enter 3 to get the book list" )
      println( "Enter 9 to exit" )
      println( "------------------------------------------" )

      var option: String = scala.io.StdIn.readLine("Please select the option(1 or 2 or 3 or 9) : ")
      option = validateEmptyInputs("option",option)

      if(bookService.isAllDigits(option)){
        if(1 == option.toInt){
          // method call to add book data
          addBookData()
          optionList()
        }else if(2 == option.toInt){
          // method call to get the book details by id
          bookDetailsById()
        }else if(3 == option.toInt){
          // method call to retrieving book list
          readBookList()
        }else if (9 == option.toInt){
          println( "Exit" )
          System.exit(0)
        }else
        {
          println()
          println( "Please select the option from the below list" )
          optionList()
        }
      }else{
        optionList()
      }
    }

    /**
      * This method is used to get the book list
      */
    def readBookList(): Unit ={
      println("Getting book list")
      val info = "all"
      publishRequest(info)
      //consumeBookList
      println(s"Consuming book list\n")
      consumeData(BOOK_LIST_QUEUE)
    }

    /**
      * This method is used to get book details
      * for given id
      */
    def bookDetailsById(): Unit ={
      var bookId: String = scala.io.StdIn.readLine("Please enter book id  : ")
      //validation for price
      bookId = validateEmptyInputs("bookId",bookId)
      //publish the book id in order to get book data
      publishRequest(bookId)
      println(s"Consuming book data by id \n")
      consumeData(BOOK_QUEUE)
    }

    /**
      * This method is used to
      * publish/add book details
      *
      */
    def addBookData(): Unit ={
      //getting user inputs
      var name: String = scala.io.StdIn.readLine("Please enter book name  : ")
      name = validateEmptyInputs("name",name)

      var author: String = scala.io.StdIn.readLine("Please enter author's name  : ")
      author = validateEmptyInputs("author",author)

      var price: String = scala.io.StdIn.readLine("Please enter book price  : ")
      price = validateEmptyInputs("price",price)

      var description: String = scala.io.StdIn.readLine("Please enter book description  : ")
      description = validateEmptyInputs("description",description)

      // creating book object and publishing
      var  book: Book =  Book(2,name,author,price.toInt,description)
      println();
      println(separateLine)
      println();

      // publishing
      publishRequest(book)

    }

    def publishRequest(value: Any): Unit ={
      val bookJson = write(value)
      channel.basicPublish(exchange, BOOK_CRUD_QUEUE, null, bookJson.getBytes())
      println(s"Sent book data : $bookJson")
      println()
      println(separateLine)
    }

    /**
      * This is a public method to use consume data
      * for a given queue
      * @param queue
      */
    def consumeData(queue:String): Unit ={
      val callback: DeliverCallback = (consumerTag, delivery) => {
        val message = new String(delivery.getBody, "UTF-8")
        println()
        println(separateLine)
        println()
        println(s"Received data :  $message")
        println()
        println(separateLine)
      }
      val cancel: CancelCallback = consumerTag => {}
      // consuming from the queue:BOOK_LIST_QUEUE
      val autoAck = true
      channel.basicConsume(queue, autoAck, callback, cancel)
      optionList()
    }

    /**
      * This method is used to validate user input for empty values
      * @param property
      * @return
      */
    def validateEmptyInputs(property: String, input:String): String ={
      var value = input
      // validation for null inputs
      if(input.toString.length == 0 ){
        while(value.toString.length == 0){
          value = scala.io.StdIn.readLine("Please enter value for "+property+"  :")
        }
      }
      //validation for numbers
      if(property == "price" || property == "bookId" || property == "option"){
        while(!bookService.isAllDigits(value)){
          value = scala.io.StdIn.readLine("Please enter number for "+property+"  :")
          while(value.toString.length == 0){
            value = scala.io.StdIn.readLine("Please enter number for "+property+"  :")
          }
        }
      }
      value
    }

    while(true) {
      Thread.sleep(1000)
    }

    channel.close()
    connection.close()
  }

}
