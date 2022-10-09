package io.github.effiban.scala2java.writers

import java.io.Writer

object ConsoleJavaWriter extends JavaWriterImpl(new Writer {
  override def write(cbuf: Array[Char], off: Int, len: Int): Unit = print(cbuf.mkString.substring(off, len))

  override def flush(): Unit = {}

  override def close(): Unit = {}
})
