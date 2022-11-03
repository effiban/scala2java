package io.github.effiban.scala2java.testwriters

import io.github.effiban.scala2java.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.writers.JavaWriter

import java.io.StringWriter

class TestJavaWriter(sw: StringWriter) extends JavaWriter {
  override def writeTypeDeclaration(modifiers: List[JavaModifier], typeKeyword: JavaKeyword, name: String): Unit = {
    writeModifiers(modifiers)
    writeNamedType(typeKeyword, name)
  }

  override def writeNamedType(typeKeyword: JavaKeyword, name: String): Unit = {
    write(s"${typeKeyword.name} $name")
  }

  override def writeModifiers(modifiers: List[JavaModifier]): Unit = {
    write(modifiers.map(_.name).mkString(" "))
    if (modifiers.nonEmpty) {
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
  }

  override def writeBlockEnd(): Unit = {
    writeEndDelimiter(CurlyBrace)
    writeLineBreak()
  }

  override def writeArgumentsStart(delimType: EnclosingDelimiter): Unit = {
    writeStartDelimiter(delimType)
  }

  override def writeArgumentsEnd(delimType: EnclosingDelimiter): Unit = {
    writeEndDelimiter(delimType)
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

  override def write(str: String): Unit = {
    sw.write(str)
  }

  private def writeLineBreak(): Unit = {
    write("\n")
  }
}
