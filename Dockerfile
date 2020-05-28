FROM openjdk:8-jre

RUN mkdir /root/OSBot && mkdir /root/OSBot/Data && mkdir /root/OSBot/Scripts
WORKDIR /client
ENV RS_USERNAME BatFitches@protonmail.com
ENV RS_PASSWORD dD7rqLGZrTC2f43k
ENV BotID Kroonjay
COPY  . .
RUN cp bottify-client-1.0-SNAPSHOT.jar /root/OSBot/Scripts/bottify-client.jar
RUN apt-get update && apt-get install -y xvfb libxrender1 libxtst6 libxi6
RUN unzip -d /root/OSBot/Data map.zip
CMD xvfb-run -a -e display.log java -jar osbot.jar -login SantiagoDunbar:rRYGJ@yy7Tu7xg3 -script BottifyClient:"${BotID}" -bot ${RS_USERNAME}:${RS_PASSWORD}:0000 -proxy us3943.nordvpn.com:1080:mkeown42@gmail.com:t5^EC@SDca.?ZM9
-world 308 -debug 5005 -allow nointerface lowcpu
