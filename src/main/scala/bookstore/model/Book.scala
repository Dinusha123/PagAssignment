package bookstore.model

class Book {

  private var _name: String = _
  private var _author: String = _
  private var _price: Double = _
  private var _description: String = _

  //getter methods
  def name: String = _name
  def author: String = _author
  def price: Double = _price
  def description: String = _description

  //setter methods
  def name_= (name: String){
    _name = name
  }

  def author_= (author: String){
    _author = author
  }

  def price_= (price: Double){
    _price = price
  }

  def description_= (description: String){
    _description = description
  }


  //constructor
  def this( name: String,  author: String,  price: Double,  description: String){
    this()
    this._name = name
    this._author = author
    this._price = price
    this._description = description
  }
}
