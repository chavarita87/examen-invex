// Declarative pipeline equivalent to .github/workflows/ci.yml.
// Needs a Jenkins agent with Docker, plus the JUnit and JaCoCo plugins.
pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        IMAGE = "examen-invex:${env.BUILD_NUMBER}"
        TRIVY_IMAGE = 'aquasec/trivy:0.57.1'
    }

    stages {
        stage('Build, test and coverage') {
            steps {
                sh 'chmod +x mvnw && ./mvnw -B verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec', classPattern: 'target/classes',
                           sourcePattern: 'src/main/java'
                }
            }
        }

        stage('Dependency vulnerability scan') {
            steps {
                sh '''
                    docker run --rm -v "$PWD":/src -w /src $TRIVY_IMAGE fs \
                        --scanners vuln,secret --severity CRITICAL --ignore-unfixed \
                        --ignorefile .trivyignore --exit-code 1 .
                '''
            }
        }

        stage('Build Docker image') {
            when { anyOf { branch 'develop'; branch 'main'; branch 'release/*' } }
            steps {
                sh 'docker build -t $IMAGE .'
            }
        }

        stage('Image vulnerability scan') {
            when { anyOf { branch 'develop'; branch 'main'; branch 'release/*' } }
            steps {
                sh '''
                    docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
                        -v "$PWD/.trivyignore":/.trivyignore $TRIVY_IMAGE image \
                        --severity CRITICAL --ignore-unfixed --ignorefile /.trivyignore --exit-code 1 $IMAGE
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
