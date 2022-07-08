package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.ApplyType

trait ApplyTypeTraverser extends ScalaTreeTraverser[ApplyType]

private[scala2java] class ApplyTypeTraverserImpl(typeTraverser: => TypeTraverser,
                                                 termTraverser: => TermTraverser,
                                                 typeListTraverser: => TypeListTraverser)
                                                (implicit javaWriter: JavaWriter) extends ApplyTypeTraverser {

  import javaWriter._

  // parametrized type application, e.g.: classOf[X], identity[X]
  override def traverse(termApplyType: ApplyType): Unit = {
    termApplyType.fun match {
      case Term.Name("classOf") =>
        termApplyType.targs match {
          case arg :: _ =>
            typeTraverser.traverse(arg)
            write(".class")
          case _ => write(s"UNPARSEABLE class type: $termApplyType")
        }
      case fun =>
        termTraverser.traverse(fun)
        if (termApplyType.targs.nonEmpty) {
          write(".")
        }
        typeListTraverser.traverse(termApplyType.targs)
    }
  }
}

object ApplyTypeTraverser extends ApplyTypeTraverserImpl(TypeTraverser, TermTraverser, TypeListTraverser)
