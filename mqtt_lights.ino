#include <WiFi.h>
#include <PubSubClient.h>
const int greenNS = 19;
const int yellowNS = 18;
const int redNS = 5;

const int greenWE = 17;
const int yellowWE = 16;
const int redWE = 4;
//const int yellow = 2;
//tb2j60YpfvFsWEBXFg3TmVUv6QG58YI4
const char* ssid = "colosseum";
const char* pswd = "";

const char *mqtt_broker = "ff9eefa9-internet-facing-4a22d2ce6a6119fc.elb.us-east-1.amazonaws.com";
const char *topic = "test";
const char *mqtt_username = "lights1234";
const char *mqtt_password = "buttons1234";
const int mqtt_port = 1883;

WiFiClient espClient;
PubSubClient client(espClient);
void initWiFi() {
  WiFi.mode(WIFI_STA); //
  WiFi.begin(ssid, pswd);
  Serial.println("connecting to wifi...");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print('.');
    delay(1000);
  }
  Serial.print("\nConnected to ");
  Serial.print(ssid);
  Serial.print("\nIP: ");
  Serial.print(WiFi.localIP());
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  initWiFi();
  Serial.print("\nRRSI: ");
  Serial.print(WiFi.RSSI());
  //pinMode(yellow, OUTPUT);
  pinMode (redNS, OUTPUT);
  pinMode (yellowNS, OUTPUT);
  pinMode (greenNS, OUTPUT);

  pinMode (redWE, OUTPUT);
  pinMode (yellowWE, OUTPUT);
  pinMode (greenWE, OUTPUT);
  digitalWrite(redWE, 1);
  digitalWrite(redNS, 1);

  client.setServer(mqtt_broker, mqtt_port);
  client.setCallback(callback);
  while (!client.connected()) {
    String client_id = "esp32-client-";
    client_id += String(WiFi.macAddress());
    Serial.printf("The client %s connects to the public mqtt broker\n", client_id.c_str());
    if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
      Serial.println("Public mqtt broker connected");
    } else {
      Serial.print("failed with state ");
      Serial.print(client.state());
      delay(2000);
    }
  }
  client.publish(topic, "lights here");
  client.subscribe(topic);
}


void callback(char *topic, byte *payload, unsigned int length) {
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  Serial.print("Message:");
  String message;
  for (int i = 0; i < length; i++) {
    message = message + (char) payload[i];  // convert *byte to string
  }
  Serial.print(message);
  if (message == "NS green") {
    digitalWrite(redWE, 1);
    digitalWrite(redNS, 0);
    digitalWrite(greenWE, 0);
    digitalWrite(greenNS, 1);
    Serial.println("NS GO");
  }
  if (message == "WE green") {
    digitalWrite(redWE, 0);
    digitalWrite(redNS, 1);
    digitalWrite(greenWE, 1);
    digitalWrite(greenNS, 0);
    Serial.println("WE GO");
  } 
  Serial.println();
  Serial.println("-----------------------");
}

void loop() {
  client.loop();
}
