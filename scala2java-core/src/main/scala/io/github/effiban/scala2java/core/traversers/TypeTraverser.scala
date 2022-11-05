package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeTraverser extends ScalaTreeTraverser[Type]

private[traversers] class TypeTraverserImpl(typeRefTraverser: => TypeRefTraverser,
                                            typeApplyTraverser: => TypeApplyTraverser,
                                            typeApplyInfixTraverser: => TypeApplyInfixTraverser,
                                            typeFunctionTraverser: => TypeFunctionTraverser,
                                            typeTupleTraverser: => TypeTupleTraverser,
                                            typeWithTraverser: => TypeWithTraverser,
                                            typeRefineTraverser: => TypeRefineTraverser,
                                            typeExistentialTraverser: => TypeExistentialTraverser,
                                            typeAnnotateTraverser: => TypeAnnotateTraverser,
                                            typeLambdaTraverser: => TypeLambdaTraverser,
                                            typeAnonymousParamTraverser: => TypeAnonymousParamTraverser,
                                            typeWildcardTraverser: => TypeWildcardTraverser,
                                            typeByNameTraverser: => TypeByNameTraverser,
                                            typeRepeatedTraverser: => TypeRepeatedTraverser,
                                            typeVarTraverser: => TypeVarTraverser)
                                           (implicit javaWriter: JavaWriter) extends TypeTraverser {

  import javaWriter._

  override def traverse(`type`: Type): Unit = `type` match {
    case typeRef: Type.Ref => typeRefTraverser.traverse(typeRef)
    case typeApply: Type.Apply => typeApplyTraverser.traverse(typeApply)
    case typeApplyInfix: Type.ApplyInfix => typeApplyInfixTraverser.traverse(typeApplyInfix)
    case functionType: Type.Function => typeFunctionTraverser.traverse(functionType)
    case tupleType: Type.Tuple => typeTupleTraverser.traverse(tupleType)
    case withType: Type.With => typeWithTraverser.traverse(withType)
    case typeRefine: Type.Refine => typeRefineTraverser.traverse(typeRefine)
    case existentialType: Type.Existential => typeExistentialTraverser.traverse(existentialType)
    case typeAnnotation: Type.Annotate => typeAnnotateTraverser.traverse(typeAnnotation)
    case lambdaType: Type.Lambda => typeLambdaTraverser.traverse(lambdaType)
    case anonymousParamType: Type.AnonymousParam => typeAnonymousParamTraverser.traverse(anonymousParamType)
    case wildcardType: Type.Wildcard => typeWildcardTraverser.traverse(wildcardType)
    case byNameType: Type.ByName => typeByNameTraverser.traverse(byNameType)
    case repeatedType: Type.Repeated => typeRepeatedTraverser.traverse(repeatedType)
    case typeVar: Type.Var => typeVarTraverser.traverse(typeVar)
    case _ => writeComment(s"UNSUPPORTED: ${`type`}")
  }
}
