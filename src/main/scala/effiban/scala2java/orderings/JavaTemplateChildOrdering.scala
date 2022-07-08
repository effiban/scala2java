package effiban.scala2java.orderings

import scala.meta.{Ctor, Decl, Defn, Tree}

trait JavaTemplateChildOrdering extends Ordering[Tree]

object JavaTemplateChildOrdering extends JavaTemplateChildOrdering {
  private val ChildOrder: List[Class[_ <: Tree]] = List[Class[_ <: Tree]](
    classOf[Defn.Val],
    classOf[Decl.Val],
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
      .filter { case (childType, _) => child.getClass == childType }
      .map { case (_, idx) => idx }
      .headOption
      .getOrElse(Int.MaxValue)
  }
}
