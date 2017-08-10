FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/cfrqtool.jar /cfrqtool/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/cfrqtool/app.jar"]
