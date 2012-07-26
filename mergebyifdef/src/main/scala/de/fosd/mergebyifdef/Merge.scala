package de.fosd.mergebyifdef

import java.io._

/**
 * the merge tool takes a .csv file from SAVE with the merge information
 */
object Merge extends com.github.paulp.optional.Application {

    def main(workingDir: Option[java.io.File], outputDir: Option[java.io.File], arg1: java.io.File) {

        val _workingDir = workingDir.getOrElse(new File("."))
        assert(_workingDir.exists(), "working dir " + _workingDir + " does not exist")
        if (!arg1.exists)
            throw new Exception("Expected csv file as parameter")

        val lines = scala.io.Source.fromFile(arg1).getLines().toList.tail
        val numBranches = lines.head.split(";").size


        var files: Array[List[String]] = Array()
        var outputWriter: FileWriter = null
        val allFiles = (0 to (numBranches - 1)).toSet
        var lastFE: Set[Int] = allFiles

        def write(s: String) {
            outputWriter.write(s + "\n")
        }

        val _outputDir = outputDir.getOrElse(new File("out"))

        if (!_outputDir.exists())
            _outputDir.mkdirs()

        for (line <- lines) {
            val v = line.split(";",-1)
            if (v.size <= 1)
                "ignoring line"
            else if (v.take(numBranches).exists(_ != "")) {
                val filenames=v.take(numBranches)
                files=filenames.map(filename => if (filename=="") Nil else scala.io.Source.fromFile(new File(_workingDir, filename)).getLines().toList)
                assert(files.size==numBranches)
                if (outputWriter != null) {
                    if (lastFE != allFiles)
                        write("//#endif")
                    write("//#endif");
                    outputWriter.close()
                }
                val outFile=new File(_outputDir, v.head + ".merged")
                outFile.getParentFile.mkdirs
                outputWriter = new FileWriter(outFile)
                write("//#if " + allFiles.map("V" + _).mkString(" || "))
                lastFE=allFiles
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
            write("//#endif");
            outputWriter.close()
        }

    }
}
