package reader

import com.typesafe.config.ConfigFactory
import reader.TPCHFileReaderOpt.{updateViews, view_L, view_PS, view_S}

import scala.collection.mutable
import scala.io.Source

object TPCHFileReaderIndex {

  //three hashmaps for each table
  val view_L : mutable.HashMap[Tuple2[Int,Int],List[String]] = mutable.HashMap.empty
  val view_PS : mutable.HashMap[Int, List[String]] = mutable.HashMap.empty
  val view_P : mutable.HashMap[Int,List[String]] = mutable.HashMap.empty

  def main(args: Array[String]):Unit = {
    val config = ConfigFactory.load()

    //get the start time of the reading
    val startTime = System.nanoTime

    //read the tpch file
    val inputFileName = config.getString("tpch.streamFileName")

    updateViews(inputFileName)
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
      //parse the line
      val (relationName, tuple) = TPCHFileParser.parse(l)

      //println("to make an insertion in relation: "+relationName)
      val tupleToInsert: (List[String], Int) = (tuple,1)
      relationName match {
        case "L"  => view_L.put((tuple(0).toInt,tuple(1).toInt),tuple.tail.tail)
        case "PS" => view_PS.put(tuple(0).toInt,tuple.tail)
        case "S"  => view_P.put(tuple(0).toInt,tuple.tail)
        case _    =>
      }
    }
  }
}
