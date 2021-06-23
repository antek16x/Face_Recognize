# Face_Recognize
The purpose of this application for Android devices is to use the Microsoft Face API to not only detect individual faces in images but also provide certain information about them (age, facial hair, gender, smile). However, before we see all this information, a note is displayed on how many adults and children there are in the picture (age limit 18 years)

<img align="center" src="https://github.com/antek16x/Face_Recognize/blob/master/FaceRecognize_1.png" width="220"> <img align="center" src="https://github.com/antek16x/Face_Recognize/blob/master/FaceRecognize_3.png" width="220"> <img align="center" src="https://github.com/antek16x/Face_Recognize/blob/master/FaceRecognize_4.png" width="220">

_____

<img align="right" src="https://github.com/antek16x/Face_Recognize/blob/master/Demo.gif" width="220">

## Usage:

The application is quite easy to use: on the first page we see a menu where we can choose from: loading a photo from the device's memory, taking a photo with the camera or closing the application. After selecting or taking a photo, image analysis using AsyncTask and Microsoft Face API will automatically start to detect faces in the image and analyze them.

After processing the image, we will be transferred to the second activity, where information about the number of adults and children detected in the image will be displayed. After pressing the "See details" button we will see a ListView with generated thumbnail of each person and data about them. Microsoft Face API provides various features that can be found on their site. We can choose the attributes we are interested in by specifying them in AsyncTask.

## Setup:

**Please note that this app requires the use of Microsoft Azure's Face API. Without an API Key, you will not be able to use the app as it was intended. The following sections contain the full set of instructions to getting your own API key for free and using it in the app by changing a single line of code.
