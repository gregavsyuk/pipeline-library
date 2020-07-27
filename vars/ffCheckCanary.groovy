def call(body) {
	changedFile = sh (script:"git diff-tree --no-commit-id --name-only -r ${env.GIT_COMMIT}", returnStdout: true)
	echo(changedFile)
	experimentFile = changedFile.contains("experiments/")
	if(experimentFile){
		experimentText = sh (script:"cat ${changedFile}", returnStdout: true)
		sh "cat ${changedFile}"
	    env.experimentYaml = readYaml (text: "$experimentText")
	    flagName = env.experimentYaml.flag
	    if(env.experimentYaml.labels)
	    {
	      canaryBool = env.experimentYaml.labels.contains("Canary-deploy")
	      return canaryBool
	    }
	}
}