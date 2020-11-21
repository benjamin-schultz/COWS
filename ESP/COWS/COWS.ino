#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino

//needed for library
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include "WiFiManager.h"         //https://github.com/tzapu/WiFiManager

#include <uri/UriBraces.h>

ESP8266WebServer server(80);

void handleDuration() {
  String duration = server.pathArg(0);
  server.send(200, "text/plain", "it worked: " + duration);
}

void setup() {

  pinMode(0, OUTPUT);

  Serial.begin(115200);

  //WiFiManager
  //Local intialization. Once its business is done, there is no need to keep it around
  WiFiManager wifiManager;
  //reset saved settings
  wifiManager.resetSettings();

  //fetches ssid and pass from eeprom and tries to connect
  //if it does not connect it starts an access point with the specified name
  //here  "AutoConnectAP"
  //and goes into a blocking loop awaiting configuration
  //wifiManager.autoConnect("AutoConnectAP");
  //or use this for auto generated name ESP + ChipID
  wifiManager.autoConnect("COWS", "cowsgomoo");

  //if you get here you have connected to the WiFi
  Serial.println("connected...yeey :)");

  server.on(UriBraces("/duration={}"), handleDuration);

  server.begin();
  Serial.println("HTTP server started");
  Serial.println(WiFi.localIP());
}

void loop() {
  server.handleClient();
}
