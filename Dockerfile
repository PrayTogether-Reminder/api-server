# 빌드의 첫 번째 스테이지로 가벼운 Alpine Linux 사용
FROM alpine:latest as tools
# apk 패키지 매니저로 coreutils를 설치합니다. (base64, tar 포함)
# --no-cache 옵션으로 불필요한 캐시를 남기지 않습니다.
RUN apk add --no-cache coreutils

# JRE만 있는 가벼운 이미지 사용
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=tools /usr/bin/base64 /usr/bin/base64
COPY --from=tools /bin/tar /bin/tar

COPY app/*.jar ./app.jar

# =========================================================================
# 1. 폴더 Secret 처리 (Base64 -> tar.gz -> 폴더 복원)
# =========================================================================
# 'wallet_base64' id로 secret을 마운트합니다.
RUN --mount=type=secret,id=wallet_base64 \
    # Secret 파일의 내용을 읽어 base64 디코딩 후, tar로 압축을 해제합니다.
    # 결과물은 /app/src/main/resources/main-wallet 경로에 생성됩니다.
    mkdir -p /app/src/main/resources && \
    cat /run/secrets/wallet_base64 | base64 -d | tar -xz -C /app/src/main/resources

# =========================================================================
# 2. 파일 Secret 처리 (JSON 내용 -> 파일 생성)
# =========================================================================
# 'config_json' id로 secret을 마운트합니다.
RUN --mount=type=secret,id=firebase_json \
    # 먼저 디렉토리를 생성하고 파일을 생성합니다. \
    # Secret 파일 내용을 /app/src/main/resources/pray-together-firebase-adminsdk.json 파일에 씁니다.
    cat /run/secrets/firebase_json > /app/src/main/resources/pray-together-firebase-adminsdk.json

ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]