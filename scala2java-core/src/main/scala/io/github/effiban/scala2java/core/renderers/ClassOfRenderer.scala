package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

// A Renderer for the special expression 'classOf[T]'
trait ClassOfRenderer extends JavaTreeRenderer[Term.ApplyType]

private[renderers] class ClassOfRendererImpl(typeRenderer: => TypeRenderer)
                                            (implicit javaWriter: JavaWriter) extends ClassOfRenderer {
  import javaWriter._

  override def render(classOfType: Term.ApplyType): Unit =
    classOfType.targs match {
      case targ :: Nil =>
        typeRenderer.render(targ)
        write(".class")
      case _ => write(s"UNPARSEABLE 'classOf' with types: ${if (classOfType.targs.nonEmpty) {classOfType.targs.mkString(", ")} else "(none)"}")
  }
}