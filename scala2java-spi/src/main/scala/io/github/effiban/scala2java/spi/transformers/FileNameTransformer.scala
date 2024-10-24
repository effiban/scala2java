package io.github.effiban.scala2java.spi.transformers

import scala.meta.Init

/** A transformer which can modify a file name.<br>
 * @deprecated all implementations are currently ignored, it will be removed in the next major version
 */
@deprecated
trait FileNameTransformer extends SameTypeTransformer1[String, List[Init]]

object FileNameTransformer {

  /** The default transformer which returns the file name unchanged, indicating that no transformation is needed */
  val Identity: FileNameTransformer = (fileName, _) => fileName
}
