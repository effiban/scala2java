package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeTraverser extends ScalaTreeTraverser1[Type]

private[traversers] class TypeTraverserImpl(typeRefTraverser: => TypeRefTraverser,
                                            typeApplyTraverser: => TypeApplyTraverser,
                                            typeApplyInfixTraverser: => TypeApplyInfixTraverser,
                                            typeFunctionTraverser: => TypeFunctionTraverser,
                                            typeTupleTraverser: => TypeTupleTraverser,
                                            typeWithTraverser: => TypeWithTraverser,
                                            typeRefineTraverser: => TypeRefineTraverser,
                                            typeExistentialTraverser: => TypeExistentialTraverser,
                                            typeAnnotateTraverser: => TypeAnnotateTraverser,
                                            typeWildcardTraverser: => TypeWildcardTraverser,
                                            typeByNameTraverser: => TypeByNameTraverser,
                                            typeRepeatedTraverser: => TypeRepeatedTraverser) extends TypeTraverser {

  override def traverse(`type`: Type): Type = `type` match {
    case typeRef: Type.Ref => typeRefTraverser.traverse(typeRef)
    case typeApply: Type.Apply => typeApplyTraverser.traverse(typeApply)
    case typeApplyInfix: Type.ApplyInfix => typeApplyInfixTraverser.traverse(typeApplyInfix)
    case functionType: Type.Function => typeFunctionTraverser.traverse(functionType)
    case tupleType: Type.Tuple => typeTupleTraverser.traverse(tupleType)
    case withType: Type.With => typeWithTraverser.traverse(withType)
    case typeRefine: Type.Refine => typeRefineTraverser.traverse(typeRefine)
    case existentialType: Type.Existential => typeExistentialTraverser.traverse(existentialType)
    case typeAnnotation: Type.Annotate => typeAnnotateTraverser.traverse(typeAnnotation)
    case wildcardType: Type.Wildcard => typeWildcardTraverser.traverse(wildcardType)
    case byNameType: Type.ByName => typeByNameTraverser.traverse(byNameType)
    case repeatedType: Type.Repeated => typeRepeatedTraverser.traverse(repeatedType)
    case aType => aType
  }
}
