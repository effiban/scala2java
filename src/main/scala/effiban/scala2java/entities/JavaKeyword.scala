package effiban.scala2java.entities

sealed abstract class JavaKeyword(val name: String)

object JavaKeyword {

  case object Class extends JavaKeyword("class")
  case object Enum extends JavaKeyword("enum")
  case object Extends extends JavaKeyword("extends")
  case object Implements extends JavaKeyword("implements")
  case object Interface extends JavaKeyword("interface")
  case object New extends JavaKeyword("new")
  case object Package extends JavaKeyword("package")
  case object Permits extends JavaKeyword("permits")
  case object Record extends JavaKeyword("record")

  case object NoKeyword extends JavaKeyword("")
}
