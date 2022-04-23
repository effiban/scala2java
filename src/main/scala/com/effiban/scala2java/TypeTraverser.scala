package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

object TypeTraverser extends ScalaTreeTraverser[Type] {

  override def traverse(`type`: Type): Unit = `type` match {
    case typeApply: Type.Apply => TypeApplyTraverser.traverse(typeApply)
    case typeApplyInfix: Type.ApplyInfix => TypeApplyInfixTraverser.traverse(typeApplyInfix)
    case functionType: Type.Function => TypeFunctionTraverser.traverse(functionType)
    case tupleType: Type.Tuple => TypeTupleTraverser.traverse(tupleType)
    case withType: Type.With => TypeWithTraverser.traverse(withType)
    case typeRefine: Type.Refine => TypeRefineTraverser.traverse(typeRefine)
    case existentialType: Type.Existential => TypeExistentialTraverser.traverse(existentialType)
    case typeAnnotation: Type.Annotate => TypeAnnotateTraverser.traverse(typeAnnotation)
    case lambdaType: Type.Lambda => TypeLambdaTraverser.traverse(lambdaType)
    case placeholderType: Type.Placeholder => TypePlaceholderTraverser.traverse(placeholderType)
    case byNameType: Type.ByName => TypeByNameTraverser.traverse(byNameType)
    case repeatedType: Type.Repeated => TypeRepeatedTraverser.traverse(repeatedType)
    case typeVar: Type.Var => TypeVarTraverser.traverse(typeVar)
    case _ => emitComment(s"UNSUPPORTED: ${`type`}")
  }
}
