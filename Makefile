.PHONY: podman-login build-image push-image build-and-push run clean

IMAGE_NAME = docker.io/sushankk/ecom-application:latest

podman-login:
	echo $(DOCKER_PASSWORD) | podman login docker.io --username $(DOCKER_USERNAME) --password-stdin

build-image:
	./mvnw clean package spring-boot:build-image -Dspring-boot.build-image.imageName=$(IMAGE_NAME) -DskipTests

push-image: podman-login
	podman push $(IMAGE_NAME)

build-and-push: build-image push-image

run:
	podman-compose up -d

clean:
	podman-compose down --rmi all --volumes --remove-orphans
