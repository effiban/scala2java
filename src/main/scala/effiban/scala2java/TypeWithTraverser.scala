package effiban.scala2java

import scala.meta.Type

trait TypeWithTraverser extends ScalaTreeTraverser[Type.With]

private[scala2java] class TypeWithTraverserImpl(typeTraverser: => TypeTraverser)
                                               (implicit javaEmitter: JavaEmitter) extends TypeWithTraverser {

  import javaEmitter._

  // type with parent, e.g. 'A with B' in: type X = A with B
  // approximated by Java "extends" but might not compile
  override def traverse(typeWith: Type.With): Unit = {
    typeTraverser.traverse(typeWith.lhs)
    emit(" extends ")
    typeTraverser.traverse(typeWith.rhs)
  }
}

object TypeWithTraverser extends TypeWithTraverserImpl(TypeTraverser)