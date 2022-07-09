package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeWithTraverser extends ScalaTreeTraverser[Type.With]

private[traversers] class TypeWithTraverserImpl(typeTraverser: => TypeTraverser)
                                               (implicit javaWriter: JavaWriter) extends TypeWithTraverser {

  import javaWriter._

  // type with parent, e.g. 'A with B' in: type X = A with B
  // approximated by Java "extends" but might not compile
  override def traverse(typeWith: Type.With): Unit = {
    typeTraverser.traverse(typeWith.lhs)
    write(" extends ")
    typeTraverser.traverse(typeWith.rhs)
  }
}

object TypeWithTraverser extends TypeWithTraverserImpl(TypeTraverser)