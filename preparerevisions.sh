#assumes the git repo of jenkins in directory ./jenkins


cat revisions | while read rev; do 
	mkdir -p branches/$rev/
	cd jenkins

	git checkout $rev
	echo $rev > ../branches/$rev/.rev
	git log -n 1 >> ../branches/$rev/.rev
	
	git archive --format=tar $rev | (cd ../branches/$rev/ && tar xf -)
	cd ..
done
