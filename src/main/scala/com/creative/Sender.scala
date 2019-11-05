package com.creative

import com.rabbitmq.client.ConnectionFactory

import scala.util.Random

object Sender extends App{
  private val BOOK_QUEUE = "book_store";

//  override def main(args: Array[String]) = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare(BOOK_QUEUE,false,false,false,null)

    val message = "Hello world! " + Random.nextInt(100)
    val exchange = ""
    channel.basicPublish(exchange, BOOK_QUEUE, null, message.getBytes)

    println(s"sent message $message")

    channel.close()
    connection.close()
//  }












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
