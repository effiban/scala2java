package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeTraverser extends ScalaTreeTraverser[Type]

private[traversers] class TypeTraverserImpl(typeRefTraverser: => TypeRefTraverser,
                                            typeProjectTraverser: => TypeProjectTraverser,
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
                                            typeRepeatedTraverser: => TypeRepeatedTraverser,
                                            typeRenderer: => TypeRenderer)
                                           (implicit javaWriter: JavaWriter) extends TypeTraverser {

  import javaWriter._

  override def traverse(`type`: Type): Unit = `type` match {
    case typeRef: Type.Ref => typeRef match {
      case typeProject: Type.Project => typeProjectTraverser.traverse(typeProject)
      case aTypeRef =>
        val traversedTypeRef = typeRefTraverser.traverse(aTypeRef)
        typeRenderer.render(traversedTypeRef)
    }
    case typeApply: Type.Apply => typeApplyTraverser.traverse(typeApply)
    case typeApplyInfix: Type.ApplyInfix =>
      val traversedTypeApplyInfix = typeApplyInfixTraverser.traverse(typeApplyInfix)
      typeRenderer.render(traversedTypeApplyInfix)
    case functionType: Type.Function => typeFunctionTraverser.traverse(functionType)
    case tupleType: Type.Tuple => typeTupleTraverser.traverse(tupleType)
    case withType: Type.With => typeWithTraverser.traverse(withType)
    case typeRefine: Type.Refine => typeRefineTraverser.traverse(typeRefine)
    case existentialType: Type.Existential => typeExistentialTraverser.traverse(existentialType)
    case typeAnnotation: Type.Annotate => typeAnnotateTraverser.traverse(typeAnnotation)
    case lambdaType: Type.Lambda =>
      typeRenderer.render(lambdaType)
    case anonymousParamType: Type.AnonymousParam =>
      typeRenderer.render(anonymousParamType)
    case wildcardType: Type.Wildcard => typeWildcardTraverser.traverse(wildcardType)
    case byNameType: Type.ByName => typeByNameTraverser.traverse(byNameType)
    case repeatedType: Type.Repeated => typeRepeatedTraverser.traverse(repeatedType)
    case typeVar: Type.Var =>
      typeRenderer.render(typeVar)
    case _ => writeComment(s"UNSUPPORTED: ${`type`}")
  }
}
