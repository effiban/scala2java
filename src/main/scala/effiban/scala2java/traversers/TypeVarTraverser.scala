package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeVarTraverser extends ScalaTreeTraverser[Type.Var]

private[scala2java] class TypeVarTraverserImpl(implicit javaWriter: JavaWriter) extends TypeVarTraverser {

  import javaWriter._

  // Variable in type, e.g.: `t` in case _:List(t) =>
  // Unsupported in Java and no replacement I can think of
  override def traverse(typeVar: Type.Var): Unit = {
    writeComment(typeVar.toString())
  }
}

object TypeVarTraverser extends TypeVarTraverserImpl
