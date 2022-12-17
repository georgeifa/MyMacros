# MyMacros
This is an android phone app with which you can count calories in meals, track calories burnt in excersices and note down your goals and achievements.
This app was created as a team project for a class for the University of Pireaus.

The app was to me used for tracking the daily calorie intake of the user. It was also required that we had to use at least 2 phone sensors as well as the GPS sensor.

The user should be able to do the following:

  > To create an account with his email and a password.
    - Extra: Will be able to create account with Google and Twitter as well

  > Be able to connect to an existing account
    - Extra: It will also be able to connect to Google and Twitter

  > To enter personal data such as his weight, height and age.

  > For each day to be able to see the calories he has consumed and burned, as well as how many macronutrients he received that day.

  > For each day to be able to add the meals and exercise he has done during the day.
    - Extra: The user will be able to add their own foods and exercises, which will be stored locally on the device.
     
  > To be able to see statistics such as the proportion of macronutrients in his diet, the progress of his weight etc.
    - Extra: The statistics will be seen in the form of charts, and will be able to choose a period of time (per week, month, quarter)

  > To be able to add goals which he will be able to mark as completed when he achieves them.

  > Receive notifications to remind him to use the application.
  
The application makes the following use of the following sensors:
    
  > GPS sensor and Type_Step_Counter:
    - Used to count user's step as well as show user's current location and route taken.

  > Bio-metrics sensor – Fingerprint Scanner:
    - Used to unlock the phone when it's automatically locked after 10 seconds in the background ( App Lock ).

Libraries

    • 'androidx.biometric:biometric:1.1.0'
        ◦ For the use of the fingerprint scanner.
    • 'androidx.lifecycle:lifecycle-process:2.4.1'
        ◦ So we can know when the app went to background.
    • 'com.github.AnyChart:AnyChart-Android:1.1.2'
        ◦ To present the statistics in the form of charts.
    • 'com.google.android.gms:play-services-maps:18.0.2'
        ◦ To use Google Maps
    • 'com.google.code.gson:gson:2.9.0'
        ◦ To add items to Shared Preferences
    • 'com.squareup.picasso:picasso:2.71828'
        ◦ To display your profile picture.
   
   
   > Starting - Sign Up - Log in Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208249978-c3dd57d2-4c5d-4602-a014-d8b0530cb705.png)  ![image](https://user-images.githubusercontent.com/47480663/208249998-51202b4c-06b2-4775-b089-17ace79a4427.png)  ![image](https://user-images.githubusercontent.com/47480663/208249983-329c749e-5a39-436f-ae17-688b94ada931.png)  

   > Quiz Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208250021-0863ff0d-f85d-4f69-9863-c0647ecbcbb2.png)
   
   > Home Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208250028-d51887cc-49b3-4e30-8ac9-92aeba2c0208.png)  ![image](https://user-images.githubusercontent.com/47480663/208250032-113bb492-fb0b-4c4e-a324-14a600fa4418.png)

   > Add Meal - Excersice Page
   
  ![image](https://user-images.githubusercontent.com/47480663/208250047-e401beda-70c8-432b-9d08-9919dea26d14.png)  ![17](https://user-images.githubusercontent.com/47480663/208250167-6c65de38-b861-48bb-9569-71822f4d30a6.png)

   > Insert Food - Excersice Dialog Window
   
   ![image](https://user-images.githubusercontent.com/47480663/208250062-79931665-d830-4a39-b47a-1d40318a1706.png)  ![image](https://user-images.githubusercontent.com/47480663/208250064-7b66d89e-dd45-488e-a1d8-09785419a1e9.png)

   > Add custom food - excersice Dialog Window
   
   ![image](https://user-images.githubusercontent.com/47480663/208250071-9762967e-f315-456b-846d-6b90c98f3d0a.png) ![image](https://user-images.githubusercontent.com/47480663/208250077-a38be98e-f90a-4802-8c14-d960558ba7e6.png)

   > Map - Step Counter Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208250081-f55f61d9-e7df-4d75-91e9-199240481794.png)

   > Statistics Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208250086-90c915d3-e899-4a98-92ce-c4c5c1dd1103.png)  ![image](https://user-images.githubusercontent.com/47480663/208250088-a986acf2-48b0-4d5c-9322-091a7dc7c0b2.png)  ![image](https://user-images.githubusercontent.com/47480663/208250094-12d3c063-5b88-4900-b788-b85477a2b5da.png)

   > Goal - Achievements Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208250101-06bf4df5-4540-4e51-8f9e-ff49a3e40432.png)  ![image](https://user-images.githubusercontent.com/47480663/208250113-7a4bc1af-b72d-43be-a229-6854d7a6cd8b.png)

   > Account Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208250110-f39e72a2-3e1e-4209-82ea-10cdb9679cef.png)  ![image](https://user-images.githubusercontent.com/47480663/208250123-ab52a600-47e2-4acc-bc19-74a86780b0da.png)

   > Settings Page
   
   ![image](https://user-images.githubusercontent.com/47480663/208250125-4b2d6a88-155a-4bfc-9d31-e0ae798ed666.png)

   > App Lock
   
   ![image](https://user-images.githubusercontent.com/47480663/208250129-ce6413da-21dc-46ca-bd05-02464e74051b.png)  ![image](https://user-images.githubusercontent.com/47480663/208250132-2fec6b0c-0d3e-4f2e-b4cd-b752552f09db.png)  ![image](https://user-images.githubusercontent.com/47480663/208250139-46dd5ccd-b79b-43d1-bb34-13fb1994d6cc.png)


Firebase was used to store all the necessary data.

Below are the GitHub profiles of all the contributors.

> George Ifanits (myself): https://github.com/georgeifa
> John Touloupis: https://github.com/Jtouloupis
> John Fotopoulos: https://github.com/john-fotopoulos
   
