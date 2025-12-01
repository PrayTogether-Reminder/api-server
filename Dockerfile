FROM ubuntu:22.04 as tools
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
    coreutils && \
    rm -rf /var/lib/apt/lists/*

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
# 2. 파일 Secret 처리 (Base43 -> JSON 내용 -> 파일 생성)
# =========================================================================
# id로 secret을 마운트합니다.
RUN --mount=type=secret,id=firebase_base64 \
    # 먼저 디렉토리를 생성하고 파일을 생성합니다. \
    # Secret 파일 내용을 /app/src/main/resources/pray-together-firebase-adminsdk.json 파일에 씁니다.
    cat /run/secrets/firebase_base64 | base64 -d > /app/src/main/resources/pray-together-firebase-adminsdk.json

ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT [
"java", \
"-Xms256m", \
"-Xmx512m", \
"-Xss1m", \
"-XX:MaxMetaspaceSize=160m", \
"-XX:+UseG1GC", \
"-XX:+UseStringDeduplication", \
"-XX:MaxDirectMemorySize=64m", \

# G1GC 튜닝
"-XX:G1NewSizePercent=10", \
"-XX:G1MaxNewSizePercent=20", \
"-XX:MaxGCPauseMillis=200", \

# CodeCache 최적화
"-XX:InitialCodeCacheSize=32m", \
"-XX:ReservedCodeCacheSize=64m", \

# JIT 최적화 (작은 서버용)
"-XX:+TieredCompilation", \
"-XX:TieredStopAtLevel=1", \

# Class Unloading
"-XX:+ClassUnloadingWithConcurrentMark", \
"-XX:+ClassUnloading", \

"-XX:+ExitOnOutOfMemoryError", \
"-Djava.security.egd=file:/dev/./urandom", \
"-jar", \
"app.jar" \
]

