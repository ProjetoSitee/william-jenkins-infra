def call(Map config = [:]) {
    validateConfig(config)

    pipeline {
        agent any

        tools {
            jdk 'jdk21'
            maven 'maven3'
        }

        options {
            timestamps()
            disableConcurrentBuilds()
            buildDiscarder(logRotator(numToKeepStr: '10'))
        }

        environment {
            APP_NAME = "${config.repositoryName}"
            IMAGE_TAG = "${config.repositoryName}:${env.BUILD_NUMBER}"
        }

        stages {
            stage('Contexto') {
                steps {
                    echo "Pipeline: ${config.name}"
                    echo "Projeto: ${config.projectType} | Java: ${config.javaVersion}"
                }
            }

            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Testes') {
                steps {
                    sh 'mvn -B clean test'
                }
                post {
                    always {
                        junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
                    }
                }
            }

            stage('Pacote') {
                steps {
                    sh 'mvn -B package -DskipTests'
                }
                post {
                    success {
                        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    }
                }
            }

            stage('Imagem') {
                when {
                    expression { config.buildImage && env.BRANCH_NAME == 'main' }
                }
                steps {
                    sh 'docker build -t ${IMAGE_TAG} .'
                }
            }
        }

        post {
            success {
                echo "${config.repositoryName}: pipeline concluído com sucesso."
            }
            failure {
                echo "${config.repositoryName}: pipeline falhou. Consulte o estágio destacado."
            }
            cleanup {
                deleteDir()
            }
        }
    }
}

private void validateConfig(Map config) {
    def required = ['name', 'javaVersion', 'projectType', 'repositoryName']
    def missing = required.findAll { !config[it] }

    if (missing) {
        error "Parâmetros obrigatórios ausentes: ${missing.join(', ')}"
    }

    if (config.javaVersion != '21') {
        error "Versão Java não suportada neste ambiente: ${config.javaVersion}"
    }

    if (config.projectType != 'spring-boot') {
        error "Tipo de projeto não suportado: ${config.projectType}"
    }
}
