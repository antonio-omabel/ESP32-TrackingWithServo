
#include <Stepper.h>
#include <WiFi.h>
#include <WebServer.h>

WebServer server(80);

const int stepsPerRevolution = 2048;  // change this to fit the number of steps per revolution

 const char* ssid     = "RedmiEma";
 const char* password = "APPAMIT0";

// ULN2003 Motor Driver Pins
#define IN1 27
#define IN2 13
#define IN3 14
#define IN4 12


//Stepper creation
Stepper myStepper(stepsPerRevolution, IN1, IN3, IN2, IN4);

//Converts desired degrees in steps depending on stepsPerRevolution
void moveInDegrees (int degrees){
  int steps= (degrees*stepsPerRevolution)/360;
  myStepper.step(steps);
}



void handleRoot() {
  server.send(200, "text/plain", "Ready");
}

// Reads GET request, for example
// write http://192.168.0.177/get?data=809 to have 809 degrees movement
void handleGet() {
  if (server.hasArg("data")) {
    String data = server.arg("data");
    Serial.println("Data: " + data);

    //converts data received to int and moves motor
    int degrees = data.toInt();
    moveInDegrees(degrees);
    //if data is a configuration text, change motor speed accordingly
    if (data="CONFIG3") {myStepper.setSpeed(3);
    if (data="CONFIG5") {myStepper.setSpeed(5);
    if (data="CONFIG10") {myStepper.setSpeed(10);
    //TODO: if data is invalid (for motor) print error message
    }
  }

  //TODO: decide if other data type has to be printed or not
  server.send(200, "text/plain", "Data Received");

}

void handlePost() {
  server.send(200, "text/plain", "Processing Data");
}

void handleUpload() {
  HTTPUpload& upload = server.upload();
  if (upload.status == UPLOAD_FILE_START) {
    Serial.println("Receiving data:");
  } else if (upload.status == UPLOAD_FILE_WRITE) {
    Serial.write(upload.buf, upload.currentSize);
  } else if (upload.status == UPLOAD_FILE_END) {
    server.send(200, "text/plain", "Data: ");
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
   
    
    server.on("/", handleRoot);
    server.on("/get", HTTP_GET, handleGet);
    server.on("/post", HTTP_POST, handlePost, handleUpload);
    server.begin();
    myStepper.setSpeed(10);
}

void loop()
{
  server.handleClient();
}


