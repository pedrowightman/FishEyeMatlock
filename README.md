# FishEye Matlock

This work presents a RFE mechanism for Matlock-coded location data, called FishEye Matlock, created by Pedro Wightman, Nicoás Avilán, and Augusto Salazar. This technique generates disposable random key matrices that only reveal a desired portion of the path, with the possibility for the user to add random noise to protect the revealed data and to control the amount of noise added to the rest of the path. This allows secure information sharing with particular actors, like law enforcement, so that the information of interest is shared without affecting the user’s privacy. 

The code, written in Java, implements the code used for testing in the paper "FishEye Matlock: A Random Functional Encoding Mechanism for Secure Location Sharing" to be published in IEEE Latin America Transactions.


# Executing in Netbeans

This is a Netbeans IDE project that will run the FishEye MAtlock algorithm over a dataset of locations and time stamp. Open the project in a Netbeans IDE and execute it. The takoradi.csv should be in root folder of the project. 

# Executing java files
The java files could be compiled and executed outside the IDE. The program has no dependencies or libraries apart from the standard Java SE, which can be downloaded at the following link:
[Java SE Download](https://www.oracle.com/java/technologies/downloads/)

For compiling, run the following command:

> javac *.java

For executing, run the following command, where the .class file is stored:
> java FishEyeMatlock

The results will be shown on screen.

# Files

 - The main file is FishEyeMatlock.java, which defines all the support data structures and executes the experiment. The main function has documentation on the relevant lines.
 - The MatInverse.java file includes functionalities for calculating the inverse of a matrix, necessary for the Matlock and FishEye Matlock algorithms. The file was taken from https://www.sanfoundry.com/java-program-find-inverse-matrix/
