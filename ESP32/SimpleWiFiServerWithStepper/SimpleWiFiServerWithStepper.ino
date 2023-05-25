
#include <Stepper.h>
#include <WiFi.h>
#include <WebServer.h>

WiFiServer server(80);
WebServer server2(80);

const int stepsPerRevolution = 2048;  // change this to fit the number of steps per revolution

 const char* ssid     = "RedmiEma";
 const char* password = "APPAMIT0";

// ULN2003 Motor Driver Pins
#define IN1 27
#define IN2 13
#define IN3 14
#define IN4 12

Stepper myStepper(stepsPerRevolution, IN1, IN3, IN2, IN4);

void moveInDegrees (int degrees){
  int steps= (degrees*stepsPerRevolution)/360;
  myStepper.step(steps);
}
void handleRoot() {
  server2.send(200, "text/plain", "Ready");
}

// write http://192.168.0.177/get?data=809 to have 809 degrees movement
void handleGet() {
  if (server2.hasArg("data")) {
    String data = server2.arg("data");
    Serial.println("Data: " + data);
    int degrees = data.toInt();
    moveInDegrees(degrees);
  }
  server2.send(200, "text/plain", "Data Received");
}

void handlePost() {
  server2.send(200, "text/plain", "Processing Data");
}

void handleUpload() {
  HTTPUpload& upload = server2.upload();
  if (upload.status == UPLOAD_FILE_START) {
    Serial.println("Receiving data:");
  } else if (upload.status == UPLOAD_FILE_WRITE) {
    Serial.write(upload.buf, upload.currentSize);
  } else if (upload.status == UPLOAD_FILE_END) {
    server2.send(200, "text/plain", "Data: ");
  }
}



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
    
    //server.begin();
    server2.on("/", handleRoot);
    server2.on("/get", HTTP_GET, handleGet);
    server2.on("/post", HTTP_POST, handlePost, handleUpload);
    server2.begin();
    myStepper.setSpeed(10);
}

void loop()
{
  server2.handleClient();
}


