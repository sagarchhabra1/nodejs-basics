pipeline {
    agent any

    triggers {
        githubPush()
    }

    
        stage('Checkout'){
            steps{
            checkout([$class: 'GitSCM', branches: [[name: '*/main']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '115f51a9517edc2a1bf5c6602aa7b0b451', url: 'https://github.com/sagarchhabra1/nodejs-basics.git']]])
                      
            }
        }
}
