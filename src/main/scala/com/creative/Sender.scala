//package com.creative
//
//import bookstore.model.Book
//import bookstore.service.BookService
//import com.google.gson.Gson
//import com.rabbitmq.client.ConnectionFactory
//
//import scala.collection.mutable
//
//object Sender extends App{
//
//  // creating book list
//  var bookList= mutable.MutableList[Book]()
//
//  def invokeMethod(value: String): Unit ={
//
//    val gson = new Gson
//    val BOOK_CRUD_QUEUE = "crud";
//    val exchange = ""
//    var bookService: BookService = new BookService
//
//    // initiate rabbitMQ
//    val factory = new ConnectionFactory
//    factory.setHost("localhost")
//    val connection = factory.newConnection
//    val channel = connection.createChannel()
//    channel.queueDeclare(BOOK_CRUD_QUEUE,false,false,false,null)
//
//
//
//
//
//
//
//  }
//
//
//
//
////
////  /**
////    * This method is used to get the book list
////    */
////  def getBookList(): Unit ={
////
////    println("Getting book list")
////    val info = "all"
////    channel.basicPublish(exchange, BOOK_CRUD_QUEUE, null, info.getBytes())
////
////  }
//
////  /**
////    * This method is used to get book details
////    * for given id
////    * @param id
////    */
////  def getBookDetailsById(id:Int): Unit ={
////
////    println("Getting book by id")
////
////  }
//
////  /**
////    * This method is used to publish/add book details
////    * @param book
////    */
////  def addBookData(chanel: CON): Unit ={
////
////    //getting user inputs
////    //      var name: String = scala.io.StdIn.readLine("Please enter book name  : ")
////    //      var author: String = scala.io.StdIn.readLine("Please enter author's name  : ")
////    //      var price: String = scala.io.StdIn.readLine("Please enter book price  : ")
////    //      var description: String = scala.io.StdIn.readLine("Please enter book description  : ")
////
////    var name: String = "Champ"
////    var author: String = "Champ Auth"
////    var price: String = "2500"
////    var description: String = "Description"
////
////    //      //validation for price
////    //      while(!bookService.isAllDigits(price)){
////    //        price = scala.io.StdIn.readLine("Please enter number for price : ")
////    //      }
////    // creating book object and publishing
////    var  book: Book = new Book(1,name,author,price.toInt,description)
////
////    println();
////    println();
////
////    bookList += book
////
////    // publishing
////    for(book: Book <- bookList){
////      val bookJson = gson.toJson(book)
////      channel.basicPublish(exchange, BOOK_CRUD_QUEUE, null, bookJson.getBytes())
////      println(s"sent book details by Sender : $bookJson")
////      println()
////    }
////  }
//
//  while(true) {
//    Thread.sleep(1000)
//  }
//
//  channel.close()
//  connection.close()
//
//
//
//}
