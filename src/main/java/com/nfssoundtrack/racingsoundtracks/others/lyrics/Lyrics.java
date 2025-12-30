package com.nfssoundtrack.racingsoundtracks.others.lyrics;

/**
 * Lyrics for some song
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Lyrics
{
    private final String title, author, content, url, source;
    //these fields below are going to be useful in the future once i make something to submit lyrics to LrcLib
    private String albumName;
    private int duration;
    private String escapedContent;

    protected Lyrics(String title, String author, String content, String url, String source)
    {
        this.title = title;
        this.author = author;
        this.content = content;
        this.url = url;
        this.source = source;
    }

    /**
     * The title of the song
     *
     * @return the title of the song
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * The author/artist of the song
     *
     * @return the author/artist of the song
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * The content of the lyrics
     *
     * @return the lyrics
     */
    public String getContent()
    {
        return content;
    }

    /**
     * The URL the lyrics can be found at
     *
     * @return the URL the lyrics can be found at
     */
    public String getURL()
    {
        return url;
    }

    /**
     * The source that was used to find these lyrics
     *
     * @return the source that was used to find these lyrics
     */
    public String getSource()
    {
        return source;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getEscapedContent() {
        return escapedContent;
    }

    public void setEscapedContent(String escapedContent) {
        this.escapedContent = escapedContent;
    }


}
