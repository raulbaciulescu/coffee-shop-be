FROM openjdk:21
COPY target/coffee-shop-be-1.0.0.jar coffee-shop.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=cloud", "coffee-shop.jar"]
