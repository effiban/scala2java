package com.effiban.scala2java.stubs

import com.effiban.scala2java.{InitTraverser, JavaEmitter}

import scala.meta.Term.This
import scala.meta.{Init, Name, Type}

class StubInitTraverser(implicit javaEmitter: JavaEmitter) extends InitTraverser {
  import javaEmitter._

  override def traverse(init: Init): Unit = {
    val tpe = init.tpe match {
      case Type.Singleton(This(Name.Anonymous())) => "this"
      case tpe => tpe.toString()
    }
    val name = init.name match {
      case Name.Anonymous() => ""
      case nm => s" $nm"
    }
    emit(s"$tpe$name(${init.argss.flatten.mkString(", ")})")
  }
}
