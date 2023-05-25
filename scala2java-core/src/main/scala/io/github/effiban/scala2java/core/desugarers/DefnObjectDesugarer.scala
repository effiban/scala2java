package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Defn, Template}

trait DefnObjectDesugarer extends SameTypeDesugarer[Defn.Object]

private[desugarers] class DefnObjectDesugarerImpl(templateDesugarer: => SameTypeDesugarer[Template]) extends DefnObjectDesugarer {

  override def desugar(defnObject: Defn.Object): Defn.Object = {
    import defnObject._

    defnObject.copy(templ = templateDesugarer.desugar(templ))
  }
}
