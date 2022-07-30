package dummy

trait Sample {

  def foo(@MyAnnot(name = "myName") param1: String, param2: Int): Unit
}