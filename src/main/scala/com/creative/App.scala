package com.creative

import bookstore.model.Book

/**
 * @author ${user.name}
 */
object App {

  def main(args : Array[String]) {
    println( "Welcome to the Book Store\n" )

    println( "Please enter book data according to the below mentioned properties \n" )
    println( "1.Name \n" )
    println( "2.Author \n" )
    println( "3.Price \n" )
    println( "4.Description \n" )

    // creating book object
    val name = args(0);
    val author = args(1);
    val price = args(2).toInt;
    val description = args(3);

    // creating book object and publishing
    val  book: Book = new Book(name,author,price,description)
    Sender.runSender(book);





  }

}
