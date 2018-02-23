package reader

import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

object TPCHFileReader extends App {

  val config = ConfigFactory.load()

  //three hashmaps for each table
  /*val view_L : mutable.HashMap[List[String],Int] = mutable.HashMap.empty
  val view_PS : mutable.HashMap[List[String],Int] = mutable.HashMap.empty
  val view_S : mutable.HashMap[List[String],Int] = mutable.HashMap.empty*/
  val view_L :ListBuffer[(List[String],Int)] = ListBuffer.empty
  val view_PS :ListBuffer[(List[String],Int)] = ListBuffer.empty
  val view_S :ListBuffer[(List[String],Int)] = ListBuffer.empty

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
    val tupleToInsert: (List[String], Int) = (tuple,1)
    relationName match {
      case "L" => view_L += tupleToInsert
      case "PS" => view_PS += tupleToInsert
      case "S" => view_S += tupleToInsert
      case _      =>
    }
  }

  //get the end time of the reading
  val endTime = System.nanoTime

  //print the duration
  val duration = (endTime-startTime)/1000000
  println("reading the file and inserting it into the corresponding hashmaps took "+duration+" ms")

}
