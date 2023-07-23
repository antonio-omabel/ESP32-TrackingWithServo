
#include <Stepper.h>
#include <WiFi.h>
#include <WebServer.h>
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

WebServer server(80);

const int stepsPerRevolution = 2048;  // change this to fit the number of steps per revolution

 const char* ssid     = "RedmiEma";
 const char* password = "APPAMIT0";

// ULN2003 Motor Driver Pins
#define IN1 27
#define IN2 13
#define IN3 14
#define IN4 12

#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 64 // OLED display height, in pixels

#define OLED_RESET     -1 // Reset pin # (or -1 if sharing Arduino reset pin)
#define SCREEN_ADDRESS 0x3C //Oled Screen Address for initialization
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET); //initialization of Oled Screen


int displayCounter = -1; //Used to reset display when full
int rowCounter = 17; //Used to print to the second column correctly
int movement = 0;
int smallStep = 4;
String currentData;

//Stepper initialization
Stepper myStepper(stepsPerRevolution, IN1, IN3, IN2, IN4);

//Converts desired degrees in steps depending on stepsPerRevolution
void moveInDegrees (int degrees){
  int steps= (degrees*stepsPerRevolution)/360;
  myStepper.step(steps);
}



void displayMessage (String message){
  
  if (displayCounter < 11){
    
    if(displayCounter >= 5){ //when we exceed the space on the first column move to the second
      display.setCursor(64, rowCounter);
      display.println(message);
      display.display();
      displayCounter++;
      rowCounter += 8;
    }
    else{
      display.println(message);
      display.display();
      displayCounter++;
    }
    
  }
  else {
    //when display is full reset it but printing again the address
    display.clearDisplay();
    display.setCursor(0, 8);
    display.println(WiFi.localIP());
    display.println(message);
    display.display(); 
    displayCounter=0;
    rowCounter = 17;
  }
}



void handleRoot() {
  server.send(200, "text/plain", "Ready");
}

// Reads GET request, for example
// write http://192.168.0.177/get?data=809 to have 809 degrees movement
void handleGet() {
  if (server.hasArg("data")) {
    String data = server.arg("data");

    //if the recived data is a number we roteate by that amount
      if (data.toInt()!=0)
      {
        displayMessage (data);
        //converts data received to int and moves motor
        int degrees = data.toInt();
        moveInDegrees(degrees);
      }
      Serial.println("Data: " + data);
    

    //if the data has changed in comparison to the one before and it is a string
    if (currentData!=data){
      currentData = data;
      if (data.toInt()==0)
      {
        //if data is a configuration text, change motor speed accordingly
        if (data =="CONFIG3"){
          myStepper.setSpeed(3);
          displayMessage("SLOW SPEED");
          }
        if (data =="CONFIG5"){
          myStepper.setSpeed(5);
          displayMessage("NORM SPEED");
        }
        if (data == "CONFIG10"){
          myStepper.setSpeed(10);
          displayMessage("FAST SPEED");
        }
        //Display successful connection
        if (data== "TEST"){
          displayMessage("TEST");
          displayMessage("SUCCESS");
          displayMessage("");
        }
        if (data== "CW"){
          movement = smallStep;
          displayMessage("CLOCKWISE");
        }
        if (data== "CCW"){
          movement = -smallStep;
          displayMessage("COUNTERCLO");
        }
        if (data== "STOP"){
          myStepper.step(0);
          movement = 0;
          displayMessage("STOP");
        }
      }
    
    }
    //TODO: decide if other data type has to be printed or not
    server.send(200, "text/plain", "Data Received");
  }
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
    Wire.begin(26, 25); // change deafult I2C pins

    // SSD1306_SWITCHCAPVCC = generate display voltage from 3.3V internally
  if(!display.begin(SSD1306_SWITCHCAPVCC, SCREEN_ADDRESS)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;); // Don't proceed, loop forever
    } 

  // Show initial display buffer contents on the screen --
  // the library initializes this with an Adafruit splash screen.
    display.display();
    delay(100);
    display.clearDisplay();
    display.setTextSize(1);
    display.setTextColor(WHITE);
    display.setCursor(0, 0);
    display.println("Connecting to:");
    display.println(ssid);
    display.display();

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
    myStepper.setSpeed(5);
    
    //Print address for the first time
    display.clearDisplay();
    display.setTextSize(1);
    display.setTextColor(WHITE);
    display.setCursor(0, 8);
    display.println(WiFi.localIP());
    display.display(); 
}

void loop()
{
  myStepper.step(movement);
  server.handleClient();
}


