APP_PACKAGE  := $(shell ./gradlew -q print-package | tail -1)
MAIN_DIR     := src/main/java/$(shell echo $(APP_PACKAGE) | sed 's/\./\//g')
TEST_DIR     := $(shell echo $(MAIN_DIR) | sed 's/main/test/')
BIN_DIR      := .tools/bin

C_BLUE := "\\033[94m"
C_NONE := "\\033[0m"
C_CYAN := "\\033[36m"
C_RED  := "\\033[31m"

#
# Meta Targets
#

.PHONY: help
help:
	@echo "Targets:\n`cat makefile | grep '^[a-z\-]\+:' | sed 's/:.*//;s/^/  /'`"

#
# Project & Environment Setup Targets
#

.PHONY: new-project-initialization
new-project-initialization:
	@if [ ! 1 -eq `git rev-list --all --count` ]; then \
	  echo ; \
	  echo "$(C_RED)-- ERROR -----------------------------------------------------------------------$(C_NONE)"; \
	  echo "This make target cannot not be used after a new project already has commits.";\
	  echo "Merging the histories of the template repository and a project based on it may";\
	  echo "cause issues and must be done with care.";\
	  echo "$(C_RED)--------------------------------------------------------------------------------$(C_NONE)";\
	  echo ; \
	  exit 1; \
	fi
	$(MAKE) register-template-repo
	@git fetch template
	@git merge template/master --allow-unrelated-histories

.PHONY: link-template-repo
link-template-repo:
	@git remote add template git@github.com:VEuPathDB/example-jaxrs-container-service.git

# Merges in changes from the template/example repo.
.PHONY: merge-template-repo
merge-template-repo:
	@git remote show template > /dev/null 2>&1 || (echo "\n$(C_RED)Template repo has not been registered.$(C_NONE)\n\nRun 'make link-template-repo'\n"; exit 1)
	@git fetch template
	@git merge template/master

.PHONY: install-dev-env
install-dev-env:
	@if [ ! -d .tools ]; then \
	  git clone https://github.com/VEuPathDB/lib-jaxrs-container-build-utils .tools; \
	else \
	  cd .tools && \
	  git pull && \
	  cd ..; \
	fi
	@./gradlew check-env install-raml-4-jax-rs
	@$(BIN_DIR)/install-raml-merge.sh
	@$(BIN_DIR)/install-npm.sh


#
# Build & Test Targets
#

.PHONY: compile
compile: install-dev-env gen-jaxrs
	@./gradlew clean compile

.PHONY: test
test: install-dev-env gen-jaxrs gen-docs
	@./gradlew clean test

.PHONY: jar
jar: install-dev-env gen-jaxrs gen-docs build/libs/service.jar

.PHONY: docker
docker:
	@./gradlew build-docker --stacktrace


#
# Code & Doc Generation
#

.PHONY: gen-jaxrs
gen-jaxrs: raml-gen-code
	@echo
	@echo "$(C_RED)THIS MAKE TARGET IS DEPRECATED AND WILL BE REMOVED IN THE FUTURE$(C_NONE)"
	@echo "PLEASE SHIFT TO USING \"raml-gen-code\" INSTEAD"
	@echo

.PHONY: raml-gen-code
raml-gen-code: api.raml merge-raml
	@./gradlew generate-jaxrs
	@$(BIN_DIR)/generate-jaxrs-streams.sh $(APP_PACKAGE)
	@$(BIN_DIR)/generate-jaxrs-postgen-mods.sh $(APP_PACKAGE)

# See raml-gen-docs
.PHONY: gen-docs
gen-docs: raml-gen-docs
	@echo
	@echo "$(C_RED)THIS MAKE TARGET IS DEPRECATED AND WILL BE REMOVED IN THE FUTURE$(C_NONE)"
	@echo "PLEASE SHIFT TO USING \"raml-gen-docs\" INSTEAD"
	@echo

.PHONY: raml-gen-docs
raml-gen-docs: api.raml merge-raml
	@./gradlew generate-raml-docs

.PHONY: merge-raml
merge-raml:
	@$(BIN_DIR)/merge-raml schema > schema/library.raml


.PHONY: example-build
example-build:
	@docker-compose -f docker-compose/docker-compose.dev.yml build \
		--build-arg=GITHUB_USERNAME=${GITHUB_USERNAME} \
		--build-arg=GITHUB_TOKEN=${GITHUB_TOKEN}

.PHONY: example-run
example-run:
	@docker-compose -f docker-compose/docker-compose.dev.yml up

.PHONY: example-clean
example-clean:
	@docker-compose -f docker-compose/docker-compose.dev.yml down

#
# File based targets
#

build/libs/service.jar: build.gradle.kts
	@echo "$(C_BLUE)Building application jar$(C_NONE)"
	@./gradlew clean test shadowJar

