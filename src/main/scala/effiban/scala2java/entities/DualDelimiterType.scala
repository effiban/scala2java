package effiban.scala2java.entities

sealed trait DualDelimiterType

case object Parentheses extends DualDelimiterType

case object SquareBracket extends DualDelimiterType

case object CurlyBrace extends DualDelimiterType

case object AngleBracket extends DualDelimiterType


