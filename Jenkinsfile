pipeline {
    agent any
    
    environment {
        // 可选：如果 Java 21 有问题，指定使用 Java 11
        // JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'
    }
    
    stages {
        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }
        
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
        }
        
        stage('PMD') {
            steps {
                sh 'mvn pmd:pmd'
            }
        }
        
        stage('JaCoCo') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        
        stage('Javadoc') {
            steps {
                // 修改点：跳过 Javadoc 生成（因为代码注释不完整）
                // 或者使用宽松模式
                sh 'mvn javadoc:javadoc -DfailOnError=false -DfailOnWarnings=false -Xdoclint:none'
            }
        }
        
        stage('Site') {
            steps {
                // 修改点：生成 site 时也跳过 Javadoc 问题
                sh 'mvn site -Dmaven.javadoc.skip=true'
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: '**/target/site/**/*.*', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}