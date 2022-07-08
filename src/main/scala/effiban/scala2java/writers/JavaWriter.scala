package effiban.scala2java.writers

import effiban.scala2java.entities.EnclosingDelimiter.{EnclosingDelimiter, _}

trait JavaWriter {

  def writeTypeDeclaration(modifiers: List[String], typeKeyword: String, name: String): Unit

  def writeModifiers(modifiers: List[String]): Unit

  def writeInheritanceKeyword(): Unit

  def writeStatementEnd(): Unit

  def writeArrow(): Unit

  def writeBlockStart(): Unit

  def writeBlockEnd(): Unit

  def writeArgumentsStart(delim: EnclosingDelimiter): Unit

  def writeArgumentsEnd(delim: EnclosingDelimiter): Unit

  def writeListSeparator(): Unit

  def writeComment(comment: String): Unit

  def writeLine(str: String = ""): Unit

  def writeEllipsis(): Unit

  def writeStartDelimiter(delim: EnclosingDelimiter): Unit

  def writeEndDelimiter(delim: EnclosingDelimiter): Unit

  def write(str: String): Unit
}

private[scala2java] class JavaWriterImpl extends JavaWriter {
  var indentationLevel = 0
  var indentationRequired = false

  override def writeTypeDeclaration(modifiers: List[String], typeKeyword: String, name: String): Unit = {
    writeModifiers(modifiers)
    write(s"$typeKeyword $name")
  }

  override def writeModifiers(modifiers: List[String]): Unit = {
    write(modifiers.mkString(" "))
    if (modifiers.nonEmpty) {
      write(" ")
    }
  }

  override def writeInheritanceKeyword(): Unit = {
    //TODO - fix, handle class vs. interface
    write(s" implements ")
  }

  override def writeStatementEnd(): Unit = {
    write(";")
    writeLineBreak()
  }

  override def writeArrow(): Unit = {
    write(" -> ")
  }

  override def writeBlockStart(): Unit = {
    write(" ")
    writeStartDelimiter(CurlyBrace)
    writeLineBreak()
    indentationLevel += 1
  }

  override def writeBlockEnd(): Unit = {
    indentationLevel -= 1
    writeEndDelimiter(CurlyBrace)
    writeLineBreak()
  }

  override def writeArgumentsStart(delim: EnclosingDelimiter): Unit = {
    writeStartDelimiter(delim)
    indentationLevel += 1
  }

  override def writeArgumentsEnd(delim: EnclosingDelimiter): Unit = {
    writeEndDelimiter(delim)
    indentationLevel -= 1
  }

  override def writeListSeparator(): Unit = {
    write(",")
  }

  override def writeComment(comment: String): Unit = {
    if (comment.contains("\n")) {
      val commentLines = comment.split("\n")
      writeLine("/**")
      commentLines.foreach(commentLine => writeLine(s"* $commentLine"))
      writeLine("*/")
    } else {
      write(s"/* $comment */")
    }
  }

  override def writeLine(str: String = ""): Unit = {
    write(str)
    writeLineBreak()
  }

  override def writeEllipsis(): Unit = {
    write("...")
  }

  override def writeStartDelimiter(delim: EnclosingDelimiter): Unit = {
    val delimStr = delim match {
      case Parentheses => "("
      case SquareBracket => "["
      case CurlyBrace => "{"
      case AngleBracket => "<"
    }
    write(delimStr)
  }

  override def writeEndDelimiter(delim: EnclosingDelimiter): Unit = {
    val delimStr = delim match {
      case Parentheses => ")"
      case SquareBracket => "]"
      case CurlyBrace => "}"
      case AngleBracket => ">"
    }
    write(delimStr)
  }

  override def write(str: String): Unit = {
    if (indentationRequired) {
      print(indentation())
      indentationRequired = false
    }
    print(str)
  }

  private def writeLineBreak(): Unit = {
    write("\n")
    indentationRequired = true
  }

  private def indentation() = "\t" * indentationLevel
}

object JavaWriter extends JavaWriterImpl
