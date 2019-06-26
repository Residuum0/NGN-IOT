
//*******************************//
//********** Includes ***********//
//#include <Arduino.h>
#include <ESP8266WiFi.h>          //Generell ESP8266 Lib
//#include "Adafruit_MQTT.h"      //Generell ESP8266 Lib
#include "Adafruit_MQTT_Client.h" //MQTT CLient Class
#include "credentials.h"          // Passwords and Feeds

//*******************************//
//********** Defines ***********//
//#define DHTPIN 5
//#define DHTTYPE DHT22

//*******************************//
//**** Forward declarations ****//
void Connect_Wifi();
void Connect_AIO();
void Tast_MQTT();
void Tast_ADC();

//*******************************//
//*********** Globals ***********//
WiFiClient client;                                                                     // Create an ESP8266 WiFiClient
Adafruit_MQTT_Client mqtt(&client, AIO_SERVER, AIO_SERVERPORT, AIO_USERNAME, AIO_KEY); // Setup the MQTT client class
Adafruit_MQTT_Publish temperature = Adafruit_MQTT_Publish(&mqtt, AIO_USERNAME "/feeds/analog"); // Setup feeds for temp
const int IO_ADC0 = A0; //Naming analog input pin

int inputVal  = 0;        //Variable to store analog input values
//*******************************//
//*********** Setup ***********//
void setup() {
  Serial.begin(115200);                     //Init Serial for debug
  Serial.println("Hello IOT");              //Hello send
  Connect_Wifi();                           //Connect to Wifi
  Connect_AIO();                            //Connect to AIO
}

//*******************************//
//*********** Loop ***********//
void loop() {
  static uint loop_Ctr = 0;

  // 1000ms Task
  if(loop_Ctr % 1000 == 0){}
  // 10000ms Tasks
  if(loop_Ctr % 2050 == 0)
  {
   Tast_ADC();   // check for reconnect
   Tast_MQTT();   // check for reconnect

  if (!temperature.publish(inputVal)) // Publish data
      Serial.println("Publish Temp failed");
  else
      Serial.println("Publish Temp success");
  }
  delay(1);
  loop_Ctr++;
}

//*******************************//
//********** Functions **********//

void Tast_MQTT()
{
  if(! mqtt.ping(3)) {         // ping AIO to stay connected
    if(! mqtt.connected())     // reconnect to AIO
      Connect_AIO();
  }
}
void Tast_ADC(){
  inputVal = analogRead (IO_ADC0);
}

// connect to adafruit io via MQTT
void Connect_AIO() {

  Serial.print(F("Connecting to Adafruit IO... "));

  int8_t ret;

  while ((ret = mqtt.connect()) != 0) {
    switch (ret) {
      case 1: Serial.println(F("Wrong protocol")); break;
      case 2: Serial.println(F("ID rejected")); break;
      case 3: Serial.println(F("Server unavail")); break;
      case 4: Serial.println(F("Bad user/pass")); break;
      case 5: Serial.println(F("Not authed")); break;
      case 6: Serial.println(F("Failed to subscribe")); break;
      default: Serial.println(F("Connection failed")); break;
    }

    if(ret >= 0)
      mqtt.disconnect();

    Serial.println(F("Retrying connection..."));
    delay(5000);
  }
  Serial.println(F("Adafruit IO Connected!"));
}

// connect to WiFi access point
void Connect_Wifi(){

  Serial.println();
  delay(10);
  Serial.print(F("Connecting to "));
  Serial.println(WLAN_SSID);

  WiFi.begin(WLAN_SSID, WLAN_PASS);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(F("."));
  }
  Serial.println();

  Serial.println(F("WiFi connected"));
  Serial.println(F("IP address: "));
  Serial.println(WiFi.localIP());
}
