package com.creative

import bookstore.model.Book
import bookstore.service.BookService
import com.google.gson.Gson
import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}
import net.liftweb.json.DefaultFormats

import scala.collection.mutable

object ConsumeProducer extends App{

  private val BOOK_QUEUE = "book";
  private val BOOK_QUEUE_CONSUME = "list";
  implicit val formats = DefaultFormats

  var bookService: BookService = new BookService
  val gson = new Gson
  var bookListConsumer= mutable.MutableList[Book]()
  var bookListConsumerStringList= mutable.MutableList[String]()

  // initiate rabbitMQ
  val factory = new ConnectionFactory()
  factory.setHost("localhost")
  val connection = factory.newConnection()
  val channel = connection.createChannel()

  channel.queueDeclare(BOOK_QUEUE,false,false,false,null)
  println(s"waiting for book details from Sender : Queue Name : $BOOK_QUEUE")

  //consume book details from Sender
  consumeBookData()

  while(true) {
    Thread.sleep(1000)
  }

  channel.close()
  connection.close()

  def publishBookList(): Unit ={
    val exchange = ""
    channel.queueDeclare(BOOK_QUEUE_CONSUME,false,false,false,null)

    //converting list into Json string
    val bookList = gson.toJson(bookListConsumerStringList)

    //publishing the array
    channel.basicPublish(exchange, BOOK_QUEUE_CONSUME, null, bookList.getBytes())
    println(s"Sent book list for consumers  $bookList")
  }

  def consumeBookData(): Unit ={
    val callback: DeliverCallback = (consumerTag, delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(s"Received message from Sender $message")

      // creating a book object list
      bookListConsumerStringList += message
      publishBookList()

    }

    val cancel: CancelCallback = consumerTag => {}

    // consuming from the queue
    val autoAck = true
    channel.basicConsume(BOOK_QUEUE, autoAck, callback, cancel)
  }


}
