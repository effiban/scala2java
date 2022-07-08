package effiban.scala2java

import scala.meta.Type

trait TypeAnnotateTraverser extends ScalaTreeTraverser[Type.Annotate]

private[scala2java] class TypeAnnotateTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeTraverser: => TypeTraverser)
                                                   (implicit javaEmitter: JavaEmitter) extends TypeAnnotateTraverser {

  import javaEmitter._

  // type with annotation, e.g.: T @annot
  override def traverse(annotatedType: Type.Annotate): Unit = {
    annotListTraverser.traverseAnnotations(annotations = annotatedType.annots, onSameLine = true)
    emit(" ")
    typeTraverser.traverse(annotatedType.tpe)
  }
}

object TypeAnnotateTraverser extends TypeAnnotateTraverserImpl(AnnotListTraverser, TypeTraverser)
