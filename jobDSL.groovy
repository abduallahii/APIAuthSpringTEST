pipeline {
    agent any
    stages {
        stage('Build Application'){
            steps{
                sh "mvn -f pom.xml clean package"
            }
            post{
                success {
                    echo "Now Archiving .."
                    archiveArtifacts artifacts:'**/*.jar'
                }
            }
        }

        stage('Create  DOCKER image'){
            steps {
                sh "pwd"
                sh "ls -a"
                sh "docker build . -t tomcatsample:${env.BUILD_ID}"
            }
        }
    }
}
