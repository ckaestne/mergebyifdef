package de.fosd.mergebyifdef

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.featureexpr.FeatureExprFactory
import java.io._
import de.fosd.typechef.parser.java15._

object Parse extends com.github.paulp.optional.Application {

    def d(s: String) = FeatureExprFactory.createDefinedExternal(s)

    private def oneOf(features: List[FeatureExpr]): FeatureExpr =
        (for (f1 <- features; f2 <- features if (f1 != f2)) yield f1 mex f2).
            foldLeft(features.foldLeft(FeatureExprFactory.False)(_ or _))(_ and _)

    def main(numFeatures: Int, arg1: java.io.File) {
        if (!arg1.exists())
            throw new Exception("Expected a merged file as parameter")



        val initialFExpr = oneOf((0 to (numFeatures - 1)).map("V" + _).map(d(_)).toList)


        val tokens = JavaLexer.lexFile(arg1.getAbsolutePath)
        val p = new JavaParser()
        var ast = p.phrase(p.CompilationUnit)(tokens, initialFExpr)


        def printAST(ast: p.MultiParseResult[Any], f: FeatureExpr): Unit = ast match {
            case p.Success(r, _) =>
                println(f + ": \nSuccess")
                val w=new FileWriter(new File(arg1.getParentFile,arg1.getName+".ast"))
                w.write(r.toString)
                w.close()
            case p.Failure(m, n, _) => System.err.println(f + ": \n" + m + " (" + n + ")")
            case p.SplittedParseResult(fe, a, b) => printAST(a, f and fe); printAST(b, f andNot fe)
        }


        printAST(ast, initialFExpr)

    }


}
