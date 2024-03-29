package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Init

trait JavaInheritanceKeywordResolver {

  def resolve(scope: JavaScope, inits: List[Init]): JavaKeyword
}

object JavaInheritanceKeywordResolver extends JavaInheritanceKeywordResolver {

  override def resolve(scope: JavaScope, inits: List[Init]): JavaKeyword = {
    val haveArgs = doInitsHaveArgs(inits)
    (scope, haveArgs) match {
      case (JavaScope.Interface, _) => JavaKeyword.Extends
      case (JavaScope.UtilityClass, _) => throw new IllegalStateException("A Java utility class cannot have a parent")
      case (JavaScope.Enum, true) => throw new IllegalStateException("A Java enum cannot extend a class")
      case (JavaScope.Enum, false) => JavaKeyword.Implements
      case (_, true) => JavaKeyword.Extends
      case _ => JavaKeyword.Implements
      // TODO handle scenario of class having parent class + parent interface
    }
  }

  private def doInitsHaveArgs(inits: List[Init]) = {
    // NOTE that emptiness is only checked at the upper level.
    // If there are empty lists inside (displayed as empty parens), they are still considered to be "existing args" -
    // and indicate that the parent is a class.
    inits.exists(_.argss.nonEmpty)
  }

}
