package io.github.effiban.scala2java.core.writers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.{EnclosingDelimiter, _}
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}

import java.io.Writer

trait JavaWriter {

  def writeTypeDeclaration(modifiers: List[JavaModifier], typeKeyword: JavaKeyword, name: String): Unit

  def writeNamedType(typeKeyword: JavaKeyword, name: String): Unit
  def writeModifiers(modifiers: List[JavaModifier]): Unit

  def writeKeyword(keyword: JavaKeyword): Unit

  def writeStatementEnd(): Unit

  def writeArrow(): Unit

  def writeBlockStart(): Unit

  def writeBlockEnd(): Unit

  def writeArgumentsStart(delim: EnclosingDelimiter): Unit

  def writeArgumentsEnd(delim: EnclosingDelimiter): Unit

  def writeListSeparator(): Unit

  def writeQualifierSeparator(): Unit

  def writeComment(comment: String): Unit

  def writeLine(str: String = ""): Unit

  def writeEllipsis(): Unit

  def writeStartDelimiter(delim: EnclosingDelimiter): Unit

  def writeEndDelimiter(delim: EnclosingDelimiter): Unit

  def write(str: String): Unit

  def close(): Unit = {}
}

class JavaWriterImpl(writer: Writer) extends JavaWriter {
  private final val indentationLength = 4
  private var indentationLevel = 0
  private var indentationRequired = false

  override def writeTypeDeclaration(modifiers: List[JavaModifier], typeKeyword: JavaKeyword, name: String): Unit = {
    writeModifiers(modifiers)
    writeNamedType(typeKeyword, name)
  }

  override def writeNamedType(typeKeyword: JavaKeyword, name: String): Unit = {
    write(s"${typeKeyword.name} $name")
  }

  override def writeModifiers(modifiers: List[JavaModifier]): Unit = {
    if (modifiers.nonEmpty) {
      write(modifiers.map(_.name).mkString(" "))
      write(" ")
    }
  }

  override def writeKeyword(keyword: JavaKeyword): Unit = {
    write(keyword.name)
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

  override def writeQualifierSeparator(): Unit = {
    write(".")
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

  private def writeLineBreak(): Unit = {
    write("\n")
    indentationRequired = true
  }

  override def write(str: String): Unit = {
    if (indentationRequired) {
      if (!str.isBlank) {
        writer.write(indentation())
      }
      indentationRequired = false
    }
    writer.write(str)
  }

  override def close(): Unit = writer.close()

  private def indentation() = " " * indentationLength * indentationLevel
}
