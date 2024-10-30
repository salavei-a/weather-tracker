FROM gradle:jdk21 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build

FROM tomcat:jre21
COPY --from=build /app/build/libs/*.war $CATALINA_HOME/webapps/ROOT.war
CMD ["catalina.sh", "run"]