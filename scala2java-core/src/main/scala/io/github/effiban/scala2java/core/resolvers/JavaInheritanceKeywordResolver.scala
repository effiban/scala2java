package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaScope}

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
    inits.flatMap(_.argss).flatten.nonEmpty
  }

}
