package effiban.scala2java.entities

sealed abstract class JavaKeyword(val name: String)

object JavaKeyword {
  case object Extends extends JavaKeyword("extends")
  case object Implements extends JavaKeyword("implements")
}
