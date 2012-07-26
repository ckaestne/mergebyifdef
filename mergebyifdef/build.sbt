name := "MergeByIfdef"

version := "0.0.2"

scalaVersion := "2.9.1"

libraryDependencies += "de.fosd.typechef" %% "frontend" % "0.3.3-SNAPSHOT"

libraryDependencies += "com.thoughtworks.paranamer" % "paranamer" % "2.2.1"

TaskKey[File]("mkrun") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -ea -Xmx1G -Xms128m -Xss10m -classpath "%s" %s "$@"
"""
  val mainStr = ""
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "run.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}
