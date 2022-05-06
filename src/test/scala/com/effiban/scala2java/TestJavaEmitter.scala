package com.effiban.scala2java

import java.io.StringWriter

class TestJavaEmitter(sw: StringWriter) extends JavaEmitter {
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
    emit(s" extends/implements ")
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
  }

  override def emitBlockEnd(): Unit = {
    emitLine("}")
  }

  override def emitArgumentsStart(delimType: DualDelimiterType): Unit = {
    emitStartDelimiter(delimType)
  }

  override def emitArgumentsEnd(delimType: DualDelimiterType): Unit = {
    emitEndDelimiter(delimType)
  }

  override def emitListSeparator(): Unit = {
    emit(",")
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

  override def emitStartDelimiter(delimType: DualDelimiterType): Unit = {
    val delimStr = delimType match {
      case Parentheses => "("
      case SquareBracket => "["
      case CurlyBrace => "{"
      case AngleBracket => "<"
    }
    emit(delimStr)
  }

  override def emitEndDelimiter(delimType: DualDelimiterType): Unit = {
    val delimStr = delimType match {
      case Parentheses => ")"
      case SquareBracket => "]"
      case CurlyBrace => "}"
      case AngleBracket => ">"
    }
    emit(delimStr)
  }

  override def emit(str: String): Unit = {
    sw.write(str)
  }

  private def emitLineBreak(): Unit = {
    emit("\n")
  }
}
