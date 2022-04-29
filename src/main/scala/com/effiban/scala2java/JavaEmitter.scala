package com.effiban.scala2java

trait JavaEmitter {

  def emitTypeDeclaration(modifiers: List[String], typeKeyword: String, name: String): Unit

  def emitModifiers(modifiers: List[String]): Unit

  def emitParentNamesPrefix(): Unit

  def emitStatementEnd(): Unit

  def emitArrow(): Unit

  def emitBlockStart(): Unit

  def emitBlockEnd(): Unit

  def emitArgumentsStart(delimType: DualDelimiterType): Unit

  def emitArgumentsEnd(delimType: DualDelimiterType): Unit

  def emitListSeparator(): Unit

  def emitComment(comment: String): Unit

  def emitLine(str: String = ""): Unit

  def emitEllipsis(): Unit

  def emit(str: String): Unit
}

object JavaEmitter extends JavaEmitter {
  var indentationLevel = 0
  var indentationRequired = false

  override def emitTypeDeclaration(modifiers: List[String], typeKeyword: String, name: String): Unit = {
    emitModifiers(modifiers)
    emit(s"$typeKeyword $name")
  }

  override def emitModifiers(modifiers: List[String]): Unit = {
    emit(modifiers.mkString(" "))
    if (modifiers.nonEmpty) {
      emit(" ")
    }
  }

  override def emitParentNamesPrefix(): Unit = {
    // TODO - fix, handle class vs. interface
    emit(s" implements ")
  }

  override def emitStatementEnd(): Unit = {
    emit(";")
    emitLineBreak()
  }

  override def emitArrow(): Unit = {
    emit(" -> ")
  }

  override def emitBlockStart(): Unit = {
    emitLine(" {")
    indentationLevel += 1
  }

  def emitBlockEnd(): Unit = {
    indentationLevel -= 1
    emitLine("}")
  }

  override def emitArgumentsStart(delimType: DualDelimiterType): Unit = {
    emitStartDelimiter(delimType)
    indentationLevel += 1
  }

  override def emitArgumentsEnd(delimType: DualDelimiterType): Unit = {
    emitEndDelimiter(delimType)
    indentationLevel -= 1
  }

  override def emitListSeparator(): Unit = {
    emit(", ")
  }

  override def emitComment(comment: String): Unit = {
    emit(s"/* $comment */")
  }

  override def emitLine(str: String = ""): Unit = {
    emit(str)
    emitLineBreak()
  }

  override def emitEllipsis(): Unit = {
    emit("...")
  }

  override def emit(str: String): Unit = {
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
