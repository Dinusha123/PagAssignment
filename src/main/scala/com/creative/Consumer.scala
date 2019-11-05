package com.creative

import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}

object Consumer extends App{

  private val BOOK_QUEUE_CONSUME = "book_store";

//  override def main(args: Array[String]) = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")

    val connection = factory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare(BOOK_QUEUE_CONSUME,false,false,false,null)
    println(s"waiting for messages on $BOOK_QUEUE_CONSUME")

    val callback: DeliverCallback = (consumerTag, delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(s"Received $message with tag $consumerTag")
    }

    val cancel: CancelCallback = consumerTag => {}

    val autoAck = true
    channel.basicConsume(BOOK_QUEUE_CONSUME, autoAck, callback, cancel)

    while(true) {
      // we don't want to kill the receiver,
      // so we keep him alive waiting for more messages
      Thread.sleep(1000)
    }

    channel.close()
    connection.close()


//  }

}
