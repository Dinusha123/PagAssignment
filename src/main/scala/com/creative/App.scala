package com.creative

import bookstore.model.Book

/**
 * @author ${user.name}
 */
object App {

  def main(args : Array[String]) {
    println( "Welcome to the Book Store" )
    // You pass any thing at runtime
    // that will be print on the console
    for(arg<-args)
    {
      println(arg);

      val  book: Book = new Book(200,arg,"Author Sample"+arg,2500,"Sample Description "+arg)
      Sender.runSender(book);
    }
  }

}
