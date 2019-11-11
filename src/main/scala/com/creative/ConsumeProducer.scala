package com.creative

import bookstore.model.Book
import bookstore.service.BookService
import com.google.gson.Gson
import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}
import net.liftweb.json.{DefaultFormats, parse}

import scala.collection.mutable

object ConsumeProducer extends App{

  private val BOOK_CRUD_QUEUE = "crud";
  private val BOOK_LIST_QUEUE = "list";
  private val BOOK_QUEUE = "set";
  implicit val formats = DefaultFormats
  val exchange = ""

  var bookService: BookService = new BookService
  val gson = new Gson
  var bookList= mutable.MutableList[Book]()
  var bookListConsumerStringList= mutable.MutableList[String]()

  // initiate rabbitMQ
  val factory = new ConnectionFactory()
  factory.setHost("localhost")
  val connection = factory.newConnection()
  val channel = connection.createChannel()

  channel.queueDeclare(BOOK_CRUD_QUEUE,false,false,false,null)
  channel.queueDeclare(BOOK_LIST_QUEUE,false,false,false,null)
  channel.queueDeclare(BOOK_QUEUE,false,false,false,null)

  println(s"waiting for book details from Sender : Queue Name : $BOOK_CRUD_QUEUE")

  //consume book details from Sender
  consume()

  /**
    * This method is used to consume book details
    * from the sender
    * using the queue BOOK_QUEUE
    */
  def consume(): Unit ={
    val callback: DeliverCallback = (consumerTag, delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(s"Received message from Client $message")

      // if the sent message is a decimal
     if(message.contains("all")){
       //publish book list
       publishBookList()
     }else if(bookService.isAllDigits(message.replaceAll("\"",""))){
       // if the sent message is a decimal
       val bookId = message.replaceAll("\"","").toInt
       publishBookDetailsById(bookId)
     }else{
        // add book
        addBook(message)
      }
    }
    val cancel: CancelCallback = consumerTag => {}
    // consuming from the queue
    val autoAck = true
    channel.basicConsume(BOOK_CRUD_QUEUE, autoAck, callback, cancel)
  }

  /**
    * This method is used to send all book list for
    * consumers
    * using the queue BOOK_QUEUE_CONSUME
    */
  def publishBookList(): Unit ={
    publishQueue(bookList,BOOK_LIST_QUEUE)
    println(s"published new 'BOOK LIST' \n")
  }

  /**
    * This method is used to publish book data for
    * a given id to the BOOK_QUEUE
    * @param id book id
    */
  def publishBookDetailsById(bookId:Int): Unit ={

    val book = bookList.filter(_.id==bookId )

    publishQueue(book(0),BOOK_QUEUE)
    println(s"Sent book details by id :"+bookId.toString +": "+book(0).toString+"\n")

  }

  def publishQueue(value:Any,queue:String)(): Unit ={
//    channel.queueDeclare(queue,false,false,false,null)
    //converting list into Json string
    val bookListToPublish = gson.toJson(value)
    //publishing book list to queue BOOK_QUEUE_CONSUME



    try{
      channel.basicPublish(exchange, queue, null, bookListToPublish.getBytes())
    } catch {
      case e => e.printStackTrace
    }



    println(s"PUBLISH :"+value.toString+ "\n")
  }

  /**
    * This method is used to add book to a list
    * @param bookJsonString json string of book object
    */
  def addBook(bookJsonString: String): Unit ={

    // converting json string to book object and save to a list
    val newBookJsonString = parse(bookJsonString)
    var newBook = newBookJsonString.extract[Book]
    newBook.id= bookList.length+1;

    bookList += newBook
    println("Added book to book list")
  }

    while(true) {
      Thread.sleep(1000)
    }

  channel.close()
  connection.close()

}


