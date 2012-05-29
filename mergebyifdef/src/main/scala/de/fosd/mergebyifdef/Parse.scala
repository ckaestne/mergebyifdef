package de.fosd.mergebyifdef

import java.io._

object Parse extends App {
  if (args.size != 1 || !new File(args(0)).exists)
    throw new Exception("Expected a merged file as parameter")



}
