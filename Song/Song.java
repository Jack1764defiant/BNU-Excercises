package Song;

import java.io.Serializable;

public class Song implements Serializable{
    public String title = "";
    public String artistName = "";
    public int playCount = 0;
    public String filePath;
    public Song(String title, String artistName, int playCount, String filePath){
        this.title = title;
        this.artistName = artistName;
        this.playCount = playCount;
        this.filePath = filePath;
    }
}