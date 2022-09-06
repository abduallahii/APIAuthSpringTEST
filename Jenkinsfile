pipeline {
    agent any
    stages {
        stage('Build Application') {
            steps {
                sh 'mvn -f pom.xml clean package'
            }
            post {
                success {
                    echo "Now Archiving the Artifacts...."
                    archiveArtifacts artifacts: '**/*.jar'
                }
            }
        }

        stage('Create Spring Docker Image'){
            steps {
                sh "pwd"
                sh "ls -a"
                sh "docker build . -t dockerapp:${env.BUILD_ID}"
            }
        }
        
        stage('Deploy Image'){
            steps {
                sh "pwd"
                sh "ls -a"
                sh "docker container run  --detach  --publish 9898:9898 dockerapp:${env.BUILD_ID}"
            }
        }

    }
}
