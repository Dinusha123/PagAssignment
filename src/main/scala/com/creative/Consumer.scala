package com.creative

import bookstore.service.BookService
import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}

object Consumer extends App{

  private val BOOK_QUEUE_CONSUME = "list";
  var bookService: BookService = new BookService

  // initiate rabbitMQ
  val factory = new ConnectionFactory()
  factory.setHost("localhost")
  val connection = factory.newConnection()
  val channel = connection.createChannel()
  channel.queueDeclare(BOOK_QUEUE_CONSUME,false,false,false,null)
  println(s"waiting for book list: Queue Name :$BOOK_QUEUE_CONSUME")

  val callback: DeliverCallback = (consumerTag, delivery) => {
    val message = new String(delivery.getBody, "UTF-8")
    println(s"\nReceived book list $message")
  }
  val cancel: CancelCallback = consumerTag => {}
  val autoAck = true
  channel.basicConsume(BOOK_QUEUE_CONSUME, autoAck, callback, cancel)

  while(true) {
    Thread.sleep(1000)
  }

  channel.close()
  connection.close()

}
