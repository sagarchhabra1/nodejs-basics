pipeline {
    agent any

    triggers {
        githubPush()
    }

    stages{
        stage('Checkout'){
            steps{
            checkout([$class: 'GitSCM', branches: [[name: '*/main']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '62b47fbc-4b55-4b96-bc70-ab5f202c69ae', url: 'https://github.com/sagarchhabra1/nodejs-basics.git']]])
                      
            }
        }
    }
}
