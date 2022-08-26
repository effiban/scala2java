package effiban.scala2java.orderings

trait JavaModifierOrdering extends Ordering[String]

object JavaModifierOrdering extends JavaModifierOrdering {

  private final val JavaModifierNamePosition = Map(
    "private" -> 0,
    "protected" -> 0,
    "public" -> 0,
    "default" -> 0,
    "static" -> 1,
    "sealed" -> 2,
    "abstract" -> 3,
    "final" -> 4
  )

  override def compare(modifierName1: String, modifierName2: String): Int = positionOf(modifierName1) - positionOf(modifierName2)

  private def positionOf(modifierName: String) = JavaModifierNamePosition.getOrElse(modifierName, Int.MaxValue)
}
