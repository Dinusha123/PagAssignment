package com.creative

import bookstore.model.Book

/**
  * @author ${user.name}
  */
object App {

  def main(args : Array[String]) {
    println( "*** Welcome to the Book Store ***\n" )

    optionList

    def optionList(): Unit ={
      println( "Please enter 1 to enter book details" )
      println( "Please enter 2 to exit" )

      val input: String = scala.io.StdIn.readLine("Please select the option(1 or 2) ")
      println();

      if(1 == input.toInt){
        sendBookData
      }else
      {
        println( "Exit" )
      }
    }

    //    sendBookData(name,author,price,description)
    //    def sendBookData(name:String, author:String, price:String, description:String  ): Unit ={
    def sendBookData(): Unit ={

      var name: String = scala.io.StdIn.readLine("Please enter book name  : ")
      var author: String = scala.io.StdIn.readLine("Please enter author's name  : ")
      var price: String = scala.io.StdIn.readLine("Please enter book price  : ")
      var description: String = scala.io.StdIn.readLine("Please enter book description  : ")

      while(!isAllDigits(price)){
        price = scala.io.StdIn.readLine("Please enter number for price : ")
      }
      // creating book object and publishing
      var  book: Book = new Book(name,author,price.toInt,description)
      Sender.runSender(book);

      optionList
      println();
      println();
    }

    /**
      * This method is used to check whether the
      * given string is a decimal number of not
      * @param x
      * @return boolean
      */
    def isAllDigits(x: String) = x forall Character.isDigit
  }

}
