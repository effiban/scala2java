package effiban.scala2java

import effiban.scala2java.writers.JavaWriter

package object traversers {

  implicit val javaWriter: JavaWriter = JavaWriter
}
