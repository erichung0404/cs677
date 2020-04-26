FROM maven:3.6.3-ibmjava-8-alpine as build

COPY . /

ARG component

ENV component $component

SHELL ["/bin/bash", "-c"]

CMD ./run.sh docker $component