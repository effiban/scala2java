package io.github.effiban.scala2java.core.traversers

import scala.meta.{Type, XtensionQuasiquoteType}

trait TypeApplyTraverser extends ScalaTreeTraverser1[Type.Apply]

private[traversers] class TypeApplyTraverserImpl(typeTraverser: => TypeTraverser) extends TypeApplyTraverser {

  // type definition with generic args, e.g. F[T]
  override def traverse(typeApply: Type.Apply): Type.Apply = {
    typeApply.tpe match {
      case t"scala.Array" => traverseArrayType(typeApply)
      case _ =>
        val traversedType = typeTraverser.traverse(typeApply.tpe)
        val traversedArgs = typeApply.args.map(typeTraverser.traverse)
        Type.Apply(traversedType, traversedArgs)
    }
  }

  private def traverseArrayType(arrayTypeApply: Type.Apply): Type.Apply = {
    arrayTypeApply.args match {
      case Nil => throw new IllegalStateException(s"A Type.Apply must have at least one type argument")
      case arg :: Nil => arrayTypeApply.copy(args = List(typeTraverser.traverse(arg)))
      case args => throw new IllegalStateException(s"An Array type must have one type argument, but ${args.length} found")
    }
  }
}
