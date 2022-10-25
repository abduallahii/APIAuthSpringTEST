pipeline {
    agent any
    stages {
        stage('Build Application') {
            steps {
                sh 'cp /home/Configuration/application.yaml src/main/resources/'
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
                sh "docker build . -t dockerapp"
            }
        }


        stage('Pull Spring Docker Image'){
            steps {
                sh "pwd"
                sh "echo 'Pushing Image'"
                sh "sudo docker tag dockerapp bb1994/dockerapp"
                sh "sudo docker push bb1994/dockerapp"
                sh "echo 'Removing Image'"
                sh "sudo docker rmi dockerapp:latest"
            }
        }
        
        // stage('Deploy Image'){
        //     steps {
        //         timeout(time:5, unit:'DAYS'){
        //             input message:'Approve PRODUCTION Deployment?'
        //         }
        //         sh "pwd"
        //         sh "ls -a"
        //         sh "docker container run  --detach --network application_nwt --publish 9898:9898 dockerapp:${env.BUILD_ID}"
        //     }
        // }

    }
}
