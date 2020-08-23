# InstrumentGauge

Instrument guage application displays speedometer and odometer reading in car instrument panel.
Application consists of two custom views,one for speedometer and other for odometer.Transfer between views are accomplished by double finger swip
Source code is organized in following structure  
InstrumentGauge  
->DriverInformation  
->VehicleService

DriverInformation is the application that displays user facing UI and VehicleService is AIDL service that provide interfaces for signal readings.
VehicleService also includes Android library - VehicleServiceLib that contains AIDL IPC translations that can be used by VehicleService and DriverInformation application.

## Block Diagram

![cluster](https://user-images.githubusercontent.com/20403980/90974203-40b14280-e529-11ea-8a90-77391d442c0f.png)

## Steps to build

Both projects are Android gradle based.Follow the steps to install both apks  

1.Import both projects in Android Studio  
2.Build both projects  
3.If build is success ,then deploy the VehicleService package to target by executing following command  
  In terminal go to directory-> VehicleService  
  $ adb install -r ./app/build/outputs/apk/debug/app-debug.apk && adb reboot
  
 4.Once target device is booted up,VehicleService will be up and running in background which generates readings  
 5.Deploy the DriverInformation app from Android Studio which will display speedometer view
 
 ## Cluster demo - Screenrecord captured
[cluster.zip](https://github.com/RenjithRajagopal89/InstrumentGauge/files/5113942/cluster.zip)

 

