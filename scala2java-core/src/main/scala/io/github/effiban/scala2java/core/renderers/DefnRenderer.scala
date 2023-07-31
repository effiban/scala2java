package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn
import scala.meta.Defn.Trait

trait DefnRenderer {
  def render(defn: Defn, context: DefnRenderContext = EmptyStatRenderContext): Unit
}

private[renderers] class DefnRendererImpl(defnVarRenderer: => DefnVarRenderer,
                                          defnDefRenderer: => DefnDefRenderer,
                                          caseClassRenderer: => CaseClassRenderer,
                                          regularClassRenderer: => RegularClassRenderer,
                                          traitRenderer: => TraitRenderer,
                                          objectRenderer: => ObjectRenderer,
                                          classClassifier: ClassClassifier)
                                         (implicit javaWriter: JavaWriter) extends DefnRenderer {

  import javaWriter._

  override def render(defn: Defn, context: DefnRenderContext = EmptyStatRenderContext): Unit =
    (defn, context) match {
      case (defnVar: Defn.Var, varContext: VarRenderContext) => defnVarRenderer.render(defnVar, varContext)
      case (defnVar: Defn.Var, EmptyStatRenderContext) => defnVarRenderer.render(defnVar)
      case (defnVar: Defn.Var, aContext) => handleInvalidContext(defnVar, aContext)

      case (defDef: Defn.Def, defContext: DefRenderContext) => defnDefRenderer.render(defDef, defContext)
      case (defDef: Defn.Def, EmptyStatRenderContext) => defnDefRenderer.render(defDef)
      case (defDef: Defn.Def, aContext) => handleInvalidContext(defDef, aContext)

      case (defnClass: Defn.Class, caseClassContext: CaseClassRenderContext) if classClassifier.isCase(defnClass) =>
        caseClassRenderer.render(defnClass, caseClassContext)
      case (defnClass: Defn.Class, regularClassContext: RegularClassRenderContext) if classClassifier.isRegular(defnClass) =>
        regularClassRenderer.render(defnClass, regularClassContext)
      case (defnClass: Defn.Class, aContext) => handleInvalidContext(defnClass, aContext)

      case (defnTrait: Trait, traitContext: TraitRenderContext) => traitRenderer.render(defnTrait, traitContext)
      case (defnTrait: Trait, aContext) => handleInvalidContext(defnTrait, aContext)

      case (defnObject: Defn.Object, objectContext: ObjectRenderContext) => objectRenderer.render(defnObject, objectContext)
      case (defnObject: Defn.Object, aContext) => handleInvalidContext(defnObject, aContext)

      case (aDefn, _) => writeComment(s"UNSUPPORTED: $aDefn")
    }

  private def handleInvalidContext(defn: Defn, aContext: DefnRenderContext): Unit = {
    throw new IllegalStateException(s"Got an invalid context type $aContext for: $defn")
  }
}
