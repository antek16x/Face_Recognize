# Face_Recognize
**The purpose of this application for Android devices is to use the Microsoft Face API to not only detect individual faces in images but also provide certain information about them (age, facial hair, gender, smile). However, before we see all this information, a note is displayed on how many adults and children there are in the picture (age limit 18 years).**

<img align="center" src="https://github.com/antek16x/Face_Recognize/blob/master/FaceRecognize_1.png" width="220"> <img align="center" src="https://github.com/antek16x/Face_Recognize/blob/master/FaceRecognize_3.png" width="220"> <img align="center" src="https://github.com/antek16x/Face_Recognize/blob/master/FaceRecognize_4.png" width="220">

_____

<img align="right" src="https://github.com/antek16x/Face_Recognize/blob/master/Demo.gif" width="220">

## Usage:

The application is quite easy to use: on the first page we see a menu where we can choose from: loading a photo from the device's memory, taking a photo with the camera or closing the application. After selecting or taking a photo, image analysis using AsyncTask and Microsoft Face API will automatically start to detect faces in the image and analyze them.

After processing the image, we will be transferred to the second activity, where information about the number of adults and children detected in the image will be displayed. After pressing the "See details" button we will see a ListView with generated thumbnail of each person and data about them. Microsoft Face API provides various features that can be found on their site. We can choose the attributes we are interested in by specifying them in AsyncTask.

## Setup:

**Please note that this app requires the use of Microsoft Azure's Face API. Without an API Key, you will not be able to use the app as it was intended. The following sections contain the full set of instructions to getting your own API key for free and using it in the app by changing a single line of code.**

### Downloading to Android Studio

You can fork this project on GitHub, download it, and then open it as a project in Android Studio. Once you have done so, it can be run on your Android device.

### Making the Azure Account

In order to run the face dectection and analysis, you must get an API Subscription Key from the Azure Portal. [This page](https://azure.microsoft.com/en-us/services/cognitive-services/face/) by Microsoft provides the features and capabilities of the Face API. **You can create a free Azure account that doesn't expire at [this link here](https://azure.microsoft.com/en-us/try/cognitive-services/?api=face-api) by clicking on the "Get API Key" button and choosing the option to create an Azure account**. 

### Getting the Face API Key from Azure Portal

Once you have created your account, head to the Azure Portal. Follow these steps:

1. Click on "Create a resource" on the left side of the portal.
2. To quickly find the API use the search engine by typing the word "face".
3. Now select Face and click the Create button.
4. You should now be at [this page](https://portal.azure.com/#create/Microsoft.CognitiveServicesFace). **Fill in the required information and press "Create" when done**.
5. Now, click on "All resources" on the left hand side of the Portal.
6. Click on the name you gave the API.
7. Underneath "Resource Management", click on "Keys and Endpoint".

You should now be able to see two different subscription keys that you can use. Follow the additional instructions to see how to use the API Key in the app.

### Using the API Key in the app

Head over to the [MenuActivity page](https://github.com/antek16x/Face_Recognize/blob/master/app/src/main/java/com/example/facerecognize/MenuActivity.java) in Android Studio since that is where the API Key will be used when creating the `FaceServiceClient` object.

`
private static FaceServiceClient faceServiceClient =
           new FaceServiceRestClient("<YOUR API SUBSCRIPTION KEY>");
           `

replace `<YOUR API SUBSCRIPTION KEY>` with one of your 2 keys from the Azure Portal. (If you haven't gotten your API Key yet, read the previous two sections above). 
  
Now that you have the Face API Key, you can use the app as it was intended. **Please note that if you are using the free, standard plan, you can only make 20 API transactions/calls per minute. Therefore, if that limit is exceeded, you may run into runtime errors**.

### Detecting Particular Facial Attributes

The face analysis happens in the `detectAndFrame` method of [MenuActivity.java](https://github.com/antek16x/Face_Recognize/blob/master/app/src/main/java/com/example/facerecognize/MenuActivity.java). More specifically, `detectAndFrame` -> `AsyncTask` -> `doInBackground`. This is what the code looks like for detecting age, gender, smile, and facial hair:

` 
FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]{
                        FaceServiceClient.FaceAttributeType.Age,
                        FaceServiceClient.FaceAttributeType.Gender,
                        FaceServiceClient.FaceAttributeType.Smile,
                        FaceServiceClient.FaceAttributeType.FacialHair,
                };
                
                `

You can change it to something like `FaceServiceClient.FaceAttributeType.hairColor`. For more of the `FaceAttributeTypes`, you can check out one of the JSON files from the [Face API page](https://azure.microsoft.com/en-us/services/cognitive-services/face/).

Now that you have detected the face attributes, you will have to change the [CustomAdapter.java](https://github.com/antek16x/Face_Recognize/blob/master/app/src/main/java/com/example/facerecognize/CustomAdapter.java) in order to display the results from the detection process. In the `getView` method, to get the facial attributes of a face, the code uses `faces[position]` to get an element in the array of type `Face`. Then, you can use `faces[position].faceAttributes.faceAttribute` to get information about a particular attribute. The code is below:

`
//Getting the Gender:
faces[position].faceAttributes.gender

//Getting facial hair information:
//Probability of having a beard:
faces[position].faceAttributes.facialHair.beard
//Probability of having sideburns:
faces[position].faceAttributes.facialHair.sideburns
//Probability of having a moustache:
faces[position].faceAttributes.facialHair.moustache

`

#### Please note that if you do not specify a certain face attribute to be detected, then doing `faces[position].faceAttributes.thatFacialAttribute` in the `getView` method will give you errors. Additionally, certain attributes, like Head Position and Facial Hair, have attributes within themselves such as `faces[position].faceAttributes.facialHair.moustache` which can end in `moustache, sideburns, beard` or `faces[position].faceAttributes.headPose.yaw` which can end in `yaw, roll, pitch`.
