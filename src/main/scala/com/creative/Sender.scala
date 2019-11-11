package com.creative

import bookstore.model.Book
import bookstore.service.BookService
import com.google.gson.Gson
import com.rabbitmq.client.ConnectionFactory

import scala.collection.mutable

object Sender extends App{

  def runSender(book: Book): Unit ={
    val BOOK_QUEUE = "book";
    var bookService: BookService = new BookService
    val gson = new Gson
    val exchange = ""

    // initiate rabbitMQ
    val factory = new ConnectionFactory
    factory.setHost("localhost")
    val connection = factory.newConnection
    val channel = connection.createChannel()
    channel.queueDeclare(BOOK_QUEUE,false,false,false,null)

    // creating book list
    var bookList= mutable.MutableList[Book]()
    bookList += book

    // publishing
    for(book: Book <- bookList){
      val bookJson = gson.toJson(book)
      channel.basicPublish(exchange, BOOK_QUEUE, null, bookJson.getBytes())
      println(s"sent book details by Sender : $bookJson")
      println()
    }

    channel.close()
    connection.close()
  }


}
