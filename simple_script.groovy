pipeline {
    agent { label 'cpu1' }

    stages {
        stage("Cleanup") {
            steps {
                cleanWs()
            }
        }

        stage('Setup parameters'){
            steps{
                script{
                    properties(
                        [[$class: 'JiraProjectProperty'], 
                        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
                        parameters([
                            string(defaultValue: '3414', description: '', name: 'RUN_ID', trim: false)
                        ]),
                        [$class: 'JobLocalConfiguration', changeReasonComment: '']]
                    )
                }
            }
        }
    }
}
