node {
  def mvnHome = tool 'Maven-3.9'
  env.JAVA_HOME = tool 'JDK-21'
  def mavenSetting = 'dfe73d5e-dd12-4ed1-965f-7c8dcebd9101'

  stage('Checkout') {
    checkout scm
  }
  
  stage('Build') {
    withMaven(maven: 'Maven-3.9', mavenSettingsConfig: mavenSetting) {
      sh "mvn clean install -Dquarkus.container-image.build=false"
    }
  }
  
  stage('Publish') {
    withMaven(maven: 'Maven-3.9', mavenSettingsConfig: mavenSetting) {
      sh "mvn deploy -DskipTests"
      sh "mvn sonar:sonar"
    }
  }
}
