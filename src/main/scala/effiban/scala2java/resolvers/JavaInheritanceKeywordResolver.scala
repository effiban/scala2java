package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.{JavaKeyword, JavaScope}

import scala.meta.Init

trait JavaInheritanceKeywordResolver {

  def resolve(scope: JavaScope, inits: List[Init]): JavaKeyword
}

object JavaInheritanceKeywordResolver extends JavaInheritanceKeywordResolver {

  override def resolve(scope: JavaScope, inits: List[Init]): JavaKeyword = {
    val haveArgs = doInitsHaveArgs(inits)
    // The wildcard covers scenarios of both explicit and anonymous classes
    (scope, haveArgs) match {
      case (JavaScope.Interface, _) => JavaKeyword.Extends
      case (_, true) => JavaKeyword.Extends
      case (_, false) => JavaKeyword.Implements
      // TODO handle scenario of class having parent class + parent interface
    }
  }

  private def doInitsHaveArgs(inits: List[Init]) = {
    inits.flatMap(_.argss).flatten.nonEmpty
  }

}
