import java.io.File
import scala.annotation.tailrec
import scala.collection.immutable.Vector
import scala.io.Source
import scala.util.{Try, Using}

object Application extends App{
  Program
    .readFile(args)
    .fold(
      println,
      file => Program.iterate(Program.indexDir(file))
    )
}

object Program {
  import scala.io.StdIn.readLine
  type WordMap = Map[String, Int]

  case class Index(indexedFiles: Vector[IndexedFile])
  case class IndexedFile(name: String, words: WordMap)

  sealed trait ReadFileError

  case object MissingPathArg extends ReadFileError
  case class NotDirectory(error: String) extends ReadFileError
  case class FileNotFound(t: Throwable) extends ReadFileError
  case class ErrorReadingFile(fileName: String, t: Throwable) extends ReadFileError


  def readFile(args: Array[String]): Either[ReadFileError, File] = {
    for {
      path <- args.headOption.toRight(MissingPathArg)
      file <- Try(new java.io.File(path))
        .fold(
          throwable => Left(FileNotFound(throwable)),
          file =>
            if (file.isDirectory) Right(file)
            else Left(NotDirectory(s"Path [$path] is not a directory"))
        )
    } yield file
  }

  //assumption is that we are only reading txt files and omitting any other files
  def indexDir(dir: File): Index = {
    val indexedFiles =  dir.listFiles.toVector
      //support nested dirs
      .flatMap(file => if (file.isFile) Vector(file) else file.listFiles.toVector)
      .filter(_.getName.endsWith(".txt"))
      .map(indexFile).collect{
      case Right(value) => value
    }
    Index(indexedFiles)
  }

  def indexFile(file: File): Either[ReadFileError, IndexedFile] = {
    val name: String = file.getName
    val words: Either[Throwable, WordMap] = Using(Source.fromFile(file)) {
      source => source.getLines().toVector
        .map(normalizeLine)
        .flatMap(tokenize)
        .map(normalizeWord)
        .groupBy(identity)
        .view.mapValues(_.size).toMap
    }.toEither

    words match {
      case Left(value) => Left(ErrorReadingFile(name, value))
      case Right(value) => Right(IndexedFile(name, value))
    }
  }

  //we are omitting all the special signs
  def normalizeLine(value: String) =
    value.replaceAll("[^A-Za-z0-9]", " ")

  //assumption: two words match regardless of the casing, so we normalize all to lowercase
  def normalizeWord(value: String) = value.trim.toLowerCase


  def tokenize(value: String): Vector[String] =
    value.split(" ").toVector
      .filter(!_.isBlank)


  def iterate(index: Index): Unit = {
    if(index.indexedFiles.isEmpty){
      println("Directory is empty. Please re-run and specify non-empty one.")
      return
    }
    print(s"search> ")
    val searchString = readLine()
    println(scoringResult(searchString, index))
    iterate(index)
  }

  def scoringResult(searchString: String, index: Index): String = {
    val searchArray = tokenize(searchString)
    val searchWordMap = searchArray.map(normalizeWord).groupBy(identity).view.mapValues(_.size).toMap
    @tailrec
    def iterateIndexedFiles(indexFiles: Vector[IndexedFile], aggregate: String): String = {
      indexFiles.headOption match {
        case Some(IndexedFile(name, words)) =>
          val percentage = score(words, searchWordMap)/searchArray.size * 100
          iterateIndexedFiles(indexFiles.tail, aggregate + s"$name: ${percentage}% ")
        case None => aggregate
      }
    }
    iterateIndexedFiles(index.indexedFiles, "")
  }

  def score(indexedFileWordMap: WordMap, searchWordMap: WordMap): Float = {
    searchWordMap.toVector.map {
      case (word, occurrences) =>
        val occurrencesInFile: Int = indexedFileWordMap.applyOrElse[String, Int](word, _ => 0)
        if (occurrencesInFile >= occurrences) occurrences
        else if (occurrencesInFile < occurrences && occurrencesInFile > 0) occurrences - occurrencesInFile
        else 0
    }.sum
  }
}
