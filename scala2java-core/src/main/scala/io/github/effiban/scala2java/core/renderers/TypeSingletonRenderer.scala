package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Term, Type}

trait TypeSingletonRenderer extends JavaTreeRenderer[Type.Singleton]

private[renderers] class TypeSingletonRendererImpl(thisRenderer: ThisRenderer)
                                                  (implicit javaWriter: JavaWriter) extends TypeSingletonRenderer {

  import javaWriter._

  override def render(singletonType: Type.Singleton): Unit = {
    singletonType.ref match {
      case `this`: Term.This => thisRenderer.render(`this`)
      case aRef => writeComment(s"$aRef.type")
    }
  }
}
