package com.effiban.scala2java

object JavaEmitter {
  var indentationLevel = 0
  var indentationRequired = false

  sealed trait DualDelimiterType
  case object Parentheses extends DualDelimiterType
  case object SquareBracket extends DualDelimiterType
  case object CurlyBrace extends DualDelimiterType
  case object AngleBracket extends DualDelimiterType

  def emitTypeDeclaration(modifiers: List[String], typeKeyword: String, name: String): Unit = {
    emitModifiers(modifiers)
    emit(s"$typeKeyword $name")
  }

  def emitModifiers(modifiers: List[String]): Unit = {
    emit(modifiers.mkString(" "))
    if (modifiers.nonEmpty) {
      emit(" ")
    }
  }

  def emitParentNamesPrefix(): Unit = {
    // TODO - fix, handle class vs. interface
    emit(s" implements ")
  }

  def emitStatementEnd(): Unit = {
    emit(";")
    emitLineBreak()
  }

  def emitArrow(): Unit = {
    emit(" -> ")
  }

  def emitBlockStart(): Unit = {
    emitLine(" {")
    indentationLevel += 1
  }

  def emitBlockEnd(): Unit = {
    indentationLevel -= 1
    emitLine("}")
  }

  def emitArgumentsStart(delimType: DualDelimiterType): Unit = {
    emitStartDelimiter(delimType)
    indentationLevel += 1
  }

  def emitArgumentsEnd(delimType: DualDelimiterType): Unit = {
    emitEndDelimiter(delimType)
    indentationLevel -= 1
  }

  def emitListSeparator(): Unit = {
    emit(", ")
  }

  def emitComment(comment: String): Unit = {
    emit(s"/* $comment */")
  }

  def emitLine(str: String = ""): Unit = {
    emit(str)
    emitLineBreak()
  }

  def emitEllipsis(): Unit = {
    emit("...")
  }

  def emit(str: String): Unit = {
    if (indentationRequired) {
      print(indentation())
      indentationRequired = false
    }
    print(str)
  }

  private def emitLineBreak(): Unit = {
    emit("\n")
    indentationRequired = true
  }

  private def emitStartDelimiter(delimType: DualDelimiterType): Unit = {
    val delimStr = delimType match {
      case Parentheses => "("
      case SquareBracket => "["
      case CurlyBrace => "{"
      case AngleBracket => "<"
    }
    emit(delimStr)
  }

  private def emitEndDelimiter(delimType: DualDelimiterType): Unit = {
    val delimStr = delimType match {
      case Parentheses => ")"
      case SquareBracket => "]"
      case CurlyBrace => "}"
      case AngleBracket => ">"
    }
    emit(delimStr)
  }

  private def indentation() = "\t" * indentationLevel
}
