package io.github.effiban.scala2java.core.entities

sealed abstract class JavaModifier(val name: String)

object JavaModifier {
  case object Abstract extends JavaModifier("abstract")
  case object Default extends JavaModifier("default")
  case object Final extends JavaModifier("final")
  case object NonSealed extends JavaModifier("non-sealed")
  case object Private extends JavaModifier("private")
  case object Protected extends JavaModifier("protected")
  case object Public extends JavaModifier("public")
  case object Sealed extends JavaModifier("sealed")
  case object Static extends JavaModifier("static")
}
