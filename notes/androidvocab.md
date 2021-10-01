Android Basics
================
# Vocab
SDK: Software development kit
* Numbered by API level

API: Application programming interface  
AVD: Android virtual device
ART: Android runtime (similar to JVM) 
ADB: Android Debug Bridge
APK: Android package
AAB: Android App Bundle (Publishing format)

Gradle: Build system  

The API level and AVD will be specific to each project 

# Basic Project Structure
Manifest: Comprehensive detailing of project contents
* Generally used for the play store
* Details can include: App icon, app name, primary app function, app description, etc
* Will be an .xml file

Activity: A 'screen' in an app  
Main Activity: A default screen to begin the app on 
* Android apps do not include a main method because there are multiple entrypoints to an app   

# Application fundamentals
Every Adr. app lives in its own security sandbox
* Adr. operating system is a multi-user Linux system in which each app is a different user
* Each app is assigned a unique linux user ID by the system. The system set persmissiosn for all the files in an app so that only the user ID assigned to that app can access them
* Each process has its own VM so the app's code runs in isolation
* Each app runs its own linux process

Adr. apps should implement the principle of least priviledge  
It is possible to arrange for two apps to share same linux user id to access each other's files. To conserve system resources apps with the same user id can also arrange to run of same linux process and share same vm
* Must be signed with the same certificate


