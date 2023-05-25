/*
 WiFi Web Server LED Blink

 A simple web server that lets you blink an LED via the web.
 This sketch will print the IP address of your WiFi Shield (once connected)
 to the Serial monitor. From there, you can open that address in a web browser
 to turn on and off the LED on pin 5.

 If the IP address of your shield is yourAddress:
 http://yourAddress/H turns the LED on
 http://yourAddress/L turns it off

 This example is written for a network using WPA2 encryption. For insecure
 WEP or WPA, change the Wifi.begin() call and use Wifi.setMinSecurity() accordingly.

ported for sparkfun esp32 
31.01.2017 by Jan Hendrik Berlin
 
 */
#include <Stepper.h>
#include <WiFi.h>

const int stepsPerRevolution = 2048;  // change this to fit the number of steps per revolution

// ULN2003 Motor Driver Pins
#define IN1 27
#define IN2 13
#define IN3 14
#define IN4 12

Stepper myStepper(stepsPerRevolution, IN1, IN3, IN2, IN4);

void setDegreeMovement (float degree);

const char* ssid     = "RedmiEma";
const char* password = "APPAMIT0";

WiFiServer server(80);

void setup()
{
    Serial.begin(115200);
    pinMode(2, OUTPUT);      // set the LED pin mode

    delay(10);

    // We start by connecting to a WiFi network

    Serial.println();
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);

    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected.");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());

    //Blink 3 times to signal wifi connection
    for (int i=0; i<3; i++){
      digitalWrite(2, HIGH);
      delay(200);
       digitalWrite(2, LOW);
      delay(100);

    }
    
    server.begin();
    myStepper.setSpeed(10);

    // set the speed at 10 rpm
  //myStepper.setSpeed(10);

}

void loop(){
 WiFiClient client = server.available();   // listen for incoming clients

  if (client) {                             // if you get a client,
    Serial.println("New Client.");           // print a message out the serial port
    String currentLine = "";                // make a String to hold incoming data from the client
    while (client.connected()) {            // loop while the client's connected
      if (client.available()) {             // if there's bytes to read from the client,
        char c = client.read();             // read a byte, then
        Serial.write(c);                    // print it out the serial monitor
        if (c == '\n') {                    // if the byte is a newline character

          // if the current line is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            client.println("HTTP/1.1 200 OK");
            client.println("Content-type:text/html");
            client.println();

            // the content of the HTTP response follows the header:
            client.print("Click <a href=\"/H\">here</a> to turn the LED on pin 2 on.<br>");
            client.print("Click <a href=\"/L\">here</a> to turn the LED on pin 2 off.<br>");

            // The HTTP response ends with another blank line:
            client.println();
            // break out of the while loop:
            break;
          } else {    // if you got a newline, then clear currentLine:
            currentLine = "";
          }
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }

        


        
        // Check to see if the client request was "GET /H" or "GET /L":
        if (currentLine.endsWith("GET /H")) {
          digitalWrite(2, HIGH); // GET /H turns the LED on
          
          Serial.println("\nclockwise");
          moveInDegrees(360);
          delay(1000);
        
        }
        if (currentLine.endsWith("GET /L")) {
          digitalWrite(2, LOW);                // GET /L turns the LED off
          
          Serial.println("\nclockwise");
          moveInDegrees(-360);

          //myStepper.step(-512);
          delay(1000);
        }
        if (currentLine.endsWith("GET /8")) {
          digitalWrite(2, LOW);                // GET /L turns the LED off
          
          moveInDegrees(180);
          //myStepper.step(-512);
          delay(1000);
        }
      }
    }
    
        Serial.println("\nInizio stringa intercettata: ");
        String movValue = currentLine;
        Serial.println(currentLine);
        Serial.println("fine\n");
    // close the connection:
    client.stop();
    Serial.println("Client Disconnected.");
  }
}

void moveInDegrees (int degrees){
  int steps= (degrees*stepsPerRevolution)/360;
  myStepper.step(steps);
}
