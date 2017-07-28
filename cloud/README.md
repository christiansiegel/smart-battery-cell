# Setup Cloud Server

## Configure Firewall
Add the following firewall rules to your system.

| Protocol | Port | Description    |
|----------|------|----------------|
| TCP      | 80   | HTTP           |
| TCP      | 1883 | MQTT           |
| TCP      | 9001 | Websocket MQTT |

## Install Docker Engine

Follow the instructions on [docs.docker.com](https://docs.docker.com/engine/installation/linux/ubuntulinux/) to install the latest Docker Engine needed to run docker images.

## Install Docker Compose

Execute the following commands in a terminal to install the Docker Compose version 1.8.1.

```
curl -L https://github.com/docker/compose/releases/download/1.8.1/docker-compose-‘uname -s‘-‘uname -m‘ > /tmp/docker-compose
sudo mv /tmp/docker-compose /usr/bin
sudo chmod 755 /usr/bin/docker-compose
```

## Create Docker Compose File

Create a docker compose file named [mosquitto+nginx.yml](mosquitto+nginx.yml) with the following content.

```
version: ’2’
services:
  mqtt:
    container_name: "mqtt"
    image: toke/mosquitto
    ports:
      - 1883:1883
      - 9001:9001
  web:
    container_name: "web"
    image: kyma/docker-nginx
    ports:
      - 80:80
    volumes:
      - ~/www:/var/www
```

It runs two docker containers. The first container, named `mqtt`, starts the open source MQTT broker Mosquitto listening on port 1883 for MQTT and port 9001 for Websocket MQTT client connections. The second container, named `web`, starts the open source static HTTP web server Nginx listening on port 80 and providing the contents of the directory `~/www.`

## Create Web Content

Create at least a index.html file in `~/www`. You can, for example, use the [Eclipse Paho JavaScript Client](https://eclipse.org/paho/clients/js/) library to interface the MQTT broker on the cloud server. For example, [index.html](index.html) displays the cell voltages in a graph and allows to control the cell balancing via MQTT.

## Start MQTT Broker and HTTP Server

Execute the following command to start the docker containers with Docker Compose:

```
docker-compose -f mosquitto+nginx.yml up -d
```
