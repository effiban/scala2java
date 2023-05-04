package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeRenderer extends JavaTreeRenderer[Type]

private[renderers] class TypeRendererImpl(typeRefRenderer: => TypeRefRenderer,
                                          typeApplyRenderer: => TypeApplyRenderer,
                                          typeApplyInfixRenderer: TypeApplyInfixRenderer,
                                          typeWithRenderer: => TypeWithRenderer,
                                          typeRefineRenderer: => TypeRefineRenderer,
                                          typeExistentialRenderer: => TypeExistentialRenderer,
                                          typeAnonymousParamRenderer: TypeAnonymousParamRenderer,
                                          typeVarRenderer: TypeVarRenderer)
                                         (implicit javaWriter: JavaWriter) extends TypeRenderer {

  import javaWriter._

  override def render(`type`: Type): Unit = `type` match {
    case typeRef: Type.Ref => typeRefRenderer.render(typeRef)
    case typeApply: Type.Apply => typeApplyRenderer.render(typeApply)
    case typeApplyInfix: Type.ApplyInfix => typeApplyInfixRenderer.render(typeApplyInfix)
    case withType: Type.With => typeWithRenderer.render(withType)
    case typeRefine: Type.Refine => typeRefineRenderer.render(typeRefine)
    case existentialType: Type.Existential => typeExistentialRenderer.render(existentialType)
    case typeAnnotation: Type.Annotate => // TODO
    case anonymousParamType: Type.AnonymousParam => typeAnonymousParamRenderer.render(anonymousParamType)
    case wildcardType: Type.Wildcard => // TODO
    case byNameType: Type.ByName => // TODO
    case repeatedType: Type.Repeated => // TODO
    case typeVar: Type.Var => typeVarRenderer.render(typeVar)
    case _ => writeComment(s"UNSUPPORTED: ${`type`}")
  }
}
