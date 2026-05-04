################### Overview #######################################
# Bioinformatics tools need glibc → Can't use Alpine → Use Debian for build → Need compatible runtime → Eclipse Temurin 
# Eclipse Temurin (Debian-based) is a production quality Java runtime that maintains binary compatibility with the Conda packages.
####################################################################
 

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# Stage 1: Prep (Build the Java Application)
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM gradle:8.7-jdk21 AS prep

WORKDIR /workspace

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

# Copy files and build the project
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew ./
RUN ./gradlew download-dependencies

COPY . .
RUN ./gradlew clean shadowJar


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# Stage 2: Bio-Builder (Install Tools via Conda)
#
# Use debian because these tools are not available in alpine
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM debian:bookworm-slim AS bio-builder

RUN apt-get update && apt-get install -y wget bzip2 ca-certificates && rm -rf /var/lib/apt/lists/*

RUN wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -O /tmp/miniconda.sh \
    && bash /tmp/miniconda.sh -b -p /opt/conda \
    && rm /tmp/miniconda.sh


# Remove problematic defaults immediately
RUN /opt/conda/bin/conda config --remove channels defaults || true \
    && /opt/conda/bin/conda config --remove channels https://repo.anaconda.com/pkgs/main || true \
    && /opt/conda/bin/conda config --remove channels https://repo.anaconda.com/pkgs/r || true

# Install tools
RUN /opt/conda/bin/conda install -y -c conda-forge -c bioconda --override-channels clustalo mafft fasttree \
    && /opt/conda/bin/conda clean -afy

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# Stage 3: Runtime (Use Eclipse Temurin Debian-based)
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM eclipse-temurin:21-jre-jammy

# 1. Install dependencies for the tools
# Temurin uses apt (Debian/Ubuntu), so this will work perfectly
RUN apt-get update && apt-get install -y \
    libgomp1 \
    libstdc++6 \
    bash \
    netcat-openbsd \    
    && rm -rf /var/lib/apt/lists/*

# 2. Copy the built Java application
COPY --from=prep /workspace/build/libs/service.jar /service.jar

# 3. Copy the entire Conda directory
COPY --from=bio-builder /opt/conda /opt/conda

# 4. Set PATH
ENV PATH="/opt/conda/bin:${PATH}"

# Runtime configuration
COPY startup.sh startup.sh

CMD ["/bin/bash", "./startup.sh"]
