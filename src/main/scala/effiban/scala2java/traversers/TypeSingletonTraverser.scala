package effiban.scala2java.traversers

import effiban.scala2java.transformers.TypeSingletonToTermTransformer

import scala.meta.Type

trait TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton]

private[traversers] class TypeSingletonTraverserImpl(termTraverser: => TermTraverser,
                                                     typeSingletonToTermTransformer: TypeSingletonToTermTransformer)
  extends TypeSingletonTraverser {

  override def traverse(singletonType: Type.Singleton): Unit = {
    termTraverser.traverse(typeSingletonToTermTransformer.transform(singletonType))
  }
}

object TypeSingletonTraverser extends TypeSingletonTraverserImpl(TermTraverser, TypeSingletonToTermTransformer)