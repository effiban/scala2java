package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{TypeAnonymousParamRenderer, TypeApplyInfixRenderer, TypeLambdaRenderer, TypeVarRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeTraverser extends ScalaTreeTraverser[Type]

private[traversers] class TypeTraverserImpl(typeRefTraverser: => TypeRefTraverser,
                                            typeApplyTraverser: => TypeApplyTraverser,
                                            typeApplyInfixTraverser: => TypeApplyInfixTraverser,
                                            typeApplyInfixRenderer: TypeApplyInfixRenderer,
                                            typeFunctionTraverser: => TypeFunctionTraverser,
                                            typeTupleTraverser: => TypeTupleTraverser,
                                            typeWithTraverser: => TypeWithTraverser,
                                            typeRefineTraverser: => TypeRefineTraverser,
                                            typeExistentialTraverser: => TypeExistentialTraverser,
                                            typeAnnotateTraverser: => TypeAnnotateTraverser,
                                            typeLambdaRenderer: TypeLambdaRenderer,
                                            typeAnonymousParamRenderer: TypeAnonymousParamRenderer,
                                            typeWildcardTraverser: => TypeWildcardTraverser,
                                            typeByNameTraverser: => TypeByNameTraverser,
                                            typeRepeatedTraverser: => TypeRepeatedTraverser,
                                            typeVarRenderer: TypeVarRenderer)
                                           (implicit javaWriter: JavaWriter) extends TypeTraverser {

  import javaWriter._

  override def traverse(`type`: Type): Unit = `type` match {
    case typeRef: Type.Ref => typeRefTraverser.traverse(typeRef)
    case typeApply: Type.Apply => typeApplyTraverser.traverse(typeApply)
    case typeApplyInfix: Type.ApplyInfix =>
      val traversedTypeApplyInfix = typeApplyInfixTraverser.traverse(typeApplyInfix)
      typeApplyInfixRenderer.render(traversedTypeApplyInfix)
    case functionType: Type.Function => typeFunctionTraverser.traverse(functionType)
    case tupleType: Type.Tuple => typeTupleTraverser.traverse(tupleType)
    case withType: Type.With => typeWithTraverser.traverse(withType)
    case typeRefine: Type.Refine => typeRefineTraverser.traverse(typeRefine)
    case existentialType: Type.Existential => typeExistentialTraverser.traverse(existentialType)
    case typeAnnotation: Type.Annotate => typeAnnotateTraverser.traverse(typeAnnotation)
    case lambdaType: Type.Lambda =>
      typeLambdaRenderer.render(lambdaType)
    case anonymousParamType: Type.AnonymousParam =>
      typeAnonymousParamRenderer.render(anonymousParamType)
    case wildcardType: Type.Wildcard => typeWildcardTraverser.traverse(wildcardType)
    case byNameType: Type.ByName => typeByNameTraverser.traverse(byNameType)
    case repeatedType: Type.Repeated => typeRepeatedTraverser.traverse(repeatedType)
    case typeVar: Type.Var =>
      typeVarRenderer.render(typeVar)
    case _ => writeComment(s"UNSUPPORTED: ${`type`}")
  }
}
