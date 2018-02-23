package reader

import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.io.Source

object TPCHFileReader extends App {

  val config = ConfigFactory.load()

  //three hashmaps for each table
  val view_L : mutable.HashMap[List[String],Int] = mutable.HashMap.empty
  val view_PS : mutable.HashMap[List[String],Int] = mutable.HashMap.empty
  val view_S : mutable.HashMap[List[String],Int] = mutable.HashMap.empty

  //get the start time of the reading
  val startTime = System.nanoTime

  //read the tpch file
  val inputFileName = config.getString("tpch.streamFileName")

  val streamFile = Source.fromFile(inputFileName, "UTF-8")

  val streamInsertionLines = streamFile.getLines()

  //for each read line, parse and then insert in the hashmap
  for (l <- streamInsertionLines) {
    //parse the line
    val (relationName, tuple) = TPCHFileParser.parse(l)

    println("to make an insertion in relation: "+relationName)
    relationName match {
      case "L" => view_L.put(tuple,1)
      case "PS" => view_PS.put(tuple,1)
      case "S" => view_S.put(tuple,1)
      case _      =>
    }
  }

  //get the end time of the reading
  val endTime = System.nanoTime

  //print the duration
  val duration = (endTime-startTime)/1000000
  println("reading the file and inserting it into the corresponding hashmaps took "+duration+" ms")

}
