package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.TypeByNameToSupplierTypeTransformer

import scala.meta.Type

trait TypeByNameTraverser extends ScalaTreeTraverser2[Type.ByName, Type.Apply]

private[traversers] class TypeByNameTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                  typeByNameToSupplierTypeTransformer: TypeByNameToSupplierTypeTransformer)
  extends TypeByNameTraverser {

  // Type by name, e.g.: =>T in f(x: => T)
  override def traverse(typeByName: Type.ByName): Type.Apply = {
    typeApplyTraverser.traverse(typeByNameToSupplierTypeTransformer.transform(typeByName))
  }
}
