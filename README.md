# COWS
Chuckys Online Watering System (Soon to be much less online)

---

## Motivation
When my partner comes to visit me for long periods of time, she has no reliable way of watering her plants. Her roommate can water them, but he may sometimes forget. Therefore, I attempted to create a system that allows for her to control when and how much to water her plants.

---

## Build status

Initial release was completed, however there is a significant issue with battery life. Due to the current draw of the system (the ESP8266) while using WiFi, the current system architecture and protocol needs to be completely revised to allow for a reasonable battery life.

To Do List:

- [ ] Complete system protocol and architecture rewrite
  - [ ] Implement device sleep mode
  - [ ] Redesign system operation to allow for minimum wake time
  - [ ] Rewrite everything to support new mode of operation
- [ ] If interet/wifi connectivity is still used in new system, fix IP address

---

## Usage

Use the app to interact with the device. Put the COWS system's pump in a tank of water, with a pipe leading out of the nozzle of the pump into a plant tank.

Basically everything else is going to change after rewrite i imagine.

---

## Installation
Current means of *"installation"* is to get the COWS system from me as well as the app binary from me.

---

## Tests
No tests at the moment. I have no experience writing tests for java and i dont currently have the hardware for automated tests of the embedded system.

