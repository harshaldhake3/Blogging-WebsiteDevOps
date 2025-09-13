pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
    ansiColor('xterm')
  }

  parameters {
    string(name: 'REGISTRY', defaultValue: 'docker.io', description: 'Container registry host (docker.io, ghcr.io, etc.)')
    string(name: 'REGISTRY_NAMESPACE', defaultValue: 'your-namespace', description: 'Registry namespace/org (e.g., dockerhub user or GHCR org)')
    string(name: 'IMAGE_NAME', defaultValue: 'devops-blog', description: 'Image name')
    booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip unit tests')
  }

  environment {
    // Tools configured in Jenkins global tools
    JAVA_HOME = tool name: 'jdk17', type: 'jdk'
    MAVEN_HOME = tool name: 'maven3', type: 'maven'
    PATH = "${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"

    // Kubernetes namespaces
    K8S_NAMESPACE_DEV  = 'devops-blog'
    K8S_NAMESPACE_PROD = 'devops-blog'

    // App settings
    SPRING_PROFILES_ACTIVE = 'prod'

    // Credentials IDs (configure in Jenkins)
    DOCKER_CREDS   = 'dockerhub-creds'
    KUBECONFIG_ID  = 'kubeconfig-devops'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'git log -1 --pretty=oneline'
      }
    }

    stage('Resolve Project Version') {
      steps {
        script {
          // Use Maven to read the version declared in pom.xml
          env.PROJECT_VERSION = sh(
            script: "mvn -q -Dexec.executable=echo -Dexec.args='\\${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:3.1.0:exec",
            returnStdout: true
          ).trim()

          // Compose image tag: version + build number + short sha
          def sha = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
          env.IMAGE_TAG = "${env.PROJECT_VERSION}-${env.BUILD_NUMBER}-${sha}"

          echo "Resolved version: ${env.PROJECT_VERSION}"
          echo "Image tag will be: ${env.IMAGE_TAG}"
        }
      }
    }

    stage('Build & Test') {
      steps {
        sh """
          mvn -B -ntp -DskipTests=${params.SKIP_TESTS} clean verify
        """
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, onlyIfSuccessful: true
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          def imageRef = "${params.REGISTRY}/${params.REGISTRY_NAMESPACE}/${params.IMAGE_NAME}:${env.IMAGE_TAG}"
          sh """
            echo "Building image ${imageRef}"
            docker build -t ${imageRef} .
          """
          env.IMAGE_REF = imageRef
        }
      }
    }

    stage('Push Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: "${env.DOCKER_CREDS}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh """
            echo "${DOCKER_PASS}" | docker login ${params.REGISTRY} -u "${DOCKER_USER}" --password-stdin
            docker push ${env.IMAGE_REF}
            docker logout ${params.REGISTRY}
          """
        }
      }
    }

    stage('Deploy to Kubernetes') {
      when {
        anyOf {
          branch 'main'
          branch 'develop'
        }
      }
      steps {
        withCredentials([file(credentialsId: "${env.KUBECONFIG_ID}", variable: 'KUBECONFIG_FILE')]) {
          sh '''
            export KUBECONFIG="${KUBECONFIG_FILE}"

            # Decide namespace by branch
            if [ "${BRANCH_NAME}" = "main" ]; then
              NS="${K8S_NAMESPACE_PROD}"
            else
              NS="${K8S_NAMESPACE_DEV}"
            fi
            echo "Using namespace: ${NS}"

            # Apply (or create) base manifests
            kubectl apply -f k8s/00-namespace.yaml
            kubectl apply -f k8s/01-configmap.yaml
            kubectl apply -f k8s/02-secret.yaml
            kubectl apply -f k8s/03-pvc.yaml
            kubectl apply -f k8s/10-postgres.yaml
            kubectl apply -f k8s/20-app.yaml
            kubectl apply -f k8s/30-ingress.yaml || true

            # Set the newly built image on the deployment (don’t rely on manifest tag)
            kubectl -n ${NS} set image deployment/devops-blog devops-blog=${IMAGE_REF} --record

            # Wait for rollout
            kubectl -n ${NS} rollout status deployment/devops-blog --timeout=120s
            kubectl -n ${NS} get pods -o wide
          '''
        }
      }
    }
  }

  post {
    success {
      echo "Build ${env.BUILD_NUMBER} succeeded. Image: ${env.IMAGE_REF}"
    }
    failure {
      echo "Build ${env.BUILD_NUMBER} failed."
    }
    always {
      cleanWs(deleteDirs: true)
    }
  }
}
