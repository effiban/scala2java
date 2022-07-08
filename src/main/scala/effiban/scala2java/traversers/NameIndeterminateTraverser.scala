package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Name

trait NameIndeterminateTraverser extends ScalaTreeTraverser[Name.Indeterminate]

class NameIndeterminateTraverserImpl(implicit javaEmitter: JavaEmitter) extends NameIndeterminateTraverser {

  import javaEmitter._

  // Name that cannot be distinguished between a term and a type (for example, name in an "import" clause)
  // Since it cannot be distinguished, we can assume that the name should be unchanged from Scala (there is no rule to convert by)
  override def traverse(indeterminateName: Name.Indeterminate): Unit = {
    emit(indeterminateName.value)
  }
}

object NameIndeterminateTraverser extends NameIndeterminateTraverserImpl()
