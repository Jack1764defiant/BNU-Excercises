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

public class Excercises{
    static ArrayList<Song> songs = new ArrayList<Song>();
    public static void main(String[] args) throws IOException{
        try {
            Load();
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Scanner input = new Scanner(System.in)) {
            while (true){
                System.out.println("\nMusic Program");
                System.out.println("+---------------------------------------------+");
                System.out.println("| 1. Play a song                              |");
                System.out.println("| 2. Create a new song                        |");
                System.out.println("| 3. Delete a song                            |");
                System.out.println("| 4. Print all songs                          |");
                System.out.println("| 5. Print all songs played more than x times |");
                System.out.println("+---------------------------------------------+");
                int choice = input.nextInt();
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PlaySong(Scanner input){
        System.out.println("Enter a song.");
        input.nextLine();
        String songName = input.nextLine();
        Song selectedSong = null;
        for (Song song : songs){
            if (songName.toLowerCase().equals(song.title.toLowerCase())){
                selectedSong = song;
                break;
            }
        }
        if (selectedSong == null){
            System.out.println("No song with title " + songName + ".");
            return;
        }
        if (selectedSong.filePath == ""){
            System.out.println("Song " + songName + " has no associated wav file.");
            return;
        }
        selectedSong.playCount++;
        Save();
        try{
            File yourFile = new File(selectedSong.filePath);
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;

            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
            while (true){
                System.out.println("Press enter to pause the song.");
                input.nextLine();
                clip.stop();
                System.out.println("Press enter to resume the song, or enter x to end it");
                String text = input.nextLine();
                if (text.equals("")){
                    clip.start();
                }
                else if (text.toLowerCase().equals("x")){
                    clip.close();
                    break;
                }
            }         
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Save(){
        try {
            File savedSongs = new File("./SavedSongs.bin");
            if (savedSongs.createNewFile()) {
                FileOutputStream fileStream = new FileOutputStream("./SavedSongs.bin");
                ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
                objectStream.writeObject(songs);
                objectStream.close();
            } 
            else {
                FileOutputStream fileStream = new FileOutputStream("./SavedSongs.bin");
                ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
                objectStream.writeObject(songs);
                objectStream.close();
            }
        } 
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void Load() throws ClassNotFoundException{
        try{
            FileInputStream fileStream = new FileInputStream("./SavedSongs.bin");
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            songs = (ArrayList<Song>) objectStream.readObject();
            objectStream.close();
        } 
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void CreateSong(Scanner input){
        System.out.println("Enter the name of the song.");
        input.nextLine();
        String songName = input.nextLine();
        System.out.println("Enter the artist the song is by.");
        String artist = input.nextLine();
        System.out.println("Enter the number of times the song has been played.");
        int timesPlayed = input.nextInt();
        System.out.println("Enter the path to the wav file to play for this song, or a newline if there is no associated wav.");
        input.nextLine();
        String filePath = input.nextLine();
        
        Song song = new Song(songName, artist, timesPlayed, filePath);
        songs.add(song);
        Save();
    }

    public static void DeleteSong(Scanner input){
        System.out.println("Enter the name of the song.");
        input.nextLine();
        String songName = input.nextLine();
        for (Song song : songs){
            if (songName.toLowerCase().equals(song.title.toLowerCase())){
                songs.remove(song);
                System.out.println("Deleted " + songName + ".");
                return;
            }
        }
        System.out.println("No song was found with title " + songName);
        Save();
    }

    public static void PrintAllSongs(){
        int count = 1;
        for (Song song : songs){
            System.out.println(count + ". " + song.title + ", by " + song.artistName + ", played " + song.playCount + " times.");
            count++;
        }
    }   
    
    public static void PrintAllSongsPlayedXTimes(Scanner input){
        System.out.println("Enter the number of times the displayed songs should have been played more than.");
        int timesPlayed = input.nextInt();
        int count = 1;
        for (Song song : songs){
            if (song.playCount > timesPlayed){
                System.out.println(count + ". " + song.title + ", by " + song.artistName + ", played " + song.playCount + " times.");
                count++;
            }
        }
    }
}

