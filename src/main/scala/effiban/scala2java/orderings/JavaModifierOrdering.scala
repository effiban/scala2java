package effiban.scala2java.orderings

import effiban.scala2java.entities.JavaModifier

trait JavaModifierOrdering extends Ordering[JavaModifier]

object JavaModifierOrdering extends JavaModifierOrdering {

  private final val JavaModifierToPosition: Map[JavaModifier, Int] = Map(
    JavaModifier.Private -> 0,
    JavaModifier.Protected -> 0,
    JavaModifier.Public -> 0,
    JavaModifier.Default -> 0,
    JavaModifier.Static -> 1,
    JavaModifier.Abstract -> 2,
    JavaModifier.Sealed -> 3,
    JavaModifier.NonSealed -> 3,
    JavaModifier.Final -> 3
  )

  override def compare(modifier1: JavaModifier, modifier2: JavaModifier): Int = positionOf(modifier1) - positionOf(modifier2)

  private def positionOf(modifier: JavaModifier) = JavaModifierToPosition.getOrElse(modifier, Int.MaxValue)
}
