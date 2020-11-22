#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino

//needed for library
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include "WiFiManager.h"         //https://github.com/tzapu/WiFiManager

#include <uri/UriBraces.h>

ESP8266WebServer server(80);

#define MOTOR_PIN 0

void handleDuration() {
  int duration = server.pathArg(0).toInt();
  server.send(200, "text/plain", "Watering for " + String(duration) + " seconds");
  digitalWrite(MOTOR_PIN, LOW);
  delay(duration * 1000);
  digitalWrite(MOTOR_PIN, HIGH);
}

void setup() {

  pinMode(MOTOR_PIN, OUTPUT);

  Serial.begin(115200);

  //WiFiManager
  //Local intialization. Once its business is done, there is no need to keep it around
  WiFiManager wifiManager;
  wifiManager.resetSettings();

  IPAddress _ip = IPAddress(192, 168, 86, 25);
  IPAddress _gw = IPAddress(192, 168, 86, 1);
  IPAddress _sn = IPAddress(255, 255, 255, 0);

  wifiManager.setAPStaticIPConfig(_ip, _gw, _sn);
  wifiManager.setSTAStaticIPConfig(_ip, _gw, _sn);

  //fetches ssid and pass from eeprom and tries to connect
  //if it does not connect it starts an access point with the specified name
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
