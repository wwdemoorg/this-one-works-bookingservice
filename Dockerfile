FROM websphere-liberty:beta
COPY src/main/liberty/config/server.xml /config/server.xml
RUN installUtility install  --acceptLicense defaultServer
COPY src/main/liberty/config/jvm.options /config/jvm.options
COPY target/bookingservice-java-1.0.0-SNAPSHOT.war /config/apps/

ENV MONGO_HOST=booking-db
