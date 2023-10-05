pipeline {
    agent { label 'cpu1' }

    stages {
        stage("Cleanup") {
            steps {
                cleanWs()
            }
        }
    }
}
