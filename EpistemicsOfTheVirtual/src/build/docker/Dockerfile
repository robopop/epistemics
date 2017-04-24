# Dockerfile for Epistemics

FROM	tomcat:8-jre8

ENV	VERSION 1.2
ENV	ARTIFACTORY_URL http://artifactory:8081/artifactory/libs-release-local/selemca

ADD	epistemics-beliefsystem-rest.war /usr/local/tomcat/webapps/beliefsystem-rest.war
ADD	epistemics-beliefsystem-webadmin.war /usr/local/tomcat/webapps/beliefsystem-webadmin.war
ADD	epistemics-mentalworld-webapp.war /usr/local/tomcat/webapps/mentalworld-webapp.war
ADD	epistemics-mentalworld-webadmin.war /usr/local/tomcat/webapps/mentalworld-webadmin.war
