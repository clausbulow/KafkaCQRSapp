//Remember: https://dockerturtle.medium.com/how-to-design-jenkins-jobs-with-git-branching-model-and-target-environments-f5940beef73c
//When time allows - also look into https://koudingspawn.de/the-complete-ci-cd-part-5/
podTemplate(containers: [
    containerTemplate(name: 'maven', image: 'maven:3.6.3-openjdk-11', ttyEnabled: true, command: 'cat', envVars: [envVar(key: 'DOCKER_HOST', value: 'tcp://docker:2375')])
    ],
    volumes: [
        hostPathVolume(mountPath: '/root/.m2/repository', hostPath: '/tmp'),
        persistentVolumeClaim(mountPath: '/tmp/cache', claimName: 'jenkins', readOnly: 'false')
      ]
    ) {
    node(POD_LABEL) {
        stage('Get the project') {
            echo 'Pulling sourcecode...' + env.BRANCH_NAME
            checkout scm
            def pom = readMavenPom file: 'pom.xml'
            container('maven') {
                stage('Build a Maven project') {
                    sh 'mvn -B clean package'
                }

                stage('Make k8s resources') {
                  echo 'making resources for '+pom.artifactId
                  echo 'on branch ' + env.BRANCH_NAME
                  if (env.BRANCH_NAME == 'master') {
                    sh 'mvn -B k8s:resource'
                  } else {
                    serviceName = env.BRANCH_NAME+"-"+pom.artifactId
                    sh 'mvn -B -Djkube.enricher.jkube-service.name='+serviceName+' k8s:resource'
                  }
                }

                stage('Build k8s image') {
                  sh 'mvn -B k8s:build'
                }

                stage('Publish k8s image') {
                    sh 'mvn -B k8s:push'
                }

                stage('Apply config to k8s cluster') {
                    sh 'mvn -B k8s:apply'
                }
            }
        }
    }
}
