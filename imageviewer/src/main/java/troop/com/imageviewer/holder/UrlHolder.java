package troop.com.imageviewer.holder;

import java.net.URL;

/**
 * Created by Ingo on 27.12.2015.
 */
public class UrlHolder extends BaseHolder
{
    private URL url;

    public UrlHolder(URL url)
    {
        this.url=url;
    }

    public URL getUrl()
    {
        return url;
    }
}
