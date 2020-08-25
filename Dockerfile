FROM openjdk:8-jre

RUN mkdir /root/OSBot && mkdir /root/OSBot/Data && mkdir /root/OSBot/Scripts
WORKDIR /client
ENV CHECK_IN_TOKEN vVFe_MD6oloLuPCOszc6hOl3FMODyTIK
ENV RS_USERNAME unsuspicioustestaccount@protonmail.com
ENV RS_PASSWORD wu7ja7UYpYV2RXCmKyQd2GMD
COPY ./local-repo/osbot.jar .
COPY  . .
RUN cp bottify-client-1.0-SNAPSHOT.jar /root/OSBot/Scripts/bottify-client.jar
RUN apt-get update && apt-get install -y xvfb libxrender1 libxtst6 libxi6
RUN unzip -d /root/OSBot/Data map.zip
CMD xvfb-run -a -e display.log java -jar osbot.jar -login fridaypasta:bhTAfBBbWtgD4eAhPDAyfhjG -script BottifyClient:tiechV0HThqL3uQhGflOprqEGfzrCuPS-unsuspicioustestaccount+two@protonmail.com-wu7ja7UYpYV2RXCmKyQd2GMD -bot unsuspicioustestaccount@protonmail.com:wu7ja7UYpYV2RXCmKyQd2GMD:0000 -debug 5005 -world 301 -allow nointerface lowcpu norandoms
