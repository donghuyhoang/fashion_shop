# Giai đoạn 1: Build ứng dụng bằng Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copy file cấu hình và source code vào container
COPY pom.xml .
COPY src ./src
# Build file .jar và bỏ qua bước test để chạy nhanh hơn
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng với JRE siêu nhẹ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy file .jar đã build từ giai đoạn 1 sang
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8081 (khớp với application.properties của bạn)
EXPOSE 8081

# Lệnh khởi chạy server
ENTRYPOINT ["java", "-jar", "app.jar"]