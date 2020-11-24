#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino
#include <TinyUPnP.h>
#include <ESP8266mDNS.h>
#include <WiFiUdp.h>

//needed for library
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include "WiFiManager.h"         //https://github.com/tzapu/WiFiManager

#include <uri/UriBraces.h>

#define LISTEN_PORT 6665
#define LEASE_DURATION 36000
#define FRIENDLY_NAME "COWS"
#define MOTOR_PIN 0

TinyUPnP tinyUPnP(20000);
ESP8266WebServer server(LISTEN_PORT);

void handleDuration() {
  
  int duration = server.pathArg(0).toInt();
  server.send(200, "text/plain", "Watering for " + String(duration) + " seconds");
  digitalWrite(MOTOR_PIN, LOW);
  delay(duration * 1000);
  digitalWrite(MOTOR_PIN, HIGH);
}

void setupUPnP() {
  portMappingResult portMappingAdded;
  tinyUPnP.addPortMappingConfig(WiFi.localIP(), LISTEN_PORT, RULE_PROTOCOL_TCP, LEASE_DURATION, FRIENDLY_NAME);
  while (portMappingAdded != SUCCESS && portMappingAdded != ALREADY_MAPPED) {
    portMappingAdded = tinyUPnP.commitPortMappings();
    Serial.println("");

    if (portMappingAdded != SUCCESS && portMappingAdded != ALREADY_MAPPED) {
      tinyUPnP.printAllPortMappings();
      Serial.println(F("This was printed because adding the required port mapping failed"));
      delay(30000);
    }
  }

  Serial.println("UPnP done");
}

void setup() {

  pinMode(MOTOR_PIN, INPUT);

  Serial.begin(115200);

  //WiFiManager
  //Local intialization. Once its business is done, there is no need to keep it around
  WiFiManager wifiManager;
  delay(5);
  if (MOTOR_PIN == 0) { 
    wifiManager.resetSettings();
  }

  pinMode(MOTOR_PIN, OUTPUT);
  
  IPAddress _ip = IPAddress(192, 168, 5, 20);
  IPAddress _gw = IPAddress(192, 168, 5, 1);
  IPAddress _sn = IPAddress(255, 255, 255, 0);

  wifiManager.setAPStaticIPConfig(_ip, _gw, _sn);

  //fetches ssid and pass from eeprom and tries to connect
  //if it does not connect it starts an access point with the specified name
  wifiManager.autoConnect("COWS", "cowsgomoo");

  //if you get here you have connected to the WiFi
  Serial.println("connected...yeey :)");

  Serial.println(WiFi.localIP());

  setupUPnP();

  if (MDNS.begin("esp8266")) {
    Serial.println(F("MDNS responder started"));
  }

  server.on(UriBraces("/duration={}"), handleDuration);

  server.begin();
  Serial.println("HTTP server started");
  Serial.println(WiFi.localIP());
}

void loop() {
  server.handleClient();
}
