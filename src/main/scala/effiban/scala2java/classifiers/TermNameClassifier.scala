package effiban.scala2java.classifiers

import scala.meta.Term

trait TermNameClassifier {
  def isJavaStreamLike(termName: Term.Name): Boolean

  def isJavaListLike(termName: Term.Name): Boolean

  def isJavaSetLike(termName: Term.Name): Boolean

  def isJavaMapLike(termName: Term.Name): Boolean
}

object TermNameClassifier extends TermNameClassifier {

  final val JavaStreamLike: Set[String] = Set(
    "LazyList",
    "Stream",
  )

  final val JavaListLike: Set[String] = Set(
    "Seq",
    "LinearSeq",
    "IndexedSeq",
    "ArraySeq",
    "List",
    "Vector"
  )

  final val JavaSetLike: Set[String] = Set(
    "Set",
    "HashSet"
  )

  final val JavaMapLike: Set[String] = Set(
    "Map",
    "HashMap"
  )

  override def isJavaStreamLike(termName: Term.Name): Boolean = JavaStreamLike.contains(termName.value)

  override def isJavaListLike(termName: Term.Name): Boolean = JavaListLike.contains(termName.value)

  override def isJavaSetLike(termName: Term.Name): Boolean = JavaSetLike.contains(termName.value)

  override def isJavaMapLike(termName: Term.Name): Boolean = JavaMapLike.contains(termName.value)
}
