pipeline {
    agent any

    triggers {
        githubPush()
    }

    
        stage('Checkout'){
            steps{
            checkout([$class: 'GitSCM', branches: [[name: '*/test_gitaction']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9e673d23-974c-460c-ba67-1188333cf4b4', url: 'https://github.com/AccessibleAI/jenkins_pipelines_qa.git']]])
                      
            }
        }
}