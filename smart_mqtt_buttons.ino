#include <WiFi.h>
#include <PubSubClient.h>

const char* ssid = "colosseum";
const char* pswd = "";

const int buttonN = 34;
const int buttonS = 35;
const int buttonW = 32;
const int buttonE = 33;
int buttonStateN = 0;
int buttonStateS = 0;
int buttonStateW = 0;
int buttonStateE = 0;

const char *mqtt_broker = "ff9eefa9-internet-facing-4a22d2ce6a6119fc.elb.us-east-1.amazonaws.com";
const char *topic = "testing";
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

  pinMode(buttonN, INPUT);
  pinMode(buttonS, INPUT);
  pinMode(buttonW, INPUT);
  pinMode(buttonE, INPUT);

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
  client.publish(topic, "buttons here");
  client.subscribe(topic);
}

void callback(char *topic, byte *payload, unsigned int length) {
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  String message;
  for (int i = 0; i < length; i++) {
        message = message + (char) payload[i];  // convert *byte to string
    }
}

void loop() {
  int vehicleCountNS = 0;
  int vehicleCountWE = 0;
  buttonStateN = digitalRead(buttonN);
  buttonStateS = digitalRead(buttonS);
  buttonStateW = digitalRead(buttonW);
  buttonStateE = digitalRead(buttonE);
  bool buttonN_prev = false;
  bool buttonS_prev = false ;
  bool buttonW_prev = false;
  bool buttonE_prev = false;
  
  if (buttonStateN == HIGH  & buttonN_prev == false ) {
    vehicleCountNS += 1;
    buttonN_prev = true;
  } 
  if (buttonStateN == LOW && buttonN_prev == true){
    buttonN_prev = false;
  }
  
  if (buttonStateS == HIGH & buttonS_prev == false) {
    vehicleCountNS += 1;
    buttonS_prev = true;
  }
  if (buttonStateS == LOW & buttonS_prev == true){
    buttonS_prev = false;
  }
  
  if (buttonStateW == HIGH & buttonW_prev == false) {
    vehicleCountWE += 1;
    buttonW_prev = true;
  }
  if (buttonStateW == LOW && buttonW_prev == true){
    buttonW_prev = false;
  }
  
  if (buttonStateE == HIGH  & buttonE_prev == false) {
    vehicleCountWE += 1;
    buttonE_prev = true;
  }
  if (buttonStateE == LOW && buttonE_prev == true){
    buttonE_prev = false;
  }

  if(vehicleCountNS >= vehicleCountWE){
    client.publish(topic, "NS green");
  } else {
    client.publish(topic, "WE green");
  }
  client.loop();
}
