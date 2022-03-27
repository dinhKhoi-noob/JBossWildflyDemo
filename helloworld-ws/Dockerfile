# Use latest jboss/base-jdk:11 image as the base
FROM jboss/wildfly:latest

# Set the WILDFLY_VERSION env variable
ENV JBOSS_HOME /opt/jboss/wildfly

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

USER root

COPY /target/helloworld-ws.war $JBOSS_HOME/standalone/deployments/

# RUN /subsystem=undertow/configuration=handler/file=images:add(path="/opt/jboss/wildfly/standalone/data/upload") && \
#     /subsystem=undertow/server=default-server/host=default-host/location=\/:write-attribute(name=handler,value=images)

# COPY standalone.xml $JBOSS_HOME/standalone/configuration

# Expose the ports in which we're interested
EXPOSE 8080/tcp 9990/tcp

# Set the default command to run on boot
# This will boot WildFly in standalone mode and bind to all interfaces
CMD ["/opt/jboss/wildfly/bin/standalone.sh","-c","standalone-full.xml","-b","0.0.0.0","-bmanagement","0.0.0.0"]