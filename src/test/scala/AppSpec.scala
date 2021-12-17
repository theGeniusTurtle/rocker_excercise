import org.scalatest.Assertions
import org.scalatest.flatspec.AnyFlatSpecLike

class AppSpec extends AnyFlatSpecLike with Assertions{

  "Program" should "read only txt files" in {
    val path = getClass.getResource("/text_files").getPath
    Program.readFile(Array(path)) match {
      case Left(_) => assert(false, "Could not read any files from the dir")
      case Right(value) => assert(Program.indexDir(value).indexedFiles.size == 2, "Number of valid files does not match")
    }
  }
  "Program" should "get proper scoring" in {
    val path = getClass.getResource("/text_files2").getPath
    val searchString = "kablam kading kaboom"
    Program.readFile(Array(path)) match {
      case Left(_) => assert(false, "Could not read any files from the dir")
      case Right(file) =>
        val index = Program.indexDir(file)
        val result = Program.scoringResult(searchString, index)
        println(result)
    }
  }

}
