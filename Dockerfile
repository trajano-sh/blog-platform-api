FROM ubuntu:latest
LABEL authors="lora"

ENTRYPOINT ["top", "-b"]