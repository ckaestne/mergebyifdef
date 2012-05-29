package de.fosd.mergebyifdef

import java.io._
import de.fosd.typechef.parser.java15._
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}

object Parse extends App {
  if (args.size != 1 || !new File(args(0)).exists)
    throw new Exception("Expected a merged file as parameter")


  def d(s:String)=FeatureExprFactory.createDefinedExternal(s)
  private def oneOf(features: List[FeatureExpr]): FeatureExpr =
      (for (f1 <- features; f2 <- features if (f1 != f2)) yield f1 mex f2).
          foldLeft(features.foldLeft(FeatureExprFactory.False)(_ or _))(_ and _)
  val initialFExpr=oneOf((0 to 4).map("V"+_).map(d(_)).toList)

  val file = new File(args(0))
  val tokens = JavaLexer.lexFile(file.getAbsolutePath)
  val p = new JavaParser()
  var ast = p.phrase(p.CompilationUnit)(tokens, initialFExpr)


  def printAST(ast:p.MultiParseResult[Any], f:FeatureExpr):Unit  = ast match {
    case p.Success(r,_)=>println(f+": \nSuccess: "+r)
    case p.Failure(m,n,_)  =>println(f+": \n"+m+" ("+n+")")
    case p.SplittedParseResult(fe,a,b)=>printAST(a,f and fe); printAST(b, f andNot fe)

  }


  printAST(ast,initialFExpr)




}
