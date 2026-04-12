# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Build Service & Dependencies
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM veupathdb/alpine-dev-base:jdk-21-gradle-8.7 AS prep

LABEL service="sequence-retrieval-build"

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /workspace

RUN apk add --no-cache git sed findutils coreutils make npm curl gawk jq \
    && git config --global advice.detachedHead false

RUN npm install -gs raml2html raml2html-modern-theme

# download gradle
COPY gradlew ./
COPY gradle gradle
RUN bash -c 'echo "\n\n" | ./gradlew init --type basic --dsl kotlin --no-daemon'

# copy files required to build dev environment and fetch dependencies
COPY build.gradle.kts settings.gradle.kts ./

# download raml tools (these never change)
RUN ./gradlew install-raml-4-jax-rs install-raml-merge

# download project dependencies in advance
RUN ./gradlew download-dependencies

# copy raml over for merging, then perform code and documentation generation
COPY api.raml ./
COPY schema schema
RUN ./gradlew generate-jaxrs generate-raml-docs

# copy remaining files
COPY . .

# build the project
RUN ./gradlew clean test shadowJar

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Run the service
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM amazoncorretto:21-alpine3.16

LABEL service="sequence-retrieval"

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

# Install clustal-omega from tar file
COPY resources/clustalo-runtime.tar /tmp/clustalo-runtime.tar
RUN cd / \
    && tar -xf /tmp/clustalo-runtime.tar \
    && rm -f /tmp/clustalo-runtime.tar \
    && chmod +x /usr/bin/clustalo

ENV JVM_MEM_ARGS="-Xms256M -Xmx5G" \
    JVM_ARGS="" \
    LD_LIBRARY_PATH="/lib64:${LD_LIBRARY_PATH}"

COPY --from=prep /workspace/build/libs/service.jar /service.jar

COPY startup.sh startup.sh

CMD ./startup.sh
