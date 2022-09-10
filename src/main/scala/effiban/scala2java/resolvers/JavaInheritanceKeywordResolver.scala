package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaKeyword, JavaTreeType}

import scala.meta.Init

trait JavaInheritanceKeywordResolver {

  def resolve(scope: JavaTreeType, inits: List[Init]): JavaKeyword
}

object JavaInheritanceKeywordResolver extends JavaInheritanceKeywordResolver {

  override def resolve(scope: JavaTreeType, inits: List[Init]): JavaKeyword = {
    val haveArgs = doInitsHaveArgs(inits)
    // The wildcard covers scenarios of both explicit and anonymous classes
    (scope, haveArgs) match {
      case (JavaTreeType.Interface, _) => JavaKeyword.Extends
      case (JavaTreeType.Class | JavaTreeType.Record, true) => JavaKeyword.Extends
      case (JavaTreeType.Enum, true) => throw new IllegalStateException("A Java enum cannot extend a class")
      case (JavaTreeType.Enum, false) => JavaKeyword.Implements
      case (_, true) => JavaKeyword.Extends
      case _ => JavaKeyword.Implements
      // TODO handle scenario of class having parent class + parent interface
    }
  }

  private def doInitsHaveArgs(inits: List[Init]) = {
    inits.flatMap(_.argss).flatten.nonEmpty
  }

}
