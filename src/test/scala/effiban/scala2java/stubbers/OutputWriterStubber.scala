package effiban.scala2java.stubbers

import org.mockito.MockitoSugar.doAnswer
import org.mockito.stubbing.Stubber

import java.io.StringWriter

object OutputWriterStubber {

  def doWrite(answer: String)(implicit outputWriter: StringWriter): Stubber = doAnswer(outputWriter.write(answer))
}
