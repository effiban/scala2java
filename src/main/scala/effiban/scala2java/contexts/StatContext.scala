package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

case class StatContext(javaScope: JavaTreeType = JavaTreeType.Unknown)
