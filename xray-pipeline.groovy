pipeline {
    agent any

    stages {
    
        stage('Install node and npm versions') {
             steps {
                    sh "node -v"
                    sh 'npm install -g newman'   
             }
        }


        
        stage('Checkout'){
            steps{
            checkout([$class: 'GitSCM', branches: [[name: '*/upload-result-on-env']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9e673d23-974c-460c-ba67-1188333cf4b4', url: 'https://github.com/AccessibleAI/jenkins_pipelines_qa.git']]])
                      
            }
        }
        
        
        
    }
}
