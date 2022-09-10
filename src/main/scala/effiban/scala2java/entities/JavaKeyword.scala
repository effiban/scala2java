package effiban.scala2java.entities

sealed abstract class JavaKeyword(val name: String)

object JavaKeyword {

  case object Class extends JavaKeyword("class")
  case object Extends extends JavaKeyword("extends")
  case object Implements extends JavaKeyword("implements")
  case object Interface extends JavaKeyword("interface")
  case object Package extends JavaKeyword("package")
  case object Record extends JavaKeyword("record")

  case object NoKeyword extends JavaKeyword("")
}
