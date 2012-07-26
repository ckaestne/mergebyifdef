name := "MergeByIfdef"

version := "0.0.1"

scalaVersion := "2.9.1"

libraryDependencies += "de.fosd.typechef" %% "frontend" % "0.3.3"

TaskKey[File]("mkrun") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -ea -Xmx2G -Xms128m -Xss10m -classpath "%s" %s "$@"
"""
  val mainStr = ""
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "run.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}
