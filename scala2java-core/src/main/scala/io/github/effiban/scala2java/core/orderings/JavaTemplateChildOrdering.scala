package io.github.effiban.scala2java.core.orderings

import scala.meta.{Ctor, Decl, Defn, Import, Tree}

trait JavaTemplateChildOrdering extends Ordering[Tree]

object JavaTemplateChildOrdering extends JavaTemplateChildOrdering {
  private val ChildOrder: List[Class[_ <: Tree]] = List[Class[_ <: Tree]](
    classOf[Import],
    classOf[Defn.Val],
    classOf[Defn.Var],
    classOf[Decl.Val],
    classOf[Decl.Var],
    classOf[Ctor.Primary],
    classOf[Ctor.Secondary],
    classOf[Defn.Def],
    classOf[Defn.Type],
    classOf[Decl.Def],
    classOf[Decl.Type]
  )

  override def compare(child1: Tree, child2: Tree): Int = positionOf(child1) - positionOf(child2)

  private def positionOf(child: Tree) = {
    ChildOrder.zipWithIndex
      .filter { case (childType, _) => childType.isAssignableFrom(child.getClass) }
      .map { case (_, idx) => idx }
      .headOption
      .getOrElse(Int.MaxValue)
  }
}
