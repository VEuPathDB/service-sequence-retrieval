APP_PACKAGE  := $(shell ./gradlew -q print-package | tail -1)
MAIN_DIR     := src/main/java/$(shell echo $(APP_PACKAGE) | sed 's/\./\//g')
TEST_DIR     := $(shell echo $(MAIN_DIR) | sed 's/main/test/')
BIN_DIR      := .tools/bin

C_BLUE := "\\033[94m"
C_NONE := "\\033[0m"
C_CYAN := "\\033[36m"

.PHONY: help
help:
	@echo "Tasks:\n`cat makefile | grep '^[a-z\-]\+:' | sed 's/:.*//;s/^/  /'`"

.PHONY: compile
compile: install-dev-env gen-jaxrs gen-docs
	@./gradlew clean compileJava

.PHONY: test
test: install-dev-env gen-jaxrs gen-docs
	@./gradlew clean test

.PHONY: jar
jar: install-dev-env build/libs/service.jar

.PHONY: docker
docker:
	@./gradlew build-docker --stacktrace

.PHONY: install-dev-env
install-dev-env:
	@if [ ! -d .tools ]; then git clone https://github.com/VEuPathDB/lib-jaxrs-container-build-utils .tools; else cd .tools && git pull && cd ..; fi
	@./gradlew check-env install-raml-4-jax-rs
	@$(BIN_DIR)/install-raml-merge.sh
	@$(BIN_DIR)/install-npm.sh

.PHONY: gen-jaxrs
gen-jaxrs: api.raml merge-raml
	@./gradlew generate-jaxrs
	@$(BIN_DIR)/generate-jaxrs-streams.sh $(APP_PACKAGE)
	@$(BIN_DIR)/generate-jaxrs-postgen-mods.sh $(APP_PACKAGE)

.PHONY: clean
clean:
	@rm -rf .bin .gradle .tools build vendor

.PHONY: gen-docs
gen-docs: api.raml merge-raml
	@./gradlew generate-raml-docs

.PHONY: merge-raml
merge-raml:
	@$(BIN_DIR)/merge-raml schema > schema/library.raml


.PHONY: example-build
example-build:
	@docker-compose -f docker-compose.example.yml build \
		--build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
		--build-arg=GITHUB_TOKEN=${GITHUB_TOKEN}

.PHONY: example-run
example-run:
	@docker-compose -f docker-compose.example.yml up

.PHONY: example-clean
example-clean:
	@docker-compose -f docker-compose.example.yml down

#
# File based targets
#

build/libs/service.jar: gen-jaxrs gen-docs build.gradle.kts
	@echo "$(C_BLUE)Building application jar$(C_NONE)"
	@./gradlew clean test shadowJar

