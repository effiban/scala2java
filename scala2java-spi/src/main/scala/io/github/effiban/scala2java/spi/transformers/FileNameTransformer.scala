package io.github.effiban.scala2java.spi.transformers

import scala.meta.Init

/** A transformer which can modify a file name.<br>
 * The transformer will additionally receive a list of the template inits (parents) of the first class in the file, if any.<br>
 * These may be needed for determining how to transform the file name.
 * <p>
 * This transformer is useful for extensions which transform to a Java framework that has file-naming conventions.<br>
 * For example - a ScalaTest filename might end in 'Spec', while the corresponding JUnit filename should end in 'Test'.
 */
trait FileNameTransformer extends SameTypeTransformer1[String, List[Init]]

object FileNameTransformer {

  /** The default transformer which returns the file name unchanged, indicating that no transformation is needed */
  val Identity: FileNameTransformer = (fileName, _) => fileName
}
