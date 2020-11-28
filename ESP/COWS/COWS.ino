#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino
#include <TinyUPnP.h>
#include <EasyDDNS.h>
#include <WiFiUdp.h>
#include "secrets.h"

//needed for library
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include "WiFiManager.h"         //https://github.com/tzapu/WiFiManager

#include <uri/UriBraces.h>

#define LISTEN_PORT 1250
#define LEASE_DURATION 36000
#define FRIENDLY_NAME "COWS"
#define MOTOR_PIN 16
#define LED_PIN 0
#define DDNS_NAME "cows-for-yy.duckdns.org"

TinyUPnP tinyUPnP(20000);
ESP8266WebServer server(LISTEN_PORT);
DNSServer dnsServer;

void handleDuration() {
  if (!server.authenticate(COWS_USERNAME, COWS_PASSWORD)) {
    return server.requestAuthentication();
  }
  
  int duration = server.pathArg(0).toInt();
  
  digitalWrite(MOTOR_PIN, HIGH);
  delay(duration * 1000);
  digitalWrite(MOTOR_PIN, LOW);
  server.send(200, "text/plain", "Finished watering!");
}

void setupWiFi() {
  //WiFiManager
  //Local intialization. Once its business is done, there is no need to keep it around
  WiFiManager wifiManager;
  //wifiManager.setHostname("cows-for-yy.duckdns.org");
  
  delay(5000);
  pinMode(LED_PIN, INPUT);
  if (digitalRead(LED_PIN) == LOW) { 
    wifiManager.resetSettings();
  }
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, HIGH);

  pinMode(MOTOR_PIN, OUTPUT);
  
  IPAddress _ip = IPAddress(192, 168, 5, 20);
  IPAddress _gw = IPAddress(192, 168, 5, 1);
  IPAddress _sn = IPAddress(255, 255, 255, 0);

  wifiManager.setAPStaticIPConfig(_ip, _gw, _sn);

  IPAddress sta_ip = IPAddress(192.168.1.250);
  IPAddress sta_gw = IPAddress(192.168.1.1);

  wifiManager.setSTAStaticIPConfig(sta_ip,sta_gw,_sn);

  //fetches ssid and pass from eeprom and tries to connect
  //if it does not connect it starts an access point with the specified name
  wifiManager.autoConnect("COWS", "cowsgomoo");

  //if you get here you have connected to the WiFi
  Serial.println("Connected to Wifi :)");

  Serial.println(WiFi.localIP());
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

void setupDDNS() {
  EasyDDNS.service("duckdns");
  
  EasyDDNS.client(DDNS_NAME, DDNS_TOKEN);

  EasyDDNS.onUpdate([&](const char* oldIP, const char* newIP){
    Serial.println("EasyDDNS - IP Change Detected: ");
    Serial.println(newIP);
  });
}

void setupServer() {
  server.on(UriBraces("/duration={}"), handleDuration);

  server.begin();
  Serial.println("HTTP server started");
  Serial.println(WiFi.localIP());
}

void setup() {

  pinMode(MOTOR_PIN, INPUT);

  Serial.begin(115200);

  setupWiFi();

  setupDDNS();

  setupUPnP();

  setupServer();

  Serial.println(WiFi.hostname());

  EasyDDNS.update(1);
}

void loop() {
  server.handleClient();
  EasyDDNS.update(100000);
}
