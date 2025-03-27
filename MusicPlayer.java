import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;


import Song.Song;

public class MusicPlayer{
    //initialise an empty list of songs
    static ArrayList<Song> songs = new ArrayList<Song>();
    public static void main(String[] args) throws IOException{
        try {
            Load();
            //create a new scanner to take input from the console
            Scanner input = new Scanner(System.in);
            //Repeat the input loop forever so that you can request songs multiple times
            while (true){
                //print the menu
                System.out.println("\nMusic Program");
                System.out.println("+---------------------------------------------+");
                System.out.println("| 1. Play a song                              |");
                System.out.println("| 2. Create a new song                        |");
                System.out.println("| 3. Delete a song                            |");
                System.out.println("| 4. Print all songs                          |");
                System.out.println("| 5. Print all songs played more than x times |");
                System.out.println("+---------------------------------------------+");
                //Take in an input of 1 number
                int choice = input.nextInt();
                //Run a different function depending on the number provided
                // pass in the input to most of them so that they can take input from 
                // the console too without having to unnecessarily create a new input
                if (choice == 1){
                    PlaySong(input);
                }
                else if (choice == 2){
                    CreateSong(input);
                }
                else if (choice == 3){
                    DeleteSong(input);
                }
                else if (choice == 4){
                    PrintAllSongs();
                }
                else if (choice == 5){
                    PrintAllSongsPlayedXTimes(input);
                }
            }
        }
        //If an error occurs, print it to the console
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Play a song entered
    public static void PlaySong(Scanner input){
        System.out.println("Enter a song.");
        //input.nextLine takes in the input from input.nextInt when you run one after the other 
        //so we have to have an unused input to get rid of that input so we can get the next input
        input.nextLine();
        //take in a string input to choose our song
        String songName = input.nextLine();
        Song selectedSong = null;
        //iterate through each song we have saved to check if it is in the list
        for (Song song : songs){
            //make both the song title we are checking and the song title inputted lowercase so we don't have to worry about capitalisation
            if (songName.toLowerCase().equals(song.title.toLowerCase())){
                selectedSong = song;
                //dont bother looping after we find the right song
                break;
            }
        }
        //If there is no song which matches the entered text, print out a statement saying that
        if (selectedSong == null){
            System.out.println("No song with title " + songName + ".");
            return;
        }
        //increment the play count for this song, and save the new value
        selectedSong.playCount++;
        Save();
        //If the song was found, but it has no associated wav file, we cannot play the song audio, so print out a statement saying that
        if (selectedSong.filePath == ""){
            System.out.println("Song " + songName + " has no associated wav file.");
            return;
        }        
        
        try{
            //Open the file at the path stored in the selected song
            File file = new File(selectedSong.filePath);
            //load an audio stream from the file and get its format and info
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            //start playing the audio
            clip.start();
            //Repeat this, so that it can be run multiple times while the song is playing
            while (true){
                //If the clip has finished, close the clip and return to the main menu
                if (clip.getMicrosecondLength() != clip.getMicrosecondPosition()){
                    clip.close();
                    return;
                }
                System.out.println("Press enter to pause the song, or enter x to end it.");
                //take in a text input
                String text = input.nextLine();
                //If they press enter, pause the song
                if (text.equals("")){
                    clip.stop();
                }
                //If they enter x, end the song and return to the menu
                else if (text.toLowerCase().equals("x")){
                    clip.close();
                    return;
                }
                
                System.out.println("Press enter to resume the song, or enter x to end it.");
                //take in another text input
                text = input.nextLine();
                //If they have already pressed enter and paused the song, if they press enter again resume the song
                if (text.equals("")){
                    clip.start();
                }
                //If they enter x, end the song and return to the menu
                else if (text.toLowerCase().equals("x")){
                    clip.close();
                    return;
                }
            }         
        }
        //If an error occurs, print that playing the chosen song failed, then print the error
        catch (Exception e) {
            System.out.println("Playing the chosen song failed.");
            e.printStackTrace();
        }
    }

    //save the list of songs
    public static void Save(){
        try {
            //Open a new file at the path SavedSongs.bin at the current directory
            File savedSongs = new File("./SavedSongs.bin");
            //create a new file there
            savedSongs.createNewFile();
            //create a stream to that location
            FileOutputStream fileStream = new FileOutputStream("./SavedSongs.bin");
            //create an object stream because we want to save song objects
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            //write the list of songs to the file
            objectStream.writeObject(songs);
            //close the streams now we dont need them
            objectStream.close();
            fileStream.close();
        } 
        //If the saving process fails, print that it failed and then print the error
        catch (IOException e) {
            System.out.println("Saving songs list failed.");
            e.printStackTrace();
        }
    }

    //Load the list of songs from a file
    @SuppressWarnings("unchecked")
    public static void Load() throws ClassNotFoundException{
        try{
            //Open a file input stream at the path SavedSongs.bin at the current directory
            FileInputStream fileStream = new FileInputStream("./SavedSongs.bin");
            //Open a object input stream using the filestream because we are loading song objects
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            //read from the object stream and convert it from an array to a list
            songs = (ArrayList<Song>) objectStream.readObject();
            //close the streams now we dont need them
            objectStream.close();
            fileStream.close();
        } 
        //If the loading process fails, print that it failed and then print the error
        catch (IOException e) {
            System.out.println("Reading songs list failed.");
            e.printStackTrace();
        }
    }

    // create a new song, add it to the list and save the new updated list
    public static void CreateSong(Scanner input){
        System.out.println("Enter the name of the song.");
        //input.nextLine takes in the input from input.nextInt when you run one after the other 
        //so we have to have an unused input to get rid of that input so we can get the next input
        input.nextLine();
        //take in a text input
        String songName = input.nextLine();
        System.out.println("Enter the artist the song is by.");
        //take in a text input
        String artist = input.nextLine();
        System.out.println("Enter the number of times the song has been played.");
        //take in a text input
        int timesPlayed = input.nextInt();
        System.out.println("Enter the path to the wav file to play for this song, or a newline if there is no associated wav.");
        //input.nextLine takes in the input from input.nextInt when you run one after the other 
        //so we have to have an unused input to get rid of that input so we can get the next input
        input.nextLine();
        //take in a text input
        String filePath = input.nextLine();
        //create a new song from the inputs we've taken 
        Song song = new Song(songName, artist, timesPlayed, filePath);
        //add the song to the list of songs
        songs.add(song);
        //save the updated list of songs
        Save();
    }

    //delete an inputted song
    public static void DeleteSong(Scanner input){
        System.out.println("Enter the name of the song.");
        //input.nextLine takes in the input from input.nextInt when you run one after the other 
        //so we have to have an unused input to get rid of that input so we can get the next input
        input.nextLine();
        //take in a text input for the song name
        String songName = input.nextLine();
        //iterate through each song to find the right one
        for (Song song : songs){
            //make both the song title we are checking and the song title inputted lowercase so we don't have to worry about capitalisation
            if (songName.toLowerCase().equals(song.title.toLowerCase())){
                //remove the song from the list
                songs.remove(song);
                //print out what song was deleted
                System.out.println("Deleted " + songName + ".");
                //save the new updated list of songs and return to the menu
                Save();
                return;
            }
        }
        //if no song was found, print that out
        System.out.println("No song was found with title " + songName);
    }

    //print the details of each song in the list
    public static void PrintAllSongs(){
        //the count variable is used to keep track of what number song we are at, so we can number them
        int count = 1;
        //loop through each song and print out its number, title, artists name and play count. Then increment count to increase the number printed in front of the next song
        for (Song song : songs){
            System.out.println(count + ". " + song.title + ", by " + song.artistName + ", played " + song.playCount + " times.");
            count++;
        }
    }   
    
    //print the details of all songs played more than an inputed number of times
    public static void PrintAllSongsPlayedXTimes(Scanner input){
        System.out.println("Enter the number of times the displayed songs should have been played more than.");
        //take a numberic input
        int timesPlayed = input.nextInt();
        //the count variable is used to keep track of what number song we are at, so we can number them
        int count = 1;
        //loop through each song and check if it has been played more than the inputted number of times.
        //If it has, print out its number, title, artists name and play count. Then increment count to increase the number printed in front of the next song
        for (Song song : songs){
            if (song.playCount > timesPlayed){
                System.out.println(count + ". " + song.title + ", by " + song.artistName + ", played " + song.playCount + " times.");
                count++;
            }
        }
    }
}

