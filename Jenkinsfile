pipeline {
    agent {
        docker {
            image 'maven:3-alpine' 
            args '-v /root/.m2:/root/.m2 -p 8899:8899' 
        }
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn clean install' 
            }
        }
        stage('Deliver') { 
            steps {
                sh 'cd app-interface'
                sh 'mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run' 
            }
        }
    }
}
