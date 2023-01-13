package io.github.effiban.scala2java.spi.transformers

/** A transformer which can modify a file name.
 * Useful for extensions which require that the generated Java file name be different than the original Scala one.
 * For example - a ScalaTest filename might end in 'Spec', while the corresponding JUnit filename should end in 'Test'.
 */
trait FileNameTransformer extends SameTypeTransformer0[String]

object FileNameTransformer {

  /** The default transformer which returns the file name unchanged, indicating that no transformation is needed */
  val Identity: FileNameTransformer = identity[String]
}
