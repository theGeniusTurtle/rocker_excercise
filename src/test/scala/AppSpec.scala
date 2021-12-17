import org.scalatest.Assertions
import org.scalatest.flatspec.AnyFlatSpecLike

class AppSpec extends AnyFlatSpecLike with Assertions{

  "Program" should "read only txt files" in {
    val path = getClass.getResource("/text_files").getPath
    Program.readFile(Array(path)) match {
      case Left(_) => assert(false, "Could not read any files from the dir")
      case Right(value) => assert(Program.indexDir(value).indexedFiles.size == 3, "Number of valid files does not match")
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
        assert(result == "bla1.txt: 66.66667% bla.txt: 100.0%", "Result is not as expected.")
    }
  }

  "Program" should "get rid of all special characters" in {
    val path = getClass.getResource("/text_files2").getPath
    Program.readFile(Array(path)) match {
      case Left(_) => assert(false, "Could not read any files from the dir")
      case Right(file) =>
        val index = Program.indexDir(file)
        index.indexedFiles.foreach(file => assert(!file.words.keys
          .exists(key => key.matches(Program.specialCharsRegex))))
    }
  }

}
