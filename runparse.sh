cd mergebyifdef
sbt mkrun
cd ..


find branches/out/ -name *.merged | while read file; do
	echo parsing $file
	mergebyifdef/run.sh de.fosd.mergebyifdef.Parse --numFeatures 9 $file &> $file.err
done
