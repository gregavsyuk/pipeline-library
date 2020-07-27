def call(body) {
	changedFile = sh (script:"git diff-tree --no-commit-id --name-only -r ${env.GIT_COMMIT}", returnStdout: true)
	echo(changedFile)
	experimentFile = changedFile.contains("experiments/")
	currentPhase = ""
	if(experimentFile){
		experimentText = sh (script:"cat ${changedFile}", returnStdout: true)
		sh "cat ${changedFile}"
	    experimentYaml = readYaml (text: "$experimentText")
	    env.flagName = experimentYaml.flag
	    if(experimentYaml.labels)
	    {
	      canaryBool = experimentYaml.labels.contains("Canary-deploy")
	      if(canaryBool){
                //Start of check for deployment phase shared library
                //If it is labeled for a canary deployment, figure out which deployment phase we're in
                if(experimentYaml.conditions){
                  groupBool = false
                  if(experimentYaml.conditions.group.name){
                    groupBool = true
                    targetGroupBool = experimentYaml.conditions.group.name.contains("Internal-Testing")
                    if(targetGroupBool){
                      currentPhase = "internalTesting"
                    }
                  }
                  if(!(groupBool && experimentYaml.conditions.size() == 1)) {
                    if(experimentYaml.conditions[-1].value.percentage){
                      echo experimentYaml.conditions[-1].value.percentage.toString()
                      currentPhase = "percentageDeploy"
                    }
                  }
                }
                //If there are no additional conditions in the ruleset, check if flag is fully on or fully off
                else{
                  currentPhase = "fullyFalse"
                }
                echo "$currentPhase"
                //End of deployment phase shared library
              }
	      return currentPhase
	    }
	}
}