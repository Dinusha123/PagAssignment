package com.creative

import bookstore.model.Book
import bookstore.service.BookService
import com.google.gson.Gson
import com.rabbitmq.client.ConnectionFactory

import scala.collection.mutable

object Sender extends App{

  private val BOOK_QUEUE = "book";
  var bookService: BookService = new BookService
  val gson = new Gson
  val exchange = ""

  // initiate rabbitMQ
  val factory = new ConnectionFactory
  factory.setHost("localhost")
  val connection = factory.newConnection
  val channel = connection.createChannel()
  channel.queueDeclare(BOOK_QUEUE,false,false,false,null)

  //creating book objects
  var  bookOne: Book = new Book(200,"Poliyana","Author Sample",2500,"Sample Description Poliyana")
  var  bookTwo: Book = new Book(520,"Titanic","Author Titanic",2500,"Sample Description Titanic")
  var  bookThree: Book = new Book(800,"Five","Author Sample Five",2500,"Sample Description Five")

  // creating book list
  var bookList= mutable.MutableList[Book]()
  bookList += bookOne
  bookList += bookTwo
  bookList += bookThree

  // publishing
  for(book: Book <- bookList){
    val bookJson = gson.toJson(book)
    channel.basicPublish(exchange, BOOK_QUEUE, null, bookJson.getBytes())
    println(s"sent book details by Sender : $bookJson")
  }


  channel.close()
  connection.close()


  //  def initiateRabbitMQSender:Channel = {
  //        val factory = new ConnectionFactory
  //        factory.setHost("localhost")
  //        val connection = factory.newConnection
  //        val channel = connection.createChannel()
  //        channel.queueDeclare(BOOK_QUEUE,false,false,false,null)
  //        channel
  //      }










  //  def initiateRabbitMQSender:Channel = {
  //    val factory = new ConnectionFactory
  //    factory.setHost("localhost")
  //    val connection = factory.newConnection
  //    val channel = connection.createChannel()
  //    channel.queueDeclare(BOOK_QUEUE,false,false,false,null)
  //    channel
  //  }
  //
  //  def send(channel: Channel,topic:String ,bookName:String): Unit ={
  //    println(bookName)
  //    channel.basicPublish(BOOK_QUEUE,topic,null,serialise(bookName))
  //  }
  //
  //  def serialise(value: Any): Array[Byte] = {
  //    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
  //    val oos = new ObjectOutputStream(stream)
  //    oos.writeObject(value)
  //    oos.close
  //    stream.toByteArray
  //  }

}
