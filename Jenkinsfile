// Powered by Infostretch 

timestamps {

node ('jenkins-slave2') { 

	stage ('de-config-connector-master - Checkout') {
 	 checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '378d0676-aa57-4a10-b89f-ac72b251392d', url: 'https://github.com/entando/config-connector.git']]]) 
	}
	stage ('de-config-connector-master - Build') {
 			// Maven build step
	withMaven(maven: 'Maven-3.3.9', mavenOpts: '-DskipTests') { 
 			if(isUnix()) {
 				sh "mvn clean deploy jacoco:report sonar:sonar -Pprepare-for-central -Dsonar.host.url=https://sonar.entandocloud.com -Dsonar.login=admin -Dsonar.password=61b0217508585701668b7ddc " 
			} else { 
 				bat "mvn clean deploy jacoco:report sonar:sonar -Pprepare-for-central -Dsonar.host.url=https://sonar.entandocloud.com -Dsonar.login=admin -Dsonar.password=61b0217508585701668b7ddc " 
			} 
 		} 
	}
}
}