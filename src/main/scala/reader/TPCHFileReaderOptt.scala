package reader

import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ListBuffer
import scala.io.Source

object TPCHFileReaderOptt {

  val view_L :ListBuffer[(List[String],Int)] = ListBuffer.empty
  val view_PS :ListBuffer[(List[String],Int)] = ListBuffer.empty
  val view_S :ListBuffer[(List[String],Int)] = ListBuffer.empty

  def main(args: Array[String]):Unit = {
    val config = ConfigFactory.load()

    //get the start time of the reading
    val startTime = System.nanoTime

    //read the tpch file
    val inputFileName = config.getString("tpch.streamFileName")

    updateViews(inputFileName)

    /*val streamFile = Source.fromFile(inputFileName, "UTF-8")

    val streamInsertionLines = streamFile.getLines()

    //for each read line, parse and then insert in the hashmap
    for (l <- streamInsertionLines) {
      //parse the line
      val (relationName, tuple) = TPCHFileParser.parse(l)

      //println("to make an insertion in relation: "+relationName)
      val tupleToInsert: (List[String], Int) = (tuple,1)
      relationName match {
        case "L" => view_L += tupleToInsert
        case "PS" => view_PS += tupleToInsert
        case "S" => view_S += tupleToInsert
        case _      =>
      }
    }*/

    //get the end time of the reading
    val endTime = System.nanoTime

    //print the duration
    val duration = (endTime-startTime)/1000000
    println("reading the file and inserting it into the corresponding hashmaps took "+duration+" ms")

  }

  def updateViews(inputFileName: String) ={
    val streamFile = Source.fromFile(inputFileName, "UTF-8")

    val streamInsertionLines = streamFile.getLines()

    for (l <- streamInsertionLines) {
      parseAndUpdate(l)
    }
  }

  def parseAndUpdate(l:String) ={
    //println("to make an insertion in relation: "+relationName)
    //parse the line
    val (relationName, tuple) = TPCHFileParser.parse(l)
    val tupleToInsert: (List[String], Int) = (tuple,1)
    relationName match {
      case "L"  => view_L += tupleToInsert
      case "PS" => view_PS += tupleToInsert
      case "S"  => view_S += tupleToInsert
      case _    =>
    }
  }
}
