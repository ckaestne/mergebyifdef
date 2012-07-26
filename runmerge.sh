cd mergebyifdef
sbt mkrun
cd ..

mergebyifdef/run.sh de.fosd.mergebyifdef.Merge --workingDir branches --outputDir branches/out jenkinsmerge.csv
