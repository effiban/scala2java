package effiban.scala2java

import effiban.scala2java.entities.EnclosingDelimiter.{EnclosingDelimiter, _}

trait JavaEmitter {

  def emitTypeDeclaration(modifiers: List[String], typeKeyword: String, name: String): Unit

  def emitModifiers(modifiers: List[String]): Unit

  def emitInheritanceKeyword(): Unit

  def emitStatementEnd(): Unit

  def emitArrow(): Unit

  def emitBlockStart(): Unit

  def emitBlockEnd(): Unit

  def emitArgumentsStart(delim: EnclosingDelimiter): Unit

  def emitArgumentsEnd(delim: EnclosingDelimiter): Unit

  def emitListSeparator(): Unit

  def emitComment(comment: String): Unit

  def emitLine(str: String = ""): Unit

  def emitEllipsis(): Unit

  def emitStartDelimiter(delim: EnclosingDelimiter): Unit

  def emitEndDelimiter(delim: EnclosingDelimiter): Unit

  def emit(str: String): Unit
}

private[scala2java] class JavaEmitterImpl extends JavaEmitter {
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

  override def emitInheritanceKeyword(): Unit = {
    //TODO - fix, handle class vs. interface
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
    emit(" ")
    emitStartDelimiter(CurlyBrace)
    emitLineBreak()
    indentationLevel += 1
  }

  override def emitBlockEnd(): Unit = {
    indentationLevel -= 1
    emitEndDelimiter(CurlyBrace)
    emitLineBreak()
  }

  override def emitArgumentsStart(delim: EnclosingDelimiter): Unit = {
    emitStartDelimiter(delim)
    indentationLevel += 1
  }

  override def emitArgumentsEnd(delim: EnclosingDelimiter): Unit = {
    emitEndDelimiter(delim)
    indentationLevel -= 1
  }

  override def emitListSeparator(): Unit = {
    emit(",")
  }

  override def emitComment(comment: String): Unit = {
    if (comment.contains("\n")) {
      val commentLines = comment.split("\n")
      emitLine("/**")
      commentLines.foreach(commentLine => emitLine(s"* $commentLine"))
      emitLine("*/")
    } else {
      emit(s"/* $comment */")
    }
  }

  override def emitLine(str: String = ""): Unit = {
    emit(str)
    emitLineBreak()
  }

  override def emitEllipsis(): Unit = {
    emit("...")
  }

  override def emitStartDelimiter(delim: EnclosingDelimiter): Unit = {
    val delimStr = delim match {
      case Parentheses => "("
      case SquareBracket => "["
      case CurlyBrace => "{"
      case AngleBracket => "<"
    }
    emit(delimStr)
  }

  override def emitEndDelimiter(delim: EnclosingDelimiter): Unit = {
    val delimStr = delim match {
      case Parentheses => ")"
      case SquareBracket => "]"
      case CurlyBrace => "}"
      case AngleBracket => ">"
    }
    emit(delimStr)
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

  private def indentation() = "\t" * indentationLevel
}

object JavaEmitter extends JavaEmitterImpl
