package de.fosd.mergebyifdef

import java.io._


object Merge extends App {

  if (args.size != 1 || !new File(args(0)).exists)
    throw new Exception("Expected csv file as parameter")

  val lines = scala.io.Source.fromFile(args(0)).getLines().toList.tail
  val numBranches = 5


  var files: Array[List[String]] = Array()
  var outputWriter: FileWriter = null
  val allFiles = (0 to (numBranches - 1)).toSet
  var lastFE: Set[Int] = allFiles

  def write(s: String) {
    outputWriter.write(s + "\n")
  }

  for (line <- lines) {
    val v = line.split(";")
    if (v.size <= 1)
      "ignoring line"
    else if (v(0) != "") {
      files = v.take(numBranches).map(filename => scala.io.Source.fromFile(filename).getLines().toList)
      if (outputWriter != null) {
        write("//#endif"); outputWriter.close()
      }
      outputWriter = new FileWriter("out/" + v.head + ".merged")
      write("//#if " + allFiles.map("V" + _).mkString(" || "))
    } else {
      val offsets = v.drop(numBranches)
      val (line, fileidx) = offsets.zip(0 to (numBranches - 1)).filter(_._1 != "").head
      val fileidxs = offsets.zip(0 to (numBranches - 1)).filter(_._1 != "").map(_._2)

      if (lastFE != fileidxs.toSet) {
        if (lastFE != allFiles)
          write("//#endif")
        lastFE = fileidxs.toSet
        if (lastFE != allFiles)
          write("//#if " + fileidxs.map("V" + _).mkString(" || "))
      }

      write(files(fileidx)(line.toInt - 1))
    }

  }

  if (outputWriter != null) {
    write("//#endif"); outputWriter.close()
  }


}
