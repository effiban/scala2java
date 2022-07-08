package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Type

trait TypeVarTraverser extends ScalaTreeTraverser[Type.Var]

private[scala2java] class TypeVarTraverserImpl(implicit javaEmitter: JavaEmitter) extends TypeVarTraverser {

  import javaEmitter._

  // Variable in type, e.g.: `t` in case _:List(t) =>
  // Unsupported in Java and no replacement I can think of
  override def traverse(typeVar: Type.Var): Unit = {
    emitComment(typeVar.toString())
  }
}

object TypeVarTraverser extends TypeVarTraverserImpl
