package com.creative

import bookstore.model.Book
import bookstore.service.BookService
import com.google.gson.Gson
import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}

import scala.collection.mutable

/**
  * @author ${user.name}
  */
object App {

  private val BOOK_CRUD_QUEUE = "crud";
  private val BOOK_LIST_QUEUE = "list";
  private val BOOK_QUEUE = "set";
  val exchange = ""
  var bookService: BookService = new BookService
  val gson = new Gson
  var bookList= mutable.MutableList[Book]()

  // initiate rabbitMQ
  val factory = new ConnectionFactory
  factory.setHost("localhost")
  val connection = factory.newConnection
  val channel = connection.createChannel()

  channel.queueDeclare(BOOK_CRUD_QUEUE,false,false,false,null)
//  channel.queueDeclare(BOOK_LIST_QUEUE,false,false,false,null)
//  channel.queueDeclare(BOOK_QUEUE,false,false,false,null)


  def main(args : Array[String]) {
    println( "*** Welcome to the Book Store ***\n" )

    optionList

    def optionList(): Unit ={
      println( "Please enter 1 to enter book details" )
      println( "Please enter 2 to get the details of a book by id" )
      println( "Please enter 3 to get the book list" )
      println( "Please enter 9 to exit" )

      val input: String = scala.io.StdIn.readLine("Please select the option(1 or 2 or 3 or 9) ")
      //      val input: String = "1"


      if(bookService.isAllDigits(input)){
        if(1 == input.toInt){
          // method call for entering book data
          addBookData
        }else if(2 == input.toInt){
          // method call for get the book details by id
          getBookDetailsById

        }else if(3 == input.toInt){
          // method call for retrieving book list
          getBookList
        }else if (9 == input.toInt){

          println( "Exit" )
          System.exit(0)
        }else
        {
          println( "Please select the option" )
        }
      }else{
        optionList
      }
      optionList
    }


    /**
      * This method is used to get the book list
      */
    def getBookList(): Unit ={

      println("Getting book list")

      val info = "all"
      publishRequest(info)
//      consumeBookList
      println(s"Consuming book list\n")
      consumeData(BOOK_LIST_QUEUE)

    }

    /**
      * This method is used to get book details
      * for given id
      * @param id
      */
    def getBookDetailsById(): Unit ={

      var bookId: String = scala.io.StdIn.readLine("Please enter book id  : ")

      //validation for price
      while(!bookService.isAllDigits(bookId)){
        bookId = scala.io.StdIn.readLine("Please enter number for book id : ")
      }

      publishRequest(bookId)
//      consumeBookDetailsById

      println(s"Consuming book data by id \n")
      consumeData(BOOK_QUEUE)

    }

    /**
      * This method is used to publish/add book details
      * @param book
      */
    def addBookData(): Unit ={

      //      getting user inputs
      var name: String = scala.io.StdIn.readLine("Please enter book name  : ")
      var author: String = scala.io.StdIn.readLine("Please enter author's name  : ")
      var price: String = scala.io.StdIn.readLine("Please enter book price  : ")
      var description: String = scala.io.StdIn.readLine("Please enter book description  : ")

      //      var name: String = "Champ"
      //      var author: String = "Champ Auth"
      //      var price: String = "2500"
      //      var description: String = "Description"

      //validation for price
      while(!bookService.isAllDigits(price)){
        price = scala.io.StdIn.readLine("Please enter number for price : ")
      }
      // creating book object and publishing
      var  book: Book = new Book(1,name,author,price.toInt,description)

      println();
      println();

      bookList += book

      // publishing
      for(book: Book <- bookList){
        publishRequest(book)

      }
    }

    def publishRequest(value: Any): Unit ={

      val bookJson = gson.toJson(value)
      channel.basicPublish(exchange, BOOK_CRUD_QUEUE, null, bookJson.getBytes())
      println(s"sent book details by Sender : $bookJson")
      println()

    }

    def consumeBookList(): Unit ={
      println(s"Consuming book list\n")
      consumeData(BOOK_LIST_QUEUE)
    }

    def consumeBookDetailsById(): Unit ={

      println(s"Consuming book data by id \n")
      consumeData(BOOK_QUEUE)
    }

    /**
      * This is a public method to use consume data
      * for a given queue
      * @param queue
      */
    def consumeData(queue:String): Unit ={
      val callback: DeliverCallback = (consumerTag, delivery) => {
        val message = new String(delivery.getBody, "UTF-8")
        println(s"Received Data :  $message")
        println()

      }
      val cancel: CancelCallback = consumerTag => {}
      // consuming from the queue:BOOK_LIST_QUEUE
      val autoAck = true
      channel.basicConsume(queue, autoAck, callback, cancel)

    }
//
    while(true) {
      Thread.sleep(1000)
    }
    channel.close()
    connection.close()

  }

}
